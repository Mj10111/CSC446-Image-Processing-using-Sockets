package thread_testing;

import java.io.*;
import java.net.*;

public class TServer {

	public static void main(String[] args) throws IOException {
		@SuppressWarnings("resource")
		// Create out server socket, port 5000
		ServerSocket ss = new ServerSocket(5000);
	
		// Always be looking for new Clients to connect while the server is running
		while(true) {
			// Display Server attributes in the console
			Socket socket = null;
			System.out.println("Server has been created!");
			InetAddress ip = InetAddress.getLocalHost();
			String hostname = ip.getHostName();
			String hostAddress = ip.getHostAddress();
			System.out.println("Server IP: " + ip);
			System.out.println("Hostname: " + hostname);
			System.out.println("HostAddress: " + hostAddress);
			
			try {
				// Accept new client
				socket = ss.accept();
				
				System.out.println("A new client is connected: " + socket);
				
				// Create inputStream so we can accept the image
				InputStream inputStream = socket.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(inputStream);
				
				// Create outputStream so we can send the processed image after
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				
				System.out.println("Assigning new thread for this client");
				// Create new ClientHandler who knows is passed the socket, BufferedInputStream, and ObjectOutputStream
				Thread t = new ClientHandler(socket, bis, oos);
				
				t.start();
			} catch (Exception e) {
				// If something goes wrong, close the socket
				socket.close();
				e.printStackTrace();
			}
		}
	}
}
