package com.goldenraspberry.application.usecase;

import com.goldenraspberry.application.dto.MovieDto;
import com.goldenraspberry.common.annotation.UseCase;
import com.goldenraspberry.domain.model.Movie;
import com.goldenraspberry.domain.port.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Caso de uso para obter filmes vencedores
 * Retorna apenas filmes que ganharam o premio
 */
@UseCase
public class GetWinnerMoviesUseCase {

    private final MovieRepository movieRepository;

    @Autowired
    public GetWinnerMoviesUseCase(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    /**
     * Executa o caso de uso para obter filmes vencedores
     * @return Lista de DTOs de filmes vencedores
     */
    public List<MovieDto> execute() {
        List<Movie> winnerMovies = movieRepository.findAllWinners();

        return winnerMovies.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Converte Movie do dominio para DTO
     * @param movie Entidade do dominio
     * @return DTO do filme
     */
    private MovieDto convertToDto(Movie movie) {
        List<String> producerNames = movie.getProducers().stream()
                .map(producer -> producer.getName())
                .collect(Collectors.toList());

        return new MovieDto(
                movie.getId(),
                movie.getYear().getValue(),
                movie.getTitle(),
                movie.getStudios(),
                producerNames,
                movie.isWinner()
        );
    }
}
