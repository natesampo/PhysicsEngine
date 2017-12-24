package sim;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Body {
	Polygon shape; // The polygon of the Body, used for collision detection and drawing
	ArrayList<Point2D> vertex;
	ArrayList<ArrayList<Double>> forces; // A list of forces acting on the object
	ArrayList<Double> drag, f; // Two forces that will act on the object
	Simulation simulation;
	public double velX, velY, mass, restitution; // Variables that will be used and stored by the object
	public boolean chosen; // Is this object the currently selected one?
	public int layer, collide; // The layer and collision ID of the object

	public void tick() {
		this.collide = checkCollision(); // Check if this object collides with any others
		if(this.collide > -1) { // If there is a collision, print the collision
			System.out.println(this.collide);
		} else { // If no collision, set X and Y velocities based on forces acting on the object
			this.drag.set(0, -this.velX * this.simulation.drag);
			this.drag.set(1, -this.velY * this.simulation.drag);
			for(int i=0;i<forces.size();i++) {
				this.velX += this.forces.get(i).get(0)/this.mass;
				this.velY += this.forces.get(i).get(1)/this.mass;
			}
			for(int i=0;i<this.vertex.size();i++) { // Move every vertex of the object based on the X and Y velocities
				this.vertex.set(i, new Point2D.Double(this.vertex.get(i).getX()+this.velX,this.vertex.get(i).getY()+this.velY));
			}
		}
	}
	
	public void render(Graphics g) { // How the object is drawn on screen
		this.shape = new Polygon(); // Shape will represent every vertex of the object in a list
		for(int i=0;i<this.vertex.size();i++) {
			this.shape.addPoint((int) this.vertex.get(i).getX(), (int) this.vertex.get(i).getY());
		}
		if(this.chosen) { // If this is the currently selected object, draw it in yellow
			g.setColor(Color.yellow);
		} else { // If not selected, draw in black
			g.setColor(Color.black);
		}
		g.fillPolygon(this.shape);
	}
	
	public boolean checkSelect(int mouseX, int mouseY, int layerCheck) { // This method checks if the object is currently selected or not
		if(this.shape.contains(mouseX, mouseY) && layer>layerCheck) {
			return true;
		} else {
			return false;
		}
	}
	
	public int checkCollision() { // This method checks if the object collides with any other objects
		for(int i=0;i<this.simulation.objects.size();i++) { 
			if(this.simulation.objects.get(i) == this) {
				continue;
			}
			for(int v=0;v<this.simulation.objects.get(i).vertex.size();v++) {
				if(this.shape.contains(this.simulation.objects.get(i).vertex.get(v).getX(), this.simulation.objects.get(i).vertex.get(v).getY())) {
					return i; // If there is a collision, return the index of the object it collided with
				}
			}
		}
		return -1; // If no collision, return -1 (no collision)
	}
	
	public Body(ArrayList<Point2D> vertex, int layer, Simulation simulation) {
		
		// setting default variables for all objects
		// Add arguments in constructor to be able to change these depending on the object
		this.collide = -1;
		this.velX = 0;
		this.velY = 0;
		this.mass = 5;
		this.drag = new ArrayList<Double>();
		this.drag.add(0.0);
		this.drag.add(0.0);
		this.forces = new ArrayList<ArrayList<Double>>();
		this.forces.add(drag);
		this.layer = layer;
		this.vertex = vertex;
		this.simulation = simulation;
	}
}
