package kr.ac.hansung.somebody.group;

import java.util.ArrayList;

import kr.ac.hansung.somebody.ConnectServer;
import kr.ac.hansung.somebody.SBUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;

/**
 * ������ �����ϴ� �׷���̾�Ʈ �̼��� Listȭ ��Ű�� Class
 * @author An yongsoo(yongsu9@gmail.com)
 * @version 1.0
 */
public class SBGroupMissionList extends ArrayList<SBGroupMission>{
	SBUser user;
	
	private static final long serialVersionUID = 1L;
	private static SBGroupMissionList group_mission_list = null;
	private SBGroupMissionList(){
		super();
	}

	public static SBGroupMissionList getInstace(){
		if(group_mission_list == null)
		{
			group_mission_list = new SBGroupMissionList();

		}
		group_mission_list.take_info();
		return group_mission_list;
	}

	/**
	 * �������� ���� Json ������ String�� SBGroupMission��ü�� ��ȯ�ϴ� �Լ�
	 */
	public void take_info(){
		group_mission_list.clear();
		user = SBUser.getInstance();
		String Json = ConnectServer.DownloadHtml("display_group_mission.php");

		if(Json.equals("error")){
			new AlertDialog.Builder(SBGroupMake.activity)
			.setTitle("")
			.setMessage("�̼������� ������ �� �����ϴ�.\n3G�Ǵ�WIFI������ Ȯ�����ּ���.")
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
				int temp_level = order.getInt("limit_level");
				if(user.user_level >= temp_level)
				{
					SBGroupMission mission = new SBGroupMission();

					mission.mission_id = order.getInt("mission_id");
					mission.mission_name = order.getString("mission_name");
					mission.limit_level = temp_level;
					mission.limit_term = order.getInt("limit_term");
					mission.walking_id = order.getInt("walking_id");
					mission.walking_goal = order.getDouble("walking_goal");
					mission.motion_id = order.getInt("motion_id");
					mission.motion_goal = order.getDouble("motion_goal");
					mission.description = order.getString("description");

					group_mission_list.add(mission);
				}
			}
		} catch(JSONException e) {

		}
	}
}
