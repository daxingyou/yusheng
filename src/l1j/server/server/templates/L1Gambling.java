package l1j.server.server.templates;

/**
 * 赌场纪录
 * @author dexc
 *
 */
public class L1Gambling {

	// TODO 资料存放区
	
	private int _id;// 场次编号
	private int _npcid;// 优胜者
	private double _rate;// 赔率
	private int _moneyNo;// 币质
	
	// TODO 资料输入区
	
	/**
	 * <font color=#ff0000>设定场次编号</font>
	 * @param eventId
	 */
	public void set_id(int i) {
		_id = i;
	}

	/**
	 * <font color=#ff0000>设定优胜者</font>
	 * @param s
	 */
	public void set_npcid(int i) {
		_npcid = i;
	}

	/**
	 * <font color=#ff0000>设定赔率</font>
	 * @param flag
	 */
	public void set_rate(double i) {
		_rate = i;
	}

	/**
	 * <font color=#ff0000>设定币质</font>
	 * @param i 0金币 1NC纪念币
	 */
	public void set_moneyNo(int i) {
		_moneyNo = i;
	}
	
	// TODO 资料输出区
	
	/**
	 * <font color=#0000ff>传回场次编号</font>
	 * @return
	 */
	public int get_id() {
		return _id;
	}

	/**
	 * <font color=#0000ff>传回优胜者</font>
	 * @return
	 */
	public int get_npcid() {
		return _npcid;
	}

	/**
	 * <font color=#0000ff>传回赔率</font>
	 * @return
	 */
	public double get_rate() {
		return _rate;
	}

	/**
	 * <font color=#0000ff>传回币质</font>
	 * @return 0金币 1NC纪念币
	 */
	public int get_moneyNo() {
		return _moneyNo;
	}

}
