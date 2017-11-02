package sim;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Body {
	Polygon shape;
	ArrayList<Point2D> vertex;
	ArrayList<ArrayList<Double>> forces;
	ArrayList<Double> drag, f;
	Simulation simulation;
	public double velX, velY, mass, restitution;
	public boolean chosen;
	public int layer, collide;

	public void tick() {
		this.collide = this.checkCollision();
		if(this.collide > -1) {
			System.out.println(this.collide);
		} else {
			this.drag.set(0, -this.velX * this.simulation.drag);
			this.drag.set(1, -this.velY * this.simulation.drag);
			for(int i=0;i<forces.size();i++) {
				this.velX += this.forces.get(i).get(0)/this.mass;
				this.velY += this.forces.get(i).get(1)/this.mass;
			}
			for(int i=0;i<this.vertex.size();i++) {
				this.vertex.set(i, new Point2D.Double(this.vertex.get(i).getX()+this.velX,this.vertex.get(i).getY()+this.velY));
			}
		}
	}
	
	public void render(Graphics g) {
		this.shape = new Polygon();
		for(int i=0;i<this.vertex.size();i++) {
			this.shape.addPoint((int) this.vertex.get(i).getX(), (int) this.vertex.get(i).getY());
		}
		if(this.chosen) {
			g.setColor(Color.yellow);
		} else {
			g.setColor(Color.black);
		}
		g.fillPolygon(this.shape);
	}
	
	public boolean checkSelect(int mouseX, int mouseY, int layerCheck) {
		if(this.shape.contains(mouseX, mouseY) && layer>layerCheck) {
			return true;
		} else {
			return false;
		}
	}
	
	public int checkCollision() {
		for(int i=0;i<this.simulation.objects.size();i++) {
			if(this.simulation.objects.get(i) == this) {
				continue;
			}
			for(int v=0;v<this.simulation.objects.get(i).vertex.size();v++) {
				if(this.shape.contains(this.simulation.objects.get(i).vertex.get(v).getX(), this.simulation.objects.get(i).vertex.get(v).getY())) {
					return i;
				}
			}
		}
		return -1;
	}
	
	public Body(ArrayList<Point2D> vertex, int layer, Simulation simulation) {
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
