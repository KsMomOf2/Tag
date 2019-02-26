package grillom.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Connection {
	private Client client;
	private String address;

	private Socket socket;
	private PrintWriter output;
	private BufferedReader input;

	public Connection(Client client, String address) {
		this.client = client;
		this.address = address;

		startConnection();
	}

	private void startConnection() {
		try {
			socket = new Socket(address, Client.PORT);

			output = new PrintWriter(socket.getOutputStream(), true);
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			client.setHasConnection(true);
			client.print("Connected to " + address + " on port " + Client.PORT);
		} catch (IOException e) {
			client.print("could not connect to that server");
		}
	}

	public String check() {
		try {
			if (input.ready()) {
				String s = input.readLine();
				return s;
			}
		} catch (IOException e) {
		}
		return null;
	}

	public void sendPacket(String packet) {
		output.println(packet);
	}
}
