package com.goldenraspberry.application.usecase;

import com.goldenraspberry.application.dto.ProducerIntervalDto;
import com.goldenraspberry.application.dto.ProducerIntervalResponseDto;
import com.goldenraspberry.common.annotation.UseCase;
import com.goldenraspberry.domain.model.ProducerInterval;
import com.goldenraspberry.domain.port.ProducerIntervalService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Caso de uso para obter intervalos
 * Retorna os intervalos minimos e maximos entre premios consecutivos
 */
@UseCase
public class GetProducerIntervalsUseCase {

    private final ProducerIntervalService producerIntervalService;

    @Autowired
    public GetProducerIntervalsUseCase(ProducerIntervalService producerIntervalService) {
        this.producerIntervalService = producerIntervalService;
    }

    /**
     * Executa o caso de uso para obter intervalos
     * @return DTO com intervalos minimos e maximos
     */
    public ProducerIntervalResponseDto execute() {
        Map<String, List<ProducerInterval>> intervals = producerIntervalService.getMinMaxIntervals();

        List<ProducerIntervalDto> minIntervals = convertToDto(intervals.get("min"));
        List<ProducerIntervalDto> maxIntervals = convertToDto(intervals.get("max"));

        return new ProducerIntervalResponseDto(minIntervals, maxIntervals);
    }

    /**
     * Converte lista de ProducerInterval para DTO
     * @param intervals Lista de intervalos do dominio
     * @return Lista de DTOs
     */
    private List<ProducerIntervalDto> convertToDto(List<ProducerInterval> intervals) {
        if (intervals == null) {
            return List.of();
        }

        return intervals.stream()
                .map(interval -> new ProducerIntervalDto(
                        interval.getProducer().getName(),
                        interval.getInterval(),
                        interval.getPreviousWin().getValue(),
                        interval.getFollowingWin().getValue()
                ))
                .collect(Collectors.toList());
    }
}
