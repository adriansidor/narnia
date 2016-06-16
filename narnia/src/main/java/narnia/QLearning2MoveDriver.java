package narnia;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class QLearning2MoveDriver implements BallMoveDriver{
	static double epsilon = 1;
	static double epochs = 6000;
	static double beta = 0.1;
	double gamma = 0.8;
	int samplesSize = 80;
	int t = 40;
	LinkedList<QLearningSample> samples = new LinkedList<QLearningSample>();

	public void move(Ball ball, ContainerBox box, BallPosition[] positionVector) {
		
		int action = 0;
		NeuralNetwork network = NeuralNetwork.getInstance();
		BallPosition pos = null;
		for(BallPosition ball2 : positionVector) {
			if(ball2 != null) {
				pos = ball2;
				break;
			}
		}
		//double[] vector = computeVector(ball, positionVector);
		double[] vector = computeVector2(ball, positionVector);
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
		Ball oldPlayer = new Ball(ball.x, ball.y, ball.radius, ball.speedX, ball.speedY, ball.driver);
		ball.moveBaseOnAction(action, box);
		//Reward
		double reward = 0.0;
		boolean topHalf = true;
		if(ball.y > (box.maxY/2)) {
			topHalf = false;
		}
		if(pos != null) {
			if(pos.getSpeedY() < 0) {
				if(!topHalf) {
					if(action == 0)
						reward = 5.0;
					if(action == 1)
						reward = -1.0;
					if(action == 2)
						reward = -1.0;
				} else {
					if(action == 0)
						reward = -1.0;
					if(action == 1)
						reward = -1.0;
					if(action == 2)
						reward = 5.0;
				}
			} else {
				if(topHalf) {
					if(action == 0)
						reward = -1.0;
					if(action == 1)
						reward = -1.0;
					if(action == 2)
						reward = 5.0;
				} else {
					if(action == 0)
						reward = 5.0;
					if(action == 1)
						reward = -1.0;
					if(action == 2)
						reward = -1.0;
				}

			}
		}

		boolean collision = false;
		for(BallPosition position : positionVector) {
			if(position != null) {
				if(ball.detectCollision(position)) {
					reward = -1000.0;
					collision = true;
				}
			}
		}
		
		QLearningSample sample = new QLearningSample(positionVector, oldPlayer, action, reward, ball);
		if(samples.size() < samplesSize) {
			samples.addLast(sample);
		} else {
			samples.removeFirst();
			samples.addLast(sample);
		}
		if(!samples.isEmpty()) {
			for(int i = 0; i<t; i++) {
				int a = rand.nextInt(samples.size());
				sample = samples.get(a);
				vector = computeVector2(sample.oldPlayer, sample.oldState);
				double[] old_qval = network.predict(vector);
				vector = computeVector2(sample.newPlayer, sample.oldState);
				double[] newQ = network.predict(vector);
				double newQMax = maxQ(newQ);
				double update = 0;
				boolean collision2 = false;
				for(BallPosition position : positionVector) {
					if(position != null) {
						if(ball.detectCollision(position)) {
							collision2 = true;
						}
					}
				}
				if(collision2) {
					newQMax = 0.0;
				}
				update = old_qval[sample.action] + beta*(sample.reward + gamma*newQMax-old_qval[sample.action]);
				old_qval[action] = update;
				double[][] newInput = new double[1][vector.length];
				newInput[0] = vector;
				double[][] newOutput = new double[1][qval.length];
				newOutput[0] = old_qval;
				network.train(newInput, newOutput);
			}
		}

		
		if(collision) {
			if (epsilon > 0.1)
		        epsilon -= (1/epochs);
			epochs--;
			System.out.println("Epoch " + epochs);
			System.out.println("Epsilon " + epsilon);
			System.out.println("Kolizja");
		}
		
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
	private double[] computeVector2(Ball player, BallPosition[] positionVector) {
		int size = 6;
		double[] vector = new double[size];
		int i = 0;
		for(BallPosition ball : positionVector) {
			if(ball == null) {
			} else {
				vector[i] = (double)(player.x-ball.getX());
				i++;
				vector[i] = (double)(player.y-ball.getY());
				i++;
			}
		}
		for(int a = i; a<size; a++) {
			vector[a] = -10.0;
		}
		return vector;
	}
}
