package liuclue.DeviBot.commands;

public abstract class BotCommand {
	private final String name;

	public BotCommand(String name) {
		this.name = name;
	}
	public String getName() {
			return name;
	}
}
