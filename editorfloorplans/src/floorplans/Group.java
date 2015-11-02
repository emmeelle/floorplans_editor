package floorplans;

import java.util.UUID;
import java.util.ArrayList;
import processing.core.PApplet;
import processing.data.XML;

public class Group {
	public PApplet parent;
	public ArrayList<Node>  nodes;
	public String name;
	public int r;
	public int g;
	public int b;
	public int x;
	public int y;
	
	Group(PApplet _parent){
		this.parent = _parent;
		name = "";
		nodes = new ArrayList<Node>();
	}
	void removeLast(){
		nodes.remove(nodes.size()-1);
	}
	public void addChar(char c){
		name+=c;
	}
	
	public void setPose(int x, int y){
		this.x = x;
		this.y = y;
	}
	public void addNode(Node n){
		nodes.add(n);
	}
	
	void display(){
	    parent.fill(r,g,b);
	    parent.ellipse(x,y,Globals.shape*2,Globals.shape*2);
	    parent.fill(0);
	    parent.textAlign(parent.CENTER);
	    parent.textSize(13);
	    parent.text(name,x+65,y+4);
	}
	
	void setColor(int r, int g, int b) {
	    this.r = r;
	    this.g = g;
	    this.b = b;
		  }
	 void toXML(XML xml){
		 XML group = xml.addChild("group_features");
		 XML xName = group.addChild("name");
		 XML type = group.addChild("type");
		 type.setContent("functional_area");
		 xName.setContent(this.name);
		 group.setString("id",UUID.randomUUID().toString());
		 //id.setContent(UUID.randomUUID().toString());
		 for (int i=0; i<nodes.size(); i++){
			 Node tn = nodes.get(i);
			 XML sid = group.addChild("space");
			 sid.setContent(tn.uid.toString());
		 }
	 }
}
