import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.sun.corba.se.spi.orbutil.fsm.Input;

public class Server {

	public static void main(String[] args) {
		List<StatoParcheggio> parcheggi = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			parcheggi.add(new StatoParcheggio(new Parcheggio(i, 5, 10), true));
		}
		ServerSocket server = null;
		try {
			server = new ServerSocket(53535);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (server == null)
			System.exit(-1);
		while (true) {
			try {
				Socket socket = server.accept();
				DataInputStream in;
				byte[] byteReceived = new byte[1000];
				String messageString = "";
				in = new DataInputStream(socket.getInputStream());
				int bytesRead = 0;
				bytesRead = in.read(byteReceived);
				messageString += new String(byteReceived, 0, bytesRead);
				if (messageString.equals("richiestaParcheggi")) {
					ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
					List<Parcheggio> daRitornare = new ArrayList<>();
					for (StatoParcheggio stato : parcheggi) {
						if (stato.stato)
							daRitornare.add(stato.parcheggio);
					}
					objectOutput.writeObject(daRitornare);
					objectOutput.flush();
					objectOutput.close();
				} else if (messageString.contains("parcheggioOccupato")) {
					PrintWriter stringOutput = new PrintWriter(socket.getOutputStream(), true);
					int idParcheggio = Integer.parseInt(messageString.substring(18));
					boolean trovato = false;
					for (StatoParcheggio stato : parcheggi) {
						if (stato.parcheggio.getIdParcheggio() == idParcheggio) {
							if (stato.parcheggio.occupaPosto() == 0)
								stato.stato = false;
							stato.parcheggio.incrementaTicket();
							trovato = true;
						}
					}
					if (trovato)
						stringOutput.write("Occupato un posto nel parcheggio " + idParcheggio + ".");
					else
						stringOutput.write("Parcheggio con id " + idParcheggio + " non trovato.");
					stringOutput.flush();
					stringOutput.close();
				} else if (messageString.contains("parcheggioDisponibile")) {
					PrintWriter stringOutput = new PrintWriter(socket.getOutputStream(), true);
					int idParcheggio = Integer.parseInt(messageString.substring(21));
					boolean trovato = false;
					for (StatoParcheggio stato : parcheggi) {
						if (stato.parcheggio.getIdParcheggio() == idParcheggio) {
							if (stato.parcheggio.liberaPosto() > 0)
								stato.stato = true;
							trovato = true;
						}
					}
					if (trovato)
						stringOutput.write("Liberato un posto nel parcheggio " + idParcheggio + ".");
					else
						stringOutput.write("Parcheggio con id " + idParcheggio + " non trovato.");
					stringOutput.flush();
					stringOutput.close();
				}
				in.close();
				socket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}