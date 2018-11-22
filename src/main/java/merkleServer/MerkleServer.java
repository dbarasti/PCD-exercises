package merkleServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
 
public class MerkleServer {
 
	public static final String END_OF_SESSION = "close";
	
	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException {
 
		// Selector: multiplexor of SelectableChannel objects
		Selector selector = Selector.open(); // selector is open here
 
		// ServerSocketChannel: selectable channel for stream-oriented listening sockets
		ServerSocketChannel serverSocket = ServerSocketChannel.open();
		InetSocketAddress localAddr = new InetSocketAddress("localhost", 1111);
 
		// Binds the channel's socket to a local address and configures the socket to listen for connections
		serverSocket.bind(localAddr);
 
		// Adjusts this channel's blocking mode.
		serverSocket.configureBlocking(false);
 
		int ops = serverSocket.validOps();
		SelectionKey selectKy = serverSocket.register(selector, ops, null);
 
		// Infinite loop..
		// Keep server running
		while (true) {

			log("i'm a server and i'm waiting for new connection and buffer select...", "out");
			// Selects a set of keys whose corresponding channels are ready for I/O operations
			selector.select();

			// token representing the registration of a SelectableChannel with a Selector
			Set<SelectionKey> activeKeys = selector.selectedKeys();
			Iterator<SelectionKey> keys = activeKeys.iterator();

			while (keys.hasNext()) {
				SelectionKey myKey = keys.next();

				// Tests whether this key's channel is ready to accept a new socket connection
				if (myKey.isAcceptable()) {
					SocketChannel clientSocket = serverSocket.accept();

					// Adjusts this channel's blocking mode to false
					clientSocket.configureBlocking(false);

					// Operation-set bit for read operations
					clientSocket.register(selector, SelectionKey.OP_READ);
					log("Connection Accepted: " + clientSocket.getLocalAddress() + "\n", "err");

					// Tests whether this key's channel is ready for reading
				} else if (myKey.isReadable()) {
					SocketChannel clientSocket = (SocketChannel) myKey.channel();
					ByteBuffer buffer = ByteBuffer.allocate(256);
					clientSocket.read(buffer);
					String result = new String(buffer.array()).trim();

					log("--- Message received: " + result, "err");

					if (result.equals("close")) {
						clientSocket.close();
						log("\nIt's time to close this connection as we got a close packet", "out");
					}

					buffer.clear();

					if(clientSocket.isConnected()) {
						byte[] message = new String("risposta al client").getBytes();
						buffer = ByteBuffer.wrap(message);
						try {
							clientSocket = (SocketChannel) myKey.channel();
							clientSocket.write(buffer);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				} else if (myKey.isWritable()) {
					byte[] message = new String("risposta al client").getBytes();
					ByteBuffer buffer = ByteBuffer.wrap(message);
					try {
						SocketChannel clientSocket = (SocketChannel) myKey.channel();
						clientSocket.write(buffer);
					} catch (IOException e) {
						e.printStackTrace();
					}
					buffer.clear();
				}


			}
			//important: should delete, otherwise re-iterated the next turn again.
			keys.remove();
		}
	}
 
	private static void log(String str, String mode) {
		switch(mode) {
			case "out": {System.out.println(str); break;}
			case "err": {System.err.println(str); break;}
			default: {}
		}
	}
}
