package me.roundaround.windowsize.mixin;

import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Window.class)
public interface WindowAccessor {
  @Accessor
  void setWindowedWidth(int windowedWidth);

  @Accessor
  void setWindowedHeight(int windowedHeight);
}
