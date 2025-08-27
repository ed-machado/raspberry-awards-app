package com.goldenraspberry.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Movie Domain Tests")
class MovieTest {

    @Test
    @DisplayName("Should create valid movie with all required fields")
    void shouldCreateValidMovie() {
        // Given
        Long id = 1L;
        Year year = new Year(2020);
        String title = "Test Movie";
        String studios = "Test Studios";
        Producer producer1 = new Producer("Producer One");
        Producer producer2 = new Producer("Producer Two");
        boolean winner = true;

        // When
        Movie movie = new Movie(id, year, title, studios, Arrays.asList(producer1, producer2), winner);

        // Then
        assertEquals(id, movie.getId());
        assertEquals(year, movie.getYear());
        assertEquals(title, movie.getTitle());
        assertEquals(studios, movie.getStudios());
        assertEquals(2, movie.getProducers().size());
        assertTrue(movie.isWinner());
    }

    @Test
    @DisplayName("Should throw exception when title is null")
    void shouldThrowExceptionWhenTitleIsNull() {
        // Given
        Long id = 1L;
        Year year = new Year(2020);
        String studios = "Test Studios";
        Producer producer = new Producer("Producer");

        // When & Then
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> new Movie(id, year, null, studios, Collections.singletonList(producer), false)
        );

        assertEquals("Title nao pode ser nulo", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when title is empty")
    void shouldThrowExceptionWhenTitleIsEmpty() {
        // Given
        Long id = 1L;
        Year year = new Year(2020);
        String studios = "Test Studios";
        Producer producer = new Producer("Producer");

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Movie(id, year, "", studios, Collections.singletonList(producer), false)
        );

        assertEquals("Title nao pode ser vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when producers list is null")
    void shouldThrowExceptionWhenProducersIsNull() {
        // Given
        Long id = 1L;
        Year year = new Year(2020);
        String title = "Test Movie";
        String studios = "Test Studios";

        // When & Then
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> new Movie(id, year, title, studios, null, false)
        );

        assertEquals("Producers nao podem ser nulos", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when producers list is empty")
    void shouldThrowExceptionWhenProducersIsEmpty() {
        // Given
        Long id = 1L;
        Year year = new Year(2020);
        String title = "Test Movie";
        String studios = "Test Studios";

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Movie(id, year, title, studios, Collections.emptyList(), false)
        );

        assertEquals("Movie deve ter pelo menos um Producer", exception.getMessage());
    }

    @Test
    @DisplayName("Should implement equals and hashCode correctly")
    void shouldImplementEqualsAndHashCodeCorrectly() {
        // Given
        Producer producer = new Producer("Producer");
        Movie movie1 = new Movie(1L, new Year(2020), "Title", "Studios",
            Collections.singletonList(producer), true);
        Movie movie2 = new Movie(1L, new Year(2020), "Title", "Studios",
            Collections.singletonList(producer), true);
        Movie movie3 = new Movie(2L, new Year(2021), "Other Title", "Other Studios",
            Collections.singletonList(producer), false);

        // Then
        assertEquals(movie1, movie2);
        assertEquals(movie1.hashCode(), movie2.hashCode());
        assertNotEquals(movie1, movie3);
        assertNotEquals(movie1.hashCode(), movie3.hashCode());
    }

    @Test
    @DisplayName("Should return correct string representation")
    void shouldReturnCorrectStringRepresentation() {
        // Given
        Producer producer = new Producer("Test Producer");
        Movie movie = new Movie(1L, new Year(2020), "Test Movie", "Test Studios",
            Collections.singletonList(producer), true);

        // When
        String result = movie.toString();

        // Then
        assertTrue(result.contains("Movie{"));
        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("title='Test Movie'"));
        assertTrue(result.contains("winner=true"));
    }
}
