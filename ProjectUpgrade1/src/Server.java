import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Server {

	public static void main(String[] args) {
		List<Parcheggio> parcheggi = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			parcheggi.add(new Parcheggio(i, 1, 5));
		}
		try {
			Selector selector = Selector.open();
			ServerSocketChannel serverSocket = ServerSocketChannel.open();
			System.out.println("Server started...");
			serverSocket.bind(new InetSocketAddress(53535));
			serverSocket.configureBlocking(false);
			serverSocket.register(selector, SelectionKey.OP_ACCEPT);

			while (true) {
				selector.select();
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> iter = selectedKeys.iterator();
				while (iter.hasNext()) {
					SelectionKey key = iter.next();
					if (key.isAcceptable()) {
						acceptClientRequest(selector, serverSocket);
					}
					if (key.isReadable()) {
						readClientBytes(key, parcheggi);
					}
					iter.remove();
				}
			}
		} catch (Exception e) {
		}
	}

	private static void acceptClientRequest(Selector selector, ServerSocketChannel serverSocket) throws IOException {
		SocketChannel client = serverSocket.accept();
		client.configureBlocking(false);
		client.register(selector, SelectionKey.OP_READ);
	}

	private static void readClientBytes(SelectionKey key, List<Parcheggio> parcheggi) throws IOException {
		SocketChannel client = (SocketChannel) key.channel();
		try {
			int BUFFER_SIZE = 256;
			ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
			try {
				if (client.read(buffer) == -1) {
					client.close();
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			buffer.flip();
			Charset charset = Charset.forName("UTF-8");
			CharsetDecoder decoder = charset.newDecoder();
			CharBuffer charBuffer = decoder.decode(buffer);
			String messageString = charBuffer.toString();
			System.out.println("Received: " + messageString);
			if (messageString.equals("richiestaParcheggi")) {
				List<Parcheggio> daRitornare = new ArrayList<>();
				for (Parcheggio parcheggio : parcheggi) {
					if (parcheggio.postiLiberi() > 0)
						daRitornare.add(parcheggio);
				}
				ByteBuffer writeBuffer = ByteBuffer.wrap(toString(daRitornare).getBytes(charset));
				client.write(writeBuffer);
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
					ByteBuffer writeBuffer = ByteBuffer.wrap("ricevuto".getBytes(charset));
					client.write(writeBuffer);
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
						ByteBuffer writeBuffer = ByteBuffer.wrap(toString(nuovoStato).getBytes(charset));
						client.write(writeBuffer);
					} else {
						System.out.println("Command not valid.");
					}
				} catch (Exception e) {
					System.out.println("Command not valid.");
				}
			} else if (messageString.equals("aggiornaStatoParcheggio")) {
				try {
					Thread.sleep(400);
					ByteBuffer receivedBuffer = ByteBuffer.allocate(100000);
					try {
						if (client.read(receivedBuffer) == -1) {
							client.close();
							return;
						}
					} catch (Exception e) {
						e.printStackTrace();
						return;
					}
					receivedBuffer.flip();
					Object received = fromString(receivedBuffer);
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
					e.printStackTrace();
					System.out.println("Command not recognized.");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			client.close();
		}
	}

	private static Object fromString(ByteBuffer buf) throws IOException, ClassNotFoundException {
		byte[] data = new byte[buf.remaining()];
		buf.get(data);
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
		Object o = ois.readObject();
		ois.close();
		return o;
	}

	private static String toString(Serializable o) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(o);
		oos.close();
		return Base64.getEncoder().encodeToString(baos.toByteArray());
	}

	private static String toString(List<Parcheggio> l) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(l);
		oos.close();
		return Base64.getEncoder().encodeToString(baos.toByteArray());
	}
}