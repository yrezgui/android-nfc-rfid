package com.example.rfid;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.XmlResourceParser;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class LectureProfileActivity extends Activity {
	
	NfcAdapter adapter;
	PendingIntent pendingIntent;
	IntentFilter writeTagFilters[];
	Tag mytag;
	List<String> values;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_screen);
		
		 adapter = NfcAdapter.getDefaultAdapter(this);
		pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
		tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
		writeTagFilters = new IntentFilter[] { tagDetected };
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		Log.i("Lecture" , "OK");
		values = this.getCardValues(intent);
		createProfile(values.get(0), intent);

	}
	
	private String lecture(DesfireProtocol dfp,int file) throws Exception {
    	byte[] msg = dfp.readFile(file);
    	return Utils.decodeUTF8(msg);
    }
	  
	
	public void createProfile(String id, Intent intent){
		
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = null;
			
			sp = spf.newSAXParser();
			 /* * Create a new ContentHandler and apply it to the
			 * XML-Reader
			 * */
			XMLParser gsh = new XMLParser ();
			InputStream instream = getAssets().open("profiles/profile_"+id+".xml");

	        sp.parse(instream, gsh);
	        
	        Profile p = gsh.getProfile();
	        p.setValues(values);
	        
	        this.displayProfile(p);

	        
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		
	}
	public void displayProfile(Profile profile){
		
		TextView profile_id = (TextView)this.findViewById(R.id.profil_number);
		if(profile.getName() == null || profile.getName().equals(""))
			profile_id.setText("Profil #" + profile.getId());
		else
			profile_id.setText(profile.getName());
		
		ListView listView = (ListView)this.findViewById(R.id.description_profile);
		List<Property> properties = profile.getProperties();
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		
		for(int i = 0; i < properties.size(); i++){
			Map<String, String> entity = new HashMap<String, String>(2);
			entity.put("title", properties.get(i).getName());
			entity.put("value", profile.getValue(i));
		    data.add(entity);
		}
		
		SimpleAdapter adapter = new SimpleAdapter(this, data,
                android.R.layout.simple_list_item_2,
                new String[] {"title", "value"},
                new int[] {android.R.id.text1,
                           android.R.id.text2});
		
		listView.setAdapter(adapter);
	}
	
	
	
	private List<String> getCardValues(Intent intent){
		
		List<String> values = new ArrayList<String>();
		
		mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		
		IsoDep idTag = IsoDep.get(mytag);
		DesfireProtocol dfp = new DesfireProtocol(idTag);
		
		try {
	          idTag.connect();
	          int[] appList = dfp.getAppList();
	          
	          dfp.selectApp(appList[0]);

	          int[] fileList = dfp.getFileList();
	          byte[] detailFile = dfp.readFile(fileList[0]);
	          String str = Utils.decodeUTF8(detailFile);
	          Log.i("Str", str);
	          String[] part = str.split("#", -1);
	          String[] tokens = part[0].split(";", -1);
		  		
		  	  for(int i = 0; i < tokens.length; i++) {
		  		  Log.i("Test", tokens[i]);
		  		  values.add(tokens[i]);
		  	  }
	  		
	          
	          Log.v("List File detail", str);


	      } catch (IOException e) {
	    	  Log.i("Erreur", "IOException");
	      } catch (Exception e) {
	    	  Log.i("Erreur", "Exception");
	      }
		
	
		return values;
	}
	
    @Override
	public void onPause(){
		super.onPause();
		adapter.disableForegroundDispatch(this);
	}

	@Override
	public void onResume(){
		super.onResume();
		adapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
	}
	
}
