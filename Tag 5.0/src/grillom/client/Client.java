package grillom.client;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class Client {
	public static final int TICKRATE = 240;
	public static final int PORT = 8546;

	public static final int WIDTH = 1024;
	public static final int HEIGHT = 720;
	private static final String TITLE = "Matthew Grillo APCSA Final Project 2018";

	private static final int SPEED = 1;

	private JFrame frame;
	private GamePanel panel;
	private GameLoop loop;
	private String username;
	private Connection connection;

	private Player player;
	private ArrayList<Player> allPlayers;

	private boolean hasConnection = false;
	
	private int playerIt = -2;
	private int lastTagged = -2;

	public Client() {
		allPlayers = new ArrayList<Player>();

		frame = new JFrame(TITLE);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (hasConnection) {
					connection.sendPacket(Packet.Disconnection(player.getPlayerNumber()));
				}
				print("Disconnected");
				System.exit(0);
			}
		});
		frame.setSize(WIDTH, HEIGHT);
		frame.setLocationRelativeTo(null);

		panel = new GamePanel(this);
		panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));

		loop = new GameLoop(this);
		addKeyListeners();

		frame.add(panel);
		frame.pack();
		frame.setVisible(true);

		username = JOptionPane.showInputDialog("Enter a username:");
		if (username == null || username.equals(""))
			username = "Default";
		print(username);

		player = new Player(username);
		allPlayers.add(player);

		render();
		loop.start();

		String address = JOptionPane.showInputDialog("Enter an address for multiplayer:");
		if (address != null || !address.equals("")) {
			// Start connection
			connection = new Connection(this, address);
			if (hasConnection) {
				String packet = Packet.Connection(username);
				connection.sendPacket(packet);
				print(packet);
			}
		}
	}

	public void addKeyListeners() {
		InputMap inputMap = panel.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = panel.getActionMap();

		// W key map
		Action wPress = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				loop.setYdec(-SPEED);
			}
		};
		Action wRelease = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				loop.setYdec(0);
			}
		};
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, false), "w.press");
		actionMap.put("w.press", wPress);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, true), "w.release");
		actionMap.put("w.release", wRelease);

		// S key map
		Action sPress = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				loop.setYinc(SPEED);
			}
		};
		Action sRelease = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				loop.setYinc(0);
			}
		};
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, false), "s.press");
		actionMap.put("s.press", sPress);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, true), "s.release");
		actionMap.put("s.release", sRelease);

		// A key map
		Action aPress = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				loop.setXdec(-SPEED);
			}
		};
		Action aRelease = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				loop.setXdec(0);
			}
		};
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, false), "a.press");
		actionMap.put("a.press", aPress);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, true), "a.release");
		actionMap.put("a.release", aRelease);

		// D key map
		Action dPress = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				loop.setXinc(SPEED);
			}
		};
		Action dRelease = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				loop.setXinc(0);
			}
		};
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false), "d.press");
		actionMap.put("d.press", dPress);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, true), "d.release");
		actionMap.put("d.release", dRelease);
	}

	public void render() {
		this.frame.repaint();
		this.panel.repaint();
	}

	public void print(String str) {
		System.out.println(str);
	}

	public String checkConnection() {
		String s = connection.check();
		return s;
	}
	public int getPlayerIt(){
		return playerIt;
	}
	public int getLastTagged(){
		return lastTagged;
	}

	public void processPacket(String lineIn) {
		//print ("Client from Server: " + lineIn);
		if (lineIn.startsWith("Welcome")) {
			int playerNumber = Integer.parseInt(lineIn.substring(lineIn.indexOf(':') + 1));
			
			player.setPlayerNumber(playerNumber);
			
			connection.sendPacket(Packet.Player(playerNumber, username));
			print(Packet.Player(playerNumber, username));
			
			return;
		}
		else if (lineIn.startsWith("Tag")) {
			int oldp = Integer.parseInt(lineIn.substring(lineIn.indexOf('g') + 1, lineIn.indexOf(':')));
			int newp = Integer.parseInt(lineIn.substring(lineIn.indexOf(':') + 1));
			
			playerIt = newp;
			lastTagged = oldp;
			for (Player p : allPlayers) {
				if (p.getPlayerNumber() == oldp)
					p.setIt(false);
				if (p.getPlayerNumber() == newp)
					p.setIt(true);
			}
			print(lineIn);
			return;
		}
		// Add new player packet
		else if (lineIn.substring(0, 1).equals("&")) {
			int playerNumber = Integer.parseInt(lineIn.substring(1, 2));
			String username = lineIn.substring(lineIn.indexOf(':') + 1);

			allPlayers.add(new Player(playerNumber, username));
			return;
		}
		// Update location packet
		else if (lineIn.startsWith("#")) {
			int playerNumber = Integer.parseInt(lineIn.substring(1, 2));
			int x = Integer.parseInt(lineIn.substring(lineIn.indexOf('x') + 1, lineIn.indexOf('y')));
			int y = Integer.parseInt(lineIn.substring(lineIn.indexOf('y') + 1));

			for (Player p : allPlayers) {
				if (p.getPlayerNumber() == playerNumber) {
					p.setX(x);
					p.setY(y);

					return;
				}
			}
		}
		// Remove Player Packet
		else if (lineIn.startsWith("Disc")) {
			int playerNumber = Integer.parseInt(lineIn.substring(lineIn.indexOf(':') + 1));

			for (int i = 0; i < allPlayers.size(); i++) {
				if (allPlayers.get(i).getPlayerNumber() == playerNumber) {
					allPlayers.remove(i);
					return;
				}
			}
		}
		// Chat packet
		else if (lineIn.startsWith("@")) {
			// addChatMessage(lineIn.substring(1));
			return;
		}
	}

	public static void main(String[] args) {
		new Client();
	}

	public Player getPlayer() {
		return player;
	}

	public ArrayList<Player> getPlayers() {
		return allPlayers;
	}
	
	public Connection getConnection () {
		return this.connection;
	}

	public boolean hasConnection() {
		return hasConnection;
	}

	public void setHasConnection(boolean status) {
		hasConnection = status;
	}

}
