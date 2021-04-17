package onim.en.etl.api;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import onim.en.etl.api.dto.ApiResponse;
import onim.en.etl.api.dto.DungeonInfo;
import onim.en.etl.api.dto.LocationResponse;
import onim.en.etl.api.dto.PlayerStatus;
import onim.en.etl.api.dto.SkillCooltimeResponse;
import onim.en.etl.event.SkillEnterCooltimeEvent;
import onim.en.etl.util.TickTaskExecutor;

public class HandleAPI {
  public static final String PLAYER_DATA_MSG = "§r§a正常にプレイヤーデータを";
  private static ExecutorService service = Executors.newFixedThreadPool(20);

  public static List<String> API_TYPES = Arrays.asList("dungeon", "player");

  public static void requestDatas() {
    for (String type : HandleAPI.API_TYPES) {
      sendRequest(type);
    }
  }

  public static void sendRequest(String apiType) {
    TickTaskExecutor.addTask(() -> {
      Minecraft.getMinecraft().thePlayer.sendChatMessage(String.format("/thelow_api %s", apiType));
    });
  }

  public static boolean process(ClientChatReceivedEvent event) {
    String message = event.message.getUnformattedText();

    if (!message.startsWith("$api")) {
      return false;
    }

    String[] split = message.split(" ", 2);

    if (split.length != 2) {
      return false;
    }

    service.submit(() -> {
      processJSON(split[1]);
      DataStorage.cache();
    });

    return true;
  }

  private static void processJSON(String json) {
    Gson gson = new GsonBuilder().create();
    ApiResponse<?> response = gson.fromJson(json, ApiResponse.class);

    if (response.version != 1) {
      return;
    }
    
    Type type;
    switch (response.apiType) {
      case "player_status":
        type = new TypeToken<ApiResponse<PlayerStatus>>() {}.getType();
        ApiResponse<PlayerStatus> playerStatus = gson.fromJson(json, type);
        DataStorage.setStatus(playerStatus.response);
        break;
      case "dungeon":
        type = new TypeToken<ApiResponse<DungeonInfo[]>>() {}.getType();
        ApiResponse<DungeonInfo[]> dungeonInfos = gson.fromJson(json, type);
        DataStorage.clearDungeons();
        for (DungeonInfo dungeonInfo : dungeonInfos.response) {
          DataStorage.addDungeon(dungeonInfo);
        }
        break;
      case "location":
        type = new TypeToken<ApiResponse<LocationResponse>>() {}.getType();
        ApiResponse<LocationResponse> location = gson.fromJson(json, type);
        DataStorage.setCurrentWorldName(location.response.worldName);
        break;
      case "skill_cooltime":
        type = new TypeToken<ApiResponse<SkillCooltimeResponse>>() {}.getType();
        ApiResponse<SkillCooltimeResponse> skillCooltime = gson.fromJson(json, type);
        MinecraftForge.EVENT_BUS.post(new SkillEnterCooltimeEvent(skillCooltime.response));
      default:
    }
  }
}
