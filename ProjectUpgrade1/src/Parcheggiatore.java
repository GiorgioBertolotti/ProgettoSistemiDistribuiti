
public class Parcheggiatore extends Thread {
	private Parcheggio parcheggio;
	private Automobile automobile;
	private boolean depositare;
	private int ticketNo;

	public Parcheggiatore(Parcheggio parcheggio, Automobile automobile, boolean depositare, int ticketNo) {
		super();
		this.parcheggio = parcheggio;
		this.automobile = automobile;
		this.depositare = depositare;
		this.ticketNo = ticketNo;
	}

	public int getTicketNo() {
		return ticketNo;
	}

	public Automobile getAutomobile() {
		return automobile;
	}

	public void run() {
		if (depositare) {
			parcheggio.deposita();
		} else {
			parcheggio.ritira();
		}
	}
}
