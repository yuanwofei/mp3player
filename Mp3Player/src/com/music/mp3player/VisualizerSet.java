package com.music.mp3player;

import com.music.lyric.VisualizerView;

import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.media.audiofx.Visualizer.OnDataCaptureListener;

public class VisualizerSet {
	private Visualizer visualizer = null;
	private MediaPlayer player = null;
	private VisualizerView visualizerView = null;
	
	public VisualizerSet(MediaPlayer player, VisualizerView visualizerView) {
		this.player = player;
		this.visualizerView = visualizerView;
	}

	private void init() {			
		visualizer = new Visualizer(player.getAudioSessionId());
		visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
		visualizer.setDataCaptureListener(new Visualizerlistener(),
				Visualizer.getMaxCaptureRate() / 2, true, false);
	}
	
	private class Visualizerlistener implements OnDataCaptureListener{	
		public void onFftDataCapture(Visualizer visualizer, byte[] fft,
				int samplingRate) {			
		}
		
		public void onWaveFormDataCapture(Visualizer visualizer,
				byte[] waveform, int samplingRate) {
			visualizerView.refreshVisualizer(waveform);
		}		
	}
}
