package at.fhj.app.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Model class for Exams
 * 
 * Single Exam items are stored in this class.
 * Serializability is once again needed for bundling purposes.
 * 
 * @author Markus Deutsch <Markus.Deutsch.ITM09@fh-joanneum.at>
 */
public class Exam implements Serializable {
	private static final long serialVersionUID = 1895844772342340812L;
	private String id;
	private String status;
	private String course;
	private String term;
	private String type;
	private String mode;
	private Date examDate;
	private Date examRegistrationEnd;
	private String note;
    private String statusReadable;

    public String getStatusReadable() {
        return statusReadable;
    }

    public void setStatusReadable(String statusReadable) {
        this.statusReadable = statusReadable;
    }

    private boolean locked;
	
	public Exam(){
		this.locked = false;
	}
	
	public String getCourse() {
		return course;
	}

	public void setCourse(String course) {
		this.course = course;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public Date getExamDate() {
		return examDate;
	}

	/**
	 * Set the date and time of the exam.
	 * 
	 * If the date has passed, lock exam.
	 * 
	 * @param examDate UNIX timestamp representation of date.
	 */
	public void setExamDate(long examDate) {
		this.examDate = new Date(examDate * 1000);
		if(examDate*1000 <= new Date().getTime()){
			this.locked = true;
		}
	}

	public Date getExamRegistrationEnd() {
		return examRegistrationEnd;
	}

	/**
	 * Set the deadline for registration.
	 * 
	 * If the deadline has passed, lock exam.
	 * 
	 * @param examRegistrationEnd UNIX timestamp representation of date.
	 */
	public void setExamRegistrationEnd(long examRegistrationEnd) {
		this.examRegistrationEnd = new Date(examRegistrationEnd * 1000);
		if(examRegistrationEnd*1000 <= new Date().getTime()){
			this.locked = true;
		}
	}

	public String getNote() {
		return note;
	}
	
	public void setNote(String note){
		this.note = note;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	/**
	 * Set the status of an exam.
	 * 
	 * If the status does not allow signing up, lock the exam.
	 * 
	 * @param status Status the exam has
	 */
	public void setStatus(String status) {
		this.status = status;
		if(status.equals("signedUpByOffice") || status.equals("registeredByOffice") || status.equals("takenPlace") || status.equals("notallowed")){
			this.locked = true;
		}
	}

	public boolean isLocked() {
		return locked;
	}

}
