package liuclue.DeviBot;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.security.auth.login.LoginException;

import liuclue.DeviBot.listener.CommandListener;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;


public class DeviBot {
	private static String prefix = "+";
	
	public static boolean debug = false;
	
	private static ShardManager shardManager;
	
	public static void main(String[] args) throws LoginException {
		String token = null;
        try {
            File tokenFile = Paths.get("token.txt").toFile();
            if (!tokenFile.exists()) {
                System.out.println("[ERROR] Could not find token.txt file");
                System.out.print("Please paste in your bot token: ");
                Scanner s = new Scanner(System.in);
                token = s.nextLine();
                System.out.println();
                System.out.println("[INFO] Creating token.txt - please wait");
                if (!tokenFile.createNewFile()) {
                    System.out.println(
                            "[ERROR] Could not create token.txt - please create this file and paste in your token"
                                    + ".");
                    s.close();
                    return;
                }
                Files.write(tokenFile.toPath(), token.getBytes());
                s.close();
            }
            token = new String(Files.readAllBytes(tokenFile.toPath()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
		
		
		List<GatewayIntent> intents = new ArrayList<>(
				Arrays.asList(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_EMOJIS,
						GatewayIntent.GUILD_MESSAGE_REACTIONS));
		
		DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.create(token, intents);
		
		builder.setStatus(OnlineStatus.ONLINE);
		builder.setActivity(Activity.playing("Type #help"));
        builder.addEventListeners(new CommandListener());
		shardManager = builder.build();
		

        Thread consoleThread = new Thread(() -> {
            Scanner s = new Scanner(System.in);
            while (s.hasNextLine()) {
                processCommand(s.nextLine());
            }
        });
        
        consoleThread.setDaemon(true);
        consoleThread.setName("Console Thread");
        consoleThread.start();
	}
	
	private static void processCommand(String cmd) {
		if(cmd.equalsIgnoreCase("stop")) {
            System.out.println("Shutting down...");
			shardManager.shutdown();
            System.out.println("Bye!");
			System.exit(0);
			return;
		}

        if (cmd.equalsIgnoreCase("debug")) {
            debug = !debug;
            String response = debug ? "on" : "off";
            System.out.println("[INFO] Turned " + response + " debug mode");
            DeviBot.debug("Make sure to turn off debug mode after necessary information has been collected.");
            return;
        }
	}
	
	public static ShardManager getShardManager() {
        return shardManager;
    }
	
	public static String getPrefix() {
		return prefix;
	}
	

    // Print a message when debug is on
    public static void debug(String log) {
        if (debug) {
            System.out.println("[DEBUG] " + log);
        }
    }
}
