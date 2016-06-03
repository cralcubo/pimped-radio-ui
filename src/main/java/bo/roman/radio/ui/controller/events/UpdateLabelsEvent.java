package bo.roman.radio.ui.controller.events;

import bo.roman.radio.player.model.CodecInformation;
import bo.roman.radio.ui.model.RadioInformation;
import javafx.event.Event;
import javafx.event.EventType;

public class UpdateLabelsEvent extends Event {
	private static final long serialVersionUID = 1L;
	
	public static final EventType<UpdateLabelsEvent> UPDATE_LABELS = new EventType<>("UPDATE_LABELS");
	
	private CodecInformation codecInfo;
	private RadioInformation radioInfo;

	public UpdateLabelsEvent(EventType<? extends Event> eventType) {
		super(eventType);
	}

	public CodecInformation getCodecInfo() {
		return codecInfo;
	}
	
	public void setCodecInfo(CodecInformation codecInfo) {
		this.codecInfo = codecInfo;
	}

	public RadioInformation getRadioInfo() {
		return radioInfo;
	}
	
	public void setRadioInfo(RadioInformation radioInfo) {
		this.radioInfo = radioInfo;
	}

}
