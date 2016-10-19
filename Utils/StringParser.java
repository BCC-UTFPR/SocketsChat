package Utils;

public class StringParser {
	public static String convertjoin(String[] array) {
		StringBuilder builder = new StringBuilder();
		String[] arrayOfString = array;
		int j = array.length;
		for (int i = 0; i < j; i++) {
			String str = arrayOfString[i];
			builder.append(str + " ");
		}
		String message = builder.toString().replace("JOIN", "");
		return message.trim();
	}

	public static String convertmsg(String[] array, String username) {
		StringBuilder builder = new StringBuilder();
		String[] arrayOfString = array;
		int j = array.length;
		for (int i = 0; i < j; i++) {
			String str = arrayOfString[i];
			builder.append(str + " ");
		}
		String message = builder.toString().replace("MSG", "").replace(username, username + " diz:");
		return message.trim();
	}

	public static String convertjoined(String message_receive) {
		String message = message_receive.replace("JOINACK", "");
		return message;
	}

	public static String convertleaved(String message_receive) {
		String message = message_receive.replace("LEAVEACK", "");
		return message;
	}
	
	public static String convertDownfile(String message_downfile){
		String[] array = message_downfile.split(" ");
		String message = array[2];
		return message.trim();
	}

	public static String convertMsgUdp(String[] array, String username, String d_username) {
		StringBuilder builder = new StringBuilder();
		String[] arrayOfString = array;
		int j = array.length;
		for (int i = 0; i < j; i++) {
			String str = arrayOfString[i];
			builder.append(str + " ");
		}
		String message = builder.toString().replace("MSGIDV", "").replace("FROM", "").replace(username, "")
				.replace("TO", "").replace(d_username, "");
		return message.trim();
	}
}
