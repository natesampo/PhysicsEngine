package sim;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Body {
	Polygon shape;
	ArrayList<Point2D> vertex;
	
	public void tick() {
		
	}
	
	public void render(Graphics g) {
		g.setColor(Color.black);
		g.fillPolygon(this.shape);
	}
	
	public Body(ArrayList<Point2D> vertex) {
		this.vertex = vertex;
		this.shape = new Polygon();
		for(int i=0;i<this.vertex.size();i++) {
			this.shape.addPoint((int) this.vertex.get(i).getX(), (int) this.vertex.get(i).getY());
		}
	}
}
