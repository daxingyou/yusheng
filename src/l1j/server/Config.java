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
package l1j.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Properties;

import l1j.server.server.datatables.ExpTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class Config {
	private static final Log _log = LogFactory.getLog(Config.class);

	/** Debug/release mode */
	public static final boolean DEBUG = false;

	/** Thread pools size */
	public static int THREAD_P_EFFECTS;

	public static int THREAD_P_GENERAL;

	public static int AI_MAX_THREAD;

	public static int THREAD_P_TYPE_GENERAL;

	public static int THREAD_P_SIZE_GENERAL;

	/** Server control */
	/**服务器名称*/
	public static String SERVERNAME = "熊猫 ";
	
	public static final int SVer = 0x0734fd33;
	public static final int CVer = 0x0734fd30;
	public static final int AVer = 0x77cf6eba;
	public static final int NVer = 0x0734fd31;
	public static final int TVer = 0x77d82;
	
	public static int ALT_WHO_TYPE;
	
	public static int VER = 2700806;
	
	public static String GAME_SERVER_HOST_NAME;

	/**伺服器端口*///服务器监听端口以"-"减号分隔 允许设置多个(允许设置一个)
	public static String GAME_SERVER_PORT;

	public static String DB_DRIVER;

	public static String DB_URL;

	public static String DB_LOGIN;

	public static String DB_PASSWORD;

	public static String TIME_ZONE;

	public static int CLIENT_LANGUAGE;

	public static boolean HOSTNAME_LOOKUPS;

	public static int AUTOMATIC_KICK;

	public static boolean AUTO_CREATE_ACCOUNTS;

	public static short MAX_ONLINE_USERS;

	public static boolean CACHE_MAP_FILES;

	public static boolean LOAD_V2_MAP_FILES;

	public static boolean CHECK_MOVE_INTERVAL;
	
	public static int CHECK_MOVE_STRICTNESS;

	public static boolean CHECK_ATTACK_INTERVAL;

	public static boolean CHECK_SPELL_INTERVAL;

	public static short INJUSTICE_COUNT;

	public static int JUSTICE_COUNT;

	public static int CHECK_STRICTNESS;

	public static byte LOGGING_WEAPON_ENCHANT;

	public static byte LOGGING_ARMOR_ENCHANT;

	public static boolean LOGGING_CHAT_NORMAL;

	public static boolean LOGGING_CHAT_WHISPER;

	public static boolean LOGGING_CHAT_SHOUT;

	public static boolean LOGGING_CHAT_WORLD;

	public static boolean LOGGING_CHAT_CLAN;

	public static boolean LOGGING_CHAT_PARTY;

	public static boolean LOGGING_CHAT_COMBINED;

	public static boolean LOGGING_CHAT_CHAT_PARTY;

	public static int AUTOSAVE_INTERVAL;

	public static int AUTOSAVE_INTERVAL_INVENTORY;

	public static int SKILLTIMER_IMPLTYPE;

	public static int NPCAI_IMPLTYPE;

	public static boolean TELNET_SERVER;

	public static int TELNET_SERVER_PORT;
	
	public static int PC_RECOGNIZE_RANGE;

	public static boolean CHARACTER_CONFIG_IN_SERVER_SIDE;
	
	/**端口重置时间(单位:分钟)*/
	public static int RESTART_LOGIN;

	/** Rate control */
	public static double RATE_XP;

	public static double PET_RATE_XP;//宠物经验倍率
	
	public static double RATE_LA;

	public static double RATE_KARMA;

	public static double RATE_DROP_ADENA;
	
	public static double RATE_DROP_ITEMS;

	public static int ENCHANT_CHANCE_WEAPON;

	public static int ENCHANT_CHANCE_ARMOR;

	public static double RATE_WEIGHT_LIMIT;

	public static double RATE_WEIGHT_LIMIT_PET;
	
	public static double RATE_SHOP_SELLING_PRICE;

	public static double RATE_SHOP_PURCHASING_PRICE;

	/** AltSettings control */
	public static short GLOBAL_CHAT_LEVEL;

	public static byte AUTO_LOOT;

	public static int LOOTING_RANGE;

	public static boolean ALT_NONPVP;

	public static boolean ALT_ATKMSG;

	public static boolean CHANGE_TITLE_BY_ONESELF;

	public static int MAX_PT;

	public static int REST_TIME;//伺服器重启 by 丫杰
	
	public static boolean SIM_WAR_PENALTY;

	public static boolean GET_BACK;

	public static int ALT_ITEM_DELETION_TIME;

	public static byte ALT_ITEM_DELETION_RANGE;

	public static boolean ALT_GMSHOP;

	public static int ALT_GMSHOP_MIN_ID;

	public static int ALT_GMSHOP_MAX_ID;

	public static boolean ALT_HALLOWEENIVENT;

	public static boolean ALT_WHO_COMMAND;

	public static boolean ALT_REVIVAL_POTION;

	public static int ALT_WAR_TIME;

	public static int ALT_WAR_TIME_UNIT;

	public static int ALT_WAR_INTERVAL;

	public static int ALT_WAR_INTERVAL_UNIT;

	public static int ALT_RATE_OF_DUTY;

	public static boolean SPAWN_HOME_POINT;

	public static int SPAWN_HOME_POINT_RANGE;

	public static int SPAWN_HOME_POINT_COUNT;

	public static int SPAWN_HOME_POINT_DELAY;
	
	public static boolean INIT_BOSS_SPAWN;

	public static boolean SERVER_BORAD; // 伺服器公告 by 丫杰

	public static int ELEMENTAL_STONE_AMOUNT;

	public static int Show_Announcecycle_Time; // 循环公告 by 丫杰

	public static boolean ALL_ITEM_SELL; // 全道具贩卖 by 丫杰

	public static boolean DOLL_AND_HIERARCH; // 魔法娃娃与祭司共存开关 by 丫杰
	
	public static boolean EXP_DOUBLE; // 离线满一小时经验值加倍开关 by 丫杰

	public static boolean GM_OVERHEARD; // GM偷听开关 by 丫杰
	
	public static boolean LOGINS_TO_AUTOENTICATION; // TGG伺服器绑稛开关 by 丫杰

	/** CharSettings control */
	public static int PRINCE_MAX_HP;

	public static int PRINCE_MAX_MP;

	public static int KNIGHT_MAX_HP;

	public static int KNIGHT_MAX_MP;

	public static int ELF_MAX_HP;

	public static int ELF_MAX_MP;

	public static int WIZARD_MAX_HP;

	public static int WIZARD_MAX_MP;

	public static int DARKELF_MAX_HP;

	public static int DARKELF_MAX_MP;
	
	// 龙骑士最大血量
	public static int DragonKnight_MAX_HP;
	// 龙骑士最大魔力
	public static int DragonKnight_MAX_MP;
	
	// 幻术师最大血量
	public static int Illusionist_MAX_HP;
	// 幻术师最大魔力
	public static int Illusionist_MAX_MP;

	public static int LV50_EXP;

	public static int LV51_EXP;

	public static int LV52_EXP;

	public static int LV53_EXP;

	public static int LV54_EXP;

	public static int LV55_EXP;

	public static int LV56_EXP;

	public static int LV57_EXP;

	public static int LV58_EXP;

	public static int LV59_EXP;

	public static int LV60_EXP;

	public static int LV61_EXP;

	public static int LV62_EXP;

	public static int LV63_EXP;

	public static int LV64_EXP;

	public static int LV65_EXP;

	public static int LV66_EXP;

	public static int LV67_EXP;

	public static int LV68_EXP;

	public static int LV69_EXP;

	public static int LV70_EXP;

	public static int LV71_EXP;

	public static int LV72_EXP;

	public static int LV73_EXP;

	public static int LV74_EXP;

	public static int LV75_EXP;

	public static int LV76_EXP;

	public static int LV77_EXP;

	public static int LV78_EXP;

	public static int LV79_EXP;

	public static int LV80_EXP;

	public static int LV81_EXP;

	public static int LV82_EXP;

	public static int LV83_EXP;

	public static int LV84_EXP;

	public static int LV85_EXP;

	public static int LV86_EXP;

	public static int LV87_EXP;

	public static int LV88_EXP;

	public static int LV89_EXP;

	public static int LV90_EXP;

	public static int LV91_EXP;

	public static int LV92_EXP;

	public static int LV93_EXP;

	public static int LV94_EXP;

	public static int LV95_EXP;

	public static int LV96_EXP;

	public static int LV97_EXP;

	public static int LV98_EXP;

	public static int LV99_EXP;
	
	public static int MAXLV;
	
	//能力值上限调整 by 丫杰
	/**
	 * 能力值上限
	 */
	public static int BONUS_STATS1;

	public static int BONUS_STATS2;

	public static int BONUS_STATS3;

	public static int PET_LEVEL;

	public static int REVIVAL_POTION;
	//能力值上限调整 by 丫杰 end
	
	//语言 by 丫杰
	public static String LANGUAGE;
	public static int SERVER_VERSION;
	//语言 by 丫杰 end

	/** Configuration files */
	public static final String SERVER_CONFIG_FILE = "./config/server.properties";

	public static final String RATES_CONFIG_FILE = "./config/rates.properties";

	public static final String ALT_SETTINGS_FILE = "./config/altsettings.properties";

	public static final String CHAR_SETTINGS_CONFIG_FILE = "./config/charsettings.properties";
	
	public static final String PACK_CONFIG_FILE = "./config/pack.properties";

	/** 他设定 */

	// NPC吸MP限界
	public static final int MANA_DRAIN_LIMIT_PER_NPC = 40;

	// 一回攻击吸MP限界(SOM、钢铁SOM）
	public static final int MANA_DRAIN_LIMIT_PER_SOM_ATTACK = 16;

	public static final String[] dollposs = new String[]{"■□□□□□□□□□","■■□□□□□□□□","■■■□□□□□□□","■■■■□□□□□□","■■■■■□□□□□","■■■■■■□□□□","■■■■■■■□□□","■■■■■■■■□□","■■■■■■■■■□","■■■■■■■■■■"};

	public static boolean mobRandomName = false;//是否开启怪物名称后面加随机数

	public static boolean clanItemCheck = true;

	public static  String RSA_KEY_E;

	public static  String RSA_KEY_D;
	
	public static String RSA_KEY_N;

	/** 重新启动时间设置 */
	public static String[] AUTORESTART = null;
	
	/** 1个IP仅允许一个连线 */
	public static int ISONEIP;
	
	public static boolean AICHECK = false;
	
	public static int MINAITIME;
	
	public static int MAXAITIME;
	
	public static int WAITTIME;

	public static int CLANCOUNT;
	
	public static int CHATSLEEP;
	
	
	public static short RateBugRaceTime;//奇岩赌场场次时间(单位分钟)
	public static boolean GamStart;		// 赌肥场是否开启
	public static int GamNpcIsWhat;		// 赌狗场NPC外观设定
	public static boolean GamCross;		// 赌肥是否直接穿越障碍物
	public static double Gam_CHIP;		// 奇岩赌场最低下注金(单位金币)
	public static double COMMISSION;	// 赌场抽佣(除率)
	
	public static boolean BigHotStart;	// 大乐透是否开启
	public static int BigHotMoney;		// 下注币值
	public static int BigHotCount;		// 下注单位
	public static int BigHotAddGold;	// 每期累计加注金额
	public static int BigHotGold;		// 彩金初始金额
	public static int BigHotAddMoney;	// 当彩金累积到多少就自动重置为初始金额
	public static boolean BigHotGood1;	// 电脑选号是否让玩家得到头彩
	public static boolean BigHotGood2;	// 同上变一奖
	public static boolean BigHotGood3;	// 同上变贰奖
	
	public static boolean GUAJI_AI;
	public static int GUAJI_AI_TIME;
	public static int GUAJI_AI_NPCID;
	public static int GUAJI_AI_NO_TIME;
	public static int GUAJI_AI_OK_ITEMID;
	public static int GUAJI_AI_OK_ITEMCOUNT;
	
	public static boolean AI_ONLIN;
	public static int AI_ONLIN_TIME;
	public static int AI_ONLIN_ITEMID;
	public static int AI_ONLIN_ITEMCOUNT;

	public static int HUODONGNEXTTIME;
	public static short HUODONGMAPID;
	public static int HUODONGLOCX;
	public static int HUODONGLOCY;
	public static int HUODONGITEMCOUNT;
	public static int HUODONGITEMID;

	public static int BowHit = 0;

	public static int BLESSENCHANT;

	public static boolean DuBo = false;

	public static int WEAPON_MAXENCHANTLEVEL;

	public static boolean HANHUA = false;

	public static boolean AttakMobAIDeath;
	
	public static int dollPower4Random;
	public static int dollPower3Random;
	public static int dollPower2Random;

	public static boolean GM_CANCELLATION_ON = false;

	public static Boolean HPBAR = true;//血条显示开关

	public static Boolean damageDisplay=false;//攻击怪物或pc回传伤害开关

	public static Boolean dummyFunction;//假人功能，玩家下线后，人物不消失

	public static void load() {
		_log.info("loading gameserver config");
		//TODO 伺服器捆绑
		try {
			Properties packSettings = new Properties();
			InputStream is = new FileInputStream(new File(PACK_CONFIG_FILE));
			packSettings.load(is);
			is.close();
			LOGINS_TO_AUTOENTICATION = Boolean.parseBoolean(packSettings.getProperty(
					"Autoentication", "false"));
			/*for(int i = 0 ; i < LOGINS_TO_AUTOENTICATION.length ; i++)
			{
				LOGINS_TO_AUTOENTICATION[i] = Integer.parseInt(packSettings.getProperty(
					"Autoentication" + (i + 1), "0"));
			}*/
			// TGG伺服器绑稛开关
			RSA_KEY_E = packSettings.getProperty("RSA_KEY_E", "0");
			RSA_KEY_D = packSettings.getProperty("RSA_KEY_N", "0");
			RSA_KEY_N = packSettings.getProperty("RSA_KEY_N", "0");
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
			throw new Error("Failed to Load " + PACK_CONFIG_FILE + " File.");
		}
		// server.properties
		try {
			Properties serverSettings = new Properties();
			InputStream is = new FileInputStream(new File(SERVER_CONFIG_FILE));
			serverSettings.load(is);
			is.close();

			GAME_SERVER_HOST_NAME = serverSettings.getProperty(
					"GameserverHostname", "*");
			GAME_SERVER_PORT = (serverSettings.getProperty(
					"GameserverPort", "2000"));
			DB_DRIVER = serverSettings.getProperty("Driver",
					"com.mysql.jdbc.Driver");
			DB_URL = serverSettings
					.getProperty("URL",
							"jdbc:mysql://localhost/l1jdb?useUnicode=true&characterEncoding=big5");
			DB_LOGIN = serverSettings.getProperty("Login", "root");
			DB_PASSWORD = serverSettings.getProperty("Password", "");
			THREAD_P_TYPE_GENERAL = Integer.parseInt(serverSettings
					.getProperty("GeneralThreadPoolType", "0"), 10);
			THREAD_P_SIZE_GENERAL = Integer.parseInt(serverSettings
					.getProperty("GeneralThreadPoolSize", "0"), 10);
			CLIENT_LANGUAGE = Integer.parseInt(serverSettings.getProperty(
					"ClientLanguage", "4"));
			TIME_ZONE = serverSettings.getProperty("TimeZone", "JST");
			HOSTNAME_LOOKUPS = Boolean.parseBoolean(serverSettings.getProperty(
					"HostnameLookups", "false"));
			AUTOMATIC_KICK = Integer.parseInt(serverSettings.getProperty(
					"AutomaticKick", "10"));
			AUTO_CREATE_ACCOUNTS = Boolean.parseBoolean(serverSettings
					.getProperty("AutoCreateAccounts", "true"));
			MAX_ONLINE_USERS = Short.parseShort(serverSettings.getProperty(
					"MaximumOnlineUsers", "30"));
			CACHE_MAP_FILES = Boolean.parseBoolean(serverSettings.getProperty(
					"CacheMapFiles", "false"));
			LOAD_V2_MAP_FILES = Boolean.parseBoolean(serverSettings
					.getProperty("LoadV2MapFiles", "false"));
			CHECK_MOVE_INTERVAL = Boolean.parseBoolean(serverSettings
					.getProperty("CheckMoveInterval", "false"));
			CHECK_ATTACK_INTERVAL = Boolean.parseBoolean(serverSettings
					.getProperty("CheckAttackInterval", "false"));
			CHECK_SPELL_INTERVAL = Boolean.parseBoolean(serverSettings
					.getProperty("CheckSpellInterval", "false"));
			INJUSTICE_COUNT = Short.parseShort(serverSettings.getProperty(
					"InjusticeCount", "10"));
			JUSTICE_COUNT = Integer.parseInt(serverSettings.getProperty(
					"JusticeCount", "4"));
			CHECK_STRICTNESS = Integer.parseInt(serverSettings.getProperty(
					"CheckStrictness", "102"));
			// 行走加速误差
			CHECK_MOVE_STRICTNESS = Integer.parseInt(serverSettings.getProperty(
								"CheckMoveStrictness", "105"));
			LOGGING_WEAPON_ENCHANT = Byte.parseByte(serverSettings.getProperty(
					"LoggingWeaponEnchant", "0"));
			LOGGING_ARMOR_ENCHANT = Byte.parseByte(serverSettings.getProperty(
					"LoggingArmorEnchant", "0"));
			LOGGING_CHAT_NORMAL = Boolean.parseBoolean(serverSettings
					.getProperty("LoggingChatNormal", "false"));
			LOGGING_CHAT_WHISPER = Boolean.parseBoolean(serverSettings
					.getProperty("LoggingChatWhisper", "false"));
			LOGGING_CHAT_SHOUT = Boolean.parseBoolean(serverSettings
					.getProperty("LoggingChatShout", "false"));
			LOGGING_CHAT_WORLD = Boolean.parseBoolean(serverSettings
					.getProperty("LoggingChatWorld", "false"));
			LOGGING_CHAT_CLAN = Boolean.parseBoolean(serverSettings
					.getProperty("LoggingChatClan", "false"));
			LOGGING_CHAT_PARTY = Boolean.parseBoolean(serverSettings
					.getProperty("LoggingChatParty", "false"));
			LOGGING_CHAT_COMBINED = Boolean.parseBoolean(serverSettings
					.getProperty("LoggingChatCombined", "false"));
			LOGGING_CHAT_CHAT_PARTY = Boolean.parseBoolean(serverSettings
					.getProperty("LoggingChatChatParty", "false"));
			AUTOSAVE_INTERVAL = Integer.parseInt(serverSettings.getProperty(
					"AutosaveInterval", "1200"), 10);
			AUTOSAVE_INTERVAL_INVENTORY = Integer.parseInt(serverSettings
					.getProperty("AutosaveIntervalOfInventory", "300"), 10);
			SKILLTIMER_IMPLTYPE = Integer.parseInt(serverSettings.getProperty(
					"SkillTimerImplType", "1"));
			NPCAI_IMPLTYPE = Integer.parseInt(serverSettings.getProperty(
					"NpcAIImplType", "1"));
			TELNET_SERVER = Boolean.parseBoolean(serverSettings.getProperty(
					"TelnetServer", "false"));
			TELNET_SERVER_PORT = Integer.parseInt(serverSettings.getProperty(
					"TelnetServerPort", "23"));
			PC_RECOGNIZE_RANGE = Integer.parseInt(serverSettings.getProperty(
					"PcRecognizeRange", "30"));
			CHARACTER_CONFIG_IN_SERVER_SIDE = Boolean
					.parseBoolean(serverSettings.getProperty(
							"CharacterConfigInServerSide", "true"));
			//语言 by 丫杰
			LANGUAGE = serverSettings.getProperty("Language", "big5");
			SERVER_VERSION = Integer.parseInt(serverSettings.getProperty(
					"ServerVersion", "1"));
			/** 重新启动时间设置 */
			String tmp = serverSettings.getProperty("AutoRestart", "");
			if (!tmp.equalsIgnoreCase("null")) {
				AUTORESTART = tmp.split(",");
			}
			// WHO 显示 额外设置方式 0:对话视窗显示 1:视窗显示
			// 这一项设置必须在WhoCommandx = true才有作用
			ALT_WHO_TYPE = Integer.parseInt(serverSettings.getProperty(
					"Who_type", "0"));
			AICHECK = Boolean.parseBoolean(serverSettings.getProperty("AI", "false"));
			MINAITIME = Integer.parseInt(serverSettings.getProperty(
					"MinAItime", "1800"));
			MAXAITIME = Integer.parseInt(serverSettings.getProperty(
					"MaxAItime", "1800"));
			WAITTIME = Integer.parseInt(serverSettings.getProperty(
					"WaitAItime", "180"));
			CHATSLEEP = Integer.parseInt(serverSettings.getProperty(
					"ChatSleep", "3"));
			RESTART_LOGIN = Integer.parseInt(serverSettings.getProperty(
					"restartlogin", "30"));
			ISONEIP = Integer.parseInt(serverSettings.getProperty("ISONEIP",
					"0"));
			// 语言 by 丫杰 end
			// 奇岩赌场场次时间(单位分钟)
			RateBugRaceTime = Short.parseShort(serverSettings.getProperty(
					"RateBugRaceTime", "15"));
			// 赌肥场是否开启
			GamStart = Boolean.parseBoolean(serverSettings.getProperty(
					"GamStart", "false"));
			// 赌狗场NPC外观设定
			GamNpcIsWhat = Integer.parseInt(serverSettings.getProperty(
					"GamNpcIsWhat", "1"));
			// 赌肥是否直接穿越障碍物
			GamCross = Boolean.parseBoolean(serverSettings.getProperty(
					"GamCross", "false"));
			// 奇岩赌场最低下注金(单位金币)
			Gam_CHIP = Double.parseDouble(serverSettings.getProperty(
					"Gam_CHIP", "100.0"));
			// 赌场抽佣(除率)
			COMMISSION = Double.parseDouble(serverSettings.getProperty(
					"COMMISSION", "2.0"));
			// 大乐透是否开启
			BigHotStart = Boolean.parseBoolean(serverSettings.getProperty(
					"BigHotStart", "false"));
			BigHotMoney = Integer.parseInt(serverSettings.getProperty(
					"BigHotMoney", "70356"));
			BigHotCount = Integer.parseInt(serverSettings.getProperty(
					"BigHotCount", "10"));
			BigHotAddGold = Integer.parseInt(serverSettings.getProperty(
					"BigHotAddGold", "1000"));
			BigHotGold = Integer.parseInt(serverSettings.getProperty(
					"BigHotGold", "10"));
			BigHotAddMoney = Integer.parseInt(serverSettings.getProperty(
					"BigHotAddMoney", "100000"));
			BigHotGood1 = Boolean.parseBoolean(serverSettings.getProperty(
					"BigHotGood1", "false"));
			BigHotGood2 = Boolean.parseBoolean(serverSettings.getProperty(
					"BigHotGood2", "false"));
			BigHotGood3 = Boolean.parseBoolean(serverSettings.getProperty(
					"BigHotGood3", "false"));
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
			throw new Error("Failed to Load " + SERVER_CONFIG_FILE + " File.");
		}

		// rates.properties
		try {
			Properties rateSettings = new Properties();
			InputStream is = new FileInputStream(new File(RATES_CONFIG_FILE));
			rateSettings.load(is);
			is.close();

			RATE_XP = Double.parseDouble(rateSettings.getProperty("RateXp",
					"1.0"));
			//宠物经验倍率 by 丫杰
			PET_RATE_XP = Double.parseDouble(rateSettings.getProperty("PetRateXp",
					"1.0"));
			//宠物经验倍率 by 丫杰 end
			RATE_LA = Double.parseDouble(rateSettings.getProperty("RateLawful",
					"1.0"));
			RATE_KARMA = Double.parseDouble(rateSettings.getProperty(
					"RateKarma", "1.0"));
			RATE_DROP_ADENA = Double.parseDouble(rateSettings.getProperty(
					"RateDropAdena", "1.0"));
			RATE_DROP_ITEMS = Double.parseDouble(rateSettings.getProperty(
					"RateDropItems", "1.0"));
			ENCHANT_CHANCE_WEAPON = Integer.parseInt(rateSettings.getProperty(
					"EnchantChanceWeapon", "68"));
			ENCHANT_CHANCE_ARMOR = Integer.parseInt(rateSettings.getProperty(
					"EnchantChanceArmor", "52"));
			RATE_WEIGHT_LIMIT = Double.parseDouble(rateSettings.getProperty("RateWeightLimit",
					"1"));
			RATE_WEIGHT_LIMIT_PET = Double.parseDouble(rateSettings
					.getProperty("RateWeightLimitforPet", "1"));
			RATE_SHOP_SELLING_PRICE = Double.parseDouble(rateSettings
					.getProperty("RateShopSellingPrice", "1.0"));
			RATE_SHOP_PURCHASING_PRICE = Double.parseDouble(rateSettings
					.getProperty("RateShopPurchasingPrice", "1.0"));
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
			throw new Error("Failed to Load " + RATES_CONFIG_FILE + " File.");
		}

		// altsettings.properties
		try {
			Properties altSettings = new Properties();
			InputStream is = new FileInputStream(new File(ALT_SETTINGS_FILE));
			altSettings.load(is);
			is.close();

			mobRandomName = Boolean.parseBoolean(altSettings.getProperty("mobRandomName", "true"));//是否开启怪物名称后面加随机数

			dummyFunction = Boolean.parseBoolean(altSettings.getProperty("dummyFunction", "false"));//假人功能开关

			damageDisplay = Boolean.parseBoolean(altSettings.getProperty("damageDisplay", "true"));

			HPBAR = Boolean.parseBoolean(altSettings.getProperty("hpBar", "true"));

			dollPower4Random = Integer.parseInt(altSettings.getProperty("dollPower4Random ","5"));
			
			dollPower3Random = Integer.parseInt(altSettings.getProperty("dollPower3Random ","20"));
			
			dollPower2Random = Integer.parseInt(altSettings.getProperty("dollPower2Random ","40"));
			
			AttakMobAIDeath =Boolean.parseBoolean(altSettings.getProperty("AttakMobAIDeath","false"));
					
			WEAPON_MAXENCHANTLEVEL = Integer.parseInt(altSettings.getProperty("WeaponMaxEnchantLevel","9"));
			
			BLESSENCHANT = Integer.parseInt(altSettings.getProperty("BlessEnchant", "30"));
			
			GLOBAL_CHAT_LEVEL = Short.parseShort(altSettings.getProperty(
					"GlobalChatLevel", "1"));
			AUTO_LOOT = Byte
					.parseByte(altSettings.getProperty("AutoLoot", "2"));
			LOOTING_RANGE = Integer.parseInt(altSettings.getProperty(
					"LootingRange", "3"));
			ALT_NONPVP = Boolean.parseBoolean(altSettings.getProperty("NonPvP",
					"true"));
			ALT_ATKMSG = Boolean.parseBoolean(altSettings.getProperty(
					"AttackMessageOn", "true"));
			CHANGE_TITLE_BY_ONESELF = Boolean.parseBoolean(altSettings
					.getProperty("ChangeTitleByOneself", "false"));
			MAX_PT = Integer.parseInt(altSettings.getProperty("MaxPT", "8"));
			//伺服器重启 by 丫杰
			REST_TIME = Integer.parseInt(altSettings.getProperty("RestartTime", "60"));
			//伺服器重启 by 丫杰 end
			SIM_WAR_PENALTY = Boolean.parseBoolean(altSettings.getProperty(
					"SimWarPenalty", "true"));
			GET_BACK = Boolean.parseBoolean(altSettings.getProperty("GetBack",
					"false"));
			ALT_ITEM_DELETION_TIME = Integer.parseInt(altSettings.getProperty(
					"AutomaticItemDeletionTime", "300"));
			ALT_ITEM_DELETION_RANGE = Byte.parseByte(altSettings.getProperty(
					"AutomaticItemDeletionRange", "5"));
			ALT_GMSHOP = Boolean.parseBoolean(altSettings.getProperty("GMshop",
					"false"));
			ALT_GMSHOP_MIN_ID = Integer.parseInt(altSettings.getProperty(
					"GMshopMinID", "0xffffffff")); // 取得失败时无效
			ALT_GMSHOP_MAX_ID = Integer.parseInt(altSettings.getProperty(
					"GMshopMaxID", "0xffffffff")); // 取得失败时无效
			ALT_HALLOWEENIVENT = Boolean.parseBoolean(altSettings.getProperty(
					"HalloweenIvent", "true"));
			ALT_WHO_COMMAND = Boolean.parseBoolean(altSettings.getProperty(
					"WhoCommand", "false"));
			ALT_REVIVAL_POTION = Boolean.parseBoolean(altSettings.getProperty(
					"RevivalPotion", "false"));
			CLANCOUNT = Integer.parseInt(altSettings.getProperty("clancount", "100"));
			String strWar;
			strWar = altSettings.getProperty("WarTime", "2h");
			if (strWar.indexOf("d") >= 0) {
				ALT_WAR_TIME_UNIT = Calendar.DATE;
				strWar = strWar.replace("d", "");
			} else if (strWar.indexOf("h") >= 0) {
				ALT_WAR_TIME_UNIT = Calendar.HOUR_OF_DAY;
				strWar = strWar.replace("h", "");
			} else if (strWar.indexOf("m") >= 0) {
				ALT_WAR_TIME_UNIT = Calendar.MINUTE;
				strWar = strWar.replace("m", "");
			}
			ALT_WAR_TIME = Integer.parseInt(strWar);
			strWar = altSettings.getProperty("WarInterval", "4d");
			if (strWar.indexOf("d") >= 0) {
				ALT_WAR_INTERVAL_UNIT = Calendar.DATE;
				strWar = strWar.replace("d", "");
			} else if (strWar.indexOf("h") >= 0) {
				ALT_WAR_INTERVAL_UNIT = Calendar.HOUR_OF_DAY;
				strWar = strWar.replace("h", "");
			} else if (strWar.indexOf("m") >= 0) {
				ALT_WAR_INTERVAL_UNIT = Calendar.MINUTE;
				strWar = strWar.replace("m", "");
			}
			ALT_WAR_INTERVAL = Integer.parseInt(strWar);
			SPAWN_HOME_POINT = Boolean.parseBoolean(altSettings.getProperty(
					"SpawnHomePoint", "true"));
			SPAWN_HOME_POINT_COUNT = Integer.parseInt(altSettings.getProperty(
					"SpawnHomePointCount", "2"));
			SPAWN_HOME_POINT_DELAY = Integer.parseInt(altSettings.getProperty(
					"SpawnHomePointDelay", "100"));
			SPAWN_HOME_POINT_RANGE = Integer.parseInt(altSettings.getProperty(
					"SpawnHomePointRange", "8"));
			INIT_BOSS_SPAWN = Boolean.parseBoolean(altSettings.getProperty(
					"InitBossSpawn", "true"));
			ELEMENTAL_STONE_AMOUNT = Integer.parseInt(altSettings.getProperty(
					"ElementalStoneAmount", "300"));
			// 循环公告 by 丫杰
			Show_Announcecycle_Time = Integer.parseInt(altSettings.getProperty(
					"ShowAnnouncecycleTime", "30"));
			// 循环公告 by 丫杰 end
			// 伺服器公告 by 丫杰
			SERVER_BORAD = Boolean.parseBoolean(altSettings.getProperty(
					"ServerBorad", "false"));
			// 伺服器公告 by 丫杰 end
			// 全道具贩卖 by 丫杰
			ALL_ITEM_SELL = Boolean.parseBoolean(altSettings.getProperty(
					"AllItemSell", "false"));
			// 全道具贩卖 by 丫杰 end
			// 魔法娃娃与祭司共存判断 by 丫杰
			DOLL_AND_HIERARCH = Boolean.parseBoolean(altSettings.getProperty(
					"DOLL_AND_HIERARCH", "false"));
			// 魔法娃娃与祭司共存判断 by 丫杰 end
			// 离线满一小时经验值加倍开关 by 丫杰
			EXP_DOUBLE = Boolean.parseBoolean(altSettings.getProperty(
					"EXP_DOUBLE", "false"));
			// 离线满一小时经验值加倍开关 by 丫杰 end
			// GM偷听开关 by 丫杰
			GM_OVERHEARD = Boolean.parseBoolean(altSettings.getProperty(
					"GM_OVERHEARD", "false"));
			// GM偷听开关 by 丫杰 end
			// TGG伺服器绑稛开关 by 丫杰
			/*LOGINS_TO_AUTOENTICATION = Boolean.parseBoolean(altSettings.getProperty(
					"Logins_to_Autoentication", "false"));*/
			// TGG伺服器绑稛开关 by 丫杰
			GUAJI_AI = Boolean.parseBoolean(altSettings.getProperty("GuaJi_AI", "false"));
			GUAJI_AI_TIME = Integer.parseInt(altSettings.getProperty("GuaJi_AI_Time", "10"));
			GUAJI_AI_NPCID = Integer.parseInt(altSettings.getProperty("GuaJi_AI_NpcId", "0"));
			GUAJI_AI_NO_TIME = Integer.parseInt(altSettings.getProperty("GuaJi_AI_NO_Time", "3"));
			GUAJI_AI_OK_ITEMID = Integer.parseInt(altSettings.getProperty("GuaJi_AI_OK_ItemId", "0"));
			GUAJI_AI_OK_ITEMCOUNT = Integer.parseInt(altSettings.getProperty("GuaJi_AI_OK_ItemCount", "0"));
			
			AI_ONLIN = Boolean.parseBoolean(altSettings.getProperty("AI_Onlin", "false"));
			AI_ONLIN_TIME = Integer.parseInt(altSettings.getProperty("AI_Onlin_Time", "1"));
			AI_ONLIN_ITEMID = Integer.parseInt(altSettings.getProperty("AI_Onlin_ItemId", "40308"));
			AI_ONLIN_ITEMCOUNT = Integer.parseInt(altSettings.getProperty("AI_Onlin_ItemCount", "100"));
			
			HUODONGNEXTTIME = Integer.parseInt(altSettings.getProperty("HuoDongMap_Time", "60"));
			HUODONGMAPID = Short.parseShort(altSettings.getProperty("HuoDongMap_MapId", "4"));
			HUODONGLOCX = Integer.parseInt(altSettings.getProperty("HuoDongMap_Locx", "32704"));
			HUODONGLOCY = Integer.parseInt(altSettings.getProperty("HuoDongMap_locy", "32856"));
			HUODONGITEMID = Integer.parseInt(altSettings.getProperty("HuoDongMap_ItemId", "40308"));
			HUODONGITEMCOUNT = Integer.parseInt(altSettings.getProperty("HuoDongMap_ItemCount", "100000"));
			
			BowHit = Integer.parseInt(altSettings.getProperty("BowHit", "0"));
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
			throw new Error("Failed to Load " + ALT_SETTINGS_FILE + " File.");
		}

		// charsettings.properties
		try {
			Properties charSettings = new Properties();
			InputStream is = new FileInputStream(new File(
					CHAR_SETTINGS_CONFIG_FILE));
			charSettings.load(is);
			is.close();

			PRINCE_MAX_HP = Integer.parseInt(charSettings.getProperty(
					"PrinceMaxHP", "1000"));
			PRINCE_MAX_MP = Integer.parseInt(charSettings.getProperty(
					"PrinceMaxMP", "800"));
			KNIGHT_MAX_HP = Integer.parseInt(charSettings.getProperty(
					"KnightMaxHP", "1400"));
			KNIGHT_MAX_MP = Integer.parseInt(charSettings.getProperty(
					"KnightMaxMP", "600"));
			ELF_MAX_HP = Integer.parseInt(charSettings.getProperty("ElfMaxHP",
					"1000"));
			ELF_MAX_MP = Integer.parseInt(charSettings.getProperty("ElfMaxMP",
					"900"));
			WIZARD_MAX_HP = Integer.parseInt(charSettings.getProperty(
					"WizardMaxHP", "800"));
			WIZARD_MAX_MP = Integer.parseInt(charSettings.getProperty(
					"WizardMaxMP", "1200"));
			DARKELF_MAX_HP = Integer.parseInt(charSettings.getProperty(
					"DarkelfMaxHP", "1000"));
			DARKELF_MAX_MP = Integer.parseInt(charSettings.getProperty(
					"DarkelfMaxMP", "900"));
			// 龙骑士最大HP
			DragonKnight_MAX_HP = Integer.parseInt(charSettings.getProperty(
					"DragonKnightMaxHP", "1000"));
			// 龙骑士最大MP
			DragonKnight_MAX_MP = Integer.parseInt(charSettings.getProperty(
					"DragonKnightMaxMP", "900"));
			// 幻术师最大HP
			Illusionist_MAX_HP = Integer.parseInt(charSettings.getProperty(
					"IllusionistMaxHP", "1000"));
			// 幻术师最大MP
			Illusionist_MAX_MP = Integer.parseInt(charSettings.getProperty(
					"IllusionistMaxMP", "900"));
			
			LV50_EXP = Integer.parseInt(charSettings
					.getProperty("Lv50Exp", "1"));
			LV51_EXP = Integer.parseInt(charSettings
					.getProperty("Lv51Exp", "1"));
			LV52_EXP = Integer.parseInt(charSettings
					.getProperty("Lv52Exp", "1"));
			LV53_EXP = Integer.parseInt(charSettings
					.getProperty("Lv53Exp", "1"));
			LV54_EXP = Integer.parseInt(charSettings
					.getProperty("Lv54Exp", "1"));
			LV55_EXP = Integer.parseInt(charSettings
					.getProperty("Lv55Exp", "1"));
			LV56_EXP = Integer.parseInt(charSettings
					.getProperty("Lv56Exp", "1"));
			LV57_EXP = Integer.parseInt(charSettings
					.getProperty("Lv57Exp", "1"));
			LV58_EXP = Integer.parseInt(charSettings
					.getProperty("Lv58Exp", "1"));
			LV59_EXP = Integer.parseInt(charSettings
					.getProperty("Lv59Exp", "1"));
			LV60_EXP = Integer.parseInt(charSettings
					.getProperty("Lv60Exp", "1"));
			LV61_EXP = Integer.parseInt(charSettings
					.getProperty("Lv61Exp", "1"));
			LV62_EXP = Integer.parseInt(charSettings
					.getProperty("Lv62Exp", "1"));
			LV63_EXP = Integer.parseInt(charSettings
					.getProperty("Lv63Exp", "1"));
			LV64_EXP = Integer.parseInt(charSettings
					.getProperty("Lv64Exp", "1"));
			LV65_EXP = Integer.parseInt(charSettings
					.getProperty("Lv65Exp", "2"));
			LV66_EXP = Integer.parseInt(charSettings
					.getProperty("Lv66Exp", "2"));
			LV67_EXP = Integer.parseInt(charSettings
					.getProperty("Lv67Exp", "2"));
			LV68_EXP = Integer.parseInt(charSettings
					.getProperty("Lv68Exp", "2"));
			LV69_EXP = Integer.parseInt(charSettings
					.getProperty("Lv69Exp", "2"));
			LV70_EXP = Integer.parseInt(charSettings
					.getProperty("Lv70Exp", "4"));
			LV71_EXP = Integer.parseInt(charSettings
					.getProperty("Lv71Exp", "4"));
			LV72_EXP = Integer.parseInt(charSettings
					.getProperty("Lv72Exp", "4"));
			LV73_EXP = Integer.parseInt(charSettings
					.getProperty("Lv73Exp", "4"));
			LV74_EXP = Integer.parseInt(charSettings
					.getProperty("Lv74Exp", "4"));
			LV75_EXP = Integer.parseInt(charSettings
					.getProperty("Lv75Exp", "8"));
			LV76_EXP = Integer.parseInt(charSettings
					.getProperty("Lv76Exp", "8"));
			LV77_EXP = Integer.parseInt(charSettings
					.getProperty("Lv77Exp", "8"));
			LV78_EXP = Integer.parseInt(charSettings
					.getProperty("Lv78Exp", "8"));
			LV79_EXP = Integer.parseInt(charSettings.getProperty("Lv79Exp",
					"16"));
			LV80_EXP = Integer.parseInt(charSettings.getProperty("Lv80Exp",
					"32"));
			LV81_EXP = Integer.parseInt(charSettings.getProperty("Lv81Exp",
					"64"));
			LV82_EXP = Integer.parseInt(charSettings.getProperty("Lv82Exp",
					"128"));
			LV83_EXP = Integer.parseInt(charSettings.getProperty("Lv83Exp",
					"256"));
			LV84_EXP = Integer.parseInt(charSettings.getProperty("Lv84Exp",
					"512"));
			LV85_EXP = Integer.parseInt(charSettings.getProperty("Lv85Exp",
					"1024"));
			LV86_EXP = Integer.parseInt(charSettings.getProperty("Lv86Exp",
					"2048"));
			LV87_EXP = Integer.parseInt(charSettings.getProperty("Lv87Exp",
					"4096"));
			LV88_EXP = Integer.parseInt(charSettings.getProperty("Lv88Exp",
					"8192"));
			LV89_EXP = Integer.parseInt(charSettings.getProperty("Lv89Exp",
					"16384"));
			LV90_EXP = Integer.parseInt(charSettings.getProperty("Lv90Exp",
					"32768"));
			LV91_EXP = Integer.parseInt(charSettings.getProperty("Lv91Exp",
					"65536"));
			LV92_EXP = Integer.parseInt(charSettings.getProperty("Lv92Exp",
					"131072"));
			LV93_EXP = Integer.parseInt(charSettings.getProperty("Lv93Exp",
					"262144"));
			LV94_EXP = Integer.parseInt(charSettings.getProperty("Lv94Exp",
					"524288"));
			LV95_EXP = Integer.parseInt(charSettings.getProperty("Lv95Exp",
					"1048576"));
			LV96_EXP = Integer.parseInt(charSettings.getProperty("Lv96Exp",
					"2097152"));
			LV97_EXP = Integer.parseInt(charSettings.getProperty("Lv97Exp",
					"4194304"));
			LV98_EXP = Integer.parseInt(charSettings.getProperty("Lv98Exp",
					"8388608"));
			LV99_EXP = Integer.parseInt(charSettings.getProperty("Lv99Exp",
					"16777216"));
			MAXLV = Integer.parseInt(charSettings.getProperty("MaxLevel",
					"52"));
			ExpTable.MAX_EXP = ExpTable.getExpByMaxLevel(MAXLV);
			//调整能力值上限 by 丫杰
			BONUS_STATS1 = Integer.parseInt(charSettings.getProperty(
					"BONUS_STATS1", "25"));
			BONUS_STATS2 = Integer.parseInt(charSettings.getProperty(
					"BONUS_STATS2", "5"));
			BONUS_STATS3 = Integer.parseInt(charSettings.getProperty(
					"BONUS_STATS3", "25"));
			PET_LEVEL = Integer.parseInt(charSettings.getProperty(
					"Pet_Level", "51"));
			REVIVAL_POTION = Integer.parseInt(charSettings.getProperty(
					"Revival_Potion", "5"));
			//调整能力值上限 by 丫杰 end
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
			throw new Error("Failed to Load " + CHAR_SETTINGS_CONFIG_FILE
					+ " File.");
		}
	}

	public static boolean setParameterValue(String pName, String pValue) {
		// server.properties
		if (pName.equalsIgnoreCase("GameserverHostname")) {
			GAME_SERVER_HOST_NAME = pValue;
		} else if (pName.equalsIgnoreCase("GameserverPort")) {
			GAME_SERVER_PORT = pValue;
		} else if (pName.equalsIgnoreCase("Driver")) {
			DB_DRIVER = pValue;
		} else if (pName.equalsIgnoreCase("URL")) {
			DB_URL = pValue;
		} else if (pName.equalsIgnoreCase("Login")) {
			DB_LOGIN = pValue;
		} else if (pName.equalsIgnoreCase("Password")) {
			DB_PASSWORD = pValue;
		} else if (pName.equalsIgnoreCase("ClientLanguage")) {
			CLIENT_LANGUAGE = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("TimeZone")) {
			TIME_ZONE = pValue;
		} else if (pName.equalsIgnoreCase("AutomaticKick")) {
			AUTOMATIC_KICK = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("AutoCreateAccounts")) {
			AUTO_CREATE_ACCOUNTS = Boolean.parseBoolean(pValue);
		} else if (pName.equalsIgnoreCase("MaximumOnlineUsers")) {
			MAX_ONLINE_USERS = Short.parseShort(pValue);
		} else if (pName.equalsIgnoreCase("LoggingWeaponEnchant")) {
			LOGGING_WEAPON_ENCHANT = Byte.parseByte(pValue);
		} else if (pName.equalsIgnoreCase("LoggingArmorEnchant")) {
			LOGGING_ARMOR_ENCHANT = Byte.parseByte(pValue);
		} else if (pName.equalsIgnoreCase("CharacterConfigInServerSide")) {
			CHARACTER_CONFIG_IN_SERVER_SIDE = Boolean.parseBoolean(pValue);
		}
		//语言 by 丫杰
		else if (pName.equalsIgnoreCase("Language")) {
			LANGUAGE = pValue;
		} else if (pName.equalsIgnoreCase("ServerVersion")) {
			SERVER_VERSION = Integer.parseInt(pValue);
		} 
		//语言 by 丫杰
		// rates.properties
		else if (pName.equalsIgnoreCase("RateXp")) {
			RATE_XP = Double.parseDouble(pValue);
		} else if (pName.equalsIgnoreCase("RateLawful")) {
			RATE_LA = Double.parseDouble(pValue);
		} else if (pName.equalsIgnoreCase("RateKarma")) {
			RATE_KARMA = Double.parseDouble(pValue);
		} else if (pName.equalsIgnoreCase("RateDropAdena")) {
			RATE_DROP_ADENA = Double.parseDouble(pValue);
		} else if (pName.equalsIgnoreCase("RateDropItems")) {
			RATE_DROP_ITEMS = Double.parseDouble(pValue);
		} else if (pName.equalsIgnoreCase("EnchantChanceWeapon")) {
			ENCHANT_CHANCE_WEAPON = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("EnchantChanceArmor")) {
			ENCHANT_CHANCE_ARMOR = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Weightrate")) {
			RATE_WEIGHT_LIMIT = Byte.parseByte(pValue);
		}
		//宠物经验倍率 by 丫杰
		else if (pName.equalsIgnoreCase("PetRateXp")) {
			PET_RATE_XP = Double.parseDouble(pValue);
		}
		//宠物经验倍率 by 丫杰 end
		// altsettings.properties
		else if (pName.equalsIgnoreCase("GlobalChatLevel")) {
			GLOBAL_CHAT_LEVEL = Short.parseShort(pValue);
		} else if (pName.equalsIgnoreCase("AutoLoot")) {
			AUTO_LOOT = Byte.parseByte(pValue);
		} else if (pName.equalsIgnoreCase("LOOTING_RANGE")) {
			LOOTING_RANGE = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("AltNonPvP")) {
			ALT_NONPVP = Boolean.valueOf(pValue);
		} else if (pName.equalsIgnoreCase("AttackMessageOn")) {
			ALT_ATKMSG = Boolean.valueOf(pValue);
		} else if (pName.equalsIgnoreCase("ChangeTitleByOneself")) {
			CHANGE_TITLE_BY_ONESELF = Boolean.valueOf(pValue);
		} else if (pName.equalsIgnoreCase("MaxPT")) {
			MAX_PT = Integer.parseInt(pValue);
		} 
		//伺服器重启 by 丫杰
		else if (pName.equalsIgnoreCase("RestartTime")) {
			REST_TIME = Integer.parseInt(pValue);
		}
		//伺服器重启 by 丫杰 end 
		 else if (pName.equalsIgnoreCase("SimWarPenalty")) {
			SIM_WAR_PENALTY = Boolean.valueOf(pValue);
		} else if (pName.equalsIgnoreCase("GetBack")) {
			GET_BACK = Boolean.valueOf(pValue);
		} else if (pName.equalsIgnoreCase("AutomaticItemDeletionTime")) {
			ALT_ITEM_DELETION_TIME = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("AutomaticItemDeletionRange")) {
			ALT_ITEM_DELETION_RANGE = Byte.parseByte(pValue);
		} else if (pName.equalsIgnoreCase("GMshop")) {
			ALT_GMSHOP = Boolean.valueOf(pValue);
		} else if (pName.equalsIgnoreCase("GMshopMinID")) {
			ALT_GMSHOP_MIN_ID = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("GMshopMaxID")) {
			ALT_GMSHOP_MAX_ID = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("HalloweenIvent")) {
			ALT_HALLOWEENIVENT = Boolean.valueOf(pValue);
		}
		// charsettings.properties
		else if (pName.equalsIgnoreCase("PrinceMaxHP")) {
			PRINCE_MAX_HP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("PrinceMaxMP")) {
			PRINCE_MAX_MP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("KnightMaxHP")) {
			KNIGHT_MAX_HP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("KnightMaxMP")) {
			KNIGHT_MAX_MP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("ElfMaxHP")) {
			ELF_MAX_HP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("ElfMaxMP")) {
			ELF_MAX_MP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("WizardMaxHP")) {
			WIZARD_MAX_HP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("WizardMaxMP")) {
			WIZARD_MAX_MP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("DarkelfMaxHP")) {
			DARKELF_MAX_HP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("DarkelfMaxMP")) {
			DARKELF_MAX_MP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv50Exp")) {
			LV50_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv51Exp")) {
			LV51_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv52Exp")) {
			LV52_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv53Exp")) {
			LV53_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv54Exp")) {
			LV54_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv55Exp")) {
			LV55_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv56Exp")) {
			LV56_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv57Exp")) {
			LV57_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv58Exp")) {
			LV58_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv59Exp")) {
			LV59_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv60Exp")) {
			LV60_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv61Exp")) {
			LV61_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv62Exp")) {
			LV62_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv63Exp")) {
			LV63_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv64Exp")) {
			LV64_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv65Exp")) {
			LV65_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv66Exp")) {
			LV66_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv67Exp")) {
			LV67_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv68Exp")) {
			LV68_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv69Exp")) {
			LV69_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv70Exp")) {
			LV70_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv71Exp")) {
			LV71_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv72Exp")) {
			LV72_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv73Exp")) {
			LV73_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv74Exp")) {
			LV74_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv75Exp")) {
			LV75_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv76Exp")) {
			LV76_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv77Exp")) {
			LV77_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv78Exp")) {
			LV78_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv79Exp")) {
			LV79_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv80Exp")) {
			LV80_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv81Exp")) {
			LV81_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv82Exp")) {
			LV82_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv83Exp")) {
			LV83_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv84Exp")) {
			LV84_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv85Exp")) {
			LV85_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv86Exp")) {
			LV86_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv87Exp")) {
			LV87_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv88Exp")) {
			LV88_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv89Exp")) {
			LV89_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv90Exp")) {
			LV90_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv91Exp")) {
			LV91_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv92Exp")) {
			LV92_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv93Exp")) {
			LV93_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv94Exp")) {
			LV94_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv95Exp")) {
			LV95_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv96Exp")) {
			LV96_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv97Exp")) {
			LV97_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv98Exp")) {
			LV98_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv99Exp")) {
			LV99_EXP = Integer.parseInt(pValue);
		} 
		//调整能力值上限 by 丫杰
			else if (pName.equalsIgnoreCase("BONUS_STATS1")) {
			BONUS_STATS1 = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("BONUS_STATS2")) {
			BONUS_STATS2 = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("BONUS_STATS3")) {
			BONUS_STATS3 = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Pet_Level")) {
			PET_LEVEL = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Revival_Potion")) {
			REVIVAL_POTION = Integer.parseInt(pValue);
		}
		//调整能力值上限 by 丫杰	end
			else {
			return false;
		}
		return true;
	}

	private Config() {
	}
	
	public static void reMaxLevelload() {
		try {
			final Properties charSettings = new Properties();
			final InputStream is = new FileInputStream(new File(CHAR_SETTINGS_CONFIG_FILE));
			charSettings.load(is);
			is.close();
			MAXLV = Integer.parseInt(charSettings.getProperty("MaxLevel","52"));
			ExpTable.MAX_EXP = ExpTable.getExpByMaxLevel(MAXLV);
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
	
	public static void reEnchantload() {
		try {
			final Properties rateSettings = new Properties();
			final InputStream is = new FileInputStream(new File(RATES_CONFIG_FILE));
			rateSettings.load(is);
			is.close();

			ENCHANT_CHANCE_WEAPON = Integer.parseInt(rateSettings.getProperty("EnchantChanceWeapon","1"));
			ENCHANT_CHANCE_ARMOR = Integer.parseInt(rateSettings.getProperty("EnchantChanceArmor","1"));
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
	
	public static void reBowHitload() {
		try {
			final Properties altSettings = new Properties();
			final InputStream is = new FileInputStream(new File(ALT_SETTINGS_FILE));
			altSettings.load(is);
			is.close();
			BowHit = Integer.parseInt(altSettings.getProperty("BowHit", "0"));
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
	public static void reAttakMobAIDeathload() {
		try {
			final Properties altSettings = new Properties();
			final InputStream is = new FileInputStream(new File(ALT_SETTINGS_FILE));
			altSettings.load(is);
			is.close();
			AttakMobAIDeath = Boolean.parseBoolean(altSettings.getProperty("AttakMobAIDeath","false"));
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
	public static void reBlessEnchantload() {
		try {
			final Properties altSettings = new Properties();
			final InputStream is = new FileInputStream(new File(ALT_SETTINGS_FILE));
			altSettings.load(is);
			is.close();
			BLESSENCHANT = Integer.parseInt(altSettings.getProperty("BlessEnchant", "30"));
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	public static void reMaxWeaponEnchantLevelload() {
		try {
			final Properties charSettings = new Properties();
			final InputStream is = new FileInputStream(new File(ALT_SETTINGS_FILE));
			charSettings.load(is);
			is.close();
			WEAPON_MAXENCHANTLEVEL = Integer.parseInt(charSettings.getProperty("WeaponMaxEnchantLevel","9"));
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
	
	public static void reDollPowerRandomload(){
		try {
			final Properties altSettings = new Properties();
			final InputStream is = new FileInputStream(new File(ALT_SETTINGS_FILE));
			altSettings.load(is);
			is.close();
			dollPower4Random = Integer.parseInt(altSettings.getProperty("dollPower4Random ","5"));
			dollPower3Random = Integer.parseInt(altSettings.getProperty("dollPower3Random ","20"));
			dollPower2Random = Integer.parseInt(altSettings.getProperty("dollPower2Random ","40"));
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}