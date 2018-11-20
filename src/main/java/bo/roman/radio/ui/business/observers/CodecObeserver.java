package bo.roman.radio.ui.business.observers;

import static bo.roman.radio.utilities.LoggerUtils.logDebug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.player.model.Codec;
import bo.roman.radio.ui.business.displayer.LabelsManager;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class CodecObeserver implements Observer<Codec> {
	private final static Logger logger = LoggerFactory.getLogger(CodecObeserver.class);
	private final LabelsManager labelsManager;

	public CodecObeserver(LabelsManager labelsManager) {
		this.labelsManager = labelsManager;
	}

	@Override
	public void onSubscribe(Disposable d) {
		// nothing really to do here
	}

	@Override
	public void onNext(Codec c) {
		logDebug(logger, () -> "Codec changed:" + c);
		labelsManager.updateCodecLabel(c);
	}

	@Override
	public void onError(Throwable e) {
		labelsManager.updateCodecLabel("");
	}

	@Override
	public void onComplete() {
		logDebug(logger, () -> "Completed");
		labelsManager.updateCodecLabel("");
	}

}
