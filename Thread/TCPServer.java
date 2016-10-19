package Thread;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

import Utils.ListFiles;
import Utils.StringParser;

public class TCPServer extends Thread {
	public TCPServer() {
		this.start();
	}

	public void run() {
		try {
			int serverPort = 7896;
			ServerSocket listenSocket = new ServerSocket(serverPort);

			while (true) {
				Socket clientSocket = listenSocket.accept();
				Connection c = new Connection(clientSocket);
			}

		} catch (IOException e) {
			System.out.println("Listen socket:" + e.getMessage());
		}
	}
}

class Connection extends Thread {
	DataInputStream in;
	DataOutputStream out;
	Socket clientSocket;
	ServerSocket listenSocket;
	
	public Connection(Socket aClientSocket) {
		try {
			clientSocket = aClientSocket;
			in = new DataInputStream(clientSocket.getInputStream());
			out = new DataOutputStream(clientSocket.getOutputStream());
			this.start();
		} catch (IOException e) {
			System.out.println("Connection:" + e.getMessage());
		}
	}

	public void run() {
		StringParser parser = new StringParser();
		try {

			while (true) {
				String data = in.readUTF();
				if (data.equals("LISTFILES")) {
					System.out
							.println("TCPServer: Usuário requisitando lista de arquivos...");
					ListFiles filesfinder = new ListFiles();
					ArrayList<String> myList = filesfinder.listFilesForFolder();
					out.writeUTF("FILES " + Arrays.toString(myList.toArray()));
					break;
				} else if (data.startsWith("DOWNFILE")) {
					String filename = parser.convertDownfile(data);
					String home = System.getProperty("user.home");
					String path = home + "/Downloads/" + filename;
					File f = null;
					f = new File(path);
					if (f.exists() && !f.isDirectory()) {
						long filelength = f.length();

						System.out
								.println("TCPServer: Usuário requisitando arquivos... enviando: "
										+ filename);
						out.writeUTF("DOWNINFO [" + filename + "," + filelength
								+ ","
								+ clientSocket.getLocalAddress().toString()
								+ "," + clientSocket.getLocalPort() + "]");

				        File file = new File(path);
				        FileInputStream fis = new FileInputStream(file);
				        BufferedInputStream bis = new BufferedInputStream(fis); 
				        OutputStream os = clientSocket.getOutputStream();
				        byte[] contents;
				        long fileLength = file.length(); 
				        long current = 0;
				        long start = System.nanoTime();
				        
				        while(current!=fileLength){ 
				            int size = 10000;
				            if(fileLength - current >= size)
				                current += size;    
				            else{ 
				                size = (int)(fileLength - current); 
				                current = fileLength;
				            } 
				            contents = new byte[size]; 
				            bis.read(contents, 0, size); 
				            os.write(contents);
				            System.out.println("Enviando arquivo... "+ (current*100)/fileLength + "% completo!");
				        }   
				        
				        os.flush();
				        System.out.println("Arquivo enviado com sucesso!");

					}
					else {
						System.out.println("Usuário requisitando arquivo inexistente, cancelando operação.");
					}

				}
			}
			in.close();
			out.close();
			clientSocket.close();
		} catch (EOFException e) {
			System.out.println("EOF: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("leitura: " + e.getMessage());
		}
	}
}
