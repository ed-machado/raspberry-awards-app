package com.goldenraspberry.domain.model;

import java.util.Objects;

/** Producer value object Representa um Producer de filme com nome imutavel */
public class Producer {

  private final String name;

  public Producer(String name) {
    this.name = Objects.requireNonNull(name, "Nome do Producer nao pode ser nulo");
    validateName();
  }

  private void validateName() {
    if (name.trim().isEmpty()) {
      throw new IllegalArgumentException("Nome do Producer nao pode ser vazio");
    }
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Producer producer = (Producer) o;
    return Objects.equals(name, producer.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public String toString() {
    return "Producer{" + "name='" + name + '\'' + '}';
  }
}
