package yuan.model;

import java.util.List;

/**
 * 随机获取前后一首歌的位置
 * @author Administrator
 *
 */
public class RandomPlayMode extends AbstractPlayMode {

	private static final long serialVersionUID = 1L;

	@Override
	public int getBeforePosition(int position, List<Mp3Info> mp3Infos) {
		//Math.random()*(b-a)+a从a - b之间随机选取一个数
		position = (int) (Math.random()*(mp3Infos.size() - 1));
		return position;
	}

	@Override
	public int getAfterPosition(int position, List<Mp3Info> mp3Infos) {
		position = (int) (Math.random()*(mp3Infos.size() - 1));
		return position;
	}

}
