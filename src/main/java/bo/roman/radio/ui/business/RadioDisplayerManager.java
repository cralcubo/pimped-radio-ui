package bo.roman.radio.ui.business;

import bo.roman.radio.ui.Initializable;
import bo.roman.radio.ui.business.displayer.CoverArtManager;
import bo.roman.radio.ui.business.displayer.DockInfoManager;
import bo.roman.radio.ui.business.displayer.LabelsManager;
import bo.roman.radio.ui.controller.util.NodeFader;
import javafx.scene.layout.GridPane;

public class RadioDisplayerManager implements Initializable {
	private static final double MAXOPACITY_INFO = 1.0;
	private static final double MINOPACITY_INFO = 0.0;
	
	private final NodeFader fader = new NodeFader(200);
	
	private GridPane controlsPane;
	private GridPane subInfoPane;
	
	private static RadioDisplayerManager instance;
	
	private RadioDisplayerManager(GridPane controlsPane, GridPane subInfoPane) {
		this.controlsPane = controlsPane;
		this.subInfoPane = subInfoPane;
	}
	
	public static RadioDisplayerManager getInstance() {
		return getInstance(null, null);
	}
	
	public static RadioDisplayerManager getInstance(GridPane controlsPane, GridPane subInfoPane) {
		if(instance == null) {
			if(controlsPane == null) {
				throw new IllegalStateException("A new instance of RadioDisplayerManager was tried to be created, but no GridPane was provided.");
			}
			instance = new RadioDisplayerManager(controlsPane, subInfoPane);
		}
		return instance;
	}
	
	@Override
	public void initialize() {
		// Nothing to do
	}
	
	public void displayPlayerInformation() {
		CoverArtManager.getInstance().shadeIt(fader);
		// Make Player Controls and Song Info appear 
		fader.fadeNode(MINOPACITY_INFO, MAXOPACITY_INFO, controlsPane);
		// Make subInfo disappear
		fader.fadeNode(MAXOPACITY_INFO, MINOPACITY_INFO, subInfoPane);
	}
	
	public void clearPlayerInformation() {
		CoverArtManager.getInstance().clearIt(fader);
		// Make Player Controls and Song Info disappear
		fader.fadeNode(MAXOPACITY_INFO, MINOPACITY_INFO, controlsPane);
		// Make subInfo appear
		fader.fadeNode(MINOPACITY_INFO, MAXOPACITY_INFO, subInfoPane);
	}
	
	public void unblurPlayerInformation() {
		CoverArtManager.getInstance().shadeUnblurIt(fader);
		// Make Player Controls and Song Info appear 
		fader.fadeNode(MINOPACITY_INFO, MAXOPACITY_INFO, controlsPane);
	}
	
	public void reloadUI() {
		DockInfoManager.getInstance().initialize();
		CoverArtManager.getInstance().initialize();
		LabelsManager.getInstance().initialize();
		AddEditButtonManager.getInstance().disableButton();
	}
}
