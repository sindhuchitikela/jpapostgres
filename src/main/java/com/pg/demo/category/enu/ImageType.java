package com.pg.demo.category.enu;

public enum ImageType {

  CATEGORY("category");

  private final String val;

  private ImageType(String val) {
    this.val = val;
  }

  public String getVal() {
    return val;
  }
}
