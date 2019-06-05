
public class Parcheggiatore extends Thread{
	
	 private int identity;
	 private boolean stopRequested = false;
	 private Parcheggio parcheggio;
	 private Automobilista automobilista;
	 
	  public Parcheggiatore(int identity,  Parcheggio parcheggio, Automobilista automobilista) {
		super();
		this.identity = identity;
		this.parcheggio = parcheggio;
		this.automobilista = automobilista;
	}

     public void run() {
	        while (!stopRequested) {
	            try {
	                sleep(50000);
	                parcheggio.ritira();
	                sleep(50000);
	                parcheggio.restituisci();
	                
	            } catch (InterruptedException e) {

	            }
	        }
     }
     
     public void stopRequested() {
         stopRequested = true;
     }
     public int getAutomobilista(){
         return automobilista;
     }

}
