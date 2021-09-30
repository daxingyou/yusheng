package l1j.server.server.model;

import static l1j.server.server.model.skill.L1SkillId.AWAKEN_ANTHARAS;
import static l1j.server.server.model.skill.L1SkillId.AWAKEN_FAFURION;
import static l1j.server.server.model.skill.L1SkillId.AWAKEN_VALAKAS;

import java.util.ArrayList;
import java.util.StringTokenizer;

import l1j.server.server.datatables.ArmorSetTable;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.templates.L1ArmorSets;

public abstract class L1ArmorSet {
	public abstract void giveEffect(L1PcInstance pc);

	public abstract void cancelEffect(L1PcInstance pc);

	public abstract boolean isValid(L1PcInstance pc);

	public abstract boolean isPartOfSet(int id);

	public static ArrayList<L1ArmorSet> getAllSet() {
		return _allSet;
	}

	private static ArrayList<L1ArmorSet> _allSet = new ArrayList<L1ArmorSet>();

	private static int[] getArray(String s, String sToken) {
		final StringTokenizer st = new StringTokenizer(s, sToken);
		int size = st.countTokens();
		String temp = null;
		int[] array = new int[size];
		for (int i = 0; i < size; i++) {
			temp = st.nextToken();
			array[i] = Integer.parseInt(temp);
		}
		return array;
	}

	/*
	 * 初期化美气
	 */
	static {
		for (final L1ArmorSets armorset : ArmorSetTable.getInstance()
				.getAllList()) {
			final L1ArmorSetImpl impl = new L1ArmorSetImpl(getArray(
					armorset.getSets(), ","));

			if (armorset.getPolyId1() != -1) {
				impl.addEffect(new PolymorphEffect(armorset.getPolyId0(),
						armorset.getPolyId1(), armorset.getPolyId2(), armorset
								.getPolyId3(), armorset.getPolyId4()));
			}

			impl.addEffect(new AcHpMpBonusEffect(armorset.getAc(), armorset
					.getHp(), armorset.getMp(), armorset.getHpr(), armorset
					.getMpr(), armorset.getMr()));
			impl.addEffect(new StatBonusEffect(armorset.getStr(), armorset
					.getDex(), armorset.getCon(), armorset.getWis(), armorset
					.getCha(), armorset.getIntl()));
			impl.addEffect(new DefenseBonusEffect(armorset.getDefenseFire(),
					armorset.getDefenseWind(), armorset.getDefenseWater(),
					armorset.getDefenseEarth()));
			impl.addEffect(new ResistBonusEffect(armorset.getRegistFreeze(),
					armorset.getRegistSleep(), armorset.getRegistStan(),
					armorset.getRegistStone()));
			impl.addEffect(new HitDmgModifierEffect(armorset.getHitModifier(),
					armorset.getDmgModifier(), armorset.getBowHitModifier(),
					armorset.getBowDmgModifier(), armorset.getSp()));

			_allSet.add(impl);
		}
	}
}

interface L1ArmorSetEffect {
	public void giveEffect(L1PcInstance pc);

	public void cancelEffect(L1PcInstance pc);
}

class L1ArmorSetImpl extends L1ArmorSet {
	private final int _ids[];
	private final ArrayList<L1ArmorSetEffect> _effects;

	protected L1ArmorSetImpl(int ids[]) {
		_ids = ids;
		_effects = new ArrayList<L1ArmorSetEffect>();
	}

	public void addEffect(L1ArmorSetEffect effect) {
		_effects.add(effect);
	}

	public void removeEffect(L1ArmorSetEffect effect) {
		_effects.remove(effect);
	}

	@Override
	public void cancelEffect(L1PcInstance pc) {
		for (L1ArmorSetEffect effect : _effects) {
			effect.cancelEffect(pc);
		}
	}

	@Override
	public void giveEffect(L1PcInstance pc) {
		for (L1ArmorSetEffect effect : _effects) {
			effect.giveEffect(pc);
		}
	}

	@Override
	public final boolean isValid(L1PcInstance pc) {
		return pc.getInventory().checkEquipped(_ids);
	}

	@Override
	public boolean isPartOfSet(int id) {
		for (int i : _ids) {
			if (id == i) {
				return true;
			}
		}
		return false;
	}
}

class AcHpMpBonusEffect implements L1ArmorSetEffect {
	private final int _ac;
	private final int _addHp;
	private final int _addMp;
	private final int _regenHp;
	private final int _regenMp;
	private final int _addMr;

	public AcHpMpBonusEffect(int ac, int addHp, int addMp, int regenHp,
			int regenMp, int addMr) {
		_ac = ac;
		_addHp = addHp;
		_addMp = addMp;
		_regenHp = regenHp;
		_regenMp = regenMp;
		_addMr = addMr;
	}

