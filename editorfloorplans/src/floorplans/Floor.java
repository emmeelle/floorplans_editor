package floorplans;

import java.util.ArrayList;

import javax.management.loading.PrivateClassLoader;

import processing.core.*;
import processing.data.XML;

public class Floor {

	public ArrayList<Room> rooms = new ArrayList<Room>();
	public int conncetions_visibility = 2;

	Floor(XML floor) {

		XML[] xmlrooms = floor.getChild("spaces").getChildren("space");
		for (XML xmlroom : xmlrooms) {
			rooms.add(new Room(xmlroom));
		}

	}

	public void display(FloorPlanEditorSketch parent) {
		for (Room room : rooms) {
			for (String[] c : room.connections) {
				int x = 0;
				int y = 0;
				for (Room room2 : rooms) {
					if (room2.id.equals(c[0])) {
						x = room2.centroid[0];
						y = room2.centroid[1];
						// break;
					}
				}
				parent.stroke(255, 0, 0);
				parent.strokeWeight(conncetions_visibility);
				parent.line(x - parent.oo.zx, y - parent.oo.zy, room.centroid[0] - parent.oo.zx,
						room.centroid[1] - parent.oo.zy);
			}
		}
		for (Room room : rooms) {
			room.display(parent);
		}
	}

	public String centroid_selection(int x, int y) {
		String found = "none";
		for (Room r : rooms) {
			if ((Math.abs(r.centroid[0] - x) < 15) && (Math.abs(r.centroid[1] - y) < 15)) {
				found = r.id;
				for (Room r2 : rooms) {
					if (r2 != r) {
						r2.visibility = 0;
					}

				}
				break;
			}
		}
		return found;
	}

	public void change_room_label(String id, String newlabel, String newtype) {
		for (Room r : rooms) {
			if (r.id.equals(id)) {
				r.label = newlabel;
				r.type = newtype;
				break;
			}
		}
	}
	
	public void change_door_type(int x,int y){
		for (Room r:rooms){
			for (Linesegment l:r.linesegments){
				if (l.class_.equals("PORTAL")&&Math.abs(l.points[0]-x)<5&&Math.abs(l.points[1]-y)<5){
					if (l.type.equals("EXPLICIT") && l.features.equals("NORMAL")) {
						l.features = "DOUBLE";
					} else {
						if (l.type.equals("EXPLICIT")) {
							l.type = "IMPLICIT";
							l.features = "NORMAL";
						} else {
							l.type = "EXPLICIT";
						}
					}
				}
			}
		}
		
	}

	public Room add_room(FloorPlanEditorSketch parent, int x, int y, String type, String label) {
		Room newroom = new Room(x, y, type, label);
		rooms.add(newroom);
		return newroom;
	}

	public void delete_room(String id) {
		Room deletion_target = null;
		for (Room r : rooms) {
			r.delete_connection(id);
			if (r.id.equals(id)) {
				deletion_target = r;
			}
		}
		rooms.remove(deletion_target);
	}

	public void set_room_walls(String id, ArrayList<Integer[]> points, ArrayList<Linesegment> ls) {

		for (Room r : rooms) {
			if (r.id.equals(id)) {
				r.boundingp.clear();
				r.linesegments.clear();
				r.boundingp.addAll(points);
				r.linesegments.addAll(ls);
				break;
			}
		}
	}

