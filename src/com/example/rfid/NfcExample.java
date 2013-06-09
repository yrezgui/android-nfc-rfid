package com.example.rfid;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.example.rfid.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.Toast;

public class NfcExample extends Activity {
 
	NfcAdapter adapter;
	PendingIntent pendingIntent;
	IntentFilter writeTagFilters[];
	Tag mytag;
	Context ctx;
	AlertDialog mDialog;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc_detail);
        
        adapter = NfcAdapter.getDefaultAdapter(this);
		pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
		tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
		writeTagFilters = new IntentFilter[] { tagDetected };
		
		// Dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.dialog_text).setTitle(R.string.nfc_card_waiting);
		mDialog = builder.create();
		mDialog.show();
		
		String str = "ATELIER;58;Yacine;Rezgui;Étudiant;14.5;;06300;Nice;1#";
		String[] tokens = str.split(";", -1);
		
		for(int i = 0; i < tokens.length; i++) {
			Log.v("SPLIT", tokens[i]);
		}
    }
    
    @Override
	protected void onNewIntent(Intent intent) {
		Log.d("ISO_DEP","OnNewIntent: "+intent);
	    
		mDialog.dismiss();
		
		mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		//Toast.makeText(this, this.getString(R.string.ok_detection) + mytag.toString(), Toast.LENGTH_LONG ).show();
		
		IsoDep idTag = IsoDep.get(mytag);
		DesfireProtocol dfp = new DesfireProtocol(idTag);
		String info = null;
		
		try {
	          idTag.connect();
	          //toast("Connected to IsoDep Tag...\n");

	          int[] appList = dfp.getAppList();
	          
	          /*for(int i = 0; i < appList.length; i++) {
	        	  Log.v("List App", String.valueOf(appList[i]));
	          }*/
	          
	          dfp.selectApp(appList[0]);
	          //toast("Selected app no: " + appList[0] + "..\n");

	          int[] fileList = dfp.getFileList();
	          /*toast("Selected file no: " + fileList[0] + "\n");
	          
	          for(int i = 0; i < fileList.length; i++) {
	        	  Log.v("List File", String.valueOf(fileList[i]));
	          }*/
	          
//	          byte[] detailFile = dfp.readFile(fileList[0]);
//	          String s = Utils.decodeUTF8(detailFile);
	          String s = "58;Yacine;Rezgui;etudiant;14.5;;06300;Nice;1#";
	          
	          Log.i("Debut","Ecriture");
	          
	          
	   
//	          
//	          Log.v("List File detail", s);
//
//	          /*byte[] params = {(byte)fileList[0], 
//	                           (byte)0x0, (byte)0x0, (byte)0x0, 
//	                           (byte)0x2, (byte)0x0, (byte)0x0,
//	                           (byte)0x41, (byte)0x41};
//	          byte[] message = wrapMessage((byte) 0x3d, params);
//
//	          byte[] result = idTag.transceive(writeFile(fileList[0], "Text like this"));*/
//	          
//	          
//	          byte[] bytes =  Utils.encodeISOByte(s, 47);
//	         Log.i("Byte length", bytes.length + "");
////	          byte[59] params = {
////	              // FileId 1 Byte
////        		  (byte)fileList[0],
////        		  // Offset 3 Bytes
////                  (byte)0x0, (byte)0x0, (byte)0x0,
////                  // Length 3 Bytes
////                  (byte)0x34, (byte)0x0, (byte)0x0
////                  // Data 0-52 Bytes
////                  /*(byte)0x59, (byte)0x41, (byte)0x43, (byte)0x49, (byte)0x4E, (byte)0x45,
////                  (byte)0x20, (byte)0x52, (byte)0x4F, (byte)0x43, (byte)0x4B, (byte)0x53*/
//////                  (byte)0x40, 0x0
////              };
//	          
//	          byte[] params = new byte[54];
//	          
//	          params[0] = (byte)fileList[0];
//	          params[1] = (byte)0x0;
//	          params[2] = (byte)0x0;
//	          params[3] = (byte)0x0;
//	          params[4] = (byte)0x2F;
//	          params[5] = (byte)0x0;
//	          params[6] = (byte)0x0;
//	          
//	          for(int i = 0 ; i < bytes.length; i++){
//	        	  params[7 + i] = (byte)bytes[i];
//	          }
//	          
//	          
//	          Log.i("ok", "ok");
//	          byte[] message = Utils.wrapMessage((byte) 0x3d, params);
//	          Log.i("ok", Utils.getHexString(message));
//	          
//	          byte[] result = idTag.transceive(message);
	          
	          byte[] result = this.ecriture(dfp, fileList[0], s);
	          
	          AlertDialog.Builder builder = new AlertDialog.Builder(this);
   	  		  builder.setMessage(Utils.getHexString(result)).setTitle(R.string.dialog_title);
   	  		  mDialog = builder.create();
   	  		  mDialog.show();
	   	  		  
	          builder = new AlertDialog.Builder(this);
	          //toast("Result bytes: " + Utils.getHexString(result) + "\n");

	      } catch (IOException e) {
	    	  toast("Could not connect to IsoDep Tag...\n");
	      } catch (Exception e) {
	    	  toast("Error messages: " + e.getMessage() + " -- " + e.getLocalizedMessage() + "\n");
	      }
	}
    
    private String lecture(DesfireProtocol dfp,int file) throws Exception {
    	byte[] msg = dfp.readFile(file);
    	return Utils.decodeUTF8(msg);
    }
    
    private byte[] ecriture(DesfireProtocol dfp,int file,  String message) throws Exception{
    	return dfp.writeFile(file, message);
    }
    
    private void toast(String info) {
		Toast.makeText(this, info, Toast.LENGTH_LONG ).show();
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
