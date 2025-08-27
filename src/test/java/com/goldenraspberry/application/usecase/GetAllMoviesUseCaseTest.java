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
 * Testes de integracao para GetAllMoviesUseCase
 * Valida o fluxo completo de obtencao de todos os filmes
 */
@ExtendWith(MockitoExtension.class)
class GetAllMoviesUseCaseTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private GetAllMoviesUseCase useCase;

    private Movie movie1;
    private Movie movie2;

    @BeforeEach
    void setUp() {
        List<Producer> producers1 = List.of(new Producer("Allan Carr"));
        List<Producer> producers2 = List.of(new Producer("Bo Derek"));

        movie1 = new Movie(
                1L,
                new Year(1980),
                "Can't Stop the Music",
                "Associated Film Distribution",
                producers1,
                true
        );

        movie2 = new Movie(
                2L,
                new Year(1981),
                "Mommie Dearest",
                "Paramount Pictures",
                producers2,
                false
        );
    }

    @Test
    void shouldReturnAllMoviesSuccessfully() {
        // Given
        List<Movie> movies = List.of(movie1, movie2);
        when(movieRepository.findAll()).thenReturn(movies);

        // When
        List<MovieDto> result = useCase.execute();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        // Validar primeiro filme
        MovieDto dto1 = result.get(0);
        assertEquals(1L, dto1.getId());
        assertEquals(1980, dto1.getYear());
        assertEquals("Can't Stop the Music", dto1.getTitle());
        assertEquals("Associated Film Distribution", dto1.getStudios());
        assertEquals(List.of("Allan Carr"), dto1.getProducers());
        assertTrue(dto1.getWinner());

        // Validar segundo filme
        MovieDto dto2 = result.get(1);
        assertEquals(2L, dto2.getId());
        assertEquals(1981, dto2.getYear());
        assertEquals("Mommie Dearest", dto2.getTitle());
        assertEquals("Paramount Pictures", dto2.getStudios());
        assertEquals(List.of("Bo Derek"), dto2.getProducers());
        assertFalse(dto2.getWinner());
    }

    @Test
    void shouldReturnEmptyListWhenNoMovies() {
        // Given
        when(movieRepository.findAll()).thenReturn(List.of());

        // When
        List<MovieDto> result = useCase.execute();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldHandleMovieWithMultipleProducers() {
        // Given
        List<Producer> multipleProducers = List.of(
                new Producer("Producer 1"),
                new Producer("Producer 2"),
                new Producer("Producer 3")
        );

        Movie movieWithMultipleProducers = new Movie(
                3L,
                new Year(1982),
                "Test Movie",
                "Test Studios",
                multipleProducers,
                true
        );

        when(movieRepository.findAll()).thenReturn(List.of(movieWithMultipleProducers));

        // When
        List<MovieDto> result = useCase.execute();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

        MovieDto dto = result.get(0);
        assertEquals(3, dto.getProducers().size());
        assertTrue(dto.getProducers().contains("Producer 1"));
        assertTrue(dto.getProducers().contains("Producer 2"));
        assertTrue(dto.getProducers().contains("Producer 3"));
    }
}
