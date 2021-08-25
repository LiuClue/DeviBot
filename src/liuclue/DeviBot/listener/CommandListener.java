package liuclue.DeviBot.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import liuclue.DeviBot.DeviBot;
import liuclue.DeviBot.HangmanGame;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter{
	
    private static final ArrayList<String> commandsNoPrefix = new ArrayList<>(
            Arrays.asList("info", "guess", "stop", "play"));
	
	HangmanGame game = new HangmanGame();
	
	public CommandListener() {
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		User user = event.getAuthor();
		Message message = event.getMessage();
		TextChannel channel = event.getChannel();
		Guild guild = event.getGuild();
		String msgRaw = message.getContentRaw();
		String[] args = msgRaw.split("\\s+");
		
		if(args.length > 0) {
			String prefix = DeviBot.getPrefix();
			String arg = args[0].toLowerCase();
			if (arg.startsWith(prefix)) {
				String currCom = arg.substring(1);
                if (!hasPermissions(guild, channel)) {
                    DeviBot.debug("Not enough permissions to run command: " + arg);
                    sendInvalidPermissionsMessage(user, channel);
                    return;
                }
                
                if(commandsNoPrefix.contains(currCom)) {
                	if(currCom.equals("guess")) {
                		String guess = "";
                		for (int x = 1; x < args.length; x++) {
                			guess += args[x];
                		}
                			
                		game.run(guild, channel, guess);
                	} else {
                		game.run(guild, channel, currCom);
                	}
                } else {
                	System.out.println("not command " + currCom);
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
