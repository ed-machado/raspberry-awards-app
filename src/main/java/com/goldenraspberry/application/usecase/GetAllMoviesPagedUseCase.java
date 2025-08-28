package com.goldenraspberry.application.usecase;

import com.goldenraspberry.application.dto.MovieDto;
import com.goldenraspberry.application.dto.PagedResponseDto;
import com.goldenraspberry.common.annotation.UseCase;
import com.goldenraspberry.domain.model.Movie;
import com.goldenraspberry.domain.port.MovieRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/** Caso de uso para obter todos os filmes com paginação */
@UseCase
public class GetAllMoviesPagedUseCase {

  private final MovieRepository movieRepository;

  @Autowired
  public GetAllMoviesPagedUseCase(MovieRepository movieRepository) {
    this.movieRepository = movieRepository;
  }

  /**
   * Executa o caso de uso para obter filmes paginados
   *
   * @param page Número da página (baseado em zero)
   * @param size Tamanho da página
   * @param sortBy Campo para ordenação (padrão: id)
   * @return Resposta paginada com filmes
   */
  public PagedResponseDto<MovieDto> execute(int page, int size, String sortBy) {
    // Validações
    if (page < 0) {
      page = 0;
    }
    if (size <= 0 || size > 100) {
      size = 20; // Tamanho padrão
    }
    if (sortBy == null || sortBy.trim().isEmpty()) {
      sortBy = "id";
    }

    // Usa ordenação ascendente por padrão
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortBy));
    Page<Movie> moviePage = movieRepository.findAll(pageable);

    List<MovieDto> movieDtos =
        moviePage.getContent().stream().map(this::convertToDto).collect(Collectors.toList());

    return new PagedResponseDto<>(
        movieDtos, moviePage.getNumber(), moviePage.getSize(), moviePage.getTotalElements());
  }

  private MovieDto convertToDto(Movie movie) {
    MovieDto dto = new MovieDto();
    dto.setId(movie.getId());
    dto.setYear(movie.getYear().getValue());
    dto.setTitle(movie.getTitle());
    dto.setStudios(movie.getStudios());
    dto.setProducers(
        movie.getProducers().stream()
            .map(producer -> producer.getName())
            .collect(Collectors.toList()));
    dto.setWinner(movie.isWinner());
    return dto;
  }
}
