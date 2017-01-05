package com.alon.vuze.vuzemanager.testing;

import com.alon.vuze.vuzemanager.utils.NetworkUtils;

import java.io.IOException;

class Main {



  public static void main(String[] args) throws IOException {

    System.out.println(NetworkUtils.getLocalhostAddress());
  }

}