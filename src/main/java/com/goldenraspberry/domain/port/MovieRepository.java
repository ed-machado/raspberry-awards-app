package com.goldenraspberry.domain.port;

import com.goldenraspberry.domain.model.Movie;
import com.goldenraspberry.domain.model.Year;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Repository interface de Movie */
public interface MovieRepository {

  /**
   * Encontra todos os filmes
   *
   * @return Lista de todos os filmes
   */
  List<Movie> findAll();

  /**
   * Encontra todos os filmes com paginação
   *
   * @param pageable Configuração de paginação
   * @return Página de filmes
   */
  Page<Movie> findAll(Pageable pageable);

  /**
   * Encontra filme por ID
   *
   * @param id ID do Movie
   * @return Optional contendo o filme se encontrado
   */
  Optional<Movie> findById(Long id);

  /**
   * Encontra todos os filmes vencedores
   *
   * @return Lista de filmes que ganharam premios
   */
  List<Movie> findAllWinners();

  /**
   * Encontra filmes por ano
   *
   * @param year Ano do Movie
   * @return Lista de filmes do ano especificado
   */
  List<Movie> findByYear(Year year);

  /**
   * Encontra filmes por nome do Producer
   *
   * @param producerName Nome do Producer
   * @return Lista de filmes do Producer especificado
   */
  List<Movie> findByProducerName(String producerName);

  /**
   * Salva filme
   *
   * @param movie Movie para salvar
   * @return Movie salvo
   */
  Movie save(Movie movie);

  /**
   * Salva todos os filmes
   *
   * @param movies Movies para salvar
   * @return Lista de Movies salvos
   */
  List<Movie> saveAll(List<Movie> movies);

  /**
   * Deleta filme por ID
   *
   * @param id ID do Movie para deletar
   */
  void deleteById(Long id);

  /** Deleta todos os filmes */
  void deleteAll();

  /**
   * Verifica se existe filme por ID
   *
   * @param id ID do Movie
   * @return true se existe, false caso contrário
   */
  boolean existsById(Long id);

  /**
   * Verifica se existe filme com título e ano específicos
   *
   * @param title Título do filme
   * @param year Ano do filme
   * @return true se existe, false caso contrário
   */
  boolean existsByTitleAndYear(String title, Integer year);

  /**
   * Conta total de filmes
   *
   * @return Número total de filmes
   */
  long count();
}
