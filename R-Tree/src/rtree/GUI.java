package rtree;
import java.awt.Color;
import java.util.ArrayList;

import processing.core.PApplet;

public class GUI extends PApplet {


	public static ArrayList<Rectangle> drawRectangleList = new ArrayList<Rectangle>();
	Color [] myColor = { new Color(255, 0, 0), new Color(0, 255, 0), new Color(0, 0, 255), new Color(100,100,100), new Color(0, 250, 154), new Color(255,255,0), new Color(133, 95, 22), new Color(23, 95, 160)};
	RTree myTree = new RTree(3);

	String insert = "Insert";
	String search = "Search";
	String delete = "Delete";
	String enter = "Enter";
	String minX = "Min X";
	String minY = "Min Y";
	boolean insertClicked = false;
	boolean searchClicked = false;
	boolean deleteClicked = false;
	boolean enterClicked = false;
	boolean minXClicked = false;
	boolean minYClicked = false;
	boolean searchDone = false;
	boolean enterDone = false;
	Color regTextColor = new Color(250, 250, 250, 255);
	Color regButtonColor = new Color(20, 20, 20, 255);
	Color pressedColor = new Color(250, 50, 50, 255);
	Color eraseColor = new Color(255, 255, 255, 255);
	Color insertColor = new Color(1);
	Color searchColor = new Color(1);
	Color deleteColor = new Color(1);
	Color enterColor = new Color(1);
	Color minXColor = new Color(1);
	Color minYColor = new Color(1);
	Color contColor = new Color(0, 0, 0, 255);
	int panelX = 560;
	int insertY = 100;
	int searchY = 160;
	int deleteY = 260; 
	int enterY = 300;
	int panelMinY = 360;
	int minX_X = panelX;
	int minY_X = 680;
	int minButtonWidth = 80;
	int minButtonHeight = 80;
	int buttonWidth = 200;
	int buttonHeight = 50;
	int contX = 20;
	int contY = 20; 
	int contWidth = 500;
	int contHeight = 450;
	int pointCount = 0;

	// Points for rectangle
	Point p1; 
	Point p2;

	// Holds existing points (they are held in rectangle objects)
	private static ArrayList<Point> pointList = new ArrayList<Point> ();

	Rectangle searchSpace;	// Holds current search rectangle
	int searchRectX;
	int searchRectY;

	// Keeps track of current mode
	public static enum Mode {start, insert, delete, search, enter, minX, minY};
	Mode mode;

	// Setup works like a constructor. It initializes the window and necessary variables.
	public void setup() {
		mode = mode.start;
		// Window
		size(800, 500);
		background(250, 250, 250, 250);
	}

	// Draw is called continuously with each frame render. It redraws everything for each render.
	// The display changes as variables are updated.
	public void draw() {
		// Container
		background(190, 190, 190, 255);
		fill(240, 240, 240, 250);
		stroke(contColor.getRGB(), 255);
		strokeWeight(2);
		rect(20, 20, 500, 450);
		// Text
		textFont(createFont("Georgia", 20));
		textSize(20);

		if (!enterDone) {
			// Button Shapes
			buttonHeight = 80;
			insertY = 150;
			enterY = 280;
			fill(insertColor.getRGB(), 255);
			rect(panelX, insertY, buttonWidth, buttonHeight);
			fill(enterColor.getRGB(), 255);
			rect(panelX, enterY, buttonWidth, buttonHeight);
			// Button Text
			fill(240, 245, 245, 255);
			text(insert, panelX+70, insertY+47);
			text(enter, panelX+73, enterY+47);
		} else {
			// Button Shapes
			buttonHeight = 50;
			insertY = 60;
			fill(insertColor.getRGB(), 255);
			rect(panelX, insertY, buttonWidth, buttonHeight);
			fill(searchColor.getRGB(), 255);
			rect(panelX, searchY, buttonWidth, buttonHeight);
			fill(deleteColor.getRGB(), 255);
			rect(panelX, deleteY, buttonWidth, buttonHeight);
			fill(minXColor.getRGB(), 255);
			rect(minX_X, panelMinY, minButtonWidth, minButtonHeight);
			fill(minYColor.getRGB(), 255);
			rect(minY_X, panelMinY, minButtonWidth, minButtonHeight);
			// Button Text
			fill(240, 245, 245, 255);
			text(insert, panelX+70, insertY+30);
			text(search, panelX+70, searchY+30);
			text(delete, panelX+70, deleteY+30);
			text(minX, minX_X+12, panelMinY+45);
			text(minY, minY_X+12, panelMinY+45);
		}
		// If not searching, draw all points
		if (mode != mode.search && mode!=mode.minX && mode!=mode.minY) {
			// Draw Points
			for (Point p: pointList) {
				fill(0, 0, 15, 100);
				ellipse(p.x, p.y, 5, 5);
			}
			// Draw Rectangles
			for (Rectangle r: drawRectangleList){
				drawRect(r);
			}
		}

		// If searching, draw rectangles within selected region
		if (mode == mode.search && searchSpace != null) {
			if (contains(searchSpace.p1.x, searchSpace.p1.y, contX, contY, contWidth, contHeight)) {
				drawRect(searchSpace);
				ArrayList<Rectangle> drawPointList = myTree.search(searchSpace);
				for (Rectangle p: drawPointList) {
					fill(0, 0, 15, 100);
					ellipse(p.getP1().getX(), p.getP1().getY(), 5, 5);
				}
			}
		}
		if(mode == mode.minX) {
			Rectangle min = myTree.getMinimumX();
			fill(0,0,15,100);
			ellipse(min.getP1().getX(), min.getP1().getY(), 5, 5);
		}
		if(mode == mode.minY) {
			Rectangle min = myTree.getMinimumY();
			fill(0,0,15,100);
			ellipse(min.getP1().getX(), min.getP1().getY(), 5, 5);
		}
		// Set button pressed colors
		setPressedColors();
	}

