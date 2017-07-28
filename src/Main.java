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
		BufferedImage test = loadImage("./res/raw/outlet.jpg");
//		storeImage(highlightShape(findEdges(convertGrayscale(test)), test), "./res/processed/triangle_out.png");
//		storeImage(findEdges(test), "./res/processed/edge.png");
//		storeImage(findEdges(gaussianBlur(test)), "./res/processed/blur+edge.png");
		BufferedImage out = test;
		for (int i = 0; i < 3; i++) {
			out = gaussianBlur(out);
			System.out.println("iteration: " + i);
		}
		storeImage(out, "./res/processed/blur.png");
		storeImage(findEdges(out), "./res/processed/superblur.png");
		String input = "input";
		int iter = 0;
		int trainingIndex = 0;
//		NeuralNetwork net = new NeuralNetwork();
//		double[] data = new double[net.getNumInputs()];
//		
//		while(iter <= 800000){
//			net.train(trainingIndex);
//			if(iter % 100000 == 0){
//				System.out.println("ITERATION: " + iter);
//				net.displayWeights();
//				net.displayResult(false);
//			}
//			iter++;
//			trainingIndex++;
//			if(trainingIndex == net.trainingData.length){
//				trainingIndex = 0;
//			}
//		}
//		
//		while(true){
//			System.out.println();
//			for(int i = 0; i < data.length; i++){
//				System.out.print("Enter data point " + (i+1) + ": ");
//				input = scan.nextLine();
//				try{
//					data[i] = Double.parseDouble(input);
//					System.out.println(data[i]);
//				}
//				catch (Exception e){
//					System.out.println("Enter a double!");
//					i--;
//				}
//			}
//			net.run(data);
//		}	
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
	
	private BufferedImage gaussianBlur(BufferedImage img){
		double sigma = 1;
		int radius = 1;
		int width = img.getWidth();
		int height = img.getHeight();
		BufferedImage convertedImage = new BufferedImage(width, height, img.getType()); 
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < radius; x++) {
				convertedImage.setRGB(x, y, img.getRGB(x, y));
			}
			for (int x = width - radius; x < width; x++) {
				convertedImage.setRGB(x, y, img.getRGB(x, y));
			}
		}
		for (int x = radius; x < width - radius; x++) {
			for (int y = 0; y < radius; y++) {
				convertedImage.setRGB(x, y, img.getRGB(x, y));
			}
			for (int y = height - radius; y < height; y++) {
				convertedImage.setRGB(x, y, img.getRGB(x, y));
			}
		}
		for(int xpos = radius; xpos < width - radius; xpos++){
			for(int ypos = radius; ypos < height - radius; ypos++){
				double red = 0;
				double blue = 0;
				double green = 0;
				double[][] weights = new double[2*radius + 1][2* radius + 1];
				double sumWeights = 0;
				Color[][] colors = new Color[2*radius + 1][2* radius + 1];
				//find color and weight for the 3 * 3 box surrounding target pixel
				for(int i = xpos - radius; i <= xpos + radius; i++){
					for(int j = ypos - radius; j <= ypos + radius; j++){
						double weight = gaussian(i - xpos, j - ypos, sigma);
						weights[i - xpos + radius][j - ypos + radius] = weight;
						sumWeights += weight;
						colors[i - xpos + radius][j - ypos + radius] = new Color(img.getRGB(i, j));
					}
				}
				//set sum of all weights to 1
				for (int i = 0; i < weights.length; i++) {
					for (int j = 0; j < weights[0].length; j++) {
						weights[i][j]  /= sumWeights;
					}
				}
				//get gaussian blur value of center point
				for (int i = 0; i < weights.length; i++) {
					for (int j = 0; j < weights[0].length; j++) {
						red +=  weights[i][j] * colors[i][j].getRed();
						green +=  weights[i][j] * colors[i][j].getGreen();
						blue +=  weights[i][j] * colors[i][j].getBlue();
					}
				}
				Color newColor = new Color((int) red, (int) green, (int) blue);
				convertedImage.setRGB(xpos, ypos, newColor.getRGB());
			}
		}
		return convertedImage;
	}
	
	private double gaussian(double x, double y, double sigma){
		return 1/(2*Math.PI*Math.pow(sigma, 2))*Math.exp(-(Math.pow(x, 2) + Math.pow(y, 2))/ 2*Math.pow(sigma, 2));
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
