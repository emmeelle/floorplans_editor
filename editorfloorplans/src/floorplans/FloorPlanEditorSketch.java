package floorplans;

import processing.core.*;
import processing.data.XML;

import java.awt.geom.Line2D;
import java.io.File;
import java.util.*;

public class FloorPlanEditorSketch extends PApplet {

	PImage img;

	// grandezza x e y dell'immagine + grandezza pannello dx
	String filename = "C:/Users/Michele/workspace3/editorfloorplans/poli.png";
	String xmlfilename = "...";
	boolean show_image = true;
	static int x_dim = 1300, y_dim = 900;
	static int x2_dim = 300;
	public int zx = 0;
	public int zy = 0;
	public Offset oo;
	int zoom = 2;

	// width e height original dell'immagine
	int x;
	int y;

	// nodi disegnati
	public ArrayList<Node> n = new ArrayList<Node>();
	public ArrayList<Connection> c = new ArrayList<Connection>();

	// tolerance value for point-to-point comparison, in pixel;
	public int resolution = 15;

	String message = " ";

	XML initial_xml;

	// dove inizia pannello dx
	int offset = x_dim - x2_dim;

	public Label[] labels;
	String buildingtype;
	String labelxml = "C:/Users/Michele/workspace3/floorplans/school.xml";

	// PUNTI E LINEE GLOBALI
	// Coordinate X e Y dei punti
	ArrayList<Integer> X = new ArrayList<Integer>();
	ArrayList<Integer> Y = new ArrayList<Integer>();
	// Punti come linee
	ArrayList<Line2D.Float> L = new ArrayList<Line2D.Float>();
	// indice delle linee / punti che sono da considerare come porte.
	ArrayList<Integer> D = new ArrayList<Integer>();
	// indice delle delle stanze a cui appartengono le porte.
	ArrayList<Integer> D2 = new ArrayList<Integer>();
	int set_door = 0;

	boolean select_cluster = false;

	// PIANO E STANZE
	boolean xmlloaded = false;
	Floor myFloor;

	String roomselected = "none";
	int current_label = -1;
	int insertion_phase = 0;
	int insertion_label = -1;
	int wall_phase = 0;
	String mod_room;
	ArrayList<Integer[]> wall_points = new ArrayList<Integer[]>();
	int[] first_point = new int[2];
	ArrayList<Linesegment> linesegmentlist = new ArrayList<Linesegment>();
	boolean doorinsertion = false;
	String doortype;
	String doorfeatures;

	public void setup() {
		size(x_dim, y_dim);
		textSize(11);
		// The background image must be the same size as the parameters
		// into the size() method. In this program, the size of the image
		// is 640 x 360 pixels.
		img = loadImage(filename);
		x = img.width;
		y = img.height;
		image(img, 0, 0, x_dim - x2_dim, (x_dim - x2_dim) / x * y);
		fill(100, 255, 0);
		loadData();
		noLoop();
		redraw();
		oo = new Offset();

	}

