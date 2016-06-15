package narnia.util;

import narnia.Utils;
import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.persist.EncogDirectoryPersistence;

import java.io.File;

/**
 * Created by Radosław on 15.06.2016.
 */
public class GameNetwork {

    public static int NUMBER_OF_INPUT_NEURON = 21;
    public static int NUMBER_OF_OUTPUT_NEURON = 3;

    private static GameNetwork gameNetwork;
    private BasicNetwork network;
    private TrainingContinuation tc = null;

    private static final boolean readFromFile = true;

    private GameNetwork(){
        if(readFromFile){
            loadNetwork("localNet");
        }else {
            network = new BasicNetwork();
            network.addLayer(new BasicLayer(null, true, NUMBER_OF_INPUT_NEURON));
            network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 80));
            network.addLayer(new BasicLayer(new ActivationLinear(), false, NUMBER_OF_OUTPUT_NEURON));
            network.getStructure().finalizeStructure();
            network.reset();
        }
    }

    public static GameNetwork getInstance() {
        if(gameNetwork==null){
            gameNetwork = new GameNetwork();
        }
        return gameNetwork;
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

    public void train(GameState gameState, double[] out){
        double[][] nout = {out};
        double[][] input = {Utils.getInputByState(gameState)};
        train(input,nout);
    }

    public double[] predict(double[] input) {
        MLData data = new BasicMLData(input);
        MLData output = network.compute(data);
        System.out.println();
        return output.getData();
    }

    public void saveNetwork(String filename) {
        EncogDirectoryPersistence.saveObject(new File(filename), network);
    }

    public void loadNetwork(String filename) {
        network = (BasicNetwork)EncogDirectoryPersistence.loadObject(new File(filename));
    }

}
