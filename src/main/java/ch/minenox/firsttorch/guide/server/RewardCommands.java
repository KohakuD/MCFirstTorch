package ch.minenox.firsttorch.guide.server;

import ch.minenox.firsttorch.FirstTorch;
import com.mojang.brigadier.arguments.StringArgumentType;
import java.util.Locale;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = FirstTorch.MOD_ID)
public final class RewardCommands {
    private RewardCommands() {}

    @SubscribeEvent
    public static void commands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("firsttorch")
                .then(Commands.literal("claim_all").executes(context -> {
                    var player = context.getSource().getPlayerOrException();
                    var result = BulkRewardClaims.claim(player);
                    context.getSource().sendSuccess(() -> Component.translatable("command.firsttorch.claim_all.result",
                            result.claimed(), result.skipped()), false);
                    if (result.unavailable()) context.getSource().sendFailure(Component.translatable("command.firsttorch.claim.unavailable"));
                    ServerTaskEvents.sync(player, true);
                    return result.claimed();
                }))
                .then(Commands.literal("claim").then(Commands.argument("quest", StringArgumentType.word())
                        .executes(context -> {
                            var player = context.getSource().getPlayerOrException();
                            var result = RewardClaims.claim(player, StringArgumentType.getString(context, "quest"));
                            var message = Component.translatable("command.firsttorch.claim." + result.name().toLowerCase(Locale.ROOT));
                            if (result == RewardClaims.Result.CLAIMED) context.getSource().sendSuccess(() -> message, false);
                            else context.getSource().sendFailure(message);
                            ServerTaskEvents.sync(player, true);
                            return result == RewardClaims.Result.CLAIMED ? 1 : 0;
                        }))));
    }
}
