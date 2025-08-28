package com.goldenraspberry.application.dto;

import jakarta.validation.constraints.*;
import java.util.List;

/** DTO para entrada de dados de filme (POST/PUT) */
public class MovieInputDto {

  @NotNull(message = "Ano é obrigatório")
  @Min(value = 1900, message = "Ano deve ser maior ou igual a 1900")
  @Max(value = 2100, message = "Ano deve ser menor ou igual a 2100")
  private Integer year;

  @NotBlank(message = "Título é obrigatório")
  @Size(min = 1, max = 255, message = "Título deve ter entre 1 e 255 caracteres")
  private String title;

  @Size(max = 500, message = "Estúdios deve ter no máximo 500 caracteres")
  private String studios;

  @NotEmpty(message = "Lista de produtores não pode estar vazia")
  @Size(min = 1, message = "Deve haver pelo menos um produtor")
  private List<@NotBlank(message = "Nome do produtor não pode estar vazio") String> producers;

  @NotNull(message = "Status de vencedor é obrigatório")
  private Boolean winner;

  public MovieInputDto() {}

  public MovieInputDto(
      Integer year, String title, String studios, List<String> producers, Boolean winner) {
    this.year = year;
    this.title = title;
    this.studios = studios;
    this.producers = producers;
    this.winner = winner;
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

  @Override
  public String toString() {
    return "MovieInputDto{"
        + "year="
        + year
        + ", title='"
        + title
        + '\''
        + ", studios='"
        + studios
        + '\''
        + ", producers="
        + producers
        + ", winner="
        + winner
        + '}';
  }
}
