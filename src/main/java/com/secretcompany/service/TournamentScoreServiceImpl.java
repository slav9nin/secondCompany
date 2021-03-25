package com.secretcompany.service;

import com.google.common.collect.Iterables;
import com.secretcompany.dto.PlayStats;
import com.secretcompany.exception.TeamCountUnexpectedValueException;
import org.apache.commons.collections4.IterableUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

public class TournamentScoreServiceImpl implements TournamentScoreService<PlayStats> {

    private final Comparator<PlayStats> comparator;

    public TournamentScoreServiceImpl() {
        this.comparator = new PlayerStatComparator();
    }

    @Override
    public Optional<String> determineWinnerTeam(List<PlayStats> playerStats) {
        if (isEmpty(playerStats)) {
            throw new IllegalArgumentException("playStats should not be empty");
        }

        // assume teamName is not null
        Map<String, Integer> teamToScore = playerStats.stream()
                .collect(Collectors.groupingBy(PlayStats::getTeam, mapping(PlayStats::getGoals, Collectors.summingInt(score -> score))));

        // according to football match, we have to check there are two teams only.
        // if it's unnecessary -> just drop this condition and test
        if (teamToScore.size() != 2) {
            // we should have two teams to determine winner, otherwise throw Exception
            throw new TeamCountUnexpectedValueException();
        }

        //determine max score through all teams
        Integer max = teamToScore.values().stream()
                .reduce(0, BinaryOperator.maxBy(Comparator.comparingInt(score -> score)));

        // Short pass. If max score is 0, we don't have the winner.
        if (Objects.equals(max, 0)) {
            return Optional.empty();
        }

        // if more then 1 team have the same score -> returns Optional.empty
        Map<String, Integer> results = teamToScore.entrySet().stream()
                .filter(entry -> Objects.equals(entry.getValue(), max))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (Objects.equals(results.size(), 1)) {
            return results.keySet().stream().findFirst();
        } else {
            return Optional.empty();
        }
    }

    /**
     * // We didn't have time to discuss the rules, so my guesses the following
     *         // 1. EmptyList -> exception is preferable, notify that we pass incorrect values
     *         // 2. More that 1 player has the same scores -> Optional.empty
     *         // 3. Player scores: Goals + assists. Goals is the main metric,
     *         //       if has the same goal score, look at assist score, if the same -> Optional.empty

     */
    @Override
    public Optional<String> determineBeastPlayerTeam(List<PlayStats> playerStats) {
        if (isEmpty(playerStats)) {
            throw new IllegalArgumentException("playStats should not be empty");
        }

        // assume playerName is not null
        List<PlayStats> result = playerStats.stream()
                .sorted(comparator)
                .collect(toList());

        //should not be null
        PlayStats possibleOne = getFirst(result);

        long count = result.stream()
                .filter(ps -> Objects.equals(ps.getGoals(), possibleOne.getGoals()))
                .filter(ps -> Objects.equals(ps.getAssists(), possibleOne.getAssists()))
                .count();

        //more than 1 winner
        if (count != 1) {
            return Optional.empty();
        }

        return Optional.of(possibleOne)
                .map(PlayStats::getPlayerName);
    }

    public static <T> T getFirst(Iterable<T> iterable) {
        if (IterableUtils.isEmpty(iterable)) {
            return null;
        }
        return Iterables.getFirst(iterable, null);
    }

    private static class PlayerStatComparator implements Comparator<PlayStats> {

        @Override
        public int compare(PlayStats o1, PlayStats o2) {
            return comparing(PlayStats::getGoals, Comparator.nullsLast(Comparator.reverseOrder()))
                    .thenComparing(PlayStats::getAssists, Comparator.nullsLast(Comparator.reverseOrder()))
                    .compare(o1, o2);
        }
    }
}
