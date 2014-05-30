
package CH.PilatusKSD;

import java.sql.Connection;
import java.sql.DriverManager;

import javax.swing.JOptionPane;

public class DBConnect {

  Connection conn = null;

  public static Connection connectDB() {

    try {
      Class.forName("org.sqlite.JDBC");
      Connection conn = DriverManager.getConnection("jdbc:sqlite:res\\DB\\Songs.sqlite");
      return conn;
    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, e);
      return null;
    }

  }

}