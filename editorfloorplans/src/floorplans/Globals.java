package floorplans;
//QUI CI METTO LE VARIABILI GLOBALI;
public class Globals {
    public static int a;
    public static int b;
    public static int opacity1 = 130;
    public static int opacity2 = 255;
    public static int shape = 15;
    // colori stroke nodo attivo
    public static int rn_active = 255;
    public static int gn_active = 150;
    public static int bn_active	= 50;
    // colori stroke nodo non attivo
    public static int rn_unactive = 60;
    public static int gn_unactive = 60;
    public static int bn_unactive = 60;
    // colore della porta
    public static int r_door= 255;
    public static int g_door = 180;
    public static int b_door = 60;
    // soglia distanza punto-punto
    public static int p_distance = 10;
    public static int resolution = 200;
    // di quanto muovo un punto ogni volta che lo muovo
    public static int step = 2;
    public static int group_step = 40;
    // default resolution per plottare le porte mentre le disegni
    public static int default_resolution = p_distance*2;
    
	// dati che servono a disporre i vari pallini
	public static int X_label_offset = 40;
	public static int X_label_shift = 70;
	public static int Y_label_limit = 400;
	public static int Y_label_shift = 60;
	
	public static int bigzoom = 2;
	public static int smallzoom = 1;
    
}