package l1j.server.server.templates;

public class L1DollItem {

	private String _classname;
	
	public void set_classname(final String className){
		_classname = className;
	}
	
	public String get_classname(){
		return _classname;
	}
	
	private int _minValue1 = 0;
	
	public void set_minValue1(final int minValue1){
		_minValue1 = minValue1;
	}
	public int get_minValue1(){
		return _minValue1;
	}
	
    private int _maxValue1 = 0;
	
	public void set_maxValue1(final int maxValue1){
		_maxValue1 = maxValue1;
	}
	public int get_maxValue1(){
		return _maxValue1;
	}
	
    private int _minValue2 = 0;
	
	public void set_minValue2(final int minValue2){
		_minValue2 = minValue2;
	}
	public int get_minValue2(){
		return _minValue2;
	}
	
    private int _maxValue2 = 0;
	
	public void set_maxValue2(final int maxValue2){
		_maxValue2 = maxValue2;
	}
	public int get_maxValue2(){
		return _maxValue2;
	}

    private String _nameId;
	
	public void set_nameId(final String nameId){
		_nameId = nameId;
	}
	
	public String get_nameId(){
		return _nameId;
	}
	
    private String _name;
	
	public void set_name(final String name){
		_name = name;
	}
	
	public String get_name(){
		return _name;
	}
}
