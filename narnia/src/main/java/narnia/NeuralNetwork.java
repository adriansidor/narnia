package narnia;

import java.io.File;

import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.persist.EncogDirectoryPersistence;

public class NeuralNetwork {

	private static NeuralNetwork instance;
	private BasicNetwork network;
	private TrainingContinuation tc = null;
	
	private NeuralNetwork() {
		network = new BasicNetwork();
		network.addLayer(new BasicLayer(null, true, 6));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 40));
		network.addLayer(new BasicLayer(new ActivationLinear(), false, 3));
		network.getStructure().finalizeStructure();
		network.reset();
		//System.out.println("layer neuron count " + network.getLayerNeuronCount(1));
	}
	
	public static NeuralNetwork getInstance() {
		if(instance == null) {
			System.out.println("NOWA SIEC");
			instance = new NeuralNetwork();
		}
		return instance;
	}
	
	public void reset() {
		network.reset();
	}
	
	public void train(double[][] input, double[][] output) {
		MLDataSet trainingSet = new BasicMLDataSet(input, output);
		MLTrain train = new ResilientPropagation(network, trainingSet);
		if(tc != null) {
			train.resume(tc);
		}
		train.iteration();
		tc = train.pause();
		//System.out.println("iteration " + train.getIteration());
		//System.out.println("error " + train.getError());
	}
	
	public double[] predict(double[] input) {
		MLData data = new BasicMLData(input);
		MLData output = network.compute(data);
		return output.getData();
	}
	
	public void saveNetwork(String filename) {
		EncogDirectoryPersistence.saveObject(new File(filename), network);
	}
	
	public void loadNetwork(String filename) {
		network = (BasicNetwork)EncogDirectoryPersistence.loadObject(new File(filename));
	}
}
