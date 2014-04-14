package com.rtree;

import java.util.ArrayList;

public class RTree {

	ArrayList<Rectangle> returnList = new ArrayList<Rectangle>();
	double capacity = 0.0;
	Rectangle root;

	private ArrayList<Rectangle> rectangle1 = new ArrayList<Rectangle>();
	private ArrayList<Rectangle> rectangle2= new ArrayList<Rectangle>();
	private ArrayList<Rectangle> tempStorage= new ArrayList<Rectangle>();
	private ArrayList<Rectangle> splitList= new ArrayList<Rectangle>();
	private Rectangle MBR1 = null;
	private Rectangle MBR2 = null;

	double curr =0; 
	public RTree(int c){
		capacity = c;
	}
	public ArrayList<Rectangle> getRTree(){
		return returnList;
	}

	public void insert(Rectangle rect3){

		Rectangle rectParent = chooseLeaf(rect3,root);
		rectParent.addChild(rect3); 
		//rect3.setParent(rectParent);
		if(rectParent.getChildren().size()> capacity){
		
			split(rectParent);
			
		}
	}
	
	//Chooses Rectangle in which to insert new element
	public Rectangle chooseLeaf(Rectangle prect, Rectangle r){
		//System.out.println(r.getDepth());
		if(r.isLeaf()){
			//System.out.println(r.getDepth());
			return r;
		}

		Rectangle minRect = null;
		int minArea = 9999999;

		for(Rectangle p: r.getChildren())
		{
			Rectangle temp = minimumBoundingRectangle(prect,p);
			if(temp.getArea()-p.getSize()<minArea)
			{
				minRect = p;
				minArea = temp.getArea()-p.getSize();	
			}
		}	
		return chooseLeaf(prect, minRect);
	}
	
	public void split(Rectangle rect){
		Rectangle parent = rect.getParent();
		if(rectangle1.isEmpty()&&rectangle2.isEmpty()){
			tempStorage = rect.getChildren();
			pickSeeds(tempStorage);
		}
		if(tempStorage.isEmpty()){
			Rectangle r = new Rectangle(MBR1, parent.getDepth()-1, rectangle1, parent);
			Rectangle b = new Rectangle(MBR2, parent.getDepth()-1, rectangle2, parent);
			for(Rectangle c: r.getChildren())
			{
				c.setParent(r);
			}
			for(Rectangle c: b.getChildren())
			{
				c.setParent(b);
			}
			parent.removeChild(rect);
			returnList.add(r);
			returnList.add(b);
			resetSplitGlobals();
			splitList.add(r);
			splitList.add(b);
			return;
		
		}
		pickNext();
		split(rect);
	}

	
	public void pickSeeds(ArrayList<Rectangle> rect){
		int maxArea = 0;
		Rectangle rect1=null;
		Rectangle rect2=null;
		int temp = 0;
		for(Rectangle x:rect)
		{
			for(Rectangle y: rect)
			{
				
				temp = minimumBoundingRectangle(x,y).getArea()-x.getArea()-y.getArea();
//				System.out.println("x: "+x.getArea());
//				System.out.println("y: "+y.getArea());
//				System.out.println("Rect: "+minimumBoundingRectangle(x,y).getArea());
				if(temp>maxArea){
					temp = maxArea;
					rect1 = x;
					rect2 = y;
					
				}
			}
		}
		
		rectangle1.add(rect1);
		MBR1 = rect1;
		rectangle2.add(rect2);
		MBR2 = rect2;
	}
	
