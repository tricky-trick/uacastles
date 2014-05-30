package com.ukrcastles;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;

@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class MainActivity extends Activity {

	SQLiteDatabase db;
	DataBaseHelper myDbHelper;
	SharedPreferences prefs;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Gallery gallery = (Gallery) findViewById(R.id.gallery1);
		gallery.setSpacing(1);
		gallery.setAdapter(new GalleryImageAdapter(this));
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if (!prefs.getString("prefix", "").equals("")) {
			Intent i = new Intent(MainActivity.this, StartActivity.class);
			startActivity(i, savedInstanceState);
		}

		// clicklistener for Gallery
		gallery.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				 // TODO Auto-generated method stub
				if (position == 0) {
					updateValue("_ua");
					Intent i = new Intent(MainActivity.this,
							StartActivity.class);
					startActivity(i);
				} else if (position == 1) {
					updateValue("_pl");
					Intent i = new Intent(MainActivity.this,
							StartActivity.class);
					startActivity(i);
				} else if (position == 2) {
					updateValue("_en");
					Intent i = new Intent(MainActivity.this,
							StartActivity.class);
					startActivity(i);
				}
			}
		});
	}

	private void updateValue(String val) {
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor = prefs.edit();
		editor.putString("prefix", val);
		editor.commit();
	}
	
	@SuppressWarnings("unused")
	private void addShortcut() {
	    //Adding shortcut for MainActivity 
	    //on Home screen
	    Intent shortcutIntent = new Intent(getApplicationContext(),
	            MainActivity.class);

	    shortcutIntent.setAction(Intent.ACTION_MAIN);

	    Intent addIntent = new Intent();
	    addIntent
	            .putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
	    addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, R.string.app_name);
	    addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
	            Intent.ShortcutIconResource.fromContext(getApplicationContext(),
	                    R.drawable.ic_launcher));

	    addIntent
	            .setAction("com.android.launcher.action.INSTALL_SHORTCUT");
	    getApplicationContext().sendBroadcast(addIntent);
	}
}
