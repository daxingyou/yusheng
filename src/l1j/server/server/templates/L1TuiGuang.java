package l1j.server.server.templates;

import java.sql.Timestamp;

public class L1TuiGuang {
	private int _id;
	
	private String _account;
	
	private int _itemid;
	
	private int _count;
	
	private boolean _isling = false;
	
	private Timestamp _time = null;
	
	public void setTid(int id)
	{
		_id = id;
	}
	
	public void setTaccount(String name)
	{
		_account = name;		
	}
	
	public void setTitmeid(int itemid)
	{
		_itemid = itemid;
	}
	
	public void setItemcount(int count)
	{
		_count = count;
	}
	
	public void setTling(boolean flg)
	{
		_isling = flg;	
	}
	
	public void setTtime(Timestamp time)
	{
		_time = time;	
	}
	
	public String getTaccount()
	{
		return _account;
	}
	
	public int getTitemid()
	{
		return _itemid;
	}
	
	public int getTcount()
	{
		return _count;
	}
	
	public boolean isTling()
	{
		return _isling;
	}
	
	public Timestamp getTtime()
	{
		return _time;
	}
	
	public int getTid()
	{
		return _id;
	}

}
