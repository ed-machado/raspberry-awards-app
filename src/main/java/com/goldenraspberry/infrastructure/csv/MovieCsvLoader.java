package com.goldenraspberry.infrastructure.csv;

import com.goldenraspberry.domain.model.Movie;
import com.goldenraspberry.domain.model.Producer;
import com.goldenraspberry.domain.model.Year;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/** Movie CSV Loader Carregador de filmes a partir de arquivo CSV */
@Component
public class MovieCsvLoader {

  private static final Logger logger = LoggerFactory.getLogger(MovieCsvLoader.class);

  private static final String DEFAULT_CSV_FILE_PATH = "movielist.csv";
  private static final String PRODUCER_SEPARATOR = ", ";
  private static final String PRODUCER_AND_SEPARATOR = " and ";
  private static final String YES_VALUE = "yes";

  @Value("${app.csv.file-path:movielist.csv}")
  private String csvFilePath;

  // Indices das colunas no CSV
  private static final int YEAR_INDEX = 0;
  private static final int TITLE_INDEX = 1;
  private static final int STUDIOS_INDEX = 2;
  private static final int PRODUCERS_INDEX = 3;
  private static final int WINNER_INDEX = 4;

  /**
   * Carrega filmes do arquivo CSV configurado
   *
   * @return Lista de filmes carregados
   * @throws MovieCsvLoadException Se houver erro no carregamento
   */
  public List<Movie> loadMovies() throws MovieCsvLoadException {
    return loadMoviesFromPath(csvFilePath);
  }

  /**
   * Carrega filmes de TODOS os arquivos CSV encontrados no diretorio resources/
   *
   * @return Lista de filmes carregados de todos os CSVs
   * @throws MovieCsvLoadException Se houver erro no carregamento
   */
  public List<Movie> loadAllCsvMovies() throws MovieCsvLoadException {
    logger.info("Iniciando carregamento de filmes de todos os arquivos CSV no resources/");

    List<Movie> allMovies = new ArrayList<>();
    ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    try {
      // Busca todos os arquivos .csv no classpath
      Resource[] csvResources = resolver.getResources("classpath*:*.csv");

      if (csvResources.length == 0) {
        logger.warn("Nenhum arquivo CSV encontrado no diretório resources/");
        return allMovies;
      }

      logger.info("Encontrados {} arquivos CSV para carregar", csvResources.length);

      for (Resource resource : csvResources) {
        String filename = resource.getFilename();
        logger.info("Carregando arquivo CSV: {}", filename);

        try (InputStreamReader reader =
            new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {

          List<Movie> moviesFromFile = loadMoviesFromReader(reader, "arquivo " + filename);
          allMovies.addAll(moviesFromFile);

          logger.info("Carregados {} filmes do arquivo {}", moviesFromFile.size(), filename);

        } catch (IOException e) {
          logger.error("Erro ao ler arquivo CSV {}: {}", filename, e.getMessage());
          throw new MovieCsvLoadException(
              "Erro ao ler arquivo CSV " + filename + ": " + e.getMessage(), e);
        }
      }

      logger.info(
          "Carregamento concluído: {} filmes carregados de {} arquivos CSV",
          allMovies.size(),
          csvResources.length);

      return allMovies;

    } catch (IOException e) {
      logger.error("Erro ao buscar arquivos CSV: {}", e.getMessage());
      throw new MovieCsvLoadException("Erro ao buscar arquivos CSV: " + e.getMessage(), e);
    }
  }

  /**
   * Carrega filmes de um arquivo CSV especifico
   *
   * @param filePath Caminho do arquivo CSV
   * @return Lista de filmes carregados
   * @throws MovieCsvLoadException Se houver erro no carregamento
   */
  public List<Movie> loadMoviesFromPath(String filePath) throws MovieCsvLoadException {
    logger.info("Iniciando carregamento de filmes do arquivo CSV: {}", filePath);

    try {
      ClassPathResource resource = new ClassPathResource(filePath);

      if (!resource.exists()) {
        throw new MovieCsvLoadException("Arquivo CSV nao encontrado: " + filePath);
      }

      try (InputStreamReader reader =
          new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
        return loadMoviesFromReader(reader, "arquivo " + filePath);
      }

    } catch (IOException e) {
      logger.error("Erro ao ler arquivo CSV: {}", e.getMessage());
      throw new MovieCsvLoadException("Erro ao ler arquivo CSV: " + e.getMessage(), e);
    }
  }

  /**
   * Carrega filmes de um arquivo CSV via upload
   *
   * @param file Arquivo CSV enviado via upload
   * @return Lista de filmes carregados
   * @throws MovieCsvLoadException Se houver erro no carregamento
   */
  public List<Movie> loadMoviesFromUpload(MultipartFile file) throws MovieCsvLoadException {
    if (file == null || file.isEmpty()) {
      throw new MovieCsvLoadException("Arquivo CSV nao pode ser nulo ou vazio");
    }

    logger.info(
        "Iniciando carregamento de filmes do arquivo CSV via upload: {}",
        file.getOriginalFilename());

    try {
      try (InputStreamReader reader =
          new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)) {
        return loadMoviesFromReader(reader, "upload " + file.getOriginalFilename());
      }

    } catch (IOException e) {
      logger.error("Erro ao ler arquivo CSV do upload: {}", e.getMessage());
      throw new MovieCsvLoadException("Erro ao ler arquivo CSV do upload: " + e.getMessage(), e);
    }
  }

