package bo.roman.radio.ui.business.observers;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.Radio;
import bo.roman.radio.cover.model.Song;
import bo.roman.radio.player.listener.Observer;
import bo.roman.radio.player.model.RadioPlayerEntity;
import bo.roman.radio.ui.business.events.UpdateLabelsEvent;
import bo.roman.radio.ui.model.RadioPlayerInformation;
import bo.roman.radio.utilities.LoggerUtils;
import javafx.scene.Node;

public class RadioInfoObserver implements Observer<RadioPlayerEntity> {
	private final static Logger logger = LoggerFactory.getLogger(RadioInfoObserver.class);
	
	private final Node node;
	private final UpdateLabelsEvent event;
	
	public RadioInfoObserver(Node node) {
		this.node = node;
		event = new UpdateLabelsEvent(UpdateLabelsEvent.UPDATE_LABELS);
	}

	@Override
	public void update(RadioPlayerEntity rpe) {
		LoggerUtils.logDebug(logger, () -> "Updating RadioInfo with Entity=" + rpe);
		// Convert RadioPlayerEntity to a RadioPlayerInformation.
		Optional<Album> album = rpe.getAlbum();
		Optional<Song> song = rpe.getSong();
		Optional<Radio> radio = rpe.getRadio();
		
		RadioPlayerInformation ri = album.map(this::toRadioInfo).orElseGet(() -> toRadioInfo(radio, song));
		logger.info("Updating RadioInfo with {}", ri);
		event.setRadioInfo(Optional.of(ri));
		node.fireEvent(event);
	}
	
	private RadioPlayerInformation toRadioInfo(Album album) {
		return new RadioPlayerInformation(album.getSongName(), album.getArtistName(), album.getAlbumName());
	}
	
	private RadioPlayerInformation toRadioInfo(Optional<Radio> radio, Optional<Song> song) {
		String radioName = radio.map(Radio::getName).orElse("");
		if(song.isPresent()) {
			Song infoSong = song.get();
			return new RadioPlayerInformation(infoSong.getName(), infoSong.getArtist(), radioName);
		}
		
		return new RadioPlayerInformation(radioName);
	}

}
