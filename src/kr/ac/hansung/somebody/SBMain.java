package kr.ac.hansung.somebody;

import java.text.DecimalFormat;
import java.util.ArrayList;

import kr.ac.hansung.somebody.diary.SBDiary;
import kr.ac.hansung.somebody.group.SBDisplayGroupRoom;
import kr.ac.hansung.somebody.group.SBGroupDiet;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

/**
 * SomeBody 어플리케이션의 메인 Activity Class
 * 
 * @author Jaewon Lee(jaewon87@naver.com),An yongsoo(yongsu9@gmail.com)
 * @version 1.0
 */
public class SBMain extends Activity implements View.OnClickListener {
	public static final int MOTION = 1; // 모션운동으로 가는 Intent value값
	public static final int FOOD = 2; // 음식입력으로 가는 Intent value값
	
	SBDBAdapter db; // SQLite를 사용하기 위한 DBAdapter 객체 선언
	SBUser user; // Singleton SBUser 객체 선언
	SBAnalyzer analyzer; // 입력된 정보를 분석하는 메소드를 가져오는 Singleton SBAnalyzer객체 선언

	public static ArrayList<Activity> SBMain_Allact;
	Intent intent;

	ImageView now_status_image;// 현재 상태를 이미지로 출력
	ImageButton btn_ifitness; // 메인 화면의 운동입력 이미지버튼
	ImageButton btn_ifood; // 메인 화면의 음식입력 이미지버튼
	ImageButton btn_coupon; // 메인 화면의 쿠폰 이미지버튼
	ImageButton btn_diary; // 메인 화면의 다이어리 이미지버튼
	ImageButton btn_group; // 메인 화면의 그룹다이어리 이미지 버튼

	TextView display_lv; // 사용자 레벨
	TextView display_nickname; // 사용자 닉네임
	TextView display_point; // 사용자 포인트
	TextView display_ranking; // 사용자 랭킹

	TextView now_status; // 현재 상태
	TextView now_calorie; // 현재 칼로리

	int approach_flag = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		SBMain_Allact = new ArrayList<Activity>();
		SBMain_Allact.add(this);
		
		user = SBUser.getInstance(); // SBUser의 getInstance()호출
		analyzer = SBAnalyzer.getInstance(); // SBAnalyzer의 getInstance()

		// 메인 액티비티에서 초기 Intro화면을 호출
		startActivity(new Intent(SBMain.this, SomeBody.class));

		// Admob 광고 생성 및 호출
		AdView ad = (AdView) this.findViewById(R.id.adView);
		ad.loadAd(new AdRequest());

		// 각 Component에 매칭
		now_status_image = (ImageView) findViewById(R.id.now_status_image);
		display_lv = (TextView) findViewById(R.id.sbmain_textView3);
		display_nickname = (TextView) findViewById(R.id.sbmain_textView4);
		display_point = (TextView) findViewById(R.id.sbmain_textView5);

		now_calorie = (TextView) findViewById(R.id.sbmain_textView1);
		now_status = (TextView) findViewById(R.id.sbmain_textView8);
		display_ranking = (TextView) findViewById(R.id.sbmain_textView9);

		// 버튼 리스너
		btn_ifitness = (ImageButton) findViewById(R.id.input_fitness);
		btn_ifood = (ImageButton) findViewById(R.id.input_food);
		btn_group = (ImageButton) findViewById(R.id.button_group);
		btn_coupon = (ImageButton) findViewById(R.id.button_coupon);
		btn_diary = (ImageButton) findViewById(R.id.button_diary);

		btn_ifitness.setOnClickListener(this);
		btn_ifood.setOnClickListener(this);
		btn_coupon.setOnClickListener(this);
		btn_diary.setOnClickListener(this);
		btn_group.setOnClickListener(this);

		Init(); // 초기화 부분

