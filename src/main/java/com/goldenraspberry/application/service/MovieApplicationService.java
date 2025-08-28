package com.goldenraspberry.application.service;

import com.goldenraspberry.application.dto.MovieDto;
import com.goldenraspberry.application.dto.MovieInputDto;
import com.goldenraspberry.application.dto.PagedResponseDto;
import com.goldenraspberry.application.dto.ProducerIntervalResponseDto;
import com.goldenraspberry.application.usecase.CreateMovieUseCase;
import com.goldenraspberry.application.usecase.DeleteMovieUseCase;
import com.goldenraspberry.application.usecase.GetAllMoviesPagedUseCase;
import com.goldenraspberry.application.usecase.GetAllMoviesUseCase;
import com.goldenraspberry.application.usecase.GetMovieByIdUseCase;
import com.goldenraspberry.application.usecase.GetProducerIntervalsUseCase;
import com.goldenraspberry.application.usecase.GetWinnerMoviesUseCase;
import com.goldenraspberry.application.usecase.UpdateMovieUseCase;
import com.goldenraspberry.common.exception.BusinessException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Servico de aplicacao para coordenar operacoes de filmes Casos de uso e tratamento de excecoes */
@Service
public class MovieApplicationService {

  private final GetAllMoviesUseCase getAllMoviesUseCase;
  private final GetAllMoviesPagedUseCase getAllMoviesPagedUseCase;
  private final GetWinnerMoviesUseCase getWinnerMoviesUseCase;
  private final GetProducerIntervalsUseCase getProducerIntervalsUseCase;
  private final CreateMovieUseCase createMovieUseCase;
  private final UpdateMovieUseCase updateMovieUseCase;
  private final DeleteMovieUseCase deleteMovieUseCase;
  private final GetMovieByIdUseCase getMovieByIdUseCase;

  @Autowired
  public MovieApplicationService(
      GetAllMoviesUseCase getAllMoviesUseCase,
      GetAllMoviesPagedUseCase getAllMoviesPagedUseCase,
      GetWinnerMoviesUseCase getWinnerMoviesUseCase,
      GetProducerIntervalsUseCase getProducerIntervalsUseCase,
      CreateMovieUseCase createMovieUseCase,
      UpdateMovieUseCase updateMovieUseCase,
      DeleteMovieUseCase deleteMovieUseCase,
      GetMovieByIdUseCase getMovieByIdUseCase) {
    this.getAllMoviesUseCase = getAllMoviesUseCase;
    this.getAllMoviesPagedUseCase = getAllMoviesPagedUseCase;
    this.getWinnerMoviesUseCase = getWinnerMoviesUseCase;
    this.getProducerIntervalsUseCase = getProducerIntervalsUseCase;
    this.createMovieUseCase = createMovieUseCase;
    this.updateMovieUseCase = updateMovieUseCase;
    this.deleteMovieUseCase = deleteMovieUseCase;
    this.getMovieByIdUseCase = getMovieByIdUseCase;
  }

  /**
   * Obtem todos os filmes do sistema
   *
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
   * Obtem todos os filmes do sistema com paginacao
   *
   * @param page Numero da pagina (comeca em 0)
   * @param size Tamanho da pagina
   * @param sort Campo para ordenacao (opcional)
   * @return Resposta paginada com filmes
   * @throws BusinessException se ocorrer erro de negocio
   */
  public PagedResponseDto<MovieDto> getAllMoviesPaged(int page, int size, String sort) {
    try {
      return getAllMoviesPagedUseCase.execute(page, size, sort);
    } catch (Exception e) {
      throw new BusinessException("Erro ao obter filmes paginados: " + e.getMessage(), e);
    }
  }

  /**
   * Obtem apenas filmes vencedores
   *
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
   * Obtem intervalos de produtores entre premios consecutivos Retorna todos os empates para cada
   * categoria (min/max)
   *
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

  /**
   * Cria um novo filme
   *
   * @param movieInputDto Dados do filme a ser criado
   * @return Filme criado
   * @throws BusinessException se ocorrer erro de negocio
   */
  public MovieDto createMovie(MovieInputDto movieInputDto) {
    try {
      return createMovieUseCase.execute(movieInputDto);
    } catch (Exception e) {
      throw new BusinessException("Erro ao criar filme: " + e.getMessage(), e);
    }
  }

  /**
   * Busca um filme por ID
   *
   * @param id ID do filme
   * @return Filme encontrado
   * @throws BusinessException se ocorrer erro de negocio
   */
  public MovieDto getMovieById(Long id) {
    try {
      return getMovieByIdUseCase.execute(id);
    } catch (Exception e) {
      throw new BusinessException("Erro ao buscar filme por ID: " + e.getMessage(), e);
    }
  }

  /**
   * Atualiza um filme existente
   *
   * @param id ID do filme a ser atualizado
   * @param movieInputDto Novos dados do filme
   * @return Filme atualizado
   * @throws BusinessException se ocorrer erro de negocio
   */
  public MovieDto updateMovie(Long id, MovieInputDto movieInputDto) {
    try {
      return updateMovieUseCase.execute(id, movieInputDto);
    } catch (Exception e) {
      throw new BusinessException("Erro ao atualizar filme: " + e.getMessage(), e);
    }
  }

  /**
   * Deleta um filme
   *
   * @param id ID do filme a ser deletado
   * @throws BusinessException se ocorrer erro de negocio
   */
  public void deleteMovie(Long id) {
    try {
      deleteMovieUseCase.execute(id);
    } catch (Exception e) {
      throw new BusinessException("Erro ao deletar filme: " + e.getMessage(), e);
    }
  }
}
