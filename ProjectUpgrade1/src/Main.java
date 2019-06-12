import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		// TODO: main
		List<Automobilista> automobilisti = new ArrayList<>();
		for (int i = 0; i < 20; i++)
			automobilisti.add(new Automobilista(new Automobile(Integer.toString(i))));
		Scanner scanner = new Scanner(System.in);
		for (Automobilista automobilista : automobilisti) {
			List<Parcheggio> parcheggi = automobilista.richiediListaParcheggi();
			System.out.println("Seleziona un parcheggio tra i seguenti:");
			int count = 0;
			for (Parcheggio parcheggio : parcheggi) {
				System.out.println(count + ") Parcheggio con id: " + parcheggio.getIdParcheggio());
				count++;
			}
			int selection = scanner.nextInt();
			if (selection < parcheggi.size()) {
				final Parcheggio parcheggio = parcheggi.get(selection);
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
		scanner.close();
	}
}