package kr.ac.hansung.somebody.motion;

import java.io.IOException;

import kr.ac.hansung.somebody.R;
import kr.ac.hansung.somebody.smarttv.TVClientSocket;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.os.Bundle;
import android.widget.ImageButton;


/**
 * 센서로 동작되는 스트레칭에 대한 내용을 관리하는 클래스 - 부분구현
 * 
 * @author Jaewon Lee(jaewon87@naver.com)
 * @version 1.0
 */
public class SBMotionStretching extends Activity implements
SurfaceHolder.Callback {
	// 미디어 처리 관련 변수 선언
	// *** 스트리밍 형식의 mp4 또는 3gp 확장자를 가진 파일만 SurfaceView를 통해 재생가능 ***
	private MediaPlayer mediaPlayer;
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	int tv_connection_id; // 스마트 TV에 연관될때 메뉴번호를 받아옴
	TVClientSocket tv_client;
	// 파일 경로
	public String FilePath = "http://113.198.80.206/stretching.mp4";

	boolean pausing_flag = true; // 일시정지 flag

	ImageButton btn_toggle_play; // 동영상 재생/일시정지 토글 버튼

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		tv_connection_id = intent.getIntExtra("select", 0);

		if (tv_connection_id == 2) {
			setContentView(R.layout.tv_sbmotion_jump);
			tv_client = TVClientSocket.getInstance();
		} 
		else {
			setContentView(R.layout.sbmotion_stretching);

			// 미디어 처리관련 SurfaceView설정
			getWindow().setFormat(PixelFormat.UNKNOWN);
			surfaceView = (SurfaceView) findViewById(R.id.sbmotion_Sv1);
			surfaceHolder = surfaceView.getHolder();
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

			// 재생/일시정지 토글버튼 설정
			btn_toggle_play = (ImageButton) findViewById(R.id.sbmotion_Ibn1);
			btn_toggle_play.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (pausing_flag) {
						pausing_flag = false;
						btn_toggle_play.setImageResource(R.drawable.button_pause);
						mediaPlayer.start(); // 플레이어 시작
					} else {
						pausing_flag = true;
						btn_toggle_play
						.setImageResource(R.drawable.button_play);
						mediaPlayer.pause(); // 플레이어 일시 정지
					}
				}
			});
		}

	}

	/**
	 * 동영상 재생에 관련된 시작을 해주는 메소드
	 */
	void startPlaying() {
		mediaPlayer = new MediaPlayer();

		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer.setDisplay(surfaceHolder);

		try {
			mediaPlayer.setDataSource(this, Uri.parse(FilePath));
			mediaPlayer.prepare();

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mediaPlayer.start();
	}

	@Override
	protected void onStop() {
		super.onStop();

		// 액티비티가 종료될 때, 플레이어 종료
		if(tv_connection_id == 0){
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		startPlaying();
		btn_toggle_play.setImageResource(R.drawable.button_pause);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBackPressed() {
		if(tv_connection_id == 0)
			super.onBackPressed();
		if(tv_connection_id == 2){
			tv_client.sending_msg = "exit";
			super.onBackPressed();
		}
	}
}
