package bo.roman.radio.ui.business;

import java.util.Optional;

public class RadioStreamManager {
	
	private final static String DEFAULT_STREAM = "http://stream-tx3.radioparadise.com/aac-320";
	
	private static Optional<String> lastStream = Optional.empty();
	
	public static void setLastStream(String aStream) {
		lastStream = Optional.ofNullable(aStream);
	}
	
	public static String getLastStream() {
		return lastStream.orElse(DEFAULT_STREAM);
	}
}
