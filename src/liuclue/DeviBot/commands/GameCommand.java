package liuclue.DeviBot.commands;

import java.util.Arrays;

import liuclue.DeviBot.DeviBot;
import liuclue.DeviBot.HangmanGame;
import liuclue.DeviBot.event.CommandEvent;
import liuclue.DeviBot.util.GameUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class GameCommand extends BotCommand {
	
	public GameCommand(String name) {
        super(name);
    }

    @Override
    public void execute(CommandEvent event) {
        User user = event.getAuthor();
        String[] args = event.getArgs();
        String prefix = DeviBot.getPrefix();
        HangmanGame game;
        String userInput = this.getName().toLowerCase();
        if (GameUtil.hasGame(user.getIdLong())) {
        	game = GameUtil.getGame(user.getIdLong());
        } else if(userInput.equals("guess")) {
        	event.reply(user.getAsMention() + ", you need to start a game first.");
        	return;
        } else {
            game = new HangmanGame(user);
            GameUtil.setGame(user.getIdLong(), game);
        }
        //
        DeviBot.debug("Processing game input: " + userInput);
        if (userInput.equals("play")) {
            if (!game.gameActive) {
                //if (args.length > 0 && EmojiManager.isEmoji(args[0])) game.setPlayerEmote(args[0]);
            } else {
                event.reply(user.getAsMention() + ", you already have an active game.\nUse `" + prefix
                                    + "stop` to stop your current game first.");
            }
        }
        DeviBot.debug("Processing game input args: " + Arrays.toString(args));
        Guild guild = event.getGuild();
        TextChannel channel = event.getTextChannel();
        if (userInput.equals("guess")) {
        	if(args.length > 0) game.guess(args);
        }
        game.run(event.getGuild(), channel, userInput);
        if (userInput.equals("stop")) GameUtil.removeGame(user.getIdLong());
        if (game.gameActive && guild.getSelfMember().hasPermission(channel, Permission.MESSAGE_MANAGE))
            event.getMessage().delete().queue();
    }
}
