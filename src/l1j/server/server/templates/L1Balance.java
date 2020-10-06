package l1j.server.server.templates;

public class L1Balance {
	
	private String _name;
	
	private String _password;
	
	private int _balance;
	
	public void setAcName(String acname)
	{
		_name = acname;
	}
	
	public String getAcName()
	{
		return _name;
	}
	
	public void setAcPasswords(String pass)
	{
		_password = pass;
	}
	
	public String getAcPassWords()
	{
		return _password;
	}
	
	public void setAcBalance(int bce)
	{
		_balance = bce;
	}
	
	public int getAcBalance()
	{
		return _balance;
	}

}
