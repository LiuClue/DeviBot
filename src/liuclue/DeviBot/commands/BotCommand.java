package liuclue.DeviBot.commands;

import liuclue.DeviBot.event.CommandEvent;

public abstract class BotCommand {
	private final String name;

	public BotCommand(String name) {
		this.name = name;
	}
	public String getName() {
			return name;
	}
	
	public abstract void execute(CommandEvent commandEvent);
}
