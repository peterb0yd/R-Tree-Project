package com.rtree;

import java.util.ArrayList;


public class Rectangle {
	Point p1;
	Point p2;
	Point pCenter;
	int depth=0;
	ArrayList<Rectangle> children = null;
	Rectangle parent = null;
	public Rectangle(Point a, Point b, int d, ArrayList<Rectangle> c, Rectangle p)
	{
		p1 = a;
		p2 = b;
		pCenter = new Point((int)((a.getX()+b.getX())/2),(int)((a.getY()+b.getY())/2));
		depth = d;
		children = c;
		parent = p;
	}
	public Rectangle(Point a, Point b){
		p1 = a;
		p2 = b;
		pCenter = new Point((int)((a.getX()+b.getX())/2),(int)((a.getY()+b.getY())/2));
	}
	public Rectangle(Rectangle a, int d, ArrayList<Rectangle> c, Rectangle p){
		p1 = a.getP1();
		p2 = a.getP2();
		depth = d;
		children = c;
		parent = p;
		pCenter = new Point((int)((p1.getX()+p2.getX())/2),(int)((p1.getY()+p2.getY())/2));
		
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
	public Rectangle getParent(){
		return parent;
		
	}
	
	public void setParent(Rectangle r){
		parent = r;
		
	}
	
	public void removeChild(Rectangle r){
		children.remove(r);
		
	}
	
	public void addChild(Rectangle r){
		children.add(r);
	}
	
	public void setSize(Point a, Point b){
		p1 = a;
		p2 = b;
	}
	public int getSize()
	{
		return p1.getX()*p2.getY();
	}
	
	public boolean isPoint(){
		if(p1.getX()==p2.getX()&p1.getY()==p2.getY()){
			return true;
		}
		return false;
	}
	
	public boolean isLeaf(){
		if(children!=null&&!children.isEmpty()&&children.get(0).isPoint()){
			return true;
		}
		return false;
	}
	
	public int getArea(){
		return Math.abs((p1.getX()-p2.getX())*(p1.getY()-p2.getY()));
	}
	
	public void adjustMBR(){
		int newMaxX = 0;
		int newMinX =Integer.MAX_VALUE;
		int newMaxY =0;
		int newMinY =Integer.MAX_VALUE;
		for(Rectangle c:children){
			newMaxX = Math.max(c.getP2().getX(),newMaxX);
			newMinX =  Math.min(c.getP1().getX(),newMinX);
			newMaxY = Math.max(c.getP1().getY(),newMaxY);
			newMinY = Math.min(c.getP2().getY(),newMinY);
			
		}
		p1 = new Point(newMinX,newMaxY);
		p2 = new Point(newMaxX,newMinY);
	}
}
