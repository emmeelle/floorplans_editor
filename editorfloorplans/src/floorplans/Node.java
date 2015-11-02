package floorplans;

import java.awt.geom.Line2D;

import processing.core.PApplet;

import java.util.UUID;

import processing.data.XML;

public class Node{
  PApplet parent;
  public UUID uid;
  public int id;
  public int x;
  public int y;
  public Label l;
  public int type;
  public boolean connected;
  //colore del nodo
  public int r,g,b;
  // colore della linea (stroke)
  public int sr, sg, sb;
  public int opacity;
  public NodeGeometry geom;
  public Offset offset;
  
  Node ( PApplet _parent, int id, int x, int y, Label l,int type, boolean connected, int r,int g, int b) {
    this.parent = _parent;
	this.id = id;
    this.x = x ;
    this.y = y ;
    this.l = l;
    this.type = type;
    this.connected = connected;
    this.r = r;
    this.g = g;
    this.b = b;
    this.sr = 60;
    this.sg = 60;
    this.sb = 60;
    this.opacity =Globals.opacity2;
    this.geom = new NodeGeometry(this.parent);
    this.uid = UUID.randomUUID();
    
  }
  
  boolean occupied(int x,int y) {
    if ( Math.sqrt(Math.pow(this.x-x,2) + Math.pow(this.y-y,2)) < Globals.shape )
      return true;
    else 
      return false;
  }
  
  void display() {
    parent.stroke(sr,sg,sb);
    //strokeWeight(2);
    parent.fill(r,g,b,opacity);
    parent.ellipse(this.x-offset.zx, this.y-offset.zy, Globals.shape, Globals.shape);
    
    // stampo il contorno.
    this.geom.display();
    this.geom.displayDoors();
  }
  
  void displayTopological() {
	    parent.stroke(sr,sg,sb);
	    //strokeWeight(2);
	    parent.fill(r,g,b,opacity);
	    parent.ellipse(this.x/Globals.bigzoom, this.y/Globals.bigzoom, Globals.shape, Globals.shape);
  }
  
  void setColor(int r, int g, int b) {
    this.r = r;
    this.g = g;
    this.b = b;
  }
    
  void setOpacity(int opacity) {
    this.opacity = opacity;
  }
  
  void setStroke(int r, int g, int b) {
	  this.sr = r;
	  this.sg = g;
	  this.sb = b;
  }
  
  void setLastX(int nx) {
	  this.geom.setLastX(nx);
  }
  void setLastY(int ny) {
	  this.geom.setLastY(ny);
  }
  
  void movePoint(int directions) {
	  this.geom.movePoint(directions);
  }
  
  boolean closedRoom(){
	  return this.geom.closedRoom();
  }
  
  int removeLastPoint(){
	  return this.geom.removeLastPoint();
  }
  
 void toXMLPLAIN(XML xml){
   XML Xnode = xml.addChild("node");
   Xnode.setString("uid",uid.toString());
   XML Xid = Xnode.addChild("id");
   Xid.setContent(Integer.toString(id));
   XML Xlabel = Xnode.addChild("label");
   Xlabel.setContent(l.name);
   XML Xtype = Xnode.addChild("type");
   Xtype.setContent(l.type);
   XML Xcolor = Xnode.addChild("color");
   Xcolor.setInt("r",r);
   Xcolor.setInt("g",g);
   Xcolor.setInt("b",b);
   XML Xpose = Xnode.addChild("position");
   Xpose.setInt("x",x/Globals.bigzoom);
   Xpose.setInt("y",y/Globals.bigzoom);
 }
 
 void toXML(XML xml){
		   XML Xnode = xml.addChild("space");
		   Xnode.setString("id",uid.toString());
		   //setContent(Integer.toString(id));
		   l.toXML(Xnode);
//		   XML Xcolor = Xnode.addChild("color");
//		   Xcolor.setInt("r",r);
//		   Xcolor.setInt("g",g);
//		   Xcolor.setInt("b",b);
		   XML Xcent = Xnode.addChild("centroid");
		   XML Xpose = Xcent.addChild("point");
		   Xpose.setInt("x",x/Globals.bigzoom);
		   Xpose.setInt("y",y/Globals.bigzoom);
		   this.geom.toXML(Xnode);
		   
 }
 
 
 void addPoint(int x, int y)
 {
	 geom.addPoint(x, y);
 }
 
 void addDoor(int x, int y, boolean t)
 {
	 geom.addDoor(x, y, t);
 }
 
 void addConnection( Connection c){
	 this.geom.addConnection(c);
 }
 
 int pointIndex(int x, int y) {
	 return geom.pointIndex(x, y);
 }
 
 boolean firstPoint() {
	 return this.geom.X.size() == 0;
 }
 
 public void addDoorUID(int x, int y, UUID uid)
 {
	 this.geom.addDoorUID(x, y, uid);
 }
 
 public void setOffset(Offset o){
	 this.offset=o;
	 this.geom.setOffset(o);
 }
}