package Connect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import Thread.ReceiveMensagemIndividual;
import Thread.ReceiveMensagemMulticast;
import Thread.TCPServer;

import java.net.*;
import java.io.*;
import Utils.StringParser;

public class StartConnection {
	public static void main(String[] args) throws Exception {
		System.out.println("Versão: 2.5");
		System.out.println("Alunos: Luiz Felipe e Vinicius Ribeiro");
		StringParser conversor = new StringParser();
		Scanner reader = new Scanner(System.in);
		int porta_multicast = 6780;
		int porta_individual = 6790;
		int porta_download = 7896;
		MulticastSocket multicast = new MulticastSocket(porta_multicast);
		DatagramSocket udp = new DatagramSocket();
		String internet_protocol = "224.0.0.3";
		InetAddress group = InetAddress.getByName(internet_protocol);
		System.out.print("Digite o nome do usuário: ");
		String username = reader.nextLine().toString();
		String j_username = null;
		ReceiveMensagemMulticast multicast_mensagem = new ReceiveMensagemMulticast(
				multicast, group, username);
		ReceiveMensagemIndividual individual_mensagem = new ReceiveMensagemIndividual(
				porta_individual);

		TCPServer tcp_server = new TCPServer();

		while (true) {
			String command = reader.nextLine();
			String[] array = command.split("\\s+");
			String option = array[0];

			switch (option) {
			case "JOIN":
				if (j_username != null && !j_username.equals(username)) {
					System.out.println("Você já está logado como " + username);
				} else {
					j_username = StringParser.convertjoin(array);
					System.out.println(j_username);
					if (!j_username.equals(username)) {
						System.out
								.println("WARNING: Nome incompatível. Use: JOIN "
										+ username + "!");
						j_username = null;
					} else {
						multicast.joinGroup(group);
						System.out.println("Conectando...");
						String message_ack = "JOINACK " + username;
						byte[] join_bytes = message_ack.getBytes();
						DatagramPacket m_join_message = new DatagramPacket(
								join_bytes, join_bytes.length, group,
								porta_multicast);
						multicast.send(m_join_message);
					}
				}
				break;

			case "LEAVE":
				String l_username = array[1];
				if (l_username.equals(username)) {
					String message_leave = "LEAVEACK " + username;
					byte[] leave_bytes = message_leave.getBytes();
					DatagramPacket m_leave_message = new DatagramPacket(
							leave_bytes, leave_bytes.length, group,
							porta_multicast);
					multicast.send(m_leave_message);
					multicast.leaveGroup(group);
					multicast.close();
					System.exit(0);
				} else {
					System.out
							.println("Você está tentando sair como outro usuário");
				}
				break;

			case "MSG":
				String t_username = array[1];
				if (t_username.equals(username)) {
					String message = StringParser.convertmsg(array, username);
					byte[] msg_bytes = message.getBytes();
					DatagramPacket m_msg_message = new DatagramPacket(
							msg_bytes, msg_bytes.length, group, porta_multicast);
					multicast.send(m_msg_message);
				} else {
					System.out
							.println("Você está tentando enviar mensagem como outro usuário");
				}
				break;

			case "MSGIDV":
				HashMap<String, String> map = multicast_mensagem.getHash();
				String messageUdp = StringParser.convertMsgUdp(array, username,
						array[4]);
				byte[] messageUdp_bytes = messageUdp.getBytes();
				InetAddress ip_destino = InetAddress.getByName((String) map
						.get(array[4]));
				DatagramPacket udp_message = new DatagramPacket(
						messageUdp_bytes, messageUdp.length(), ip_destino,
						porta_individual);
				udp.send(udp_message);
				break;

			case "LISTFILES":
				String list_username = array[1];
				HashMap<String, String> list_map = multicast_mensagem.getHash();
				if (list_map.get(list_username) != null) {
					InetAddress ip_list_destino = InetAddress
							.getByName(list_map.get(list_username));
					Socket s = new Socket(ip_list_destino, porta_download);
					DataInputStream in = new DataInputStream(s.getInputStream());
					DataOutputStream out = new DataOutputStream(
							s.getOutputStream());
					String message_list = "LISTFILES";
					out.writeUTF(message_list);
					String data = null;
					while (data == null) {
						data = in.readUTF();
						System.out.println(data);
					}
					in.close();
					out.close();
					s.close();
				}
				break;

			case "DOWNFILE":
				String down_username = array[1];
				String filename = array[2];
				HashMap<String, String> down_map = multicast_mensagem.getHash();
				if (down_map.get(down_username) != null) {
					InetAddress ip_download_destino = InetAddress
							.getByName(down_map.get(down_username));
					Socket s = new Socket(ip_download_destino, porta_download);
					DataInputStream in = new DataInputStream(s.getInputStream());
					DataOutputStream out = new DataOutputStream(
							s.getOutputStream());
					String message_list = "DOWNFILE " + down_username + " "
							+ filename;
					out.writeUTF(message_list);
					String data = null;
					while (data == null) {
						data = in.readUTF();
						System.out.println(data);
					}

					byte[] contents = new byte[10000];
					FileOutputStream fos = new FileOutputStream(filename);
					BufferedOutputStream bos = new BufferedOutputStream(fos);
					InputStream is = s.getInputStream();
					int bytesRead = 0;

					while ((bytesRead = is.read(contents)) != -1) {
						bos.write(contents, 0, bytesRead);
					}

					System.out.println("Arquivo baixado com sucesso!");
					bos.flush();
					in.close();
					out.close();
					s.close();
				}
				break;

			case "LISTHASH":
				HashMap<String, String> listhashmap = multicast_mensagem
						.getHash();

				for (String name : listhashmap.keySet()) {
					String key = name.toString();
					String value = listhashmap.get(name).toString();
					System.out.println(key + " " + value);
				}
				break;

			}
		}
	}
}
