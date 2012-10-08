package com.example.camtest;

import java.util.Arrays;
import java.util.LinkedList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListAdapter extends BaseAdapter{
	private Context ctx;
	LinkedList<String> items;
	
	public ListAdapter(Context ctx){
		this.ctx = ctx;
		items = new LinkedList<String>();
	}
	
	public ListAdapter(Context ctx, String[] list){
		this.ctx = ctx;
		items = new LinkedList<String>(Arrays.asList(list));
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		if(convertView == null)
			convertView = (View)LayoutInflater.from(ctx).inflate(android.R.layout.simple_list_item_1, parent, false);
		
		TextView v = (TextView)convertView.findViewById(android.R.id.text1);
		v.setText(items.get(position));
		return convertView;
	}
	
	@Override
	public long getItemId(int position){
		return position;
	}
	
	@Override
	public String getItem(int position){
		return items.get(position);
	}
	
	public void append(String s){
		items.add(s);
		notifyDataSetChanged();
	}
	
	public String[] toArray(){
		String[] _items = new String[items.size()];
		int i = 0;
		for(String s : items){
			_items[i++] = s;
		}
		return _items;
	}
	
	
	@Override
	public int getCount(){
		return items.size();
	}
}