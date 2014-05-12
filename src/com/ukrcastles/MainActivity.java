package com.ukrcastles;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class MainActivity extends Activity {

	private Handler mHandler = new Handler();

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AsyncStart start = new AsyncStart();
		start.execute();
	}

	public void langUa(View v) {
		Intent i = new Intent(this, StartActivity.class);
		i.putExtra("prefix", "");
		startActivity(i);
	}

	public void langPl(View v) {
		Intent i = new Intent(this, StartActivity.class);
		i.putExtra("prefix", "_pl");
		startActivity(i);
	}

	public void langEn(View v) {
		Intent i = new Intent(this, StartActivity.class);
		i.putExtra("prefix", "_en");
		startActivity(i);
	}
}
