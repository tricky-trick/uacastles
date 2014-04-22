package com.ukrcastles;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class StartActivity extends Activity {
	
	Button buttonMap;
	Button buttonPlaces;
	String prefix;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		buttonMap = (Button) findViewById(R.id.localmap);
		buttonPlaces = (Button) findViewById(R.id.place);
		if (savedInstanceState == null) {
		    Bundle extras = getIntent().getExtras();
		    if(extras == null) {
		        prefix= "";
		    } else {
		        prefix= extras.getString("prefix");
		    }
		} else {
		    prefix= (String) savedInstanceState.getSerializable("prefix");
		}
		
		buttonMap.setText(getResources().getIdentifier("start_button_map" + prefix, "string", this.getPackageName()));
		buttonPlaces.setText(getResources().getIdentifier("start_button_places" + prefix, "string", this.getPackageName()));
	}

	public void startPlace(View v) {
		Intent i = new Intent(StartActivity.this, PlacesActivity.class);
		i.putExtra("prefix", prefix);
		startActivity(i);
	}

	public void startLocalMap(View v) {
		Intent i = new Intent(StartActivity.this, MapActivity.class);
		i.putExtra("prefix", prefix);
		startActivity(i);
	}
}
