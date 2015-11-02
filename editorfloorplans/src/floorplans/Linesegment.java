package floorplans;

public class Linesegment {

	Integer[] points=new Integer[4];
	String id;
	String class_;
	String type;
	String features;
	Linesegment(int x1,int y1,int x2,int y2,String passid,String passclass,String passtype, String passfeat){
		points[0]=x1;
		points[1]=y1;
		points[2]=x2;
		points[3]=y2;
		id = passid;
		class_=passclass;
		type=passtype;
		features=passfeat;
	}
}