	public void draw() {

		fill(235);
		rect(0, 0, offset, y_dim);
		if (x > y) {
			image(img, -oo.zx, -oo.zy, zoom * (x_dim - x2_dim), zoom * (y * (x_dim - x2_dim) / x));
		} else {
			image(img, -oo.zx, -oo.zy, zoom * (x * y_dim / y), zoom * y_dim);
		}
		deb(message);

		// DISEGNO CENTROIDI
		if (xmlloaded) {
			myFloor.display(this);
		}

		fill(235);
		stroke(135);
		rect(offset, 0, x2_dim - 2, y_dim);
		fill(50);
		textSize(8);
		textAlign(LEFT);
		if (filename.length() > 50) {
			text("..." + filename.substring(filename.length() - 35), x_dim - x2_dim + 7, 40);
		} else
			text(filename, x_dim - x2_dim + 7, 40);
		textSize(11);
		strokeWeight(3);
		for (Label l : labels) {
			l.display();
			l.rollover(mouseX, mouseY);
		}
		strokeWeight(1);
		// rettangolo di debug
		fill(220);
		rect(offset, 0, x2_dim - 2, 20);

		// bottone save;
		fill(190);
		rect(offset + 30, Globals.Y_label_limit + 100, x2_dim - 60, 30, 5);
		fill(0);
		textSize(15);
		text("save", offset + 20 + (x2_dim - 60) / 2, Globals.Y_label_limit + 120);

		// botone load background
		fill(190);
		rect(offset + 30, Globals.Y_label_limit + 150, x2_dim - 60, 30, 5);
		fill(0);
		text("load background", offset + 20 + (x2_dim - 60) / 2, Globals.Y_label_limit + 170);

		// spazio resolution
		textSize(15);
		text("1 m (n/m)(+/-)", offset + 20 + (x2_dim - 60) / 2, Globals.Y_label_limit + 200);
		textSize(8);
		text("1m", offset + 20 + (x2_dim - 60) / 2, Globals.Y_label_limit + 208);
		line(offset + 20 + (x2_dim - 60) / 2 - resolution, Globals.Y_label_limit + 210,
				offset + 20 + (x2_dim - 60) / 2 + resolution, Globals.Y_label_limit + 210);
		line(offset + 20 + (x2_dim - 60) / 2 - resolution, Globals.Y_label_limit + 212,
				offset + 20 + (x2_dim - 60) / 2 - resolution, Globals.Y_label_limit + 208);
		line(offset + 20 + (x2_dim - 60) / 2 + resolution, Globals.Y_label_limit + 212,
				offset + 20 + (x2_dim - 60) / 2 + resolution, Globals.Y_label_limit + 208);
		textSize(15);

		// bottone load XML
		fill(190);
		rect(offset + 30, Globals.Y_label_limit + 230, x2_dim - 60, 30, 5);
		fill(0);
		text("load XML", offset + 20 + (x2_dim - 60) / 2, Globals.Y_label_limit + 250);

		// bottone new
		fill(190);
		rect(offset + 30, Globals.Y_label_limit + 280, x2_dim - 60, 30, 5);
		fill(0);
		text("add room", offset + 20 + (x2_dim - 60) / 2, Globals.Y_label_limit + 300);

		// bottone delete
		fill(190);
		rect(offset + 30, Globals.Y_label_limit + 330, x2_dim - 60, 30, 5);
		fill(0);
		text("delete", offset + 20 + (x2_dim - 60) / 2, Globals.Y_label_limit + 350);

		textSize(11);
		stroke(10);

		// punti temporanei
		if (!wall_points.isEmpty()) {
			stroke(135);
			fill(135);
			strokeWeight(1);
			for (Integer[] p : wall_points) {
				ellipse(p[0] - oo.zx, p[1] - oo.zy, 5, 5);
			}
		}

	}

