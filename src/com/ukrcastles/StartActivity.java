package com.ukrcastles;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class StartActivity extends BaseActivity {

	Button buttonMap;
	Button buttonPlaces;
	String prefix;
	private static final int MAIN_MENU_ID = 1001;
	private static final int ABOUT_MENU_ID = 1005;
	SQLiteDatabase db;
	DataBaseHelper myDbHelper;
	SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefix = prefs.getString("prefix", "");
		buttonMap = (Button) findViewById(R.id.localmap);
		buttonPlaces = (Button) findViewById(R.id.place);

		buttonMap.setText(getResources().getIdentifier(
				"start_button_map" + prefix, "string", getPackageName()));
		buttonPlaces.setText(getResources().getIdentifier(
				"start_button_places" + prefix, "string", getPackageName()));
		enableGpsModal(prefix);
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
				MAIN_MENU_ID,
				0,
				getString(getResources().getIdentifier(
						"change_language" + prefix, "string", getPackageName())));
		menu.add(
				0,
				ABOUT_MENU_ID,
				1,
				getString(getResources().getIdentifier("about_menu" + prefix,
						"string", getPackageName())));
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		if (item.getItemId() == MAIN_MENU_ID) {
			prefs = PreferenceManager.getDefaultSharedPreferences(this);
			Editor editor = prefs.edit();
			editor.putString("prefix", "");
			editor.commit();
			i = new Intent(StartActivity.this, MainActivity.class);
		} else {
			i = new Intent(StartActivity.this, AboutActivity.class);
		}
		startActivity(i);
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Monitor launch times and interval from installation
		RateThisApp.onStart(this);
		// Show a dialog if criteria is satisfied
		RateThisApp.showRateDialogIfNeeded(this);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_start,
					container, false);
			return rootView;
		}
	}
}
