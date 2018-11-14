package bo.roman.radio.ui.business.observers;

import static bo.roman.radio.utilities.LoggerUtils.logDebug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.ui.business.displayer.Updateable;
import bo.roman.radio.ui.model.RadioPlayerEntity;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class PimpedRadioObserver<M extends Updateable> implements Observer<RadioPlayerEntity> {
	private final static Logger logger = LoggerFactory.getLogger(PimpedRadioObserver.class);
	private final M manager;
	
	public PimpedRadioObserver(M manager) {
		this.manager = manager;
	}
	
	@Override
	public void onSubscribe(Disposable d) {
		// nothing to do here
	}

	@Override
	public void onNext(RadioPlayerEntity e) {
		logDebug(logger, () -> "onNext:" + e);
		manager.update(e);
	}

	@Override
	public void onError(Throwable e) {
		logDebug(logger, () -> "onError:" + e.getMessage());
		manager.update(RadioPlayerEntity.getErrorInstance());
	}

	@Override
	public void onComplete() {
		logDebug(logger, () -> "onComplete");
		manager.update(RadioPlayerEntity.getStopedInstance());
	}

}
