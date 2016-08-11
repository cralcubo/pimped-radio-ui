package bo.roman.radio.ui.model;

public class StationInformation {
	private String name;
	private String streamUrl;
	private String codec;
	private float sampleRate;
	private float bitRate;
	
	public StationInformation() {
		this("", "");
	}
	
	public StationInformation(String name, String streamUrl) {
		this.name = name;
		this.streamUrl = streamUrl;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStreamUrl() {
		return streamUrl;
	}
	public void setStreamUrl(String streamUrl) {
		this.streamUrl = streamUrl;
	}
	public String getCodec() {
		return codec;
	}
	public void setCodec(String codec) {
		this.codec = codec;
	}
	public float getSampleRate() {
		return sampleRate;
	}
	public void setSampleRate(float sampleRate) {
		this.sampleRate = sampleRate;
	}
	public float getBitRate() {
		return bitRate;
	}
	public void setBitRate(float bitRate) {
		this.bitRate = bitRate;
	}

	@Override
	public String toString() {
		return "StationInformation [name=" + name + ", streamUrl=" + streamUrl + ", codec=" + codec + ", sampleRate="
				+ sampleRate + ", bitRate=" + bitRate + "]";
	}

}
