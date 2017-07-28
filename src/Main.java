//i changed it!!
import java.awt.Color;
import java.util.Collections;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;


import javax.imageio.ImageIO;

public class Main {
	

	final int colorThreshold = 5;
	final int neighborMinThreshold = 5; //five or less or stack overflow 
	final int neighborMaxThreshold = 25;
	final int blockThresh = 22;
	final int differentiator = 2;
	private ArrayList<ArrayList<Integer>> slimeTrail = new ArrayList<ArrayList<Integer>>();
	
	public Main(){
		String shapeName = "triangle1";
		BufferedImage test = loadImage("./res/raw/" + shapeName + ".png");
		storeImage(highlightShape(findEdges((convertGrayscale(test))), test), "./res/processed/" + shapeName+ "_out.png");
		System.out.print("hello!");
		BufferedImage shape = highlightShape(findEdges(convertGrayscale(test)), test);
		ArrayList<ArrayList<Integer>> mine = findEndpoints((findEdges(convertGrayscale(test))));
		System.out.println("the list size is: " + mine.size());
		
		
		//shape.setRGB(536, 33, (new Color(255,0,0)).getRGB());
		for (int k = 0; k < slimeTrail.size(); k += 1){
			shape.setRGB(slimeTrail.get(k).get(0), slimeTrail.get(k).get(1), (new Color(255,0,0)).getRGB());
		}
		for (int i = 0; i < mine.size(); i ++){
			
			//System.out.println("x is:" + mine.get(i).get(0) + ", y is: " + mine.get(i).get(1));
			ArrayList temp = mine.get(i);
			for (int j = -2; j < 2; j ++) {
				for (int k = -2; k < 2; k ++) {
					shape.setRGB((int)temp.get(0), (int)temp.get(1), (new Color(0,255,0)).getRGB());
					if (i == mine.size() -1){
						//shape.setRGB((int)temp.get(0), (int)temp.get(1), (new Color(255,255,255)).getRGB());
					}
				}
			}
			System.out.println("X:" + temp.get(0) + " Y: " + temp.get(1));
		}
		storeImage(shape, "./res/processed/" + shapeName + "_out.png");
		ArrayList<ArrayList<ArrayList<Integer>>> myList = new ArrayList<ArrayList<ArrayList<Integer>>>();
		myList.add(mine);
		
		Double[][] my = processShape(myList);
		for (Double[] value: my){
			for (Double actualValue : value){
				System.out.println(actualValue);
			}
		}
		System.out.print("done!");
		ArrayList<ArrayList<ArrayList<Integer>>> tine = new ArrayList<>();
		tine.add(mine);
		Double[][] wow = processShape(tine);
		for (double ow: wow[0]) {
			System.out.println(ow);
		}
		 
		//storeImage(findEdges(convertGrayscale(test)), "./res/output.png");
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

	private Double[][] processShape(ArrayList<ArrayList<ArrayList<Integer>>> inArray) {
		int len = inArray.size();
		int i = 0;
		Double[][] outArray = new Double[len][3];
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
				angles[j] = Math.PI - (Math.atan(Math.abs(y2 - y) / Math.abs(x2 - x))
						+ Math.atan(Math.abs(y1 - y) / Math.abs(x1 - x)));
				sum += angles[j];
				j++;
			}
			double avg = sum / angles.length;
			double sqDiffSum = 0;
			for (double angle: angles) {
				System.out.println("angle: "+angle);
				sqDiffSum += (avg - angle) * (avg - angle);
			}
			double r = (maxX - minX) / (maxY - minY);
			double sqDiffMean = sqDiffSum / angles.length;
			double std = Math.sqrt(sqDiffMean);

			outArray[i][0] = (double) shape.size();
			outArray[i][1] = r;
			outArray[i][2] = std;
			i++;
		}
		return outArray;
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
			firstPoint.remove(0);
			firstPoint.remove(0);
			firstPoint.add(234);
			firstPoint.add(200);
			
