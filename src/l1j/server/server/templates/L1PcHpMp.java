package l1j.server.server.templates;

import java.util.HashMap;
import java.util.Map;

public final class L1PcHpMp {
	
	private final Map<Integer, Integer> _levelhpup = new HashMap<Integer, Integer>();
	
	private final Map<Integer, Integer> _levelmpup = new HashMap<Integer, Integer>();
	
	public L1PcHpMp(final int hp,final int mp,final int level) {
		_levelhpup.put(level, hp);
		_levelmpup.put(level, mp);
	}
	
	public  Map<Integer, Integer> getHp(){
		return _levelhpup;
	}
	
	public  Map<Integer, Integer> getMp(){
		return _levelmpup;
	}
	
	public void puthpmp(final int level, final int hp, final int mp ){
		_levelhpup.put(level, hp);
		_levelmpup.put(level, mp);
	}

}
