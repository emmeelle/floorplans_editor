package floorplans;
import java.util.*;
import java.awt.geom.Line2D;

import processing.core.PApplet;
import processing.data.XML;
import java.util.UUID;
import static java.lang.Math.pow;

public class NodeGeometry{
  PApplet parent;
  public ArrayList<Integer> X;
  public ArrayList<Integer> Y;
  public ArrayList<Line2D.Float> L;
  public ArrayList<Integer> D;
  // indice della connessione, tra le connesioni globali, a cui appartiene la porta i-esima (D).
  // TODO LA GESTIONE DELLE CONNESSIONI E' FOLLE COSI VA UNIFICATA
  public ArrayList<Connection> C;
  // UUID di tutte le porte. le uso per stamparle e per associarle ai linesegment.
  public ArrayList<UUID> D_U;
  // ELENCO DELLE PORTE IMPLICITE
  public ArrayList<Integer> D_IMPLICIT;
  
  public Offset offset;
  public int resolution = Globals.resolution;
  private int x1,y1;
  
  private int maxX=0, maxY=0, minX=0, minY=0;
  
  NodeGeometry(PApplet _parent){
  this.parent = _parent;
  X = new ArrayList<Integer>();
  Y = new ArrayList<Integer>();
  L = new ArrayList<Line2D.Float>();
  // Porte
  D = new ArrayList<Integer>();
  C = new ArrayList<Connection>();
  D_U = new ArrayList<UUID>();
  D_IMPLICIT = new ArrayList<Integer>();
  }
  
  void setOffset(Offset o){
	  this.offset = o;
  }
  void addPoint(int x, int y) {
      X.add(x);
      Y.add(y);
      if (X.size() != 1) {
        L.add(new Line2D.Float(x,y,x1,y1));
      }
      x1 = x;
      y1 = y;
  }
  
  void addDoor(int x, int y, boolean t) {
	  
	  //TODO Sistemare in aniera piu sensata, mettendo un try-catch
	  // FIX SCEMO: SE LA PRIMA PORTA E' ANCHE IL PRIMO SEGMENTO ALLORA NON LA AGGIUNGO
	  if (X.size()==0)
		  return;
	  
	  // TODO LA PORTA E' UN PUNTO: VA RESA UN SEGMENTO.
	  // TODO LA PORTA DEVE AVERE UN MATCHING CON GLI ALTRI PUNTI PIU DIFFICILE.
      D.add(this.pointIndex(x, y));
      D_U.add(UUID.randomUUID());
      if (t == false)
    	  // Aggiungo l'indice del punto della porta implicita all'eelenco delle porte implicite
    	  D_IMPLICIT.add(this.pointIndex(x, y));

  }
  
  void addConnection(Connection c){
	  C.add(c);
  }
  
  int lineSize() {
    return L.size();
  }
  
  // restituisce l'indice di un punto se è parte dell'offset. -1 al contrario.
  int pointIndex(int x, int y) {
  int tmpX,tmpY;
  for (int i=0; i < X.size(); i++) {
    tmpX = X.get(i);
    tmpY = Y.get(i);
    if  (pow(tmpX-x,2)+pow(tmpY-y,2) < Globals.p_distance*Globals.p_distance) {
      return i;
    }
  }
  return -1;
  }
  
  void movePoint(int directions){
	  int index =this.X.size() -1;
	  // 1 su 2 giu 3 dx 4 sx 
	  switch (directions) {
	  case 1: 
		  Y.set(index,Y.get(index)-Globals.step);
		  L.get(L.size()-1).y1 -= (float)Globals.step;
		  break;
	  case 2: 
		  Y.set(index,Y.get(index)+Globals.step);
		  L.get(L.size()-1).y1 += (float)Globals.step;
		  break;
	  case 3: 
		  X.set(index,X.get(index)+Globals.step);
		  L.get(L.size()-1).x1 += (float)Globals.step;
		  break;
	  case 4: 
		  X.set(index,X.get(index)-Globals.step);
		  L.get(L.size()-1).x1 -= (float)Globals.step;
		  break;
	  }
	  //TODO MUOVO SU SIA IL PUNTO CHE QUELLI CHE CI SONO PRIMA
  }
  
  void setLastX(int nx) {
	  // TODO FARE sia linea che punto
 	  X.set(X.size()-1, nx);
 	  // TODO check se L.size() risolve
	  if (L.size()!=0){
		  L.get(L.size()-1).x1=(float)nx;
		  x1 = nx;
		  }
	  }
 
  void setLastY(int ny) {
	  // TODO Fare sia linea che punto
	  Y.set(Y.size()-1, ny);
	  if (L.size()!=0){
		L.get(L.size()-1).y1=(float)ny;
	  	y1 = ny;}
	  
	  }
  
