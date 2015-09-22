package at.fhj.app.model;

import java.util.ArrayList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * Parse XML file to retrieve Marks
 * 
 * Store Marks from XML into ArrayList.
 * This is a standard process and does not need further documentation.
 * 
 * @author Markus Deutsch <Markus.Deutsch.ITM09@fh-joanneum.at>
 *
 */
public class MarkContentHandler implements ContentHandler {

	public ArrayList<Mark> marks = new ArrayList<Mark>();
	private String current;
	private Mark mark;
	private String term;

	public void characters(char[] ch, int start, int length) throws SAXException {
		current = new String(ch, start, length);
	}

	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		if(localName.equals("Term")){
			term = atts.getValue("name");
		}
		
		if(localName.equals("Course")) {
			mark = new Mark();
			mark.setTerm(term);
    }
  }

	public void endElement(String uri, String localName, String qName) throws SAXException {
		if(localName.equals("Title")) {
			mark.setCourse(current);
		}
		
		if(localName.equals("Grade")){
			mark.setMark(Integer.parseInt(current));
		}
		
		if(localName.equals("GradeWords")){
			mark.setMarkwords(current);
		}

		if (localName.equals("Course")) {
			marks.add(mark);
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