package CH.PilatusKSD;

public class Song {

	private String artistName;
	private String songName;

	public Song(String artistName, String songName) {
		super();
		this.artistName = artistName;
		this.songName = songName;
	}

	public String getArtistName() {
		return artistName;
	}

	public String getSongName() {
		return songName;
	}

	public boolean isSameAs(String artistName, String songName) {
		return this.artistName.equals(artistName) && this.songName.equals(songName);
	}

}
