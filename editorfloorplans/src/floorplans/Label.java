package floorplans;

import processing.core.PApplet;
import processing.data.XML;
import processing.core.*;

public class Label{
      PApplet parent;
	  public String name;
	  //type e' R o C
	  public String type;
	  public int r;
	  public int g;
	  public int b;
	  public int x;
	  public int y;
	  public int shape;
	  public boolean over;
	  public boolean selected;
	  
	  
	  Label ( PApplet _parent, String  name, String type, int r, int g, int b, int shape, int x, int y) {
	    this.parent = _parent;
		this.name = name ;
	    this.type = type ;
	    this.r = r;
	    this.g = g;
	    this.b = b;
	    this.shape = shape;
	    this.x = x;
	    this.y = y;
	    this.over = false;
	    this.selected = false;
	  }
	  
	  boolean occupied(int x,int y) {
	    if ( Math.sqrt(Math.pow(this.x-x,2) + Math.pow(this.y-y,2)) < shape )
	      return true;
	    else 
	      return false;
	  }
	  void setX(int x) {
	    this.x = x;
	  }
	  
	  void switch_val() {
	    this.selected = !this.selected;
	  }
	  void setY(int y) {
	    this.y = y;
	  }
	  
	  // CHecking if mouse is over the Bubble
	  void rollover(float px, float py) {
	    float d = parent.dist(px,py,x,y);
	    if (d < shape/2) {
	      over = true; 
	    } else {
	      over = false;
	    }
	  }
	  
	  // Display the Bubble
	  void display() {
	    if (over) 
	        parent.stroke(255);
	    else 
	        parent.stroke(0);
	    if (selected) 
	        parent.stroke(180);
	    parent.fill(r,g,b);
	    parent.ellipse(x,y,shape,shape);
	    parent.fill(0);
	    if (name.length()<=10){
		    parent.textAlign(parent.CENTER);
		    parent.text(name,x,y+shape/2+20);
		    }
	    else {
	    	String[] splitStr = name.split("\\s+");
	    	parent.textSize(8);
	    	for (int i=0; i<splitStr.length; i++){
			    parent.textAlign(parent.CENTER);
			    parent.text(splitStr[i],x,y+shape/2+12+i*8);
	    	}
	    	parent.textSize(11);
	    }

	  }
	  void toXML(XML xml)
	  {
		  // TODO sistemare poi qui le multilabel ed i vari dati che abbiamo.
		  XML labels = xml.addChild("labels");
		  XML xtype = labels.addChild("type");
		  xtype.setContent(this.type);
		  XML xLab = labels.addChild("label");
		  xLab.setContent(this.name);
	  }
	}