package narnia;

public class QLearningSample {

	BallPosition[] oldState;
	Ball oldPlayer;
	int action;
	double reward;
	//BallPosition[] newState;
	Ball newPlayer;
	
	public QLearningSample(BallPosition[] oldState, Ball oldPlayer, int action, double reward,  Ball newPlayer) {
		this.oldState = oldState;
		this.oldPlayer = oldPlayer;
		this.action = action;
		this.reward = reward;
		//this.newState = newState;
		this.newPlayer = newPlayer;
	}
}
