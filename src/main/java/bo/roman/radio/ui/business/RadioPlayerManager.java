package bo.roman.radio.ui.business;

import static bo.roman.radio.utilities.ExecutorUtils.fixedThreadPoolFactory;
import static bo.roman.radio.utilities.LoggerUtils.logDebug;
import static io.reactivex.Observable.fromCallable;
import static java.util.Optional.ofNullable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.radio.player.MediaPlayerDaoApi;
import bo.radio.player.business.MediaPlayerBusiness;
import bo.radio.player.entities.MediaPlayerEntity;
import bo.radio.tuner.entities.Station;
import bo.roman.radio.cover.ICoverArtManager;
import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.Radio;
import bo.roman.radio.player.IRadioPlayer;
import bo.roman.radio.player.model.Codec;
import bo.roman.radio.player.model.MediaPlayerInformation;
import bo.roman.radio.ui.Initializable;
import bo.roman.radio.ui.business.displayer.CoverArtManager;
import bo.roman.radio.ui.business.displayer.DockInfoManager;
import bo.roman.radio.ui.business.displayer.LabelsManager;
import bo.roman.radio.ui.business.displayer.SubInfoLabelsManager;
import bo.roman.radio.ui.business.observers.PimpedRadioObserver;
import bo.roman.radio.ui.model.RadioPlayerEntity;
import bo.roman.radio.utilities.ResourceFinder;
import bo.roman.radio.utilities.StringUtils;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;

public class RadioPlayerManager implements Initializable {
	private final static Logger logger = LoggerFactory.getLogger(RadioPlayerManager.class);
	private static final String DBPROPS_PATH = "resources/database.properties";
	private static final Album emptyAlbum = new Album.Builder().build();
	private static final Radio emptyRadio = new Radio.Builder().build();

	private static final int MAX_VOL = 100;

	private final MediaPlayerDaoApi mediaPlayerDao;
	private Slider volume;
	private ToggleButton playButton;

	public static RadioPlayerManager instance;

	private IRadioPlayer radioPlayer;
	private ICoverArtManager coverManager;

	// Info streams
	private PublishSubject<Album> albumStream;
	private PublishSubject<Radio> radioStream;
	private PublishSubject<Codec> codecStream;

	// Observers
	private Observer<Codec> codecInfoObserver;
	private PimpedRadioObserver<LabelsManager> labelsObserver;
	private PimpedRadioObserver<CoverArtManager> coverObserver;
	private PimpedRadioObserver<DockInfoManager> dockObserver;
	private PimpedRadioObserver<SubInfoLabelsManager> subInfoObserver;

	private final ExecutorService executorService = fixedThreadPoolFactory(3);

	private RadioPlayerEntity radioPlayerEntity;

	private RadioPlayerManager(Slider volume, ToggleButton playButton) {
		this.volume = volume;
		this.playButton = playButton;
		radioPlayer = IRadioPlayer.getInstance;
		coverManager = ICoverArtManager.getInstance;
		radioPlayerEntity = RadioPlayerEntity.getInstance();

		Properties properties = new Properties();
		try (FileInputStream fis = new FileInputStream(ResourceFinder.findFilePath(DBPROPS_PATH))) {
			properties.load(fis);
			mediaPlayerDao = new MediaPlayerBusiness(properties);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(String.format(
					"There was an error loading the DB properties. The file %s does not exist.", DBPROPS_PATH), e);
		} catch (IOException e) {
			throw new RuntimeException(String.format("There was an error reading the DB properties.", DBPROPS_PATH), e);
		}
	}

	public static RadioPlayerManager getInstance() {
		return getInstance(null, null);
	}

	public static RadioPlayerManager getInstance(Slider vol, ToggleButton playButton) {
		if (instance == null) {
			if (vol == null && playButton == null) {
				throw new IllegalArgumentException(
						"Initializing a RadioPlayerManager must have an instance of Slider and ToggleButton.");
			}
			instance = new RadioPlayerManager(vol, playButton);
		}
		return instance;
	}

	@Override
	public void initialize() {
		Integer volLevel = mediaPlayerDao.getMediaPlayerState()//
				.map(MediaPlayerEntity::getLevel)//
				.orElse(MAX_VOL);

		volume.setValue(volLevel);
		radioPlayer.setVolume(volLevel);

		subscribeObservers();
	}

	public void changeVolume() {
		radioPlayer.setVolume((int) volume.getValue());
	}

