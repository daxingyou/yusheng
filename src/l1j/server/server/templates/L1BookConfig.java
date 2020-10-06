package l1j.server.server.templates;

/** 記憶座標 ,設置紀錄 */
public class L1BookConfig {

	private int _objId = 0;

	private byte[] _data = null;

	public int get_objId() {
		return this._objId;
	}

	public void set_objId(final int objId) {
		this._objId = objId;
	}

	public byte[] get_data() {
		return this._data;
	}

	public void set_data(final byte[] data) {
		this._data = data;
	}
}
