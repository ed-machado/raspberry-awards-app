package com.goldenraspberry.infrastructure.persistence;

import com.goldenraspberry.domain.model.Movie;
import com.goldenraspberry.domain.model.Year;
import com.goldenraspberry.domain.port.MovieRepository;
import com.goldenraspberry.infrastructure.persistence.entity.MovieJpaEntity;
import com.goldenraspberry.infrastructure.persistence.mapper.MovieEntityMapper;
import com.goldenraspberry.infrastructure.persistence.repository.MovieJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/** MovieRepository com persistencia JPA Implementacao do repositorio de filmes usando JPA */
@Repository
public class InMemoryMovieRepository implements MovieRepository {

  private final MovieJpaRepository jpaRepository;
  private final MovieEntityMapper entityMapper;

  @Autowired
  public InMemoryMovieRepository(MovieJpaRepository jpaRepository, MovieEntityMapper entityMapper) {
    this.jpaRepository = jpaRepository;
    this.entityMapper = entityMapper;
  }

  @Override
  public List<Movie> findAll() {
    List<MovieJpaEntity> entities = jpaRepository.findAll();
    return entityMapper.toDomainList(entities);
  }

  @Override
  public Optional<Movie> findById(Long id) {
    Optional<MovieJpaEntity> entity = jpaRepository.findById(id);
    return entity.map(entityMapper::toDomain);
  }

  @Override
  public List<Movie> findAllWinners() {
    List<MovieJpaEntity> entities = jpaRepository.findByWinnerTrue();
    return entityMapper.toDomainList(entities);
  }

  @Override
  public List<Movie> findByYear(Year year) {
    List<MovieJpaEntity> entities = jpaRepository.findByYear(year.getValue());
    return entityMapper.toDomainList(entities);
  }

  @Override
  public List<Movie> findByProducerName(String producerName) {
    List<MovieJpaEntity> entities = jpaRepository.findByProducerNameContaining(producerName);
    return entityMapper.toDomainList(entities);
  }

  @Override
  public Movie save(Movie movie) {
    MovieJpaEntity entity = entityMapper.toEntity(movie);
    MovieJpaEntity savedEntity = jpaRepository.save(entity);
    return entityMapper.toDomain(savedEntity);
  }

  @Override
  public List<Movie> saveAll(List<Movie> moviesToSave) {
    List<MovieJpaEntity> entities = entityMapper.toEntityList(moviesToSave);
    List<MovieJpaEntity> savedEntities = jpaRepository.saveAll(entities);
    return entityMapper.toDomainList(savedEntities);
  }

  @Override
  public void deleteById(Long id) {
    jpaRepository.deleteById(id);
  }

  @Override
  public void deleteAll() {
    jpaRepository.deleteAll();
  }

  @Override
  public boolean existsById(Long id) {
    return jpaRepository.existsById(id);
  }

  @Override
  public boolean existsByTitleAndYear(String title, Integer year) {
    return jpaRepository.existsByTitleAndYear(title, year);
  }

  @Override
  public long count() {
    return jpaRepository.count();
  }
}
