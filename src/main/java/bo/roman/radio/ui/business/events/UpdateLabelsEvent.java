package bo.roman.radio.ui.business.events;

import java.util.Optional;

import bo.roman.radio.player.model.CodecInformation;
import bo.roman.radio.ui.model.RadioPlayerInformation;
import javafx.event.Event;
import javafx.event.EventType;

public class UpdateLabelsEvent extends Event {
	private static final long serialVersionUID = 1L;
	
	public static final EventType<UpdateLabelsEvent> UPDATE_LABELS = new EventType<>("UPDATE_LABELS");
	
	private Optional<CodecInformation> codecInfo;
	private Optional<RadioPlayerInformation> radioInfo;

	public UpdateLabelsEvent(EventType<? extends Event> eventType) {
		super(eventType);
		codecInfo = Optional.empty();
		radioInfo = Optional.empty();
	}

	public Optional<CodecInformation> getCodecInfo() {
		return codecInfo;
	}

	public void setCodecInfo(Optional<CodecInformation> codecInfo) {
		this.codecInfo = codecInfo;
	}

	public Optional<RadioPlayerInformation> getRadioInfo() {
		return radioInfo;
	}

	public void setRadioInfo(Optional<RadioPlayerInformation> radioInfo) {
		this.radioInfo = radioInfo;
	}

}
