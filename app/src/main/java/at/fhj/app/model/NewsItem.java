package at.fhj.app.model;

/**
 * This is the model class for News Items.
 * 
 * News items are retrieved from FH JOANNEUM's RSS feed
 * and then stored in this class.
 * 
 * @author Markus Deutsch <Markus.Deutsch.ITM09@fh-joanneum.at>
 *
 */
public class NewsItem {
	private String title;
	private String link;
	private String description;
	
	public NewsItem(){}

	public NewsItem(String title, String link, String description) {
		super();
		this.title = title;
		this.link = link;
		this.description = description;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
