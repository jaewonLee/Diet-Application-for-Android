package kr.ac.hansung.somebody;

import java.text.DecimalFormat;
import java.util.Calendar;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

/**
 * 사용자가 목표체중과 기간을 설정하는 클래스
 * 
 * @author Jaewon Lee(jaewon87@naver.com)
 * @version 1.0
 */
public class SBSetGoal extends Activity implements View.OnClickListener {

	SBAnalyzer analyzer; // 칼로리 분석을 위한 변수
	SBUser user; // 사용자 정보를 가져오거나 저장시키는 변수
	SBDBAdapter db; // SQLite를 사용하기 위한 Adapter형 변수
	Calendar now = null; // 날짜정보를 관리하기 위한 Calendar형 변수

	public static final int MIN_GOAL_WEIGHT = 30; // 최소 몸무게 30kg
	public static final int MIN_GOAL_TERM = 10; // 최소 기간을 10일-99일까지
	public static final int MAX_GOAL_TERM = 91;

	private TextView text_bmi; // BMI지수를 나타내는 textview
	private TextView text_complete_date; // 완료일자를 나타내는 textview
	private EditText weight, term;
	// private NumberPicker pick_goal_weight; // 목표체중을 설정하기 위한 picker
	// private NumberPicker pick_goal_term; // 목표기간을 설정하기 위한 picker
	private Button join, plus_term, plus_weight, minus_term, minus_weight;
	private ImageButton btn_bmi_info; // BMI지수 도움말 버튼
	private ImageView health_light; // BMI지수에 따른 건강 신호등의 상태

	private String result_bmi; // BMI지수에 따른 한글 출력 결과
	private String complete_day; // 완료 예정일

	private int[] items_goal_weight; // 목표체중의 데이터 값들
	private int[] items_goal_term; // 목표기간의 데이터 값들

	private String[] strings_goal_weight; // 목표체중의 단위를 포함한 문자열값들
	private String[] strings_goal_term; // 목표기간의 단위를 포함한 문자열값들

