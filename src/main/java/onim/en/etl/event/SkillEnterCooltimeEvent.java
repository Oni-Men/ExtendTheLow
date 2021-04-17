package onim.en.etl.event;

import net.minecraftforge.fml.common.eventhandler.Event;
import onim.en.etl.api.dto.SkillCooltimeResponse;

public class SkillEnterCooltimeEvent extends Event {

  private long eventFiredWhen;
  private final boolean isSpecialSkill;
  private SkillCooltimeResponse data;

  public SkillEnterCooltimeEvent(SkillCooltimeResponse data) {
    this.data = data;
    eventFiredWhen = System.currentTimeMillis();
    isSpecialSkill = data.type.equals("SPECIAL_SKILL");
  }

  @Override
  public boolean isCancelable() {
    return false;
  }

  public String getSkillName() {
    return this.data.name;
  }
  
  public float getRemainingSeconds() {
    return data.cooltime - this.getElapsedSeconds();
  }

  public float getElapsedSeconds() {
    return (System.currentTimeMillis() - eventFiredWhen) / 1000F;
  }

  public String getSkillType() {
    return this.data.type;
  }

  public boolean isSpecialSkill() {
    return this.isSpecialSkill;
  }

  public long getEventFiredWhen() {
    return eventFiredWhen;
  }

}
