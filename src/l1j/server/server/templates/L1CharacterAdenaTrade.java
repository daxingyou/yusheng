package l1j.server.server.templates;

public class L1CharacterAdenaTrade {

	private int _id;
	private int _adena_count;
	private int _over;
	private int _count;
	private int _objid;
	private String _name;
	
	
	public L1CharacterAdenaTrade(){
		
	}
	
	public void set_Id(final int id){
		_id = id;
	}
	
	public int get_Id(){
		return _id;
	}
	
	public void set_adena_count(final int adena_count){
		_adena_count = adena_count;
	}
	
	public int get_adena_count(){
		return _adena_count;
	}
	
	public void set_count(final int count){
		_count = count;
	}
	
	public int get_count(){
		return _count;
	}
	
	public void set_objid(final int objid){
		_objid = objid;
	}
	
	public int get_objid(){
		return _objid;
	}
	
	public void set_over(final int over){
		_over = over;
	}
	
	public int get_over(){
		return _over;
	}
	
	public void set_name(final String name){
		_name = name;
	}
	
	public String get_name(){
		return _name;
	}
}
