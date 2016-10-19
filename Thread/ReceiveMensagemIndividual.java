package Thread;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ReceiveMensagemIndividual extends Thread {
	DatagramSocket aSocket = null;
	DatagramPacket reply = null;

	public ReceiveMensagemIndividual(int porta) throws java.net.SocketException {
		aSocket = new DatagramSocket(porta);
		this.start();
	}

	public void run() {
		try {
			while(true) {
				byte[] buffer = new byte[1000];
				reply = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(reply);
				String message = new String(reply.getData());
				System.out.println("Mensagem recebida: " + message);
			}
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		}
	}
}
