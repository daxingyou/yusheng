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

public class ScrollEnchantArmorA  extends ItemExecutor {
	private static Random _random = new Random();
    /**
	 *
	 */
    private ScrollEnchantArmorA() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new ScrollEnchantArmorA();
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

        // 安定值
        final int safe_enchant = tgItem.getItem().get_safeenchant();
        if (safe_enchant < 0) { // 强化不可
			pc.sendPackets(new S_ServerMessage(79)); // \f1何起。
			return;
		}
        if (tgItem.getItem().getType2() != 2){
        	pc.sendPackets(new S_ServerMessage(79));
        	return;
        }
        if (!ScrollEnchantIdTable.get().isEnchant(tgItem.getItem().getItemId())){
        	pc.sendPackets(new S_ServerMessage(79));
        	return;
        }
        if (tgItem.isSeal()) {
			pc.sendPackets(new S_SystemMessage(tgItem.getLogViewName() +"处于封印状态！"));
			return;
		}
        final int enchant_level = tgItem.getEnchantLevel();
        pc.getInventory().removeItem(item, 1);
        if (enchant_level < safe_enchant) {
			new EnchantExecutor().successEnchant(pc, tgItem, 1);
		} else {
			final int rnd = _random.nextInt(100) + 1; 
			int enchant_chance_armor;
			int enchant_level_tmp;
			int sum = enchant_level - safe_enchant;
			if (safe_enchant == 0 || sum >= 2) { // 骨、用补正
				enchant_level_tmp = enchant_level + 2;
			} else {
				enchant_level_tmp = enchant_level;
			}
			if (enchant_level >= safe_enchant) {
				enchant_chance_armor = (100 + enchant_level_tmp
						* Config.ENCHANT_CHANCE_ARMOR)
						/ (enchant_level_tmp * 2);
			} else {
				enchant_chance_armor = (100 + enchant_level_tmp
						* Config.ENCHANT_CHANCE_ARMOR)
						/ enchant_level_tmp;
			}
			if (rnd < enchant_chance_armor) {
				new EnchantExecutor().successEnchant(pc, tgItem, 1);
			} else {
				pc.sendPackets(new S_ServerMessage(160,tgItem.getLogName(), "$252", "$248"));
			}
		}
    }
}
