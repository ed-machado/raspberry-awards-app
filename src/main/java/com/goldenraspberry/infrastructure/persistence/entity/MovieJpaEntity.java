package com.goldenraspberry.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.util.Objects;

/** Movie JPA Entity */
@Entity
@Table(
    name = "movies",
    indexes = {
      @Index(name = "idx_movie_year", columnList = "movie_year"),
      @Index(name = "idx_movie_winner", columnList = "winner")
    })
public class MovieJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "movie_year", nullable = false)
  private Integer year;

  @Column(name = "title", nullable = false, length = 500)
  private String title;

  @Column(name = "studios", length = 500)
  private String studios;

  @Column(name = "producers", nullable = false, length = 1000)
  private String producers; // Armazenado como string separada por virgulas

  @Column(name = "winner", nullable = false)
  private Boolean winner;

  public MovieJpaEntity() {}

  public MovieJpaEntity(
      Integer year, String title, String studios, String producers, Boolean winner) {
    this.year = year;
    this.title = title;
    this.studios = studios;
    this.producers = producers;
    this.winner = winner;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Integer getYear() {
    return year;
  }

  public void setYear(Integer year) {
    this.year = year;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getStudios() {
    return studios;
  }

  public void setStudios(String studios) {
    this.studios = studios;
  }

  public String getProducers() {
    return producers;
  }

  public void setProducers(String producers) {
    this.producers = producers;
  }

  public Boolean getWinner() {
    return winner;
  }

  public void setWinner(Boolean winner) {
    this.winner = winner;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MovieJpaEntity that = (MovieJpaEntity) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "MovieJpaEntity{"
        + "id="
        + id
        + ", year="
        + year
        + ", title='"
        + title
        + '\''
        + ", studios='"
        + studios
        + '\''
        + ", producers='"
        + producers
        + '\''
        + ", winner="
        + winner
        + '}';
  }
}
