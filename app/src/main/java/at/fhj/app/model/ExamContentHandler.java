package at.fhj.app.model;

import java.util.ArrayList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * Parse XML file to retrieve Exams
 * 
 * Store Exams from XML into ArrayList.
 * This is a standard process and does not need further documentation.
 * 
 * @author Markus Deutsch <Markus.Deutsch.ITM09@fh-joanneum.at>
 *
 */
public class ExamContentHandler implements ContentHandler {

	public ArrayList<Exam> exams = new ArrayList<Exam>();
	private String current;
	private Exam exam;
	private String term;

	public void characters(char[] ch, int start, int length) throws SAXException {
		current = new String(ch, start, length);
	}

	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		if(localName.equals("Term")){
			term = atts.getValue("name");
		}
		
		if(localName.equals("Exam")) {
			exam = new Exam();
			exam.setTerm(term);
    }
  }

	public void endElement(String uri, String localName, String qName) throws SAXException {
		if(localName.equals("Title")) {
			exam.setCourse(current);
		}
		
		if(localName.equals("Type")) {
			exam.setType(current);
		}
		
		if(localName.equals("DateUnix")) {
			try {
				exam.setExamDate(Integer.parseInt(current));
			} catch(Exception e){
				exam.setExamDate(0);
			}
		}
		
		if(localName.equals("RegistrationEndUnix")){
			try {
				exam.setExamRegistrationEnd(Integer.parseInt(current));
			} catch(Exception e){
				exam.setExamRegistrationEnd(0);
			}
		}
		
		if(localName.equals("Id")){
			exam.setId(current);
		}

        if(localName.equals("ExamStatusReadable")){
            exam.setStatusReadable(current);
        }
		
		if(localName.equals("ExamStatus")){
			exam.setStatus(current);
		}

        if(localName.equals("Mode")){
            exam.setMode(current);
        }
		
		if(localName.equals("Exam")){
			exams.add(exam);
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