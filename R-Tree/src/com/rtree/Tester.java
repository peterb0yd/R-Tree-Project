package com.rtree;

import java.util.ArrayList;


public class Tester {
	public static void main(String args[]){
		
		ArrayList<Rectangle> bob = new ArrayList<Rectangle>();
		for(int x=1;x<1000;x++)
			bob.add( new Rectangle(new Point(x ,x), new Point(x-1,x-1),0,null));
		RTree myTree = new RTree(3);
		myTree.makeRTree(bob,0);
	}
}
