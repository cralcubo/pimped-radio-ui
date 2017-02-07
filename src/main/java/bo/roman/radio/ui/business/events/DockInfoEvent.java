package bo.roman.radio.ui.business.events;

import java.util.Optional;

import bo.roman.radio.cover.model.Radio;
import javafx.event.Event;
import javafx.event.EventType;

public class DockInfoEvent extends Event {
	private static final long serialVersionUID = 5017864614832671302L;

	public static final EventType<DockInfoEvent> UPDATE_DOCK = new EventType<>("UPDATE_DOCK");

	private Optional<Radio> oRadio;

	public DockInfoEvent(EventType<? extends Event> eventType) {
		super(eventType);
	}

	public Optional<Radio> getRadio() {
		return oRadio;
	}

	public void setRadio(Optional<Radio> oRadio) {
		this.oRadio = oRadio;
	}

}
