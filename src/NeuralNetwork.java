import java.util.ArrayList;
import java.util.Random;

public class NeuralNetwork {
	
	final Random random = new Random();
	final int rawInputs = 2;
	int[] neuronsInLayer = {3, 1};
	ArrayList<Neuron[]> layers = new ArrayList<>();
	
	double[][] trainingData = {{1, 1}, {1, 0}, {0, 1}, {0, 0}};
	double[][] trainingAnswers = {{0}, {1}, {1}, {0}};
	
//	double[][] trainingData = {{5, 0},{4,1},{0,3},{0,2},{1,2},{0,0}};
//	double[][] trainingAnswers = {{1},{1},{0},{0},{1},{0}};
	
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
	
	public void run(double[] input){
		calculate(input);
		displayResult();
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
				backpropagate(currentLayer-1, lastLayerContribution);
			}
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
				layerOutput = new double[layers.get(i).length];
				layerOutput[j] = n.calculateOutput();
			}
		}
		return layerOutput;
	}
	
	public void displayResult(){
		int lastLayer = layers.size() - 1;
		System.out.println("Last Layer Results: ");
		for (int i = 0; i < layers.get(lastLayer).length; i++) {
			Neuron n = layers.get(lastLayer)[i];
			System.out.println("Neuron " + i + ": " + n.lastOutput);
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

}
