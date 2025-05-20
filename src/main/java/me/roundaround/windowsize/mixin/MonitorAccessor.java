package me.roundaround.windowsize.mixin;

import net.minecraft.client.util.Monitor;
import net.minecraft.client.util.VideoMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(Monitor.class)
public interface MonitorAccessor {
  @Accessor
  List<VideoMode> getVideoModes();
}
