import java.util.ArrayList;
import java.util.List;

public class Main {
	public static void main(String[] args) {
		Parcheggio parcheggio = new Parcheggio(5, 10);
		List<Automobilista> automobilisti = new ArrayList<>();
		for (int i = 0; i < 20; i++)
			automobilisti.add(new Automobilista(new Automobile(Integer.toString(i)), parcheggio));
		for (Automobilista automobilista : automobilisti) {
			automobilista.start();
		}
	}
}