package com.rtree;

import java.awt.Color;
import java.util.ArrayList;

import processing.core.PApplet;

public class GUI extends PApplet {

	public static ArrayList<Rectangle> rectList;
	String insert = "Insert";
	String search = "Search";
	String delete = "Delete";
	String enter = "Enter";
	boolean insertClicked = false;
	boolean searchClicked = false;
	boolean deleteClicked = false;
	boolean enterClicked = false;
	Color regColor = new Color(20, 20, 20, 255);
	Color clickedColor = new Color(250, 250, 255, 255);
	Color insertColor = new Color(1);
	Color searchColor = new Color(1);
	Color deleteColor = new Color(1);
	Color enterColor = new Color(1);
	int panelX = 570;
	int insertY = 100;
	int searchY = 180;
	int deleteY = 260; 
	int enterY = 340;
	int buttonWidth = 150;
	int buttonHeight = 50;

	public void setup() {
		rectList = new ArrayList<Rectangle>();

		// Window
		size(800, 500);
		background(250, 250, 250, 250);
		
	}

	public void draw() {
		// Container
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
		text(enter, panelX+48, enterY+30);

	}

	public void mousePressed() {
		if (insertClicked(mouseX, mouseY)) { 
			insertColor = clickedColor;
		} 
		if (searchClicked(mouseX, mouseY)) { 
			searchColor = clickedColor;
		} 
		if (deleteClicked(mouseX, mouseY)) { 
			deleteColor = clickedColor;
		} 
		if (enterClicked(mouseX, mouseY)) { 
			enterColor = clickedColor;
		} 
	}
	
	public void mouseReleased() {
		insertColor = regColor;
		searchColor = regColor;
		deleteColor = regColor;
		enterColor = regColor;
	}

	public ArrayList<Rectangle> getRectList() {
		return rectList;
	}
	
	public void setRectList() {
		this.rectList = rectList;
	}
	
	public boolean insertClicked(float mouseX, float mouseY) {
		if (mouseX > panelX && mouseX < panelX+buttonWidth && mouseY > insertY && mouseY < insertY+buttonHeight) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean searchClicked(float mouseX, float mouseY) {
		if (mouseX > panelX && mouseX < panelX+buttonWidth && mouseY > searchY && mouseY < searchY+buttonHeight) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean deleteClicked(float mouseX, float mouseY) {
		if (mouseX > panelX && mouseX < panelX+buttonWidth && mouseY > deleteY && mouseY < deleteY+buttonHeight) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean enterClicked(float mouseX, float mouseY) {
		if (mouseX > panelX && mouseX < panelX+buttonWidth && mouseY > enterY && mouseY < enterY+buttonHeight) {
			return true;
		} else {
			return false;
		}
	}
}