	@Override
	public void giveEffect(L1PcInstance pc) {
		pc.addAc(_ac);
		pc.addMaxHp(_addHp);
		pc.addMaxMp(_addMp);
		pc.addHpr(_regenHp);
		pc.addMpr(_regenMp);
		pc.addMr(_addMr);
	}

	@Override
	public void cancelEffect(L1PcInstance pc) {
		pc.addAc(-_ac);
		pc.addMaxHp(-_addHp);
		pc.addMaxMp(-_addMp);
		pc.addHpr(-_regenHp);
		pc.addMpr(-_regenMp);
		pc.addMr(-_addMr);
	}
}

class StatBonusEffect implements L1ArmorSetEffect {
	private final int _str;
	private final int _dex;
	private final int _con;
	private final int _wis;
	private final int _cha;
	private final int _intl;

	public StatBonusEffect(int str, int dex, int con, int wis, int cha, int intl) {
		_str = str;
		_dex = dex;
		_con = con;
		_wis = wis;
		_cha = cha;
		_intl = intl;
	}

	@Override
	public void giveEffect(L1PcInstance pc) {
		pc.addStr((byte) _str);
		pc.addDex((byte) _dex);
		pc.addCon((byte) _con);
		pc.addWis((byte) _wis);
		pc.addCha((byte) _cha);
		pc.addInt((byte) _intl);
	}

	@Override
	public void cancelEffect(L1PcInstance pc) {
		pc.addStr((byte) -_str);
		pc.addDex((byte) -_dex);
		pc.addCon((byte) -_con);
		pc.addWis((byte) -_wis);
		pc.addCha((byte) -_cha);
		pc.addInt((byte) -_intl);
	}
}

class PolymorphEffect implements L1ArmorSetEffect {
	private final int _gfxId0;
	private final int _gfxId1;
	private final int _gfxId2;
	private final int _gfxId3;
	private final int _gfxId4;

	public PolymorphEffect(final int gfxId0, final int gfxId1,
			final int gfxId2, final int gfxId3, final int gfxId4) {
		_gfxId0 = gfxId0;
		_gfxId1 = gfxId1;
		_gfxId2 = gfxId2;
		_gfxId3 = gfxId3;
		_gfxId4 = gfxId4;
	}

	@Override
	public void giveEffect(L1PcInstance pc) {
		// System.out.println("步骤1"+_gfxId);
		int newgfxid = _gfxId0;
		if (_gfxId0 == 6080) {
			// System.out.println("步骤2"+_gfxId);
			if (pc.get_sex() == 0) {
				newgfxid = 6094;
				// System.out.println("步骤3"+_gfxId);
			}
			if (!isRemainderOfCharge(pc)) { // 
				return;
			}
		}
		if (_gfxId1 != -1) {
			if (pc.getLevel() >= 55) {
				newgfxid = _gfxId1;
			}
		}
		if (_gfxId2 != -1) {
			if (pc.getLevel() >= 60) {
				newgfxid = _gfxId2;
			}
		}
		if (_gfxId3 != -1) {
			if (pc.getLevel() >= 65) {
				newgfxid = _gfxId3;
			}
		}
		if (_gfxId4 != -1) {
			if (pc.getLevel() >= 70) {
				newgfxid = _gfxId4;
			}
		}
		// 钓鱼岛不能变身
		if (pc.getMapId() == 5124) {
			return;
		}
		// 钓鱼岛不能变身 end
		// System.out.println("步骤4"+_gfxId);
		L1PolyMorph.doPoly(pc, newgfxid, 0);

		// 判断变身套装无法使用变卷
		pc.setSkillEffect(l1j.william.New_Id.Skill_AJ_0_10, 0);
		// 判断变身套装无法使用变卷 end
	}