	// Pre: Mouse is pressed
	// Post: Mode changed based on button press
	public void mousePressed() {
		if (insertClicked(mouseX, mouseY)) {
			mode = mode.insert;
		}
		if (!enterDone && enterClicked(mouseX, mouseY)) {
			mode = mode.enter;
		} 
		if (enterDone && searchClicked(mouseX, mouseY)) { 
			mode = mode.search;
		} 
		if (enterDone && deleteClicked(mouseX, mouseY)) {
			mode = mode.delete;
		} 
		if (enterDone && minXClicked(mouseX, mouseY)) {
			mode = mode.minX;
		} 
		if (enterDone && minYClicked(mouseX, mouseY)) {
			mode = mode.minY;
		} 

		// What to do based on mode
		modeBehavior(); 
	}	

	// Pre: Mouse must be pressed down
	// Post: Mouse released and colors reset
	public void mouseReleased() {
		insertColor = regButtonColor;
		searchColor = regButtonColor;
		deleteColor = regButtonColor;
		enterColor = regButtonColor;
		minXColor = regButtonColor;
		minYColor = regButtonColor;

		if (mode == mode.search) {
			searchDone = true;
		}
	}

	// Pre: Mouse pressed down
	// Post: Mouse dragged to change search box
	public void mouseDragged() {
		if (mode == mode.search) {	// Set bounds for search rectangle
			if (contains(mouseX, mouseY, contX, contY, contWidth, contHeight)) {
				if (searchRectX < mouseX) {
					if (searchRectY > mouseY) {
						Point p1 = new Point(searchRectX, searchRectY);
						Point p2 = new Point(mouseX, mouseY);
						searchSpace = new Rectangle(p1, p2, 0, null,null);
					} else {
						Point p1 = new Point(searchRectX, mouseY);
						Point p2 = new Point(mouseX, searchRectY);
						searchSpace = new Rectangle(p1, p2, 0, null,null);
					}
				} else if (searchRectX > mouseX) {
					if (searchRectY > mouseY) {
						Point p1 = new Point(mouseX, searchRectY);
						Point p2 = new Point(searchRectX, mouseY);
						searchSpace = new Rectangle(p1, p2, 0, null,null);
					} else {
						Point p1 = new Point(mouseX, mouseY);
						Point p2 = new Point(searchRectX, searchRectY);
						searchSpace = new Rectangle(p1, p2, 0, null,null);
					}
				} 
			}
		}
	}

