package onim.en.etl.extension.normal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import onim.en.etl.annotation.PrefItem;
import onim.en.etl.extension.TheLowExtension;

public class ExpMessageUtil extends TheLowExtension {

  public enum ExpMessageProcessing {
    NORMAL, STACK, OFF
  }

  @PrefItem(id = "expMessageUtil.processingType", type = ExpMessageProcessing.class)
  public ExpMessageProcessing processingType = ExpMessageProcessing.NORMAL;

  private static final Pattern ExpMessage =
      Pattern.compile("§r§b(?<type>魔法|剣|弓|メイン)レベル \\+ (?<amount>[0-9]+) exp§r");

  private ExpMessageData previousExpMessage = null;
  private int messageID = 0;

  @Override
  public String id() {
    return "onim.en.etl.expMessageUtil";
  }

  @Override
  public String category() {
    return "onim.en.etl.category.util";
  }

  @Override
  public void onEnable() {
    previousExpMessage = null;
    messageID = 0;
  }

  @Override
  public void onDisable() {

  }

  @SubscribeEvent
  public void onClientChatReceived(ClientChatReceivedEvent event) {
    String text = event.message.getFormattedText();
    Matcher matcher = ExpMessage.matcher(text);
    if (!matcher.matches()) {
      previousExpMessage = null;
      return;
    }

    switch (processingType) {
      case NORMAL:
        break;
      case OFF:
        event.setCanceled(true);
        break;
      case STACK:
        String type = matcher.group("type");
        int amount = 0;
        try {
          amount = Integer.parseInt(matcher.group("amount"));
        } catch (NumberFormatException e) {
          previousExpMessage = null;
          return;
        }
        ExpMessageData msgData = new ExpMessageData(type, amount);
        this.stackMessage(msgData, event);
        break;
      default:
        break;
    }
  }

  private void stackMessage(ExpMessageData msgData, ClientChatReceivedEvent event) {
    event.setCanceled(true);
    GuiNewChat chat = Minecraft.getMinecraft().ingameGUI.getChatGUI();

    if (previousExpMessage == null
        || (previousExpMessage != null && !previousExpMessage.isSameType(msgData))) {
      previousExpMessage = msgData;
      this.printMessage(msgData.toChatComponent());
      return;
    }

    if (previousExpMessage.isSameType(msgData)) {
      chat.deleteChatLine(messageID);
      msgData.add(previousExpMessage);
      this.printMessage(msgData.toChatComponent());
    }

    previousExpMessage = msgData;
  }

  private void printMessage(IChatComponent chat) {
    messageID = (int) (this.hashCode() + System.currentTimeMillis());
    Minecraft.getMinecraft().ingameGUI.getChatGUI()
        .printChatMessageWithOptionalDeletion(chat, messageID);
  }

  static class ExpMessageData {
    public final String type;
    public int amount;

    public ExpMessageData(String type, int amount) {
      this.type = type;
      this.amount = amount;
    }

    public boolean isSameType(ExpMessageData data) {
      return this.type.equals(data.type);
    }

    public void add(ExpMessageData data) {
      if (!this.isSameType(data)) {
        return;
      }
      this.amount += data.amount;
    }

    public IChatComponent toChatComponent() {
      return new ChatComponentText("§b" + this.type + "レベル + " + amount + " exp§r");
    }
  }
}
