package ch.minenox.firsttorch.guide.model;

/** A bounded, client-rendered illustration attached to a quest. */
public record GuideImage(String resource, int width, int height, String altKey) {
}
