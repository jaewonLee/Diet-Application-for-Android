package kr.ac.hansung.somebody;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RemoteViews.ActionException;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * ������� ��ü������ �Է� �޴� Ŭ����
 * 
 * @author Jaewon Lee(jaewon87@naver.com)
 * @version 1.0
 */
public class SBSetInfo extends Activity implements View.OnClickListener,
		OnSeekBarChangeListener, OnCheckedChangeListener, OnTouchListener {
	SBUser user; // ����� ������ �������ų� �����Ű�� ����
	SBDBAdapter db; // SQLite�� ����ϱ� ���� Adapter�� ����
	SBMain main;
	SBAnalyzer analyzer; // Į�θ� �м��� ���� ����
	public static final int MIN_AGE = 10; // �ּ� ���� 10��
	public static final int MIN_HEIGHT = 130; // �ּ� Ű 130cm
	public static final int MIN_WEIGHT = 30; // �ּ� ������ 30kg
	public static final int MAX_AGE = 70; // MIN_AGE�κ����� ���� (10+70=80)
	public static final int MAX_HEIGHT = 230; // MIN_HEIGHT�κ��� ����(130+100=230)
	public static final int MAX_WEIGHT = 140; // MIN_WEIGHT�κ��� ����(30+140=170)

	private EditText nick, age, height, weight; // Nickname �Է��ϴ� Editâ
	private RadioGroup insert_gender; // ���� �Է��ϴ� Radio��ư
	private SeekBar activation; // Ȱ���� �Է� SeekBar
	private ImageView act_bar; // Ȱ���� ������ ��Ÿ���� �̹���
	// private NumberPicker pick_age; // ���̸� �����ϱ� ���� picker
	// private NumberPicker pick_height; // Ű�� �����ϱ� ���� picker
	// private NumberPicker pick_weight; // �����Ը� �����ϱ� ���� picker
	private int[] items_age; // ������ ������ ����
	private int[] items_height; // Ű�� ������ ����
	private int[] items_weight; // ü���� ������ ����
	private String[] strings_age; // ������ ������ ������ ���ڿ� ����
	private String[] strings_height; // Ű�� ������ ������ ���ڿ� ����
	private String[] strings_weight; // ü���� ������ ������ ���ڿ� ����
	private TextView act_info; // Ȱ���� ������ ǥ�����ִ� textview
	private Button join; // �������� �Ѿ�� ��ư
	private Button plus_age, plus_height, plus_weight, minus_age, minus_height,
			minus_weight;

	public Context con;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sbsetinfo);
		analyzer = SBAnalyzer.getInstance();
		main = new SBMain();
		main.SBMain_Allact.add(this);
		Init(); // �ʱ�ȭ
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			onBackPressed();
		}

		return super.onKeyDown(keyCode, event);

	}

	public void onBackPressed() {
		for (int i = 0; i < main.SBMain_Allact.size(); i++) {
			main.SBMain_Allact.get(i).finish(); // List�� Static �̹Ƿ�,
												// Class��.������.get����
			// ����
		}

		ActivityManager am = (ActivityManager) this
				.getSystemService(Activity.ACTIVITY_SERVICE);
		am.killBackgroundProcesses(this.getPackageName());
		super.onBackPressed();

	}

	// public void requestKillProcess(Context context)
	// {
	// // int sdkVersion= Integer.parseInt(Build.VERSION.SDK);
	// // if(sdkVersion <8) {
	// ActivityManager am =
	// (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
	// am.restartPackage(getPackageName());
	// // }
	// // else{
	//
	// // }
	// }

	/**
	 * ��ü���� �Է��� ���� �� ��ɵ��� �ʱ�ȭ�� ���
	 */
	public void Init() {
		user = SBUser.getInstance();

		// picker�� �ǵ帮�� �ʾ��� ��, �ʱⰪ���� ���� ����
		user.gender = 0;
		user.age = 24;
		user.height = 165;
		user.weight = 60;
		user.goal_weight = user.weight - 5;
		user.goal_term = 30;
		user.bmi = analyzer.CalBmi(user.weight, user.height);
		user.user_bmi_level = analyzer.BmiLevel(user.bmi);
		user.activationvalue = 0;

		nick = (EditText) findViewById(R.id.sbsetinfo_editText1);

		insert_gender = (RadioGroup) findViewById(R.id.sbsetinfo_radioGroup1);
		insert_gender.setOnCheckedChangeListener(this);

		join = (Button) findViewById(R.id.button1);
		join.setOnClickListener(this);

		activation = (SeekBar) findViewById(R.id.seekBar1);
		activation.setMax(40);
		activation.incrementProgressBy(1);
		activation.setOnSeekBarChangeListener(this);

		act_info = (TextView) findViewById(R.id.sbsetinfo_textView1);
		act_info.setText("�ַ� �����⸦ ���ַ� ��Ȱ�ϴ� �����..;");

		plus_age = (Button) findViewById(R.id.sbsetinfo_Btn1);
		plus_age.setOnClickListener(this);
		plus_age.setOnTouchListener(this);
		plus_height = (Button) findViewById(R.id.sbsetinfo_Btn2);
		plus_height.setOnClickListener(this);
		plus_weight = (Button) findViewById(R.id.sbsetinfo_Btn3);
		plus_weight.setOnClickListener(this);
		minus_age = (Button) findViewById(R.id.sbsetinfo_Btn4);
		minus_age.setOnClickListener(this);
		minus_height = (Button) findViewById(R.id.sbsetinfo_Btn5);
		minus_height.setOnClickListener(this);
		minus_weight = (Button) findViewById(R.id.sbsetinfo_Btn6);
		minus_weight.setOnClickListener(this);
		minus_weight.setOnTouchListener(this);
		
		act_bar = (ImageView) findViewById(R.id.imageView1);
		age = (EditText) findViewById(R.id.sbsetinfo_Age);
		age.setText(user.age + "");
		height = (EditText) findViewById(R.id.sbsetinfo_Height);
		height.setText(user.height + "");
		weight = (EditText) findViewById(R.id.sbsetinfo_Weight);
		weight.setText(user.weight + "");

		/*
		 * pick_age = (NumberPicker) findViewById(R.id.picker1); pick_height =
		 * (NumberPicker) findViewById(R.id.picker2); pick_weight =
		 * (NumberPicker) findViewById(R.id.picker3);
		 * 
		 * SetPickItem(); pick_age.setSpeed(100); pick_age.setRange(0,
		 * strings_age.length - 1, strings_age);
		 * pick_age.setOnChangeListener(new OnChangedListener() {
		 * 
		 * @Override public void onChanged(NumberPicker picker, int oldVal, int
		 * newVal) { user.age = items_age[newVal]; Log.e("aaa",
		 * String.valueOf(user.age)); } });
		 * 
		 * pick_height.setSpeed(100); pick_height.setRange(0,
		 * strings_height.length - 1, strings_height);
		 * pick_height.setOnChangeListener(new OnChangedListener() {
		 * 
		 * @Override public void onChanged(NumberPicker picker, int oldVal, int
		 * newVal) { user.height = items_height[newVal]; Log.e("aaa",
		 * String.valueOf(user.height)); } });
		 * 
		 * pick_weight.setSpeed(100); pick_weight.setRange(0,
		 * strings_weight.length - 1, strings_weight);
		 * pick_weight.setOnChangeListener(new OnChangedListener() {
		 * 
		 * @Override public void onChanged(NumberPicker picker, int oldVal, int
		 * newVal) { user.weight = items_weight[newVal]; Log.e("aaa",
		 * String.valueOf(user.weight)); } });
		 * 
		 * // ���� ������ġ�� ������ pick_age.setCurrent(14); pick_height.setCurrent(35);
		 * pick_weight.setCurrent(30);
		 */
	}

	/**
	 * Picker�ȿ� �迭�� ���س��� Data���� Setting�ϴ¸޼ҵ�
	 */

	// public void SetPickItem() {
	// strings_age = new String[MAX_AGE];
	// items_age = new int[MAX_AGE];
	// for (int i = 0; i < strings_age.length; i++) {
	// items_age[i] = i + MIN_AGE;
	// strings_age[i] = items_age[i] + "��";
	// }
	// strings_height = new String[MAX_HEIGHT];
	// items_height = new int[MAX_HEIGHT];
	// for (int j = 0; j < strings_height.length; j++) {
	// items_height[j] = j + MIN_HEIGHT;
	// strings_height[j] = items_height[j] + "cm";
	// }
	// strings_weight = new String[MAX_WEIGHT];
	// items_weight = new int[MAX_WEIGHT];
	// for (int k = 0; k < strings_weight.length; k++) {
	// items_weight[k] = k + MIN_WEIGHT;
	// strings_weight[k] = items_weight[k] + "kg";
	// }
	// }

	public void onClick(View v) {
		if (v == join) {
			boolean blank_flag = true;
			String temp = nick.getText().toString();
			if (temp.equals(""))
				blank_flag = false;
			else if ((Integer.parseInt(age.getText().toString()) < MIN_AGE)
					|| (Integer.parseInt(age.getText().toString()) > MAX_AGE)
					|| (Integer.parseInt(height.getText().toString()) < MIN_HEIGHT)
					|| (Integer.parseInt(height.getText().toString()) > MAX_HEIGHT)
					|| (Integer.parseInt(weight.getText().toString()) < MIN_WEIGHT)
					|| (Integer.parseInt(weight.getText().toString()) > MAX_WEIGHT))
				blank_flag = false;

			if (blank_flag) {
				user.nickname = nick.getText().toString();

				user.age = Integer.parseInt(age.getText().toString());
				user.height = Integer.parseInt(height.getText().toString());
				user.weight = Integer.parseInt(weight.getText().toString());

				ContentValues values = new ContentValues();
				values.put("nick", user.nickname);
				values.put("gender", user.gender);
				values.put("height", user.height);
				values.put("weight", user.weight);
				values.put("age", user.age);
				values.put("act", user.activationvalue);
				Intent intent = new Intent(SBSetInfo.this, SBSetGoal.class);
				startActivity(intent);

			} else

				Toast.makeText(this, "���� ����� �Է����ּ���.", Toast.LENGTH_SHORT)
						.show();
			// Toast.makeText(this, "��ĭ�� ä���ּ���!", Toast.LENGTH_SHORT).show();
		} else if (v == plus_age) {
			int temp = Integer.parseInt(age.getText().toString());
			if ((temp < MIN_AGE) || (temp > MAX_AGE))
				;
			else {
				temp++;
				age.setText(temp + "");
			}
		} else if (v == plus_height) {
			int temp = Integer.parseInt(height.getText().toString());
			if ((temp < MIN_HEIGHT) || (temp > MAX_HEIGHT))
				;
			else {
				temp++;
				height.setText(temp + "");
			}

		} else if (v == plus_weight) {
			int temp = Integer.parseInt(weight.getText().toString());
			if ((temp < MIN_WEIGHT) || (temp > MAX_WEIGHT))
				;
			else {
				temp++;
				weight.setText(temp + "");
			}
		} else if (v == minus_age) {
			int temp = Integer.parseInt(age.getText().toString());
			if ((temp < MIN_AGE) || (temp > MAX_AGE))
				;
			else {
				temp--;
				age.setText(temp + "");
			}
		} else if (v == minus_height) {
			int temp = Integer.parseInt(height.getText().toString());
			if ((temp < MIN_HEIGHT) || (temp > MAX_HEIGHT))
				;
			else {
				temp--;
				height.setText(temp + "");
			}
		} else if (v == minus_weight) {
			int temp = Integer.parseInt(weight.getText().toString());
			if ((temp < MIN_WEIGHT) || (temp > MAX_WEIGHT))
				;
			else {
				temp--;
				weight.setText(temp + "");
			}
		}
	}

	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
		user.activationvalue = arg1;
		if (user.activationvalue <= 5) {
			act_info.setText("�ַ� �����⸦ ���ַ� ��Ȱ�ϴ� �����..;");
		} else if (user.activationvalue <= 15) {
			act_info.setText("�ɾ��ִ� �ð��� ���� ȸ���,�л����� �����");
		} else if (user.activationvalue <= 25) {
			act_info.setText("���� ��Ȱ�� �ϸ�, �ȴ� �ð��� �ְ� Ȱ���� �����");
		} else if (user.activationvalue <= 35) {
			act_info.setText("����� ���� ��Ȱ�ϸ�, �̵������� ���� �����");
		} else if (user.activationvalue <= 40) {
			act_info.setText("��� ���ַ� ��Ȱ�ϸ�, �������� �ſ� Ȱ���� �����");
		}
		Log.e("shootshoot", String.valueOf(user.activationvalue));
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checked_id) {
		if (checked_id == R.id.sbsetinfo_man) {
			user.gender = 0;
			act_bar.setImageResource(R.drawable.act_bar_male);
		} else if (checked_id == R.id.sbsetinfo_woman) {
			user.gender = 1;
			act_bar.setImageResource(R.drawable.act_bar_female);
		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent e) {
		// TODO Auto-generated method stub
		if(v == plus_age)
		{
			int temp = Integer.parseInt(age.getText().toString());
			if(e.getAction() == e.ACTION_DOWN)
			{
				temp++;
			}
			else if(e.getAction() == e.ACTION_UP)
			{
				age.setText(temp+"");
						
			}
		}
		return false;
	}
}
