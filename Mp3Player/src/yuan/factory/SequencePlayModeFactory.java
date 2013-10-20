package yuan.factory;

import yuan.factory.model.AbstractPlayMode;
import yuan.factory.model.SequencePlayMode;

public class SequencePlayModeFactory implements IPlayModeFactory {

	@Override
	public AbstractPlayMode createPlayMode() {
		// TODO Auto-generated method stub
		return new SequencePlayMode();
	}

}
