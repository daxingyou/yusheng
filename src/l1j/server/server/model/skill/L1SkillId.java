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
package l1j.server.server.model.skill;

public class L1SkillId {
	public static final int SKILLS_BEGIN = 1;

	/*
	 * Regular Magic Lv1-10
	 */
	/**
	 * 初级治愈术
	 */
	public static final int HEAL = 1; // E: LESSER_HEAL

	/**
	 * 日光术2
	 */
	public static final int LIGHT = 2;

	/**
	 * 保护罩3
	 */
	public static final int SHIELD = 3;

	/**
	 * 光箭4
	 */
	public static final int ENERGY_BOLT = 4;

	/**
	 * 指定传送5
	 */
	public static final int TELEPORT = 5;

	/**
	 * 冰箭6
	 */
	public static final int ICE_DAGGER = 6;

	/**
	 * 风刃7
	 */
	public static final int WIND_CUTTER = 7; // E: WIND_SHURIKEN

	/**
	 * 神圣武器8
	 */
	public static final int HOLY_WEAPON = 8;

	/**
	 * 解毒术9
	 */
	public static final int CURE_POISON = 9;

	/**
	 * 寒冷战栗10
	 */
	public static final int CHILL_TOUCH = 10;

	/**
	 * 毒咒11
	 */
	public static final int CURSE_POISON = 11;

	/**
	 * 拟似魔法武器12
	 */
	public static final int ENCHANT_WEAPON = 12;

	/**
	 * 无所遁形术13
	 */
	public static final int DETECTION = 13;

	/**
	 * 负重强化14
	 */
	public static final int DECREASE_WEIGHT = 14;

	/**
	 * 地狱之牙15
	 */
	public static final int FIRE_ARROW = 15;

	/**
	 * 火箭16
	 */
	public static final int STALAC = 16;

	/**
	 * 极光雷电17
	 */
	public static final int LIGHTNING = 17;

	/**
	 * 起死回生术18
	 */
	public static final int TURN_UNDEAD = 18;

	/**
	 * 中级治愈术19
	 */
	public static final int EXTRA_HEAL = 19; // E: HEAL

	/**
	 * 闇盲咒术20
	 */
	public static final int CURSE_BLIND = 20;

	/**
	 * 铠甲护持21
	 */
	public static final int BLESSED_ARMOR = 21;

	/**
	 * 寒冰气息22
	 */
	public static final int FROZEN_CLOUD = 22;

	/**
	 * 能量感测23
	 */
	public static final int WEAK_ELEMENTAL = 23; // E: REVEAL_WEAKNESS

	// none = 24

	/**
	 * 燃烧的火球25
	 */
	public static final int FIREBALL = 25;

	/**
	 * 通畅气脉术26
	 */
	public static final int PHYSICAL_ENCHANT_DEX = 26; // E: ENCHANT_DEXTERITY

	/**
	 * 坏物术27
	 */
	public static final int WEAPON_BREAK = 27;

	/**
	 * 吸血鬼之吻28
	 */
	public static final int VAMPIRIC_TOUCH = 28;

	/**
	 * 缓速术29
	 */
	public static final int SLOW = 29;

	/**
	 * 岩牢30
	 */
	public static final int EARTH_JAIL = 30;

	/**
	 * 魔法屏障31
	 */
	public static final int COUNTER_MAGIC = 31;

	/**
	 * 冥想术32
	 */
	public static final int MEDITATION = 32;

	/**
	 * 木乃伊的诅咒33
	 */
	public static final int CURSE_PARALYZE = 33;

	/**
	 * 极道落雷34
	 */
	public static final int CALL_LIGHTNING = 34;

	/**
	 * 高级治愈术35
	 */
	public static final int GREATER_HEAL = 35;

	/**
	 * 迷魅术36
	 */
	public static final int TAMING_MONSTER = 36; // E: TAME_MONSTER

	/**
	 * 圣洁之光37
	 */
	public static final int REMOVE_CURSE = 37;

	/**
	 * 冰锥38
	 */
	public static final int CONE_OF_COLD = 38;

	/**
	 * 魔力夺取39
	 */
	public static final int MANA_DRAIN = 39;

	/**
	 * 黑闇之影40
	 */
	public static final int DARKNESS = 40;

	/**
	 * 造尸术41
	 */
	public static final int CREATE_ZOMBIE = 41;

	/**
	 * 体魄强健术42
	 */
	public static final int PHYSICAL_ENCHANT_STR = 42; // E: ENCHANT_MIGHTY

	/**
	 * 加速术43
	 */
	public static final int HASTE = 43;

