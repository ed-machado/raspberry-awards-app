package com.goldenraspberry.domain.service;

import com.goldenraspberry.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProducerIntervalCalculator Domain Tests")
class ProducerIntervalCalculatorTest {

    private ProducerIntervalCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new ProducerIntervalCalculator();
    }

    @Test
    @DisplayName("Should return empty map when no movies provided")
    void shouldReturnEmptyMapWhenNoMovies() {
        // When
        Map<String, List<ProducerInterval>> result = calculator.calculateAllIntervals(Collections.emptyList());

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty map when null movies provided")
    void shouldReturnEmptyMapWhenNullMovies() {
        // When
        Map<String, List<ProducerInterval>> result = calculator.calculateAllIntervals(null);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should calculate intervals for producer with multiple wins")
    void shouldCalculateIntervalsForProducerWithMultipleWins() {
        // Given
        Producer producer = new Producer("Test Producer");

        Movie movie1 = new Movie(1L, new Year(1980), "Movie 1", "Studio",
            Collections.singletonList(producer), true);
        Movie movie2 = new Movie(2L, new Year(1984), "Movie 2", "Studio",
            Collections.singletonList(producer), true);
        Movie movie3 = new Movie(3L, new Year(1990), "Movie 3", "Studio",
            Collections.singletonList(producer), true);

        List<Movie> movies = Arrays.asList(movie1, movie2, movie3);

        // When
        Map<String, List<ProducerInterval>> result = calculator.calculateAllIntervals(movies);

        // Then
        assertEquals(1, result.size());
        assertTrue(result.containsKey("Test Producer"));

        List<ProducerInterval> intervals = result.get("Test Producer");
        assertEquals(2, intervals.size());

        // First interval: 1980 -> 1984 (4 years)
        ProducerInterval interval1 = intervals.get(0);
        assertEquals(4, interval1.getInterval());
        assertEquals(1980, interval1.getPreviousWin().getValue());
        assertEquals(1984, interval1.getFollowingWin().getValue());

        // Second interval: 1984 -> 1990 (6 years)
        ProducerInterval interval2 = intervals.get(1);
        assertEquals(6, interval2.getInterval());
        assertEquals(1984, interval2.getPreviousWin().getValue());
        assertEquals(1990, interval2.getFollowingWin().getValue());
    }

    @Test
    @DisplayName("Should not include producers with single win")
    void shouldNotIncludeProducersWithSingleWin() {
        // Given
        Producer producer1 = new Producer("Single Win Producer");
        Producer producer2 = new Producer("Multiple Win Producer");

        Movie movie1 = new Movie(1L, new Year(1980), "Movie 1", "Studio",
            Collections.singletonList(producer1), true);
        Movie movie2 = new Movie(2L, new Year(1984), "Movie 2", "Studio",
            Collections.singletonList(producer2), true);
        Movie movie3 = new Movie(3L, new Year(1990), "Movie 3", "Studio",
            Collections.singletonList(producer2), true);

        List<Movie> movies = Arrays.asList(movie1, movie2, movie3);

        // When
        Map<String, List<ProducerInterval>> result = calculator.calculateAllIntervals(movies);

        // Then
        assertEquals(1, result.size());
        assertTrue(result.containsKey("Multiple Win Producer"));
        assertFalse(result.containsKey("Single Win Producer"));
    }

    @Test
    @DisplayName("Should not include non-winning movies")
    void shouldNotIncludeNonWinningMovies() {
        // Given
        Producer producer = new Producer("Test Producer");

        Movie winningMovie1 = new Movie(1L, new Year(1980), "Winner 1", "Studio",
            Collections.singletonList(producer), true);
        Movie nonWinningMovie = new Movie(2L, new Year(1982), "Non Winner", "Studio",
            Collections.singletonList(producer), false);
        Movie winningMovie2 = new Movie(3L, new Year(1984), "Winner 2", "Studio",
            Collections.singletonList(producer), true);

        List<Movie> movies = Arrays.asList(winningMovie1, nonWinningMovie, winningMovie2);

        // When
        Map<String, List<ProducerInterval>> result = calculator.calculateAllIntervals(movies);

        // Then
        assertEquals(1, result.size());
        List<ProducerInterval> intervals = result.get("Test Producer");
        assertEquals(1, intervals.size());

        // Should calculate interval between winning movies only (1980 -> 1984)
        ProducerInterval interval = intervals.get(0);
        assertEquals(4, interval.getInterval());
        assertEquals(1980, interval.getPreviousWin().getValue());
        assertEquals(1984, interval.getFollowingWin().getValue());
    }

    @Test
    @DisplayName("Should find minimum and maximum intervals correctly")
    void shouldFindMinMaxIntervalsCorrectly() {
        // Given
        Producer producer1 = new Producer("Producer 1");
        Producer producer2 = new Producer("Producer 2");

        // Producer 1: 1 year interval (min)
        Movie movie1 = new Movie(1L, new Year(1980), "Movie 1", "Studio",
            Collections.singletonList(producer1), true);
        Movie movie2 = new Movie(2L, new Year(1981), "Movie 2", "Studio",
            Collections.singletonList(producer1), true);

        // Producer 2: 10 year interval (max)
        Movie movie3 = new Movie(3L, new Year(1985), "Movie 3", "Studio",
            Collections.singletonList(producer2), true);
        Movie movie4 = new Movie(4L, new Year(1995), "Movie 4", "Studio",
            Collections.singletonList(producer2), true);

        List<Movie> movies = Arrays.asList(movie1, movie2, movie3, movie4);

        // When
        Map<String, List<ProducerInterval>> result = calculator.findMinMaxIntervals(movies);

        // Then
        assertEquals(2, result.size());
        assertTrue(result.containsKey("min"));
        assertTrue(result.containsKey("max"));

        List<ProducerInterval> minIntervals = result.get("min");
        assertEquals(1, minIntervals.size());
        assertEquals(1, minIntervals.get(0).getInterval());
        assertEquals("Producer 1", minIntervals.get(0).getProducer().getName());

        List<ProducerInterval> maxIntervals = result.get("max");
        assertEquals(1, maxIntervals.size());
        assertEquals(10, maxIntervals.get(0).getInterval());
        assertEquals("Producer 2", maxIntervals.get(0).getProducer().getName());
    }

    @Test
    @DisplayName("Should return empty lists when no intervals found")
    void shouldReturnEmptyListsWhenNoIntervalsFound() {
        // Given
        Producer producer = new Producer("Single Win Producer");
        Movie movie = new Movie(1L, new Year(1980), "Movie", "Studio",
            Collections.singletonList(producer), true);

        // When
        Map<String, List<ProducerInterval>> result = calculator.findMinMaxIntervals(
            Collections.singletonList(movie)
        );

        // Then
        assertEquals(2, result.size());
        assertTrue(result.get("min").isEmpty());
        assertTrue(result.get("max").isEmpty());
    }
}
