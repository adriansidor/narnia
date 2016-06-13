package narnia;

import javax.swing.JFrame;

/**
 * Main Program for running the bouncing ball as a standalone application.
 */
public class Main {
	// Entry main program
	public static void main(String[] args) {
		// Run UI in the Event Dispatcher Thread (EDT), instead of Main thread
		JFrame frame = new JFrame("A World of Balls");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(new BallWorld(940, 380, 2)); // BallWorld is a JPanel
		frame.pack(); // Preferred size of BallWorld
		frame.setVisible(true); // Show it
	}
}
