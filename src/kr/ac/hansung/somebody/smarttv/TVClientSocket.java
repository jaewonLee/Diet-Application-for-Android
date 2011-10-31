package kr.ac.hansung.somebody.smarttv;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import android.os.Handler;
import android.util.Log;

public class TVClientSocket implements Runnable {
	public static final int CONNECTING = 0;
	public static final int SENDING = 1;

	public PrintWriter out;
	public Socket tv_csc;
	public static int flag = 0;
	static Thread th;
	boolean sensor_flag = false; // 센서 동작여부

	String sensorvalue;
	public static String print_msg;
	public String sending_msg = "";
	public static String ex_sending_msg = "";

	Handler msg_handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// msg 별 test setting 분류
			switch (msg.what) {
			case CONNECTING:
				print_msg = "C: Connecting...";
				break;
			case SENDING:
				print_msg = sending_msg;
				break;
			}
		};
	};

	private static TVClientSocket instance;

	private TVClientSocket(){

	}
	public static TVClientSocket getInstance() {
		if(instance == null)
		{
			instance = new TVClientSocket();
			th = new Thread(instance);
			th.start();
		}
		return instance;
	}
	@Override
	public void run() {
		try {
			String ip = "192.168.0.101";
			msg_handler.sendMessage(msg_handler.obtainMessage(CONNECTING));
			tv_csc = new Socket(ip, 7233);
			try {
				while(!th.interrupted())
				{
					if(!sending_msg.equals(null) && !ex_sending_msg.equals(sending_msg))
					{
						out = new PrintWriter(new BufferedWriter(
								new OutputStreamWriter(tv_csc.getOutputStream())), true);
						out.println(sending_msg);
						msg_handler.sendMessage(msg_handler.obtainMessage(SENDING));
						//Thread.sleep(1000);
						Log.d("client message","msg : "+sending_msg);
						ex_sending_msg = sending_msg;
					}
				}
			} catch (Exception e) {
				Log.e("TCP", "S: Error", e);
			} finally {
				if (tv_csc != null) {
					try {
						tv_csc.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			Log.e("TCP", "C: Error", e);
		}
	}
	public void closed_socket(){
		try {
			tv_csc.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(th != null && th.isAlive())
			th.interrupt();
		instance = null;
	}
}
