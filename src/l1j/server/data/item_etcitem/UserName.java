package l1j.server.data.item_etcitem;

import l1j.server.data.executor.ItemExecutor;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_SystemMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 變名卡 41227
 * 
 * DELETE FROM `etcitem` WHERE `item_id`='41227'; INSERT INTO `etcitem` VALUES
 * ('41227', '變名卡', 'shop.UserName', '變名卡', 'questitem', 'normal', 'none', '0',
 * '3343', '3963', '0', '0', '0', '0', '0', '0', '0', '1', '1', '0', '0', '0',
 * '0', '0', '1');
 * 
 * @author dexc
 * 
 */
public class UserName extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(UserName.class);

	/**
	 *
	 */
	private UserName() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new UserName();
	}

	/**
	 * 道具物件執行
	 * 
	 * @param data
	 *            參數
	 * @param pc
	 *            執行者
	 * @param item
	 *            物件
	 */
	@Override
	public void execute(final int[] data, final L1PcInstance pc,
			final L1ItemInstance item) {
		if (item == null) { // 例外狀況:物件為空
			return;
		}

		if (pc == null) { // 例外狀況:人物為空
			return;
		}

		if (pc.isGhost()) { // 鬼魂模式
			return;
		}

		if (pc.isDead()) { // 死亡
			return;
		}

		if (pc.isTeleport()) { // 傳送中
			return;
		}

/*		if (pc.getLawful() < 32767) {// 正義未滿
			pc.sendPackets(new S_SystemMessage("正義質必須為32767才可以使用"));
			return;
		}*/

		if (pc.isPrivateShop()) {// 商店村模式
			pc.sendPackets(new S_SystemMessage("请先结束商店模式!"));
			return;
		}

		final Object[] petList = pc.getPetList().values().toArray();
		if (petList.length > 0) {
			pc.sendPackets(new S_SystemMessage("请先回收宠物，祭祀，魔法娃娃！"));
			return;
		}
		if (pc.getClanid() != 0) {
			pc.sendPackets(new S_SystemMessage("请先退出血盟!"));
			return;
		}
		try {
			// 名稱
//			System.out.println("验证通过执行更名提示");
			pc.sendPackets(new S_Message_YN(325,""));
			pc.rename(item.getItemId());

		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
