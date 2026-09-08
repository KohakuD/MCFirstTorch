package ch.minenox.firsttorch.network;

import ch.minenox.firsttorch.FirstTorch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** Clientbound, empty offer; the client chooses when to acknowledge it. */
public record WelcomePayload() implements CustomPacketPayload {
    public static final WelcomePayload INSTANCE = new WelcomePayload();
    public static final Type<WelcomePayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(FirstTorch.MOD_ID, "welcome"));
    public static final StreamCodec<RegistryFriendlyByteBuf, WelcomePayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);
    @Override public Type<WelcomePayload> type() { return TYPE; }
}
