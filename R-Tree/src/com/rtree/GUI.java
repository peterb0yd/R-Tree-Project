package com.rtree;

import java.awt.Rectangle;
import java.util.ArrayList;

import processing.core.PApplet;

public class GUI extends PApplet {

	public static ArrayList<Rectangle> rectList;
	private Algorithm algo;
	String words = "Click Somewhere!";
	int width = 640;
	int height = 360;
	int count = 0;

	public void setup() {
		rectList = new ArrayList<Rectangle>();
		algo = new Algorithm(rectList);
		
		// Window
		size(640, 360);
		background(250, 250, 250, 250);
		
		// Text
		textFont(createFont("Georgia", 36));
		textSize(36);
		fill(0, 0, 0, 100);
		text(words, 160, 150);
	}

	public void draw() {
		
		// Each rectangle
		for (Rectangle rect : rectList) {
			rect(rect.x, rect.y, 50, 50);
		}
	}

	public void mousePressed() {
		if (count < 1) 
			count++;
			background(250, 250, 250, 250);
			
		if (mousePressed) 
			addRect(mouseX, mouseY, 60, 60);		// add rectangle
//			ellipse(mouseX-10, mouseY-10, 5, 5);	// draw circle (for points)
			fill(0, 0, 15, 100);
		
	}
	
	public void addRect(float x, float y, float w, float h) {
		Rectangle rectangle = new Rectangle();
		rectangle.x = (int) x;
		rectangle.y = (int) y;
		rectangle.width = (int) w;
		rectangle.height = (int) h;
		rectList.add(rectangle);
		
		// Sends Rectangle List to Algorithm
		algo.setRectList(rectList);
	}
	
	public ArrayList<Rectangle> getRectList() {
		return rectList;
	}
	
	public void setRectList() {
		this.rectList = rectList;
	}
}