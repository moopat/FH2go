package at.fhj.app.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Model class for Events.
 * 
 * Events are items on student's timetables. 
 * This has to be Serializable so it can be bundled for easy
 * "transportation" between Activities.
 * 
 * @author Markus Deutsch <Markus.Deutsch.ITM09@fh-joanneum.at>
 *
 */
public class Event implements Serializable {

	private static final long serialVersionUID = -4200861910967060624L;
	private int id;
	private String subject;
	private String lecturer;
	private String location;
	private String type;
	private Date start;
	private Date end;
	
	private Date updated;
	
	private String year;
	private String course;
		
	public Event(){
		this.updated = new Date();
	}
	
	/**
	 * Standard constructor for Event.
	 * 
	 * @param subject Title of subject.
	 * @param lecturer Name of class lecturer.
	 * @param location Location of class.
	 * @param type Type of class.
	 * @param start Time of start as standard UNIX timestamp.
	 * @param end Time of end as standard UNIX timestamp.
	 * @param year Year of class.
	 * @param course Course of class. 
	 */
	public Event(String subject, String lecturer, String location, String type, long start, long end, String year, String course) {
		this.subject = subject;
		this.lecturer = lecturer;
		this.location = location;
		this.type = type;
		this.start = new Date(start*1000);
		this.end = new Date(end*1000);
		this.year = year;
		this.course = course;
		
		this.updated = new Date();
	}
	
	// Here go the getters and setters.
	public String getSubject() {
		return subject;
	}
	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getCourse() {
		return course;
	}

	public void setCourse(String course) {
		this.course = course;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getLecturer() {
		return lecturer;
	}
	public void setLecturer(String lecturer) {
		this.lecturer = lecturer;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Date getStart() {
		return start;
	}
	
	/**
	 * Set start of Event.
	 * @param start Start as standard UNIX timestamp.
	 */
	public void setStart(long start) {
		this.start = new Date(start*1000);
	}
	
	/**
	 * Set start of Event.
	 * @param start Start as Date class.
	 */
	public void setStart(Date start) {
		this.start = start;
	}
	public Date getEnd() {
		return end;
	}
	
	/**
	 * Set end of Event.
	 * @param end End as standard UNIX timestamp.
	 */
	public void setEnd(long end) {
		this.end = new Date(end*1000);
	}
	
	/**
	 * Set end of Event.
	 * @param end End as Date class.
	 */
	public void setEnd(Date end) {
		this.end = end;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(long updated) {
		this.updated = new Date(updated*1000);
	}
	
	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(this.getId());
		sb.append("\n");
		sb.append(this.getCourse());
		sb.append("+");
		sb.append(this.getYear());
		sb.append("\n");
		sb.append(this.getLecturer());
		sb.append("\n");
		sb.append(this.getSubject());
		sb.append("\n");
		sb.append(this.getStart().toString());
		sb.append("\n");
		sb.append(this.getUpdated().toString());
		
		return sb.toString();
	}
}
