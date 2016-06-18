package bo.roman.radio.ui.controller.observers;

import java.net.URI;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.CoverArt;
import bo.roman.radio.cover.model.Radio;
import bo.roman.radio.player.listener.Observer;
import bo.roman.radio.player.model.RadioPlayerEntity;
import bo.roman.radio.ui.controller.events.UpdateCoverEvent;
import bo.roman.radio.utilities.LoggerUtils;
import javafx.scene.Node;

public class CoverArtObserver implements Observer<RadioPlayerEntity> {
	private final static Logger logger = LoggerFactory.getLogger(CoverArtObserver.class);
	private final Node node;
	private final UpdateCoverEvent event;
	
	public CoverArtObserver(Node node) {
		this.node = node;
		event = new UpdateCoverEvent(UpdateCoverEvent.UPDATE_IMAGE);
	}
	
	@Override
	public void update(RadioPlayerEntity rpe) {
		Optional<URI> ca = rpe.getAlbum()
				.flatMap(Album::getCoverArt)
				.flatMap(CoverArt::getLargeUri);
		Optional<URI> cr = rpe.getRadio().flatMap(Radio::getLogoUri);
		
		LoggerUtils.logDebug(logger, () -> String.format("Updating CoverArt with Entities { Album[%s] , Radio[%s] }", ca, cr));
		
		Optional<URI> oUri = Optional.ofNullable(ca.orElseGet(() -> cr.orElse(null))); 
		Optional<String> oUrl = oUri.map(uri -> uri.toString());
		
		logger.info("Updating CoverArt with {}", oUrl);
		event.setImageUrl(oUrl);
		node.fireEvent(event);
	}

}
