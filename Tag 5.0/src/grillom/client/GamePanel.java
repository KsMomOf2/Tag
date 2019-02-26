package grillom.client;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class GamePanel extends JPanel {
	public final static int SIZE = 40;
	public final static int RINGTHICKNESS = 5;
	public final static int HALF = SIZE / 2;

	private Client client;

	public GamePanel(Client client) {
		this.client = client;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2D = (Graphics2D) g;

		g2D.setStroke(new BasicStroke(5));
		g2D.drawRect(0, 0, Client.WIDTH, Client.HEIGHT);

		for (Player p : client.getPlayers()) {
			g2D.setColor(p.getColor());
			g2D.fillOval(p.getX() - HALF, p.getY() - HALF, SIZE, SIZE);

			String name = p.getPlayerNumber() + ": " + p.getUsername();
			int stringWidth = g.getFontMetrics().stringWidth(name);
			g2D.setColor(Color.black);
			g2D.drawString(name, p.getX() - stringWidth / 2, p.getY() + SIZE);
		}
	}
}
