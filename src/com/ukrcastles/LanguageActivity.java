package com.ukrcastles;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LanguageActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_language);
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
