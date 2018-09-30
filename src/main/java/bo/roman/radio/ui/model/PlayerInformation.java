package bo.roman.radio.ui.model;

public class PlayerInformation {
	private final String mainInfo;
	private final String subInfo;
	private final String extraInfo;
	
	public PlayerInformation(String mainInfo, String subInfo, String extraInfo) {
		this.mainInfo = mainInfo;
		this.subInfo = subInfo;
		this.extraInfo = extraInfo;
	}
	
	public PlayerInformation(String mainInfo) {
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
