package bo.roman.radio.ui.business;

import java.util.List;

import bo.radio.tuner.entities.Station;
import bo.roman.radio.player.IRadioPlayer;
import bo.roman.radio.player.RadioPlayer;
import bo.roman.radio.player.listener.Observer;
import bo.roman.radio.player.listener.RadioPlayerEventListener;
import bo.roman.radio.player.model.CodecInformation;
import bo.roman.radio.player.model.ErrorInformation;
import bo.roman.radio.player.model.RadioPlayerEntity;
import bo.roman.radio.ui.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

public class RadioPlayerManager implements Initializable {

	private static final int MAX_VOL = 100;

	private Slider volume;

	private IRadioPlayer radioPlayer;

	private ToggleButton playButton;
	
	public static RadioPlayerManager instance;

	private RadioPlayerManager(Slider volume, ToggleButton playButton) {
		this.volume = volume;
		this.playButton = playButton;
		radioPlayer = new RadioPlayer();
	}
	
	public static RadioPlayerManager getInstance() {
		return getInstance(null, null);
	}
	
	public static RadioPlayerManager getInstance(Slider vol, ToggleButton playButton) {
		if(instance == null) {
			if(vol == null && playButton == null) {
				throw new IllegalArgumentException("Initializing a RadioPlayerManager must have an instance of Slider and ToggleButton.");
			}
			instance = new RadioPlayerManager(vol, playButton);
		}
		return instance;
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

	public void play(Station station) {
		radioPlayer.play(station.getStream());
		enableStop();
	}

	public void close() {
		radioPlayer.close();
	}
	
	public void stop() {
		radioPlayer.stop();
		enablePlay();
	}
	
	private void enablePlay() {
		playButton.setSelected(false);
	}
	
	private void enableStop() {
		playButton.setSelected(true);
	}
}
