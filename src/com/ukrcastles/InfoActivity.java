package com.ukrcastles;

import java.io.IOException;

import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;

@SuppressLint("NewApi")
public class InfoActivity extends Activity implements MediaPlayerControl {

	TextView textTitle;
	TextView textDescription;
	ImageView imageView;
	SQLiteDatabase db;
	private MediaController mMediaController;
	private MediaPlayer mMediaPlayer;
	ImageButton buttonPlay;
	ImageButton buttonStop;
	String prefix;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		prefix = getIntent().getStringExtra("prefix");
		ActionBar bar = getActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		Intent intent = getIntent();
		String title = intent.getStringExtra("title");
		DataBaseHelper myDbHelper = new DataBaseHelper(InfoActivity.this);
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
		db = myDbHelper.getWritableDatabase();
		String image = "";
		String description = "";
		String audioFile = "";
		Cursor c = db.query("info_data", new String[] { "*" }, "name LIKE '"
				+ title + "%'", null, null, null, null);
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			image = c.getString(c.getColumnIndex("image"));
			description = c.getString(c.getColumnIndex("description"));
			audioFile = c.getString(c.getColumnIndex("audio")) + ".mp3";
		}
		if (audioFile.contains("null"))
			audioFile = "audio.mp3";
		imageView = (ImageView) findViewById(R.id.imageView1);
		textTitle = (TextView) findViewById(R.id.textView1);
		//buttonPlay = (ImageButton) findViewById(R.id.button1);
		//buttonStop = (ImageButton) findViewById(R.id.button2);
		//buttonPlay.setBackgroundResource(R.drawable.play);
		//buttonStop.setBackgroundResource(R.drawable.pause);
		textDescription = (TextView) findViewById(R.id.textView2);
		imageView.setImageResource(this.getResources().getIdentifier(
				"drawable/" + image, null, this.getPackageName()));
		textTitle.setText(title);
		textDescription.setText(description);
		mMediaPlayer = new MediaPlayer();
		mMediaController = new MediaController(this);
		mMediaController.setMediaPlayer(InfoActivity.this);
		/*mMediaController.setAnchorView(findViewById(R.id.mediaController1));
		AssetFileDescriptor afd = null;
		try {
			afd = getAssets().openFd(audioFile);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			mMediaPlayer.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
			mMediaPlayer.prepare();
			mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					mHandler.post(new Runnable() {
						public void run() {
							//mMediaController.show(0);
							mMediaPlayer.start();
						}
					});
				}
			});
		} catch (IOException e) {
			Log.e("PlayAudioDemo", "Could not open file " + audioFile
					+ " for playback.", e);
		}*/
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMediaPlayer.stop();
		mMediaPlayer.release();
	}

	@Override
	public boolean canPause() {
		return true;
	}

	@Override
	public boolean canSeekBackward() {
		return false;
	}

	@Override
	public boolean canSeekForward() {
		return false;
	}

	@Override
	public int getBufferPercentage() {
		int percentage = (mMediaPlayer.getCurrentPosition() * 100)
				/ mMediaPlayer.getDuration();

		return percentage;
	}

	@Override
	public int getCurrentPosition() {
		return mMediaPlayer.getCurrentPosition();
	}

	@Override
	public int getDuration() {
		return mMediaPlayer.getDuration();
	}

	@Override
	public boolean isPlaying() {
		return mMediaPlayer.isPlaying();
	}

	@Override
	public void pause() {
		if (mMediaPlayer.isPlaying())
			mMediaPlayer.pause();
	}

	@Override
	public void seekTo(int pos) {
		mMediaPlayer.seekTo(pos);
	}

	@Override
	public void start() {
		mMediaPlayer.start();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mMediaController.show();

		return false;
	}

	@Override
	public int getAudioSessionId() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void playClick(View v) {
		start();
	}
	
	public void pauseClick(View v) {
		if(isPlaying())
			pause();
	}
	
	public void goToClick(View v) {
		Intent i = new Intent(InfoActivity.this, RoutActivity.class);
		i.putExtra("title", textTitle.getText());
		i.putExtra("prefix", prefix);
		startActivity(i);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{    
	   switch (item.getItemId()) 
	   {        
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