  void display(){
	  parent.stroke(10);
	  // stampo le linee
	  if (X.size() <= 1){
		  if (X.size() == 1){
			    parent.ellipse(X.get(0)-offset.zx,Y.get(0)-offset.zy,5,5);
		  }  
		  return;
		  }
	  for (int i=1; i < X.size(); i++){
		    parent.line(Math.round(L.get(i-1).getX1())-offset.zx,Math.round(L.get(i-1).getY1())-offset.zy,Math.round(L.get(i-1).getX2())-offset.zx,Math.round(L.get(i-1).getY2())-offset.zy);
		    //line(X.get(i-1),Y.get(i-1),X.get(i),Y.get(i));
		    if (i==1) {
		    	parent.stroke(255,55,55);
		    	parent.ellipse(X.get(i-1)-offset.zx,Y.get(i-1)-offset.zy,5,5);
		    	parent.stroke(10);
		    	}
		    else 
		    	parent.ellipse(X.get(i-1)-offset.zx,Y.get(i-1)-offset.zy,5,5);
		    parent.ellipse(X.get(i)-offset.zx,Y.get(i)-offset.zy,5,5);
		  }
  }
  
  void displayDoors(){
	    //parent.fill(239,192,167);
	   	parent.fill(255);
	    for (int j=0; j < D.size();j++){
    	  int i = D.get(j);
    	  
    	  // TODO STAMPO ANCHE LA PORTA COME SEGMENTO // LO STAMPO INTORNO AL PUNTO.
    	  parent.stroke(Globals.r_door,Globals.g_door,Globals.b_door);
    	  Line2D.Float tmpLine = L.get(i-1);
    	  // EXTODO INVERTO I PUNTI QUANDO GLI STAMPO CHECK SE E' OK -> DOVREBBE ESSERE OK
    	  int x1 = (int) tmpLine.getX2();
    	  int x2 = (int) tmpLine.getX1();
    	  int y1 = (int) tmpLine.getY2();
    	  int y2 = (int) tmpLine.getY1();
    	  parent.strokeWeight(3);
    	  if (Math.abs(x1-x2) >= Globals.p_distance) 
    		  parent.line(X.get(i)-offset.zx-Globals.default_resolution/2,Y.get(i)-offset.zy,X.get(i)-offset.zx + Globals.default_resolution/2,Y.get(i)-offset.zy);
    	  else 
    		  parent.line(X.get(i)-offset.zx,Y.get(i)-offset.zy-Globals.default_resolution/2,X.get(i)-offset.zx,Y.get(i)-offset.zy+Globals.default_resolution/2);
		  // EXTODO INVERTO I PUNTI QUANDO GLI STAMPO CHECK SE E' -> DOVREBBE ESSERE OK
    	  parent.strokeWeight(1);
	      parent.rect(X.get(i)-3-offset.zx,Y.get(i)-3-offset.zy,7,7);
	    }
  }
 
  boolean closedRoom(){
	  if (this.X.size()<3)
		  return false;
	  int tmpX1 = X.get(0);
	  int tmpY1 = Y.get(0);
	  int tmpX2 = X.get(X.size()-1);
	  int tmpY2 = Y.get(Y.size()-1);
	  if  (pow(tmpX1-tmpX2,2)+pow(tmpY1-tmpY2,2) < Globals.p_distance*Globals.p_distance) {
		  // TODO la linea che va dal primo all'ultimo c'è già?
	      return true;
	      }
	  return false;
  }
 
  int removeLastPoint(){
	  // restituisco 1 se era l'unico punto. Altrimenti restituisco 0.
	  
	 // TODO: SE IL PRIMO PUNTO E' DOOR, RIMUOVER ANCHE DOOR!
	  if (X.size() == 1){
		  X.remove(X.size()-1);
		  Y.remove(Y.size()-1);
		  return 1;
	  }
	  // TODO CONTROLLARE SE QUESTO FUNZIONA
	  // TODO MODIFICARE IN MODO TALE CHE RIESCA A GESTIRE IL CANCELLAMENTO DI UNA CONNESSIONE
	  //QUI DEVO CANCELLARE ANCHE IL SEGMENTO FITTIZIO CHE INSERISCO; ALTRIMENTI SE NE HO 0 MI RIMANE IL SEGMENTO FITTIZIO
	  if (D.indexOf(X.size()-1)!= -1){
		  D_U.remove(D.indexOf(X.size()-1));
		  D.remove(D.indexOf(X.size()-1));
	  }
	  x1 = X.get(X.size()-2);
	  y1 = Y.get(Y.size()-2);
	  X.remove(X.size()-1);
	  Y.remove(Y.size()-1);
	  L.remove(L.size()-1);
	  return 0;
  }

  void toXML(XML xml){

	  computeBoundingBox();
	  XML bb = xml.addChild("bounding_box");
	  XML xMaxX = bb.addChild("maxx");
	  pointXML(xMaxX,maxX);
	  XML xMaxY = bb.addChild("maxy");
	  pointXML(xMaxY,maxY);
	  XML xMinX = bb.addChild("minx");
	  pointXML(xMinX,minX);
	  XML xMinY = bb.addChild("minY");
	  pointXML(xMinY,minY);
	  XML polygon = xml.addChild("bounding_polygon");
	  for (int i=0;i<X.size(); i++) {
		  pointXML(polygon,i);
	  }
	  XML lines = xml.addChild("space_representation");
	  for (int i=0;i<L.size();i++) {
		  lineXML(lines,i);
	  }
	  XML portals = xml.addChild("portals");
	  connectionsXML(portals);
	  }
  

