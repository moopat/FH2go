package at.fhj.app.util;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import at.fhj.app.model.Event;
import at.fhj.app.model.EventContentHandler;

public class FhpiParser {
	
	public FhpiParser(){
		System.setProperty("org.xml.sax.driver","org.xmlpull.v1.sax2.Driver");
	}
	
	public ArrayList<Event> parseSchedule(String result, Activity a){
		ArrayList<Event> events = new ArrayList<Event>();
		try {
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			EventContentHandler sch = new EventContentHandler();
			xmlReader.setContentHandler(sch);
			xmlReader.parse(new InputSource(new StringReader(result)));
			events = sch.events;
			saveSchedule(events, a);
			return events;
		} catch(Exception e) {
			return null;
		}
	}
	
	@SuppressWarnings("static-access")
	private void saveSchedule(ArrayList<Event> events, Activity a){
		
		if(events.size() >= 1){	
			ScheduleDatabaseHelper helper;
			SQLiteDatabase db;
			
			// Connect to the database
			helper = new ScheduleDatabaseHelper(a);
			db = helper.getWritableDatabase();
			
			// Delete old entries
			String where = helper.SCHEDULE_KEY_COURSE + " = ? AND " + helper.SCHEDULE_KEY_YEAR + " = ?";
			String[] whereArgs = {events.get(1).getCourse(), events.get(1).getYear()};
			
			//Log.i("ScheduleChooserActivity", "Preparing to reset database if necessary.");
			db.delete(helper.SCHEDULE_TABLE_NAME, where, whereArgs);
			
			// Now insert new entries
			ContentValues values = new ContentValues();
			for(Event event : events){
				values.clear();
				values.put(helper.SCHEDULE_KEY_START, event.getStart().getTime()/1000);
				values.put(helper.SCHEDULE_KEY_END, event.getEnd().getTime()/1000);
				values.put(helper.SCHEDULE_KEY_SUBJECT, event.getSubject());
				values.put(helper.SCHEDULE_KEY_LECTURER, event.getLecturer());
				values.put(helper.SCHEDULE_KEY_LOCATION, event.getLocation());
				values.put(helper.SCHEDULE_KEY_TYPE, event.getType());
				values.put(helper.SCHEDULE_KEY_COURSE, event.getCourse());
				values.put(helper.SCHEDULE_KEY_YEAR, event.getYear());
				values.put(helper.SCHEDULE_KEY_DATE, Configuration.SIMPLE_DATE.format(new Date(event.getStart().getTime())));
				
				//Log.i("ScheduleChooserActivity", "Inserting class into database.");
				db.insertOrThrow(helper.SCHEDULE_TABLE_NAME, null, values);
			}
			
			db.close();
		
		}
	}

}
