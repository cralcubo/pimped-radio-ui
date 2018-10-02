package bo.roman.radio.ui.business;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Optional;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.radio.tuner.entities.Station;
import bo.roman.radio.cover.ICoverArtManager;
import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.CoverArt;
import bo.roman.radio.cover.model.Radio;
import bo.roman.radio.player.IRadioPlayer;
import bo.roman.radio.player.model.MediaPlayerInformation;
import bo.roman.radio.ui.Initializable;
import bo.roman.radio.ui.model.CodecInformation;
import bo.roman.radio.ui.model.PlayerImageInformation;
import bo.roman.radio.ui.model.PlayerInformation;
import bo.roman.radio.utilities.LoggerUtils;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;

public class RadioPlayerManager implements Initializable {
	private final static Logger logger = LoggerFactory.getLogger(RadioPlayerManager.class);
	private static final int MAX_VOL = 100;

	private Slider volume;
	private ToggleButton playButton;

	public static RadioPlayerManager instance;

	private IRadioPlayer radioPlayer;
	private ICoverArtManager coverManager;

	private PublishSubject<PlayerInformation> playerInfoStream;
	private PublishSubject<PlayerImageInformation> coverInfoStream;
	private PublishSubject<PlayerImageInformation> dockInfoStream;
	private PublishSubject<CodecInformation> codecInfoStream;

	private Observer<PlayerInformation> playerInfoObserver;
	private Observer<CodecInformation> codecInfoObserver;
	private Observer<PlayerImageInformation> dockInfoObserver;
	private Observer<PlayerImageInformation> coverInfoObserver;

	private RadioPlayerManager(Slider volume, ToggleButton playButton) {
		this.volume = volume;
		this.playButton = playButton;
		radioPlayer = IRadioPlayer.getInstance;
		coverManager = ICoverArtManager.getInstance;
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
		volume.setValue(MAX_VOL);
		radioPlayer.setVolume(MAX_VOL);
	}

	public void changeVolume() {
		radioPlayer.setVolume((int) volume.getValue());
	}

	private void subscribeAllObservers() {
		coverInfoStream = PublishSubject.create();
		coverInfoStream.subscribe(coverInfoObserver);

		dockInfoStream = PublishSubject.create();
		dockInfoStream.subscribe(dockInfoObserver);

		playerInfoStream = PublishSubject.create();
		playerInfoStream.subscribe(playerInfoObserver);

		codecInfoStream = PublishSubject.create();
		codecInfoStream.subscribe(codecInfoObserver);
	}

	public void play(Station station) {
		radioPlayer.play(station.getStream());

		// subscribe all observers to new streams of information
		subscribeAllObservers();

		// Get the media stream observable and subscribe to it
		radioPlayer.getMediaObservable()//
				.subscribe(this::onMediaChange, this::onMediaError, this::onMediaStop);

		// Create a observable stream with the new codec calculated
		Observable.just(radioPlayer)//
				.flatMap(rp -> rp.calculateCodec()//
						.map(CodecInformation::new)//
						.map(Observable::just)//
						.orElseGet(Observable::empty))//
				.subscribeOn(Schedulers.computation())//
				.observeOn(JavaFxScheduler.platform())//
				.subscribe(codecInfoStream::onNext);

		enableStop();
	}

	private final Function<String, RadioAlbum> defaultRadioAlbum = radioName -> new RadioAlbum(
			Optional.of(new Radio.Builder().name(radioName)//
					.logoUri(Optional.empty())//
					.build()),
			Optional.empty());

