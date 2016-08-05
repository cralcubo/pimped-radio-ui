package bo.roman.radio.ui.controller;

import java.util.Optional;

import bo.roman.radio.player.model.CodecInformation;
import bo.roman.radio.player.model.ErrorInformation;
import bo.roman.radio.ui.model.RadioPlayerInformation;
import bo.roman.radio.utilities.StringUtils;
import javafx.application.Platform;
import javafx.scene.control.Label;

public class LabelsController implements Initializable {
	private static final String STOP_MSG = "Stoping RadioPlayer.";
	
	private Label mainLabel;
	private Label subLabel;
	private Label extraLabel;
	private Label codecLabel;

	public LabelsController(Label mainLabel, Label subLabel, Label extraLabel, Label codecLabel) {
		this.mainLabel = mainLabel;
		this.subLabel = subLabel;
		this.extraLabel = extraLabel;
		this.codecLabel = codecLabel;
	}

	@Override
	public void initialize() {
		mainLabel.setText("");
		subLabel.setText("");
		extraLabel.setText("");
		codecLabel.setText("");
	}

	public void updateLabels(Optional<CodecInformation> codecInfo, Optional<RadioPlayerInformation> radioInfo) {
		codecInfo.ifPresent(this::updateCodecLabel);
		radioInfo.ifPresent(this::updateRadioInfoLabels);
	}
	
	private void updateCodecLabel(CodecInformation ci) {
		// acc | 320 kbps | 44.1 kHz | Stereo
		String codecInfo = String.format("%s | %d kbps | %.1f kHz | %s", codecTranslator(ci.getCodec()), Math.round(ci.getBitRate()), ci.getSampleRate(), channelsTranslator(ci.getChannels()));
		Platform.runLater(() -> codecLabel.setText(codecInfo));
	}
	
	public void reportError(ErrorInformation e) {
		Platform.runLater(() -> {
			mainLabel.setText(e.getSource());
			subLabel.setText(e.getMessage());
			extraLabel.setText(STOP_MSG);
		});
	}

	private void updateRadioInfoLabels(RadioPlayerInformation ri) {
		Platform.runLater(() -> {
			mainLabel.setText(StringUtils.removeBracketsInfo(ri.getMainInfo()));
			subLabel.setText(StringUtils.removeBracketsInfo(ri.getSubInfo()));
			extraLabel.setText(StringUtils.removeBracketsInfo(ri.getExtraInfo()));
		});
	}
	
	private String channelsTranslator(int channels) {
		switch (channels) {
		case 1:
			return "Mono";
		case 2:
			return "Stereo";
		default:
			return String.format("%d ch.", channels);
		}
	}

	private String codecTranslator(String codec) {
		switch (codec) {
		case "mpga":
			return "mp3";
		case "mp4a":
			return "aac";
		default:
			return codec;
		}
	}

}
