package bo.roman.radio.ui.controller.observers;

import java.util.Optional;

import bo.roman.radio.cover.model.Radio;
import bo.roman.radio.player.listener.Observer;
import bo.roman.radio.player.model.CodecInformation;
import bo.roman.radio.player.model.RadioPlayerEntity;
import bo.roman.radio.ui.business.RadioStationInfoManager;
import bo.roman.radio.ui.model.StationInformation;

public class RadioStationInfoManagerObserver {

	private final StationInformation stationInformation;

	public RadioStationInfoManagerObserver(StationInformation stationInformation) {
		this.stationInformation = stationInformation;
	}

	public Observer<RadioPlayerEntity> createRadioInfoObserver() {
		return (rpe) -> {
			Optional<Radio> oRadio = rpe.getRadio();
			if (oRadio.isPresent()) {
				Radio r = oRadio.get();
				stationInformation.setName(r.getName());
				stationInformation.setStreamUrl(r.getStreamUrl());
				RadioStationInfoManager.setCurrentStationPlaying(stationInformation);
			}
		};
	}

	public Observer<CodecInformation> createCodecInfoObserver() {
		return (ci) -> {
			stationInformation.setBitRate(ci.getBitRate());
			stationInformation.setCodec(ci.getCodec());
			stationInformation.setSampleRate(ci.getSampleRate());
			RadioStationInfoManager.setCurrentStationPlaying(stationInformation);
		};
	}

}
