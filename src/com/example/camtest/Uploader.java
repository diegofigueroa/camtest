package com.example.camtest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.util.Xml;

public class Uploader{
	private static final String API_KEY = "2200c0e30a467c77103e18ea2da9450b";
	
	private static final String UPLOAD_PATH = "http://api.imgur.com/2/upload";
	
	public static String upload(Uri uri){
		String result = null;
		HttpClient httpClient = new DefaultHttpClient();
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
	    HttpPost httpPost = new HttpPost(UPLOAD_PATH);
	    
	    ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
	    params.add(new BasicNameValuePair("key", API_KEY));
	    params.add(new BasicNameValuePair("type", "base64"));
	    
	    try{
	    	
	    	ByteArrayOutputStream bao = new ByteArrayOutputStream();
		    
		    Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath());
	    	bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
	    	
	    	byte[] ba = bao.toByteArray();
	    	String encodedData = Base64.encodeToString(ba, Base64.DEFAULT);
	    	
	    	params.add(new BasicNameValuePair("image", encodedData));
	    	httpPost.setEntity(new UrlEncodedFormEntity(params));
	        
	    	String response = (String)httpClient.execute(httpPost, responseHandler);
	        Log.d("Uploader", response);
	        
	        result = parse(response);
	        Log.d("Uploader", "mediaLink=" + result);
	        
	    }catch(IOException e){
	        e.printStackTrace();
	    }
		
		return result;
	}
	
	private static String parse(String xml){
		String response = null;
		
		XmlHandler handler = new XmlHandler();
        try{
			Xml.parse(xml, handler);
			while(!handler.finished());
			
			response = handler.getLink();
			
		}catch(SAXException e){
			e.printStackTrace();
		}
        
        return response;
	}
	
	
	private static class XmlHandler extends DefaultHandler{
	    private StringBuilder content;
	    private String link = null;
	    private boolean completed = false;
	    
	    public XmlHandler(){
	        content = new StringBuilder();
	    }
	    
	    public boolean finished(){
	    	return completed;
	    }
	    
	    public String getLink(){
	    	return link;
	    }
	    
	    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException{
	        content = new StringBuilder();
	    }
	    
	    public void endElement(String uri, String localName, String qName)throws SAXException {
	        if(localName.equalsIgnoreCase("original")){
	           link = content.toString();
	        }
	    }
	    
	    public void characters(char[] ch, int start, int length)throws SAXException{
	        content.append(ch, start, length);
	    }
	    
	    public void endDocument() throws SAXException{
	    	completed = true;
	    }
	    
	}
}
