import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
public class Main {

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

}
