package kr.ac.hansung.somebody.walking;

import java.text.DecimalFormat;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import kr.ac.hansung.somebody.R;
import kr.ac.hansung.somebody.SBUser;
import kr.ac.hansung.somebody.twitter.SBTwitter;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * GPS를 통한 걷기운동에 대한 내용을 관리하는 클래스
 * 
 * @author Jaewon Lee(jaewon87@naver.com)
 * @version 1.0
 */
public class SBWalking extends MapActivity implements OnClickListener {
	SBUser user;
	SBLineDrawOveray mylinedraw;
	PendingIntent mPending;
	Timer timer = null; // 걷기 시작후 타이머 동작
	MapView myMapView;
	MapController mapController;
	final static double INVARIABLE = 1.05; // 운동강도 상수

	private double prelat, prelng;
	private long preTime;
	static boolean timerflag = false;
	int exercise_id = 0;
	static Location location; // 시작 시 GPS정보
	public LocationManager locationManager;
	public LocationListener locationListener;

	MyLocationOverlay myLocationOverlay;
	String provider;

	GeoPoint Point;

	// 이동 시 거리계산 //
	static Location preLocation; // 이전 위치정보
	static Location currentLocation; // 현재 위치정보
	static double sumMoveDistance = 0; // 현재 위치정보와 이전 위치정보의 거리합

	int time = 0; // 시간은 1초마다
	int distance = 0; // 거리는 1m마다

	Button HereIam; // 현재위치로 화면전환
	Button start_walking; // 시작
	Button end_walking;
	TextView text_distance; // 이동한 거리를 화면에 표시해주는 TextView
	TextView text_timer; // 1초마다 증가하는 타이머 TextView
	TextView text_speed; // 속력을 나타내는 TextView

	int time_counter = 0; // time을 증가시키는 count메소드

	int timer_flag = -1;
	int hour = 0; // Timer 시간
	int min = 0; // Timer 분
	int sec = 0; // Timer 초
	static double hour2 = 0, min2 = 0, sec2 = 0;
	double secforspeed = 0; // Speed용 초

