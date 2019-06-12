import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

public class Automobilista {
	private Automobile auto;

	public Automobilista(Automobile auto) {
		super();
		this.auto = auto;
	}

	public List<Parcheggio> richiediListaParcheggi() {
		try {
			Socket socket = new Socket(InetAddress.getLocalHost(), 53535);
			PrintWriter stringOutput = new PrintWriter(socket.getOutputStream(), true);
			stringOutput.write("richiestaParcheggi");
			stringOutput.flush();
			ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
			Object received = input.readObject();
			stringOutput.close();
			input.close();
			socket.close();
			if (received instanceof List<?>) {
				return (List<Parcheggio>) received;
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Automobile getAuto() {
		return auto;
	}

	public int parcheggia(Parcheggio parcheggio) {
		return parcheggio.depositaAuto(this.auto);
	}

	public void ritira(Parcheggio parcheggio, int ticket) {
		parcheggio.ritiraAuto(ticket);
	}
}