	public void pickNext(){
		int maxDifference = 0;
		Rectangle maxDiff=null; //Will store the rectangle with maximum difference in preference between the two groups
		int maxDiffGroup = 0;
		System.out.println("dah");
		for(Rectangle t:tempStorage)
		{
			int d1 = minimumBoundingRectangle(MBR1,t).getArea()-MBR1.getArea();
			System.out.println("d1: "+d1);
			int d2 = minimumBoundingRectangle(MBR2,t).getArea()-MBR2.getArea();
			System.out.println("d2: "+d2);
			int diff = Math.abs(d1-d2);
			if(diff>=maxDifference)
			{
				diff = maxDifference;
				System.out.println("the change occurs here");
				maxDiff = t;
				if(d1>d2){
					maxDiffGroup = 1;
				}
				else
				{
					maxDiffGroup = 2;
				}
			}
		}
		if(maxDiffGroup==1)
		{
			MBR1 = minimumBoundingRectangle(MBR1,maxDiff);
			rectangle1.add(maxDiff);
		}
		else{
			//System.out.println(maxDiff);
			MBR2 = minimumBoundingRectangle(MBR2,maxDiff);
			rectangle2.add(maxDiff);
		}
		tempStorage.remove(maxDiff);
	}
	
	public void adjustTree(Rectangle r){
		
		if(r.equals(root)){
			splitList.clear();
		}
		else{
			Rectangle p = r.getParent();
			p.adjustMBR();
			if(splitList.contains(r)&&p.getChildren().size()>capacity){
				split(r.getParent());
			}
			adjustTree(p);
		}
	}

	public void resetSplitGlobals(){
		rectangle1.clear();
		rectangle2.clear();
		tempStorage.clear();		
		MBR1 = null;
		MBR2 = null;

	}

	public Rectangle minimumBoundingRectangle(Rectangle a, Rectangle b){
		int newMaxX = Math.max(a.getP2().getX(),b.getP2().getX());
		int newMinX =  Math.min(a.getP1().getX(),b.getP1().getX());
		int newMaxY = Math.max(a.getP1().getY(),b.getP1().getY());
		int newMinY = Math.min(a.getP2().getY(),b.getP2().getY());
		return new Rectangle(new Point(newMinX,newMaxY),  new Point(newMaxX, newMinY));
	}





	/*Deletions!*/
	public void delete(Rectangle r){
		r.getParent().removeChild(r);
		condenseTree(r.getParent());
		if(root.getChildren().size()==1){
			root.getChildren().get(0).setParent(null);
			root = root.getChildren().get(0);
		}
	}
	public void findLeaf(){

	}

	public void condenseTree(Rectangle r){
		Rectangle p = r.getParent();
		if(r.getChildren().size()<2){
			ArrayList<Rectangle> temp = r.getChildren();
			delete(r);
			for(Rectangle rec: temp){
				insert(rec);				//Should this be here? Might we be inserting into elements that are supposed to be changed later?
			}
			condenseTree(p);

		}
		resize(r);
		condenseTree(r.getParent());

	}


	public void resize(Rectangle rect)
	{
		int maxUlX = 0;
		int maxUlY = 0;
		int minLrX=9999999;
		int minLrY = 9999999;
		for(Rectangle r: rect.getChildren()){
			if(r.getP1().getX()>maxUlX)						//Sets upper left and lower right coordinates of minimal rectangle
			{
				maxUlX=(r.getP1().getX());
			}
			if(r.getP1().getY()>maxUlY)
			{
				maxUlY=(r.getP1().getY());
			}
			if(r.getP2().getX()<minLrX)
			{
				minLrX=(r.getP2().getX());
			}
			if(r.getP2().getY()<minLrY)
			{
				minLrY=(r.getP2().getY());
			}
		}
		rect.setSize(new Point(maxUlX,maxUlY), new Point(minLrX,minLrY));

	}



