package bo.roman.radio.ui.model;

public class AlertMessage {
	private final String message;
	private final String title;
	private final String header;

	public AlertMessage(Builder b) {
		this.message = b.message;
		this.title = b.title;
		this.header = b.header;
	}

	public String getMessage() {
		return message;
	}

	public String getTitle() {
		return title;
	}

	public String getHeader() {
		return header;
	}

	public static class Builder {
		private String message;
		private String title;
		private String header;
		
		public Builder title(String val) {
			this.title = val;
			return this;
		}
		
		public Builder message(String val) {
			this.message = val;
			return this;
		}

		public Builder header(String val) {
			this.header = val;
			return this;
		}

		public AlertMessage build() {
			return new AlertMessage(this);
		}

	}
}
