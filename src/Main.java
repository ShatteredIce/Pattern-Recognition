//i changed it!!
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class Main {
	
	Random random = new Random();
	
	final int colorThreshold = 5;
	final int neighborMinThreshold = 6;
	final int neighborMaxThreshold = 25;
	Scanner scan = new Scanner(System.in);
	
	public Main(){
//		BufferedImage test = loadImage("./res/raw/triangle3.png");
//		storeImage(highlightShape(findEdges(convertGrayscale(test)), test), "./res/processed/triangle_out.png");
		//storeImage(findEdges(convertGrayscale(test)), "./res/processed/intoutput.png");
		String input = "input";
		int iter = 0;
		int trainingIndex = 0;
		double[] data = new double[2];
		
		NeuralNetwork net = new NeuralNetwork();
		while(iter <= 1000000){
			net.train(trainingIndex);
			if(iter % 100000 == 0){
				System.out.println("ITERATION: " + iter);
				net.displayWeights();
				net.displayResult(false);
			}
			iter++;
			trainingIndex++;
			if(trainingIndex == net.trainingData.length){
				trainingIndex = 0;
			}
		}
		
		while(true){
			System.out.println();
			for(int i = 0; i < data.length; i++){
				System.out.print("Enter data point " + (i+1) + ": ");
				input = scan.nextLine();
				try{
					data[i] = Double.parseDouble(input);
					System.out.println(data[i]);
				}
				catch (Exception e){
					System.out.println("Enter a double!");
					i--;
				}
			}
			net.run(data);
		}	
	}

	public static void main(String[] args) {
		new Main();
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
	private void storeImage(BufferedImage image, String path) {
		File out = new File(path);
		try {
			ImageIO.write(image, "png", out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private BufferedImage convertGrayscale(BufferedImage test){
		//pure image is the one that is greyscaled 
		int picWidth = test.getWidth();
    	int picHeight = test.getHeight();
    	BufferedImage pureImage = new BufferedImage(picWidth, picHeight, test.getType()); ; //i think that this is a problem in the way the pointers work
    	for (int counterX = 0; counterX < (int)(picWidth ) ; counterX += 1){
    		for (int counterY = 0; counterY < (int)(picHeight ) ; counterY += 1){
    			int originalColor;
        		originalColor = test.getRGB(counterX, counterY);
        		Color myColor = new Color(originalColor);
        		int Red = myColor.getRed();
        		int Blue = myColor.getBlue();
        		int Green = myColor.getGreen();
        		
        		Color myNewColor = null;
        		int Grey = (Red + Blue + Green) / 3;
            	myNewColor = new Color (Grey, Grey, Grey);
            	int newRGB = myNewColor.getRGB();
        		pureImage.setRGB(counterX, counterY, newRGB);
    		}
    	}
    	return pureImage;
	}
	
	private BufferedImage blurImage(BufferedImage img){
		int width = img.getWidth();
		int height = img.getHeight();
		BufferedImage convertedImage = new BufferedImage(width, height, img.getType()); 
		for(int xpos = 1; xpos < width - 1; xpos++){
			for(int ypos = 1; ypos < height - 1; ypos++){
				double red = 0;
				double blue = 0;
				double green = 0;
				for(int i = xpos - 1; i <= xpos + 1; i++){
					for(int j = ypos - 1; j <= ypos + 1; j++){
						Color currentColor = new Color(img.getRGB(i, j));
						red += (1d/9d) * currentColor.getRed();
						blue += (1d/9d) * currentColor.getBlue();
						green += (1d/9d) * currentColor.getGreen();
						
					}
				}
				Color newColor = new Color((int) red, (int) green, (int) blue);
				convertedImage.setRGB(xpos, ypos, newColor.getRGB());
				
			}
		}
		return convertedImage;
	}
	
	private BufferedImage findEdges(BufferedImage img){
		int width = img.getWidth();
		int height = img.getHeight();
		BufferedImage convertedImage = new BufferedImage(width, height, img.getType()); 
		for(int xpos = 2; xpos < width - 2; xpos++){
			for(int ypos = 2; ypos < height - 2; ypos++){
				int numSimilar = 0;
				for(int i = xpos - 2; i <= xpos + 2; i++){
					for(int j = ypos - 2; j <= ypos + 2; j++){
						Color pixel1 = new Color(img.getRGB(xpos, ypos));
						Color pixel2 = new Color(img.getRGB(i, j));
						if(checkSimilarity(pixel1, pixel2)){
							numSimilar++;
						}
					}
				}
				if(numSimilar > neighborMinThreshold && numSimilar < neighborMaxThreshold){
					convertedImage.setRGB(xpos, ypos, Color.RED.getRGB());
				}
				else{
					convertedImage.setRGB(xpos, ypos, Color.WHITE.getRGB());
				}
				
			}
		}
		return convertedImage;
	}
	
	private boolean checkSimilarity(Color pixel1, Color pixel2){
		int red1 = pixel1.getRed();
		int red2 = pixel2.getRed();
		if(Math.abs(red1 - red2) > colorThreshold){
			return true;
		}
		else{
			return false;
		}
	}
	
	private BufferedImage highlightShape(BufferedImage blackLines, BufferedImage real){
		BufferedImage highlight = real;
		int picWidth = highlight.getWidth();
		int picHeight = highlight.getHeight();
		Color red = Color.RED;
		Color trace = Color.CYAN;
		for (int counterX = 0; counterX < (picWidth) ; counterX += 1){
    		for (int counterY = 0; counterY < (picHeight) ; counterY += 1){
    			int originalColor;
        		originalColor = blackLines.getRGB(counterX, counterY);
        		Color myColor = new Color(originalColor);
        		if (myColor.equals(red)){
        			highlight.setRGB(counterX, counterY, trace.getRGB());
        		}
    		}
		}
		return highlight;
		
	}
		
}
