package onim.en.etl.ui.parts;

import java.util.HashMap;

import com.google.common.collect.Maps;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class GuiActionButton extends GuiButton {

  private static ResourceLocation location = new ResourceLocation("onim.en.etl:onim.en.etl.click");

  public static class ButtonIDRegistry {
    private static HashMap<Integer, GuiActionButton> buttons = Maps.newHashMap();

    public static int nextButton(GuiActionButton actionButton) {
      int id = buttons.size();
      buttons.put(id, actionButton);
      return id;
    }

    public static GuiActionButton getButton(int id) {
      return buttons.get(id);
    }
  }

  public boolean centered = false;
  private Runnable action = null;

  public GuiActionButton(int widthIn, int heightIn, String buttonText) {
    super(-9999, 0, 0, widthIn, heightIn, I18n.format(buttonText));
    this.id = ButtonIDRegistry.nextButton(this);
  }

  public void setOnAction(Runnable action) {
    this.action = action;
  }

  public void onClick() {
    if (this.action != null) {
      this.action.run();
    }
  }

  @Override
  public void playPressSound(SoundHandler soundHandlerIn) {
    soundHandlerIn.playSound(PositionedSoundRecord.create(location, 1.0F));
  }

}