	private void subscribeObservers() {
		albumStream = PublishSubject.create();
		radioStream = PublishSubject.create();
		codecStream = PublishSubject.create();

		albumStream.distinctUntilChanged()//
				.flatMap(a -> fromCallable(() -> coverManager.getAlbumWithCover(a.getSongName(), a.getArtistName())//
						.orElse(a))//
								.subscribeOn(Schedulers.from(executorService)))//
				.subscribe(a -> {
					logDebug(logger, () -> "Album changed:" + a.toString());
					radioPlayerEntity.setAlbum(a);
					labelsObserver.onNext(radioPlayerEntity);
					subInfoObserver.onNext(radioPlayerEntity);
					coverObserver.onNext(radioPlayerEntity);
				});

		radioStream.distinctUntilChanged()//
				.filter(r -> !r.getName().matches("^https?:\\/\\/.+$"))// filter out URLs
				.flatMap(r -> fromCallable(() -> coverManager.getRadioWithLogo(r.getName()).orElse(r))//
						.subscribeOn(Schedulers.from(executorService)))//
				.subscribe(r -> {
					logDebug(logger, () -> "Radio changed:" + r.toString());
					radioPlayerEntity.setRadio(r);
					labelsObserver.onNext(radioPlayerEntity);
					subInfoObserver.onNext(radioPlayerEntity);
					coverObserver.onNext(radioPlayerEntity);
					dockObserver.onNext(radioPlayerEntity);
				});

		codecStream.subscribe(codecInfoObserver::onNext);
	}
	
	public void play(Station station) {
		// Get the media stream observable and subscribe to it
		handleMediaObservable(radioPlayer.getMediaObservable());

		// play the radio
		radioPlayer.play(station.getStream());

		// calculate new codec
		Observable.fromCallable(() -> radioPlayer.calculateCodec())//
				.subscribeOn(Schedulers.from(executorService))//
				.subscribe(oc -> oc.ifPresent(codecStream::onNext));

		enableStop();
	}

	/**
	 * Media player information streams <code>
	 * MP:	----m--m-----m-----m-----|-->
	 * A :	-------a-----a-----a-----|-->
	 * R :  ----r-----r-----r-----r--|-->
	 * </code>
	 */
	private void handleMediaObservable(Observable<MediaPlayerInformation> mediaObservable) {
		// Split the observable in two subjects: one for radio information and the other
		// for album information
		mediaObservable//
				.subscribe(mpi -> {
					ofNullable(mpi.getRadio())//
							.filter(StringUtils::exists)//
							.map(radio -> new Radio.Builder().name(radio).build())//
							.ifPresent(radioStream::onNext);

					Album album = ofNullable(mpi.getArtist())//
							.filter(StringUtils::exists)//
							.flatMap(artist -> ofNullable(mpi.getSong())//
									.filter(StringUtils::exists)//
									.map(song -> new Album.Builder()//
											.artistName(artist)//
											.songName(song)//
											.build()))//
							.orElseGet(() -> new Album.Builder().songName(mpi.getSong()).build());
					albumStream.onNext(album);
				}, e -> onMediaError(e), () -> onMediaStop());
	}

	private void onMediaError(Throwable e) {
		logger.error("The media player stopped unexpetedly. " + e.getMessage());
		// reset RPE
		RadioPlayerEntity.getInstance().setAlbum(emptyAlbum);
		RadioPlayerEntity.getInstance().setRadio(emptyRadio);

		// notify error
		codecInfoObserver.onError(e);
		labelsObserver.onError(e);
		coverObserver.onError(e);
		dockObserver.onError(e);

		// stop the player
		stop();
	}

	private void onMediaStop() {
		logger.info("Radio Player stopped...");
		// reset RPE
		RadioPlayerEntity.getInstance().setAlbum(emptyAlbum);
		RadioPlayerEntity.getInstance().setRadio(emptyRadio);

		// notify stop
		codecInfoObserver.onComplete();
		labelsObserver.onComplete();
		coverObserver.onComplete();
		dockObserver.onComplete();
	}

	public void close() {
		radioPlayer.close();
		// Save the volume level
		mediaPlayerDao.saveMediaPlayerState(new MediaPlayerEntity((int) volume.getValue()));
	}

	public void stop() {
		radioPlayer.stop();
		enablePlay();
	}

	private void enablePlay() {
		playButton.setSelected(false);
	}

	private void enableStop() {
		playButton.setSelected(true);
	}

	public void setCodecInfoObserver(Observer<Codec> observer) {
		this.codecInfoObserver = observer;
	}

	public void setCoverObserver(PimpedRadioObserver<CoverArtManager> coverObserver) {
		this.coverObserver = coverObserver;
	}

	public void setLabelsObserver(PimpedRadioObserver<LabelsManager> labelsObserver) {
		this.labelsObserver = labelsObserver;
	}

	public void setDockObserver(PimpedRadioObserver<DockInfoManager> dockObserver) {
		this.dockObserver = dockObserver;
	}

	public void setSubInfoObserver(PimpedRadioObserver<SubInfoLabelsManager> subInfoObserver) {
		this.subInfoObserver = subInfoObserver;
	}

}
