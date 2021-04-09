package onim.en.etl.api.dto;

import java.util.HashMap;
import java.util.UUID;

import com.google.common.collect.Maps;

public class PlayerStatusProvider {

  private HashMap<String, PlayerStatus> nameToStatus = Maps.newHashMap();
  private HashMap<UUID, PlayerStatus> idToStatus = Maps.newHashMap();

  public void setStatus(PlayerStatus status) {
    if (status == null) {
      return;
    }

    if (status.uuid == null || status.mcid == null) {
      return;
    }

    idToStatus.put(status.uuid, status);
    nameToStatus.put(status.mcid, status);
  }

  public PlayerStatus getStatusByUniqueId(UUID uniqueId) {
    return idToStatus.get(uniqueId);
  }

  public PlayerStatus getStatusByName(String name) {
    return nameToStatus.get(name);
  }

}
