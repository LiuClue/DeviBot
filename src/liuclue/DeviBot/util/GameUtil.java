package liuclue.DeviBot.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

public class GameUtil {

	public GameUtil() {
		// TODO Auto-generated constructor stub
	}

	public static void sendGameEmbed(MessageChannel channel, User user, String clue, String progress) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle("DeviBot Word Guesser | Clue: " + clue);
		embed.setDescription(progress);
		
		System.out.println(progress);
		channel.sendTyping().queue();
		channel.sendMessageEmbeds(embed.build()).queue();
	}
}