			ArrayList<ArrayList<Integer>> thePoints = tryToFindEndHelp(firstPoint, blackLines, 0.0, new LinkedList<ArrayList<Double>>(), true, new ArrayList<ArrayList<Integer>>(), firstPoint, -1);
			System.out.println("thePoints size is: " + thePoints.size());
			thePoints = endPointPreciser(thePoints, blackLines);
			return thePoints;
		}
		
		
	}

	private ArrayList<ArrayList<Integer>> tryToFindEndHelp(ArrayList<Integer> currentCoord, BufferedImage blackLines, double average, LinkedList<ArrayList<Double>> history, boolean first, ArrayList<ArrayList<Integer>> masterList, ArrayList<Integer> firstPoint, int currentDir){ //this is recursive
		//ends if hits the edge of screen OR can't find colored pixel
		
		//current coord is the next point along the edge. searching for the next point
		slimeTrail.add(currentCoord);
		int x = currentCoord.get(0);
		int y = currentCoord.get(1);
		int nextX = -1;
		int nextY = -1;
		
		int maxListNumber = findAllPointsOnShapes(blackLines).size()/80; 
		if (maxListNumber < 50){
			maxListNumber = 50;
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
			
			returnME = tryToFindEndHelp(newCoords, blackLines, average, history, false, masterList, firstPoint, dir);
			
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
	
	private ArrayList findAllPointsOnShapes(BufferedImage blackLines){
		
		int picWidth = blackLines.getWidth();
		int picHeight = blackLines.getHeight();
		Color red = Color.RED;
		ArrayList<ArrayList> listAll = new ArrayList<ArrayList>();
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
	private ArrayList<BufferedImage> cropToBlock(BufferedImage input) {
		ArrayList<BufferedImage> out = new ArrayList<BufferedImage>();
		List<int[]> blocks = new ArrayList<>();
		int[][] numSim = new int[input.getHeight()][input.getWidth()];
		int numBlocks = 0;
		for(int x = 2; x < input.getWidth() - 2; x ++ ) {
			for(int y = 2; y < input.getHeight() - 2; y ++) {
				int numSimilar = 25;
				for(int i = -2; i < 3; i ++) {
					Color thisColor = new Color(input.getRGB(x, y));
					//System.out.println("Checking x:"+(x+i));
					for (int j = -2; j < 3; j ++) {
						//System.out.println("Checking y:"+(y+j));
						Color thatColor = new Color(input.getRGB(x + i, y + j));
						if(checkSimilarity(thisColor,thatColor)){
							numSimilar --;
						}
					}
				}
				numSim[y][x] = numSimilar;
				//System.out.print(numSimilar+",");
			}
			//System.out.println("");
		}
		for(int[] o : numSim) {
			for(int x : o) {
				System.out.print(x + " ");
			}
			System.out.println("");
		}
		System.out.println("done");
		ArrayList<Integer> miXArray = new ArrayList<>();
		ArrayList<Integer> miYArray = new ArrayList<>();
		ArrayList<Integer> maXArray = new ArrayList<>();
		ArrayList<Integer> maYArray = new ArrayList<>();
		for(int i = 2; i < numSim.length-2; i ++) {
			for(int j = 2; j < numSim[i].length-2; j ++) {
				int current = numSim[i][j];
				Boolean withinShape = false;
				for(int k = 0; k < miXArray.size(); k ++) {
					withinShape = (
							i <= maXArray.get(k)
							&& i >= miXArray.get(k) 
							&& j <= maYArray.get(k) 
							&& j >= miYArray.get(k)
							);
					 if(withinShape) {
						 break;
					 }
				}
				if(withinShape) {
					 continue;
				 }
				//System.out.println("Current is "+current);
				if(current < blockThresh - differentiator) {
					ArrayList<int[]> edges = new ArrayList<>();
					ArrayList<int[]> tested = new ArrayList<>();
					System.out.println("Testing "+i+","+j);
					edges = tailCheck4(numSim,i,j,edges,tested,0);
					ArrayList<Integer> xs = new ArrayList<>();
					ArrayList<Integer> ys = new ArrayList<>();
					for(int[] e : edges) {
						xs.add(e[0]);
						ys.add(e[1]);
					}
					int minX = Collections.min(xs);
					int maxX = Collections.max(xs);
					int minY = Collections.min(ys);
					int maxY = Collections.max(ys);
					//System.out.println("Finished Checking "+i+","+j);
					if(!(minX == maxX && minY==maxY)) {
						System.out.println(minX+","+minY+","+maxX+","+maxY);
						maXArray.add(maxX);
						miXArray.add(minX);
						maYArray.add(maxY);
						miYArray.add(minY);
						int rectx = minX-10 >= 0 ? minX-10 : 0, 
								recty = minY-10 >= 0 ? minY-10 : 0, 
										rectX = maxX-minX+10 < numSim.length - 1? maxX-minX+10 : numSim.length - 1, 
												rectY = maxY-minY+10 < numSim.length - 1? maxY-minY+10 : numSim.length - 1;
						out.add(input.getSubimage(recty,rectx,rectY,rectX));
					}
				}
			}
		}
		return out;
	}
	private ArrayList<int[]> tailCheck4(int[][] SimilarityArray, int StartX, int StartY, ArrayList<int[]> edgeArray,ArrayList<int[]> testedArray,int Iteration) {
		int[] CurrentPoint = {StartX, StartY};
		/*for(int[] o : testedArray) {
			for(int i : o) {
				System.out.print(i+" ");
			}
			System.out.println("has been tested");
		}*/
		Iteration ++;
		//System.out.println("Iteration"+Iteration);
		if(!ALContainsArray(testedArray,CurrentPoint)) {
			//System.out.println("Testing ("+StartX+","+StartY+")"+" which is: "+SimilarityArray[StartX][StartY]);
			testedArray.add(CurrentPoint);
			if(SimilarityArray[StartX+1][StartY] > blockThresh) {
				edgeArray = tailCheck4(SimilarityArray,StartX+1,StartY,edgeArray,testedArray, Iteration);
			} else if(!ALContainsArray(edgeArray,CurrentPoint)) {
				edgeArray.add(CurrentPoint);
			}
			if(SimilarityArray[StartX][StartY+1] > blockThresh) {
				edgeArray = tailCheck4(SimilarityArray,StartX,StartY+1,edgeArray,testedArray, Iteration);
			} else if(!ALContainsArray(edgeArray,CurrentPoint)) {
				edgeArray.add(CurrentPoint);
			}
			if(SimilarityArray[StartX-1][StartY] > blockThresh) {
				edgeArray = tailCheck4(SimilarityArray,StartX-1,StartY,edgeArray,testedArray, Iteration);
			} else if(!ALContainsArray(edgeArray,CurrentPoint)) {
				edgeArray.add(CurrentPoint);
			}
			if(SimilarityArray[StartX][StartY-1] > blockThresh) {
				edgeArray = tailCheck4(SimilarityArray,StartX,StartY-1,edgeArray,testedArray, Iteration);
			} else if(!ALContainsArray(edgeArray,CurrentPoint)) {
				edgeArray.add(CurrentPoint);
			}
		}
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
