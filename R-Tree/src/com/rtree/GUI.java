package com.rtree;

import java.awt.Color;
import java.util.ArrayList;

import processing.core.PApplet;

public class GUI extends PApplet {

	
	public static ArrayList<Rectangle> drawRectangleList = new ArrayList<Rectangle>();
	Color [] myColor = { new Color(255, 0, 0), new Color(0, 255, 0), new Color(0, 0, 255), new Color(100,100,100), new Color(0, 250, 154), new Color(255,255,0)};
	RTree myTree = new RTree(3);
	
	public static ArrayList<Rectangle> rectList;
	String insert = "Insert";
	String search = "Search";
	String delete = "Delete";
	String enter = "Enter";
	boolean insertClicked = false;
	boolean searchClicked = false;
	boolean deleteClicked = false;
	boolean enterClicked = false;
	boolean searchDone = false;
	boolean enterDone = false;
	Color regColor = new Color(20, 20, 20, 255);
	Color pressedColor = new Color(250, 50, 50, 255);
	Color insertColor = new Color(1);
	Color searchColor = new Color(1);
	Color deleteColor = new Color(1);
	Color enterColor = new Color(1);
	int panelX = 580;
	int insertY = 100;
	int searchY = 200;
	int deleteY = 300; 
	int enterY = 400;
	int buttonWidth = 150;
	int buttonHeight = 50;
	int contX = 20;
	int contY = 20; 
	int contWidth = 500;
	int contHeight = 450;
	int pointCount = 0;
	
	// Points for rectangle
	Point p1; 
	Point p2;
	
	//holds existing points (they are held in rectangle objects)
	private static ArrayList<Point> pointList = new ArrayList<Point> ();
	
	Rectangle searchSpace;	//holds current search rectangle
	int searchRectX;
	int searchRectY;
	int searchRectWidth;
	int searchRectHeight;
	
	//keeps track of current mode
	public static enum Mode {start, insert, delete, search, enter};
	Mode mode;
	
	public void setup() {
		rectList = new ArrayList<Rectangle>();
		mode = mode.start;
		
		// Window
		size(800, 500);
		background(250, 250, 250, 250);
	}

	public void draw() {
		// Container
		fill(250, 250, 250, 250);
		rect(20, 20, 500, 450);
		stroke(60, 30, 30, 255);
		strokeWeight(2);
		
		// Text
		textFont(createFont("Georgia", 20));
		textSize(20);
		
		// Button Shapes
		fill(insertColor.getRGB(), 255);
		rect(panelX, insertY, buttonWidth, buttonHeight);
		fill(searchColor.getRGB(), 255);
		rect(panelX, searchY, buttonWidth, buttonHeight);
		fill(deleteColor.getRGB(), 255);
		rect(panelX, deleteY, buttonWidth, buttonHeight);
		fill(enterColor.getRGB(), 255);
		rect(panelX, enterY, buttonWidth, buttonHeight);
		
		// Button Text
		fill(240, 245, 245, 255);
		text(insert, panelX+45, insertY+30);
		text(search, panelX+45, searchY+30);
		text(delete, panelX+45, deleteY+30);
		text(enter, panelX+45, enterY+30);
		
		// If not searching, draw all points
		if (mode != mode.search) {
			// Rectangle testing
			for (Point p: pointList) {
				fill(0, 0, 15, 100);
				ellipse(p.x, p.y, 5, 5);
			}
			for (Rectangle r: drawRectangleList){
				drawRect(r);
			}
			noFill();
			for (Rectangle r: rectList)
				drawRect(r);
		}
		
		// If searching, draw rectangles within selected region
		if (mode == mode.search && searchSpace != null) {
			if (contains(searchSpace.p1.x, searchSpace.p1.y, contX, contY, contWidth, contHeight)) {
				drawRect(searchSpace);
				for (Rectangle r: rectList) {
					if (contains(r.p1.x, r.p1.y, searchRectX, searchRectY, searchRectWidth, searchRectHeight) && 
							contains(r.p2.x, r.p2.y, searchRectX, searchRectY, searchRectWidth, searchRectHeight)) {
						noFill();
						drawRect(r);
					}
				}
			}
		}
		
		// Set button pressed colors
		setPressedColors();
	}
		
