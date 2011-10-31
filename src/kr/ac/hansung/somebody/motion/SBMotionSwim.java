package kr.ac.hansung.somebody.motion;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import kr.ac.hansung.somebody.R;
import kr.ac.hansung.somebody.SBAnalyzer;
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
 * ������ ���۵Ǵ� ������ ���� ������ �����ϴ� Ŭ����
 * 
 * @author Jaewon Lee(jaewon87@naver.com)
 * @version 1.0
 */
public class SBMotionSwim extends Activity implements SensorEventListener,
SurfaceHolder.Callback {
	final static double INVARIABLE = 0.0175; // ����� ���

	// �̵�� ó�� ���� ���� ����
	// *** ��Ʈ���� ������ mp4 �Ǵ� 3gp Ȯ���ڸ� ���� ���ϸ� SurfaceView�� ���� ������� ***
	private MediaPlayer mediaPlayer;
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	static boolean timerflag = false;
	// ���� ���
	public String FilePath = "http://113.198.80.206/swim.mp4";
	SBAnalyzer analyzer;
	SBUser user;
	Timer timer;

	SensorManager sm = null;

	SoundPool pool;

	int exercise_id = 2; // � ��ȣ
	int cnt_ding; // count�Ҹ�

	boolean start_flag = false; // ���� ��ư flag
	boolean pausing_flag = true; // �Ͻ����� flag

	double former_acc = 0; // ���Ӱ� ��ȭ ������
	double now_acc = 0; // ���Ӱ� ��ȭ ���� ��
	double gap_acc; // (���Ӱ� ��ȭ���� ��) - (���Ӱ� ��ȭ ������) = abs(����)

	int swim_distance = 0; // ���� �̵��Ÿ�

	Button btn_start;
	ImageButton btn_toggle_play; // ������ ���/�Ͻ����� ��� ��ư

	TextView text_swim_distance ;
	TextView text_reverse_timer; // 1�� Ÿ�̸�

	int timer_flag = -1;
	int min = 0; // timer ��
	int sec = 20; // timer ��

	int tv_connection_id; // ����Ʈ TV�� �����ɶ� �޴���ȣ�� �޾ƿ�
	TVClientSocket tv_client;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		Intent intent = getIntent();
		tv_connection_id = intent.getIntExtra("select", 0);

		sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		user = SBUser.getInstance();
		analyzer = SBAnalyzer.getInstance();
		if(tv_connection_id == 3){
			setContentView(R.layout.tv_sbmotion_jump);
			tv_client = TVClientSocket.getInstance();

			btn_start = (Button)findViewById(R.id.tv_sbmotion_Btn1);
			btn_start.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					tv_client.out.println("start");
					start_flag = true;
					TimerStart();
					btn_start.setText("� �� �Դϴ�.");
					btn_start.setClickable(false);
				}
			});
		}
		else{
			setContentView(R.layout.sbmotion_swim);
			text_swim_distance = (TextView)findViewById(R.id.sbmotionswim_Tv2);
			text_reverse_timer = (TextView)findViewById(R.id.sbmotionswim_Tv1);
			btn_start = (Button) findViewById(R.id.sbmotionswim_Btn1);
			btn_start.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					start_flag = true;
					TimerStart();
				}
			});

			// �̵�� ó������ SurfaceView����
			getWindow().setFormat(PixelFormat.UNKNOWN);
			surfaceView = (SurfaceView) findViewById(R.id.sbmotionswim_Sv1);
			surfaceHolder = surfaceView.getHolder();
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


			// ���/�Ͻ����� ��۹�ư ����
			btn_toggle_play = (ImageButton) findViewById(R.id.sbmotionswim_Ibn1);
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
		}
		// SoundPool
		pool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		cnt_ding = pool.load(this, R.raw.cnt_ding, 1);

	}

	public void onPause() {
		super.onPause();
		timerflag = false;
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
	public void onSensorChanged(SensorEvent event) {
		synchronized (this) {
			switch (event.sensor.getType()) {
			case Sensor.TYPE_ACCELEROMETER:
				if (start_flag) {
					if (now_acc != 0) {
						if (former_acc != 0) {
							gap_acc = (double) Math.abs(now_acc - former_acc);
						}
						former_acc = now_acc;
					}
					now_acc = event.values[1];
					swim_distance += (int) gap_acc;

					// �Ÿ��� ���� ������ �Ÿ��� �°� ������
					if(start_flag == true && tv_connection_id == 3)
						tv_client.sending_msg = "" + (swim_distance / 100);
					else if(tv_connection_id == 0)
						text_swim_distance.setText((swim_distance / 100) + " m");
				}
			}
		}
	}
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}


	/**
	 * ����� Ÿ�̸� ���� �޼ҵ�
	 */
	public void TimerStart() {
		// Ÿ�̸� ����
		if (timer_flag == -1) {
			timer = new Timer(true);

			final android.os.Handler handler = new android.os.Handler();
			timer.schedule(new TimerTask() {
				@Override
				public synchronized void run() {
					handler.post(new Runnable() {
						public void run() {
							timer_flag = 0;
							sec--;
							if(tv_connection_id == 0)
								text_reverse_timer.setText(String.format("%02d:%02d", min, sec));
							if (sec == 0) {
								EndStatus();
								start_flag = false;
							}
						}
					});
				}
			}, 1000, 1000); // ���� �⵿�ð�, ����(ms)
		}
	}

	/**
	 * ����� ����ó�� �޼ҵ�
	 */
	public void EndStatus() {
		if (timer_flag == 0) {
			timer.cancel(); // Ÿ�̸� ����
			timer_flag = 1;
		}

		final DecimalFormat format = new DecimalFormat();
		format.applyLocalizedPattern("0.##");

		user.used_cal = CalMet(swim_distance / 10) * user.weight * 1
				* INVARIABLE; // MET * ü�� * �ð�(��) * ���
		user.user_point = (int) (user.used_cal * 10);
		user.now_cal = user.now_cal - user.used_cal;
		boolean flag = user.put_exercise_results(user, exercise_id,
				user.used_cal, 60, user.user_point, 50);

		if (flag == false) {
			new AlertDialog.Builder(this)
			.setTitle("")
			.setMessage("������ ����� ���� �� �����ϴ�.\n3G�Ǵ�WIFI������ Ȯ�����ּ���.")
			.setNeutralButton("Ȯ��",
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,
						int which) {
				}
			}).show();
		}

		// �˾� ���̾�α� �޽��� ��, ��Ƽ��Ƽ���� (�Ŀ� Ŀ���� ���̾�α׷� ����)
		new AlertDialog.Builder(this)
		.setTitle("��Ϸ�")
		.setMessage(
				"��� �Ϸ�Ǿ����ϴ�.\n�������� "
						+ String.valueOf(format.format(user.used_cal))
						+ "Į�θ� �Ҹ�!\n" + "����ġ 50ȹ��" + user.user_point
						+ "����Ʈ ȹ��!")
						.setNegativeButton("Twitter��",
								new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(SBMotionSwim.this,
										SBTwitter.class);
								String temp = "�������� "
										+ String.valueOf(format
												.format(user.used_cal))
												+ "Į�θ� �Ҹ�!\n" + "����ġ 50ȹ��"
												+ user.user_point + "����Ʈ ȹ��!";
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

	@SuppressWarnings("static-access")
	protected void onResume() {
		super.onResume();
		sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), sm.SENSOR_DELAY_FASTEST); // SensorSpeed Fastest
	}

	@Override
	protected void onStop() {
		super.onStop();
		sm.unregisterListener(this,	sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
		if (timer_flag == 0) {
			timer.cancel(); // Ÿ�̸� ����
			timer_flag = 1;
		}
		// ��Ƽ��Ƽ�� ����� ��, �÷��̾� ����
		if(tv_connection_id == 0){
			mediaPlayer.release();
			mediaPlayer = null;
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
		double[] met = { 0, 3, 4, 5, 6, 7 };
		if (n_exercise > 0 && n_exercise < 50) {
			return met[1];
		} else if (n_exercise >= 50 && n_exercise < 100) {
			return met[2];
		} else if (n_exercise >= 100 && n_exercise < 150) {
			return met[3];
		} else if (n_exercise >= 150 && n_exercise < 200) {
			return met[4];
		} else if (n_exercise >= 200) {
			return met[5];
		} else
			return met[0];
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
	public void onBackPressed(){
		if(tv_connection_id == 0){
			super.onBackPressed();
			finish();
		}
		if(tv_connection_id == 3){
			tv_client.out.println("exit");
			super.onBackPressed();
			finish();
		}
	}
}