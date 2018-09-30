package bo.roman.radio.ui.business.observers;

import static bo.roman.radio.utilities.LoggerUtils.logDebug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.ui.business.displayer.CoverArtManager;
import bo.roman.radio.ui.model.PlayerImageInformation;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class CoverArtObserver implements Observer<PlayerImageInformation> {
	private final static Logger logger = LoggerFactory.getLogger(CoverArtObserver.class);
	private final CoverArtManager coverManager;

	public CoverArtObserver(CoverArtManager coverManager) {
		this.coverManager = coverManager;
	}

	@Override
	public void onSubscribe(Disposable d) {
		logDebug(logger, () -> "Subscribed to Album Observable Stream");
		coverManager.initialize();
	}

	@Override
	public void onNext(PlayerImageInformation imageInfo) {
		logDebug(logger, () -> "Album playing has changed");
		coverManager.setImage(imageInfo.getImageUrl().toString());
	}

	@Override
	public void onError(Throwable e) {
		coverManager.initialize();
	}

	@Override
	public void onComplete() {
		logDebug(logger, () -> "Completed");
		coverManager.initialize();
	}

}
