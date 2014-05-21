
package CH.PilatusKSD;

import java.io.FileReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class FetchData {

  String currSongArtist;
  String currSongTitle;
  String lastSongArtist;
  String lastSongTitle;
  Connection conn;

  public FetchData() {
    currSongArtist = "";
    currSongTitle = "";
    lastSongArtist = "";
    lastSongTitle = "";
    Date date = new Date();

    connect();
    while (date.getHours() <= 17) {
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
      //FileReader reader = new FileReader(jsonURL);
      JSONParser jsonParser = new JSONParser();
      JSONObject jsonObject = (JSONObject)jsonParser.parse(jsonURL);

      JSONArray playing = (JSONArray)jsonObject.get("live");
      Iterator i = playing.iterator();

      while (i.hasNext()) {
        JSONObject innerObj = (JSONObject)i.next();
        currSongTitle = (String)innerObj.get("title");
        currSongArtist = (String)innerObj.get("interpret");
      }
    } catch (Exception e) {
      System.err.println(e);
    }

  }

  private void connect() {
    conn = DBConnect.connectDB();
  }

  private void writeData() {
    try {
      Statement stmt = conn.createStatement();
      if (!currSongTitle.equals("")) {
        String sql = "insert into songs(songname, artistname) values('" + currSongTitle + "','" + currSongArtist + "');";
        stmt.execute(sql);
        testData(stmt);
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
    lastSongArtist = currSongArtist;
    lastSongTitle = currSongTitle;
  }

  // Testing for the same song right after eachother
  private void testData(Statement stmt) {
    int currID = 0;
    int lastID = 0;
    try {
      ResultSet rs = stmt.executeQuery("select * from songs where songname='" + currSongTitle + "' and artistname='" + currSongArtist
          + "';");
      while (rs.next()) {
        currID = rs.getInt("songid");
        lastID = currID - 1;
      }
      rs = stmt.executeQuery("select * from songs where songid=" + lastID + ";");
      while (rs.next()) {
        lastSongTitle = rs.getString("songname");
        lastSongArtist = rs.getString("artistname");
      }
      if (currSongTitle.equals(lastSongTitle) && currSongArtist.equals(lastSongArtist)) {
        String sql = "delete from songs where songid='" + lastID + "';";
        stmt.execute(sql);
      } else {
        return;
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private void testDouble() {
    List<String> titles = new ArrayList<String>();
    List<String> artists = new ArrayList<String>();
    try {
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery("select * from songs;");
      while (rs.next()) {
        titles.add(rs.getString("songname"));
        artists.add(rs.getString("artistname"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    for (int i = 0; i < titles.size(); i++) {
      for (int k = 0; k < titles.size(); k++) {
        if (titles.get(i).equals(titles.get(k)) && i != k) {
          // the same song twice
          String twinSongName = titles.get(i);
          String twinSongArtist = artists.get(i);
          System.out.println("=======================FOUND SONG=======================");
          System.out.println("Song: " + twinSongName + " - " + twinSongArtist);
          return;
        }
      }
    }
  }
}
