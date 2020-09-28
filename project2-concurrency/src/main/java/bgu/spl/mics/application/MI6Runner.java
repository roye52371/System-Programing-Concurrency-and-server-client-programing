package bgu.spl.mics.application;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.subscribers.Intelligence;
import bgu.spl.mics.application.publishers.TimeService;
import bgu.spl.mics.application.subscribers.M;
import bgu.spl.mics.application.subscribers.Moneypenny;
import bgu.spl.mics.application.subscribers.Q;
import com.google.gson.*;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class MI6Runner {
    public static void main(String[] args) {
        Gson gson = new Gson();
        try {
            FileReader br = new FileReader(args[0]);
            BufferedReader b = new BufferedReader(br);
            JsonObject object = gson.fromJson(b, JsonObject.class);
            JsonArray squadJson = object.get("squad").getAsJsonArray();
            createSquad(squadJson);
            JsonArray inventoryJson = object.getAsJsonArray("inventory");
            createInventory(inventoryJson);

            JsonObject services = object.get("services").getAsJsonObject();
            int numOfM = services.get("M").getAsInt();
            Thread[] mThreads = createMs(numOfM);
            int numOfMoneypenny = services.get("Moneypenny").getAsInt();
            Thread[] moneypennyThreads = createMoneypenny(numOfMoneypenny);
            Runnable q = new Q();
            Thread qThread = new Thread(q);
            JsonArray intelligenceJson = services.get("intelligence").getAsJsonArray();
            Thread[] intelligenceThreads = createIntelligences(intelligenceJson);
            int duration = services.get("time").getAsInt();
            TimeService timeService = new TimeService(duration);
            Thread timeServiceThread = new Thread(timeService);

            for (int i = 0; i < mThreads.length; i++)
                mThreads[i].start();
            for (int i = 0; i < moneypennyThreads.length; i++)
                moneypennyThreads[i].start();
            qThread.start();
            for (int i = 0; i < intelligenceThreads.length; i++)
                intelligenceThreads[i].start();
            timeServiceThread.start();

            for (int i = 0; i < mThreads.length; i++)
                 mThreads[i].join();
            for (int i = 0; i < moneypennyThreads.length; i++)
                moneypennyThreads[i].join();
            qThread.join();
            for (int i = 0; i < intelligenceThreads.length; i++)
                intelligenceThreads[i].join();
            timeServiceThread.join();

            Inventory.getInstance().printToFile(args[1]);
            Diary.getInstance().printToFile(args[2]);

        } catch (Exception e) { }
    }

    private static Thread[] createIntelligences(JsonArray jsonArray) {
        Thread[] intelligenceThreads = new Thread[jsonArray.size()];
        for (int i = 0; i < intelligenceThreads.length; i++) {
            JsonObject intelligenceJson = jsonArray.get(i).getAsJsonObject();
            JsonArray missions = intelligenceJson.get("missions").getAsJsonArray();
            List<MissionInfo> missionInfos = new LinkedList<>();
            for (int j = 0; j < missions.size(); j++) {
                JsonObject missionInfo = missions.get(j).getAsJsonObject();
                JsonArray serialAgentsNumbersJson = missionInfo.get("serialAgentsNumbers").getAsJsonArray();
                List<String> serialAgentsNumbers = new LinkedList<>();
                for (int k = 0; k < serialAgentsNumbersJson.size(); k++) {
                    String number = serialAgentsNumbersJson.get(k).getAsString();
                    serialAgentsNumbers.add(number);
                }
                int duration = missionInfo.get("duration").getAsInt();
                String gadget = missionInfo.get("gadget").getAsString();
                String missionName = missionInfo.get("name").getAsString();
                int timeExpired = missionInfo.get("timeExpired").getAsInt();
                int timeIssued = missionInfo.get("timeIssued").getAsInt();
                MissionInfo newMissionInfo = new MissionInfo(missionName, serialAgentsNumbers, gadget, timeIssued, timeExpired, duration);
                missionInfos.add(newMissionInfo);
            }
            Runnable intelligence = new Intelligence(missionInfos);
            intelligenceThreads[i] = new Thread(intelligence);
        }
        return intelligenceThreads;
    }

    private static Thread[] createMoneypenny(int numOfMoneypenny) {
        Thread[] moneypennys = new Thread[numOfMoneypenny];
        for (int i = 0; i < moneypennys.length; i++) {
            Runnable m = new Moneypenny(i + 1);
            moneypennys[i] = new Thread(m);
        }
        return moneypennys;
    }

    private static Thread[] createMs(int numOfM) {
        Thread[] Ms = new Thread[numOfM];
        for (int i = 0; i < Ms.length; i++) {
            Runnable m = new M(i + 1);
            Ms[i] = new Thread(m);
        }
        return Ms;
    }

    private static void createSquad(JsonArray jsonArray) {
        Agent[] agents = new Agent[jsonArray.size()];
        for (int i = 0; i < agents.length; i++) {
            JsonObject agentJson = jsonArray.get(i).getAsJsonObject();
            String name = agentJson.get("name").getAsString();
            String serial = agentJson.get("serialNumber").getAsString();
            Agent agent = new Agent(serial, name);
            agents[i] = agent;
        }
        Squad.getInstance().load(agents);
    }

    private static void createInventory(JsonArray jsonArray) {
        String[] gadgets = new String[jsonArray.size()];
        for (int i = 0; i < gadgets.length; i++) {
            String gadget = jsonArray.get(i).getAsString();
            gadgets[i] = gadget;
        }
        Inventory.getInstance().load(gadgets);
    }

}
