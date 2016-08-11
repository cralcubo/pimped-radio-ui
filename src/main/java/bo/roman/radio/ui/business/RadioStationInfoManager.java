package bo.roman.radio.ui.business;

import static bo.roman.radio.utilities.StringUtils.exists;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.ui.model.StationInformation;
import bo.roman.radio.utilities.LoggerUtils;

public class RadioStationInfoManager {
	private final static Logger logger = LoggerFactory.getLogger(RadioStationInfoManager.class);
	
	private final static String DEFAULT_STREAM = "http://stream-tx3.radioparadise.com/aac-320";
	
	private static StationInformation currentStationPlaying;
	
	public static Optional<StationInformation> getCurrentStationPlaying() {
		return Optional.ofNullable(currentStationPlaying);
	}

	public static void setCurrentStationPlaying(StationInformation val) {
		currentStationPlaying = val;
	}
	
	/**
	 * Get the current station playing with all its properties set.
	 * 
	 * Note:
	 * The information to be returned might wait 
	 * a couple of seconds to be complete.
	 * 
	 * @return
	 */
	public static Optional<StationInformation> getCompleteCurrentStationPlaying() {
		/*
		 * Check that all the information is set in StationInformation.
		 * We are doing this, because the Radio Station information and the 
		 * Codec information are asynchronously sent by the Radio Player.
		 */
		return getCompleteStationInformation(currentStationPlaying);
	}
	
	/**
	 * TODO
	 * Get this information from the DB
	 * @return
	 */
	public static Optional<StationInformation> getLastStationPlaying() {
		return Optional.of(new StationInformation("NO_NAME", DEFAULT_STREAM));
	}
	
	private static Optional<StationInformation> getCompleteStationInformation(StationInformation si) {
		if(si == null) {
			LoggerUtils.logDebug(logger, () -> "There is no StationInformation object to check if all the info is set in it or not.");
			return Optional.empty();
		}
		return checkCompleteStationInformation(si, 5, 0);
	}

	private static Optional<StationInformation> checkCompleteStationInformation(StationInformation si, int maxTries, int counter) {
		if(counter >= maxTries) {
			logger.warn("After {} tries, the information of the StationInformation object was not complete.", counter);
			return Optional.empty();
		}
		
		final int debugCounter = counter + 1;
		LoggerUtils.logDebug(logger, () -> String.format("Try [%d] to check information complete in %s", debugCounter, si));
		
		if(exists(si.getName()) &&  exists(si.getCodec()) && exists(si.getStreamUrl()) && si.getBitRate() != 0f && si.getSampleRate() != 0f) {
			return Optional.of(si);
		}
		
		try {
			// Wait for all the info to update the object
			long delay = (long) Math.pow(2, counter) * 500;
			TimeUnit.MILLISECONDS.sleep(delay);
		} catch (InterruptedException e) {
			logger.warn("There was an interruption in the delay set to wait for all the information to update StatusInformation.", e);
		}
		
		return checkCompleteStationInformation(si, maxTries, counter + 1);
	}

}
