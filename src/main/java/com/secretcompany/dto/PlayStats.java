package com.secretcompany.dto;

import java.util.Objects;

/**
 * Immutable one
 */
public class PlayStats {
    private final int playerNumber;
    private final String playerName;
    private final String team;
    private final int goals;
    private final int assists;

    //Assume all provided value is non null. @NonNull
    public PlayStats(int playerNumber, String playerName, String team, int goals, int assists) {
        this.playerNumber = playerNumber;
        this.playerName = playerName;
        this.team = team;
        this.goals = goals;
        this.assists = assists;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getTeam() {
        return team;
    }

    public int getGoals() {
        return goals;
    }

    public int getAssists() {
        return assists;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayStats playStats = (PlayStats) o;
        return playerNumber == playStats.playerNumber && goals == playStats.goals && assists == playStats.assists && playerName.equals(playStats.playerName) && team.equals(playStats.team);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerNumber, playerName, team, goals, assists);
    }

    //    public static class Builder {
////        ...
//    }

}
