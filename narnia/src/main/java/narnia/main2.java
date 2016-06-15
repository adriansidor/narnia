package narnia;

import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.neuroph.nnet.MultiLayerPerceptron;

public class main2 {

	public static void main(String[] args) {
		double[][] input ={ {1.0,1.0,0.0,0.0,0.0,0.0,1.0,1.0,0.0,0.0,0.0,0.0} };
		   double[][]	output ={ {1.0,0.0,0.0} };
		   NeuralNetwork network = NeuralNetwork.getInstance();
		   network.train(input, output);
		   double[] result = network.predict(input[0]);
		   System.out.println("Predicted");
		   for(double a :result) {
			   System.out.println(a);
		   }

	}

}
