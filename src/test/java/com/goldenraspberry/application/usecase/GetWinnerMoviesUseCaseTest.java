package com.goldenraspberry.application.usecase;

import com.goldenraspberry.application.dto.MovieDto;
import com.goldenraspberry.domain.model.Movie;
import com.goldenraspberry.domain.model.Producer;
import com.goldenraspberry.domain.model.Year;
import com.goldenraspberry.domain.port.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Testes de integracao para GetWinnerMoviesUseCase
 * Valida o fluxo completo de obtencao de filmes vencedores
 */
@ExtendWith(MockitoExtension.class)
class GetWinnerMoviesUseCaseTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private GetWinnerMoviesUseCase useCase;

    private Movie winnerMovie1;
    private Movie winnerMovie2;

    @BeforeEach
    void setUp() {
        List<Producer> producers1 = List.of(new Producer("Allan Carr"));
        List<Producer> producers2 = List.of(new Producer("Joel Silver"));

        winnerMovie1 = new Movie(
                1L,
                new Year(1980),
                "Can't Stop the Music",
                "Associated Film Distribution",
                producers1,
                true
        );

        winnerMovie2 = new Movie(
                2L,
                new Year(1990),
                "The Adventures of Ford Fairlane",
                "20th Century Fox",
                producers2,
                true
        );
    }

    @Test
    void shouldReturnWinnerMoviesSuccessfully() {
        // Given
        List<Movie> winnerMovies = List.of(winnerMovie1, winnerMovie2);
        when(movieRepository.findAllWinners()).thenReturn(winnerMovies);

        // When
        List<MovieDto> result = useCase.execute();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        // Validar primeiro filme vencedor
        MovieDto dto1 = result.get(0);
        assertEquals(1L, dto1.getId());
        assertEquals(1980, dto1.getYear());
        assertEquals("Can't Stop the Music", dto1.getTitle());
        assertEquals("Associated Film Distribution", dto1.getStudios());
        assertEquals(List.of("Allan Carr"), dto1.getProducers());
        assertTrue(dto1.getWinner());

        // Validar segundo filme vencedor
        MovieDto dto2 = result.get(1);
        assertEquals(2L, dto2.getId());
        assertEquals(1990, dto2.getYear());
        assertEquals("The Adventures of Ford Fairlane", dto2.getTitle());
        assertEquals("20th Century Fox", dto2.getStudios());
        assertEquals(List.of("Joel Silver"), dto2.getProducers());
        assertTrue(dto2.getWinner());
    }

    @Test
    void shouldReturnEmptyListWhenNoWinnerMovies() {
        // Given
        when(movieRepository.findAllWinners()).thenReturn(List.of());

        // When
        List<MovieDto> result = useCase.execute();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldOnlyReturnWinnerMovies() {
        // Given
        List<Movie> winnerMovies = List.of(winnerMovie1);
        when(movieRepository.findAllWinners()).thenReturn(winnerMovies);

        // When
        List<MovieDto> result = useCase.execute();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

        // Todos os filmes retornados devem ser vencedores
        result.forEach(movie -> assertTrue(movie.getWinner()));
    }

    @Test
    void shouldHandleWinnerMovieWithMultipleProducers() {
        // Given
        List<Producer> multipleProducers = List.of(
                new Producer("Producer A"),
                new Producer("Producer B")
        );

        Movie winnerWithMultipleProducers = new Movie(
                3L,
                new Year(1985),
                "Winner Movie",
                "Test Studios",
                multipleProducers,
                true
        );

        when(movieRepository.findAllWinners()).thenReturn(List.of(winnerWithMultipleProducers));

        // When
        List<MovieDto> result = useCase.execute();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

        MovieDto dto = result.get(0);
        assertEquals(2, dto.getProducers().size());
        assertTrue(dto.getProducers().contains("Producer A"));
        assertTrue(dto.getProducers().contains("Producer B"));
        assertTrue(dto.getWinner());
    }
}
