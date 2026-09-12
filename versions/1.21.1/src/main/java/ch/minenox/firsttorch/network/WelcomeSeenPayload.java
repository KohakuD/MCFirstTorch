package ch.minenox.firsttorch.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Serverbound, empty acknowledgement tied to the sending connection. */
public record WelcomeSeenPayload() implements CustomPacketPayload {
    public static final WelcomeSeenPayload INSTANCE = new WelcomeSeenPayload();
    public static final Type<WelcomeSeenPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("firsttorch", "welcome_seen"));
    public static final StreamCodec<RegistryFriendlyByteBuf, WelcomeSeenPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);
    @Override public Type<WelcomeSeenPayload> type() { return TYPE; }
}
