package bo.roman.radio.ui.controller;

import java.util.List;

import bo.roman.radio.player.IRadioPlayer;
import bo.roman.radio.player.RadioPlayer;
import bo.roman.radio.player.listener.Observer;
import bo.roman.radio.player.listener.RadioPlayerEventListener;
import bo.roman.radio.player.model.CodecInformation;
import bo.roman.radio.player.model.RadioPlayerEntity;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

public class RadioPlayerController implements Initializable {

	private static final int MAX_VOL = 100;

	private ToggleButton play;
	private Slider volume;

	private IRadioPlayer radioPlayer;

	public RadioPlayerController(ToggleButton play, Slider volume) {
		this.play = play;
		this.volume = volume;
		radioPlayer = new RadioPlayer();
	}

	@Override
	public void initialize() {
		volume.setValue(MAX_VOL);
		radioPlayer.setVolume(MAX_VOL);
	}

	public void addObservers(List<Observer<RadioPlayerEntity>> playerEntityObservers, List<Observer<CodecInformation>> codecObservers) {
		MediaPlayerEventAdapter eventsAdapter = new RadioPlayerEventListener(playerEntityObservers, codecObservers);
		radioPlayer.addEventsListener(eventsAdapter);
	}

	public void changeVolume() {
		radioPlayer.setVolume((int) volume.getValue());
	}

	public void playAction(String station) {
		if (play.isSelected()) {
			radioPlayer.play(station);
		} else {
			radioPlayer.stop();
		}
	}

	public void close() {
		radioPlayer.close();
	}
}
