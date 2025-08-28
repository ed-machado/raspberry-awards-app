package com.goldenraspberry.application.dto;

import java.util.List;

/**
 * DTO de resposta para intervalos Representa os intervalos minimos e maximos entre premios
 * consecutivos
 */
public class ProducerIntervalResponseDto {

  private List<ProducerIntervalDto> min;
  private List<ProducerIntervalDto> max;

  public ProducerIntervalResponseDto() {}

  public ProducerIntervalResponseDto(List<ProducerIntervalDto> min, List<ProducerIntervalDto> max) {
    this.min = min;
    this.max = max;
  }

  public List<ProducerIntervalDto> getMin() {
    return min;
  }

  public void setMin(List<ProducerIntervalDto> min) {
    this.min = min;
  }

  public List<ProducerIntervalDto> getMax() {
    return max;
  }

  public void setMax(List<ProducerIntervalDto> max) {
    this.max = max;
  }
}
