package l1j.server.server.templates;

import java.util.HashMap;
import java.util.Map;

public class L1WeaponEnchantTemp {

	private int _maxenchantlevel = -1;
	
	public void set_maxenchantlevel(final int maxenchantlevel){
		_maxenchantlevel = maxenchantlevel;
	}
	
	public int get_maxenchantlevel(){
		return _maxenchantlevel;
	}
	

	private final Map<Integer,Integer> _enchantmap = new HashMap<Integer,Integer>();
	
	public void put_dmg(final int enchant,final int dmg){
		_enchantmap.put(enchant, dmg);
	}
	
	public int get_dmg(final int enchantlevel){
		if (_maxenchantlevel < 0){
			return 0;
		}
		int enchantlv = enchantlevel;
		if (enchantlv > _maxenchantlevel){
			enchantlv = _maxenchantlevel;
		}
		if (_enchantmap.containsKey(new Integer(enchantlv))){
			return _enchantmap.get(new Integer(enchantlv)).intValue();
		}
		return 0;
	}
}
