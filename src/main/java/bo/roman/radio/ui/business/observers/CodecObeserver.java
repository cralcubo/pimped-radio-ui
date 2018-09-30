package bo.roman.radio.ui.business.observers;

import static bo.roman.radio.utilities.LoggerUtils.logDebug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.ui.business.displayer.LabelsManager;
import bo.roman.radio.ui.model.CodecInformation;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class CodecObeserver implements Observer<CodecInformation> {
	private final static Logger logger = LoggerFactory.getLogger(CodecObeserver.class);
	
	private final LabelsManager labelsManager;
	
	public CodecObeserver(LabelsManager labelsManager) {
		this.labelsManager = labelsManager;
	}
	
	@Override
	public void onSubscribe(Disposable d) {
		logDebug(logger, () -> "Subscribed to Codec Observable Stream:" + d);
		labelsManager.clearCodec();
	}

	@Override
	public void onNext(CodecInformation c) {
		logDebug(logger, () -> "Codec changed...");
		labelsManager.updateCodecLabel(c.getCodec());
	}

	@Override
	public void onError(Throwable e) {
		labelsManager.clearCodec();
	}

	@Override
	public void onComplete() {
		logDebug(logger, () -> "Completed");
		labelsManager.clearCodec();
	}

}
