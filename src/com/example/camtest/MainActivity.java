package com.example.camtest;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemClickListener{
	
	private final static boolean PHOTO_MODE = true;
	private final static boolean VIDEO_MODE = false;
	
	private static final int MEDIA_TYPE_IMAGE = 0;
    private static final int MEDIA_TYPE_VIDEO = 1;
    
    private static final int CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAPTURE_VIDEO_REQUEST_CODE = 200;
    
    public static final int NOT_UPLOAD = 1;
    
    private static final String TAG = "MainActivity";
    
    private ListAdapter adapter;
    private Uri mediaUri = null;
    
    private Button.OnClickListener photoClickListener = 
		new Button.OnClickListener(){
		@Override
		public void onClick(View v){
			mediaUri = startCamera(PHOTO_MODE);
		}
	};
	
	private Button.OnClickListener videoClickListener = 
		new Button.OnClickListener(){
		@Override
		public void onClick(View v){
			mediaUri = startCamera(VIDEO_MODE);
		}
	};
	
	private Button.OnClickListener galleryClickListener = 
		new Button.OnClickListener(){
		@Override
		public void onClick(View v){
			
		}
	};
    
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ImageButton btn = (ImageButton)findViewById(R.id.btn_camera);
        btn.setOnClickListener(photoClickListener);
        
        btn = (ImageButton)findViewById(R.id.btn_video);
        btn.setOnClickListener(videoClickListener);
        
        btn = (ImageButton)findViewById(R.id.btn_gallery);
        btn.setOnClickListener(galleryClickListener);
        
        if(savedInstanceState != null){
        	String uri = savedInstanceState.getString("uri");
        	if(uri != null){
        		mediaUri = Uri.parse(uri);
        		ImageView v = (ImageView)findViewById(R.id.preview);
        		v.setImageBitmap(getPreview(mediaUri));
        		
        	}
        	
        	String[] list = savedInstanceState.getStringArray("list");
        	if(list != null)
        		adapter = new ListAdapter(this, list);
        	else
        		adapter = new ListAdapter(this);
        }else{
        	adapter = new ListAdapter(this);
        }
        
        ListView list = (ListView)findViewById(R.id.list);
        
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
	@Override
	protected void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		if(mediaUri != null){
			outState.putString("uri", mediaUri.getPath());
			outState.putStringArray("list", adapter.toArray());
		}
	}
	
	
	
	
	// Receives camera result
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	if(requestCode == CAPTURE_IMAGE_REQUEST_CODE){
	        if(resultCode == Activity.RESULT_OK){
	        	/*	PASO 2.3: <-------------     CORTE AQUI
	        	ykro */
	        	if(mediaUri != null){
	        		Toast.makeText(this, "Picture taken.", Toast.LENGTH_LONG).show();
	        		upload(mediaUri);
	        		
	        		ImageView imageView = (ImageView)findViewById(R.id.preview);
	        		imageView.setImageBitmap(getPreview(mediaUri));
	        	}
	        	/* ykro
	        	PASO 2.3: ------------->     CORTE AQUI */
	        }else if(resultCode == Activity.RESULT_CANCELED){
	           // User cancelled the image capture
	        }else{
	            // Image capture failed
	        }
	    }
	}
    
	// Sends camera intent
	private Uri startCamera(boolean forPhoto){
    	Uri uri = null;
    	if(forPhoto){
    		
    		/*	PASO 2.1: <-------------     CORTE AQUI
    		 ykro */ 
    		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    		
    		uri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
    		if(uri != null){
    			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
    		}else{
    			Toast.makeText(this, "External Storage not available", Toast.LENGTH_SHORT).show();
    		}
    		
    		startActivityForResult(intent, CAPTURE_IMAGE_REQUEST_CODE);
    		/* ykro
    		PASO 2.1: ------------->     CORTE AQUI */
    		
    	}else{
    		
    		/*	PASO 2.2: <-------------     CORTE AQUI
    		ykro */
    		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
    		
    		uri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
    		if(uri != null){
    			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
    		}else{
    			Toast.makeText(this, "External Storage not available", Toast.LENGTH_SHORT).show();
    		}
    		
    		startActivityForResult(intent, CAPTURE_VIDEO_REQUEST_CODE);
    		/* ykro
    		PASO 2.2: ------------->     CORTE AQUI */
    	}
    	
    	return uri;
    }
	
	// Upload media in background
	private void upload(Uri media){
		/* PASO 2.4: <-------------     CORTE AQUI 
		ykro */
		UploadTask uploader = new UploadTask(this);
		uploader.execute(media);
		showNotification();
		/* ykro
		PASO 2.4: ------------->     CORTE AQUI */
	}
	
	//Notifies user
	private void showNotification(){
		/*	PASO 4.1: ------------->     CORTE AQUI 
		ykro */
		NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		
		int icon = R.drawable.upload;
		CharSequence tickerText = "Uploading photo...";
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);
		
		Intent notificationIntent = new Intent(this, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		
		//notification.defaults = Notification.DEFAULT_SOUND;
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		
		// PASO 4.2.1
		
		String contentTitle = "CamTest";
		String contentText = "Uploading your photo, please wait...";
		notification.setLatestEventInfo(this, contentTitle, contentText, contentIntent);
		notificationManager.notify(NOT_UPLOAD, notification);
		
		// PASO 4.2.1
		
		/* ykro
		PASO 4.1: ------------->     CORTE AQUI	*/
		
		
		
		/*	PASO 4.2.2: ------------->     CORTE AQUI
		ykro */
		RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification);
		contentView.setImageViewResource(R.id.notification_icon, R.drawable.ic_launcher);
		contentView.setTextViewText(R.id.notification_text, "Uploading photo...");
		notification.contentView = contentView;
		notification.contentIntent = contentIntent;
		
		notificationManager.notify(NOT_UPLOAD, notification);
		/* ykro
		PASO 4.2.2: ------------->     CORTE AQUI */
	}
	
	
	
	
	
	// Build photo preview
	private Bitmap getPreview(Uri uri){
	    BitmapFactory.Options bounds = new BitmapFactory.Options();
	    bounds.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(uri.getPath(), bounds);
	    if ((bounds.outWidth == -1) || (bounds.outHeight == -1))
	        return null;

	    int originalSize = (bounds.outHeight > bounds.outWidth) ? bounds.outHeight : bounds.outWidth;

	    BitmapFactory.Options opts = new BitmapFactory.Options();
	    opts.inSampleSize = originalSize / 200;
	    return BitmapFactory.decodeFile(uri.getPath(), opts);     
	}
	
	
	//Where to save media files
	
	private Uri getOutputMediaFileUri(int type){
    	Uri result = null;
    	File f = getOutputMediaFile(type);
    	if(f != null)
    		result = Uri.fromFile(f);
    	
    	return result;
    }
    
    private File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted using Environment.getExternalStorageState() before doing this.
    	// This location works best if you want the created images to be shared between applications and persist after your app has been uninstalled.
    	
    	
    	File rootsd = Environment.getExternalStorageDirectory();
    	File dcim = new File(rootsd.getAbsolutePath() + "/DCIM");
        File mediaStorageDir = new File(dcim, getString(R.string.app_name));
        
        if(!mediaStorageDir.exists()){	// Create the storage directory if it does not exist
            if(!mediaStorageDir.mkdirs()){
                Log.d(TAG, "Failed to create directory");
                return null;
            }
        }
        
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if(type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");
        }else if(type == MEDIA_TYPE_VIDEO){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_"+ timeStamp + ".mp4");
        }else{
            return null;
        }
        
        if(mediaFile != null){
        	addToMediaScanner(mediaFile);
        }
        
        return mediaFile;
    }
    
    
    //Notify Media Scanner about my new files
    private void addToMediaScanner(File mediaFile){
    	// Tell the media scanner about the new file so that it is immediately available to the user.
    	/*
        MediaScannerConnection.scanFile(this, new String[] {mediaFile.toString()}, null, new MediaScannerConnection.OnScanCompletedListener(){
            public void onScanCompleted(String path, Uri uri){
                Log.d(TAG, "Scanned " + path + ":");
                Log.d(TAG, "-> uri=" + uri);
            }
        });
        */
    	sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()))); 
    	Log.d(TAG, mediaFile.getAbsolutePath());
    }
    
    
    // Add to log list
    public void onUploadCompleted(String link){
    	adapter.append(link);
    }
    
    
    // Open web browser to check uploaded photo
	@Override
	public void onItemClick(AdapterView<?> list, View view, int position, long id){
		String url = ((TextView)view).getText().toString();
		
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		startActivity(i);
	}
}
