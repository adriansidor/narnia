package narnia;

import narnia.utils.MoveType;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Radosław on 14.06.2016.
 */
public class GameNetwork {

    public static final int INPUT_NEURON_NUMBER = 16;
    public static final int OUTPUT_NEURON_NUMBER = 3;
    private static GameNetwork gameNetwork;

    private static final boolean READ_FROM_FILE = false;

    private NeuralNetwork neuralNetwork;

    private GameNetwork(){
        if(READ_FROM_FILE){
            try {
                neuralNetwork = NeuralNetwork.load(new FileInputStream("per.nnet"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else {
            neuralNetwork = new MultiLayerPerceptron(16, 150, 50, 3);
            neuralNetwork.setLearningRule(new BackPropagation());
        }
    }

    public static GameNetwork getInstance() {
        if (gameNetwork == null) {
            gameNetwork = new GameNetwork();
        }
        return gameNetwork;
    }

    public double[] getOut(){
        return neuralNetwork.getOutput();
    }

    public void learn(DataSet dataSet){
        neuralNetwork.learn(dataSet);
    }

    public double[] predictByInput(GameState gameState, MoveType moveType){
        double[] in = Utils.getInputByBallAndPositionVector(moveType,gameState);
        neuralNetwork.setInput(in);
        neuralNetwork.calculate();
        return neuralNetwork.getOutput();
    }

    public MoveType getMoveByState(GameState gameState){
        double[] input = Utils.getInputByBallAndPositionVector(MoveType.NIL,gameState);
        neuralNetwork.setInput(input);
        neuralNetwork.calculate();
        double [] out = neuralNetwork.getOutput();
        return movByOutPut(out);
    }

    private MoveType movByOutPut(double[] out) {
        if(out[0]==out[1]&&out[1]==out[2]){
            return Utils.getRandomMove();
        }else {
            MoveType result = MoveType.UP;
            if(out[1]>out[0]){
                result = MoveType.DO_NOT_MOVE;
            }else if(out[2]>out[1]){
                result = MoveType.DOWN;
            }
            return result;
        }
    }

    public void save(){
        neuralNetwork.save("per.nnet");
    }
}
