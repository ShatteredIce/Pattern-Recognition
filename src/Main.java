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
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class Main implements ActionListener {
	
	final Random random = new Random();
	final int colorThreshold = 400;
	int sizeThreshold = 50;
	private ArrayList<int[]> tracedOutline = new ArrayList<int[]>();
	ArrayList<ArrayList<int[]>> allEndpoints = new ArrayList<>();
	ArrayList<double[]> pixelSimilarity = new ArrayList<>();
	
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
	JButton imgButton4 = new JButton("Endpoints");
	JButton imgButton5 = new JButton("Processed");
	//east container
	Container east = new Container();
	JButton calculate = new JButton("Calculate");
	JButton loadfile = new JButton("Load");
	JButton savefile = new JButton("Save");
	JButton generate = new JButton("Generate");
	JCheckBox autocalculate = new JCheckBox("Auto-Calc");
	JCheckBox blur = new JCheckBox("Blur");
	JButton shapeIdButton = new JButton("Change Selected Shape");
	JLabel noiseSizeLabel = new JLabel("Size Threshold: ");
	JButton increaseSizeThreshold = new JButton("Increase");
	JButton decreaseSizeThreshold = new JButton("Decrease");
	JLabel pointsLabel = new JLabel("Number of Points: ");
	JLabel ratioLabel = new JLabel("Width/Height Ratio: ");
	JLabel stddevLabel = new JLabel("Angle Std Dev: ");
	JLabel anglesLabel = new JLabel("Angle Average: ");
	JLabel statusLabel = new JLabel("Status: Idle");
	
	
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
		c = setGridBagConstraints(c, 0, 2, 0, 0, 0, 0);
		east.add(shapeIdButton, c);
		shapeIdButton.addActionListener(this);
		shapeIdButton.setEnabled(false);
		c = setGridBagConstraints(c, 0, 3, 0, 10, 0, 0);
		noiseSizeLabel.setText("Size Threshold: " + sizeThreshold);
		east.add(noiseSizeLabel, c);
		c = setGridBagConstraints(c, 0, 4, 0, 0, 0, 0);
		east.add(increaseSizeThreshold, c);
		increaseSizeThreshold.addActionListener(this);
		c = setGridBagConstraints(c, 1, 4, 0, 0, 0, 0);
		east.add(decreaseSizeThreshold, c);
		decreaseSizeThreshold.addActionListener(this);
		c = setGridBagConstraints(c, 0, 5, 0, 0, 0, 0.1);
		east.add(pointsLabel, c);
		c = setGridBagConstraints(c, 0, 6, 0, 0, 0, 0.1);
		east.add(ratioLabel, c);
		c = setGridBagConstraints(c, 0, 7, 0, 0, 0, 0.1);
		east.add(stddevLabel, c);
		c = setGridBagConstraints(c, 0, 8, 0, 0, 0, 0.1);
		east.add(anglesLabel, c);
		c = setGridBagConstraints(c, 0, 9, 0, 0, 0, 2);
		east.add(statusLabel, c);
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c = setGridBagConstraints(c, 1, 10, 20, 0, 1, 0);
		east.add(generate, c);
		generate.addActionListener(this);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		NeuralNetwork net = new NeuralNetwork();
				
	}

	public static void main(String[] args) {
		new Main();
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		//remove status message
		statusLabel.setText("Status: Idle");
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
				allEndpoints.clear();
				BufferedImage blurred;
				if(blur.isSelected()){
					blurred = gaussianBlur(raw);
				}
				else{
					blurred = raw;
				}
				//erosion + dilation to smooth out edges
				BufferedImage outline = (erodeImage(dilateImage(findEdges(blurred))));
				//stores shape outlines in an image
				BufferedImage endpoints = new BufferedImage(outline.getWidth(), outline.getHeight(), outline.getType());
				for (int x = 0; x < outline.getWidth(); x++) {
					for (int y = 0; y < outline.getHeight(); y++) {
						endpoints.setRGB(x, y, outline.getRGB(x, y));
					}
				}
				allEndpoints = findVertices(endpoints);
				BufferedImage processed = overlayShape(endpoints, raw);
				
				panel.setImages(raw, blurred, outline, endpoints, processed);
				panel.setEndpoints(allEndpoints);
				//if there is no shape or one shape, disable button to change shape, else enable button
				if (allEndpoints.size() <= 1) {
					shapeIdButton.setEnabled(false);
				}
				else{
					shapeIdButton.setEnabled(true);
				}
				//if the auto-calculate checkbox is selected, calculate data
				if(autocalculate.isSelected()){
					calculateData(allEndpoints);
				}
				frame.repaint();
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
		//change the shape selected to be calculated
		else if(event.getSource().equals(shapeIdButton)){
			if(panel.getSelectedShape() + 1 < allEndpoints.size()){
				panel.setSelectedShape(panel.getSelectedShape()+1);
			}
			else{
				panel.setSelectedShape(0);
			}
			//calculate data for new shape
			if(autocalculate.isSelected()){
				calculateData(allEndpoints);
			}
			//reset data labels
			else{
				pointsLabel.setText("Number of Points: ");
				ratioLabel.setText("Width/Height Ratio: ");
				stddevLabel.setText("Angle Std Dev: ");
				anglesLabel.setText("Angle Average: ");
			}
			
		}
		//increase size threshold for shapes to be detected
		else if(event.getSource().equals(increaseSizeThreshold)){
			sizeThreshold += 50;
			noiseSizeLabel.setText("Size Threshold: " + sizeThreshold);
		}
		//decrease size threshold for shapes to be detected
		else if(event.getSource().equals(decreaseSizeThreshold)){
			if(sizeThreshold != 0){
				sizeThreshold -= 50;
				noiseSizeLabel.setText("Size Threshold: " + sizeThreshold);
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
			statusLabel.setText("Status: File not found");
			return null;
		}
	}
	
	//store an image to the computer
	private void storeImage(BufferedImage image, String path) {
		File out = new File(path);
		try {
			ImageIO.write(image, "png", out);
			statusLabel.setText("Status: Saved Image");
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		int radius = 2;
		BufferedImage convertedImage = new BufferedImage(width, height, img.getType()); 
		//set edges to white
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < radius; x++) {
				convertedImage.setRGB(x, y, Color.WHITE.getRGB());
			}
			for (int x = width - radius; x < width; x++) {
				convertedImage.setRGB(x, y,  Color.WHITE.getRGB());
			}
		}
		for (int x = radius; x < width - radius; x++) {
			for (int y = 0; y < radius; y++) {
				convertedImage.setRGB(x, y,  Color.WHITE.getRGB());
			}
			for (int y = height - radius; y < height; y++) {
				convertedImage.setRGB(x, y,  Color.WHITE.getRGB());
			}
		}
		//calculates color difference from neighbors for each inner pixel
		for(int xpos = radius; xpos < width - radius; xpos++){
			for(int ypos = radius; ypos < height - radius; ypos++){
				int totalDifference = 0;
				for(int i = xpos - radius; i <= xpos + radius; i++){
					for(int j = ypos - radius; j <= ypos + radius; j++){
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
	
	//find the vertices of all the shapes in the image
	private ArrayList<ArrayList<int[]>> findVertices(BufferedImage outline){
		ArrayList<ArrayList<int[]>> allShapeVertices = new ArrayList<>();
		BufferedImage noiseRemoved = new BufferedImage(outline.getWidth(), outline.getHeight(), outline.getType());
		for (int x = 0; x < outline.getWidth(); x++) {
			for (int y = 0; y < outline.getHeight(); y++) {
				noiseRemoved.setRGB(x, y, outline.getRGB(x, y));
			}
		}
		//image that stores the traced outlines of each shape
		BufferedImage outlineTrace = new BufferedImage(outline.getWidth(), outline.getHeight(), outline.getType());
		boolean finished = false;
		while(!finished){
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
				finished = true;
			}
			else{
				tracedOutline.add(startPoint);
				nextPoint = getNextPoint(outline, startPoint[0], startPoint[1], 2);
				//while there is a next point and the next point has not returned to the starting point, draw a single pixel outline around the initial outline
				while(nextPoint[0] != -1 && !((nextPoint[0] == startPoint[0]) && (nextPoint[1] == startPoint[1]))){
					tracedOutline.add(nextPoint);
					nextPoint = getNextPoint(outline, nextPoint[0], nextPoint[1], nextPoint[2]);
				}
				//remove noise
				if(tracedOutline.size() < sizeThreshold){
					outline = deleteTracedShape(outline, startPoint[0], startPoint[1]);
					noiseRemoved = deleteTracedShape(noiseRemoved, startPoint[0], startPoint[1]);
					continue;
				}
				//add traced outline to displayed image
				for (int i = 0; i < tracedOutline.size(); i++) {
					outlineTrace.setRGB(tracedOutline.get(i)[0], tracedOutline.get(i)[1], Color.GREEN.getRGB());
				}
				int pixelsGrouped = Math.max(10, Math.round(tracedOutline.size() / 30f));
				ArrayList<int[]> currentCorner = new ArrayList<int[]>();
				boolean groupContainsVertex = false;
				//wrap around starting pixels
				for (int i = 0; i < pixelsGrouped * 2; i++) {
					if(nextPoint[0] == -1){
						break;
					}
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
							averageX = currentCorner.get(currentCorner.size()/2)[0];
							averageY = currentCorner.get(currentCorner.size()/2)[1];
							int[] currentPixel = {averageX, averageY};
							vertices.add(currentPixel);
						}
						groupContainsVertex = false;
						currentCorner.clear();
					}
				}
				outline = deleteTracedShape(outline, startPoint[0], startPoint[1]);
				//check to see if three consecutive vertices are collinear, if true remove the middle one from the vertices list
				if(vertices.size() > 1){
					for (int firstIndex = 0; firstIndex < vertices.size(); firstIndex++) {
						int secondIndex = firstIndex + 1;
						int thirdIndex = firstIndex + 2;
						if(secondIndex >= vertices.size()){
							secondIndex -= vertices.size();
						}
						if(thirdIndex >= vertices.size()){
							thirdIndex -= vertices.size();
						}
						double firstAngle = pixelsToAngle(vertices.get(firstIndex)[0], vertices.get(firstIndex)[1], vertices.get(secondIndex)[0], vertices.get(secondIndex)[1]);
						double secondAngle = pixelsToAngle(vertices.get(secondIndex)[0], vertices.get(secondIndex)[1], vertices.get(thirdIndex)[0], vertices.get(thirdIndex)[1]);
						//less accuracy if the second pixel is close to the first
						if((Math.abs(vertices.get(secondIndex)[0] - vertices.get(firstIndex)[0]) + Math.abs(vertices.get(secondIndex)[1] - vertices.get(firstIndex)[1])) < 20){
							if(getSmallestBearing(firstAngle, secondAngle) < 15){
								vertices.remove(secondIndex);
								firstIndex--;
							}
						}
						//less accuracy if the second pixel is close to the third
						else if((Math.abs(vertices.get(secondIndex)[0] - vertices.get(thirdIndex)[0]) + Math.abs(vertices.get(secondIndex)[1] - vertices.get(thirdIndex)[1])) < 20){
							if(getSmallestBearing(firstAngle, secondAngle) < 15){
								vertices.remove(secondIndex);
								firstIndex--;
							}
						}
						else{
							if(getSmallestBearing(firstAngle, secondAngle) < 10){
								vertices.remove(secondIndex);
								firstIndex--;
							}
						}
					}
				}
				if(vertices.size() > 1){
					allShapeVertices.add(vertices);
				}
			}
		}
		//draw the outlines of the larger shapes (non-noise) onto the original image
		for (int x = 0; x < outline.getWidth(); x++) {
			for (int y = 0; y < outline.getHeight(); y++) {
				outline.setRGB(x, y, noiseRemoved.getRGB(x, y));
			}
		}
		//draw the trace around each shape onto the original image
		for (int x = 0; x < outlineTrace.getWidth(); x++) {
			for (int y = 0; y < outlineTrace.getHeight(); y++) {
				if(outlineTrace.getRGB(x, y) == Color.GREEN.getRGB()){
					outline.setRGB(x, y, outlineTrace.getRGB(x, y));
				}
			}
		}
		return allShapeVertices;
	}
	
	//deletes the red outline of one shape from the image containing outlines of many shapes
	private BufferedImage deleteTracedShape(BufferedImage outline, int currentx, int currenty){
		if(outline.getRGB(currentx, currenty) == Color.RED.getRGB()){
			outline.setRGB(currentx, currenty, Color.WHITE.getRGB());
		}
		boolean recurseRight = false;
		boolean recurseUp = false;
		boolean recurseLeft = false;
		boolean recurseDown = false;
		//checks right pixel
		if(currentx + 1 < outline.getWidth() && outline.getRGB(currentx+1, currenty) == Color.RED.getRGB()){
			outline.setRGB(currentx + 1, currenty, Color.WHITE.getRGB());
			recurseRight = true;
		}
		//checks above pixel
		if(currenty + 1 < outline.getHeight() && outline.getRGB(currentx, currenty+1) == Color.RED.getRGB()){
			outline.setRGB(currentx, currenty + 1, Color.WHITE.getRGB());
			recurseUp = true;
		}
		//checks left pixel
		if(currentx - 1 >= 0 && outline.getRGB(currentx-1, currenty) == Color.RED.getRGB()){
			outline.setRGB(currentx - 1, currenty, Color.WHITE.getRGB());
			recurseLeft = true;
		}
		//checks bottom pixel
		if(currenty - 1 >= 0 && outline.getRGB(currentx, currenty-1) == Color.RED.getRGB()){
			outline.setRGB(currentx, currenty - 1, Color.WHITE.getRGB());
			recurseDown = true;
		}
		//recurse right
		if(currentx + 1 < outline.getWidth() && recurseRight == true){
			outline = deleteTracedShape(outline, currentx+1, currenty);
		}
		//recurse up
		if(currenty + 1 < outline.getHeight() && recurseUp == true){
			outline = deleteTracedShape(outline, currentx, currenty+1);
		}
		//recurse left
		if(currentx - 1 >= 0 && recurseLeft == true){
			outline = deleteTracedShape(outline, currentx-1, currenty);
		}
		//recurse down
		if(currenty - 1 >= 0 && recurseDown == true){
			outline = deleteTracedShape(outline, currentx, currenty-1);
		}
		return outline;
	}
	
	//takes the angle between two pixels
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
			return Math.toDegrees(Math.atan((double) deltax / (double) deltay));
		}
		//quadrant 2
		else if(deltax < 0 && deltay > 0){
			return 360 + Math.toDegrees(Math.atan((double) deltax / (double) deltay));
		}
		//quadrant 3
		else if(deltax < 0 && deltay < 0){
			return 180 + Math.toDegrees(Math.atan((double) deltax / (double) deltay));
		}
		//quadrant 4
		else if(deltax > 0 && deltay < 0){
			return 180 + Math.toDegrees(Math.atan((double) deltax / (double) deltay));
		}
		else{
			System.out.println("fatal error");
			return -1;
		}
		
	}
	
	//normalizes an angle between 0 and 360
	private double normalizeAngle(double angle){
		while(angle > 360){
			angle -= 360;
		}
		while(angle < 0){
			angle += 360;
		}
		return angle;
	}
	
	//gets the smallest bearing between two angles
	private double getSmallestBearing(double angle1, double angle2){
		double leftBearing;
		double rightBearing;
		if(angle2 >= angle1){
			rightBearing = angle2 - angle1;
			leftBearing = 360 - angle2 + angle1;
		}
		else{
			rightBearing = angle1 - angle2;
			leftBearing = 360 - angle1 + angle2;
		}
		return Math.min(leftBearing, rightBearing);
	}
	
	//gets the next point in a shape outline
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
	
	//checks if a point on the outline image borders white
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
	
	//overlays the outline of the shapes on the original image
	private BufferedImage overlayShape(BufferedImage outline, BufferedImage original){
		BufferedImage overlay = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());
		//copy original image to overlay
		for (int x = 0; x < original.getWidth(); x++){
			for (int y = 0; y < original.getHeight(); y++){
				overlay.setRGB(x, y, original.getRGB(x, y));
			}
		}
		//if pixel in outline is not white, copy it to the overlay
		for (int x = 0; x < outline.getWidth(); x++){
			for (int y = 0; y < outline.getHeight(); y++){
				if(outline.getRGB(x, y) != Color.WHITE.getRGB()){
					overlay.setRGB(x, y, outline.getRGB(x, y));
				}
			}
		}
		return overlay;
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
	
	//kenny's code
	//Nested Arraylists: Shape >> Endpoints >> X,Y
	private Double[][] processShape(ArrayList<ArrayList<int[]>> inArray) {
		int len = inArray.size();
		int i = 0;
		Double[][] outArray = new Double[len][4];
		/// Identify: number of points, ratio of max-width:max-height, std dev of angles, average angle
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
	
	private double sqr(double in) {
		return in * in;
	}
	
	//calculates the shape data for an image
	public void calculateData(ArrayList<ArrayList<int[]>> allEndpoints){
		//if vertices were found
		if(allEndpoints != null && allEndpoints.size() > 0){
			Double[][] data = processShape(allEndpoints);
			//display values on GUI labels
			pointsLabel.setText("Number of Points: " + data[panel.getSelectedShape()][0]);
			ratioLabel.setText("Width/Height Ratio: " + data[panel.getSelectedShape()][1]);
			stddevLabel.setText("Angle Std Dev: " + data[panel.getSelectedShape()][2]);
			anglesLabel.setText("Angle Average: " + data[panel.getSelectedShape()][3]);
		}
		else{
			//reset values
			pointsLabel.setText("Number of Points: ");
			ratioLabel.setText("Width/Height Ratio: ");
			stddevLabel.setText("Angle Std Dev: ");
			anglesLabel.setText("Angle Average: ");
		}
	}
	
}
