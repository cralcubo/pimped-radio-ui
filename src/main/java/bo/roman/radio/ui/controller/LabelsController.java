package bo.roman.radio.ui.controller;

import java.util.Optional;

import bo.roman.radio.player.model.CodecInformation;
import bo.roman.radio.ui.model.RadioInformation;
import bo.roman.radio.utilities.StringUtils;
import javafx.application.Platform;
import javafx.scene.control.Label;

public class LabelsController implements Initializable {
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

	public void updateLabels(Optional<CodecInformation> codecInfo, Optional<RadioInformation> radioInfo) {
		codecInfo.ifPresent(this::updateCodecLabel);
		radioInfo.ifPresent(this::updateRadioInfoLabels);
	}
	
	private void updateCodecLabel(CodecInformation ci) {
		// acc | 320 kbps | 44.1 kHz | Stereo
		String codecInfo = String.format("%s | %d kbps | %.1f kHz | %s", codecTranslator(ci.getCodec()), Math.round(ci.getBitRate()), ci.getSampleRate(), channelsTranslator(ci.getChannels()));
		Platform.runLater(() -> codecLabel.setText(codecInfo));
	}

	private void updateRadioInfoLabels(RadioInformation ri) {
		Platform.runLater(() -> {
			mainLabel.setText(StringUtils.removeBrackets(ri.getMainInfo()));
			subLabel.setText(StringUtils.removeBrackets(ri.getSubInfo()));
			extraLabel.setText(StringUtils.removeBrackets(ri.getExtraInfo()));
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
		case "vorb":
			return "vorbis";
		default:
			return codec;
		}
	}

}
