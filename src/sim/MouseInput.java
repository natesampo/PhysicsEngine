package sim;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseInput implements MouseListener, MouseMotionListener {
	Simulation simulation;
	public void mouseClicked(MouseEvent e) {
		
	}

	public void mouseEntered(MouseEvent e) {
		
	}

	public void mouseExited(MouseEvent e) {
		
	}

	public void mousePressed(MouseEvent e) {
		int mouseX = e.getX();
		int mouseY = e.getY();
		this.simulation.mousePressed(mouseX, mouseY);
	}

	public void mouseReleased(MouseEvent e) {
		int mouseX = e.getX();
		int mouseY = e.getY();
		this.simulation.mouseReleased(mouseX, mouseY);
	}
	
	public MouseInput(Simulation simulation) {
		this.simulation = simulation;
	}

	public void mouseDragged(MouseEvent e) {
		int mouseX = e.getX();
		int mouseY = e.getY();
		this.simulation.mouseDragged(mouseX, mouseY);
	}

	public void mouseMoved(MouseEvent e) {
		
	}
}
