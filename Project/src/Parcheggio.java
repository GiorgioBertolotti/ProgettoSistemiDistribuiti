import java.util.HashMap;
import java.util.Map;

public class Parcheggio {
	private int maxParcheggiatori;
	private int posti;
	private Map<Integer, Automobile> autoParcheggiate = new HashMap<>();
	private int parcheggiatoriAttivi;
	private int ticketNo;

	public Parcheggio(int y, int posti) {
		super();
		this.maxParcheggiatori = y;
		this.posti = posti;
	}

	public void deposita() {
		Parcheggiatore p = (Parcheggiatore) (Thread.currentThread());
		synchronized (this) {
			System.out.println("Aspetto a parcheggiare " + p.getAutomobile().targa);
			while (parcheggiatoriAttivi == maxParcheggiatori) {
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
			System.out.println("Aspetto a ritirare il ticket " + p.getTicketNo());
			while (parcheggiatoriAttivi == maxParcheggiatori) {
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
				System.out.println("Ritirata l'auto " + auto.targa);
			} else
				System.out
						.println("L'auto con ticket " + p.getTicketNo() + " non è parcheggiata in questo parcheggio.");
		}
		synchronized (this) {
			parcheggiatoriAttivi--;
			posti++;
			notifyAll();
		}
	}

	synchronized public int depositaAuto(Automobile auto) {
		if (posti > 0) {
			Parcheggiatore parcheggiatore = new Parcheggiatore(this, auto, true, ticketNo);
			parcheggiatore.start();
			posti--;
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
}
