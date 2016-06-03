package bo.roman.radio.ui.controller.events;

import javafx.event.Event;
import javafx.event.EventType;

public class UpdateCoverEvent extends Event {
	private static final long serialVersionUID = 1L;

	public static final EventType<UpdateCoverEvent> UPDATE_IMAGE = new EventType<>("UPDATE_COVER");
	
	private String imageUrl;

	public UpdateCoverEvent(EventType<? extends Event> eventType) {
		super(eventType);
	}
	
	public String getImageUrl() {
		return imageUrl;
	}
	
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
}
