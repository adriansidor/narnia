package narnia.q_learning;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by Radosław on 13.06.2016.
 */
public class GameNetwork {

    private static final boolean READ_NETWORK_FROM_FILE = false;

    private static final int HIDEN_NEURON_NUMBER = 150;
    public static final int INPUT_NEURON_NUMBER = 16;
    public static final int OUTPUT_NEURON_NUMBER = 3;
    private NeuralNetwork neuralNetwork = new NeuralNetwork();

    private static GameNetwork gameNetwork;

    private GameNetwork(){
        if(READ_NETWORK_FROM_FILE){
            try {
                neuralNetwork = NeuralNetwork.load(new FileInputStream("perceptron.nnet"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else {
            this.neuralNetwork = new MultiLayerPerceptron(
                    INPUT_NEURON_NUMBER, HIDEN_NEURON_NUMBER, OUTPUT_NEURON_NUMBER);
            this.neuralNetwork.randomizeWeights();

        }
    }

    public static GameNetwork getInstance() {
        if(gameNetwork==null){
            gameNetwork = new GameNetwork();
        }
        return gameNetwork;
    }

    public void learn(DataSet dataSet){
        this.neuralNetwork.learn(dataSet);
    }

    public double[] getOutput(){
        return neuralNetwork.getOutput();
    }

    public double[] predict(double[] table){
        neuralNetwork.setInput(table);
        neuralNetwork.calculate();
        return neuralNetwork.getOutput();
    }

    public MoveType getMove(double[] table){
        double [] out = predict(table);
        MoveType max = MoveType.UP;
        if(out[1]>out[0]){
            max = MoveType.DO_NOT_MOVE;
        }if (out[2]>out[1]){
            max = MoveType.DOWN;
        }
        return max;
    }

    public void doSave() {
        neuralNetwork.save("perceptron.nnet");
    }
}
