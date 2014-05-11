package com.ukrcastles;

import java.io.IOException;
import java.util.ArrayList;

import org.w3c.dom.Document;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Toast;

@SuppressLint("NewApi")
public class RoutActivity extends Activity {

	DataBaseHelper myDbHelper;
	LatLng myCoord = null;
	LatLng myNearCoord = null;
	SQLiteDatabase db;
	GPSTracker gpsTracker;
	GoogleMap map;
	private Handler mHandler = new Handler();
	private Handler spHandler = new Handler();
	String title;
	Marker marker;
	String prefix;

	private class AsyncMaps extends AsyncTask<String, Void, Integer> {
		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			spHandler.post(new Runnable() {
				@SuppressLint("NewApi")
				@SuppressWarnings({ "unchecked", "rawtypes" })
				public void run() {
					dialog = new ProgressDialog(RoutActivity.this);
					dialog.setTitle(getResources().getIdentifier(
							"dialog_title_string" + prefix, "string",
							getPackageName()));
					dialog.setMessage(getString(getResources().getIdentifier(
							"load_string" + prefix, "string", getPackageName())));
					dialog.setIndeterminate(true);
					dialog.setCancelable(false);
					dialog.show();
				}
			});
		}

		@Override
		protected Integer doInBackground(String... params) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Intent intent = getIntent();
			title = intent.getStringExtra("title");
			myDbHelper = new DataBaseHelper(RoutActivity.this);
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
				public void run() {
					// check if GPS enabled
					// Get a handle to the Map Fragment
					String coordinates = "";
					if (map == null) {
						map = ((MapFragment) getFragmentManager()
								.findFragmentById(R.id.map)).getMap();
						map.setMyLocationEnabled(true);
					}
					gpsTracker = new GPSTracker(RoutActivity.this);
					if (gpsTracker.canGetLocation()) {
						String stringLatitude = String
								.valueOf(gpsTracker.latitude);
						String stringLongitude = String
								.valueOf(gpsTracker.longitude);
						if (stringLatitude.equals("0.0")) {
							stringLatitude = "49.853192";
							stringLongitude = "24.024499";
						}
						String country = gpsTracker
								.getCountryName(RoutActivity.this);
						String city = gpsTracker.getLocality(RoutActivity.this);
						String addressLine = gpsTracker
								.getAddressLine(RoutActivity.this);
						map.clear();
						myCoord = new LatLng(
								Double.parseDouble(stringLatitude), Double
										.parseDouble(stringLongitude));
						map.addMarker(new MarkerOptions()
								.title(getResources().getString(
										R.string.you_here_string))
								.snippet(
										country + ", " + city + "\n"
												+ addressLine + "\n"
												+ stringLatitude + ", "
												+ stringLongitude)
								.position(myCoord));

						map.moveCamera(CameraUpdateFactory.newLatLngZoom(
								myCoord, 8));

					}
					db = myDbHelper.getWritableDatabase();
					Cursor c = db.query("info_data", new String[] { "*" },
							"name" + prefix + " LIKE \"" + title + "%\"", null,
							null, null, null);
					for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

						coordinates = c.getString(c
								.getColumnIndex("coordinates"));
					}

					myNearCoord = new LatLng(Double.parseDouble(coordinates
							.split(",")[0]), Double.parseDouble(coordinates
							.split(",")[1]));

					if (isNetworkAvailable()) {

						// Walking
						GMapV2Direction mdDist = new GMapV2Direction();
						Document doc_dist = mdDist.getDocument(myCoord,
								myNearCoord, "driving");
						ArrayList<LatLng> directionPointDist = mdDist
								.getDirection(doc_dist);
						PolylineOptions rectLineDist = new PolylineOptions()
								.width(6).color(Color.GREEN);

						for (int i = 0; i < directionPointDist.size(); i++) {
							rectLineDist.add(directionPointDist.get(i));
						}

						map.addPolyline(rectLineDist);

					} else {
						Toast toast = Toast.makeText(
								getApplicationContext(),
								getString(getResources().getIdentifier(
										"no_inet_string" + prefix, "string",
										getPackageName())), Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					}

				}
			});
			return null;
		}

		@Override
		protected void onPostExecute(Integer i) {
			map.addMarker(new MarkerOptions()
					.title(title)
					.anchor(0.0f, 1.0f)
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.finish))
					.position(myNearCoord));
			db.close();
			dialog.dismiss();
			prefix = getIntent().getStringExtra("prefix");
			// map.setOnInfoWindowClickListener(new OnInfoWindowClickListener()
			// {
			// @Override
			// public void onInfoWindowClick(Marker marker) {
			// String title = marker.getTitle();
			// if (title.equals(getResources().getString(
			// R.string.add_place_string))) {
			// Intent intent = new Intent(RoutActivity.this,
			// AddActivity.class);
			// intent.putExtra(
			// "position",
			// marker.getPosition().latitude + ","
			// + marker.getPosition().longitude);
			// intent.putExtra("prefix", prefix);
			// startActivity(intent);
			// } else {
			// if (title.equals(getResources().getString(
			// R.string.you_here_string))) {
			// } else {
			// Intent intent = new Intent(RoutActivity.this,
			// InfoActivity.class);
			// intent.putExtra("title", title);
			// intent.putExtra("prefix", prefix);
			// startActivity(intent);
			// }
			// }
			// }
			// });

			map.setOnMarkerClickListener(new OnMarkerClickListener() {

				@Override
				public boolean onMarkerClick(Marker marker) {
					String title = marker.getTitle();
					if (title.equals(getResources().getString(
							R.string.add_place_string))) {
						Intent intent = new Intent(RoutActivity.this,
								AddActivity.class);
						intent.putExtra(
								"position",
								marker.getPosition().latitude + ","
										+ marker.getPosition().longitude);
						intent.putExtra("prefix", prefix);
						startActivity(intent);
					} else {
						if (title.equals(getResources().getString(
								R.string.you_here_string))) {
						} else {
							Intent intent = new Intent(RoutActivity.this,
									InfoActivity.class);
							intent.putExtra("title", title);
							intent.putExtra("prefix", prefix);
							startActivity(intent);
						}
					}
					return true;
				}
			});

			/*
			 * map.setOnMapClickListener(new OnMapClickListener() {
			 * 
			 * @Override public void onMapClick(LatLng point) { if (marker !=
			 * null) marker.remove(); marker = map.addMarker(new MarkerOptions()
			 * .title(getResources().getString(
			 * R.string.add_place_string)).position(point) .draggable(true)); }
			 * });
			 */
		}
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefix = getIntent().getStringExtra("prefix");
		if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
			setContentView(R.layout.activity_rout);
			AsyncMaps maps = new AsyncMaps();
			maps.execute();
			ActionBar bar = getActionBar();
			bar.setDisplayHomeAsUpEnabled(true);

			Intent i = new Intent(RoutActivity.this, RoutActivity.class);
			i.putExtra("prefix", prefix);
		} else {
			Toast toast = Toast.makeText(
					getApplicationContext(),
					getString(getResources().getIdentifier(
							"no_google_play_services" + prefix, "string",
							getPackageName())), Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			onBackPressed();
		}
	}

	public boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(this, StartActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("prefix", prefix);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
