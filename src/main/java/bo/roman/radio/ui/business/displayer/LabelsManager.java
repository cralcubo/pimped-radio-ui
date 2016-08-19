package bo.roman.radio.ui.business.displayer;

import java.util.Optional;

import bo.roman.radio.player.model.CodecInformation;
import bo.roman.radio.player.model.ErrorInformation;
import bo.roman.radio.ui.Initializable;
import bo.roman.radio.ui.controller.util.CodecFormatter;
import bo.roman.radio.ui.model.RadioPlayerInformation;
import bo.roman.radio.utilities.StringUtils;
import javafx.application.Platform;
import javafx.scene.control.Label;

public class LabelsManager implements Initializable {
	private static final String STOP_MSG = "Stoping RadioPlayer.";
	
	private Label mainLabel;
	private Label subLabel;
	private Label extraLabel;
	private Label codecLabel;
	
	private static LabelsManager instance;

	private LabelsManager(Label mainLabel, Label subLabel, Label extraLabel, Label codecLabel) {
		this.mainLabel = mainLabel;
		this.subLabel = subLabel;
		this.extraLabel = extraLabel;
		this.codecLabel = codecLabel;
	}
	
	public static LabelsManager getInstance() {
		return getInstance(null, null, null, null);
	}
	
	public static LabelsManager getInstance(Label mainLabel, Label subLabel, Label extraLabel, Label codecLabel) {
		if(instance == null) {
			if(mainLabel == null || subLabel == null || extraLabel == null || codecLabel == null) {
				throw new IllegalStateException("A new instance of LabelsManager was tried to be created, but not all the Label objects were provided.");
			}
			instance = new LabelsManager(mainLabel, subLabel, extraLabel, codecLabel);
		}
		return instance;
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
		String codecInfo = String.format("%s | %s | %s | %s", CodecFormatter.formatCodec(ci.getCodec()), CodecFormatter.formatBitRate(ci.getBitRate()), CodecFormatter.formatSampleRate(ci.getSampleRate()), CodecFormatter.formatChannels(ci.getChannels()));
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
}
