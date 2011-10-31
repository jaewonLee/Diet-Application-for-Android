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
 * ����ڰ� ��ǥü�߰� �Ⱓ�� �����ϴ� Ŭ����
 * 
 * @author Jaewon Lee(jaewon87@naver.com)
 * @version 1.0
 */
public class SBSetGoal extends Activity implements View.OnClickListener {

	SBAnalyzer analyzer; // Į�θ� �м��� ���� ����
	SBUser user; // ����� ������ �������ų� �����Ű�� ����
	SBDBAdapter db; // SQLite�� ����ϱ� ���� Adapter�� ����
	Calendar now = null; // ��¥������ �����ϱ� ���� Calendar�� ����

	public static final int MIN_GOAL_WEIGHT = 30; // �ּ� ������ 30kg
	public static final int MIN_GOAL_TERM = 10; // �ּ� �Ⱓ�� 10��-99�ϱ���
	public static final int MAX_GOAL_TERM = 91;

	private TextView text_bmi; // BMI������ ��Ÿ���� textview
	private TextView text_complete_date; // �Ϸ����ڸ� ��Ÿ���� textview
	private EditText weight, term;
	// private NumberPicker pick_goal_weight; // ��ǥü���� �����ϱ� ���� picker
	// private NumberPicker pick_goal_term; // ��ǥ�Ⱓ�� �����ϱ� ���� picker
	private Button join, plus_term, plus_weight, minus_term, minus_weight;
	private ImageButton btn_bmi_info; // BMI���� ���� ��ư
	private ImageView health_light; // BMI������ ���� �ǰ� ��ȣ���� ����

	private String result_bmi; // BMI������ ���� �ѱ� ��� ���
	private String complete_day; // �Ϸ� ������

	private int[] items_goal_weight; // ��ǥü���� ������ ����
	private int[] items_goal_term; // ��ǥ�Ⱓ�� ������ ����

	private String[] strings_goal_weight; // ��ǥü���� ������ ������ ���ڿ�����
	private String[] strings_goal_term; // ��ǥ�Ⱓ�� ������ ������ ���ڿ�����

