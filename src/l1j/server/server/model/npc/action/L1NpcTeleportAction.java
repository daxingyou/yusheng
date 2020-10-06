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
package l1j.server.server.model.npc.action;



import org.w3c.dom.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.npc.L1NpcHtml;
import l1j.server.server.serverpackets.S_ServerMessage;

public class L1NpcTeleportAction extends L1NpcXmlAction {

	private static final Log _log = LogFactory
			.getLog(L1NpcTeleportAction.class);

	private final L1Location _loc;

	private final int _heading;

	private final int _price;

	private final boolean _effect;

	public L1NpcTeleportAction(final Element element) {
		super(element);

		final int x = L1NpcXmlParser.getIntAttribute(element, "X", -1);
		final int y = L1NpcXmlParser.getIntAttribute(element, "Y", -1);
		final int mapId = L1NpcXmlParser.getIntAttribute(element, "Map", -1);
		this._loc = new L1Location(x, y, mapId);

		this._heading = L1NpcXmlParser.getIntAttribute(element, "Heading", 5);

		this._price = L1NpcXmlParser.getIntAttribute(element, "Price", 0);
		this._effect = L1NpcXmlParser.getBoolAttribute(element, "Effect", true);
	}

	@Override
	public L1NpcHtml execute(final String actionName, final L1PcInstance pc,
			final L1Object obj, final byte[] args) {
		try {
			if (!pc.getInventory().checkItem(L1ItemId.ADENA, this._price)) {
				pc.sendPackets(new S_ServerMessage(337, "$4"));
				return L1NpcHtml.HTML_CLOSE;
			}
			pc.getInventory().consumeItem(L1ItemId.ADENA, this._price);
			L1Teleport.teleport(pc, this._loc.getX(), this._loc.getY(),
					(short) this._loc.getMapId(), this._heading, this._effect);
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		}
		return null;
	}

	@Override
	public void execute(final String actionName, final String npcid) {
		// System.out.println("NpcTeleportTable.get().set");
		//NpcTable.getInstance().set(actionName, _loc.getX(), _loc.getY(),
		//		_loc.getMapId(), _price, npcid);
	}

}
