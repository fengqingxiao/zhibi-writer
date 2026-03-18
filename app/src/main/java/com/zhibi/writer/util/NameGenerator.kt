package com.zhibi.writer.util

import kotlin.random.Random

/**
 * 随机取名工具
 */
object NameGenerator {
    
    // 常用姓氏
    private val commonSurnames = listOf(
        // 单姓
        "赵", "钱", "孙", "李", "周", "吴", "郑", "王", "冯", "陈",
        "褚", "卫", "蒋", "沈", "韩", "杨", "朱", "秦", "尤", "许",
        "何", "吕", "施", "张", "孔", "曹", "严", "华", "金", "魏",
        "陶", "姜", "戚", "谢", "邹", "喻", "柏", "水", "窦", "章",
        "云", "苏", "潘", "葛", "奚", "范", "彭", "郎", "鲁", "韦",
        "昌", "马", "苗", "凤", "花", "方", "俞", "任", "袁", "柳",
        "酆", "鲍", "史", "唐", "费", "廉", "岑", "薛", "雷", "贺",
        "倪", "汤", "滕", "殷", "罗", "毕", "郝", "邬", "安", "常",
        "乐", "于", "时", "傅", "皮", "卞", "齐", "康", "伍", "余",
        "元", "卜", "顾", "孟", "平", "黄", "和", "穆", "萧", "尹",
        // 复姓
        "欧阳", "上官", "皇甫", "司马", "东方", "独孤", "南宫", "万俟", "诸葛", "慕容"
    )
    
    // 男性名字常用字
    private val maleNameChars = listOf(
        "伟", "刚", "勇", "毅", "俊", "峰", "强", "军", "平", "保",
        "东", "文", "辉", "力", "明", "永", "健", "世", "广", "志",
        "义", "兴", "良", "海", "山", "仁", "波", "宁", "贵", "福",
        "生", "龙", "元", "全", "国", "胜", "学", "祥", "才", "发",
        "武", "新", "利", "清", "飞", "彬", "富", "顺", "信", "子",
        "杰", "涛", "昌", "成", "康", "星", "光", "天", "达", "安",
        "岩", "中", "茂", "进", "林", "有", "坚", "和", "彪", "博",
        "诚", "先", "敬", "震", "振", "壮", "会", "思", "群", "豪",
        "心", "邦", "承", "乐", "绍", "功", "松", "善", "厚", "庆",
        "磊", "民", "友", "裕", "河", "哲", "江", "超", "浩", "亮"
    )
    
    // 女性名字常用字
    private val femaleNameChars = listOf(
        "芳", "娜", "敏", "静", "丽", "强", "磊", "军", "洋", "勇",
        "艳", "杰", "娟", "涛", "明", "超", "秀", "霞", "平", "刚",
        "桂", "英", "华", "慧", "建", "红", "云", "文", "玲", "芬",
        "燕", "萍", "国", "琴", "荣", "倩", "林", "梅", "晶", "欢",
        "馨", "莹", "燕", "雪", "蕾", "婷", "菊", "花", "洁", "莹",
        "璐", "楠", "馨", "琪", "榕", "琦", "琳", "婧", "妍", "蕊",
        "瑶", "悦", "欣", "婉", "岚", "彤", "梦", "晴", "蓉", "莲",
        "翠", "薇", "韵", "涵", "萱", "依", "诺", "诗", "语", "雁",
        "月", "雪", "云", "雨", "露", "霜", "霞", "虹", "彩", "春",
        "夏", "秋", "冬", "琴", "棋", "书", "画", "香", "兰", "竹"
    )
    
    // 古风名字用字
    private val ancientNameChars = listOf(
        "墨", "染", "离", "落", "清", "影", "寒", "霜", "雪", "月",
        "风", "花", "梦", "烟", "云", "水", "玉", "瑶", "琼", "璇",
        "瑾", "瑜", "珏", "琳", "琅", "珂", "琪", "瑛", "琅", "瑗",
        "墨", "渊", "尘", "轩", "逸", "凌", "霄", "苍", "玄", "昊",
        "炎", "焱", "煜", "烨", "熠", "焕", "灿", "炫", "烁", "煌",
        "幽", "冥", "幽", "夜", "暮", "晨", "曦", "晖", "曜", "晴"
    )
    
    // 地点后缀
    private val locationSuffixes = listOf(
        "城", "镇", "村", "山", "峰", "谷", "林", "原", "湖", "江",
        "河", "海", "岛", "洞", "窟", "殿", "宫", "阁", "楼", "台",
        "亭", "院", "府", "庄", "园", "观", "寺", "庙", "塔", "桥"
    )
    
    // 地点前缀
    private val locationPrefixes = listOf(
        "青", "白", "黑", "红", "金", "银", "紫", "蓝", "绿", "黄",
        "东", "西", "南", "北", "中", "上", "下", "前", "后", "左",
        "龙", "虎", "凤", "麟", "鹤", "鹰", "狼", "狮", "熊", "豹",
        "云", "雾", "雨", "雪", "霜", "雷", "电", "风", "月", "日",
        "仙", "神", "魔", "妖", "鬼", "灵", "圣", "天", "地", "玄"
    )
    
    // 宗门/势力后缀
    private val factionSuffixes = listOf(
        "门", "派", "宗", "教", "帮", "会", "盟", "阁", "宫", "殿",
        "堂", "院", "楼", "谷", "山", "岛", "城", "族", "家", "府"
    )
    
