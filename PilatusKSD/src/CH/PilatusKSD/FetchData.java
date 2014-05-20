package CH.PilatusKSD;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

import javax.print.Doc;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FetchData {

	String currSongArtist = "";
	String currSongTitle = "";
	String lastSongArtist = "";
	String lastSongTitle = "";
	
	public FetchData(){
		while(true){
		long time1 = System.currentTimeMillis();
			getData();
			WriteData();
			while(System.currentTimeMillis() - time1 < 5000){		
			}
		}
	}
	
	private void getData(){
		try{
			Document doc = Jsoup.connect("https://www.radiopilatus.ch/").get();
			
			//Artist
			Elements ereignisse = doc.select("#content > div:nth-child(2) > div > div.tile.livecenter > div > div.last-played > div > div.col-sm-8.title > span.artist");
			
			for(Element e : ereignisse){
				currSongArtist = e.text();
			}
			
			//Titel
			ereignisse = doc.select("#content > div:nth-child(2) > div > div.tile.livecenter > div > div.last-played > div > div.col-sm-8.title > span.song");
			
			for(Element e : ereignisse){
				currSongTitle = e.text();
			}
			doc = null;
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}
	
	private void WriteData(){
		DBConnect dbc = new DBConnect();
		Connection conn = dbc.connectDB();
		try{
		Statement stmt = conn.createStatement();
		if(!lastSongTitle.equals(currSongTitle) && !lastSongArtist.equals(currSongArtist)){
			String sql = "insert into songs(songname, artistname) values('" + currSongTitle + "','" + currSongArtist + "');";
			stmt.execute(sql);
		}
		ResultSet rs = stmt.executeQuery("select * from songs;");
		while(rs.next()){
			String songname = rs.getString("songname");
			String artistname = rs.getString("artistname");
			System.out.println("Songname: " + songname + " Artistname: " + artistname);
		}
		System.out.println("\n\n");
		
		//Close Statement
		stmt.close();
		}catch(Exception e){
			System.out.println(e);
		}
		lastSongArtist = currSongArtist;
		lastSongTitle = currSongTitle;
	}
	
}
