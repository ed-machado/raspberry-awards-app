package com.goldenraspberry.infrastructure.config;

import com.goldenraspberry.domain.model.Movie;
import com.goldenraspberry.infrastructure.csv.MovieCsvLoader;
import com.goldenraspberry.infrastructure.persistence.entity.MovieJpaEntity;
import com.goldenraspberry.infrastructure.persistence.mapper.MovieEntityMapper;
import com.goldenraspberry.infrastructure.persistence.repository.MovieJpaRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Data Initializer Inicializador de dados que carrega filmes do CSV na inicializacao da aplicacao
 */
@Component
public class DataInitializer {

  private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

  private final MovieCsvLoader csvLoader;
  private final MovieEntityMapper entityMapper;
  private final MovieJpaRepository movieRepository;

  @Autowired
  public DataInitializer(
      MovieCsvLoader csvLoader,
      MovieEntityMapper entityMapper,
      MovieJpaRepository movieRepository) {
    this.csvLoader = csvLoader;
    this.entityMapper = entityMapper;
    this.movieRepository = movieRepository;
  }

  /** Inicializa os dados carregando filmes do CSV */
  @EventListener(ApplicationReadyEvent.class)
  @Transactional
  public void initializeData() {
    logger.info("Iniciando inicializacao de dados...");

    try {
      // Verifica se ja existem dados no banco (agora que as tabelas foram criadas pelo Hibernate)
      long existingMoviesCount;
      try {
        existingMoviesCount = movieRepository.count();
      } catch (Exception e) {
        // Se a tabela ainda nao existe, assume que nao ha dados
        logger.warn("Tabela ainda não existe, assumindo banco vazio: {}", e.getMessage());
        existingMoviesCount = 0;
      }

      if (existingMoviesCount > 0) {
        logger.info(
            "Dados ja existem no banco ({} filmes). Pulando inicializacao.", existingMoviesCount);
        return;
      }

      // Carrega filmes de TODOS os arquivos CSV no resources/
      logger.info("Carregando filmes de todos os arquivos CSV");
      List<Movie> movies = csvLoader.loadAllCsvMovies();

      if (movies.isEmpty()) {
        logger.warn("Nenhum filme foi carregado do CSV");
        return;
      }

      // Converte para entidades JPA
      logger.info("Convertendo {} filmes para entidades JPA...", movies.size());
      List<MovieJpaEntity> entities = entityMapper.toEntityList(movies);

      // Salva no banco de dados
      logger.info("Salvando filmes no banco de dados...");
      List<MovieJpaEntity> savedEntities = movieRepository.saveAll(entities);

      // Log de estatisticas
      long totalMovies = movieRepository.count();
      long winnerMovies = movieRepository.countByWinnerTrue();

      logger.info("Inicializacao de dados concluida com sucesso!");
      logger.info("Total de filmes salvos: {}", totalMovies);
      logger.info("Filmes vencedores: {}", winnerMovies);
      logger.info("Filmes nao vencedores: {}", totalMovies - winnerMovies);

    } catch (MovieCsvLoader.MovieCsvLoadException e) {
      logger.error("Erro ao carregar dados do CSV: {}", e.getMessage());
      throw new DataInitializationException("Falha na inicializacao de dados", e);
    } catch (Exception e) {
      logger.error("Erro inesperado durante inicializacao de dados: {}", e.getMessage(), e);
      throw new DataInitializationException("Erro inesperado na inicializacao de dados", e);
    }
  }

  /** Excecao para erros de inicializacao de dados */
  public static class DataInitializationException extends RuntimeException {
    public DataInitializationException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
