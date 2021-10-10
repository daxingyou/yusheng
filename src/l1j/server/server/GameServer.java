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
package l1j.server.server;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import l1j.server.Config;
import l1j.server.server.command.GMCommandsConfig;
import l1j.server.server.datatables.*;
import l1j.server.server.datatables.lock.CharBookConfigReading;
import l1j.server.server.datatables.lock.CharBookReading;
import l1j.server.server.datatables.lock.CharSkillReading;
import l1j.server.server.datatables.lock.CharacterAdenaTradeReading;
import l1j.server.server.datatables.lock.CharaterTradeReading;
import l1j.server.server.datatables.lock.MailReading;
import l1j.server.server.datatables.lock.ShouBaoReading;
import l1j.server.server.datatables.lock.ShouShaReading;
import l1j.server.server.datatables.lock.SpawnBossReading;
import l1j.server.server.mina.LineageClient;
import l1j.server.server.model.Dungeon;
import l1j.server.server.model.ElementalStoneGenerator;
import l1j.server.server.model.Getback;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1DeleteItemOnGround;
import l1j.server.server.model.L1NpcRegenerationTimer;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1QuestInstance;
import l1j.server.server.model.gametime.L1GameTimeClock;
import l1j.server.server.model.item.L1TreasureBox;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.model.trap.L1WorldTraps;
import l1j.server.server.storage.GamblingLock;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.timecontroller.AIcheckTimer;
import l1j.server.server.timecontroller.CNTimer;
import l1j.server.server.timecontroller.CheckWar;
import l1j.server.server.timecontroller.LawfulTimer;
import l1j.server.server.timecontroller.NpcSpawnBossTimer;
import l1j.server.server.timecontroller.NpcWorkTimer;
import l1j.server.server.timecontroller.PetSaveTimer;
import l1j.server.server.timecontroller.ServerItemUserTimer;
import l1j.server.server.timecontroller.SummonTimer;
import l1j.server.server.timecontroller.updateDBTimer;
import l1j.server.server.timecontroller.pc.HprMprTimerCrown;
import l1j.server.server.timecontroller.pc.HprMprTimerDarkElf;
import l1j.server.server.timecontroller.pc.HprMprTimerDragonKnight;
import l1j.server.server.timecontroller.pc.HprMprTimerElf;
import l1j.server.server.timecontroller.pc.HprMprTimerIllusionist;
import l1j.server.server.timecontroller.pc.HprMprTimerKnight;
import l1j.server.server.timecontroller.pc.HprMprTimerWizard;
import l1j.server.server.timecontroller.pc.PartyTimer;
import l1j.server.server.timecontroller.pc.UpdateObjectCTimer;
import l1j.server.server.timecontroller.pc.UpdateObjectDTimer;
import l1j.server.server.timecontroller.pc.UpdateObjectETimer;
import l1j.server.server.timecontroller.pc.UpdateObjectHTimer;
import l1j.server.server.timecontroller.pc.UpdateObjectKTimer;
import l1j.server.server.timecontroller.pc.UpdateObjectLTimer;
import l1j.server.server.timecontroller.pc.UpdateObjectWTimer;
import l1j.server.server.world.L1World;
import l1j.william.ArmorUpgrade;
import l1j.william.BadNamesTable;
import l1j.william.ItemPrice;
import l1j.william.ItemSummon;
//import l1j.william.L1GameReStart;
import l1j.william.LastOnline;
import l1j.william.MagicCrystalItem;
import l1j.william.SystemMessage;
import l1j.william.TeleportScroll;
import l1j.william.WeaponSkill;

// Referenced classes of package l1j.server.server:
// ClientThread, Logins, RateTable, IdFactory,
// LoginController, GameTimeController, Announcements,
// MobTable, SpawnTable, SkillsTable, PolyTable,
// TeleportLocations, ShopTable, NPCTalkDataTable, NpcSpawnTable,
// IpTable, Shutdown, NpcTable, MobGroupTable