		// Log.e("main","onCreate");
	}

	@Override
	public void onStart() {
		super.onStart();
		// Log.e("main","onStart");
	}

	@Override
	public void onResume() {
		super.onResume();
		// Log.e("main","onResume");
		if (user.goal_term != 0) {
			// 소수점 두번째 자리까지 나오도록 설정
			DecimalFormat format = new DecimalFormat();
			format.applyLocalizedPattern("0.##");

			// 정보가 제대로 입력되었다면, Analyze 메소드를 처리
			// 사용자 활동지수 변환
			user.activationvalue_level = analyzer
					.CalActivation(user.activationvalue);
			// 사용자 bmr지수 측정
			user.bmr = analyzer.CalBmr(user.gender, user.height, user.weight,
					user.age);
			// 사용자 권장 섭취량
			user.recommended_intake = analyzer.CalRecommendedIntake(user.bmr,
					user.activationvalue_level);
			// 사용자 총 소비해야 될 칼로리 (현재체중 권장 섭취량 - 목표체중 권장 섭취량)
			user.total_remove_cal = analyzer.CalTotalRemoveCalorie(user.weight,
					user.goal_weight);
			// 사용자 다이어트용 권장 섭취량 (권장섭취량 - (총 소비해야될 칼로리))
			user.total_diet_recommended_intake = analyzer
					.CalDietRecommendedIntake(user.recommended_intake,
							user.total_remove_cal, user.goal_term);

			// 현재칼로리와 현재상태에 대한 부분
			display_now_status();
			now_calorie
			.setText(format.format(user.now_cal)
					+ "Kcal/"
					+ ((int) user.total_diet_recommended_intake / user.goal_term)
					+ "Kcal");
		}

		// level, nickname, point, ranking 디스플레이부
		display_lv.setText("LV " + user.user_level);
		display_nickname.setText(user.nickname);
		display_point.setText("    ⓟ" + user.user_point + "p");
		display_ranking.setText("    ⓡ" + user.user_ranking + "st");
	}

	@Override
	public void onPause() {
		super.onPause();
		// Log.e("main","onPause");
	}

	@Override
	public void onStop() {
		super.onStop();
		// Log.e("main","onStop");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		WriteSQLite();
		SendUserData();
	}

	/**
	 * 초기화 할때, SQLite에서 사용자 정보를 다시 가져오는 메소드
	 */
	public void Init() {
		// 프로그램이 종료되었다가 다시 실행될 때, 사용자가 입력한 정보를 SQLite에서 다시 받아오는 부분
		LoadSQLite();

		int temp_check_goal = -1; // 하루 목표 체크 플래그

		long _timeMillis = System.currentTimeMillis();
		String temp_date = DateFormat.format("yyyy-MM-dd", _timeMillis)
				.toString();

		if (!user.date.equals(temp_date) && !user.date.equals("")) {
			SBDBAdapter db = new SBDBAdapter(this, SBDBAdapter.SQL_CREATE_INFO,
					"daydata");

			db.openDB();

			String columns[] = { "day_goal_result" };
			Cursor cursor = db.selectTable("daydata", columns, null, null,
					null, null, null);

			if (cursor.moveToFirst()) {
				temp_check_goal = cursor.getInt(0);
			}

			StringBuffer buffer = new StringBuffer();
			buffer.append("user_id").append("=").append(user.Id).append("&");												
			buffer.append("ex_date").append("=").append(user.date).append("&");												
			buffer.append("now_date").append("=").append(temp_date).append("&"); 	
			buffer.append("check_goal").append("=").append(temp_check_goal);

			ConnectServer.HttpPostData("update_day_goal.php", buffer);
			user.date = temp_date;

			db.openDB();

			ContentValues values_daydata = new ContentValues();
			values_daydata.put("day_date", user.date);
			values_daydata.put("intake_cal", 0);
			values_daydata.put("used_cal", 0);
			values_daydata.put("day_goal_result", 0);
			values_daydata.put("now_cal", 0);
			values_daydata.put("now_status", 5);

			db.updateTable("daydata", values_daydata, null);
			db.close();
		}
	}

	@Override
	public void onClick(View v) {
		if (v == btn_ifitness) {
			intent = new Intent(SBMain.this, SBFitness.class);
			startActivity(intent);
		} else if (v == btn_ifood) {
			intent = new Intent(SBMain.this, SBFood.class);
			startActivityForResult(intent, FOOD);
		} else if (v == btn_diary) {
			intent = new Intent(SBMain.this, SBDiary.class);
			startActivity(intent);
		} else if (v == btn_group) {
			if (user.join_room_id == -1) {
				intent = new Intent(SBMain.this, SBGroupDiet.class);
				startActivity(intent);
			} else {
				intent = new Intent(SBMain.this, SBDisplayGroupRoom.class);
				startActivity(intent);
			}
		} else if (v == btn_coupon) {
			intent = new Intent(SBMain.this, SBCoupon.class);
			startActivity(intent);
		}
	}

	/**
	 * 현재 상태를 칼로리에 따라 5단계로 구분해서 Display해주는 메소드
	 */
	public void display_now_status() {

		user.now_status = analyzer.NowStatus(user.now_cal,
				((int) user.total_diet_recommended_intake / user.goal_term));
		switch (user.now_status) {
		case 0:
			now_status_image.setImageResource(R.drawable.main_before_fail);
			now_calorie.setTextColor(Color.RED);
			now_status.setText("다이어트 포기??!");
			break;
		case 1:
			now_status_image.setImageResource(R.drawable.main_before_fail);
			now_calorie.setTextColor(Color.RED);
			now_status.setText("위험!노력하세요!!");
			break;
		case 2:
			now_status_image.setImageResource(R.drawable.main_bad);
			now_calorie.setTextColor(Color.rgb(255, 165, 0));
			now_status.setText("운동이 필요합니다.");
			break;
		case 3:
			now_status_image.setImageResource(R.drawable.main_normal);
			now_calorie.setTextColor(Color.GREEN);
			now_status.setText("오늘 목표 달성!");
			break;
		case 4:
			now_status_image.setImageResource(R.drawable.main_good);
			now_calorie.setTextColor(Color.BLUE);
			now_status.setText("다이어트 욕심쟁이!");
			break;
		case 5:
			now_status_image.setImageResource(R.drawable.main_overdoit);
			now_calorie.setTextColor(Color.BLUE);
			now_status.setText("무리하지 마세요~");
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(getApplication()).inflate(R.menu.menu_main, menu);
		return (super.onCreateOptionsMenu(menu));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return (applyMenuChoice(item) || super.onContextItemSelected(item));
	}

	/**
	 * 선택한 메뉴를 실행시켜주는 메소드
	 * 
	 * @param item
	 *            메뉴item
	 * @return 동작이 정상적으로 이루어지면 True, 그렇지않으면 False
	 */
	private boolean applyMenuChoice(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item1: // 내정보 보기
			intent = new Intent(SBMain.this, SBMystatus.class);
			startActivity(intent);
			return (true);
		case R.id.item2: // 목표 재설정하기
			intent = new Intent(SBMain.this, SBResetGoal.class);
			startActivity(intent);
			return (true);
		case R.id.item3:
			intent = new Intent(SBMain.this, SBGraph.class);
			startActivity(intent);
			return (true);
		default:
			return (true);
		}
	}

	private void LoadSQLite() {
		SBDBAdapter db = new SBDBAdapter(this, SBDBAdapter.SQL_CREATE_INFO,
				"info");

		db.openDB();

		String columns[] = { "nick", "weight", "age", "gender", "height",
				"level", "exp", "goal_weight", "goal_term", "bmr", "bmi",
				"act", "rcmd_intake", "diet_rcmd_intake", "total_remove_cal",
		"diet_rcmd_intake_day" };

		Cursor cursor = db.selectTable(columns, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			user.nickname = cursor.getString(0);
			user.weight = cursor.getInt(1);
			user.age = cursor.getInt(2);
			user.gender = cursor.getInt(3);
			user.height = cursor.getInt(4);
			user.user_level = cursor.getInt(5);
			user.exp = cursor.getInt(6);
			user.goal_weight = cursor.getInt(7);
			user.goal_term = cursor.getInt(8);
			user.bmr = cursor.getDouble(9);
			user.bmi = cursor.getDouble(10);
			user.activationvalue_level = cursor.getInt(11);
			user.recommended_intake = cursor.getDouble(12);
			user.total_diet_recommended_intake = cursor.getDouble(13);
			user.total_remove_cal = cursor.getDouble(14);
			user.diet_recommended_intake = cursor.getDouble(15);
		}
		cursor.close();

		String columns_daydata[] = { "day_date", "intake_cal", "used_cal",
				"day_goal_result", "now_cal", "now_status", "join_room_id", };

		cursor = db.selectTable("daydata", columns_daydata, null, null, null,
				null, null);
		if (cursor.moveToFirst()) {

			user.date = cursor.getString(0);
			user.eat_cal = cursor.getDouble(1);
			user.used_cal = cursor.getDouble(2);
			user.now_cal = cursor.getDouble(4);
			user.now_status = cursor.getInt(5);
			user.join_room_id = cursor.getInt(6);

		}
		cursor.close();

		db.close();
	}

	private void WriteSQLite() {
		SBDBAdapter db = new SBDBAdapter(this, SBDBAdapter.SQL_CREATE_INFO,
				"info");

		db.openDB();

		ContentValues values = new ContentValues();
		values.put("nick", user.nickname);
		values.put("weight", user.weight);
		values.put("age", user.age);
		values.put("gender", user.gender);
		values.put("height", user.height);
		values.put("level", user.user_level);
		values.put("exp", user.exp);
		values.put("goal_weight", user.goal_weight);
		values.put("goal_term", user.goal_term);
		values.put("bmr", user.bmr);
		values.put("bmi", user.bmi);
		values.put("act", user.activationvalue_level);
		values.put("rcmd_intake", user.recommended_intake);
		values.put("diet_rcmd_intake", user.total_diet_recommended_intake);
		values.put("total_remove_cal", user.total_remove_cal);
		values.put("diet_rcmd_intake_day", user.diet_recommended_intake);
		values.put("point", user.user_point);

		db.updateTable("info", values, null);

		ContentValues values_daydata = new ContentValues();
		values_daydata.put("day_date", user.date);
		values_daydata.put("intake_cal", user.eat_cal);
		values_daydata.put("used_cal", user.used_cal);
		values_daydata.put("day_goal_result", user.now_status);
		values_daydata.put("now_cal", user.now_cal);
		values_daydata.put("now_status", user.now_status);
		values_daydata.put("join_room_id", user.join_room_id);

		db.updateTable("daydata", values_daydata, null);
		db.close();
	}

	private void SendUserData() {

		StringBuffer buffer = new StringBuffer();
		buffer.append("user_id").append("=").append(user.Id).append("&");
		buffer.append("nick").append("=").append(user.nickname).append("&");
		buffer.append("weight").append("=").append(user.weight).append("&");
		buffer.append("age").append("=").append(user.age).append("&");
		buffer.append("gender").append("=").append(user.gender).append("&");
		buffer.append("height").append("=").append(user.height).append("&");
		buffer.append("goal_weight").append("=").append(user.goal_weight)
		.append("&");
		buffer.append("goal_term").append("=").append(user.goal_term)
		.append("&");
		buffer.append("act").append("=").append(user.activationvalue_level)
		.append("&");
		buffer.append("bmr").append("=").append(user.bmr).append("&");
		buffer.append("bmi").append("=").append(user.bmi).append("&");
		buffer.append("rcmd_intake").append("=")
		.append(user.recommended_intake).append("&");
		buffer.append("diet_rcmd_intake").append("=")
		.append(user.total_diet_recommended_intake).append("&");
		buffer.append("total_remove_cal").append("=")
		.append(user.total_remove_cal).append("&");
		buffer.append("day_date").append("=").append(user.date).append("&");
		buffer.append("diet_rcmd_intake_day").append("=")
		.append(user.diet_recommended_intake);

		try{
			String temp_data = ConnectServer.HttpPostData("insert_user_data.php",
					buffer);
			Log.e("result", temp_data);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
