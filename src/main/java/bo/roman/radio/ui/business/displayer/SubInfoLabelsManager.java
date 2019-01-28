package bo.roman.radio.ui.business.displayer;

import static java.lang.String.format;

import java.util.Optional;
import java.util.function.UnaryOperator;

import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.Radio;
import bo.roman.radio.ui.Initializable;
import bo.roman.radio.ui.model.RadioPlayerEntity;
import bo.roman.radio.utilities.StringUtils;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

public class SubInfoLabelsManager implements Initializable, Updateable {
	private static final String PIMPED_RADIO = "Pimped Radio";

	private Label artistLabel;
	private Label songLabel;
	private Label radioLabel;
	private UnaryOperator<String> labelFormatter = v -> format(" %s  ", v);
	
	private static SubInfoLabelsManager instance;

	private SubInfoLabelsManager(Label artistLabel, Label songLabel, Label radioLabel) {
		Background background = new Background(new BackgroundFill(new Color(0, 0, 0, 0.6), new CornerRadii(3), Insets.EMPTY));
		
		this.artistLabel = artistLabel;
		this.artistLabel.setBackground(background);
		
		this.songLabel = songLabel;
		this.songLabel.setBackground(background);
		
		this.radioLabel = radioLabel;
		this.radioLabel.setBackground(background);
		
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
				.orElse(null);
		String artist = oe.map(RadioPlayerEntity::getAlbum)//
				.map(Album::getArtistName)//
				.orElse(null);
		String radio = oe.map(RadioPlayerEntity::getRadio)//
				.map(Radio::getName)//
				.filter(StringUtils::exists)//
				.orElse(PIMPED_RADIO);
		Platform.runLater(() -> {
			displayer(artistLabel, artist);
			displayer(songLabel, song);
			displayer(radioLabel, radio);
		});
	}
	
	private void displayer(Label label, String val) {
		boolean valExists = StringUtils.exists(val);
		label.setVisible(valExists);
		label.setText(valExists ? labelFormatter.apply(val) : "");
	}

}
