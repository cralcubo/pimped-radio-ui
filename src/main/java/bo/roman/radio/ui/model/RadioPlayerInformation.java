package bo.roman.radio.ui.model;

public class RadioPlayerInformation {
	private final String mainInfo;
	private final String subInfo;
	private final String extraInfo;
	
	public RadioPlayerInformation(String mainInfo, String subInfo, String extraInfo) {
		this.mainInfo = mainInfo;
		this.subInfo = subInfo;
		this.extraInfo = extraInfo;
	}
	
	public RadioPlayerInformation(String mainInfo) {
		this(mainInfo, "", "");
	}

	public String getMainInfo() {
		return mainInfo;
	}

	public String getSubInfo() {
		return subInfo;
	}

	public String getExtraInfo() {
		return extraInfo;
	}

	@Override
	public String toString() {
		return "RadioInformation [mainInfo=" + mainInfo + ", subInfo=" + subInfo + ", extraInfo=" + extraInfo + "]";
	}

}