	private void onMediaChange(MediaPlayerInformation mediaPlayerInformation) {
		/*
		 * We need three new observable streams: RadioPlayerInformation stream
		 * CoverInformation stream DockInformation stream
		 */

		// we need an entity that can have a radio and album info
		String song = mediaPlayerInformation.getSong();
		String artist = mediaPlayerInformation.getArtist();
		String radioName = mediaPlayerInformation.getRadio();

		ConnectableObservable<RadioAlbum> radioAlbumStream = Observable.just(mediaPlayerInformation)//
				.flatMap(mpi -> coverManager.getAlbumWithCover(mpi.getSong(), mpi.getArtist())//
						.map(a -> new RadioAlbum(Optional.empty(), Optional.of(a)))//
						.map(Observable::just)//
						.orElseGet(() -> coverManager.getRadioWithLogo(mpi.getRadio())//
								.map(radio -> new RadioAlbum(Optional.of(radio), Optional.empty()))
								.map(Observable::just)//
								.orElseGet(() -> Observable.just(defaultRadioAlbum.apply(radioName)))))
				.distinct()//
				.subscribeOn(Schedulers.single())//
				.observeOn(JavaFxScheduler.platform())//
				.publish();

		// subscribe radioPlayerInfo observer
		radioAlbumStream.subscribe(ra -> playerInfoStream
				.onNext(ra.album.map(a -> new PlayerInformation(a.getSongName(), a.getArtistName(), a.getAlbumName()))
						.orElseGet(() -> new PlayerInformation(song, artist, radioName))));

		// subscribe the cover art observer
		radioAlbumStream.subscribe(ra -> {
			PlayerImageInformation cover = ra.album.flatMap(Album::getCoverArt)//
					.flatMap(CoverArt::getLargeUri)//
					.flatMap(this::toURL)//
					.map(PlayerImageInformation::new)//
					.orElseGet(() -> ra.radio.flatMap(Radio::getLogoUri)//
							.flatMap(this::toURL)//
							.map(PlayerImageInformation::new)//
							.orElseGet(() -> PlayerImageInformation.DEFAULT));
			coverInfoStream.onNext(cover);

		});

		// Subscribe the dock observer
		radioAlbumStream.subscribe(ra -> {
			PlayerImageInformation dockArt = ra.radio.flatMap(Radio::getLogoUri)//
					.flatMap(this::toURL)//
					.map(PlayerImageInformation::new)//
					.orElseGet(() -> PlayerImageInformation.DEFAULT);
			dockInfoStream.onNext(dockArt);
		});

		radioAlbumStream.connect();
	}

	private Optional<URL> toURL(URI uri) {
		try {
			LoggerUtils.logDebug(logger, () -> "Converting: '" + uri + "' to URL");
			return Optional.of(uri.toURL());
		} catch (MalformedURLException e) {
			logger.error("There was an error converting URI to URL.", e);
			return Optional.empty();
		}
	}

	private static class RadioAlbum {
		final Optional<Album> album;
		final Optional<Radio> radio;

		public RadioAlbum(Optional<Radio> radio, Optional<Album> album) {
			this.radio = radio;
			this.album = album;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((album == null) ? 0 : album.hashCode());
			result = prime * result + ((radio == null) ? 0 : radio.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			RadioAlbum other = (RadioAlbum) obj;
			if (album == null) {
				if (other.album != null)
					return false;
			} else if (!album.equals(other.album))
				return false;
			if (radio == null) {
				if (other.radio != null)
					return false;
			} else if (!radio.equals(other.radio))
				return false;
			return true;
		}
	}

	private void onMediaError(Throwable e) {
		logger.error("The media player stopped unexpetedly. " + e.getMessage());
		// stop the player
		playerInfoStream.onError(e);
		codecInfoStream.onError(e);
		coverInfoStream.onError(e);
		dockInfoStream.onError(e);
		stop();
	}

	private void onMediaStop() {
		logger.info("Radio Player stopped...");
		playerInfoStream.onComplete();
		codecInfoStream.onComplete();
		coverInfoStream.onComplete();
		dockInfoStream.onComplete();
	}

	public void close() {
		radioPlayer.close();
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

	public void setRadioInfoObserver(Observer<PlayerInformation> observer) {
		this.playerInfoObserver = observer;
	}

	public void setCodecInfoObserver(Observer<CodecInformation> observer) {
		this.codecInfoObserver = observer;
	}

	public void setDockInfoObserver(Observer<PlayerImageInformation> observer) {
		this.dockInfoObserver = observer;
	}

	public void setCoverInfoObserver(Observer<PlayerImageInformation> observer) {
		this.coverInfoObserver = observer;
	}

}
