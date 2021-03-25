package com.secretcompany.service;

import java.util.List;
import java.util.Optional;

public interface TournamentScoreService<T> {

     Optional<String> determineWinnerTeam(List<T> playerStats);
     Optional<String> determineBeastPlayerTeam(List<T> playerStats);
}
