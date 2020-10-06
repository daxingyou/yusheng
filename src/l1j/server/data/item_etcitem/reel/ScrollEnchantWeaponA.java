package l1j.server.data.item_etcitem.reel;

import java.util.Random;

import l1j.server.Config;
import l1j.server.data.cmd.EnchantExecutor;
import l1j.server.data.executor.ItemExecutor;
import l1j.server.server.datatables.ScrollEnchantIdTable;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;

public class ScrollEnchantWeaponA extends ItemExecutor {
	private static Random _random = new Random();
    /**
	 *
	 */
    private ScrollEnchantWeaponA() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new ScrollEnchantWeaponA();
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
    @Override
    public void execute(final int[] data, final L1PcInstance pc,
            final L1ItemInstance item) {
        // 对象OBJID
        final int targObjId = data[0];

        final L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);

        if (tgItem == null) {
            return;
        }
        if (tgItem.getItem().getType2() != 1) {
			pc.sendPackets(new S_ServerMessage(79)); // \f1何起。
			return;
		}
        if (!ScrollEnchantIdTable.get().isEnchant(tgItem.getItem().getItemId())){
        	pc.sendPackets(new S_ServerMessage(79));
        	return;
        }
        final int safe_enchant = tgItem.getItem().get_safeenchant();
		if (safe_enchant < 0) { // 强化不可
			pc.sendPackets(new S_ServerMessage(79)); // \f1何起。
			return;
		}
		if (tgItem.isSeal()) {
			pc.sendPackets(new S_SystemMessage(tgItem.getLogViewName() +"处于封印状态！"));
			return;
		}
		final int quest_weapon = tgItem.getItem().getItemId();
		if (quest_weapon >= 246 && quest_weapon <= 249) { // 强化不可
			pc.sendPackets(new S_ServerMessage(79)); // \f1何起。
			return;
		}
		pc.getInventory().removeItem(item, 1);
		final int enchant_level = tgItem.getEnchantLevel();
		if (enchant_level < safe_enchant) {
			new EnchantExecutor().successEnchant(pc, tgItem, 1);
		} else {
			final int rnd = _random.nextInt(100) + 1;
			int enchant_chance_wepon;
			int enchant_level_tmp;
			int sum = enchant_level - safe_enchant;
			if (safe_enchant == 0 || sum >= 2) { // 骨、用补正
				enchant_level_tmp = enchant_level + 2;
			} else {
				enchant_level_tmp = enchant_level;
			}
			if (enchant_level >= safe_enchant) {
				enchant_chance_wepon = (100 + 3 * Config.ENCHANT_CHANCE_WEAPON) / enchant_level_tmp;
			} else {
				enchant_chance_wepon = (100 + 3 * Config.ENCHANT_CHANCE_WEAPON) / 3;
			}
			if (rnd < enchant_chance_wepon) {
				new EnchantExecutor().successEnchant(pc, tgItem, 1);
			}else{
				pc.sendPackets(new S_ServerMessage(160,tgItem.getLogName(), "$245", "$248"));
			}
		}
    }
}
