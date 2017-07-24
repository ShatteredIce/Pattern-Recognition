//i changed it!!
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
public class Main {
	
	public Main(){
		BufferedImage test = loadImage("./res/test_triangle.jpg:");
		
	}

	public static void main(String[] args) {

	}
	
	private BufferedImage loadImage(String path) {
		File in = new File(path);
		BufferedImage im;
		try {
			im = ImageIO.read(in);
			return im;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private convertGrayscale(){
		
	}

}