	public void mousePressed() {
		if ((mouseX > offset)) {

			if ((mouseX >= offset + 30) && (mouseX <= offset + x2_dim - 30) && (mouseY >= Globals.Y_label_limit + 100)
					&& (mouseY <= Globals.Y_label_limit + 130)) {
				saveGraph();
				message = "saved";
			}

			if ((mouseX >= offset + 30) && (mouseX <= offset + x2_dim - 30) && (mouseY >= Globals.Y_label_limit + 150)
					&& (mouseY <= Globals.Y_label_limit + 180)) {
				selectInput("Select a file to process:", "imgSelected");

			}
			if ((mouseX >= offset + 30) && (mouseX <= offset + x2_dim - 30) && (mouseY >= Globals.Y_label_limit + 230)
					&& (mouseY <= Globals.Y_label_limit + 250)) {
				selectInput("Select a file to process:", "xmlSelected");

			}

			if ((mouseX >= offset + 30) && (mouseX <= offset + x2_dim - 30) && (mouseY >= Globals.Y_label_limit + 270)
					&& (mouseY <= Globals.Y_label_limit + 300)) {
				insertion_phase = 1;
			}

			if (insertion_phase == 1) {
				for (int i = 0; i < labels.length; i++) {
					if (labels[i].occupied(mouseX, mouseY)) {
						insertion_label = i;
						insertion_phase = 2;
						break;
					}
				}
			}

			if (!(roomselected.equals("none"))) {

				if ((mouseX >= offset + 30) && (mouseX <= offset + x2_dim - 30)
						&& (mouseY >= Globals.Y_label_limit + 320) && (mouseY <= Globals.Y_label_limit + 350)) {
					myFloor.delete_room(roomselected);
					roomselected = "none";
					myFloor.all_rooms_visible();
				}

				for (int i = 0; i < labels.length; i++) {
					if (labels[i].occupied(mouseX, mouseY)) {
						myFloor.change_room_label(roomselected, labels[i].name, labels[i].type);
						myFloor.all_rooms_visible();
						roomselected = "none";

						break;
					}
				}
			}

		} else {
			if (roomselected.equals("none") && insertion_phase == 0 && wall_phase == 0) {
				roomselected = myFloor.centroid_selection(mouseX + oo.zx, mouseY + oo.zy);
				myFloor.change_door_type(mouseX + oo.zx, mouseY + oo.zy);
			}
			if (wall_phase == 1 && !doorinsertion) {
				// Disegno i punti finchè non ridisegno il primo
				// Check primo punto
				if (wall_points.isEmpty()) {
					first_point[0] = mouseX + oo.zx;
					first_point[1] = mouseY + oo.zy;
				}
				// Check ultimo coincidenza col primo
				if ((Math.abs(mouseX + oo.zx - first_point[0]) < 5) && (Math.abs(mouseY + oo.zy - first_point[1]) < 5)
						&& !wall_points.isEmpty()) {
					String id = UUID.randomUUID().toString();
					linesegmentlist.add(new Linesegment(wall_points.get(wall_points.size() - 1)[0],
							wall_points.get(wall_points.size() - 1)[1], mouseX + oo.zx, mouseY + oo.zy, id, "WALL",
							"EXPLICIT", "NORMAL"));
					Integer temppoint[] = new Integer[2];
					temppoint[0] = first_point[0];
					temppoint[1] = first_point[1];
					wall_points.add(temppoint);
					myFloor.set_room_walls(mod_room, wall_points, linesegmentlist);
					wall_phase = 0;
					mod_room = "none";
					wall_points.clear();
					linesegmentlist.clear();
					first_point[0] = 0;
					first_point[1] = 0;

				} else {
					if (!wall_points.isEmpty()) {
						String id = UUID.randomUUID().toString();
						linesegmentlist.add(new Linesegment(wall_points.get(wall_points.size() - 1)[0],
								wall_points.get(wall_points.size() - 1)[1], mouseX + oo.zx, mouseY + oo.zy, id, "WALL",
								"EXPLICIT", "NORMAL"));
					}
					Integer temppoint[] = new Integer[2];
					temppoint[0] = mouseX + oo.zx;
					temppoint[1] = mouseY + oo.zy;
					wall_points.add(temppoint);
				}
			}
			if (wall_phase == 1 && doorinsertion) {
				String id = UUID.randomUUID().toString();
				linesegmentlist.add(new Linesegment(wall_points.get(wall_points.size() - 1)[0],
						wall_points.get(wall_points.size() - 1)[1], mouseX + oo.zx, mouseY + oo.zy, id, "WALL",
						"EXPLICIT", "NORMAL"));
				Integer temppoint[] = new Integer[2];
				temppoint[0] = mouseX + oo.zx;
				temppoint[1] = mouseY + oo.zy;
				wall_points.add(temppoint);
				id = UUID.randomUUID().toString();
				linesegmentlist.add(new Linesegment(mouseX + oo.zx, mouseY + oo.zy, mouseX + oo.zx, mouseY + oo.zy, id,
						"PORTAL", doortype, doorfeatures));
				doorinsertion = false;
			}
			if (insertion_phase == 2) {
				Room temproom = myFloor.add_room(this, (mouseX + oo.zx), (mouseY + oo.zy), labels[insertion_label].type,
						labels[insertion_label].name);
				mod_room = temproom.id;
				insertion_phase = 0;// forse meglio mettere un numero a caso
				wall_phase = 1;

			}

		}

		redraw();
	}

	public void keyPressed() {

		switch (key) {

		case BACKSPACE:
			if (!roomselected.equals("none")) {
				myFloor.all_rooms_visible();
				roomselected = "none";
			}
			break;
		case 'd':
		case 'D':
			// DESTRA
			zx = zx + 50;
			oo.zx = zx;
			break;
		case 's':
		case 'S':
			// GIU
			zy = zy + 50;
			oo.zy = zy;
			break;
		case 'w':
		case 'W':
			// SU
			zy = zy - 50;
			oo.zy = zy;
			break;
		case 'a':
		case 'A':
			// SX
			zx = zx - 50;
			oo.zx = zx;
			break;
		case 'y':
		case 'Y':
			if (myFloor.conncetions_visibility == 0)
				myFloor.conncetions_visibility = 2;
			else
				myFloor.conncetions_visibility = 0;
			break;
		case 'u':
		case 'U':
			myFloor.adjust_room_points();
			break;
		case 'i':
		case 'I':
			myFloor.check_connections();
			myFloor.fix_portals();
			break;
		case 'o':
		case 'O':
			myFloor.expand_doors(resolution);
			break;
		case 'c':
		case 'C':
			doorinsertion = !doorinsertion;
			doortype = "EXPLICIT";
			doorfeatures = "NORMAL";
			break;
		case 'x':
		case 'X':
			doorinsertion = !doorinsertion;
			doortype = "IMPLICIT";
			doorfeatures = "NORMAL";
			break;
		case 'v':
		case 'V':
			doorinsertion = !doorinsertion;
			doortype = "IMPLICIT";
			doorfeatures = "DOUBLE";
			break;
		case 'm': // INGRANDISCO LA SCALA
		case 'M':
			resolution += 2;
			break;
		case 'n': // RIDUCO LA SCALA
		case 'N':
			resolution -= 2;
			break;

		}
		redraw();
	}

