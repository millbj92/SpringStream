package hello;

import java.util.ArrayList;

public class ItemResponse { 
	private String parent;
	private ArrayList<Item> dirs;
	
	public ItemResponse(String p, ArrayList<Item> d)
	{
		parent = p;
		dirs = d;
	}
	
	public String getParent(){
		return parent;
	}
	
	public ArrayList<Item> getItems(){
		return dirs;
	}
}
