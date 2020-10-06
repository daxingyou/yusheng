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
package l1j.server.server.serverpackets;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import l1j.server.Config;
import l1j.server.server.Account;
import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.world.L1World;

/**
 * 遮断表示复数用途使
 */
public class S_PacketBox extends ServerBasePacket {
	private static final String S_PACKETBOX = "[S] S_PacketBox";

	// *** S_107 sub code list ***

	// 1:Kent 2:Orc 3:WW 4:Giran 5:Heine 6:Dwarf 7:Aden 8:Diad 9:城名9 ...
	/** C(id) H(?): %s攻城战始。 */
	public static final int MSG_WAR_BEGIN = 0;

	/** C(id) H(?): %s攻城战终了。 */
	public static final int MSG_WAR_END = 1;

	/** C(id) H(?): %s攻城战进行中。 */
	public static final int MSG_WAR_GOING = 2;

	/** -: 城主导权握。 (音乐变) */
	public static final int MSG_WAR_INITIATIVE = 3;

	/** -: 城占据。 */
	public static final int MSG_WAR_OCCUPY = 4;

	/** ?: 决斗终。 (音乐变) */
	public static final int MSG_DUEL = 5;

	/** C(count): SMS送信失败。 / 全部%d件送信。 */
	public static final int MSG_SMS_SENT = 6;

	/** -: 祝福中、2人夫妇结。 (音乐变) */
	public static final int MSG_MARRIED = 9;

	/** C(weight): 重量(30段阶) */
	public static final int WEIGHT = 10;

	/** C(food): 满腹度(30段阶) */
	public static final int FOOD = 11;

	/** C(0) C(level): %d以下使用。 (0~49以外表示) */
	public static final int MSG_LEVEL_OVER = 12;

	/** UB情报HTML */
	public static final int HTML_UB = 14;

	/**
	 * C(id)<br>
	 * 1:身迂精灵力空气中溶行感。<br>
	 * 2:体隅火精灵力染。<br>
	 * 3:体隅水精灵力染。<br>
	 * 4:体隅风精灵力染。<br>
	 * 5:体隅地精灵力染。<br>
	 */
	public static final int MSG_ELF = 15;

	/** C(count) S(name)...: 遮断复数追加 */
	public static final int ADD_EXCLUDE2 = 17;

	/** S(name): 遮断追加 */
	public static final int ADD_EXCLUDE = 18;

	/** S(name): 遮断解除 */
	public static final int REM_EXCLUDE = 19;

	/**  */
	public static final int ICONS1 = 20;

	/**  */
	public static final int ICONS2 = 21;

	/** 系 */
	public static final int ICON_AURA = 22;

	/** S(name): %s选。 */
	public static final int MSG_TOWN_LEADER = 23;

	/**
	 * C(id): %s变更。<br>
	 * id - 1:见习 2:一般 3:
	 */
	public static final int MSG_RANK_CHANGED = 27;

	/** D(?) S(name) S(clanname): %s血盟%s军退。 */
	public static final int MSG_WIN_LASTAVARD = 30;

	/** -: \f1气分良。 */
	public static final int MSG_FEEL_GOOD = 31;

	/** 不明。C_30飞 */
	public static final int SOMETHING1 = 33;

	/** H(time): 表示。 */
	public static final int ICON_BLUEPOTION = 34;

	/** H(time): 变身表示。 */
	public static final int ICON_POLYMORPH = 35;

	/** H(time): 禁止表示。 */
	public static final int ICON_CHATBAN = 36;

	/** 不明。C_7飞。C_7开飞。 */
	public static final int SOMETHING2 = 37;

	/** 血盟情报HTML表示 */
	public static final int HTML_CLAN1 = 38;

	/** H(time): 表示 */
	public static final int ICON_I2H = 40;

	/** 、情报送 */
	public static final int CHARACTER_CONFIG = 41;

	/** 选择画面戾 */
	public static final int LOGOUT = 42;

	/** 战斗中再始动。 */
	public static final int MSG_CANT_LOGOUT = 43;

	/**
	 * C(count) D(time) S(name) S(info):<br>
	 * [CALL] 表示。BOT不正者
	 * 使机能。名前C_RequestWho飞、
	 * bot_list.txt生成。名前选择+押新开。
	 */
	public static final int CALL_SOMETHING = 45;

	/**
	 * C(id):  、大战<br>
	 * id - 1:开始 2:取消 3:终了
	 */
	public static final int MSG_COLOSSEUM = 49;

	// 血盟情报HTML
	public static final int HTML_CLAN2 = 51;

	// 料理开
	public static final int COOK_WINDOW = 52;

	/** C(type) H(time): 料理表示 */
	public static final int ICON_COOKING = 53;
	
	/**魔法娃娃图示*/
	public static final int DOLL_ICON = 56;
	
	/** 慎重药水 */
	public static final int WISDOM_POTION = 57;
	
	public static final int VIP_ICON = 114;
	
	/** VIP显示图片 by:42621391 2014年8月20日09:04:01 */
	public static final int BAPO = 114; // 官器标惯

	public S_PacketBox(int subCode, int type, boolean show) {

		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(subCode);

		switch (subCode) {
		case VIP_ICON:
			writeD(type); // 1~7 标惯
			writeD(show ? 0x01 : 0x00);
		default:
			break;
		}
	}
	
