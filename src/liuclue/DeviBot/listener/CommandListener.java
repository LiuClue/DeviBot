package liuclue.DeviBot.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import liuclue.DeviBot.DeviBot;
import liuclue.DeviBot.commands.GameCommand;
import liuclue.DeviBot.event.CommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter{
	
    private static final HashMap<String, GameCommand> commands = new HashMap<>();
	
	public CommandListener() {
        List<GameCommand> botCommands = new ArrayList<>();//Arrays.asList(new InfoCommand(), new PrefixCommand()));
        botCommands.addAll(Arrays.asList(new GameCommand("play"), new GameCommand("guess"),
                new GameCommand("stop")));

        for (GameCommand command : botCommands) commands.put(command.getName().toLowerCase(), command);
        System.out.println("[INFO] Loaded " + commands.size() + " commands");
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		User user = event.getAuthor();
		if(user.isBot()) {
			return;
		}
	
		Message message = event.getMessage();
		TextChannel channel = event.getChannel();
		Guild guild = event.getGuild();
		String msgRaw = message.getContentRaw();
		String[] args = msgRaw.split("\\s+");
		
		if(args.length > 0) {
			String prefix = DeviBot.getPrefix();
			String arg = args[0].toLowerCase();
			boolean isCommand;
			if (arg.startsWith(prefix)) {
				String currCom = arg.substring(prefix.length()).toLowerCase();
                if (!hasPermissions(guild, channel)) {
                    DeviBot.debug("Not enough permissions to run command: " + arg);
                    sendInvalidPermissionsMessage(user, channel);
                    return;
                }
                isCommand = commands.containsKey(currCom);
                if(isCommand) {
                    DeviBot.debug("Command received: " + currCom);
                    if (!hasPermissions(guild, channel)) {
                        DeviBot.debug("Not enough permissions to run command: " + currCom);
                        sendInvalidPermissionsMessage(user, channel);
                        return;
                    }
                    GameCommand command = commands.get(currCom);
                    if (command == null) {
                        DeviBot.debug("Received command does not exist: " + currCom);
                        return;
                    }
                    DeviBot.debug("Executing command: " + currCom);
                    command.execute(new CommandEvent(event, Arrays.copyOfRange(msgRaw.split("\\s+"), 1, args.length)));
                }
			}
		}
	}
	
	private static final Collection<Permission> requiredPermissions = Arrays
            .asList(Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_EMBED_LINKS,
                    Permission.MESSAGE_WRITE);
	
    private boolean hasPermissions(Guild guild, TextChannel channel) {
        Member self = guild.getSelfMember();
        if (self.hasPermission(Permission.ADMINISTRATOR)) return true;
        return self.hasPermission(channel, requiredPermissions);
    }
    
    private void sendInvalidPermissionsMessage(User user, TextChannel channel) {
        if (channel.canTalk()) {
            StringBuilder requiredPermissionsDisplay = new StringBuilder();
            for (Permission requiredPermission : requiredPermissions) {
                requiredPermissionsDisplay.append("`").append(requiredPermission.getName()).append("`, ");
            }
            if (requiredPermissionsDisplay.toString().endsWith(", ")) requiredPermissionsDisplay = new StringBuilder(
                    requiredPermissionsDisplay.substring(0, requiredPermissionsDisplay.length() - 2));
            channel.sendMessage(user.getAsMention() + ", I don't have enough permissions to work properly.\nMake "
                                        + "sure I have the following permissions: " + requiredPermissionsDisplay
                                        + "\nIf you think this is "
                                        + "an error, please contact a server administrator.").queue();
        }
    }
}
