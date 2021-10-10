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

public class Opcodes {

	public Opcodes() {
	}

	 /** 要求删除角色 */
    public static final int C_OPCODE_DELETECHAR = 10;
    /** 要求删除公布栏内容 */
    public static final int C_OPCODE_BOARDDELETE = 12;
    /** 要求写入公布栏讯息 */
    public static final int C_OPCODE_BOARDWRITE = 14;
    /** 要求开个人商店 */
    public static final int C_OPCODE_SHOP = 16;
    /** 要求打开邮箱 */
    public static final int C_OPCODE_MAIL = 22;
    /** 要求钓鱼收杆 */
    public static final int C_OPCODE_FISHCLICK = 26;
    /** 要求加入血盟 */
    public static final int C_OPCODE_JOINCLAN = 30;
    /** 要求存入资金 */
    public static final int C_OPCODE_DEPOSIT = 35;
    /** 要求物件对话视窗结果 */
    public static final int C_OPCODE_NPCACTION = 37;
    /** 要求列表物品取得 */
    public static final int C_OPCODE_RESULT = 40;
    /** 3.3新地图系统/视窗失焦. */
    public static final int C_OPCODE_WINDOWS = 41;
    /** 要求查看队伍名单. */
    public static final int C_OPCODE_PARTY = 42;
    /** 要求使用物品 */
    public static final int C_OPCODE_USEITEM = 44;
    /** 要求寄送简讯 未用 */
    public static final int C_OPCODE_SMS = 45;
    /** 要求决斗 */
    public static final int C_OPCODE_FIGHT = 47;
    /** 要求查询游戏人数 */
    public static final int C_OPCODE_WHO = 49;
    /** 要求下一步 ( 公告资讯 ) */
    public static final int C_OPCODE_COMMONCLICK = 53;
    /** 要求丢弃物品 */
    public static final int C_OPCODE_DROPITEM = 54;
    /** 要求登入伺服器/创建新帐号. */
    public static final int C_OPCODE_LOGINPACKET = 57;
    /** 要求物件对话视窗 */
    public static final int C_OPCODE_NPCTALK = 58;
    /** 要求阅读布告单个栏讯息 */
    public static final int C_OPCODE_BOARDREAD = 59;
    /** 要求查询朋友名单 */
    public static final int C_OPCODE_BUDDYLIST = 60;
    /** 要求点选项目的结果 */
    public static final int C_OPCODE_ATTR = 61;
    /** 要求使用广播聊天频道 */
    public static final int C_OPCODE_CHATGLOBAL = 62;
    /** 要求改变角色面向 */
    public static final int C_OPCODE_CHANGEHEADING = 65;
    /** 要求邀请加入队伍(要求创立队伍) 3.3新增委任队长功能 */
    public static final int C_OPCODE_CREATEPARTY = 166;
    /** 要求角色攻击 */
    public static final int C_OPCODE_ATTACK = 68;
    /** 要求踢出队伍 */
    public static final int C_OPCODE_BANPARTY = 70;
    /** 要求死亡后重新开始 */
    public static final int C_OPCODE_RESTART = 71;
    /** 要求读取公布栏 */
    public static final int C_OPCODE_BOARD = 73;
    /** 登入伺服器OK */
    public static final int C_OPCODE_LOGINTOSERVEROK = 75;
    /** 要求变更仓库密码 && 送出仓库密码. */
    public static final int C_OPCODE_PWD = 81;
    /** 要求使用血盟阶级功能功能(/rank 人物 见习). */
    public static final int C_OPCODE_RANK = 88;
    /** 要求角色移动 */
    public static final int C_OPCODE_MOVECHAR = 95;
    /** 要求赋予封号 */
    public static final int C_OPCODE_TITLE = 96;
    /** 要求新增好友 */
    public static final int C_OPCODE_ADDBUDDY = 99;
    /** 要求使用拒绝名单(开启指定人物讯息)/exclude 名字 */
    public static final int C_OPCODE_EXCLUDE = 101;
    /** 要求交易(个人) */
    public static final int C_OPCODE_TRADE = 103;
    /** 要求离开游戏 */
    public static final int C_OPCODE_QUITGAME = 104;
    /** 要求维修物品清单 */
    public static final int C_OPCODE_FIX_WEAPON_LIST = 106;
    /** 要求上传盟标 */
    public static final int C_OPCODE_EMBLEM = 107;
    /** 要求确定数量选取 */
    public static final int C_OPCODE_AMOUNT = 109;
    /** 要求完成交易(个人) */
    public static final int C_OPCODE_TRADEADDOK = 110;
    /** 要求队伍对话控制(命令/chatparty) */
    public static final int C_OPCODE_CAHTPARTY = 113;
    /** 要求使用技能 */
    public static final int C_OPCODE_USESKILL = 115;
    /** 要求船票数量 */
    public static final int C_OPCODE_SHIP = 117;
    /** 要求脱离血盟 */
    public static final int C_OPCODE_LEAVECLANE = 121;
    /** 要求使用密语聊天频道 */
    public static final int C_OPCODE_CHATWHISPER = 122;
    /** 要求设置治安管理 */
    public static final int C_OPCODE_CASTLESECURITY = 125;
    /** 要求登入测试 ( 接收伺服器版本 ) */
    public static final int C_OPCODE_CLIENTVERSION = 127;
    /** 要求纪录快速键 */
    public static final int C_OPCODE_CHARACTERCONFIG = 129;
    /** 要求进入游戏(确定服务器登入讯息). */
    public static final int C_OPCODE_LOGINTOSERVER = 131;
    /** 要求增加记忆座标. */
    public static final int C_OPCODE_BOOKMARK = 134;
    /** 要求查询PK次数 */
    public static final int C_OPCODE_CHECKPK = 137;
    /** 要求召唤到身边(gm) */
    public static final int C_OPCODE_CALL = 144;
    /** 要求治安管理 OK */
    public static final int C_OPCODE_SETCASTLESECURITY = 149;
    /** 要求选择 变更攻城时间 (官方已取消). */
    public static final int C_OPCODE_CHANGEWARTIME = 150;
    /** 要求创立血盟 */
    public static final int C_OPCODE_CREATECLAN = 154;
    /** 要求攻击指定物件(宠物&召唤) */
    public static final int C_OPCODE_SELECTTARGET = 155;
    /** 要求角色表情动作 */
    public static final int C_OPCODE_EXTCOMMAND = 157;
    /** 要求自动登录伺服器 与 师徒系统 */
    public static final int C_OPCODE_AUTO = 162;
    /** 要求取消交易(个人) */
    public static final int C_OPCODE_TRADEADDCANCEL = 167;
    /** 要求学习魔法(金币) */
    public static final int C_OPCODE_SKILLBUY = 173;
    /** 要求更新时间 */
    public static final int C_OPCODE_KEEPALIVE = 182;
    /** 要求结婚 (指令 /求婚) */
    public static final int C_OPCODE_PROPOSE = 185;
    /** 要求捡取物品 */
    public static final int C_OPCODE_PICKUPITEM = 188;
    /** 要求使用一般聊天频道 */
    public static final int C_OPCODE_CHAT = 190;
    /** 要求领出资金 */
    public static final int C_OPCODE_DRAWAL = 192;
    /** 要求个人商店 （物品列表） */
    public static final int C_OPCODE_PRIVATESHOPLIST = 193;
    /** 要求开关门 */
    public static final int C_OPCODE_DOOR = 199;
    /** 要求税收设定封包 */
    public static final int C_OPCODE_TAXRATE = 200;
    /** 要求脱离队伍 */
    public static final int C_OPCODE_LEAVEPARTY = 204;
    /** 要求学习魔法完成 */
    public static final int C_OPCODE_SKILLBUYOK = 207;
    /** 要求删除物品 */
    public static final int C_OPCODE_DELETEINVENTORYITEM = 209;
    /** 要求退出观看模式 */
    public static final int C_OPCODE_EXIT_GHOST = 210;
    /** 要求删除好友 */
    public static final int C_OPCODE_DELBUDDY = 211;
    /** 要求使用宠物装备 */
    public static final int C_OPCODE_USEPETITEM = 213;
    /** 要求宠物回报选单(显示宠物背包物品窗口) */
    public static final int C_OPCODE_PETMENU = 217;
    /** 要求回到登入画面(返回输入帐号密码界面). */
    public static final int C_OPCODE_RETURNTOLOGIN = 218;
    /** 要求下一页 ( 公布栏 ). */
    public static final int C_OPCODE_BOARDBACK = 221;
    /** 要求驱逐人物离开血盟 */
    public static final int C_OPCODE_BANCLAN = 222;
    /** 要求删除记忆座标 */
    public static final int C_OPCODE_BOOKMARKDELETE = 223;
    /** 要求查询血盟成员 */
    public static final int C_OPCODE_PLEDGE = 225;
    /** 玩家传送锁定 (回溯检测用). */
    public static final int C_OPCODE_MOVELOCK = 226;
    /** 要求宣战 */
    public static final int C_OPCODE_WAR = 235;
    /** 要求重置人物点数 */
    public static final int C_OPCODE_CHARRESET = 236;
    /** 要求切换角色 (到选人画面) */
    public static final int C_OPCODE_CHANGECHAR = 237;
    /** 要求物品维修/取出宠物 */
    public static final int C_OPCODE_SELECTLIST = 238;
    /** 要求交易(添加物品) */
    public static final int C_OPCODE_TRADEADDITEM = 241;
    /** 要求传送 更新周围物件 ( 无动画传送后 ) */
    public static final int C_OPCODE_TELEPORT = 242;
    /** 要求给予物品 */
    public static final int C_OPCODE_GIVEITEM = 244;
    /** 要求血盟数据(例如盟标) */
    public static final int C_OPCODE_CLAN = 246;
    /** 要求使用远距武器 */
    public static final int C_OPCODE_ARROWATTACK = 247;
    /** 要求鼠标右键传入洞穴 */
    public static final int C_OPCODE_ENTERPORTAL = 249;
    /** 要求创造角色 */
    public static final int C_OPCODE_NEWCHAR = 253;

