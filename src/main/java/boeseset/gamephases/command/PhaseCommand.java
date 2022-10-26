package boeseset.gamephases.command;

import boeseset.gamephases.GamePhases;
import boeseset.gamephases.kube.GamePhasesEventJS;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;

public class PhaseCommand {

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            LiteralCommandNode<ServerCommandSource> root = CommandManager.literal("phase")
                    .requires(source -> source.hasPermissionLevel(2))
                    .build();

            LiteralCommandNode<ServerCommandSource> grant = CommandManager.literal("grant")
                    .then(CommandManager.argument("player", EntityArgumentType.player())
                    .then(CommandManager.argument("phase", StringArgumentType.word())
                            .suggests((context, builder) -> {
                                GamePhasesEventJS.getPhases().keySet().forEach(builder::suggest);
                                return builder.buildFuture();
                            })
                            .executes(context -> {
                                PlayerEntity target = EntityArgumentType.getPlayer(context, "player");
                                String phase = StringArgumentType.getString(context, "phase");
                                GamePhases.getPhaseData(target).set(phase, true);
                                context.getSource().sendFeedback(new LiteralText(String.format("Granted phase \"§6%s§d\" to §a%s§d.", phase, target.getName().asString())).formatted(Formatting.LIGHT_PURPLE), false);
                                return 1;
                            })))
                    .build();

            LiteralCommandNode<ServerCommandSource> revoke = CommandManager.literal("revoke")
                    .then(CommandManager.argument("player", EntityArgumentType.player())
                    .then(CommandManager.argument("phase", StringArgumentType.word())
                            .suggests((context, builder) -> {
                                GamePhasesEventJS.getPhases().keySet().forEach(builder::suggest);
                                return builder.buildFuture();
                            })
                            .executes(context -> {
                                PlayerEntity target = EntityArgumentType.getPlayer(context, "player");
                                String phase = StringArgumentType.getString(context, "phase");
                                GamePhases.getPhaseData(target).set(phase, false);
                                context.getSource().sendFeedback(new LiteralText(String.format("Revoked phase \"§6%s§d\" from §a%s§d.", phase, target.getName().asString())).formatted(Formatting.LIGHT_PURPLE), false);
                                return 1;
                            })))
                    .build();

            LiteralCommandNode<ServerCommandSource> list = CommandManager.literal("list")
                    .then(CommandManager.argument("player", EntityArgumentType.player())
                            .executes(context -> {
                                    PlayerEntity target = EntityArgumentType.getPlayer(context, "player");
                                    Map<String, Boolean> phasesList = GamePhases.getPhaseData(target).getPhases();
                                    while (phasesList.values().remove(false)) ;
                                    context.getSource().sendFeedback(new LiteralText(String.format("§a%s's §dPhases: \"§6%s§d\".", target.getName().asString().formatted(Formatting.LIGHT_PURPLE), phasesList.keySet().toString().formatted(Formatting.GOLD))), false);
                                    return 1;
                            }))
                    .build();

            root.addChild(revoke);
            root.addChild(grant);
            root.addChild(list);
            dispatcher.getRoot().addChild(root);
        });
    }

    private PhaseCommand() {
        // NO-OP
    }
}
