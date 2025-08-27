package com.goldenraspberry.application.usecase;

import com.goldenraspberry.application.dto.ProducerIntervalResponseDto;
import com.goldenraspberry.domain.model.Producer;
import com.goldenraspberry.domain.model.ProducerInterval;
import com.goldenraspberry.domain.model.Year;
import com.goldenraspberry.domain.port.ProducerIntervalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Testes de integracao para GetProducerIntervalsUseCase
 * Valida o fluxo completo de obtencao de intervalos de produtores
 */
@ExtendWith(MockitoExtension.class)
class GetProducerIntervalsUseCaseTest {

    @Mock
    private ProducerIntervalService producerIntervalService;

    @InjectMocks
    private GetProducerIntervalsUseCase useCase;

    private ProducerInterval minInterval;
    private ProducerInterval maxInterval;

    @BeforeEach
    void setUp() {
        Producer producer1 = new Producer("Joel Silver");
        Producer producer2 = new Producer("Bo Derek");

        minInterval = new ProducerInterval(
                producer1,
                1,
                new Year(1990),
                new Year(1991)
        );

        maxInterval = new ProducerInterval(
                producer2,
                13,
                new Year(1984),
                new Year(1997)
        );
    }

    @Test
    void shouldReturnProducerIntervalsSuccessfully() {
        // Given
        Map<String, List<ProducerInterval>> intervals = Map.of(
                "min", List.of(minInterval),
                "max", List.of(maxInterval)
        );

        when(producerIntervalService.getMinMaxIntervals()).thenReturn(intervals);

        // When
        ProducerIntervalResponseDto result = useCase.execute();

        // Then
        assertNotNull(result);
        assertNotNull(result.getMin());
        assertNotNull(result.getMax());

        assertEquals(1, result.getMin().size());
        assertEquals(1, result.getMax().size());

        // Validar intervalo minimo
        assertEquals("Joel Silver", result.getMin().get(0).getProducer());
        assertEquals(1, result.getMin().get(0).getInterval());
        assertEquals(1990, result.getMin().get(0).getPreviousWin());
        assertEquals(1991, result.getMin().get(0).getFollowingWin());

        // Validar intervalo maximo
        assertEquals("Bo Derek", result.getMax().get(0).getProducer());
        assertEquals(13, result.getMax().get(0).getInterval());
        assertEquals(1984, result.getMax().get(0).getPreviousWin());
        assertEquals(1997, result.getMax().get(0).getFollowingWin());
    }

    @Test
    void shouldReturnEmptyListsWhenNoIntervals() {
        // Given
        Map<String, List<ProducerInterval>> intervals = Map.of(
                "min", List.of(),
                "max", List.of()
        );

        when(producerIntervalService.getMinMaxIntervals()).thenReturn(intervals);

        // When
        ProducerIntervalResponseDto result = useCase.execute();

        // Then
        assertNotNull(result);
        assertTrue(result.getMin().isEmpty());
        assertTrue(result.getMax().isEmpty());
    }

    @Test
    void shouldHandleNullIntervals() {
        // Given
        Map<String, List<ProducerInterval>> intervals = Map.of(
                "min", List.of(),
                "max", List.of()
        );

        when(producerIntervalService.getMinMaxIntervals()).thenReturn(intervals);

        // When
        ProducerIntervalResponseDto result = useCase.execute();

        // Then
        assertNotNull(result);
        assertNotNull(result.getMin());
        assertNotNull(result.getMax());
    }
}
