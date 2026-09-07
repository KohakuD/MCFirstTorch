package ch.minenox.firsttorch.guide.server;

import ch.minenox.firsttorch.FirstTorch;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.data.GuideResourceLoader;
import com.mojang.logging.LogUtils;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

public final class GuideReloadListener extends SimplePreparableReloadListener<GuideSnapshot> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final FileToIdConverter GUIDE_FILES = FileToIdConverter.json("guides");

    @Override
    protected GuideSnapshot prepare(ResourceManager manager, ProfilerFiller profiler) {
        Map<String, GuideResourceLoader.ReaderSource> sources = new LinkedHashMap<>();
        for (Map.Entry<Identifier, Resource> entry
                : GUIDE_FILES.listMatchingResourcesFromNamespace(manager, FirstTorch.MOD_ID).entrySet()) {
            sources.put(entry.getKey().toString(), entry.getValue()::openAsReader);
        }
        return GuideResourceLoader.load(sources);
    }

    @Override
    protected void apply(GuideSnapshot prepared, ResourceManager manager, ProfilerFiller profiler) {
        ServerGuideRepository.INSTANCE.replace(prepared);
        LOGGER.info("Loaded {} First Torch guide definition(s).", prepared.guides().size());
    }
}
