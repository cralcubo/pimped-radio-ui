package bo.roman.radio.ui.business;

import bo.roman.radio.ui.Initializable;
import javafx.scene.layout.GridPane;

public class RadioInfoManager implements Initializable {
	
	private final GridPane infoPane;
	private static RadioInfoManager instance;
	
	private RadioInfoManager(GridPane infoPane) {
		this.infoPane = infoPane;
	}
	
	public static RadioInfoManager getInstance(GridPane infoPane) {
		if(instance == null) {
			instance = new RadioInfoManager(infoPane);
		}
		return instance;
	}

	@Override
	public void initialize() {
	}
	
	
	
	
	
	
	
	
	
	

}
