package bo.roman.radio.ui.business.displayer;

import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;

import com.apple.eawt.Application;

import bo.roman.radio.ui.Initializable;
import bo.roman.radio.ui.model.PlayerImageInformation;

public class DockInfoManager implements Initializable {
	private static final ImageIcon DEFAULT_IMAGE = new ImageIcon(PlayerImageInformation.DEFAULT.getImageUrl());
	private static DockInfoManager instance;
	
	public static DockInfoManager getInstance() {
		if (instance == null) {
			instance = new DockInfoManager();
		}
		return instance;
	}

	@Override
	public void initialize() {
		setDockImage(DEFAULT_IMAGE.getImage());
	}

	public void update(URL imageUrl) {
		setDockImage(new ImageIcon(imageUrl).getImage());
	}

	private void setDockImage(Image img) {
		Application app = Application.getApplication();
		if (app != null) {
			app.setDockIconImage(img);
		}
	}
}
