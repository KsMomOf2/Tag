package grillom.server;

import java.io.IOException;
import java.net.Socket;

public class NewClientListener extends Thread {
	private Server server;

	public NewClientListener(Server server) {
		this.server = server;
	}

	public void run() {
		while (true) {
			try {
				Socket newSocket = server.getServer().accept();

				server.getSocketList().add(newSocket);
			} catch (IOException e) {
				server.print("Client connection issue");
			} catch (Exception e) {
			}
		}
	}
}
