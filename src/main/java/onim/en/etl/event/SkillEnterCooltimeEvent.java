package onim.en.etl.event;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.Event;
import onim.en.etl.api.dto.SkillCooltimeResponse;

public class SkillEnterCooltimeEvent extends Event {

  private final long eventFiredWhen;
  private final long cooltimeEndsWhen;
  private final boolean isSpecialSkill;
  private SkillCooltimeResponse data;

  public SkillEnterCooltimeEvent(SkillCooltimeResponse data) {
    this.data = data;
    eventFiredWhen = Minecraft.getSystemTime();
    cooltimeEndsWhen = eventFiredWhen + data.cooltimeTick;
    isSpecialSkill = data.skillType.equals("SPECIAL_SKILL");
  }

  @Override
  public boolean isCancelable() {
    return false;
  }

  public String getSkillName() {
    return this.data.skillName;
  }

  public long getCooltimeTick() {
    return this.data.cooltimeTick;
  }
  
  public long getRemainingTicks() {
    return cooltimeEndsWhen - eventFiredWhen;
  }

  public String getSkillType() {
    return this.data.skillType;
  }

  public boolean isSpecialSkill() {
    return this.isSpecialSkill;
  }
}
