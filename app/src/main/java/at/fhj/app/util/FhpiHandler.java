package at.fhj.app.util;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class FhpiHandler implements ContentHandler {

	private String current;
	private String status;

	public void characters(char[] ch, int start, int length) throws SAXException {
		current = new String(ch, start, length);
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		if(localName.equals("Status")) {
			this.status = current;
		}
	}
	
	public String getStatus(){
		return this.status;
	}
	
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {}
	public void endDocument() throws SAXException {}
	public void endPrefixMapping(String prefix) throws SAXException {}
	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {}
	public void processingInstruction(String target, String data) throws SAXException {}
	public void setDocumentLocator(Locator locator) {}
	public void skippedEntity(String name) throws SAXException {}
	public void startDocument() throws SAXException {}
	public void startPrefixMapping(String prefix, String uri) throws SAXException {}
}