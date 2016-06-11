package bo.roman.radio.ui.controller;

import java.util.List;

import bo.roman.radio.player.IRadioPlayer;
import bo.roman.radio.player.RadioPlayer;
import bo.roman.radio.player.listener.Observer;
import bo.roman.radio.player.listener.RadioPlayerEventListener;
import bo.roman.radio.player.model.CodecInformation;
import bo.roman.radio.player.model.ErrorInformation;
import bo.roman.radio.player.model.RadioPlayerEntity;
import javafx.scene.control.Slider;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

public class RadioPlayerController implements Initializable {

	private static final int MAX_VOL = 100;

	private Slider volume;

	private IRadioPlayer radioPlayer;

	public RadioPlayerController(Slider volume) {
		this.volume = volume;
		radioPlayer = new RadioPlayer();
	}

	@Override
	public void initialize() {
		volume.setValue(MAX_VOL);
		radioPlayer.setVolume(MAX_VOL);
	}

	public void addObservers(List<Observer<RadioPlayerEntity>> playerEntityObservers, List<Observer<CodecInformation>> codecObservers, List<Observer<ErrorInformation>> errorObservers) {
		MediaPlayerEventAdapter eventsAdapter = new RadioPlayerEventListener(playerEntityObservers, codecObservers, errorObservers);
		radioPlayer.addEventsListener(eventsAdapter);
	}

	public void changeVolume() {
		radioPlayer.setVolume((int) volume.getValue());
	}

	public void play(String station) {
		radioPlayer.play(station);
	}

	public void close() {
		radioPlayer.close();
	}
	
	public void stop() {
		radioPlayer.stop();
	}
}
