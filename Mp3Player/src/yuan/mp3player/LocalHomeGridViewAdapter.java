package yuan.mp3player;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LocalHomeGridViewAdapter extends BaseAdapter{

	private LayoutInflater inflater = null;
	private ImageView imageView = null;
	private TextView title = null;
	private TextView titleState = null;
	private int[] imageId = null;
	private int[] titleId = null;
	private int[] titleStateId = null;
	
	public LocalHomeGridViewAdapter(Context context, int[] imageId,
			int[] titleId, int[] titleStateId) {
		this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);   
		this.imageId = imageId;
		this.titleId = titleId;
		this.titleStateId = titleStateId;
	}
	public int getCount() {
		// TODO Auto-generated method stub
		return imageId.length;
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return imageId[position];
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null) {
			convertView = inflater.inflate(R.layout.local_home_grid_item, null);  
		}
		initComponent(convertView);	
		setComponent(position);
		return convertView;
	}
	
	private void initComponent(View convertView) {
		imageView = (ImageView)convertView.findViewById(R.id.local_home_image_item);
		title = (TextView)convertView.findViewById(R.id.local_home_title);
		titleState = (TextView)convertView.findViewById(R.id.local_home_title_state);
	}
	
	private void setComponent(int position) {
		imageView.setImageResource(imageId[position]);
		title.setText(titleId[position]);
		titleState.setText(titleStateId[position]);
	}
}
