package sim;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyInput implements KeyListener {
	Simulation simulation;
	int key;
	
	// When a key is pressed, call the method within Simulation and pass the key index of the key that was pressed
	public void keyPressed(KeyEvent e) {
		key = e.getKeyCode();
		simulation.keyPressed(key);
	}

	public void keyReleased(KeyEvent e) {
		
	}

	public void keyTyped(KeyEvent e) {
		
	}
	
	public KeyInput(Simulation simulation) { // Constructor sets simulation so that we can call that method when a key is pressed
		this.simulation = simulation;
	}

}
