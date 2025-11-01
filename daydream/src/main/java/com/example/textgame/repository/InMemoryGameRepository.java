package com.example.textgame.repository;


import com.example.textgame.model.GameChoice;
import com.example.textgame.model.GameNode;
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
     * (已大幅扩展) 心理主题游戏脚本
     */
    @PostConstruct
    public void initGameData() {
        // --- 属性键 ---
        final String INSIGHT = "insight"; // 洞察力
        final String RESOLVE = "resolve"; // 决心
        final String EMPATHY = "empathy"; // 同理心

        // --- 节点 START ---
        GameNode startNode = new GameNode("START",
                "你在一间纯白色的房间醒来，房间里只有一张桌子和两扇门。\n桌子上有一张便条，写着：'欢迎来到心智试炼，选择你的道路。'",
                List.of(
                        new GameChoice("S_C1", "（需要洞察 > 4）仔细检查便条", "CHECK_NOTE", Map.of(INSIGHT, 5), Map.of(INSIGHT, 1)),
                        new GameChoice("S_C2", "走向写着'稳定'的门", "DOOR_STABLE", null, null),
                        new GameChoice("S_C3", "走向写着'挑战'的门", "DOOR_CHALLENGE", null, Map.of(RESOLVE, 1))
                ));
        nodeStore.put("START", startNode);

        // --- 分支 1：检查便条 ---
        GameNode checkNote = new GameNode("CHECK_NOTE",
                "你发现便条背面用铅笔写着小字：'属性决定命运，但选择塑造自我'。\n你对这个世界的规则有了初步了解。",
                List.of(
                        new GameChoice("CN_C1", "走向'稳定'之门", "DOOR_STABLE", null, null),
                        new GameChoice("CN_C2", "走向'挑战'之门", "DOOR_CHALLENGE", null, Map.of(RESOLVE, 1))
                ));
        nodeStore.put("CHECK_NOTE", checkNote);

        // --- 分支 2：稳定之门 ---
        GameNode doorStable = new GameNode("DOOR_STABLE",
                "你打开门，进入一个安静的图书馆。柔和的灯光洒在书架上。一个戴眼镜的人坐在角落看书，他抬头看了你一眼，似乎并不惊讶。",
                List.of(
                        new GameChoice("LIB_C1", "（需要同理心 > 5）主动上前打招呼", "LIB_TALK", Map.of(EMPATHY, 6), Map.of(EMPATHY, 2)),
                        new GameChoice("LIB_C2", "（需要洞察 > 5）在远处观察他", "LIB_OBSERVE", Map.of(INSIGHT, 6), Map.of(INSIGHT, 2)),
                        new GameChoice("LIB_C3", "（需要决心 > 5）无视他，直接穿过图书馆寻找出口", "LIB_IGNORE", Map.of(RESOLVE, 6), Map.of(RESOLVE, 1))
                ));
        nodeStore.put("DOOR_STABLE", doorStable);

        // --- 分支 2.1：稳定 -> 对话 ---
        GameNode libTalk = new GameNode("LIB_TALK",
                "你友好地打招呼。他微笑着回应：'你好，旅行者。知识是心灵的庇护所，但有时也是枷锁。这本书或许能给你一些启示。' 他递给你一本封面空白的书。",
                List.of(
                        new GameChoice("LIB_TALK_ACCEPT", "接受书本", "LIB_BOOK", null, Map.of(INSIGHT, 2, EMPATHY, 1)),
                        new GameChoice("LIB_TALK_REJECT", "婉拒好意，继续寻找出路", "LIB_IGNORE", null, Map.of(RESOLVE, 1))
                ));
        nodeStore.put("LIB_TALK", libTalk);

        // --- 分支 2.1.1: 稳定 -> 对话 -> 接受书本 ---
        GameNode libBook = new GameNode("LIB_BOOK",
                "你翻开书，书页上自动浮现出文字：'内心的平静源于接纳，而非逃避。'\n你感觉内心安宁了许多。",
                List.of(
                        new GameChoice("END_LB", "留在图书馆阅读 (结局：平静的学者)", "END_PEACEFUL_SCHOLAR", null, Map.of(INSIGHT, 5, EMPATHY, 5))
                ));
        nodeStore.put("LIB_BOOK", libBook);

        // --- 分支 2.2：稳定 -> 观察 ---
        GameNode libObserve = new GameNode("LIB_OBSERVE",
                "你发现他看的书是关于'认知陷阱'的，书页旁边放着一张地图，似乎描绘了这个试炼空间。他似乎察觉到了你的目光。",
                List.of(
                        new GameChoice("LIB_OBS_ASK", "（需要决心 > 4）上前询问地图的事", "LIB_MAP", Map.of(RESOLVE, 5), Map.of(RESOLVE, 1)),
                        new GameChoice("LIB_OBS_LEAVE", "保持距离，悄悄离开", "LIB_LEAVE_QUIETLY", null, Map.of(INSIGHT, 1))
                ));
        nodeStore.put("LIB_OBSERVE", libObserve);

        // --- 分支 2.2.1: 稳定 -> 观察 -> 询问地图 ---
        GameNode libMap = new GameNode("LIB_MAP",
                "他合上书：'这地图？它描绘了可能性，而非定局。真正的地图在你心中。但如果你坚持，我可以告诉你一条捷径...' 他指向图书馆深处一个隐藏的门。",
                List.of(
                        new GameChoice("LIB_MAP_TRUST", "相信他，走隐藏的门", "SECRET_PASSAGE", null, Map.of(EMPATHY, -1, RESOLVE, 2)), // 捷径通常有代价
                        new GameChoice("LIB_MAP_SKEPTIC", "表示怀疑，选择自己离开", "LIB_LEAVE_QUIETLY", null, Map.of(INSIGHT, 2))
                ));
        nodeStore.put("LIB_MAP", libMap);

        // --- 分支 2.2.1.1: 稳定 -> 观察 -> 询问地图 -> 隐藏门 (新路线) ---
        GameNode secretPassage = new GameNode("SECRET_PASSAGE",
                "隐藏的门后是一条狭窄向下的楼梯，空气中弥漫着尘土和遗忘的气息。你听到了微弱的回声。",
                List.of(
                        new GameChoice("SP_DESCEND", "小心地走下去", "SP_ECHO_ROOM", null, Map.of(RESOLVE, 1)),
                        new GameChoice("SP_LISTEN", "（需要洞察 > 7）仔细倾听回声", "SP_LISTEN_SUCCESS", Map.of(INSIGHT, 8), Map.of(INSIGHT, 2)),
                        new GameChoice("SP_RETURN", "感觉不对劲，返回图书馆", "LIB_LEAVE_QUIETLY", null, Map.of(RESOLVE, -1))
                ));
        nodeStore.put("SECRET_PASSAGE", secretPassage);

        // --- 分支 2.2.1.1.1: 稳定 -> 观察 -> 询问地图 -> 隐藏门 -> 走下去 ---
        GameNode spEchoRoom = new GameNode("SP_ECHO_ROOM",
                "楼梯尽头是一个圆形石室，你的脚步声被无限放大。墙壁上刻满了模糊不清的面孔。",
                List.of(
                        new GameChoice("SP_ER_TOUCH", "（需要同理心 > 6）尝试触摸那些面孔", "SP_FACES_EMPATHY", Map.of(EMPATHY, 7), Map.of(EMPATHY, 2)),
                        new GameChoice("SP_ER_SHOUT", "（需要决心 > 6）大声呼喊打破回声", "SP_SHOUT_RESOLVE", Map.of(RESOLVE, 7), Map.of(RESOLVE, 2)),
                        new GameChoice("SP_ER_FIND_EXIT", "（需要洞察 > 6）寻找出口", "SP_EXIT_INSIGHT", Map.of(INSIGHT, 7), Map.of(INSIGHT, 2))
                ));
        nodeStore.put("SP_ECHO_ROOM", spEchoRoom);

        // --- 节点 SP_FACES_EMPATHY, SP_SHOUT_RESOLVE, SP_EXIT_INSIGHT, SP_LISTEN_SUCCESS 的后续需要添加... ---
        // (为了示例，我们先让它们都汇合到一个中间节点)
        GameNode spConverge = new GameNode("SP_CONVERGE",
                "无论是沟通、打破还是寻找，你最终找到了石室的出口，进入了一个充满星光的空间。",
                List.of(new GameChoice("END_SP", "踏入星光 (结局：穿越回声者)", "END_ECHO_WALKER", null, Map.of("insight",3, "resolve",3, "empathy",3)))
        );
        nodeStore.put("SP_FACES_EMPATHY", spConverge); // 临时指向
        nodeStore.put("SP_SHOUT_RESOLVE", spConverge); // 临时指向
        nodeStore.put("SP_EXIT_INSIGHT", spConverge); // 临时指向
        nodeStore.put("SP_LISTEN_SUCCESS", spConverge); // 临时指向 (倾听成功直接到这里)
        nodeStore.put("SP_CONVERGE", spConverge);

        // --- 分支 2.2.2 / 2.3: 稳定 -> 观察/无视 -> 离开 ---
        GameNode libLeaveQuietly = new GameNode("LIB_LEAVE_QUIETLY",
                "你悄悄地穿过图书馆，来到另一扇沉重的木门前。门上没有文字，只有一个冰冷的把手。",
                List.of(
                        new GameChoice("LIB_LEAVE_OPEN", "打开门", "GARDEN_PATH", null, Map.of(RESOLVE, 1)),
                        new GameChoice("LIB_LEAVE_RETURN", "返回白色房间", "START", null, Map.of(RESOLVE, -1))
                ));
        nodeStore.put("LIB_LEAVE_QUIETLY", libLeaveQuietly);
        // LIB_IGNORE 也指向这里
        nodeStore.put("LIB_IGNORE", libLeaveQuietly); // 更新 LIB_IGNORE 的目标


        // --- 分支 2.X: 花园路径 (新路线) ---
        GameNode gardenPath = new GameNode("GARDEN_PATH",
                "门后是一个宁静的花园，空气清新。路径分岔，一条通往阳光明媚的亭子，一条隐入阴暗的树林。",
                List.of(
                        new GameChoice("GP_PAVILION", "走向亭子", "GP_PAVILION_NODE", null, Map.of(EMPATHY, 1)),
                        new GameChoice("GP_FOREST", "进入树林", "GP_FOREST_NODE", null, Map.of(RESOLVE, 1))
                ));
        nodeStore.put("GARDEN_PATH", gardenPath);

        // --- 花园后续节点 GP_PAVILION_NODE, GP_FOREST_NODE 等需要添加... ---
        // (为了示例，让它们都汇合到一个结局)
        GameNode gardenEnd = new GameNode("GARDEN_END",
                "无论选择哪条路，你最终都来到了花园的尽头，一扇通往外界的光门。",
                List.of(new GameChoice("END_GE", "穿过光门 (结局：花园漫步者)", "END_GARDEN_WALKER", null, Map.of("empathy", 5)))
        );
        nodeStore.put("GP_PAVILION_NODE", gardenEnd); // 临时指向
        nodeStore.put("GP_FOREST_NODE", gardenEnd); // 临时指向
        nodeStore.put("GARDEN_END", gardenEnd);


        // --- 分支 3：挑战之门 ---
        GameNode doorChallenge = new GameNode("DOOR_CHALLENGE",
                "你打开门，里面是一座摇摇欲坠的吊桥，桥下是翻滚的浓厚云雾，隐约传来低语声。你感到一阵眩晕和不安。",
                List.of(
                        new GameChoice("BRIDGE_C1", "（需要决心 > 6）深呼吸，稳步走过去", "BRIDGE_SUCCESS", Map.of(RESOLVE, 7), Map.of(RESOLVE, 3)),
                        new GameChoice("BRIDGE_C2", "（需要洞察 > 6）寻找桥的薄弱环节，并避开", "BRIDGE_INSIGHT", Map.of(INSIGHT, 7), Map.of(INSIGHT, 3)),
                        new GameChoice("BRIDGE_C3", "（需要同理心 > 6）回应云雾中的低语", "BRIDGE_EMPATHY", Map.of(EMPATHY, 7), Map.of(EMPATHY, 3)),
                        new GameChoice("BRIDGE_C4", "太可怕了，退回白色房间", "START", null, Map.of(RESOLVE, -2))
                ));
        nodeStore.put("DOOR_CHALLENGE", doorChallenge);

        // --- 分支 3.1：挑战 -> 成功过桥 ---
        GameNode bridgeSuccess = new GameNode("BRIDGE_SUCCESS",
                "你凭借坚定的决心，无视了眩晕和恐惧，一步一步稳稳地走到了对岸。你感到内心更加坚韧。",
                List.of(
                        new GameChoice("BS_CAVE", "进入前方的黑暗洞穴", "CAVE_ENTRANCE", null, Map.of(RESOLVE, 1)),
                        new GameChoice("BS_LOOK_BACK", "回头望向吊桥", "BRIDGE_LOOK_BACK", null, Map.of(INSIGHT, 1))
                )
        );
        nodeStore.put("BRIDGE_SUCCESS", bridgeSuccess);

        // --- 分支 3.1.1: 回望吊桥 ---
        GameNode bridgeLookBack = new GameNode("BRIDGE_LOOK_BACK",
                "你回头望去，吊桥在你身后消失在云雾中，仿佛从未存在过。你知道已经没有退路。",
                List.of(new GameChoice("BLB_CAVE", "下定决心，进入洞穴", "CAVE_ENTRANCE", null, Map.of(RESOLVE, 2)))
        );
        nodeStore.put("BRIDGE_LOOK_BACK", bridgeLookBack);


        // --- 分支 3.2：挑战 -> 洞察过桥 ---
        GameNode bridgeInsight = new GameNode("BRIDGE_INSIGHT",
                "你仔细观察，发现桥的右侧绳索虽然看起来破旧，但连接处的木头更坚固。你贴着右侧小心翼翼地通过了，避免了可能断裂的左侧。",
                List.of(
                        new GameChoice("BI_CAVE", "进入前方的黑暗洞穴", "CAVE_ENTRANCE", null, Map.of(INSIGHT, 1)),
                        new GameChoice("BI_EXAMINE", "（需要洞察 > 8）检查桥对岸的地面", "BRIDGE_EXAMINE_SUCCESS", Map.of(INSIGHT, 9), Map.of(INSIGHT, 2))
                ));
        nodeStore.put("BRIDGE_INSIGHT", bridgeInsight);

        // --- 分支 3.2.1: 检查地面 ---
        GameNode bridgeExamineSuccess = new GameNode("BRIDGE_EXAMINE_SUCCESS",
                "你发现洞穴入口旁的地面有松动的痕迹，下面似乎隐藏着什么。",
                List.of(
                        new GameChoice("BES_DIG", "（需要决心 > 5）挖掘地面", "CAVE_HIDDEN_ITEM", Map.of(RESOLVE, 6), Map.of(RESOLVE, 1)),
                        new GameChoice("BES_IGNORE", "不理会，直接进入洞穴", "CAVE_ENTRANCE", null, null)
                ));
        nodeStore.put("BRIDGE_EXAMINE_SUCCESS", bridgeExamineSuccess);

        // --- 分支 3.3：挑战 -> 同理心过桥 ---
        GameNode bridgeEmpathy = new GameNode("BRIDGE_EMPATHY",
                "你对着云雾中的低语轻声回应，表达你的理解和善意。低语声渐渐平息，一座由光芒组成的坚固石桥在云雾中升起，取代了摇晃的吊桥。",
                List.of(
                        new GameChoice("BE_CROSS", "走上光桥", "BRIDGE_EMPATHY_CROSSED", null, Map.of(EMPATHY, 2)),
                        new GameChoice("BE_HESITATE", "（需要洞察 > 5）对这奇异景象保持警惕", "BRIDGE_EMPATHY_HESITATE", Map.of(INSIGHT, 6), Map.of(INSIGHT, 1))
                ));
        nodeStore.put("BRIDGE_EMPATHY", bridgeEmpathy);

        // --- 分支 3.3.1: 走上光桥 ---
        GameNode bridgeEmpathyCrossed = new GameNode("BRIDGE_EMPATHY_CROSSED",
                "光桥温暖而稳定。走到对岸时，你感觉心中的不安消散了许多。前方是一个发光的洞穴入口。",
                List.of(new GameChoice("BEC_CAVE", "进入发光的洞穴", "CAVE_ENTRANCE_LIGHT", null, Map.of(EMPATHY, 1))) // 通往不同的洞穴入口
        );
        nodeStore.put("BRIDGE_EMPATHY_CROSSED", bridgeEmpathyCrossed);

        // --- 分支 3.3.2: 警惕光桥 ---
        GameNode bridgeEmpathyHesitate = new GameNode("BRIDGE_EMPATHY_HESITATE",
                "你觉得这过于顺利。你仔细观察光桥，发现光芒似乎在脉动，像是在呼吸。你决定不走光桥，而是寻找其他方式。",
                List.of(
                        // 这里可以添加更多选项，比如尝试回到白色房间，或者寻找桥下的路
                        // 为了简化，我们暂时让玩家必须前进
                        new GameChoice("BEH_FORCE_CROSS", "（需要决心 > 7）虽然警惕，但还是走上光桥", "BRIDGE_EMPATHY_CROSSED", Map.of(RESOLVE, 8), Map.of(RESOLVE, 2)),
                        new GameChoice("BEH_WAIT", "在桥边等待，观察变化", "BRIDGE_WAIT_COLLAPSE", null, null) // 等待可能导致桥消失
                ));
        nodeStore.put("BRIDGE_EMPATHY_HESITATE", bridgeEmpathyHesitate);

        // --- 分支 3.3.2.1: 等待导致桥消失 ---
        GameNode bridgeWaitCollapse = new GameNode("BRIDGE_WAIT_COLLAPSE",
                "你等待了片刻，光桥的光芒逐渐暗淡，最终完全消失。只剩下摇摇欲坠的吊桥。",
                List.of(
                        new GameChoice("BWC_CROSS_NOW", "（需要决心 > 8）现在必须过吊桥了", "BRIDGE_SUCCESS", Map.of(RESOLVE, 9), Map.of(RESOLVE, 3)), // 更高的决心要求
                        new GameChoice("BWC_RETURN", "放弃，退回白色房间", "START", null, Map.of(RESOLVE, -3, INSIGHT, -1)) // 惩罚更大
                ));
        nodeStore.put("BRIDGE_WAIT_COLLAPSE", bridgeWaitCollapse);


        // --- 洞穴入口 (共同节点) ---
        GameNode caveEntrance = new GameNode("CAVE_ENTRANCE",
                "洞穴入口一片漆黑，散发着阴冷的气息。你隐约听到滴水声。",
                List.of(
                        new GameChoice("CAVE_ENTER", "摸黑前进", "CAVE_DARKNESS", null, Map.of(RESOLVE, 1)),
                        new GameChoice("CAVE_LISTEN", "（需要洞察 > 7）仔细听声音来源", "CAVE_LISTEN_DARK", Map.of(INSIGHT, 8), Map.of(INSIGHT, 2)),
                        new GameChoice("CAVE_FEEL", "（需要同理心 > 5）感受洞穴的气息", "CAVE_FEEL_DARK", Map.of(EMPATHY, 6), Map.of(EMPATHY, 1))
                ));
        nodeStore.put("CAVE_ENTRANCE", caveEntrance);
        // 从挖掘处找到物品后也来到这里
        GameNode caveHiddenItem = new GameNode("CAVE_HIDDEN_ITEM",
                "你挖开地面，找到一个冰凉的护身符，握在手中感到一丝安心。",
                // 可以考虑给护身符加一个特殊效果，或者只是增加属性
                List.of(new GameChoice("CHI_ENTER", "带着护身符进入洞穴", "CAVE_ENTRANCE", null, Map.of(RESOLVE, 1, EMPATHY, 1))) // 假设护身符增加决心和同理心
        );
        nodeStore.put("CAVE_HIDDEN_ITEM", caveHiddenItem);


        // --- 发光的洞穴入口 (同理心路线) ---
        GameNode caveEntranceLight = new GameNode("CAVE_ENTRANCE_LIGHT",
                "洞穴入口散发着柔和的光芒，温暖而平静。你能看清前方的道路。",
                List.of(
                        new GameChoice("CAVE_L_ENTER", "沿着光芒前进", "CAVE_LIGHT_PATH", null, Map.of(EMPATHY, 1)),
                        new GameChoice("CAVE_L_OBSERVE", "（需要洞察 > 6）观察光芒来源", "CAVE_OBSERVE_LIGHT", Map.of(INSIGHT, 7), Map.of(INSIGHT, 1))
                ));
        nodeStore.put("CAVE_ENTRANCE_LIGHT", caveEntranceLight);

        // --- 洞穴后续节点 CAVE_DARKNESS, CAVE_LISTEN_DARK, CAVE_FEEL_DARK, CAVE_LIGHT_PATH, CAVE_OBSERVE_LIGHT 等需要添加... ---
        // (为了示例，让所有挑战路线最终汇合到一个结局)
        GameNode caveEnd = new GameNode("CAVE_END",
                "经历了洞穴中的种种挑战与启示，你终于走到了尽头，前方是一片开阔地。",
                List.of(new GameChoice("END_CE", "走向开阔地 (结局：洞穴跋涉者)", "END_CAVE_TREKKER", null, Map.of("resolve", 5)))
        );
        nodeStore.put("CAVE_DARKNESS", caveEnd); // 临时指向
        nodeStore.put("CAVE_LISTEN_DARK", caveEnd); // 临时指向
        nodeStore.put("CAVE_FEEL_DARK", caveEnd); // 临时指向
        nodeStore.put("CAVE_LIGHT_PATH", caveEnd); // 临时指向
        nodeStore.put("CAVE_OBSERVE_LIGHT", caveEnd); // 临时指向
        nodeStore.put("CAVE_END", caveEnd);


        // --- 结局节点 ---
        nodeStore.put("END_PEACEFUL_SCHOLAR", new GameNode("END_PEACEFUL_SCHOLAR", "你在知识的海洋中找到了永恒的宁静，但或许也失去了探索的勇气。 (结局：平静的学者)", List.of()));
        // nodeStore.put("END_SHARP_OBSERVER", new GameNode("END_SHARP_OBSERVER", "你理解了试炼的象征意义，选择抽身而退，保留了自己的判断。 (结局：敏锐的观察者)", List.of())); // 被合并或移除
        nodeStore.put("END_FIRM_LONER", new GameNode("END_FIRM_LONER", "你依靠自己的决心和力量走完了全程，未曾寻求帮助，也未曾停留。 (结局：坚定的独行者)", List.of()));
        // nodeStore.put("END_BRAVE_EXPLORER", new GameNode("END_BRAVE_EXPLORER", "你直面内心的恐惧，每一次挑战都让你更加强大。 (结局：勇敢的探索者)", List.of())); // 被合并或移除
        // nodeStore.put("END_SMART_EXPLORER", new GameNode("END_SMART_EXPLORER", "你用智慧化解了前路的障碍，证明了头脑的力量。 (结局：机智的探索者)", List.of())); // 被合并或移除
        // nodeStore.put("END_KIND_COMMUNICATOR", new GameNode("END_KIND_COMMUNICATOR", "你的同理心和善意改变了试炼本身，开辟了新的道路。 (结局：仁慈的沟通者)", List.of())); // 被合并或移除

        // --- 新增结局 ---
        nodeStore.put("END_ECHO_WALKER", new GameNode("END_ECHO_WALKER", "你穿越了充满内心回响的密室，对自我有了更深的理解。(结局：穿越回声者)", List.of()));
        nodeStore.put("END_GARDEN_WALKER", new GameNode("END_GARDEN_WALKER", "你在宁静或阴暗的花园中漫步，最终找到了出口，内心平和。(结局：花园漫步者)", List.of()));
        nodeStore.put("END_CAVE_TREKKER", new GameNode("END_CAVE_TREKKER", "你在黑暗或光明的洞穴中探索，克服了挑战，变得更加成熟。(结局：洞穴跋涉者)", List.of()));

    }
}

