package kr.ac.hansung.somebody.group;

import kr.ac.hansung.somebody.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 그룹다이어트 미션 List를 ListView와 동기화 시키는 Class
 * @author An yongsoo(yongsu9@gmail.com)
 * @version 1.0
 */
public class SBGroupMissionAdapter extends BaseAdapter {

	LayoutInflater inflater;
	SBGroupMissionList sb_group_mission_list;
	Context mContext;
	int mListLayout;
	String[] motion_name_list = {"걷기","팔운동","수영","윗몸일으키기"};
	
	public SBGroupMissionAdapter(Context tContext, int listLayout, SBGroupMissionList sb_group_mission_list){
		mContext = tContext;
		mListLayout = listLayout;
		this.sb_group_mission_list = sb_group_mission_list;
		inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return sb_group_mission_list.size();
	}

	@Override
	public Object getItem(int row_num) {
		// TODO Auto-generated method stub
		return sb_group_mission_list.get(row_num);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null){
			convertView = inflater.inflate(mListLayout, parent, false);
		}
		TextView mission_name = (TextView)convertView.findViewById(R.id.mission_name);
		TextView limit_level = (TextView)convertView.findViewById(R.id.mission_limit_level);
		TextView limit_day = (TextView)convertView.findViewById(R.id.mission_limit_day);
		TextView walking_goal = (TextView)convertView.findViewById(R.id.mission_walking_goal);
		TextView motion_goal = (TextView)convertView.findViewById(R.id.mission_motion_goal);
		
		mission_name.setText(sb_group_mission_list.get(position).mission_name);
		limit_level.setText("" + sb_group_mission_list.get(position).limit_level);
		limit_day.setText("" + sb_group_mission_list.get(position).limit_term + "일");
		if(sb_group_mission_list.get(position).walking_goal != 0)
			walking_goal.setText("걷기 목표 : " + sb_group_mission_list.get(position).walking_goal + "Km");
		int temp = sb_group_mission_list.get(position).motion_id;
		if(temp != 999)
			motion_goal.setText(motion_name_list[temp] + " 모션게임 목표 : " + sb_group_mission_list.get(position).motion_goal + (temp==2?"M":"회"));
		
		
		return convertView;
	}

}
