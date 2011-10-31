package kr.ac.hansung.somebody.motion;

import java.io.IOException;
import java.text.DecimalFormat;

import kr.ac.hansung.somebody.R;
import kr.ac.hansung.somebody.SBUser;
import kr.ac.hansung.somebody.smarttv.TVClientSocket;
import kr.ac.hansung.somebody.twitter.SBTwitter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * ������ ���۵Ǵ� ��������Ű�� ���� ������ �����ϴ� Ŭ����
 * 
 * @author Jaewon Lee(jaewon87@naver.com)
 * @version 1.0
 */
public class SBMotionSitup extends Activity implements SensorEventListener,
SurfaceHolder.Callback {
	final static double INVARIABLE = 0.0175; // ����� ���

	// �̵�� ó�� ���� ���� ����
	// *** ��Ʈ���� ������ mp4 �Ǵ� 3gp Ȯ���ڸ� ���� ���ϸ� SurfaceView�� ���� ������� ***
	private MediaPlayer mediaPlayer;
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;

	// ���� ���
	public String FilePath = "http://113.198.80.206/situp.mp4";

	SBUser user;
	SensorManager sm = null;

	SoundPool pool;

	int exercise_id = 3; // � ��ȣ
	int cnt_ding; // count�Ҹ�

	boolean checkin = false; // ���������� ���۵Ǹ�, check in ������ check out
	boolean start_flag = false; // ���� ��ŸƮ flag
	boolean pausing_flag = true; // �Ͻ����� flag

	Button btn_start; // ��ŸƮ ��ư
	ImageButton btn_toggle_play; // ������ ���/�Ͻ����� ��� ��ư

	TextView text_cnt = null; // ���� ī��Ʈ

	int counter = 20; // Ƚ�� ī��Ʈ

	int tv_connection_id; // ����Ʈ TV�� �����ɶ� �޴���ȣ�� �޾ƿ�
	TVClientSocket tv_client;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		tv_connection_id = intent.getIntExtra("select", 0);

		sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		user = SBUser.getInstance();

		if (tv_connection_id == 4){
			setContentView(R.layout.tv_sbmotion_jump);
			tv_client = TVClientSocket.getInstance();

			btn_start = (Button)findViewById(R.id.tv_sbmotion_Btn1);
			btn_start.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					start_flag = true;
					tv_client.out.println("start");
					btn_start.setText("� �� �Դϴ�.");
					btn_start.setClickable(false);
				}
			});
		}
		else{
			setContentView(R.layout.sbmotion_situp);
			// �̵�� ó������ SurfaceView����
			getWindow().setFormat(PixelFormat.UNKNOWN);
			surfaceView = (SurfaceView) findViewById(R.id.sbmotion_Sv1);
			surfaceHolder = surfaceView.getHolder();
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

			btn_start = (Button) findViewById(R.id.sbmotion_Btn1);
			btn_start.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					start_flag = true;
				}
			});

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
						btn_toggle_play.setImageResource(R.drawable.button_play);
						mediaPlayer.pause(); // �÷��̾� �Ͻ� ����
					}
				}
			});
			text_cnt = (TextView) findViewById(R.id.sbmotion_Tv2);
		}
		// Sound Pool
		pool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		cnt_ding = pool.load(this, R.raw.cnt_ding, 1);
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

	@SuppressWarnings("static-access")
	@Override
	protected void onResume() {
		super.onResume();
		sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_PROXIMITY),
				sm.SENSOR_DELAY_FASTEST); // SensorSpeed Fastest
	}

	@Override
	protected void onStop() {
		super.onStop();
		sm.unregisterListener(this, sm.getDefaultSensor(Sensor.TYPE_PROXIMITY));

		// ��Ƽ��Ƽ�� ����� ��, �÷��̾� ����
		if(tv_connection_id == 0){
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	/**
	 * ����� ����ó�� �޼ҵ�
	 */
	public void EndStatus() {
		final DecimalFormat format = new DecimalFormat();
		format.applyLocalizedPattern("0.##");

		user.used_cal = CalMet(60) * user.weight * 1 * INVARIABLE; // MET *
		// ü�� *
		// �ð�(��)
		// * ���
		user.user_point = (int) (user.used_cal * 10);
		user.now_cal = user.now_cal - user.used_cal;
		boolean flag = user.put_exercise_results(user, exercise_id, user.used_cal, 60, user.user_point, 50);

		if(flag == false){
			new AlertDialog.Builder(this)
			.setTitle("")
			.setMessage("������ ����� ���� �� �����ϴ�.\n3G�Ǵ�WIFI������ Ȯ�����ּ���.")
			.setNeutralButton("Ȯ��", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.show();
		}

		// �˾� ���̾�α� �޽��� ��, ��Ƽ��Ƽ���� (�Ŀ� Ŀ���� ���̾�α׷� ����)
		new AlertDialog.Builder(this)
		.setTitle("��Ϸ�")
		.setMessage(
				"��� �Ϸ�Ǿ����ϴ�.\n��������Ű��! "
						+ String.valueOf(format.format(user.used_cal))
						+ "Į�θ� �Ҹ�!\n" + user.user_point + "����Ʈ ȹ��!")
						.setNegativeButton("Twitter��",
								new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(SBMotionSitup.this,
										SBTwitter.class);
								String temp = "��������Ű��!"
										+ String.valueOf(format
												.format(user.used_cal))
												+ "Į�θ� �Ҹ�!" + user.user_point
												+ "����Ʈ ȹ��!";
								intent.putExtra("message", temp);
								startActivityForResult(intent, 0);
							}
						})
						.setNeutralButton("Ȯ��", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								finish();
							}
						}).show();
	}

	public void onSensorChanged(SensorEvent event) {
		synchronized (this) {
			switch (event.sensor.getType()) {
			case Sensor.TYPE_PROXIMITY:
				if (start_flag) {
					if (event.values[0] != 0)
						checkin = true;
					else
						checkin = false;
				}
			}
			CheckCount();

			if(start_flag == true && tv_connection_id == 4)
				tv_client.sending_msg = "" + counter;
			else if(tv_connection_id == 0){
				text_cnt.setText(counter + " ȸ");
			}
			if (counter == 0)
				EndStatus();
		}
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	/**
	 * �������� üũ���θ� Ȯ���Ͽ� count�ϴ� �޼ҵ�
	 */
	public void CheckCount() {
		if (checkin == true) {
			counter--;
			// ���� ���
			try {
				Thread.sleep(100); // sensor�� sound�÷��̰� ���� ����
			} catch (Exception ee) {
			}
			pool.play(cnt_ding, 1, 1, 0, 0, 1);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// ������ ����� �� ���
		if (resultCode == RESULT_OK) {
			finish();
		}
		// ������ ����� ���� ���� ���
		else if (resultCode == RESULT_CANCELED) {
		}
	}

	/**
	 * �Ƚ���� ���� MET�� ���� �޼ҵ�
	 * 
	 * @param n_exercise
	 *            �Ƚ��
	 * @return �Һ�Į�θ�
	 */
	public double CalMet(int n_exercise) {
		double[] met = { 0, 3, 4, 5, 6, 7, 8 };
		if (n_exercise > 0 && n_exercise < 10) {
			return met[1];
		} else if (n_exercise >= 10 && n_exercise < 20) {
			return met[2];
		} else if (n_exercise >= 20 && n_exercise < 30) {
			return met[3];
		} else if (n_exercise >= 30 && n_exercise < 40) {
			return met[4];
		} else if (n_exercise >= 40 && n_exercise < 50) {
			return met[5];
		} else if (n_exercise >= 50) {
			return met[6];
		} else
			return met[0];
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		startPlaying();
		btn_toggle_play.setImageResource(R.drawable.button_pause);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void onBackPressed(){
		if(tv_connection_id == 0){
			super.onBackPressed();
			finish();
		}
		if(tv_connection_id == 4){
			tv_client.out.println("exit");
			super.onBackPressed();
			finish();
		}
	}
}
