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
 * 센서로 동작되는 양팔운동에 대한 내용을 관리하는 클래스(스마트티비 연동 중)
 * 
 * @author Jaewon Lee(jaewon87@naver.com)
 * @version 1.0
 */
public class SBMotionJump extends Activity implements SensorEventListener, SurfaceHolder.Callback {
	final static double INVARIABLE = 0.0175; // 운동강도 상수

	// 미디어 처리 관련 변수 선언
	// *** 스트리밍 형식의 mp4 또는 3gp 확장자를 가진 파일만 SurfaceView를 통해 재생가능 ***
	private MediaPlayer mediaPlayer;
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	public static Boolean btnflag;

	// 파일 경로
	public String FilePath = "http://113.198.80.206/jump.mp4";

	// ClientSocket cs; // 스마트TV 클라이언트 소켓
	// SBSmartTVSelector stv;
	SBUser user;
	SBAnalyzer analyzer;
	Timer timer;

	SensorManager sm = null; // SensorManager 객체

	SoundPool pool;


	int exercise_id = 1; // 운동번호
	int cnt_ding; // count소리
	boolean start_flag = false; // Start Flag
	boolean pausing_flag = true; // 일시정지 flag

	int pre_value = 0; // 이전에 움직였던 방향의 수치를 저장
	int counter = 0; // 횟수 카운트
	int goal_count = 5; // 레벨별 목표 카운트 (임시로 잡아놓음)
	int path_test[] = new int[] { 8, -8 }; // 상태 체크를 할 int형 배열
	int path_income[] = new int[] { 0, 0 }; // 경로를 저장할 경로배열

	Button btn_start;
	ImageButton btn_toggle_play; // 동영상 재생/일시정지 토글 버튼

	TextView cnt = null;
	TextView text_reverse_timer; // 1분 타이머

	int timer_flag = -1;
	int min = 0; // timer 분
	int sec = 20; // timer 초

	int tv_connection_id; // 스마트 TV에 연관될때 메뉴번호를 받아옴
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
					btn_start.setText("운동 중 입니다.");
					btn_start.setClickable(false);
				}
			});
		}
		else{
			setContentView(R.layout.sbmotion_jump);
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
						btn_toggle_play.setImageResource(R.drawable.button_play);
						mediaPlayer.pause(); // 플레이어 일시 정지
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

	public void onSensorChanged(SensorEvent event) {
		synchronized (this) {
			switch (event.sensor.getType()) {
			case Sensor.TYPE_ACCELEROMETER:
				if (start_flag) {
					// Accelerometer Y의 수치가 9 (즉, 아래로 향할때)
					if (CheckLocation(path_test[0], event.values[1])) {
						if (path_income[0] == 0) {
							path_income[0] = (int) event.values[1];
						}
					}
					// Accelerometer Y의 수치가 -9 (즉, 위로향할때)
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
				cnt.setText(counter + " 회");
		}
	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	/**
	 * 운동종료 타이머 시작 메소드
	 */
	public void TimerStart() {
		// 타이머 관련
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
			}, 1000, 1000); // 시작 기동시간, 간격(ms)
		}
	}

	/**
	 * 운동종료 상태처리 메소드
	 */
	public void EndStatus() {
		if (timer_flag == 0) {
			timer.cancel(); // 타이머 해제
			timer_flag = 1;
		}
		final DecimalFormat format = new DecimalFormat();
		format.applyLocalizedPattern("0.##");

		user.used_cal = CalMet(counter) * user.weight * 1 * INVARIABLE; // MET *
		// 체중 *
		// 시간(분)
		// * 상수
		int t_point = (int) (user.used_cal * 10);
		user.user_point = user.user_point +t_point;
		user.now_cal = user.now_cal - user.used_cal;
		boolean flag = user.put_exercise_results(user, exercise_id,
				user.used_cal, 60, user.user_point, 50);

		if (flag == false) {
			new AlertDialog.Builder(this)
			.setTitle("")
			.setMessage("서버에 결과를 보낼 수 없습니다.\n3G또는WIFI연결을 확인해주세요.")
			.setNeutralButton("확인",
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,
						int which) {
				}
			}).show();
		}

		// 팝업 다이얼로그 메시지 후, 액티비티종료 (후에 커스텀 다이얼로그로 설정)
		new AlertDialog.Builder(this)
		.setTitle("운동완료")
		.setMessage(
				"운동이 완료되었습니다.\n점프점프 "
						+ String.valueOf(format.format(user.used_cal))
						+ "칼로리 소모!\n" + "경험치 50획득" + t_point
						+ "포인트 획득!")
						.setNegativeButton("Twitter로",
								new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(SBMotionJump.this,
										SBTwitter.class);
								String temp = "점프점프 "
										+ String.valueOf(format
												.format(user.used_cal))
												+ "칼로리 소모!" + "경험치 50획득"
												+ user.user_point + "포인트 획득!";
								intent.putExtra("message", temp);
								startActivityForResult(intent, 0);
							}
						})
						.setNeutralButton("확인", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								finish();
							}
						}).show();
	}

	/**
	 * 가속계 값이 측정범위에 있는지 체크하는 메소드
	 * 
	 * @param test_value
	 *            상태를 체크할 비교하는 값
	 * @param measure_value
	 *            측정값
	 * @return 측정범위 안에 있으면 True, 그렇지 않으면 False
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
	 * 경로배열안의 내용을 체크하여 count하는 메소드
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
			// 사운드 출력
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
			timer.cancel(); // 타이머 해제
			timer_flag = 1;
		}
		// 액티비티가 종료될 때, 플레이어 종료
		if(tv_connection_id == 0){
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 수행을 제대로 한 경우
		if (resultCode == RESULT_OK) {
			finish();
		}
		// 수행을 제대로 하지 못한 경우
		else if (resultCode == RESULT_CANCELED) {
		}
	}

	/**
	 * 운동횟수에 따른 MET값 결정 메소드
	 * 
	 * @param n_exercise
	 *            운동횟수
	 * @return 소비칼로리
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