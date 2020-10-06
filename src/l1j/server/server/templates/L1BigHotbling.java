package l1j.server.server.templates;

/**
 * 四星彩纪录
 * @author dexc
 *
 */
public class L1BigHotbling {

	// TODO 资料存放区
	
	private int _id;// 场次编号
	private String _number;// 开奖号码
	private int _totalPrice;// 总累积彩金
	private int _count;// 累积头奖中奖票数
	private int _count1;// 累积一奖中奖票数
	private int _count2;// 累积二奖中奖票数
	private int _count3;// 累积三奖中奖票数
	private int _money1;// 累积头奖彩金
	private int _money2;// 累积一奖彩金
	private int _money3;// 累积二奖彩金

	// TODO 资料输入区
	
	/**
	 * <font color=#ff0000>设定场次编号</font>
	 * @param eventId
	 */
	public void set_id(int i) {
		_id = i;
	}

	/**
	 * <font color=#ff0000>设定开奖号码</font>
	 * @param s
	 */
	public void set_number(String i) {
		_number = i;
	}

	/**
	 * <font color=#ff0000>设定总累积彩金</font>
	 * @param s
	 */
	public void set_totalPrice(int i) {
		_totalPrice = i;
	}

	/**
	 * <font color=#ff0000>设定累积头奖彩金</font>
	 * @param s
	 */
	public void set_money1(int i) {
		_money1 = i;
	}

	/**
	 * <font color=#ff0000>设定累积头奖中奖票数</font>
	 * @param s
	 */
	public void set_count(int i) {
		_count = i;
	}

	/**
	 * <font color=#ff0000>设定累积一奖彩金</font>
	 * @param s
	 */
	public void set_money2(int i) {
		_money2 = i;
	}

	/**
	 * <font color=#ff0000>设定累积一奖中奖票数</font>
	 * @param s
	 */
	public void set_count1(int i) {
		_count1 = i;
	}

	/**
	 * <font color=#ff0000>设定累积二奖彩金</font>
	 * @param s
	 */
	public void set_money3(int i) {
		_money3 = i;
	}

	/**
	 * <font color=#ff0000>设定累积二奖中奖票数</font>
	 * @param s
	 */
	public void set_count2(int i) {
		_count2 = i;
	}

	/**
	 * <font color=#ff0000>设定累积三奖中奖票数</font>
	 * @param s
	 */
	public void set_count3(int i) {
		_count3 = i;
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
	 * <font color=#0000ff>开奖号码</font>
	 * @return
	 */
	public String get_number() {
		return _number;
	}

	/**
	 * <font color=#0000ff>设定总累积彩金</font>
	 * @param s
	 */
	public int get_totalPrice() {
		return _totalPrice;
	}

	/**
	 * <font color=#ff0000>设定累积头奖彩金</font>
	 * @param s
	 */
	public int get_money1() {
		return _money1;
	}

	/**
	 * <font color=#ff0000>设定累积头奖中奖票数</font>
	 * @param s
	 */
	public int get_count() {
		return _count;
	}

	/**
	 * <font color=#ff0000>设定累积一奖彩金</font>
	 * @param s
	 */
	public int get_money2() {
		return _money2;
	}

	/**
	 * <font color=#ff0000>设定累积一奖中奖票数</font>
	 * @param s
	 */
	public int get_count1() {
		return _count1;
	}

	/**
	 * <font color=#ff0000>设定累积二奖彩金</font>
	 * @param s
	 */
	public int get_money3() {
		return _money3;
	}

	/**
	 * <font color=#ff0000>设定累积二奖中奖票数</font>
	 * @param s
	 */
	public int get_count2() {
		return _count2;
	}

	/**
	 * <font color=#ff0000>设定累积三奖中奖票数</font>
	 * @param s
	 */
	public int get_count3() {
		return _count3;
	}

}
