package ch.skyfy.ghuperms.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
public abstract class EntityMixin {

//    @Inject(at = @At("HEAD"), method = "move", cancellable = true)
//    public void onMove(MovementType movementType, Vec3d movement, CallbackInfo callbackInfo) {
//        var entity = (Entity) (Object) this;
//        var result = EntityMoveCallback.EVENT.invoker().onMove(entity, movementType, movement);
//        if (result == ActionResult.FAIL)
//            callbackInfo.cancel();
//    }

}
