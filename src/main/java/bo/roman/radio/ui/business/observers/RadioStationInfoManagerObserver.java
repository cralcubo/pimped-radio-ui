package bo.roman.radio.ui.business.observers;

import java.util.Optional;

import bo.radio.tuner.entities.Station;
import bo.roman.radio.cover.model.Radio;
import bo.roman.radio.player.listener.Observer;
import bo.roman.radio.player.model.CodecInformation;
import bo.roman.radio.player.model.RadioPlayerEntity;
import bo.roman.radio.ui.business.StationPlayingManager;

public class RadioStationInfoManagerObserver {

	public static Observer<RadioPlayerEntity> createRadioInfoObserver() {
		return (rpe) -> {
			Station station = StationPlayingManager.getCurrentStationPlaying().orElseGet(() -> new Station()); 
			Optional<Radio> oRadio = rpe.getRadio();
			if (oRadio.isPresent()) {
				Radio r = oRadio.get();
				station.setName(r.getName());
				station.setStream(r.getStreamUrl());
				StationPlayingManager.setCurrentStationPlaying(station);
			}
		};
	}

	public static Observer<CodecInformation> createCodecInfoObserver() {
		return (ci) -> {
			Station station = StationPlayingManager.getCurrentStationPlaying().orElseGet(() -> new Station());
			station.setBitRate(ci.getBitRate());
			station.setCodec(ci.getCodec());
			station.setSampleRate(ci.getSampleRate());
			StationPlayingManager.setCurrentStationPlaying(station);
		};
	}

}
