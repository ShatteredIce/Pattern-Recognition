import java.util.ArrayList;
import java.util.Random;

public class Neuron {
	
	static Random random = new Random();
	
	int numInputs;
	final double rate = 0.001;
	
	double[] inputs;
	double[] weights;
	double lastOutput;
	double[] previousWeightDeltas;
	double biasWeight = -1;
	double momentumWeight = 0;
	
	public Neuron(int newInputs){
		numInputs = newInputs;
		inputs = new double[numInputs];
		weights = new double[numInputs];
		previousWeightDeltas = new double[numInputs];
		//initializes random weights between -2.0 to 2.0
		for(int i = 0; i < numInputs; i++){
			weights[i] = 2 * random.nextDouble() - 1;
		}	
	}
	
	public void setInputs(double[] data){
		for (int i = 0; i < inputs.length; i++) {
			inputs[i] = data[i];
			
		}
	}
	
	public double calculateOutput(){
		double total = 0;
		for(int i = 0; i < numInputs; i++){
			total += inputs[i] * weights[i];
		}
		total += 1 * biasWeight;
		total = sigmoidFunction(total);
		lastOutput = total;
		
		return total;
	}
	
	public double updateErrorLastLayer(double trueValue, double error){
		double residual = trueValue - lastOutput;
		error += residual * (lastOutput * (1 - lastOutput));
		return error;
		
//		double[] previousLayerContribution = new double[weights.length];
//		for (int i = 0; i < weights.length; i++) {
//			previousLayerContribution[i] = weights[i];
//		}
//		for (int i = 0; i < previousLayerContribution.length; i++) {
//			previousLayerContribution[i] *= delta;
//		}
//		return previousLayerContribution;
		
	}
	
//	//pass in a double array that contains the results for each training case
//	public void updateWeightsLastLayer(double[][] inputValues, double[] expectedValues, double[] actualValues){
//		double[] deltaWeights = new double[numInputs];
//		for (int i = 0; i < expectedValues.length; i++) {
//			double slope = actualValues[i] * (1 - actualValues[i]);
//			double residual = expectedValues[i] - actualValues[i];
//			for (int j = 0; j < numInputs; j++) {
//				deltaWeights[j] += inputValues[i][j] * slope * residual;
//			}
//			
//		}
//		for (int w = 0; w < weights.length; w++) {
//			weights[w] += rate * deltaWeights[w];
//		}
//	}
	
	
	public double calculateResidual(double truevalue){
		return truevalue - lastOutput;
	}
	
	public double[] updateWeights(double contribution){
		double error = contribution * (lastOutput * (1 - lastOutput));
		
		//calculate previous layer contribution
		double[] previousLayerContribution = new double[weights.length];
		for (int i = 0; i < weights.length; i++) {
			previousLayerContribution[i] = weights[i];
		}
		for (int i = 0; i < previousLayerContribution.length; i++) {
			previousLayerContribution[i] *= error;
		}
		
		//update actual weights
		for(int i = 0; i < weights.length; i++){
			weights[i] += (rate * error * inputs[i]) + (previousWeightDeltas[i] * momentumWeight);
			previousWeightDeltas[i] = rate * error * inputs[i];
		}
		biasWeight += rate * error;
		
		return previousLayerContribution;
		
	}
	
	public double sigmoidFunction(double x){
		return (1/ (1 + Math.exp(-x)));
	}
	
	public void printWeights(){
		for (int i = 0; i < weights.length; i++) {
			System.out.println(weights[i]);
		}
	}
		

}
