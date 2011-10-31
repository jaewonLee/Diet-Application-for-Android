package kr.ac.hansung.somebody;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 운동 직접입력을 위해 필요한 Adapter 클래스
 * @author An yongsoo(yongsu9@gmail.com)
 * @version 1.0
 */
public class SBExerciseAdapter extends BaseAdapter {

	LayoutInflater inflater;
	ArrayList<SBExercise> exercise_list;
	Context mContext;
	int mListLayout;

	public SBExerciseAdapter(Context tContext, int listLayout,
			ArrayList<SBExercise> exercise_list) {
		mContext = tContext;
		mListLayout = listLayout;
		this.exercise_list = exercise_list;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return exercise_list.size();
	}

	@Override
	public Object getItem(int row_num) {
		// TODO Auto-generated method stub
		return exercise_list.get(row_num);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(mListLayout, parent, false);
		}
		TextView exercise_name = (TextView) convertView
				.findViewById(R.id.exercise_name);
		TextView exercise_time = (TextView) convertView
				.findViewById(R.id.exercise_time);
		TextView calorie = (TextView) convertView.findViewById(R.id.calorie);

		exercise_name.setText(exercise_list.get(position).exercise_name);
		exercise_time.setText("" + exercise_list.get(position).exercise_time);
		calorie.setText("" + exercise_list.get(position).calorie);

		return convertView;
	}

}
