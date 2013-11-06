package com.music.factory.model.playmode;

import com.music.factory.PlayModeFactory;

public class CyclePlayMode extends AbstractPlayMode {


	@Override
	public int prevIndex(int index, int size) {
		return (index - 1 >= 0) ? index - 1 : size - 1;
	}

	@Override
	public int nextIndex(int index, int size) {
		return (index + 1 < size) ? index + 1 : 0;		
	}
	
	public static PlayModeFactory factory = new PlayModeFactory() {
		@Override
		public AbstractPlayMode createPlayMode() {
			return new CyclePlayMode();
		}
	};
}
