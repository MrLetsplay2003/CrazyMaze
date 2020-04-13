package me.mrletsplay.crazymaze.main;

public enum Message {
	
	COMMAND_CONSOLE("command.console", "%prefix% §cThe console can't do that"),
	COMMAND_NO_PERMISSION("command.no-permission", "%prefix% §cNo permission"),
	COMMAND_ARENA_ALREADY_EXISTS("command.arena-already-exists", "%prefix% §cArena already exists!"),
	COMMAND_ALREADY_EDITING("command.already-editing", "%prefix% §cYou are already editing an arena"),
	COMMAND_ARENA_DOESNT_EXIST("command.arena-doesnt-exist", "%prefix% §cThat arena doesn't exist"),
	COMMAND_PROPERTIES_IMPORTED("command.properties-imported", "%prefix% §aProperties of §7%arena% §aimported"),
	COMMAND_ARENA_CREATED("command.arena-created", "%prefix% §aArena created"),
	COMMAND_NOW_EDITING("command.now-editing", "%prefix% §aYou are now editing the arena §7%arena%"),
	COMMAND_NOT_EDITING("command.not-editing", "%prefix% §cYou are currently not editing an arena"),
	COMMAND_SET_MAX_PLAYERS("command.set.max-players", "%prefix% §aMax players set to §7%num%"),
	COMMAND_SET_MORE_PLAYERS("command.set.more-players", "%prefix% §cCan't have less than 1 player"),
	COMMAND_SET_SIZE("command.set.size", "%prefix% §aSize set to §7%num%"),
	COMMAND_SET_MORE_SIZE("command.set.more-size", "%prefix% §cCan't have size < 3"),
	COMMAND_SET_POWERUPS("command.set.powerups", "%prefix% §aEnablePowerups set to §7%state%"),
	COMMAND_SET_ONWIN("command.set.onwin", "%prefix% §aOnWin command set to §7/%cmd%"),
	COMMAND_SET_LOBBY("command.set.lobby", "%prefix% §aLobby set"),
	COMMAND_SET_MAINLOBBY("command.set.mainlobby", "%prefix% §aMainlobby set"),
	COMMAND_SET_DEFAULTMAINLOBBY("command.set.defaultmainlobby", "%prefix% §aDefault mainlobby set"),
	COMMAND_SET_SIGN("command.set.sign", "%prefix% §aSign set"),
	COMMAND_SET_SIGN_NOT_LOOKING("command.set.sign-not-looking", "%prefix% §cYou need to look at a sign to set it"),
	COMMAND_SET_MIN_PLAYERS("command.set.min-players", "%prefix% §aMin players set to §7%num%"),
	COMMAND_SET_TOO_MANY_PLAYERS("command.set.too-many-players", "%prefix% §cMin players cannot be > than max players"),
	COMMAND_SET_LAYOUTS("command.set.layouts", "%prefix% §aArena layouts set to §7%layouts%"),
	COMMAND_SET_LAYOUTS_NOT_FOUND("command.set.layouts-not-found", "%prefix% §cCouldn't find layouts: §7%layouts%"),
	COMMAND_SET_LAYOUTS_DUPLICATE("command.set.layouts-duplicate", "%prefix% §cFound duplicate entries: §7%layouts%"),
	COMMAND_SETUP_INCOMPLETE("command.setup-incomplete", "%prefix% §cArena setup incomplete. Missing: %missing%"),
	COMMAND_ARENA_SAVED("command.arena-saved", "%prefix% §aArena saved"),
	COMMAND_CHANGES_DISCARDED("command.changes-discarded", "%prefix% §cChanges discarded"),
	COMMAND_ARENA_IS_BEING_EDITED("command.arena-is-being-edited", "%prefix% §cThat arena is currently being edited"),
	COMMAND_ARENA_DELETED("command.arena-deleted", "%prefix% §cDeleted Arena §6%arena% §csuccessfully"),
	COMMAND_KICK_PLAYER_NOT_ONLINE("command.kick.player-not-online", "%prefix% §cThat player doesn't exist or isn't currently online"),
	COMMAND_KICK_PLAYER_NOT_INGAME("command.kick.player-not-ingame", "%prefix% §cThat player is currently not ingame"),
	COMMAND_KICK_KICKED("command.kick.kicked", "%prefix% §aYou've kicked the player §7%player% §afrom their game successfully"),
	COMMAND_START_ALREADY_RUNNING("command.start.already-running", "%prefix% §cThe game is already running"),
	COMMAND_START_ALREADY_STARTING("command.start.already-starting", "%prefix% §cThe game is already starting"),
	COMMAND_START_NOT_ENOUGH_PLAYERS("command.start.not-enough-players", "%prefix% §cThere are not enough players in the game"),
	COMMAND_START_SKIPPED("command.start.skipped", "%prefix% §aCountdown skipped"),
	COMMAND_LIST_TITLE("command.list.title", "%prefix% §aAll Arenas:"),
	COMMAND_LIST_ENTRY("command.list.entry", "- %name% §7(%size%x, Powerups: %powerups%)"),
	COMMAND_HELP_UNKNOWN_COMMAND("command.help.unknown-command", "%prefix% §cUnknown command"),

