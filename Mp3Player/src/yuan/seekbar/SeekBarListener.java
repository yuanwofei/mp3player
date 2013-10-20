package yuan.seekbar;

import yuan.constant.MusicPlayer;
import android.media.MediaPlayer;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
	
/**
 * 歌曲进度条监听器
 * @author Administrator
 */
public class SeekBarListener implements OnSeekBarChangeListener {	

	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		
	}

	public void onStartTrackingTouch(SeekBar seekBar) {
		
	}
	
	/**单击进度条后，设置player的播放进度到进度条所在的位置*/
	public void onStopTrackingTouch(SeekBar seekBar) {		
		MediaPlayer player = MusicPlayer.getPlayer();
		if(player != null) {
			int currentProgressValue = seekBar.getProgress();
			int songDuration = player.getDuration();		
			int seekBarMaxValue = seekBar.getMax();
			player.seekTo(songDuration * currentProgressValue / seekBarMaxValue);	
		} else 
			seekBar.setProgress(0);
	}	
}

