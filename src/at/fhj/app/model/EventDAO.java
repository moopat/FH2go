package at.fhj.app.model;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import at.fhj.app.util.Configuration;
import at.fhj.app.util.ScheduleDatabaseHelper;

public class EventDAO {
	
	private static ScheduleDatabaseHelper sdh;
	
	public static void init(Context context){		
		sdh = new ScheduleDatabaseHelper(context);		
	}
	
	@SuppressWarnings("static-access")
	public static int getCount(String course, String year){
		int count = 0;
		Cursor cursor = null;
        SQLiteDatabase db = sdh.getReadableDatabase();
        try {
        	String where = sdh.SCHEDULE_KEY_COURSE + " = ? AND " + sdh.SCHEDULE_KEY_YEAR + " = ?";
			String[] args = {course, year};
			String order = sdh.SCHEDULE_KEY_START + " ASC";
			cursor = db.query(sdh.SCHEDULE_TABLE_NAME, null, where, args, null, null, order);
			
            count = cursor.getCount();
        } finally {
        	if(cursor != null){
        		cursor.close();
        	}
        	db.close();
        }
    	return count;
	}
	
	@SuppressWarnings("static-access")
	public static long getLastUpdate(String searchCourse, String searchYear) {
		long updated = 0;
		boolean running = true;
        Cursor cursor = null;
        SQLiteDatabase db = sdh.getReadableDatabase();
        try {
        	String where = sdh.SCHEDULE_KEY_COURSE + " = ? AND " + sdh.SCHEDULE_KEY_YEAR + " = ?";
			String[] args = {searchCourse, searchYear};
			String order = sdh.SCHEDULE_KEY_START + " ASC";
			cursor = db.query(sdh.SCHEDULE_TABLE_NAME, new String[]{"updated"}, where, args, null, null, order);
			
            if(cursor.getCount() > 0) {
                int updatedIndex = cursor.getColumnIndex("updated");
                
                cursor.moveToFirst();
                
                do {
                	updated = cursor.getInt(updatedIndex);
                    running = false;
                    cursor.moveToNext();
                } while (running && !cursor.isAfterLast());
            } 
        } finally {
        	if(cursor != null){
        		cursor.close();
        	}
        	db.close();
        }
        
    	return updated;
    }
	
	
	
