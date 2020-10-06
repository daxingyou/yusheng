package l1j.server.server.templates;

public class L1FailureEnchant {

	private int _objId;
	
	public void set_objId(final int objId){
		_objId = objId;
	}
	public int get_objId(){
		return _objId;
	}
	
	private int _itemId;
	
	public void set_itemId(final int itemId){
		_itemId = itemId;
	}
	public int get_itemId(){
		return _itemId;
	}
	
	private int _enchantLevel;
	
	public void set_enchantLevel(final int enchantLevel){
		_enchantLevel = enchantLevel;
	}
	public int get_enchantLevel(){
		return _enchantLevel;
	}
	
	private int _failureCount;
	
	public void set_failureCount(final int failureCount){
		_failureCount = failureCount;
	}
	public int get_failureCount(){
		return _failureCount;
	}
}
