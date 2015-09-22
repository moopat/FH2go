package at.fhj.app.model;

public class NavigationItem {
	
	private int id;
	private int imageResource;
	private int titleResource;
	
	public NavigationItem(int id, int imageResource, int titleResource) {
		super();
		this.id = id;
		this.imageResource = imageResource;
		this.titleResource = titleResource;
	}
	
	public int getImageResource() {
		return imageResource;
	}
	
	public void setImageResource(int imageResource) {
		this.imageResource = imageResource;
	}
	
	public int getTitleResource() {
		return titleResource;
	}
	
	public void setTitleResource(int titleResource) {
		this.titleResource = titleResource;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

}