	INGAME_KICK_EDIT("ingame.kick-edit", "%prefix% §cYou were kicked from the game because the arena is being edited"),
	INGAME_PLAYER_JOINED("ingame.player-joined", "%prefix% §7%player% §ajoined the game §8[§7%currplayers%/%maxplayers%§8]"),
	INGAME_PLAYER_LEFT("ingame.player-left", "%prefix% §7%player% §cleft the game §8[§7%currplayers%/%maxplayers%§8]"),
	INGAME_COUNTDOWN_START("ingame.countdown.start", "%prefix% §aThe game starts in §7%countdown% §asecond(s)"),
	INGAME_COUNTDOWN_GO("ingame.countdown.go", "%prefix% §aGo!"),
	INGAME_COUNTDOWN_END_MINUTES("ingame.countdown.end.minutes", "%prefix% §6The game ends in §7%countdown% §6minute(s)"),
	INGAME_COUNTDOWN_END_SECONDS("ingame.countdown.end.seconds", "%prefix% §6The game ends in §7%countdown% §6second(s)"),
	INGAME_END_TIE("ingame.end.tie", "%prefix% §cLooks like no one won. §6It's a tie!"),
	INGAME_END_WIN("ingame.end.win", "%prefix% §7%winner% §awon the game!"),
	INGAME_COUNTDOWN_STOPPED("ingame.countdown-stopped", "%prefix% §cThe countdown was stopped"),
	INGAME_KICK_EVERYONE_LEFT("ingame.kick-everyone-left", "%prefix% §cYou were kicked from the game because all your opponents left"),
	INGAME_ARENA_LOADING_1("ingame.arena-loading.1", "%prefix% §6Loading arena..."),
	INGAME_ARENA_LOADING_2("ingame.arena-loading.2", "%prefix% §6Expected loading time: %time%"),
	INGAME_CANNOT_PASS("ingame.cannot-pass", "%prefix% §cYou cannot pass through that wall"),
	INGAME_CANNOT_CREATE_BARRIER("ingame.cannot-create-barrier", "%prefix% §cThere is already a wall behind you"),
	INGAME_KICK_RESTART("ingame.kick-restart", "%prefix% §cYou were kicked from the game because the plugin is restarting"),

	OTHER_NOT_INGAME("other.not-ingame", "%prefix% §cYou are currently not ingame"),
	OTHER_ALREADY_INGAME("other.already-ingame", "%prefix% §cYou are already in a game"),
	OTHER_GAME_RUNNING("other.game-running", "%prefix% §cThat game is already running"),
	OTHER_GAME_RESTARTING("other.game-restarting", "%prefix% §cThat game is currently restarting"),
	OTHER_GAME_FULL("other.game-full", "%prefix% §cThat game is already full"),
	OTHER_GAME_LEFT("other.game-left", "%prefix% §aYou've left the game successfully"),

	GUI_VOTE_GAME_DURATION("gui.vote-game-duration", "§bVote for game duration"),
	GUI_GAME_DURATION_5_MINUTES("gui.game-duration.5-mins", "§75 Minutes"),
	GUI_GAME_DURATION_10_MINUTES("gui.game-duration.10-mins", "§710 Minutes"),
	GUI_GAME_DURATION_20_MINUTES("gui.game-duration.20-mins", "§720 Minutes"),
	GUI_GAME_DURATION_INFINITE("gui.game-duration.infinite", "§7No time limit"),
	GUI_VOTE_MAZE_LAYOUT("gui.vote-maze-layout", "§bVote for maze layout"),
	GUI_MAZE_LAYOUT_RANDOM("gui.maze-layout.random", "§7Random"),
	GUI_MAZE_LAYOUT_NOT_AVAILABLE("gui.maze-layout.not-available", "§cThat option is not available"),
	GUI_VOTING_MAZE_DISABLED("gui.voting-maze-disabled", "§cLayout voting is disabled on this map"),
	
	SIGN_JOIN_LINE_1("sign.join.line.1", "§8[§6Crazy§5Maze§8]"),
	SIGN_JOIN_LINE_2("sign.join.line.2", "%name% §8(%size%x)"),
	SIGN_JOIN_LINE_3_POWERUPS("sign.join.line.3.powerups", "§5P §8[§7%players%/%min-players%/%max-players%§8]"),
	SIGN_JOIN_LINE_3_NO_POWERUPS("sign.join.line.3.no-powerups", "§aV §8[§7%players%/%min-players%/%max-players%§8]"),
	SIGN_JOIN_LINE_4_WAITING("sign.join.line.4.waiting", "§7Waiting..."),
	SIGN_JOIN_LINE_4_RUNNING("sign.join.line.4.running", "§aRunning"),
	SIGN_JOIN_LINE_4_RESTARTING("sign.join.line.4.restarting", "§cRestarting..."),

	SIGN_FINISH_LINE_1("sign.finish.line.1", "§8[§6Crazy§5Maze§8]"),
	SIGN_FINISH_LINE_2("sign.finish.line.2", "§aFinish"),
	;
	
	private final String
		path,
		fallback;
	
	private Message(String path, String fallback) {
		this.path = path;
		this.fallback = fallback;
	}
	
	public String getPath() {
		return path;
	}
	
	public String getFallback() {
		return fallback;
	}

}
