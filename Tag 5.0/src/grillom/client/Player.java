package grillom.client;

import java.awt.Color;

public class Player {
	private int playerNumber;

	private int x;
	private int y;
	private String username;
	private Color color;
	
	private boolean it = false;

	public Player(String username) {
		this (-1, username);
	}
	
	public Player(int pn, String username){
		this (pn, Client.WIDTH/2, Client.HEIGHT/2, username, Color.green);
	}

	public Player(int pn, int x, int y, String username, Color color) {
		this.playerNumber = pn;
		this.x = x;
		this.y = y;
		this.username = username;
		this.color = color;
	}

	public boolean validX(int newX) {
		if (newX < 0 || newX > Client.WIDTH)
			return false;
		return true;
	}

	public boolean validY(int newY) {
		if (newY < 0 || newY > Client.HEIGHT)
			return false;
		return true;
	}

	public int getPlayerNumber() {
		return playerNumber;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public String getUsername() {
		return username;
	}

	public Color getColor() {
		return color;
	}

	public void setPlayerNumber(int playerNumber) {
		this.playerNumber = playerNumber;
	}

	public boolean isIt() {
		return it;
	}

	public void setIt(boolean b) {
		if (b == true) {
			color = Color.red;
		}else {
			color = Color.green;
		}
		it = b;
	}
}
