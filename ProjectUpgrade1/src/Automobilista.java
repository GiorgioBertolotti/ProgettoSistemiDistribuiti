import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Base64;
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
			Thread.sleep(100);
			DataInputStream in;
			byte[] byteReceived = new byte[1000];
			String messageString = "";
			in = new DataInputStream(socket.getInputStream());
			int bytesRead = 0;
			bytesRead = in.read(byteReceived);
			messageString += new String(byteReceived, 0, bytesRead);
			Object received = fromString(messageString);
			stringOutput.close();
			in.close();
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

	private Object fromString(String s) throws IOException, ClassNotFoundException {
		byte[] data = Base64.getDecoder().decode(s);
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
		Object o = ois.readObject();
		ois.close();
		return o;
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