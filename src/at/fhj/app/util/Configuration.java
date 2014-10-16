package at.fhj.app.util;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.TimeZone;

import at.fhj.app.R;

public class Configuration {
	/**
	 * Number of total tries.
	 * 
	 * A retry will be made when there seems to be a network error.
	 */
	public static final int NETWORK_TRIES = 3;
	
	/**
	 * The FHPI-Key obtained from API_ROOT
	 */
	public static final String API_KEY = "";
	
	/**
	 * FHPI Server root, ending with an "/".
	 */
	public static final String API_ROOT = "https://ws.fh-joanneum.at/";
	
	/**
	 * Date format displayed throughout the app.
	 */
	public static final SimpleDateFormat SIMPLE_DATE = new SimpleDateFormat("dd.MM.yyyy");
	public static final SimpleDateFormat SIMPLE_DATE_DAY = new SimpleDateFormat("EE, dd.MM.yyyy");
	public static final SimpleDateFormat SIMPLE_DATE_TIME = new SimpleDateFormat("HH:mm");
	public static final SimpleDateFormat SIMPLE_DATETIME = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	
	/**
	 * Array of known event types.
	 */
	public static final String[] EVENT_TYPES = {
			"VO",
			"G1",
			"PA",
			"G2",
			"KLA",
			"SO",
			"G3",
			"GA",
			"GB",
			"GC",
			"GD",
			"GM",
			"P",
			"P1",
			"P2",
			"PVO",
			"PR",
			"RES",
			"SE",
			"S", 
			"IL", 
			"VMN", 
			"VAE"
	};
	
	/**
	 * Event types mapped to colors.
	 */
	public static HashMap<String, Integer> EVENT_COLORS = new HashMap<String, Integer>();
	
	/**
	 * Initially map types to colors.
	 */
	public static void initEventColors(){
		if(EVENT_COLORS.size() == 0){	
			for(int i = 0; i < EVENT_TYPES.length; i++){
				if(EVENT_TYPES[i].equals("PA") || EVENT_TYPES[i].equals("P") || EVENT_TYPES[i].equals("KLA")){
					EVENT_COLORS.put(EVENT_TYPES[i], R.color.red);
				} else if (EVENT_TYPES[i].equals("G1") || EVENT_TYPES[i].equals("GA")  || EVENT_TYPES[i].equals("VAE")){
					EVENT_COLORS.put(EVENT_TYPES[i], R.color.lightgreen);
				} else if (EVENT_TYPES[i].equals("G2") || EVENT_TYPES[i].equals("GB")  || EVENT_TYPES[i].equals("VMN")){
					EVENT_COLORS.put(EVENT_TYPES[i], R.color.lightblue);
				} else if (EVENT_TYPES[i].equals("G3") || EVENT_TYPES[i].equals("GC")){
					EVENT_COLORS.put(EVENT_TYPES[i], R.color.lightorange);
				} else if (EVENT_TYPES[i].equals("G4") || EVENT_TYPES[i].equals("GD")){
					EVENT_COLORS.put(EVENT_TYPES[i], R.color.darkpurple);
				} else if (EVENT_TYPES[i].equals("GM")){
					EVENT_COLORS.put(EVENT_TYPES[i], R.color.girlypink);
				} else if (EVENT_TYPES[i].equals("SO")){
					EVENT_COLORS.put(EVENT_TYPES[i], R.color.gray);
				} else if (EVENT_TYPES[i].equals("VO")){
					EVENT_COLORS.put(EVENT_TYPES[i], R.color.yellow);
				} else if (EVENT_TYPES[i].equals("P1")){
					EVENT_COLORS.put(EVENT_TYPES[i], R.color.pink);
				} else if (EVENT_TYPES[i].equals("P2")){
					EVENT_COLORS.put(EVENT_TYPES[i], R.color.purple);
				} else if (EVENT_TYPES[i].equals("PVO") || EVENT_TYPES[i].equals("IL")){
					EVENT_COLORS.put(EVENT_TYPES[i], R.color.ultrapink);
				} else if (EVENT_TYPES[i].equals("PR")){
					EVENT_COLORS.put(EVENT_TYPES[i], R.color.red);
				} else if (EVENT_TYPES[i].equals("RES")){
					EVENT_COLORS.put(EVENT_TYPES[i], R.color.gold);
				} else if (EVENT_TYPES[i].equals("SE")){
					EVENT_COLORS.put(EVENT_TYPES[i], R.color.blue1);
				} else if (EVENT_TYPES[i].equals("S")){
					EVENT_COLORS.put(EVENT_TYPES[i], R.color.pink1);
				} else {
					EVENT_COLORS.put(EVENT_TYPES[i], R.color.white);
				}
				//EVENT_COLORS.put(EVENT_TYPES[i], ColorHelper.getRandomColorResource());
			}
		}
		
	}
	
	/**
	 * Set timezone for SimpleDateFormat to resolve issues
	 * with wrong start time of courses.
	 */
	public static void initFormatter(){
		TimeZone tz = TimeZone.getTimeZone("Europe/Vienna");
		
		SIMPLE_DATE.setTimeZone(tz);
		SIMPLE_DATE_DAY.setTimeZone(tz);
		SIMPLE_DATE_TIME.setTimeZone(tz);
		SIMPLE_DATETIME.setTimeZone(tz);
	}
	
}