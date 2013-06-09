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

    // Vérifie si le NFC est activé
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
    
    // Conversion d'un int en tableau de d'octet
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
    
    // Décode les données selon l'encodage ISO-8859-1
    public static String decodeISO(byte[] bytes) {
	    return new String(bytes, Charset.forName("ISO-8859-1"));
	}
    
    // Encode les caractéres selon l'encodage ISO-8859-1
    public static byte[] encodeISO(String string) {
	    return string.getBytes(Charset.forName("ISO-8859-1"));
	}
    
    // Encode les caractéres selon l'encodage ISO-8859-1 et les renvoi sous la forme de l'objet Byte
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
    
    // Encode les caractéres selon l'encodage ISO-8859-1 et les renvoi sous la forme du type primaire byte
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
    
    // Décode les données selon l'encodage UTF-8
    public static String decodeUTF8(byte[] bytes) {
	    return new String(bytes, Charset.forName("UTF-8"));
	}
    
    // Encode les caractéres selon l'encodage UTF-8
    public static Byte[] encodeUTF8(String string) {
    	byte[] result = string.getBytes(Charset.forName("UTF-8"));
    	Byte[] resultConvert = new Byte[result.length];
    	
    	for(int i = 0; i < result.length; i++){
    		resultConvert[i] = (Byte)result[i];
    	}
	    return resultConvert;
	}
	
    // Encapsulation du message pour standardisation
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

    // Affichage erreur
    public static void showError (final Activity activity, Exception ex) {
        Log.e(activity.getClass().getName(), ex.getMessage(), ex);
        new AlertDialog.Builder(activity)
            .setMessage(Utils.getErrorMessage(ex))
            .show();
    }

    // Cas de crash
    public static void showErrorAndFinish (final Activity activity, Exception ex) {
        try {
            Log.e(activity.getClass().getName(), Utils.getErrorMessage(ex));
            ex.printStackTrace();

            new AlertDialog.Builder(activity)
                .setMessage(Utils.getErrorMessage(ex))
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        activity.finish();
                    }
                })
                .show();
        } catch (WindowManager.BadTokenException unused) {
            /* Ignore... happens if the activity was destroyed */
        }
    }

    // Conversion hexa des données en chaine
    public static String getHexString (byte[] b) throws Exception {
        String result = "";
        for (int i=0; i < b.length; i++) {
            result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
        }
        return result;
    }

    // Conversion hexa des données en chaine
    public static String getHexString (byte[] b, String defaultResult) {
        try {
            return getHexString(b);
        } catch (Exception ex) {
            return defaultResult;
        }
    }

    // Conversion chaine vers hexa
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
    
    // Conversion tableau de données vers int
    public static int byteArrayToInt(byte[] b) {
        return byteArrayToInt(b, 0);
    }
    
    // Conversion tableau de données vers int
    public static int byteArrayToInt(byte[] b, int offset) {
        return byteArrayToInt(b, offset, b.length);
    }
    
    // Conversion tableau de données vers int
    public static int byteArrayToInt(byte[] b, int offset, int length) {
        return (int) byteArrayToLong(b, offset, length);
    }

    // Conversion tableau de données vers long
    public static long byteArrayToLong(byte[] b, int offset, int length) {
        if (b.length < length)
            throw new IllegalArgumentException("length must be less than or equal to b.length");

        long value = 0;
        for (int i = 0; i < length; i++) {
            int shift = (length - 1 - i) * 8;
            value += (b[i + offset] & 0x000000FF) << shift;
        }
        return value;
    }

    // Découpe du tableau de données selon une certaine longueur
    public static byte[] byteArraySlice(byte[] b, int offset, int length) {
        byte[] ret = new byte[length];
        for (int i = 0; i < length; i++)
            ret[i] = b[offset+i];
        return ret;
    }

    // Conversion des nodes xml vers chaine de caractére
    public static String xmlNodeToString (Node node) throws Exception {
        // The amount of code required to do simple things in Java is incredible.
        Source source = new DOMSource(node);
        StringWriter stringWriter = new StringWriter();
        Result result = new StreamResult(stringWriter);
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.setURIResolver(null);
        transformer.transform(source, result);
        return stringWriter.getBuffer().toString();
    }

    // Récupére message d'erreur
    public static String getErrorMessage (Throwable ex) {
        String errorMessage = ex.getLocalizedMessage();
        if (errorMessage == null)
            errorMessage = ex.getMessage();
        if (errorMessage == null)
            errorMessage = ex.toString();

        if (ex.getCause() != null) {
            String causeMessage = ex.getCause().getLocalizedMessage();
            if (causeMessage == null)
                causeMessage = ex.getCause().getMessage();
            if (causeMessage == null)
                causeMessage = ex.getCause().toString();

            if (causeMessage != null)
                errorMessage += ": " + causeMessage;
        }

        return errorMessage;
    }

    // récupére des informations sur le systéme tel que le modéle et le numéro de série
    public static String getDeviceInfoString() {
        return String.format("nModel: %s (%s %s)\nOS: %s\n\n",
            Build.MODEL,
            Build.MANUFACTURER,
            Build.BRAND,
            Build.VERSION.RELEASE);
    }

    // Recherche dans une liste
    public static <T> T findInList(List<T> list, Matcher matcher) {
        for (T item : list) {
            if (matcher.matches(item)) {
                return item;
            }
        }
        return null;
    }

    public static interface Matcher<T> {
        public boolean matches(T t);
    }

    // Conversion de données vers int
    public static int convertBCDtoInteger(byte data) {
        return (((data & (char)0xF0) >> 4) * 10) + ((data & (char)0x0F));
    }

    public static int getBitsFromInteger(int buffer, int iStartBit, int iLength) {
        return (buffer >> (iStartBit)) & ((char)0xFF >> (8 - iLength));
    }

    /* Based on function from mfocGUI by 'Huuf' (http://www.huuf.info/OV/) */
    public static int getBitsFromBuffer(byte[] buffer, int iStartBit, int iLength) {
        int iEndBit = iStartBit + iLength - 1;
        int iSByte = iStartBit / 8;
        int iSBit = iStartBit % 8;
        int iEByte = iEndBit / 8;
        int iEBit = iEndBit % 8;

        if (iSByte == iEByte) {
            return (int)(((char)buffer[iEByte] >> (7 - iEBit)) & ((char)0xFF >> (8 - iLength)));
        } else {
            int uRet = (((char)buffer[iSByte] & (char)((char)0xFF >> iSBit)) << (((iEByte - iSByte - 1) * 8) + (iEBit + 1)));

            for (int i = iSByte + 1; i < iEByte; i++) {
                uRet |= (((char)buffer[i] & (char)0xFF) << (((iEByte - i - 1) * 8) + (iEBit + 1)));
            }

            uRet |= (((char)buffer[iEByte] & (char)0xFF)) >> (7 - iEBit);

            return uRet;
        }
    }
}