  /**
   * Metodo generico para carregar filmes de um Reader
   *
   * @param reader Reader para ler o CSV
   * @param source Descricao da fonte para logs
   * @return Lista de filmes carregados
   * @throws MovieCsvLoadException Se houver erro no carregamento
   */
  private List<Movie> loadMoviesFromReader(InputStreamReader reader, String source)
      throws MovieCsvLoadException {
    try {
      List<Movie> movies = new ArrayList<>();

      try (CSVReader csvReader =
          new CSVReaderBuilder(reader)
              .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
              .build()) {

        List<String[]> records = csvReader.readAll();

        if (records.isEmpty()) {
          logger.warn("Arquivo CSV esta vazio: {}", source);
          return movies;
        }

        // Pula o cabecalho se existir
        boolean hasHeader = isHeaderRow(records.get(0));
        int startIndex = hasHeader ? 1 : 0;

        logger.info(
            "Processando {} registros de {} (cabecalho: {})",
            records.size() - startIndex,
            source,
            hasHeader);

        for (int i = startIndex; i < records.size(); i++) {
          String[] record = records.get(i);

          try {
            Movie movie = parseMovieRecord(record, i + 1);
            movies.add(movie);
          } catch (Exception e) {
            logger.error("Erro ao processar linha {} de {}: {}", i + 1, source, e.getMessage());
            throw new MovieCsvLoadException(
                String.format("Erro na linha %d de %s: %s", i + 1, source, e.getMessage()), e);
          }
        }

        logger.info("Carregamento de {} concluido: {} filmes processados", source, movies.size());
        return movies;
      }

    } catch (IOException | CsvException e) {
      logger.error("Erro ao ler CSV de {}: {}", source, e.getMessage());
      throw new MovieCsvLoadException("Erro ao ler CSV de " + source + ": " + e.getMessage(), e);
    }
  }

  /**
   * Verifica se a primeira linha e um cabecalho
   *
   * @param firstRow Primeira linha do CSV
   * @return true se for cabecalho
   */
  private boolean isHeaderRow(String[] firstRow) {
    if (firstRow.length < 2) {
      return false;
    }

    // Verifica se a primeira coluna contem "year" ou se nao e um numero
    String firstColumn = firstRow[0].trim().toLowerCase();
    return firstColumn.equals("year") || !isNumeric(firstColumn);
  }

  /**
   * Faz parsing de um registro do CSV para objeto Movie
   *
   * @param record Array com dados da linha
   * @param lineNumber Numero da linha para mensagens de erro
   * @return Objeto Movie
   */
  private Movie parseMovieRecord(String[] record, int lineNumber) {
    if (record.length < 5) {
      throw new IllegalArgumentException(
          String.format("Linha deve ter pelo menos 5 colunas, mas tem %d", record.length));
    }

    // Parse year
    String yearStr = record[YEAR_INDEX].trim();
    if (yearStr.isEmpty()) {
      throw new IllegalArgumentException("Year nao pode ser vazio");
    }

    int yearValue;
    try {
      yearValue = Integer.parseInt(yearStr);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Year deve ser um numero valido: " + yearStr);
    }

    // Parse title
    String title = record[TITLE_INDEX].trim();
    if (title.isEmpty()) {
      throw new IllegalArgumentException("Title nao pode ser vazio");
    }

    // Parse studios (pode ser vazio)
    String studios = record[STUDIOS_INDEX].trim();

    // Parse producers
    String producersStr = record[PRODUCERS_INDEX].trim();
    if (producersStr.isEmpty()) {
      throw new IllegalArgumentException("Producers nao pode ser vazio");
    }

    List<Producer> producers = parseProducers(producersStr);

    // Parse winner
    String winnerStr = record[WINNER_INDEX].trim().toLowerCase();
    boolean winner = YES_VALUE.equals(winnerStr);

    return new Movie(null, new Year(yearValue), title, studios, producers, winner);
  }

  /**
   * Faz parsing da string de produtores
   *
   * @param producersStr String com produtores
   * @return Lista de produtores
   */
  private List<Producer> parseProducers(String producersStr) {
    // Primeiro substitui ' and ' por ', ' para padronizar o separador
    String normalizedProducers = producersStr.replace(PRODUCER_AND_SEPARATOR, PRODUCER_SEPARATOR);

    return Arrays.stream(normalizedProducers.split(PRODUCER_SEPARATOR))
        .map(String::trim)
        .filter(name -> !name.isEmpty())
        .map(Producer::new)
        .collect(Collectors.toList());
  }

  /**
   * Verifica se uma string e numerica
   *
   * @param str String a verificar
   * @return true se for numerica
   */
  private boolean isNumeric(String str) {
    try {
      Integer.parseInt(str);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  /** Excecao para erros de carregamento de CSV */
  public static class MovieCsvLoadException extends Exception {
    public MovieCsvLoadException(String message) {
      super(message);
    }

    public MovieCsvLoadException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
