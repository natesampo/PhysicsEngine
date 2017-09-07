package sim;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.LinkedList;

public class Game extends Canvas implements Runnable {
	private static final long serialVersionUID = -6748925586576993018L;
	public boolean running;
	public static final int WIDTH = 1024, HEIGHT = 768;
	public double gravity = 0.5;
	public int mouseX, mouseY;
	MouseInput mouseInput;
	LinkedList<Body> objects = new LinkedList<Body>();
	private Thread thread;
	
	public void mousePressed(int mouseX, int mouseY) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;
	}
	
	public void mouseReleased(int mouseX, int mouseY) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;
	}
	
	public void mouseDragged(int mouseX, int mouseY) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;
	}

	public void tick() {
		for(int i=0;i<objects.size();i++) {
			Body tempObject = objects.get(i);
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
            	System.out.println("FPS: "+ frames);
            	frames = 0;
            }
        }
        stop();
    }
	
	public void addObject(Body obj) {
		objects.add(obj);
	}
	
	public void removeObject(Body obj) {
		objects.remove(obj);
	}
	
	public Game() {
		new Window(WIDTH, HEIGHT, "Game", this);
		mouseInput = new MouseInput(this);
		this.addMouseListener(mouseInput);
		this.addMouseMotionListener(mouseInput);
		ArrayList<Point2D> v = new ArrayList<Point2D>();
		v.add(new Point2D.Double(0, 650));
		v.add(new Point2D.Double(0, 800));
		v.add(new Point2D.Double(1100, 800));
		v.add(new Point2D.Double(1100, 650));
		addObject(new Body(v));
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
		new Game();
	}
}