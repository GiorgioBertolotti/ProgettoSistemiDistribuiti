import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

public class Automobilista extends Thread {
	private Automobile auto;
	private Parcheggio parcheggio;
	private Integer ticketNo;

	public Automobilista(Automobile auto) {
		super();
		this.auto = auto;
	}

	@SuppressWarnings("unchecked")
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

	public Integer getTicketNo() {
		return ticketNo;
	}

	public void setParcheggio(Parcheggio parcheggio) {
		this.parcheggio = parcheggio;
	}

	public void run() {
		if (this.parcheggio == null) {
			System.out.println("Please specify the parking.");
			return;
		}
		ticketNo = this.parcheggio.deposita();
		try {
			Thread.sleep(3000);
		} catch (Exception e) {
		}
		this.parcheggio.ritira();
		this.ticketNo = null;
	}
}