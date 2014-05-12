package com.ukrcastles;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
	private Handler mHandler = new Handler();
	
	private class AsyncStartActivity extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			mHandler.post(new Runnable() {
				@SuppressLint("NewApi")
				public void run() {
					setContentView(R.layout.activity_start);
					buttonMap = (Button) findViewById(R.id.localmap);
					buttonPlaces = (Button) findViewById(R.id.place);
					prefix = getIntent().getStringExtra("prefix");
					
					buttonMap.setText(getResources().getIdentifier("start_button_map" + prefix, "string", getPackageName()));
					buttonPlaces.setText(getResources().getIdentifier("start_button_places" + prefix, "string", getPackageName()));
					
					Intent i = new Intent( StartActivity.this, StartActivity.class);
					i.putExtra("prefix", prefix);
				}
			});
			return null;
		}
		
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AsyncStartActivity start = new AsyncStartActivity();
		start.execute();
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
