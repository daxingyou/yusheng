package l1j.server.server.templates;

public class L1ServerFailureEnchant {

	private int _minFailureCount;
	
	public void set_minFailureCount(final int minFailureCount){
		_minFailureCount = minFailureCount;
	}
	public int get_minFailureCount(){
		return _minFailureCount;
	}
	
	private int _maxFailureCount;
	
	public void set_maxFailureCount(final int maxFailureCount){
		_maxFailureCount = maxFailureCount;
	}
	public int get_maxFailureCount(){
		return _maxFailureCount;
	}
}
