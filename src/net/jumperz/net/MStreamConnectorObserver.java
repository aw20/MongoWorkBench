package net.jumperz.net;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.jumperz.util.MObserver1;
import net.jumperz.util.MStreamConnector;
import net.jumperz.util.MSystemUtil;

public class MStreamConnectorObserver implements MObserver1 {
	private MStreamConnector streamConnector;

	private List socketList = new ArrayList();

	// --------------------------------------------------------------------------------
	public MStreamConnectorObserver(MStreamConnector streamConnector) {
		this.streamConnector = streamConnector;
	}

	// --------------------------------------------------------------------------------
	public void update() {
		int state = streamConnector.getState();
		if (state == MStreamConnector.CLOSED) {
			try {
				Thread.sleep(2000);
			} catch (Exception e) {
			}
			Iterator p = socketList.iterator();
			while (p.hasNext()) {
				Socket socket = (Socket) p.next();
				MSystemUtil.closeSocket(socket);
			}
		}
	}

	// --------------------------------------------------------------------------------
	public void addSocket(Socket socket) {
		socketList.add(socket);
	}
	// --------------------------------------------------------------------------------
}