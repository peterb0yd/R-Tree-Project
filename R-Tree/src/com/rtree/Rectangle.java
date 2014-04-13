package com.rtree;

import java.util.ArrayList;


public class Rectangle {
	Point p1;
	Point p2;
	Point pCenter;
	int depth=0;
	ArrayList<Rectangle> children = null;
	public Rectangle(Point a, Point b, int d, ArrayList<Rectangle> c)
	{
		p1 = a;
		p2 = b;
		pCenter = new Point((int)((a.getX()+b.getX())/2),(int)((a.getY()+b.getY())/2));
		depth = d;
		children = c;
	}
	public Point getP1(){
		return p1;
	}
	public Point getP2(){
		return p2;
	}
	public Point getPCenter(){
		return pCenter;
		
	}
	public int getDepth(){
		return depth;
	}
	public void setDepth(int d){
		depth = d;
		
	}
	public ArrayList<Rectangle> getChildren(){
		return children;
	}
}
