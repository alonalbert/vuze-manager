package com.alon.vuze.vuzemanager.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class NetworkUtils {
  private static final String LOCALHOST = "localhost";

  public static String getLocalhostAddress() {
    final Enumeration<NetworkInterface> networkInterfaces;
    try {
      networkInterfaces = NetworkInterface.getNetworkInterfaces();
    } catch (SocketException e) {
      return LOCALHOST;
    }
    while (networkInterfaces.hasMoreElements()) {
      final NetworkInterface networkInterface = networkInterfaces.nextElement();
      final Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
      while (addresses.hasMoreElements()) {
        final InetAddress address = addresses.nextElement();
        if (address instanceof Inet4Address) {
          final Inet4Address v4 = (Inet4Address) address;
          if (!v4.isLoopbackAddress()) {
            return v4.getHostAddress();
          }
        }
      }
    }
    return LOCALHOST;
  }
}
