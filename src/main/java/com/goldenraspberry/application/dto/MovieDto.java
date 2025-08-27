package com.goldenraspberry.application.dto;

import java.util.List;

/**
 * DTO para representar um filme na resposta da API
 * Contem todas as informacoes relevantes do filme
 */
public class MovieDto {

    private Long id;
    private Integer year;
    private String title;
    private String studios;
    private List<String> producers;
    private Boolean winner;

    public MovieDto() {
    }

    public MovieDto(Long id, Integer year, String title, String studios, List<String> producers, Boolean winner) {
        this.id = id;
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

    public List<String> getProducers() {
        return producers;
    }

    public void setProducers(List<String> producers) {
        this.producers = producers;
    }

    public Boolean getWinner() {
        return winner;
    }

    public void setWinner(Boolean winner) {
        this.winner = winner;
    }
}