	@SuppressWarnings("static-access")
	public static int insert(Event event){
		int lastid = -1;
        SQLiteDatabase db = sdh.getWritableDatabase();
        try {
        	db.beginTransaction();
                
            String subject = event.getSubject();
            String lecturer = event.getLecturer();
            String location = event.getLocation();
            String type = event.getType();
            int start = (int) (event.getStart().getTime()/1000);
            int end = (int) (event.getEnd().getTime()/1000);
            String year = event.getYear();
            String course = event.getCourse();
            int updated = (int) (event.getUpdated().getTime()/1000);
            
            ContentValues cv = new ContentValues();
            cv.put("start", start);
            cv.put("end", end);
            cv.put("subject", subject);
            cv.put("lecturer", lecturer);
            cv.put("location", location);
            cv.put("type", type);
            cv.put("course", course);
            cv.put("year", year);
            cv.put("updated", updated);
            cv.put("key_date", Configuration.SIMPLE_DATE.format(event.getStart()));
          
            lastid = (int) db.insert(sdh.SCHEDULE_TABLE_NAME, null, cv);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
        
        return lastid;
	}
	
	@SuppressWarnings("static-access")
	public static List<Event> readAll(String searchCourse, String searchYear, String searchDate) {
        Cursor cursor = null;
        SQLiteDatabase db = sdh.getReadableDatabase();
        List<Event> all = new ArrayList<Event>();
        try {
        	String where = sdh.SCHEDULE_KEY_COURSE + " = ? AND " + sdh.SCHEDULE_KEY_YEAR + " = ? AND " + sdh.SCHEDULE_KEY_DATE + " = ?";
			String[] args = {searchCourse, searchYear, searchDate};
			String order = sdh.SCHEDULE_KEY_START + " ASC";
			cursor = db.query(sdh.SCHEDULE_TABLE_NAME, null, where, args, null, null, order);
			
            if(cursor.getCount() > 0) {
                int idIndex = cursor.getColumnIndex("id");
                int startIndex = cursor.getColumnIndex("start");
                int endIndex = cursor.getColumnIndex("end");
                int subjectIndex = cursor.getColumnIndex("subject");
                int lecturerIndex = cursor.getColumnIndex("lecturer");
                int locationIndex = cursor.getColumnIndex("location");
                int typeIndex = cursor.getColumnIndex("type");
                int courseIndex = cursor.getColumnIndex("course");
                int yearIndex = cursor.getColumnIndex("year");
                int updatedIndex = cursor.getColumnIndex("updated");
                
                cursor.moveToFirst();
                
                do {
                	int id = cursor.getInt(idIndex);
                    int start = cursor.getInt(startIndex);
                    int end = cursor.getInt(endIndex);
                    int updated = cursor.getInt(updatedIndex);
                    String subject = cursor.getString(subjectIndex);
                    String lecturer = cursor.getString(lecturerIndex);
                    String location = cursor.getString(locationIndex);
                    String type = cursor.getString(typeIndex);
                    String course = cursor.getString(courseIndex);
                    String year = cursor.getString(yearIndex);
                 
                    Event event = new Event(subject, lecturer, location, type, start, end, year, course);
                    event.setUpdated(updated);
                    event.setId(id);
            
                    all.add(event);
                                
                    cursor.moveToNext();
                } while (!cursor.isAfterLast());
            } 
        } finally {
        	if(cursor != null){
        		cursor.close();
        	}
        	db.close();
        }
        
    	return all;
    }
	
	@SuppressWarnings("static-access")
	public static List<Event> readAll(String searchCourse, String searchYear) {
        Cursor cursor = null;
        SQLiteDatabase db = sdh.getReadableDatabase();
        List<Event> all = new ArrayList<Event>();
        try {
        	String where = sdh.SCHEDULE_KEY_COURSE + " = ? AND " + sdh.SCHEDULE_KEY_YEAR + " = ?";
			String[] args = {searchCourse, searchYear};
			String order = sdh.SCHEDULE_KEY_START + " ASC";
			cursor = db.query(sdh.SCHEDULE_TABLE_NAME, null, where, args, null, null, order);
			
            if(cursor.getCount() > 0) {
                int idIndex = cursor.getColumnIndex("id");
                int startIndex = cursor.getColumnIndex("start");
                int endIndex = cursor.getColumnIndex("end");
                int subjectIndex = cursor.getColumnIndex("subject");
                int lecturerIndex = cursor.getColumnIndex("lecturer");
                int locationIndex = cursor.getColumnIndex("location");
                int typeIndex = cursor.getColumnIndex("type");
                int courseIndex = cursor.getColumnIndex("course");
                int yearIndex = cursor.getColumnIndex("year");
                int updatedIndex = cursor.getColumnIndex("updated");
                
                cursor.moveToFirst();
                
                do {
                	int id = cursor.getInt(idIndex);
                    int start = cursor.getInt(startIndex);
                    int end = cursor.getInt(endIndex);
                    int updated = cursor.getInt(updatedIndex);
                    String subject = cursor.getString(subjectIndex);
                    String lecturer = cursor.getString(lecturerIndex);
                    String location = cursor.getString(locationIndex);
                    String type = cursor.getString(typeIndex);
                    String course = cursor.getString(courseIndex);
                    String year = cursor.getString(yearIndex);
                 
                    Event event = new Event(subject, lecturer, location, type, start, end, year, course);
                    event.setUpdated(updated);
                    event.setId(id);
            
                    all.add(event);
                                
                    cursor.moveToNext();
                } while (!cursor.isAfterLast());
            } 
        } finally {
        	if(cursor != null){
        		cursor.close();
        	}
        	db.close();
        }
        
    	return all;
    }
	
	public static int deleteAll(String course, String year){
		SQLiteDatabase db = sdh.getWritableDatabase();
		int count = 0;
        try {
        	db.beginTransaction();
            
            String delete = "DELETE FROM schedule WHERE course='"+course+"' AND year = '"+year+"'";
            Log.w("EventDAO", delete);
            db.execSQL(delete);
            
            db.setTransactionSuccessful();
        } finally {
        	db.endTransaction();
            db.close();
        }
        
        return count;
	}
	
	@SuppressWarnings("static-access")
	public static int deleteAll(){
		SQLiteDatabase db = sdh.getWritableDatabase();
		int count = 0;
        try {
            count = db.delete(sdh.SCHEDULE_TABLE_NAME, null, null);
        } finally {
            db.close();
        }
        
        return count;
	}

}
