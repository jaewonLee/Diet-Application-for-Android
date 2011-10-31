package kr.ac.hansung.somebody.motion;

import kr.ac.hansung.somebody.R;
import kr.ac.hansung.somebody.smarttv.SBSmartTVSelector;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

/**
 * Smart TV와 Wifi통신을 관리하는 클래스
 * 
 * @author Jaewon Lee(jaewon87@naver.com)
 * @version 1.0
 */
public class SBSmartTVForWifi extends Activity implements OnClickListener{
	ImageButton btn_connect_tv;
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.sbsmarttv_wifi);
		
		btn_connect_tv = (ImageButton)findViewById(R.id.sbsmart_Ibn1);
		btn_connect_tv.setOnClickListener(this); 
	}
	
	@Override
	public void onClick(View v) {
		if(v == btn_connect_tv)
		{
			// Google Smart TV 연동
			Intent i = new Intent(this, SBSmartTVSelector.class);
			startActivity(i);
		}
	}
}
