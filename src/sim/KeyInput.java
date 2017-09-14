package sim;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyInput implements KeyListener {
	Simulation simulation;
	int key;
	
	public void keyPressed(KeyEvent e) {
		key = e.getKeyCode();
		this.simulation.keyPressed(key);
	}

	public void keyReleased(KeyEvent e) {
		
	}

	public void keyTyped(KeyEvent e) {
		
	}
	
	public KeyInput(Simulation simulation) {
		this.simulation = simulation;
	}

}
