package kr.ac.hansung.somebody;

import java.text.DecimalFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * � ��ǥ �缳�� Ŭ����
 * 
 * @author MinSik Kim(minsik1218@naver.com)
 * @version 1.0
 */

public class SBResetGoal extends Activity implements OnClickListener {
	public static final int MIN_GOAL_WEIGHT = 30; // �ּ� ������ 30kg
	public static final int MIN_GOAL_TERM = 10; // �ּ� �Ⱓ�� 10��-99�ϱ���
	public static final int MAX_GOAL_TERM = 91;
	EditText goalweight, term, bmi; // ������� ��ǥü��, �Ⱓ, BMI������ Ȯ�� �� �� �ִ� ����
	SBUser user;
	Button okbtn, cancelbtn;
	ImageButton bmibtn;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sbreset_goal);
		user = SBUser.getInstance();

		okbtn = (Button) findViewById(R.id.sbreset_Btn1);
		cancelbtn = (Button) findViewById(R.id.sbreset_Btn2);
		bmibtn = (ImageButton) findViewById(R.id.sbreset_Ibt1);
		bmibtn.setOnClickListener(this);
		okbtn.setOnClickListener(this);
		cancelbtn.setOnClickListener(this);
		bmi = (EditText) findViewById(R.id.sbreset_Ed1);
		goalweight = (EditText) findViewById(R.id.sbreset_Ed2);
		term = (EditText) findViewById(R.id.sbreset_Ed3);

		final DecimalFormat format = new DecimalFormat();
		format.applyLocalizedPattern("0.##");

		bmi.setText(format.format(user.bmi) + "");
		goalweight.setText(user.goal_weight + "");
		term.setText(user.goal_term + "");

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == okbtn) {
			boolean blank_flag = true;
			String temp[] = { goalweight.getText().toString(),
					term.getText().toString() };

			for (int i = 0; i < temp.length; i++) {
				if (temp[i].equals("")) {
					blank_flag = false;
					break;
				}
			}
			if ((Integer.parseInt(temp[1]) < MIN_GOAL_TERM)
					|| (Integer.parseInt(temp[1]) > MAX_GOAL_TERM)
					|| (Integer.parseInt(temp[0]) < MIN_GOAL_WEIGHT)) {
				blank_flag = false;
			}
			if (blank_flag) {
				user.goal_weight = Integer.parseInt(goalweight.getText()
						.toString());
				user.goal_term = Integer.parseInt(term.getText().toString());
				Intent intent = getIntent();
				setResult(RESULT_OK, intent);
				finish();
			} else {
				Toast.makeText(this,
						"���� ����� �Է��� �ּ���.(��ǥü���� �ּ�30kg�̰� ��ǥ�Ⱓ�� 10~90���Դϴ�.)",
						Toast.LENGTH_LONG).show();
			}

		} else if (v == cancelbtn) {
			Intent intent = getIntent();
			setResult(RESULT_OK, intent);
			finish();
		} else if (v == bmibtn) {
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
		}

	}

}
