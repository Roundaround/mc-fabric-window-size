package me.roundaround.windowsize.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import me.roundaround.windowsize.Resolution;
import me.roundaround.windowsize.VideoOptionsScreenExtensions;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.VideoOptionsScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.Monitor;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(VideoOptionsScreen.class)
public abstract class VideoOptionsScreenMixin extends GameOptionsScreen implements VideoOptionsScreenExtensions {
  @Unique
  @Nullable
  private List<Resolution> resolutions;

  @Unique
  @Nullable
  private SimpleOption<Integer> resolutionOption;

  @Unique
  private boolean dirty;

  @Inject(
      method = "addOptions", at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/client/gui/widget/OptionListWidget;addSingleOptionEntry" +
               "(Lnet/minecraft/client/option/SimpleOption;)V",
      ordinal = 0,
      shift = At.Shift.AFTER
  )
  )
  private void afterFullscreenResolutionOptionAdded(CallbackInfo ci, @Local Monitor monitor, @Local Window window) {
    this.resolutions = this.getAvailableResolutions(monitor);

    this.resolutionOption = new SimpleOption<>(
        "windowsize.options.resolution",
        SimpleOption.emptyTooltip(),
        (optionText, value) -> {
          if (this.getResolutions().isEmpty()) {
            return Text.translatable("windowsize.options.resolution.unavailable");
          }

          Resolution resolution = this.getSelectedResolution(value);
          if (resolution == null) {
            return GameOptions.getGenericValueText(
                optionText,
                Text.translatable("windowsize.options.resolution.current")
            );
          }

          return GameOptions.getGenericValueText(
              optionText,
              Text.translatable("windowsize.options.resolution.entry", resolution.width(), resolution.height())
          );
        },
        new SimpleOption.ValidatingIntSliderCallbacks(-1, this.getResolutions().size() - 1),
        this.getCurrentIndex(),
        (value) -> {

          Resolution resolution = this.getSelectedResolution(value);
          if (resolution == null) {
            this.dirty = false;
            return;
          }

          this.dirty = true;
          this.gameOptions.overrideWidth = resolution.width();
          this.gameOptions.overrideHeight = resolution.height();
        }
    );

    assert this.body != null;
    this.body.addSingleOptionEntry(this.resolutionOption);
  }

  @Inject(method = "close", at = @At("HEAD"))
  private void onClose(CallbackInfo ci) {
    if (!this.dirty || this.client == null || this.resolutionOption == null) {
      return;
    }

    Window window = this.client.getWindow();
    if (window == null) {
      return;
    }

    Resolution resolution = this.getSelectedResolution(this.resolutionOption.getValue());
    if (resolution == null) {
      return;
    }

    if (!window.isFullscreen()) {
      window.setWindowedSize(resolution.width(), resolution.height());
    } else {
      ((WindowAccessor) (Object) window).setWindowedWidth(resolution.width());
      ((WindowAccessor) (Object) window).setWindowedHeight(resolution.height());
    }
  }

  @Override
  public void windowsize$onResolutionChange(int width, int height) {
    if (this.resolutionOption == null) {
      return;
    }
    this.resolutionOption.setValue(this.getCurrentIndex());
  }

  @Unique
  private List<Resolution> getAvailableResolutions(@Nullable Monitor monitor) {
    if (monitor == null) {
      return List.of();
    }

    return ((MonitorAccessor) (Object) monitor).getVideoModes()
        .stream()
        .map((videoMode) -> new Resolution(videoMode.getWidth(), videoMode.getHeight()))
        .distinct()
        .toList();
  }

  @Unique
  private List<Resolution> getResolutions() {
    if (this.resolutions == null) {
      return List.of();
    }
    return this.resolutions;
  }

  @Unique
  private int getCurrentIndex() {
    if (this.resolutions == null || this.resolutions.isEmpty()) {
      return -1;
    }
    return this.resolutions.indexOf(new Resolution(this.gameOptions.overrideWidth, this.gameOptions.overrideHeight));
  }

  @Unique
  @Nullable
  private Resolution getSelectedResolution(int index) {
    List<Resolution> resolutions = this.getResolutions();
    if (index < 0 || index >= resolutions.size()) {
      return null;
    }
    return resolutions.get(index);
  }

  private VideoOptionsScreenMixin(Screen parent, GameOptions gameOptions, Text title) {
    super(parent, gameOptions, title);
  }
}
