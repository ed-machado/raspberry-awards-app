package com.goldenraspberry.infrastructure.web;

import com.goldenraspberry.domain.model.Movie;
import com.goldenraspberry.infrastructure.csv.MovieCsvLoader;
import com.goldenraspberry.infrastructure.persistence.entity.MovieJpaEntity;
import com.goldenraspberry.infrastructure.persistence.mapper.MovieEntityMapper;
import com.goldenraspberry.infrastructure.persistence.repository.MovieJpaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Data Management Controller Controller para gerenciamento de dados (upload de CSV, recarregamento,
 * etc.)
 */
@RestController
@RequestMapping("/api/data")
@Tag(name = "Data Management", description = "Endpoints para gerenciamento de dados")
public class DataManagementController {

  private static final Logger logger = LoggerFactory.getLogger(DataManagementController.class);

  private final MovieCsvLoader csvLoader;
  private final MovieEntityMapper entityMapper;
  private final MovieJpaRepository movieRepository;

  @Autowired
  public DataManagementController(
      MovieCsvLoader csvLoader,
      MovieEntityMapper entityMapper,
      MovieJpaRepository movieRepository) {
    this.csvLoader = csvLoader;
    this.entityMapper = entityMapper;
    this.movieRepository = movieRepository;
  }

  /**
   * Upload de arquivo CSV e recarregamento dos dados
   *
   * @param file Arquivo CSV com dados de filmes
   * @return Resultado do carregamento
   */
  @PostMapping(value = "/upload-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(
      summary = "Upload de arquivo CSV",
      description = "Faz upload de um arquivo CSV com dados de filmes e recarrega a base de dados")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "CSV carregado com sucesso",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Map.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Erro no formato do arquivo ou dados inválidos",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(
            responseCode = "500",
            description = "Erro interno do servidor",
            content = @Content(mediaType = "application/json"))
      })
  @Transactional
  public ResponseEntity<Map<String, Object>> uploadCsv(
      @Parameter(description = "Arquivo CSV com dados de filmes") @RequestParam("file")
          MultipartFile file) {

    logger.info(
        "Recebido upload de CSV: {} (tamanho: {} bytes)",
        file.getOriginalFilename(),
        file.getSize());

    try {
      // Validacoes basicas
      if (file.isEmpty()) {
        return ResponseEntity.badRequest()
            .body(createErrorResponse("Arquivo não pode estar vazio"));
      }

      String filename = file.getOriginalFilename();
      if (filename == null || !filename.toLowerCase().endsWith(".csv")) {
        return ResponseEntity.badRequest()
            .body(createErrorResponse("Arquivo deve ter extensão .csv"));
      }

      // Carrega filmes do CSV
      List<Movie> movies = csvLoader.loadMoviesFromUpload(file);

      if (movies.isEmpty()) {
        return ResponseEntity.badRequest()
            .body(createErrorResponse("Nenhum filme válido encontrado no CSV"));
      }

      // Limpa dados existentes e salva novos
      logger.info("Limpando dados existentes...");
      movieRepository.deleteAll();

      logger.info("Salvando {} novos filmes...", movies.size());
      List<MovieJpaEntity> entities = entityMapper.toEntityList(movies);
      movieRepository.saveAll(entities);

      // Estatisticas finais
      long totalMovies = movieRepository.count();
      long winnerMovies = movieRepository.countByWinnerTrue();

      Map<String, Object> response = new HashMap<>();
      response.put("success", true);
      response.put("message", "CSV carregado com sucesso");
      response.put("filename", filename);
      response.put("totalMovies", totalMovies);
      response.put("winnerMovies", winnerMovies);
      response.put("nonWinnerMovies", totalMovies - winnerMovies);

      logger.info(
          "Upload concluído: {} filmes carregados ({} vencedores)", totalMovies, winnerMovies);

      return ResponseEntity.ok(response);

    } catch (MovieCsvLoader.MovieCsvLoadException e) {
      logger.error("Erro ao processar CSV: {}", e.getMessage());
      return ResponseEntity.badRequest()
          .body(createErrorResponse("Erro no formato do CSV: " + e.getMessage()));
    } catch (Exception e) {
      logger.error("Erro inesperado no upload: {}", e.getMessage(), e);
      return ResponseEntity.internalServerError()
          .body(createErrorResponse("Erro interno: " + e.getMessage()));
    }
  }

  /**
   * Recarrega dados do CSV padrao configurado
   *
   * @return Resultado do recarregamento
   */
  @PostMapping("/reload")
  @Operation(
      summary = "Recarregar dados do CSV padrão",
      description = "Recarrega os dados do arquivo CSV padrão configurado na aplicação")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Dados recarregados com sucesso",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Map.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Erro interno do servidor",
            content = @Content(mediaType = "application/json"))
      })
  @Transactional
  public ResponseEntity<Map<String, Object>> reloadData() {
    logger.info("Iniciando recarregamento de dados do CSV padrão...");

    try {
      // Carrega filmes do CSV padrao
      List<Movie> movies = csvLoader.loadMovies();

      // Limpa dados existentes e salva novos
      logger.info("Limpando dados existentes...");
      movieRepository.deleteAll();

      logger.info("Salvando {} filmes do CSV padrão...", movies.size());
      List<MovieJpaEntity> entities = entityMapper.toEntityList(movies);
      movieRepository.saveAll(entities);

      // Estatisticas finais
      long totalMovies = movieRepository.count();
      long winnerMovies = movieRepository.countByWinnerTrue();

      Map<String, Object> response = new HashMap<>();
      response.put("success", true);
      response.put("message", "Dados recarregados com sucesso");
      response.put("totalMovies", totalMovies);
      response.put("winnerMovies", winnerMovies);
      response.put("nonWinnerMovies", totalMovies - winnerMovies);

      logger.info(
          "Recarregamento concluído: {} filmes carregados ({} vencedores)",
          totalMovies,
          winnerMovies);

      return ResponseEntity.ok(response);

    } catch (Exception e) {
      logger.error("Erro no recarregamento: {}", e.getMessage(), e);
      return ResponseEntity.internalServerError()
          .body(createErrorResponse("Erro no recarregamento: " + e.getMessage()));
    }
  }

  /**
   * Obtem estatisticas dos dados carregados
   *
   * @return Estatisticas dos dados
   */
  @GetMapping("/stats")
  @Operation(
      summary = "Estatísticas dos dados",
      description = "Obtém estatísticas dos dados atualmente carregados na base")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Estatísticas obtidas com sucesso",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Map.class)))
      })
  public ResponseEntity<Map<String, Object>> getStats() {
    long totalMovies = movieRepository.count();
    long winnerMovies = movieRepository.countByWinnerTrue();
    List<Integer> winnerYears = movieRepository.findDistinctYearsByWinnerTrueOrderByYear();

    Map<String, Object> stats = new HashMap<>();
    stats.put("totalMovies", totalMovies);
    stats.put("winnerMovies", winnerMovies);
    stats.put("nonWinnerMovies", totalMovies - winnerMovies);
    stats.put("winnerYears", winnerYears);
    stats.put(
        "yearRange",
        winnerYears.isEmpty()
            ? null
            : Map.of("min", winnerYears.get(0), "max", winnerYears.get(winnerYears.size() - 1)));

    return ResponseEntity.ok(stats);
  }

  /**
   * Cria resposta de erro padronizada
   *
   * @param message Mensagem de erro
   * @return Mapa com erro
   */
  private Map<String, Object> createErrorResponse(String message) {
    Map<String, Object> error = new HashMap<>();
    error.put("success", false);
    error.put("error", message);
    return error;
  }
}