	// Pre: Mode must be changed from start (default mode)
	// Post: Mode changes implemented
	private void modeBehavior() {	
		switch (mode)	{
		case insert:
			if (contains(mouseX, mouseY, contX, contY, contWidth, contHeight))
				if (!pointExists (mouseX, mouseY)) {
					p1 = new Point(mouseX, mouseY);
					pointList.add(p1);
					if(enterDone) {
						myTree.insert(new Rectangle(p1,p1,0,null,null));
						drawRectangleList = myTree.getRTree();
						drawRectangleList();
					}
				}
			break;
		case delete:
			if (pointExists (mouseX, mouseY)) {
				for (int i = 0; i < pointList.size(); i++) {
					Point point = pointList.get(i);
					if (Math.pow(mouseX - pointList.get(i).x,2) + Math.pow(mouseY - pointList.get(i).y,2) <= 25) {
						for (int j = 0; j < drawRectangleList.size(); j++) {
							Rectangle rect = drawRectangleList.get(j);
							if (rect.isPoint()&&rect.getP1().getX()==point.x&&rect.getP2().getY()==point.y)  { 
								pointList.remove(point);
								myTree.delete(rect);
								drawRectangleList = myTree.getRTree();
							}	
						}
					}
				}
			}
			break;
		case search:
			searchRectX = 0;		// Reset search rectangle values
			searchRectY = 0;
			if (contains(mouseX, mouseY, contX, contY, contWidth, contHeight)) {
				searchRectX = mouseX;
				searchRectY = mouseY;
			}
			searchDone = false;
			break;
		case enter:
			enterDone = true;
			ArrayList<Rectangle> tempArray = new ArrayList<Rectangle>();
			for(Point p: pointList){
				tempArray.add(new Rectangle(p,p,0,null,null));
			}
			drawRectangleList = myTree.makeRTree(tempArray,1);
			drawRectangleList();
			break;
		}
	}

	// Pre: Undrawn rectangle within R-Tree
	// Post: Rectangle drawn to screen
	public void drawRect(Rectangle r) {
		noFill();
		int x = r.p1.x;
		int y = r.p1.y;
		int w = r.p2.x - r.p1.x;
		int h = r.p2.y - r.p1.y;
		stroke(myColor[r.getDepth()].getRGB());	
		rect(x, y, w, h);

	}

	// Pre: Undrawn list of rectangles from RTree class
	// Post: Each rectangle within list sent to drawRect() for drawing
	public void drawRectangleList(){
		for(Rectangle r:drawRectangleList) {
			drawRect(r);
		}
	}

	// Pre: Point value that may or may not exist
	// Post: True if the point is drawn, false if not
	public boolean pointExists(int x, int y) {
		for (int i = 0; i < pointList.size(); i++) {
			if (Math.pow(x - pointList.get(i).x,2) + Math.pow(y - pointList.get(i).y,2) <= 25)
				return true;
		}
		return false;
	}

	// Pre: Button was clicked on
	// Post: Button color changed to show that it's pressed down
	public void setPressedColors() {
		if (mode == mode.insert)
			insertColor = pressedColor;
		if (!enterDone && mode == mode.enter)
			searchColor = pressedColor;
		if (enterDone && mode == mode.delete)
			deleteColor = pressedColor;
		if (enterDone && mode == mode.search)
			searchColor = pressedColor;
		if (enterDone && mode == mode.minX)
			minXColor = pressedColor;
		if (enterDone && mode == mode.minY)
			minYColor = pressedColor;

	}

	// Pre: X and Y value either inside or outside of 4 Rectangle values (x, y, width, height)
	// Post: True if X and Y value are within Rectangle, false if not
	public boolean contains(int x, int y, int rectX, int rectY, int rectWidth, int rectHeight) {
		if (x > rectX && x < rectX+rectWidth && y > rectY && y < rectY+rectHeight) {
			return true;
		} else {
			return false;
		}
	}
	
	// Clicked methods checks if the buttons are clicked, checks mouse position with button rectangles
	public boolean insertClicked(int mouseX, int mouseY) {
		if (contains(mouseX, mouseY, panelX, insertY, buttonWidth, buttonHeight)) {
			return true;
		} else {
			return false;
		}
	}
	public boolean searchClicked(int mouseX, int mouseY) {
		if (contains(mouseX, mouseY, panelX, searchY, buttonWidth, buttonHeight)) {
			return true;
		} else {
			return false;
		}
	}
	public boolean deleteClicked(int mouseX, int mouseY) {
		if (contains(mouseX, mouseY, panelX, deleteY, buttonWidth, buttonHeight)) {
			return true;
		} else {
			return false;
		}
	}
	public boolean enterClicked(int mouseX, int mouseY) {
		if (contains(mouseX, mouseY, panelX, enterY, buttonWidth, buttonHeight)) {
			return true;
		} else {
			return false;
		}
	}
	public boolean minXClicked(int mouseX, int mouseY) {
		if (contains(mouseX, mouseY, minX_X, panelMinY, minButtonWidth, minButtonHeight)) {
			return true;
		} else {
			return false;
		}
	}
	public boolean minYClicked(int mouseX, int mouseY) {
		if (contains(mouseX, mouseY, minY_X, panelMinY, minButtonWidth, minButtonHeight)) {
			return true;
		} else {
			return false;
		}
	}
}