	public void adjust_room_points() {
		for (Room r : rooms) {
			for (Room r2 : rooms) {
				//adjusting points
				for (Integer[] p : r.boundingp) {
					for (Integer[] p2 : r2.boundingp) {
						if ((Math.abs(p[0] - p2[0]) <= 8) && (Math.abs(p[1] - p2[1]) <= 8)) {
							int newx = (p[0] + p2[0]) / 2;
							int newy = (p[1] + p2[1]) / 2;
							p[0] = newx;
							p2[0] = newx;
							p[1] = newy;
							p2[1] = newy;
						}
					}
				}
				//adjust lineseg (portals)
				for (Linesegment l:r.linesegments){
					if (l.class_.equals("PORTAL")){
					for(Linesegment l2:r2.linesegments){
						if(l2.class_.equals("PORTAL")&&(Math.abs(l.points[0] - l2.points[0]) <= 8) && (Math.abs(l.points[1] - l2.points[1]) <= 8)){
							int newx = (l.points[0] + l2.points[0]) / 2;
							int newy = (l.points[1] + l2.points[1]) / 2;
							Linesegment precl = r.linesegments.get(r.linesegments.indexOf(l)-1);
							Linesegment succl = r.linesegments.get(r.linesegments.indexOf(l)+1);
							Linesegment precl2 = r2.linesegments.get(r2.linesegments.indexOf(l2)-1);
							Linesegment succl2 = r2.linesegments.get(r2.linesegments.indexOf(l2)+1);
							l.points[0]=newx;
							l.points[2]=newx;
							l.points[1]=newy;
							l.points[3]=newy;
							precl.points[2]=newx;
							precl.points[3]=newy;
							succl.points[0]=newx;
							succl.points[1]=newy;
							l2.points[0]=newx;
							l2.points[2]=newx;
							l2.points[1]=newy;
							l2.points[3]=newy;
							precl2.points[2]=newx;
							precl2.points[3]=newy;
							succl2.points[0]=newx;
							succl2.points[1]=newy;
							l2.id=l.id;
						}
					}
				}
				}
			}

		}
		//Grid alignement
//		 for(Room r:rooms){
//		 for (Integer[] p:r.boundingp){
//		
//		 p[0]=p[0]-p[0]%8;
//		 p[1]=p[1]-p[1]%8;
//		 }
//		 for (Linesegment l:r.linesegments){
//			 l.points[0]=l.points[0]-l.points[0]%8;
//			 l.points[1]=l.points[1]-l.points[1]%8;
//			 l.points[2]=l.points[2]-l.points[2]%8;
//			 l.points[3]=l.points[3]-l.points[3]%8;
//		 }
//		 }
	}
	
	public void expand_doors(int resolution){
		for (Room r:rooms){
			for (Linesegment l:r.linesegments){
				if (l.class_.equals("PORTAL")&&l.type.equals("EXPLICIT")&&l.points[0].equals(l.points[2])&&l.points[1].equals(l.points[3])){
					int index = r.linesegments.indexOf(l);
					Linesegment prec = r.linesegments.get(index-1);
					Linesegment succ = r.linesegments.get(index+1);
					int num = (prec.points[1]-succ.points[3]);
					int denom = (prec.points[0]-succ.points[2]);
					int delta=0;
					if (denom==0){//To avoid division by 0
						//Vertical
						if(num>0){
						prec.points[3]=prec.points[3]+resolution;
						succ.points[1]=succ.points[1]-resolution;
						}
						else{
						prec.points[3]=prec.points[3]-resolution;
						succ.points[1]=succ.points[1]+resolution;
						}
					}
					else{
						delta = num/denom;
						if(delta>-1&&delta<1){
							//More horizontal
							if (denom>0){
							prec.points[2]=prec.points[2]+resolution;
							succ.points[0]=succ.points[0]-resolution;
							}
							else{
							prec.points[2]=prec.points[2]-resolution;
							succ.points[0]=succ.points[0]+resolution;
							}
							
						}
						else{
							//More vertical
							if(num>0){
								prec.points[3]=prec.points[3]+resolution;
								succ.points[1]=succ.points[1]-resolution;
								}
								else{
								prec.points[3]=prec.points[3]-resolution;
								succ.points[1]=succ.points[1]+resolution;
								}
						}
					}
					l.points[0]=prec.points[2];
					l.points[1]=prec.points[3];
					l.points[2]=succ.points[0];
					l.points[3]=succ.points[1];
				}
			}
		}
	}
	
	public void check_connections(){
		for (Room r:rooms){
			for (Room r2:rooms){
				if(!r.id.equals(r2.id)){
				for (Linesegment l:r.linesegments){
					for (Linesegment l2:r2.linesegments){
					if(l.id.equals(l2.id)){
						boolean flag=false;
						for (String[] c:r.connections){
							if(c[0].equals(r2.id)){
								flag=true;
								break;
							}
						}
						if (flag==false){
							String[] temparray = {r2.id,l.id,"HORIZONTAL",l.type,"BOTH",l.features};
							r.connections.add(temparray);
						}
						flag=false;
						for (String[] c:r2.connections){
							if(c[0].equals(r.id)){
								flag=true;
								break;
							}
						}
						if (flag==false){
							String[] temparray = {r.id,l.id,"HORIZONTAL",l.type,"BOTH",l.features};
							r2.connections.add(temparray);
						}
					}
					}
				}
			}
			}
			
		}
	}

	public void all_rooms_visible() {
		for (Room r : rooms) {
			r.visibility = 255;
		}
	}
	
	public void fix_portals(){
		for (Room r : rooms) {
			r.fix_portals();
		}
	}
	
	public void to_xml(XML parent){
		XML xmlfloor = parent.addChild("floor");
		XML xmlspaces = xmlfloor.addChild("spaces");
		for (Room r:rooms){
			r.to_xml(xmlspaces);
		}
	}

}