	public void mousePressed() {
		if (insertClicked(mouseX, mouseY)) {
			mode = mode.insert;
		} 
		if (searchClicked(mouseX, mouseY)) { 
			mode = mode.search;
		} 
		if (deleteClicked(mouseX, mouseY)) {
			mode = mode.delete;
		} 
		if (enterClicked(mouseX, mouseY)) {
			mode = mode.enter;
		} 
		
		// What to do based on mode
		modeBehavior(); 
	}	
	
	public void mouseReleased() {
		insertColor = regColor;
		searchColor = regColor;
		deleteColor = regColor;
		enterColor = regColor;
		
		if (mode == mode.search) {
			searchDone = true;
		}
	}
	
	public void mouseDragged() {
		if (mode == mode.search) {	// Set bounds for search rectangle
			if (contains(mouseX, mouseY, contX, contY, contWidth, contHeight)) {
				Point p1 = new Point(searchRectX, searchRectY);
				Point p2 = new Point(mouseX, mouseY);
				searchRectWidth = p2.x - p1.x;
				searchRectHeight = p2.y - p1.y;
				searchSpace = new Rectangle(p1, p2, 0, null,null);
			}
		}
	}
	
	// Checks mode and applies actions
	private void modeBehavior() {	
		switch (mode)	{
			case insert:
				if (contains(mouseX, mouseY, contX, contY, contWidth, contHeight))
					if (!pointExists (mouseX, mouseY))
					{
							p1 = new Point(mouseX, mouseY);
							pointList.add(p1);
							if(enterDone)
							{
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
							for (int j = 0; j < rectList.size(); j++) {
								Rectangle rect = rectList.get(j);
								if (pointOfRect(point, rect)) { 
									System.out.println(rect.p1.x + "  " + rect.p1.y);
									rectList.remove(j);
									pointList.remove(p1);
									pointList.remove(p2);
								}	
							}
						}
					}
				}
				break;
			case search:
				searchRectX = 0;		// Reset search rectangle values
				searchRectY = 0;
				searchRectWidth = 0;
				searchRectHeight = 0;
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
				drawRectangleList = myTree.makeRTree(tempArray,0);
				drawRectangleList();
				break;
		}
	}
	
	// Adds rectangle to the display
	public void drawRect(Rectangle r) {
		noFill();
		int x = r.p1.x;
		int y = r.p1.y;
		int w = r.p2.x - r.p1.x;
		int h = r.p2.y - r.p1.y;
		stroke(myColor[r.getDepth()].getRGB());
		rect(x, y, w, h);
		
	}
	
	public void drawRectangleList(){
		for(Rectangle r:drawRectangleList)
		{
			drawRect(r);
		}
	}
	
	
	// Determines if a point exists at given coordinates
	public boolean pointExists(int x, int y) {
		for (int i = 0; i < pointList.size(); i++) {
			if (Math.pow(x - pointList.get(i).x,2) + Math.pow(y - pointList.get(i).y,2) <= 25)
				return true;
		}
		return false;
	}
	
	// Set color of pressed buttons
	public void setPressedColors() {
		if (mode == mode.insert)
			insertColor = pressedColor;
		if (mode == mode.delete)
			deleteColor = pressedColor;
		if (mode == mode.search)
			searchColor = pressedColor;
		if (mode == mode.enter)
			searchColor = pressedColor;
	}
	
	public ArrayList<Rectangle> getRectList() {
		return rectList;
	}
	
	public void setRectList() {
		this.rectList = rectList;
	}
	
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
	// If first two values are within rectangle bounds of last 4 values
	public boolean contains(int x, int y, int rectX, int rectY, int rectWidth, int rectHeight) {
		if (x > rectX && x < rectX+rectWidth && y > rectY && y < rectY+rectHeight) {
			return true;
		} else {
			return false;
		}
	}
	
	// If point given is part of the rectangle given
	public boolean pointOfRect(Point point, Rectangle rect) {
		if (point.x == rect.p1.x && point.y == rect.p1.y || point.x == rect.p2.x && point.y == rect.p2.y) {
			p1 = rect.p1;
			p2 = rect.p2;
			return true;
		} else {
			return false;
		}
	}
	
}