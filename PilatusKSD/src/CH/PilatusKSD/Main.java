
package CH.PilatusKSD;

import java.util.Date;

public class Main {

	static String emailAdress;
	static String emailPassword;
	
  public Main() {
    Date date = new Date();
    while (true) {
      if (date.getHours() >= 8) {
        FetchData fd = new FetchData(emailAdress, emailPassword);
      } else {
        System.out.println("Noch nicht 8 Uhr!");
      }
      try {
        Thread.sleep(60000); // wait 1 min
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  // ========================main========================
  public static void main(String[] args) {
	  emailAdress = args[0];
	  emailPassword = args[0];
	  Main m = new Main();
  }

}