public class GameServer{
	//private ServerSocket _serverSocket;

	// private Logins _logins;
	private LoginController _loginController;
	private int chatlvl;

/*	@Override
	public void run() {
		System.out.println("记忆体使用: " + SystemUtil.getUsedMemoryMB() + "MB");
		System.out.println("等待玩家连线中...");
		while (true) {
			try {
				Socket socket = _serverSocket.accept();
				System.out.println("尝试连线的IP: " + socket.getInetAddress());
				String host = socket.getInetAddress().getHostAddress();
				if (IpTable.getInstance().isBannedIp(host)) {
					_log.info("banned IP(" + host + ")");
				} else {
					ClientThread client = new ClientThread(socket);
					GeneralThreadPool.getInstance().execute(client);
				}
			} catch (IOException ioexception) {
			}
		}
	}*/

	private static GameServer _instance;

	private GameServer() {
		//super("GameServer");
	}

	public static GameServer getInstance() {
		if (_instance == null) {
			_instance = new GameServer();
		}
		return _instance;
	}

	public void initialize() throws Exception {
//		String s = Config.GAME_SERVER_HOST_NAME;
		double rateXp = Config.RATE_XP;
		double LA = Config.RATE_LA;
		double rateKarma = Config.RATE_KARMA;
		double rateDropItems = Config.RATE_DROP_ITEMS;
		double rateDropAdena = Config.RATE_DROP_ADENA;

		chatlvl = Config.GLOBAL_CHAT_LEVEL;
/*		_port = Config.GAME_SERVER_PORT;*/
		/*if (!"*".equals(s)) {
			InetAddress inetaddress = InetAddress.getByName(s);
			inetaddress.getHostAddress();
			_serverSocket = new ServerSocket(_port, 50, inetaddress);
			System.out.println("伺服器设定：伺服器启动");
		} else {
			_serverSocket = new ServerSocket(_port);
			System.out.println("伺服器设定：伺服器启动");
		}*/
		DBClearAllUtil dBClearAllUtil = new DBClearAllUtil();
		if(Config.DB_ClEAR_USER_DATA){
			if(dBClearAllUtil.getDBClearAllState()){
				System.out.println("一键开区，开始清空用户数据库表");
				dBClearAllUtil.start();
			}else {
				System.out.println();
				System.out.println("请在 newDistrictSet表 中id=1记录,state值更改为true,重启后即可删除用户全部数据,一键开区专用");
				System.out.println();
			}
		}

		System.out.println("伺服器语系：" + Config.LANGUAGE);
		System.out.println("-------------------------------------------------------------------");
		System.out.println("经验值：" + (rateXp) + " 倍");
		System.out.println("正义值：" + (LA) + " 倍");
		System.out.println("友好度：" + (rateKarma) + " 倍");
		System.out.println("负重率：" + (Config.RATE_WEIGHT_LIMIT) + " 倍");
		System.out.println("掉宝率：" + (rateDropItems) + " 倍");
		System.out.println("取得金币：" + (rateDropAdena) + " 倍");
		System.out.println("宠物经验值：" + (Config.PET_RATE_XP) + " 倍");
		System.out.println("宠物等级上限：" + (Config.PET_LEVEL));
		System.out.println("冲装率：武器 " + (Config.ENCHANT_CHANCE_WEAPON) + "%  /  防具 " + (Config.ENCHANT_CHANCE_ARMOR) + "%");
		//调整能力值上限 
		System.out.println("-------------------------------------------------------------------");
		if (Config.ALT_NONPVP) { // Non-PvP设定
			System.out.println("Non-PvP：关闭");
		} else {
			System.out.println("Non-PvP：开启");
		}
		if (Config.ALL_ITEM_SELL) {
			System.out.println("全道具贩卖：开启");
		} else {
			System.out.println("全道具贩卖：关闭");
		}
		if (Config.ALT_REVIVAL_POTION) {
			System.out.println("转生药水取得：开启");
		} else {
			System.out.println("转生药水取得：关闭");
		}
		if (Config.REVIVAL_POTION == 1) {
			System.out.println("转生血魔保留：10%");
		} else if (Config.REVIVAL_POTION == 2) {
			System.out.println("转生血魔保留：20%");
		} else if (Config.REVIVAL_POTION == 3) {
			System.out.println("转生血魔保留：30%");
		} else if (Config.REVIVAL_POTION == 4) {
			System.out.println("转生血魔保留：40%");
		} else if (Config.REVIVAL_POTION == 5) {
			System.out.println("转生血魔保留：50%");
		} else if (Config.REVIVAL_POTION == 6) {
			System.out.println("转生血魔保留：60%");
		} else if (Config.REVIVAL_POTION == 7) {
			System.out.println("转生血魔保留：70%");
		} else if (Config.REVIVAL_POTION == 8) {
			System.out.println("转生血魔保留：80%");
		} else if (Config.REVIVAL_POTION == 9) {
			System.out.println("转生血魔保留：90%");
		} else {
			System.out.println("转生血魔保留：100%");
		}
		if (Config.ALT_WHO_COMMAND) {
			System.out.println("线上查询功能：开启");
		} else {
			System.out.println("线上查询功能：开启");
		}
		System.out.println("TGG开关当前状态为:"+Config.LOGINS_TO_AUTOENTICATION);
		System.out.println("全体聊天频道使用等级：" + (chatlvl));
		System.out.println("-------------------------------------------------------------------");
		int maxOnlineUsers = Config.MAX_ONLINE_USERS;
		System.out.println("版本V"+Config.VER+"连线人数限制: 最大 " + (maxOnlineUsers) + " 人");
		IdFactory.getInstance();
		MailIdFactory.get().load();
		//SkillIdFactory.getInstance();
		L1WorldMap.getInstance();
		_loginController = LoginController.getInstance();
		_loginController.setMaxAllowedOnlinePlayers(maxOnlineUsers);

		// 状态
		CharacterTable.clearOnlineStatus();

		// 时间时计
		L1GameTimeClock.init();

/*		// 伺服器自动重启 
		if (Config.REST_TIME != 0) {
			L1GameReStart.init();
		}*/
		// 伺服器自动重启  end
		// UB
		UBSpawnItemTable.getInstance().load();
	
		UbTimeController ubTimeContoroller = UbTimeController.getInstance();
		GeneralThreadPool.getInstance().execute(ubTimeContoroller);

		// 战争
		WarTimeController warTimeController = WarTimeController.getInstance();
		GeneralThreadPool.getInstance().execute(warTimeController);

		// 船
		ShipTimeController shipTimeController = ShipTimeController
				.getInstance();
		GeneralThreadPool.getInstance().execute(shipTimeController);

		// 精灵石生成
		if (Config.ELEMENTAL_STONE_AMOUNT > 0) {
			ElementalStoneGenerator elementalStoneGenerator
					= ElementalStoneGenerator.getInstance();
			GeneralThreadPool.getInstance().execute(elementalStoneGenerator);
		}

		// 
		HomeTownTimeController.getInstance();

		// 竞卖
		AuctionTimeController auctionTimeController = AuctionTimeController
				.getInstance();
		GeneralThreadPool.getInstance().execute(auctionTimeController);

		// 钓
		FishingTimeController fishingTimeController = FishingTimeController
				.getInstance();
		GeneralThreadPool.getInstance().execute(fishingTimeController);

		Announcements.getInstance();
		SprTable.get().load();
		NpcTable.getInstance();
		L1DeleteItemOnGround deleteitem = new L1DeleteItemOnGround();
		deleteitem.onAction();

		if (!NpcTable.getInstance().isInitialized()) {
			throw new Exception("Could not initialize the npc table");
		}
		MobGroupTable.getInstance();
		SpawnTable.getInstance();
		// 召唤BOSS资料
		SpawnBossReading.get().load();
		SkillsTable.getInstance();
		CharSkillReading.get().load();
		PolyTable.getInstance().load();
		ItemTable.getInstance();
		DropTable.getInstance(); 
		DropItemTable.getInstance();
		ShopTable.getInstance();
		NPCTalkDataTable.getInstance();
		L1World.getInstance();
		L1WorldTraps.getInstance();
		Dungeon.getInstance();
		NpcSpawnTable.getInstance();
		IpTable.getInstance();
		MapsTable.getInstance();
		MapsNotAllowedTable.getInstance();//加载禁止的地图
		UBSpawnTable.getInstance();
		PetTable.getInstance();
		ClanTable.getInstance();
		PcTable.getInstance();
		CastleTable.getInstance();
		L1CastleLocation.setCastleTaxRate(); // CastleTable初期化后
		GetBackRestartTable.getInstance();
		DoorSpawnTable.getInstance();
		GeneralThreadPool.getInstance();
		L1NpcRegenerationTimer.getInstance();
		ChatLogTable.getInstance();
		WeaponSkillTable.getInstance();
		NpcActionTable.load();
		ArmorSetTable.getInstance();
		GMCommandsConfig.load();
		Getback.loadGetBack();
		PetTypeTable.load();
		//L1BossCycle.load();
		L1TreasureBox.load();
		TeleportScroll.getInstance(); // 传送卷轴 
		WeaponSkill.getInstance(); // 魔法武器扩充 
		LastOnline.getInstance(); // 最后在线时间 
		MagicCrystalItem.getInstance(); // 溶解剂 
		//PlayerSpeed.getInstance(); // 速度限制 
		SystemMessage.getInstance(); // 讯息 
		ItemSummon.getInstance(); // 召唤道具 
		ItemPrice.getInstance(); // 物品价钱 
		ArmorUpgrade.getInstance(); // 装备强化 
		BadNamesTable.getInstance(); // 限制名称 
		TownSetTable.get().load();//村庄管理
		CenterTable.getInstance();
		IPCountTable.get().load();
		PetItemTable.getInstance();
		FurnitureSpawnTable.getInstance();
		// 赛狗赌场
		IdLoad.getInstance();
		GamblingLock.create().load();// 赌场纪录
		Announcecycle.getInstance();
		CharBookConfigReading.get().load();
		MailReading.get().load();
		CharacterAdenaTradeReading.get().load();
/*		if (Config.GamStart) {
			T_Gambling.getStart();
		}*/
/*		GamblingTime gam = new GamblingTime();
		gam.startGambling();*/
		// 在黄昏山脉召唤一名调查员(巨人) 
		spawn(l1j.william.New_Id.Npc_AJ_2_9, 34242, 33356, 5, (short) 4);
		// 在黄昏山脉召唤一名调查员(巨人)  end
		
		// 在妖森召唤一名安迪亚 
		spawn(l1j.william.New_Id.Npc_AJ_2_11, 32944, 32280, 5, (short) 4);
		// 在妖森召唤一名安迪亚  end
		final ServerRestartTimer autoRestart = new ServerRestartTimer();
		autoRestart.start();
		Thread.sleep(50);
		final NpcWorkTimer workTimer = new NpcWorkTimer();
		workTimer.start();
		Thread.sleep(50);
		final CNTimer cnTimer = new CNTimer();
		cnTimer.start();
		Thread.sleep(50);
		final NpcSpawnBossTimer npcSpawnBossTimer = new NpcSpawnBossTimer();
		npcSpawnBossTimer.start();
		Thread.sleep(50);
		final CheckWar checkWar = new CheckWar();
		checkWar.start();
		Thread.sleep(50);
		final PetSaveTimer petSaveTimer = new PetSaveTimer();
		petSaveTimer.start();
		Thread.sleep(50);
		// PC 可见物更新处理 时间轴 XXX
		final UpdateObjectCTimer objectCTimer = new UpdateObjectCTimer();
		objectCTimer.start();
		final UpdateObjectDTimer objectDTimer = new UpdateObjectDTimer();
		objectDTimer.start();
		final UpdateObjectETimer objectETimer = new UpdateObjectETimer();
		objectETimer.start();
		final UpdateObjectKTimer objectKTimer = new UpdateObjectKTimer();
		objectKTimer.start();
		final UpdateObjectWTimer objectWTimer = new UpdateObjectWTimer();
		objectWTimer.start();
		// 可见物品更新 龙骑士
		final UpdateObjectLTimer objectLTimer = new UpdateObjectLTimer();
		objectLTimer.start();
		// 可见物品更新 龙骑士
		// 可见物品更新 幻术师
		final UpdateObjectHTimer objectHTimer = new UpdateObjectHTimer();
		objectHTimer.start();
		// 可见物品更新 幻术师
		Thread.sleep(50);// 延迟
		final HprMprTimerCrown hprMprTimerCrown = new HprMprTimerCrown();
		hprMprTimerCrown.start();
		Thread.sleep(50);// 延迟
		final HprMprTimerDarkElf hprMprTimerDarkElf = new HprMprTimerDarkElf();
		hprMprTimerDarkElf.start();
		Thread.sleep(50);// 延迟
		final HprMprTimerElf hprMprTimerElf = new HprMprTimerElf();
		hprMprTimerElf.start();
		Thread.sleep(50);// 延迟
		final HprMprTimerKnight hprMprTimerKnight = new HprMprTimerKnight();
		hprMprTimerKnight.start();
		Thread.sleep(50);// 延迟
		final HprMprTimerWizard hprMprTimerWizard = new HprMprTimerWizard();
		hprMprTimerWizard.start();
		Thread.sleep(50);// 延迟
		// HPMP更新 龙骑士
		final HprMprTimerDragonKnight hprMprTimerDragonKnight = new HprMprTimerDragonKnight();
		hprMprTimerDragonKnight.start();
		Thread.sleep(50);// 延迟
		// HPMP更新 龙骑士
		
		// HPMP更新 幻术师
		final HprMprTimerIllusionist hprMprTimerIllusionist = new HprMprTimerIllusionist();
		hprMprTimerIllusionist.start();
		Thread.sleep(50);// 延迟
		// HPMP更新 幻术师
		
		final PartyTimer partyTimer = new PartyTimer();
		partyTimer.start();
		Thread.sleep(50);// 延迟
		final SummonTimer summonTimer = new SummonTimer();
		summonTimer.start();
		Thread.sleep(50);// 延迟
		final AIcheckTimer aIcheckTimer = new AIcheckTimer();
		aIcheckTimer.start();
		Thread.sleep(50);// 延迟
		final LawfulTimer clanghTimer = new LawfulTimer();
		clanghTimer.start();
		Thread.sleep(50);// 延迟
		final updateDBTimer updateDb = new updateDBTimer();
		updateDb.start();
		Thread.sleep(50);// 延迟
		final ServerItemUserTimer itemUserTime = new ServerItemUserTimer();
		itemUserTime.start();
  		// 自動輔助魔法(妖精)
//		final AutoMagic_GJ AutoSkill = new AutoMagic_GJ();
//		AutoSkill.start();
//		Thread.sleep(50);// 延遲
		// 载入自动状态
		// 载入自动状态
		
		HeallingPotionTable.get().load();
		CallClanMapTable.get().load();
		//FindItemCountTable.get().load();
		WeaponEnchantDmgTable.get().load();
		ShouShaReading.get().load();
		ShouBaoReading.get().load();
		ScrollEnchantIdTable.get().load();
		MapExpTable.get().load();
		EnchantRingListTable.get().load();
		WorldExpBuffTable.get().load();
		LoadZnoe.getInstance().load();
		NotDropTable.getInstance().load();
		FailureEnchantTable.get().load();
		ServerFailureEnchantTable.get().load();
		CharaterTradeReading.get().load();
		EnchantDmgReductionTable.get().load();
		EtcItemSkillTable.getInstance();
		ServerBlessEnchantTable.get().load();
		// 人物记忆座标纪录资料
        CharBookReading.get().load();
        ChatObsceneTable.getInstance().load();
        DollPowerTable.get().load();
        DollXiLianTable.get().load();
        
		System.out.println("伺服器启动完毕。");
		// 自動重啟計時器
		Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());
		// 监听端口启动重置作业
		EchoServerTimer.get().start();
//		Mail.Sendlog();
		//this.start();
	}

	/**
	 * 中全对kick、情报保存。
	 */
	public void disconnectAllCharacters() {
		ArrayList<LineageClient> list = new ArrayList<LineageClient>();
		list.addAll(LoginController.getInstance().getClients());
		for (LineageClient client : list) {
			client.kick();
		}
	}