	public void Change_HHMMSS() {
		if (sec >= 60) {
			secforspeed += sec;
			sec = 0;
			min++;
		}
		if (min >= 60) {
			secforspeed += sec;
			sec = 0;
			min = 0;
			hour++;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sbwalking);
		user = SBUser.getInstance();
		chkGpsService();
		timer = new Timer(true);
	
		text_distance = (TextView) findViewById(R.id.sbwalking_Tv2);
		text_timer = (TextView) findViewById(R.id.sbwalking_Tv4);
		text_speed = (TextView) findViewById(R.id.sbwalking_Tv3);

		start_walking = (Button) findViewById(R.id.sbwalking_Btn2);
		start_walking.setOnClickListener(this);
		end_walking = (Button) findViewById(R.id.sbwalking_Btn3);
		end_walking.setOnClickListener(this);
		HereIam = (Button) findViewById(R.id.sbwalking_Btn1);

		// 맵 기능 설정 부분
		myMapView = (MapView) findViewById(R.id.mapView);
		mapController = myMapView.getController(); // 이거 안넣으면 에러남
		myMapView.setSatellite(false); // false는 일반모드 true는 위성모드
		myMapView.displayZoomControls(true); // 맵뷰에 확대축소 버튼 적용
		myMapView.setBuiltInZoomControls(false); // 줌컨트롤 사용여부 결정
		mapController.setZoom(19);
		List<Overlay> overlays = myMapView.getOverlays();

		// 처음 실행 시 위치정보를 가져와서 업데이트
		// String location_context = Context.LOCATION_SERVICE;

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		provider = LocationManager.GPS_PROVIDER;
		location = locationManager.getLastKnownLocation(provider);
		// 처음 실행 시 GPS정보를 업데이트

		// 리스너를 달아서 위치정보가 변경 시 업데이트
		locationListener = new MyLocationListener();
		locationManager.requestLocationUpdates(provider, time, distance,
				locationListener);


		// 맵에 MyLocationOverlay를 추가
		myLocationOverlay = new MyLocationOverlay(this, myMapView);
		myMapView.getOverlays().add(myLocationOverlay);
		myLocationOverlay.enableCompass();
		myLocationOverlay.enableMyLocation();

		// 맵에 이동경로 그리기
		mylinedraw = new SBLineDrawOveray();
		overlays.add(mylinedraw);

		// 위치가 변할 때마다 이동 마커를 자동으로 찍어줌
		myLocationOverlay.runOnFirstFix(new Runnable() {
			public void run() {
				mapController.animateTo(myLocationOverlay.getMyLocation());
				// updateMoveLocation(location);
			}
		});

		HereIam.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 위치가 변할 때마다 이동 마커를 자동으로 찍어줌

				try {
					mapController.animateTo(myLocationOverlay.getMyLocation());
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(),
							"현재 위치를 찾지 못하였습니다.", Toast.LENGTH_SHORT).show();
				}

			}
		});
	}

	private boolean chkGpsService() {
		String gs = android.provider.Settings.Secure.getString(
				getContentResolver(),
				android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if (gs.indexOf("gps", 0) < 0) {
			// GPS OFF 일때 Dialog 띄워서 설정 화면으로 튀어봅니다..
			AlertDialog.Builder gsDialog = new AlertDialog.Builder(this);
			gsDialog.setTitle("GPS Status OFF !!!");
			gsDialog.setMessage("기능을 사용하려면 GPS위성 사용을 체크하여 주십시오!!");
			gsDialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// GPS설정 화면으로 튀어요
							Intent intent = new Intent(
									android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
							intent.addCategory(Intent.CATEGORY_DEFAULT);
							startActivity(intent);
						}
					}).create().show();
			return false;
		} else {
			return true;
		}
	}

	public void onDestroy() {

		super.onDestroy();
		myLocationOverlay.disableCompass();
		myLocationOverlay.disableMyLocation();
		
		locationManager.removeUpdates(locationListener);	
		

	}

	public void onResume() {
		super.onResume();
		myLocationOverlay.enableCompass();
		myLocationOverlay.enableMyLocation();
		locationManager.requestLocationUpdates(provider, time, distance,
				locationListener);
		// locationManager.requestLocationUpdates(provider, time, distance,
		// locationListener);
	}

	@Override
	public void onPause() {
		myLocationOverlay.disableCompass();
		myLocationOverlay.disableMyLocation();
		super.onPause();
		locationManager.removeUpdates(locationListener);	
		
	
	
		if (timer_flag == 0) {
			timer.cancel(); // 타이머 해제
			timer_flag = 1;
		}
	}

	class MyLocationListener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			// 이동거리 계산 매소드
			updateMoveLocation(location);
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
		}
	}

	public void updateMoveLocation(Location location) {

		mylinedraw.setLocation(location);
		// myLineDrawOverlay.setLocation(location);
		double lat = location.getLatitude();

		double lng = location.getLongitude();

		long curTime = location.getTime();

		double deltaTime = (curTime - preTime);

		// 현재 위도, 경도, 시간, 지난 시간

		float[] distance = new float[2];

		Location.distanceBetween(prelat, prelng, lat, lng, distance);

		double speed = distance[0] / deltaTime * 3600;
		int speed2 = (int) speed;

		preTime = curTime;

		prelat = lat;

		prelng = lng;
		text_speed.setText(Integer.toString(speed2) + "");

		if (preLocation == null) {
			preLocation = location;

		} else if (preLocation != null && currentLocation == null) {
			currentLocation = location;

		} else if (preLocation != null && currentLocation != null) {
			if (preLocation != currentLocation) {
				displayMoveDistance(preLocation, currentLocation);
				preLocation = currentLocation;
				currentLocation = null;
			}
		}
	}

	// 이동중 거리정보를 측정
	public void displayMoveDistance(Location loc1, Location loc2) {
		double distance = 0;
		distance = loc1.distanceTo(loc2);
		distance = distance * 1 / 1000;
		sumMoveDistance = sumMoveDistance + distance;

		// DataFormat을 통한 소수점 줄이기 (이하 두자리 표현)
		DecimalFormat format = new DecimalFormat();
		format.applyLocalizedPattern("0.##");
		text_distance.setText(format.format(sumMoveDistance) + "");
	}

	/**
	 * 이동속도 계산 메소드
	 * 
	 * @param distance
	 *            나의 이동 거리
	 * @param speedsec
	 *            이동 시간
	 * @return 이동 속도
	 */
	public double displayRealTimeSpeed(double distance, double speedsec) {
		double speed = 0;
		speed = distance / (speedsec / 3600);
		return speed;
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * 운동횟수에 따른 MET값 결정 메소드
	 * 
	 * @param n_exercise
	 *            운동횟수
	 * @return 소비칼로리
	 */
	public double CalMet(int n_exercise) {
		double[] met = { 0, 3, 5, 10, 14, 16 };
		if (n_exercise > 0 && n_exercise < 5) {
			return met[1];
		} else if (n_exercise >= 5 && n_exercise < 7) {
			return met[2];
		} else if (n_exercise >= 7 && n_exercise < 10) {
			return met[3];
		} else if (n_exercise >= 10 && n_exercise < 13) {
			return met[4];
		} else if (n_exercise >= 14) {
			return met[5];
		} else
			return met[0];
	}

	@Override
	public void onClick(View v) {
		if (v == start_walking) {
			if (timerflag == false) {
				timerflag = true;
				final android.os.Handler handler = new android.os.Handler();

				// 타이머 관련
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						handler.post(new Runnable() {
							public void run() {
								timer_flag = 0;
								sec++;
								Change_HHMMSS();
								DecimalFormat format = new DecimalFormat();
								format.applyLocalizedPattern("0.##");

								text_timer.setText(String.format(
										"%02d:%02d:%02d", hour, min, sec));
							}
						});
					}
				}, 1000, 1000); // 시작 기동시간, 간격(ms)
			}

		} else if (v == end_walking) {
			timerflag = false;
			if (timer_flag == 0) {
				hour2 = hour;
				min2 = min;
				sec2 = sec;
				hour = 0;
				min = 0;
				sec = 0;
				timer.cancel(); // 타이머 해제
				timer_flag = 1;

				double totaltime = hour2 + (min2 / 60) + (sec2 / 3600);

				int averagespeed = (int) (sumMoveDistance / totaltime);

				user.used_cal = CalMet(averagespeed) * user.weight * totaltime
						* INVARIABLE; // MET * 체중 * 시간(분) * 상수
				user.user_point = (int) (user.used_cal * 10);
				user.now_cal = user.now_cal - user.used_cal;
				user.put_exercise_results(user, exercise_id, user.used_cal, 60,
						user.user_point, 50);

			}
			// 팝업 다이얼로그 메시지 후, 액티비티종료 (후에 커스텀 다이얼로그로 설정)

			final DecimalFormat format = new DecimalFormat();
			format.applyLocalizedPattern("0.##");

			new AlertDialog.Builder(this)
					.setTitle("운동완료")
					.setMessage(
							"운동이 완료되었습니다."
									+ String.valueOf(format
											.format(user.used_cal))
									+ "칼로리 소모!\n" + "경험치 50획득"
									+ user.user_point + "포인트 획득!")
					.setNegativeButton("Twitter로",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent = new Intent(SBWalking.this,
											SBTwitter.class);
									startActivityForResult(intent, 0);

								}
							})
					.setNeutralButton("확인",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									finish();
								}
							}).show();
		}

	}
}