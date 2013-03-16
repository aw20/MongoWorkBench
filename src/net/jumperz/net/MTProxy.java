package net.jumperz.net;


public class MTProxy {
	// --------------------------------------------------------------------------------
	public static native int tProxy(int socket, String serverHost, int serverPort, String clientHost, int clientPort, String proxyHost);

	// --------------------------------------------------------------------------------
}