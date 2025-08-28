package com.goldenraspberry.infrastructure.persistence.repository;

import com.goldenraspberry.infrastructure.persistence.entity.MovieJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** Movie JPA Repository */
@Repository
public interface MovieJpaRepository extends JpaRepository<MovieJpaEntity, Long> {

  /**
   * Busca todos os filmes vencedores
   *
   * @return Lista de filmes vencedores
   */
  List<MovieJpaEntity> findByWinnerTrue();

  /**
   * Busca filmes por ano
   *
   * @param year Ano do filme
   * @return Lista de filmes do ano especificado
   */
  List<MovieJpaEntity> findByYear(Integer year);

  /**
   * Busca filmes vencedores por ano
   *
   * @param year Ano do filme
   * @return Lista de filmes vencedores do ano especificado
   */
  List<MovieJpaEntity> findByYearAndWinnerTrue(Integer year);

  /**
   * Busca filmes que contem um produtor especifico
   *
   * @param producerName Nome do produtor
   * @return Lista de filmes do produtor
   */
  @Query("SELECT m FROM MovieJpaEntity m WHERE m.producers LIKE CONCAT('%', :producerName, '%')")
  List<MovieJpaEntity> findByProducerNameContaining(@Param("producerName") String producerName);

  /**
   * Verifica se existe filme com titulo e ano especificos
   *
   * @param title Titulo do filme
   * @param year Ano do filme
   * @return true se existe, false caso contrario
   */
  boolean existsByTitleAndYear(String title, Integer year);

  /**
   * Busca filmes vencedores que contem um produtor especifico
   *
   * @param producerName Nome do produtor
   * @return Lista de filmes vencedores do produtor
   */
  @Query(
      "SELECT m FROM MovieJpaEntity m WHERE m.producers LIKE CONCAT('%', :producerName, '%') AND"
          + " m.winner = true")
  List<MovieJpaEntity> findByProducersContainingAndWinnerTrue(
      @Param("producerName") String producerName);

  /**
   * Busca todos os anos distintos de filmes vencedores
   *
   * @return Lista de anos com filmes vencedores
   */
  @Query("SELECT DISTINCT m.year FROM MovieJpaEntity m WHERE m.winner = true ORDER BY m.year")
  List<Integer> findDistinctYearsByWinnerTrueOrderByYear();

  /**
   * Conta total de filmes
   *
   * @return Numero total de filmes
   */
  long count();

  /**
   * Conta filmes vencedores
   *
   * @return Numero de filmes vencedores
   */
  long countByWinnerTrue();
}
