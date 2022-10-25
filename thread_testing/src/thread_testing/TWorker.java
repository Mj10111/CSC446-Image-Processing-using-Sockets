package thread_testing;


import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

class TWorker implements Runnable 
{ 
     Image imageHalf = null;
     Image processedHalf = null;
     char option;
    
    public static Image invertImage(Image image) {
		for (int x = 0; x < image.getWidth(null); x++) {
			for(int y = 0; y < image.getHeight(null); y++) {
				int rgba = ((BufferedImage) image).getRGB(x, y);
				Color col = new Color (rgba, true);
				col = new Color(255 - col.getRed(),
								255 - col.getGreen(),
								255 - col.getBlue());
				((BufferedImage) image).setRGB(x, y, col.getRGB());
				
			}
		}
		
		return image;
	}
	
	public static Image grayImage(Image image) {
		for(int x = 0; x < image.getWidth(null); x++) {
			for(int y = 0; y < image.getHeight(null); y++) {
				Color c = new Color(((BufferedImage) image).getRGB(x, y));
				int red = (int) (c.getRed() * 0.299);
				int green = (int) (c.getGreen() * 0.587);
                int blue = (int) (c.getBlue() * 0.114);
                Color newColor = new Color(
                        red + green + blue,
                        red + green + blue,
                        red + green + blue);
                ((BufferedImage) image).setRGB(x, y, newColor.getRGB());
			}
		}
		return image;
	}
	
	public static Image satUp(Image image) {
		for(int x = 0; x < image.getWidth(null); x++) {
			for(int y = 0; y < image.getHeight(null); y++) {
			 Color c = new Color(((BufferedImage) image).getRGB(x, y));
			 int red = (int) (c.getRed());
			 int green = (int) (c.getGreen());
			 int blue = (int) (c.getBlue());
			 
			 Color newColor;
			 
			 if(red <= green && red <= blue) {
				 newColor = new Color(red - red/2, 
						 			  green - red/2, 
						 			  blue - red/2);
				 ((BufferedImage) image).setRGB(x, y, newColor.getRGB());
				 
			 } else if (green <= red && green <= blue) {
				 newColor = new Color(red - green/2, 
			 			  			  green - green/2, 
			 			              blue - green/2);
				 ((BufferedImage) image).setRGB(x, y, newColor.getRGB());
			 } else {
				 newColor = new Color(red - blue/2, 
			 			              green - blue/2, 
			 			              blue - blue/2);
				 ((BufferedImage) image).setRGB(x, y, newColor.getRGB());
			 }
			}
		}
		return image;
	}
	
	public static Image satDown(Image image) {
		for(int x = 0; x < image.getWidth(null); x++) {
			for(int y = 0; y < image.getHeight(null); y++) {
			 Color c = new Color(((BufferedImage) image).getRGB(x, y));
			 int red = (int) (c.getRed());
			 int green = (int) (c.getGreen());
			 int blue = (int) (c.getBlue());
			 
			 int avg = (red+green+blue)/3;
			 
			 if(red < avg) {
				 red = red + (avg-red)/2;
			 } else if (red > avg) {
				 red = red -(red-avg)/2;
			 }
			 if(green < avg) {
				 green = green + (avg-green)/2;
			 } else if (green > avg) {
				 green = green -(green-avg)/2;
			 }
			 if(blue < avg) {
				 blue = blue + (avg-blue)/2;
			 } else if (blue > avg) {
				 blue = blue -(blue-avg)/2;
			 }
			 
			 Color newColor = new Color(red, green, blue);
			 ((BufferedImage) image).setRGB(x, y, newColor.getRGB());
			}
		}
		return image;
	}
	
	public static Image sharpen(Image image) {
		
		final float[] SHARPEN3x3 = {0.f, -1.f, 0.f,
									-1.f, 5.0f, -1.f,
									0.f, -1.f, 0.f};
	
		Kernel kernel = new Kernel(3, 3, SHARPEN3x3);
		ConvolveOp cop = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
		
		image = cop.filter((BufferedImage) image, null);
		
		return image;
	}
	
	public static Image blur(Image image) {
		int radius = 1;
		int size = radius * 4 + 3;
		float weight = 1.0f / (size * size);
		float [] data = new float[size * size];
		
		for(int i = 0; i < data.length; i++) {
			data[i] = weight;
		}
		
		Kernel kernel = new Kernel(size, size, data);
		BufferedImageOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
		
		BufferedImage blurred = op.filter((BufferedImage) image, null);
		
		return blurred;
	}
	
	public Image getImage() {
		return this.imageHalf;
	}
	
	public TWorker(Image image, char option) {
		this.imageHalf  = image;
		this.option = option;
	}
	
	public void run() {
		
		switch (option) {
		case '1': System.out.println("Image will be set to gray-scale");
				  imageHalf = grayImage(imageHalf);
				  break;
		case '2': System.out.println("Image will have its colors inverted");
				  imageHalf = invertImage(imageHalf);
				  break;
		case '3': System.out.println("Image will be sharpened");
				  imageHalf = sharpen(imageHalf);
				  break;
		case '4': System.out.println("Image will made blurry");
				  imageHalf = blur(imageHalf);
				  break;
		case '5': System.out.println("Image will have its saturation increased");
				  imageHalf = satUp(imageHalf);
				  break;
		case '6': System.out.println("Image will have its saturation decreased");
				  imageHalf = satDown(imageHalf);
				  break;
		default:  System.out.println("You messed up.");
		
		System.out.println("Print statement at the end of TWorker");
	}
		
		
	}
	

}
