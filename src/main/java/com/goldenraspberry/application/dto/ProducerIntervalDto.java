package com.goldenraspberry.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

/**
 * DTO para representar um intervalo Contem informacoes sobre o produtor e o intervalo entre premios
 */
public class ProducerIntervalDto {

  @NotBlank(message = "Nome do produtor é obrigatório")
  @Size(max = 255, message = "Nome do produtor deve ter no máximo 255 caracteres")
  private String producer;

  @NotNull(message = "Intervalo é obrigatório")
  @Min(value = 0, message = "Intervalo deve ser maior ou igual a 0")
  private Integer interval;

  @NotNull(message = "Ano da vitória anterior é obrigatório")
  @Min(value = 1900, message = "Ano deve ser maior que 1900")
  @Max(value = 2100, message = "Ano deve ser menor que 2100")
  @JsonProperty("previous_win")
  private Integer previousWin;

  @NotNull(message = "Ano da vitória seguinte é obrigatório")
  @Min(value = 1900, message = "Ano deve ser maior que 1900")
  @Max(value = 2100, message = "Ano deve ser menor que 2100")
  @JsonProperty("following_win")
  private Integer followingWin;

  public ProducerIntervalDto() {}

  public ProducerIntervalDto(
      String producer, Integer interval, Integer previousWin, Integer followingWin) {
    this.producer = producer;
    this.interval = interval;
    this.previousWin = previousWin;
    this.followingWin = followingWin;
  }

  public String getProducer() {
    return producer;
  }

  public void setProducer(String producer) {
    this.producer = producer;
  }

  public Integer getInterval() {
    return interval;
  }

  public void setInterval(Integer interval) {
    this.interval = interval;
  }

  public Integer getPreviousWin() {
    return previousWin;
  }

  public void setPreviousWin(Integer previousWin) {
    this.previousWin = previousWin;
  }

  public Integer getFollowingWin() {
    return followingWin;
  }

  public void setFollowingWin(Integer followingWin) {
    this.followingWin = followingWin;
  }

  @Override
  public String toString() {
    return "ProducerIntervalDto{"
        + "producer='"
        + producer
        + '\''
        + ", interval="
        + interval
        + ", previousWin="
        + previousWin
        + ", followingWin="
        + followingWin
        + '}';
  }
}
