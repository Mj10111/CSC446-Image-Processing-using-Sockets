package thread_testing;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.*;

public class TClient {

	public static void main(String[] args) throws UnknownHostException, IOException {
		// The IP for my laptop is 192.168.1.16
		Socket socket = new Socket("localhost", 5000);
		System.out.println("Connected to server.");
		
		JFrame jFrame = new JFrame("Client - 1");
		jFrame.setSize(500, 500);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		ImageIcon imageIcon = new ImageIcon("src\\legoshi.png");
		
		
		JLabel jLabelPic = new JLabel(imageIcon);
		JLabel jLabelPicAfter = new JLabel();
		JButton jButton = new JButton("Send Image to Server.");
		jButton.setEnabled(false);
		JButton jButton1 = new JButton("Lock in choice.");
		
		String[] choices = {"1 Set image to gray-scale", "2 Invert colors", "3 Sharpen", "4 Blur",
							"5 Increase Saturation", "6 Decrease Saturation"};
		final JComboBox<String> cb = new JComboBox<String>(choices);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(3, 1));
		buttonPanel.add(cb);
		buttonPanel.add(jButton1);
		buttonPanel.add(jButton);
	
		JPanel imagesPanel = new JPanel();
		imagesPanel.setLayout(new GridLayout(1, 2));
		imagesPanel.add(jLabelPic);
		imagesPanel.add(jLabelPicAfter);
		
		
		
		jFrame.add(imagesPanel, BorderLayout.CENTER);
		jFrame.add(buttonPanel,BorderLayout.SOUTH);
				
		jFrame.setVisible(true);
		
		jButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				
				jButton.setEnabled(false);
				char option = ((String) cb.getSelectedItem()).charAt(0);
				
				try {
					OutputStream outputStream = socket.getOutputStream();
					BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
					
					
					Image image = imageIcon.getImage();	
					BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);		
					Graphics graphics = bufferedImage.createGraphics();
					graphics.drawImage(image, 0, 0, null);
					graphics.dispose();
					
					bufferedOutputStream.write(option);
					ImageIO.write(bufferedImage, "png", bufferedOutputStream);
					bufferedOutputStream.flush();
		
					System.out.println("Option sent to server: " + option);
					System.out.println("Image sent.");
					
					
					try {
						ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
						ImageIcon processedImage = (ImageIcon) ois.readObject();
						System.out.println("Processed image recieved from server");
						jLabelPicAfter.setIcon(processedImage);
						ois.close();
					} catch (IOException ex) {
						ex.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					
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
		
		
		
	}
}
