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
      * 在服务启动时初始化游戏数据
      */
     @PostConstruct
     public void initGameData() {
         // --- 节点 START ---
         GameChoice choice1 = new GameChoice("S_C1", "勇敢地走进森林", "FOREST_1",
                 null, Map.of("courage", 1));
         GameChoice choice2 = new GameChoice("S_C2", "沿着河流寻找线索", "RIVER_1",
                 null, Map.of("wisdom", 1));
         GameNode startNode = new GameNode("START", "你站在一个神秘世界的入口，面前有两条路：一条通往黑暗的森林，一条沿着蜿蜒的河流。",
                 List.of(choice1, choice2));
         nodeStore.put("START", startNode);
 
 
         // --- 节点 FOREST_1 ---
         GameChoice f1_c1 = new GameChoice("F1_C1", "（需要智慧>5）解读古老的符文", "FOREST_SECRET",
                 Map.of("wisdom", 6), Map.of("wisdom", 2));
         GameChoice f1_c2 = new GameChoice("F1_C2", "（需要勇气>5）强行推开石门", "FOREST_DANGER",
                 Map.of("courage", 6), Map.of("courage", 2));
         GameChoice f1_c3 = new GameChoice("F1_C3", "（善良>5）帮助受伤的小动物", "FOREST_FRIEND",
                 Map.of("kindness", 6), Map.of("kindness", 2));
         GameChoice f1_c4 = new GameChoice("F1_C4", "太黑了，转身离开", "START",
                 null, Map.of("courage", -1));
         
         GameNode forestNode1 = new GameNode("FOREST_1", "森林深处，你发现一个刻着符文的石门，旁边有一只受伤的小狐狸。石门看起来可以强行推开。",
                 List.of(f1_c1, f1_c2, f1_c3, f1_c4));
         nodeStore.put("FOREST_1", forestNode1);
 
 
         // --- 节点 RIVER_1 ---
         GameChoice r1_c1 = new GameChoice("R1_C1", "（需要智慧>5）修复小船", "RIVER_SUCCESS",
                 Map.of("wisdom", 6), Map.of("wisdom", 2));
         GameChoice r1_c2 = new GameChoice("R1_C2", "（需要勇气>5）跳入河中游过去", "RIVER_DANGER",
                 Map.of("courage", 6), Map.of("courage", 2));
         GameNode riverNode1 = new GameNode("RIVER_1", "河边有一艘破旧的小船，河水湍急。",
                 List.of(r1_c1, r1_c2));
         nodeStore.put("RIVER_1", riverNode1);
 
 
         // --- 结局节点 ---
         GameNode fsNode = new GameNode("FOREST_SECRET", "你解读了符文，石门应声而开，里面是古代智者的图书馆。 (结局：智慧的胜利)", List.of());
         GameNode fdNode = new GameNode("FOREST_DANGER", "你推开了石门，但触发了陷阱。 (结局：有勇无谋)", List.of());
         GameNode ffNode = new GameNode("FOREST_FRIEND", "你救了小狐狸，它是森林的守护者，它带你找到了安全的出口。 (结局：善良的回报)", List.of());
         GameNode rsNode = new GameNode("RIVER_SUCCESS", "你修好了小船，顺利渡河。 (结局：智慧的胜利)", List.of());
         GameNode rdNode = new GameNode("RIVER_DANGER", "你被湍急的河水冲走了。 (结局：有勇无谋)", List.of());
 
         nodeStore.put("FOREST_SECRET", fsNode);
         nodeStore.put("FOREST_DANGER", fdNode);
         nodeStore.put("FOREST_FRIEND", ffNode);
         nodeStore.put("RIVER_SUCCESS", rsNode);
         nodeStore.put("RIVER_DANGER", rdNode);
     }
 }