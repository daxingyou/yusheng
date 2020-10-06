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

package l1j.server.server.clientpackets;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import l1j.server.server.BadNamesList;
import l1j.server.server.IdFactory;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.PcTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.datatables.lock.CharSkillReading;
import l1j.server.server.mina.LineageClient;
import l1j.server.server.model.Beginner;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_CharCreateStatus;
import l1j.server.server.serverpackets.S_NewCharPacket;
import l1j.server.server.templates.L1Skills;
import l1j.server.server.utils.CalcInitHpMp;
import l1j.william.BadNamesTable; // 无法创造的名称 

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//语言 

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_CreateChar extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_CreateChar.class);
	private static final String C_CREATE_CHAR = "[C] C_CreateChar";
	
	// 各職業初始化屬性(王族, 騎士, 精靈, 法師, 黑妖)
	public static final int[] ORIGINAL_STR = new int[] { 13, 16, 11, 8, 12, 13, 11 };
	public static final int[] ORIGINAL_DEX = new int[] { 10, 12, 12, 7, 15, 11, 10 };
	public static final int[] ORIGINAL_CON = new int[] { 10, 14, 12, 12, 8, 14, 12 };
	public static final int[] ORIGINAL_WIS = new int[] { 11, 9, 12, 12, 10, 12, 12 };
	public static final int[] ORIGINAL_CHA = new int[] { 13, 12, 9, 8, 9, 8, 8 };
	public static final int[] ORIGINAL_INT = new int[] { 10, 8, 12, 12, 11, 11, 12 };
	// 各职业初始化可分配点数(王族, 骑士, 精灵, 法师, 黑妖, 龙骑士, 幻术师)
	public static final int[] ORIGINAL_AMOUNT = new int[] { 8, 4, 7, 16, 10, 6, 10 };

	public C_CreateChar(byte[] abyte0, LineageClient _client)
			throws Exception {
		super(abyte0);
		final L1PcInstance pc = new L1PcInstance();
		String name = readS();

		/*删除if (isInvalidName(name)) {
			S_CharCreateStatus s_charcreatestatus = new S_CharCreateStatus(
					S_CharCreateStatus.REASON_INVALID_NAME);
			client.sendPacket(s_charcreatestatus);
			return;
		}删除*/
		if (name.isEmpty()) {
			return;
		}
		// 修正 
		if (!isInvalidName(name,_client.getLanguage())) {
			S_CharCreateStatus s_charcreatestatus = new S_CharCreateStatus(S_CharCreateStatus.REASON_INVALID_NAME);
			_client.sendPacket(s_charcreatestatus);
			return;
		}
		// 修正  end

		if (CharacterTable.doesCharNameExist(name)) {
			_log.info("charname: " + pc.getName() + " already exists. creation failed.");
			S_CharCreateStatus s_charcreatestatus1 = new S_CharCreateStatus(S_CharCreateStatus.REASON_ALREADY_EXSISTS);
			_client.sendPacket(s_charcreatestatus1);
			return;
		}

		// 无法创造的名称 
		if (BadNamesTable.doesCharNameExist(name)) {
			S_CharCreateStatus s_charcreatestatus = new S_CharCreateStatus(S_CharCreateStatus.REASON_INVALID_NAME);
			_client.sendPacket(s_charcreatestatus);
			return;
		}
		// 无法创造的名称 

		if (_client.getAccount().countCharacters() >= 8) {
			_log.info("account: " + _client.getAccountName()
					+ " 4超作成要求。");
			S_CharCreateStatus s_charcreatestatus1 = new S_CharCreateStatus(
					S_CharCreateStatus.REASON_WRONG_AMOUNT);
			_client.sendPacket(s_charcreatestatus1);
			return;
		}

		pc.setName(name);
		pc.setType(readC());
		pc.set_sex(readC());
		pc.addBaseStr((byte) readC());
		pc.addBaseDex((byte) readC());
		pc.addBaseCon((byte) readC());
		pc.addBaseWis((byte) readC());
		pc.addBaseCha((byte) readC());
		pc.addBaseInt((byte) readC());
		if (pc.getType() >= 5) {
			_log.info("幻术暂时未开放，阻止创建成功！");
			S_CharCreateStatus s_charcreatestatus1 = new S_CharCreateStatus(
					S_CharCreateStatus.REASON_WRONG_AMOUNT);
			_client.sendPacket(s_charcreatestatus1);
			return;
		}
		
		boolean isStatusError = false;
		final int originalStr = ORIGINAL_STR[pc.getType()];
		final int originalDex = ORIGINAL_DEX[pc.getType()];
		final int originalCon = ORIGINAL_CON[pc.getType()];
		final int originalWis = ORIGINAL_WIS[pc.getType()];
		final int originalCha = ORIGINAL_CHA[pc.getType()];
		final int originalInt = ORIGINAL_INT[pc.getType()];
		final int originalAmount = ORIGINAL_AMOUNT[pc.getType()];

		if (((pc.getBaseStr() < originalStr)
				|| (pc.getBaseDex() < originalDex)
				|| (pc.getBaseCon() < originalCon)
				|| (pc.getBaseWis() < originalWis)
				|| (pc.getBaseCha() < originalCha) || (pc.getBaseInt() < originalInt))
				|| ((pc.getBaseStr() > originalStr + originalAmount)
						|| (pc.getBaseDex() > originalDex + originalAmount)
						|| (pc.getBaseCon() > originalCon + originalAmount)
						|| (pc.getBaseWis() > originalWis + originalAmount)
						|| (pc.getBaseCha() > originalCha + originalAmount) || (pc
						.getBaseInt() > originalInt + originalAmount))) {
			isStatusError = true;
		}

		int statusAmount = pc.getDex() + pc.getCha() + pc.getCon()
				+ pc.getInt() + pc.getStr() + pc.getWis();

		if (statusAmount != 75||isStatusError) {
			_log.info("Character have wrong value");
			S_CharCreateStatus s_charcreatestatus3 = new S_CharCreateStatus(
					S_CharCreateStatus.REASON_WRONG_AMOUNT);
			_client.sendPacket(s_charcreatestatus3);
			return;
		}
	
		S_CharCreateStatus s_charcreatestatus2 = new S_CharCreateStatus(S_CharCreateStatus.REASON_OK);
		_client.sendPacket(s_charcreatestatus2);
		initNewChar(_client, pc);
		_log.info("charname: " + pc.getName() + " classId: "
				+ pc.getClassId());
	}

	private static final int[] MALE_LIST = new int[] { 0, 61, 138, 734, 2786, 6658, 6671 };
	private static final int[] FEMALE_LIST = new int[] { 1, 48, 37, 1186, 2796, 6661, 6650 };
	// 删除private static final int[] LOCX_LIST = new int[] { 32734, 32734, 32734, 32734, 32734 };
	// 删除private static final int[] LOCY_LIST = new int[] { 32798, 32798, 32798, 32798, 32798 };
	// 删除private static final short[] MAPID_LIST = new short[] { 8013, 8013, 8013, 8013, 8013 };
	// 起始座标改回台版 32578 32928
