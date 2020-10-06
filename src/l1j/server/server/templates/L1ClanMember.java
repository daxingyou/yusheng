package l1j.server.server.templates;

public class L1ClanMember {
	
	private final String _name;
	
	private final int _rank;
	
	public L1ClanMember(final String name, final int rank)
	{
		_rank = rank;
		_name = name;	
	}
	
	public int getRank()
	{
		return _rank;
	}

	public String getName()
	{
		return _name;
	}
}
