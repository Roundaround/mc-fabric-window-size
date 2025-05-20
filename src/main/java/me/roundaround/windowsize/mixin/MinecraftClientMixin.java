package me.roundaround.windowsize.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.VideoOptionsScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.Window;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
  @Shadow
  @Final
  public GameOptions options;

  @Shadow
  @Final
  private Window window;

  @Shadow
  @Nullable
  public Screen currentScreen;

  @Inject(method = "onResolutionChanged", at = @At("RETURN"))
  private void afterResolutionChanged(CallbackInfo ci) {
    if (this.window.isFullscreen()) {
      return;
    }

    this.options.overrideWidth = this.window.getWidth();
    this.options.overrideHeight = this.window.getHeight();

    if (this.currentScreen instanceof VideoOptionsScreen screen) {
      screen.windowsize$onResolutionChange(this.options.overrideWidth, this.options.overrideHeight);
    }
  }
}
