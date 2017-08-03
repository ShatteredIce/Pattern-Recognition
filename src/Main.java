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
	private ArrayList<ArrayList<Integer>> slimeTrail = new ArrayList<ArrayList<Integer>>();
	
	final Color slimeTrailColor = new Color(255, 0, 0);
	final Color endPointColor = new Color(0, 255, 0);
	ArrayList<ArrayList<Integer>> endpoints = null;
	
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
	JButton imgButton2 = new JButton("Outline");
	JButton imgButton3 = new JButton("Processed");
	//east container
	Container east = new Container();
	JButton calculate = new JButton("Calculate");
	JButton loadfile = new JButton("Load");
	JButton savefile = new JButton("Save");
	JButton generate = new JButton("Generate");
	JCheckBox autocalculate = new JCheckBox("Auto-Calculate");
	JLabel pointsLabel = new JLabel("Number of Points: ");
	JLabel ratioLabel = new JLabel("Width/Height Ratio: ");
	JLabel stddevLabel = new JLabel("Angle Std Dev: ");
	JLabel anglesLabel = new JLabel("Angle Average: ");
	JLabel tempLabel = new JLabel("Temp: ");
	
	
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
		c.gridwidth = 3;
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
		c = setGridBagConstraints(c, 0, 1, 0, 10, 0, 0);
		east.add(autocalculate, c);
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
		c = setGridBagConstraints(c, 2, 7, 0, 0, 0, 0);
		east.add(generate, c);
		generate.addActionListener(this);
		
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		generateShapeData();
		
		
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
		if(event.getSource().equals(loadfile) || event.getSource().equals(generate)){
			BufferedImage raw;
			if(event.getSource().equals(loadfile)){
				raw = loadImage("./res/raw/" + filename.getText());
			}
			else{
				raw = generateImage();
			}
			if(raw != null){
				slimeTrail.clear();
				if(endpoints != null){
					endpoints.clear();
				}
				BufferedImage outline = checkEdges(findEdges(raw));
				BufferedImage processed = highlightShape(outline, raw);
				endpoints = findEndpoints(outline);
				//display slime trail as outline
				for (int k = 0; k < slimeTrail.size(); k += 1){
					outline.setRGB(slimeTrail.get(k).get(0), slimeTrail.get(k).get(1), slimeTrailColor.getRGB());
					processed.setRGB(slimeTrail.get(k).get(0), slimeTrail.get(k).get(1), slimeTrailColor.getRGB());
				}
				//display endpoints as dots
				if(endpoints != null){
					for (int i = 0; i < endpoints.size(); i ++){
						ArrayList<Integer> temp = endpoints.get(i);
						int endpointWidth = 1;
						for (int j = -endpointWidth; j <= endpointWidth; j++) {
							for (int k = -endpointWidth; k <= endpointWidth; k++) {
								outline.setRGB((int)temp.get(0)+j, (int)temp.get(1)+k, endPointColor.getRGB());
								processed.setRGB((int)temp.get(0)+j, (int)temp.get(1)+k, endPointColor.getRGB());
								if (i == endpoints.size() -1){
									//shape.setRGB((int)temp.get(0), (int)temp.get(1), (new Color(255,255,255)).getRGB());
								}
							}
						}
						System.out.println("X:" + temp.get(0) + " Y: " + temp.get(1));
					}
				}
				panel.setImages(raw, outline, processed);
				if(autocalculate.isSelected()){
					calculateData();
				}
				frame.repaint();
			}
			else{
				endpoints = null;
			}
		}
		else if(event.getSource().equals(calculate)){
			calculateData();
		}
		else if(event.getSource().equals(savefile)){
			if(panel.getCurrentImage() != null){
				storeImage(panel.getCurrentImage(), "./res/processed/savedimage.png");
			}
		}
		else if(event.getSource().equals(imgButton1)){
			panel.setImgType(0);
			frame.repaint();
		}
		else if(event.getSource().equals(imgButton2)){
			panel.setImgType(1);
			frame.repaint();
		}
		else if(event.getSource().equals(imgButton3)){
			panel.setImgType(2);
			frame.repaint();
		}
	}
	
	public GridBagConstraints setGridBagConstraints(GridBagConstraints c, int x, int y, int xpad, int ypad, double xweight, double yweight){
		c.gridx = x;
		c.gridy = y;
		c.ipadx = xpad;
		c.ipady = ypad;
		c.weightx = xweight;
		c.weighty = yweight;
		return c;
	}
	
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
				int totalDifference = 0;
				for(int i = xpos - 2; i <= xpos + 2; i++){
					for(int j = ypos - 2; j <= ypos + 2; j++){
						Color pixel1 = new Color(img.getRGB(xpos, ypos));
						Color pixel2 = new Color(img.getRGB(i, j));
						totalDifference += checkSimilarity(pixel1, pixel2);
						}
					}
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
	
	
	private double checkSimilarity(Color pixel1, Color pixel2){
		int red1 = pixel1.getRed();
		int green1 = pixel1.getGreen();
		int blue1 = pixel1.getBlue();
		int red2 = pixel2.getRed();
		int green2 = pixel2.getGreen();
		int blue2 = pixel2.getBlue();
		double difference = Math.sqrt(Math.pow(red1 - red2, 2) + Math.pow(green1 - green2, 2) + Math.pow(blue1 - blue2, 2));
		return difference;
	}
	
	public BufferedImage generateImage(){
		int imgsize = 300;
		Polygon p = new Polygon();
		int shapeType = random.nextInt(2);
		if(shapeType == 0){
			int numPoints = 3;
			for (int i = 0; i < numPoints; i++) {
				p.addPoint(random.nextInt(imgsize + 1), random.nextInt(imgsize + 1));
			}
		}
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
		BufferedImage generatedImage = new BufferedImage(imgsize, imgsize, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < generatedImage.getWidth(); x++) {
			for (int y = 0; y < generatedImage.getHeight(); y++) {
				if(p.contains(new Point(x, y))){
					generatedImage.setRGB(x, y, Color.RED.getRGB());
				}
				else{
					generatedImage.setRGB(x, y, Color.WHITE.getRGB());
				}
			}
		}
		
		return generatedImage;
	}
	
	
	
	public void generateShapeData(){
		int numPoints = 3;
		int upperBound = 200;
		ArrayList<ArrayList<Integer>> shapePoints = new ArrayList<>();
		for (int i = 0; i < numPoints; i++) {
			ArrayList<Integer> currentPoint = new ArrayList<>();
			currentPoint.add(random.nextInt(upperBound + 1));
			currentPoint.add(random.nextInt(upperBound + 1));
			shapePoints.add(currentPoint);
			System.out.println("X: " + shapePoints.get(i).get(0) + " Y: " + shapePoints.get(i).get(1));
		}
		ArrayList<ArrayList<ArrayList<Integer>>> myList = new ArrayList<ArrayList<ArrayList<Integer>>>();
		myList.add(shapePoints);
		Double[] result = processShape(myList)[0];
		for (int i = 0; i < result.length; i++) {
			System.out.println(result[i]);
		}
		
	}
	
	//Nested Arraylists: Shape >> Endpoints >> X,Y
	private Double[][] processShape(ArrayList<ArrayList<ArrayList<Integer>>> inArray) {
		int len = inArray.size();
		int i = 0;
		Double[][] outArray = new Double[len][4];
		/// Identify: ratio of max-width:max-height, number of points, std from
		/// average angle
		for (ArrayList<ArrayList<Integer>> shape : inArray) {
			double minX = (double) shape.get(0).get(0), minY = (double) shape.get(0).get(1),
					maxX = (double) shape.get(0).get(1), maxY = (double) shape.get(0).get(1);
			Double[] angles = new Double[shape.size()];
			double sum = 0;
			int j = 0;
			for (ArrayList<Integer> points : shape) {
				double x = (double) points.get(0);
				double y = (double) points.get(1);
				double x1 = (double) shape.get(j == shape.size() - 1 ? 0 : j + 1).get(0);
				double x2 = (double) shape.get(j == 0 ? shape.size() - 1 : j - 1).get(0);
				double y1 = (double) shape.get(j == shape.size() - 1 ? 0 : j + 1).get(1);
				double y2 = (double) shape.get(j == 0 ? shape.size() - 1 : j - 1).get(1);
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
	
	public void calculateData(){
		//if endpoints were found
		if(endpoints != null){
			ArrayList<ArrayList<ArrayList<Integer>>> myList = new ArrayList<ArrayList<ArrayList<Integer>>>();
			myList.add(endpoints);
		
			Double[][] my = processShape(myList);
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
	//have no idea how this works
	
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
			int currentBig = 0;
			int bigIndex = -1;
			for (int i = 0; i < theBlobs.size(); i += 1){ //finds the biggest blob
				if (theBlobs.get(i).size() > currentBig){
					
					currentBig = (int)theBlobs.get(i).size();
					bigIndex = i;
				}
			}
	
			BufferedImage returnME = new BufferedImage(picWidth, picHeight, edges.getType());
			for (int i = 0; i < picWidth; i +=1){ //sets all pixels to white
				for (int k = 0; k < picHeight; k += 1){
					returnME.setRGB(i, k, Color.WHITE.getRGB());
				}
			}
			for (int k = 0; k < theBlobs.get(bigIndex).size(); k+=1 ){ //sets selected pixels to red
				int x = theBlobs.get(bigIndex).get(k).get(0);
				int y = theBlobs.get(bigIndex).get(k).get(1);
				returnME.setRGB(x, y, Color.RED.getRGB());
			}
			System.out.println("about to return");
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
			ArrayList<ArrayList<Integer>> thePoints = tryToFindEndHelp(firstPoint, blackLines, 0.0, new LinkedList<ArrayList<Double>>(), true, new ArrayList<ArrayList<Integer>>(), firstPoint, -1, maxListNumber);
			System.out.println("thePoints size is: " + thePoints.size());
			thePoints = endPointPreciser(thePoints, blackLines);
			return thePoints;
		}
		
		
	}

	private ArrayList<ArrayList<Integer>> tryToFindEndHelp(ArrayList<Integer> currentCoord, BufferedImage blackLines, double average, LinkedList<ArrayList<Double>> history, boolean first, ArrayList<ArrayList<Integer>> masterList, ArrayList<Integer> firstPoint, int currentDir, int maxListNumber){ //this is recursive
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
			
			returnME = tryToFindEndHelp(newCoords, blackLines, average, history, false, masterList, firstPoint, dir, maxListNumber);
			
			}else if (history.size() > maxListNumber){
				System.out.println("well shit. size should never exceed max number");
			}
		else{
			System.out.println("i didnt find  a next point. i am this x: " + x + " and this y: " + y);
		}
		//System.out.println("returnME's size is " + returnME.size());
		return returnME;
		
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
