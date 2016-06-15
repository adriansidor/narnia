package narnia.util;

import narnia.Utils;
import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;

/**
 * Created by Radosław on 15.06.2016.
 */
public class GameNetwork {

    public static int NUMBER_OF_INPUT_NEURON = 18;
    public static int NUMBER_OF_OUTPUT_NEURON = 3;

    private static GameNetwork gameNetwork;
    private BasicNetwork network;



    private GameNetwork(){
        this.network = new BasicNetwork();
        this.network.addLayer( new BasicLayer(null,true,NUMBER_OF_INPUT_NEURON));
        this.network.addLayer(new BasicLayer(new ActivationSigmoid(),true,75));
        this.network.addLayer(new BasicLayer(new ActivationLinear(),true,NUMBER_OF_OUTPUT_NEURON));
        this.network.getStructure().finalizeStructure();
        this.network.reset();
    }

    public static GameNetwork getInstance() {
        if(gameNetwork==null){
            gameNetwork = new GameNetwork();
        }
        return gameNetwork;
    }

    private double predict(GameState gameState){
        double [] input = Utils.getInputByState(gameState);
        MLData mlData = new BasicMLData(input);
//        this.network.compute(mlData);
        return this.network.winner(mlData);
    }
}
