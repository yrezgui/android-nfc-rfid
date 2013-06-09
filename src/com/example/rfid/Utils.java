package com.example.rfid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.rfid.R;

import org.w3c.dom.Node;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.List;

public class Utils {

    // Vérify if the NFC is activated
    public static void checkNfcEnabled(final Activity activity, NfcAdapter adapter) {
        if (adapter.isEnabled()) {
            return;
        }
        new AlertDialog.Builder(activity)
            .setTitle(R.string.nfc_disabled)
            .setMessage(R.string.turn_on_nfc)
            .setCancelable(true)
            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            })
            .setNeutralButton(R.string.wireless_settings, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    activity.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                }
            })
            .show();
    }
    
    // Conversion from an int to an array of bytes
    public static final byte[] intToByteArray(int value) {
        byte[] tmp = new byte[] {
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value
        };
        
        return new byte[] {
        		tmp[2],
                tmp[1],
                tmp[0]
        };
    }
    
    // Decoding the data in the charset ISO-8859-1
    public static String decodeISO(byte[] bytes) {
	    return new String(bytes, Charset.forName("ISO-8859-1"));
	}
    
    // Encoding the data in the charset ISO-8859-1
    public static byte[] encodeISO(String string) {
	    return string.getBytes(Charset.forName("ISO-8859-1"));
	}
    
    // Encoding the data in the charset ISO-8859-1 and return an array of Bytes
    public static Byte[] encodeISO(String string, int length) {
	    byte[] temp = string.getBytes(Charset.forName("ISO-8859-1"));
	    byte[] result = new byte[length];

	    
	    for(int i = 0; i < temp.length; i++) {
	    	result[i] = temp[i];
	    }
	    
	    for(int i = temp.length; i < length; i++) {
	    	result[i] = (byte) 0x0;
	    }
	    
	    Byte[] resultConvert = new Byte[result.length];
    	
    	for(int i = 0; i < result.length; i++){
    		resultConvert[i] = (Byte)result[i];
    	}
    	
	    return resultConvert;
	}
    
    // Encoding the data in the charset ISO-8859-1 and return an array of bytes
    public static byte[] encodeISOByte(String string, int length) {
	    byte[] temp = string.getBytes(Charset.forName("ISO-8859-1"));
	    byte[] result = new byte[length];
	    
	    
	    if(length > temp.length){
		    for(int i = 0; i < temp.length; i++) {
		    	result[i] = temp[i];
		    }
    	}
	    else{
	    	for(int i = 0; i < length; i++) {
		    	result[i] = temp[i];
		    }
	    }
	    
	    for(int i = temp.length; i < length; i++) {
	    	result[i] = (byte) 0x0;
	    }
	    
    	Log.i("Resultat", result + "");
	    return result;
	}
	
    // Encapsulation of the message for the NFC communication
    public static byte[] wrapMessage (byte command, byte[] parameters) throws Exception {
	    ByteArrayOutputStream stream = new ByteArrayOutputStream();

	    stream.write((byte) 0x90);
	    stream.write(command);
	    stream.write((byte) 0x00);
	    stream.write((byte) 0x00);
	    if (parameters != null) {
	        stream.write((byte) parameters.length);
	        stream.write(parameters);
	    }
	    stream.write((byte) 0x00);

	    return stream.toByteArray();
	}

    // Conversion bytes data to string
    public static String getHexString (byte[] b) throws Exception {
        String result = "";
        for (int i=0; i < b.length; i++) {
            result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
        }
        return result;
    }

    // Conversion bytes data to string with a default result
    public static String getHexString (byte[] b, String defaultResult) {
        try {
            return getHexString(b);
        } catch (Exception ex) {
            return defaultResult;
        }
    }

    // Conversion string data to bytes[]
    public static byte[] hexStringToByteArray (String s) {
        if ((s.length() % 2) != 0) {
            throw new IllegalArgumentException("Bad input string: " + s);
        }
        
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    
    // Conversion byte[] to int
    public static int byteArrayToInt(byte[] b) {
        return byteArrayToInt(b, 0);
    }

    // Get information about the card (NFC)
    public static String getDeviceInfoString() {
        return String.format("nModel: %s (%s %s)\nOS: %s\n\n",
            Build.MODEL,
            Build.MANUFACTURER,
            Build.BRAND,
            Build.VERSION.RELEASE);
    }
}
