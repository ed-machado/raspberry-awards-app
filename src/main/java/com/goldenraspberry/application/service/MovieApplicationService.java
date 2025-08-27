package com.goldenraspberry.application.service;

import com.goldenraspberry.application.dto.MovieDto;
import com.goldenraspberry.application.dto.ProducerIntervalResponseDto;
import com.goldenraspberry.application.usecase.GetAllMoviesUseCase;
import com.goldenraspberry.application.usecase.GetProducerIntervalsUseCase;
import com.goldenraspberry.application.usecase.GetWinnerMoviesUseCase;
import com.goldenraspberry.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servico de aplicacao para coordenar operacoes de filmes
 * Casos de uso e tratamento de excecoes
 */
@Service
public class MovieApplicationService {

    private final GetAllMoviesUseCase getAllMoviesUseCase;
    private final GetWinnerMoviesUseCase getWinnerMoviesUseCase;
    private final GetProducerIntervalsUseCase getProducerIntervalsUseCase;

    @Autowired
    public MovieApplicationService(
            GetAllMoviesUseCase getAllMoviesUseCase,
            GetWinnerMoviesUseCase getWinnerMoviesUseCase,
            GetProducerIntervalsUseCase getProducerIntervalsUseCase) {
        this.getAllMoviesUseCase = getAllMoviesUseCase;
        this.getWinnerMoviesUseCase = getWinnerMoviesUseCase;
        this.getProducerIntervalsUseCase = getProducerIntervalsUseCase;
    }

    /**
     * Obtem todos os filmes do sistema
     * @return Lista de filmes
     * @throws BusinessException se ocorrer erro de negocio
     */
    public List<MovieDto> getAllMovies() {
        try {
            return getAllMoviesUseCase.execute();
        } catch (Exception e) {
            throw new BusinessException("Erro ao obter todos os filmes: " + e.getMessage(), e);
        }
    }

    /**
     * Obtem apenas filmes vencedores
     * @return Lista de filmes vencedores
     * @throws BusinessException se ocorrer erro de negocio
     */
    public List<MovieDto> getWinnerMovies() {
        try {
            return getWinnerMoviesUseCase.execute();
        } catch (Exception e) {
            throw new BusinessException("Erro ao obter filmes vencedores: " + e.getMessage(), e);
        }
    }

    /**
     * Obtem intervalos de produtores entre premios consecutivos
     * @return Intervalos minimos e maximos
     * @throws BusinessException se ocorrer erro de negocio
     */
    public ProducerIntervalResponseDto getProducerIntervals() {
        try {
            return getProducerIntervalsUseCase.execute();
        } catch (Exception e) {
            throw new BusinessException("Erro ao obter intervalos de produtores: " + e.getMessage(), e);
        }
    }
}
