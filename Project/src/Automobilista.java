public class Automobilista extends Thread {
	private Automobile auto;
	private Parcheggio parcheggio;
	private Integer ticketNo;

	public Automobilista(Automobile auto, Parcheggio parcheggio) {
		super();
		this.auto = auto;
		this.parcheggio = parcheggio;
	}

	public Automobile getAuto() {
		return auto;
	}

	public Integer getTicketNo() {
		return ticketNo;
	}

	public void run() {
		ticketNo = this.parcheggio.deposita();
		try {
			Thread.sleep(3000);
		} catch (Exception e) {
		}
		this.parcheggio.ritira();
		this.ticketNo = null;
	}
}