	public S_PacketBox(int subCode) {
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(subCode);

		switch (subCode) {
		case LOGOUT:
	        this.writeC(0x00);
	        this.writeC(0x00);
	        this.writeC(0x00);
	        this.writeC(0x00);
	        this.writeC(0x00);
	        this.writeC(0x00);
			break;
		case MSG_WAR_INITIATIVE:
		case MSG_WAR_OCCUPY:
		case MSG_MARRIED:
		case MSG_FEEL_GOOD:
		case MSG_CANT_LOGOUT:
			break;
		case CALL_SOMETHING:
			callSomething();
		case COOK_WINDOW:
			writeC(0xdb); // ?
			writeC(0x31);
			writeC(0xdf);
			writeC(0x02);
			writeC(0x01);
			writeC(0x00);
			break;
		default:
			break;
		}
	}

	public S_PacketBox(int subCode, int value) {
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(subCode);

		switch (subCode) {
		case ICON_BLUEPOTION:
		case ICON_CHATBAN:
		case ICON_I2H:
		case ICON_POLYMORPH:
		case DOLL_ICON:
			writeH(value); // time
			break;
		case MSG_WAR_BEGIN:
		case MSG_WAR_END:
		case MSG_WAR_GOING:
			writeC(value); // castle id
			writeH(0); // ?
			break;
		case MSG_SMS_SENT:
		case WEIGHT:
		case FOOD:
			writeC(value);
			break;
		case MSG_ELF:
		case MSG_RANK_CHANGED:
		case MSG_COLOSSEUM:
			writeC(value); // msg id
			break;
		case MSG_DUEL: // 引数意味不明
			writeC(value);
			break;
		case MSG_LEVEL_OVER:
			writeC(0); // ?
			writeC(value); // 0-49以外表示
			break;
		case 88: // + 闪避率
            this.writeC(value);
            this.writeC(0x00);
            break;
		case 101: // - 闪避率
             this.writeC(value);
             break;
		default:
			break;
		}
	}
	
	public S_PacketBox(int subCode, int rank, String name) {
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(subCode);
		switch (subCode) {
		case MSG_RANK_CHANGED: // 你的階級變更為%s
			writeC(rank);
			writeS(name);
			break;
		}
	}

	public S_PacketBox(int subCode, int type, int time) {
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(subCode);

		switch (subCode) {
		case ICON_COOKING:
			if (type != 7) {
				writeC(0x0c);
				writeC(0x0c);
				writeC(0x0c);
				writeC(0x12);
				writeC(0x0c);
				writeC(0x09);
				writeC(0x00);
				writeC(0x00);
				writeC(type);
				writeC(0x24);
				writeH(time);
				writeH(0x00);
			} else {
				writeC(0x0c);
				writeC(0x0c);
				writeC(0x0c);
				writeC(0x12);
				writeC(0x0c);
				writeC(0x09);
				writeC(0xc8);
				writeC(0x00);
				writeC(type);
				writeC(0x26);
				writeH(time);
				writeC(0x3e);
				writeC(0x87);
			}
			break;
		case WISDOM_POTION:
			this.writeC(type);// 44
			this.writeH(time);
			break;
		case MSG_DUEL:
			this.writeD(type); // 相手のオブジェクトID
			this.writeD(time); // 自分のオブジェクトID
			break;
		default:
			break;
		}
	}

	public S_PacketBox(int subCode, String name) {
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(subCode);

		switch (subCode) {
		case ADD_EXCLUDE:
		case REM_EXCLUDE:
		case MSG_TOWN_LEADER:
			writeS(name);
			break;
		default:
			break;
		}
	}

	public S_PacketBox(int subCode, int id, String name, String clanName) {
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(subCode);

		switch (subCode) {
		case MSG_WIN_LASTAVARD:
			writeD(id); // ID何？
			writeS(name);
			writeS(clanName);
			break;
		default:
			break;
		}
	}

	public S_PacketBox(int subCode, Object[] names) {
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(subCode);

		switch (subCode) {
		case ADD_EXCLUDE2:
			writeC(names.length);
			for (Object name : names) {
				writeS(name.toString());
			}
			break;
		default:
			break;
		}
	}

	private void callSomething() {
		Iterator<L1PcInstance> itr = L1World.getInstance().getAllPlayers().iterator();

		final List<L1PcInstance> listpc = new ArrayList<L1PcInstance>();
		while (itr.hasNext()) {
			final L1PcInstance tagerpc = itr.next();
			if (tagerpc.getNetConnection() != null){
				listpc.add(tagerpc);
			}
		}
		writeC(listpc.size() + 1);

		for (final L1PcInstance pc : listpc) {
			final Account acc = Account.load(pc.getAccountName());
			// 时间情报 时间入
			if (acc == null) {
				writeD(0);
			} else {
				Calendar cal = Calendar
						.getInstance(TimeZone.getTimeZone(Config.TIME_ZONE));
				long lastactive = acc.getLastActive().getTime();
				cal.setTimeInMillis(lastactive);
				cal.set(Calendar.YEAR, 1970);
				int time = (int) (cal.getTimeInMillis() / 1000);
				writeD(time); // JST 1970 1/1 09:00 基准
			}
			// 情报
			writeS(pc.getName()); // 半角12字
			writeS(String.valueOf(pc.getLevel())); // []内表示文字列。半角12字
		}
		
		writeD(0);
		writeS("当前实际在线人数:");
		writeS(String.valueOf(listpc.size()));
		
		listpc.clear();
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	public String getType() {
		return S_PACKETBOX;
	}
}
