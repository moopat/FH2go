package at.fhj.app.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import at.fhj.app.model.Event;
import at.fhj.app.model.EventContentHandler;
import at.fhj.app.model.EventDAO;
import at.fhj.app.retriever.FHPIRetriever;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.StringReader;
import java.util.ArrayList;

/**
 * Markus Deutsch
 * <markus.deutsch@moop.at>
 * 17.10.14
 */
public class ScheduleProvider {

    private Context context;
    private static ScheduleProvider instance;

    private FHPIRetriever r;
    private String[] arguments = {"c", "y"};
    private String[] values;

    private ScheduleProvider(Context context) {
        this.context = context;
        EventDAO.init(context);
        System.setProperty("org.xml.sax.driver","org.xmlpull.v1.sax2.Driver");
    }

    public static ScheduleProvider getInstance(Context context){
        if(instance == null) {
            instance = new ScheduleProvider(context);
        }
        return instance;
    }

    public int getCount(String course, String year){
        return EventDAO.getCount(course, year);
    }

    /**
     * Download and store new events
     * @param course Degree programme
     * @param year Class year
     */
    public void update(String course, String year, ScheduleActionListener listener){
        ScheduleHelperTask task = new ScheduleHelperTask(course, year, listener);
        task.execute("los!");
    }

    public ArrayList<Event> getSchedule(String course, String year){
        return (ArrayList<Event>) EventDAO.readAll(course, year);
    }

    public ArrayList<Event> getSchedule(String course, String year, long date){
        String date_formatted = Configuration.SIMPLE_DATE.format(date);

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

    private String retrieve(String course, String year){
        //Log.i("ScheduleHelper", "Retrieval started.");
        r = new FHPIRetriever();
        values = new String[] {course, year};
        r.prepareRequest("getschedule.php", arguments, values);
        return r.retrieve();
    }

    private ArrayList<Event> parse(String result){
        //Log.i("ScheduleHelper", "Parsing started.");
        ArrayList<Event> events;
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
            if(events != null && events.size() >= 1){
                // Delete old entries
                String course = events.get(0).getCourse();
                String year = events.get(0).getYear();
                int deleted = EventDAO.deleteAll(course, year);
                //int deleted = EventDAO.deleteAll();
                Log.i("ScheduleHelper", deleted + " Events aus " + course + " " + year + " gelöscht.");

                // Now insert new entries
                for(Event event : events){
                    EventDAO.insert(event);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Thread to perform schedule synchronization in.
     * @author Markus Deutsch <Markus.Deutsch.ITM09@fh-joanneum.at>
     */
    class ScheduleHelperTask extends AsyncTask<String, String, String> {

        private String year;
        private String course;
        private ScheduleActionListener listener;

        public ScheduleHelperTask(String course, String year, ScheduleActionListener listener){
            this.year = year;
            this.course = course;
            this.listener = listener;
        }

        @Override
        protected String doInBackground(String... params) {
            store(parse(retrieve(course, year)));
            return "null";
        }

        protected void onPostExecute(String result) {
            if(listener != null) listener.onUpdated();
        }

    }

    public interface ScheduleListener {
        void onEventsDelivered(ArrayList<Event> events);
    }

    public interface ScheduleActionListener {
        void onUpdated();
    }
}
