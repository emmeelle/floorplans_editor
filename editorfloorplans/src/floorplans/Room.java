package floorplans;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import processing.core.PApplet;
import processing.data.XML;

public class Room {
	String id;
	String type;
	String label;
	int[] centroid = new int[2];
	ArrayList<Integer[]> boundingp = new ArrayList<Integer[]>();
	ArrayList<String[]> connections = new ArrayList<String[]>();
	int visibility;
	ArrayList<Linesegment> linesegments = new ArrayList<Linesegment>();
	Integer[] boundingbox = { Integer.MAX_VALUE, 0, 0, Integer.MAX_VALUE, Integer.MIN_VALUE, 0, 0, Integer.MIN_VALUE };

	Room(XML room) {
		visibility = 255;
		id = room.getString("id");
		type = room.getChild("labels").getChild("type").getContent();
		label = room.getChild("labels").getChild("label").getContent();
		centroid[0] = room.getChild("centroid").getChild("point").getInt("x") * 2;
		centroid[1] = room.getChild("centroid").getChild("point").getInt("y") * 2;
		XML[] xmlpoints = room.getChild("bounding_polygon").getChildren("point");
		for (XML xmlpoint : xmlpoints) {
			boundingp.add(new Integer[] { xmlpoint.getInt("x") * 2, xmlpoint.getInt("y") * 2 });
		}
		XML[] xmlportals = room.getChild("portals").getChildren("portal");
		for (XML xmlportal : xmlportals) {
			if (xmlportal.getChild("id") != null) {
				String tempid = xmlportal.getChild("id").getContent();
				xmlportal.removeChild(xmlportal.getChild("id"));
				XML newchild = xmlportal.addChild("linesegment");
				newchild.setContent(tempid);
			}
			XML[] candidates = xmlportal.getChild("target").getChildren("id");
			for (XML candidate : candidates) {
				if (!candidate.getContent().equals(id)) {
					String[] temparray = { candidate.getContent(), xmlportal.getChild("linesegment").getContent(),
							xmlportal.getChild("class").getContent(), xmlportal.getChild("type").getContent(),
							xmlportal.getChild("direction").getContent(), xmlportal.getChild("features").getContent() };
					connections.add(temparray);
				}
			}
		}
		XML[] xmllineseg = room.getChild("space_representation").getChildren("linesegment");
		for (XML lineseg : xmllineseg) {
			XML[] points = lineseg.getChildren("point");
			linesegments.add(
					new Linesegment(points[0].getInt("x") * 2, points[0].getInt("y") * 2, points[1].getInt("x") * 2,
							points[1].getInt("y") * 2, lineseg.getString("id"), lineseg.getChild("class").getContent(),
							lineseg.getChild("type").getContent(), lineseg.getChild("features").getContent()));
		}

	}

	Room(int newx, int newy, String newtype, String newlabel) {
		visibility = 255;
		id = UUID.randomUUID().toString();
		type = newtype;
		label = newlabel;
		centroid[0] = newx;
		centroid[1] = newy;
	}

	public void display(FloorPlanEditorSketch parent) {
		int r = 60;
		int g = 60;
		int b = 60;
		// CENTROID
		parent.stroke(60, 60, 60, visibility);
		parent.strokeWeight(1);
		for (Label l : parent.labels) {
			if (l.name.equals(label)) {
				r = l.r;
				g = l.g;
				b = l.b;
			}
		}
		parent.fill(r, g, b, visibility);
		parent.ellipse(centroid[0] - parent.oo.zx, centroid[1] - parent.oo.zy, 15, 15);
		parent.stroke(10, visibility);
		// WALLS
		for (int i = 0; i < boundingp.size() - 1; i++) {
			parent.stroke(255, 0, 255);
			parent.strokeWeight(2);
			parent.line(boundingp.get(i)[0] - parent.oo.zx, boundingp.get(i)[1] - parent.oo.zy,
					boundingp.get(i + 1)[0] - parent.oo.zx, boundingp.get(i + 1)[1] - parent.oo.zy);
		}
		// LINESEG
		for (Linesegment l : linesegments) {
			parent.stroke(0, 255, 255);
			parent.line(l.points[0] - parent.oo.zx, l.points[1] - parent.oo.zy, l.points[2] - parent.oo.zx,
					l.points[3] - parent.oo.zy);
		}
		// CORNERS
		parent.stroke(60, 60, 60, visibility);
		parent.strokeWeight(1);
		parent.fill(r, g, b, visibility);
		for (Integer[] xy : boundingp) {
			parent.ellipse(xy[0] - parent.oo.zx, xy[1] - parent.oo.zy, 5, 5);
		}
		// DOORS
		for (Linesegment l : linesegments) {
			if (l.class_.equals("PORTAL")) {
				parent.strokeWeight(2);
				if (l.type.equals("EXPLICIT") && l.features.equals("NORMAL")) {
					parent.fill(255, 0, 255);
					parent.stroke(0, 255, 0);
				} else {
					if (l.type.equals("EXPLICIT")) {
						parent.fill(255, 255, 0);
						parent.stroke(0, 255, 0);
					} else {
						parent.fill(255, 0, 255);
						parent.stroke(0, 0, 255);
					}
				}
				if ((l.points[0].equals(l.points[2])) && (l.points[1].equals(l.points[3]))) {
					parent.ellipse(l.points[0] - parent.oo.zx, l.points[1] - parent.oo.zy, 7, 7);
				} else {
					parent.line(l.points[0] - parent.oo.zx, l.points[1] - parent.oo.zy, l.points[2] - parent.oo.zx,
							l.points[3] - parent.oo.zy);
				}
			}
		}

	}

