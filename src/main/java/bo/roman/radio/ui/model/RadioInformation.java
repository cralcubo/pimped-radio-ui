package bo.roman.radio.ui.model;

public class RadioInformation {
	private final String songName;
	private final String artistName;
	private final String extra;
	
	public RadioInformation(String songName, String artistName, String extra) {
		this.songName = songName;
		this.artistName = artistName;
		this.extra = extra;
	}

	public String getSongName() {
		return songName;
	}

	public String getArtistName() {
		return artistName;
	}

	public String getExtra() {
		return extra;
	}

}
