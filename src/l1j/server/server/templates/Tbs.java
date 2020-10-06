package l1j.server.server.templates;

import java.sql.Timestamp;

public class Tbs {
	private int _tbsid;
	
	public void setTbsId(int id){
		_tbsid = id;
	}
	
	public int getTbsId(){
		return _tbsid;
	}
	
	private String _accountname;
	
	public void setTbsName(String name){
		_accountname = name;
	}
	
	public String getTbsName(){
		return _accountname;
	}
	
	private int _itemid;
	
	public void setCnitemid(int itemid){
		_itemid = itemid;
	}
	
	public int getCnitemid(){
		return _itemid;
	}
	
	private boolean _isuse;
	
	public void setCnUse(boolean flg){
		_isuse = flg;
	}
	public boolean getCnUse(){
		return _isuse;
	}
	private int _counts;
	
	public void setCnCount(int count){
		_counts = count;
	}
	
	public int getCnCount(){
		return _counts;
	}
	
	private Timestamp _dates;
	
	public void setDate(Timestamp time){
		_dates = time;
	}
	
	public Timestamp getDate(){
		return _dates;
	}
	
	
	

}
