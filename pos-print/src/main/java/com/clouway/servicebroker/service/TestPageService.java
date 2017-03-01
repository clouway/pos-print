package com.clouway.servicebroker.service;

import com.google.sitebricks.At;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Service;
import com.google.sitebricks.http.Get;
import com.google.sitebricks.http.Post;

/**
 * @author Lazo Apostolovski (lazo.apostolovski@gmail.com)
 */
@At("test")
@Service
public class TestPageService {
  @Post
  @Get
  public Reply<String> printReceipt() {
    return Reply.with("Im alive!");
  }
}