    // //////旧版没有的////////////

    /** 要求血盟推荐数据 */
    public static final int C_OPCODE_CLAN_RECOMMEND = 228;
    // 客户端点击 0000: e4 04 00 00

    // XXX 未知 unknown
    /** 要求提取元宝. */
    public static final int C_OPCODE_CNITEM = -1;
    /** 要求确认未知购物清单2. */
    public static final int C_OPCODE_SHOPX2 = -2;
    /** 要求简讯服务. */
    public static final int C_OPCODE_MSG = -3;
    /** 要求完成学习魔法(材料). */
    public static final int C_OPCODE_SKILLBUYOKITEM = -19;
    /** 要求更新周围物件(座标点/洞穴点切换进出后). */
    public static final int C_OPCODE_TELEPORT2 = -20;
    /** 要求学习魔法清单(材料). */
    public static final int C_OPCODE_SKILLBUYITEM = -26;
    /** 要求配置已雇用的士兵. */
    public static final int C_OPCODE_PUTSOLDIER = -57;
    /** 要求新增帐号. */
    public static final int C_OPCODE_NEWACC = -59;
    /** 要求配置已雇用的士兵OK. */
    public static final int C_OPCODE_PUTHIRESOLDIER = -94;
    /** 雇请佣兵(购买佣兵完成). */
    public static final int C_OPCODE_HIRESOLDIER = -97;
    /** 要求配置城墙上的弓箭手OK. */
    public static final int C_OPCODE_PUTBOWSOLDIER = -110;

