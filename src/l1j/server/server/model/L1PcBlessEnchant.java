package l1j.server.server.model;

import java.util.ArrayList;

import l1j.server.server.datatables.CharacterBlessEnchantTable;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.templates.L1CharacterBlessEnchant;

public class L1PcBlessEnchant {
	
	private final L1PcInstance _owner; // 所有者
	
	private ArrayList<L1CharacterBlessEnchant> _items = new ArrayList<L1CharacterBlessEnchant>();
	
	public L1PcBlessEnchant(final L1PcInstance pc){
		_owner = pc;
	}
	
	public void loadBlessEnchant(){
		_items = CharacterBlessEnchantTable.get().load(_owner.getId());
	}
	
	public int getEnchantCount(final int itemId){
		int count = 0;
		for(L1CharacterBlessEnchant temp : _items){
			if (itemId == temp.get_ItemId()){
				return temp.get_count();
			}
		}
		return count;
	}
	
	
	public void updateEnchantCount(final int itemId,final int count){
		L1CharacterBlessEnchant blessEnchant = null;
		for(L1CharacterBlessEnchant temp : _items){
			if (itemId == temp.get_ItemId()){
				blessEnchant = temp;
				break;
			}
		}
		if (blessEnchant != null){
			blessEnchant.set_count(count);
			CharacterBlessEnchantTable.get().updateBlessEnchant(_owner.getId(),blessEnchant);
		}else{
			blessEnchant = new L1CharacterBlessEnchant();
			blessEnchant.set_count(count);
			blessEnchant.set_ItemId(itemId);
			_items.add(blessEnchant);
			CharacterBlessEnchantTable.get().addBlessEnchant(_owner.getId(),blessEnchant);
		}
	}
	
	public void clear(){
		_items.clear();
	}
}
