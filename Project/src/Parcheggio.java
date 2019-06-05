import java.util.ArrayList;

public class Parcheggio {
	
	private int posti = 50;
	private Parcheggiatore [] parcheggiatori;
	private ArrayList<Automobile> autoParcheggiati = new ArrayList<Automobile>() ;
	
	synchronized int ritira () {
		
		Parcheggiatore p = (Parcheggiatore)(Thread.currentThread());
		
	if(autoParcheggiati.size() < posti) {
		              autoParcheggiati.add(p.getAutomobilista().getAuto());
		              return autoParcheggiati.indexOf(id);
		           
		}else {
			 
		}
	}
	
	synchronized Automobile restituisci () {
		
		Parcheggiatore p = (Parcheggiatore)(Thread.currentThread());
		Automobile auto =  autoParcheggiati.get(ticket);
		autoParcheggiati.remove(ticket);
		return auto;
		
	}
}