	// serverpackets
    /** 雇请佣兵(佣兵购买视窗). */
    protected static final int S_OPCODE_HIRESOLDIER = -123;
    /** 配置城墙上的弓箭手列表(佣兵购买视窗). */
    protected static final int S_OPCODE_PUTBOWSOLDIERLIST = -23;
    /** 佣兵配置清单. */
    protected static final int S_OPCODE_PUTSOLDIER = -77;

    /** 强制登出人物. */
    protected static final int S_OPCODE_CHAROUT = -110;
    /** 服务器登入讯息(使用string.tbl). */
    protected static final int S_OPCODE_COMMONINFO = -88;
    /** 未知购物清单1 Server op: 0. */
    protected static final int S_OPCODE_SHOPX1 = -0;
    /** 未知购物清单2 Server op: 71. */
    protected static final int S_OPCODE_SHOPX2 = -71;

    // 没注释的也是未知
    // //////////////
    // /////////////////
    /** 魔法购买清单(材料). */
    protected static final int S_OPCODE_SKILLBUYITEM = -22;
    /** 阅读邮件(旧). */
    public static final int S_OPCODE_LETTER = -33;

    /** 物理范围攻击 Server op: 0000. */
    protected static final int S_OPCODE_ATTACKRANGE = -127;

    // //////////////
    // /////////////////
    // 0 佣兵窗口 要有已雇用的士兵才可以配置，你要选择哪一种士兵呢？(没有选项)
    // 46 客户端传来239
    // 52 直接返回角色选择画面
    // 171 何伦对话窗口:那些法术我也只能教你到第3级。
    // 192 推荐血盟大窗口
    // 210 购买窗口 (佣兵购买?)
    // 240 内存错误
    // ////////////////////////
    /** 角色锁定(座标异常重整)【19/23/29/81/82/181/182/211/230】. */
    protected static final int S_OPCODE_CHARLOCK = 135; // TODO 不知官方啥效果

