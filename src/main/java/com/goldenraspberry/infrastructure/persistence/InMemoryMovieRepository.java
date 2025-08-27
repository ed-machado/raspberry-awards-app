package com.goldenraspberry.infrastructure.persistence;

import com.goldenraspberry.domain.model.Movie;
import com.goldenraspberry.domain.model.Year;
import com.goldenraspberry.domain.port.MovieRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * MovieRepository em memoria
 */
@Repository
public class InMemoryMovieRepository implements MovieRepository {

    private final List<Movie> movies = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public List<Movie> findAll() {
        return new ArrayList<>(movies);
    }

    @Override
    public Optional<Movie> findById(Long id) {
        return movies.stream()
                .filter(movie -> movie.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Movie> findAllWinners() {
        return movies.stream()
                .filter(Movie::isWinner)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movie> findByYear(Year year) {
        return movies.stream()
                .filter(movie -> movie.getYear().equals(year))
                .collect(Collectors.toList());
    }

    @Override
    public List<Movie> findByProducerName(String producerName) {
        return movies.stream()
                .filter(movie -> movie.getProducers().stream()
                        .anyMatch(producer -> producer.getName().equals(producerName)))
                .collect(Collectors.toList());
    }

    @Override
    public Movie save(Movie movie) {
        if (movie.getId() == null) {
            // Criar novo filme com ID gerado
            Movie newMovie = new Movie(
                    idGenerator.getAndIncrement(),
                    movie.getYear(),
                    movie.getTitle(),
                    movie.getStudios(),
                    movie.getProducers(),
                    movie.isWinner()
            );
            movies.add(newMovie);
            return newMovie;
        } else {
            // Atualizar filme existente
            movies.removeIf(m -> m.getId().equals(movie.getId()));
            movies.add(movie);
            return movie;
        }
    }

    @Override
    public List<Movie> saveAll(List<Movie> moviesToSave) {
        List<Movie> savedMovies = new ArrayList<>();
        for (Movie movie : moviesToSave) {
            savedMovies.add(save(movie));
        }
        return savedMovies;
    }

    @Override
    public void deleteById(Long id) {
        movies.removeIf(movie -> movie.getId().equals(id));
    }

    @Override
    public void deleteAll() {
        movies.clear();
    }

    @Override
    public boolean existsById(Long id) {
        return movies.stream()
                .anyMatch(movie -> movie.getId().equals(id));
    }

    @Override
    public long count() {
        return movies.size();
    }
}
