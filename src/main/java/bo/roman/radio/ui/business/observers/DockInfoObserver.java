package bo.roman.radio.ui.business.observers;

import bo.roman.radio.ui.business.displayer.DockInfoManager;
import bo.roman.radio.ui.model.PlayerImageInformation;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class DockInfoObserver implements Observer<PlayerImageInformation> {
	private final DockInfoManager dockManager;
	
	public DockInfoObserver(DockInfoManager dockManager) {
		this.dockManager = dockManager;
	}

	@Override
	public void onSubscribe(Disposable d) {
		dockManager.initialize();
	}

	@Override
	public void onNext(PlayerImageInformation dai) {
		dockManager.update(dai.getImageUrl());
	}

	@Override
	public void onError(Throwable e) {
		dockManager.initialize();
	}

	@Override
	public void onComplete() {
		dockManager.initialize();
	}
	
}