    /** NPC改变外型(宠物/谜魅用). */
    protected static final int S_OPCODE_NPCPOLY = 164; // TODO 未测试
    // /////////////////////////////////////////////////////////////
    /** 邮件封包 */
    public static final int S_OPCODE_MAIL = 1;
    /** 物件封包 */
    public static final int S_OPCODE_CHARPACK = 3;
    /** 要求传送 ( NPC传送反手 ) */
    public static final int S_OPCODE_TELEPORT = 4;
    /** 角色移除 [ 非立即 ] */
    public static final int S_OPCODE_DETELECHAROK = 5;
    /** 更新血盟数据. */
    protected static final int S_OPCODE_UPDATECLANID = 8;
    /** 广播聊天频道 */
    public static final int S_OPCODE_GLOBALCHAT = 10;
    /** 角色记忆座标名单 */
    public static final int S_OPCODE_BOOKMARKS = 11;
    /** 效果图示 { 水底呼吸 } */
    public static final int S_OPCODE_BLESSOFEVA = 12;
    /** 物件新增主人. */
    protected static final int S_OPCODE_NEWMASTER = 13;
    /** 伺服器讯息(行数, 附加字串 ) */
    public static final int S_OPCODE_SERVERMSG = 14;
    /** 角色防御 & 属性防御 更新 */
    public static final int S_OPCODE_OWNCHARATTRDEF = 15;
    /** 范围魔法 */
    public static final int S_OPCODE_RANGESKILLS = 16;
    /** 移除魔法出魔法名单 */
    public static final int S_OPCODE_DELSKILL = 18;
    /** 血盟小屋名单 */
    public static final int S_OPCODE_HOUSELIST = 24;
    /** 敏捷提升封包 */
    public static final int S_OPCODE_DEXUP = 28;
    /** 公告视窗 */
    public static final int S_OPCODE_COMMONNEWS = 30;
    /** 海底波纹(第三段加速) */
    public static final int S_OPCODE_LIQUOR = 31;
    /** 重置设定 */
    public static final int S_OPCODE_CHARRESET = 33;
    /** 物件属性 (门 开关) */
    public static final int S_OPCODE_ATTRIBUTE = 35;
    /** 可配置排列佣兵数(HTML)(EX:雇用的总佣兵数:XX 可排列的佣兵数:XX ). */
    protected static final int S_OPCODE_PUTHIRESOLDIER = 39;
    /** 角色选择视窗 / 开启拒绝名单 (封包盒子) */
    public static final int S_OPCODE_PACKETBOX = 40;
    /** 体力更新 */
    public static final int S_OPCODE_HPUPDATE = 42;
    /** 物品资讯讯息 { 使用String-h.tbl } */
    public static final int S_OPCODE_IDENTIFYDESC = 43;
    /** 血盟小屋地图 [ 地点 ] */
    public static final int S_OPCODE_HOUSEMAP = 44;
    /** 增加魔法进魔法名单 */
    public static final int S_OPCODE_ADDSKILL = 48;
    /** 围城时间设定 */
    public static final int S_OPCODE_WARTIME = 49;
    /** 角色盟徽 */
    public static final int S_OPCODE_EMBLEM = 50;
    /** 登入状态 */
    public static final int S_OPCODE_LOGINRESULT = 51;
    /** 物件亮度 */
    public static final int S_OPCODE_LIGHT = 53;
    /** 布告栏( 讯息阅读 ) */
    public static final int S_OPCODE_BOARDREAD = 56;
    /** 物件隐形 & 现形 */
    public static final int S_OPCODE_INVIS = 57;
    /** 蓝色讯息 { 使用String-h.tbl } 红色字(地狱显示字) */
    public static final int S_OPCODE_BLUEMESSAGE = 59;
    /** 物品增加封包 */
    public static final int S_OPCODE_ADDITEM = 63;
    /** 布告栏 ( 讯息列表 ) */
    public static final int S_OPCODE_BOARD = 64;
    /** 角色皇冠 */
    public static final int S_OPCODE_CASTLEMASTER = 66;
    /** 魔法效果 : 防御颣 */
    public static final int S_OPCODE_SKILLICONSHIELD = 69;
    /** 税收设定封包 */
    public static final int S_OPCODE_TAXRATE = 72;
    /** 魔力更新 */
    public static final int S_OPCODE_MPUPDATE = 73;
    /** 一般聊天频道 */
    public static final int S_OPCODE_NORMALCHAT = 76;
    /** 交易封包 */
    public static final int S_OPCODE_TRADE = 77;
    /** 播放音效(客户端Sound文件夹内的.wav文件). */
    public static final int S_OPCODE_SOUND = 84;
    /** 增加交易物品封包 */
    public static final int S_OPCODE_TRADEADDITEM = 86;
    /** Ping time[Server] = 60618372. */
    protected static final int S_OPCODE_PINGTIME = 88;
    /** 学习魔法材料不足. */
    protected static final int S_OPCODE_ITEMERROR = 89;
    /** 画面中间出现红色讯息(登入来源)(Account has just logged in form). */
    protected static final int S_OPCODE_RED = 90;
    /** 魔法效果 : 中毒 { 编号 } */
    public static final int S_OPCODE_POISON = 93;
    /** 立即中断连线 */
    public static final int S_OPCODE_DISCONNECT = 95;
    /** 魔法动画 { 精准目标 } */
    public static final int S_OPCODE_TRUETARGET = 110;
    /** 产生动画 [ 地点 ] */
    public static final int S_OPCODE_EFFECTLOCATION = 112;
    /** 物件动作种类 ( 长时间 ) */
    public static final int S_OPCODE_CHARVISUALUPDATE = 113;
    /** 夜视功能 */
    public static final int S_OPCODE_ABILITY = 116;
    /** 产生对话视窗 */
    public static final int S_OPCODE_SHOWHTML = 119;
    /** 力量提升封包 */
    public static final int S_OPCODE_STRUP = 120;
    /** 经验值更新封包 */
    public static final int S_OPCODE_EXP = 121;
    /** 物件移动 */
    public static final int S_OPCODE_MOVEOBJECT = 122;
    /** 血盟战争讯息 { 编号, 血盟名称, 目标血盟名称 } */
    public static final int S_OPCODE_WAR = 123;
    /** 角色列表 */
    public static final int S_OPCODE_CHARAMOUNT = 126;
    /** 物品可用次数 */
    public static final int S_OPCODE_ITEMAMOUNT = 127;
    /** 物件血条 */
    public static final int S_OPCODE_HPMETER = 128;
    /** 宣告进入游戏 */
    public static final int S_OPCODE_UNKNOWN1 = 131;
    /** 非玩家聊天频道 { 一般 & 大喊 } NPC */
    public static final int S_OPCODE_NPCSHOUT = 133;
    /** 人物回硕检测 OR 传送锁定 ( 无动画 ) */
    public static final int S_OPCODE_TELEPORTLOCK = 135;
    /** 正义值更新 */
    public static final int S_OPCODE_LAWFUL = 140;
    /** 物件攻击 */
    public static final int S_OPCODE_ATTACKPACKET = 142;
    /** 物品状态 (祝福 & 诅咒) */
    public static final int S_OPCODE_ITEMCOLOR = 144;
    /** 角色属性与能力值 */
    public static final int S_OPCODE_OWNCHARSTATUS = 145;
    /** 物品删除 */
    public static final int S_OPCODE_DELETEINVENTORYITEM = 148;
    /** 魔法 | 物品效果 { 加速颣 } */
    public static final int S_OPCODE_SKILLHASTE = 149;
    /** 更新角色所在的地图 */
    public static final int S_OPCODE_MAPID = 150;
    /** 伺服器版本 */
    public static final int S_OPCODE_SERVERVERSION = 151;
    /** 角色创造失败 */
    public static final int S_OPCODE_NEWCHARWRONG = 153;
    /** 选项封包 { Yes | No } */
    public static final int S_OPCODE_YES_NO = 155;
    /** 初始化OpCode */
    public static final int S_OPCODE_INITOPCODE = 161;
    /** NPC外型改变 */
    public static final int S_OPCODE_POLY = 164;
    /** 魔法效果 : 诅咒类 { 编号 } 麻痹,瘫痪 */
    public static final int S_OPCODE_PARALYSIS = 165;
    /** NPC物品贩卖 */
    public static final int S_OPCODE_SHOWSHOPSELLLIST = 170;
    /** 魔法攻击力与魔法防御力 */
    public static final int S_OPCODE_SPMR = 174;
    /** 选择一个目标 */
    public static final int S_OPCODE_SELECTTARGET = 177;
    /** 物品名单 */
    public static final int S_OPCODE_INVLIST = 180;
    /** 角色资讯 */
    public static final int S_OPCODE_CHARLIST = 184;
    /** 物件删除 */
    public static final int S_OPCODE_REMOVE_OBJECT = 185;
    /** 角色个人商店 { 购买 } */
    public static final int S_OPCODE_PRIVATESHOPLIST = 190;
    /** 游戏天气 */
    public static final int S_OPCODE_WEATHER = 193;
    /** 更新目前游戏时间 */
    public static final int S_OPCODE_GAMETIME = 194;
    /** 物品显示名称 */
    public static final int S_OPCODE_ITEMNAME = 195;
    /** 物件面向 */
    public static final int S_OPCODE_CHANGEHEADING = 199;
    /** 魔法 | 物品效果图示 { 勇敢药水颣 } */
    public static final int S_OPCODE_SKILLBRAVE = 200;
    /** 角色封号 */
    public static final int S_OPCODE_CHARTITLE = 202;
    /** 存入资金城堡宝库 (2) */
    public static final int S_OPCODE_DEPOSIT = 203;
    /** 改变物件名称 */
    public static final int S_OPCODE_CHANGENAME = 81;
    /** 损坏武器名单 */
    public static final int S_OPCODE_SELECTLIST = 208;
    /** 创造角色封包 */
    public static final int S_OPCODE_NEWCHARPACK = 212;
    /** 角色状态 (2) */
    public static final int S_OPCODE_OWNCHARSTATUS2 = 216;
    /** 物件动作种类 ( 短时间 ) */
    public static final int S_OPCODE_DOACTIONGFX = 218;
    /** 魔法购买 (金币) */
    public static final int S_OPCODE_SKILLBUY = 222;
    /** 取出城堡宝库金币 (1) */
    public static final int S_OPCODE_DRAWAL = 224;
    /** 物件复活 */
    public static final int S_OPCODE_RESURRECTION = 227;
    /** 产生动画 [ 物件 ] */
    public static final int S_OPCODE_SKILLSOUNDGFX = 232;
    /** 魔法效果 - 暗盲咒术 { 编号 } */
    public static final int S_OPCODE_CURSEBLIND = 238;
    /** 交易状态 */
    public static final int S_OPCODE_TRADESTATUS = 239;
    /** 仓库物品名单 */
    public static final int S_OPCODE_SHOWRETRIEVELIST = 250;
    /** 角色名称变紫色 */
    public static final int S_OPCODE_PINKNAME = 252;
    /** 拍卖公告栏选取金币数量 选取物品数量 */
    public static final int S_OPCODE_INPUTAMOUNT = 253;
    /** 物品购买 */
    public static final int S_OPCODE_SHOWSHOPBUYLIST = 254;
    /** 要求使用密语聊天频道 */
    public static final int S_OPCODE_WHISPERCHAT = 255;
    
    
	 public static final int S_OPCODE_MSG = 243; // 시스템 메세지 (전챗). 대만: S_OPCODE_GLOBALCHAT
    
    /** 血盟推荐 by:42621391 2014年8月19日16:43:17 */
	public static final int S_OPCODE_CLANMATCHING = 192;

    // //////////旧版没有的////////////////
    /** 丢弃物品封包 */
    // public static final int S_OPCODE_DROPITEM = 3;
    /** 动作自带魔法动画的范围魔法封包(无指定动画) */
    // public static final int S_OPCODE_NO_GFX_RANGE_ATTACK = 8;
    /** 界面宠物控制菜单消失 */
    // public static final int S_OPCODE_DELPETMENU = 33;
    /** 画面正中出现红色字(Account ? has just logged in form) */
    // public static final int S_OPCODE_REDMESSAGE = 90;
    /** 物品状态更新 */
    // public static final int S_OPCODE_ITEMSTATUS = 127;

}