	/**
	 * 魔法相消术44
	 */
	public static final int CANCELLATION = 44; // E: CANCEL MAGIC

	/**
	 * 地裂术45
	 */
	public static final int ERUPTION = 45;

	/**
	 * 烈炎术46
	 */
	public static final int SUNBURST = 46;

	/**
	 * 弱化术47
	 */
	public static final int WEAKNESS = 47;

	/**
	 * 祝福魔法武器48
	 */
	public static final int BLESS_WEAPON = 48;

	/**
	 * 体力回复术49
	 */
	public static final int HEAL_ALL = 49; // E: HEAL_PLEDGE

	/**
	 * 冰矛围篱50
	 */
	public static final int ICE_LANCE = 50;

	/**
	 * 召唤术51
	 */
	public static final int SUMMON_MONSTER = 51;

	/**
	 * 神圣疾走52
	 */
	public static final int HOLY_WALK = 52;

	/**
	 * 龙卷风53
	 */
	public static final int TORNADO = 53;

	/**
	 * 强力加速术54
	 */
	public static final int GREATER_HASTE = 54;

	/**
	 * 狂暴术55
	 */
	public static final int BERSERKERS = 55;

	/**
	 * 疾病术56
	 */
	public static final int DISEASE = 56;

	/**
	 * 全部治愈术57
	 */
	public static final int FULL_HEAL = 57;

	/**
	 * 火牢58
	 */
	public static final int FIRE_WALL = 58;

	/**
	 * 冰雪暴59
	 */
	public static final int BLIZZARD = 59;

	/**
	 * 隐身术60
	 */
	public static final int INVISIBILITY = 60;

	/**
	 * 返生术61
	 */
	public static final int RESURRECTION = 61;

	/**
	 * 震裂术62
	 */
	public static final int EARTHQUAKE = 62;

	/**
	 * 治愈能量风暴63
	 */
	public static final int LIFE_STREAM = 63;

	/**
	 * 魔法封印64
	 */
	public static final int SILENCE = 64;

	/**
	 * 雷霆风暴65
	 */
	public static final int LIGHTNING_STORM = 65;

	/**
	 * 沉睡之雾66
	 */
	public static final int FOG_OF_SLEEPING = 66;

	/**
	 * 变形术67
	 */
	public static final int SHAPE_CHANGE = 67; // E: POLYMORPH

	/**
	 * 圣结界68
	 */
	public static final int IMMUNE_TO_HARM = 68;

	/**
	 * 集体传送术69
	 */
	public static final int MASS_TELEPORT = 69;

	/**
	 * 火风暴70
	 */
	public static final int FIRE_STORM = 70;

	/**
	 * 药水霜化术71
	 */
	public static final int DECAY_POTION = 71;

	/**
	 * 强力无所遁形术72
	 */
	public static final int COUNTER_DETECTION = 72;

	/**
	 * 创造魔法武器73
	 */
	public static final int CREATE_MAGICAL_WEAPON = 73;

	/**
	 * 流星雨74
	 */
	public static final int METEOR_STRIKE = 74;

	/**
	 * 终极返生术75
	 */
	public static final int GREATER_RESURRECTION = 75;

	/**
	 * 集体缓速术76
	 */
	public static final int MASS_SLOW = 76;

	/**
	 * 究极光裂术77
	 */
	public static final int DISINTEGRATE = 77; // E: DESTROY

	/**
	 * 绝对屏障78
	 */
	public static final int ABSOLUTE_BARRIER = 78;

	/**
	 * 灵魂升华79
	 */
	public static final int ADVANCE_SPIRIT = 79;

	/**
	 * 冰雪飓风80
	 */
	public static final int FREEZING_BLIZZARD = 80;

	// none = 81 - 86
	/*
	 * Knight skills
	 */

	/**
	 * 冲击之晕87
	 */
	public static final int SHOCK_STUN = 87; // E: STUN_SHOCK

	/**
	 * 增幅防御88
	 */
	public static final int REDUCTION_ARMOR = 88;

	/**
	 * 尖刺盔甲89
	 */
	public static final int BOUNCE_ATTACK = 89;

	/**
	 * 坚固防护90
	 */
	public static final int SOLID_CARRIAGE = 90;

	/**
	 * 反击屏障91
	 */
	public static final int COUNTER_BARRIER = 91;

	// none = 92-96
	/*
	 * Dark Spirit Magic
	 */

	/**
	 * 暗隐术97
	 */
	public static final int BLIND_HIDING = 97;

	/**
	 * 附加剧毒98
	 */
	public static final int ENCHANT_VENOM = 98;

