package l1j.server.server.model;

import java.util.ArrayList;

public class L1ExcludingMailList {
	private ArrayList<String> _nameList = new ArrayList<String>();

	public void add(String name) {
		_nameList.add(name);
	}

	public String remove(String name) {
		for (String each : _nameList) {
			if (each.equalsIgnoreCase(name)) {
				_nameList.remove(each);
				return each;
			}
		}
		return null;
	}

	public boolean contains(String name) {
		for (String each : _nameList) {
			if (each.equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	public boolean isFull() {
		return _nameList.size() >= 50;
	}
}