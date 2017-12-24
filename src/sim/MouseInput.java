package sim;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseInput implements MouseListener, MouseMotionListener {
	Simulation simulation;
	
	// All of the methods called by the MouseListener and MouseMotionListener. The ones being used are passed to the Simulation object as well as mouse position
	public void mouseClicked(MouseEvent e) {
		
	}

	public void mouseEntered(MouseEvent e) {
		
	}

	public void mouseExited(MouseEvent e) {
		
	}

	public void mousePressed(MouseEvent e) {
		int mouseX = e.getX();
		int mouseY = e.getY();
		simulation.mousePressed(mouseX, mouseY);
	}

	public void mouseReleased(MouseEvent e) {
		int mouseX = e.getX();
		int mouseY = e.getY();
		simulation.mouseReleased(mouseX, mouseY);
	}
	
	public MouseInput(Simulation simulation) { // mouseInput constructor stores the simulation so that it can call those methods
		this.simulation = simulation;
	}

	public void mouseDragged(MouseEvent e) {
		int mouseX = e.getX();
		int mouseY = e.getY();
		simulation.mouseDragged(mouseX, mouseY);
	}

	public void mouseMoved(MouseEvent e) {
		
	}
}
