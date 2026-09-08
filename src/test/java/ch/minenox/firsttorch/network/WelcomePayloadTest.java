package ch.minenox.firsttorch.network;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.network.connection.ConnectionType;
import org.junit.jupiter.api.Test;

final class WelcomePayloadTest {
    @Test void emptyPayloadsRoundTripWithoutBytes() {
        assertUnitRoundTrip(WelcomePayload.STREAM_CODEC, WelcomePayload.INSTANCE);
        assertUnitRoundTrip(WelcomeSeenPayload.STREAM_CODEC, WelcomeSeenPayload.INSTANCE);
    }

    private static <T> void assertUnitRoundTrip(net.minecraft.network.codec.StreamCodec<RegistryFriendlyByteBuf, T> codec,
            T expected) {
        ByteBuf raw = Unpooled.buffer();
        try {
            RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(raw, RegistryAccess.EMPTY, ConnectionType.NEOFORGE);
            codec.encode(buffer, expected);
            assertEquals(0, buffer.readableBytes());
            assertEquals(expected, codec.decode(buffer));
            assertEquals(0, buffer.readableBytes());
        } finally {
            raw.release();
        }
    }
}
