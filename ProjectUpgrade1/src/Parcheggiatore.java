import java.io.DataInputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;

public class Parcheggiatore implements Serializable {
	private static final long serialVersionUID = 2L;
	private int id;
	private boolean libero;
	private Parcheggio parcheggio;

	public Parcheggiatore(int id, Parcheggio parcheggio) {
		super();
		this.id = id;
		this.parcheggio = parcheggio;
		this.libero = true;
	}

	public boolean isLibero() {
		return libero;
	}

	public void setLibero(boolean libero) {
		this.libero = libero;
	}

	synchronized public void parcheggia() {
		Automobilista a = (Automobilista) (Thread.currentThread());
		this.libero = false;
		System.out.println("I'm parking " + a.getAuto().targa + ".");
		inviaStatoParcheggiatore();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.libero = true;
		System.out.println(a.getAuto().targa + " parked.");
		inviaStatoParcheggiatore();
	}

	synchronized public void ritira() {
		Automobilista a = (Automobilista) (Thread.currentThread());
		this.libero = false;
		System.out.println("I'm picking up " + a.getAuto().targa + ".");
		inviaStatoParcheggiatore();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.libero = true;
		System.out.println(a.getAuto().targa + " picked up.");
		inviaStatoParcheggiatore();
	}

	private void inviaStatoParcheggiatore() {
		try {
			Socket socket = new Socket(InetAddress.getLocalHost(), 53535);
			PrintWriter stringOutput = new PrintWriter(socket.getOutputStream(), true);
			stringOutput.write("statoParcheggiatore-" + this.parcheggio.getIdParcheggio() + "-" + this.id + "-"
					+ (this.libero ? "1" : "0"));
			stringOutput.flush();
			DataInputStream in;
			byte[] byteReceived = new byte[1000];
			String messageString = "";
			in = new DataInputStream(socket.getInputStream());
			int bytesRead = 0;
			bytesRead = in.read(byteReceived);
			messageString += new String(byteReceived, 0, bytesRead);
			if (messageString == "ricevuto") {
				System.out.println("Server state updated for parking attendant.");
			}
			stringOutput.close();
			in.close();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
