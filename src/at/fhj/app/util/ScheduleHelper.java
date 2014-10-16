package at.fhj.app.util;

import java.io.StringReader;
import java.util.ArrayList;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import android.content.Context;
import android.util.Log;
import at.fhj.app.model.Event;
import at.fhj.app.model.EventContentHandler;
import at.fhj.app.model.EventDAO;
import at.fhj.app.retriever.FHPIRetriever;

public class ScheduleHelper {
	
	private Context context;
	
	private final String course;
	private final String year;
		
	// Retrieval part
	private FHPIRetriever r;
	private String[] arguments = {"c", "y"};
	private String[] values;
	
	// Information
	private int size = -1;
	private boolean initialized = false;
	
	public ScheduleHelper(String course, String year, Context context){
		this.course = course;
		this.year = year;
		this.context = context;

		System.setProperty("org.xml.sax.driver","org.xmlpull.v1.sax2.Driver");
		
		updateSize();
	}
	
	private void updateSize(){
		EventDAO.init(context);
		this.size = EventDAO.getCount(course, year);
		
        if(this.size > 0){
        	this.initialized = true;
        }
	}
	
	public boolean isInitialized(){
		return this.initialized;
	}

	public String getCourse() {
		return course;
	}

	public int getSize(){
		return this.size;
	}
	
	public String getYear() {
		return year;
	}
	
	public void synchronize(boolean force){
		store(parse(retrieve()));
		this.initialized = true;
	}
	
	private String retrieve(){
		//Log.i("ScheduleHelper", "Retrieval started.");
		r = new FHPIRetriever();
		values = new String[] {this.course, this.year};
		r.prepareRequest("getschedule.php", arguments, values);
		return r.retrieve();
	}
	
	private ArrayList<Event> parse(String result){
		//Log.i("ScheduleHelper", "Parsing started.");
		ArrayList<Event> events = new ArrayList<Event>();
		try {
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			EventContentHandler sch = new EventContentHandler();
			xmlReader.setContentHandler(sch);
			xmlReader.parse(new InputSource(new StringReader(result)));
			events = sch.events;
			return events;
		} catch(Exception e) {
			return null;
		}
	}
	
	private void store(ArrayList<Event> events){
		EventDAO.init(context);
		
		try {
			if(events.size() >= 1){	
				// Delete old entries
				int deleted = EventDAO.deleteAll(course, year);
				//int deleted = EventDAO.deleteAll();
				Log.i("ScheduleHelper", deleted + " Events aus "+course+" "+year+" gelöscht.");
				
				// Now insert new entries
				for(Event event : events){
					EventDAO.insert(event);
					this.size = events.size();
				}
			}
		} catch (Exception e){
			e.printStackTrace();
			this.size = 0;
		}
					
	}

	public ArrayList<Event> getSchedule(){
		ArrayList<Event> events = new ArrayList<Event>();
		
		if(size < 0){
			synchronize(true);
		} else {
			EventDAO.init(context);
			events = (ArrayList<Event>) EventDAO.readAll(course, year);
		}
		
		return events;
	}
	
	public ArrayList<Event> getSchedule(long date){
		String date_formatted = Configuration.SIMPLE_DATE.format(date);
		
		if(!this.initialized){
			synchronize(true);
		}
		
		EventDAO.init(context);

		ArrayList<Event> eventsList = (ArrayList<Event>) EventDAO.readAll(course, year, date_formatted);
		ArrayList<Event> result = new ArrayList<Event>();
		
		// Remove hidden events
		GroupFilter f = new GroupFilter(context);
		for (Event event : eventsList) {
			if(!f.isHidden(event.getType())){
				result.add(event);
			}
		}

        return result;
		
	}
}
