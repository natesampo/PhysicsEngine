package sim;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Body {
	Polygon shape;
	ArrayList<Point2D> vertex;
	Simulation simulation;
	public boolean chosen;
	public int layer;

	public void tick() {
		
	}
	
	public void render(Graphics g) {
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
	
	public Body(ArrayList<Point2D> vertex, int layer, Simulation simulation) {
		this.layer = layer;
		this.vertex = vertex;
		this.simulation = simulation;
		this.shape = new Polygon();
		for(int i=0;i<this.vertex.size();i++) {
			this.shape.addPoint((int) this.vertex.get(i).getX(), (int) this.vertex.get(i).getY());
		}
	}
}
