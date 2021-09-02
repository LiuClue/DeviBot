package liuclue.DeviBot;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
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
	String clue = "";
	String curr = "";
	
	File puzzleFile;
	
	public HangmanGame(User user) {
        try {
            puzzleFile = Paths.get("puzzle.txt").toFile();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
		this.user = user;
	}
	
	public void newGame(MessageChannel channel) {
		if (!gameActive) {
			gameActive = true;
			Random rand = new Random();
			try {
				String puzzle = choose(puzzleFile);
				String[] puzzleArray = puzzle.split(",");
		        DeviBot.debug("Generating puzzle: " + puzzleArray[1]);
		        
				answer = puzzleArray[0];
				clue = puzzleArray[1];
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
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
			GameUtil.sendGameEmbed(channel, user, clue, curr, lives);
		}
	}
	

	  public static String choose(File f) throws FileNotFoundException
	  {
	     String result = null;
	     Random rand = new Random();
	     int n = 0;
	     for(Scanner sc = new Scanner(f); sc.hasNext(); )
	     {
	        ++n;
	        String line = sc.nextLine();
	        if(rand.nextInt(n) == 0)
	           result = line;         
	     }

	     return result;      
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
			if(curr.equals(answer)) {
				GameUtil.sendGameEmbed(channel, user, clue, curr, lives);
				stop();
			}else if (lives <= 0) {
				GameUtil.sendGameEmbed(channel, user, clue, answer, lives);
				stop(); 
			}else {
				lives --;
				GameUtil.sendGameEmbed(channel, user, clue, curr, lives);
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
