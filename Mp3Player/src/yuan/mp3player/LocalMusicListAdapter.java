package yuan.mp3player;

import java.util.List;

import yuan.model.Mp3Info;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LocalMusicListAdapter extends BaseAdapter{
	private List<Mp3Info> mp3Infos = null;
	private LayoutInflater inflater = null;
	private TextView songName = null;
	private TextView singerName = null;
	private ImageView listMeunImg = null;
	
	public LocalMusicListAdapter(Context context, List<Mp3Info> mp3Infos) {
		this.mp3Infos = mp3Infos;
		this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {		
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

	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null) {
			convertView = inflater.inflate(R.layout.local_home_song_list_item, null);  
		}
		initComponent(convertView);	
		setComponent(position);
		return convertView;
	}
	
	private void initComponent(View convertView) {
		songName = (TextView)convertView.findViewById(R.id.list_song_name);
		singerName = (TextView)convertView.findViewById(R.id.list_singer_name);
		listMeunImg = (ImageView)convertView.findViewById(R.id.list_item_meun);
	}
	
	private void setComponent(int position) {
		songName.setText(mp3Infos.get(position).getMp3SimpleName());
		singerName.setText(mp3Infos.get(position).getSingerName());
		listMeunImg.setImageResource(R.drawable.list_item_menu_normal);
	}
}
