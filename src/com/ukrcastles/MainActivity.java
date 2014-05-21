package com.ukrcastles;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;

public class MainActivity extends Activity {

	SQLiteDatabase db;
	DataBaseHelper myDbHelper;
	SharedPreferences prefs;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if (!prefs.getString("prefix", "").equals("")) {
			Intent i = new Intent(MainActivity.this, StartActivity.class);
			startActivity(i, savedInstanceState);
		}
	}

	public void langUa(View v) {
		updateValue("_ua");
		Intent i = new Intent(this, StartActivity.class);
		startActivity(i);
	}

	public void langPl(View v) {
		updateValue("_pl");
		Intent i = new Intent(this, StartActivity.class);
		startActivity(i);
	}

	public void langEn(View v) {
		updateValue("_en");
		Intent i = new Intent(this, StartActivity.class);
		startActivity(i);
	}

	private void updateValue(String val) {
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor = prefs.edit();
		editor.putString("prefix", val);
		editor.commit();
	}    
}
