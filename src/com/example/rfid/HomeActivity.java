package com.example.rfid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;

public class HomeActivity extends Activity {
	Button btn_lecture;
	Button btn_ecriture;
	Button btn_nfc;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homescreen);
		
		btn_lecture = (Button)this.findViewById(R.id.btn_lecture);
		btn_ecriture = (Button)this.findViewById(R.id.btn_ecriture);
		btn_nfc = (Button)this.findViewById(R.id.btn_nfc);
		
		btn_lecture.setOnClickListener(myOnlyhandler);
		btn_ecriture.setOnClickListener(myOnlyhandler);
		btn_nfc.setOnClickListener(myOnlyhandler);

		
	}
	
	View.OnClickListener myOnlyhandler = new View.OnClickListener() {
	  public void onClick(View v) {
	      if( btn_lecture.getId() == ((Button)v).getId() ){
	    	  Intent detailIntent = new Intent(v.getContext(), LectureProfileActivity.class);
	    	  startActivity(detailIntent);
	      }
	      else if( btn_ecriture.getId() == ((Button)v).getId() ){
	    	  Intent detailIntent = new Intent(v.getContext(), ProfileListActivity.class);
	    	  startActivity(detailIntent);
	      }
	      else if( btn_nfc.getId() == ((Button)v).getId() ){
	    	  Intent detailIntent = new Intent(v.getContext(), NfcExample.class);
	    	  startActivity(detailIntent);
	      }
	  }
	};

}
