package grillom.client;

public class Packet {
	// ex. Conn:Will Mueller
	public static String Connection(String username) {
		String str = String.format("Conn:%s", username);
		return str;
	}

	// ex. Disc:8
	public static String Disconnection(int playerNumber) {
		String str = String.format("Disc:%d", playerNumber);
		return str;
	}

	// ex. #8x200y1000
	public static String Location(int playerNumber, int x, int y) {
		String str = String.format("#%dx%dy%d", playerNumber, x, y);
		return str;
	}

	// ex. @Will Mueller:Hello World
	public static String Chat(String username, String message) {
		String str = String.format("@%s:%s", username, message);
		return str;
	}

	// ex. &8:Will Mueller
	public static String Player(int playerNumber, String username) {
		String str = String.format("&%d:%s", playerNumber, username);
		return str;
	}
	
	// ex. Welcome:8
	public static String Welcome(int playerNumber) {
		String str = String.format("Welcome:%d", playerNumber);
		return str;
	}
	
	// ex. Tag2:8
	public static String Tag(int oldplayer, int newplayer) {
		String str = String.format("Tag%d:%d", oldplayer, newplayer);
		return str;
	}

}
