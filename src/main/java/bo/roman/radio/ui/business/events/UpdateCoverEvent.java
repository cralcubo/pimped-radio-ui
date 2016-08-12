package bo.roman.radio.ui.business.events;

import java.util.Optional;

import javafx.event.Event;
import javafx.event.EventType;

public class UpdateCoverEvent extends Event {
	private static final long serialVersionUID = 1L;

	public static final EventType<UpdateCoverEvent> UPDATE_IMAGE = new EventType<>("UPDATE_COVER");
	
	private Optional<String> imageUrl;

	public UpdateCoverEvent(EventType<? extends Event> eventType) {
		super(eventType);
		imageUrl = Optional.empty();
	}
	
	public Optional<String> getImageUrl() {
		return imageUrl;
	}
	
	public void setImageUrl(Optional<String> imageUrl) {
		this.imageUrl = imageUrl;
	}
}