	public void delete_connection(String id) {
		String[] target = null;
		for (String[] c : connections) {
			if (c[0].equals(id))
				target = c;
		}
		if (target != null)
			connections.remove(target);
	}

	private void computeBoundingBox() {
		for (Integer[] i : boundingp) {
			if (i[0] < boundingbox[0]) {
				boundingbox[0] = i[0];
				boundingbox[1] = i[1];
			}
			if (i[1] < boundingbox[3]) {
				boundingbox[3] = i[1];
				boundingbox[2] = i[0];
			}
			if (i[0] > boundingbox[4]) {
				boundingbox[4] = i[0];
				boundingbox[5] = i[1];
			}
			if (i[1] > boundingbox[7]) {
				boundingbox[7] = i[1];
				boundingbox[6] = i[0];
			}

		}
	}

	private void computeCentroid() {
		// TODO eventualmente implementare...
	}

	public void fix_portals() {
		for (Linesegment l : linesegments) {
			if (l.class_.equals("PORTAL")) {
				for (String[] c : connections)
					if (c[1].equals(l.id)) {
						c[3] = l.type;
					}
			}
		}
	}

	public void to_xml(XML parent) {
		XML xmlspace = parent.addChild("space");
		xmlspace.setString("id", id);
		XML xmllabels = xmlspace.addChild("labels");
		XML xmlltype = xmllabels.addChild("type");
		xmlltype.setContent(type);
		XML xmllabel = xmllabels.addChild("label");
		xmllabel.setContent(label);
		XML xmlcent = xmlspace.addChild("centroid");
		XML xmlcentpoint = xmlcent.addChild("point");
		computeCentroid();
		xmlcentpoint.setInt("x", centroid[0] / 2);
		xmlcentpoint.setInt("y", centroid[1] / 2);
		computeBoundingBox();
		XML xmlboundingbox = xmlspace.addChild("bounding_box");
		XML xmlmaxx = xmlboundingbox.addChild("maxx");
		XML xmlmaxxp = xmlmaxx.addChild("point");
		xmlmaxxp.setInt("x", boundingbox[4] / 2);
		xmlmaxxp.setInt("y", boundingbox[5] / 2);
		XML xmlmaxy = xmlboundingbox.addChild("maxy");
		XML xmlmaxyp = xmlmaxy.addChild("point");
		xmlmaxyp.setInt("x", boundingbox[6] / 2);
		xmlmaxyp.setInt("y", boundingbox[7] / 2);
		XML xmlminx = xmlboundingbox.addChild("minx");
		XML xmlminxp = xmlminx.addChild("point");
		xmlminxp.setInt("x", boundingbox[0] / 2);
		xmlminxp.setInt("y", boundingbox[1] / 2);
		XML xmlminy = xmlboundingbox.addChild("miny");
		XML xmlminyp = xmlminy.addChild("point");
		xmlminyp.setInt("x", boundingbox[2] / 2);
		xmlminyp.setInt("y", boundingbox[3] / 2);
		XML xmlboundingpol = xmlspace.addChild("bounding_polygon");
		for (Integer[] i : boundingp) {
			XML xmlpoint = xmlboundingpol.addChild("point");
			xmlpoint.setInt("x", i[0] / 2);
			xmlpoint.setInt("y", i[1] / 2);
		}
		XML xmlspacerep = xmlspace.addChild("space_representation");
		for (Linesegment l : linesegments) {
			XML xmllineseg = xmlspacerep.addChild("linesegment");
			xmllineseg.setString("id", l.id);
			XML xmlpoint = xmllineseg.addChild("point");
			xmlpoint.setInt("x", l.points[0] / 2);
			xmlpoint.setInt("y", l.points[1] / 2);
			XML xmlpoint2 = xmllineseg.addChild("point");
			xmlpoint2.setInt("x", l.points[2] / 2);
			xmlpoint2.setInt("y", l.points[3] / 2);
			XML xmllsclass = xmllineseg.addChild("class");
			xmllsclass.setContent(l.class_);
			XML xmllstype = xmllineseg.addChild("type");
			xmllstype.setContent(l.type);
			XML xmllsfeatures = xmllineseg.addChild("features");
			xmllsfeatures.setContent(l.features);
		}
		XML xmlportals = xmlspace.addChild("portals");
		for (String[] c : connections) {
			XML xmlportal = xmlportals.addChild("portal");
			XML xmlplineseg = xmlportal.addChild("linesegment");
			xmlplineseg.setContent(c[1]);
			XML xmlpclass = xmlportal.addChild("class");
			xmlpclass.setContent(c[2]);
			XML xmlptype = xmlportal.addChild("type");
			xmlptype.setContent(c[3]);
			XML xmlpfeatures = xmlportal.addChild("features");
			xmlpfeatures.setContent(c[5]);
			XML xmlpdirection = xmlportal.addChild("direction");
			xmlpdirection.setContent(c[4]);
			XML xmltarget = xmlportal.addChild("target");
			XML xmlidtarget = xmltarget.addChild("id");
			xmlidtarget.setContent(id);
			XML xmlidtarget2 = xmltarget.addChild("id");
			xmlidtarget2.setContent(c[0]);
		}
	}

}
