
package CH.PilatusKSD;

import java.util.Calendar;

public class Main {

	private void run() throws InterruptedException {
		Calendar calendar = Calendar.getInstance();
		SongFetcher songFetcher = new SongFetcher();
		while (calendar.get(Calendar.HOUR_OF_DAY) >= 8 && calendar.get(Calendar.HOUR_OF_DAY) <= 17) {
			songFetcher.fetch();
			songFetcher.checkForDuplicates();
			Thread.sleep(60000); // wait 1 min
		}
		System.out.println("It's not between 08:00 and 17:00");
	}

	public static void main(String[] args) throws InterruptedException {
		Main m = new Main();
		m.run();
	}

}