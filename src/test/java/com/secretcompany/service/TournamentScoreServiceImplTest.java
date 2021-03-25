package com.secretcompany.service;

import com.secretcompany.dto.PlayStats;
import com.secretcompany.exception.TeamCountUnexpectedValueException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TournamentScoreServiceImplTest {

    private static final String TEAM_FIRST = "TEAM_ONE";
    private static final String TEAM_SECOND = "TEAM_TWO";
    private static final String TEAM_THIRD = "TEAM_THIRD";

    private final TournamentScoreServiceImpl service = new TournamentScoreServiceImpl();

    // test determineWinnerTeam method

    @Test
    public void shouldThrowExceptionIfEmptyOrNull() {
        assertThatThrownBy(() -> service.determineWinnerTeam(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageMatching("playStats should not be empty");

        assertThatThrownBy(() -> service.determineWinnerTeam(emptyList()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageMatching("playStats should not be empty");
    }

    @Test
    public void shouldThrowExceptionIfPassMoreThan2TeamsOrLess() {
        final PlayStats first = new PlayStats(11, RandomStringUtils.randomAlphabetic(5), TEAM_FIRST, 1, 0);
        final PlayStats second = new PlayStats(12, RandomStringUtils.randomAlphabetic(5), TEAM_SECOND, 0, 0);
        final PlayStats third = new PlayStats(13, RandomStringUtils.randomAlphabetic(5), TEAM_THIRD, 2, 0);

        List<PlayStats> playStats = Arrays.asList(first, second, third);

        assertThatThrownBy(() -> service.determineWinnerTeam(playStats))
                .isInstanceOf(TeamCountUnexpectedValueException.class);

        assertThatThrownBy(() -> service.determineWinnerTeam(Collections.singletonList(first)))
                .isInstanceOf(TeamCountUnexpectedValueException.class);
    }

    //For coverage purpose. Should go through short pass.
    @Test
    public void name() {
        final PlayStats first = new PlayStats(12, RandomStringUtils.randomAlphabetic(5), TEAM_FIRST, 0, 0);
        final PlayStats second = new PlayStats(12, RandomStringUtils.randomAlphabetic(5), TEAM_FIRST, 0, 0);
        final PlayStats third = new PlayStats(12, RandomStringUtils.randomAlphabetic(5), TEAM_SECOND, 0, 0);
        final PlayStats fourth = new PlayStats(12, RandomStringUtils.randomAlphabetic(5), TEAM_SECOND, 0, 0);

        Optional<String> result = service.determineWinnerTeam(Arrays.asList(first, second, third, fourth));
        assertThat(result).isEmpty();
    }

    @Test
    public void shouldReturnCorrectWinner() {
        final PlayStats looserOne = new PlayStats(11, RandomStringUtils.randomAlphabetic(5), TEAM_FIRST, 1, 0);
        final PlayStats looserTwo = new PlayStats(12, RandomStringUtils.randomAlphabetic(5), TEAM_FIRST, 0, 0);
        final PlayStats looserThird = new PlayStats(13, RandomStringUtils.randomAlphabetic(5), TEAM_FIRST, 2, 0);

        final PlayStats winnerOne = new PlayStats(21, RandomStringUtils.randomAlphabetic(5), TEAM_SECOND, 1, 0);
        final PlayStats winnerTwo = new PlayStats(22, RandomStringUtils.randomAlphabetic(5), TEAM_SECOND, 3, 0);

        List<PlayStats> playStats = Arrays.asList(looserOne, looserTwo, looserThird, winnerOne, winnerTwo);

        Optional<String> winnerTeam = service.determineWinnerTeam(playStats);

        assertThat(winnerTeam).contains(TEAM_SECOND);
    }

    @Test
    public void shouldReturnEmptyResultIfHasTheSameResult() {
        final PlayStats looserOne = new PlayStats(11, RandomStringUtils.randomAlphabetic(5), TEAM_FIRST, 0, 0);
        final PlayStats looserTwo = new PlayStats(12, RandomStringUtils.randomAlphabetic(5), TEAM_FIRST, 0, 0);
        final PlayStats looserThird = new PlayStats(13, RandomStringUtils.randomAlphabetic(5), TEAM_FIRST, 2, 0);

        final PlayStats winnerOne = new PlayStats(21, RandomStringUtils.randomAlphabetic(5), TEAM_SECOND, 1, 0);
        final PlayStats winnerTwo = new PlayStats(22, RandomStringUtils.randomAlphabetic(5), TEAM_SECOND, 1, 0);

        List<PlayStats> playStats = Arrays.asList(looserOne, looserTwo, looserThird, winnerOne, winnerTwo);

        Optional<String> winnerTeam = service.determineWinnerTeam(playStats);

        assertThat(winnerTeam).isEmpty();
    }

    // test determineBeastPlayerTeam
    @Test
    public void shouldReturnBestPlayer() {
        final PlayStats first = new PlayStats(11, "first", TEAM_FIRST, 1, 0);
        final PlayStats second = new PlayStats(12, "second", TEAM_FIRST, 0, 0);
        final PlayStats third = new PlayStats(13, "third", TEAM_FIRST, 2, 0);

        Optional<String> result = service.determineBeastPlayerTeam(Arrays.asList(first, second, third));

        assertThat(result).contains(third.getPlayerName());
    }

    @Test
    public void shouldNotReturnBestPlayerDueToTheSameGoalScore() {
        final PlayStats first = new PlayStats(11, "first", TEAM_FIRST, 1, 0);
        final PlayStats second = new PlayStats(12, "second", TEAM_SECOND, 1, 0);
        final PlayStats third = new PlayStats(13, "third", TEAM_THIRD, 1, 0);

        Optional<String> result = service.determineBeastPlayerTeam(Arrays.asList(first, second, third));
        assertThat(result).isEmpty();
    }

    @Test
    public void shouldNotReturnBestPlayerDueToTheSameGoalAndAssistScore() {
        final PlayStats first = new PlayStats(11, "first", TEAM_FIRST, 1, 1);
        final PlayStats second = new PlayStats(12, "second", TEAM_SECOND, 1, 1);
        final PlayStats third = new PlayStats(13, "third", TEAM_THIRD, 1, 1);

        Optional<String> result = service.determineBeastPlayerTeam(Arrays.asList(first, second, third));
        assertThat(result).isEmpty();
    }

    @Test
    public void shouldReturnBestPlayerBasedOnGoalAndAssistScore() {
        final PlayStats first = new PlayStats(11, "first", TEAM_FIRST, 2, 1);
        final PlayStats second = new PlayStats(12, "second", TEAM_SECOND, 2, 1);
        final PlayStats third = new PlayStats(13, "third", TEAM_THIRD, 2, 2);

        Optional<String> result = service.determineBeastPlayerTeam(Arrays.asList(first, second, third));
        assertThat(result).contains(third.getPlayerName());
    }
}
