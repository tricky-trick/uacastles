package com.ukrcastles;

import java.io.IOException;

import com.google.android.gms.maps.GoogleMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
	private static final int NEW_MENU_ID = Menu.FIRST + 1;
	SQLiteDatabase db;
	DataBaseHelper myDbHelper;
	SharedPreferences prefs;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefix = prefs.getString("prefix", "");
		setContentView(R.layout.activity_start);
		buttonMap = (Button) findViewById(R.id.localmap);
		buttonPlaces = (Button) findViewById(R.id.place);

		buttonMap.setText(getResources().getIdentifier(
				"start_button_map" + prefix, "string",
				getPackageName()));
		buttonPlaces.setText(getResources().getIdentifier(
				"start_button_places" + prefix, "string",
				getPackageName()));
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(
				0,
				NEW_MENU_ID,
				0,
				getString(getResources().getIdentifier(
						"change_language" + prefix, "string", getPackageName())));
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case NEW_MENU_ID: {
			prefs = PreferenceManager.getDefaultSharedPreferences(this);
			Editor editor = prefs.edit();
			editor.putString("prefix", "");
			editor.commit();
			Intent i = new Intent(StartActivity.this, MainActivity.class);
			startActivity(i);
		}
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
