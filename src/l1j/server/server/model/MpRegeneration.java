package l1j.server.server.model;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MpRegeneration {
	private static final Log _log = LogFactory.getLog(MpRegeneration.class);

	private final L1PcInstance _pc;

	private int _regenPoint = 0;

	private int _curPoint = 4;

	public MpRegeneration(L1PcInstance pc) {
		_pc = pc;
	}

	public void setState(int state) {
		if (_curPoint < state) {
			return;
		}

		_curPoint = state;
	}
	
	public void keepmpr(){
		try {
			if (_pc.isDead()) {
				return;
			}

			_regenPoint += _curPoint;
			_curPoint = 4;

			if (64 <= _regenPoint) {
				_regenPoint = 0;
				regenMp();
			}
		} catch (Throwable e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	public void regenMp() {
		int baseMpr = 1;
		int wis = _pc.getWis();
		if (wis == 15 || wis == 16) {
			baseMpr = 2;
		} else if (wis >= 17) {
			baseMpr = 3;
		}

		if (_pc.hasSkillEffect(L1SkillId.STATUS_BLUE_POTION) == true) { // 使用中
			if (wis < 11) { // WIS11未满MPR+1
				wis = 11;
			}
			baseMpr += wis - 10;
		}
		if (_pc.hasSkillEffect(L1SkillId.MEDITATION) == true) { // 中
			baseMpr += 5;
		}
		if (_pc.hasSkillEffect(L1SkillId.COOKING_1_2_N)
				|| _pc.hasSkillEffect(L1SkillId.COOKING_1_2_S)) {
			baseMpr += 3;
		}
		// 盟屋、旅馆内回血量增加 
		if (L1HouseLocation.isInHouse(_pc.getX(), _pc.getY(), (short)_pc.getMapId())) {
			baseMpr += 5;
		}
		if (_pc.getMapId() >= 16384 && _pc.getMapId() <= 25088) { // 旅馆
			baseMpr += 3;
		}
		// 盟屋、旅馆内回血量增加  end

		int itemMpr = _pc.getInventory().mpRegenPerTick();
		itemMpr += _pc.getMpr();

		if (_pc.get_food() < 24 || isOverWeight(_pc)) {
			baseMpr = 0;
			if (itemMpr > 0) {
				itemMpr = 0;
			}
		}
		int mpr = baseMpr + itemMpr;
		int newMp = _pc.getCurrentMp() + mpr + getMprReductionByClan(_pc);
		if (newMp < 0) {
			newMp = 0;
		}
		_pc.setCurrentMp(newMp);
	}
	/**
     * 血盟技能MPR增加
     * 
     * @return
     */
    private int getMprReductionByClan(final L1PcInstance pc) {
        int mp = 0;
        try {
            if (pc == null) {
                return 0;
            }
            if (pc.getClanid() == 0){
            	return 0;
            }
            final L1Clan clan = pc.getClan();
            if (clan == null) {
                return 0;
            }
            // 具有血盟技能
            if (clan.isClanskill()) {
                if (clan.getSkillLevel() == 1) {
                	mp += 1;
                }else if (clan.getSkillLevel() == 2) {
                	mp += 2;
                }else if (clan.getSkillLevel() == 3) {
                	mp += 3;
                }else if (clan.getSkillLevel() == 4) {
                	mp += 4;
                }else if (clan.getSkillLevel() == 5) {
                	mp += 5;
                }
            }

        } catch (final Exception e) {
            return 0;
        }
        return mp;
    }
	private boolean isOverWeight(L1PcInstance pc) {
		// 状态、状态、
		// 重量无。
		if (pc.hasSkillEffect(L1SkillId.EXOTIC_VITALIZE)
				|| pc.hasSkillEffect(L1SkillId.ADDITIONAL_FIRE)) {
			return false;
		}

		return (120 <= pc.getInventory().getWeight240()) ? true : false;
	}
}
