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

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import l1j.server.server.model.L1Object;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.npc.L1NpcHtml;
import l1j.server.server.utils.IterableElementList;

public class L1NpcShowHtmlAction extends L1NpcXmlAction {
	private final String _htmlId;
	private final String[] _args;

	public L1NpcShowHtmlAction(final Element element) {
		super(element);

		this._htmlId = element.getAttribute("HtmlId");
		final NodeList list = element.getChildNodes();
		final ArrayList<String> dataList = new ArrayList<String>();
		for (final Element elem : new IterableElementList(list)) {
			if (elem.getNodeName().equalsIgnoreCase("Data")) {
				dataList.add(elem.getAttribute("Value"));
			}
		}
		this._args = dataList.toArray(new String[dataList.size()]);
	}

	@Override
	public L1NpcHtml execute(final String actionName, final L1PcInstance pc,
			final L1Object obj, final byte[] args) {
		return new L1NpcHtml(this._htmlId, this._args);
	}

	@Override
	public void execute(String actionName, String npcid) {
		// TODO Auto-generated method stub

	}


}
