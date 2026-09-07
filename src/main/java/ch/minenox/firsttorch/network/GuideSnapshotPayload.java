package ch.minenox.firsttorch.network;

import ch.minenox.firsttorch.FirstTorch;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record GuideSnapshotPayload(GuideSnapshot snapshot) implements CustomPacketPayload {
    public static final String NETWORK_VERSION = "1";
    public static final Type<GuideSnapshotPayload> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(FirstTorch.MOD_ID, "guide_snapshot"));
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
