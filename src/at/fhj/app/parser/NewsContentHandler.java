package at.fhj.app.parser;

import java.util.ArrayList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import at.fhj.app.model.NewsItem;

/**
 * Parse XML file to retrieve News
 * 
 * Store News from XML into ArrayList.
 * This is a standard process and does not need further documentation.
 * 
 * @author Markus Deutsch <Markus.Deutsch.ITM09@fh-joanneum.at>
 *
 */
public class NewsContentHandler implements ContentHandler {

	public ArrayList<NewsItem> news = new ArrayList<NewsItem>();
	private String current;
	private NewsItem item;
	
	/**
	 * There are several duplicate tag names in RSS feeds, like link and title, which 
	 * appear also before the actual news items.
	 * To avoid them triggering Exceptions we need a flag which decides whether to use
	 * tag contents or not.
	 */
	private boolean running = false;

	public void characters(char[] ch, int start, int length) throws SAXException {
		current = new String(ch, start, length);
	}

	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		if(localName.equals("item")) {
			item = new NewsItem();
			running = true;
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		if(running){
			if(localName.equals("title")) {
				item.setTitle(current);
			}
			
			if(localName.equals("link")){
				item.setLink(current);
			}
			
			if(localName.equals("description")){
				item.setDescription(current);
			}
	
			if (localName.equals("item")) {
				news.add(item);
			}
		}

  }

  public void endDocument() throws SAXException {}
  public void endPrefixMapping(String prefix) throws SAXException {}
  public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {}
  public void processingInstruction(String target, String data) throws SAXException {}
  public void setDocumentLocator(Locator locator) {}
  public void skippedEntity(String name) throws SAXException {}
  public void startDocument() throws SAXException {}
  public void startPrefixMapping(String prefix, String uri) throws SAXException {}
}