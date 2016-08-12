package bo.roman.radio.ui.business.observers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.player.listener.Observer;
import bo.roman.radio.player.model.ErrorInformation;
import bo.roman.radio.ui.business.events.ReportErrorEvent;
import javafx.scene.Node;

public class ErrorObserver implements Observer<ErrorInformation> {
	private final static Logger logger = LoggerFactory.getLogger(ErrorObserver.class);
	
	private final Node node;
	private final ReportErrorEvent event;
	
	public ErrorObserver(Node node) {
		this.node = node;
		event = new ReportErrorEvent(ReportErrorEvent.REPORT_ERROR);
	}

	@Override
	public void update(ErrorInformation e) {
		logger.info("Updating UI with {}", e);
		
		event.setErrorInformation(e);
		node.fireEvent(event);
	}

}
