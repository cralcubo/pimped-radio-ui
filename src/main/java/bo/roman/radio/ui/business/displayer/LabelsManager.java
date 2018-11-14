package bo.roman.radio.ui.business.displayer;

import java.util.Optional;

import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.Radio;
import bo.roman.radio.player.model.Codec;
import bo.roman.radio.ui.Initializable;
import bo.roman.radio.ui.controller.util.CodecFormatter;
import bo.roman.radio.ui.model.RadioPlayerEntity;
import javafx.application.Platform;
import javafx.scene.control.Label;

public class LabelsManager implements Initializable, Updateable {
	private static final String PIMPED_RADIO = "Pimped Radio";

	private Label songLabel;
	private Label artistLabel;
	private Label albumRadioLabel;
	private Label codecLabel;

	private static LabelsManager instance;

	private LabelsManager(Label mainLabel, Label subLabel, Label extraLabel, Label codecLabel) {
		this.songLabel = mainLabel;
		this.artistLabel = subLabel;
		this.albumRadioLabel = extraLabel;
		this.codecLabel = codecLabel;
	}

	public static LabelsManager getInstance() {
		return getInstance(null, null, null, null);
	}

	public static LabelsManager getInstance(Label mainLabel, Label subLabel, Label extraLabel, Label codecLabel) {
		if (instance == null) {
			if (mainLabel == null || subLabel == null || extraLabel == null || codecLabel == null) {
				throw new IllegalStateException(
						"A new instance of LabelsManager was tried to be created, but not all the Label objects were provided.");
			}
			instance = new LabelsManager(mainLabel, subLabel, extraLabel, codecLabel);
		}
		return instance;
	}

	@Override
	public void initialize() {
		songLabel.setText("");
		artistLabel.setText("");
		albumRadioLabel.setText("");
		codecLabel.setText("");
	}

	public void clearCodec() {
		codecLabel.setText("");
	}

	public void updateCodecLabel(Codec ci) {
		// acc | 320 kbps | 44.1 kHz | Stereo
		String codecInfo = String.format("%s | %s | %s | %s", CodecFormatter.formatCodec(ci.getCodec()),
				CodecFormatter.formatBitRate(ci.getBitRate()), CodecFormatter.formatSampleRate(ci.getSampleRate()),
				CodecFormatter.formatChannels(ci.getChannels()));
		updateCodecLabel(codecInfo);
	}

	public void updateCodecLabel(String val) {
		Platform.runLater(() -> codecLabel.setText(val));
	}

	public void update(RadioPlayerEntity e) {
		Optional<RadioPlayerEntity> oe = Optional.of(e);

		String song = oe.map(RadioPlayerEntity::getAlbum)//
				.map(Album::getSongName)//
				.orElse("");
		String artist = oe.map(RadioPlayerEntity::getAlbum)//
				.map(Album::getArtistName)//
				.orElse("");
		String album = oe.map(RadioPlayerEntity::getAlbum)//
				.map(Album::getAlbumName)//
				.orElseGet(() -> oe.map(RadioPlayerEntity::getRadio)//
						.map(Radio::getName)//
						.orElse(PIMPED_RADIO));

		Platform.runLater(() -> {
			songLabel.setText(song);
			artistLabel.setText(artist);
			albumRadioLabel.setText(album);
		});
	}

}
