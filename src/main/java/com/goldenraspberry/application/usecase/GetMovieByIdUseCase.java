package com.goldenraspberry.application.usecase;

import com.goldenraspberry.application.dto.MovieDto;
import com.goldenraspberry.common.annotation.UseCase;
import com.goldenraspberry.domain.model.Movie;
import com.goldenraspberry.domain.model.Producer;
import com.goldenraspberry.domain.port.MovieRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;

/** Caso de uso para obter um filme por ID Busca e retorna um filme especifico */
@UseCase
public class GetMovieByIdUseCase {

  private final MovieRepository movieRepository;

  @Autowired
  public GetMovieByIdUseCase(MovieRepository movieRepository) {
    this.movieRepository = movieRepository;
  }

  /**
   * Executa o caso de uso para obter um filme por ID
   *
   * @param id ID do filme a ser buscado
   * @return DTO do filme encontrado
   * @throws IllegalArgumentException se o filme nao for encontrado
   */
  public MovieDto execute(Long id) {
    Movie movie =
        movieRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        String.format("Filme com ID %d não encontrado", id)));

    return convertToDto(movie);
  }

  /**
   * Converte Movie do dominio para DTO de resposta
   *
   * @param movie Entidade do dominio
   * @return DTO do filme
   */
  private MovieDto convertToDto(Movie movie) {
    List<String> producerNames =
        movie.getProducers().stream().map(Producer::getName).collect(Collectors.toList());

    return new MovieDto(
        movie.getId(),
        movie.getYear().getValue(),
        movie.getTitle(),
        movie.getStudios(),
        producerNames,
        movie.isWinner());
  }
}
