package ch.minenox.firsttorch.network;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record GuideSnapshotPayload(GuideSnapshot snapshot) implements CustomPacketPayload {
    public static final String NETWORK_VERSION = "12";
    public static final Type<GuideSnapshotPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("firsttorch", "guide_snapshot"));
    public static final StreamCodec<RegistryFriendlyByteBuf, GuideSnapshotPayload> STREAM_CODEC = StreamCodec.of(
            (buffer, payload) -> GuideSnapshotWireCodec.encode(buffer, payload.snapshot()),
            buffer -> new GuideSnapshotPayload(GuideSnapshotWireCodec.decode(buffer)));

    public GuideSnapshotPayload {
        if (snapshot == null) {
            throw new IllegalArgumentException("Guide snapshot payload must not be null.");
        }
    }

    @Override
    public Type<GuideSnapshotPayload> type() {
        return TYPE;
    }
}
