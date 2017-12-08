package com.wzh.fun.entity;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVStatus;

public class Status {
  private AVStatus innerStatus;
  private AVObject detail;

  public AVStatus getInnerStatus() {
    return innerStatus;
  }

  public void setInnerStatus(AVStatus innerStatus) {
    this.innerStatus = innerStatus;
  }

  public AVObject getDetail() {
    return detail;
  }

  public void setDetail(AVObject detail) {
    this.detail = detail;
  }
}
