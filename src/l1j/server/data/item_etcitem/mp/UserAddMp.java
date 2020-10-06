package l1j.server.data.item_etcitem.mp;

import java.util.Random;
import java.util.logging.Logger;

import l1j.server.data.executor.ItemExecutor;
import l1j.server.data.item_etcitem.UserName;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;

public class UserAddMp extends ItemExecutor {

	
	private Random _random = new Random();

	/**
	 *
	 */
	private UserAddMp() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new UserAddMp();
	}
	
	@Override
	public void execute(final int[] data, final L1PcInstance pc,
			final L1ItemInstance item) {
		try {
			if (pc.hasSkillEffect(L1SkillId.DECAY_POTION)) { // 藥水霜化術狀態
				pc.sendPackets(new S_ServerMessage(698)); // 喉嚨灼熱，無法喝東西。
				return;
			}
			pc.sendPackets(new S_SkillSound(pc.getId(), 190));
			pc.broadcastPacket(new S_SkillSound(pc.getId(), 190));
			pc.sendPackets(new S_ServerMessage(338, "$1084")); // 你的 魔力 漸漸恢復。
			int newMp = 0;
			if (_minmp > 0) {
				newMp = _random.nextInt(_maxmp)+_minmp;
			} else {
				newMp = _maxmp;
			}
			pc.setCurrentMp(pc.getCurrentMp() + newMp);
			pc.getInventory().removeItem(item, 1);
		} catch (Exception e) {
		}
		
	}
	
	private int _minmp = 1;
	
	private int _maxmp = 1;
	
	@Override
	public void set_set(String[] set) {
		try {
			_minmp = Integer.parseInt(set[1]);

			if (_minmp <= 0) {
//				_log.error("UserHpr 設置錯誤:最小恢復質小於等於0! 使用預設1");
				_minmp = 1;
			}

		} catch (Exception e) {
			_minmp = 1;
		}
		try {
			_maxmp = Integer.parseInt(set[2]);

			if (_maxmp >= _minmp) {
				_maxmp = (_maxmp - _minmp) + 1;

			} else {
//				_log.error("UserHpr 設置錯誤:最大恢復質小於最小恢復質!(" + _min_hp + " "
//						+ max_hp + ")");
				_maxmp = 1;
			}

		} catch (Exception e) {
			_maxmp = 1;
		}
	}
}
