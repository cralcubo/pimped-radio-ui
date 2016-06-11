package bo.roman.radio.ui.controller.events;

import bo.roman.radio.player.model.ErrorInformation;
import javafx.event.Event;
import javafx.event.EventType;

public class ReportErrorEvent extends Event {
	private static final long serialVersionUID = 1L;
	
	public static final EventType<ReportErrorEvent> REPORT_ERROR = new EventType<>("REPORT_ERROR");
	
	private ErrorInformation error;

	public ReportErrorEvent(EventType<? extends Event> eventType) {
		super(eventType);
		
	}
	
	public ErrorInformation getErrorInformation() {
		return error;
	}
	
	public void setErrorInformation(ErrorInformation e) {
		error = e;
	}


}
