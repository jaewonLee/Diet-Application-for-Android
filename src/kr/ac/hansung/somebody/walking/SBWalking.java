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
 * GPS�� ���� �ȱ��� ���� ������ �����ϴ� Ŭ����
 * 
 * @author Jaewon Lee(jaewon87@naver.com)
 * @version 1.0
 */
public class SBWalking extends MapActivity implements OnClickListener {
	SBUser user;
	SBLineDrawOveray mylinedraw;
	PendingIntent mPending;
	Timer timer = null; // �ȱ� ������ Ÿ�̸� ����
	MapView myMapView;
	MapController mapController;
	final static double INVARIABLE = 1.05; // ����� ���

	private double prelat, prelng;
	private long preTime;
	static boolean timerflag = false;
	int exercise_id = 0;
	static Location location; // ���� �� GPS����
	public LocationManager locationManager;
	public LocationListener locationListener;

	MyLocationOverlay myLocationOverlay;
	String provider;

	GeoPoint Point;

	// �̵� �� �Ÿ���� //
	static Location preLocation; // ���� ��ġ����
	static Location currentLocation; // ���� ��ġ����
	static double sumMoveDistance = 0; // ���� ��ġ������ ���� ��ġ������ �Ÿ���

	int time = 0; // �ð��� 1�ʸ���
	int distance = 0; // �Ÿ��� 1m����

	Button HereIam; // ������ġ�� ȭ����ȯ
	Button start_walking; // ����
	Button end_walking;
	TextView text_distance; // �̵��� �Ÿ��� ȭ�鿡 ǥ�����ִ� TextView
	TextView text_timer; // 1�ʸ��� �����ϴ� Ÿ�̸� TextView
	TextView text_speed; // �ӷ��� ��Ÿ���� TextView

	int time_counter = 0; // time�� ������Ű�� count�޼ҵ�

	int timer_flag = -1;
	int hour = 0; // Timer �ð�
	int min = 0; // Timer ��
	int sec = 0; // Timer ��
	static double hour2 = 0, min2 = 0, sec2 = 0;
	double secforspeed = 0; // Speed�� ��

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

		// �� ��� ���� �κ�
		myMapView = (MapView) findViewById(R.id.mapView);
		mapController = myMapView.getController(); // �̰� �ȳ����� ������
		myMapView.setSatellite(false); // false�� �Ϲݸ�� true�� �������
		myMapView.displayZoomControls(true); // �ʺ信 Ȯ����� ��ư ����
		myMapView.setBuiltInZoomControls(false); // ����Ʈ�� ��뿩�� ����
		mapController.setZoom(19);
		List<Overlay> overlays = myMapView.getOverlays();

		// ó�� ���� �� ��ġ������ �����ͼ� ������Ʈ
		// String location_context = Context.LOCATION_SERVICE;

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		provider = LocationManager.GPS_PROVIDER;
		location = locationManager.getLastKnownLocation(provider);
		// ó�� ���� �� GPS������ ������Ʈ

		// �����ʸ� �޾Ƽ� ��ġ������ ���� �� ������Ʈ
		locationListener = new MyLocationListener();
		locationManager.requestLocationUpdates(provider, time, distance,
				locationListener);


		// �ʿ� MyLocationOverlay�� �߰�
		myLocationOverlay = new MyLocationOverlay(this, myMapView);
		myMapView.getOverlays().add(myLocationOverlay);
		myLocationOverlay.enableCompass();
		myLocationOverlay.enableMyLocation();

		// �ʿ� �̵���� �׸���
		mylinedraw = new SBLineDrawOveray();
		overlays.add(mylinedraw);

		// ��ġ�� ���� ������ �̵� ��Ŀ�� �ڵ����� �����
		myLocationOverlay.runOnFirstFix(new Runnable() {
			public void run() {
				mapController.animateTo(myLocationOverlay.getMyLocation());
				// updateMoveLocation(location);
			}
		});

		HereIam.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// ��ġ�� ���� ������ �̵� ��Ŀ�� �ڵ����� �����

				try {
					mapController.animateTo(myLocationOverlay.getMyLocation());
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(),
							"���� ��ġ�� ã�� ���Ͽ����ϴ�.", Toast.LENGTH_SHORT).show();
				}

			}
		});
	}

	private boolean chkGpsService() {
		String gs = android.provider.Settings.Secure.getString(
				getContentResolver(),
				android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if (gs.indexOf("gps", 0) < 0) {
			// GPS OFF �϶� Dialog ����� ���� ȭ������ Ƣ��ϴ�..
			AlertDialog.Builder gsDialog = new AlertDialog.Builder(this);
			gsDialog.setTitle("GPS Status OFF !!!");
			gsDialog.setMessage("����� ����Ϸ��� GPS���� ����� üũ�Ͽ� �ֽʽÿ�!!");
			gsDialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// GPS���� ȭ������ Ƣ���
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
			timer.cancel(); // Ÿ�̸� ����
			timer_flag = 1;
		}
	}

	class MyLocationListener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			// �̵��Ÿ� ��� �żҵ�
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

		// ���� ����, �浵, �ð�, ���� �ð�

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

	// �̵��� �Ÿ������� ����
	public void displayMoveDistance(Location loc1, Location loc2) {
		double distance = 0;
		distance = loc1.distanceTo(loc2);
		distance = distance * 1 / 1000;
		sumMoveDistance = sumMoveDistance + distance;

		// DataFormat�� ���� �Ҽ��� ���̱� (���� ���ڸ� ǥ��)
		DecimalFormat format = new DecimalFormat();
		format.applyLocalizedPattern("0.##");
		text_distance.setText(format.format(sumMoveDistance) + "");
	}

	/**
	 * �̵��ӵ� ��� �޼ҵ�
	 * 
	 * @param distance
	 *            ���� �̵� �Ÿ�
	 * @param speedsec
	 *            �̵� �ð�
	 * @return �̵� �ӵ�
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
	 * �Ƚ���� ���� MET�� ���� �޼ҵ�
	 * 
	 * @param n_exercise
	 *            �Ƚ��
	 * @return �Һ�Į�θ�
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

				// Ÿ�̸� ����
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
				}, 1000, 1000); // ���� �⵿�ð�, ����(ms)
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
				timer.cancel(); // Ÿ�̸� ����
				timer_flag = 1;

				double totaltime = hour2 + (min2 / 60) + (sec2 / 3600);

				int averagespeed = (int) (sumMoveDistance / totaltime);

				user.used_cal = CalMet(averagespeed) * user.weight * totaltime
						* INVARIABLE; // MET * ü�� * �ð�(��) * ���
				user.user_point = (int) (user.used_cal * 10);
				user.now_cal = user.now_cal - user.used_cal;
				user.put_exercise_results(user, exercise_id, user.used_cal, 60,
						user.user_point, 50);

			}
			// �˾� ���̾�α� �޽��� ��, ��Ƽ��Ƽ���� (�Ŀ� Ŀ���� ���̾�α׷� ����)

			final DecimalFormat format = new DecimalFormat();
			format.applyLocalizedPattern("0.##");

			new AlertDialog.Builder(this)
					.setTitle("��Ϸ�")
					.setMessage(
							"��� �Ϸ�Ǿ����ϴ�."
									+ String.valueOf(format
											.format(user.used_cal))
									+ "Į�θ� �Ҹ�!\n" + "����ġ 50ȹ��"
									+ user.user_point + "����Ʈ ȹ��!")
					.setNegativeButton("Twitter��",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent = new Intent(SBWalking.this,
											SBTwitter.class);
									startActivityForResult(intent, 0);

								}
							})
					.setNeutralButton("Ȯ��",
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