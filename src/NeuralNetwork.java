import java.util.ArrayList;
import java.util.Random;

public class NeuralNetwork {
	
	final Random random = new Random();
	final int rawInputs = 3;
	int[] neuronsInLayer = {4, 4, 3};
	ArrayList<Neuron[]> layers = new ArrayList<>();
	
	double[][] trainingData = {{5, 1, 1}, {7, 6, 1}, {3, 0, 2}, {5, 7, 5},
			{2, 6, 1}, {0, 4, 3}, {1, 1, 4}, {7, 2, 8}, {6, 8, 9},
			{7, 6, 2}, {1, 5, 4}, {0, 8, 9},
			{2, 2, 2}, {7, 7, 7}, {0, 0, 0}, {5, 5, 4}, {3, 1, 3},
			{2, 6, 6}, {9, 9, 5}, {6, 2, 6}, {1, 3, 3}, {6, 6, 2}, 
			{8, 7, 8}, {3, 4, 4}, {7, 7, 0}, {6, 1, 6}, {0, 9, 9},
			{6, 6, 0}, {4, 3, 4}, {5, 8, 8}};
	
	double[][] trainingAnswers = {{1, 0, 0}, {1, 0, 0}, {1, 0, 0}, {0, 1, 0},
			{0, 1, 0}, {0, 1, 0}, {0, 0, 1}, {0, 0, 1}, {0, 0, 1}, {1, 0, 0}, {0, 1, 0}, {0, 0, 1}, 
			{0.33, 0.33, 0.33}, {0.33, 0.33, 0.33}, {0.33, 0.33, 0.33}, {0.5, 0.5, 0}, {0.5, 0, 0.5},
			{0, 0.5, 0.5}, {0.5, 0.5, 0}, {0.5, 0, 0.5}, {0, 0.5, 0.5},
			{0.5, 0.5, 0}, {0.5, 0, 0.5}, {0, 0.5, 0.5}, {0.5, 0.5, 0}, {0.5, 0, 0.5}, 
			{0, 0.5, 0.5}, {0.5, 0.5, 0}, {0.5, 0, 0.5}, {0, 0.5, 0.5}};
	
	
//	final int trainingSetSize = 100;
//	final int trainingMax = 100;
//	double[][] trainingData = new double[trainingSetSize][rawInputs];
//	double[][] trainingAnswers = new double[trainingSetSize][neuronsInLayer[1]];

	
	public NeuralNetwork(){
		
		//populate layers of neurons
		for(int i = 0; i < neuronsInLayer.length; i++){
			Neuron[] layer = new Neuron[neuronsInLayer[i]];
			int inputsPerLayer = (i == 0) ? rawInputs : neuronsInLayer[i - 1];
			for(int j = 0; j < neuronsInLayer[i]; j++){
				layer[j] = new Neuron(inputsPerLayer);
			}
			layers.add(layer);
		}
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
	
	public void run(double[] input){
		calculate(input);
		displayResult(true);
	}
	
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
