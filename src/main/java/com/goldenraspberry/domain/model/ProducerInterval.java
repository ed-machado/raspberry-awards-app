package com.goldenraspberry.domain.model;

import java.util.Objects;

/**
 * ProducerInterval value object Representa o intervalo entre vitorias consecutivas de um Producer
 */
public class ProducerInterval {

  private final Producer producer;
  private final int interval;
  private final Year previousWin;
  private final Year followingWin;

  public ProducerInterval(Producer producer, int interval, Year previousWin, Year followingWin) {
    this.producer = Objects.requireNonNull(producer, "Producer nao pode ser nulo");
    this.interval = interval;
    this.previousWin = Objects.requireNonNull(previousWin, "PreviousWin nao pode ser nulo");
    this.followingWin = Objects.requireNonNull(followingWin, "FollowingWin nao pode ser nulo");

    validateInterval();
  }

  private void validateInterval() {
    if (interval < 0) {
      throw new IllegalArgumentException("Interval nao pode ser negativo");
    }

    int calculatedInterval = followingWin.getValue() - previousWin.getValue();
    if (interval != calculatedInterval) {
      throw new IllegalArgumentException(
          String.format(
              "Interval informado (%d) nao confere com calculo (%d)",
              interval, calculatedInterval));
    }
  }

  public Producer getProducer() {
    return producer;
  }

  public int getInterval() {
    return interval;
  }

  public Year getPreviousWin() {
    return previousWin;
  }

  public Year getFollowingWin() {
    return followingWin;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ProducerInterval that = (ProducerInterval) o;
    return interval == that.interval
        && Objects.equals(producer, that.producer)
        && Objects.equals(previousWin, that.previousWin)
        && Objects.equals(followingWin, that.followingWin);
  }

  @Override
  public int hashCode() {
    return Objects.hash(producer, interval, previousWin, followingWin);
  }

  @Override
  public String toString() {
    return "ProducerInterval{"
        + "producer="
        + producer
        + ", interval="
        + interval
        + ", previousWin="
        + previousWin
        + ", followingWin="
        + followingWin
        + '}';
  }
}
