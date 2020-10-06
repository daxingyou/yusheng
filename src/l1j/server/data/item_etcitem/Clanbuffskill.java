package l1j.server.data.item_etcitem;

import l1j.server.data.executor.ItemExecutor;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_GreenMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.world.L1World;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Clanbuffskill extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(Clanbuffskill.class);

	private Clanbuffskill() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Clanbuffskill();
	}

	private static final int skillIds[] = new int[] { 26, 42, 43, 48, 79, 151,
			158, 148, 115, 117 };

	@Override
	public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
		try {
			// 例外状况:物件为空
			if (item == null) {
				return;
			}
			// 例外状况:人物为空
			if (pc == null) {
				return;
			}
			if (pc.getClanid() == 0 || pc.getClan() == null) {
				pc.sendPackets(new S_SystemMessage("\\F2你还没有加入血盟"));
				return;
			}
			if (pc.getInventory().checkItem(40308, 1)) {
				pc.getInventory().consumeItem(40308, 1);
				for (final L1PcInstance targetchanpc : pc.getClan()
						.getOnlineClanMember()) {
					if (targetchanpc.isPrivateShop()
							|| targetchanpc.getNetConnection() == null) {
						continue;
					}
					for (final int element : skillIds) {
						int skillId = element;
						if (skillId == 148) {
							if (targetchanpc.isElf()) {
								skillId = 149;
							}
						}
						new L1SkillUse().handleCommands(targetchanpc, skillId,
								targetchanpc.getId(), targetchanpc.getX(),
								targetchanpc.getY(), null, 1800,
								L1SkillUse.TYPE_GMBUFF);
					}
				}
				final StringBuilder msg = new StringBuilder();
				msg.append("\\f=【\\f2");
				msg.append(pc.getClanname());
				msg.append("\\f=】血盟的土豪<\\f2");
				msg.append(pc.getName());
				msg.append("\\f=>使用元宝为其在线成员加buff");
				L1World.getInstance().broadcastPacketToAll(
						new S_GreenMessage(msg.toString()));
			} else {
				pc.sendPackets(new S_SystemMessage("元宝不足5."));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
