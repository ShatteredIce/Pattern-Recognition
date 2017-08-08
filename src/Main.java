import java.util.LinkedList;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class Main implements ActionListener {
	
	final Random random = new Random();
	final int colorThreshold = 400;
	final int blockThresh = 22;
	final int differentiator = 2;
	final int spaceAroundCrop = 25;
	final int spArCr = spaceAroundCrop;
	int sizeThreshold = 500;
	private ArrayList<ArrayList<Integer>> slimeTrail = new ArrayList<ArrayList<Integer>>();
	private ArrayList<int[]> tracedOutline = new ArrayList<int[]>();
	
	final Color slimeTrailColor = new Color(255, 0, 0);
	final Color endPointColor = new Color(0, 255, 0);
	ArrayList<int[]> endpoints = null;
	ArrayList<ArrayList<int[]>> allEndpoints = new ArrayList<>();
	
	//set up GUI
	JFrame frame = new JFrame("Shapes");
	//north container
	Container north = new Container();
	JLabel filenameLabel = new JLabel(" File Name: ");
	JTextField filename = new JTextField();
	//center container
	Container center = new Container();
	ImagePanel panel = new ImagePanel();
	JButton imgButton1 = new JButton("Raw");
	JButton imgButton2 = new JButton("Blur");
	JButton imgButton3 = new JButton("Outline");
	JButton imgButton4 = new JButton("Dilate+Erode");
	JButton imgButton5 = new JButton("Processed");
	//east container
	Container east = new Container();
	JButton calculate = new JButton("Calculate");
	JButton loadfile = new JButton("Load");
	JButton savefile = new JButton("Save");
	JButton generate = new JButton("Generate");
	JCheckBox autocalculate = new JCheckBox("Auto-Calc");
	JCheckBox blur = new JCheckBox("Blur");
	JLabel pointsLabel = new JLabel("Number of Points: ");
	JLabel ratioLabel = new JLabel("Width/Height Ratio: ");
	JLabel stddevLabel = new JLabel("Angle Std Dev: ");
	JLabel anglesLabel = new JLabel("Angle Average: ");
	JLabel tempLabel = new JLabel("Temp: ");
	
	ArrayList<double[]> pixelSimilarity = new ArrayList<>();
	
	
	public Main(){
		
		//Frame setup
		frame.setSize(900, 600);
		frame.setLayout(new BorderLayout());
		frame.add(north, BorderLayout.NORTH);
		frame.add(center, BorderLayout.CENTER);
		frame.add(east, BorderLayout.EAST);
		
		//set north layout
		north.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.BOTH;
		c = setGridBagConstraints(c, 0, 0, 0, 0, 0, 0);
		north.add(filenameLabel, c);
		c = setGridBagConstraints(c, 1, 0, 0, 20, 1, 0);
		north.add(filename, c);
		
		//set center layout
		center.setLayout(new GridBagLayout());
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 4;
		c = setGridBagConstraints(c, 0, 0, 0, 0, 1, 1);
		center.add(panel, c);
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 1;
		c = setGridBagConstraints(c, 0, 1, 0, 0, 0.2, 0);
		center.add(imgButton1, c);	
		imgButton1.addActionListener(this);
		c = setGridBagConstraints(c, 1, 1, 0, 0, 0.2, 0);
		center.add(imgButton2, c);
		imgButton2.addActionListener(this);
		c = setGridBagConstraints(c, 2, 1, 0, 0, 0.2, 0);
		center.add(imgButton3, c);
		imgButton3.addActionListener(this);
		c = setGridBagConstraints(c, 3, 1, 0, 0, 0.2, 0);
		center.add(imgButton4, c);
		imgButton4.addActionListener(this);
		c = setGridBagConstraints(c, 4, 1, 0, 0, 0.2, 0);
		center.add(imgButton5, c);
		imgButton5.addActionListener(this);
		
		//set east layout
		east.setLayout(new GridBagLayout());
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.NONE;
		c = setGridBagConstraints(c, 0, 0, 20, 20, 0, 0);
		east.add(loadfile, c);
		loadfile.addActionListener(this);
		c = setGridBagConstraints(c, 1, 0, 10, 20, 0, 0);
		east.add(calculate, c);
		calculate.addActionListener(this);
		c = setGridBagConstraints(c, 2, 0, 20, 20, 0, 0);
		east.add(savefile, c);
		savefile.addActionListener(this);
		c.gridwidth = 3;
		c = setGridBagConstraints(c, 1, 1, 0, 10, 0, 0);
		east.add(autocalculate, c);
		c = setGridBagConstraints(c, 0, 1, 0, 10, 0, 0);
		east.add(blur, c);
		blur.setSelected(true);
		blur.addActionListener(this);
		c = setGridBagConstraints(c, 0, 2, 0, 0, 0, 0.1);
		east.add(pointsLabel, c);
		c = setGridBagConstraints(c, 0, 3, 0, 0, 0, 0.1);
		east.add(ratioLabel, c);
		c = setGridBagConstraints(c, 0, 4, 0, 0, 0, 0.1);
		east.add(stddevLabel, c);
		c = setGridBagConstraints(c, 0, 5, 0, 0, 0, 0.1);
		east.add(anglesLabel, c);
		c = setGridBagConstraints(c, 0, 6, 0, 0, 0, 2);
		east.add(tempLabel, c);
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c = setGridBagConstraints(c, 1, 7, 20, 0, 1, 0);
		east.add(generate, c);
		generate.addActionListener(this);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		NeuralNetwork net = new NeuralNetwork();
				
		
//		String shapeName = "triangle4";
//		String type = "png";
//
//				
//		BufferedImage input = loadImage("./res/raw/" + shapeName + "." + type);
////		ArrayList<BufferedImage> tests = cropToBlock(convertGrayscale(input));
//		ArrayList<BufferedImage> tests = new ArrayList<>();
//		tests.add(input);
////		BufferedImage test = convertGrayscale(best);
//		int index = -1;
//		for(BufferedImage test : tests) {
//			index ++;
//			BufferedImage shape = highlightShape(checkEdges(findEdges((test))), test);
//		storeImage(findEdges(input), "./res/processed/" + shapeName + "_out.png");
//			System.out.println("finished highlighting shape");
//			
//			ArrayList<ArrayList<Integer>> endpoints = findEndpoints(checkEdges(findEdges(test)));
//			System.out.println("the list size is: " + endpoints.size());
//			
//			//display slime trail as red outline
//			for (int k = 0; k < slimeTrail.size(); k += 1){
//				shape.setRGB(slimeTrail.get(k).get(0), slimeTrail.get(k).get(1), slimeTrailColor.getRGB());
//			}
//			//storeImage(shape, "./res/processed/" + shapeName + "_out.png");
//			//display endpoints as green dots
//			for (int i = 0; i < endpoints.size(); i ++){
//				ArrayList temp = endpoints.get(i);
//				for (int j = -2; j < 2; j ++) {
//					for (int k = -2; k < 2; k ++) {
//						shape.setRGB((int)temp.get(0), (int)temp.get(1), endPointColor.getRGB());
//						if (i == endpoints.size() -1){
//							//shape.setRGB((int)temp.get(0), (int)temp.get(1), (new Color(255,255,255)).getRGB());
//						}
//					}
//				}
//				System.out.println("X:" + temp.get(0) + " Y: " + temp.get(1));
//			}
//			storeImage(shape, "./res/processed/" + shapeName + "_out.png");
//			ArrayList<ArrayList<ArrayList<Integer>>> myList = new ArrayList<ArrayList<ArrayList<Integer>>>();
//			myList.add(endpoints);
//			
//			Double[][] my = processShape(myList);
//			for (Double[] value: my){
//				for (Double actualValue : value){
//					System.out.println(actualValue);
//				}
//			}
//			
//		}
		
	}

	public static void main(String[] args) {
		new Main();
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		//if load or generate button is clicked, attempt to create images to be processed
		if(event.getSource().equals(loadfile) || event.getSource().equals(generate)){
			BufferedImage raw;
			//load image from /res/raw
			if(event.getSource().equals(loadfile)){
				raw = loadImage("./res/raw/" + filename.getText());
			}
			//generate image
			else{
				raw = generateImage();
			}
			//if raw image exists, create images for outline and processed image
			if(raw != null){
				slimeTrail.clear();
				if(endpoints != null){
					endpoints.clear();
				}
				allEndpoints.clear();
				BufferedImage blurred;
				if(blur.isSelected()){
					blurred = gaussianBlur(raw);
				}
				else{
					blurred = raw;
				}
				BufferedImage outline = checkEdges(findEdges(blurred));
				BufferedImage modifiedOutline = erodeImage(dilateImage(outline));
//				for (int i = 0; i < 2; i++) {	
//					modifiedOutline = dilateImage(modifiedOutline);
//				}
//				for (int i = 0; i < 2; i++) {
//					modifiedOutline = erodeImage(modifiedOutline);
//				}
				BufferedImage processed = highlightShape(modifiedOutline, raw);
				//endpoints = findEndpoints(modifiedOutline);
				ArrayList<int[]> vertices = findVertices(modifiedOutline);
				allEndpoints.add(vertices);
//				//display slime trail as outline
//				for (int k = 0; k < slimeTrail.size(); k += 1){
//					processed.setRGB(slimeTrail.get(k).get(0), slimeTrail.get(k).get(1), slimeTrailColor.getRGB());
//				}
//				//display endpoints as dots
//				if(endpoints != null){
//					for (int i = 0; i < endpoints.size(); i ++){
//						ArrayList<Integer> temp = endpoints.get(i);
//						int endpointWidth = 1;
//						for (int j = -endpointWidth; j <= endpointWidth; j++) {
//							for (int k = -endpointWidth; k <= endpointWidth; k++) {
//								outline.setRGB((int)temp.get(0)+j, (int)temp.get(1)+k, endPointColor.getRGB());
//								modifiedOutline.setRGB((int)temp.get(0)+j, (int)temp.get(1)+k, endPointColor.getRGB());
//								processed.setRGB((int)temp.get(0)+j, (int)temp.get(1)+k, endPointColor.getRGB());
//								if (i == endpoints.size() -1){
//									//shape.setRGB((int)temp.get(0), (int)temp.get(1), (new Color(255,255,255)).getRGB());
//								}
//							}
//						}
//						System.out.println("X:" + temp.get(0) + " Y: " + temp.get(1));
//					}
//				}
				for (int i = 0; i < vertices.size(); i++) {
					int endpointWidth = 1;
					for (int j = -endpointWidth; j <= endpointWidth; j++) {
						for (int k = -endpointWidth; k <= endpointWidth; k++) {
							outline.setRGB(vertices.get(i)[0]+j, vertices.get(i)[1]+k, endPointColor.getRGB());
							modifiedOutline.setRGB(vertices.get(i)[0]+j, vertices.get(i)[1]+k, endPointColor.getRGB());
							processed.setRGB(vertices.get(i)[0]+j, vertices.get(i)[1]+k, endPointColor.getRGB());
						}
					}
					System.out.println("X:" + vertices.get(i)[0] + " Y: " + vertices.get(i)[1]);
				}
				panel.setImages(raw, blurred, outline, modifiedOutline, processed);
				//if the auto-calculate checkbox is selected, calculate data
				if(autocalculate.isSelected()){
					calculateData(allEndpoints);
				}
				frame.repaint();
			}
			else{
				endpoints = null;
			}
		}
		//display processed data of shape
		else if(event.getSource().equals(calculate)){
			calculateData(allEndpoints);
		}
		//save the current displayed image
		else if(event.getSource().equals(savefile)){
			if(panel.getCurrentImage() != null){
				storeImage(panel.getCurrentImage(), "./res/processed/savedimage.png");
			}
		}
		//select the raw image to be displayed
		else if(event.getSource().equals(imgButton1)){
			panel.setImgType(0);
			frame.repaint();
		}
		//select the blurred image to be displayed
		else if(event.getSource().equals(imgButton2)){
			panel.setImgType(1);
			frame.repaint();
		}
		//select the outline image to be displayed
		else if(event.getSource().equals(imgButton3)){
			panel.setImgType(2);
			frame.repaint();
		}
		//select the modified outine image to be displayed
		else if(event.getSource().equals(imgButton4)){
			panel.setImgType(3);
			frame.repaint();
		}
		//select the processed image to be displayed
		else if(event.getSource().equals(imgButton5)){
			panel.setImgType(4);
			frame.repaint();
		}
		//if blur checkbox was changed
		else if(event.getSource().equals(blur)){
			if(blur.isSelected()){
				imgButton2.setEnabled(true);
			}
			else{
				imgButton2.setEnabled(false);
			}
		}
	}
	
	//set constraints for GUI layout
	public GridBagConstraints setGridBagConstraints(GridBagConstraints c, int x, int y, int xpad, int ypad, double xweight, double yweight){
		c.gridx = x;
		c.gridy = y;
		c.ipadx = xpad;
		c.ipady = ypad;
		c.weightx = xweight;
		c.weighty = yweight;
		return c;
	}
	
	//load an image from file path
	private BufferedImage loadImage(String path) {
		File in = new File(path);
		BufferedImage im;
		try {
			im = ImageIO.read(in);
			return im;
		}
		catch (IOException e) {
			System.out.println("File not found!");
			return null;
		}
	}
	
	//store an image to the computer
	private void storeImage(BufferedImage image, String path) {
		File out = new File(path);
		try {
			ImageIO.write(image, "png", out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//convert an RGB image to grayscale
	private BufferedImage convertGrayscale(BufferedImage test){
		//pure image is the one that is greyscaled 
		int picWidth = test.getWidth();
    	int picHeight = test.getHeight();
    	BufferedImage pureImage = new BufferedImage(picWidth, picHeight, test.getType());
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
	
	//blur image using mean blur algorithm
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
	
	//blur image using gaussian blur algorithm
	private BufferedImage gaussianBlur(BufferedImage img){
		double sigma = 1;
		int radius = 1;
		int width = img.getWidth();
		int height = img.getHeight();

		BufferedImage convertedImage = new BufferedImage(width, height, img.getType()); 
		//copy edges from original image over to blurred image
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
		//blur inner pixels
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

	//two-dimensional gaussian function
	private double gaussian(double x, double y, double sigma){
		return 1/(2*Math.PI*Math.pow(sigma, 2))*Math.exp(-(Math.pow(x, 2) + Math.pow(y, 2))/ 2*Math.pow(sigma, 2));
	}
	
	//finds the edges of the shapes in an image
	private BufferedImage findEdges(BufferedImage img){
		pixelSimilarity.clear();
		int width = img.getWidth();
		int height = img.getHeight();
		BufferedImage convertedImage = new BufferedImage(width, height, img.getType()); 
		//calculates color difference from neighbors for each inner pixel
		for(int xpos = 2; xpos < width - 2; xpos++){
			for(int ypos = 2; ypos < height - 2; ypos++){
				int totalDifference = 0;
				for(int i = xpos - 2; i <= xpos + 2; i++){
					for(int j = ypos - 2; j <= ypos + 2; j++){
						Color pixel1 = new Color(img.getRGB(xpos, ypos));
						Color pixel2 = new Color(img.getRGB(i, j));
						totalDifference += checkSimilarity(pixel1, pixel2);
						}
					}
				double[] currentPixel = {xpos, ypos, totalDifference};
				pixelSimilarity.add(currentPixel);
				//mark the pixel if color difference is greater than specified threshold
				if(totalDifference > colorThreshold){
					convertedImage.setRGB(xpos, ypos, Color.RED.getRGB());
				}
				else{
					convertedImage.setRGB(xpos, ypos, Color.WHITE.getRGB());
				}
				
			}
		}
		return convertedImage;
	}
	
//	private ArrayList<int[]> findVerticies(){
//		ArrayList<int[]> verticies = new ArrayList<int[]>();
//		double maxDifference = 0;
//		double mean = 0;
//		double size = 0;
//		double stddev = 0;
//		double precision = 0;
//		for (int i = 0; i < pixelSimilarity.size(); i++) {
//			if(pixelSimilarity.get(i)[2] != 0){
//				maxDifference = Math.max(maxDifference, pixelSimilarity.get(i)[2]);
//				mean += pixelSimilarity.get(i)[2];
//				size++;
//			}
//		}
//		mean = mean/size;
//		//calculate standard deviation
//		for (int i = 0; i < pixelSimilarity.size(); i++) {
//			if(pixelSimilarity.get(i)[2] != 0){
//				stddev += Math.pow((pixelSimilarity.get(i)[2] - mean), 2);
//			}
//		}
//		stddev /= size;
//		stddev = Math.sqrt(stddev);
//		precision = maxDifference / 12;
//		for (int i = 0; i < pixelSimilarity.size(); i++) {
//			if (pixelSimilarity.get(i)[2] > (maxDifference - stddev/4)) {
//				int[] currentPixel = {(int) pixelSimilarity.get(i)[0], (int) pixelSimilarity.get(i)[1]};
//				verticies.add(currentPixel);
//			}
//		}
//		return verticies;
//	}
	
	private ArrayList<int[]> findVertices(BufferedImage outline){
		tracedOutline.clear();
		ArrayList<int[]> vertices = new ArrayList<int[]>();
		int[] startPoint = {-1, -1};
		int[] nextPoint = {-1, -1, -1};
		//finds the top-leftmost red pixel and set it to the starting point
		for (int y = 0; y < outline.getHeight(); y++) {
			if(startPoint[0] != -1){
				break;
			}
			for (int x = 0; x < outline.getWidth(); x++) {
				if(outline.getRGB(x, y) == Color.RED.getRGB()){
					startPoint[0] = x;
					startPoint[1] = y;
					break;
				}
			}
		}
		//no red pixel found
		if(startPoint[0] == -1){
			return vertices;
		}
		tracedOutline.add(startPoint);
		nextPoint = getNextPoint(outline, startPoint[0], startPoint[1], 2);
		//while there is a next point and the next point has not returned to the starting point, draw a single pixel outline around the initial outline
		while(nextPoint[0] != -1 && !((nextPoint[0] == startPoint[0]) && (nextPoint[1] == startPoint[1]))){
			tracedOutline.add(nextPoint);
			nextPoint = getNextPoint(outline, nextPoint[0], nextPoint[1], nextPoint[2]);
		}
		int pixelsGrouped = Math.max(10, Math.round(tracedOutline.size() / 30f));
		System.out.println(pixelsGrouped);
		ArrayList<int[]> currentCorner = new ArrayList<int[]>();
		boolean groupContainsVertex = false;
		//wrap around starting pixels
		for (int i = 0; i < pixelsGrouped * 2; i++) {
			tracedOutline.add(nextPoint);
			nextPoint = getNextPoint(outline, nextPoint[0], nextPoint[1], nextPoint[2]);
		}
		//for however many pixels grouped together, add all the different connecting directions to an arraylist
		for (int i = pixelsGrouped; i < tracedOutline.size() - pixelsGrouped; i++) {
			ArrayList<Integer> distinctDirections = new ArrayList<Integer>();
			for (int j = i + 1; j < i + pixelsGrouped; j++) {
				if(!distinctDirections.contains(tracedOutline.get(j)[2])){
					distinctDirections.add(tracedOutline.get(j)[2]);
				}
			}
			if(distinctDirections.size() > 2){
				groupContainsVertex = true;
				int[] currentPixel = {tracedOutline.get(i+(pixelsGrouped/2))[0], tracedOutline.get(i+(pixelsGrouped/2))[1]};
				currentCorner.add(currentPixel);
			}
			else{
				//when transitioning to a group that does not contain a vertex from a group that did contain a vertex
				if(groupContainsVertex == true){
					int averageX = 0;
					int averageY = 0;
//					for (int j = 0; j < currentCorner.size(); j++) {
//						averageX += currentCorner.get(j)[0];
//						averageY += currentCorner.get(j)[1];
//					}
//					averageX = (int) Math.round(averageX / (double) currentCorner.size());
//					averageY = (int) Math.round(averageY / (double) currentCorner.size());
					averageX = currentCorner.get(currentCorner.size()/2)[0];
					averageY = currentCorner.get(currentCorner.size()/2)[1];
					int[] currentPixel = {averageX, averageY};
					vertices.add(currentPixel);
				}
				groupContainsVertex = false;
				currentCorner.clear();
			}
		}
		int initialChecks = 0;
		int numInitialChecks = 10;
		double currentAngle = 0;
		double newAngle = 0;
//		//put starting pixels at the end of the traced outline
//		for (int i = 0; i < numInitialChecks; i++) {
//			int[] currentPixel = {tracedOutline.get(i)[0], tracedOutline.get(i)[1]};
//			tracedOutline.add(currentPixel);
//		}
//		int lineStartIndex = 0;
//		for (int i = 0; i < tracedOutline.size()-1; i++) {
//			//set initial angle to the angle between the first two pixels checked
//			if(initialChecks == 0){
//				currentAngle = pixelsToAngle(tracedOutline.get(lineStartIndex)[0], tracedOutline.get(lineStartIndex)[1], tracedOutline.get(i+1)[0], tracedOutline.get(i+1)[1]);
//			}
//			//allow the angle of the line to stablize
//			else if(initialChecks < numInitialChecks){
//				newAngle = pixelsToAngle(tracedOutline.get(lineStartIndex)[0], tracedOutline.get(lineStartIndex)[1], tracedOutline.get(i+1)[0], tracedOutline.get(i+1)[1]);
//				currentAngle = (currentAngle * 0.5) + (newAngle * 0.5);
//			}
//			else{
//				newAngle = pixelsToAngle(tracedOutline.get(lineStartIndex)[0], tracedOutline.get(lineStartIndex)[1], tracedOutline.get(i+1)[0], tracedOutline.get(i+1)[1]);
//
//				//check if angle deviates from current angle
//				if(getSmallestBearing(currentAngle, newAngle) > 20){
//					int[] currentPixel = {tracedOutline.get(i)[0], tracedOutline.get(i)[1]};
//					vertices.add(currentPixel);
//					initialChecks = 0;
//					lineStartIndex = i;
//				}
//			}
//			currentAngle = normalizeAngle(currentAngle);
//			initialChecks++;
//		}
		return vertices;
	}
	
	private int checkPattern(ArrayList<Integer> set){
		double ratio = -1;
		int differentRatios = 0;
		int firstChange = 0;
		for (int i = set.size() % 2 == 0 ? 0 : 1; i < set.size() - 1; i+=2) {
			if(ratio == -1){
				ratio = ((double) set.get(i) / (double) set.get(i+1));
			}
			else{
				if(Math.abs(ratio - ((double) set.get(i) / (double) set.get(i+1))) > 0){
					if(firstChange == 0){
						firstChange = i;
					}
					differentRatios += Math.abs(ratio - ((double) set.get(i) / (double) set.get(i+1)));
				}
			}
		}
		if(differentRatios > 5){
			return firstChange;
		}
		return -1;
	}
	
	private double pixelsToAngle(int x1, int y1, int x2, int y2){
		int deltax = x2 - x1;
		int deltay = y1 - y2;
		//pixel above
		if(deltax == 0 && deltay > 0){
			return 0;
		}
		//pixel below
		else if(deltax == 0 && deltay < 0){
			return 180;
		}
		//pixel to the right
		else if(deltay == 0 && deltax > 0){
			return 90;
		}
		//pixel to the left
		else if(deltay == 0 && deltax < 0){
			return 270;
		}
		//quadrant 1
		else if(deltax > 0 && deltay > 0){
			return Math.atan(deltax/deltay);
		}
		//quadrant 2
		else if(deltax < 0 && deltay > 0){
			return 360 + Math.atan(deltax/deltay);
		}
		//quadrant 3
		else if(deltax < 0 && deltay < 0){
			return 180 + Math.atan(deltax/deltay);
		}
		//quadrant 4
		else if(deltax > 0 && deltay < 0){
			return 180 + Math.atan(deltax/deltay);
		}
		else{
			System.out.println("fatal error");
			return -1;
		}
		
	}
	
	private double normalizeAngle(double angle){
		while(angle > 360){
			angle -= 360;
		}
		while(angle < 0){
			angle += 360;
		}
		return angle;
	}
	
	private double getSmallestBearing(double angle1, double angle2){
		double leftBearing;
		double rightBearing;
		if(angle2 >= angle1){
			leftBearing = angle2 - angle1;
			rightBearing = 360 - angle2 + angle1;
		}
		else{
			leftBearing = angle1 - angle2;
			rightBearing = 360 - angle1 + angle2;
		}
		return Math.min(leftBearing, rightBearing);
	}
	
	private int[] getNextPoint(BufferedImage outline, int x, int y, int direction){
		//direction turns clockwise, starts 90 degrees to the left of original direction
		direction = direction - 2;
		int numChecks = 0;
		int[] nextPoint = {-1, -1, -1};
		while(numChecks < 8){
			if(direction % 8 == 2){
				//right
				if(x+1 < outline.getWidth() && outline.getRGB(x+1, y) == Color.RED.getRGB() && bordersWhite(outline, x+1, y)){
					nextPoint[0] = x+1;
					nextPoint[1] = y;
					nextPoint[2] = 2;
					break;
				}
			}
			else if(direction % 8 == 3){
				//bottom right
				if(x+1 < outline.getWidth() && y+1 < outline.getHeight() && outline.getRGB(x+1, y+1) == Color.RED.getRGB() && bordersWhite(outline, x+1, y+1)){
					nextPoint[0] = x+1;
					nextPoint[1] = y+1;
					nextPoint[2] = 3;
					break;
				}
			}
			else if(direction % 8 == 4){
				//bottom
				if(y+1 < outline.getHeight() && outline.getRGB(x, y+1) == Color.RED.getRGB() && bordersWhite(outline, x, y+1)){
					nextPoint[0] = x;
					nextPoint[1] = y+1;
					nextPoint[2] = 4;
					break;
				}
			}
			else if(direction % 8 == 5){
				//bottom left
				if(x-1 >= 0 && y+1 < outline.getHeight() && outline.getRGB(x-1, y+1) == Color.RED.getRGB() && bordersWhite(outline, x-1, y+1)){
					nextPoint[0] = x-1;
					nextPoint[1] = y+1;
					nextPoint[2] = 5;
					break;
				}
			}
			else if(direction % 8 == 6){
				//left
				if(x-1 >= 0 && outline.getRGB(x-1, y) == Color.RED.getRGB() && bordersWhite(outline, x-1, y)){
					nextPoint[0] = x-1;
					nextPoint[1] = y;
					nextPoint[2] = 6;
					break;
				}
			}
			else if(direction % 8 == 7){
				//top left
				if(x-1 >= 0 && y-1 >= 0 && outline.getRGB(x-1, y-1) == Color.RED.getRGB() && bordersWhite(outline, x-1, y-1)){
					nextPoint[0] = x-1;
					nextPoint[1] = y-1;
					nextPoint[2] = 7;
					break;
				}
			}
			else if(direction % 8 == 0){
				//top
				if(y-1 >= 0 && outline.getRGB(x, y-1) == Color.RED.getRGB() && bordersWhite(outline, x, y-1)){
					nextPoint[0] = x;
					nextPoint[1] = y-1;
					nextPoint[2] = 8;
					break;
				}
			}
			else if(direction % 8 == 1){
				//top right
				if(x+1 < outline.getWidth() && y-1 >= 0 && outline.getRGB(x+1, y-1) == Color.RED.getRGB() && bordersWhite(outline, x+1, y-1)){
					nextPoint[0] = x+1;
					nextPoint[1] = y-1;
					nextPoint[2] = 9;
					break;
				}
			}
			numChecks++;
			direction++;
		}
		return nextPoint;
	}
	
	private boolean bordersWhite(BufferedImage outline, int x, int y){
		if(x+1 < outline.getWidth() && outline.getRGB(x+1, y) == Color.WHITE.getRGB()){
			return true;
		}
		else if(x-1 >= 0 && outline.getRGB(x-1, y) == Color.WHITE.getRGB()){
			return true;
		}
		else if(y+1 < outline.getHeight() && outline.getRGB(x, y+1) == Color.WHITE.getRGB()){
			return true;
		}
		else if(y-1 >= 0 && outline.getRGB(x, y-1) == Color.WHITE.getRGB()){
			return true;
		}
		else{
			return false;
		}
	}
	
	//dilate an image, used to dilate the red outline of a shape
	private BufferedImage dilateImage(BufferedImage img){
		BufferedImage dilated = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
		Color edgeColor = Color.RED;
		Color backgroundColor = Color.WHITE;
		//set dilated image to original image 
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				dilated.setRGB(x, y, img.getRGB(x, y));
			}
		}
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				//if pixel is colored in original image, set adjacent pixels to colored in dilated image
				if (img.getRGB(x, y) == edgeColor.getRGB()) {
					if(x < img.getWidth() - 1){
						dilated.setRGB(x+1, y, edgeColor.getRGB()); //right
						if(y < img.getHeight() - 1){
							dilated.setRGB(x+1, y+1, edgeColor.getRGB()); //top right
						}
						if(y > 0){
							dilated.setRGB(x+1, y-1, edgeColor.getRGB()); //bottom right
						}
					}
					if(x > 0){
						dilated.setRGB(x-1, y, edgeColor.getRGB());
						if(y < img.getHeight() - 1){ //left
							dilated.setRGB(x-1, y+1, edgeColor.getRGB()); //top left
						}
						if(y > 0){
							dilated.setRGB(x-1, y-1, edgeColor.getRGB()); //bottom left
						}
					}
					if(y < img.getHeight() - 1){
						dilated.setRGB(x, y+1, edgeColor.getRGB()); //top
					}
					if(y > 0){
						dilated.setRGB(x, y-1, edgeColor.getRGB()); //bottom
					}
				}
			}
		}
		return dilated;
	}
	
	//erode an image, used to erode the red outline of a shape
	private BufferedImage erodeImage(BufferedImage img){
		BufferedImage eroded = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
		Color edgeColor = Color.RED;
		Color backgroundColor = Color.WHITE;
		//set eroded image to original image 
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				eroded.setRGB(x, y, img.getRGB(x, y));
			}
		}
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				//if pixel is uncolored in original image, set adjacent pixels to uncolored in eroded image
				if (img.getRGB(x, y) == backgroundColor.getRGB()) {
					if(x < img.getWidth() - 1){
						eroded.setRGB(x+1, y, backgroundColor.getRGB());
						if(y < img.getHeight() - 1){
							eroded.setRGB(x+1, y+1, backgroundColor.getRGB()); //top right
						}
						if(y > 0){
							eroded.setRGB(x+1, y-1, backgroundColor.getRGB()); //bottom right
						}
					}
					if(x > 0){
						eroded.setRGB(x-1, y, backgroundColor.getRGB());
						if(y < img.getHeight() - 1){
							eroded.setRGB(x-1, y+1, backgroundColor.getRGB()); //top left
						}
						if(y > 0){
							eroded.setRGB(x-1, y-1, backgroundColor.getRGB()); //bottom left
						}
					}
					if(y < img.getHeight() - 1){
						eroded.setRGB(x, y+1, backgroundColor.getRGB()); //top
					}
					if(y > 0){
						eroded.setRGB(x, y-1, backgroundColor.getRGB()); //bottom
					}
					
				}
			}
		}
		return eroded;
	}
	
	private BufferedImage highlightShape(BufferedImage blackLines, BufferedImage real){
		BufferedImage highlight = new BufferedImage(real.getWidth(), real.getHeight(), real.getType());
		int picWidth = highlight.getWidth();
		int picHeight = highlight.getHeight();
		
		for (int i = 0; i < picWidth; i +=1){
			for (int k = 0; k < picHeight; k += 1){
				highlight.setRGB(i, k, real.getRGB(i, k));
			}
		}
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
	
	//compares the RGB values of two pixels
	private double checkSimilarity(Color pixel1, Color pixel2){
		double red1 = pixel1.getRed();
		double green1 = pixel1.getGreen();
		double blue1 = pixel1.getBlue();
		double red2 = pixel2.getRed();
		double green2 = pixel2.getGreen();
		double blue2 = pixel2.getBlue();
		double difference = Math.sqrt(Math.pow(red1 - red2, 2) + Math.pow(green1 - green2, 2) + Math.pow(blue1 - blue2, 2));
		return difference;
	}
	
	//generate an image containing a square or triangle
	public BufferedImage generateImage(){
		int imgsize = 300;
		Polygon p = new Polygon();
		//possible shape and background colors
		ArrayList<Color> colors = new ArrayList<>();
		colors.add(Color.RED);
		colors.add(Color.BLUE);
		colors.add(Color.GREEN);
		colors.add(Color.LIGHT_GRAY);
		colors.add(Color.DARK_GRAY);
		colors.add(Color.CYAN);
		colors.add(Color.ORANGE);
		colors.add(Color.YELLOW);
		colors.add(Color.PINK);
		colors.add(Color.MAGENTA);
		colors.add(Color.WHITE);
		colors.add(Color.BLACK);
		int shapeColorIndex = random.nextInt(colors.size());
		int backgroundColorIndex = random.nextInt(colors.size());
		while(backgroundColorIndex == shapeColorIndex){ backgroundColorIndex = random.nextInt(colors.size()); }
		int shapeType = random.nextInt(3);
		//generate triangle
		if(shapeType < 2){
			int numPoints = 3;
			for (int i = 0; i < numPoints; i++) {
				p.addPoint(random.nextInt(imgsize + 1), random.nextInt(imgsize + 1));
			}
		}
		//generate square
		else{
			int topX = random.nextInt(imgsize + 1);
			int topY = random.nextInt(imgsize + 1);
			int botX = random.nextInt(imgsize + 1);
			int botY = random.nextInt(imgsize + 1);
			p.addPoint(topX, topY);
			p.addPoint(topX, botY);
			p.addPoint(botX, botY);
			p.addPoint(botX, topY);
		}
		//set color of pixels in new image
		BufferedImage generatedImage = new BufferedImage(imgsize, imgsize, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < generatedImage.getWidth(); x++) {
			for (int y = 0; y < generatedImage.getHeight(); y++) {
				if(p.contains(new Point(x, y))){
					generatedImage.setRGB(x, y, colors.get(shapeColorIndex).getRGB());
				}
				else{
					generatedImage.setRGB(x, y, colors.get(backgroundColorIndex).getRGB());
				}
			}
		}
		
		return generatedImage;
	}
	
	
	//used to generate random shape data and display it to the console
	public void generateShapeData(){
		int numPoints = 3;
		int upperBound = 200;
		//generate random points
		ArrayList<int[]> shapePoints = new ArrayList<>();
		for (int i = 0; i < numPoints; i++) {
			int[] currentPoint = new int[2];
			currentPoint[0] = (random.nextInt(upperBound + 1));
			currentPoint[1] = (random.nextInt(upperBound + 1));
			shapePoints.add(currentPoint);
			System.out.println("X: " + shapePoints.get(i)[0] + " Y: " + shapePoints.get(i)[1]);
		}
		ArrayList<ArrayList<int[]>> myList = new ArrayList<ArrayList<int[]>>();
		myList.add(shapePoints);
		//display values: number of points, ratio of width/height, std dev of angles, average of angles
		Double[] result = processShape(myList)[0];
		for (int i = 0; i < result.length; i++) {
			System.out.println(result[i]);
		}
		
	}
	
	//Nested Arraylists: Shape >> Endpoints >> X,Y
	private Double[][] processShape(ArrayList<ArrayList<int[]>> inArray) {
		int len = inArray.size();
		int i = 0;
		Double[][] outArray = new Double[len][4];
		/// Identify: ratio of max-width:max-height, number of points, std from
		/// average angle
		for (ArrayList<int[]> shape : inArray) {
			double minX = (double) shape.get(0)[0], minY = (double) shape.get(0)[0],
					maxX = (double) shape.get(0)[1], maxY = (double) shape.get(0)[1];
			Double[] angles = new Double[shape.size()];
			double sum = 0;
			int j = 0;
			for (int[] points : shape) {
				double x = (double) points[0];
				double y = (double) points[1];
				double x1 = (double) shape.get(j == shape.size() - 1 ? 0 : j + 1)[0];
				double x2 = (double) shape.get(j == 0 ? shape.size() - 1 : j - 1)[0];
				double y1 = (double) shape.get(j == shape.size() - 1 ? 0 : j + 1)[1];
				double y2 = (double) shape.get(j == 0 ? shape.size() - 1 : j - 1)[1];
				minX = x < minX ? x : minX;
				minY = y < minY ? y : minY;
				maxX = x > maxX ? x : maxX;
				maxY = y > maxY ? y : maxY;
				double a = Math.sqrt(sqr(x-x1)+sqr(y-y1));
				double b = Math.sqrt(sqr(x-x2)+sqr(y-y2));
				double c = Math.sqrt(sqr(x2-x1)+sqr(y2-y1));
				//System.out.println("Distances 1,2,&3 are"+a+", "+b+", & "+c);
				double C = Math.acos((sqr(c)-(sqr(a)+sqr(b)))/(-2*a*b));
				//System.out.println("Before acos:"+C);
				angles[j] = C;
				sum += angles[j];
				j++;
			}
			double avg = sum / angles.length;
			double sqDiffSum = 0;
			for (double angle: angles) {
//				System.out.println("angle: "+angle);
//				System.out.println("angle in degrees: "+(angle*180/Math.PI));
				sqDiffSum += (avg - angle) * (avg - angle);
			}
			double r = (maxX - minX) / (maxY - minY);
			double sqDiffMean = sqDiffSum / angles.length;
			double std = Math.sqrt(sqDiffMean);

			outArray[i][0] = (double) shape.size();
			outArray[i][1] = r;
			outArray[i][2] = std;
			outArray[i][3] = avg;
			i++;
		}
		return outArray;
	}
	
	public void calculateData(ArrayList<ArrayList<int[]>> allEndpoints){
		//if vertices were found
		if(allEndpoints != null && allEndpoints.size() > 0){
			Double[][] my = processShape(allEndpoints);
			//display values on GUI labels
			for (Double[] value: my){
				pointsLabel.setText("Number of Points: " + value[0]);
				ratioLabel.setText("Width/Height Ratio: " + value[1]);
				stddevLabel.setText("Angle Std Dev: " + value[2]);
				anglesLabel.setText("Angle Average: " + value[3]);
				for (Double actualValue : value){
					System.out.println(actualValue);
				}
			}
		}
		//reset values
		else{
			pointsLabel.setText("Number of Points: ");
			ratioLabel.setText("Width/Height Ratio: ");
			stddevLabel.setText("Angle Std Dev: ");
			anglesLabel.setText("Angle Average: ");
		}
	}
		
	private double sqr(double in) {
		return in * in;
	}
	
	//kenny's code for cropping, currently broken
	private ArrayList<int[]> tailCheck4(int[][] SimilarityArray, int StartX, int StartY, ArrayList<int[]> edgeArray,ArrayList<int[]> testedArray,int Iteration) {
		int[] CurrentPoint = {StartX, StartY};
		/*for(int[] o : testedArray) {
			for(int i : o) {
				System.out.print(i+" ");
			}
			System.out.println("has been tested");
		}*/
		Iteration ++;
		System.out.println("Iteration "+Iteration);
		if(!ALContainsArray(testedArray,CurrentPoint)) {
			System.out.println("Testing ("+StartX+","+StartY+")"+" which is: "+SimilarityArray[StartX][StartY]);
			testedArray.add(CurrentPoint);
			try {
				if(SimilarityArray[StartX+1][StartY] > blockThresh) {
					edgeArray = tailCheck4(SimilarityArray,StartX+1,StartY,edgeArray,testedArray, Iteration);
				} else if(!ALContainsArray(edgeArray,CurrentPoint)) {
					edgeArray.add(CurrentPoint);
				}
			}catch (ArrayIndexOutOfBoundsException e) {}
			try {
				if(SimilarityArray[StartX][StartY+1] > blockThresh) {
					edgeArray = tailCheck4(SimilarityArray,StartX,StartY+1,edgeArray,testedArray, Iteration);
				} else if(!ALContainsArray(edgeArray,CurrentPoint)) {
					edgeArray.add(CurrentPoint);
				}
			}catch (ArrayIndexOutOfBoundsException e) {}
			try {
				if(SimilarityArray[StartX-1][StartY] > blockThresh) {
					edgeArray = tailCheck4(SimilarityArray,StartX-1,StartY,edgeArray,testedArray, Iteration);
				} else if(!ALContainsArray(edgeArray,CurrentPoint)) {
					edgeArray.add(CurrentPoint);
				}
			}catch (ArrayIndexOutOfBoundsException e) {}
			try {
				if(SimilarityArray[StartX][StartY-1] > blockThresh) {
					edgeArray = tailCheck4(SimilarityArray,StartX,StartY-1,edgeArray,testedArray, Iteration);
				} else if(!ALContainsArray(edgeArray,CurrentPoint)) {
					edgeArray.add(CurrentPoint);
				}
			}catch (ArrayIndexOutOfBoundsException e) {}
		}
		System.out.println("returning");
		return edgeArray;
	}
	private Boolean ALContainsArray(ArrayList<int[]> AL, int[] intArray) {
		for(int[] iA : AL) {
			if(Arrays.equals(iA, intArray)) {
				return true;
			}
		}
		return false;
	}
	
	
	//natalie's code 
	
	//ik bad form but...
	private int connectedPixels = 0;
	private ArrayList<ArrayList<Integer>> connectedPoints;
	
	private BufferedImage checkEdges(BufferedImage edges){
		ArrayList<ArrayList<Integer>> thePoints = findAllPointsOnShapes(edges);
		
		BufferedImage modified;
		int picWidth = edges.getWidth();
		int picHeight = edges.getHeight();
		modified = new BufferedImage(picWidth, picHeight, edges.getType());
		for (int i = 0; i < picWidth; i +=1){
			for (int k = 0; k < picHeight; k += 1){
				modified.setRGB(i, k, edges.getRGB(i, k));
			}
		}
		ArrayList<ArrayList<ArrayList<Integer>>> theBlobs = new ArrayList<ArrayList<ArrayList<Integer>>>();
		for (int k = 0; k < thePoints.size(); k += 1){
			Color myColor = new Color(modified.getRGB(thePoints.get(k).get(0), thePoints.get(k).get(1)));
			if (myColor.equals(Color.RED)){
				ArrayList<Integer> eachPoint = thePoints.get(k);
				connectedPoints = new ArrayList<ArrayList<Integer>>();
				connectedPixels = 1;
				connectedPoints.add(eachPoint);
				checkEdgesHelper(eachPoint.get(0), eachPoint.get(1), modified, true);
				for (int j = 0; j < connectedPoints.size(); j +=1){
					modified.setRGB(connectedPoints.get(j).get(0), connectedPoints.get(j).get(1), Color.WHITE.getRGB());
				}
				theBlobs.add(connectedPoints);
			}
		}
		if (theBlobs.size() > 5){
			real = true;
		}
		if (theBlobs.size() > 0){
			ArrayList<Integer> largeOutlines = new ArrayList<Integer>();
			for (int i = 0; i < theBlobs.size(); i += 1){ //finds the biggest blob
				if (theBlobs.get(i).size() > sizeThreshold){
					largeOutlines.add(i);
				}
			}
	
			BufferedImage returnME = new BufferedImage(picWidth, picHeight, edges.getType());
			for (int i = 0; i < picWidth; i +=1){ //sets all pixels to white
				for (int k = 0; k < picHeight; k += 1){
					returnME.setRGB(i, k, Color.WHITE.getRGB());
				}
			}
			for (int i = 0; i < largeOutlines.size(); i++) {
				for (int k = 0; k < theBlobs.get(largeOutlines.get(i)).size(); k+=1 ){ //sets selected pixels to red
					int x = theBlobs.get(largeOutlines.get(i)).get(k).get(0);
					int y = theBlobs.get(largeOutlines.get(i)).get(k).get(1);
					returnME.setRGB(x, y, Color.RED.getRGB());
				}
			}
			return returnME;
		}
		
		return edges; //not what i want to return i think
	}
	
	private void checkEdgesHelper(int x, int y, BufferedImage edges, boolean first){ //recursive
		//add to connectedPixels
		//add to connectedPoints
		BufferedImage modified;
		if (first){
			int picWidth = edges.getWidth();
			int picHeight = edges.getHeight();
			modified = new BufferedImage(picWidth, picHeight, edges.getType());
			for (int i = 0; i < picWidth; i +=1){
				for (int k = 0; k < picHeight; k += 1){
					modified.setRGB(i, k, edges.getRGB(i, k));
				}
			}
		}else{
			modified = edges;
		}
		for (int i = (x - 1); i <= (x + 1); i +=1){
			for (int k = (y - 1); k <= (y + 1); k += 1){
				if (i>= 0 && k >= 0 && i < edges.getWidth() && k < edges.getHeight()){ //not off edge
					if (!(i == x && k == y)){ //not currentpoint
						Color myC = new Color(edges.getRGB(i, k));
						if (myC.equals(Color.RED)){
							connectedPixels += 1;
							ArrayList<Integer> thePoint = new ArrayList<Integer>();
							thePoint.add(i);
							thePoint.add(k);
							connectedPoints.add(thePoint);
							modified.setRGB(i, k, Color.WHITE.getRGB());
							checkEdgesHelper(i, k, modified, false);
						}			
					}
				}			
			}
		}	
	}
	
	private boolean real;
	private boolean realORfake(BufferedImage pic){
		real = false;
		checkEdges(findEdges(convertGrayscale(pic)));
		return real;
	}
	
	private ArrayList findEndpoints(BufferedImage blackLines){
		//passing him an array list of shapes (arraylists) of points (arraylists)
		//ArrayList<ArrayList> allPoints = findAllPointsOnShapes(blackLines);
		//WHITE ON RIGHT FROM POV
		
		ArrayList<Integer> firstPoint = new ArrayList<Integer>(); //what i start from
		ArrayList<ArrayList<Integer>> allEnds = new ArrayList<ArrayList<Integer>>();
		
		int picHeight = blackLines.getHeight();
		int picWidth = blackLines.getWidth();
		for (int counterY = 0; counterY < (picHeight) ; counterY += 1){
    		for (int counterX = 0; counterX < (picWidth) ; counterX += 1){
    			int originalColor;
    			originalColor = blackLines.getRGB(counterX, counterY);
        		Color myColor = new Color(originalColor);
        		if (myColor.equals(Color.RED)){
        			firstPoint.add(counterX);
        			firstPoint.add(counterY);
        			break;
        		}
        		
    		}
		}if (firstPoint.size() == 0){ //this means there is not a shape!!
			System.out.println("not a shape...");
			return null;
			
		}else{
			//ArrayList<ArrayList<Integer>> thePoints = tryToFindEndHelp(firstPoint, blackLines, 0.0, new LinkedList<ArrayList<Double>>(), true, new ArrayList<ArrayList<Integer>>(), firstPoint);
			/*firstPoint.remove(0);
			firstPoint.remove(0);
			firstPoint.add(234);
			firstPoint.add(200);
			
			*/
			int maxListNumber = findAllPointsOnShapes(blackLines).size()/200; 
			ArrayList<ArrayList<Integer>> thePoints = tryToFindEndHelp(firstPoint, blackLines, 0.0, new LinkedList<ArrayList<Double>>(), true, new ArrayList<ArrayList<Integer>>(), firstPoint, -1, maxListNumber, 1);
			System.out.println("thePoints size is: " + thePoints.size());
			thePoints = endPointPreciser(thePoints, blackLines);
			return thePoints;
		}
		
		
	}

	private ArrayList<ArrayList<Integer>> tryToFindEndHelp(ArrayList<Integer> currentCoord, BufferedImage blackLines, double average, LinkedList<ArrayList<Double>> history, boolean first, ArrayList<ArrayList<Integer>> masterList, ArrayList<Integer> firstPoint, int currentDir, int maxListNumber, int time){ //this is recursive
		//ends if hits the edge of screen OR can't find colored pixel
		//current coord is the next point along the edge. searching for the next point
		
		//make nother base case with max list number
		boolean thisChange = false;
		
		System.out.println("the size of max history should  is " + maxListNumber);
		if (first){
			System.out.println("the first x is " + currentCoord.get(0));
			System.out.println("the size of history should be 0 and is " + history.size());
		}
		
		//Debugging the bug:
			//something with master length list, first time it maxes out it selects that point MEANING WOAH DER
		slimeTrail.add(currentCoord);
		int x = currentCoord.get(0);
		int y = currentCoord.get(1);
		int nextX = -1;
		int nextY = -1;
		
		
		if (first){
			if (maxListNumber < 15){
				maxListNumber = 15;
			}
		}
		
		//System.out.println("how many pixels" + findAllPointsOnShapes(blackLines).size()/80);
		ArrayList<ArrayList<Integer>> returnME = new ArrayList<ArrayList<Integer>>();
		 
		int dir = -1;

		if (x != 0){ //not on left edge
			if (y != 0){ //not on top screen
				//look up left
				if (nextPixelHelper(x -1, y-1, blackLines, 0)){
					nextX = x -1; 
					nextY = y -1;
					dir = 10;
				}
			}
			if (y != blackLines.getHeight()){ //not on bottom screen
				//look down left
				if (nextPixelHelper(x - 1, y + 1, blackLines, 1)){
					nextX = x - 1; 
					nextY = y + 1;
					dir = 12;
				}
			}
			if (nextPixelHelper(x-1, y, blackLines, 1)){ //go left left
				nextX = x-1;
				nextY = y;
				dir = 11;
			}
		}
		if (x != blackLines.getWidth()){ //not on right edge
			if (y != 0){ //not on top screen
				//look up right up
				if (nextPixelHelper(x + 1, y-1, blackLines, 3)){
					nextX = x +1; 
					nextY = y -1;
					dir = 16;
				}
			}
			if (y != blackLines.getHeight()){ //not on bottom screen
				//look down right
				if (nextPixelHelper(x + 1, y + 1, blackLines, 2)){
					nextX = x + 1; 
					nextY = y + 1;
					dir = 14;
				}
			}
			//look right right
			if (nextPixelHelper(x + 1, y, blackLines, 3)){
				nextX = x + 1; 
				nextY = y;
				dir = 15;
			}
		}
		if (y != 0){ //not on top screen
			//look up up
			if (nextPixelHelper(x, y-1, blackLines, 0)){
				nextX = x; 
				nextY = y -1;
				dir = 17;
			}
		}
		if (y != blackLines.getHeight()){ //not on bottom screen
			//look down down
			if (nextPixelHelper(x, y+1, blackLines, 2)){
				nextX = x; 
				nextY = y + 1;
				dir = 13;
			}
		}if (first == false && firstPoint.get(0) == nextX && firstPoint.get(1)== nextY || time == maxListNumber * 200){ //made it to the begginning and is not the first time around
			System.out.println("i made it to my base case");
			if (history.size() == maxListNumber){
				masterList.add(firstPoint);
			}
			return masterList; // this is my base case
			
		}else if (nextX >= 0){ //means has been changed, since coords are not negative
			boolean restart = false;
			ArrayList<Integer> newCoords = new ArrayList<Integer>();
			ArrayList<Double> bundledHist = new ArrayList<Double>(); //order is average, x, y
			
			if (history.size() > 0){
				double slope = (double)(history.get(history.size()-1).get(2) - nextY) / (history.get(history.size()-1).get(1) - nextX); //find the slope in a double 
				if (history.get(history.size()-1).get(1) - nextX == 0){
					//slope = 1000000000000.0;
					slope = 10.0;
				}
				if (first){ //find / change the running average -- use boolean "first"
					average = slope;
				}else{
					if (history.size()> 4){
						average = average *3;
						average += slope;
						average = average/4;
					}else{
						average += slope;
						average = average/2;
					}
				}
			
			}else{
				average = 0.0; //does this matter??
			}
			
			//if linked list is len odd, and delete from front each time add to back.. compare slope to first element in linked list
			if (history.size() > maxListNumber){
				System.out.println("you erred. please relook your code not in a rage. or, there is no shape so please relook the data");
				//should i return null meaning no polygon, or return the found verticees
			}else if (history.size() == maxListNumber){
				//int sensitivity = -5;
				int sensitivity = -(maxListNumber - 2);
				Double nowAverage = 0.0; 
				int checkBack = 2;
				/*for (int i = 0; i < maxListNumber - 2; i +=1){ //goes thru and finds slope of the last 10
					double thisSlope = (history.get(history.size()- 1 - i).get(2) - history.get(history.size()- 2 - i).get(2))/(history.get(history.size()- 1 - i).get(1) - history.get(history.size()- 2 - i).get(1)) ;
					if ((history.get(history.size()- 1 - i).get(1) - history.get(history.size()- 2 - i).get(1)) == 0){
						//thisSlope = 1000000000000.0;
						thisSlope = 10.0;
					}
				*/
				for (int i = sensitivity; i <0; i +=1){ //goes thru and finds slope of the last 10
					double thisSlope = (history.get(history.size()- 1 + i).get(2) - history.get(history.size()- 2 + i).get(2))/(history.get(history.size()- 1 + i).get(1) - history.get(history.size()- 2 + i).get(1)) ;
					if ((history.get(history.size()- 1 + i).get(1) - history.get(history.size()- 2 + i).get(1)) == 0){
						//thisSlope = 1000000000000.0;
						thisSlope = 10.0;
					}
					if (i == sensitivity){
						nowAverage = thisSlope;
					}else{
						nowAverage += thisSlope;
						nowAverage = nowAverage/2;
					}
				}
				//history.get(history.size() - 1 - sensitivity - checkBack);
				Double myAngle = slopesToAngle(nowAverage, history.get(history.size() - 2 + sensitivity).get(0)); //given slope 1, slope 2
				myAngle = Math.toDegrees(myAngle);
				while (myAngle > 180){
					myAngle = 360 - myAngle;
				}
				if (myAngle > 175){
					myAngle =0.0;
				}
				//now I have an angle called myAngle which is how far away they are
				
				//System.out.println("im outside the if statement");
				if (myAngle > 35 ){//compare current slope average to this one, kinda
					thisChange = true;
					boolean goOn = true;
					for (int i = 1; i < 3; i +=1){
						if (history.get(history.size() - i).get(4) <0){
							goOn = false;
						}
					}
					if (goOn){
						System.out.println("the compared slopes are : " + nowAverage + " and " + history.get(history.size() - 2 + sensitivity).get(0));
						System.out.println("my angle is " + myAngle + " guessed corner is" + nextX + ", " + nextY);
						ArrayList<Integer> corner = new ArrayList<Integer>(); //the guessed corner, the next point
						System.out.println("I found an angle and history's size is " + history.size());
						corner.add(nextX);
						corner.add(nextY); 
						masterList.add(corner);
						
						history = new LinkedList<ArrayList<Double>>();
						average = 0.0;
						restart = true;
						thisChange = false;
					}else{
						history.removeFirst();
					}
				}else{
					history.removeFirst();
				}
			}
			
			//if (history.size() < 4){
				//restart = true;
			//}
			//if they are the same
			if (restart == false){
				bundledHist.add(average);
				bundledHist.add((double)nextX);
				bundledHist.add((double)nextY);
				bundledHist.add((double) dir);
				if (thisChange){
					bundledHist.add(1.0); //if this change is true
				}else{
					bundledHist.add(-1.0);
				}
				history.addLast(bundledHist);
				
				newCoords.add(nextX);
				newCoords.add(nextY);
			}else{
				newCoords.add(x);
				newCoords.add(y);
			}
			
			returnME = tryToFindEndHelp(newCoords, blackLines, average, history, restart, masterList, firstPoint, dir, maxListNumber, time += 1);
			
			}else if (history.size() > maxListNumber){
				System.out.println("well shit. size should never exceed max number");
			}
		else{
			System.out.println("i didnt find  a next point. i am this x: " + x + " and this y: " + y);
		}
		//System.out.println("returnME's size is " + returnME.size());
		return returnME;
		
	}
	
	private double slopesToAngle(double slope1, double slope2){
		return Math.abs((2*Math.PI + Math.atan2(slope1, 1)%(2*(Math.PI))) - (2*Math.PI + Math.atan2(slope2, 1)%(2*(Math.PI))));
	}
	
	private ArrayList findEndpointsOld(BufferedImage blackLines){
		//passing him an array list of shapes (arraylists) of points (arraylists)
		//ArrayList<ArrayList> allPoints = findAllPointsOnShapes(blackLines);
		//I GO COUNTER CLOCKWISE AROUND, WHITE ON RIGHT FROM POV
		
		ArrayList<Integer> firstPoint = new ArrayList<Integer>(); //what i start from
		ArrayList<ArrayList<Integer>> allEnds = new ArrayList<ArrayList<Integer>>();
		
		int picHeight = blackLines.getHeight();
		int picWidth = blackLines.getWidth();
		for (int counterY = 0; counterY < (picHeight) ; counterY += 1){
    		for (int counterX = 0; counterX < (picWidth) ; counterX += 1){
    			int originalColor;
    			originalColor = blackLines.getRGB(counterX, counterY);
        		Color myColor = new Color(originalColor);
        		if (myColor.equals(Color.RED)){
        			firstPoint.add(counterX);
        			firstPoint.add(counterY);
        			break;
        		}
        		
    		}
		}if (firstPoint.size() == 0){ //this means there is not a shape!!
			System.out.println("not a shape...");
			return null;
			
		}else{
			//ArrayList<ArrayList<Integer>> thePoints = tryToFindEndHelp(firstPoint, blackLines, 0.0, new LinkedList<ArrayList<Double>>(), true, new ArrayList<ArrayList<Integer>>(), firstPoint);
			/*firstPoint.remove(0);
			firstPoint.remove(0);
			firstPoint.add(234);
			firstPoint.add(200);
			
			*/
			int maxListNumber = findAllPointsOnShapes(blackLines).size()/80; 
			ArrayList<ArrayList<Integer>> thePoints = tryToFindEndHelpOld(firstPoint, blackLines, 0.0, new LinkedList<ArrayList<Double>>(), true, new ArrayList<ArrayList<Integer>>(), firstPoint, -1, maxListNumber);
			System.out.println("thePoints size is: " + thePoints.size());
			thePoints = endPointPreciser(thePoints, blackLines);
			return thePoints;
		}
		
		
	}

	private ArrayList<ArrayList<Integer>> tryToFindEndHelpOld(ArrayList<Integer> currentCoord, BufferedImage blackLines, double average, LinkedList<ArrayList<Double>> history, boolean first, ArrayList<ArrayList<Integer>> masterList, ArrayList<Integer> firstPoint, int currentDir, int maxListNumber){ //this is recursive
		//ends if hits the edge of screen OR can't find colored pixel
		
		//current coord is the next point along the edge. searching for the next point
		slimeTrail.add(currentCoord);
		int x = currentCoord.get(0);
		int y = currentCoord.get(1);
		int nextX = -1;
		int nextY = -1;
		
		
		if (first){
			if (maxListNumber < 50){
				maxListNumber = 50;
			}
		}
		//System.out.println("how many pixels" + findAllPointsOnShapes(blackLines).size()/80);
		ArrayList<ArrayList<Integer>> returnME = new ArrayList<ArrayList<Integer>>();
		 
		int dir = -1;

		if (x != 0){ //not on left edge
			if (y != 0){ //not on top screen
				//look up left
				if (nextPixelHelper(x -1, y-1, blackLines, 0)){
					nextX = x -1; 
					nextY = y -1;
					dir = 10;
				}
			}
			if (y != blackLines.getHeight()){ //not on bottom screen
				//look down left
				if (nextPixelHelper(x - 1, y + 1, blackLines, 1)){
					nextX = x - 1; 
					nextY = y + 1;
					dir = 12;
				}
			}
			if (nextPixelHelper(x-1, y, blackLines, 1)){ //go left left
				nextX = x-1;
				nextY = y;
				dir = 11;
			}
		}
		if (x != blackLines.getWidth()){ //not on right edge
			if (y != 0){ //not on top screen
				//look up right up
				if (nextPixelHelper(x + 1, y-1, blackLines, 3)){
					nextX = x +1; 
					nextY = y -1;
					dir = 16;
				}
			}
			if (y != blackLines.getHeight()){ //not on bottom screen
				//look down right
				if (nextPixelHelper(x + 1, y + 1, blackLines, 2)){
					nextX = x + 1; 
					nextY = y + 1;
					dir = 14;
				}
			}
			//look right right
			if (nextPixelHelper(x + 1, y, blackLines, 3)){
				nextX = x + 1; 
				nextY = y;
				dir = 15;
			}
		}
		if (y != 0){ //not on top screen
			//look up up
			if (nextPixelHelper(x, y-1, blackLines, 0)){
				nextX = x; 
				nextY = y -1;
				dir = 17;
			}
		}
		if (y != blackLines.getHeight()){ //not on bottom screen
			//look down down
			if (nextPixelHelper(x, y+1, blackLines, 2)){
				nextX = x; 
				nextY = y + 1;
				dir = 13;
			}
		}if (first == false && firstPoint.get(0) == nextX && firstPoint.get(1)== nextY){ //made it to the begginning and is not the first time around
			System.out.println("i made it to my base case");
			if (history.size() == maxListNumber){
				masterList.add(firstPoint);
			}
			return masterList; // this is my base case
			
		}else if (nextX >= 0){ //means has been changed, since coords are not negative
			ArrayList<Integer> newCoords = new ArrayList<Integer>();
			ArrayList<Double> bundledHist = new ArrayList<Double>(); //order is average, x, y
			double slope = (double)(y - nextY) / (x - nextX); //find the slope in a double 
			if (first){ //find / change the running average -- use boolean "first"
				average = slope;
			}else{
				average += slope;
				average = average/2;
			}
			//if linked list is len odd, and delete from front each time add to back.. compare slope to first element in linked list
			if (history.size() > maxListNumber){
				System.out.println("you erred. please relook your code not in a rage. or, there is no shape so please relook the data");
				//should i return null meaning no polygon, or return the found verticees
			}else if (history.size() == maxListNumber){
				ArrayList<Double> passME = new ArrayList<Double>();
				for (int i = 1; i < history.size(); i += 1){
					passME.add(history.get(i).get(3));
				}
				//System.out.println("im outside the if statement");
				if (!(patternMatch(passME, dir))){//compare current slope average to this one, kinda
					ArrayList<Integer> corner = new ArrayList<Integer>(); //the guessed corner, last point in history
		
					corner.add(nextX);
					corner.add(nextY);
					masterList.add(corner);
					
					//this loop is so it doesnt detect the end of the corner (:
					int thisManyToDelete = history.size()/2; 
					while(thisManyToDelete > 0){
						history.removeFirst();
						thisManyToDelete -= 1;
					}
				}else{
					history.removeFirst();
				}
			}
			//if they are the same
			bundledHist.add(average);
			bundledHist.add((double)nextX);
			bundledHist.add((double)nextY);
			bundledHist.add((double) dir);
			history.addLast(bundledHist);
			
			newCoords.add(nextX);
			newCoords.add(nextY);
			
			returnME = tryToFindEndHelpOld(newCoords, blackLines, average, history, false, masterList, firstPoint, dir, maxListNumber);
			
			}else if (history.size() > maxListNumber){
				System.out.println("well shit. size should never exceed max number");
			}
		else{
			System.out.println("i didnt find  a next point. i am this x: " + x + " and this y: " + y);
		}
		//System.out.println("returnME's size is " + returnME.size());
		return returnME;
		
	}
	
	private boolean slopeMatch (LinkedList<ArrayList<Double>> averages){
		boolean slopeMatch = false; //what i will return. false if slope doesnt match meaning corner
		double nowAverage = 0; //this is the average slope of the last 10
		int sensitivity = -5;
		int checkBack = 4; //in normal this is 2
		for (int i = sensitivity; i <0; i +=1){ //goes thru and finds slope of the last 10
			double thisSlope = (averages.get(averages.size()- 1 + i).get(2) - averages.get(averages.size()- checkBack + i).get(2))/(averages.get(averages.size()- 1 + i).get(1) - averages.get(averages.size()- 2 + i).get(1)) ;
			if ((averages.get(averages.size()- 1 + i).get(1) - averages.get(averages.size()- 2 + i).get(1)) == 0){
				thisSlope = 1000000000000.0;
			}
			if (i == 0){
				nowAverage = thisSlope;
			}else{
				nowAverage += thisSlope;
				nowAverage = nowAverage/2;
			}
		}
		double oldAverage = averages.get(averages.size() - checkBack + sensitivity).get(0);
		if (oldAverage == nowAverage){
			slopeMatch = true;
		}else if (oldAverage != 0){
			if (Math.abs(nowAverage / oldAverage) < 1.25 && Math.abs(nowAverage / oldAverage) > .75 ){
				slopeMatch = true;
			}
		}else if (nowAverage != 0){
			if (Math.abs(oldAverage/nowAverage) < 1.25 && Math.abs(oldAverage/nowAverage) > .75){
				slopeMatch = true;
			}
		}
		if (slopeMatch == false){
			System.out.println("the old slope is " + oldAverage + ", the new slope is " + nowAverage);
		}
		return slopeMatch;
	}
	
	private boolean patternMatch (ArrayList<Double> pattern, int myDir){
		boolean patternMatches = false;
		for (int i = 0; i < pattern.size() - 3; i += 1){
			//if (pattern.get(i).intValue() == pattern.get(pattern.size()-3)){
				if (pattern.get(i + 1).intValue() == pattern.get(pattern.size()-2)){
					if (pattern.get(i + 2).intValue() == pattern.get(pattern.size()-1)){
						if (pattern.get(i + 3).intValue() == myDir){
							patternMatches = true;
						}
					}
				}
			//}
		}
		return patternMatches;
	}
	
	private boolean nextPixelHelper(int x, int y, BufferedImage blackLines, int dir){ //figure out the next pixel
		//x y is the coordinates in question
		boolean next = false;
		Color mine = new Color(blackLines.getRGB(x, y));
		if (mine.equals(Color.RED)){
			Color mine1;
			if (dir == 0){ //right is right
				mine1 = new Color(blackLines.getRGB(x + 1, y));
				if (!(mine1.equals(Color.RED))){ //if right is white
					next = true;
				}
			}else if (dir == 1){ //up is right
				mine1 = new Color(blackLines.getRGB(x, (y - 1)));
				if (!(mine1.equals(Color.RED))){ //if right is white
					next = true;
				}
			}else if (dir == 2){ //left is right
				mine1 = new Color(blackLines.getRGB(x - 1, y));
				if (!(mine1.equals(Color.RED))){ //if right is white
					next = true;
				}
			}else if (dir == 3){ //down is right
				mine1 = new Color(blackLines.getRGB(x, y + 1));
				if (!(mine1.equals(Color.RED))){ //if right is white
					next = true;
				}
			}
		}
		
		return next;
	}
	
	private ArrayList<ArrayList<Integer>> findAllPointsOnShapes(BufferedImage blackLines){
		
		int picWidth = blackLines.getWidth();
		int picHeight = blackLines.getHeight();
		Color red = Color.RED;
		ArrayList<ArrayList<Integer>> listAll = new ArrayList<ArrayList<Integer>>();
		for (int counterY = 0; counterY < (picHeight) ; counterY += 1){
    		for (int counterX = 0; counterX < (picWidth) ; counterX += 1){
    			int originalColor;
    			originalColor = blackLines.getRGB(counterX, counterY);
        		Color myColor = new Color(originalColor);
        		if (myColor.equals(red)){
        			//this means this "point" is on a side!!
        			ArrayList<Integer> temporary = new ArrayList<Integer>();
        			temporary.add(counterX);
        			temporary.add(counterY);
        			listAll.add(temporary);
        		}
    		}
		}
		return listAll;
	}

	private ArrayList endPointPreciser(ArrayList<ArrayList<Integer>> endPoints, BufferedImage blackLines){
		ArrayList <ArrayList> precise = new ArrayList<ArrayList>(); //what i will return
		for (int i = 0; i < endPoints.size(); i += 1){
			ArrayList<Integer> temporaryX = new ArrayList<Integer>();
			ArrayList<Integer> temporaryY = new ArrayList<Integer>();
			
			int scale = Math.abs(5); 
			
			//checking for edges
			boolean outOfRange = true;
			while (outOfRange){
				if (endPoints.get(i).get(0) + scale < blackLines.getWidth()){
					if (endPoints.get(i).get(1) + scale < blackLines.getHeight()){
						if (endPoints.get(i).get(0) - scale > 0 ){
							if (endPoints.get(i).get(1) - scale > 0){
								outOfRange = false;
								break;
							}
						}
					}
				}
				scale -= 1;
			}
			for (int j = -scale; j < scale; j+=1){
				for (int k = -scale; k < scale; k +=1){
					int originalColor;
	    			originalColor = blackLines.getRGB(endPoints.get(i).get(0) + j, endPoints.get(i).get(1) + k);
	        		Color myColor = new Color(originalColor);
	        		if (myColor.equals(Color.RED)){
	        			temporaryX.add(endPoints.get(i).get(0) + j);
	        			temporaryY.add((endPoints.get(i).get(1) + k));
	        		}
				}
			}
			
			int xTot = 0;
			for (int m = 0; m < temporaryX.size(); m += 1){
				xTot += temporaryX.get(m);
			}
			int preciseX = xTot / temporaryX.size();
			int yTot = 0;
			for (int n = 0; n < temporaryY.size(); n += 1){
				yTot += temporaryY.get(n);
			}
			int preciseY = yTot / temporaryY.size();
			
			ArrayList<Integer> myList = new ArrayList<Integer>();
			myList.add(preciseX);
			myList.add(preciseY);
			precise.add(myList);
		}
		return precise;
	}
}
