package com.goldenraspberry.infrastructure.web;

import com.goldenraspberry.application.dto.MovieDto;
import com.goldenraspberry.application.dto.MovieInputDto;
import com.goldenraspberry.application.dto.ProducerIntervalResponseDto;
import com.goldenraspberry.application.service.MovieApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Controller REST para operações relacionadas a filmes Implementa Richardson Maturity Model Level 2
 */
@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
@Tag(name = "Movies", description = "Operações relacionadas a filmes do Golden Raspberry Awards")
public class MovieController {

  private final MovieApplicationService movieApplicationService;

  @Autowired
  public MovieController(MovieApplicationService movieApplicationService) {
    this.movieApplicationService = movieApplicationService;
  }

  /**
   * Endpoint principal do desafio: obter intervalos de produtores GET /api/v1/producers/intervals
   *
   * @return Intervalos mínimos e máximos entre prêmios consecutivos
   */
  @GetMapping("/producers/intervals")
  @Operation(
      summary = "Obter intervalos entre prêmios de produtores",
      description =
          "Retorna o produtor com maior intervalo entre dois prêmios consecutivos, e o que obteve"
              + " dois prêmios mais rápido")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Intervalos encontrados com sucesso",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProducerIntervalResponseDto.class),
                    examples =
                        @ExampleObject(
                            name = "Exemplo de resposta",
                            value =
                                "{\"min\":[{\"producer\":\"Joel"
                                    + " Silver\",\"interval\":1,\"previousWin\":1990,\"followingWin\":1991}],\"max\":[{\"producer\":\"Matthew"
                                    + " Vaughn\",\"interval\":13,\"previousWin\":2002,\"followingWin\":2015}]}"))),
        @ApiResponse(
            responseCode = "500",
            description = "Erro interno do servidor",
            content = @Content(mediaType = "application/json"))
      })
  public ResponseEntity<ProducerIntervalResponseDto> getProducerIntervals() {
    ProducerIntervalResponseDto intervals = movieApplicationService.getProducerIntervals();
    return ResponseEntity.ok(intervals);
  }

  /**
   * Obter todos os filmes GET /api/v1/movies
   *
   * @return Lista de todos os filmes
   */
  @GetMapping("/movies")
  @Operation(
      summary = "Listar todos os filmes",
      description = "Retorna a lista completa de todos os filmes carregados do arquivo CSV")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de filmes retornada com sucesso",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = MovieDto.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Erro interno do servidor",
            content = @Content(mediaType = "application/json"))
      })
  public ResponseEntity<List<MovieDto>> getAllMovies() {
    List<MovieDto> movies = movieApplicationService.getAllMovies();
    return ResponseEntity.ok(movies);
  }

  /**
   * Obter apenas filmes vencedores GET /api/v1/movies/winners
   *
   * @return Lista de filmes vencedores
   */
  @GetMapping("/movies/winners")
  @Operation(
      summary = "Listar filmes vencedores",
      description = "Retorna apenas os filmes que ganharam o prêmio Golden Raspberry (Pior Filme)")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de filmes vencedores retornada com sucesso",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = MovieDto.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Erro interno do servidor",
            content = @Content(mediaType = "application/json"))
      })
  public ResponseEntity<List<MovieDto>> getWinnerMovies() {
    List<MovieDto> winners = movieApplicationService.getWinnerMovies();
    return ResponseEntity.ok(winners);
  }

  /**
   * Health check endpoint GET /api/v1/health
   *
   * @return Status da aplicação
   */
  @GetMapping("/health")
  @Operation(
      summary = "Verificação de saúde da API",
      description = "Endpoint para verificar se a API está funcionando corretamente")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "API funcionando corretamente",
            content =
                @Content(
                    mediaType = "text/plain",
                    examples =
                        @ExampleObject(
                            name = "Resposta de saúde",
                            value = "Golden Raspberry Awards API is running")))
      })
  public ResponseEntity<String> health() {
    return ResponseEntity.ok("Golden Raspberry Awards API is running");
  }

  /**
   * Criar um novo filme POST /api/v1/movies
   *
   * @param movieInputDto Dados do filme a ser criado
   * @return Filme criado com status 201 e header Location
   */
  @PostMapping("/movies")
  @Operation(
      summary = "Criar novo filme",
      description = "Cria um novo filme no sistema com os dados fornecidos")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Filme criado com sucesso",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = MovieDto.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos ou filme já existe",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(
            responseCode = "500",
            description = "Erro interno do servidor",
            content = @Content(mediaType = "application/json"))
      })
  public ResponseEntity<MovieDto> createMovie(@Valid @RequestBody MovieInputDto movieInputDto) {
    MovieDto createdMovie = movieApplicationService.createMovie(movieInputDto);

    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(createdMovie.getId())
            .toUri();

    return ResponseEntity.created(location).body(createdMovie);
  }

  /**
   * Buscar filme por ID GET /api/v1/movies/{id}
   *
   * @param id ID do filme
   * @return Filme encontrado
   */
  @GetMapping("/movies/{id}")
  @Operation(
      summary = "Buscar filme por ID",
      description = "Retorna um filme específico pelo seu ID")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Filme encontrado com sucesso",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = MovieDto.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Filme não encontrado",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(
            responseCode = "500",
            description = "Erro interno do servidor",
            content = @Content(mediaType = "application/json"))
      })
  public ResponseEntity<MovieDto> getMovieById(@PathVariable Long id) {
    MovieDto movie = movieApplicationService.getMovieById(id);
    return ResponseEntity.ok(movie);
  }

  /**
   * Atualizar filme existente PUT /api/v1/movies/{id}
   *
   * @param id ID do filme a ser atualizado
   * @param movieInputDto Novos dados do filme
   * @return Filme atualizado
   */
  @PutMapping("/movies/{id}")
  @Operation(
      summary = "Atualizar filme existente",
      description = "Atualiza completamente um filme existente com os novos dados fornecidos")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Filme atualizado com sucesso",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = MovieDto.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(
            responseCode = "404",
            description = "Filme não encontrado",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(
            responseCode = "500",
            description = "Erro interno do servidor",
            content = @Content(mediaType = "application/json"))
      })
  public ResponseEntity<MovieDto> updateMovie(
      @PathVariable Long id, @Valid @RequestBody MovieInputDto movieInputDto) {
    MovieDto updatedMovie = movieApplicationService.updateMovie(id, movieInputDto);
    return ResponseEntity.ok(updatedMovie);
  }

  /**
   * Deletar filme DELETE /api/v1/movies/{id}
   *
   * @param id ID do filme a ser deletado
   * @return Status 204 No Content
   */
  @DeleteMapping("/movies/{id}")
  @Operation(summary = "Deletar filme", description = "Remove um filme do sistema pelo seu ID")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Filme deletado com sucesso"),
        @ApiResponse(
            responseCode = "404",
            description = "Filme não encontrado",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(
            responseCode = "500",
            description = "Erro interno do servidor",
            content = @Content(mediaType = "application/json"))
      })
  public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
    movieApplicationService.deleteMovie(id);
    return ResponseEntity.noContent().build();
  }
}
