package bo.roman.radio.ui.controller.observers;

import bo.roman.radio.player.listener.Observer;
import bo.roman.radio.player.model.RadioPlayerEntity;
import bo.roman.radio.ui.controller.events.UpdateLabelsEvent;
import javafx.scene.Node;

public class RadioInfoObserver implements Observer<RadioPlayerEntity> {
	
	private final Node node;
	private final UpdateLabelsEvent event;
	
	public RadioInfoObserver(Node node) {
		this.node = node;
		event = new UpdateLabelsEvent(UpdateLabelsEvent.UPDATE_LABELS);
	}

	@Override
	public void update(RadioPlayerEntity rpe) {
		event.setRadioInfo(rpe);
		node.fireEvent(event);
	}

}
