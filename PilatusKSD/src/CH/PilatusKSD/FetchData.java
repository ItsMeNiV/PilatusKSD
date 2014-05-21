
package CH.PilatusKSD;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FetchData {

  String currSongArtist;
  String currSongTitle;
  String lastSongArtist;
  String lastSongTitle;
  Connection conn;
  DBConnect dbc;

  public FetchData() {
    currSongArtist = "";
    currSongTitle = "";
    lastSongArtist = "";
    lastSongTitle = "";
    connect();
    while (true) {
      getData();
      writeData();
      testDouble();
      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private void getData() {
    System.out.println("Getting Data...");
    try {
      Document doc = Jsoup.connect("https://www.radiopilatus.ch/").get();

      // artistname
      Elements ereignisse = doc
          .select("#content > div:nth-child(2) > div > div.tile.livecenter > div > div.last-played > div > div.col-sm-8.title > span.artist");

      for (Element e : ereignisse) {
        currSongArtist = e.text();
      }

      // songname
      ereignisse = doc
          .select("#content > div:nth-child(2) > div > div.tile.livecenter > div > div.last-played > div > div.col-sm-8.title > span.song");

      for (Element e : ereignisse) {
        currSongTitle = e.text();
      }
      doc = null;
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  private void connect() {
    System.out.println("Connecting...");
    dbc = new DBConnect();
    conn = dbc.connectDB();
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

  // Testing if 2 times the same song right after eachother
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
        String sql = "delete from songs where songid=" + lastID + ";";
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
