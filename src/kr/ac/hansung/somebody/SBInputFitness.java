package kr.ac.hansung.somebody;

import java.util.ArrayList;



import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * � �����Է��� �����ϴ� Ŭ����
 * 
 * @author An yongsoo(yongsu9@gmail.com) , MinSik Kim(minsik1218@naver.com)
 * @version 1.0
 */
public class SBInputFitness extends Activity implements OnItemSelectedListener,
		OnClickListener, OnItemClickListener {

	SBExercise exercise;
	SBExerciseAdapter exercise_adapter;
	ArrayList<SBExercise> exercise_list = new ArrayList<SBExercise>();

	SBAnalyzer analyzer; // Į�θ� �м��� ���� ����
	SBUser user; // ����� ������ �������ų� �����Ű�� ����
	SBDBAdapter db; // SQLite�� ����ϱ� ���� Adapter�� ����
	Spinner level;
	ListView exerciseList;
	EditText name, cal;
	Button okbtn, cancelbtn;
	String[] category = { "���� �", "������", "����� �", "�ϻ��Ȱ" };

	String temp_exercise_name = "";
	int temp_exercise_id = -1;
	int temp_exercise_time = -1;
	double temp_calorie = -1;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sbinput_fitness);

		user = SBUser.getInstance();
		exercise = new SBExercise();
		level = (Spinner) findViewById(R.id.sbinputfit_Sp1);
		exerciseList = (ListView) findViewById(R.id.sbinputfit_list1);
		name = (EditText) findViewById(R.id.sbinputfit_Ed1);
		cal = (EditText) findViewById(R.id.sbinputfit_Ed2);
		okbtn = (Button) findViewById(R.id.sbinputfit_Btn1);
		cancelbtn = (Button) findViewById(R.id.sbinputfit_Btn2);

		okbtn.setOnClickListener(this);
		cancelbtn.setOnClickListener(this);

		ArrayAdapter<String> levelselected = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, category);
		exercise_adapter = new SBExerciseAdapter(this, R.layout.exercise,
				exercise_list);

		level.setAdapter(levelselected);
		level.setOnItemSelectedListener(this);
		exerciseList.setOnItemClickListener(this);
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View view, int position,
			long arg3) {
		if (arg0 == level) {
			take_info(position);
			exerciseList.setAdapter(exercise_adapter);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		if (v == okbtn) {
			boolean blank_flag = true;
			String temp[] = { name.getText().toString(),
					cal.getText().toString() };

			for (int i = 0; i < temp.length; i++) {
				if (temp[i].equals("")) {
					blank_flag = false;
					break;
				}
			}

			if (blank_flag) {
				new AlertDialog.Builder(this)
						.setTitle("��Ϸ�")
						.setMessage(
								"" + temp_exercise_name + "��� �����Է� �Ͻðڽ��ϱ�?")
						.setNegativeButton("���",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {

									}
								})
						.setNeutralButton("Ȯ��",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										user.used_cal = Double.parseDouble(cal
												.getText().toString());
										// user.used_cal = temp_calorie;
										user.now_cal = user.now_cal
												- user.used_cal;
										user.user_point = 	user.user_point + (int) (user.used_cal * 10);
										if (temp_exercise_id != -1
												&& temp_exercise_time != -1
												&& temp_exercise_time != -1
												&& temp_calorie != -1) {
											user.put_exercise_results(user,
													temp_exercise_id,
													temp_calorie,
													temp_exercise_time, 0, 0);
										}
										finish();
									}
								}).show();
			} else
				Toast.makeText(this, "��ĭ�� ä���ּ���!", Toast.LENGTH_SHORT).show();
		} else if (v == cancelbtn) {
			Intent intent = getIntent();
			setResult(RESULT_OK, intent);
			finish();
		}
	}

	/**
	 * �������� ���� Json ������ String�� SBGroupMission��ü�� ��ȯ�ϴ� �Լ�
	 */
	public void take_info(int position) {
		exercise_list.clear();

		StringBuffer buffer = new StringBuffer();
		buffer.append("level_one").append("=").append(position);

		try {
			String Json = ConnectServer.HttpPostData("display_exercise.php",
					buffer);
			JSONArray ja = new JSONArray(Json);
			for (int j = 0; j < ja.length(); j++) {
				JSONObject order = ja.getJSONObject(j);

				SBExercise temp_exercise = new SBExercise();
				temp_exercise.exercise_name = order.getString("exercise_name");
				temp_exercise.exercise_time = order.getInt("exercise_time");
				temp_exercise.calorie = order.getDouble("calorie");
				temp_exercise.exercise_id = order.getInt("exercise_id");

				exercise_list.add(temp_exercise);

			}
		} catch (Exception e) {
			new AlertDialog.Builder(this)
					.setTitle("")
					.setMessage("����� �����ü� �����ϴ�.\n3G�Ǵ�WIFI������ Ȯ�����ּ���.")
					.setNeutralButton("Ȯ��",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).show();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		temp_exercise_name = exercise_list.get(position).exercise_name;
		temp_exercise_id = exercise_list.get(position).exercise_id;
		temp_exercise_time = exercise_list.get(position).exercise_time;
		temp_calorie = exercise_list.get(position).calorie;

		name.setText("" + exercise_list.get(position).exercise_name);
		cal.setText("" + exercise_list.get(position).calorie);
	}
}
