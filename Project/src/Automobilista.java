public class Automobilista {
	private Automobile auto;

	public Automobilista(Automobile auto) {
		super();
		this.auto = auto;
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