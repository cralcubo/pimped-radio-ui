package bo.roman.radio.ui.controller.util;

public class CodecFormatter {
	
	public static String formatCodec(String codec) {
		switch (codec) {
		case "mpga":
			return "mp3";
		case "mp4a":
			return "aac";
		default:
			return codec;
		}
	}
	
	public static String formatChannels(int channels) {
		switch (channels) {
		case 1:
			return "Mono";
		case 2:
			return "Stereo";
		default:
			return String.format("%d ch.", channels);
		}
	}
	
	public static String formatBitRate(float val) {
		return String.format("%d kbps", Math.round(val));
	}
	
	public static String formatSampleRate(float val) {
		return String.format("%.1f kHz", val);
	}

}
