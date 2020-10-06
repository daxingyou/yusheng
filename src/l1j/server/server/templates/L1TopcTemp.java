package l1j.server.server.templates;

public class L1TopcTemp {
	private String _name; // ● 名前

	public String getName() {
		return _name;
	}

	private int _level; // ● 

	public int getLevel() {
		return _level;
	}

	public L1TopcTemp(final String name,final int level){
		_name = name;
		_level = level;
	}
}