	/**
	 * 影之防护99
	 */
	public static final int SHADOW_ARMOR = 99;

	/**
	 * 提炼魔石100
	 */
	public static final int BRING_STONE = 100;

	/**
	 * 行走加速101
	 */
	public static final int MOVING_ACCELERATION = 101; // E: PURIFY_STONE

	/**
	 * 燃烧斗志102
	 */
	public static final int BURNING_SPIRIT = 102;

	/**
	 * 暗黑盲咒103
	 */
	public static final int DARK_BLIND = 103;

	/**
	 * 毒性抵抗104
	 */
	public static final int VENOM_RESIST = 104;

	/**
	 * 双重破坏105
	 */
	public static final int DOUBLE_BRAKE = 105;

	/**
	 * 暗影闪避106
	 */
	public static final int UNCANNY_DODGE = 106;

	/**
	 * 暗影之牙107
	 */
	public static final int SHADOW_FANG = 107;

	/**
	 * 会心一击108
	 */
	public static final int FINAL_BURN = 108;

	/**
	 * 力量提升109
	 */
	public static final int DRESS_MIGHTY = 109;

	/**
	 * 敏捷提升110
	 */
	public static final int DRESS_DEXTERITY = 110;

	/**
	 * 闪避提升111
	 */
	public static final int DRESS_EVASION = 111;

	// none = 112
	/*
	 * Royal Magic
	 */

	/**
	 * 精准目标113
	 */
	public static final int TRUE_TARGET = 113;

	/**
	 * 激励士气114
	 */
	public static final int GLOWING_AURA = 114;

	/**
	 * 钢铁士气115
	 */
	public static final int SHINING_AURA = 115;

	/**
	 * 呼唤盟友116
	 */
	public static final int CALL_CLAN = 116; // E: CALL_PLEDGE_MEMBER

	/**
	 * 冲击士气117
	 */
	public static final int BRAVE_AURA = 117;

	/**
	 * 援护盟友118
	 */
	public static final int RUN_CLAN = 118;

	// unknown = 119 - 120
	// none = 121 - 128
	/*
	 * Spirit Magic
	 */

	/**
	 * 魔法防御129
	 */
	public static final int RESIST_MAGIC = 129;

	/**
	 * 心灵转换130
	 */
	public static final int BODY_TO_MIND = 130;

	/**
	 * 世界树的呼唤131
	 */
	public static final int TELEPORT_TO_MATHER = 131;

	/**
	 * 三重矢132
	 */
	public static final int TRIPLE_ARROW = 132;

	/**
	 * 弱化属性133
	 */
	public static final int ELEMENTAL_FALL_DOWN = 133;

	/**
	 * 镜反射134
	 */
	public static final int COUNTER_MIRROR = 134;

	// none = 135 - 136

	/**
	 * 净化精神137
	 */
	public static final int CLEAR_MIND = 137;

	/**
	 * 属性防御138
	 */
	public static final int RESIST_ELEMENTAL = 138;

	// none = 139 - 144

	/**
	 * 释放元素145
	 */
	public static final int RETURN_TO_NATURE = 145;

	/**
	 * 魂体转换146
	 */
	public static final int BLOODY_SOUL = 146; // E: BLOOD_TO_SOUL

	/**
	 * 单属性防御147
	 */
	public static final int ELEMENTAL_PROTECTION = 147; // E:PROTECTION_FROM_ELEMENTAL

	/**
	 * 火焰武器148
	 */
	public static final int FIRE_WEAPON = 148;

	/**
	 * 风之神射149
	 */
	public static final int WIND_SHOT = 149;

	/**
	 * 风之疾走150
	 */
	public static final int WIND_WALK = 150;

	/**
	 * 大地防护151
	 */
	public static final int EARTH_SKIN = 151;

	/**
	 * 地面障碍152
	 */
	public static final int ENTANGLE = 152;

	/**
	 * 魔法消除153
	 */
	public static final int ERASE_MAGIC = 153;

	/**
	 * 召唤属性精灵154
	 */
	public static final int LESSER_ELEMENTAL = 154; // E:SUMMON_LESSER_ELEMENTAL

	/**
	 * 烈炎气息155
	 */
	public static final int FIRE_BLESS = 155; // E: BLESS_OF_FIRE

	/**
	 * 暴风之眼156
	 */
	public static final int STORM_EYE = 156; // E: EYE_OF_STORM

	/**
	 * 大地屏障157
	 */
	public static final int EARTH_BIND = 157;

	/**
	 * 生命之泉158
	 */
	public static final int NATURES_TOUCH = 158;

