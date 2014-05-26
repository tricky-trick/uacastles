package com.ukrcastles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.model.LatLng;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

@SuppressLint("NewApi")
public class PlacesActivity extends Activity implements OnItemClickListener {

	DataBaseHelper myDbHelper;
	LatLng myCoord = null;
	LatLng myNearCoord = null;
	SQLiteDatabase db;
	Handler mHandler = new Handler();;
	ArrayList<String> it;
	GPSTracker gpsTracker;
	ListView listView;
	List<RowItem> rowItems;
	String prefix;

	private class AsyncMaps extends AsyncTask<String, Void, ArrayList<String>> {

		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(PlacesActivity.this);
			dialog.setTitle(getResources().getIdentifier(
					"dialog_title_string" + prefix, "string", getPackageName()));
			dialog.setMessage(getString(getResources().getIdentifier(
					"load_string" + prefix, "string", getPackageName())));
			dialog.setIndeterminate(true);
			dialog.setCancelable(false);
			dialog.show();
		}

		@Override
		protected ArrayList<String> doInBackground(String... params) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			myDbHelper = new DataBaseHelper(PlacesActivity.this);
			try {
				myDbHelper.createDataBase();
			} catch (IOException ioe) {
				throw new Error("Unable to create database");
			}
			try {
				myDbHelper.openDataBase();
			} catch (SQLException sqle) {
				throw sqle;
			}
			mHandler.post(new Runnable() {
				@SuppressLint("NewApi")
				@SuppressWarnings({ "unchecked", "rawtypes" })
				public void run() {
					gpsTracker = new GPSTracker(PlacesActivity.this);
					if (gpsTracker.canGetLocation()) {
						String stringLatitude = String
								.valueOf(gpsTracker.latitude);
						String stringLongitude = String
								.valueOf(gpsTracker.longitude);
						if (stringLatitude.equals("0.0")) {
							stringLatitude = "49.853192";
							stringLongitude = "24.024499";
						}
						myCoord = new LatLng(
								Double.parseDouble(stringLatitude), Double
										.parseDouble(stringLongitude));
					}
					db = myDbHelper.getWritableDatabase();
					Cursor c = db.query("info_data", new String[] { "*" },
							null, null, null, null, null);
					float distance;
					Map<String, Float> mapDist = new HashMap<>();
					for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

						String coordinates = c.getString(c
								.getColumnIndex("coordinates"));
						String name = c.getString(c.getColumnIndex("name" + prefix))
								.replace(";", ",");
						String image = c.getString(c.getColumnIndex("image"));

						LatLng coord = new LatLng(Double
								.parseDouble(coordinates.split(",")[0]), Double
								.parseDouble(coordinates.split(",")[1]));
						Location locMy = new Location("");
						locMy.setLatitude(myCoord.latitude);
						locMy.setLongitude(myCoord.longitude);

						Location locTo = new Location("");
						locTo.setLatitude(coord.latitude);
						locTo.setLongitude(coord.longitude);
						distance = locMy.distanceTo(locTo);

						mapDist.put(distance + ";" + image + ";" + coordinates
								+ ";" + name, distance);
					}
					List list = new LinkedList(mapDist.entrySet());
					Collections.sort(list, new Comparator() {
						public int compare(Object o1, Object o2) {
							return ((Comparable) ((Map.Entry) (o1)).getValue())
									.compareTo(((Map.Entry) (o2)).getValue());
						}
					});

					Map<String, Float> result = new LinkedHashMap<String, Float>();
					for (Iterator it = list.iterator(); it.hasNext();) {
						Map.Entry<String, Float> entry = (Map.Entry) it.next();
						result.put(entry.getKey(), entry.getValue());
					}
					myNearCoord = new LatLng(Double.parseDouble(result.keySet()
							.iterator().next().toString().split(";")[2]
							.split(",")[0]), Double.parseDouble(result.keySet()
							.iterator().next().toString().split(";")[2]
							.split(",")[1]));

					it = new ArrayList<String>();
					for (Entry<String, Float> ent : result.entrySet()) {
						it.add(ent.getKey());
					}
				}
			});
			return it;
		}

		@Override
		protected void onPostExecute(ArrayList<String> list) {
			rowItems = new ArrayList<RowItem>();
			String distance = "";
			for (int i = 0; i < it.size(); i++) {
				int distanceMeter = Math.round(Float.parseFloat(it.get(i)
						.toString().split(";")[0]) * 100) / 100;
				if (distanceMeter < 1000)
					distance = String.valueOf(distanceMeter) + " m";
				else {
					float distanceKilometer = Float.valueOf(distanceMeter);
					distanceKilometer = distanceKilometer / 1000;
					distance = String.valueOf(distanceKilometer);
					distance = distance.replace(".", ",");
					if (distance.split(",")[1].length() >= 3)
						distance = distance.split(",")[0] + ","
								+ distance.split(",")[1].substring(0, 2)
								+ " km";
					else {
						distance = distance + " km";
					}

				}

				String image = "ico_" + it.get(i).toString().split(";")[1];
				String name = it.get(i).toString().split(";")[3];
				RowItem item = new RowItem(PlacesActivity.this.getResources()
						.getIdentifier("drawable/" + image, null,
								PlacesActivity.this.getPackageName()), name, distance);
				rowItems.add(item);
			}
			listView = (ListView) findViewById(R.id.list);
			CustomListViewAdapter adapter = new CustomListViewAdapter(
					PlacesActivity.this, R.layout.list_item, rowItems);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(PlacesActivity.this);
			dialog.dismiss();
		}

	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefix = prefs.getString("prefix", "");
		if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
			setContentView(R.layout.activity_places);
			AsyncMaps aMaps = new AsyncMaps();
			aMaps.execute();
			ActionBar bar = getActionBar();
			bar.setDisplayHomeAsUpEnabled(true);
			Intent i = new Intent(PlacesActivity.this, PlacesActivity.class);
			i.putExtra("prefix", prefix);
		} else {
			Toast toast = Toast.makeText(getApplicationContext(), getString(getResources().getIdentifier(
					"no_google_play_services" + prefix, "string", getPackageName())),
					Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			onBackPressed();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent i = new Intent(PlacesActivity.this, InfoActivity.class);
		i.putExtra("title", rowItems.get(position).getTitle());
		startActivity(i);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(this, StartActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
