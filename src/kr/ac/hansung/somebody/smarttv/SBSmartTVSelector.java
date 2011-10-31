package kr.ac.hansung.somebody.smarttv;

import kr.ac.hansung.somebody.R;
import kr.ac.hansung.somebody.motion.SBMotionJump;
import kr.ac.hansung.somebody.motion.SBMotionSitup;
import kr.ac.hansung.somebody.motion.SBMotionStretching;
import kr.ac.hansung.somebody.motion.SBMotionSwim;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class SBSmartTVSelector extends Activity implements SensorEventListener, OnClickListener {
	public static final int CONNECTING = 0;
	public static final int SENDING = 1;

	int selectflag= 0;
	TVClientSocket tv_client;	
	SensorManager sm = null; // SensorManager 객체
	String statusReceive = "";
	String sending_msg;

	public int activityflag = 0;
	public int btnflag = 0;
	Intent intent;

	// 메뉴선택 관련 selector
	int selector = 1;

	Button based_sensor; //센서의 기준값을 결정하는 버튼
	float based_sensor_value = -1; //센서의 기준값을 저장
	float temp_sensor_value = 0; //기준값을 넘겨줄 때 사용

	Button right_button;
	Button left_button;
	Button choice_menu;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sbtvselect);
		
		right_button = (Button)findViewById(R.id.right_button);
		left_button = (Button)findViewById(R.id.left_button);
		choice_menu = (Button)findViewById(R.id.choice_menu);
		
		right_button.setOnClickListener(this);
		left_button.setOnClickListener(this);
		choice_menu.setOnClickListener(this);

		tv_client = TVClientSocket.getInstance();
		tv_client.sending_msg = "1";
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		btnflag = 0;
		selectflag=0;
		// if (tv_client.tv_csc != null) {
		// try {
		// // cs.out.close(); // 스트림을 닫아서 자원을 환원함
		// tv_client.tv_csc.close(); // 소켓을 닫아서 자원은 환원함
		// tv_client.tv_csc = null; // 소켓에 null을 저장 함으로 써 닫은 소켓임을 정확하게 명시
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }

		//sm.unregisterListener(this,	sm.getDefaultSensor(Sensor.TYPE_ORIENTATION));
		//sm.unregisterListener(this,	sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));

	}

	@Override
	protected void onRestart() {
		super.onRestart();
		selector = 1;
		//tv_client.out.println("1");
		//sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ORIENTATION),	sm.SENSOR_DELAY_FASTEST); // SensorSpeed Fastest
		//sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),	sm.SENSOR_DELAY_FASTEST); // SensorSpeed Fastest
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			tv_client.closed_socket();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		synchronized (this) {
			// Intent msg_intent = new Intent(SENDING_MSG);
			switch (event.sensor.getType()) {
			case Sensor.TYPE_ORIENTATION:
				// 1번 가르키기
				if (event.values[0] > 200 && event.values[0] < 300) {
					selector = 1;
					tv_client.sending_msg = "" + selector;
				}
				// 2번 가르키기
				if (event.values[0] > 300 && event.values[0] < 366) {
					selector = 2;
					tv_client.sending_msg = "" + selector;
				}
				// 3번 가르키기
				if (event.values[0] > 0 && event.values[0] < 50) {
					selector = 3;
					tv_client.sending_msg = "" + selector;
				}
				// 4번 가르키기
				if (event.values[0] > 50 && event.values[0] < 120) {
					selector = 4;
					tv_client.sending_msg = "" + selector;
				}
			case Sensor.TYPE_ACCELEROMETER:
				if (event.values[1] >= 9) {
					Log.e("acc", "" + event.values[1]);
					tv_client.sending_msg = "select";
					selectflag = 1;
					SelectMotion(selector);

					selector = -1;
					activityflag = 1;
				}
			}
		}
	}

	void SelectMotion(int sel_num) {
		switch (sel_num) {
		case 1:
			if (btnflag == 0) {
				btnflag = 1;
				intent = new Intent(SBSmartTVSelector.this, SBMotionJump.class);
				intent.putExtra("select", sel_num);
				startActivity(intent);
			}
			break;
		case 2:
			if (btnflag == 0) {
				btnflag = 1;
				intent = new Intent(SBSmartTVSelector.this, SBMotionStretching.class);
				intent.putExtra("select", sel_num);
				startActivity(intent);
			}
			break;
		case 3:
			if (btnflag == 0) {
				btnflag = 1;
				intent = new Intent(SBSmartTVSelector.this, SBMotionSwim.class);
				intent.putExtra("select", sel_num);
				startActivity(intent);
			}
			break;
		case 4:
			if (btnflag == 0) {
				btnflag = 1;
				intent = new Intent(SBSmartTVSelector.this, SBMotionSitup.class);
				intent.putExtra("select", sel_num);
				startActivity(intent);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		if(v == right_button){
			selector++;
			if(selector == 5)
				selector = 1;
			tv_client.sending_msg = "" + selector;
		}
		else if(v == left_button){
			selector--;
			if(selector == 0)
				selector = 4;
			tv_client.sending_msg = "" + selector;
		}
		else if(v == choice_menu){
			tv_client.out.println("select");
			SelectMotion(selector);
			selector = -1;
		}
	}
}