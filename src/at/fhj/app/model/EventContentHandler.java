package at.fhj.app.model;

import java.util.ArrayList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * Parse XML file to retrieve Events
 * 
 * Store Events from XML into ArrayList.
 * This is a standard process and does not need further documentation.
 * 
 * @author Markus Deutsch <Markus.Deutsch.ITM09@fh-joanneum.at>
 *
 */
public class EventContentHandler implements ContentHandler {

	public ArrayList<Event> events = new ArrayList<Event>();
	private String current;
	private Event event;
	
	private String course;
	private String year;

	public void characters(char[] ch, int start, int length) throws SAXException {
		current = new String(ch, start, length);
	}

	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		if(localName.equals("Event")) {
			event = new Event();
		}
  }

	public void endElement(String uri, String localName, String qName) throws SAXException {
		if(localName.equals("Course")) {
			this.course = current;
		}
		
		if(localName.equals("Year")) {
			this.year = current;
		}
		
		if(localName.equals("Title")) {
			event.setSubject(current);
		}
		
		if(localName.equals("Lecturer")){
			event.setLecturer(current);
		}
		
		if(localName.equals("Location")){
			event.setLocation(current);
		}
		
		if(localName.equals("Type")) {
			event.setType(current);
		}
		
		if(localName.equals("Start")){
			event.setStart(Integer.parseInt(current));
		}
		
		if(localName.equals("End")){
			event.setEnd(Integer.parseInt(current));
		}

		if (localName.equals("Event")) {
			event.setCourse(course);
			event.setYear(year);
			events.add(event);
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