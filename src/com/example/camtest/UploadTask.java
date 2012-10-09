package com.example.camtest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

public class UploadTask extends AsyncTask<Uri, Void, String>{
	private MainActivity ctx;
	
	public UploadTask(Context ctx){
		this.ctx = (MainActivity)ctx;
	}
	
	@Override
	protected String doInBackground(Uri... params){
		Uri media = params[0];
		String result = Uploader.upload(media);
		
		return result;
	}
	
	@Override
	protected void onPostExecute(String result){
		if(result == null){
			Toast.makeText(ctx, "Upload failed.", Toast.LENGTH_SHORT).show();
		}else{
			ctx.onUploadCompleted(result);
			updateNotification();
		}
	}
	
	
	
	private void updateNotification(){
		NotificationManager notificationManager = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		
		int icon = R.drawable.ic_launcher;
		CharSequence tickerText = "Uploading photo...";
		long when = System.currentTimeMillis();
		
		Notification notification = new Notification(icon, tickerText, when);
		
		String contentTitle = "CamTest";
		String contentText = "Upload completed.";
		Intent notificationIntent = new Intent(ctx, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, notificationIntent, 0);
		
		notification.defaults = Notification.DEFAULT_SOUND;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.setLatestEventInfo(ctx, contentTitle, contentText, contentIntent);
		
		notificationManager.notify(MainActivity.NOT_UPLOAD, notification);
	}
	
}
