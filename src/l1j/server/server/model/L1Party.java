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

import java.util.ArrayList;
import java.util.List;

import l1j.server.Config;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_HPMeter;
import l1j.server.server.serverpackets.S_PacketBoxParty;
import l1j.server.server.serverpackets.S_ServerMessage;

// Referenced classes of package l1j.server.server.model:
// L1Party

public class L1Party {

	private final List<L1PcInstance> _membersList = new ArrayList<L1PcInstance>();

	private L1PcInstance _leader = null;
	
	private boolean _isLeader = false;// 隊長已經加入

	public void addMember(L1PcInstance pc) {
		boolean hpUpdate = false;
		if (pc == null) {
			throw new NullPointerException();
		}
		if (_membersList.size() == Config.MAX_PT && !_leader.isGm()
				|| _membersList.contains(pc)) {
			return;
		}
		if (_membersList.isEmpty()) {
			// 最初PT
			setLeader(pc);
		} else {
			hpUpdate = true;
			//createMiniHp(pc);
		}
		_membersList.add(pc);
		pc.setParty(this);
		for (L1PcInstance member : this._membersList) {
			if (!member.equals(this._leader)) {
				member.sendPackets(new S_PacketBoxParty(member, this));

			} else {
				if (_isLeader) {
					member.sendPackets(new S_PacketBoxParty(pc));
				}
			}
			// 隊伍UI更新
			member.sendPackets(new S_PacketBoxParty(this, member));
		}

		if (!_isLeader) {
			_isLeader = true;
		}
		if (hpUpdate) {
			createMiniHp(pc);
		}
	}

	private void removeMember(L1PcInstance pc) {
		if (!_membersList.contains(pc)) {
			return;
		}
		_membersList.remove(pc);
		pc.setParty(null);
		if (!_membersList.isEmpty()) {
			deleteMiniHp(pc);
		}
	}

	public boolean isVacancy() {
		return _membersList.size() < Config.MAX_PT;
	}

	public int getVacancy() {
		return Config.MAX_PT - _membersList.size();
	}

	public boolean isMember(L1PcInstance pc) {
		return _membersList.contains(pc);
	}

	private void setLeader(L1PcInstance pc) {
		_leader = pc;
	}
	
/*	public void ChangeLeader(L1PcInstance pc)
	{
		_leader = pc;
		msgToAll();
		updateparty(pc);
	}*/
	

	/*
	private void updateparty(L1PcInstance pc)
	{
		for (L1PcInstance member : _membersList) {	
			member.sendPackets(new S_PacketBoxParty(pc));
		}	
	}*/
	
	public L1PcInstance getLeader() {
		return _leader;
	}

	public boolean isLeader(L1PcInstance pc) {
		return pc.getId() == _leader.getId();
	}

	public String getMembersNameList() {
		String _result = new String("");
		for (L1PcInstance pc : _membersList) {
			_result = _result + pc.getName() + " ";
		}
		return _result;
	}

	private void createMiniHp(L1PcInstance pc) {
		// 加入时、相互HP表示
		L1PcInstance[] members = getMembers();

		for (L1PcInstance member : members) {
			member.sendPackets(new S_HPMeter(pc.getId(), 100
					* pc.getCurrentHp() / pc.getMaxHp()));
			pc.sendPackets(new S_HPMeter(member.getId(), 100
					* member.getCurrentHp() / member.getMaxHp()));
		}
	}

	private void deleteMiniHp(L1PcInstance pc) {
		// 离脱时、HP削除。
		L1PcInstance[] members = getMembers();

		for (L1PcInstance member : members) {
			pc.sendPackets(new S_HPMeter(member.getId(), 0xff));
			member.sendPackets(new S_HPMeter(pc.getId(), 0xff));
		}
	}

	public void updateMiniHP(L1PcInstance pc) {
		L1PcInstance[] members = getMembers();

		for (L1PcInstance member : members) { // 分更新
			member.sendPackets(new S_HPMeter(pc.getId(), 100
					* pc.getCurrentHp() / pc.getMaxHp()));
		}
	}

	private void breakup() {
		L1PcInstance[] members = getMembers();

		for (L1PcInstance member : members) {
			removeMember(member);
			member.sendPackets(new S_ServerMessage(418)); // 解散。
		}
	}
	public void leaveMember(L1PcInstance pc) {
		L1PcInstance[] members = getMembers();
		if (isLeader(pc)) {
			// 场合
			breakup();
		} else {
			// 场合
			if (getNumOfMembers() == 2) {
				// 自分
				removeMember(pc);
				L1PcInstance leader = getLeader();
				removeMember(leader);

				sendLeftMessage(pc, pc);
				sendLeftMessage(leader, pc);
			} else {
				// 残２人以上
				removeMember(pc);
				for (L1PcInstance member : members) {
					sendLeftMessage(member, pc);
				}
				sendLeftMessage(pc, pc);
			}
		}
	}

	public void kickMember(L1PcInstance pc) {
		if (getNumOfMembers() == 2) {
			// 自分
			removeMember(pc);
			L1PcInstance leader = getLeader();
			removeMember(leader);

			sendLeftMessage(pc, pc);
			sendLeftMessage(leader, pc);
		} else {
			// 残２人以上
			removeMember(pc);
			for (L1PcInstance member : _membersList) {
				sendLeftMessage(member, pc);
			}
			sendLeftMessage(pc, pc);
		}
		pc.sendPackets(new S_ServerMessage(419)); // 追放。
	}

	public L1PcInstance[] getMembers() {
		return _membersList.toArray(new L1PcInstance[_membersList.size()]);
	}

	public int getNumOfMembers() {
		return _membersList.size();
	}

	private void sendLeftMessage(L1PcInstance sendTo, L1PcInstance left) {
		// %0去。
		sendTo.sendPackets(new S_ServerMessage(420, left.getName()));
	}
	
	public void msgToAll() {
		for (final L1PcInstance member : this._membersList) {
			if (!member.equals(this._leader)) {
				// 传递新队长信息
				member.sendPackets(new S_PacketBoxParty(this._leader.getId(),
						this._leader.getName()));
			}
		}
	}

}
