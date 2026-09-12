package ch.minenox.firsttorch.guide;

import ch.minenox.firsttorch.guide.model.GuideDefinition;
import ch.minenox.firsttorch.guide.validation.GuideSetValidator;
import java.util.List;

public record GuideSnapshot(List<GuideDefinition> guides) {
    public static final GuideSnapshot EMPTY = new GuideSnapshot(List.of());

    public GuideSnapshot {
        guides = guides == null ? null : List.copyOf(guides);
        GuideSetValidator.validate(guides);
    }

}
