package floorplans;

import processing.core.PApplet;
import processing.data.XML;
import java.util.UUID;

public class Connection{
      PApplet parent;
	  public int first;
	  public int second;
	  public int x1,y1,x2,y2;
	  // x e y sono la posizione della porta
	  public int x,y;
	  // e questo è l'indice della porta per la stanza 1
	  public int door1; 
	  // e questo e' per la stanza 2
	  public int door2;
	  // UUID delle due stanze
	  public UUID uid1, uid2;
	  // UUID DEL LINESEGMENT
	  public UUID segment_uid;
	  public Offset offset;
	  
	  Connection(PApplet _parent, int first, int second, int x1, int y1, int x2, int y2, UUID uid1, UUID uid2, UUID segment_uid,Offset offset) {
	    this.parent = _parent;
		this.first = first;
	    this.second = second;
	    this.x1 = x1;
	    this.y1 = y1;
	    this.x2 = x2;
	    this.y2 = y2;
	    // TODO Aggiungere l'indice della porta.
	    this.x = 0;
	    this.y =0;
	    this.door1 = 0;
	    this.door2 = 0;
	    this.uid1 = uid1;
	    this.uid2 = uid2;
	    this.segment_uid = segment_uid;
	    this.offset = offset;
	  }
	 void display() {
		parent.strokeWeight(2);
	    parent.fill(111);
	    parent.line(x1-offset.zx,y1-offset.zy,x2-offset.zx,y2-offset.zy);
	 } 
	 
	 void displayTopological(){
			parent.strokeWeight(2);
		    parent.fill(111);
		    parent.line(x1/Globals.bigzoom,y1/Globals.bigzoom,x2/Globals.bigzoom,y2/Globals.bigzoom);
	 }
	 void setDoor(int x, int y, int door1, int door2){
		this.x=x;
		this.y=y;
		this.door1=door1;
		this.door2=door2;
	 }
	 void toXMLPLAIN(XML xml){
		 // TODO STAMPARE ANCHE DOOR1 e DOOR2
	   XML tmp = xml.addChild("connection");
	   XML id1 = tmp.addChild("id");
	   id1.setContent(Integer.toString(first));
	   XML id2 = tmp.addChild("id");
	   id2.setContent(Integer.toString(second));
	 }
	 
	 void toXML(XML xml){
		 // TODO STAMPARE ANCHE DOOR1 e DOOR2
	   XML tmp = xml.addChild("connection");
	   XML id1 = tmp.addChild("id");
	   id1.setContent(Integer.toString(first));
	   XML id2 = tmp.addChild("id");
	   id2.setContent(Integer.toString(second));
	 }
	 
	}