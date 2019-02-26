package grillom.server;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import grillom.client.Client;
import grillom.client.Packet;

public class Server {
	private ServerSocket server;
	private NewClientListener listener;
	private ServerLoop loop;

	private ArrayList<Socket> socketList;
	private ArrayList<String> playerList;
	
	private int numberCount = 0;
	
	private int playerIt = 0;
	private int lastTagged = -1;

	public Server() {
		try {
			playerList = new ArrayList<String>();
			socketList = new ArrayList<Socket>();

			server = new ServerSocket(Client.PORT);

			print("Server Started on " + Inet4Address.getLocalHost().getHostAddress() + ", Port " + Client.PORT);
			// Connection listener
			listener = new NewClientListener(this);
			listener.start();

			// ServerLoop
			loop = new ServerLoop(this);
			loop.start();
		} catch (IOException e) {
			print("Could not start server");
		}
	}

	public void processPacket(Socket s, String lineIn) {
		//print("Server from Client: " + lineIn);
		
		// new connection packet
		if (lineIn.startsWith("Conn")) {
			loop.sendPacketTo(s, Packet.Welcome(numberCount));
			for (String playerPacket : playerList) {
				loop.sendPacketTo(s, playerPacket);
			}
			loop.sendPacketTo(s, Packet.Tag(lastTagged, playerIt));
			numberCount++;
			
			return;
		}
		else if (lineIn.startsWith("Tag")) {
			print(lineIn);
			playerIt = Integer.parseInt(lineIn.substring(lineIn.indexOf(':') + 1));
			lastTagged = Integer.parseInt(lineIn.substring(lineIn.indexOf('g') + 1, lineIn.indexOf(':')));
			loop.sendPacketToAll(lineIn);
			return;
		}
		// Add new player packet
		else if (lineIn.startsWith("&")) {
			playerList.add(lineIn);
			loop.sendPacketToAllBut(s, lineIn);
			
			return;
		}
		// Update location packet
		else if (lineIn.startsWith("#")) {
			loop.sendPacketToAllBut(s, lineIn);
			return;
		}
		// Remove Player Packet
		else if (lineIn.startsWith("Disc")) {
			int playerNumber = Integer.parseInt(lineIn.substring(lineIn.indexOf(':') + 1));

			for (int i = 0; i < playerList.size(); i++) {
				if (Integer.parseInt(playerList.get(i).substring(1, 2)) == playerNumber)
					 playerList.remove(i);
			}
			loop.sendPacketToAllBut(s, lineIn);
			
			if (playerIt == playerNumber) {
				Random rand = new Random();
				int r = rand.nextInt(playerList.size());
				loop.sendPacketToAll(Packet.Tag(-1, r));
			}
			return;
		}
		// Chat packet
		else if (lineIn.startsWith("@")) {
			loop.sendPacketToAll(lineIn);
			return;
		}
		
	}

	public void print(String str) {
		System.out.println(str);
	}

	public static void main(String[] args) {
		new Server();
	}

	public ArrayList<Socket> getSocketList() {
		return socketList;
	}

	public ArrayList<String> getPlayersList() {
		return playerList;
	}

	public ServerSocket getServer() {
		return server;
	}

	public int getPlayerIt() {
		return playerIt;
	}

	public ArrayList<Socket> getSocketCopy() {
		ArrayList<Socket> copy = new ArrayList<Socket>();
		for (Socket s : socketList){
			copy.add(s);
		}
		return copy;
	}

	public ServerLoop getLoop() {
		return loop;
	}
}
