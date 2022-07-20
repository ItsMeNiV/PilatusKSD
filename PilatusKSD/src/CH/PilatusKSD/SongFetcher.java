
package CH.PilatusKSD;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class SongFetcher {

	private List<Song> songList;
	private Song lastSong;

	public SongFetcher() {
		songList = new ArrayList<>();
		lastSong = new Song("", "");
	}

	public void fetch() {
		Song currentSong = loadCurrentSong();
		if (currentSong != null) {
			this.songList.add(currentSong);
			System.out
					.println("Added song to list: " + currentSong.getArtistName() + " - " + currentSong.getSongName());
			lastSong = currentSong;
		}
	}

	public void checkForDuplicates() {
		for (Song song : songList) {
			for (Song possibleDuplicate : songList) {
				if (!song.equals(possibleDuplicate)
						&& song.isSameAs(possibleDuplicate.getArtistName(), possibleDuplicate.getSongName())) {
					System.out.println("!!!!!!!!!!!!!!Duplicate song found!!!!!!!!!!!!!!");
					System.out.println(song.getArtistName() + " - " + song.getSongName());
				}
			}
		}
	}

	private Song loadCurrentSong() {
		Song currentSong = null;
		try {
			String url = "https://www.radiopilatus.ch/api/pub/gql/radiopilatus/AudioLiveData/e0c0de0fa34937485951a3a3c1fb2aaffc94311e?variables=%7B%22streamName%22%3A%22radiopilatus.main%22%7D";
			String jsonURL = IOUtils.toString(new URL(url));
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonURL);

			JSONObject live = ((JSONObject) ((JSONObject) ((JSONObject) ((JSONObject) jsonObject.get("data"))
					.get("audioPlayer")).get("stream")).get("live"));

			String currSongTitle = (String) live.get("title");
			String currSongArtist = (String) live.get("interpret");
			if (!lastSong.isSameAs(currSongArtist, currSongTitle)) {
				currentSong = new Song(currSongArtist, currSongTitle);
			}
		} catch (IOException e) {
			System.err.println(e);
		} catch (ParseException e) {
			System.err.println(e);
		}

		return currentSong;
	}

}