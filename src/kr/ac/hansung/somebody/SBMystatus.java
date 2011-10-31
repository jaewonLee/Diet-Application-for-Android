package kr.ac.hansung.somebody;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


/**
 * �������� Ȯ��, ���� �ϴ�  Ŭ���� 
 * @author An yongsoo(yongsu9@gmail.com) , MinSik Kim(minsik1218@naver.com)
 * @version 1.0
 */

public class SBMystatus extends Activity implements OnClickListener {
	
	public static final int MIN_AGE = 10; // �ּ� ���� 10��
	public static final int MIN_HEIGHT = 130; // �ּ� Ű 130cm
	public static final int MIN_WEIGHT = 30; // �ּ� ������ 30kg
	public static final int MAX_AGE = 70; // MIN_AGE�κ����� ���� (10+70=80)
	public static final int MAX_HEIGHT = 230; // MIN_HEIGHT�κ��� ����(130+100=230)
	public static final int MAX_WEIGHT = 140; // MIN_WEIGHT�κ��� ����(30+140=170)
	SBUser user;
	SBAnalyzer analyzer;
	RadioGroup gender;
	RadioButton man, woman;                               // ������� ������ ��Ÿ���� ���� ����
	EditText name, age, height, weight, twitID, twitPW;   // ������� �̸�, ����, Ű ,������, Ʈ����ID, �н����� ����
	TextView agetext, heighttext, weighttext, level, ranking, point; 
	Button okbtn, cancelbtn;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sbmystatus);
		user = SBUser.getInstance();
		analyzer = SBAnalyzer.getInstance();
		agetext = (TextView) findViewById(R.id.sbmystatus_Tv4);
		heighttext = (TextView) findViewById(R.id.sbmystatus_Tv6);
		weighttext = (TextView) findViewById(R.id.sbmystatus_Tv8);

		name = (EditText) findViewById(R.id.sbmystatus_Ed1);
		gender = (RadioGroup) findViewById(R.id.sbmystatus_radioGroup1);
		man = (RadioButton) findViewById(R.id.sbmystatus_man);
		woman = (RadioButton) findViewById(R.id.sbmystatus_woman);
		age = (EditText) findViewById(R.id.sbmystatus_Ed2);
		height = (EditText) findViewById(R.id.sbmystatus_Ed3);
		weight = (EditText) findViewById(R.id.sbmystatus_Ed4);
		level = (TextView) findViewById(R.id.sbmystatus_Tv10);
		ranking = (TextView) findViewById(R.id.sbmystatus_Tv12);
		point = (TextView) findViewById(R.id.sbmystatus_Tv14);
		twitID = (EditText) findViewById(R.id.sbmystatus_Ed5);
		twitPW = (EditText) findViewById(R.id.sbmystatus_Ed6);

		okbtn = (Button) findViewById(R.id.sbmystatus_Btn1);
		okbtn.setOnClickListener(this);
		cancelbtn = (Button) findViewById(R.id.sbmystatus_Btn2);
		cancelbtn.setOnClickListener(this);

		gender.setEnabled(false);

		if (user.gender == 0)
			man.setChecked(true);
		else
			woman.setChecked(true);

		twitID.setText(user.twitID);
		twitPW.setText(user.twitPW);
		name.setText(user.nickname);
		age.setText(user.age + "");
		height.setText(user.height + "");
		weight.setText(user.weight + "");
		level.setText("LV " + user.user_level);
		ranking.setText("�� " + user.user_ranking);
		point.setText("�� " + user.user_point);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == okbtn) {
			boolean blank_flag = true;
			String temp[] = {	name.getText().toString(), age.getText().toString(), 
								height.getText().toString(),weight.getText().toString()
			};
			
			for(int i = 0;i < temp.length; i++){
				if(temp[i].equals("")){
					blank_flag = false;
					break;
				}
			}	
			if	((Integer.parseInt(temp[1]) < MIN_AGE)
					|| (Integer.parseInt(temp[1]) > MAX_AGE)
					|| (Integer.parseInt(temp[2]) < MIN_HEIGHT)
					|| (Integer.parseInt(temp[2]) > MAX_HEIGHT)
					|| (Integer.parseInt(temp[3]) < MIN_WEIGHT)
					|| (Integer.parseInt(temp[3]) > MAX_WEIGHT))
				blank_flag = false;
			if(blank_flag){
				user.nickname = temp[0];
				user.age = Integer.parseInt(temp[1]);
				user.height = Integer.parseInt(temp[2]);
				user.weight = Integer.parseInt(temp[3]);
				user.twitID = twitID.getText().toString();
				user.twitPW = twitPW.getText().toString();
			
				user.bmi = analyzer.CalBmi(user.weight, user.height);
				user.user_bmi_level = analyzer.BmiLevel(user.bmi);
				Intent intent = getIntent();
				setResult(RESULT_OK, intent);
				finish();
			}
			else
				Toast.makeText(this, "���� ����� �Է����ּ���!", Toast.LENGTH_SHORT).show();
		} else {
			Intent intent = getIntent();
			setResult(RESULT_OK, intent);
			finish();
		}

	}

}
