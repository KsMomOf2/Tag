package grillom.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import grillom.client.Packet;

public class ServerLoop extends Thread {
	private Server server;

	public ServerLoop(Server server) {
		this.server = server;
	}

	public void run() {
		while (true) {
			// Check every socket to see if they are empty, if not then process
			// the readers
			try {
				for (Socket s : server.getSocketList()) {
					BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));

					if (input.ready()) {
						server.processPacket(s, input.readLine());
						//server.print(input.readLine());
					}
				}
			} catch (Exception e) {
				server.print("Listening error");
				e.printStackTrace();
			}

		}
	}

	public void sendPacketTo(Socket s, String packet) {
		try {
			PrintWriter output = new PrintWriter(s.getOutputStream(), true);
			output.println(packet);
		} catch (Exception e) {
			server.print("Packet error: " + packet);
		}
	}

	public void sendPacketToAll(String packet) {
		for (Socket s : server.getSocketList()) {
			try {
				PrintWriter output = new PrintWriter(s.getOutputStream(), true);
				output.println(packet);
			} catch (Exception e) {
				server.print("Packet error: " + packet);
			}
		}
	}

	public void sendPacketToAllBut(Socket but, String packet) {
		for (Socket s : server.getSocketList()) {
			if (s != but) {
				try {
					PrintWriter output = new PrintWriter(s.getOutputStream(), true);
					output.println(packet);
				} catch (Exception e) {
					server.print("Packet error: " + packet);
				}
			}
		}
	}
}
