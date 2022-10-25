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

	
	public Image getProcessedHalf(TWorker w) {
		return w.getImage();
	}
	
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
	    	char option = (char) bis.read();
			System.out.println("Option recieved by the server: " + option);
			BufferedImage bufferedImage = ImageIO.read(bis);
			
			int width = bufferedImage.getWidth();
			int height = bufferedImage.getHeight();
			
			BufferedImage topHalf = bufferedImage.getSubimage(0, 0, width, height/2);
			BufferedImage bottomHalf = bufferedImage.getSubimage(0, height/2, width, height/2);
			
			Runnable w1 = new TWorker(topHalf, option);
			Runnable w2 = new TWorker(bottomHalf, option);
			
			BufferedImage fullProcessed = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
			
			w1.run();
			w2.run();
			
			Graphics2D g = fullProcessed.createGraphics();
			g.drawImage(((TWorker) w1).getImage(), 0, 0, null);
			g.drawImage(((TWorker) w2).getImage(), 0, (height/2), null);
			g.dispose();
			
			ImageIcon iconProcessed = new ImageIcon(buffToIcon(fullProcessed));
			
			
			oos.writeObject(iconProcessed);
			System.out.println("Processed image has been sent to client");
			
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	
    } 
} 
