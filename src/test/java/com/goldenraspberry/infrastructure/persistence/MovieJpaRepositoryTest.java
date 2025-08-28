package com.goldenraspberry.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.*;

import com.goldenraspberry.infrastructure.persistence.entity.MovieJpaEntity;
import com.goldenraspberry.infrastructure.persistence.repository.MovieJpaRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/** Testes de integração para MovieJpaRepository */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MovieJpaRepositoryTest {

  @Autowired private MovieJpaRepository movieRepository;

  private MovieJpaEntity winnerMovie1;
  private MovieJpaEntity winnerMovie2;
  private MovieJpaEntity nonWinnerMovie;

  @BeforeEach
  void setUp() {
    // Limpar dados existentes
    movieRepository.deleteAll();

    // Filme vencedor 1
    winnerMovie1 = new MovieJpaEntity();
    winnerMovie1.setYear(1980);
    winnerMovie1.setTitle("Can't Stop the Music");
    winnerMovie1.setStudios("Associated Film Distribution");
    winnerMovie1.setProducers("Allan Carr");
    winnerMovie1.setWinner(true);
    winnerMovie1 = movieRepository.save(winnerMovie1);

    // Filme vencedor 2
    winnerMovie2 = new MovieJpaEntity();
    winnerMovie2.setYear(1981);
    winnerMovie2.setTitle("Mommie Dearest");
    winnerMovie2.setStudios("Paramount Pictures");
    winnerMovie2.setProducers("Frank Yablans");
    winnerMovie2.setWinner(true);
    winnerMovie2 = movieRepository.save(winnerMovie2);

    // Filme não vencedor
    nonWinnerMovie = new MovieJpaEntity();
    nonWinnerMovie.setYear(1980);
    nonWinnerMovie.setTitle("Cruising");
    nonWinnerMovie.setStudios("Lorimar Productions, United Artists");
    nonWinnerMovie.setProducers("Jerry Weintraub");
    nonWinnerMovie.setWinner(false);
    nonWinnerMovie = movieRepository.save(nonWinnerMovie);
  }

  @Test
  void testFindAll() {
    List<MovieJpaEntity> movies = movieRepository.findAll();

    assertEquals(3, movies.size());
    assertTrue(movies.stream().anyMatch(m -> m.getTitle().equals("Can't Stop the Music")));
    assertTrue(movies.stream().anyMatch(m -> m.getTitle().equals("Mommie Dearest")));
    assertTrue(movies.stream().anyMatch(m -> m.getTitle().equals("Cruising")));
  }

  @Test
  void testFindById() {
    Optional<MovieJpaEntity> found = movieRepository.findById(winnerMovie1.getId());

    assertTrue(found.isPresent());
    assertEquals("Can't Stop the Music", found.get().getTitle());
    assertEquals(1980, found.get().getYear());
    assertTrue(found.get().getWinner());
  }

  @Test
  void testFindByWinnerTrue() {
    List<MovieJpaEntity> winners = movieRepository.findByWinnerTrue();

    assertEquals(2, winners.size());
    assertTrue(winners.stream().allMatch(MovieJpaEntity::getWinner));
    assertTrue(winners.stream().anyMatch(m -> m.getTitle().equals("Can't Stop the Music")));
    assertTrue(winners.stream().anyMatch(m -> m.getTitle().equals("Mommie Dearest")));
  }

  @Test
  void testFindByYear() {
    List<MovieJpaEntity> movies1980 = movieRepository.findByYear(1980);
    List<MovieJpaEntity> movies1981 = movieRepository.findByYear(1981);
    List<MovieJpaEntity> movies1999 = movieRepository.findByYear(1999);

    assertEquals(2, movies1980.size());
    assertEquals(1, movies1981.size());
    assertEquals(0, movies1999.size());

    assertTrue(movies1980.stream().allMatch(m -> m.getYear() == 1980));
    assertTrue(movies1981.stream().allMatch(m -> m.getYear() == 1981));
  }

  @Test
  void testFindByYearAndWinnerTrue() {
    List<MovieJpaEntity> winners1980 = movieRepository.findByYearAndWinnerTrue(1980);
    List<MovieJpaEntity> winners1981 = movieRepository.findByYearAndWinnerTrue(1981);
    List<MovieJpaEntity> winners1999 = movieRepository.findByYearAndWinnerTrue(1999);

    assertEquals(1, winners1980.size());
    assertEquals(1, winners1981.size());
    assertEquals(0, winners1999.size());

    assertEquals("Can't Stop the Music", winners1980.get(0).getTitle());
    assertEquals("Mommie Dearest", winners1981.get(0).getTitle());
  }

  @Test
  void testFindByProducersContaining() {
    List<MovieJpaEntity> allanCarrMovies =
        movieRepository.findByProducerNameContaining("Allan Carr");
    List<MovieJpaEntity> frankYablansMovies =
        movieRepository.findByProducerNameContaining("Frank Yablans");
    List<MovieJpaEntity> unknownMovies =
        movieRepository.findByProducerNameContaining("Unknown Producer");

    assertEquals(1, allanCarrMovies.size());
    assertEquals(1, frankYablansMovies.size());
    assertEquals(0, unknownMovies.size());

    assertEquals("Can't Stop the Music", allanCarrMovies.get(0).getTitle());
    assertEquals("Mommie Dearest", frankYablansMovies.get(0).getTitle());
  }

  @Test
  void testFindByProducersContainingAndWinnerTrue() {
    List<MovieJpaEntity> allanCarrWinners =
        movieRepository.findByProducersContainingAndWinnerTrue("Allan Carr");
    List<MovieJpaEntity> jerryWeintraubWinners =
        movieRepository.findByProducersContainingAndWinnerTrue("Jerry Weintraub");

    assertEquals(1, allanCarrWinners.size());
    assertEquals(0, jerryWeintraubWinners.size()); // Jerry Weintraub não tem filme vencedor

    assertEquals("Can't Stop the Music", allanCarrWinners.get(0).getTitle());
    assertTrue(allanCarrWinners.get(0).getWinner());
  }

  @Test
  void testCountByWinnerTrue() {
    long winnerCount = movieRepository.countByWinnerTrue();

    assertEquals(2, winnerCount);
  }

  @Test
  void testFindDistinctYearsByWinnerTrueOrderByYear() {
    List<Integer> winnerYears = movieRepository.findDistinctYearsByWinnerTrueOrderByYear();

    assertEquals(2, winnerYears.size());
    assertEquals(Integer.valueOf(1980), winnerYears.get(0));
    assertEquals(Integer.valueOf(1981), winnerYears.get(1));
  }

  @Test
  void testSaveAndUpdate() {
    // Criar novo filme
    MovieJpaEntity newMovie = new MovieJpaEntity();
    newMovie.setYear(1982);
    newMovie.setTitle("Inchon");
    newMovie.setStudios("MGM");
    newMovie.setProducers("Mitsuharu Ishii");
    newMovie.setWinner(true);

    MovieJpaEntity saved = movieRepository.save(newMovie);

    assertNotNull(saved.getId());
    assertEquals("Inchon", saved.getTitle());

    // Atualizar filme
    saved.setTitle("Inchon Updated");
    MovieJpaEntity updated = movieRepository.save(saved);

    assertEquals(saved.getId(), updated.getId());
    assertEquals("Inchon Updated", updated.getTitle());
  }

  @Test
  void testDeleteById() {
    Long movieId = winnerMovie1.getId();

    assertTrue(movieRepository.existsById(movieId));

    movieRepository.deleteById(movieId);

    assertFalse(movieRepository.existsById(movieId));
    assertEquals(2, movieRepository.count());
  }

  @Test
  void testDeleteAll() {
    assertEquals(3, movieRepository.count());

    movieRepository.deleteAll();

    assertEquals(0, movieRepository.count());
  }

  @Test
  void testCount() {
    long count = movieRepository.count();

    assertEquals(3, count);
  }

  @Test
  void testExistsById() {
    assertTrue(movieRepository.existsById(winnerMovie1.getId()));
    assertTrue(movieRepository.existsById(winnerMovie2.getId()));
    assertTrue(movieRepository.existsById(nonWinnerMovie.getId()));
    assertFalse(movieRepository.existsById(999L));
  }
}
