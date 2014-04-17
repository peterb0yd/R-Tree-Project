package rtree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class RTree {

	private ArrayList<Rectangle> returnList = new ArrayList<Rectangle>();
	private double capacity = 0.0;
	private Rectangle root;
	private ArrayList<Rectangle> rectangle1 = new ArrayList<Rectangle>();
	private ArrayList<Rectangle> rectangle2= new ArrayList<Rectangle>();
	private ArrayList<Rectangle> tempStorage= new ArrayList<Rectangle>();
	private ArrayList<Rectangle> splitList= new ArrayList<Rectangle>();
	private ArrayList<Rectangle> elimNodes= new ArrayList<Rectangle>();
	private ArrayList<Rectangle> elimNodes2 = new ArrayList<Rectangle>();
	private ArrayList<Rectangle> allChildren = new ArrayList<Rectangle>();
	private Queue<Rectangle> q = new LinkedList<Rectangle>();

	private Rectangle MBR1 = null;
	private Rectangle MBR2 = null;

	
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


	//Precondition: returnList has at least two Rectangle
	public void insert(Rectangle rect3){
		returnList.add(rect3);
		Rectangle rectParent = chooseLeaf(rect3,root);
		rectParent.addChild(rect3); 
		rect3.setParent(rectParent);

		if(rectParent.getChildren().size()> capacity){
			split(rectParent);
		}
		else
			adjustTree(rect3);											//Tree is adjusted after split if split is called. If split is not called, it is adjusted here.


	}
	//Postcondition: The element has been inserted into the RTree, the GUI, and all of the effected rectangles have been resized 

	
	//Precondition: insert has been called. 
	//This method attempts to find the most suitable rectangle to insert the new point.
	public Rectangle chooseLeaf(Rectangle prect, Rectangle r){
		if(r.isLeaf()){
			return r;
		}

		Rectangle minRect = null;	
		int minArea = Integer.MAX_VALUE;
		for(Rectangle p: r.getChildren())
		{
			Rectangle temp = minimumBoundingRectangle(prect,p);		//Finds rectangle whose area has to increase the least to include the newest point.
			if(temp.getArea()-p.getArea()<minArea)
			{
				minRect = p;
				minArea = temp.getArea()-p.getArea();	
			}
		}	

		return chooseLeaf(prect, minRect);
	}
	//Postcondition: The most suitable rectangle to hold the inserted point has been chosen

	//Precondition: insert has been called and a suitable rectangle has been chosen to insert into. This rectangle does not have the capacity to store this point, and must be split.
	public void split(Rectangle rect){
		Rectangle parent = rect.getParent();
		if(rectangle1.isEmpty()&&rectangle2.isEmpty()){				//tempStorage holds all children from the original group that need to be sorted into two new groups
			tempStorage = rect.getChildren();
			pickSeeds(tempStorage);											
		}
		if(tempStorage.isEmpty()){													//When tempStorage is empty, we're done adding the children into two new lists
			if(parent == null)															//If the root node is being split, there must be a new root node created that contains both of these.
			{
				parent = new Rectangle(minimumBoundingRectangle(MBR1,MBR2), root.getDepth()+1,  new ArrayList<Rectangle>(), null);
				returnList.remove(root);
				returnList.add(parent);
				root = parent;

			}

			Rectangle r = new Rectangle(MBR1, parent.getDepth()-1, new ArrayList<Rectangle>(rectangle1), parent);	//Create two new rectangles that are going to be the parents of the children that were just sorted into new lists
			Rectangle b = new Rectangle(MBR2, parent.getDepth()-1,new ArrayList<Rectangle>(rectangle2), parent);

			resetSplitGlobals();
			parent.addChild(r);
			parent.addChild(b);
			for(Rectangle c: r.getChildren())		//Parent-child bonding!
			{
				c.setParent(r);
			}
			for(Rectangle c: b.getChildren())
			{
				c.setParent(b);
			}
			parent.removeChild(rect);
			returnList.remove(rect);						//Add to the master list.
			returnList.add(r);
			returnList.add(b);

			splitList.add(r);									//Stores a rectangle if it is the new result of a recent split. Used to decide whether the question of splitting is asked to its parent.
			adjustTree(r);

			return;

		}
		pickNext();
		split(rect);
	}
	//Postcondition: There are two new parents that hold the children of the overloaded rectangle that had to split. 
	
	
	//Precondition: split was called. 
	//This method will put the two points that are the least efficient pairing and puts them in separate sets.
	public void pickSeeds(ArrayList<Rectangle> rect){
		int maxArea = 0;
		Rectangle rect1=null;
		Rectangle rect2=null;
		int temp = 0;
		for(Rectangle x:rect)				
		{
			for(Rectangle y: rect)
			{
				temp = minimumBoundingRectangle(x,y).getArea()-x.getArea()-y.getArea();	//Finds the pairing with the most wasted area.

				if(temp>maxArea){
					maxArea = temp;
					rect1 = x;
					rect2 = y;

				}
			}
		}

		rectangle1.add(rect1);
		MBR1 = rect1;							//Stores the current minimum bounding rectangle of the rect1 list we are building.
		rectangle2.add(rect2);
		MBR2 = rect2;
		tempStorage.remove(rect1);
		tempStorage.remove(rect2);
	}
	//Postcondition: rect1 and rect2 each contain one rectangle-point that was originally in tempStorage.
	
	
	//split and pickSeeds() were both called.
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

		if((rectangle1.size()==1||rectangle2.size()==1)&&rectangle1.size()!=rectangle2.size()){		//Ignores the most efficient choice if one list is too small.
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
	//Postcondition: Everything that was in tempStorage is now in one of the two lists.

	
	//Precondition: An insert occured
	public void adjustTree(Rectangle r){
		if(r.equals(root)){

			splitList.clear();
		}
		else
		{
			Rectangle p = r.getParent();

			if(splitList.contains(r)&&p.getChildren().size()>capacity){
				split(r.getParent());																//If there was a split in its child and it is now over capacity, needs to split.
			}
			else
			{
				p.adjustMBR();
				adjustTree(p);
			}

		}
	}

	//Precondition: A split has just finished.
	public void resetSplitGlobals(){
		rectangle1.clear();
		rectangle2.clear();
		tempStorage.clear();		
		MBR1 = null;
		MBR2 = null;
	}
	//Postcondition: Global variables are prepared for the next split.

	
	//Precondition: a and b are rectangles
	public Rectangle minimumBoundingRectangle(Rectangle a, Rectangle b){
		int newMaxX = Math.max(a.getP2().getX(),b.getP2().getX());
		int newMinX =  Math.min(a.getP1().getX(),b.getP1().getX());
		int newMaxY = Math.max(a.getP1().getY(),b.getP1().getY());
		int newMinY = Math.min(a.getP2().getY(),b.getP2().getY());
		return new Rectangle(new Point(newMinX,newMaxY),  new Point(newMaxX, newMinY));
	}
	//Postcondition: the minimum bounding rectangle of a and b is returned.




	/*
	 * DELETION
	 * DELETION
	 * DELETION
	 */


	//Precondition: A rectangle-point has been clicked on for deletion.
	public void delete(Rectangle r){
		Rectangle leaf = findLeaf(r);		//Finds the rectangle attempting to be deleted

		leaf.removeChild(r);							
		returnList.remove(r);

		condenseTree(leaf);
		if(root.getChildren().size()==1){		//If after the deletion the root only has one child, this child becomes the new root.
			Rectangle newRoot = root.getChildren().get(0);
			newRoot.setParent(null);

			returnList.remove(root);

			root = newRoot;
		}
	}
	//Postcondition: The rectangle-point picked has been deleted. The tree has been condensed to proper size.
	
	//Precondition: delete has been called
	public Rectangle findLeaf(Rectangle p){
		q.clear();
		q.add(root);
		while(!q.isEmpty())
		{
			Rectangle next = q.poll();
			if(next.isLeaf())
			{
				if(next.getChildren().contains(p))	//The child has been found! return its parent
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
						q.add(c);
					}
				}
			}
		}
		return null;								
	}
	//Postcondition: The parent of the rectangle-point being deleted has been returned.
	
	//Precondition: A deletion has occured.
	public void condenseTree(Rectangle r){

		if(r.equals(root))								
		{
			if(elimNodes.size()>0)
			{
				for(Rectangle rect:elimNodes)
				{
					allChildren(rect);							//gets all the rectangle-point children of rectangles being deleted
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

				for(Rectangle c:allChildren)		//Reinserts children who need to relocate because their parents only had one child.
				{
					returnList.remove(c);
					c.setReinserted();
					insert(c);
				}
				elimNodes.get(elimNodes.size()-1).getParent().removeChild(elimNodes.get(elimNodes.size()-1)); //The last element of elimNodes will be the highest level among them. This detatches this entire portion from the rest of the tree

				elimNodes.clear();
				elimNodes2.clear();
				allChildren.clear();
			}
			r.adjustMBR();

		}
		else
		{
			Rectangle p = r.getParent();
			if(r.getChildren().size()<2){				//If the rectangle has only one child, add it to the elimNodes list so it can be removed later. 
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

	//Precondition: a deletion has occurred and some rectangle-points must be reinserted due to their parents not having enough children
	public void allChildren(Rectangle r){
		if(r.isPoint()&&!allChildren.contains(r)){
			allChildren.add(r);
		}
		else
		{
			for(Rectangle c: r.getChildren())
			{
				if(!elimNodes.contains(c)&&!elimNodes2.contains(c)&&!c.isPoint())	//Any rectangle-points of the highest level rectangle being removed must be reinserted.
				{
					elimNodes2.add(c);
				}
				allChildren(c);
			}
		}
	}
	//Postcondition: elimNodes and elimNodes2 contain all rectangles that must be deleted. allChildren contains all rectangle-points that must be reinserted.



	//Precondition: At least two points were inserted and enter was pressed.
	public ArrayList<Rectangle> makeRTree(ArrayList<Rectangle> rec, int depth)	//#MERLEISSOSWAG
	{
		returnList.addAll(rec);
		
		if(rec.size()==1){				//Base case: Add the root node to master list of rectangles and return list

			root = rec.get(0);
			return returnList;
		}
		for(int x =0; x< rec.size();x++){
			for(int y=0;y<rec.size()-1;y++){
				if(rec.get(y).getPCenter().getX()>rec.get(y+1).getPCenter().getX())	//Sort rectangles by x (center point)
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
		for(ArrayList<Rectangle> arr:partitionArray){							//Sort rectangles by y (center point)
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
	//Postcondition: An RTree containing all the rectangle-points added before enter was pressed has been formed.


	//Precondition: Search was called, offering a search box
	public ArrayList<Rectangle> search(Rectangle r){
		ArrayList<Rectangle> searchList = new ArrayList<Rectangle>();
		q.clear();
		q.add(root);
		while(!q.isEmpty())
		{
			Rectangle next = q.poll();
			if(next.isPoint())										//This point is inside the search rectangle, add it to the list
			{
				searchList.add(next);
			}
			else
			{
				for(Rectangle c: next.getChildren())
				{
					if(r.overlaps(c))									//If this rectangle overlaps the search area, you must check all of its children
					{		
						q.add(c);
					}
				}
			}
		}
		return searchList;
	}
	//Postcondition: An arraylist of all rectangle-points in the search area have been returned
	
	
	
	
	public Rectangle getMinimumX(){
		return minimumX(root);
	}
	public Rectangle getMinimumY(){
		return minimumY(root);
	}

	//Precondition: The button looking for minimum x has been pressed. An RTree already exists
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
	//Postcondition: The rectangle-point with the smallest x component is returned.

	//Precondition: The button looking for minimum y has been pressed. An RTree already exists
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
	//Postcondition: The rectangle-point with the smallest y component is returned.
}
