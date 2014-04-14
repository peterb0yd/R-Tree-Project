import java.util.ArrayList;


public class RTree {
	
	ArrayList<Rectangle> returnList = new ArrayList<Rectangle>();
	double capacity = 0.0;
	Rectangle root;
	private ArrayList<Rectangle> rectangle1;
	private ArrayList<Rectangle> rectangle2;
	double curr =0; 
	public RTree(int c){
		capacity = c;
	}
	public ArrayList<Rectangle> getRTree(){
		return returnList;
	}

	
	/*
	 * picks two points that are the farthest away (s.t. we are able to maximize area)
	 * and inserting the remaining entries/points into the two groups evenly.	 */
	public ArrayList<Rectangle> split(ArrayList<Rectangle> rect,ArrayList<Rectangle> rect2){
	for(int x=0; x < rect.size(); x++){
		for(int y =0;y < rect.size(); y++){
			double temp= distance(rectangle1.get(x), rectangle2.get(y));
				double maxDistance = 0;
				if(temp > maxDistance){
					maxDistance = temp; 
					ArrayList<Rectangle> maxRect = rectangle1;
					ArrayList<Rectangle> maxRect2 = rectangle2;
				}
			}
		}
	return rect;
	}
	/*
	 * computes the distance formula in order to determine the two farthest points
	 */
	private double distance(Rectangle rectangle, Rectangle rectangle2) {
		//distance formula to dertime the distance between two rectangles
		double distance = Math.sqrt(Math.pow((rectangle1.getP1().getX() - rectangle2.getP1().getX()),2) 
				+ Math.pow(rectangle1.getP1().getY() - rectangle2.getP1().getY(),2));
		return distance; 
	}
	/*
	 * inserts new points/rectangles into the set of rectangles
	 */
	public void insert(ArrayList<Rectangle> rect, ArrayList<Rectangle> rect3){
		//if(the leaf node is not full) add the new element
		if(curr != capacity){
			rect.add(rect3); }
		//the leaf node is full
		else{ 
		//Split the leaf node (dodgeball method)
		//update the directory rectangle
			split(rectangle1,rectangle2);
		}
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
		/*
		 	Base Case: Add the root node to list and return list
		 */
		if(rec.size()==1){
			rec.get(0).setDepth(depth);
			returnList.add(rec.get(0));
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
			int maxUlX = 0;
			int maxUlY = 0;
			int minLrX=9999999;
			int minLrY = 9999999;
			for(Rectangle r: arr){
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
			
			rtrn.add(new Rectangle(new Point(maxUlX,maxUlY),new Point( minLrX,minLrY), depth,arr));
			
		}
		System.out.println(rtrn.size());
		return makeRTree(rtrn, depth +1);
		
		
	}
	
	
	
}
