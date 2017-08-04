import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;

public class NeuralNetwork {
	
	final Random random = new Random();
	final int rawInputs = 4;
	int[] neuronsInLayer = {4, 4, 3};
	ArrayList<Neuron[]> layers = new ArrayList<>();
	
//	double[][] trainingData = {{3, 0.863, 0.007, 1.047}, {3, 1.158, 0.003, 1.047}, {3, 1.571, 0.201, 1.047}, 
//		{3, 0.864, 0.005, 1.047}, {3, 1.366, 0.404, 1.047}, {3, 1.988, 0.432, 1.047}, {3, 1.355, 1.224, 1.047},
//		{4, 1, 0.002, 1.571}, {4, 1, 0.002, 1.571}, {4, 3.075, 0.009, 1.571}, {4, 0.841, 0.004, 1.571}, {4, 0.821, 0.004, 1.571}, {4, 0.554, 0, 1.571}}; 
//	
//	//triangle, square, rectangle
//	double[][] trainingAnswers = {{1, 0, 0}, {1, 0, 0}, {1, 0, 0}, {1, 0, 0}, {1, 0, 0}, {1, 0, 0}, {1, 0, 0}, 
//			{0, 1, 0}, {0, 1, 0}, {0, 0, 1}, {0, 0, 1}, {0, 0, 1}, {0, 0, 1}};

	final int trainingSetSize = 12;
	double[][] trainingData = new double[trainingSetSize][rawInputs];
	double[][] trainingAnswers = new double[trainingSetSize][neuronsInLayer[neuronsInLayer.length - 1]];

	
	public NeuralNetwork(){
		loadTrainingData(trainingSetSize);

//		//populate layers of neurons
//		for(int i = 0; i < neuronsInLayer.length; i++){
//			Neuron[] layer = new Neuron[neuronsInLayer[i]];
//			int inputsPerLayer = (i == 0) ? rawInputs : neuronsInLayer[i - 1];
//			for(int j = 0; j < neuronsInLayer[i]; j++){
//				layer[j] = new Neuron(inputsPerLayer);
//			}
//			layers.add(layer);
//		}
		//displayWeights();
	}
	
	public void generateTrainingData(){
		//fill training data with random numbers
//		for(double[] tD : trainingData) {
//			tD[0] = random.nextInt(trainingMax);
//			tD[1] = random.nextInt(trainingMax);
//			tD[2] = random.nextInt(trainingMax);
//		}
//		for(int i = 0; i < trainingSetSize; i ++) {
//			double max = Integer.MIN_VALUE;
//			int[] highestIndex = new int[3];
//			int k = 0;
//			int l = 0;
//			int added = 0;
//			for(double td : trainingData[i]) {
//				if(td > max) {
//					max = td;
//					if(added == 0)	highestIndex[l] = k;
//				} else if(td == max){
//					l++;
//					highestIndex[l] = k;
//				}
//				k++;
//			}
//			System.out.println(max);
//			System.out.println(highestIndex);
//			for(int j= 0; j < 3; j ++) {
//				trainingAnswers[i][j] = (j == highestIndex) ? 1 : 0;
//			}
//		}
	}
	
	public void loadTrainingData(int numData){
		//load training data
		BufferedReader input;
		try {
			input = new BufferedReader(new FileReader("./data/trainingdata.txt"));
			for (int i = 0; i < numData; i++) {
				try {
					String current = input.readLine();
					StringTokenizer st = new StringTokenizer(current, ", ");
				    for (int j = 0; j < rawInputs; j++) {
				    	trainingData[i][j] = Double.parseDouble(st.nextToken());
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//load training answers
		try {
			input = new BufferedReader(new FileReader("./data/traininganswers.txt"));
			for (int i = 0; i < numData; i++) {
				try {
					String current = input.readLine();
					StringTokenizer st = new StringTokenizer(current, ", ");
				    for (int j = 0; j < neuronsInLayer[neuronsInLayer.length - 1]; j++) {
				    	trainingAnswers[i][j] = Double.parseDouble(st.nextToken());
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run(double[] input){
		calculate(input);
		displayResult(true);
	}
	
	//train the neural net using the training data
	public void train(int trainingIndex){
		int lastLayer = layers.size() - 1;
		double[][] lastLayerContribution = new double[layers.get(lastLayer).length][];
		for (int i = 0; i < layers.get(lastLayer).length; i++) {
			Neuron n = layers.get(lastLayer)[i];
			double[] currentSet = trainingData[trainingIndex];
			//System.out.println(currentSet[0] + " " + currentSet[1] + " " + currentSet[2]);
			double[] resultArray = calculate(currentSet);
			double[] correctAnswers = trainingAnswers[trainingIndex];
			lastLayerContribution[i] = n.updateWeights(n.calculateResidual(correctAnswers[i]));
		}
		backpropagate(lastLayer-1, lastLayerContribution);
	}
	
	public void backpropagate(int currentLayer, double[][] layerContribution){
		if(currentLayer < 0){
			return;
		}
		//update weights on other layers using contribution from previous layer
		else{
			double[][] lastLayerContribution = new double[layers.get(currentLayer).length][];
			for (int i = 0; i < layers.get(currentLayer).length; i++) {
				Neuron n = layers.get(currentLayer)[i];
				double totalContribution = 0;
				for (int j = 0; j < layerContribution.length; j++) {
					totalContribution += layerContribution[j][i];
				}
				lastLayerContribution[i] = n.updateWeights(totalContribution);
			}
			backpropagate(currentLayer-1, lastLayerContribution);
		}
		
	}
	
	//calculate the output of the neural net
	public double[] calculate(double[] data){
		double[] layerOutput = null;
		for(int i = 0; i < layers.size(); i++){
			for(int j = 0; j < layers.get(i).length; j++){
				Neuron n = layers.get(i)[j];
				//pass raw inputs into first layer of neurons
				if(i == 0){
					n.setInputs(data);
				}
				//pass previous layer's output into subsequent layers
				else{
					n.setInputs(layerOutput);
				}
			}
			layerOutput = new double[layers.get(i).length];
			for(int j = 0; j < layers.get(i).length; j++){
				layerOutput[j] = layers.get(i)[j].calculateOutput();
			}
		}
		return layerOutput;
	}
	
	//print out the result from the last layer of neurons
	public void displayResult(boolean formatted){
		int lastLayer = layers.size() - 1;
		System.out.println("Last Layer Results: ");
		if(formatted) System.out.println("(formatted output)");
		for (int i = 0; i < layers.get(lastLayer).length; i++) {
			Neuron n = layers.get(lastLayer)[i];
			if(formatted){
				System.out.println("Neuron " + i + ": " + Math.round(n.lastOutput*1000000)/10000d);
			}
			else{
				System.out.println("Neuron " + i + ": " + n.lastOutput);
			}
		}
		System.out.println();
	}
	
	//print out weights of each neuron in the network
	public void displayWeights(){
		for (int i = 0; i < layers.size(); i++) {
			for (int j = 0; j < layers.get(i).length; j++) {
				System.out.println("Neuron " + i + "-" + j +": ");
				layers.get(i)[j].printWeights();
				
			}
			System.out.println();
		}
	}
	
	public int getNumInputs(){
		return rawInputs;
	}

}
