package narnia;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LoadNetworkMoveDriver implements BallMoveDriver{

	public void move(Ball ball, ContainerBox box, BallPosition[] positionVector) {
		NeuralNetwork.getInstance().loadNetwork("network_save");
		NeuralNetwork network = NeuralNetwork.getInstance();
		float ballMinX = box.minX + ball.radius;
		float ballMinY = box.minY + ball.radius;
		float ballMaxX = box.maxX - ball.radius;
		float ballMaxY = box.maxY - ball.radius;
		
		//double epsilon = 1;
		//double gamma = 0.8;
		int action = 0;
		double[] vector = computeVector(ball, positionVector);
		double[] qval =  network.predict(vector);
			//choose best action from Q(s,a) values
	    action = argmax(qval);
		//make a move
		switch(action) {
		case 0: ball.y +=ball.speedY;
				break;
		case 2: ball.y -=ball.speedY;
				break;
		//case 1 is not moving so we dont change ball.y
		}
		if (ball.x < ballMinX) {
			ball.speedX = -ball.speedX; // Reflect along normal
			ball.x = ballMinX; // Re-position the ball at the edge
		} else if (ball.x > ballMaxX) {
			ball.speedX = -ball.speedX;
			ball.x = ballMaxX;
		}
		if (ball.y < ballMinY) {
			//ball.speedY = -ball.speedY;
			ball.y = ballMinY;
		} else if (ball.y > ballMaxY) {
			//ball.speedY = -ball.speedY;
			ball.y = ballMaxY;
		}	
	}
	
	private int argmax(double[] qval) {
		int action = 0;
		double max = qval[0];
		for(int i = 1; i<qval.length; i++) {
			if(qval[i]>max) {
				max = qval[i];
				action = i;
			}
		}
		return action;
	}
	
	private double[] computeVector(Ball player, BallPosition[] positionVector) {
		int size = positionVector.length * 2;
		List<Double> list = new ArrayList<Double>();
		for(BallPosition ball : positionVector) {
			if(ball == null) {
				list.add(10000.0);
				list.add(10000.0);
			} else {
				list.add((double)(player.x-ball.getX()));
				list.add((double)(player.y-ball.getY()));
			}
		}
		double[] vector = new double[size];
		for(int i = 0; i<size; i++) {
			double v = list.get(i).doubleValue();
			vector[i] = v;
		}
		
		return vector;
	}


}
