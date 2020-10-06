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
package l1j.server.server.model.Instance;

import java.util.Timer;
import java.util.TimerTask; // NPC说话计时器 
import java.util.logging.Logger;

import l1j.server.server.datatables.NPCTalkDataTable;
import l1j.server.server.datatables.TownTable;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1NpcTalkData;
import l1j.server.server.model.L1Quest;
import l1j.server.server.model.L1TownLocation;
import l1j.server.server.model.L1Trade;
import l1j.server.server.model.gametime.L1GameTimeClock;
import l1j.server.server.serverpackets.S_ChangeHeading;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.utils.FaceToFace;
import l1j.server.server.world.L1World;
import l1j.william.NpcSpawn;
import l1j.william.NpcTalk;


public class L1MerchantInstance extends L1NpcInstance {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	//对话时停止移动 
	private Talk _talk;
	private static final Timer _talkTimer = new Timer(true);
	private RestMonitor _monitor;
	private static final long REST_MILLISEC = 10000;
	private static final Timer _restTimer = new Timer(true);
	//对话时停止移动  end
	
	/**
	 * @param template
	 */
	public L1MerchantInstance(L1Npc template) {
		super(template);
	}

	@Override
	public void searchTarget() {
		L1PcInstance targetPlayer = null;

		for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(this)) {
			if (pc != null) {
				targetPlayer = pc;
				break;
			}
		}

