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

import l1j.gui.J_Main;
import l1j.server.Config;
import l1j.server.server.Opcodes;
import l1j.server.server.datatables.ChatLogTable;
import l1j.server.server.datatables.ChatObsceneTable;
import l1j.server.server.mina.LineageClient;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.world.L1World;
import l1j.william.L1WilliamSystemMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_ChatWhisper extends ClientBasePacket {

	private static final String C_CHAT_WHISPER = "[C] C_ChatWhisper";
	private static final Log _log = LogFactory.getLog(C_ChatWhisper.class);

	public C_ChatWhisper(byte abyte0[], LineageClient _client)
			throws Exception {
		super(abyte0);
		String targetName = readS();
		String text = readS();
		L1PcInstance whisperFrom = _client.getActiveChar();
		// 禁止中场合
		if (whisperFrom.hasSkillEffect(L1SkillId.SILENCE)
				|| whisperFrom.hasSkillEffect(L1SkillId.AREA_OF_SILENCE)
				|| whisperFrom.hasSkillEffect(L1SkillId.STATUS_POISON_SILENCE)) {
			return;
		}
		if (whisperFrom.hasSkillEffect(1005)) {
			whisperFrom.sendPackets(new S_ServerMessage(242)); // 现在禁止中。
			return;
		}
		if (abyte0.length > 80) {
			_log.info("人物:" + whisperFrom.getName() + "对话(密语)长度超过限制:" + _client.getIp().toString());
			//client.set_error(client.get_error() + 1);
			return;
		}
		L1PcInstance whisperTo = L1World.getInstance().getPlayer(targetName);
		// 场合
		if (whisperTo == null) {
			final String newName = L1World.getInstance().getNewName(targetName);
			whisperTo = L1World.getInstance().getPlayer(newName);
		}
		if (whisperTo == null) {
			whisperFrom.sendPackets(new S_ServerMessage(73, targetName)); // \f1%0。
			return;
		}
		// 自分自身对wis场合
		if (whisperTo.equals(whisperFrom)) {
			return;
		}
		// 遮断场合
		if (whisperTo.excludes(whisperFrom.getName())) {
			whisperFrom.sendPackets(new S_ServerMessage(117,
					whisperTo.getName())); // %0遮断。
			return;
		}
		// \f1%0%d 目前关闭悄悄话。
		if (!whisperTo.isCanWhisper()) {
			whisperFrom.sendPackets(new S_ServerMessage(205, whisperTo
					.getName()));
			return;
		}

		text = ChatObsceneTable.getInstance().getObsceneText(text);
		
		ChatLogTable.getInstance().storeChat(whisperFrom, whisperTo, text, 1);
		whisperFrom.sendPackets(new S_ChatPacket(whisperTo, text,
				Opcodes.S_OPCODE_GLOBALCHAT, 9));
		whisperTo.sendPackets(new S_ChatPacket(whisperFrom, text,
				Opcodes.S_OPCODE_WHISPERCHAT, 16));
		// GM偷听功能 
		if (Config.GM_OVERHEARD) {
			for (L1Object visible : L1World.getInstance().getAllPlayers()) {
				if (visible instanceof L1PcInstance) { 
            		L1PcInstance GM = (L1PcInstance) visible; 
            		if (GM.isGm() && whisperFrom.getId() != GM.getId()) {
            			GM.sendPackets(new S_ServerMessage(166, L1WilliamSystemMessage.ShowMessage(1116), " ( " + whisperFrom.getName() + " ) :‘ ", text + " ’"));
            		}
				}
			}
		}
		J_Main.getInstance().addPrivateChat(whisperFrom.getName(),whisperTo.getName(), text);//GUI
		// GM偷听功能  end
		if (!whisperFrom.isGm()) {
			whisperFrom.checkWhisperChatInterval();
		}
	}

	@Override
	public String getType() {
		return C_CHAT_WHISPER;
	}
}