	@Override
	public void cancelEffect(L1PcInstance pc) {
		// 龙骑士觉醒后 套装 无法变身
		final int awakeSkillId = pc.getAwakeSkillId();
		if ((awakeSkillId == AWAKEN_ANTHARAS)
				|| (awakeSkillId == AWAKEN_FAFURION)
				|| (awakeSkillId == AWAKEN_VALAKAS)) {
			final S_ServerMessage msg = new S_ServerMessage(1384);
			pc.sendPackets(msg); // 目前状态中无法变身。
			return;
		}
		// 龙骑士觉醒后 套装 无法变身
		int newgfxid = _gfxId0;
		if (_gfxId0 == 6080) {
			if (pc.get_sex() == 0) {
				newgfxid = 6094;
			}
		}
		if (_gfxId1 != -1) {
			if (pc.getLevel() >= 55) {
				newgfxid = _gfxId1;
			}
		}
		if (_gfxId2 != -1) {
			if (pc.getLevel() >= 60) {
				newgfxid = _gfxId2;
			}
		}
		if (_gfxId3 != -1) {
			if (pc.getLevel() >= 65) {
				newgfxid = _gfxId3;
			}
		}
		if (_gfxId4 != -1) {
			if (pc.getLevel() >= 70) {
				newgfxid = _gfxId4;
			}
		}
		if (pc.getTempCharGfx() != newgfxid) {
			return;
		}
		// 钓鱼岛不能变身
		if (pc.getMapId() == 5124) {
			return;
		}
		// 钓鱼岛不能变身 end

		L1PolyMorph.undoPoly(pc);

		// 判断变身套装无法使用变卷
		pc.killSkillEffectTimer(l1j.william.New_Id.Skill_AJ_0_10);
		// 判断变身套装无法使用变卷 end
	}

	private boolean isRemainderOfCharge(L1PcInstance pc) {
		boolean isRemainderOfCharge = false;
		if (pc.getInventory().checkItem(20383, 1)) {
			L1ItemInstance item = pc.getInventory().findItemId(20383);
			if (item != null) {
				if (item.getChargeCount() != 0) {
					isRemainderOfCharge = true;
				}
			}
		}
		return isRemainderOfCharge;
	}
}

// 加入 william功能
class ResistBonusEffect implements L1ArmorSetEffect {
	private final int _freeze;
	private final int _sleep;
	private final int _stan;
	private final int _stone;

	public ResistBonusEffect(int freeze, int sleep, int stan, int stone) {
		_freeze = freeze;
		_sleep = sleep;
		_stan = stan;
		_stone = stone;

	}

	@Override
	public void giveEffect(L1PcInstance pc) {
		pc.addRegistFreeze(_freeze);
		pc.addRegistSleep(_sleep);
		pc.addRegistStan(_stan);
		pc.addRegistStone(_stone);
	}

	@Override
	public void cancelEffect(L1PcInstance pc) {
		pc.addRegistFreeze(-_freeze);
		pc.addRegistSleep(-_sleep);
		pc.addRegistStan(-_stan);
		pc.addRegistStone(-_stone);
	}
}

class HitDmgModifierEffect implements L1ArmorSetEffect {
	private final int _hitModifier;

	private final int _dmgModifier;

	private final int _bowHitModifier;

	private final int _bowDmgModifier;

	private final int _sp;

	public HitDmgModifierEffect(int hitModifier, int dmgModifier,
			int bowHitModifier, int bowDmgModifier, int sp) {
		_hitModifier = hitModifier;
		_dmgModifier = dmgModifier;
		_bowHitModifier = bowHitModifier;
		_bowDmgModifier = bowDmgModifier;
		_sp = sp;
	}

	// @Override
	@Override
	public void giveEffect(L1PcInstance pc) {
		pc.addHitModifierByArmor(_hitModifier);
		pc.addDmgModifierByArmor(_dmgModifier);
		pc.addBowHitModifierByArmor(_bowHitModifier);
		pc.addBowDmgModifierByArmor(_bowDmgModifier);
		pc.addSp(_sp);
	}

	// @Override
	@Override
	public void cancelEffect(L1PcInstance pc) {
		pc.addHitModifierByArmor(-_hitModifier);
		pc.addDmgModifierByArmor(-_dmgModifier);
		pc.addBowHitModifierByArmor(-_bowHitModifier);
		pc.addBowDmgModifierByArmor(-_bowDmgModifier);
		pc.addSp(-_sp);
	}
}

class DefenseBonusEffect implements L1ArmorSetEffect {
	private final int _defense_fire;
	private final int _defense_wind;
	private final int _defense_water;
	private final int _defense_earth;

	public DefenseBonusEffect(int defense_fire, int defense_wind,
			int defense_water, int defense_earth) {
		_defense_fire = defense_fire;
		_defense_wind = defense_wind;
		_defense_water = defense_water;
		_defense_earth = defense_earth;
	}

	@Override
	public void giveEffect(L1PcInstance pc) {
		pc.addFire(_defense_fire);
		pc.addWind(_defense_wind);
		pc.addWater(_defense_water);
		pc.addEarth(_defense_earth);
	}

	@Override
	public void cancelEffect(L1PcInstance pc) {
		pc.addFire(-_defense_fire);
		pc.addWind(-_defense_wind);
		pc.addWater(-_defense_water);
		pc.addEarth(-_defense_earth);
	}
}
// 加入 william功能 end
