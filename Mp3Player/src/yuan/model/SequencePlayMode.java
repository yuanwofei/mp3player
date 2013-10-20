package yuan.model;

import java.util.List;

/**
 * 顺序获取前后一首歌的位置
 * @author Administrator
 */
public class SequencePlayMode extends AbstractPlayMode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int getBeforePosition(int position, List<Mp3Info> mp3Infos) {
		//System.out.println("当前歌曲的ID----before--->" + position);
		if(position - 1 >= 0) {
			position = position - 1;
		} else {
			position = mp3Infos.size() - 1;
		}
		//System.out.println("前一首歌曲的ID------->" + position);
		return position;
	}

	@Override
	public int getAfterPosition(int position, List<Mp3Info> mp3Infos) {
		//System.out.println("当前歌曲的ID----after--->" + position);
		if(position + 1 < mp3Infos.size()) {
			position = position + 1;
		} else {
			position = 0;
		}
		//System.out.println("前一首歌曲的ID------->" + position);
		return position;		
	}

}
