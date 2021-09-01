package liuclue.DeviBot;

import java.util.*;
import java.util.concurrent.TimeUnit;

import liuclue.DeviBot.util.GameUtil;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class HangmanGame {

	long gameMessageID;
	long channelID;
	User user;
	public boolean gameActive = false;
	int lives;
	String answer = "";
	String curr = "";
	
	Map<String, String> listOfWords = new HashMap<String, String>() {{
		put("happy go lucky", "cheerful");
		put("second wind", "another burst");
	}};
	List<String> valuesList = new ArrayList<String>(listOfWords.keySet());
	
	public HangmanGame(User user) {
		this.user = user;
	}
	
	public void newGame(MessageChannel channel) {
		if (!gameActive) {
			gameActive = true;
			Random rand = new Random();
			answer = valuesList.get(rand.nextInt(valuesList.size()));
			StringBuilder sb = new StringBuilder();
			lives = 5;
			for (char c : answer.toCharArray()) {
				if(c == ' ') {
					sb.append(' ');
				}else {
					sb.append('?');
				}
			}
			curr = sb.toString();
			GameUtil.sendGameEmbed(channel, user, listOfWords.get(answer), curr);
		}
	}
	
	public void checkChar(char guess) {
		StringBuilder sb = new StringBuilder(curr);
		for (int x = 0; x < answer.length(); x++) {
			if (Character.toLowerCase(answer.charAt(x)) == Character.toLowerCase(guess)) {
				sb.replace(x, x + 1, Character.toString(guess));
			}
		}
		curr = sb.toString();
	}

	public void run(Guild guild, TextChannel channel, String userInput) {
		if (userInput.equals("stop") && gameActive) {
			stop();
            channel.sendMessage("Thanks for playing, " + user.getAsMention() + "!")
            .queue(msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
		}
		if (userInput.equals("play") && !gameActive) {
			newGame(channel);
		} else if (gameActive) {
			if (lives <= 0) {
				System.out.println("Failed");
				stop();
			}else if(curr.equals(answer)) {
				GameUtil.sendGameEmbed(channel, user, listOfWords.get(answer), curr);
				System.out.println("Guessed Right");
				stop();
			} else {
				GameUtil.sendGameEmbed(channel, user, listOfWords.get(answer), curr);
				System.out.println("Guessed Letter");
			}
			
		}
	}
	
	public void guess(String[] input) {
		if(input.length == 1) {
			if(input[0].length() == 1) {
				checkChar(input[0].charAt(0));
				return;
			}
		}
		int counter = 0;
		String[] ansArray = answer.split("\\s+");
		if(ansArray.length > input.length) return;
		for(String word: ansArray) {
			if(!word.equals(input[counter])) return;
			counter ++;
		}
		curr = answer;
	}
	
	public void stop() {
		gameActive = false;
		answer = "";
		TextChannel textChannel = DeviBot.getShardManager().getTextChannelById(channelID);
        if (textChannel != null) {
            textChannel.retrieveMessageById(gameMessageID).queue(gameMessage -> gameMessage.delete().queue());
        }
	}
}
