import java.io.DataInputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parcheggio implements Serializable {
	private int idParcheggio;
	private int maxParcheggiatori;
	private int posti;
	private Map<Integer, Automobile> autoParcheggiate = new HashMap<>();
	private int parcheggiatoriAttivi;
	private int ticketNo;

	public int occupaPosto() {
		return --this.posti;
	}

	public int liberaPosto() {
		return ++this.posti;
	}

	public int incrementaTicket() {
		return ++this.ticketNo;
	}

	public int getIdParcheggio() {
		return idParcheggio;
	}

	public Parcheggio(int idParcheggio, int y, int posti) {
		super();
		this.idParcheggio = idParcheggio;
		this.maxParcheggiatori = y;
		this.posti = posti;
	}

	public void deposita() {
		Parcheggiatore p = (Parcheggiatore) (Thread.currentThread());
		synchronized (this) {
			while (parcheggiatoriAttivi == maxParcheggiatori) {
				System.out.println("Aspetto a parcheggiare " + p.getAutomobile().targa);
				try {
					wait();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			parcheggiatoriAttivi++;
		}
		System.out.println("Inizio a parcheggiare " + p.getAutomobile().targa);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		synchronized (autoParcheggiate) {
			autoParcheggiate.put(p.getTicketNo(), p.getAutomobile());
			System.out.println(p.getAutomobile().targa + " parcheggiata.");
		}
		synchronized (this) {
			parcheggiatoriAttivi--;
			notifyAll();
		}
	}

	public void ritira() {
		Parcheggiatore p = (Parcheggiatore) (Thread.currentThread());
		synchronized (this) {
			while (parcheggiatoriAttivi == maxParcheggiatori) {
				System.out.println("Aspetto a ritirare il ticket " + p.getTicketNo());
				try {
					wait();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			parcheggiatoriAttivi++;
		}
		System.out.println("Vado a ritirare l'auto con ticket " + p.getTicketNo());
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		synchronized (autoParcheggiate) {
			if (autoParcheggiate.containsKey(p.getTicketNo())) {
				Automobile auto = autoParcheggiate.get(p.getTicketNo());
				autoParcheggiate.remove(p.getTicketNo());
				System.out.println("Ritirata l'auto con ticket " + p.getTicketNo() + ", la targa è " + auto.targa);
			} else
				System.out
						.println("L'auto con ticket " + p.getTicketNo() + " non è parcheggiata in questo parcheggio.");
		}
		synchronized (this) {
			parcheggiatoriAttivi--;
			segnalaParcheggioDisponibile();
			notifyAll();
		}
	}

	synchronized public int depositaAuto(Automobile auto) {
		if (posti > 0) {
			Parcheggiatore parcheggiatore = new Parcheggiatore(this, auto, true, ticketNo);
			parcheggiatore.start();
			segnalaParcheggioOccupato();
			return ticketNo++;
		} else {
			System.out.println(auto.targa + " non parcheggiata, sono finiti i parcheggi.");
			return -1;
		}
	}

	public void ritiraAuto(int ticket) {
		Parcheggiatore parcheggiatore = new Parcheggiatore(this, null, false, ticket);
		parcheggiatore.start();
	}

	public void segnalaParcheggioOccupato() {
		inviaMessaggioSocket("parcheggioOccupato" + this.idParcheggio);
	}

	public void segnalaParcheggioDisponibile() {
		inviaMessaggioSocket("parcheggioDisponibile" + this.idParcheggio);
	}

	private void inviaMessaggioSocket(String messaggio) {
		try {
			Socket socket = new Socket(InetAddress.getLocalHost(), 53535);
			PrintWriter stringOutput = new PrintWriter(socket.getOutputStream(), true);
			stringOutput.write(messaggio);
			stringOutput.flush();
			DataInputStream in;
			byte[] byteReceived = new byte[1000];
			String messageString = "";
			in = new DataInputStream(socket.getInputStream());
			int bytesRead = 0;
			bytesRead = in.read(byteReceived);
			messageString += new String(byteReceived, 0, bytesRead);
			System.out.println(messageString);
			stringOutput.close();
			in.close();
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
