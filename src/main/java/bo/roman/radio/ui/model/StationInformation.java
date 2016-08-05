package bo.roman.radio.ui.model;

public class StationInformation {
	private String name;
	private String url;
	private String codec;
	private float sampleRate;
	private float bitRate;

	private StationInformation(Builder b) {
		name = b.name;
		url = b.url;
		codec = b.codec;
		sampleRate = b.sampleRate;
		bitRate = b.bitRate;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	public String getCodec() {
		return codec;
	}

	public float getSampleRate() {
		return sampleRate;
	}

	public float getBitRate() {
		return bitRate;
	}

	public class Builder {
		private String name;
		private String url;
		private String codec;
		private float sampleRate;
		private float bitRate;

		public Builder(String name, String url) {
			this.name = name;
			this.url = url;
		}

		public Builder codec(String val) {
			codec = val;
			return this;
		}

		public Builder sampleRate(float val) {
			sampleRate = val;
			return this;
		}

		public Builder bitRate(float val) {
			bitRate = val;
			return this;
		}

		public StationInformation build() {
			return new StationInformation(this);
		}

	}

}
