package com.goldenraspberry.infrastructure.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goldenraspberry.application.dto.MovieDto;
import com.goldenraspberry.application.dto.MovieInputDto;
import com.goldenraspberry.application.dto.ProducerIntervalResponseDto;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

/** Testes de integracao para MovieController Valida todos os endpoints REST da API */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MovieControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  /**
   * Testa o endpoint principal do desafio: /api/v1/producers/intervals Deve retornar intervalos
   * minimos e maximos entre premios consecutivos
   */
  @Test
  void shouldReturnProducerIntervals() throws Exception {
    MvcResult result =
        mockMvc
            .perform(get("/api/v1/producers/intervals").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.min").isArray())
            .andExpect(jsonPath("$.max").isArray())
            .andReturn();

    String responseContent = result.getResponse().getContentAsString();
    ProducerIntervalResponseDto response =
        objectMapper.readValue(responseContent, ProducerIntervalResponseDto.class);

    // Validacoes especificas do formato esperado
    assertThat(response).isNotNull();
    assertThat(response.getMin()).isNotNull();
    assertThat(response.getMax()).isNotNull();

    // Valida estrutura dos intervalos minimos
    if (!response.getMin().isEmpty()) {
      response
          .getMin()
          .forEach(
              interval -> {
                assertThat(interval.getProducer()).isNotBlank();
                assertThat(interval.getInterval()).isNotNull();
                assertThat(interval.getPreviousWin()).isNotNull();
                assertThat(interval.getFollowingWin()).isNotNull();
                assertThat(interval.getFollowingWin())
                    .isGreaterThanOrEqualTo(interval.getPreviousWin());
              });
    }

    // Valida estrutura dos intervalos maximos
    if (!response.getMax().isEmpty()) {
      response
          .getMax()
          .forEach(
              interval -> {
                assertThat(interval.getProducer()).isNotBlank();
                assertThat(interval.getInterval()).isNotNull();
                assertThat(interval.getPreviousWin()).isNotNull();
                assertThat(interval.getFollowingWin()).isNotNull();
                assertThat(interval.getFollowingWin())
                    .isGreaterThanOrEqualTo(interval.getPreviousWin());
              });
    }
  }

  /** Testa o endpoint /api/v1/movies Deve retornar todos os filmes carregados */
  @Test
  void shouldReturnAllMovies() throws Exception {
    MvcResult result =
        mockMvc
            .perform(get("/api/v1/movies").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andReturn();

    String responseContent = result.getResponse().getContentAsString();
    List<MovieDto> movies =
        objectMapper.readValue(
            responseContent,
            objectMapper.getTypeFactory().constructCollectionType(List.class, MovieDto.class));

    // Validacoes dos dados carregados
    assertThat(movies).isNotEmpty();

    // Valida estrutura dos filmes
    movies.forEach(
        movie -> {
          assertThat(movie.getId()).isNotNull();
          assertThat(movie.getYear()).isNotNull();
          assertThat(movie.getTitle()).isNotBlank();
          assertThat(movie.getProducers()).isNotEmpty();
          assertThat(movie.getWinner()).isNotNull();
        });
  }

  /** Testa o endpoint /api/v1/movies/winners Deve retornar apenas filmes vencedores */
  @Test
  void shouldReturnOnlyWinnerMovies() throws Exception {
    MvcResult result =
        mockMvc
            .perform(get("/api/v1/movies/winners").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andReturn();

    String responseContent = result.getResponse().getContentAsString();
    List<MovieDto> winners =
        objectMapper.readValue(
            responseContent,
            objectMapper.getTypeFactory().constructCollectionType(List.class, MovieDto.class));

    // Validacoes especificas para vencedores
    assertThat(winners).isNotEmpty();
    // Verifica se há vencedores carregados (quantidade depende do CSV fornecido)

    // Todos os filmes retornados devem ser vencedores
    winners.forEach(
        movie -> {
          assertThat(movie.getWinner()).isTrue();
          assertThat(movie.getId()).isNotNull();
          assertThat(movie.getYear()).isNotNull();
          assertThat(movie.getTitle()).isNotBlank();
          assertThat(movie.getProducers()).isNotEmpty();
        });
  }

  /** Testa o endpoint de health check Deve retornar status 200 com mensagem de saúde */
  @Test
  void shouldReturnHealthStatus() throws Exception {
    mockMvc
        .perform(get("/api/v1/health").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType("text/plain;charset=UTF-8"))
        .andExpect(content().string("Golden Raspberry Awards API is running"));
  }

  /**
   * Testa comportamento com endpoint inexistente Pode retornar 404 ou 500 dependendo da
   * configuração
   */
  @Test
  void shouldReturnErrorForNonExistentEndpoint() throws Exception {
    mockMvc
        .perform(get("/api/v1/nonexistent").contentType(MediaType.APPLICATION_JSON))
        .andExpect(
            result -> {
              int status = result.getResponse().getStatus();
              assertThat(status).isIn(404, 500); // Aceita tanto 404 quanto 500
            });
  }

  /**
   * Testa validação de dados com intervalos de produtores Verifica se os dados estão consistentes
   * com o CSV carregado
   */
  @Test
  void shouldValidateProducerIntervalsWithRealData() throws Exception {
    MvcResult result =
        mockMvc.perform(get("/api/v1/producers/intervals")).andExpect(status().isOk()).andReturn();

    String responseContent = result.getResponse().getContentAsString();
    ProducerIntervalResponseDto response =
        objectMapper.readValue(responseContent, ProducerIntervalResponseDto.class);

    // Validações específicas baseadas nos dados do CSV
    assertThat(response.getMin()).isNotNull();
    assertThat(response.getMax()).isNotNull();

    // Se há intervalos mínimos, devem ter intervalo menor ou igual aos máximos
    if (!response.getMin().isEmpty() && !response.getMax().isEmpty()) {
      int minInterval = response.getMin().get(0).getInterval();
      int maxInterval = response.getMax().get(0).getInterval();
      assertThat(minInterval).isLessThanOrEqualTo(maxInterval);
    }

    // Valida anos válidos (devem ser anos positivos e realistas)
    response
        .getMin()
        .forEach(
            interval -> {
              assertThat(interval.getPreviousWin()).isPositive();
              assertThat(interval.getFollowingWin()).isPositive();
              assertThat(interval.getFollowingWin())
                  .isGreaterThanOrEqualTo(interval.getPreviousWin());
            });

    response
        .getMax()
        .forEach(
            interval -> {
              assertThat(interval.getPreviousWin()).isPositive();
              assertThat(interval.getFollowingWin()).isPositive();
              assertThat(interval.getFollowingWin())
                  .isGreaterThanOrEqualTo(interval.getPreviousWin());
            });
  }

  /**
   * Testa consistência entre endpoints de filmes Verifica se todos os vencedores retornados pelo
   * endpoint específico estão incluídos no endpoint geral
   */
  @Test
  void shouldValidateDataConsistencyBetweenEndpoints() throws Exception {
    // Testa total de filmes
    MvcResult allMoviesResult =
        mockMvc.perform(get("/api/v1/movies")).andExpect(status().isOk()).andReturn();

    List<MovieDto> allMovies =
        objectMapper.readValue(
            allMoviesResult.getResponse().getContentAsString(),
            objectMapper.getTypeFactory().constructCollectionType(List.class, MovieDto.class));

    // Testa filmes vencedores
    MvcResult winnersResult =
        mockMvc.perform(get("/api/v1/movies/winners")).andExpect(status().isOk()).andReturn();

    List<MovieDto> winners =
        objectMapper.readValue(
            winnersResult.getResponse().getContentAsString(),
            objectMapper.getTypeFactory().constructCollectionType(List.class, MovieDto.class));

    // Validações de consistência
    assertThat(allMovies).isNotEmpty();
    assertThat(winners).isNotEmpty();

    // Todos os vencedores devem estar na lista geral
    List<Long> allMovieIds = allMovies.stream().map(MovieDto::getId).toList();
    List<Long> winnerIds = winners.stream().map(MovieDto::getId).toList();
    assertThat(allMovieIds).containsAll(winnerIds);

    // Quantidade de vencedores deve ser menor ou igual ao total
    assertThat(winners.size()).isLessThanOrEqualTo(allMovies.size());

    // Todos os filmes marcados como vencedores devem estar na lista de vencedores
    List<MovieDto> winnersFromAllMovies = allMovies.stream().filter(MovieDto::getWinner).toList();
    assertThat(winners.size()).isEqualTo(winnersFromAllMovies.size());
  }

  /**
   * Testa criação de novo filme via POST /api/v1/movies Deve retornar status 201 com header
   * Location e o filme criado
   */
  @Test
  @Transactional
  void shouldCreateNewMovie() throws Exception {
    MovieInputDto newMovie = new MovieInputDto();
    newMovie.setYear(2023);
    newMovie.setTitle("Test Movie for Creation");
    newMovie.setStudios("Test Studio");
    newMovie.setProducers(List.of("Test Producer"));
    newMovie.setWinner(false);

    String movieJson = objectMapper.writeValueAsString(newMovie);

    MvcResult result =
        mockMvc
            .perform(
                post("/api/v1/movies").contentType(MediaType.APPLICATION_JSON).content(movieJson))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.title").value("Test Movie for Creation"))
            .andExpect(jsonPath("$.year").value(2023))
            .andExpect(jsonPath("$.winner").value(false))
            .andReturn();

    String responseContent = result.getResponse().getContentAsString();
    MovieDto createdMovie = objectMapper.readValue(responseContent, MovieDto.class);

    assertThat(createdMovie.getId()).isNotNull();
    assertThat(createdMovie.getTitle()).isEqualTo("Test Movie for Creation");
    assertThat(createdMovie.getYear()).isEqualTo(2023);
  }

  /**
   * Testa busca de filme por ID via GET /api/v1/movies/{id} Deve retornar status 200 com o filme
   * encontrado
   */
  @Test
  void shouldGetMovieById() throws Exception {
    // Primeiro, obtém um filme existente para usar seu ID
    MvcResult allMoviesResult =
        mockMvc.perform(get("/api/v1/movies")).andExpect(status().isOk()).andReturn();

    List<MovieDto> movies =
        objectMapper.readValue(
            allMoviesResult.getResponse().getContentAsString(),
            objectMapper.getTypeFactory().constructCollectionType(List.class, MovieDto.class));

    assertThat(movies).isNotEmpty();
    Long movieId = movies.get(0).getId();

    // Testa busca por ID
    mockMvc
        .perform(get("/api/v1/movies/{id}", movieId).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(movieId))
        .andExpect(jsonPath("$.title").isNotEmpty())
        .andExpect(jsonPath("$.year").isNotEmpty());
  }

  /** Testa busca de filme inexistente por ID Deve retornar status 400 (BusinessException) */
  @Test
  void shouldReturn400ForNonExistentMovieId() throws Exception {
    Long nonExistentId = 999999L;

    mockMvc
        .perform(get("/api/v1/movies/{id}", nonExistentId).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.title").value("Erro de Negócio"));
  }

  /**
   * Testa atualização de filme via PUT /api/v1/movies/{id} Deve retornar status 200 com o filme
   * atualizado
   */
  @Test
  @Transactional
  void shouldUpdateExistingMovie() throws Exception {
    // Primeiro, cria um filme para atualizar
    MovieInputDto newMovie = new MovieInputDto();
    newMovie.setYear(2023);
    newMovie.setTitle("Movie to Update");
    newMovie.setStudios("Original Studio");
    newMovie.setProducers(List.of("Original Producer"));
    newMovie.setWinner(false);

    String createJson = objectMapper.writeValueAsString(newMovie);

    MvcResult createResult =
        mockMvc
            .perform(
                post("/api/v1/movies").contentType(MediaType.APPLICATION_JSON).content(createJson))
            .andExpect(status().isCreated())
            .andReturn();

    MovieDto createdMovie =
        objectMapper.readValue(createResult.getResponse().getContentAsString(), MovieDto.class);

    // Agora atualiza o filme
    MovieInputDto updateMovie = new MovieInputDto();
    updateMovie.setYear(2024);
    updateMovie.setTitle("Updated Movie Title");
    updateMovie.setStudios("Updated Studio");
    updateMovie.setProducers(List.of("Updated Producer"));
    updateMovie.setWinner(true);

    String updateJson = objectMapper.writeValueAsString(updateMovie);

    mockMvc
        .perform(
            put("/api/v1/movies/{id}", createdMovie.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(createdMovie.getId()))
        .andExpect(jsonPath("$.title").value("Updated Movie Title"))
        .andExpect(jsonPath("$.year").value(2024))
        .andExpect(jsonPath("$.winner").value(true));
  }

  /** Testa deleção de filme via DELETE /api/v1/movies/{id} Deve retornar status 204 No Content */
  @Test
  @Transactional
  void shouldDeleteExistingMovie() throws Exception {
    // Primeiro, cria um filme para deletar
    MovieInputDto newMovie = new MovieInputDto();
    newMovie.setYear(2023);
    newMovie.setTitle("Movie to Delete");
    newMovie.setStudios("Test Studio");
    newMovie.setProducers(List.of("Test Producer"));
    newMovie.setWinner(false);

    String createJson = objectMapper.writeValueAsString(newMovie);

    MvcResult createResult =
        mockMvc
            .perform(
                post("/api/v1/movies").contentType(MediaType.APPLICATION_JSON).content(createJson))
            .andExpect(status().isCreated())
            .andReturn();

    MovieDto createdMovie =
        objectMapper.readValue(createResult.getResponse().getContentAsString(), MovieDto.class);

    // Deleta o filme
    mockMvc
        .perform(delete("/api/v1/movies/{id}", createdMovie.getId()))
        .andExpect(status().isNoContent());

    // Verifica se o filme foi realmente deletado
    mockMvc
        .perform(
            get("/api/v1/movies/{id}", createdMovie.getId())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  /**
   * Testa validação de dados inválidos na criação Deve retornar status 400 com detalhes do erro de
   * validação
   */
  @Test
  void shouldReturn400ForInvalidMovieData() throws Exception {
    MovieInputDto invalidMovie = new MovieInputDto();
    // Deixa campos obrigatórios vazios para testar validação
    invalidMovie.setYear(1800); // Ano inválido (muito antigo)
    invalidMovie.setTitle(""); // Título vazio
    invalidMovie.setStudios(""); // String vazia
    invalidMovie.setProducers(List.of()); // Lista vazia

    String movieJson = objectMapper.writeValueAsString(invalidMovie);

    mockMvc
        .perform(post("/api/v1/movies").contentType(MediaType.APPLICATION_JSON).content(movieJson))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.title").value("Erro de Validação"));
  }

  /**
   * Testa criação de filme duplicado (mesmo título e ano) Deve retornar status 400 com erro de
   * negócio
   */
  @Test
  @Transactional
  void shouldReturn400ForDuplicateMovie() throws Exception {
    MovieInputDto movie1 = new MovieInputDto();
    movie1.setYear(2023);
    movie1.setTitle("Duplicate Test Movie");
    movie1.setStudios("Test Studio");
    movie1.setProducers(List.of("Test Producer"));
    movie1.setWinner(false);

    String movieJson = objectMapper.writeValueAsString(movie1);

    // Cria o primeiro filme
    mockMvc
        .perform(post("/api/v1/movies").contentType(MediaType.APPLICATION_JSON).content(movieJson))
        .andExpect(status().isCreated());

    // Tenta criar o mesmo filme novamente
    mockMvc
        .perform(post("/api/v1/movies").contentType(MediaType.APPLICATION_JSON).content(movieJson))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.title").value("Erro de Negócio"));
  }
}
