package l1j.server.data.item_etcitem.hp;

import l1j.server.data.executor.ItemExecutor;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1BuffUtil;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * HP恢復藥劑類型<BR>
 * classname: UserAddHp<BR>
 * <BR>
 * 設置對象:道具(etcitem)<BR>
 * <BR>
 * 設置範例:hp.UserAddHp 30 50 197<BR>
 * 恢復30~50點體力 動畫白光(動畫設置小於等於0 不顯示動畫)<BR>
 * 
 */
public class UserAddHp extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(UserAddHp.class);

	/**
	 *
	 */
	private UserAddHp() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new UserAddHp();
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
		try {
			// 例外狀況:物件為空
			if (item == null) {
				return;
			}
			// 例外狀況:人物為空
			if (pc == null) {
				return;
			}
			if (L1BuffUtil.stopPotion(pc)) {
				pc.setSelHealHpPotion(item.getItem().getItemId(),_max_addhp,_gfxid);
		        if (pc.getHealHPAI()){
					return;
		        }
				pc.getInventory().removeItem(item, 1);

				// 解除魔法技能绝对屏障
				L1BuffUtil.cancelAbsoluteBarrier(pc);

				if (_gfxid > 0) {// 具備動畫
					pc.sendPacketsAll(new S_SkillSound(pc.getId(), _gfxid));
				}

				int addhp = _min_hp;

				if (_max_addhp > 0) {// 具備最大質
					addhp += (int) (Math.random() * _max_addhp);// 隨機數字範圍
				}

/*				if (pc.get_up_hp_potion() > 0) {// 藥水使用HP恢復增加(1/100)
					addhp += ((addhp * pc.get_up_hp_potion()) / 100);
				}*/
				if (pc.hasSkillEffect(L1SkillId.POLLUTE_WATER)) {// 污濁之水
					addhp = (addhp >> 1);
				}
				if (addhp > 0) {
					// 你覺得舒服多了訊息
					pc.sendPackets(new S_ServerMessage(77));
				}
				pc.setCurrentHp(pc.getCurrentHp() + addhp);
			}

		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private int _min_hp = 1;
	private int _max_addhp = 0;
	private int _gfxid = 0;

	@Override
	public void set_set(String[] set) {
		try {
			_min_hp = Integer.parseInt(set[1]);

			if (_min_hp <= 0) {
//				_log.error("UserHpr 設置錯誤:最小恢復質小於等於0! 使用預設1");
				_min_hp = 1;
			}

		} catch (Exception e) {
		}
		try {
			int max_hp = Integer.parseInt(set[2]);

			if (max_hp >= _min_hp) {
				_max_addhp = (max_hp - _min_hp) + 1;

			} else {
//				_log.error("UserHpr 設置錯誤:最大恢復質小於最小恢復質!(" + _min_hp + " "
//						+ max_hp + ")");
				_max_addhp = 0;
			}

		} catch (Exception e) {
		}
		try {
			_gfxid = Integer.parseInt(set[3]);

		} catch (Exception e) {
		}
	}
}
