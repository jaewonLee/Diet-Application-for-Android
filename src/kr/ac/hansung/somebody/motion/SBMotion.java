package kr.ac.hansung.somebody.motion;

import kr.ac.hansung.somebody.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * 모션 게임을 선택하는 모션 게임 선택 클래스
 * 
 * @author Jaewon Lee(jaewon87@naver.com)
 * @version 1.0
 */
public class SBMotion extends Activity implements OnClickListener {
	Intent intent;
	public ConnectivityManager SBMotion_cm;
	ImageButton btn_motion_stretching; // 스트레칭 버튼
	ImageButton btn_motion_jump; // 양팔운동 버튼
	ImageButton btn_motion_situp; // 윗몸일으키기 버튼
	ImageButton btn_motion_swim; // 수영 버튼
	ImageButton btn_smart_tv; // 스마트tv와 연동 버튼
	boolean is3g, iswifi;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sbmotion_menu);
		SBMotion_cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		is3g = SBMotion_cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.isConnectedOrConnecting();
		iswifi = SBMotion_cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.isConnectedOrConnecting();

		if(is3g){
			Toast.makeText(this, "모션게임의 동영상 시청을 위해서는 Wifi를 권장합니다.", Toast.LENGTH_LONG).show();
		}
		btn_motion_stretching = (ImageButton) findViewById(R.id.sbmenu_Ibn1);
		btn_motion_jump = (ImageButton) findViewById(R.id.sbmenu_Ibn2);
		btn_motion_situp = (ImageButton) findViewById(R.id.sbmenu_Ibn4);
		btn_motion_swim = (ImageButton) findViewById(R.id.sbmenu_Ibn3);
		btn_smart_tv = (ImageButton) findViewById(R.id.sbmenu_Ibn5);

		btn_motion_stretching.setOnClickListener(this);
		btn_motion_jump.setOnClickListener(this);
		btn_motion_situp.setOnClickListener(this);
		btn_motion_swim.setOnClickListener(this);
		btn_smart_tv.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == btn_motion_stretching) {
			intent = new Intent(SBMotion.this, SBMotionStretching.class);
			startActivity(intent);
		} else if (v == btn_motion_jump) {
			intent = new Intent(SBMotion.this, SBMotionJump.class);
			startActivity(intent);
		} else if (v == btn_motion_situp) {
			intent = new Intent(SBMotion.this, SBMotionSitup.class);
			startActivity(intent);
		} else if (v == btn_motion_swim) {
			intent = new Intent(SBMotion.this, SBMotionSwim.class);
			startActivity(intent);
		} else if (v == btn_smart_tv) {
			intent = new Intent(SBMotion.this, SBSmartTVForWifi.class);
			startActivity(intent);
		}
	}
}
