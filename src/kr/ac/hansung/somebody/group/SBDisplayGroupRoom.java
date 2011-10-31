package kr.ac.hansung.somebody.group;

import kr.ac.hansung.somebody.ConnectServer;
import kr.ac.hansung.somebody.R;
import kr.ac.hansung.somebody.SBMain;
import kr.ac.hansung.somebody.SBUser;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * �׷���̾�Ʈ ���� ������ִ� Class
 * 
 * @author An yongsoo(yongsu9@gmail.com)
 * @version 1.0
 */
public class SBDisplayGroupRoom extends Activity implements OnClickListener {
	SBUser user;
	SBGroupRoom group_room;
	int button_status_flag;

	int position;
	String[] mosion_name = { "�ȿ", "����", "��������Ű��" };
	TextView room_title;
	TextView mission_name;
	TextView room_persons;
	TextView room_term;
	TextView room_walking_goal;
	TextView room_motion_goal;
	Button join_group;
	Button finish_group;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sbgrouproom);

		room_title = (TextView) findViewById(R.id.room_title);
		mission_name = (TextView) findViewById(R.id.room_mission_name);
		room_persons = (TextView) findViewById(R.id.room_persons);
		room_term = (TextView) findViewById(R.id.room_term);
		room_walking_goal = (TextView) findViewById(R.id.room_walking_goal);
		room_motion_goal = (TextView) findViewById(R.id.room_motion_goal);
		join_group = (Button) findViewById(R.id.join_group);
		join_group.setOnClickListener(this);
		finish_group = (Button) findViewById(R.id.finish_group);
		user = SBUser.getInstance();

		Intent intent = getIntent();
		position = intent.getIntExtra("position", 9999);
		if (position != 9999) {
			// �׷���̾�Ʈ ���� List���� �������� ��
			SBGroupRoomList list = SBGroupRoomList.getInstace();
			group_room = list.get(position);
		} else {
			// �׷���̾�Ʈ ���� ���� ���� ��
			group_room = new SBGroupRoom();
			int room_id = intent.getIntExtra("room_id", 0);
			if (room_id == 0)
				room_id = user.join_room_id;
			StringBuffer buffer = new StringBuffer();
			buffer.append("room_id").append("=").append(room_id);
			String temp_data = ConnectServer.HttpPostData(
					"display_group_room.php", buffer);
			ConnectServer.JsonBuilder(group_room, temp_data);
		}
		room_title.setText(group_room.room_name);
		mission_name.setText(group_room.mission_name);
		room_persons.setText(group_room.join_person + "�� / "
				+ group_room.persons + "��");
		room_term.setText(group_room.limit_remain_term + "�� / "
				+ group_room.m_limit_term + "��");
		room_walking_goal.setText(group_room.walking_goal + "Km" + " / "
				+ (group_room.m_walking_goal * group_room.persons) + "Km");
		room_motion_goal.setText(group_room.motion_goal
				+ (group_room.motion_id == 2 ? "M" : "Kcal") + " / "
				+ (group_room.m_motion_goal * group_room.persons)
				+ (group_room.motion_id == 2 ? "M" : "Kcal"));
		CheckSucflag(group_room.suc_flag);
	}

	@Override
	public void onResume() {
		super.onResume();

		/*if(user.join_room_id != -1 && user.Id.equals(group_room.room_maker)){
			join_group.setOnClickListener(null);
			join_group.setClickable(false);
			join_group.setVisibility(View.GONE);

			finish_group.setOnClickListener(this);
			finish_group.setClickable(true);
			finish_group.setVisibility(View.VISIBLE);
		}
		else if(user.join_room_id != -1 && button_status_flag == 1){
			join_group.setOnClickListener(null);
			join_group.setClickable(false);
			join_group.setVisibility(View.GONE);

			finish_group.setOnClickListener(this);
			finish_group.setClickable(true);
			finish_group.setVisibility(View.VISIBLE);
		}
		else if(button_status_flag == 2){
			join_group.setOnClickListener(null);
			join_group.setClickable(false);
			join_group.setVisibility(View.GONE);

			finish_group.setOnClickListener(null);
			finish_group.setClickable(false);
			finish_group.setVisibility(View.GONE);
		}*/
		if(group_room.suc_flag == 2){
			join_group.setOnClickListener(null);
			join_group.setClickable(false);
			join_group.setVisibility(View.GONE);

			finish_group.setOnClickListener(null);
			finish_group.setClickable(false);
			finish_group.setVisibility(View.VISIBLE);
		}
		else if (user.join_room_id != -1) {
			join_group.setOnClickListener(null);
			join_group.setClickable(false);
			join_group.setVisibility(View.GONE);

			finish_group.setOnClickListener(this);
			finish_group.setClickable(true);
			finish_group.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		if (v == join_group) {
			new AlertDialog.Builder(this)
			.setTitle("�׷���̾�Ʈ")
			.setMessage(
					"�ѹ� ���� �����Ͻø� ����ñ���\n ���� �����Ǽ� �����ϴ�\n �̹濡 ���� �Ͻðڽ��ϱ�?")
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
							StringBuffer buffer = new StringBuffer();
							buffer.append("user_id").append("=")
							.append(user.Id)
							.append("&"); // ���� ������ '&' ���
							buffer.append("room_id").append("=")
							.append(group_room.room_id);
							String temp = ConnectServer.HttpPostData(
									"join_group_room.php", buffer);
							int temp_check = Integer.parseInt(temp);
							if (temp_check != 0) {
								user.join_room_id = group_room.room_id;
								Toast.makeText(SBDisplayGroupRoom.this,
										"�濡 ���� �ϼ̽��ϴ�.", 2000).show();
								Intent intent = new Intent(
										SBDisplayGroupRoom.this,
										SBDisplayGroupRoom.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
								SBGroupDiet.activity.finish();
								startActivity(intent);
							} else {
								Toast.makeText(SBDisplayGroupRoom.this,
										"�濡 ���� �Ͻ� �� �����ϴ�..", 2000)
										.show();
							}
						}
					}).show();
		} else if (v == finish_group) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("room_id").append("=").append(user.join_room_id);
			String temp = ConnectServer.HttpPostData("finish_group_room.php",
					buffer);
			int temp_check = Integer.parseInt(temp);
			if (temp_check != 0) {
				user.join_room_id = -1;
				user.coupon_flag = 1;
				new AlertDialog.Builder(this)
				.setTitle("�׷���̾�Ʈ")
				.setMessage(
						"�׷���̾�Ʈ�� �Ϸ� �ƽ��ϴ�.\nȹ�� ����ġ : 500\nȹ�� ����Ʈ: 300")
						.setNeutralButton("Ȯ��",
								new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(
										SBDisplayGroupRoom.this,
										SBMain.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
										| Intent.FLAG_ACTIVITY_SINGLE_TOP);
								startActivity(intent);
								finish();
							}
						}).show();
			} else {
				Toast.makeText(SBDisplayGroupRoom.this, "�������� ���߽��ϴ�..", 2000)
				.show();
			}
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			event.startTracking();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.isTracking()
				&& !event.isCanceled()) {
			if (user.join_room_id != -1) {
				Intent intent = new Intent(SBDisplayGroupRoom.this,
						SBMain.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			} else
				finish();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	/**
	 * �׷���̾�Ʈ���� ���¸� üũ�ؼ� ������ �˷��ְų� ��������ִ� �Լ�
	 * @param suc_flag 
	 * 					�׷���̾�Ʈ���� ���� FLAG
	 * @return
	 * 					���� ���� �� ���¸� 0, ���� ���¸� 1, ���� ���¸� 2�� return
	 */
	public int CheckSucflag(int suc_flag){
		/*if(suc_flag == 1){
			new AlertDialog.Builder(this)
			.setTitle("")
			.setMessage("�׷���̾�Ʈ�� ���۵ƽ��ϴ�.\n������ ��� �ּ���~")
			.setNeutralButton("Ȯ��", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.show();
			return 1;
		}*/
		if(suc_flag == 2){
			user.join_room_id = -1;
			new AlertDialog.Builder(this)
			.setTitle("")
			.setMessage("�׷���̾�Ʈ�� ����ƽ��ϴ�.\n�����ϼ̽��ϴ�~")
			.setNeutralButton("Ȯ��", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.show();
			return 2;
		}
		else return 0;
	}
}
