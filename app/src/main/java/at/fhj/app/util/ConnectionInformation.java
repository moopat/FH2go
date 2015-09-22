package at.fhj.app.util;

import java.io.StringReader;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import at.fhj.app.R;

public class ConnectionInformation {
	
	public static String getConnectionStatus(String result){
		System.setProperty("org.xml.sax.driver","org.xmlpull.v1.sax2.Driver");
		try {
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			FhpiHandler fh = new FhpiHandler();
			xmlReader.setContentHandler(fh);
			xmlReader.parse(new InputSource(new StringReader(result)));
			return fh.getStatus() == null ? "OK" : fh.getStatus();
		} catch(Exception e) {
			e.printStackTrace();
			return "ERROR";
		}
	}
	
	public static int getConnectionStatusString(String status){
		if(status.equals("ERROR")){
			return R.string.msg_error;
		} else if (status.equals("INVALID_DATA")){
			return R.string.msg_invaliddata;
		} else if (status.equals("INCOMPLETE_DATA")){
			return R.string.msg_incompletedata;
		} else if (status.equals("LOCKED")){
			return R.string.msg_locked;
		} else {
			return R.string.msg_error;
		}
	}
	
	public static int getExamStatusString(String status){
		if(status.equals("registered")){
			return R.string.exam_registered;
		} else if (status.equals("notRegistered")){
			return R.string.exam_notregistered;
		} else if (status.equals("takenPlace")){
			return R.string.exam_takenplace;
		} else if (status.equals("signedOff")){
			return R.string.exam_signedoff;
		} else if (status.equals("signedOffByOffice")){
			return R.string.exam_signedoffbyoffice;
		} else if (status.equals("registeredByOffice")){
			return R.string.exam_registeredbyoffice;
		} else {
			return R.string.msg_unknown;
		}
	}
}
