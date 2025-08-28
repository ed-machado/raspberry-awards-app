package com.goldenraspberry.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/** DTO para resposta paginada */
@Schema(description = "Resposta paginada com metadados")
public class PagedResponseDto<T> {

  @Schema(description = "Lista de itens da página atual")
  private List<T> content;

  @Schema(description = "Número da página atual (baseado em zero)")
  private int page;

  @Schema(description = "Tamanho da página")
  private int size;

  @Schema(description = "Total de elementos")
  private long totalElements;

  @Schema(description = "Total de páginas")
  private int totalPages;

  @Schema(description = "Se é a primeira página")
  private boolean first;

  @Schema(description = "Se é a última página")
  private boolean last;

  @Schema(description = "Se tem próxima página")
  private boolean hasNext;

  @Schema(description = "Se tem página anterior")
  private boolean hasPrevious;

  public PagedResponseDto() {}

  public PagedResponseDto(List<T> content, int page, int size, long totalElements) {
    this.content = content;
    this.page = page;
    this.size = size;
    this.totalElements = totalElements;
    this.totalPages = (int) Math.ceil((double) totalElements / size);
    this.first = page == 0;
    this.last = page >= totalPages - 1;
    this.hasNext = page < totalPages - 1;
    this.hasPrevious = page > 0;
  }

  public List<T> getContent() {
    return content;
  }

  public void setContent(List<T> content) {
    this.content = content;
  }

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public long getTotalElements() {
    return totalElements;
  }

  public void setTotalElements(long totalElements) {
    this.totalElements = totalElements;
  }

  public int getTotalPages() {
    return totalPages;
  }

  public void setTotalPages(int totalPages) {
    this.totalPages = totalPages;
  }

  public boolean isFirst() {
    return first;
  }

  public void setFirst(boolean first) {
    this.first = first;
  }

  public boolean isLast() {
    return last;
  }

  public void setLast(boolean last) {
    this.last = last;
  }

  public boolean isHasNext() {
    return hasNext;
  }

  public void setHasNext(boolean hasNext) {
    this.hasNext = hasNext;
  }

  public boolean isHasPrevious() {
    return hasPrevious;
  }

  public void setHasPrevious(boolean hasPrevious) {
    this.hasPrevious = hasPrevious;
  }
}