	/**
	 * 大地的祝福159
	 */
	public static final int EARTH_BLESS = 159; // E: BLESS_OF_EARTH

	/**
	 * 水之防护160
	 */
	public static final int AQUA_PROTECTER = 160;

	/**
	 * 封印禁地161
	 */
	public static final int AREA_OF_SILENCE = 161;

	/**
	 * 召唤强力属性精灵162
	 */
	public static final int GREATER_ELEMENTAL = 162; // E:SUMMON_GREATER_ELEMENTAL

	/**
	 * 烈炎武器163
	 */
	public static final int BURNING_WEAPON = 163;

	/**
	 * 生命的祝福164
	 */
	public static final int NATURES_BLESSING = 164;

	/**
	 * 生命呼唤165
	 */
	public static final int CALL_OF_NATURE = 165; // E: NATURES_MIRACLE

	/**
	 * 暴风神射166
	 */
	public static final int STORM_SHOT = 166;

	/**
	 * 风之枷锁167
	 */
	public static final int WIND_SHACKLE = 167;

	/**
	 * 钢铁防护168
	 */
	public static final int IRON_SKIN = 168;

	/**
	 * 体能激发169
	 */
	public static final int EXOTIC_VITALIZE = 169;

	/**
	 * 水之元气170
	 */
	public static final int WATER_LIFE = 170;

	/**
	 * 属性之火171
	 */
	public static final int ELEMENTAL_FIRE = 171;

	/**
	 * 暴风疾走172
	 */
	public static final int STORM_WALK = 172;

	/**
	 * 污浊之水173
	 */
	public static final int POLLUTE_WATER = 173;

	/**
	 * 精准射击174
	 */
	public static final int STRIKER_GALE = 174;

	/**
	 * 烈焰之魂175
	 */
	public static final int SOUL_OF_FLAME = 175;

	/**
	 * 能量激发176
	 */
	public static final int ADDITIONAL_FIRE = 176;
	
	
	/**
	 * 龍之護鎧181
	 */
	public static final int DRAGON_SKIN = 181;

	/**
	 * 燃燒擊砍182
	 */
	public static final int BURNING_SLASH = 182;

	/**
	 * 護衛毀滅183
	 */
	public static final int GUARD_BRAKE = 183;

	/**
	 * 岩漿噴吐184
	 */
	public static final int MAGMA_BREATH = 184;

	/**
	 * 覺醒：安塔瑞斯185
	 */
	public static final int AWAKEN_ANTHARAS = 185;

	/**
	 * 血之渴望186
	 */
	public static final int BLOODLUST = 186;

	/**
	 * 屠宰者187
	 */
	public static final int FOE_SLAYER = 187;

	/**
	 * 恐懼無助188
	 */
	public static final int RESIST_FEAR = 188;

	/**
	 * 衝擊之膚189
	 */
	public static final int SHOCK_SKIN = 189;

	/**
	 * 覺醒：法利昂190
	 */
	public static final int AWAKEN_FAFURION = 190;

	/**
	 * 致命身軀191
	 */
	public static final int MORTAL_BODY = 191;

	/**
	 * 奪命之雷192
	 */
	public static final int THUNDER_GRAB = 192;

	/**
	 * 驚悚死神193
	 */
	public static final int HORROR_OF_DEATH = 193;

	/**
	 * 暴龍之眼194
	 */
	public static final int FREEZING_BREATH = 194;

	/**
	 * 覺醒：巴拉卡斯195
	 */
	public static final int AWAKEN_VALAKAS = 195;

	/*
	 * Illusionist Magic
	 */

	/**
	 * 镜像201
	 */
	public static final int MIRROR_IMAGE = 201;

	/**
	 * 混亂202
	 */
	public static final int CONFUSION = 202;

	/**
	 * 暴擊203
	 */
	public static final int SMASH = 203;

	/**
	 * 幻覺：歐吉204
	 */
	public static final int ILLUSION_OGRE = 204;

	/**
	 * 立方：燃燒205
	 */
	public static final int CUBE_IGNITION = 205;

	/**
	 * 專注206
	 */
	public static final int CONCENTRATION = 206;

	/**
	 * 心靈破壞207
	 */
	public static final int MIND_BREAK = 207;

	/**
	 * 骷髏毀壞208
	 */
	public static final int BONE_BREAK = 208;

	/**
	 * 幻覺：巫妖209
	 */
	public static final int ILLUSION_LICH = 209;

	/**
	 * 立方：地裂210
	 */
	public static final int CUBE_QUAKE = 210;

	/**
	 * 耐力211
	 */
	public static final int PATIENCE = 211;

	/**
	 * 幻想212
	 */
	public static final int PHANTASM = 212;

