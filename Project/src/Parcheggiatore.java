public class Parcheggiatore {
	private boolean libero;

	public Parcheggiatore() {
		super();
		this.libero = true;
	}

	public boolean isLibero() {
		return libero;
	}

	synchronized public void parcheggia() {
		Automobilista a = (Automobilista) (Thread.currentThread());
		this.libero = false;
		System.out.println("I'm parking " + a.getAuto().targa + ".");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.libero = true;
		System.out.println(a.getAuto().targa + " parked.");
	}

	synchronized public void ritira() {
		Automobilista a = (Automobilista) (Thread.currentThread());
		this.libero = false;
		System.out.println("I'm picking up " + a.getAuto().targa + ".");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.libero = true;
		System.out.println(a.getAuto().targa + " picked up.");
	}
}