	public void loadData() {

		int counter = 1;

		// Load LABELS XML file
		initial_xml = loadXML(labelxml);
		XML building_name = initial_xml.getChild("type_name");
		buildingtype = building_name.getContent();
		message = buildingtype;

		XML Xlabels = initial_xml.getChild("labels");
		XML[] children = Xlabels.getChildren("label");

		// The size of the array of Bubble objects is determined by the total
		// XML elements named "bubble"
		labels = new Label[children.length];
		// deb( Integer.toString(labels.length));
		int xo = Globals.X_label_offset + offset;
		int yo = 0;
		for (int i = 0; i < labels.length; i++) {

			XML typeElement = children[i].getChild("type");
			String type = typeElement.getContent();

			XML nameElement = children[i].getChild("name");
			String name = nameElement.getContent();

			// The position element has two attributes: x and y
			XML colorElement = children[i].getChild("color");
			// Note how with attributes we can get an integer or float via
			// getInt() and getFloat()
			int r = colorElement.getInt("r");
			int g = colorElement.getInt("g");
			int b = colorElement.getInt("b");

			if (yo > Globals.Y_label_limit) {
				xo += Globals.X_label_shift;
				counter = 1;
			}
			yo = Globals.Y_label_shift * counter + 10;
			counter++;
			// Make a Bubble object out of the data read
			labels[i] = new Label(this, name, type, r, g, b, Globals.shape * 2, xo, yo);
		}

	}

	public void imgSelected(File selection) {
		if (selection == null) {
			message = "Window was closed or the user hit cancel.";
		} else {
			filename = selection.getAbsolutePath();
			message = "loaded";
			img = loadImage(filename);
			x = img.width;
			y = img.height;
			redraw();
		}
	}

	public void xmlSelected(File selection) {
		if (selection == null) {
			message = "Window was closed or the user hit cancel.";
		} else {
			xmlfilename = selection.getAbsolutePath();
			message = "loaded";

			// loading and parsing XML
			initial_xml = loadXML(xmlfilename);
			resolution = initial_xml.getChild("scale").getChild("represented_distance").getChild("value")
					.getIntContent();
			XML floor = initial_xml.getChild("floor");
			myFloor = new Floor(floor);
			xmlloaded = true;
			redraw();
		}
	}

	public void saveGraph() {
		myFloor.adjust_room_points();
		myFloor.check_connections();
		myFloor.fix_portals();
		// filename.xml => filename_updated.xml
		String newfile = xmlfilename.substring(0, xmlfilename.lastIndexOf('.')) + "_updated.xml";
		// The things that are not being modified are copied from the original
		XML newxml = new XML("building");
		newxml.setString("id", initial_xml.getString("id"));
		System.out.println(newxml.toString());
		XML xmlname = newxml.addChild("name");
		xmlname.setContent(initial_xml.getChild("name").getContent());
		XML xmlscale = newxml.addChild("scale");
		XML xRepresented = xmlscale.addChild("represented_distance");
		XML xValue = xRepresented.addChild("value");
		xValue.setIntContent(resolution);
		XML xUM1 = xRepresented.addChild("um");
		xUM1.setContent("pixel");
		XML xReal = xmlscale.addChild("real_distance");
		XML xValue2 = xReal.addChild("value");
		xValue2.setIntContent(90);
		XML xUM2 = xReal.addChild("um");
		xUM2.setContent("cm");
		XML info = newxml.addChild("Info");
		info.setContent("Inserire informazioni sull'edificio");
		XML xmlbt = newxml.addChild("building_type");
		XML xmlmt = xmlbt.addChild("main_type");
		xmlmt.setContent(initial_xml.getChild("building_type").getChild("main_type").getContent());
		// writing the new floor
		myFloor.to_xml(newxml);
		saveXML(newxml, newfile);
	}

	public void deb(String s) {
		fill(0);
		text(s, offset + 10 + (x2_dim - 60) / 2, 15);
	}

}