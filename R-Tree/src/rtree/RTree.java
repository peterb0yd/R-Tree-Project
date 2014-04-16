package rtree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;


public class RTree {

	ArrayList<Rectangle> returnList = new ArrayList<Rectangle>();
	double capacity = 0.0;
	Rectangle root;

	private ArrayList<Rectangle> rectangle1 = new ArrayList<Rectangle>();
	private ArrayList<Rectangle> rectangle2= new ArrayList<Rectangle>();
	private ArrayList<Rectangle> tempStorage= new ArrayList<Rectangle>();
	private ArrayList<Rectangle> splitList= new ArrayList<Rectangle>();
	private ArrayList<Rectangle> elimNodes= new ArrayList<Rectangle>();
	private ArrayList<Rectangle> elimNodes2 = new ArrayList<Rectangle>();
	private ArrayList<Rectangle> allChildren = new ArrayList<Rectangle>();


	//private ArrayList<Rectangle> inList = new ArrayList<Rectangle>();

	private Queue<Rectangle> q = new LinkedList<Rectangle>();

	private Rectangle MBR1 = null;
	private Rectangle MBR2 = null;

	double curr =0; 
	public RTree(int c){
		capacity = c;
	}
	public ArrayList<Rectangle> getRTree(){
		return returnList;
	}



	/*
	 * INSERTION
	 * INSERTION
	 * INSERTION
	 */


	public void insert(Rectangle rect3){
		returnList.add(rect3);
		Rectangle rectParent = chooseLeaf(rect3,root);
		rectParent.addChild(rect3); 
		//System.out.println(rectParent.getChildren().size()+" rectparent size");
		rect3.setParent(rectParent);

		if(rectParent.getChildren().size()> capacity){
			split(rectParent);
		}
		else
			adjustTree(rect3);											//Tree is adjusted after split if split is called. If split is not called, it is adjusted here.

		//System.out.println(checkParents());


	}

	//Chooses Rectangle in which to insert new element
	public Rectangle chooseLeaf(Rectangle prect, Rectangle r){
		if(r.isLeaf()){
			return r;
		}

		Rectangle minRect = null;
		int minArea = Integer.MAX_VALUE;
		for(Rectangle p: r.getChildren())
		{
			Rectangle temp = minimumBoundingRectangle(prect,p);
			if(temp.getArea()-p.getArea()<minArea)
			{
				minRect = p;
				minArea = temp.getArea()-p.getArea();	
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
			if(parent == null)
			{
				parent = new Rectangle(minimumBoundingRectangle(MBR1,MBR2), root.getDepth()+1,  new ArrayList<Rectangle>(), null);
				returnList.remove(root);
				returnList.add(parent);
				root = parent;

			}

			Rectangle r = new Rectangle(MBR1, parent.getDepth()-1, new ArrayList<Rectangle>(rectangle1), parent);
			Rectangle b = new Rectangle(MBR2, parent.getDepth()-1,new ArrayList<Rectangle>(rectangle2), parent);

			resetSplitGlobals();
			parent.addChild(r);
			parent.addChild(b);
			for(Rectangle c: r.getChildren())
			{
				c.setParent(r);
			}
			for(Rectangle c: b.getChildren())
			{
				c.setParent(b);
			}
			parent.removeChild(rect);
			returnList.remove(rect);
			returnList.add(r);
			returnList.add(b);

			splitList.add(r);
			adjustTree(r);

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

				if(temp>maxArea){
					maxArea = temp;
					rect1 = x;
					rect2 = y;

				}
			}
		}

		rectangle1.add(rect1);
		MBR1 = rect1;
		rectangle2.add(rect2);
		MBR2 = rect2;
		tempStorage.remove(rect1);
		tempStorage.remove(rect2);
	}

	public void pickNext(){
		int maxDifference = 0;
		Rectangle maxDiff=null; //Will store the rectangle with maximum difference in preference between the two groups
		int maxDiffGroup = 0;
		for(Rectangle t:tempStorage)
		{
			int d1 = minimumBoundingRectangle(MBR1,t).getArea()-MBR1.getArea();
			int d2 = minimumBoundingRectangle(MBR2,t).getArea()-MBR2.getArea();
			int diff = Math.abs(d1-d2);
			if(diff>=maxDifference)
			{
				maxDifference = diff;
				maxDiff = t;
				if(d1>d2){
					maxDiffGroup = 2;
				}
				else
				{
					maxDiffGroup = 1;
				}
			}
		}

		if((rectangle1.size()==1||rectangle2.size()==1)&&rectangle1.size()!=rectangle2.size()){
			if(rectangle1.size()>rectangle2.size()){
				maxDiffGroup = 2;

			}
			else
			{
				maxDiffGroup = 1;
			}
		}

		if(maxDiffGroup==1)
		{
			MBR1 = minimumBoundingRectangle(MBR1,maxDiff);
			rectangle1.add(maxDiff);
		}
		else{
			MBR2 = minimumBoundingRectangle(MBR2,maxDiff);
			rectangle2.add(maxDiff);
		}
		tempStorage.remove(maxDiff);



	}

