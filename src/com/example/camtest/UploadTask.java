package com.example.camtest;

import android.content.Context;
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
		}
	}
	
}