		if (targetPlayer != null) {
			// NPC说话 
			if (_talk == null) {

				NpcTalk.forL1MonsterTalking(this);

				// 计时器
				synchronized (this) {
					_talk = new Talk();
					_talkTimer.schedule(_talk, 20000);
				}
				// 计时器
			}
			// NPC说话  end
		}
	}
	
	// NPC移动 
	@Override
	public void onNpcAI() {
		if (isAiRunning()) {
			return;
		}
		setActived(false);
		startAI();
	}
	// NPC移动  end
	
	@Override
	public void onAction(L1PcInstance player) {
		if (ATTACK != null) {
			ATTACK.attack(player, this);
		}
		if (this.getNpcId() == 8895){
			if (player.getTradeID() != 0){
				final L1Trade trade = new L1Trade();
				trade.tradeCancel(player);
			}
			if (player.getTradeID() == 0){
				if (FaceToFace.faceToFacenpc(player, this)){
					player.setTradeID(this.getId());
					player.sendPackets(new S_Message_YN(252, this.getName()));
				}
			}
		}
		L1Attack attack = new L1Attack(player, this);
		// 变更 
		if (attack.calcHit()) {
			attack.calcDamage();
			attack.calcStaffOfMana();
			attack.addPcPoisonAttack(player, this);
		}
		// 变更  end
		attack.action();
		attack.commit();
	}

	@Override
	public void onTalkAction(L1PcInstance player) {
		int objid = getId();
		L1NpcTalkData talking = NPCTalkDataTable.getInstance().getTemplate(
				getNpcTemplate().get_npcId());
		int npcid = getNpcTemplate().get_npcId();
		L1Quest quest = player.getQuest();
		String htmlid = null;
		String[] htmldata = null;
		//加入 william功能
		if( NpcSpawn.getInstance().forFirstTimeTalking(player, npcid , objid) )
			return;
		if( l1j.william.NpcAction.forNpcAction(player, npcid , objid))
			return;
       	//加入 william功能 end

		if (talking != null) {
/*			System.out.println("弹出对话框不为空");
			System.out.println("正常对话框为"+talking.getNormalAction());
			System.out.println("非正常对话框为"+talking.getCaoticAction());*/
			//面向对话的玩家 
			if (npcid == 70889 
			 || npcid == 70906 
			 || npcid == 70909 
			 || npcid == 80093 
			 || npcid == 80098) {
			int pcx = player.getX(); // PCX座标
			int pcy = player.getY(); // PCY座标
			int npcx = getX(); // NPCX座标
			int npcy = getY(); // NPCY座标

			if (pcx == npcx && pcy < npcy) {
				setHeading(0);
			} else if (pcx > npcx && pcy < npcy) {
				setHeading(1);
			} else if (pcx > npcx && pcy == npcy) {
				setHeading(2);
			} else if (pcx > npcx && pcy > npcy) {
				setHeading(3);
			} else if (pcx == npcx && pcy > npcy) {
				setHeading(4);
			} else if (pcx < npcx && pcy > npcy) {
				setHeading(5);
			} else if (pcx < npcx && pcy == npcy) {
				setHeading(6);
			} else if (pcx < npcx && pcy < npcy) {
				setHeading(7);
      		}
      		broadcastPacket(new S_ChangeHeading(this));
			}
      		//面向对话的玩家  end
      		
			//变更成switch 
			switch (npcid)
			{
				case 70841: { // 
					if (player.isElf()) { // 
						htmlid = "luudielE1";
					} else if (player.isDarkelf()) { // 
						htmlid = "luudielCE1";
					} else {
						htmlid = "luudiel1";
					}
				}
				break;
				case 70522: { // 
					if (player.isCrown()) { // 君主
						if (player.getLevel() >= 15) {
							int lv15_step = quest.get_step(L1Quest.QUEST_LEVEL15);
							if (lv15_step == 2 || lv15_step == L1Quest.QUEST_END) { // 济
								htmlid = "gunterp11";
							} else {
								htmlid = "gunterp9";
							}
						} else { // Lv15未满
							htmlid = "gunterp12";
						}
					} else if (player.isKnight()) { // 
						int lv30_step = quest.get_step(L1Quest.QUEST_LEVEL30);
						if (lv30_step == 0) { // 未开始
							htmlid = "gunterk9";
						} else if (lv30_step == 1) {
							htmlid = "gunterkE1";
						} else if (lv30_step == 2) { // 同意济
							htmlid = "gunterkE2";
						} else if (lv30_step >= 3) { // 终了济
							htmlid = "gunterkE3";
						}
					} else if (player.isElf()) { // 
						htmlid = "guntere1";
					} else if (player.isWizard()) { // 
						htmlid = "gunterw1";
					} else if (player.isDarkelf()) { // 
						htmlid = "gunterde1";
					}
				}
				break;
				case 70653: { // 
					if (player.isCrown()) { // 君主
						if (player.getLevel() >= 45) {
							if (quest.isEnd(L1Quest.QUEST_LEVEL30)) { // lv30济
								int lv45_step = quest
										.get_step(L1Quest.QUEST_LEVEL45);
								if (lv45_step == L1Quest.QUEST_END) { // 济
									htmlid = "masha4";
								} else if (lv45_step >= 1) { // 同意济
									htmlid = "masha3";
								} else { // 未同意
									htmlid = "masha1";
								}
							}
						}
					} else if (player.isKnight()) { // 
						if (player.getLevel() >= 45) {
							if (quest.isEnd(L1Quest.QUEST_LEVEL30)) { // Lv30终了济
								int lv45_step = quest
										.get_step(L1Quest.QUEST_LEVEL45);
								if (lv45_step == L1Quest.QUEST_END) { // 济
									htmlid = "mashak3";
								} else if (lv45_step == 0) { // 未开始
									htmlid = "mashak1";
								} else if (lv45_step >= 1) { // 同意济
									htmlid = "mashak2";
								}
							}
						}
					} else if (player.isElf()) { // 
						if (player.getLevel() >= 45) {
							if (quest.isEnd(L1Quest.QUEST_LEVEL30)) { // Lv30终了济
								int lv45_step = quest
										.get_step(L1Quest.QUEST_LEVEL45);
								if (lv45_step == L1Quest.QUEST_END) { // 济
									htmlid = "mashae3";
								} else if (lv45_step >= 1) { // 同意济
									htmlid = "mashae2";
								} else { // 未同意
									htmlid = "mashae1";
								}
							}
						}
					}
				}
				break;
				case 70554: { // 
					if (player.isCrown()) { // 君主
						if (player.getLevel() >= 15) {
							int lv15_step = quest.get_step(L1Quest.QUEST_LEVEL15);
							if (lv15_step == 1) { // 济
								htmlid = "zero5";
							} else if (lv15_step == L1Quest.QUEST_END) { // 、济
								htmlid = "zero6";
							} else {
								htmlid = "zero1";
							}
						} else { // Lv15未满
							htmlid = "zero6";
						}
					}
				}
				break;
				case 70783: { // 
					if (player.isCrown()) { // 君主
						if (player.getLevel() >= 30) {
							if (quest.isEnd(L1Quest.QUEST_LEVEL15)) { // lv15试练济
								int lv30_step = quest
										.get_step(L1Quest.QUEST_LEVEL30);
								if (lv30_step == L1Quest.QUEST_END) { // 济
									htmlid = "aria3";
								} else if (lv30_step == 1) { // 同意济
									htmlid = "aria2";
								} else { // 未同意
									htmlid = "aria1";
								}
							}
						}
					}
				}
				break;
				case 70782: { // 
					if (player.getTempCharGfx() == 1037) {// 变身
						if (player.isCrown()) { // 君主
							if (quest.get_step(L1Quest.QUEST_LEVEL30) == 1) {
								htmlid = "ant1";
							} else {
								htmlid = "ant3";
							}
						} else { // 君主以外
							htmlid = "ant3";
						}
					}
				}
				break;
				case 70545: { // 
					if (player.isCrown()) { // 君主
						int lv45_step = quest.get_step(L1Quest.QUEST_LEVEL45);
						if (lv45_step >= 1 && lv45_step != L1Quest.QUEST_END) { // 开始未终了
							if (player.getInventory().checkItem(40586)) { // 王家纹章(左)
								htmlid = "richard4";
							} else {
								htmlid = "richard1";
							}
						}
					}
				}
				break;
				case 70776: { // 
					if (player.isCrown()) { // 君主
						int lv45_step = quest.get_step(L1Quest.QUEST_LEVEL45);
						if (lv45_step == 1) {
							htmlid = "meg1";
						} else if (lv45_step == 2) { // 同意济
							htmlid = "meg2";
						} else if (lv45_step >= 3) { // 济
							htmlid = "meg3";
						}
					}
				}
				break;
				case 70751: { // 
					if (player.isCrown()) { // 君主
						if (player.getLevel() >= 45) {
							if (quest.get_step(L1Quest.QUEST_LEVEL45) == 2) { // 同意济
								htmlid = "brad1";
							}
						}
					}
				}
				break;
				case 70798: { // 
					if (player.isKnight()) { // 
						if (player.getLevel() >= 15) {
							int lv15_step = quest.get_step(L1Quest.QUEST_LEVEL15);
							if (lv15_step >= 1) { // 济
								htmlid = "riky5";
							} else {
								htmlid = "riky1";
							}
						} else { // Lv15未满
							htmlid = "riky6";
						}
					}
				}
				break;
				case 70802: { // 
					if (player.isKnight()) { // 
						if (player.getLevel() >= 15) {
							int lv15_step = quest.get_step(L1Quest.QUEST_LEVEL15);
							if (lv15_step == L1Quest.QUEST_END) { // 济
								htmlid = "aanon7";
							} else if (lv15_step == 1) { // 济
								htmlid = "aanon4";
							}
						}
					}
				}
				break;
				case 70775: { // 
					if (player.isKnight()) { // 
						if (player.getLevel() >= 30) {
							if (quest.isEnd(L1Quest.QUEST_LEVEL15)) { // LV15终了济
								int lv30_step = quest
										.get_step(L1Quest.QUEST_LEVEL30);
								if (lv30_step == 0) { // 未开始
									htmlid = "mark1";
								} else {
									htmlid = "mark2";
								}
							}
						}
					}
				}
				break;
				case 70794: { // 
						if (player.isCrown()) { // 君主
						htmlid = "gerardp1";
					} else if (player.isKnight()) { // 
						int lv30_step = quest.get_step(L1Quest.QUEST_LEVEL30);
						if (lv30_step == L1Quest.QUEST_END) { // 终了济
							htmlid = "gerardkE5";
						} else if (lv30_step < 3) { // 未终了
							htmlid = "gerardk7";
						} else if (lv30_step == 3) { // 终了济
							htmlid = "gerardkE1";
						} else if (lv30_step == 4) { // 同意济
							htmlid = "gerardkE2";
						} else if (lv30_step == 5) { // 鳞 终了济
							htmlid = "gerardkE3";
						} else if (lv30_step >= 6) { // 复活同意济
							htmlid = "gerardkE4";
						}
					} else if (player.isElf()) { // 
						htmlid = "gerarde1";
					} else if (player.isWizard()) { // 
						htmlid = "gerardw1";
					} else if (player.isDarkelf()) { // 
						htmlid = "gerardde1";
					}
				}
				break;
				case 70555: { // 
					if (player.getTempCharGfx() == 2374) { // 变身
						if (player.isKnight()) { // 
							if (quest.get_step(L1Quest.QUEST_LEVEL30) == 6) { // 复活同意济
								htmlid = "jim2";
							} else {
								htmlid = "jim4";
							}
						} else { // 以外
							htmlid = "jim4";
						}
					}
					//补上未变身的对话 
					else {
						htmlid = "jim1";
					}
					//补上未变身的对话  end
				}
				break;
				case 70715: { // 
					if (player.isKnight()) { // 
						int lv45_step = quest.get_step(L1Quest.QUEST_LEVEL45);
						if (lv45_step == 1) { // 同意济
							htmlid = "jimuk1";
						} else if (lv45_step >= 2) { // 同意济
							htmlid = "jimuk2";
						}
					}
				}
				break;
				case 70711: { //  
					if (player.isKnight()) { // 
						int lv45_step = quest.get_step(L1Quest.QUEST_LEVEL45);
						if (lv45_step == 2) { // 同意济
							if (player.getInventory().checkItem(20026)) { // 
								htmlid = "giantk1";
							}
						} else if (lv45_step == 3) { // 同意济
							htmlid = "giantk2";
						} else if (lv45_step >= 4) { // 古代：上半分
							htmlid = "giantk3";
						}
					}
				}
				break;
				case 70826: { // 
					if (player.isElf()) { // 
						if (player.getLevel() >= 15) {
							if (quest.isEnd(L1Quest.QUEST_LEVEL15)) {
								htmlid = "oth5";
							} else {
								htmlid = "oth1";
							}
						} else { // １５未满
							htmlid = "oth6";
						}
					}
				}
				break;
				case 70844: { // 森母
					if (player.isElf()) { // 
						if (player.getLevel() >= 30) {
							if (quest.isEnd(L1Quest.QUEST_LEVEL15)) { // Lv15终了济
								int lv30_step = quest
										.get_step(L1Quest.QUEST_LEVEL30);
								if (lv30_step == L1Quest.QUEST_END) { // 终了济
									htmlid = "motherEE3";
								} else if (lv30_step >= 1) { // 同意济
									htmlid = "motherEE2";
								} else if (lv30_step <= 0) { // 未同意
									htmlid = "motherEE1";
								}
							} else { // Lv15未终了
								htmlid = "mothere1";
							}
						} else { // Lv30未满
							htmlid = "mothere1";
						}
					}
				}
				break;
				case 70724: { // 
				if (player.isElf()) { // 
					int lv45_step = quest.get_step(L1Quest.QUEST_LEVEL45);
					if (lv45_step >= 4) { // 终了济
						htmlid = "heit5";
					} else if (lv45_step >= 3) { // 交换济
						htmlid = "heit3";
					} else if (lv45_step >= 2) { // 同意济
						htmlid = "heit2";
					} else if (lv45_step >= 1) { // 同意济
						htmlid = "heit1";
					}
				}
				}
				break;
				case 70531: { // 
				if (player.isWizard()) { // 
					if (player.getLevel() >= 15) {
						if (quest.isEnd(L1Quest.QUEST_LEVEL15)) { // 终了济
							htmlid = "jem6";
						} else {
							htmlid = "jem1";
						}
					}
				}	
				}
				break;
				case 70009: {
					htmlid = "gerengw3";
				/*
				if (player.isCrown()) { // 君主
					htmlid = "gerengp1";
				} else if (player.isKnight()) { // 
					htmlid = "gerengk1";
				} else if (player.isElf()) { // 
					htmlid = "gerenge1";
				} else if (player.isWizard()) { // 
					if (player.getLevel() >= 30) {
						if (quest.isEnd(L1Quest.QUEST_LEVEL15)) {
							int lv30_step = quest
									.get_step(L1Quest.QUEST_LEVEL30);
							if (lv30_step >= 4) { // 终了济
								htmlid = "gerengw3";
							} else if (lv30_step >= 3) { // 要求济
								htmlid = "gerengT4";
							} else if (lv30_step >= 2) { // 骨交换济
								htmlid = "gerengT3";
							} else if (lv30_step >= 1) { // 同意济
								htmlid = "gerengT2";
							} else { // 未同意
								htmlid = "gerengT1";
							}
						} else { // Lv15未终了
							htmlid = "gerengw3";
						}
					} else { // Lv30未满
						htmlid = "gerengw3";
					}
				} else if (player.isDarkelf()) { // 
					htmlid = "gerengde1";
				}*/	
				}
				break;
				case 70763: { // 
				if (player.isWizard()) { // 
					int lv30_step = quest.get_step(L1Quest.QUEST_LEVEL30);
					if (lv30_step == L1Quest.QUEST_END) {
						if (player.getLevel() >= 45) {
							int lv45_step = quest
									.get_step(L1Quest.QUEST_LEVEL45);
							if (lv45_step >= 1
									&& lv45_step != L1Quest.QUEST_END) { // 同意济
								htmlid = "talassmq2";
							} else if (lv45_step <= 0) { // 未同意
								htmlid = "talassmq1";
							}
						}
					} else if (lv30_step == 4) {
						htmlid = "talassE1";
					} else if (lv30_step == 5) {
						htmlid = "talassE2";
					}
				}	
				}
				break;
				case 81105: { // 神秘岩
				if (player.isWizard()) { // 
					int lv45_step = quest.get_step(L1Quest.QUEST_LEVEL45);
					if (lv45_step >= 3) { // 神秘岩终了济
						htmlid = "stoenm3";
					} else if (lv45_step >= 2) { // 神秘岩 同意济
						htmlid = "stoenm2";
					} else if (lv45_step >= 1) { //  同意济
						htmlid = "stoenm1";
					}
				}	
				}
				break;
				case 70739: { // 
				if (player.getLevel() >= 50) {
					int lv50_step = quest.get_step(L1Quest.QUEST_LEVEL50);
					if (lv50_step == L1Quest.QUEST_END) {
						if (player.isCrown()) { // 君主
							htmlid = "dicardingp3";
						} else if (player.isKnight()) { // 
							htmlid = "dicardingk3";
						} else if (player.isElf()) { // 
							htmlid = "dicardinge3";
						} else if (player.isWizard()) { // 
							htmlid = "dicardingw3";
						} else if (player.isDarkelf()) { // 
							htmlid = "dicarding";
						}
					} else if (lv50_step >= 1) { //  同意济
						if (player.isCrown()) { // 君主
							htmlid = "dicardingp2";
						} else if (player.isKnight()) { // 
							htmlid = "dicardingk2";
						} else if (player.isElf()) { // 
							htmlid = "dicardinge2";
						} else if (player.isWizard()) { // 
							htmlid = "dicardingw2";
						} else if (player.isDarkelf()) { // 
							htmlid = "dicarding";
						}
					} else if (lv50_step >= 0) {
						if (player.isCrown()) { // 君主
							htmlid = "dicardingp1";
						} else if (player.isKnight()) { // 
							htmlid = "dicardingk1";
						} else if (player.isElf()) { // 
							htmlid = "dicardinge1";
						} else if (player.isWizard()) { // 
							htmlid = "dicardingw1";
						} else if (player.isDarkelf()) { // 
							htmlid = "dicarding";
						}
					} else {
						htmlid = "dicarding";
					}
				} else { // Lv50未满
					htmlid = "dicarding";
				}	
				}
				break;
				case 70885: { // 
				if (player.isDarkelf()) { // 
					if (player.getLevel() >= 15) {
						int lv15_step = quest.get_step(L1Quest.QUEST_LEVEL15);
						if (lv15_step == L1Quest.QUEST_END) { // 终了济
							htmlid = "kanguard3";
						} else if (lv15_step >= 1) { // 同意济
							htmlid = "kanguard2";
						} else { // 未同意
							htmlid = "kanguard1";
						}
					} else { // Lv15未满
						htmlid = "kanguard5";
					}
				}	
				}
				break;
				case 70892: { // 
				if (player.isDarkelf()) { // 
					if (player.getLevel() >= 30) {
						if (quest.isEnd(L1Quest.QUEST_LEVEL15)) {
							int lv30_step = quest
									.get_step(L1Quest.QUEST_LEVEL30);
							if (lv30_step == L1Quest.QUEST_END) { // 终了济
								htmlid = "ronde5";
							} else if (lv30_step >= 2) { // 名簿交换济
								htmlid = "ronde3";
							} else if (lv30_step >= 1) { // 同意济
								htmlid = "ronde2";
							} else { // 未同意
								htmlid = "ronde1";
							}
						} else { // Lv15未终了
							htmlid = "ronde7";
						}
					} else { // Lv30未满
						htmlid = "ronde7";
					}
				}	
				}
				break;
				case 70895: { // 
				if (player.isDarkelf()) { // 
					if (player.getLevel() >= 45) {
						if (quest.isEnd(L1Quest.QUEST_LEVEL30)) {
							int lv45_step = quest
									.get_step(L1Quest.QUEST_LEVEL45);
							if (lv45_step == L1Quest.QUEST_END) { // 终了济
								if (player.getLevel() < 50) { // Lv50未满
									htmlid = "bluedikaq3";
								} else {
									int lv50_step = quest
											.get_step(L1Quest.QUEST_LEVEL50);
									if (lv50_step == L1Quest.QUEST_END) { // 终了济
										htmlid = "bluedikaq8";
									} /*删除else {
										htmlid = "bluedikaq6";
									} 删除*/
									//判断变更 
									else if (lv50_step == 0) {
										htmlid = "bluedikaq6";
									} else {
										htmlid = "bluedikaq7";
									}
									//判断变更  end
								}
							} else if (lv45_step >= 1) { // 同意济
								htmlid = "bluedikaq2";
							} else { // 未同意
								htmlid = "bluedikaq1";
							}
						} else { // Lv30未终了
							htmlid = "bluedikaq5";
						}
					} else { // Lv45未满
						htmlid = "bluedikaq5";
					}
				}
				}
				break;
				case 70906: { // 奇马 
					if (player.isDarkelf()) { // 黑妖
						if (player.getLevel() >= 50) { // 等级大于等于50
							if (quest.isEnd(L1Quest.QUEST_LEVEL45)) { // 已完成45级试炼任务
								int lv50_step = quest
									.get_step(L1Quest.QUEST_LEVEL50);
								if (lv50_step == 1) { // 已接受布鲁迪卡的任务
									htmlid = "kimaq1";
								} else if (lv50_step == 2) { // 已给予安迪亚之信
									htmlid = "kimaq3";
								}
							}
						}
					}
				}
				break;
				case 70904: { // 
				if (player.isDarkelf()) {
					if (quest.get_step(L1Quest.QUEST_LEVEL45) == 1) { // 同意济
						htmlid = "koup12";
					}
				}
				}
				break;
				case 70824: { // 追从者
				if (player.getTempCharGfx() == 3634) { // 变身
					if (player.isDarkelf()) {
						int lv45_step = quest.get_step(L1Quest.QUEST_LEVEL45);
						if (lv45_step == 1) {
							htmlid = "assassin1";
						} else if (lv45_step == 2) {
							htmlid = "assassin2";
						} else {
							htmlid = "assassin3";
						}
					} else { // 以外
						htmlid = "assassin4";
					}
				} 
				//补上未变身的对话 
				else {
					htmlid = "assassin4";
				}
				//补上未变身的对话  end
				}
				break;
				case 70744: { // 
				if (player.isDarkelf()) { // 
					int lv45_step = quest.get_step(L1Quest.QUEST_LEVEL45);
					if (lv45_step >= 5) { // ２回目同意济
						htmlid = "roje14";
					} else if (lv45_step >= 4) { // 头部 交换济
						htmlid = "roje13";
					} else if (lv45_step >= 3) { //  同意济
						htmlid = "roje12";
					} else if (lv45_step >= 2) { // 追从者 同意济
						htmlid = "roje11";
					} else { // 追从者 未同意
						htmlid = "roje15";
					}
				}	
				}
				break;
				case 70811: { // 
				if (quest.get_step(L1Quest.QUEST_LYRA) >= 1) { // 契约济
					htmlid = "lyraEv3";
				} else { // 未契约
					htmlid = "lyraEv1";
				}	
				}
				break;
				case 70087: { // 
				if (player.isDarkelf()) {
					htmlid = "sedia";
				}	
				}
				break;
				case 70099: { // 
				if (!quest.isEnd(L1Quest.QUEST_OILSKINMANT)) {
					if (player.getLevel() > 13) {
						htmlid = "kuper1";
					}
				}
				}
				break;
				case 70796: { // 
				if (!quest.isEnd(L1Quest.QUEST_OILSKINMANT)) {
					if (player.getLevel() > 13) {
						htmlid = "dunham1";
					}
				}
				}
				break;
				case 70011: { // 话岛船着管理人
				long time = L1GameTimeClock.getInstance().getGameTime()
						.getSeconds() % 86400;
				if (time < 60 * 60 * 6 || time > 60 * 60 * 20) { // 20:00～6:00
					htmlid = "shipEvI6";
				}
				}
				break;
				case 70553: { // 城 侍从长 
				boolean hascastle = checkHasCastle(player,
						L1CastleLocation.KENT_CASTLE_ID);
				if (hascastle) { // 城主员
					if (checkClanLeader(player)) { // 血盟主
						htmlid = "ishmael1";
					} else {
						htmlid = "ishmael6";
						htmldata = new String[] { player.getName() };
					}
				} else {
					htmlid = "ishmael7";
				}	
				}
				break;
				case 70822: { // 森  
				boolean hascastle = checkHasCastle(player,
						L1CastleLocation.OT_CASTLE_ID);
				if (hascastle) { // 城主员
					if (checkClanLeader(player)) { // 血盟主
						htmlid = "seghem1";
					} else {
						htmlid = "seghem6";
						htmldata = new String[] { player.getName() };
					}
				} else {
					htmlid = "seghem7";
				}	
				}
				break;
				case 70784: { // 城 侍从长 
				boolean hascastle = checkHasCastle(player,
						L1CastleLocation.WW_CASTLE_ID);
				if (hascastle) { // 城主员
					if (checkClanLeader(player)) { // 血盟主
						htmlid = "othmond1";
					} else {
						htmlid = "othmond6";
						htmldata = new String[] { player.getName() };
					}
				} else {
					htmlid = "othmond7";
				}	
				}
				break;
				case 70623: { // 城 侍从长 
				boolean hascastle = checkHasCastle(player,
						L1CastleLocation.GIRAN_CASTLE_ID);
				if (hascastle) { // 城主员
					if (checkClanLeader(player)) { // 血盟主
						htmlid = "orville1";
					} else {
						htmlid = "orville6";
						htmldata = new String[] { player.getName() };
					}
				} else {
					htmlid = "orville7";
				}
				}
				break;
				case 70880: { // 城 侍从长 
				boolean hascastle = checkHasCastle(player,
						L1CastleLocation.HEINE_CASTLE_ID);
				if (hascastle) { // 城主员
					if (checkClanLeader(player)) { // 血盟主
						htmlid = "fisher1";
					} else {
						htmlid = "fisher6";
						htmldata = new String[] { player.getName() };
					}
				} else {
					htmlid = "fisher7";
				}	
				}
				break;
				case 70665: { // 城 侍从长 
				boolean hascastle = checkHasCastle(player,
						L1CastleLocation.DOWA_CASTLE_ID);
				if (hascastle) { // 城主员
					if (checkClanLeader(player)) { // 血盟主
						htmlid = "potempin1";
					} else {
						htmlid = "potempin6";
						htmldata = new String[] { player.getName() };
					}
				} else {
					htmlid = "potempin7";
				}	
				}
				break;
				case 70721: { // 城 侍从长 
				boolean hascastle = checkHasCastle(player,
						L1CastleLocation.ADEN_CASTLE_ID);
				if (hascastle) { // 城主员
					if (checkClanLeader(player)) { // 血盟主
						htmlid = "timon1";
					} else {
						htmlid = "timon6";
						htmldata = new String[] { player.getName() };
					}
				} else {
					htmlid = "timon7";
				}
				}
				break;
				case 81155: { // 要塞 
				boolean hascastle = checkHasCastle(player,
						L1CastleLocation.DIAD_CASTLE_ID);
				if (hascastle) { // 城主员
					if (checkClanLeader(player)) { // 血盟主
						htmlid = "olle1";
					} else {
						htmlid = "olle6";
						htmldata = new String[] { player.getName() };
					}
				} else {
					htmlid = "olle7";
				}
				}
				break;
				case 80057: { // 
				switch (player.getKarmaLevel()) {
					case 0:
					htmlid = "alfons1";
					break;
				case -1:
					htmlid = "cyk1";
					break;
				case -2:
					htmlid = "cyk2";
					break;
				case -3:
					htmlid = "cyk3";
					break;
				case -4:
					htmlid = "cyk4";
					break;
				case -5:
					htmlid = "cyk5";
					break;
				case -6:
					htmlid = "cyk6";
					break;
				case -7:
					htmlid = "cyk7";
					break;
				case -8:
					htmlid = "cyk8";
					break;
				case 1:
					htmlid = "cbk1";
					break;
				case 2:
					htmlid = "cbk2";
					break;
				case 3:
					htmlid = "cbk3";
					break;
				case 4:
					htmlid = "cbk4";
					break;
				case 5:
					htmlid = "cbk5";
					break;
				case 6:
					htmlid = "cbk6";
					break;
				case 7:
					htmlid = "cbk7";
					break;
				case 8:
					htmlid = "cbk8";
					break;
				default:
					htmlid = "alfons1";
					break;
				}	
				}
				break;
				case 80058: { // 次元扉(砂漠)
				int level = player.getLevel();
				if (level <= 44) {
					htmlid = "cpass03";
				} else if (level <= 51 && 45 <= level) {
					htmlid = "cpass02";
				} else {
					htmlid = "cpass01";
				}	
				}
				break;
				case 80059: { // 次元扉(土)
				if (player.getKarmaLevel() > 0) {
					htmlid = "cpass03";
				} else if (player.getInventory().checkItem(40921)) { // 元素支配者
					htmlid = "wpass02";
				} else if (player.getInventory().checkItem(40917)) { // 地支配者
					htmlid = "wpass14";
				} else if (player.getInventory().checkItem(40912) // 风通行证
						|| player.getInventory().checkItem(40910) // 水通行证
						|| player.getInventory().checkItem(40911)) { // 火通行证
					htmlid = "wpass04";
				} else if (player.getInventory().checkItem(40909)) { // 地通行证
					int count = getNecessarySealCount(player);
					if (player.getInventory().checkItem(40913, count)) { // 地印章
						createRuler(player, 1, count);
						htmlid = "wpass06";
					} else {
						htmlid = "wpass03";
					}
				} else if (player.getInventory().checkItem(40913)) { // 地印章
					htmlid = "wpass08";
				} else {
					htmlid = "wpass05";
				}	
				}
				break;
				case 80060: { // 次元扉(风)
				if (player.getKarmaLevel() > 0) {
					htmlid = "cpass03";
				} else if (player.getInventory().checkItem(40921)) { // 元素支配者
					htmlid = "wpass02";
				} else if (player.getInventory().checkItem(40920)) { // 风支配者
					htmlid = "wpass13";
				} else if (player.getInventory().checkItem(40909) // 地通行证
						|| player.getInventory().checkItem(40910) // 水通行证
						|| player.getInventory().checkItem(40911)) { // 火通行证
					htmlid = "wpass04";
				} else if (player.getInventory().checkItem(40912)) { // 风通行证
					int count = getNecessarySealCount(player);
					if (player.getInventory().checkItem(40916, count)) { // 风印章
						createRuler(player, 8, count);
						htmlid = "wpass06";
					} else {
						htmlid = "wpass03";
					}
				} else if (player.getInventory().checkItem(40916)) { // 风印章
					htmlid = "wpass08";
				} else {
					htmlid = "wpass05";
				}
				}
				break;
				case 80076: { // 倒れた航海士
					if (player.getInventory().checkItem(41058)) { // 完成した航海日誌
						htmlid = "voyager8";
					} else if (player.getInventory().checkItem(49082) // 未完成の航海日誌
							|| player.getInventory().checkItem(49083)) {
						// ページを追加していない状態
						if (player.getInventory().checkItem(41038) // 航海日誌 1ページ
								|| player.getInventory().checkItem(41039) // 航海日誌
																			// 2ページ
								|| player.getInventory().checkItem(41039) // 航海日誌
																			// 3ページ
								|| player.getInventory().checkItem(41039) // 航海日誌
																			// 4ページ
								|| player.getInventory().checkItem(41039) // 航海日誌
																			// 5ページ
								|| player.getInventory().checkItem(41039) // 航海日誌
																			// 6ページ
								|| player.getInventory().checkItem(41039) // 航海日誌
																			// 7ページ
								|| player.getInventory().checkItem(41039) // 航海日誌
																			// 8ページ
								|| player.getInventory().checkItem(41039) // 航海日誌
																			// 9ページ
								|| player.getInventory().checkItem(41039)) { // 航海日誌
																				// 10ページ
							htmlid = "voyager9";
						} else {
							htmlid = "voyager7";
						}
					} else if (player.getInventory().checkItem(49082) // 未完成の航海日誌
							|| player.getInventory().checkItem(49083)
							|| player.getInventory().checkItem(49084)
							|| player.getInventory().checkItem(49085)
							|| player.getInventory().checkItem(49086)
							|| player.getInventory().checkItem(49087)
							|| player.getInventory().checkItem(49088)
							|| player.getInventory().checkItem(49089)
							|| player.getInventory().checkItem(49090)
							|| player.getInventory().checkItem(49091)) {
						// ページを追加した状態
						htmlid = "voyager7";
					}
				}
				break;
				case 80061: { // 次元扉(水)
				if (player.getKarmaLevel() > 0) {
					htmlid = "cpass03";
				} else if (player.getInventory().checkItem(40921)) { // 元素支配者
					htmlid = "wpass02";
				} else if (player.getInventory().checkItem(40918)) { // 水支配者
					htmlid = "wpass11";
				} else if (player.getInventory().checkItem(40909) // 地通行证
						|| player.getInventory().checkItem(40912) // 风通行证
						|| player.getInventory().checkItem(40911)) { // 火通行证
					htmlid = "wpass04";
				} else if (player.getInventory().checkItem(40910)) { // 水通行证
					int count = getNecessarySealCount(player);
					if (player.getInventory().checkItem(40914, count)) { // 水印章
						createRuler(player, 4, count);
						htmlid = "wpass06";
					} else {
						htmlid = "wpass03";
					}
				} else if (player.getInventory().checkItem(40914)) { // 水印章
					htmlid = "wpass08";
				} else {
					htmlid = "wpass05";
				}	
				}
				break;
				case 80062: { // 次元扉(火)
				if (player.getKarmaLevel() > 0) {
					htmlid = "cpass03";
				} else if (player.getInventory().checkItem(40921)) { // 元素支配者
					htmlid = "wpass02";
				} else if (player.getInventory().checkItem(40919)) { // 火支配者
					htmlid = "wpass12";
				} else if (player.getInventory().checkItem(40909) // 地通行证
						|| player.getInventory().checkItem(40912) // 风通行证
						|| player.getInventory().checkItem(40910)) { // 水通行证
					htmlid = "wpass04";
				} else if (player.getInventory().checkItem(40911)) { // 火通行证
					int count = getNecessarySealCount(player);
					if (player.getInventory().checkItem(40915, count)) { // 火印章
						createRuler(player, 2, count);
						htmlid = "wpass06";
					} else {
						htmlid = "wpass03";
					}
				} else if (player.getInventory().checkItem(40915)) { // 火印章
					htmlid = "wpass08";
				} else {
					htmlid = "wpass05";
				}
				}
				break;
				case 80065: { // 密侦
				if (player.getKarmaLevel() < 3) {
					htmlid = "uturn0";
				} else {
					htmlid = "uturn1";
				}
				}
				break;
				case 80047: { // 召使
				if (player.getKarmaLevel() > -3) {
					htmlid = "uhelp1";
				} else {
					htmlid = "uhelp2";
				}	
				}
				break;
				case 80049: { // 摇者
				if (player.getKarma() <= -10000000) {
					htmlid = "betray11";
				} else {
					htmlid = "betray12";
				}
				}
				break;
				case 80050: { // 执政官
				if (player.getKarmaLevel() > -1) {
					htmlid = "meet103";
				} else {
					htmlid = "meet101";
				}
				}
				break;
				case 80053: { // 锻冶屋
				int karmaLevel = player.getKarmaLevel();
				if (karmaLevel == 0) {
					htmlid = "aliceyet";
				} else if (karmaLevel >= 1) {
					if (player.getInventory().checkItem(196)
							|| player.getInventory().checkItem(197)
							|| player.getInventory().checkItem(198)
							|| player.getInventory().checkItem(199)
							|| player.getInventory().checkItem(200)
							|| player.getInventory().checkItem(201)
							|| player.getInventory().checkItem(202)
							|| player.getInventory().checkItem(203)) {
						htmlid = "alice_gd";
					} else {
						htmlid = "gd";
					}
				} else if (karmaLevel <= -1) {
					if (player.getInventory().checkItem(40991)) {
						if (karmaLevel <= -1) {
							htmlid = "Mate_1";
						}
					} else if (player.getInventory().checkItem(196)) {
						if (karmaLevel <= -2) {
							htmlid = "Mate_2";
						} else {
							htmlid = "alice_1";
						}
					} else if (player.getInventory().checkItem(197)) {
						if (karmaLevel <= -3) {
							htmlid = "Mate_3";
						} else {
							htmlid = "alice_2";
						}
					} else if (player.getInventory().checkItem(198)) {
						if (karmaLevel <= -4) {
							htmlid = "Mate_4";
						} else {
							htmlid = "alice_3";
						}
					} else if (player.getInventory().checkItem(199)) {
						if (karmaLevel <= -5) {
							htmlid = "Mate_5";
						} else {
							htmlid = "alice_4";
						}
					} else if (player.getInventory().checkItem(200)) {
						if (karmaLevel <= -6) {
							htmlid = "Mate_6";
						} else {
							htmlid = "alice_5";
						}
					} else if (player.getInventory().checkItem(201)) {
						if (karmaLevel <= -7) {
							htmlid = "Mate_7";
						} else {
							htmlid = "alice_6";
						}
					} else if (player.getInventory().checkItem(202)) {
						if (karmaLevel <= -8) {
							htmlid = "Mate_8";
						} else {
							htmlid = "alice_7";
						}
					} else if (player.getInventory().checkItem(203)) {
						htmlid = "alice_8";
					} else {
						htmlid = "alice_no";
					}
				}	
				}
				break;
				case 80055: { // 补佐官
				int amuletLevel = 0;
				if (player.getInventory().checkItem(20358)) { // 奴隶
					amuletLevel = 1;
				} else if (player.getInventory().checkItem(20359)) { // 约束
					amuletLevel = 2;
				} else if (player.getInventory().checkItem(20360)) { // 解放
					amuletLevel = 3;
				} else if (player.getInventory().checkItem(20361)) { // 猎犬
					amuletLevel = 4;
				} else if (player.getInventory().checkItem(20362)) { // 魔族
					amuletLevel = 5;
				} else if (player.getInventory().checkItem(20363)) { // 勇士
					amuletLevel = 6;
				} else if (player.getInventory().checkItem(20364)) { // 将军
					amuletLevel = 7;
				} else if (player.getInventory().checkItem(20365)) { // 大将军
					amuletLevel = 8;
				}
				if (player.getKarmaLevel() == -1) {
					if (amuletLevel >= 1) {
						htmlid = "uamuletd";
					} else {
						htmlid = "uamulet1";
					}
				} else if (player.getKarmaLevel() == -2) {
					if (amuletLevel >= 2) {
						htmlid = "uamuletd";
					} else {
						htmlid = "uamulet2";
					}
				} else if (player.getKarmaLevel() == -3) {
					if (amuletLevel >= 3) {
						htmlid = "uamuletd";
					} else {
						htmlid = "uamulet3";
					}
				} else if (player.getKarmaLevel() == -4) {
					if (amuletLevel >= 4) {
						htmlid = "uamuletd";
					} else {
						htmlid = "uamulet4";
					}
				} else if (player.getKarmaLevel() == -5) {
					if (amuletLevel >= 5) {
						htmlid = "uamuletd";
					} else {
						htmlid = "uamulet5";
					}
				} else if (player.getKarmaLevel() == -6) {
					if (amuletLevel >= 6) {
						htmlid = "uamuletd";
					} else {
						htmlid = "uamulet6";
					}
				} else if (player.getKarmaLevel() == -7) {
					if (amuletLevel >= 7) {
						htmlid = "uamuletd";
					} else {
						htmlid = "uamulet7";
					}
				} else if (player.getKarmaLevel() == -8) {
					if (amuletLevel >= 8) {
						htmlid = "uamuletd";
					} else {
						htmlid = "uamulet8";
					}
				} else {
					htmlid = "uamulet0";
				}
				}
				break;
				case 80056: { // 业管理者
				if (player.getKarma() <= -10000000) {
					htmlid = "infamous11";
				} else {
					htmlid = "infamous12";
				}
				}
				break;
				case 80064: { // 执政官
				if (player.getKarmaLevel() < 1) {
					htmlid = "meet003";
				} else {
					htmlid = "meet001";
				}
				}
				break;
				case 80066: { // 摇者
				if (player.getKarma() >= 10000000) {
					htmlid = "betray01";
				} else {
					htmlid = "betray02";
				}
				}
				break;
				case 80071: { // 补佐官
				int earringLevel = 0;
				if (player.getInventory().checkItem(21020)) { // 踊跃
					earringLevel = 1;
				} else if (player.getInventory().checkItem(21021)) { // 双子
					earringLevel = 2;
				} else if (player.getInventory().checkItem(21022)) { // 友好
					earringLevel = 3;
				} else if (player.getInventory().checkItem(21023)) { // 极知
					earringLevel = 4;
				} else if (player.getInventory().checkItem(21024)) { // 暴走
					earringLevel = 5;
				} else if (player.getInventory().checkItem(21025)) { // 从魔
					earringLevel = 6;
				} else if (player.getInventory().checkItem(21026)) { // 血族
					earringLevel = 7;
				} else if (player.getInventory().checkItem(21027)) { // 奴隶
					earringLevel = 8;
				}
				if (player.getKarmaLevel() == 1) {
					if (earringLevel >= 1) {
						htmlid = "lringd";
					} else {
						htmlid = "lring1";
					}
				} else if (player.getKarmaLevel() == 2) {
					if (earringLevel >= 2) {
						htmlid = "lringd";
					} else {
						htmlid = "lring2";
					}
				} else if (player.getKarmaLevel() == 3) {
					if (earringLevel >= 3) {
						htmlid = "lringd";
					} else {
						htmlid = "lring3";
					}
				} else if (player.getKarmaLevel() == 4) {
					if (earringLevel >= 4) {
						htmlid = "lringd";
					} else {
						htmlid = "lring4";
					}
				} else if (player.getKarmaLevel() == 5) {
					if (earringLevel >= 5) {
						htmlid = "lringd";
					} else {
						htmlid = "lring5";
					}
				} else if (player.getKarmaLevel() == 6) {
					if (earringLevel >= 6) {
						htmlid = "lringd";
					} else {
						htmlid = "lring6";
					}
				} else if (player.getKarmaLevel() == 7) {
					if (earringLevel >= 7) {
						htmlid = "lringd";
					} else {
						htmlid = "lring7";
					}
				} else if (player.getKarmaLevel() == 8) {
					if (earringLevel >= 8) {
						htmlid = "lringd";
					} else {
						htmlid = "lring8";
					}
				} else {
					htmlid = "lring0";
				}
				}
				break;
				case 80072: { // 锻冶屋
				int karmaLevel = player.getKarmaLevel();
				if (karmaLevel == 1) {
					htmlid = "lsmith0";
				} else if (karmaLevel == 2) {
					htmlid = "lsmith1";
				} else if (karmaLevel == 3) {
					htmlid = "lsmith2";
				} else if (karmaLevel == 4) {
					htmlid = "lsmith3";
				} else if (karmaLevel == 5) {
					htmlid = "lsmith4";
				} else if (karmaLevel == 6) {
					htmlid = "lsmith5";
				} else if (karmaLevel == 7) {
					htmlid = "lsmith7";
				} else if (karmaLevel == 8) {
					htmlid = "lsmith8";
				} else {
					htmlid = "";
				}	
				}
				break;
				case 80074: { // 业管理者
				if (player.getKarma() >= 10000000) {
					htmlid = "infamous01";
				} else {
					htmlid = "infamous02";
				}	
				}
				break;
				case 80104: { // 骑马员
				if (!player.isCrown()) { // 君主
					htmlid = "horseseller4";
				}
				}
				break;
				case 70528: { // 话岛村 
				htmlid = talkToTownmaster(player,
						L1TownLocation.TOWNID_TALKING_ISLAND);
				}
				break;
				case 70546: { // 村 
				htmlid = talkToTownmaster(player, L1TownLocation.TOWNID_KENT);
				}
				break;
				case 70567: { // 村 
				htmlid = talkToTownmaster(player, L1TownLocation.TOWNID_GLUDIO);
				}
				break;
				case 70815: { // 火田村 
				htmlid = talkToTownmaster(player,
						L1TownLocation.TOWNID_ORCISH_FOREST);
				}
				break;
				case 70774: { // 村 
				htmlid = talkToTownmaster(player,
						L1TownLocation.TOWNID_WINDAWOOD);
				}
				break;
				case 70799: { //  
				htmlid = talkToTownmaster(player,
						L1TownLocation.TOWNID_SILVER_KNIGHT_TOWN);
				}
				break;
				case 70594: { // 都市 
				htmlid = talkToTownmaster(player, L1TownLocation.TOWNID_GIRAN);
				}
				break;
				case 70860: { // 都市 
				htmlid = talkToTownmaster(player, L1TownLocation.TOWNID_HEINE);
				}
				break;
				case 70654: { // 村 
				htmlid = talkToTownmaster(player, L1TownLocation.TOWNID_WERLDAN);
				}
				break;
				case 70748: { // 象牙塔村 
				htmlid = talkToTownmaster(player, L1TownLocation.TOWNID_OREN);	
				}
				break;
				case 70534: { // 话岛村 
				htmlid = talkToTownadviser(player,
						L1TownLocation.TOWNID_TALKING_ISLAND);
				}
				break;
				case 70556: { // 村 
				htmlid = talkToTownadviser(player, L1TownLocation.TOWNID_KENT);
				}
				break;
				case 70572: { // 村 
				htmlid = talkToTownadviser(player, L1TownLocation.TOWNID_GLUDIO);
				}
				break;
				case 70830: { // 火田村 
				htmlid = talkToTownadviser(player,
						L1TownLocation.TOWNID_ORCISH_FOREST);
				}
				break;
				case 70788: { // 村 
				htmlid = talkToTownadviser(player,
						L1TownLocation.TOWNID_WINDAWOOD);
				}
				break;
				case 70806: { //  
				htmlid = talkToTownadviser(player,
						L1TownLocation.TOWNID_SILVER_KNIGHT_TOWN);
				}
				break;
				case 70631: { // 都市 
				htmlid = talkToTownadviser(player, L1TownLocation.TOWNID_GIRAN);
				}
				break;
				case 70876: { // 都市 
				htmlid = talkToTownadviser(player, L1TownLocation.TOWNID_HEINE);
				}
				break;
				case 70663: { // 村 
				htmlid = talkToTownadviser(player,
						L1TownLocation.TOWNID_WERLDAN);
				}
				break;
				case 70761: { // 象牙塔村 
				htmlid = talkToTownadviser(player, L1TownLocation.TOWNID_OREN);
				}
				break;
				case 70997: { // 
				htmlid = talkToDoromond(player);
				}
				break;
				case 70998: { // 歌岛
				htmlid = talkToSIGuide(player);
				}
				break;
				case 70999: { // (歌岛)
				htmlid = talkToAlex(player);
				}
				break;
				case 71000: { // (训练场)
				htmlid = talkToAlexInTrainingRoom(player);
				}
				break;
				case 71002: { // 师
				htmlid = cancellation(player);
				}
				break;
				case 70506: { // 
				htmlid = talkToRuba(player);
				}
				break;
				case 71005: { // 
				htmlid = talkToPopirea(player);
				}
				break;
				case 71009: { // 
				if (player.getLevel() < 13) {
					htmlid = "jpe0071";
				}
				}
				break;
				case 71011: { // 
				if (player.getLevel() < 13) {
					htmlid = "jpe0061";
				}
				}
				break;
				case 71013: { // 
				if (player.isDarkelf()) {
					if (player.getLevel() <= 3) {
						htmlid = "karen1";
					} else if (player.getLevel() > 3 && player.getLevel() < 50) {
						htmlid = "karen3";
					} else if (player.getLevel() >= 50) {
						htmlid = "karen4";
					}
				}
				}
				break;
				case 71014: { // 村自警团(右)
				if (player.getLevel() < 13) {
					htmlid = "en0241";
				}
				}
				break;
				case 71015: { // 村自警团(上)
				if (player.getLevel() < 13) {
					htmlid = "en0261";
				} else if (player.getLevel() >= 13 && player.getLevel() < 25) {
					htmlid = "en0262";
				}
				}
				break;
				case 71031: { // 佣兵
				if (player.getLevel() < 25) {
					htmlid = "en0081";
				}
				}
				break;
				case 71032: { // 冒险者
				if (player.isElf()) {
					htmlid = "en0091e";
				} else if (player.isDarkelf()) {
					htmlid = "en0091d";
				} else if (player.isKnight()) {
					htmlid = "en0091k";
				} else if (player.isWizard()) {
					htmlid = "en0091w";
				} else if (player.isCrown()) {
					htmlid = "en0091p";
				}
				}
				break;
				case 71034: { // 
				if (player.getInventory().checkItem(41227)) { // 绍介状
					if (player.isElf()) {
						htmlid = "en0201e";
					} else if (player.isDarkelf()) {
						htmlid = "en0201d";
					} else if (player.isKnight()) {
						htmlid = "en0201k";
					} else if (player.isWizard()) {
						htmlid = "en0201w";
					} else if (player.isCrown()) {
						htmlid = "en0201p";
					}
				}
				}
				break;
				case 71033: { // 
				if (player.getInventory().checkItem(41228)) { // 守
					if (player.isElf()) {
						htmlid = "en0211e";
					} else if (player.isDarkelf()) {
						htmlid = "en0211d";
					} else if (player.isKnight()) {
						htmlid = "en0211k";
					} else if (player.isWizard()) {
						htmlid = "en0211w";
					} else if (player.isCrown()) {
						htmlid = "en0211p";
					}
				}
				}
				break;
				case 71026: { // 
				if (player.getLevel() < 10) {
					htmlid = "en0113";
				} else if (player.getLevel() >= 10 && player.getLevel() < 25) {
					htmlid = "en0111";
				} else if (player.getLevel() > 25) {
					htmlid = "en0112";
				}
				}
				break;
				case 71027: { // 
				if (player.getLevel() < 10) {
					htmlid = "en0283";
				} else if (player.getLevel() >= 10 && player.getLevel() < 25) {
					htmlid = "en0281";
				} else if (player.getLevel() > 25) {
					htmlid = "en0282";
				}
				}
				break;
				case 71021: { // 骨细工师
				if (player.getLevel() < 12) {
					htmlid = "en0197";
				} else if (player.getLevel() >= 12 && player.getLevel() < 25) {
					htmlid = "en0191";
				}
				}
				break;
				case 71022: { // 骨细工师
				if (player.getLevel() < 12) {
					htmlid = "jpe0155";
				} else if (player.getLevel() >= 12 && player.getLevel() < 25) {
					if (player.getInventory().checkItem(41230)
							|| player.getInventory().checkItem(41231)
							|| player.getInventory().checkItem(41232)
							|| player.getInventory().checkItem(41233)
							|| player.getInventory().checkItem(41235)
							|| player.getInventory().checkItem(41238)
							|| player.getInventory().checkItem(41239)
							|| player.getInventory().checkItem(41240)) {
						htmlid = "jpe0158";
					}
				}
				}
				break;
				case 71023: { // 骨细工师
				if (player.getLevel() < 12) {
					htmlid = "jpe0145";
				} else if (player.getLevel() >= 12 && player.getLevel() < 25) {
					if (player.getInventory().checkItem(41233)
							|| player.getInventory().checkItem(41234)) {
						htmlid = "jpe0143";	
					} else if (player.getInventory().checkItem(41238)
							|| player.getInventory().checkItem(41239)
							|| player.getInventory().checkItem(41240)) {
						htmlid = "jpe0147";
					} else if (player.getInventory().checkItem(41235)
							|| player.getInventory().checkItem(41236)
							|| player.getInventory().checkItem(41237)) {
						htmlid = "jpe0144";
					}
				}
				}
				break;
				case 71020: { // 
				if (player.getLevel() < 12) {
					htmlid = "jpe0125";
				} else if (player.getLevel() >= 12 && player.getLevel() < 25) {
					if (player.getInventory().checkItem(41231)) {
						htmlid = "jpe0123";	
					} else if (player.getInventory().checkItem(41232)
							|| player.getInventory().checkItem(41233)
							|| player.getInventory().checkItem(41234)
							|| player.getInventory().checkItem(41235)
							|| player.getInventory().checkItem(41238)
							|| player.getInventory().checkItem(41239)
							|| player.getInventory().checkItem(41240)) {
						htmlid = "jpe0126";
					}
				}
				}
				break;
				case 71019: { // 弟子
				if (player.getLevel() < 12) {
					htmlid = "jpe0114";
				} else if (player.getLevel() >= 12 && player.getLevel() < 25) {
					if (player.getInventory().checkItem(41239)) { // 手纸
						htmlid = "jpe0113";
					} else {
						htmlid = "jpe0111";
					}
				}
				}
				break;
				case 71018: { // 
				if (player.getLevel() < 12) {
					htmlid = "jpe0133";
				} else if (player.getLevel() >= 12 && player.getLevel() < 25) {
					if (player.getInventory().checkItem(41240)) { // 手纸
						htmlid = "jpe0132";
					} else {
						htmlid = "jpe0131";
					}
				}
				}
				break;
				case 71025: { // 
				if (player.getLevel() < 10) {
					htmlid = "jpe0086";
				} else if (player.getLevel() >= 10 && player.getLevel() < 25) {
					if (player.getInventory().checkItem(41226)) { // 药
						htmlid = "jpe0084";
					} else if (player.getInventory().checkItem(41225)) { // 发注书
						htmlid = "jpe0083";
					} else if (player.getInventory().checkItem(40653)
							|| player.getInventory().checkItem(40613)) { // 赤键黑键
						htmlid = "jpe0081";
					}
				}
				}
				break;
				/*删除case 70512: { // 治疗师（歌岛 村中）
				if (player.getLevel() >= 25) {
					htmlid = "jpe0102";
				}
				}
				break;
				case 70514: { // 师
				if (player.getLevel() >= 25) {
					htmlid = "jpe0092";
				}
				}
				break;删除*/
				case 71038: { // 长老 
				if (player.getInventory().checkItem(41060)) { // 推荐书
					if (player.getInventory().checkItem(41090) // 
							|| player.getInventory().checkItem(41091) // -
							|| player.getInventory().checkItem(41092)) { // 
						htmlid = "orcfnoname7";
					} else {
						htmlid = "orcfnoname8";
					}
				} else {
					htmlid = "orcfnoname1";
				}
			}
			break;
			case 71040: { // 调查团长  
				if (player.getInventory().checkItem(41060)) { // 推荐书
					if (player.getInventory().checkItem(41065)) { // 调查团证书
						if (player.getInventory().checkItem(41086) // 根
								|| player.getInventory().checkItem(41087) // 表皮
								|| player.getInventory().checkItem(41088) // 叶
								|| player.getInventory().checkItem(41089)) { // 木枝
							htmlid = "orcfnoa6";
						} else {
							htmlid = "orcfnoa5";
						}
					} else {
						htmlid = "orcfnoa2";
					}
				} else {
					htmlid = "orcfnoa1";
				}
			}
			break;
			case 71041: { //  
				if (player.getInventory().checkItem(41060)) { // 推荐书
					if (player.getInventory().checkItem(41064)) { // 调查团证书
						if (player.getInventory().checkItem(41081) // 
								|| player.getInventory().checkItem(41082) // 
								|| player.getInventory().checkItem(41083) // 
								|| player.getInventory().checkItem(41084) // 
								|| player.getInventory().checkItem(41085)) { // 予言者
							htmlid = "orcfhuwoomo2";
						} else {
							htmlid = "orcfhuwoomo8";
						}
					} else {
						htmlid = "orcfhuwoomo1";
					}
				} else {
					htmlid = "orcfhuwoomo5";
				}
			}
			break;
			case 71042: { //  
				if (player.getInventory().checkItem(41060)) { // 推荐书
					if (player.getInventory().checkItem(41062)) { // 调查团证书
						if (player.getInventory().checkItem(41071) // 银盆
								|| player.getInventory().checkItem(41072) // 银烛台
								|| player.getInventory().checkItem(41073) // 键
								|| player.getInventory().checkItem(41074) // 袋
								|| player.getInventory().checkItem(41075)) { // 污发毛
							htmlid = "orcfbakumo2";
						} else {
							htmlid = "orcfbakumo8";
						}
					} else {
						htmlid = "orcfbakumo1";
					}
				} else {
					htmlid = "orcfbakumo5";
				}
			}
			break;
			case 71043: { // - 
				if (player.getInventory().checkItem(41060)) { // 推荐书
					if (player.getInventory().checkItem(41063)) { // 调查团证书
						if (player.getInventory().checkItem(41076) // 污地
								|| player.getInventory().checkItem(41077) // 污水
								|| player.getInventory().checkItem(41078) // 污火
								|| player.getInventory().checkItem(41079) // 污风
								|| player.getInventory().checkItem(41080)) { // 污精灵
							htmlid = "orcfbuka2";
						} else {
							htmlid = "orcfbuka8";
						}
					} else {
						htmlid = "orcfbuka1";
					}
				} else {
					htmlid = "orcfbuka5";
				}
			}
			break;
			case 71044: { // - 
				if (player.getInventory().checkItem(41060)) { // 推荐书
					if (player.getInventory().checkItem(41061)) { // 调查团证书
						if (player.getInventory().checkItem(41066) // 污根
								|| player.getInventory().checkItem(41067) // 污枝
								|| player.getInventory().checkItem(41068) // 污拔壳
								|| player.getInventory().checkItem(41069) // 污
								|| player.getInventory().checkItem(41070)) { // 污妖精羽
							htmlid = "orcfkame2";
						} else {
							htmlid = "orcfkame8";
						}
					} else {
						htmlid = "orcfkame1";
					}
				} else {
					htmlid = "orcfkame5";
				}
			}
			break;
			case 71055: { // （海贼岛秘密）
				if (player.getQuest().get_step(L1Quest.QUEST_RESTA)
						== 3) {
					htmlid = "lukein13";
				} else if (player.getQuest().get_step(L1Quest.QUEST_LUKEIN1)
						== L1Quest.QUEST_END
						&& player.getQuest().get_step(L1Quest.QUEST_RESTA)
						== 2
						&& player.getInventory().checkItem(40631)) {
					htmlid = "lukein10";
				} else if (player.getQuest().get_step(L1Quest.QUEST_LUKEIN1)
						== L1Quest.QUEST_END) {
					htmlid = "lukein0";
				} else if (player.getQuest().get_step(L1Quest.QUEST_LUKEIN1)
						== 11) {
					if (player.getInventory().checkItem(40716)) {
						htmlid = "lukein9";
					}
				} else if (player.getQuest().get_step(L1Quest.QUEST_LUKEIN1)
						>= 1
						&& player.getQuest().get_step(L1Quest.QUEST_LUKEIN1)
						<= 10) {
					htmlid = "lukein8";
				}
			}
			break;
			case 71063: { // 小箱-１番目（海贼岛秘密）
				if (player.getQuest().get_step(L1Quest.QUEST_TBOX1)
						== L1Quest.QUEST_END) {
				} else if (player.getQuest().get_step(L1Quest.QUEST_LUKEIN1)
						== 1) {
					htmlid = "maptbox";
				}
			}
			break;
			case 71064: { // 小箱-2番目-ｂ地点（海贼岛秘密）
				if (player.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 2) {
					htmlid = talkToSecondtbox(player);
				}
			}
			break;
			case 71065: { // 小箱-2番目-c地点（海贼岛秘密）
				if (player.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 3) {
					htmlid = talkToSecondtbox(player);
				}
			}
			break;
			case 71066: { // 小箱-2番目-d地点（海贼岛秘密）
				if (player.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 4) {
					htmlid = talkToSecondtbox(player);
				}
			}
			break;
			case 71067: { // 小箱-3番目-e地点（海贼岛秘密）
				if (player.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 5) {
					htmlid = talkToThirdtbox(player);
				}
			}
			break;
			case 71068: { // 小箱-3番目-f地点（海贼岛秘密）
				if (player.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 6) {
					htmlid = talkToThirdtbox(player);
				}
			}
			break;
			case 71069: { // 小箱-3番目-g地点（海贼岛秘密）
				if (player.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 7) {
					htmlid = talkToThirdtbox(player);
				}
			}
			break;
			case 71070: { // 小箱-3番目-h地点（海贼岛秘密）
				if (player.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 8) {
					htmlid = talkToThirdtbox(player);
				}
			}
			break;
			case 71071: { // 小箱-3番目-i地点（海贼岛秘密）
				if (player.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 9) {
					htmlid = talkToThirdtbox(player);
				}
			}
			break;
			case 71072: { // 小箱-3番目-j地点（海贼岛秘密）
				if (player.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 10) {
					htmlid = talkToThirdtbox(player);
				}
			}
			break;
			case 71056: { // （消息子）
				if (player.getQuest().get_step(L1Quest.QUEST_RESTA)
						== 4) {
					if (player.getInventory().checkItem(40631)) {
						htmlid = "SIMIZZ11";
					} else {
						htmlid = "SIMIZZ0";
					}
				} else if (player.getQuest().get_step(L1Quest.QUEST_SIMIZZ)
						== 2) {
					htmlid = "SIMIZZ0";
				} else if (player.getQuest().get_step(L1Quest.QUEST_SIMIZZ)
						== L1Quest.QUEST_END) {
					htmlid = "SIMIZZ15";
				} else if (player.getQuest().get_step(L1Quest.QUEST_SIMIZZ)
						== 1) {
					htmlid = "SIMIZZ6";
				}
			}
			break;
			case 71057: { // （宝地图1）
				if (player.getQuest().get_step(L1Quest.QUEST_DOIL)
						== L1Quest.QUEST_END) {
					htmlid = "doil4b";
				}
			}
			break;
			case 71059: { // （宝地图2）
				if (player.getQuest().get_step(L1Quest.QUEST_RUDIAN)
						== L1Quest.QUEST_END) {
					htmlid = "rudian1c";
				} else if (player.getQuest().get_step(L1Quest.QUEST_RUDIAN)
						== 1) {
					htmlid = "rudian7";
				} else if (player.getQuest().get_step(L1Quest.QUEST_DOIL)
						== L1Quest.QUEST_END) {
					htmlid = "rudian1b";
				} else {
					htmlid = "rudian1a";
				}
			}break;
			case 71060: { // （宝地图3）
				if (player.getQuest().get_step(L1Quest.QUEST_RESTA)
						== L1Quest.QUEST_END) {
					htmlid = "resta1e";
				} else if (player.getQuest().get_step(L1Quest.QUEST_SIMIZZ)
						== L1Quest.QUEST_END) {
					htmlid = "resta14";
				} else if (player.getQuest().get_step(L1Quest.QUEST_RESTA)
						== 4) {
					htmlid = "resta13";
				} else if (player.getQuest().get_step(L1Quest.QUEST_RESTA)
						== 3) {
					htmlid = "resta11";
					player.getQuest().set_step(L1Quest.QUEST_RESTA, 4);
				} else if (player.getQuest().get_step(L1Quest.QUEST_RESTA)
						== 2) {
					htmlid = "resta16";
				} else if (player.getQuest().get_step(L1Quest.QUEST_SIMIZZ)
						== 2 
							&& player.getQuest().get_step(L1Quest.
									QUEST_CADMUS) == 1
							|| player.getInventory().checkItem(40647)) {
					htmlid = "resta1a";
				} else if (player.getQuest().get_step(L1Quest.QUEST_CADMUS)
						== 1 
						|| player.getInventory().checkItem(40647)) {
					htmlid = "resta1c";
				} else if (player.getQuest().get_step(L1Quest.QUEST_SIMIZZ)
						== 2) {
					htmlid = "resta1b";
				}
			}break;
			case 71061: { // （宝地图4）
				if (player.getQuest().get_step(L1Quest.QUEST_CADMUS)
						== L1Quest.QUEST_END) {
					htmlid = "cadmus1c";
				} else if (player.getQuest().get_step(L1Quest.QUEST_CADMUS)
						== 3) {
					htmlid = "cadmus8";
				} else if (player.getQuest().get_step(L1Quest.QUEST_CADMUS)
						== 2) {
					htmlid = "cadmus1a";
				} else if (player.getQuest().get_step(L1Quest.QUEST_DOIL)
						== L1Quest.QUEST_END) {
					htmlid = "cadmus1b";
				}
			}break;
			case 71062: { // （宝地图4）
				if (player.getQuest().get_step(L1Quest.QUEST_CADMUS)
						>= 3) {
					htmlid = "kamit2";
				} else if (player.getQuest().get_step(L1Quest.QUEST_CADMUS)
						== 2) {
					htmlid = "kamit1b";
				}
			}break;
			case 71036: { // （真）
				if (player.getQuest().get_step(L1Quest.QUEST_KAMYLA)
						== L1Quest.QUEST_END) {
					htmlid = "kamyla26";
				} else if (player.getQuest().get_step(L1Quest.QUEST_KAMYLA)
						== 4 && player.getInventory().checkItem(40717)) {
					htmlid = "kamyla15";
				} else if (player.getQuest().get_step(L1Quest.QUEST_KAMYLA)
						== 4 ) {
					htmlid = "kamyla14";
				} else if (player.getQuest().get_step(L1Quest.QUEST_KAMYLA)
						== 3 && player.getInventory().checkItem(40630)) {
					htmlid = "kamyla12";
				} else if (player.getQuest().get_step(L1Quest.QUEST_KAMYLA)
						== 3 ) {
					htmlid = "kamyla11";
				} else if (player.getQuest().get_step(L1Quest.QUEST_KAMYLA)
						== 2 && player.getInventory().checkItem(40644)) {
					htmlid = "kamyla9";
				} else if (player.getQuest().get_step(L1Quest.QUEST_KAMYLA)
						== 1 ) {
					htmlid = "kamyla8";
				} else if (player.getQuest().get_step(L1Quest.QUEST_CADMUS)
						==  L1Quest.QUEST_END && player.getInventory()
							.checkItem(40621)) {
					htmlid = "kamyla1";
				}
			}break;
			case 71089: { // （真）
				if (player.getQuest().get_step(L1Quest.QUEST_KAMYLA)
						== 2 ) {
					htmlid = "francu12";
				}
			}break;
			case 71090: { // 试练2（真）
				if (player.getQuest().get_step(L1Quest.QUEST_CRYSTAL)
						== 1 && player.getInventory().checkItem(40620)) {
					htmlid = "jcrystal2";
				} else if (player.getQuest().get_step(L1Quest.QUEST_CRYSTAL)
						== 1){
					htmlid = "jcrystal3";
				}
			}break;
			case 71091: { // 试练3（真）
				if (player.getQuest().get_step(L1Quest.QUEST_CRYSTAL)
						== 2 && player.getInventory().checkItem(40654)) {
					htmlid = "jcrystall2";
				}
			}break;
			case 71074: { // 长老
				if (player.getQuest().get_step(L1Quest.QUEST_LIZARD)
						== L1Quest.QUEST_END) {
					htmlid = "lelder0";
				} else if (player.getQuest().get_step(L1Quest.QUEST_LIZARD)
						== 3  && player.getInventory().checkItem(40634)) {
					htmlid = "lelder12";
				} else if (player.getQuest().get_step(L1Quest.QUEST_LIZARD)
						== 3) {
					htmlid = "lelder11";
				} else if (player.getQuest().get_step(L1Quest.QUEST_LIZARD)
						== 2  && player.getInventory().checkItem(40633)) {
					htmlid = "lelder7";
				} else if (player.getQuest().get_step(L1Quest.QUEST_LIZARD)
						== 2) {
					htmlid = "lelder7b";
				} else if (player.getQuest().get_step(L1Quest.QUEST_LIZARD)
						== 1) {
					htmlid = "lelder7b";
				} else if (player.getLevel() >= 40) {
					htmlid = "lelder1";
				}
			}break;
			case 71075: { // 疲果
				if (player.getQuest().get_step(L1Quest.QUEST_LIZARD)
						== 1) {
					htmlid = "llizard1b";
				} else {
				}
			}break;
			case 71076: { // 
				if (player.getQuest().get_step(L1Quest.QUEST_LIZARD)
						== L1Quest.QUEST_END) {
					htmlid = "ylizardb";
				} else {
				}
			}break;
			/*删除case 80079: { // 
				if (player.getInventory().checkItem(41314)) { // 占星术师守
					if (player.getInventory().checkItem(41312)) {
						htmlid = "keplisha3";
					} else {
						htmlid = "keplisha6";
					}
				} else if (player.getInventory().checkItem(41313)) { // 占星术师玉
					if (player.getInventory().checkItem(41312)) {
						htmlid = "keplisha2";
					} else {
						htmlid = "keplisha6";
					}
				} else if (player.getInventory().checkItem(41312)) { // 占星术师壶
					htmlid = "keplisha4";
				}
			}
			break;
			case 80102: { // 
				if (player.getInventory().checkItem(41329)) {
					htmlid = "fillis3";
				}
			}
			break;删除*/
			case 70518: { //提奥兑换黑钥匙 
				if (player.isCrown() || player.isWizard()) {//王族、法师
					htmlid = "tio4";
				} else {
					htmlid = "tio";
				}
			}
			break;
			case l1j.william.New_Id.Npc_AJ_5_2: { // 拉奇牧师 
				if (player.getInventory().checkItem(l1j.william.New_Id.Armor_AJ_1_1)) {// 布拉斯耳环
					htmlid = "nuevent020";
				} else {
					htmlid = "nuevent021";
				}
			}
			break;
			}
//			System.out.println("弹出对话框："+htmlid);
			//变更成switch  end
			// html表示送信
			if (htmlid != null) { // htmlid指定场合
				if (htmldata != null) { // html指定场合表示
					player.sendPackets(new S_NPCTalkReturn(objid, htmlid,
							htmldata));
				} else {
					player.sendPackets(new S_NPCTalkReturn(objid, htmlid));	
				}
			} else {
				if (player.getLawful() < -1000) { // 
					player.sendPackets(new S_NPCTalkReturn(talking, objid, 2));
				} else {
					player.sendPackets(new S_NPCTalkReturn(talking, objid, 1));
				}
			}
			
			//对话时停止移动 
			synchronized (this) {
				if (_monitor != null) {
					_monitor.cancel();
				}
				setRest(true);
				_monitor = new RestMonitor();
				_restTimer.schedule(_monitor, REST_MILLISEC);
			}
			//对话时停止移动  end
		}
	}

	private static String talkToTownadviser(L1PcInstance pc, int town_id) {
		String htmlid;
		if (pc.getHomeTownId() == town_id
				&& TownTable.getInstance().isLeader(pc, town_id)) {
			htmlid = "secretary1";
		} else {
			htmlid = "secretary2";
		}

		return htmlid;
	}

	private static String talkToTownmaster(L1PcInstance pc, int town_id) {
		String htmlid;
		if (pc.getHomeTownId() == town_id) {
			htmlid = "hometown";
		} else {
			htmlid = "othertown";
		}
		return htmlid;
	}

	@Override
	public void onFinalAction(L1PcInstance player, String action) {
	}

	public void doFinalAction(L1PcInstance player) {
	}

	private boolean checkHasCastle(L1PcInstance player, int castle_id) {
		if (player.getClanid() != 0) { // 所属中
			L1Clan clan = L1World.getInstance().getClan(player.getClanname());
			if (clan != null) {
				if (clan.getCastleId() == castle_id) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean checkClanLeader(L1PcInstance player) {
		if (player.isCrown()) { // 君主
			L1Clan clan = L1World.getInstance().getClan(player.getClanname());
			if (clan != null) {
				if (player.getId() == clan.getLeaderId()) {
					return true;
				}
			}
		}
		return false;
	}

	private int getNecessarySealCount(L1PcInstance pc) {
		int rulerCount = 0;
		int necessarySealCount = 10;
		if (pc.getInventory().checkItem(40917)) { // 地支配者
			rulerCount++;
		}
		if (pc.getInventory().checkItem(40920)) { // 风支配者
			rulerCount++;
		}
		if (pc.getInventory().checkItem(40918)) { // 水支配者
			rulerCount++;
		}
		if (pc.getInventory().checkItem(40919)) { // 火支配者
			rulerCount++;
		}
		if (rulerCount == 0) {
			necessarySealCount = 10;
		} else if (rulerCount == 1) {
			necessarySealCount = 100;
		} else if (rulerCount == 2) {
			necessarySealCount = 200;
		} else if (rulerCount == 3) {
			necessarySealCount = 500;
		}
		return necessarySealCount;
	}

	private void createRuler(L1PcInstance pc, int attr, int sealCount) {
		// 1.地属性,2.火属性,4.水属性,8.风属性
		int rulerId = 0;
		int protectionId = 0;
		int sealId = 0;
		//变更成 switch 
		switch (attr)
		{
			case 1: {
				rulerId = 40917;
				protectionId = 40909;
				sealId = 40913;
			}
			break;
			case 2: {
				rulerId = 40919;
				protectionId = 40911;
				sealId = 40915;
			}
			break;
			case 4: {
				rulerId = 40918;
				protectionId = 40910;
				sealId = 40914;
			}
			break;
			case 8: {
				rulerId = 40920;
				protectionId = 40912;
				sealId = 40916;
			}
			break;
		}
		//变更成 switch  end
		pc.getInventory().consumeItem(protectionId, 1);
		pc.getInventory().consumeItem(sealId, sealCount);
		L1ItemInstance item = pc.getInventory().storeItem(rulerId, 1);
		if (item != null) {
			pc.sendPackets(new S_ServerMessage(143,
					getNpcTemplate().get_name(), item.getLogName())); // \f1%0%1。
		}
	}

	private String talkToDoromond(L1PcInstance pc) {
		String htmlid = "";
		if (pc.getQuest().get_step(L1Quest.QUEST_DOROMOND) == 0) {
			htmlid = "jpe0011";
		} else if (pc.getQuest().get_step(L1Quest.QUEST_DOROMOND) == 1) {
			htmlid = "jpe0015";
		}

		return htmlid;
	}

	private String talkToAlex(L1PcInstance pc) {
		String htmlid = "";
		if (pc.getLevel() < 3) {
			htmlid = "jpe0021";
		} else if (pc.getQuest().get_step(L1Quest.QUEST_DOROMOND) < 2) {
			htmlid = "jpe0022";
		} else if (pc.getQuest().get_step(L1Quest.QUEST_AREX) == L1Quest.QUEST_END) {
			htmlid = "jpe0023";
		} else if (pc.getLevel() >= 10 && pc.getLevel() < 25) {
			if (pc.getInventory().checkItem(41227)) { // 绍介状
				htmlid = "jpe0023";
			} else if (pc.isCrown()) {
				htmlid = "jpe0024p";
			} else if (pc.isKnight()) {
				htmlid = "jpe0024k";
			} else if (pc.isElf()) {
				htmlid = "jpe0024e";
			} else if (pc.isWizard()) {
				htmlid = "jpe0024w";
			} else if (pc.isDarkelf()) {
				htmlid = "jpe0024d";
			}
		} else if (pc.getLevel() > 25) {
			htmlid = "jpe0023";
		} else {
			htmlid = "jpe0021";
		}
		return htmlid;
	}

	private String talkToAlexInTrainingRoom(L1PcInstance pc) {
		String htmlid = "";
		if (pc.getLevel() < 3) {
			htmlid = "jpe0031";
		} else {
			if (pc.getQuest().get_step(L1Quest.QUEST_DOROMOND) < 2) {
				htmlid = "jpe0035";
			} else {
				htmlid = "jpe0036";
			}
		}

		return htmlid;
	}

	private String cancellation(L1PcInstance pc) {
		String htmlid = "";
		if (pc.getLevel() < 13) {
			htmlid = "jpe0161";
		} else {
			htmlid = "jpe0162";
		}

		return htmlid;
	}

	private String talkToRuba(L1PcInstance pc) {
		String htmlid = "";

		/*删除if (pc.isCrown() || pc.isWizard()) {
			htmlid = "en0101";
		} else if (pc.isKnight() || pc.isElf() || pc.isDarkelf()) {
			htmlid = "en0102";
		}删除*/
		//变更
		if (pc.isCrown() || pc.isWizard()) {//王族、法师
			htmlid = "ruba";
		} else if (pc.isKnight()) {//骑士
			htmlid = "ruba4";
		} else if (pc.isElf()) {//妖精
			htmlid = "ruba5";
		} else if (pc.isDarkelf()) {//黑妖
			htmlid = "ruba6";
		}
		//变更 end

		return htmlid;
	}

	private String talkToSIGuide(L1PcInstance pc) {
		String htmlid = "";
		if (pc.getLevel() < 3) {
			htmlid = "en0301";
		} else if (pc.getLevel() >= 3 && pc.getLevel() < 7) {
			htmlid = "en0302";
		} else if (pc.getLevel() >= 7 && pc.getLevel() < 9) {
			htmlid = "en0303";
		} else if (pc.getLevel() >= 9 && pc.getLevel() < 12) {
			htmlid = "en0304";
		} else if (pc.getLevel() >= 12 && pc.getLevel() < 13) {
			htmlid = "en0305";
		} else if (pc.getLevel() >= 13 && pc.getLevel() < 25) {
			htmlid = "en0306";
		} else {
			htmlid = "en0307";
		}
		return htmlid;
	}

	private String talkToPopirea(L1PcInstance pc) {
		String htmlid = "";
		if (pc.getLevel() < 25) {
			htmlid = "jpe0041";
			if (pc.getInventory().checkItem(41209)
					|| pc.getInventory().checkItem(41210)
					|| pc.getInventory().checkItem(41211)
					|| pc.getInventory().checkItem(41212)) {
				htmlid = "jpe0043";
			}
			if (pc.getInventory().checkItem(41213)) {
				htmlid = "jpe0044";
			}
		} else {
			htmlid = "jpe0045";
		}
		return htmlid;
	}

	private String talkToSecondtbox(L1PcInstance pc) {
		String htmlid = "";
		if (pc.getQuest().get_step(L1Quest.QUEST_TBOX1) ==  L1Quest.QUEST_END) {
			if (pc.getInventory().checkItem(40701)) {
				htmlid = "maptboxa";
			} else {
				htmlid = "maptbox0";
			}
		} else {
			htmlid = "maptbox0";
		}
		return htmlid;
	}

	private String talkToThirdtbox(L1PcInstance pc) {
		String htmlid = "";
		if (pc.getQuest().get_step(L1Quest.QUEST_TBOX2) ==  L1Quest.QUEST_END) {
			if (pc.getInventory().checkItem(40701)) {
				htmlid = "maptboxd";
			} else {
				htmlid = "maptbox0";
			}
		} else {
			htmlid = "maptbox0";
		}
		return htmlid;
	}
	
	// NPC说话计时器 
	public class Talk extends TimerTask {
		@Override
		public void run() {
			_talk = null;
		}
	}
	
	public class RestMonitor extends TimerTask {
		@Override
		public void run() {
			setRest(false);
		}
	}
	// NPC说话计时器  end
}
