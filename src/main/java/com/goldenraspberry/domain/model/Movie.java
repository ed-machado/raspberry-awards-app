package com.goldenraspberry.domain.model;

import java.util.List;
import java.util.Objects;

/**
 * Movie entity
 * Representa um filme com seus dados basicos
 */
public class Movie {

    private final Long id;
    private final Year year;
    private final String title;
    private final String studios;
    private final List<Producer> producers;
    private final boolean winner;

    public Movie(Long id, Year year, String title, String studios, List<Producer> producers, boolean winner) {
        this.id = id;
        this.year = Objects.requireNonNull(year, "Year nao pode ser nulo");
        this.title = Objects.requireNonNull(title, "Title nao pode ser nulo");
        this.studios = studios;
        this.producers = Objects.requireNonNull(producers, "Producers nao podem ser nulos");
        this.winner = winner;

        validateTitle();
        validateProducers();
    }

    private void validateTitle() {
        if (title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title nao pode ser vazio");
        }
    }

    private void validateProducers() {
        if (producers.isEmpty()) {
            throw new IllegalArgumentException("Movie deve ter pelo menos um Producer");
        }
    }

    public Long getId() {
        return id;
    }

    public Year getYear() {
        return year;
    }

    public String getTitle() {
        return title;
    }

    public String getStudios() {
        return studios;
    }

    public List<Producer> getProducers() {
        return List.copyOf(producers);
    }

    public boolean isWinner() {
        return winner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return Objects.equals(id, movie.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", year=" + year +
                ", title='" + title + '\'' +
                ", winner=" + winner +
                '}';
    }
}
