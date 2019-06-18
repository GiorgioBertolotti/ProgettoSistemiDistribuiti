import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Parcheggio implements Serializable {
	private static final long serialVersionUID = 1L;
	private int idParcheggio;
	private int posti;
	private Map<Integer, Automobile> autoParcheggiate = new HashMap<>();
	public Parcheggiatore[] parcheggiatori;
	private int ticketNo;

	public Parcheggio(int idParcheggio, int numParcheggiatori, int posti) {
		super();
		this.idParcheggio = idParcheggio;
		this.parcheggiatori = new Parcheggiatore[numParcheggiatori];
		for (int i = 0; i < numParcheggiatori; i++) {
			this.parcheggiatori[i] = new Parcheggiatore(i, this);
		}
		this.posti = posti;
	}

	public int postiLiberi() {
		return this.posti - this.autoParcheggiate.size();
	}

	public int getNumParcheggiatori() {
		return this.parcheggiatori.length;
	}

	public int getIdParcheggio() {
		return idParcheggio;
	}

	public int deposita() {
		Automobilista automobilista = (Automobilista) (Thread.currentThread());
		int postiLiberi = this.posti - this.autoParcheggiate.size();
		if (postiLiberi > 0) {
			syncParcheggiatori();
			int index = parcheggiatoreLibero();
			synchronized (this) {
				boolean detto = false;
				while (index == -1) {
					try {
						if (!detto) {
							System.out.println("I'm waiting for a parking attendant to be free to park the car.");
							detto = true;
						}
						Thread.sleep(1000);
						syncParcheggiatori();
						index = parcheggiatoreLibero();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			this.parcheggiatori[index].parcheggia();
			this.autoParcheggiate.put(this.ticketNo, automobilista.getAuto());
			int toReturn = this.ticketNo++;
			inviaStato();
			return toReturn;
		} else {
			System.out.println(automobilista.getAuto().targa + " not parked, there are no more parkings.");
			return -1;
		}
	}

	public void ritira() {
		Automobilista automobilista = (Automobilista) (Thread.currentThread());
		if (automobilista.getTicketNo() != null && this.autoParcheggiate.containsKey(automobilista.getTicketNo())) {
			syncParcheggiatori();
			int index = parcheggiatoreLibero();
			synchronized (this) {
				boolean detto = false;
				while (index == -1) {
					try {
						if (!detto) {
							System.out.println("I'm waiting for a parking attendant to be free to pick up the car.");
							detto = true;
						}
						Thread.sleep(1000);
						syncParcheggiatori();
						index = parcheggiatoreLibero();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			this.parcheggiatori[index].ritira();
			this.autoParcheggiate.remove(automobilista.getTicketNo());
			inviaStato();
			return;
		} else {
			System.out.println("Ticket not valid.");
		}
	}

	synchronized private int parcheggiatoreLibero() {
		for (int i = 0; i < this.parcheggiatori.length; i++) {
			if (this.parcheggiatori[i].isLibero())
				return i;
		}
		return -1;
	}

	private void syncParcheggiatori() {
		try {
			Socket socket = new Socket(InetAddress.getLocalHost(), 53535);
			PrintWriter stringOutput = new PrintWriter(socket.getOutputStream(), true);
			stringOutput.write("getStatoParcheggiatori-" + this.idParcheggio);
			stringOutput.flush();
			ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
			Object received = input.readObject();
			stringOutput.close();
			input.close();
			socket.close();
			if (received instanceof Parcheggiatore[]) {
				this.parcheggiatori = (Parcheggiatore[]) received;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void inviaStato() {
		try {
			Socket socket = new Socket(InetAddress.getLocalHost(), 53535);
			PrintWriter stringOutput = new PrintWriter(socket.getOutputStream(), true);
			stringOutput.write("aggiornaStatoParcheggio");
			stringOutput.flush();
			Thread.sleep(500);
			ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
			objectOutput.writeObject(this);
			objectOutput.flush();
			stringOutput.close();
			objectOutput.close();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idParcheggio;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Parcheggio other = (Parcheggio) obj;
		if (idParcheggio != other.idParcheggio)
			return false;
		return true;
	}
}
