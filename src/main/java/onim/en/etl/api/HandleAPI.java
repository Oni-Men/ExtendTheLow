package onim.en.etl.api;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import onim.en.etl.api.dto.ApiResponse;
import onim.en.etl.api.dto.DungeonInfo;
import onim.en.etl.api.dto.PlayerStatus;

public class HandleAPI {
  public static final String PLAYER_DATA_MSG = "§r§a正常にプレイヤーデータを";
  private static ExecutorService service = Executors.newFixedThreadPool(20);

  public static List<String> API_TYPES = Arrays.asList("dungeon", "player");

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
      default:
    }
  }
}
