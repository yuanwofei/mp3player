package com.music.mp3player;

import java.util.List;

import com.music.factory.model.Mp3Info;
import com.music.mp3player.service.DownloadService;

import com.music.R;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SearchListAdapter extends BaseAdapter{
	/**试听标志*/
	private final static int PRE_LISTENER_LAYOUT = 1;
	/**下载标志*/
	private final static int DOWN_MP3_LAYOUT = 2;
	/**收藏标志*/
	private final static int COLLECTION_LAYOUT = 3;
	
	private List<Mp3Info> mp3Infos = null;
	private LayoutInflater inflater = null;
	private Context context = null;
	
	public SearchListAdapter(Context context, List<Mp3Info> mp3Infos, View selectView) {
		this.mp3Infos = mp3Infos;
		this.context = context;
		this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);        
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return mp3Infos.size();  
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mp3Infos.get(position);  
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public void addItem(Mp3Info mp3Info){
		mp3Infos.add(mp3Info);
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if (convertView == null) {  
			convertView = inflater.inflate(R.layout.search_result_item, null); 
		} 
		
		LinearLayout listItemView = (LinearLayout)convertView.findViewById(R.id.search_result_item);
		
		LinearLayout selectView = (LinearLayout)inflater.inflate(R.layout.search_result_item_operate, null); 				
		
		TextView preListen = (TextView)selectView.findViewById(R.id.pre_listen);
		TextView download = (TextView)selectView.findViewById(R.id.download);
		TextView favourse = (TextView)selectView.findViewById(R.id.favourse);
		
		preListen.setOnClickListener(new SelectViewListener(PRE_LISTENER_LAYOUT, 
				position, listItemView, selectView));
		download.setOnClickListener(new SelectViewListener(DOWN_MP3_LAYOUT, 
				position, listItemView, selectView));
		favourse.setOnClickListener(new SelectViewListener(COLLECTION_LAYOUT, 
				position, listItemView, selectView));
		
		TextView songName = (TextView)convertView.findViewById(R.id.mp3_name); 
		TextView singerAlbumName = (TextView)convertView.findViewById(R.id.album_singer_name);
		ImageButton menuBtn = (ImageButton) convertView.findViewById(R.id.search_list_menu_btn);	
		songName.setText(mp3Infos.get(position).getMp3SimpleName());
		singerAlbumName.setText(mp3Infos.get(position).getSingerName() + " - " + 
				mp3Infos.get(position).getAlbumName());		
		menuBtn.setFocusable(false);//这一句非常重要，有它才可以使列表项获得焦点						
		menuBtn.setOnClickListener(new AddRemoveViewListener(listItemView, selectView));

		return convertView;	
	}
	
	/**
	 * 
	 * @author Administrator
	 */
	private class AddRemoveViewListener implements OnClickListener {	
		LinearLayout listItemView = null;		
		LinearLayout selectView = null;		
		
		public AddRemoveViewListener(LinearLayout listItemView, LinearLayout selectView) {
			this.listItemView = listItemView;
			this.selectView = selectView;
			
		}
		
		public void onClick(View v) {
			if(selectView.isShown()){
				removeSelectView(listItemView, selectView);
			} else {			
				addSelectView(listItemView, selectView);
			}							
		}		
	}
	
	/**
	 * 添加SelectView
	 * @param listItemView
	 * @param selectView
	 */
	private void addSelectView(LinearLayout listItemView, LinearLayout selectView){
		listItemView.addView(selectView);
	}
	
	/**
	 * 移除SelectView
	 * @param listItemView
	 * @param addSelectView
	 */
	private void removeSelectView(LinearLayout listItemView, LinearLayout selectView) {
		listItemView.removeView(selectView);
	}
	
	private class SelectViewListener implements OnClickListener {
		int flag = 0;
		int position = 0;
		LinearLayout listItemView = null;		
		LinearLayout selectView = null;
		public SelectViewListener(int flag, int position, LinearLayout listItemView, LinearLayout selectView) {
			this.flag = flag;
			this.position = position;
			this.listItemView = listItemView;
			this.selectView = selectView;
		}

		public void onClick(View v) {
			switch(flag) {
				case PRE_LISTENER_LAYOUT : 
					startListeningService();
					removeSelectView(listItemView, selectView);
					break;
				case DOWN_MP3_LAYOUT : 
					removeSelectView(listItemView, selectView); 
					startDownloadService(position); 
					break;
				case COLLECTION_LAYOUT :
					removeSelectView(listItemView, selectView); ; break;
			}
		}		
	}
	
	/**启动下载Service*/
	private void startDownloadService(int position){
		Intent intent = new Intent();
		intent.putExtra("mp3Info", mp3Infos.get(position));
		intent.setClass(context, DownloadService.class);
		context.startService(intent);
	}
	
	/**启动试听Service*/
	private void startListeningService() {
		
	}
}