/*	private static final int[] LOCX_LIST = new int[] { 32780, 32714, 33043, 32780, 32924 };
	private static final int[] LOCY_LIST = new int[] { 32781, 32877, 32336, 32781, 32799 };
	private static final short[] MAPID_LIST = new short[] { 68, 69, 4, 68, 304 };*/
	// 起始座标改回台版  end
	private static final int[] LOCX_LIST = new int[] { 32578, 32578, 32578, 32578, 32578, 32578, 32578 };
	private static final int[] LOCY_LIST = new int[] { 32928, 32928, 32928, 32928, 32928, 32928, 32928 };
	private static final short[] MAPID_LIST = new short[] { 0, 0, 0, 0, 0, 0, 0 };

	private static void initNewChar(LineageClient _client, L1PcInstance pc)
			throws IOException, Exception {

		int init_hp = 0;
		int init_mp = 0;

		pc.setId(IdFactory.getInstance().nextId());
		if (pc.get_sex() == 0) {
			pc.setClassId(MALE_LIST[pc.getType()]);
		} else {
			pc.setClassId(FEMALE_LIST[pc.getType()]);
		}
		pc.setX(LOCX_LIST[pc.getType()]);
		pc.setY(LOCY_LIST[pc.getType()]);
		pc.setMap(MAPID_LIST[pc.getType()]);
		pc.setHeading(0);
		pc.setLawful(0);
/*		if (pc.isCrown()) { // 君主
			init_hp = 14;
			switch (pc.getWis()) {
			case 11:
				init_mp = 2;
				break;
			case 12:
			case 13:
			case 14:
			case 15:
				init_mp = 3;
				break;
			case 16:
			case 17:
			case 18:
				init_mp = 4;
				break;
			default:
				init_mp = 2;
				break;
			}
		} else if (pc.isKnight()) { // 
			init_hp = 16;
			switch (pc.getWis()) {
			case 9:
			case 10:
			case 11:
				init_mp = 1;
				break;
			case 12:
			case 13:
				init_mp = 2;
				break;
			default:
				init_mp = 1;
				break;
			}
		} else if (pc.isElf()) { // 
			init_hp = 15;
			switch (pc.getWis()) {
			case 12:
			case 13:
			case 14:
			case 15:
				init_mp = 4;
				break;
			case 16:
			case 17:
			case 18:
				init_mp = 6;
				break;
			default:
				init_mp = 4;
				break;
			}
		} else if (pc.isWizard()) { // WIZ
			init_hp = 12;
			switch (pc.getWis()) {
			case 12:
			case 13:
			case 14:
			case 15:
				init_mp = 6;
				break;
			case 16:
			case 17:
			case 18:
				init_mp = 8;
				break;
			default:
				init_mp = 6;
				break;
			}
		} else if (pc.isDarkelf()) { // DE
			init_hp = 12;
			switch (pc.getWis()) {
			case 10:
			case 11:
				init_mp = 3;
				break;
			case 12:
			case 13:
			case 14:
			case 15:
				init_mp = 4;
				break;
			case 16:
			case 17:
			case 18:
				init_mp = 6;
				break;
			default:
				init_mp = 3;
				break;
			}
		}*/
		init_hp = CalcInitHpMp.calcInitHp(pc);
		init_mp = CalcInitHpMp.calcInitMp(pc);
		pc.addBaseMaxHp(init_hp);
		pc.setCurrentHp(init_hp);
		pc.addBaseMaxMp(init_mp);
		pc.setCurrentMp(init_mp);
		pc.resetBaseAc();
		pc.setTitle("");
		pc.setClanid(0);
		pc.setClanRank(0);
		pc.set_food(40);
		pc.setAccessLevel((short) 0);
		pc.setGm(false);
		pc.setMonitor(false);
		pc.setGmInvis(false);
		pc.setExp(0);
		pc.setStatus(0);
		pc.setAccessLevel((short) 0);
		pc.setClanname("");
		pc.setBonusStats(0);
		pc.setElixirStats(0);
		pc.resetBaseMr();
		pc.setElfAttr(0);
		pc.set_PKcount(0);
		pc.setExpRes(0);
		pc.setPartnerId(0);
		pc.setOnlineStatus(0);
		pc.setHomeTownId(0);
		pc.setContribution(0);
		pc.setBanned(false);
		pc.setKarma(0);
		if (pc.isWizard()) { // WIZ
			//pc.sendPackets(new S_AddSkill(pc,4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					//0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,0,0,0,0));
			int object_id = pc.getId();
			L1Skills l1skills = SkillsTable.getInstance().getTemplate(4); // EB
			String skill_name = l1skills.getName();
			int skill_id = l1skills.getSkillId();
			CharSkillReading.get().spellMastery(object_id, skill_id, skill_name, 0, 0);
		}
		Beginner.getInstance().GiveItem(pc);
		pc.setAccountName(_client.getAccountName());
		CharacterTable.getInstance().storeNewCharacter(pc);
		PcTable.getInstance().createPc(pc.getAccountName(), pc.getId());
		final S_NewCharPacket s_newcharpacket = new S_NewCharPacket(pc);
		_client.sendPacket(s_newcharpacket);
	}

	private static boolean isAlphaNumeric(String s) {
		boolean flag = true;
		char ac[] = s.toCharArray();
		int i = 0;
		do {
			if (i >= ac.length) {
				break;
			}
			if (!Character.isLetterOrDigit(ac[i])) {
				flag = false;
				break;
			}
			i++;
		} while (true);
		return flag;
	}

	public static boolean isInvalidName(String name,int language) {
		int numOfNameBytes = 0;
		try {
			numOfNameBytes = name.getBytes(LANGUAGE_CODE[language]).length; // 改成Config.LANGUAGE 
		} catch (UnsupportedEncodingException e) {
			_log.error(e.getLocalizedMessage(), e);
			return false;
		}

		/*删除if (isAlphaNumeric(name)) {
			return false;
		}删除*/
		// 修正 
		if (!isAlphaNumeric(name)) {
			return false;
		}
		// 修正  end

		// XXX - 本鲭仕样同等未确认
		// 全角文字5文字超、全体12超无效名前
		if (5 < (numOfNameBytes - name.length()) || 12 < numOfNameBytes) {
			return false;
		}

		if (BadNamesList.getInstance().isBadName(name)) {
			return false;
		}
		return true;
	}

	@Override
	public String getType() {
		return C_CREATE_CHAR;
	}
}