	public void adjustTree(Rectangle r){
		if(r.equals(root)){

			splitList.clear();
		}
		else
		{
			Rectangle p = r.getParent();

			if(splitList.contains(r)&&p.getChildren().size()>capacity){
				split(r.getParent());
			}
			else
			{
				p.adjustMBR();
				adjustTree(p);
			}

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





	/*
	 * DELETION
	 * DELETION
	 * DELETION
	 */



	public void delete(Rectangle r){
		for(int x =0; x< 5;x++)
			System.out.println();

		//System.out.println(checkParents());
		Rectangle leaf = findLeaf(r);
		//System.out.println(checkPath(r)+", can be pathed to from root.");
		//System.out.println("Before Delete: "+checkPath2AllChildren());

		//checkTree();
		leaf.removeChild(r);
		returnList.remove(r);


		condenseTree(leaf);
		if(root.getChildren().size()==1){
			Rectangle newRoot = root.getChildren().get(0);
			newRoot.setParent(null);

			returnList.remove(root);

			root = newRoot;
		}

		//System.out.println("After Delete: "+checkPath2AllChildren());
		System.out.println(checkParents());
	}

	public Rectangle findLeaf(Rectangle p){
		if(p.getArea()>1)
			System.out.println("MAYDAYMADAY PROBLEM!!!!!");
		q.clear();
		q.add(root);
		while(!q.isEmpty())
		{
			Rectangle next = q.poll();
			if(next.isLeaf())
			{
				if(next.getChildren().contains(p))
				{
					q.clear();
					return next;
				}
			}
			else
			{
				for(Rectangle c: next.getChildren())
				{
					if(c.contains(p))
					{
						System.out.println("Overlaps: "+next.getDepth()+" "+ next+ " "+c.getDepth()+" "+c+" ");
						for(Rectangle d: c.getChildren())
						{
							d.printBounds();
							System.out.println();
						}
						p.printBounds();
						q.add(c);
					}
				}
			}
		}
		//System.out.println("return list equals temp? "+(returnList.size()==temp.size()));
		//System.out.println("Element of returnList? "+returnList.contains(p));
		System.out.println("Does everything have a parent?: "+checkParents());
		System.out.println("Couldn't find it :c");
		System.out.println(p.getParent());
		System.out.println(p.getParent().getChildren().contains(p));
		System.out.println("Was reinserted: "+p.hasReinserted());
		System.out.println(checkPathToMe(p.getParent()));
		return null;
	}

	public void condenseTree(Rectangle r){

		if(r.equals(root))
		{
			if(elimNodes.size()>0)
			{
				for(Rectangle rect:elimNodes)
				{
					allChildren(rect);
				}
				for(Rectangle e:elimNodes){	
					for(Rectangle c : e.getChildren())
					{
						c.setParent(null);
					}
					returnList.remove(e);
				}
				for(Rectangle e:elimNodes2)
				{
					for(Rectangle c : e.getChildren())
					{
						c.setParent(null);
					}
					returnList.remove(e);
				}

				for(Rectangle c:allChildren)
				{
					returnList.remove(c);
					c.setReinserted();
					insert(c);
				}
				elimNodes.get(elimNodes.size()-1).getParent().removeChild(elimNodes.get(elimNodes.size()-1));

				elimNodes.clear();
				elimNodes2.clear();
				allChildren.clear();
			}
			r.adjustMBR();

		}
		else
		{
			Rectangle p = r.getParent();
			if(r.getChildren().size()<2){
				p.removeChild(r);
				elimNodes.add(r);
			}
			else
			{
				r.adjustMBR();
			}
			condenseTree(p);
		}
	}

	public void allChildren(Rectangle r){
		if(r.isPoint()&&!allChildren.contains(r)){
			allChildren.add(r);
		}
		else
		{
			for(Rectangle c: r.getChildren())
			{
				if(!elimNodes.contains(c)&&!elimNodes2.contains(c)&&!c.isPoint())
				{
					elimNodes2.add(c);
				}
				allChildren(c);
			}
		}
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


			rtrn.add(new Rectangle(new Point(newMinX,newMaxY),new Point( newMaxX,newMinY), depth,arr,null));
			for(Rectangle r:arr)
			{
				r.setParent(rtrn.get(rtrn.size()-1));
			}
		}
		return makeRTree(rtrn, depth +1);


	}



	public ArrayList<Rectangle> search(Rectangle r){
		ArrayList<Rectangle> searchList = new ArrayList<Rectangle>();
		q.clear();
		q.add(root);
		while(!q.isEmpty())
		{
			Rectangle next = q.poll();
			if(next.isPoint())
			{
				searchList.add(next);
			}
			else
			{
				for(Rectangle c: next.getChildren())
				{
					if(r.overlaps(c))
					{		
						q.add(c);
					}
				}
			}
		}
		return searchList;
	}


	/*
	 * will return the lowest level of X in that Rectangle
	 * and the last call will be a point 
	 */
	public Rectangle getMinimumX(){
		return minimumX(root);
	}
	public Rectangle getMinimumY(){
		return minimumY(root);
	}

	public Rectangle minimumX(Rectangle r){
		//if it's only a point, return that point
		if(r.isPoint()){
			return r;
		}else{
			int minX = Integer.MAX_VALUE; 
			Rectangle rec =null;
			for(Rectangle p : r.getChildren())
				if(p.getP1().getX() < minX){
					//possibly might need to change from p to r
					minX = p.getP1().getX();
					rec = p; 
				}
			return minimumX(rec);
		}
	}
	/*
	 * will return the lowest level of Y in that Rectangle
	 * and the last call will be a point 
	 */
	public Rectangle minimumY(Rectangle r){
		//if it's only a point, return that point
		if(r.isPoint()){
			return r;
		}else{
			int minY = Integer.MAX_VALUE; 
			Rectangle rec =null;
			for(Rectangle p : r.getChildren())
				if(p.getP2().getY() < minY){
					//possibly might need to change from p to r
					minY = p.getP2().getY();
					rec = p; 
				}
			return minimumY(rec);
		}
	}


	public void printTree(){
		for(Rectangle r: returnList){
			System.out.println("This: "+r);
			System.out.println("Parent: "+r.getParent());
			System.out.println("Children: "+r.getChildren());
			System.out.println("Depth: "+r.getDepth());
			System.out.println("");
		}
		for(int x = 0 ;x<20;x++)
		{
			System.out.println("");
		}


	}

	public void checkTree(){
		for(Rectangle r: returnList)
		{
			if(!r.isPoint())
			{
				for(Rectangle c: r.getChildren())
				{
					if(!c.getParent().equals(r))
					{
						System.out.println("Oh noes!");
					}
				}
			}
			if(!r.equals(root))
			{
				if(!r.getParent().getChildren().contains(r))
				{
					System.out.println("Oh noes!");
				}
			}
		}


	}
	public boolean checkPath(Rectangle r){
		if(r==null)
			return false;
		if(r.equals(root))
		{
			return true;
		}
		return checkPath(r.getParent());
	}


	public boolean checkPath2(Rectangle r)
	{
		q.clear();
		q.add(root);
		while(!q.isEmpty())
		{
			Rectangle next = q.poll();

			if(next.equals(r))
			{
				q.clear();
				return true;
			}
			if(!next.isPoint())
			{
				for(Rectangle c:next.getChildren())
				{
					q.add(c);

				}
			}
		}
		return false;
	}
	public boolean checkPath2AllChildren(){
		for(Rectangle r: returnList)
		{
			if(!checkPath2(r))
			{
				System.out.println(r.getDepth());
				System.out.println(r.getP1().getX()+" "+r.getP1().getY());
				return false;
			}
		}
		return true;

	}


	public void checkOverlap()
	{
		for(Rectangle r: returnList)
		{
			if(r.isLeaf())
			{
				for(Rectangle c: r.getChildren())
				{
					System.out.println(r.overlaps(c));
				}
			}
		}
	}

	public boolean checkParents(){
		for(Rectangle r: returnList)
		{
			if(r!=root&&r.getParent()==null)
			{
				System.out.println("Depth: "+r.getDepth());
				System.out.println("Is a point: "+r.isPoint()+"x: "+ r.getP1().getX()+" y: "+r.getP1().getY());
				return false;
			}
		}
		return true;


	}
	public boolean checkPathToMe(Rectangle r)
	{
		q.clear();
		q.add(root);
		while(!q.isEmpty())
		{
			Rectangle next = q.poll();

			if(next.equals(r))
			{
				q.clear();
				return true;
			}
			if(!next.isLeaf())
			{
				for(Rectangle c:next.getChildren())
				{
					q.add(c);

				}
			}
		}
		return false;
	}

}
