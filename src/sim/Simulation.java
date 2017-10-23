package sim;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.LinkedList;

public class Simulation extends Canvas implements Runnable {
	private static final long serialVersionUID = -6748925586576993018L;
	public boolean running, pressed = false;
	public static final int WIDTH = 1024, HEIGHT = 768;
	public int select = 0;
	public double gravity = 0.5;
	public double drag = 0.5;
	public int mouseX, mouseY, startX, startY, endX, endY, lastStartX, lastStartY;
	public Polygon shape;
	public int chosen = -1;
	ArrayList<Point2D> v2 = new ArrayList<Point2D>();
	ArrayList<Point2D> mouseDelta;
	MouseInput mouseInput;
	KeyInput keyInput;
	Body tempObject;
	LinkedList<Body> objects = new LinkedList<Body>();
	private Thread thread;
	
	public void mousePressed(int mouseX, int mouseY) {
		this.startX = mouseX;
		this.startY = mouseY;
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		pressed = true;
		switch(select) {
			case 0: int highestLayer = -1;
					int highestLayerIndex = -1;
					this.chosen = -1;
					for(int i=0;i<objects.size();i++) {
						objects.get(i).chosen = false;
						if(objects.get(i).checkSelect(this.mouseX, this.mouseY, highestLayer)) {
							highestLayer = objects.get(i).layer;
							highestLayerIndex = i;
						}
					}
					if(highestLayerIndex != -1) {
						objects.get(highestLayerIndex).chosen = true;
						this.chosen = highestLayerIndex;
						this.mouseDelta = new ArrayList<Point2D>();
						for(int i=0;i<objects.get(highestLayerIndex).vertex.size();i++) {
							this.mouseDelta.add(new Point2D.Double(objects.get(highestLayerIndex).vertex.get(i).getX()-this.mouseX, objects.get(highestLayerIndex).vertex.get(i).getY()-this.mouseY));
						}
					}
		}
	}
	
	public void mouseReleased(int mouseX, int mouseY) {
		this.endX = mouseX;
		this.endY = mouseY;
		switch(select) {
			case 0: if(this.chosen > -1) {
				objects.get(this.select).velX = this.endX-this.lastStartX;
				objects.get(this.select).velY = this.endY-this.lastStartY;
			} break;
			case 1: if(this.endX != this.startX && this.endY != this.startY && pressed) {
						ArrayList<Point2D> v1 = new ArrayList<Point2D>();
						v1.add(new Point2D.Double(this.startX, this.startY));
						v1.add(new Point2D.Double(this.startX, this.endY));
						v1.add(new Point2D.Double(this.endX, this.endY));
						v1.add(new Point2D.Double(this.endX, this.startY));
						this.addObject(new Body(v1, 1, this));
					} break;
			case 2: v2.add(new Point2D.Double(this.endX, this.endY)); break;
		}
		this.mouseX = this.startX;
		this.mouseY = this.startY;
		if(select!=2) {
			pressed = false;
		}
	}
	
	public void keyPressed(int key) {
		if(key==10) {
			switch(select) {
				case 2: if(v2.size() > 2) {
							addObject(new Body(v2, 1, this));
						} else {
							System.out.println("Must have at least 3 points to make a shape");
						}
						v2 = new ArrayList<Point2D>(); break;
			}
		} else if(key==8) {
			switch(select) {
				case 2: if(v2.size() >= 1) {
							v2.remove(v2.size()-1);
						}
			}
		} else if(key>=48 && key<=57) {
			if(select != key-48) {
				select = key-48;
				v2 = new ArrayList<Point2D>();
				pressed = false;
			}
		}
	}
	
	public void mouseDragged(int mouseX, int mouseY) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		for(int i=0;i<objects.size();i++) {
			if(objects.get(i).chosen) {
				for(int j=0;j<objects.get(i).vertex.size();j++) {
					objects.get(i).vertex.set(j, new Point2D.Double(this.mouseX + this.mouseDelta.get(j).getX(), this.mouseY + this.mouseDelta.get(j).getY()));
				}
				//objects.get(i).velX = this.mouseX-this.startX;
				//objects.get(i).velY = this.mouseY-this.startY;
				this.lastStartX = this.startX;
				this.lastStartY = this.startY;
				this.startX = this.mouseX;
				this.startY = this.mouseY;
			}
		}
	}

	public void tick() {
		for(int i=0;i<objects.size();i++) {
			tempObject = objects.get(i);
			tempObject.tick();
		}
	}
	
	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if(bs==null) {
			this.createBufferStrategy(3);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		
		g.setColor(Color.cyan);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		for(int i=0;i<objects.size();i++) {
			Body tempObject = objects.get(i);
			tempObject.render(g);
		}
		
		if(pressed) {
			g.setColor(Color.red);
			switch(select) {
				case 1: g.fillRect(Math.min(this.startX, this.mouseX), Math.min(this.startY, this.mouseY), Math.abs(this.mouseX - this.startX), Math.abs(this.mouseY - this.startY)); break;
				case 2: shape = new Polygon();
						for(int i=0;i<v2.size();i++) {
							g.fillOval((int) v2.get(i).getX() - 5, (int) v2.get(i).getY() - 5, 10, 10);
							shape.addPoint((int) v2.get(i).getX(), (int) v2.get(i).getY());
						}
						g.fillPolygon(shape); break;
			}
		}
		
		g.dispose();
		bs.show();
	}
	
	public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        while(running) {
        	long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while(delta >=1) {
            	tick();
            	delta--;
            }
            render();
            frames++;
            if(System.currentTimeMillis() - timer > 1000) {
            	timer += 1000;
            	//System.out.println("FPS: "+ frames);
            	frames = 0;
            }
        }
        stop();
    }
	
	public void addObject(Body obj) {
		boolean added = false;
		for(int i=0;i<objects.size();i++) {
			if(objects.get(i).layer > obj.layer) {
				objects.add(i, obj);
				added = true;
				break;
			}
		}
		if(!added) {
			objects.add(obj);
		}
	}
	
	public void removeObject(Body obj) {
		objects.remove(obj);
	}
	
	public Simulation() {
		new Window(WIDTH, HEIGHT, "Sim", this);
		keyInput = new KeyInput(this);
		mouseInput = new MouseInput(this);
		this.addMouseListener(mouseInput);
		this.addKeyListener(keyInput);
		this.addMouseMotionListener(mouseInput);
		/*ArrayList<Point2D> v = new ArrayList<Point2D>();
		v.add(new Point2D.Double(0, 650));
		v.add(new Point2D.Double(0, 800));
		v.add(new Point2D.Double(1100, 800));
		v.add(new Point2D.Double(1100, 650));
		addObject(new Body(v, 1, this));*/
	}
	
	public synchronized void start() {
		thread = new Thread(this);
		thread.start();
		running = true;
	}
	
	public synchronized void stop() {
		try {
			thread.join();
			running = false;
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) {
		new Simulation();
	}
}