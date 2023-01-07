package ch.skyfy.ghuperms.mixin;

import ch.skyfy.ghuperms.api.Permission;
import com.mojang.brigadier.Command;
import net.minecraft.server.command.AdvancementCommand;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AdvancementCommand.class)
public class AdvancementCommandMixin {

    @ModifyArg(at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/brigadier/builder/RequiredArgumentBuilder;executes(Lcom/mojang/brigadier/Command;)Lcom/mojang/brigadier/builder/ArgumentBuilder;",
            ordinal = 0),
            method = "register"
    )
    private static Command<ServerCommandSource> modifyArg(Command<ServerCommandSource> command) {
        return new Permission("minecraft.commands.advancement.grant.only", command);
    }

    @ModifyArg(at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/brigadier/builder/RequiredArgumentBuilder;executes(Lcom/mojang/brigadier/Command;)Lcom/mojang/brigadier/builder/ArgumentBuilder;",
            ordinal = 1),
            method = "register"
    )
    private static Command<ServerCommandSource> modifyArg2(Command<ServerCommandSource> command) {
        return new Permission("minecraft.commands.advancement.grant.only.criterion", command);
    }

    @ModifyArg(at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/brigadier/builder/RequiredArgumentBuilder;executes(Lcom/mojang/brigadier/Command;)Lcom/mojang/brigadier/builder/ArgumentBuilder;",
            ordinal = 2),
            method = "register"
    )
    private static Command<ServerCommandSource> modifyArg3(Command<ServerCommandSource> command) {
        return new Permission("minecraft.commands.advancement.grant.from", command);
    }

}
