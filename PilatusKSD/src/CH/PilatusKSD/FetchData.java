
package CH.PilatusKSD;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class FetchData {

  private String _currSongArtist;
  private String _currSongTitle;
  private String _lastSongArtist;
  private String _lastSongTitle;
  private Connection _conn;

  private String _emailAdress, _emailPassword;

  public FetchData(String emailAdress, String emailPassword) {
    _currSongArtist = "";
    _currSongTitle = "";
    _lastSongArtist = "";
    _lastSongTitle = "";
    this._emailAdress = emailAdress;
    this._emailPassword = emailPassword;
    Date date = new Date();

    connect();
    while (/* date.getHours() <= 17 */true) {
      getData();
      writeData();
      testDouble();
      try {
        Thread.sleep(4000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private void getData() {
    try {
      String url = "http://player.radiopilatus.ch/data/generated_content/pilatus/production/playlist/playlist_radiopilatus.json";
      String jsonURL = IOUtils.toString(new URL(url));
      // FileReader reader = new FileReader(jsonURL);
      JSONParser jsonParser = new JSONParser();
      JSONObject jsonObject = (JSONObject)jsonParser.parse(jsonURL);

      JSONArray playing = (JSONArray)jsonObject.get("live");
      JSONObject innerObj = (JSONObject)playing.get(0);
      _currSongTitle = (String)innerObj.get("title");
      _currSongArtist = (String)innerObj.get("interpret");
    } catch (IOException e) {
      System.err.println(e);
    } catch (ParseException e) {
      System.err.println(e);
    }
  }

  private void connect() {
    _conn = DBConnect.connectDB();
  }

  private void writeData() {
    try {
      Statement stmt = _conn.createStatement();
      if (!_currSongTitle.equals("") && testData(stmt)) {
        String sql = "insert into songs(songname, artistname) values('" + _currSongTitle + "','" + _currSongArtist + "');";
        stmt.execute(sql);
      }
      ResultSet rs = stmt.executeQuery("select * from songs;");
      while (rs.next()) {
        String songname = rs.getString("songname");
        String artistname = rs.getString("artistname");
        System.out.println("Songname: " + songname + " Artistname: " + artistname);
      }
      System.out.println("\n\n");

      // Closing the Statement
      stmt.close();
    } catch (Exception e) {
      System.out.println(e);
    }
    _lastSongArtist = _currSongArtist;
    _lastSongTitle = _currSongTitle;
  }

  // Get newest added song and compare it with the song which should get added.
  private boolean testData(Statement stmt) {
    String lastSongName = "";
    String lastArtistName = "";
    try {
      ResultSet rs = stmt.executeQuery("select * from songs where songid = (select max(songid) from songs)");
      while (rs.next()) {
        lastSongName = rs.getString("songname");
        lastArtistName = rs.getString("artistname");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    if (lastSongName.equals(_currSongTitle) && lastArtistName.equals(_currSongArtist)) {
      return false;
    } else {
      return true;
    }
  }

  private void testDouble() {
    List<String> titles = new ArrayList<String>();
    List<String> artists = new ArrayList<String>();
    try {
      Statement stmt = _conn.createStatement();
      ResultSet rs = stmt.executeQuery("select * from songs");
      while (rs.next()) {
        titles.add(rs.getString("songname"));
        artists.add(rs.getString("artistname"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    for (int i = 0; i < titles.size(); i++) {
      for (int k = 0; k < titles.size(); k++) {
        if (titles.get(i).equals(titles.get(k)) && artists.get(i).equals(artists.get(k)) && i != k) {
          // the same song twice
          String twinSongName = titles.get(i);
          String twinSongArtist = artists.get(i);
          System.out.println("=======================FOUND SONG=======================");
          Mailer mailer = new Mailer(twinSongName, twinSongArtist, _emailAdress, _emailPassword);
          System.exit(0);
        }
      }
    }
  }
}