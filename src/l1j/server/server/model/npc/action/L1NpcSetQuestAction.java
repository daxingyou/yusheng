package l1j.server.server.model.npc.action;

import org.w3c.dom.Element;

import l1j.server.server.model.L1Object;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.npc.L1NpcHtml;

//public class L1NpcSetQuestAction extends L1NpcXmlAction {
//	private final int _id;
//	private final int _step;
//
//	public L1NpcSetQuestAction(Element element) {
//		super(element);
//
//		_id = L1NpcXmlParser.parseQuestId(element.getAttribute("Id"));
//		_step = L1NpcXmlParser.parseQuestStep(element.getAttribute("Step"));
//
//		if (_id == -1 || _step == -1) {
//			throw new IllegalArgumentException();
//		}
//	}
//
//	@Override
//	public L1NpcHtml execute(String actionName, L1PcInstance pc, L1Object obj,
//			byte[] args) {
//		pc.getQuest().set_step(_id, _step);
//		return null;
//	}
public class L1NpcSetQuestAction extends L1NpcXmlAction {
	private final int _id;
	private final int _step;

	public L1NpcSetQuestAction(final Element element) {
		super(element);

		this._id = L1NpcXmlParser.parseQuestId(element.getAttribute("Id"));
		this._step = L1NpcXmlParser
				.parseQuestStep(element.getAttribute("Step"));

		if ((this._id == -1) || (this._step == -1)) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public L1NpcHtml execute(final String actionName, final L1PcInstance pc,
			final L1Object obj, final byte[] args) {
		pc.getQuest().set_step(this._id, this._step);
		return null;
	}

	@Override
	public void execute(String actionName, String npcid) {
		// TODO Auto-generated method stub

	}


}
