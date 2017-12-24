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
	public static final int WIDTH = 1024, HEIGHT = 768; // Window width and height
	public int select = 0; // Which input mode is the user currently using
	public double gravity = 0.5; // Gravity (negative delta Y velocity) per simulation tick
	public double drag = 0.5; // Drag per tick (depends on motion of object)
	public int mouseX, mouseY, startX, startY, endX, endY, lastStartX, lastStartY;
	public Polygon shape;
	public int chosen = -1; // Which object is currently selected, represented by index of object in objects list (-1 being unselected)
	ArrayList<Point2D> v2 = new ArrayList<Point2D>();
	ArrayList<Point2D> mouseDelta; // How far has the mouse moved in the last tick
	MouseInput mouseInput; // How we get mouse input data
	KeyInput keyInput; // How we get key input data
	Body tempObject;
	LinkedList<Body> objects = new LinkedList<Body>(); // List containing every object
	private Thread thread;
	
	public void mousePressed(int mouseX, int mouseY) {
		
		// startX and startY record where the mouse was initially pressed, and won't change while mouse is dragged
		startX = mouseX;
		startY = mouseY;
		
		// mouseX and mouseY will represent current mouse position
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		
		pressed = true;
		
		/* Change behavior depending on select state
		 * Select 0 = Clicking and dragging will select and move objects (Clicking on multiple overlapping objects will select the one with the highest layer)
		 * Select 1 = Dragging will create rectangles
		 * Select 2 = Clicking will create vertices of polygons, 'Enter' will create that polygon
		 */
		switch(select) {
			case 0: int highestLayer = -1;
					int highestLayerIndex = -1;
					chosen = -1; // Deselect any previously selected object
					for(int i=0;i<objects.size();i++) { // Loop through every object
						objects.get(i).chosen = false; // Make sure every object is set as not the selected object
						if(objects.get(i).checkSelect(this.mouseX, this.mouseY, highestLayer)) {
							highestLayer = objects.get(i).layer;
							highestLayerIndex = i; // If the object is clicked and has a higher layer than the last object, set a new highestLayer and store the index of that object
						}
					}
					if(highestLayerIndex != -1) { // If you clicked on any object
						objects.get(highestLayerIndex).chosen = true; // Set that object as the chosen object
						chosen = highestLayerIndex;
						mouseDelta = new ArrayList<Point2D>();
						for(int i=0;i<objects.get(highestLayerIndex).vertex.size();i++) {
							mouseDelta.add(new Point2D.Double(objects.get(highestLayerIndex).vertex.get(i).getX()-this.mouseX, objects.get(highestLayerIndex).vertex.get(i).getY()-this.mouseY));
						} // mouseDelta makes sure that when a polygon is dragged, every vertex moves the same amount simultaneously
					}
		}
	}
	
	public void mouseReleased(int mouseX, int mouseY) {
		// The opposite of startX and Y, endX and endY represent where the mouse was released and where dragging ceased
		this.endX = mouseX;
		this.endY = mouseY;
		
		/* Change behavior depending on select state
		 * Select 0 = Clicking and dragging will select and move objects (Clicking on multiple overlapping objects will select the one with the highest layer)
		 * Select 1 = Dragging will create rectangles
		 * Select 2 = Clicking will create vertices of polygons, 'Enter' will create that polygon
		 */
		switch(select) {
			case 0: if(this.chosen > -1) { // If an object is selected, the user is throwing an object, so move the object depending on the speed of the cursor in the last tick
				objects.get(this.select).velX = this.endX-this.lastStartX;
				objects.get(this.select).velY = this.endY-this.lastStartY;
			} break;
			case 1: if(this.endX != this.startX && this.endY != this.startY && pressed) { // The user is drawing a rectangle
						ArrayList<Point2D> v1 = new ArrayList<Point2D>();
						v1.add(new Point2D.Double(this.startX, this.startY));
						v1.add(new Point2D.Double(this.startX, this.endY));
						v1.add(new Point2D.Double(this.endX, this.endY));
						v1.add(new Point2D.Double(this.endX, this.startY));
						this.addObject(new Body(v1, 1, this)); // Add a new rectangle with vertices determined by the start and end coordinates
					} break;
			case 2: v2.add(new Point2D.Double(this.endX, this.endY)); break; //The user is creating a polygon, so add a new vertex
		}
		this.mouseX = this.startX;
		this.mouseY = this.startY;
		if(select!=2) { // If not creating a polygon, the mouse is no longer pressed
			pressed = false;
		}
	}
	
	public void keyPressed(int key) {
		if(key==10) { // Enter key
			switch(select) {
				case 2: if(v2.size() > 2) { // If user is creating a polygon, check for how many vertices they have
							addObject(new Body(v2, 1, this));
						} else { // User cannot create a line or a point
							System.out.println("Must have at least 3 points to make a shape");
						}
						v2 = new ArrayList<Point2D>(); break;
			}
		} else if(key==8) { // Backspace key
			switch(select) {
				case 2: if(v2.size() >= 1) { // Remove the last vertex
							v2.remove(v2.size()-1);
						}
			}
		} else if(key>=48 && key<=57) { // Number keys 0-9
			if(select != key-48) { // Make sure user is actually switching to a new setting and not staying on the same one
				select = key-48; // Set the input setting to the user's choice
				v2 = new ArrayList<Point2D>(); // Reset vertices for polygon drawing
				pressed = false;
			}
		}
	}
	
	public void mouseDragged(int mouseX, int mouseY) {
		//mouseX and mouseY represent current mouse position
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		
		for(int i=0;i<objects.size();i++) {
			if(objects.get(i).chosen) { // Go through every object and find the currently selected one
				for(int j=0;j<objects.get(i).vertex.size();j++) {
					objects.get(i).vertex.set(j, new Point2D.Double(this.mouseX + this.mouseDelta.get(j).getX(), this.mouseY + this.mouseDelta.get(j).getY()));
				} // Move the selected one according to how far the mouse moved in that tick
				
				// Store the last starting mouse coordinates
				this.lastStartX = this.startX;
				this.lastStartY = this.startY;
				
				//These are now the startX and startY coordinates
				this.startX = this.mouseX;
				this.startY = this.mouseY;
			}
		}
	}

	public void tick() {
		for(int i=0;i<objects.size();i++) {
			tempObject = objects.get(i);
			tempObject.tick(); // Go through every object and do their actions for every simulation tick
		}
	}
	
	public void render() { // This is where graphics take place
		
		// Set the buffer strategy to 3
		BufferStrategy bs = this.getBufferStrategy();
		if(bs==null) {
			this.createBufferStrategy(3);
			return;
		}
		
		// Drawing using the Graphics library
		Graphics g = bs.getDrawGraphics();
		
		//Fill screen with color cyan
		g.setColor(Color.cyan);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		for(int i=0;i<objects.size();i++) {
			Body tempObject = objects.get(i);
			tempObject.render(g); // Call the render method for every object
		}
		
		if(pressed) { // If mouse is currently pressed, or user is making vertices in the polygon creator tool
			g.setColor(Color.red);
			switch(select) {
				// Display the temporary rectangle in the rectangle create tool
				case 1: g.fillRect(Math.min(this.startX, this.mouseX), Math.min(this.startY, this.mouseY), Math.abs(this.mouseX - this.startX), Math.abs(this.mouseY - this.startY)); break;
				
				// Display the temporary polygon in the polygon create tool
				case 2: shape = new Polygon();
						for(int i=0;i<v2.size();i++) {
							g.fillOval((int) v2.get(i).getX() - 5, (int) v2.get(i).getY() - 5, 10, 10);
							shape.addPoint((int) v2.get(i).getX(), (int) v2.get(i).getY());
						}
						g.fillPolygon(shape); break;
			}
		}
		
		g.dispose();
		bs.show(); // Display everything drawn
	}
	
	public void run() { // This is the main loop of the program
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0; // Set ticks per second to 60
        double ns = 1000000000 / amountOfTicks; // Determine number of nanoseconds per tick
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        while(running) {
        	long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while(delta >=1) { // When delta in nanoseconds is above one tick, tick once and subtract the time for one tick
            	tick();
            	delta--;
            }
            render(); // Always render everything, regardless of whether or not a tick happened
            frames++; // Counts number of frames passed
            if(System.currentTimeMillis() - timer > 1000) { // Records frames per second, or speed of the simulation every 1000 ticks
            	timer += 1000;
            	//System.out.println("FPS: "+ frames);
            	frames = 0;
            }
        }
        stop();
    }
	
	public void addObject(Body obj) { // Appends an object to the list of objects
		boolean added = false;
		for(int i=0;i<objects.size();i++) {
			if(objects.get(i).layer > obj.layer) { // Sorts objects by layer
				objects.add(i, obj);
				added = true;
				break;
			}
		}
		if(!added) { // If object was the highest layer, append to end of list
			objects.add(obj);
		}
	}
	
	public void removeObject(Body obj) { // Removes object from list of all objects
		objects.remove(obj);
	}
	
	public Simulation() {
		new Window(WIDTH, HEIGHT, "Sim", this); // Create the window at specified size
		
		// Setup key and mouse inputs
		keyInput = new KeyInput(this);
		mouseInput = new MouseInput(this);
		this.addMouseListener(mouseInput);
		this.addKeyListener(keyInput);
		this.addMouseMotionListener(mouseInput);
	}
	
	public synchronized void start() { // Starts the thread and main loop
		thread = new Thread(this);
		thread.start();
		running = true;
	}
	
	public synchronized void stop() { // Stops the thread and main loop
		try {
			thread.join();
			running = false;
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) { // On program run, create a Simulation object
		new Simulation();
	}
}