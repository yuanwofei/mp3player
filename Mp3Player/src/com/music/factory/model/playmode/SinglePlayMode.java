package com.music.factory.model.playmode;

import com.music.factory.PlayModeFactory;

public class SinglePlayMode extends AbstractPlayMode {

	private SinglePlayMode() {
	}

	@Override
	public int preSongIndex(int index, int size) {
		return index;
	}

	@Override
	public int nextSongIndex(int index, int size) {
		return index;
	}
	
	public static PlayModeFactory factory = new PlayModeFactory() {		
		@Override
		public AbstractPlayMode createPlayMode() {
			return new SinglePlayMode();
		}
	};
}
