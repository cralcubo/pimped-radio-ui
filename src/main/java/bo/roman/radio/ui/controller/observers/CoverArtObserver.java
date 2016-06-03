package bo.roman.radio.ui.controller.observers;

import java.net.URI;
import java.nio.file.Paths;
import java.util.Optional;

import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.CoverArt;
import bo.roman.radio.cover.model.Radio;
import bo.roman.radio.player.listener.Observer;
import bo.roman.radio.player.model.RadioPlayerEntity;
import bo.roman.radio.ui.controller.events.UpdateCoverEvent;
import javafx.scene.Node;

public class CoverArtObserver implements Observer<RadioPlayerEntity> {
	private static final String DEFAULTLOGO_PATH = "src/main/resources/pimped-radio-glossy.jpeg";
	
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
		
		String cover = ca.orElse(cr.orElse(Paths.get(DEFAULTLOGO_PATH).toUri())).toString();
		event.setImageUrl(cover);
		node.fireEvent(event);
	}

}
