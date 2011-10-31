package kr.ac.hansung.somebody.group;

import kr.ac.hansung.somebody.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.*;

/**
 * 그룹다이어트기능의 Main Activity 
 * @author An yongsoo(yongsu9@gmail.com)
 * @version 1.0
 */
public class SBGroupDiet extends Activity implements OnItemClickListener, OnClickListener {
	Intent intent;
	ListView list_room;
	Button btn_makeroom;
	SBGroupRoomList group_room_list;
	SBGroupRoomAdapter group_room_adapter;
	public static Activity activity;
	int count = 0;
	int room_number = 0;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sbgrouplist);
		activity = this;
		
		group_room_list = SBGroupRoomList.getInstace();
		group_room_adapter = new SBGroupRoomAdapter(this, R.layout.room, group_room_list);
		btn_makeroom = (Button)findViewById(R.id.sbgroup_Btn1);
		btn_makeroom.setOnClickListener(this);
		
		list_room = (ListView)findViewById(R.id.sbgroup_list);
		list_room.setAdapter(group_room_adapter);
		list_room.setOnItemClickListener(this);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		Log.e("position", String.valueOf(position).toString());
		//그룹다이어트 방 List에서 선택한 방의 정보 보기
		intent = new Intent(SBGroupDiet.this, SBDisplayGroupRoom.class);
		intent.putExtra("position", position);
		startActivity(intent);
	}
	
	@Override
	public void onClick(View v) {
		if(v==btn_makeroom) {
			//그룹다이어트 방을 만들기 위한 Activity 이동
			intent = new Intent(SBGroupDiet.this, SBGroupMake.class);
			startActivity(intent);
		}
	}
}
