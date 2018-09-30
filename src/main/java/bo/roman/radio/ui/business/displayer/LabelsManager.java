package bo.roman.radio.ui.business.displayer;

import bo.roman.radio.player.model.Codec;
import bo.roman.radio.ui.Initializable;
import bo.roman.radio.ui.controller.util.CodecFormatter;
import bo.roman.radio.ui.model.PlayerInformation;
import javafx.scene.control.Label;

public class LabelsManager implements Initializable {
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
	
	public void clearCodec() {
		codecLabel.setText("");
	}
	
	public void updateCodecLabel(Codec ci) {
		// acc | 320 kbps | 44.1 kHz | Stereo
		String codecInfo = String.format("%s | %s | %s | %s", CodecFormatter.formatCodec(ci.getCodec()), CodecFormatter.formatBitRate(ci.getBitRate()), CodecFormatter.formatSampleRate(ci.getSampleRate()), CodecFormatter.formatChannels(ci.getChannels()));
		codecLabel.setText(codecInfo);
	}

	public void updateRadioInfoLabels(PlayerInformation ri) {
		mainLabel.setText(ri.getMainInfo());
		subLabel.setText(ri.getSubInfo());
		extraLabel.setText(ri.getExtraInfo());
	}
}
