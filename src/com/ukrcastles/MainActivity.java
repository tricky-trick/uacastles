package com.ukrcastles;

import java.io.IOException;

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
import android.view.View;

public class MainActivity extends Activity {

	private Handler mHandler = new Handler();
	SQLiteDatabase db;
	DataBaseHelper myDbHelper;
	private String prefix;
	SharedPreferences prefs;
	private class AsyncStart extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			mHandler.post(new Runnable() {
				@SuppressLint("NewApi")
				public void run() {
					setContentView(R.layout.activity_main);
				}
			});
			return null;
		}
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if (!prefs.getString("prefix", "").equals("")){
			Intent i = new Intent(MainActivity.this,
					StartActivity.class);
			startActivity(i);
		}
		AsyncStart start = new AsyncStart();
		start.execute();
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
