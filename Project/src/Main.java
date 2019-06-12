import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
	public static void main(String[] args) {
		Parcheggio parcheggio = new Parcheggio(5, 10);
		List<Automobilista> automobilisti = new ArrayList<>();
		for (int i = 0; i < 20; i++)
			automobilisti.add(new Automobilista(new Automobile(Integer.toString(i))));
		for (Automobilista automobilista : automobilisti) {
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					int ticket = automobilista.parcheggia(parcheggio);
					if (ticket != -1) {
						try {
							Random rand = new Random();
							Thread.sleep(20000 + rand.nextInt(10000));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						automobilista.ritira(parcheggio, ticket);
					}
				}
			});
			t.start();
		}
	}
}