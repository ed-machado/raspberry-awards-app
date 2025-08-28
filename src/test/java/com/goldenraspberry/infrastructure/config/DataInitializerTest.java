package com.goldenraspberry.infrastructure.config;

import static org.junit.jupiter.api.Assertions.*;

import com.goldenraspberry.infrastructure.persistence.repository.MovieJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

/** Testes de integração para DataInitializer */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(
    properties = {"app.csv.file-path=movielist.csv", "logging.level.com.goldenraspberry=DEBUG"})
@Transactional
class DataInitializerTest {

  @Autowired private MovieJpaRepository movieRepository;

  @Test
  void testDataInitializationLoadsMovies() {
    // Verificar se dados foram carregados na inicialização
    long totalMovies = movieRepository.count();

    // Deve ter carregado filmes do CSV
    assertTrue(totalMovies > 0, "Deveria ter carregado filmes do CSV");

    // Verificar se há filmes vencedores
    long winnerMovies = movieRepository.countByWinnerTrue();
    assertTrue(winnerMovies > 0, "Deveria ter filmes vencedores");

    // Verificar se há filmes não vencedores
    long nonWinnerMovies = totalMovies - winnerMovies;
    assertTrue(nonWinnerMovies > 0, "Deveria ter filmes não vencedores");

    System.out.println("Total de filmes carregados: " + totalMovies);
    System.out.println("Filmes vencedores: " + winnerMovies);
    System.out.println("Filmes não vencedores: " + nonWinnerMovies);
  }

  @Test
  void testDataIntegrityAfterInitialization() {
    // Verificar integridade dos dados carregados
    var allMovies = movieRepository.findAll();

    assertFalse(allMovies.isEmpty(), "Lista de filmes não deveria estar vazia");

    // Verificar se todos os filmes têm dados obrigatórios
    allMovies.forEach(
        movie -> {
          assertNotNull(movie.getId(), "ID não deveria ser nulo");
          assertNotNull(movie.getYear(), "Ano não deveria ser nulo");
          assertNotNull(movie.getTitle(), "Título não deveria ser nulo");
          assertFalse(movie.getTitle().trim().isEmpty(), "Título não deveria estar vazio");

          // Verificar se ano está em range válido
          assertTrue(
              movie.getYear() >= 1900 && movie.getYear() <= 2100,
              "Ano deveria estar entre 1900 e 2100: " + movie.getYear());
        });
  }

  @Test
  void testWinnerMoviesHaveValidData() {
    var winnerMovies = movieRepository.findByWinnerTrue();

    assertFalse(winnerMovies.isEmpty(), "Deveria ter filmes vencedores");

    winnerMovies.forEach(
        movie -> {
          assertTrue(movie.getWinner(), "Filme deveria ser marcado como vencedor");
          assertNotNull(movie.getTitle(), "Título não deveria ser nulo");
          assertNotNull(movie.getYear(), "Ano não deveria ser nulo");

          // Log para debug
          System.out.println(
              String.format(
                  "Filme vencedor: %d - %s (%s)",
                  movie.getYear(), movie.getTitle(), movie.getProducers()));
        });
  }

  @Test
  void testDistinctYearsAreLoaded() {
    var distinctYears = movieRepository.findDistinctYearsByWinnerTrueOrderByYear();

    assertFalse(distinctYears.isEmpty(), "Deveria ter anos distintos de filmes vencedores");

    // Verificar se anos estão ordenados
    for (int i = 1; i < distinctYears.size(); i++) {
      assertTrue(distinctYears.get(i - 1) <= distinctYears.get(i), "Anos deveriam estar ordenados");
    }

    System.out.println("Anos com filmes vencedores: " + distinctYears);
  }

  @Test
  void testProducersAreLoadedCorrectly() {
    var allMovies = movieRepository.findAll();

    // Verificar se há filmes com produtores
    long moviesWithProducers =
        allMovies.stream()
            .filter(movie -> movie.getProducers() != null && !movie.getProducers().trim().isEmpty())
            .count();

    assertTrue(moviesWithProducers > 0, "Deveria ter filmes com produtores");

    // Verificar alguns produtores específicos (se existirem no CSV)
    var producerMovies = movieRepository.findByProducerNameContaining("Allan Carr");
    if (!producerMovies.isEmpty()) {
      System.out.println("Filmes de Allan Carr encontrados: " + producerMovies.size());
      producerMovies.forEach(
          movie -> System.out.println("  - " + movie.getYear() + ": " + movie.getTitle()));
    }
  }

  @Test
  void testStudiosAreLoadedCorrectly() {
    var allMovies = movieRepository.findAll();

    // Verificar se há filmes com estúdios
    long moviesWithStudios =
        allMovies.stream()
            .filter(movie -> movie.getStudios() != null && !movie.getStudios().trim().isEmpty())
            .count();

    assertTrue(moviesWithStudios > 0, "Deveria ter filmes com estúdios");

    // Log alguns exemplos
    allMovies.stream()
        .filter(movie -> movie.getStudios() != null && !movie.getStudios().trim().isEmpty())
        .limit(5)
        .forEach(
            movie ->
                System.out.println(
                    String.format(
                        "Estúdio: %s - Filme: %s", movie.getStudios(), movie.getTitle())));
  }
}
