package com.example.textgame.repository;

import com.example.textgame.model.GameChoice;
import com.example.textgame.model.GameNode;
import com.example.textgame.model.DialogueLine; // (新) 导入
import com.example.textgame.model.CharacterSprite; // (新) 导入
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryGameRepository {

    private final Map<String, GameNode> nodeStore = new ConcurrentHashMap<>();

    public Optional<GameNode> findNodeById(String nodeId) {
        return Optional.ofNullable(nodeStore.get(nodeId));
    }

    /**
     * (已完全重构为视觉小说格式)
     * 心理主题游戏脚本
     * * 注意：
     * 1. `background` 和 `image` 字段引用的图片需要您自己提供。
     * 2. 我将使用 "旁白", "你", "图书管理员" 等作为示例角色名。
     * 3. 立绘位置 "left", "center", "right" 将由前端 CSS 处理。
     */
    @PostConstruct
    public void initGameData() {
        // --- 属性键 ---
        final String INSIGHT = "insight"; // 洞察力
        final String RESOLVE = "resolve"; // 决心
        final String EMPATHY = "empathy"; // 同理心

        // --- 资源占位符 (请替换为您自己的文件名) ---
        // (注意：路径现在在 game.html 中处理，这里只存文件名)
        final String BG_WHITE_ROOM = "bg_white_room.jpg";
        final String BG_LIBRARY = "bg_library.jpg";
        final String BG_LIBRARY_DOOR = "bg_library_door.jpg";
        final String BG_SECRET_PASSAGE = "bg_secret_passage.jpg";
        final String BG_ECHO_ROOM = "bg_echo_room.jpg";
        final String BG_STARLIGHT = "bg_starlight.jpg";
        final String BG_GARDEN_PATH = "bg_garden_path.jpg";
        final String BG_LIGHT_GATE = "bg_light_gate.jpg";
        final String BG_ROPE_BRIDGE = "bg_rope_bridge.jpg";
        final String BG_BRIDGE_OTHER_SIDE = "bg_bridge_other_side.jpg";
        final String BG_CAVE_ENTRANCE = "bg_cave_entrance.jpg";
        final String BG_LIGHT_BRIDGE = "bg_light_bridge.jpg";
        final String BG_CAVE_DARK = "bg_cave_dark.jpg";
        final String BG_CAVE_LIGHT = "bg_cave_light.jpg";
        final String BG_OPEN_FIELD = "bg_open_field.jpg";

        // 立绘 (修复：从 .png 修改为 .jpg)
        final CharacterSprite LIBRARIAN_NEUTRAL = new CharacterSprite("sprite_librarian_neutral.jpg", "right");
        final CharacterSprite LIBRARIAN_SMILE = new CharacterSprite("sprite_librarian_smile.jpg", "right");
        final CharacterSprite LIBRARIAN_MAP = new CharacterSprite("sprite_librarian_map.jpg", "right");


        // --- 节点 START ---
        GameNode startNode = new GameNode();
        startNode.setNodeId("START");
        startNode.setBackground(BG_WHITE_ROOM);
        startNode.setSprites(List.of());
        startNode.setDialogue(List.of(
                new DialogueLine("旁白", "你在一间纯白色的房间醒来，房间里只有一张桌子和两扇门。"),
                new DialogueLine("旁白", "桌子上有一张便条，写着：'欢迎来到心智试炼，选择你的道路。'")
        ));
        startNode.setChoices(List.of(
                new GameChoice("S_C1", "（需要洞察 > 4）仔细检查便条", "CHECK_NOTE", Map.of(INSIGHT, 5), Map.of(INSIGHT, 1)),
                new GameChoice("S_C2", "走向写着'稳定'的门", "DOOR_STABLE", null, null),
                new GameChoice("S_C3", "走向写着'挑战'的门", "DOOR_CHALLENGE", null, Map.of(RESOLVE, 1))
        ));
        nodeStore.put("START", startNode);

        // --- 分支 1：检查便条 ---
        GameNode checkNote = new GameNode("CHECK_NOTE", BG_WHITE_ROOM, List.of(), List.of(
                new DialogueLine("旁白", "你发现便条背面用铅笔写着小字：'属性决定命运，但选择塑造自我'。"),
                new DialogueLine("旁白", "你对这个世界的规则有了初步了解。")
        ), List.of(
                new GameChoice("CN_C1", "走向'稳定'之门", "DOOR_STABLE", null, null),
                new GameChoice("CN_C2", "走向'挑战'之门", "DOOR_CHALLENGE", null, Map.of(RESOLVE, 1))
        ));
        nodeStore.put("CHECK_NOTE", checkNote);

        // --- 分支 2：稳定之门 ---
        GameNode doorStable = new GameNode("DOOR_STABLE", BG_LIBRARY, List.of(LIBRARIAN_NEUTRAL), List.of(
                new DialogueLine("旁白", "你打开门，进入一个安静的图书馆。柔和的灯光洒在书架上。"),
                new DialogueLine("旁白", "一个戴眼镜的人坐在角落看书，他抬头看了你一眼，似乎并不惊讶。"),
                new DialogueLine("图书管理员", "...")
        ), List.of(
                new GameChoice("LIB_C1", "（需要同理心 > 5）主动上前打招呼", "LIB_TALK", Map.of(EMPATHY, 6), Map.of(EMPATHY, 2)),
                new GameChoice("LIB_C2", "（需要洞察 > 5）在远处观察他", "LIB_OBSERVE", Map.of(INSIGHT, 6), Map.of(INSIGHT, 2)),
                new GameChoice("LIB_C3", "（需要决心 > 5）无视他，直接寻找出口", "LIB_IGNORE", Map.of(RESOLVE, 6), Map.of(RESOLVE, 1))
        ));
        nodeStore.put("DOOR_STABLE", doorStable);

        // --- 分支 2.1：稳定 -> 对话 ---
        GameNode libTalk = new GameNode("LIB_TALK", BG_LIBRARY, List.of(LIBRARIAN_SMILE), List.of(
                new DialogueLine("你", "你好。"),
                new DialogueLine("图书管理员", "你好，旅行者。知识是心灵的庇护所，但有时也是枷锁。"),
                new DialogueLine("图书管理员", "这本书或许能给你一些启示。")
        ), List.of(
                new GameChoice("LIB_TALK_ACCEPT", "接受书本", "LIB_BOOK", null, Map.of(INSIGHT, 2, EMPATHY, 1)),
                new GameChoice("LIB_TALK_REJECT", "婉拒好意，继续寻找出路", "LIB_IGNORE", null, Map.of(RESOLVE, 1))
        ));
        nodeStore.put("LIB_TALK", libTalk);

        // --- 分支 2.1.1: 稳定 -> 对话 -> 接受书本 ---
        GameNode libBook = new GameNode("LIB_BOOK", BG_LIBRARY, List.of(LIBRARIAN_SMILE), List.of(
                new DialogueLine("旁白", "你翻开书，书页上自动浮现出文字：'内心的平静源于接纳，而非逃避。'"),
                new DialogueLine("旁白", "你感觉内心安宁了许多。")
        ), List.of(
                new GameChoice("END_LB", "留在图书馆阅读 (结局：平静的学者)", "END_PEACEFUL_SCHOLAR", null, Map.of(INSIGHT, 5, EMPATHY, 5))
        ));
        nodeStore.put("LIB_BOOK", libBook);

        // --- 分支 2.2：稳定 -> 观察 ---
        GameNode libObserve = new GameNode("LIB_OBSERVE", BG_LIBRARY, List.of(LIBRARIAN_NEUTRAL), List.of(
                new DialogueLine("旁白", "你发现他看的书是关于'认知陷阱'的，书页旁边放着一张地图，似乎描绘了这个试炼空间。"),
                new DialogueLine("旁白", "他似乎察觉到了你的目光。")
        ), List.of(
                new GameChoice("LIB_OBS_ASK", "（需要决心 > 4）上前询问地图的事", "LIB_MAP", Map.of(RESOLVE, 5), Map.of(RESOLVE, 1)),
                new GameChoice("LIB_OBS_LEAVE", "保持距离，悄悄离开", "LIB_LEAVE_QUIETLY", null, Map.of(INSIGHT, 1))
        ));
        nodeStore.put("LIB_OBSERVE", libObserve);

        // --- 分支 2.2.1: 稳定 -> 观察 -> 询问地图 ---
        GameNode libMap = new GameNode("LIB_MAP", BG_LIBRARY, List.of(LIBRARIAN_MAP), List.of(
                new DialogueLine("图书管理员", "这地图？它描绘了可能性，而非定局。真正的地图在你心中。"),
                new DialogueLine("图书管理员", "但如果你坚持，我可以告诉你一条捷径...")
        ), List.of(
                new GameChoice("LIB_MAP_TRUST", "相信他，走隐藏的门", "SECRET_PASSAGE", null, Map.of(EMPATHY, -1, RESOLVE, 2)),
                new GameChoice("LIB_MAP_SKEPTIC", "表示怀疑，选择自己离开", "LIB_LEAVE_QUIETLY", null, Map.of(INSIGHT, 2))
        ));
        nodeStore.put("LIB_MAP", libMap);

        // --- 分支 2.2.1.1: 隐藏门 ---
        GameNode secretPassage = new GameNode("SECRET_PASSAGE", BG_SECRET_PASSAGE, List.of(), List.of(
                new DialogueLine("旁白", "隐藏的门后是一条狭窄向下的楼梯，空气中弥漫着尘土和遗忘的气息。"),
                new DialogueLine("旁白", "你听到了微弱的回声。")
        ), List.of(
                new GameChoice("SP_DESCEND", "小心地走下去", "SP_ECHO_ROOM", null, Map.of(RESOLVE, 1)),
                new GameChoice("SP_LISTEN", "（需要洞察 > 7）仔细倾听回声", "SP_LISTEN_SUCCESS", Map.of(INSIGHT, 8), Map.of(INSIGHT, 2)),
                new GameChoice("SP_RETURN", "感觉不对劲，返回图书馆", "LIB_LEAVE_QUIETLY", null, Map.of(RESOLVE, -1))
        ));
        nodeStore.put("SECRET_PASSAGE", secretPassage);

        // --- 分支 2.2.1.1.1: 回声石室 ---
        GameNode spEchoRoom = new GameNode("SP_ECHO_ROOM", BG_ECHO_ROOM, List.of(), List.of(
                new DialogueLine("旁白", "楼梯尽头是一个圆形石室，你的脚步声被无限放大。"),
                new DialogueLine("旁白", "墙壁上刻满了模糊不清的面孔。")
        ), List.of(
                new GameChoice("SP_ER_TOUCH", "（需要同理心 > 6）尝试触摸那些面孔", "SP_FACES_EMPATHY", Map.of(EMPATHY, 7), Map.of(EMPATHY, 2)),
                new GameChoice("SP_ER_SHOUT", "（需要决心 > 6）大声呼喊打破回声", "SP_SHOUT_RESOLVE", Map.of(RESOLVE, 7), Map.of(RESOLVE, 2)),
                new GameChoice("SP_ER_FIND_EXIT", "（需要洞察 > 6）寻找出口", "SP_EXIT_INSIGHT", Map.of(INSIGHT, 7), Map.of(INSIGHT, 2))
        ));
        nodeStore.put("SP_ECHO_ROOM", spEchoRoom);

        // --- 分支 2.2.1.1.X: 汇合点 (星光) ---
        GameNode spConverge = new GameNode("SP_CONVERGE", BG_STARLIGHT, List.of(), List.of(
                new DialogueLine("旁白", "无论是沟通、打破还是寻找，你最终找到了石室的出口，进入了一个充满星光的空间。")
        ), List.of(
                new GameChoice("END_SP", "踏入星光 (结局：穿越回声者)", "END_ECHO_WALKER", null, Map.of("insight",3, "resolve",3, "empathy",3))
        ));
        nodeStore.put("SP_FACES_EMPATHY", spConverge);
        nodeStore.put("SP_SHOUT_RESOLVE", spConverge);
        nodeStore.put("SP_EXIT_INSIGHT", spConverge);
        nodeStore.put("SP_LISTEN_SUCCESS", spConverge);
        nodeStore.put("SP_CONVERGE", spConverge);

        // --- 分支 2.2.2 / 2.3: 离开图书馆 ---
        GameNode libLeaveQuietly = new GameNode("LIB_LEAVE_QUIETLY", BG_LIBRARY_DOOR, List.of(), List.of(
                new DialogueLine("旁白", "你悄悄地穿过图书馆，来到另一扇沉重的木门前。"),
                new DialogueLine("旁白", "门上没有文字，只有一个冰冷的把手。")
        ), List.of(
                new GameChoice("LIB_LEAVE_OPEN", "打开门", "GARDEN_PATH", null, Map.of(RESOLVE, 1)),
                new GameChoice("LIB_LEAVE_RETURN", "返回白色房间", "START", null, Map.of(RESOLVE, -1))
        ));
        nodeStore.put("LIB_LEAVE_QUIETLY", libLeaveQuietly);
        nodeStore.put("LIB_IGNORE", libLeaveQuietly); // LIB_IGNORE 也指向这里

        // --- 分支 2.X: 花园路径 ---
        GameNode gardenPath = new GameNode("GARDEN_PATH", BG_GARDEN_PATH, List.of(), List.of(
                new DialogueLine("旁白", "门后是一个宁静的花园，空气清新。"),
                new DialogueLine("旁白", "路径分岔，一条通往阳光明媚的亭子，一条隐入阴暗的树林。")
        ), List.of(
                new GameChoice("GP_PAVILION", "走向亭子", "GP_PAVILION_NODE", null, Map.of(EMPATHY, 1)),
                new GameChoice("GP_FOREST", "进入树林", "GP_FOREST_NODE", null, Map.of(RESOLVE, 1))
        ));
        nodeStore.put("GARDEN_PATH", gardenPath);

        // --- 分支 2.X.X: 花园汇合点 ---
        GameNode gardenEnd = new GameNode("GARDEN_END", BG_LIGHT_GATE, List.of(), List.of(
                new DialogueLine("旁白", "无论选择哪条路，你最终都来到了花园的尽头，一扇通往外界的光门。")
        ), List.of(
                new GameChoice("END_GE", "穿过光门 (结局：花园漫步者)", "END_GARDEN_WALKER", null, Map.of("empathy", 5))
        ));
        nodeStore.put("GP_PAVILION_NODE", gardenEnd);
        nodeStore.put("GP_FOREST_NODE", gardenEnd);
        nodeStore.put("GARDEN_END", gardenEnd);


        // --- 分支 3：挑战之门 ---
        GameNode doorChallenge = new GameNode("DOOR_CHALLENGE", BG_ROPE_BRIDGE, List.of(), List.of(
                new DialogueLine("旁白", "你打开门，里面是一座摇摇欲坠的吊桥，桥下是翻滚的浓厚云雾，隐约传来低语声。"),
                new DialogueLine("旁白", "你感到一阵眩晕和不安。")
        ), List.of(
                new GameChoice("BRIDGE_C1", "（需要决心 > 6）深呼吸，稳步走过去", "BRIDGE_SUCCESS", Map.of(RESOLVE, 7), Map.of(RESOLVE, 3)),
                new GameChoice("BRIDGE_C2", "（需要洞察 > 6）寻找桥的薄弱环节", "BRIDGE_INSIGHT", Map.of(INSIGHT, 7), Map.of(INSIGHT, 3)),
                new GameChoice("BRIDGE_C3", "（需要同理心 > 6）回应云雾中的低语", "BRIDGE_EMPATHY", Map.of(EMPATHY, 7), Map.of(EMPATHY, 3)),
                new GameChoice("BRIDGE_C4", "太可怕了，退回白色房间", "START", null, Map.of(RESOLVE, -2))
        ));
        nodeStore.put("DOOR_CHALLENGE", doorChallenge);

        // --- 分支 3.1：挑战 -> 成功过桥 ---
        GameNode bridgeSuccess = new GameNode("BRIDGE_SUCCESS", BG_BRIDGE_OTHER_SIDE, List.of(), List.of(
                new DialogueLine("旁白", "你凭借坚定的决心，无视了眩晕和恐惧，一步一步稳稳地走到了对岸。你感到内心更加坚韧。")
        ), List.of(
                new GameChoice("BS_CAVE", "进入前方的黑暗洞穴", "CAVE_ENTRANCE", null, Map.of(RESOLVE, 1)),
                new GameChoice("BS_LOOK_BACK", "回头望向吊桥", "BRIDGE_LOOK_BACK", null, Map.of(INSIGHT, 1))
        ));
        nodeStore.put("BRIDGE_SUCCESS", bridgeSuccess);

        // --- 分支 3.1.1: 回望吊桥 ---
        GameNode bridgeLookBack = new GameNode("BRIDGE_LOOK_BACK", BG_BRIDGE_OTHER_SIDE, List.of(), List.of(
                new DialogueLine("旁白", "你回头望去，吊桥在你身后消失在云雾中，仿佛从未存在过。你知道已经没有退路。")
        ), List.of(
                new GameChoice("BLB_CAVE", "下定决心，进入洞穴", "CAVE_ENTRANCE", null, Map.of(RESOLVE, 2))
        ));
        nodeStore.put("BRIDGE_LOOK_BACK", bridgeLookBack);

        // --- 分支 3.2：挑战 -> 洞察过桥 ---
        GameNode bridgeInsight = new GameNode("BRIDGE_INSIGHT", BG_BRIDGE_OTHER_SIDE, List.of(), List.of(
                new DialogueLine("旁白", "你仔细观察，发现桥的右侧绳索虽然看起来破旧，但连接处的木头更坚固。"),
                new DialogueLine("旁白", "你贴着右侧小心翼翼地通过了，避免了可能断裂的左侧。")
        ), List.of(
                new GameChoice("BI_CAVE", "进入前方的黑暗洞穴", "CAVE_ENTRANCE", null, Map.of(INSIGHT, 1)),
                new GameChoice("BI_EXAMINE", "（需要洞察 > 8）检查桥对岸的地面", "BRIDGE_EXAMINE_SUCCESS", Map.of(INSIGHT, 9), Map.of(INSIGHT, 2))
        ));
        nodeStore.put("BRIDGE_INSIGHT", bridgeInsight);

        // --- 分支 3.2.1: 检查地面 ---
        GameNode bridgeExamineSuccess = new GameNode("BRIDGE_EXAMINE_SUCCESS", BG_CAVE_ENTRANCE, List.of(), List.of(
                new DialogueLine("旁白", "你发现洞穴入口旁的地面有松动的痕迹，下面似乎隐藏着什么。")
        ), List.of(
                new GameChoice("BES_DIG", "（需要决心 > 5）挖掘地面", "CAVE_HIDDEN_ITEM", Map.of(RESOLVE, 6), Map.of(RESOLVE, 1)),
                new GameChoice("BES_IGNORE", "不理会，直接进入洞穴", "CAVE_ENTRANCE", null, null)
        ));
        nodeStore.put("BRIDGE_EXAMINE_SUCCESS", bridgeExamineSuccess);

        // --- 分支 3.3：挑战 -> 同理心过桥 ---
        GameNode bridgeEmpathy = new GameNode("BRIDGE_EMPATHY", BG_ROPE_BRIDGE, List.of(), List.of(
                new DialogueLine("你", "（你对着云雾轻声回应，表达你的理解和善意...）"),
                new DialogueLine("旁白", "低语声渐渐平息，一座由光芒组成的坚固石桥在云雾中升起，取代了摇晃的吊桥。")
        ), List.of(
                new GameChoice("BE_CROSS", "走上光桥", "BRIDGE_EMPATHY_CROSSED", null, Map.of(EMPATHY, 2)),
                new GameChoice("BE_HESITATE", "（需要洞察 > 5）对这奇异景象保持警惕", "BRIDGE_EMPATHY_HESITATE", Map.of(INSIGHT, 6), Map.of(INSIGHT, 1))
        ));
        nodeStore.put("BRIDGE_EMPATHY", bridgeEmpathy);

        // --- 分支 3.3.1: 走上光桥 ---
        GameNode bridgeEmpathyCrossed = new GameNode("BRIDGE_EMPATHY_CROSSED", BG_LIGHT_BRIDGE, List.of(), List.of(
                new DialogueLine("旁白", "光桥温暖而稳定。走到对岸时，你感觉心中的不安消散了许多。"),
                new DialogueLine("旁白", "前方是一个发光的洞穴入口。")
        ), List.of(
                new GameChoice("BEC_CAVE", "进入发光的洞穴", "CAVE_ENTRANCE_LIGHT", null, Map.of(EMPATHY, 1))
        ));
        nodeStore.put("BRIDGE_EMPATHY_CROSSED", bridgeEmpathyCrossed);

        // --- 分支 3.3.2: 警惕光桥 ---
        GameNode bridgeEmpathyHesitate = new GameNode("BRIDGE_EMPATHY_HESITATE", BG_LIGHT_BRIDGE, List.of(), List.of(
                new DialogueLine("旁白", "你觉得这过于顺利。你仔细观察光桥，发现光芒似乎在脉动，像是在呼吸。"),
                new DialogueLine("旁白", "你决定不走光桥，而是寻找其他方式。")
        ), List.of(
                new GameChoice("BEH_FORCE_CROSS", "（需要决心 > 7）虽然警惕，但还是走上光桥", "BRIDGE_EMPATHY_CROSSED", Map.of(RESOLVE, 8), Map.of(RESOLVE, 2)),
                new GameChoice("BEH_WAIT", "在桥边等待，观察变化", "BRIDGE_WAIT_COLLAPSE", null, null)
        ));
        nodeStore.put("BRIDGE_EMPATHY_HESITATE", bridgeEmpathyHesitate);

        // --- 分支 3.3.2.1: 等待导致桥消失 ---
        GameNode bridgeWaitCollapse = new GameNode("BRIDGE_WAIT_COLLAPSE", BG_ROPE_BRIDGE, List.of(), List.of(
                new DialogueLine("旁白", "你等待了片刻，光桥的光芒逐渐暗淡，最终完全消失。只剩下摇摇欲坠的吊桥。")
        ), List.of(
                new GameChoice("BWC_CROSS_NOW", "（需要决心 > 8）现在必须过吊桥了", "BRIDGE_SUCCESS", Map.of(RESOLVE, 9), Map.of(RESOLVE, 3)),
                new GameChoice("BWC_RETURN", "放弃，退回白色房间", "START", null, Map.of(RESOLVE, -3, INSIGHT, -1))
        ));
        nodeStore.put("BRIDGE_WAIT_COLLAPSE", bridgeWaitCollapse);

        // --- 洞穴入口 (共同节点) ---
        GameNode caveEntrance = new GameNode("CAVE_ENTRANCE", BG_CAVE_DARK, List.of(), List.of(
                new DialogueLine("旁白", "洞穴入口一片漆黑，散发着阴冷的气息。你隐约听到滴水声。")
        ), List.of(
                new GameChoice("CAVE_ENTER", "摸黑前进", "CAVE_DARKNESS", null, Map.of(RESOLVE, 1)),
                new GameChoice("CAVE_LISTEN", "（需要洞察 > 7）仔细听声音来源", "CAVE_LISTEN_DARK", Map.of(INSIGHT, 8), Map.of(INSIGHT, 2)),
                new GameChoice("CAVE_FEEL", "（需要同理心 > 5）感受洞穴的气息", "CAVE_FEEL_DARK", Map.of(EMPATHY, 6), Map.of(EMPATHY, 1))
        ));
        nodeStore.put("CAVE_ENTRANCE", caveEntrance);

        // --- 挖掘处找到物品 ---
        GameNode caveHiddenItem = new GameNode("CAVE_HIDDEN_ITEM", BG_CAVE_ENTRANCE, List.of(), List.of(
                new DialogueLine("旁白", "你挖开地面，找到一个冰凉的护身符，握在手中感到一丝安心。")
        ), List.of(
                new GameChoice("CHI_ENTER", "带着护身符进入洞穴", "CAVE_ENTRANCE", null, Map.of(RESOLVE, 1, EMPATHY, 1))
        ));
        nodeStore.put("CAVE_HIDDEN_ITEM", caveHiddenItem);

        // --- 发光的洞穴入口 (同理心路线) ---
        GameNode caveEntranceLight = new GameNode("CAVE_ENTRANCE_LIGHT", BG_CAVE_LIGHT, List.of(), List.of(
                new DialogueLine("旁白", "洞穴入口散发着柔和的光芒，温暖而平静。你能看清前方的道路。")
        ), List.of(
                new GameChoice("CAVE_L_ENTER", "沿着光芒前进", "CAVE_LIGHT_PATH", null, Map.of(EMPATHY, 1)),
                new GameChoice("CAVE_L_OBSERVE", "（需要洞察 > 6）观察光芒来源", "CAVE_OBSERVE_LIGHT", Map.of(INSIGHT, 7), Map.of(INSIGHT, 1))
        ));
        nodeStore.put("CAVE_ENTRANCE_LIGHT", caveEntranceLight);

        // --- 洞穴后续汇合点 ---
        GameNode caveEnd = new GameNode("CAVE_END", BG_OPEN_FIELD, List.of(), List.of(
                new DialogueLine("旁白", "经历了洞穴中的种种挑战与启示，你终于走到了尽头，前方是一片开阔地。")
        ), List.of(
                new GameChoice("END_CE", "走向开阔地 (结局：洞穴跋涉者)", "END_CAVE_TREKKER", null, Map.of("resolve", 5))
        ));
        nodeStore.put("CAVE_DARKNESS", caveEnd);
        nodeStore.put("CAVE_LISTEN_DARK", caveEnd);
        nodeStore.put("CAVE_FEEL_DARK", caveEnd);
        nodeStore.put("CAVE_LIGHT_PATH", caveEnd);
        nodeStore.put("CAVE_OBSERVE_LIGHT", caveEnd);
        nodeStore.put("CAVE_END", caveEnd);


        // --- 结局节点 ---
        nodeStore.put("END_PEACEFUL_SCHOLAR", new GameNode("END_PEACEFUL_SCHOLAR", BG_LIBRARY, List.of(LIBRARIAN_SMILE), List.of(
                new DialogueLine("旁白", "你在知识的海洋中找到了永恒的宁静，但或许也失去了探索的勇气。"),
                new DialogueLine("旁白", "(结局：平静的学者)")
        ), List.of()));

        nodeStore.put("END_ECHO_WALKER", new GameNode("END_ECHO_WALKER", BG_STARLIGHT, List.of(), List.of(
                new DialogueLine("旁白", "你穿越了充满内心回响的密室，对自我有了更深的理解。"),
                new DialogueLine("旁白", "(结局：穿越回声者)")
        ), List.of()));

        // (修复) 修正了拼写错误
        nodeStore.put("END_GARDEN_WALKER", new GameNode("END_GARDEN_WALKER", BG_LIGHT_GATE, List.of(), List.of(
                new DialogueLine("旁白", "你在宁静或阴暗的花园中漫步，最终找到了出口，内心平和。"),
                new DialogueLine("旁白", "(结局：花园漫步者)")
        ), List.of()));

        nodeStore.put("END_CAVE_TREKKER", new GameNode("END_CAVE_TREKKER", BG_OPEN_FIELD, List.of(), List.of(
                new DialogueLine("旁白", "你在黑暗或光明的洞穴中探索，克服了挑战，变得更加成熟。"),
                new DialogueLine("旁白", "(结局：洞穴跋涉者)")
        ), List.of()));

        // (旧的结局节点，如果您的代码逻辑有引用，请确保它们也被转换或删除)
        nodeStore.put("END_FIRM_LONER", new GameNode("END_FIRM_LONER", BG_OPEN_FIELD, List.of(), List.of(
                new DialogueLine("旁白", "你依靠自己的决心和力量走完了全程，未曾寻求帮助，也未曾停留。"),
                new DialogueLine("旁白", "(结局：坚定的独行者)")
        ), List.of()));
    }
}