	AccountManager mgr; // Main화면에 대한 인증 정보를 가져오는 Manager
	Account[] accts; // 폰에 등록된 사용자 정보를 가져와 담는 Account 배열
	Account acct; // 배열에서 하나의 정보를 가져올 때 사용하는 Account 변수

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sbsetgoal);
		Init(); // 초기화
	}

	/**
	 * 목표설정을 위한 각 기능들의 초기화를 담당
	 */
	public void Init() {
		// Singleton객체 Instance를 받아오는 부분
		user = SBUser.getInstance();
		analyzer = SBAnalyzer.getInstance();

		// picker를 건드리지 않았을 때, 초기값으로 들어가는 정보
		user.goal_weight = user.weight - 5; // 입력한 체중보다 -1kg을 기본값으로
		user.goal_term = 30;

		health_light = (ImageView) findViewById(R.id.sbsetgoal_imageView1);
		// BMI지수별 관련 정보
		btn_bmi_info = (ImageButton) findViewById(R.id.sbsetgoal_imageButton1);
		btn_bmi_info.setOnClickListener(this);

		// 사용자 입력에 따른 BMI지수와 단계 계산
		user.bmi = analyzer.CalBmi(user.weight, user.height);
		user.user_bmi_level = analyzer.BmiLevel(user.bmi);
		/*
		 * Log.e("BMI", "BMI : "+user.bmi); Log.e("BMI",
		 * "BMI level : "+user.user_bmi_level);
		 */

		// DataFormat을 통한 소수점 줄이기 (이하 두자리 표현)
		DecimalFormat format = new DecimalFormat();
		format.applyLocalizedPattern("0.##");

		// BMI지수에 따른 건강신호등
		/**
		 * < 상태에 따른 신호등 분류 > 정상 - 녹색 저체중, 초도비만, 중등도비만, 고도비만 - 빨간색 과체중, 위험체중 - 황색
		 */
		if (user.user_bmi_level == 2) {
			health_light.setImageResource(R.drawable.bmi_green);
		} else if (user.user_bmi_level == 1 || user.user_bmi_level == 6
				|| user.user_bmi_level == 5 || user.user_bmi_level == 0) {
			health_light.setImageResource(R.drawable.bmi_red);
		} else {
			health_light.setImageResource(R.drawable.bmi_yellow);
		}

		result_bmi = analyzer.CalUserState(user.user_bmi_level);

		text_bmi = (TextView) findViewById(R.id.sbsetgoal_textView1);
		text_bmi.setText(result_bmi + "(" + format.format(user.bmi) + ")");

		// 목표기간을 통해서 완료일자 구하기
		complete_day = GetEndDate(user.goal_term);
		text_complete_date = (TextView) findViewById(R.id.textView3);
		text_complete_date.setText("완료일자 : " + complete_day);

		// 목표체중과 목표기간 picker설정 및 조정
		weight = (EditText) findViewById(R.id.sbsetgoal_Weight);
		weight.setText(user.goal_weight + "");
		term = (EditText) findViewById(R.id.sbsetgoal_Term);
		term.setText(user.goal_term + "");
		
		plus_weight = (Button) findViewById(R.id.sbsetgoal_Btn1);
		plus_weight.setOnClickListener(this);
		plus_term = (Button) findViewById(R.id.sbsetgoal_Btn2);
		plus_term.setOnClickListener(this);
		minus_weight = (Button) findViewById(R.id.sbsetgoal_Btn3);
		minus_weight.setOnClickListener(this);
		minus_term = (Button) findViewById(R.id.sbsetgoal_Btn4);
		minus_term.setOnClickListener(this);
	
		// pick_goal_weight = (NumberPicker)
		// findViewById(R.id.sbsetgoal_picker1);
		// pick_goal_term = (NumberPicker) findViewById(R.id.sbsetgoal_picker2);
		//
		// // Data세팅
		// SetPickItem();
		// pick_goal_weight.setSpeed(100); // pick버튼 속도 설정
		// pick_goal_weight.setRange(0, strings_goal_weight.length - 1,
		// strings_goal_weight); // picker 범위
		// pick_goal_weight.setOnChangeListener(new OnChangedListener() {
		// @Override
		// public void onChanged(NumberPicker picker, int oldVal, int newVal) {
		// user.goal_weight = items_goal_weight[newVal];
		// // Log.e("aaa",String.valueOf(user.goal_weight));
		// }
		// });
		//
		// pick_goal_term.setSpeed(100);
		// pick_goal_term.setRange(0, strings_goal_term.length - 1,
		// strings_goal_term);
		// pick_goal_term.setOnChangeListener(new OnChangedListener() {
		//
		// @Override
		// public void onChanged(NumberPicker picker, int oldVal, int newVal) {
		// user.goal_term = items_goal_term[newVal];
		// complete_day = GetEndDate(user.goal_term);
		// text_complete_date.setText("완료일자 : " + complete_day);
		// // Log.e("aaa",String.valueOf(user.goal_term));
		// }
		// });
		//
		// // 현재 시작위치를 설정함
		// pick_goal_weight.setCurrent(strings_goal_weight.length - 1);
		// pick_goal_term.setCurrent(20);

		// 가입하기
		join = (Button) findViewById(R.id.button1);
		join.setOnClickListener(this);
	}

	/**
	 * Picker안에 배열로 정해놓은 Data들을 Setting하는메소드
	 */
	/*
	 * public void SetPickItem() { strings_goal_weight = new String[user.weight
	 * - MIN_GOAL_WEIGHT]; items_goal_weight = new int[user.weight -
	 * MIN_GOAL_WEIGHT]; for (int i = 0; i < strings_goal_weight.length; i++) {
	 * items_goal_weight[i] = i + MIN_GOAL_WEIGHT; strings_goal_weight[i] =
	 * items_goal_weight[i] + "Kg"; }
	 * 
	 * strings_goal_term = new String[MAX_GOAL_TERM]; items_goal_term = new
	 * int[MAX_GOAL_TERM]; for (int j = 0; j < items_goal_term.length; j++) {
	 * items_goal_term[j] = j + MIN_GOAL_TERM; strings_goal_term[j] =
	 * items_goal_term[j] + "일"; } }
	 */
	/**
	 * 다이어트 시작 일자 구하는 메소드 (현재는 오늘날짜를 가져오게 되어 있는 상태)
	 */
	public String GetStartDate() {
		Calendar temp = Calendar.getInstance();
		StringBuffer sbDate = new StringBuffer();

		int nYear = temp.get(Calendar.YEAR);
		int nMonth = temp.get(Calendar.MONTH) + 1;
		int nDay = temp.get(Calendar.DAY_OF_MONTH);

		sbDate.append(nYear + "년");
		sbDate.append(nMonth + "월");
		sbDate.append(nDay + "일");

		return sbDate.toString();
	}

	/**
	 * 다이어트 시작일로 부터 일정 기간 후의 날짜 구하는 메소드 (완료일자)
	 * 
	 * @param iDay
	 *            기간
	 * @return 완료일자 문자열 값
	 */
	public String GetEndDate(int iDay) {
		Calendar temp = Calendar.getInstance();
		StringBuffer sbDate = new StringBuffer();

		temp.add(Calendar.DAY_OF_MONTH, iDay);

		int nYear = temp.get(Calendar.YEAR);
		int nMonth = temp.get(Calendar.MONTH) + 1;
		int nDay = temp.get(Calendar.DAY_OF_MONTH);

		sbDate.append(nYear + "년 ");
		if (nMonth < 10)
			sbDate.append("0");
		sbDate.append(nMonth + "월 ");
		if (nDay < 10)
			sbDate.append("0");
		sbDate.append(nDay + "일");

		return sbDate.toString();
	}

	public void onClick(View v) {
		if (v == join) {
			user.goal_term = Integer.parseInt(term.getText().toString());
			user.goal_weight = Integer.parseInt(weight.getText().toString());
			if ((Integer.parseInt(term.getText().toString()) < MIN_GOAL_TERM)
					|| (Integer.parseInt(term.getText().toString()) > MAX_GOAL_TERM)
					|| (Integer.parseInt(weight.getText().toString()) < MIN_GOAL_WEIGHT))
				Toast.makeText(this,
						"값을 제대로 입력해 주세요.(목표체중은 최소30kg이고 목표기간은 10~90일입니다.)",
						Toast.LENGTH_LONG).show();
			else {
				new AlertDialog.Builder(this)
						.setTitle("가입완료")
						.setMessage("가입이 완료 되었습니다.")
						.setNeutralButton("확인",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										Intent intent = new Intent(
												SBSetGoal.this, SBMain.class);
										intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
												| Intent.FLAG_ACTIVITY_CLEAR_TOP);
										user.user_level = 1;

										// user Data를 Sqlite에 Update
										/*
										 * ContentValues values = new
										 * ContentValues();
										 * values.put("goal_weight",
										 * user.goal_weight);
										 * values.put("goal_term",
										 * user.goal_term); values.put("bmi",
										 * user.bmi); values.put("level",
										 * user.user_level);
										 * 
										 * db = new SBDBAdapter(SBSetGoal.this,
										 * SBDBAdapter.SQL_CREATE_INFO, "info");
										 * db.openDB(); db.updateTable(values,
										 * null); db.close();
										 */

										if (user.Id.equals("")) {
											mgr = AccountManager
													.get(SBSetGoal.this);
											accts = mgr.getAccounts();
											for (int i = 0; i < accts.length; i++) {
												acct = accts[i];
											}

											user.Id = acct.name;
											user.now_cal = 0;
											user.eat_cal = 0;
											user.used_cal = 0;
											user.user_level = 1;
											user.user_point = 0;
											user.user_ranking = 3; // 나중에 서버에서
																	// 인원 수에
																	// 대비해서 초기화
																	// 시켜주면 됨.

											ContentValues values = new ContentValues();
											values.put("user_id", user.Id);
											values.put("nick", user.nickname);
											values.put("gender", user.gender);
											values.put("height", user.height);
											values.put("weight", user.weight);
											values.put("age", user.age);
											values.put("act",
													user.activationvalue);
											values.put("goal_weight",
													user.goal_weight);
											values.put("goal_term",
													user.goal_term);
											values.put("bmi", user.bmi);
											values.put("level", user.user_level);

											db = new SBDBAdapter(
													SBSetGoal.this,
													SBDBAdapter.SQL_CREATE_INFO,
													"info");
											db.openDB();
											db.insertTable(values);
											values.clear();

											values.put("user_id", user.Id);
											values.put("day_date", user.date);
											db.insertTable("daydata", values);
											db.close();

											StringBuffer buffer = new StringBuffer();
											buffer.append("user_id")
													.append("=")
													.append(user.Id);
											try {
												String temp_room_id = ConnectServer
														.HttpPostData(
																"check_group_diet.php",
																buffer);
												user.join_room_id = Integer
														.parseInt(temp_room_id);
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
										startActivity(intent);
									}
								}).show();
			}
		} else if (v == btn_bmi_info) {
			new AlertDialog.Builder(this)
					.setTitle("BMI지수별 도움말 ")
					.setMessage(
							"      ~18.49 : 저체중\n" + "18.5~22.9 : 정상체중\n"
									+ "23.0~23.9 : 과체중\n"
									+ "24.0~24.9 : 위험체중\n"
									+ "25.0~29.9 : 초도비만\n"
									+ "30.0~34.9 : 중등도비만\n"
									+ "        35.0~ : 고도비만")
					.setNeutralButton("확인",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}
							}).show();
		} else if (v == plus_term) {
			int temp = Integer.parseInt(term.getText().toString());
			if ((temp < MIN_GOAL_TERM) || (temp > MAX_GOAL_TERM)) 
				;
			else{
				temp++;
				term.setText(temp + "");
			}
			
		} else if (v == minus_term) {
			int temp = Integer.parseInt(term.getText().toString());
			if ((temp < MIN_GOAL_TERM) || (temp > MAX_GOAL_TERM)) 
				;
			else{
				temp--;
				term.setText(temp + "");
			}
		} else if (v == plus_weight) {
			int temp = Integer.parseInt(weight.getText().toString());
			if (temp < MIN_GOAL_WEIGHT) 
				;
			else{
				temp++;
				weight.setText(temp + "");
			}
		} else if (v == minus_weight) {
			int temp = Integer.parseInt(weight.getText().toString());
			if (temp < MIN_GOAL_TERM) 
				;
			else{
				temp--;
				weight.setText(temp + "");
			}

		}
	}
}
