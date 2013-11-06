package com.music.factory.model.playmode;

import com.music.factory.PlayModeFactory;

public class SinglePlayMode extends AbstractPlayMode {

	private SinglePlayMode() {
	}

	@Override
	public int prevIndex(int index, int size) {
		return index;
	}

	@Override
	public int nextIndex(int index, int size) {
		return index;
	}
	
	public static PlayModeFactory factory = new PlayModeFactory() {		
		@Override
		public AbstractPlayMode createPlayMode() {
			return new SinglePlayMode();
		}
	};
}
