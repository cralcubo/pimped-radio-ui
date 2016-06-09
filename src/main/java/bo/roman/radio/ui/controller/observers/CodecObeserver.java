package bo.roman.radio.ui.controller.observers;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.player.listener.Observer;
import bo.roman.radio.player.model.CodecInformation;
import bo.roman.radio.ui.controller.events.UpdateLabelsEvent;
import javafx.scene.Node;

public class CodecObeserver implements Observer<CodecInformation> {
	private final static Logger loggger = LoggerFactory.getLogger(CodecObeserver.class);
	
	private final Node node;
	private final UpdateLabelsEvent event;
	
	public CodecObeserver(Node node) {
		this.node = node;
		event = new UpdateLabelsEvent(UpdateLabelsEvent.UPDATE_LABELS);
	}

	@Override
	public void update(CodecInformation codecInformation) {
		loggger.info("Updating CodecInformation with Entity {}", codecInformation);
		event.setCodecInfo(Optional.of(codecInformation));
		node.fireEvent(event);
	}

}