  private void computeBoundingBox(){
	  for (int i=1; i<X.size();i++){
		  if (X.get(i)>=X.get(maxX))
			  maxX = i;
		  if (X.get(i)<=X.get(minX))
			  minX = i;
		  if (Y.get(i)>=X.get(maxY))
			  maxY = i;
		  if (Y.get(i)<=X.get(minY))
			  minY = i;
	  }
  }
  
  // EXTODO CHECK SE ZOOM FUNZIONA -> OK DOVREBBE ANDARE
  private void pointXML(XML xml,int i){
	   XML xpoint = xml.addChild("point");
	   xpoint.setInt("x",X.get(i)/Globals.bigzoom);
	   xpoint.setInt("y",Y.get(i)/Globals.bigzoom);
  }
  private void pointXML(XML xml,int x,int y){
	   XML xpoint = xml.addChild("point");
	   xpoint.setInt("x",x/Globals.bigzoom);
	   xpoint.setInt("y",y/Globals.bigzoom);
 }
  
  private void lineXML(XML xml, int index){
	  UUID tmpID;
	  Line2D.Float tmpLine = L.get(index);
	  // EXTODO INVERTO I PUNTI QUANDO GLI STAMPO CHECK SE E' OK -> DOVREBBE ESSERE OK
	  int x1 = (int) tmpLine.getX2();
	  int x2 = (int) tmpLine.getX1();
	  int y1 = (int) tmpLine.getY2();
	  int y2 = (int) tmpLine.getY1();
	  int isdoor = -1;
	  for (int i=0; i<D.size(); i++) {
		  int j = D.get(i);
		  if ((X.get(j) == x1)&&(Y.get(j)==y1)){
			  isdoor = j;
			  break;
		  }
	  }
	  // TODO QUESTO VA RIMOSSO E AGGIUNGO IN MODO PIU PRECISO. NON POSSO AGGIUNGERE UN DOPPIO SEGMENTO
	  if (isdoor != -1) {
		  //ho trovato l'index, è una porta, la aggiungo.
			 // altrimenti e' una porta.
			 //SE E' UNA PORTA ALLORA AGGIUNGO UN ALTRO SEGMENTO FITTIZIO
		  XML doorLinesegment = xml.addChild("linesegment");
		  tmpID= D_U.get(D.indexOf(isdoor));
		  doorLinesegment.setString("id",tmpID.toString());
		  pointXML(doorLinesegment,isdoor);
		  pointXML(doorLinesegment,isdoor);
		  XML lclassxml = doorLinesegment.addChild("class");
		  lclassxml.setContent("PORTAL");
		  XML ltypexml = doorLinesegment.addChild("type");
		  // TODO CHECCKARE SE IMPLICIT E EXPLICIT VANNO
		  if (D_IMPLICIT.indexOf(isdoor)!=-1) {
			  ltypexml.setContent("IMPLICIT");			  
		  }
		  else 
			  ltypexml.setContent("EXPLICIT");
		  
	  }
	  XML linesegment = xml.addChild("linesegment");
	  tmpID = UUID.randomUUID();
	  linesegment.setString("id",tmpID.toString());
	  pointXML(linesegment,x1,y1);
	  pointXML(linesegment,x2,y2);
	  XML lclassxml = linesegment.addChild("class");
	  lclassxml.setContent("WALL");  
	  XML ltypexml = linesegment.addChild("type");
	  ltypexml.setContent("EXPLICIT");
  }
  
  private void connectionsXML(XML xml){
	  for(int i=0; i<C.size(); i++){
		  //TODO RENDERLA HORIZONTAL O VERTICAL IMPLICIT O ESPLICIT
		  Connection tmpC = C.get(i);
		  XML portal = xml.addChild("portal");
		  XML c_id = portal.addChild("linesegment");
		  c_id.setContent(tmpC.segment_uid.toString());
		  XML classxml = portal.addChild("class");
		  classxml.setContent("HORIZONTAL");
		  XML typexml = portal.addChild("type");
		  typexml.setContent("EXPLICIT");
		  XML direction = portal.addChild("direction");
		  direction.setContent("BOTH");
		  XML other_room = portal.addChild("target");
		  XML t_id1 = other_room.addChild("id");
		  t_id1.setContent(tmpC.uid1.toString());
		  XML t_id2 = other_room.addChild("id");
		  t_id2.setContent(tmpC.uid2.toString());
	  }
  }
  
  public void addDoorUID(int x, int y, UUID uid)
  {
      int index  = D.indexOf(this.pointIndex(x, y));
      D_U.set(index, uid);
 	 
  }
}