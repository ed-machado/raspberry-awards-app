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

/**
 * Caso de uso para atualizar um filme existente Valida dados de entrada e atualiza o filme no
 * repositorio
 */
@UseCase
public class UpdateMovieUseCase {

  private final MovieRepository movieRepository;

  @Autowired
  public UpdateMovieUseCase(MovieRepository movieRepository) {
    this.movieRepository = movieRepository;
  }

  /**
   * Executa o caso de uso para atualizar um filme existente
   *
   * @param id ID do filme a ser atualizado
   * @param movieInput DTO com novos dados do filme
   * @return DTO do filme atualizado
   */
  public MovieDto execute(Long id, MovieInputDto movieInput) {
    // Verificar se o filme existe
    Movie existingMovie =
        movieRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        String.format("Filme com ID %d não encontrado", id)));

    validateUniqueMovieForUpdate(id, movieInput.getTitle(), movieInput.getYear());
    Movie updatedMovie = convertToDomain(id, movieInput);
    Movie savedMovie = movieRepository.save(updatedMovie);

    // Converter para DTO de resposta
    return convertToDto(savedMovie);
  }

  /**
   * Valida se nao existe outro filme com mesmo titulo e ano (exceto o atual)
   *
   * @param currentId ID do filme atual sendo atualizado
   * @param title Novo titulo do filme
   * @param year Novo ano do filme
   */
  private void validateUniqueMovieForUpdate(Long currentId, String title, Integer year) {
    movieRepository.findAll().stream()
        .filter(movie -> !movie.getId().equals(currentId))
        .filter(movie -> movie.getTitle().equals(title))
        .filter(movie -> movie.getYear().equals(year))
        .findFirst()
        .ifPresent(
            movie -> {
              throw new IllegalArgumentException(
                  String.format("Já existe outro filme com título '%s' no ano %d", title, year));
            });
  }

  /**
   * Converte MovieInputDto para entidade de dominio
   *
   * @param id ID do filme
   * @param movieInput DTO de entrada
   * @return Entidade de dominio
   */
  private Movie convertToDomain(Long id, MovieInputDto movieInput) {
    List<Producer> producers =
        movieInput.getProducers().stream().map(Producer::new).collect(Collectors.toList());

    return new Movie(
        id,
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
