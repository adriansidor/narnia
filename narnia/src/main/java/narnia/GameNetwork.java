package narnia;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;

/**
 * Created by Radosław on 14.06.2016.
 */
public class GameNetwork {

    private static GameNetwork gameNetwork;

    private NeuralNetwork neuralNetwork;

    private GameNetwork(){
        neuralNetwork = new MultiLayerPerceptron(16,150,50,3);
        neuralNetwork.setLearningRule(new BackPropagation());
    }

    public static GameNetwork getInstance() {
        if (gameNetwork == null) {
            gameNetwork = new GameNetwork();
        }
        return gameNetwork;
    }
}
