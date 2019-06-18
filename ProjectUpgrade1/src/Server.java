import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		List<Parcheggio> parcheggi = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			parcheggi.add(new Parcheggio(i, 1, 5));
		}
		ServerSocket server = null;
		try {
			server = new ServerSocket(53535);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (server == null)
			System.exit(-1);
		System.out.println("Server started...");
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
				System.out.println("Received: " + messageString);
				if (messageString.equals("richiestaParcheggi")) {
					ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
					List<Parcheggio> daRitornare = new ArrayList<>();
					for (Parcheggio parcheggio : parcheggi) {
						if (parcheggio.postiLiberi() > 0)
							daRitornare.add(parcheggio);
					}
					objectOutput.writeObject(daRitornare);
					objectOutput.flush();
				} else if (messageString.contains("statoParcheggiatore")) {
					try {
						String[] splitted = messageString.split("-");
						int idParcheggio = Integer.parseInt(splitted[1]);
						int indexParcheggiatore = Integer.parseInt(splitted[2]);
						boolean libero = splitted[3] == "1";
						for (Parcheggio parcheggio : parcheggi) {
							if (parcheggio.getIdParcheggio() == idParcheggio
									&& parcheggio.getNumParcheggiatori() > indexParcheggiatore) {
								parcheggio.parcheggiatori[indexParcheggiatore].setLibero(libero);
							}
						}
						PrintWriter stringOutput = new PrintWriter(socket.getOutputStream(), true);
						stringOutput.write("ricevuto");
						stringOutput.flush();
					} catch (Exception e) {
						System.out.println("Command not valid.");
					}
				} else if (messageString.contains("getStatoParcheggiatori")) {
					try {
						String[] splitted = messageString.split("-");
						int idParcheggio = Integer.parseInt(splitted[1]);
						Parcheggiatore[] nuovoStato = null;
						for (Parcheggio parcheggio : parcheggi) {
							if (parcheggio.getIdParcheggio() == idParcheggio) {
								nuovoStato = parcheggio.parcheggiatori;
							}
						}
						if (nuovoStato != null) {
							ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
							objectOutput.writeObject(nuovoStato);
							objectOutput.flush();
						} else {
							System.out.println("Command not valid.");
						}
					} catch (Exception e) {
						System.out.println("Command not valid.");
					}
				} else if (messageString.equals("aggiornaStatoParcheggio")) {
					try {
						ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
						Object received = input.readObject();
						if (received instanceof Parcheggio) {
							Parcheggio nuovoStato = (Parcheggio) received;
							for (int i = 0; i < parcheggi.size(); i++) {
								if (parcheggi.get(i).getIdParcheggio() == nuovoStato.getIdParcheggio()) {
									parcheggi.remove(i);
									parcheggi.add(nuovoStato);
									break;
								}
							}
						}
					} catch (Exception e) {
						System.out.println("Command not recognized.");
					}
				}
				in.close();
				socket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}