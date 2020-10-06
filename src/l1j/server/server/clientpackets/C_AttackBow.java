package l1j.server.server.clientpackets;

import static l1j.server.server.model.Instance.L1PcInstance.REGENSTATE_ATTACK;

import java.util.Calendar;

import l1j.server.AcceleratorChecker;
import l1j.server.Config;
import l1j.server.server.WriteLogTxt;
import l1j.server.server.mina.LineageClient;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_ChangeHeading;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_UseArrowSkill;
import l1j.server.server.world.L1World;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class C_AttackBow extends ClientBasePacket {
	private static final Log _log = LogFactory.getLog(C_AttackBow.class);

	public C_AttackBow(byte[] decrypt, LineageClient _client) {
		super(decrypt);
		try {
			final int targetId = readD();
			final int x = readH();
			final int y = readH();
			final L1PcInstance pc = _client.getActiveChar();

			pc.isFoeSlayer(false);
			
			if (pc == null || pc.isGhost() || pc.isDead() || pc.isTeleport()) {
				return;
			}
			
			if (pc.isStop()) {
				return;
			}
			if (pc.isPrivateShop()) {
				return;
			}
			final L1Object target = L1World.getInstance().findObject(targetId);
			// 攻态确认
			if (pc.getInventory().getWeight240() >= 197) { // 重量
				pc.sendPackets(new S_ServerMessage(110)); // \f1重。
				return;
			}

			if (pc.isInvisble()) { // 、中
				return;
			}

			if (pc.isInvisDelay()) { // 中
				return;
			}

			if (target instanceof L1Character) {
				if (target.getMapId() != pc.getMapId()
						|| pc.getLocation().getLineDistance(target.getLocation()) > 20D) { // 异常场所终了
					return;
				}
			}

			if (target instanceof L1NpcInstance) {
				if (((L1NpcInstance) target).getHiddenStatus() != 0) { // 地中、飞
					return;
				}
			}

			// 攻要求间隔
			if (Config.CHECK_ATTACK_INTERVAL) {
				final int result = pc.speed_Attack().checkIntervalattack();
				if (result == AcceleratorChecker.R_DISPOSED) {
					// _log.error("要求角色攻击:速度异常(" + pc.getName() + ")");
					return;
				}
			}
			if (pc.isCheckFZ()) {
				WriteLogTxt.Recording(pc.getName()+"攻击","变身ID"+pc.getTempCharGfx()+" 武器"+pc.getWeapon().getLogViewName()+"攻击动作");
			}

			// 攻场合理
			if (pc.hasSkillEffect(L1SkillId.ABSOLUTE_BARRIER)) { //  解除
				pc.killSkillEffectTimer(L1SkillId.ABSOLUTE_BARRIER);
				pc.startHpRegeneration();
				pc.startMpRegeneration();
//				pc.startMpRegenerationByDoll();
			}
			pc.killSkillEffectTimer(L1SkillId.MEDITATION);

			pc.delInvis(); // 透明态解除

			pc.setRegenState(REGENSTATE_ATTACK);
			
			pc.setCheck(false);
			
			/**
			 * 弱点曝光
			 */
			if (pc.get_weaknss() != 0) {
				long h_time = Calendar.getInstance().getTimeInMillis() / 1000;// 换算为秒
				if (h_time - pc.get_weaknss_t() > 16) {
					pc.set_weaknss(0, 0);
				}
			}

			if (target != null && (target instanceof L1Character)&&!((L1Character) target).isDead()) { // 补上&& !injustice 判断 
				//System.out.println("攻击输出开始！");
				target.onAction(pc);
				//System.out.println("攻击输出完毕！");
			} else { // 空攻				
                // 设置面向
                pc.setHeading(pc.targetDirection(x, y));
                // 取回使用武器
                final L1ItemInstance weapon = pc.getWeapon();

                if (weapon != null) {
                    // 武器类型
                    final int weaponType = weapon.getItem().getType1();

                    switch (weaponType) {
                        case 20:// 弓
                            final L1ItemInstance arrow = pc.getInventory().getArrow();
                            if (arrow != null) { // 具有箭
                                this.arrowAction(pc, arrow, 66, x, y);
                            } else {
                                if (weapon.getItem().getItemId() == 190) {// 沙哈之弓
                                    this.arrowAction(pc, null, 2349, x,y);
                                } else {
                                    this.nullAction(pc);
                                }
                            }
                            break;

                        case 62:// 铁手甲
                            final L1ItemInstance sting = pc.getInventory().getSting();
                            if (sting != null) { // 具有飞刀
                                this.arrowAction(pc, sting, 2989, x,y);

                            } else {
                                this.nullAction(pc);
                            }
                            break;
                    }
                }
			}
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}finally {
			this.over();
		}
	}
	private void arrowAction(L1PcInstance pc, L1ItemInstance item, int gfx,
            int targetX, int targetY) {
        pc.sendPacketsAll(new S_UseArrowSkill(pc, gfx, targetX, targetY));

        if (item != null) {
            pc.getInventory().removeItem(item, 1);
        }
    }
	private void nullAction(L1PcInstance pc) {
        int aid = 1;
        // 外型编号改变动作
        if (pc.getTempCharGfx() == 3860) {
            aid = 21;
        }

        pc.sendPacketsAll(new S_ChangeHeading(pc));
        // 送出封包(动作)
        pc.sendPacketsAll(new S_DoActionGFX(pc.getId(), aid));
    }
}
