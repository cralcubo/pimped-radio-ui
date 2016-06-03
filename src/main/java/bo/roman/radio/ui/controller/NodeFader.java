package bo.roman.radio.ui.controller;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class NodeFader {
	private Duration duration;
	
	public NodeFader(double fadeDuration) {
		duration = Duration.millis(fadeDuration);
	}
	
	public void fadeNode(double from, double to, Node node) {
		if(to != node.getOpacity())
		{
			FadeTransition transition = new FadeTransition(duration, node);
			transition.setFromValue(from);
			transition.setToValue(to);
			transition.play();
		}
	}

}
