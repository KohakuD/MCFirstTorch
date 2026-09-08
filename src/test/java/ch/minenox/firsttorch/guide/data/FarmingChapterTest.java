package ch.minenox.firsttorch.guide.data;
import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import ch.minenox.firsttorch.guide.progress.*;
import java.util.*;
import org.junit.jupiter.api.Test;
final class FarmingChapterTest {
 private static final List<String> Q=List.of("0C42E8A51D739BF6","2E640AC73F95BD18","40862CE951B7DF3A","62A84E0B73D9F15C","04CA602D95FB137E","26EC824FB71D3590");
 @Test void preservesIdsCountsAndChain() throws Exception { var c=s().guides().getFirst().chapters().get(12); assertEquals("5D91A7C30E624BF8",c.id()); assertEquals(Q,c.quests().stream().map(q->q.id()).toList()); assertEquals(List.of("6C03B5E98A417DF2"),c.quests().getFirst().prerequisiteQuestIds()); for(int i=1;i<6;i++)assertEquals(List.of(Q.get(i-1)),c.quests().get(i).prerequisiteQuestIds()); assertEquals(List.of(3,1,1,1,3,1),c.quests().stream().flatMap(q->q.tasks().stream()).map(t->t.count()).toList()); assertEquals(TaskDefinition.Type.MANUAL,c.quests().get(2).tasks().getFirst().type()); assertEquals(TaskDefinition.Type.ADVANCEMENT,c.quests().get(3).tasks().getFirst().type()); assertEquals(3,c.quests().get(3).rewards().getFirst().amount()); assertEquals(5,c.quests().getLast().rewards().getFirst().amount()); }
 @Test void plantingIsAutomaticStickyButFarmlandIsManualAndGated() throws Exception { var x=s(); var p=new ProgressState(Set.of(),Set.of("6C03B5E98A417DF2",Q.get(0),Q.get(1),Q.get(2))); var a=TaskEvaluator.evaluate(x,p,k->k.equals("@minecraft:husbandry/plant_seed|wheat")?1:0); assertTrue(a.completedTaskIds().contains("73B95F1C84EA026D")); assertTrue(TaskEvaluator.evaluate(x,a,k->0).completedTaskIds().contains("73B95F1C84EA026D")); assertThrows(IllegalArgumentException.class,()->TaskEvaluator.confirm(x,ProgressState.EMPTY,Q.get(2),"51973DFA62C8E04B",k->0)); }
 private GuideSnapshot s() throws Exception {try(var i=getClass().getResourceAsStream("/data/firsttorch/guides/course.json")){return new GuideSnapshot(List.of(GuideJson.read(i)));}}
}
