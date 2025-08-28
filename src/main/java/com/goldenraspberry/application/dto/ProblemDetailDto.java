package com.goldenraspberry.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Map;

/** DTO para Problem Details (RFC 7807) */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProblemDetailDto {

  private String type;
  private String title;
  private Integer status;
  private String detail;
  private String instance;
  private LocalDateTime timestamp;
  private Map<String, Object> extensions;

  public ProblemDetailDto() {
    this.timestamp = LocalDateTime.now();
  }

  public ProblemDetailDto(
      String type, String title, Integer status, String detail, String instance) {
    this();
    this.type = type;
    this.title = title;
    this.status = status;
    this.detail = detail;
    this.instance = instance;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public String getDetail() {
    return detail;
  }

  public void setDetail(String detail) {
    this.detail = detail;
  }

  public String getInstance() {
    return instance;
  }

  public void setInstance(String instance) {
    this.instance = instance;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public Map<String, Object> getExtensions() {
    return extensions;
  }

  public void setExtensions(Map<String, Object> extensions) {
    this.extensions = extensions;
  }
}
