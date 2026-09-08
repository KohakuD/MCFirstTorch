package ch.minenox.firsttorch.client;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.data.GuideJson;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.network.ProgressPayload;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

final class ExperienceRewardSoundTest {
    private static final String XP = "12E6801F4CAD3579";
    private static final String ITEMS = "34A8023B6ECF5791";

    private GuideSnapshot guides() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        }
    }

    private ProgressPayload claimed(String... ids) {
        return new ProgressPayload(ProgressState.EMPTY, Map.of(), true, Set.of(ids), Set.of());
    }

    @Test void playsOnceForConfirmedXpButNotItemOnlyClaims() throws Exception {
        var guides = guides();
        assertTrue(ExperienceRewardSound.shouldPlay(guides, claimed(), claimed(XP)));
        assertFalse(ExperienceRewardSound.shouldPlay(guides, claimed(XP), claimed(XP)));
        assertFalse(ExperienceRewardSound.shouldPlay(guides, claimed(), claimed(ITEMS)));
    }

    @Test void loginReloadFailureAndPendingReservationsAreSilent() throws Exception {
        var guides = guides();
        var pending = new ProgressPayload(ProgressState.EMPTY, Map.of(), true, Set.of(), Set.of(XP));
        assertFalse(ExperienceRewardSound.shouldPlay(guides, null, claimed(XP)));
        assertFalse(ExperienceRewardSound.shouldPlay(guides, ProgressPayload.UNAVAILABLE, claimed(XP)));
        assertFalse(ExperienceRewardSound.shouldPlay(guides, claimed(), ProgressPayload.UNAVAILABLE));
        assertFalse(ExperienceRewardSound.shouldPlay(guides, claimed(), pending));
        assertTrue(ExperienceRewardSound.shouldPlay(guides, pending, claimed(XP)));
        assertFalse(ExperienceRewardSound.shouldPlay(GuideSnapshot.EMPTY, claimed(), claimed(XP)));
    }
}
