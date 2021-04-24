package onim.en.etl.ui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public abstract class ActionButton extends ComponentBase {

  private static ResourceLocation location = new ResourceLocation("onim.en.etl:onim.en.etl.click");
  public String displayString;

  public boolean centered = false;
  private Runnable action = null;

  public ActionButton(int widthIn, int heightIn, String buttonText) {
    this.width = widthIn;
    this.height = heightIn;
    this.displayString = I18n.format(buttonText);
  }

  public void setOnAction(Runnable action) {
    this.action = action;
  }

  @Override
  public void mousePressed(int button) {
    this.onClick();
  }

  public void onClick() {
    if (this.action != null) {
      this.action.run();
    }
  }

  public void playPressSound(SoundHandler soundHandlerIn) {
    soundHandlerIn.playSound(PositionedSoundRecord.create(location, 1.0F));
  }

  public String trimStringWithLeader(String s, String leader, int width) {
    FontRenderer f = Minecraft.getMinecraft().fontRendererObj;
    int leaderWidth = f.getStringWidth(leader);
    return f.trimStringToWidth(s, width - leaderWidth);
  }
}
