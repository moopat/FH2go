package at.fhj.app.model;

/**
 * Model class for Marks
 * 
 * @author Markus Deutsch <Markus.Deutsch.ITM09@fh-joanneum.at>
 *
 */
public class Mark {
	private String course;
	private int mark;
	private String term;
	private String markwords;

	public Mark(String course, int mark, String term, String markwords){
		this.setCourse(course);
		this.setMark(mark);
		this.setTerm(term);
		this.setMarkwords(markwords);
	}
	
	public Mark(){}

	public String getMarkwords() {
		return markwords;
	}

	public void setMarkwords(String markwords) {
		this.markwords = markwords;
	}
	
	public String getCourse() {
		return course;
	}

	public void setCourse(String course) {
		this.course = course;
	}

	public int getMark() {
		return mark;
	}

	public void setMark(int mark) {
		this.mark = mark;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}
	
	public String toString(){
		return course + " - " + mark + " (" + term + ")";
	}

}
