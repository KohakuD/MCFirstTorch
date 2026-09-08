package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.RewardDefinition;
import ch.minenox.firsttorch.network.ProgressPayload;

/** Successful claim transitions only; saved claims and reservations are silent. */
final class ExperienceRewardSound {
    static boolean shouldPlay(GuideSnapshot guides, ProgressPayload previous, ProgressPayload current) {
        if (previous == null || !previous.available() || !current.available()) return false;
        return guides.guides().stream().flatMap(guide -> guide.chapters().stream())
                .flatMap(chapter -> chapter.quests().stream())
                .anyMatch(quest -> current.claimedQuestIds().contains(quest.id())
                        && !previous.claimedQuestIds().contains(quest.id())
                        && quest.rewards().stream().anyMatch(reward -> reward.type() == RewardDefinition.Type.EXPERIENCE));
    }

    private ExperienceRewardSound() {}
}
