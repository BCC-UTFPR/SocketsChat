package Thread;

import Utils.StringParser;
import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

public class ReceiveMensagemMulticast extends Thread {
	MulticastSocket msocket;
	HashMap<String, String> map = new HashMap();
	StringParser conversor = new StringParser();
	String internet_protocol = "224.0.0.3";
	InetAddress group;
	String username = null;
	String hostaddress = null;
	Thread t;

	public ReceiveMensagemMulticast(MulticastSocket ms, InetAddress gp, String usr) {
		msocket = ms;
		group = gp;
		username = usr;
		this.start();
	}

	public void run() {
		try {
			while(true) {
				byte[] buffer = new byte[1000];
				DatagramPacket message_receive_datagram = new DatagramPacket(buffer, buffer.length);
				msocket.receive(message_receive_datagram);
				String message_receive = new String(message_receive_datagram.getData());
				
				if (message_receive.startsWith("JOINACK")) {
					hostaddress = message_receive_datagram.getAddress().getHostAddress();
					String data = StringParser.convertjoined(message_receive);
					String new_user = data.trim();
					
					if (!this.map.containsKey(new_user)) {
						String message_ack = "JOINACK " + username;
						byte[] join_bytes = message_ack.getBytes();
						System.out.println("O usuário " + new_user + " entrou no chat.");
						DatagramPacket m_join_message = new DatagramPacket(join_bytes, join_bytes.length, group,
								6780);
						msocket.send(m_join_message);
						map.put(new_user, hostaddress);

						for (Map.Entry<String, String> entry : map.entrySet()) {
							String key = (String) entry.getKey();
							String value = (String) entry.getValue();
							System.out.println(key + " : " + value);
						}
					}
				} else if (message_receive.startsWith("LEAVEACK")) {
					String leave_user = StringParser.convertleaved(message_receive).trim();
					System.out.println("O usuário " + leave_user + " saiu da sala.");
					map.remove(leave_user);

					for (Map.Entry<String, String> entry : map.entrySet()) {
						String key = (String) entry.getKey();
						String value = (String) entry.getValue();
						System.out.println(key + " : " + value);
					}
				} else {
					System.out.println(message_receive);
				}
			}
		} catch (IOException ex) {
			Logger.getLogger(ReceiveMensagemMulticast.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
	}

	public HashMap getHash() {
		return this.map;
	}
}
