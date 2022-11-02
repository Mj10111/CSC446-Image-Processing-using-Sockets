package thread_testing;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.*;

public class TClient {

	public static void main(String[] args) throws UnknownHostException, IOException {
		// Create socket that connects to server IP, socket 5000
		Socket socket = new Socket("localhost", 5000);
		System.out.println("Connected to server.");
		
		// Create the window for the GUI
		JFrame jFrame = new JFrame("Client - 1");
		jFrame.setSize(500, 500);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// This is our original image, pulled from its file path
		ImageIcon imageIcon = new ImageIcon("src\\legoshi.png");
		
		// This entire section are components to be used in panels
		JLabel jLabelPic = new JLabel(imageIcon);
		JLabel jLabelPicAfter = new JLabel();
		JButton jButton = new JButton("Send Image to Server.");
		jButton.setEnabled(false);
		JButton jButton1 = new JButton("Lock in choice.");	
		String[] choices = {"1 Set image to gray-scale", "2 Invert colors", "3 Sharpen", "4 Blur",
							"5 Increase Saturation", "6 Decrease Saturation"};
		
		final JComboBox<String> cb = new JComboBox<String>(choices);
		
		// The panel for the buttons at the bottom of the GUI
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(3, 1));
		buttonPanel.add(cb);
		buttonPanel.add(jButton1);
		buttonPanel.add(jButton);
	
		// The panel for the before and after version of the image
		JPanel imagesPanel = new JPanel();
		imagesPanel.setLayout(new GridLayout(1, 2));
		imagesPanel.add(jLabelPic);
		imagesPanel.add(jLabelPicAfter);
		
		
		// Add the two panels to the JFrame, images in the center, buttons on the bottom
		jFrame.add(imagesPanel, BorderLayout.CENTER);
		jFrame.add(buttonPanel,BorderLayout.SOUTH);
			
		// Enable the JFrame
		jFrame.setVisible(true);
		
		// Custom code for when we send the image to the server
		jButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				
				jButton.setEnabled(false);
				// Grab the first character of the currently selected item in the option box
				char option = ((String) cb.getSelectedItem()).charAt(0);
				
				try {
					// Create the outputStream we'll send out option and image over
					OutputStream outputStream = socket.getOutputStream();
					BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
					
					// Make the image compatible with the BufferedOutputStream
					Image image = imageIcon.getImage();	
					BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);		
					Graphics graphics = bufferedImage.createGraphics();
					graphics.drawImage(image, 0, 0, null);
					graphics.dispose();
					
					// Send the option and image over the BufferedOuputStream
					bufferedOutputStream.write(option);
					ImageIO.write(bufferedImage, "png", bufferedOutputStream);
					bufferedOutputStream.flush();
		
					// Confirmations
					System.out.println("Option sent to server: " + option);
					System.out.println("Image sent.");
					
					
					try {
						// Create the inputStream so we can wait for out image to come back
						ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
						ImageIcon processedImage = (ImageIcon) ois.readObject();
						System.out.println("Processed image recieved from server");
						// Sent the second jLabel to the processed image, displaying the before and after side by side
						jLabelPicAfter.setIcon(processedImage);
						// Close out inputStream
						ois.close();
					} catch (IOException ex) {
						ex.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					
					// Close the outputStream and the socket
					bufferedOutputStream.close();
					socket.close();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		jButton1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jButton.setEnabled(true);
				jButton1.setEnabled(false);
				cb.setEnabled(false);
			}	
		});
		
		jFrame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		
	}
}
