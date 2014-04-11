package com.rtree;

import java.awt.Rectangle;
import java.util.ArrayList;

public class Algorithm {
	
	public ArrayList <Rectangle> rectList;
	
	public Algorithm (ArrayList<Rectangle> rectList) {
		this.rectList = rectList;
	}
	
	public void main (String[] args) {
		GUI.rectList = rectList;
	}
	
	public ArrayList<Rectangle> getRectList() {
		return rectList;
	}
	
	public void setRectList(ArrayList<Rectangle> rectList) {
		this.rectList = rectList;
		for (Rectangle rect : rectList) {
			System.out.print("[" + rect.x + ", " + rect.y +"]" + "   ");
		}
		System.out.println();
	}

}
