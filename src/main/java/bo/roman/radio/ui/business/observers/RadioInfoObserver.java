package bo.roman.radio.ui.business.observers;

import static bo.roman.radio.utilities.LoggerUtils.logDebug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.ui.business.displayer.LabelsManager;
import bo.roman.radio.ui.model.PlayerInformation;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class RadioInfoObserver implements Observer<PlayerInformation> {
	private final static Logger logger = LoggerFactory.getLogger(RadioInfoObserver.class);
	private final PlayerInformation EMPTY = new PlayerInformation("", "", "Pimped Radio");
	private final PlayerInformation ERROR = new PlayerInformation("", "", "Error playing the station");

	private final LabelsManager labelsManager;

	public RadioInfoObserver(LabelsManager labelsManager) {
		this.labelsManager = labelsManager;
	}

	@Override
	public void onSubscribe(Disposable d) {
		logDebug(logger, () -> "Subscribed to:" + d);
		labelsManager.updateRadioInfoLabels(EMPTY);
	}

	@Override
	public void onNext(PlayerInformation rpi) {
		logDebug(logger, () -> "on Next:" + rpi);
		labelsManager.updateRadioInfoLabels(rpi);
	}

	@Override
	public void onError(Throwable e) {
		labelsManager.updateRadioInfoLabels(ERROR);
	}

	@Override
	public void onComplete() {
		logDebug(logger, () -> "Completed");
		labelsManager.updateRadioInfoLabels(EMPTY);
	}

}
