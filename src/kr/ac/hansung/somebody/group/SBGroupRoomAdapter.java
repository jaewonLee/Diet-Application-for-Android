package kr.ac.hansung.somebody.group;

import kr.ac.hansung.somebody.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 그룹다이어트 방 List를 ListView와 동기화 시키는 Class
 * @author An yongsoo(yongsu9@gmail.com)
 * @version 1.0
 */
public class SBGroupRoomAdapter extends BaseAdapter {

	LayoutInflater inflater;
	SBGroupRoomList sb_group_room_list;
	Context mContext;
	int mListLayout;
	
	
	public SBGroupRoomAdapter(Context tContext, int listLayout, SBGroupRoomList sb_group_room_list){
		mContext = tContext;
		mListLayout = listLayout;
		this.sb_group_room_list = sb_group_room_list;
		inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return sb_group_room_list.size();
	}

	@Override
	public Object getItem(int row_num) {
		// TODO Auto-generated method stub
		return sb_group_room_list.get(row_num);
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
		TextView room_name = (TextView)convertView.findViewById(R.id.room_name);
		TextView limit_day = (TextView)convertView.findViewById(R.id.limit_day);
		TextView room_size = (TextView)convertView.findViewById(R.id.room_size);
		TextView walking_cal = (TextView)convertView.findViewById(R.id.walking_cal);
		TextView motion_cal = (TextView)convertView.findViewById(R.id.motion_cal);
		TextView room_maker = (TextView)convertView.findViewById(R.id.room_maker);
		
		room_name.setText(sb_group_room_list.get(position).room_name);
		limit_day.setText("" + sb_group_room_list.get(position).m_limit_term);
		room_size.setText("" + sb_group_room_list.get(position).persons);
		walking_cal.setText("" + sb_group_room_list.get(position).m_motion_goal);
		motion_cal.setText("" + sb_group_room_list.get(position).m_motion_goal);
		room_maker.setText(sb_group_room_list.get(position).maker_nick);
		
		return convertView;
	}
}
