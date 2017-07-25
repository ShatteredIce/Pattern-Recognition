import java.util.Random;

public class Neuron {
	
	static Random random = new Random();
	
	final int numInputs = 3;
	
	double[] inputs = new double[numInputs];
	double[] weights = new double[numInputs];
	double lastOutput;
	
	public Neuron(){
		//initializes random weights between -2.0 to 2.0
		for(int i = 0; i < numInputs; i++){
			weights[i] = 4 * (random.nextDouble() - 0.5);
		}
	}
	
	public void setInputs(double[] data){
		inputs = data;
	}
	
	public double calculateOutput(){
		double total = 0;
		for(int i = 0; i < numInputs; i++){
			total += inputs[i] * weights[i];
		}
		total = sigmoidFunction(total);
		lastOutput = total;
		return total;
	}
	
	public void updateWeights(double trueValue){
		double error = trueValue - lastOutput;
		double delta = error * (lastOutput * 1 - lastOutput);
		for(int i = 0; i < numInputs; i++){
			weights[i] += inputs[i] * delta;
		}
		
	}
	
	public double sigmoidFunction(double x){
		return (1/ 1 + Math.exp(-x));
	}
	
	

}
