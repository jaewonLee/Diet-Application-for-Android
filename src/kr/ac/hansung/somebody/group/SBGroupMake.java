package kr.ac.hansung.somebody.group;

import kr.ac.hansung.somebody.ConnectServer;
import kr.ac.hansung.somebody.R;
import kr.ac.hansung.somebody.SBUser;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.*;

/**
 * 그룹다이어트 방을 생성하는 Class
 * 
 * @author An yongsoo(yongsu9@gmail.com)
 * @version 1.0
 */
public class SBGroupMake extends Activity implements OnItemClickListener,
		OnItemSelectedListener, OnClickListener {
	SBUser user;
	static public Activity activity;
	
	TextView insert_room_name;
	TextView mission_description;
	ListView mission_list;
	Button make_room;
	Spinner persons_s;
	String[] person_num = { "2", "3", "4", "5", "6", "7", "8", "9", "10" };

	String room_name = "";
	String room_maker;

	int persons = 2;
	int mission_id = -1;

	SBGroupMissionList group_mission_list;
	SBGroupMissionAdapter group_mission_adapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sbgroupmake);

		user = SBUser.getInstance();
		activity = this;
		
		insert_room_name = (TextView) findViewById(R.id.sbgroupmake_room_name);
		mission_description = (TextView) findViewById(R.id.sbgroupmake_mission_description);
		make_room = (Button) findViewById(R.id.sbgroupmake_make_room);
		make_room.setOnClickListener(this);

		persons_s = (Spinner) findViewById(R.id.sbgroupmake_persons);
		persons_s.setOnItemSelectedListener(this);

		ArrayAdapter<String> person_n = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, person_num);
		person_n.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		persons_s.setAdapter(person_n);

		group_mission_list = SBGroupMissionList.getInstace();
		group_mission_adapter = new SBGroupMissionAdapter(this,
				R.layout.mission, group_mission_list);

		mission_list = (ListView) findViewById(R.id.sbgroupmake_mission_list);
		mission_list.setAdapter(group_mission_adapter);
		mission_list.setOnItemClickListener(this);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		TextView temp = (TextView) persons_s.getSelectedView();
		String t = (String) temp.getText();
		Log.e("persons", t);
		this.persons = Integer.parseInt(t);

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		mission_id = group_mission_list.get(position).mission_id;
		String temp = "미션네임: " + group_mission_list.get(position).mission_name
				+ "\n";
		temp += "미션설명: " + group_mission_list.get(position).description;
		mission_description.setText(temp);
	}

	@Override
	public void onClick(View view) {
		room_name = insert_room_name.getText().toString();

		if (room_name.equals("")) {
			Toast.makeText(this, "방제목을 입력해주세요.", 2000).show();
		} else if (mission_id <= -1) {
			Toast.makeText(this, "미션을 선택해주세요.", 2000).show();
		} else if (mission_id > -1) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("room_name").append("=").append(room_name)
					.append("&"); // php 변수에 값 대입
			buffer.append("persons").append("=").append(persons).append("&"); // php변수	 앞에 '$' 붙이지않는다																 
			buffer.append("room_maker").append("=").append(user.Id)
					.append("&"); // 변수 구분은 '&' 사용
			buffer.append("mission_id").append("=").append(mission_id);

			String temp_data = ConnectServer.HttpPostData(
					"make_group_room.php", buffer);
			if (!temp_data.equals("")) {
				int room_id = Integer.parseInt(temp_data);
				user.join_room_id = room_id;
				Intent intent = new Intent(SBGroupMake.this,
						SBDisplayGroupRoom.class);
				intent.putExtra("room_id", room_id);
				startActivity(intent);
				finish();
			} else {
				Toast.makeText(SBGroupMake.this, "현재 방을 만들 수 없습니다.", 2000).show();
			}
		}
	}
}