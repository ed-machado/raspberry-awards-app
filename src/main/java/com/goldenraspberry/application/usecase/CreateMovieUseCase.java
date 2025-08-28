package com.goldenraspberry.application.usecase;

import com.goldenraspberry.application.dto.MovieDto;
import com.goldenraspberry.application.dto.MovieInputDto;
import com.goldenraspberry.common.annotation.UseCase;
import com.goldenraspberry.domain.model.Movie;
import com.goldenraspberry.domain.model.Producer;
import com.goldenraspberry.domain.model.Year;
import com.goldenraspberry.domain.port.MovieRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;

/** Caso de uso para criar um novo filme */
@UseCase
public class CreateMovieUseCase {

  private final MovieRepository movieRepository;

  @Autowired
  public CreateMovieUseCase(MovieRepository movieRepository) {
    this.movieRepository = movieRepository;
  }

  /**
   * Executa o caso de uso para criar um novo filme
   *
   * @param movieInput DTO com dados do filme a ser criado
   * @return DTO do filme criado com ID gerado
   */
  public MovieDto execute(MovieInputDto movieInput) {
    validateUniqueMovie(movieInput.getTitle(), movieInput.getYear());
    Movie movie = convertToDomain(movieInput);
    Movie savedMovie = movieRepository.save(movie);

    return convertToDto(savedMovie);
  }

  /**
   * Valida se ja existe um filme com mesmo titulo e ano
   *
   * @param title Titulo do filme
   * @param year Ano do filme
   */
  private void validateUniqueMovie(String title, Integer year) {
    boolean exists = movieRepository.existsByTitleAndYear(title, year);
    if (exists) {
      throw new IllegalArgumentException(
          String.format("Já existe um filme com título '%s' no ano %d", title, year));
    }
  }

  /**
   * Converte MovieInputDto para entidade de dominio
   *
   * @param movieInput DTO de entrada
   * @return Entidade de dominio
   */
  private Movie convertToDomain(MovieInputDto movieInput) {
    List<Producer> producers =
        movieInput.getProducers().stream().map(Producer::new).collect(Collectors.toList());

    return new Movie(
        null, // ID sera gerado pelo repositorio
        new Year(movieInput.getYear()),
        movieInput.getTitle(),
        movieInput.getStudios(),
        producers,
        movieInput.getWinner());
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
