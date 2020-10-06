package l1j.server.data.item_etcitem.shop;

import l1j.server.Config;
import l1j.server.data.executor.ItemExecutor;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;

/**
 * 经验卡59008 $1499：经验值 $1487：魔法卷轴 DELETE FROM `etcitem` WHERE `item_id`='59008';
 * INSERT INTO `etcitem` VALUES (44156, '经验卡', 'shop.Clan_Honor_Reel', '$1499
 * $1487', 'scroll', 'normal', 'paper', 0, 3069, 3963, 0, 1, 0, 0, 0, 0, 0, 0,
 * 1, 1, 0, 0, 0, 1, 1);
 */
public class AddExp_Car extends ItemExecutor {

    /**
	 *
	 */
    private AddExp_Car() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new AddExp_Car();
    }

	/**
	 * 道具物件执行
	 * 
	 * @param data
	 *            参数
	 * @param pc
	 *            执行者
	 * @param item
	 *            物件
	 */
	public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
		//try {
			// 例外状况:物件为空
			if (item == null) {
				return;
			}
			// 例外状况:人物为空
			if (pc == null) {
				return;
			}

			if (pc.getLevel() >= sec) {// 已达最大等级终止计算
				pc.sendPackets(new S_SystemMessage("已经达到最高使用等级!"));
				return;
			}
			

			if (pc.getInventory().consumeItem(40308, 1)) {
				double addExp = 2000000; // 经验值增加1500

				// 目前等级可获取的经验值
				final double exppenalty = ExpTable
						.getPenaltyRate(pc.getLevel());
				// 目前等级可获取的经验值
				if (exppenalty < 1D) {
					addExp *= exppenalty;
				}

				// 服务器经验加倍
				if (Config.RATE_XP > 1.0) {
					addExp *= Config.RATE_XP;
				}

				pc.addExp((int) addExp);
				pc.getInventory().removeItem(item, 1);
				pc.sendPacketsAll(new S_SkillSound(pc.getId(), 9714));
				
			} else {
				// 189 \f1金币不足。
				pc.sendPackets(new S_ServerMessage(189));
			}
	}
	
	private int sec;
	
	@Override
	public void set_set(String[] set) {
		try {
			sec = Integer.parseInt(set[1]);		
		} catch (Exception e) {
			sec =  52;
		}
	}
}
