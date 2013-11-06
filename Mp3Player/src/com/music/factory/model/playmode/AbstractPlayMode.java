package com.music.factory.model.playmode;

/**
 * 获取前后一首歌的位置，使用工厂方法设计模式（内部类）
 * @author Administrator
 */
public abstract class AbstractPlayMode {
	
	/**
	 * @param index 当前播放的歌曲的索引
	 * @param size 歌曲列表的总数
	 * @return 前一首歌在歌曲列表中的索引
	 */
	public abstract int prevIndex(int index, int size);
	
	/**
	 * @param index 当前播放的歌曲的索引
	 * @param size 歌曲列表的总数
	 * @return 下一首歌的位置
	 */
	public abstract int nextIndex(int index, int size);
}
