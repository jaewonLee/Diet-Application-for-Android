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
 * ������ ���۵Ǵ� ���ȿ�� ���� ������ �����ϴ� Ŭ����(����ƮƼ�� ���� ��)
 * 
 * @author Jaewon Lee(jaewon87@naver.com)
 * @version 1.0
 */
public class SBMotionJump extends Activity implements SensorEventListener, SurfaceHolder.Callback {
	final static double INVARIABLE = 0.0175; // ����� ���

	// �̵�� ó�� ���� ���� ����
	// *** ��Ʈ���� ������ mp4 �Ǵ� 3gp Ȯ���ڸ� ���� ���ϸ� SurfaceView�� ���� ������� ***
	private MediaPlayer mediaPlayer;
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	public static Boolean btnflag;

	// ���� ���
	public String FilePath = "http://113.198.80.206/jump.mp4";

	// ClientSocket cs; // ����ƮTV Ŭ���̾�Ʈ ����
	// SBSmartTVSelector stv;
	SBUser user;
	SBAnalyzer analyzer;
	Timer timer;

	SensorManager sm = null; // SensorManager ��ü

	SoundPool pool;


	int exercise_id = 1; // ���ȣ
	int cnt_ding; // count�Ҹ�
	boolean start_flag = false; // Start Flag
	boolean pausing_flag = true; // �Ͻ����� flag

	int pre_value = 0; // ������ �������� ������ ��ġ�� ����
	int counter = 0; // Ƚ�� ī��Ʈ
	int goal_count = 5; // ������ ��ǥ ī��Ʈ (�ӽ÷� ��Ƴ���)
	int path_test[] = new int[] { 8, -8 }; // ���� üũ�� �� int�� �迭
	int path_income[] = new int[] { 0, 0 }; // ��θ� ������ ��ι迭

	Button btn_start;
	ImageButton btn_toggle_play; // ������ ���/�Ͻ����� ��� ��ư

	TextView cnt = null;
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

		if (tv_connection_id == 1){
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
			setContentView(R.layout.sbmotion_jump);
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
						btn_toggle_play.setImageResource(R.drawable.button_play);
						mediaPlayer.pause(); // �÷��̾� �Ͻ� ����
					}
				}
			});

			text_reverse_timer = (TextView) findViewById(R.id.sbmotion_Tv1);
			cnt = (TextView) findViewById(R.id.sbmotion_Tv2);
			btn_start = (Button) findViewById(R.id.sbmotion_Btn1);
			btn_start.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					start_flag = true;
					TimerStart();
				}
			});
		}
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

	public void onSensorChanged(SensorEvent event) {
		synchronized (this) {
			switch (event.sensor.getType()) {
			case Sensor.TYPE_ACCELEROMETER:
				if (start_flag) {
					// Accelerometer Y�� ��ġ�� 9 (��, �Ʒ��� ���Ҷ�)
					if (CheckLocation(path_test[0], event.values[1])) {
						if (path_income[0] == 0) {
							path_income[0] = (int) event.values[1];
						}
					}
					// Accelerometer Y�� ��ġ�� -9 (��, �������Ҷ�)
					if (CheckLocation(path_test[1], event.values[1])) {
						if (path_income[0] == path_test[0]
								&& path_income[1] == 0) {
							path_income[1] = (int) event.values[1];
						}
					}
				}
			}
			CheckCount();
			if(start_flag == true && tv_connection_id == 1)
				tv_client.sending_msg = "" + counter;
			else if(tv_connection_id == 0)
				cnt.setText(counter + " ȸ");
		}
	}

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

		user.used_cal = CalMet(counter) * user.weight * 1 * INVARIABLE; // MET *
		// ü�� *
		// �ð�(��)
		// * ���
		int t_point = (int) (user.used_cal * 10);
		user.user_point = user.user_point +t_point;
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
						+ "Į�θ� �Ҹ�!\n" + "����ġ 50ȹ��" + t_point
						+ "����Ʈ ȹ��!")
						.setNegativeButton("Twitter��",
								new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(SBMotionJump.this,
										SBTwitter.class);
								String temp = "�������� "
										+ String.valueOf(format
												.format(user.used_cal))
												+ "Į�θ� �Ҹ�!" + "����ġ 50ȹ��"
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

	/**
	 * ���Ӱ� ���� ���������� �ִ��� üũ�ϴ� �޼ҵ�
	 * 
	 * @param test_value
	 *            ���¸� üũ�� ���ϴ� ��
	 * @param measure_value
	 *            ������
	 * @return �������� �ȿ� ������ True, �׷��� ������ False
	 */
	public boolean CheckLocation(int test_value, float measure_value) {
		int trans_value;

		trans_value = (int) measure_value;
		if (test_value == trans_value) {
			return true;
		}
		return false;
	}

	/**
	 * ��ι迭���� ������ üũ�Ͽ� count�ϴ� �޼ҵ�
	 */
	public void CheckCount() {
		int temp_cnt = 0;
		for (int i = 0; i < path_income.length; i++) {
			if (path_income[i] == 0)
				break;
			else
				temp_cnt++;
		}
		if (temp_cnt == 2) {
			counter++;
			// ���� ���
			try {
				Thread.sleep(100);
			} catch (Exception ee) {
			}
			pool.play(cnt_ding, 1, 1, 0, 0, 1);
			for (int i = 0; i < path_income.length; i++)
				path_income[i] = 0;

		}
	}

	@SuppressWarnings("static-access")
	@Override
	protected void onResume() {
		super.onResume();
		// stv.activityflag= 0;
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
		double[] met = { 0, 1, 2, 3, 4 };
		if (n_exercise > 0 && n_exercise < 5) {
			return met[1];
		} else if (n_exercise >= 5 && n_exercise < 10) {
			return met[2];
		} else if (n_exercise >= 10 && n_exercise < 15) {
			return met[3];
		} else if (n_exercise >= 15) {
			return met[4];
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
		if(tv_connection_id == 1){
			tv_client.out.println("exit");
			super.onBackPressed();
			finish();
		}
	}
}