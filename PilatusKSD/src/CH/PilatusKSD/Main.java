
package CH.PilatusKSD;

import java.util.Date;

public class Main {

  public Main() {
    Date date = new Date();
    while (true) {
      if (date.getHours() >= 8) {
        FetchData fd = new FetchData();
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
    Main m = new Main();
  }

}
