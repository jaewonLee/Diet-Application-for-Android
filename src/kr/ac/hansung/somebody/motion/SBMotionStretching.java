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
 * ������ ���۵Ǵ� ��Ʈ��Ī�� ���� ������ �����ϴ� Ŭ���� - �κб���
 * 
 * @author Jaewon Lee(jaewon87@naver.com)
 * @version 1.0
 */
public class SBMotionStretching extends Activity implements
SurfaceHolder.Callback {
	// �̵�� ó�� ���� ���� ����
	// *** ��Ʈ���� ������ mp4 �Ǵ� 3gp Ȯ���ڸ� ���� ���ϸ� SurfaceView�� ���� ������� ***
	private MediaPlayer mediaPlayer;
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	int tv_connection_id; // ����Ʈ TV�� �����ɶ� �޴���ȣ�� �޾ƿ�
	TVClientSocket tv_client;
	// ���� ���
	public String FilePath = "http://113.198.80.206/stretching.mp4";

	boolean pausing_flag = true; // �Ͻ����� flag

	ImageButton btn_toggle_play; // ������ ���/�Ͻ����� ��� ��ư

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

			// �̵�� ó������ SurfaceView����
			getWindow().setFormat(PixelFormat.UNKNOWN);
			surfaceView = (SurfaceView) findViewById(R.id.sbmotion_Sv1);
			surfaceHolder = surfaceView.getHolder();
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

			// ���/�Ͻ����� ��۹�ư ����
			btn_toggle_play = (ImageButton) findViewById(R.id.sbmotion_Ibn1);
			btn_toggle_play.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (pausing_flag) {
						pausing_flag = false;
						btn_toggle_play.setImageResource(R.drawable.button_pause);
						mediaPlayer.start(); // �÷��̾� ����
					} else {
						pausing_flag = true;
						btn_toggle_play
						.setImageResource(R.drawable.button_play);
						mediaPlayer.pause(); // �÷��̾� �Ͻ� ����
					}
				}
			});
		}

	}

	/**
	 * ������ ����� ���õ� ������ ���ִ� �޼ҵ�
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

		// ��Ƽ��Ƽ�� ����� ��, �÷��̾� ����
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