	AccountManager mgr; // Mainȭ�鿡 ���� ���� ������ �������� Manager
	Account[] accts; // ���� ��ϵ� ����� ������ ������ ��� Account �迭
	Account acct; // �迭���� �ϳ��� ������ ������ �� ����ϴ� Account ����

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sbsetgoal);
		Init(); // �ʱ�ȭ
	}

	/**
	 * ��ǥ������ ���� �� ��ɵ��� �ʱ�ȭ�� ���
	 */
	public void Init() {
		// Singleton��ü Instance�� �޾ƿ��� �κ�
		user = SBUser.getInstance();
		analyzer = SBAnalyzer.getInstance();

		// picker�� �ǵ帮�� �ʾ��� ��, �ʱⰪ���� ���� ����
		user.goal_weight = user.weight - 5; // �Է��� ü�ߺ��� -1kg�� �⺻������
		user.goal_term = 30;

		health_light = (ImageView) findViewById(R.id.sbsetgoal_imageView1);
		// BMI������ ���� ����
		btn_bmi_info = (ImageButton) findViewById(R.id.sbsetgoal_imageButton1);
		btn_bmi_info.setOnClickListener(this);

		// ����� �Է¿� ���� BMI������ �ܰ� ���
		user.bmi = analyzer.CalBmi(user.weight, user.height);
		user.user_bmi_level = analyzer.BmiLevel(user.bmi);
		/*
		 * Log.e("BMI", "BMI : "+user.bmi); Log.e("BMI",
		 * "BMI level : "+user.user_bmi_level);
		 */

		// DataFormat�� ���� �Ҽ��� ���̱� (���� ���ڸ� ǥ��)
		DecimalFormat format = new DecimalFormat();
		format.applyLocalizedPattern("0.##");

		// BMI������ ���� �ǰ���ȣ��
		/**
		 * < ���¿� ���� ��ȣ�� �з� > ���� - ��� ��ü��, �ʵ���, �ߵ��, ���� - ������ ��ü��, ����ü�� - Ȳ��
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

		// ��ǥ�Ⱓ�� ���ؼ� �Ϸ����� ���ϱ�
		complete_day = GetEndDate(user.goal_term);
		text_complete_date = (TextView) findViewById(R.id.textView3);
		text_complete_date.setText("�Ϸ����� : " + complete_day);

		// ��ǥü�߰� ��ǥ�Ⱓ picker���� �� ����
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
		// // Data����
		// SetPickItem();
		// pick_goal_weight.setSpeed(100); // pick��ư �ӵ� ����
		// pick_goal_weight.setRange(0, strings_goal_weight.length - 1,
		// strings_goal_weight); // picker ����
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
		// text_complete_date.setText("�Ϸ����� : " + complete_day);
		// // Log.e("aaa",String.valueOf(user.goal_term));
		// }
		// });
		//
		// // ���� ������ġ�� ������
		// pick_goal_weight.setCurrent(strings_goal_weight.length - 1);
		// pick_goal_term.setCurrent(20);

		// �����ϱ�
		join = (Button) findViewById(R.id.button1);
		join.setOnClickListener(this);
	}

	/**
	 * Picker�ȿ� �迭�� ���س��� Data���� Setting�ϴ¸޼ҵ�
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
	 * items_goal_term[j] + "��"; } }
	 */
	/**
	 * ���̾�Ʈ ���� ���� ���ϴ� �޼ҵ� (����� ���ó�¥�� �������� �Ǿ� �ִ� ����)
	 */
	public String GetStartDate() {
		Calendar temp = Calendar.getInstance();
		StringBuffer sbDate = new StringBuffer();

		int nYear = temp.get(Calendar.YEAR);
		int nMonth = temp.get(Calendar.MONTH) + 1;
		int nDay = temp.get(Calendar.DAY_OF_MONTH);

		sbDate.append(nYear + "��");
		sbDate.append(nMonth + "��");
		sbDate.append(nDay + "��");

		return sbDate.toString();
	}

	/**
	 * ���̾�Ʈ �����Ϸ� ���� ���� �Ⱓ ���� ��¥ ���ϴ� �޼ҵ� (�Ϸ�����)
	 * 
	 * @param iDay
	 *            �Ⱓ
	 * @return �Ϸ����� ���ڿ� ��
	 */
	public String GetEndDate(int iDay) {
		Calendar temp = Calendar.getInstance();
		StringBuffer sbDate = new StringBuffer();

		temp.add(Calendar.DAY_OF_MONTH, iDay);

		int nYear = temp.get(Calendar.YEAR);
		int nMonth = temp.get(Calendar.MONTH) + 1;
		int nDay = temp.get(Calendar.DAY_OF_MONTH);

		sbDate.append(nYear + "�� ");
		if (nMonth < 10)
			sbDate.append("0");
		sbDate.append(nMonth + "�� ");
		if (nDay < 10)
			sbDate.append("0");
		sbDate.append(nDay + "��");

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
						"���� ����� �Է��� �ּ���.(��ǥü���� �ּ�30kg�̰� ��ǥ�Ⱓ�� 10~90���Դϴ�.)",
						Toast.LENGTH_LONG).show();
			else {
				new AlertDialog.Builder(this)
						.setTitle("���ԿϷ�")
						.setMessage("������ �Ϸ� �Ǿ����ϴ�.")
						.setNeutralButton("Ȯ��",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										Intent intent = new Intent(
												SBSetGoal.this, SBMain.class);
										intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
												| Intent.FLAG_ACTIVITY_CLEAR_TOP);
										user.user_level = 1;

										// user Data�� Sqlite�� Update
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
											user.user_ranking = 3; // ���߿� ��������
																	// �ο� ����
																	// ����ؼ� �ʱ�ȭ
																	// �����ָ� ��.

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
					.setTitle("BMI������ ���� ")
					.setMessage(
							"      ~18.49 : ��ü��\n" + "18.5~22.9 : ����ü��\n"
									+ "23.0~23.9 : ��ü��\n"
									+ "24.0~24.9 : ����ü��\n"
									+ "25.0~29.9 : �ʵ���\n"
									+ "30.0~34.9 : �ߵ��\n"
									+ "        35.0~ : ����")
					.setNeutralButton("Ȯ��",
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
