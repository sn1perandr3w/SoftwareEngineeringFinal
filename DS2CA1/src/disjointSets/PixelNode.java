package disjointSets;

import javafx.scene.paint.Color;

public class PixelNode {

	private int UID;
	private static int UIDCounter = 0;
	private int parentID = -1;
	private int root;
	private Color color;
	
	private int children = 0;
	
	private int x;
	private int y;
	
	private int minX;
	private int minY;
	
	private int maxX;
	private int maxY;

	
	
	public PixelNode(int coordX, int coordY, Color colorOfPixel)
	{
		this.x = coordX;
		this.y = coordY;
		
		this.color = colorOfPixel;
		
		UIDCounter++;
		this.UID = UIDCounter;
	}
	
	
	
	public int getUID()
	{
		return UID;
	}
	
	public int getParentID() 
	{
		return parentID;
	}
	
	public void setParentID(int ID) 
	{
		this.parentID = ID;
	}
	
	public int getRoot() 
	{
		return root;
	}
	
	public void setRoot(int Root) 
	{
		this.root = Root;
	}
	
	public Color getColor() 
	{
		return color;
	}
	
	public int getXpos() 
	{
		return x;
	}
	
	public int getYpos() 
	{
		return y;
	}
	
	public int getMinX() 
	{
		return minX;
	}
	
	public int getMinY() 
	{
		return minY;
	}
	
	public int getMaxX() 
	{
		return maxX;
	}
	
	public int getMaxY() 
	{
		return maxY;
	}
	
	public void setMaxX(int xCoord) 
	{
		this.maxX = xCoord;
	}
	
	public void setMaxY(int yCoord) 
	{
		this.maxY = yCoord;
	}
	
	public void setMinX(int xCoord) 
	{
		this.minX = xCoord;
	}
	
	public void setMinY(int yCoord) 
	{
		this.minY = yCoord;
	}
	
	public void addChildCount(int matches) 
	{
		this.children = matches;
	}
	
	public int getChildCount() 
	{
		return children;
	}
	
	
	public Boolean isRoot() 
	{
		if(parentID == root) {
		return true;
		}
		return false;
	}
	
	
}
