package com.sharif.thunder.handler.entity;

public class RequestProperty {

  private final String header;
  private final String directive;

  public RequestProperty(String header, String directive) {
    this.header = header;
    this.directive = directive;
  }

  public String getHeader() {
    return header;
  }

  public String getDirective() {
    return directive;
  }
}
