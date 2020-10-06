package l1j.server.server.model.doll;

import l1j.server.server.model.Instance.L1PcInstance;

/**
 * 娃娃各项能力设置抽象接口
 * 
 * @author daien
 * 
 */
public abstract class L1DollExecutor {

    /**
     * 设置能力设定值
     * 
     * @param int1
     * @param int2
     * @param int3
     */
    public abstract void set_power(int int1, int int2, int int3);

    /**
     * 装备娃娃效果
     * 
     * @param pc
     * @return
     */
    public abstract void setDoll(L1PcInstance pc);

    /**
     * 解除娃娃效果
     * 
     * @param pc
     * @return
     */
    public abstract void removeDoll(L1PcInstance pc);

    /**
     * 是否重新设置
     * 
     * @return
     */
    public abstract boolean is_reset();
    
    public int getValue1(){
    	return 0;
    }
    
    public int getValue2(){
    	return 0;
    }
    public int getValue3(){
    	return 0;
    }
    
    private String _className;

	public void set_powerClassName(String className) {
		_className = className;
	}
	
	public String getClassName(){
		return _className;
	}
	
	private String _Name;

	public void set_Name(String Name) {
		_Name = Name;
	}
	
	public String getName(){
		return _Name;
	}
	
	private String _NameId;

	public void set_NameId(String NameId) {
		_NameId = NameId;
	}
	
	public String getNameId(){
		return _NameId;
	}
}
