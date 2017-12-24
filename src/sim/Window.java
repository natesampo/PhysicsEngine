package sim;

import java.awt.Canvas;
import java.awt.Dimension;

import javax.swing.JFrame;

public class Window extends Canvas {

	private static final long serialVersionUID = -7975505597069675943L;
	
	public Window(int width, int height, String title, Simulation simulation) { // Window constructor
		
		// Create new JFrame window with title being passed as an argument
		JFrame frame = new JFrame(title);
		
		// Set JFrame size settings
		frame.setPreferredSize(new Dimension(width, height));
		frame.setMaximumSize(new Dimension(width, height));
		frame.setMinimumSize(new Dimension(width, height));
		
		// Set JFrame options
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.add(simulation);
		frame.setVisible(true);
		simulation.start();
	}
}