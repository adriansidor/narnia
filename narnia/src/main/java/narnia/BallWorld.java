package narnia;

import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Random;

import javax.swing.*;

/**
 * The control logic and main display panel for game.
 */
public class BallWorld extends JPanel {
	private static int UPDATE_RATE = 30000; // Frames per second (fps)

	boolean network_flag = false;
	// private Ball ball; // A single bouncing Ball's instance
	private String algorithm;
	private int numberOfGames;
	private int n = 12;
	private LinkedList<Ball> balls = new LinkedList<Ball>();
	private Ball player;
	private ContainerBox box; // The container rectangular box
	private boolean collision = false;
	private DrawCanvas canvas; // Custom canvas for drawing the box/ball
	private int canvasWidth;
	private int canvasHeight;

	/**
	 * Constructor to create the UI components and init the game objects. Set
	 * the drawing canvas to fill the screen (given its width and height).
	 * 
	 * @param width
	 *            : screen width
	 * @param height
	 *            : screen height
	 */
	public BallWorld(int width, int height, int numberOfGames, String algorithm, boolean save) {

		canvasWidth = width;
		canvasHeight = height;
		this.numberOfGames = numberOfGames;
		this.network_flag = save;
		this.algorithm = algorithm;
		// Init the Container Box to fill the screen
		box = new ContainerBox(0, 0, canvasWidth, canvasHeight, Color.BLACK,
				Color.WHITE);

		// Init the custom drawing panel for drawing the game
		canvas = new DrawCanvas();
		this.setLayout(new BorderLayout());
		this.add(canvas, BorderLayout.CENTER);

		// Handling window resize.
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				Component c = (Component) e.getSource();
				Dimension dim = c.getSize();
				canvasWidth = dim.width;
				canvasHeight = dim.height;
				// Adjust the bounds of the container to fill the window
				box.set(0, 0, canvasWidth, canvasHeight);
			}
		});

		// Start the ball bouncing
		reset();
		gameStart();
	}

	public void reset() {
		Random rand = new Random();
		int radius = 40;
		int x = rand.nextInt(canvasWidth - radius * 2 - 20) + radius + 10;
		int y = rand.nextInt(canvasHeight - radius * 2 - 20) + radius + 10;
		int speedY = 10;
		if(network_flag) {
			if(algorithm == "qlearning1") {
				player = new Ball(radius - 100, y, radius, 0, speedY,
						Color.RED, new PlayerLearningMoveDriver());
			}
			if(algorithm == "qlearning2") {
				player = new Ball(radius - 100, y, radius, 0, speedY,
						Color.RED, new PlayerLearningMoveDriver());
			}
				
		} else {
			UPDATE_RATE=30;
			player = new Ball(radius - 100, y, radius, 0, speedY,
			Color.RED, new LoadNetworkMoveDriver());
		}
		balls = new LinkedList<Ball>();
		collision = false;
	}

	/** Start the ball bouncing. */
	public void gameStart() {
      // Run the game logic in its own thread.
      Thread gameThread = new Thread() {
         public void run() {
        	 for(int i = 0; i<numberOfGames; i++) {
                 while (!collision) {
                     // Execute one time-step for the game 
                     gameUpdate();
                     // Refresh the display
                     repaint();
                     // Delay and give other thread a chance
                     try {
                        Thread.sleep(1000 / UPDATE_RATE);
                     } catch (InterruptedException ex) {}
                  }
                 reset();
        	 }
        	 if(network_flag) {
        		 NeuralNetwork.getInstance().saveNetwork("network_save");
        		 System.out.println("Zapisalem siec");
        	 }
        	 
         }
      };
      gameThread.start();  // Invoke GaemThread.run()
   }

	private void moveBalls(LinkedList<Ball> balls) {
		for (Ball ball : balls) {
			ball.moveOneStepWithCollisionDetection(box, null);
		}
	}

	private boolean detectCollision(Ball player, LinkedList<Ball> balls) {
		for (Ball ball : balls) {
			if (player.detectCollision(ball)) {
				return (true);
			}
		}
		return (false);
	}

	private void removeBall(LinkedList<Ball> balls) {
		try {
			Ball ball = balls.getFirst();
			float edge = ball.x + ball.radius;
			if (edge < box.minX) {
				balls.removeFirst();
			}
		} catch (NoSuchElementException e) {

		}
	}

	private Ball generateBall(int radius, float speedX, float speedY) {
		Random rand = new Random();
		int y = rand.nextInt(canvasHeight - radius * 2 - 20) + radius + 10;
		int direction = rand.nextInt(2);
		switch (direction) {
		case 0:
			speedY = -1*speedY;
			break;
		}
		Ball ball = new Ball((box.maxX + radius), y, radius, speedX,
				speedY, Color.BLUE, new IceFloeMoveDriver());
		return (ball);
	}

	private void addBall(LinkedList<Ball> balls, int n, int radius, float speedX, float speedY) {
		if (balls.size() < n) {
			try {
				Ball lastBall = balls.getLast();
				float distance = box.maxX / n;
				//System.out.println(distance);
				float edge = lastBall.x - lastBall.radius;
				if (edge <= (box.maxX - 4 * distance)) {
					balls.add(generateBall(radius, speedX, speedY));
				}

			} catch (NoSuchElementException e) {
				balls.add(generateBall(radius, speedX, speedY));
			}

		}
	}

	public BallPosition[] positionVector(LinkedList<Ball> balls, int n) {
		float distance = box.maxX / n;
		BallPosition[] vector = new BallPosition[n];
		for (int i = 0; i < n; i++) {
			vector[i] = null;
			float leftEdge = i * distance;
			float rightEdge = (i + 1) * distance;
			for (Ball ball : balls) {
				if (ball.x > leftEdge && ball.x < rightEdge) {
					vector[i] = new BallPosition(ball);
				}
			}
		}
		return vector;
	}

	/**
	 * One game time-step. Update the game objects, with proper collision
	 * detection and response.
	 */
	public void gameUpdate() {
		addBall(balls, n, 40, -10.0f, 3.0f);
		moveBalls(balls);
		BallPosition[] positionVector = positionVector(balls, n);
		player.moveOneStepWithCollisionDetection(box, positionVector);
		if (detectCollision(player, balls)) {
			collision = true;
		}
		removeBall(balls);
	}

	public void simpleTrain() {
		double[][] input = { { 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0,
				0.0, 0.0, 0.0 } };
		double[][] output = { { 1.0, 0.0, 0.0 } };
		NeuralNetwork network = NeuralNetwork.getInstance();
		network.train(input, output);
		double[] result = network.predict(input[0]);
		System.out.println("Predicted");
		for (double a : result) {
			System.out.println(a);
		}
	}

	public void drawLines(Graphics g) {
		for (int i = 1; i < n; i++) {
			g.drawLine(i * box.maxX / n, 0, i * box.maxX / n, box.maxY);
		}
	}

	/** The custom drawing panel for the bouncing ball (inner class). */
	class DrawCanvas extends JPanel {
		/** Custom drawing codes */
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g); // Paint background
			// Draw the box and the ball
			box.draw(g);
			for (Ball ball : balls) {
				ball.draw(g);
			}

			player.draw(g);
			drawLines(g);
		}

		/** Called back to get the preferred size of the component. */
		@Override
		public Dimension getPreferredSize() {
			return (new Dimension(canvasWidth, canvasHeight));
		}
	}
}