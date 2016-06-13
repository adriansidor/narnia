package narnia;

import narnia.q_learning.TimeStatistic;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

/**
 * The control logic and main display panel for game.
 */
public class BallWorld extends JPanel {
    private static final int UPDATE_RATE = 800;  // Frames per second (fps)


    //private Ball ball;         // A single bouncing Ball's instance

    private int numberOfGames;
    private int n = 6;
    private LinkedList<Ball> balls = new LinkedList<Ball>();
    private Ball player;
    private ContainerBox box;  // The container rectangular box
    private boolean collision = false;
    private DrawCanvas canvas; // Custom canvas for drawing the box/ball
    private int canvasWidth;
    private int canvasHeight;

    /**
     * Constructor to create the UI components and init the game objects.
     * Set the drawing canvas to fill the screen (given its width and height).
     *
     * @param width  : screen width
     * @param height : screen height
     */
    public BallWorld(int width, int height, int numberOfGames) {

        canvasWidth = width;
        canvasHeight = height;
        this.numberOfGames = numberOfGames;

        // Init the Container Box to fill the screen
        box = new ContainerBox(0, 0, canvasWidth, canvasHeight, Color.BLACK, Color.WHITE);

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
        int speed = 10;
        int angleInDegree = rand.nextInt(360);
        player = new Ball(radius - 100, radius, radius, speed, angleInDegree,
                Color.RED, new NewQLearning());
        balls = new LinkedList<Ball>();
        collision = false;
    }

    /**
     * Start the ball bouncing.
     */
    public void gameStart() {
        // Run the game logic in its own thread.


        final java.util.List<TimeStatistic> timeStatistics = new ArrayList<TimeStatistic>();
        Thread gameThread = new Thread() {
            public void run() {
                for (int i = 0; i < numberOfGames; i++) {
                    long begin = System.currentTimeMillis();
                    while (!collision) {

                        // Execute one time-step for the game
                        gameUpdate();
                        // Refresh the display
                        repaint();
                        // Delay and give other thread a chance
                        try {
                            Thread.sleep(1000 / UPDATE_RATE);
                        } catch (InterruptedException ex) {
                        }

                    }
                    long end = System.currentTimeMillis();
                    reset();
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

    private Ball generateBall(int radius, int speed) {
        Random rand = new Random();
        int y = rand.nextInt(canvasHeight - radius * 2 - 20) + radius + 10;
        int direction = rand.nextInt(2);
        int angleInDegree = 0;
        switch (direction) {
            case 0:
                angleInDegree = 120;
                break;
            case 1:
                angleInDegree = 240;
                break;
        }
        Ball ball = new Ball((box.maxX + radius), y, radius, speed,
                angleInDegree, Color.BLUE, new IceFloeMoveDriver());
        return (ball);
    }

    private void addBall(LinkedList<Ball> balls, int n, int radius, int speed) {
        if (balls.size() < n) {
            try {
                Ball lastBall = balls.getLast();
                float distance = box.maxX / n;
                System.out.println(distance);
                float edge = lastBall.x + lastBall.radius;
                if (edge <= (box.maxX - distance)) {
                    balls.add(generateBall(radius, speed));
                }

            } catch (NoSuchElementException e) {
                balls.add(generateBall(radius, speed));
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
     * One game time-step.
     * Update the game objects, with proper collision detection and response.
     */
    public void gameUpdate() {
        addBall(balls, n, 40, 10);
        System.out.println(balls.size());
        moveBalls(balls);
        BallPosition[] positionVector = positionVector(balls, n);
        System.out.println("Ball position");
        for (BallPosition position : positionVector) {
            System.out.println(position);
        }
        player.moveOneStepWithCollisionDetection(box, positionVector);
        if (detectCollision(player, balls)) {
            collision = true;
        }
        removeBall(balls);
    }

    public void drawLines(Graphics g) {
        for (int i = 1; i < n; i++) {
            g.drawLine(i * box.maxX / n, 0, i * box.maxX / n, box.maxY);
        }
    }

    /**
     * The custom drawing panel for the bouncing ball (inner class).
     */
    class DrawCanvas extends JPanel {
        /**
         * Custom drawing codes
         */
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);    // Paint background
            // Draw the box and the ball
            box.draw(g);
            for (Ball ball : balls) {
                ball.draw(g);
            }

            player.draw(g);
            drawLines(g);
        }


        /**
         * Called back to get the preferred size of the component.
         */
        @Override
        public Dimension getPreferredSize() {
            return (new Dimension(canvasWidth, canvasHeight));
        }
    }
}