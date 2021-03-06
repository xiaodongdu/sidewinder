/**
 * Copyright 2017 Ambud Sharma
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.srotya.sidewinder.cluster.storage;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

import com.srotya.linea.clustering.WorkerEntry;
import com.srotya.sidewinder.core.storage.DataPoint;

/**
 * @author ambud
 *
 */
public class TCPClient {

	private static Logger logger = Logger.getLogger(TCPClient.class.getName());
	private BufferedOutputStream outputStream;
	private WorkerEntry entry;

	public TCPClient(WorkerEntry entry) {
		this.entry = entry;
	}

	public void connect() throws IOException {
		retryConnectLoop(entry);
	}

	public void disconnect() throws IOException {
		outputStream.close();
	}

	public void write(DataPoint dp) throws IOException {
		ByteBuffer buf = ByteBuffer.allocate(1024);
		encodeDPointToBuf(buf, dp);
		try {
			outputStream.write(buf.array());
		} catch (Exception e) {
			retryConnectLoop(entry);
		}
	}

	public static void encodeDPointToBuf(ByteBuffer buf, DataPoint dp) {
		byte[] db = dp.getDbName().getBytes();
		buf.putInt(db.length);
		buf.put(db);
		byte[] measurement = dp.getMeasurementName().getBytes();
		buf.putInt(measurement.length);
		buf.put(measurement);
		buf.putLong(dp.getTimestamp());
		if (dp.isFp()) {
			buf.putChar('0');
			buf.putDouble(dp.getValue());
		}else {
			buf.putChar('1');
			buf.putDouble(dp.getLongValue());
		}
	}

	private void retryConnectLoop(WorkerEntry value) throws IOException {
		boolean connected = false;
		int retryCount = 1;
		while (!connected && retryCount < 100) {
			Socket socket = tryConnect(value, retryCount);
			if (socket != null) {
				connected = true;
				outputStream = new BufferedOutputStream(socket.getOutputStream(), 8192 * 4);
			}
		}
	}

	private Socket tryConnect(WorkerEntry value, int retryCount) {
		try {
			Socket socket = new Socket(value.getWorkerAddress(), value.getDataPort());
			socket.setSendBufferSize(1048576);
			socket.setKeepAlive(true);
			socket.setPerformancePreferences(0, 1, 2);
			return socket;
		} catch (Exception e) {
			logger.warning("Worker connection refused:" + value.getWorkerAddress() + ". Retrying in " + retryCount
					+ " seconds.....");
			retryCount++;
			try {
				Thread.sleep(1000 * retryCount);
			} catch (InterruptedException e1) {
				return null;
			}
		}
		return null;
	}

}
