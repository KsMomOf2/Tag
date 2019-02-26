package grillom.client;

public class GameLoop extends Thread {
	private Client client;

	private int xinc = 0;
	private int yinc = 0;
	private int xdec = 0;
	private int ydec = 0;

	public GameLoop(Client client) {
		this.client = client;
	}

	public void run() {
		Player player = client.getPlayer();

		float initial = System.nanoTime();
		double nano = 1000000000 / Client.TICKRATE;
		double elapsed = 0;

		while (true) {
			if (client.hasConnection()) {
				String lineIn = client.checkConnection();
				if (lineIn != null) {
					client.processPacket(lineIn);
				}
			}
			// create ticker
			long current = System.nanoTime();
			elapsed += (current - initial) / nano;
			initial = current;

			if (elapsed >= 1) {
				move(player);
				elapsed -= 1;
			}
		}
	}

	public void move(Player player) {
		int newX = player.getX() + xinc + xdec;
		int newY = player.getY() + yinc + ydec;

		boolean send = false;
		if (player.validX(newX)) {
			player.setX(newX);
			send = true;
		}
		if (player.validY(newY)) {
			player.setY(newY);
			send = true;
		}
		if (send == true && client.hasConnection() && player.getPlayerNumber() != -1)
			client.getConnection().sendPacket(Packet.Location(player.getPlayerNumber(), player.getX(), player.getY()));

		checkTag(player);
		client.render();
	}

	public void checkTag(Player player) {
		if (player.isIt()) {
			for (Player other : client.getPlayers()) {
				if (player.getPlayerNumber() != other.getPlayerNumber()
						&& other.getPlayerNumber() != client.getLastTagged()) {
					double distance = Math.sqrt(
							Math.pow(player.getX() - other.getX(), 2) + Math.pow(player.getY() - other.getY(), 2));
					if (distance < GamePanel.SIZE) {
						player.setIt(false);
						other.setIt(true);
						client.print("You tagged " + other.getUsername());
						client.getConnection()
							.sendPacket(Packet.Tag(player.getPlayerNumber(), other.getPlayerNumber()));
						client.getConnection()
							.sendPacket(Packet.Tag(player.getPlayerNumber(), other.getPlayerNumber()));
						client.getConnection()
							.sendPacket(Packet.Tag(player.getPlayerNumber(), other.getPlayerNumber()));
						return;
					}
				}
			}
		}
	}

	public void setXinc(int xinc) {
		this.xinc = xinc;
	}

	public void setYinc(int yinc) {
		this.yinc = yinc;
	}

	public void setXdec(int xdec) {
		this.xdec = xdec;
	}

	public void setYdec(int ydec) {
		this.ydec = ydec;
	}

}
