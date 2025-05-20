package me.roundaround.windowsize;

import me.roundaround.gradle.api.annotation.Entrypoint;
import me.roundaround.windowsize.generated.Constants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Entrypoint(Entrypoint.CLIENT)
public class WindowSizeMod implements ClientModInitializer {
  public static final Logger LOGGER = LogManager.getLogger(Constants.MOD_ID);

  @Override
  public void onInitializeClient() {

  }
}
