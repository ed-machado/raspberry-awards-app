package com.goldenraspberry.application.service;

import com.goldenraspberry.application.dto.MovieDto;
import com.goldenraspberry.application.dto.ProducerIntervalResponseDto;
import com.goldenraspberry.application.usecase.GetAllMoviesUseCase;
import com.goldenraspberry.application.usecase.GetProducerIntervalsUseCase;
import com.goldenraspberry.application.usecase.GetWinnerMoviesUseCase;
import com.goldenraspberry.common.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes de integracao para MovieApplicationService
 * Valida orquestracao entre casos de uso e tratamento de excecoes
 */
@ExtendWith(MockitoExtension.class)
class MovieApplicationServiceTest {

    @Mock
    private GetAllMoviesUseCase getAllMoviesUseCase;

    @Mock
    private GetWinnerMoviesUseCase getWinnerMoviesUseCase;

    @Mock
    private GetProducerIntervalsUseCase getProducerIntervalsUseCase;

    @InjectMocks
    private MovieApplicationService applicationService;

    private MovieDto movieDto;
    private ProducerIntervalResponseDto intervalResponseDto;

    @BeforeEach
    void setUp() {
        movieDto = new MovieDto(
                1L,
                1980,
                "Test Movie",
                "Test Studios",
                List.of("Test Producer"),
                true
        );

        intervalResponseDto = new ProducerIntervalResponseDto(List.of(), List.of());
    }

    @Test
    void shouldGetAllMoviesSuccessfully() {
        // Given
        List<MovieDto> expectedMovies = List.of(movieDto);
        when(getAllMoviesUseCase.execute()).thenReturn(expectedMovies);

        // When
        List<MovieDto> result = applicationService.getAllMovies();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(movieDto, result.get(0));
        verify(getAllMoviesUseCase).execute();
    }

    @Test
    void shouldGetWinnerMoviesSuccessfully() {
        // Given
        List<MovieDto> expectedWinners = List.of(movieDto);
        when(getWinnerMoviesUseCase.execute()).thenReturn(expectedWinners);

        // When
        List<MovieDto> result = applicationService.getWinnerMovies();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(movieDto, result.get(0));
        verify(getWinnerMoviesUseCase).execute();
    }

    @Test
    void shouldGetProducerIntervalsSuccessfully() {
        // Given
        when(getProducerIntervalsUseCase.execute()).thenReturn(intervalResponseDto);

        // When
        ProducerIntervalResponseDto result = applicationService.getProducerIntervals();

        // Then
        assertNotNull(result);
        assertEquals(intervalResponseDto, result);
        verify(getProducerIntervalsUseCase).execute();
    }

    @Test
    void shouldThrowBusinessExceptionWhenGetAllMoviesFails() {
        // Given
        RuntimeException originalException = new RuntimeException("Database error");
        when(getAllMoviesUseCase.execute()).thenThrow(originalException);

        // When & Then
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> applicationService.getAllMovies()
        );

        assertTrue(exception.getMessage().contains("Erro ao obter todos os filmes"));
        assertTrue(exception.getMessage().contains("Database error"));
        assertEquals(originalException, exception.getCause());
    }

    @Test
    void shouldThrowBusinessExceptionWhenGetWinnerMoviesFails() {
        // Given
        RuntimeException originalException = new RuntimeException("Service error");
        when(getWinnerMoviesUseCase.execute()).thenThrow(originalException);

        // When & Then
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> applicationService.getWinnerMovies()
        );

        assertTrue(exception.getMessage().contains("Erro ao obter filmes vencedores"));
        assertTrue(exception.getMessage().contains("Service error"));
        assertEquals(originalException, exception.getCause());
    }

    @Test
    void shouldThrowBusinessExceptionWhenGetProducerIntervalsFails() {
        // Given
        RuntimeException originalException = new RuntimeException("Calculation error");
        when(getProducerIntervalsUseCase.execute()).thenThrow(originalException);

        // When & Then
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> applicationService.getProducerIntervals()
        );

        assertTrue(exception.getMessage().contains("Erro ao obter intervalos de produtores"));
        assertTrue(exception.getMessage().contains("Calculation error"));
        assertEquals(originalException, exception.getCause());
    }
}
