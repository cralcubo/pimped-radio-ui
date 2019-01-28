package bo.roman.radio.ui.business.displayer;

import java.util.Optional;

import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.Radio;
import bo.roman.radio.ui.Initializable;
import bo.roman.radio.ui.model.RadioPlayerEntity;
import javafx.application.Platform;
import javafx.scene.control.Label;

public class SubInfoLabelsManager implements Initializable, Updateable {
	private static final String PIMPED_RADIO = "Pimped Radio";

	private Label artistLabel;
	private Label songLabel;
	private Label radioLabel;

	private static SubInfoLabelsManager instance;

	private SubInfoLabelsManager(Label artistLabel, Label songLabel, Label radioLabel) {
		this.artistLabel = artistLabel;
		this.songLabel = songLabel;
		this.radioLabel = radioLabel;
	}

	public static SubInfoLabelsManager getInstance(Label artistLabel, Label songLabel, Label radioLabel) {
		if (instance == null) {
			instance = new SubInfoLabelsManager(artistLabel, songLabel, radioLabel);
		}
		return instance;
	}

	@Override
	public void initialize() {
		artistLabel.setText("");
		songLabel.setText("");
		radioLabel.setText("");
	}

	@Override
	public void update(RadioPlayerEntity e) {
		Optional<RadioPlayerEntity> oe = Optional.of(e);

		String song = oe.map(RadioPlayerEntity::getAlbum)//
				.map(Album::getSongName)//
				.orElse("");
		String artist = oe.map(RadioPlayerEntity::getAlbum)//
				.map(Album::getArtistName)//
				.orElse("");
		String radio = oe.map(RadioPlayerEntity::getRadio)//
				.map(Radio::getName)//
				.orElse(PIMPED_RADIO);

		Platform.runLater(() -> {
			artistLabel.setText(artist);
			songLabel.setText(song);
			radioLabel.setText(radio);
		});
	}

}
