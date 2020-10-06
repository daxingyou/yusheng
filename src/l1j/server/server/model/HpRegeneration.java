/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */

package l1j.server.server.model;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.server.model.Instance.L1EffectInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;

public class HpRegeneration {

	private static final Log _log = LogFactory.getLog(HpRegeneration.class);

	private final L1PcInstance _pc;

	private int _regenMax = 0;

	private int _regenPoint = 0;

	private int _curPoint = 4;

	private static Random _random = new Random();

	public HpRegeneration(L1PcInstance pc) {
		_pc = pc;

		updateLevel();
	}

	public void setState(int state) {
		if (_curPoint < state) {
			return;
		}

		_curPoint = state;
	}

	public void keephpr() {
		try {
			if (_pc.isDead()) {
				return;
			}

			_regenPoint += _curPoint;
			_curPoint = 4;

/*			synchronized (this) {
				if (_regenMax <= _regenPoint) {
					_regenPoint = 0;
					regenHp();
				}
			}*/
			
			if (_regenMax <= _regenPoint) {//由于是1根线程所以去掉同步锁
				_regenPoint = 0;
				regenHp();
			}
			
		} catch (Throwable e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	public void updateLevel() {
		final int lvlTable[] = new int[] { 30, 25, 20, 16, 14, 12, 11, 10, 9,
				3, 2 };

		int regenLvl = Math.min(10, _pc.getLevel());
		if (30 <= _pc.getLevel() && _pc.isKnight()) {
			regenLvl = 11;
		}

/*		synchronized (this) {
			_regenMax = lvlTable[regenLvl - 1] * 4;
		}*/
		_regenMax = lvlTable[regenLvl - 1] * 4;//由于是1根线程所以去掉同步锁
	}

	public void regenHp() {
		if (_pc.isDead()) {
			return;
		}

		int maxBonus = 1;

		// CON
		if (11 < _pc.getLevel() && 14 <= _pc.getCon()) {
			maxBonus = _pc.getCon() - 12;
			if (25 < _pc.getCon()) {
				maxBonus = 14;
			}
		}

		int equipHpr = _pc.getInventory().hpRegenPerTick();
		equipHpr += _pc.getHpr();
		int bonus = _random.nextInt(maxBonus) + 1;

		if (_pc.hasSkillEffect(L1SkillId.NATURES_TOUCH)) {
			bonus += 15;
		}
		if (_pc.hasSkillEffect(L1SkillId.COOKING_1_5_N)
				|| _pc.hasSkillEffect(L1SkillId.COOKING_1_5_S)) {
			bonus += 3;
		}
		// 盟屋、旅馆内回血量增加 
		if (L1HouseLocation.isInHouse(_pc.getX(), _pc.getY(), (short)_pc.getMapId())) {
			bonus += 10;
		}
		if (_pc.getMapId() >= 16384 && _pc.getMapId() <= 25088) { // 旅馆
			bonus += 6;
		}
		// 盟屋、旅馆内回血量增加  end

		boolean inLifeStream = false;
		if (isPlayerInLifeStream(_pc)) {
			inLifeStream = true;
			// 古代空间、魔族神殿HPR+3？
			bonus += 3;
		}

		// 空腹重量
		if (_pc.get_food() < 24 || isOverWeight(_pc)
				|| _pc.hasSkillEffect(L1SkillId.BERSERKERS)) {
			bonus = 0;
			// 装备ＨＰＲ增加满腹度、重量、 减少场合满腹度、重量关系效果残
			if (equipHpr > 0) {
				equipHpr = 0;
			}
		}

		int newHp = _pc.getCurrentHp();
		newHp += bonus + equipHpr;
        newHp += getHprReductionByClan(_pc);
        
		if (newHp < 1) {
			newHp = 1; // ＨＰＲ减少装备死亡
		}
		// 水中减少处理
		// 减少不明
		if (isUnderwater(_pc)) {
			newHp -= 20;
			if (newHp < 1) {
				if (_pc.isGm()) {
					newHp = 1;
				} else {
					_pc.death(null); // 窒息ＨＰ０场合死亡。
				}
			}
		}
		// Lv50古代空间1F2F减少处理
		if (isLv50Quest(_pc) && !inLifeStream) {
			newHp -= 10;
			if (newHp < 1) {
				if (_pc.isGm()) {
					newHp = 1;
				} else {
					_pc.death(null); // ＨＰ０场合死亡。
				}
			}
		}
		// 魔族神殿减少理
		if (_pc.getMapId() == 410 && !inLifeStream) {
			newHp -= 10;
			if (newHp < 1) {
				if (_pc.isGm()) {
					newHp = 1;
				} else {
					_pc.death(null); // ＨＰ０场合死亡。
				}
			}
		}
		
		// 邪念地监扣血 
		if (_pc.getMapId() == 306 && !inLifeStream) {
			newHp -= 10;
			if (newHp < 1) {
				if (_pc.isGm()) {
					newHp = 1;
				} else {
					_pc.death(null); // ＨＰ０场合死亡。
				}
			}
		}
		// 邪念地监扣血  end

		if (!_pc.isDead()) {
			_pc.setCurrentHp(Math.min(newHp, _pc.getMaxHp()));
		}
	}
	
	/**
     * 血盟技能HPR增加
     * 
     * @return
     */
    private int getHprReductionByClan(final L1PcInstance pc) {
        int hp = 0;
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
                	hp += 1;
                }else if (clan.getSkillLevel() == 2) {
                	hp += 2;
                }else if (clan.getSkillLevel() == 3) {
                	hp += 3;
                }else if (clan.getSkillLevel() == 4) {
                	hp += 4;
                }else if (clan.getSkillLevel() == 5) {
                	hp += 5;
                }
            }

        } catch (final Exception e) {
            return 0;
        }
        return hp;
    }
	private boolean isUnderwater(L1PcInstance pc) {
		// 装备时、 祝福状态、水中无。
		if (pc.getInventory().checkEquipped(20207)) {
			return false;
		}
		if (pc.hasSkillEffect(L1SkillId.STATUS_UNDERWATER_BREATH)) {
			return false;
		}

		return pc.getMap().isUnderwater();
	}

	private boolean isOverWeight(L1PcInstance pc) {
		// 状态、状态
		// 装备时、重量无。
		if (pc.hasSkillEffect(L1SkillId.EXOTIC_VITALIZE)
				|| pc.hasSkillEffect(L1SkillId.ADDITIONAL_FIRE)) {
			return false;
		}
		if (pc.getInventory().checkEquipped(20049)) {
			return false;
		}

		return (120 <= pc.getInventory().getWeight240()) ? true : false;
	}

	private boolean isLv50Quest(L1PcInstance pc) {
		int mapId = pc.getMapId();
		return (mapId == 2000 || mapId == 2001) ? true : false;
	}

	/**
	 * 指定PC范
	 * 
	 * @param pc
	 *            PC
	 * @return true PC范场合
	 */
	private static boolean isPlayerInLifeStream(L1PcInstance pc) {
		for (L1Object object : pc.getKnownObjects()) {
			if (object instanceof L1EffectInstance == false) {
				continue;
			}
			L1EffectInstance effect = (L1EffectInstance) object;
			if (effect.getNpcId() == 81169 && effect.getLocation()
					.getTileLineDistance(pc.getLocation()) < 4) {
				return true;
			}
		}
		return false;
	}
}
