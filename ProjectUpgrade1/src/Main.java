import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		try {
			List<Automobilista> automobilisti = new ArrayList<>();
			for (int i = 0; i < 20; i++)
				automobilisti.add(new Automobilista(new Automobile(Integer.toString(i))));
			Scanner scanner = new Scanner(System.in);
			for (Automobilista automobilista : automobilisti) {
				List<Parcheggio> parcheggi = automobilista.richiediListaParcheggi();
				System.out.println("Select a parking:");
				int count = 0;
				for (Parcheggio parcheggio : parcheggi) {
					System.out.println(count + ") Parking with ID: " + parcheggio.getIdParcheggio());
					count++;
				}
				int selection = scanner.nextInt();
				if (selection < parcheggi.size()) {
					final Parcheggio parcheggio = parcheggi.get(selection);
					automobilista.setParcheggio(parcheggio);
					automobilista.start();
				}
				Thread.sleep(1000);
			}
			scanner.close();
		} catch (Exception e) {
			System.out.println("Please, turn on the server before starting the main. Thanks!");
		}
	}
}