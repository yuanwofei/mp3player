package com.music.mp3player;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.music.constant.Music;
import com.music.constant.MusicPlayer;
import com.music.factory.model.CopyMp3Infos;
import com.music.factory.model.Mp3Info;
import com.music.mp3player.service.PlayService;
import com.music.utils.FileUtils;

public class LocalMp3List{
	private ArrayList<Mp3Info> mp3Infos = null;
	private Context context = null;
	private ListView listView = null;
	public LocalMp3List(Context context, ListView listView) {
		this.context = context;
		this.listView = listView;
		this.listView.setOnItemClickListener(new AllMusicListListener());		
		updateLocalListView();
		CopyMp3Infos.setMP3INFOS(mp3Infos);
	}
			
	/**
	 * 获取本地SD卡中mp3列表信息
	 */
	private void updateLocalListView() {
		mp3Infos = FileUtils.getMediaStoreMp3Infos(context);
		CopyMp3Infos.setMP3INFOS(mp3Infos);
		if(mp3Infos == null) {
			Toast.makeText(context, "找不到歌曲！", Toast.LENGTH_LONG).show();
			return;
		}
		LocalMusicListAdapter adapter = new LocalMusicListAdapter(context ,mp3Infos);		
		listView.setAdapter(adapter);
	}
		
	private class AllMusicListListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if(mp3Infos != null) {		
				CopyMp3Infos.setMP3INFOS(mp3Infos);
				MusicPlayer.isFirstPlaying =true;
				MainActivity.index = position;
				Mp3Info mp3Info = mp3Infos.get(position);
				Intent intent = new Intent();				
				intent.putExtra("MSG", Music.PlayState.PLAY);
				intent.putExtra("mp3Info", mp3Info);		
				intent.putExtra("index", position);
				if (MainActivity.isStartedPlayService()) {				
					intent.setAction(Music.PLAY_MUSIC_ACTION);
					context.sendBroadcast(intent);
				} else {
					MainActivity.setStartedPlayService(true);
					intent.setClass(context, PlayService.class);
					context.startService(intent);					
				}				
			}			
		}		
	}
}