    // 功法/武学后缀
    private val skillSuffixes = listOf(
        "功", "法", "诀", "经", "典", "术", "技", "招", "式", "剑",
        "刀", "拳", "掌", "指", "腿", "步", "阵", "符", "咒", "印"
    )
    
    // 丹药/灵草后缀
    private val medicineSuffixes = listOf(
        "丹", "丸", "散", "膏", "液", "露", "花", "草", "果", "根",
        "叶", "茎", "实", "子", "仁"
    )
    
    // 武器/法宝后缀
    private val weaponSuffixes = listOf(
        "剑", "刀", "枪", "戟", "斧", "钺", "钩", "叉", "鞭", "锏",
        "锤", "戈", "镋", "棍", "槊", "棒", "矛", "耙", "锤", "铲",
        "环", "轮", "镜", "铃", "塔", "鼎", "印", "扇", "伞", "杖"
    )
    
    /**
     * 生成随机姓名
     */
    fun generateName(
        gender: Gender = Gender.MALE,
        style: NameStyle = NameStyle.MODERN,
        surnameCount: Int = 1,  // 1=单姓，2=复姓
        nameLength: IntRange = 1..2
    ): String {
        // 选择姓氏
        val surname = if (surnameCount == 2) {
            commonSurnames.filter { it.length == 2 }.random()
        } else {
            commonSurnames.filter { it.length == 1 }.random()
        }
        
        // 选择名字用字
        val nameChars = when {
            style == NameStyle.ANCIENT -> ancientNameChars
            gender == Gender.MALE -> maleNameChars
            else -> femaleNameChars
        }
        
        // 生成名字
        val length = nameLength.random()
        val name = (1..length).map { nameChars.random() }.joinToString("")
        
        return surname + name
    }
    
    /**
     * 批量生成姓名
     */
    fun generateNames(
        count: Int,
        gender: Gender = Gender.MALE,
        style: NameStyle = NameStyle.MODERN
    ): List<String> {
        return (1..count).map {
            generateName(gender, style)
        }
    }
    
    /**
     * 生成地点名称
     */
    fun generateLocation(): String {
        val prefix = locationPrefixes.random()
        val middle = if (Random.nextBoolean()) {
            locationPrefixes.random()
        } else {
            ""
        }
        val suffix = locationSuffixes.random()
        return prefix + middle + suffix
    }
    
    /**
     * 批量生成地点名称
     */
    fun generateLocations(count: Int): List<String> {
        return (1..count).map { generateLocation() }
    }
    
    /**
     * 生成宗门/势力名称
     */
    fun generateFaction(): String {
        val prefix = when (Random.nextInt(3)) {
            0 -> locationPrefixes.random()  // 方位/颜色等
            1 -> commonSurnames.filter { it.length == 1 }.random() // 姓氏命名
            else -> ancientNameChars.random()  // 古风字
        }
        val suffix = factionSuffixes.random()
        return prefix + suffix
    }
    
    /**
     * 批量生成宗门名称
     */
    fun generateFactions(count: Int): List<String> {
        return (1..count).map { generateFaction() }
    }
    
    /**
     * 生成功法名称
     */
    fun generateSkill(): String {
        val prefix = ancientNameChars.random()
        val middle = if (Random.nextBoolean()) {
            ancientNameChars.random()
        } else {
            ""
        }
        val suffix = skillSuffixes.random()
        return prefix + middle + suffix
    }
    
    /**
     * 批量生成功法名称
     */
    fun generateSkills(count: Int): List<String> {
        return (1..count).map { generateSkill() }
    }
    
    /**
     * 生成丹药/灵草名称
     */
    fun generateMedicine(): String {
        val prefix = ancientNameChars.random()
        val suffix = medicineSuffixes.random()
        return prefix + suffix
    }
    
    /**
     * 生成武器/法宝名称
     */
    fun generateWeapon(): String {
        val prefix = ancientNameChars.random()
        val middle = if (Random.nextBoolean()) {
            listOf("龙", "凤", "虎", "麟", "天", "地", "玄", "黄").random()
        } else {
            ""
        }
        val suffix = weaponSuffixes.random()
        return prefix + middle + suffix
    }
    
    /**
     * 根据类型生成名称
     */
    fun generateByType(type: NameType, count: Int = 1): List<String> {
        return when (type) {
            NameType.MALE_NAME -> generateNames(count, Gender.MALE)
            NameType.FEMALE_NAME -> generateNames(count, Gender.FEMALE)
            NameType.ANCIENT_NAME -> generateNames(count, Gender.MALE, NameStyle.ANCIENT)
            NameType.LOCATION -> generateLocations(count)
            NameType.FACTION -> generateFactions(count)
            NameType.SKILL -> generateSkills(count)
            NameType.MEDICINE -> (1..count).map { generateMedicine() }
            NameType.WEAPON -> (1..count).map { generateWeapon() }
        }
    }
}

/**
 * 性别
 */
enum class Gender {
    MALE, FEMALE
}

/**
 * 名字风格
 */
enum class NameStyle {
    MODERN,     // 现代
    ANCIENT,    // 古风
    WESTERN     // 西式
}

/**
 * 名称类型
 */
enum class NameType {
    MALE_NAME,      // 男性姓名
    FEMALE_NAME,    // 女性姓名
    ANCIENT_NAME,   // 古风姓名
    LOCATION,       // 地点
    FACTION,        // 宗门/势力
    SKILL,          // 功法/武学
    MEDICINE,       // 丹药/灵草
    WEAPON          // 武器/法宝
}
