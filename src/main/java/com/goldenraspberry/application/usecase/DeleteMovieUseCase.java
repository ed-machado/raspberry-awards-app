package com.goldenraspberry.application.usecase;

import com.goldenraspberry.common.annotation.UseCase;
import com.goldenraspberry.domain.model.Movie;
import com.goldenraspberry.domain.port.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;

/** Caso de uso para deletar um filme Valida existencia e remove o filme do repositorio */
@UseCase
public class DeleteMovieUseCase {

  private final MovieRepository movieRepository;

  @Autowired
  public DeleteMovieUseCase(MovieRepository movieRepository) {
    this.movieRepository = movieRepository;
  }

  /**
   * Executa o caso de uso para deletar um filme
   *
   * @param id ID do filme a ser deletado
   * @throws IllegalArgumentException se o filme nao for encontrado
   */
  public void execute(Long id) {
    // Verifica se o filme existe antes de deletar
    Movie existingMovie =
        movieRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        String.format("Filme com ID %d não encontrado", id)));

    // Deletar o filme
    movieRepository.deleteById(id);
  }
}
