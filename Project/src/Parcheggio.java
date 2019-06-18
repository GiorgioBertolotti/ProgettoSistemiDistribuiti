import java.util.HashMap;
import java.util.Map;

public class Parcheggio {
	private int posti;
	private Map<Integer, Automobile> autoParcheggiate = new HashMap<>();
	private Parcheggiatore[] parcheggiatori;
	private int ticketNo;

	public Parcheggio(int numParcheggiatori, int posti) {
		super();
		this.parcheggiatori = new Parcheggiatore[numParcheggiatori];
		for (int i = 0; i < numParcheggiatori; i++) {
			this.parcheggiatori[i] = new Parcheggiatore();
		}
		this.posti = posti;
	}

	public int deposita() {
		Automobilista automobilista = (Automobilista) (Thread.currentThread());
		int postiLiberi = this.posti - this.autoParcheggiate.size();
		if (postiLiberi > 0) {
			int index = parcheggiatoreLibero();
			synchronized (this) {
				boolean detto = false;
				while (index == -1)
					try {
						if (!detto) {
							System.out.println("I'm waiting for a parking attendant to be free to park the car.");
							detto = true;
						}
						wait();
						index = parcheggiatoreLibero();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			}
			this.parcheggiatori[index].parcheggia();
			synchronized (this) {
				notifyAll();
			}
			this.autoParcheggiate.put(this.ticketNo, automobilista.getAuto());
			return this.ticketNo++;
		} else {
			System.out.println(automobilista.getAuto().targa + " not parked, there are no more parkings.");
			return -1;
		}
	}

	public void ritira() {
		Automobilista automobilista = (Automobilista) (Thread.currentThread());
		if (automobilista.getTicketNo() != null && this.autoParcheggiate.containsKey(automobilista.getTicketNo())) {
			int index = parcheggiatoreLibero();
			synchronized (this) {
				boolean detto = false;
				while (index == -1)
					try {
						if (!detto) {
							System.out.println("I'm waiting for a parking attendant to be free to pick up the car.");
							detto = true;
						}
						wait();
						index = parcheggiatoreLibero();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			}
			this.parcheggiatori[index].ritira();
			synchronized (this) {
				notifyAll();
			}
			this.autoParcheggiate.remove(automobilista.getTicketNo());
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
}
