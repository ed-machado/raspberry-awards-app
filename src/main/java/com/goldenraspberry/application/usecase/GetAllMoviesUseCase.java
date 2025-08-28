package com.goldenraspberry.application.usecase;

import com.goldenraspberry.application.dto.MovieDto;
import com.goldenraspberry.common.annotation.UseCase;
import com.goldenraspberry.domain.model.Movie;
import com.goldenraspberry.domain.port.MovieRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;

/** Caso de uso para obter todos os filmes Retorna lista completa de filmes do sistema */
@UseCase
public class GetAllMoviesUseCase {

  private final MovieRepository movieRepository;

  @Autowired
  public GetAllMoviesUseCase(MovieRepository movieRepository) {
    this.movieRepository = movieRepository;
  }

  /**
   * Executa o caso de uso para obter todos os filmes
   *
   * @return Lista de DTOs de filmes
   */
  public List<MovieDto> execute() {
    List<Movie> movies = movieRepository.findAll();

    return movies.stream().map(this::convertToDto).collect(Collectors.toList());
  }

  /**
   * Converte Movie do dominio para DTO
   *
   * @param movie Entidade do dominio
   * @return DTO do filme
   */
  private MovieDto convertToDto(Movie movie) {
    List<String> producerNames =
        movie.getProducers().stream()
            .map(producer -> producer.getName())
            .collect(Collectors.toList());

    return new MovieDto(
        movie.getId(),
        movie.getYear().getValue(),
        movie.getTitle(),
        movie.getStudios(),
        producerNames,
        movie.isWinner());
  }
}
