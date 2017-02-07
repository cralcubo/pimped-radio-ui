package bo.roman.radio.ui.business.observers;

import java.util.Optional;

import bo.roman.radio.cover.model.Radio;
import bo.roman.radio.player.listener.Observer;
import bo.roman.radio.player.model.RadioPlayerEntity;
import bo.roman.radio.ui.business.events.DockInfoEvent;
import javafx.scene.Node;

public class DockInfoObserver implements Observer<RadioPlayerEntity> {
	private final Node node;
	private final DockInfoEvent event;

	public DockInfoObserver(final Node node) {
		this.node = node;
		event = new DockInfoEvent(DockInfoEvent.UPDATE_DOCK);
	}

	@Override
	public void update(RadioPlayerEntity entity) {
		Optional<Radio> oRadio = entity.getRadio();
		event.setRadio(oRadio);
		node.fireEvent(event);
	}
}
