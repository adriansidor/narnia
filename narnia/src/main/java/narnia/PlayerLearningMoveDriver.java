package narnia;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayerLearningMoveDriver implements BallMoveDriver {
	static double epsilon = 1;
	static double epochs = 1000;
	double gamma = 0.8;

	public void move(Ball ball, ContainerBox box, BallPosition[] positionVector) {
		float ballMinX = box.minX + ball.radius;
		float ballMinY = box.minY + ball.radius;
		float ballMaxX = box.maxX - ball.radius;
		float ballMaxY = box.maxY - ball.radius;
		
		//double epsilon = 1;
		//double gamma = 0.8;
		int action = 0;
		NeuralNetwork network = NeuralNetwork.getInstance();
		double[] vector = computeVector(ball, positionVector);
		double[] qval =  network.predict(vector);
		Random rand = new Random();
		double random = rand.nextDouble();
		if (random < epsilon) {
			//choose random action
			action = rand.nextInt(3);
		} else {
			//choose best action from Q(s,a) values
			action = argmax(qval);
		}
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
		//Reward
		double reward = 1.0;
		boolean collision = false;
		for(BallPosition position : positionVector) {
			if(position != null) {
				if(ball.detectCollision(position)) {
					reward = -100.0;
					collision = true;
				}
			}
		}
		//get maxQ from next state
		vector = computeVector(ball, positionVector);
		double[] newQ = network.predict(vector);
		double newQMax = maxQ(newQ);
		double update = 0;
		if(collision) {
			update = reward;
			if (epsilon > 0.1)
		        epsilon -= (1/epochs);
			epochs--;
			System.out.println("Epoch " + epochs);
			System.out.println("Epsilon " + epsilon);
			System.out.println("Kolizja");
		} else {
			update = reward + gamma*newQMax;
		}
		//train model
		qval[action] = update;
		double[][] newInput = new double[1][vector.length];
		newInput[0] = vector;
		double[][] newOutput = new double[1][qval.length];
		newOutput[0] = qval;
		network.train(newInput, newOutput);
		
	}
	
	private double maxQ(double [] newQ) {
		double max = newQ[0];
		for(int i = 1; i<newQ.length; i++) {
			if(newQ[i]>max) {
				max = newQ[i];
			}
		}
		return max;
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