	/**
	 * 隱形破壞者213
	 */
	public static final int ARM_BREAKER = 213;

	/**
	 * 幻覺：鑽石高崙214
	 */
	public static final int ILLUSION_DIA_GOLEM = 214;

	/**
	 * 立方：衝擊215
	 */
	public static final int CUBE_SHOCK = 215;

	/**
	 * 洞察216
	 */
	public static final int INSIGHT = 216;

	/**
	 * 恐慌217
	 */
	public static final int PANIC = 217;

	/**
	 * 疼痛的歡愉218
	 */
	public static final int JOY_OF_PAIN = 218;

	/**
	 * 幻覺：化身219
	 */
	public static final int ILLUSION_AVATAR = 219;

	/**
	 * 立方：和諧220
	 */
	public static final int CUBE_BALANCE = 220;


	public static final int SKILLS_END = 220;//结束ID

	/*
	 * Status
	 */
	public static final int STATUS_BEGIN = 1000;

	public static final int STATUS_BRAVE = 1000;

	public static final int STATUS_HASTE = 1001;

	public static final int STATUS_BLUE_POTION = 1002;

	public static final int STATUS_UNDERWATER_BREATH = 1003;

	public static final int STATUS_WISDOM_POTION = 1004;

	public static final int STATUS_CHAT_PROHIBITED = 1005;

	public static final int STATUS_POISON = 1006;

	public static final int STATUS_POISON_SILENCE = 1007;

	public static final int STATUS_POISON_PARALYZING = 1008;

	public static final int STATUS_POISON_PARALYZED = 1009;

	public static final int STATUS_CURSE_PARALYZING = 1010;

	public static final int STATUS_CURSE_PARALYZED = 1011;

	public static final int STATUS_FLOATING_EYE = 1012;

	// 补充激励、能力药水判断 by 丫杰
	public static final int STATUS_STR_POISON = 1013;
	public static final int STATUS_DEX_POISON = 1014;
	// 补充激励、能力神药水判断 by 丫杰
	
	/**
	 * 精靈餅乾效果
	 */
	public static final int STATUS_ELFBRAVE = 1016;

	public static final int STATUS_END = 1016; // 改成1014 by 丫杰

	public static final int GMSTATUS_BEGIN = 2000;

	public static final int GMSTATUS_INVISIBLE = 2000;

	public static final int GMSTATUS_HPBAR = 2001;

	public static final int GMSTATUS_SHOWTRAPS = 2002;

	public static final int GMSTATUS_END = 2002;

	public static final int COOKING_BEGIN = 3000;

	public static final int COOKING_1_0_N = 3000;

	public static final int COOKING_1_1_N = 3001;

	public static final int COOKING_1_2_N = 3002;

	public static final int COOKING_1_3_N = 3003;

	public static final int COOKING_1_4_N = 3004;

	public static final int COOKING_1_5_N = 3005;

	public static final int COOKING_1_6_N = 3006;

	public static final int COOKING_1_7_N = 3007;

	public static final int COOKING_1_0_S = 3050;

	public static final int COOKING_1_1_S = 3051;

	public static final int COOKING_1_2_S = 3052;

	public static final int COOKING_1_3_S = 3053;

	public static final int COOKING_1_4_S = 3054;

	public static final int COOKING_1_5_S = 3055;

	public static final int COOKING_1_6_S = 3056;

	public static final int COOKING_1_7_S = 3057;

	public static final int COOKING_END = 3057;

	public static final int STATUS_FREEZE = 10071;
	
	public static final int CHECKAITIME= 10081;
	
	public static final int AITIME = 10082;
	
	public static final int WAITTIME = 10083;
	
	public static final int CHATSLEEPTIME = 10084;
	
	/**
	 * 经验道具
	 */
	public static final int EXPITEM = 10085;
	
	public static final int CHECKFZ = 999999;

	public static final int GUAJI_AI_SKILLID = 10086;

	public static final int AI_ONLIN_SKILLID = 10087;
	
	/**
	 * 弱点曝光
	 */
    /** 锁链剑 (弱点曝光 LV1) **/
    public static final int SPECIAL_EFFECT_WEAKNESS_LV1 = 5001;
    /** 锁链剑 (弱点曝光 LV2) **/
    public static final int SPECIAL_EFFECT_WEAKNESS_LV2 = 5002;
    /** 锁链剑 (弱点曝光 LV3) **/
    public static final int SPECIAL_EFFECT_WEAKNESS_LV3 = 5003;

	/** AI系统 */
/*	public static final int AICHECK = 20006;*/

	
}
