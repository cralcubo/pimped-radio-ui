package bo.roman.radio.ui.controller;

import java.util.List;

import bo.roman.radio.player.IRadioPlayer;
import bo.roman.radio.player.listener.Observer;
import bo.roman.radio.player.listener.RadioPlayerEventListener;
import bo.roman.radio.player.model.CodecInformation;
import bo.roman.radio.player.model.RadioPlayerEntity;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

public class RadioPlayerController implements Initializable {
	
	private Button close;
	private Button load;
	private ToggleButton play;
	private Slider volume;
	
	private IRadioPlayer radioPlayer;
	
	public RadioPlayerController(Button close, Button load, ToggleButton play, Slider volume) {
		this.close = close;
		this.load = load;
		this.play = play;
		this.volume = volume;
	}

	@Override
	public void initialize() {
		
	}
	
	public void addObservers(List<Observer<RadioPlayerEntity>> playerEntityObservers, List<Observer<CodecInformation>> codecObservers) {
		MediaPlayerEventAdapter eventsAdapter = new RadioPlayerEventListener(playerEntityObservers, codecObservers);
		radioPlayer.addEventsListener(eventsAdapter);
	}
	
	public void play(String radioStationUrl) {
		radioPlayer.play(radioStationUrl);
	}

}