	public ArrayList<Rectangle> makeRTree(ArrayList<Rectangle> rec, int depth)	//#MERLEISSOSWAG
	{
		returnList.addAll(rec);
		/*
		 	Base Case: Add the root node to list and return list
		 */
		if(rec.size()==1){
			//rec.get(0).setDepth(depth);
			//returnList.add(rec.get(0));

			root = rec.get(0);
			return returnList;
		}
		/*
			Sort by x
		 */
		for(int x =0; x< rec.size();x++){
			for(int y=0;y<rec.size()-1;y++){
				if(rec.get(y).getPCenter().getX()>rec.get(y+1).getPCenter().getX())
				{
					Rectangle temp = rec.get(y);
					rec.remove(y);
					rec.add(y+1,temp);
				}
			}
		}

		double pages = rec.size()/capacity;
		int partition =  (int)(Math.ceil(Math.sqrt(pages)));

		ArrayList<ArrayList<Rectangle>> partitionArray = new ArrayList<ArrayList<Rectangle>>();
		int y=0;
		int c = 0;			//Index of ArrayList  of ArrayLists
		partitionArray.add(new ArrayList<Rectangle>());
		for(int x=0;x<rec.size();x++)
		{
			if(y == partition&&x<rec.size()-1)
			{
				partitionArray.add(new ArrayList<Rectangle>());
				c++;
				y=0;
			}
			partitionArray.get(c).add(rec.get(x));
			y++;
		}
		/*
		Sort by y
		 */
		for(ArrayList<Rectangle> arr:partitionArray){
			for(int x =0; x<arr.size();x++){
				for(int z=0;z<arr.size()-1;z++){
					if(arr.get(z).getPCenter().getY()>arr.get(z+1).getPCenter().getY())
					{
						Rectangle temp = arr.get(z);
						arr.remove(z);
						arr.add(z+1,temp);
					}
				}
			}	
		}

		//Packing!
		ArrayList<ArrayList<Rectangle>> partitionArray2 = new ArrayList<ArrayList<Rectangle>>();


		int c2=-1;
		for(ArrayList<Rectangle> arr:partitionArray){
			int y2=0;
			partitionArray2.add(new ArrayList<Rectangle>());
			c2++;
			for(int x=0;x<arr.size();x++)
			{
				if(y2 == capacity){
					partitionArray2.add(new ArrayList<Rectangle>());
					c2++;
					y2=0;
				}
				partitionArray2.get(c2).add(arr.get(x));
				y2++;
			}
		}


		//find minimum spanning rectangle.

		ArrayList<Rectangle> rtrn = new ArrayList<Rectangle>();

		for(ArrayList<Rectangle> arr:partitionArray2)
		{
			int newMaxX = 0;
			int newMinX =Integer.MAX_VALUE;
			int newMaxY =0;
			int newMinY =Integer.MAX_VALUE;
			for(Rectangle r:arr){
				newMaxX = Math.max(r.getP2().getX(),newMaxX);
				newMinX =  Math.min(r.getP1().getX(),newMinX);
				newMaxY = Math.max(r.getP1().getY(),newMaxY);
				newMinY = Math.min(r.getP2().getY(),newMinY);
				
			}
			
			
			
//			int maxUlX = 0;
//			int maxUlY = 0;
//			int minLrX=Integer.MAX_VALUE;
//			int minLrY = Integer.MAX_VALUE;
//			for(Rectangle r: arr){
//				if(r.getP1().getX()>maxUlX)						//Sets upper left and lower right coordinates of minimal rectangle
//				{
//					maxUlX=(r.getP1().getX());
//				}
//				if(r.getP1().getY()>maxUlY)
//				{
//					maxUlY=(r.getP1().getY());
//				}
//				if(r.getP2().getX()<minLrX)
//				{
//					minLrX=(r.getP2().getX());
//				}
//				if(r.getP2().getY()<minLrY)
//				{
//					minLrY=(r.getP2().getY());
//				}
//
//			}
			rtrn.add(new Rectangle(new Point(newMinX,newMaxY),new Point( newMaxX,newMinY), depth,arr,null));
			for(Rectangle r:arr)
			{
				r.setParent(rtrn.get(rtrn.size()-1));
			}
		}
		return makeRTree(rtrn, depth +1);


	}


}
