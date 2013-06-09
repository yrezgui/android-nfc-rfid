package com.example.rfid;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.util.Log;

public class XMLParser extends DefaultHandler {

	private Profile profile = null;
	private StringBuffer buffer;
	
	
	@Override
	public void startDocument() throws SAXException {
		//Open the document
		Log.w("DEBUT DOCUMENT","Handler");
		
	}
	@Override
	public void endDocument() throws SAXException {
		// End of the document
		Log.w("END DOCUMENT","Handler");
	}
	
	@Override
	public void startElement(String namespaceURI, String localName,String qName, Attributes atts) throws SAXException {
		
		Log.i("Start LocalName", localName);
		buffer = new StringBuffer();
		
		if (localName.equals("profil"))
		{
			Log.i("XML", "Creation du profile");
			profile = new Profile();
		}
		else if (localName.equals("properties"))
		{
			profile.initProperties();
		}
		else if (localName.equals("entry"))
		{
			String name = atts.getValue("name");
			String type = atts.getValue("type");
			String required = atts.getValue("required");
			profile.addProperty(name,type,required);
		}
		else{
		
		}
	}
	
	@Override
	public void endElement(String namespaceURI, String localName, String qName)throws SAXException {
		if(localName.equals("id")){
			profile.setId(buffer.toString());
		}
		else if(localName.equals("name")){
			profile.setName(buffer.toString());
		}
	}
	
	public void characters(char[] ch,int start, int length) throws SAXException{
        String lecture = new String(ch,start,length);
        if(buffer != null) buffer.append(lecture);
    }

	
	public Profile getProfile()
	{
		return this.profile;
	}
	
}
