package com.goldenraspberry.infrastructure.persistence.mapper;

import com.goldenraspberry.domain.model.Movie;
import com.goldenraspberry.domain.model.Producer;
import com.goldenraspberry.domain.model.Year;
import com.goldenraspberry.infrastructure.persistence.entity.MovieJpaEntity;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/** Movie Entity Mapper Mapper para conversao entre entidade JPA e modelo de dominio */
@Component
public class MovieEntityMapper {

  private static final String PRODUCER_SEPARATOR = ", ";
  private static final String PRODUCER_AND_SEPARATOR = " and ";

  /**
   * Converte entidade JPA para modelo de dominio
   *
   * @param entity Entidade JPA
   * @return Modelo de dominio
   */
  public Movie toDomain(MovieJpaEntity entity) {
    if (entity == null) {
      return null;
    }

    return new Movie(
        entity.getId(),
        new Year(entity.getYear()),
        entity.getTitle(),
        entity.getStudios(),
        parseProducers(entity.getProducers()),
        entity.getWinner());
  }

  /**
   * Converte modelo de dominio para entidade JPA
   *
   * @param movie Modelo de dominio
   * @return Entidade JPA
   */
  public MovieJpaEntity toEntity(Movie movie) {
    if (movie == null) {
      return null;
    }

    MovieJpaEntity entity =
        new MovieJpaEntity(
            movie.getYear().getValue(),
            movie.getTitle(),
            movie.getStudios(),
            formatProducers(movie.getProducers()),
            movie.isWinner());

    if (movie.getId() != null) {
      entity.setId(movie.getId());
    }

    return entity;
  }

  /**
   * Converte lista de entidades JPA para lista de modelos de dominio
   *
   * @param entities Lista de entidades JPA
   * @return Lista de modelos de dominio
   */
  public List<Movie> toDomainList(List<MovieJpaEntity> entities) {
    if (entities == null) {
      return List.of();
    }

    return entities.stream().map(this::toDomain).collect(Collectors.toList());
  }

  /**
   * Converte lista de modelos de dominio para lista de entidades JPA
   *
   * @param movies Lista de modelos de dominio
   * @return Lista de entidades JPA
   */
  public List<MovieJpaEntity> toEntityList(List<Movie> movies) {
    if (movies == null) {
      return List.of();
    }

    return movies.stream().map(this::toEntity).collect(Collectors.toList());
  }

  /**
   * Faz parsing da string de producers para lista de objetos Producer
   *
   * @param producersString String com producers separados por virgula ou 'and'
   * @return Lista de producers
   */
  private List<Producer> parseProducers(String producersString) {
    if (producersString == null || producersString.trim().isEmpty()) {
      throw new IllegalArgumentException("Producers string nao pode ser nula ou vazia");
    }

    // Primeiro substitui ' and ' por ', ' para padronizar o separador
    String normalizedProducers =
        producersString.replace(PRODUCER_AND_SEPARATOR, PRODUCER_SEPARATOR);

    return Arrays.stream(normalizedProducers.split(PRODUCER_SEPARATOR))
        .map(String::trim)
        .filter(name -> !name.isEmpty())
        .map(Producer::new)
        .collect(Collectors.toList());
  }

  /**
   * Formata lista de producers para string separada por virgulas
   *
   * @param producers Lista de producers
   * @return String formatada
   */
  private String formatProducers(List<Producer> producers) {
    if (producers == null || producers.isEmpty()) {
      throw new IllegalArgumentException("Lista de producers nao pode ser nula ou vazia");
    }

    return producers.stream()
        .map(Producer::getName)
        .collect(Collectors.joining(PRODUCER_SEPARATOR));
  }
}
