package ch.minenox.firsttorch.guide.server;

import static org.junit.jupiter.api.Assertions.*;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.validation.GuideValidationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.junit.jupiter.api.Test;

final class GuideReloadListenerTest {
    private static final String VALID = """
            {"schemaVersion":1,"id":"0000000000000001","titleKey":"test.title",
             "descriptionKey":"test.description","chapters":[{
             "id":"1000000000000001","order":0,"titleKey":"test.chapter",
             "descriptionKey":"test.description","quests":[{
             "id":"2000000000000001","order":0,"titleKey":"test.quest",
             "descriptionKey":"test.description","position":{"x":0,"y":0},
             "prerequisiteQuestIds":[]}]}]}
            """;

    @Test void invalidAndUnreadableResourcesLeaveLastPublishedSnapshotIntact() {
        var repository = ServerGuideRepository.INSTANCE;
        GuideSnapshot previous = repository.snapshot();
        var listener = new GuideReloadListener();
        try {
            var good = manager(Map.of(Identifier.parse("firsttorch:guides/good.json"), resource(VALID)));
            GuideSnapshot original = listener.prepare(good, null);
            listener.apply(original, good, null);
            for (Resource broken : new Resource[]{resource("{broken"), new Resource(null, () -> {
                throw new IOException("Synthetic read failure");
            })}) {
                var bad = manager(Map.of(
                        Identifier.parse("firsttorch:guides/good.json"), resource(VALID),
                        Identifier.parse("firsttorch:guides/bad.json"), broken));
                assertThrows(GuideValidationException.class, () -> listener.prepare(bad, null));
                assertSame(original, repository.snapshot());
            }
            GuideSnapshot recovered = listener.prepare(good, null);
            assertSame(original, repository.snapshot()); // Preparation alone must not publish.
            listener.apply(recovered, good, null);
            assertSame(recovered, repository.snapshot());
            assertEquals(original, recovered);
        } finally {
            repository.replace(previous);
        }
    }

    private static Resource resource(String json) {
        return new Resource(null, () -> new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));
    }

    private static ResourceManager manager(Map<Identifier, Resource> resources) {
        return (ResourceManager) Proxy.newProxyInstance(ResourceManager.class.getClassLoader(),
                new Class<?>[]{ResourceManager.class}, (proxy, method, args) -> {
                    if (method.getName().equals("listResources")) return resources;
                    throw new UnsupportedOperationException(method.getName());
                });
    }
}
