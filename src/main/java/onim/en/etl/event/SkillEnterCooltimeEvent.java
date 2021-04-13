package onim.en.etl.event;

import net.minecraftforge.fml.common.eventhandler.Event;
import onim.en.etl.api.dto.SkillCooltimeResponse;

public class SkillEnterCooltimeEvent extends Event {

  private final long cooltimeEndsWhen;
  private final long cooltimeStartsWhen;
  private final boolean isSpecialSkill;
  private SkillCooltimeResponse data;

  public SkillEnterCooltimeEvent(SkillCooltimeResponse data) {
    this.data = data;
    cooltimeStartsWhen = System.currentTimeMillis();
    cooltimeEndsWhen = data.cooltimeEndsWhen;
    isSpecialSkill = data.skillType.equals("SPECIAL_SKILL");
  }

  @Override
  public boolean isCancelable() {
    return false;
  }

  public String getSkillName() {
    return this.data.skillName;
  }
  
  public long getRemainingTicks() {
    return cooltimeEndsWhen - System.currentTimeMillis();
  }

  public String getSkillType() {
    return this.data.skillType;
  }

  public boolean isSpecialSkill() {
    return this.isSpecialSkill;
  }

  public long getCooltimeStartsWhen() {
    return cooltimeStartsWhen;
  }
}
