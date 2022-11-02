package thread_testing;


import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import java.net.*;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;

class ClientHandler extends Thread  
{ 
    final Socket s;
	private BufferedInputStream bis;
	private ObjectOutputStream oos; 

	// A method that can be called to get the processed half of an image from the Worker
	public Image getProcessedHalf(TWorker w) {
		return w.getImage();
	}
	
	// Used to convert BufferedImage to a normal image, which can then be converted to an Imageicon
	public Image buffToIcon(Image image) {
		return image;
	}
	
  
    // Constructor 
    public ClientHandler(Socket s, BufferedInputStream bis, ObjectOutputStream oos)  
    { 
		this.s = s; 
        this.bis = bis; 
        this.oos = oos; 
    } 
  
    @Override
    public void run()  
    { 
   
    	try {
    		// Read the option and image using the BufferedInputStream received from Server
	    	char option = (char) bis.read();
			System.out.println("[ClientHandler] Option recieved by the server: " + option);
			BufferedImage bufferedImage = ImageIO.read(bis);
			
			// If no image was send just say that no image was sent and close the connection
			if(bufferedImage == null) {
				System.out.println("[ClientHandler] No image was passed to server");	
				return;
			}
			
			
			// Split the image into two halves to send to two different Workers
			int width = bufferedImage.getWidth();
			int height = bufferedImage.getHeight();
			
			BufferedImage topHalf = bufferedImage.getSubimage(0, 0, width, height/2);
			BufferedImage bottomHalf = bufferedImage.getSubimage(0, height/2, width, height/2);
			
			// Create our workers and send them each a half and the option
			Runnable w1 = new TWorker(topHalf, option);
			Runnable w2 = new TWorker(bottomHalf, option);
			
			// Create a new blank image that will act as our full processed image
			BufferedImage fullProcessed = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
			
			// Start our Workers so they can do their job
			w1.run();
			w2.run();
			
			// Create Graphics for our processed image and draw two two halves recieved from the Workers onto the blank image
			// The resulting image will be a stiched together processed image
			Graphics2D g = fullProcessed.createGraphics();
			g.drawImage(((TWorker) w1).getImage(), 0, 0, null);
			g.drawImage(((TWorker) w2).getImage(), 0, (height/2), null);
			g.dispose();
			
			// Convert the image from BufferedImage to ImageIcon, since the JLabel's accept ImageIcons
			ImageIcon iconProcessed = new ImageIcon(buffToIcon(fullProcessed));
			
			// Write the image over the ObjectOutputStream
			oos.writeObject(iconProcessed);
			System.out.println("[ClientHandler] Processed image has been sent to client");
			
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	
    } 
} 
