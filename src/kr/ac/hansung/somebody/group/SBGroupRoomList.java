package kr.ac.hansung.somebody.group;

import java.util.ArrayList;

import kr.ac.hansung.somebody.ConnectServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;

/**
 * ������ �����ϴ� �׷���̾�Ʈ ���� Listȭ ��Ű�� Class
 * @author An yongsoo(yongsu9@gmail.com)
 * @version 1.0
 */
public class SBGroupRoomList extends ArrayList<SBGroupRoom> {

	private static final long serialVersionUID = 1L;
	private static SBGroupRoomList group_list = null;
	private SBGroupRoomList(){
		super();
	}

	public static SBGroupRoomList getInstace(){
		if(group_list == null)
		{
			group_list = new SBGroupRoomList();
		}
		group_list.take_info();
		return group_list;
	}

	/**
	 * �������� ���� Json ������ String�� SBGroupRoom��ü�� ��ȯ�ϴ� �Լ�
	 */
	public void take_info(){

		group_list.clear();
		String Json = ConnectServer.DownloadHtml("display_group_room.php");
		
		if(Json.equals("error")){
			new AlertDialog.Builder(SBGroupDiet.activity)
			.setTitle("")
			.setMessage("�������� ������ �� �����ϴ�.\n3G�Ǵ�WIFI������ Ȯ�����ּ���.")
			.setNeutralButton("Ȯ��", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.show();
		}
		
		Log.i("Json : " , Json);

		try{
			JSONArray ja = new JSONArray(Json);
			for(int j=0; j<ja.length(); j++){
				JSONObject order = ja.getJSONObject(j);

				SBGroupRoom room = new SBGroupRoom();

				room.room_id = order.getInt("room_id");
				room.room_name = order.getString("room_name");
				room.room_maker = order.getString("room_maker");
				room.limit_level = order.getInt("limit_level");
				room.persons = order.getInt("persons");
				room.limit_remain_term = order.getInt("limit_remain_term");
				room.walking_id = order.getInt("walking_id");
				room.walking_goal = order.getDouble("walking_goal");
				room.motion_id = order.getInt("motion_id");
				room.motion_goal = order.getDouble("motion_goal");
				room.suc_flag = order.getInt("suc_flag");
				room.maker_nick = order.getString("maker_nick");
				room.mission_name = order.getString("mission_name");
				room.join_person = order.getInt("join_person");
				room.m_limit_term = order.getInt("m_limit_term");
				room.m_walking_goal = order.getDouble("m_walking_goal");
				room.m_motion_goal = order.getDouble("m_motion_goal");
				
				group_list.add(room);
			}
		} catch(JSONException e) {

		}
	}
}
