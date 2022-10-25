package thread_testing;

import java.io.*;
import java.net.*;

public class TServer {

	public static void main(String[] args) throws IOException {
		@SuppressWarnings("resource")
		ServerSocket ss = new ServerSocket(5000);
		
		while(true) {
			Socket socket = null;
			System.out.println("Server has been created!");
			InetAddress ip = InetAddress.getLocalHost();
			String hostname = ip.getHostName();
			String hostAddress = ip.getHostAddress();
			System.out.println("Server IP: " + ip);
			System.out.println("Hostname: " + hostname);
			System.out.println("HostAddress: " + hostAddress);
			
			try {
				socket = ss.accept();
				
				System.out.println("A new client is connected: " + socket);
				
				InputStream inputStream = socket.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(inputStream);
				
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				
				System.out.println("Assigning new thread for this client");
				Thread t = new ClientHandler(socket, bis, oos);
				
				t.start();
			} catch (Exception e) {
				socket.close();
				e.printStackTrace();
			}
		}
	}
}