/*
	private class ServerShutdownThread extends Thread {
		private final int _secondsCount;

		public ServerShutdownThread(int secondsCount) {
			_secondsCount = secondsCount;
		}

		@Override
		public void run() {
			L1World world = L1World.getInstance();
			try {
				int secondsCount = _secondsCount;
				world.broadcastServerMessage("伺服器即将关闭。");
				world.broadcastServerMessage("请玩家移动到安全区域先行登出。");
				while (0 < secondsCount) {
					if (secondsCount <= 30) {
						world.broadcastServerMessage("伺服器将在 " + secondsCount
								+ "秒后关闭，请玩家移动到安全区域先行登出。");
					} else {
						if (secondsCount % 60 == 0) {
							world.broadcastServerMessage("伺服器将在 " + secondsCount
									/ 60 + "分钟后关闭。");
						}
					}
					Thread.sleep(1000);
					secondsCount--;
				}
				shutdown();
			} catch (InterruptedException e) {
				world.broadcastServerMessage("与服务器连线中断。");
				return;
			}
		}
	}

	private ServerShutdownThread _shutdownThread = null;

	public synchronized void shutdownWithCountdown(int secondsCount) {
		if (_shutdownThread != null) {
			// 既要求行
			// TODO 通知必要
			return;
		}
		_shutdownThread = new ServerShutdownThread(secondsCount);
		GeneralThreadPool.getInstance().execute(_shutdownThread);
	}

	public void shutdown() {
		disconnectAllCharacters();
		System.exit(0);
	}

	public synchronized void abortShutdown() {
		if (_shutdownThread == null) {
			// 要求行
			// TODO 通知必要
			return;
		}

		_shutdownThread.interrupt();
		_shutdownThread = null;
	}
	*/
	// 召唤 
	private void spawn(int npcId, int X, int Y, int H, short Map) {
		L1Npc spawnmonster = NpcTable.getInstance().getTemplate(npcId);
		if (spawnmonster != null) {
			L1NpcInstance mob = null;
			try {
				String implementationName = spawnmonster.getImpl();
				Constructor<?> _constructor = Class.forName((new StringBuilder()).append("l1j.server.server.model.Instance.").append(implementationName).append("Instance").toString()).getConstructors()[0];
				mob = (L1NpcInstance) _constructor.newInstance(new Object[] { spawnmonster });
				mob.setId(IdFactory.getInstance().nextId());
				mob.setX(X);
				mob.setY(Y);
				mob.setHomeX(X);
				mob.setHomeY(Y);
				mob.setMap(Map);
				mob.setHeading(H);
				L1World.getInstance().storeWorldObject(mob);
				L1World.getInstance().addVisibleObject(mob);
				L1Object object = L1World.getInstance().findObject(mob.getId());
				L1QuestInstance newnpc = (L1QuestInstance) object;
				newnpc.onNpcAI();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	// 召唤  end
}
