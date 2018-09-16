package com.cryo.modules.api;

import com.cryo.modules.index.IndexModule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.HashMap;

@AllArgsConstructor
@RequiredArgsConstructor
public enum APITypes {

    POSTDAO("PostDAO", new APIReturn[]{
            new APIReturn("id", "Integer", "ID of the post to look up."),
            new APIReturn("subject", "String", "Title or subject of the post."),
            new APIReturn("message", "String", "Message of the post."),
            new APIReturn("username", "String", "Username of the author."),
            new APIReturn("formattedName", "String", "Formatted name of the author"),
            new APIReturn("url", "String", "URL to the post."),
            new APIReturn("dateline", "Long", "Time of the post in milliseconds.")
    }),
    ONLINE_PLAYERS("OnlineInfoDAO", new APIReturn[]{
            new APIReturn("all", "Integer", "Total number of players currently online."),
            new APIReturn("staff", "Integer", "Number of staff currently online."),
            new APIReturn("contributors", "Integer", "Number of contributors currently online."),
            new APIReturn("sponsors", "Integer", "Number of sponsors currently online."),
            new APIReturn("regular", "Integer", "Number of regular players currently online.")
    }),
    MINIGAME_INFO("MinigameInfoDAO", new APIReturn[]{
            new APIReturn("name", "String", "Name of the minigame.")
    }, true, null),
    LAVA_FLOW_INFO("LavaFlowMine", new APIReturn[]{
            new APIReturn("row", "Integer", "Index of the row the blockage is on. 0=A,1=B,2=C,3=D"),
            new APIReturn("boiler", "Boolean", "Whether or not the boiler is broken. True=Broken")
    }, false, "MinigameInfoDAO"),
    ART_WORKSHOP_INFO("ArtWorkshop", new APIReturn[]{
            new APIReturn("armour", "String", "Which type of armour is currently active in the ancient armour section."),
            new APIReturn("ancestor", "Boolean", "Whether or not an ancestor is currently active in the workshop.")
    }, false, "MinigameInfoDAO"),
    SOUL_WARS("SoulWars", new APIReturn[]{
            new APIReturn("started", "Boolean", "Whether or not a game is currently ongoing."),
            new APIReturn("remaining", "String", "Formatted time remaining in game or until next game."),
            new APIReturn("east_control", "String", "Name of the team with control over the east graveyard."),
            new APIReturn("west_control", "String", "Name of the team with control over the west graveyard."),
            new APIReturn("obelisk_control", "String", "Name of the team with control over the obelisk."),
            new APIReturn("red", "SoulWarsTeamInfo", "Object containing information on the red team."),
            new APIReturn("blue", "SoulWarsTeamInfo", "Object containing information on the blue team.")
    }, false, "MinigameInfoDAO"),
    FIGHT_PITS("FightPits", new APIReturn[]{
            new APIReturn("started", "Boolean", "Whether or not a round of Fight Pits is currently ongoing."),
            new APIReturn("remaining", "Integer", "Number of seconds remaining in the round or until next round."),
            new APIReturn("champion", "String", "Name of the champion if active."),
            new APIReturn("lobby_size", "Integer", "Number of players in the lobby."),
            new APIReturn("game_size", "Integer", "Number of players in the game."),
            new APIReturn("lobby_players", "List<String>", "List of players in the lobby."),
            new APIReturn("game_players", "List<String>", "List of players in the game.")
    }, false, "MinigameInfoDAO"),
    CASTLE_WARS("CastleWars", new APIReturn[] {
            new APIReturn("started", "Boolean", "Whether or not a game of Soul Wars is currently ongoing."),
            new APIReturn("remaining", "String", "Formatted time remaining in game or until next game."),
            new APIReturn("door_health", "Integer", "Health of the door."),
            new APIReturn("red", "CastleWarsTeamInfo", "Object containing information on the red team."),
            new APIReturn("blue", "CastleWarsTeamInfo", "Object containing information on the blue team.")
    }, false, "MinigameInfoDAO"),
    SHOOTING_STAR("ShootingStar", new APIReturn[] {
            new APIReturn("alive", "Boolean", "Whether or not a shooting star is currently crashed."),
            new APIReturn("sprite_alive", "Boolean", "Whether or not a star sprite is currently alive."),
            new APIReturn("sprite_spawned", "Long", "Time the sprite was spawned, in milliseconds. (If sprite alive)"),
            new APIReturn("next_crash", "Long", "Time for next crsah, in milliseconds. Time is approximated for regular players."),
            new APIReturn("location", "String", "Name of the crash location.", 2),
            new APIReturn("tile", "String", "Tile of the crash location.", 2),
            new APIReturn("hint", "String", "Hint for the crash location."),
            new APIReturn("tagged", "Boolean", "Whether the star has been tagged or not."),
            new APIReturn("size", "Integer", "Current size of the star."),
            new APIReturn("charges", "Integer", "Number of charges remaining on the star.", 2)
    }, false, "MinigameInfoDAO"),
    CASTLE_WARS_TEAM("CastleWarsTeamInfo", new APIReturn[]{
            new APIReturn("name", "String", "Name of the team."),
            new APIReturn("size", "Integer", "Number of players on the team."),
            new APIReturn("score", "Integer", "Score of the team."),
            new APIReturn("status", "String", "Flag status for the team."),
            new APIReturn("barricades", "Integer", "Number of barricades the team has down."),
            new APIReturn("players", "List<String>", "List of players on the team.")
    }),
    SOUL_WARS_TEAM("SoulWarsTeamInfo", new APIReturn[]{
            new APIReturn("name", "String", "Name of the team."),
            new APIReturn("size", "Integer", "Size of the team."),
            new APIReturn("players", "List<String>", "List of the players in the team."),
            new APIReturn("avatar", "SoulWarsAvatarInfo", "Information on the team's avatar.")
    }),
    SOUL_WARS_AVATAR("SoulWarsAvatarInfo", new APIReturn[]{
            new APIReturn("level", "Integer", "Currently slayer level of the avatar."),
            new APIReturn("deaths", "Integer", "Number of times the avatar has died."),
            new APIReturn("alive", "Integer", "Whether or not the avatar is alive."),
            new APIReturn("hitpoints", "Integer", "Current hitpoints of the avatar.")
    }),
    COMMIT_INFO("CommitInfoDAO", new APIReturn[] {
            new APIReturn("date", "String", "Formatted date string of commit."),
            new APIReturn("author", "String", "Name of the committer."),
            new APIReturn("commit_message", "String", "Message for the commit."),
            new APIReturn("url", "String", "URL for the commit.")
    }),
    CREATION_TIME("CreationTimeDAO", new APIReturn[]{
            new APIReturn("formatted", "String", "Formatted string of the creation date."),
            new APIReturn("millis", "Long", "Time of the creation date in milliseconds.")
    });

    private @Getter
    final String name;
    private @Getter
    final APIReturn[] returns;

    private @Getter
    boolean abstractt;
    private @Getter
    String parentClass;

    private static @Getter
    HashMap<String, APITypes> types;

    static {
        types = new HashMap<>();
        Arrays.stream(APITypes.values()).forEach(type -> types.put(type.name, type));
    }


}
