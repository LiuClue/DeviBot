package liuclue.DeviBot.util;

import java.util.HashMap;

import liuclue.DeviBot.HangmanGame;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class GameUtil {

    private static final HashMap<Long, HangmanGame> games = new HashMap<>();

	public static void sendGameEmbed(MessageChannel channel, User user, String clue, String progress) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle("DeviBot Word Guesser ");
		embed.setDescription(progress);
        embed.addField("Player:", user.getAsMention(), true);
        embed.addField("Clue:", clue, true);
		
		System.out.println(progress);
		channel.sendTyping().queue();
		channel.sendMessageEmbeds(embed.build()).queue();
	}
	
	public static void sendWinEmbed(MessageChannel channel, User user, String clue, String progress) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle("DeviBot Word Guesser | Clue: " + clue);
		embed.setDescription(progress);
		
		System.out.println(progress);
		channel.sendTyping().queue();
		channel.sendMessageEmbeds(embed.build()).queue();
	}

    public static void setGame(long userId, HangmanGame game) {
        games.put(userId, game);
    }

    public static HangmanGame getGame(long userId) {
        return games.get(userId);
    }

    public static void removeGame(long userId) {
        games.remove(userId);
    }
	
	public static boolean hasGame(long userId) {
		// TODO Auto-generated method stub
		return games.containsKey(userId);
	}
}
