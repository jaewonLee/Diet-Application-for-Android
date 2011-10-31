package kr.ac.hansung.somebody;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 음식 Class
 * 
 * @author An yongsoo(yongsu9@gmail.com)
 * @version 1.0
 */
class Food {

	String food_name;
	int gram;
	double calorie;

	Food() {
		food_name = "";
		gram = 0;
		calorie = 0;
	}
}

/**
 * 
 * @author Jaewon Lee(jaewon87@naver.com),An yongsoo(yongsu9@gmail.com)
 * @version 1.0
 */
public class SBFood extends Activity implements OnClickListener {
	// references to our images
	Context c = this;

	Food food = new Food();

	Gallery level1_food_list;
	Gallery level2_food_list;
	Gallery level3_food_list;
	EditText insert_food_name;
	TextView descrip_food;
	Button insert_food;
	Button serch_food;
	Button input_food;

	int level_one_position = 0;
	int level_two_position = 0;
	int level_three_position = 0;
	String food_description = "";

	// 음식 레벨별 이미지 구현
	private Integer[] img_level1 = { R.drawable.level1_meals,
			R.drawable.level1_fastfoods, R.drawable.level1_breads,
			R.drawable.level1_snacks, R.drawable.level1_fruits,
			R.drawable.level1_drinks };
	private Integer[][] img_level2_fastfood = {
			{ R.drawable.level1_meals, R.drawable.level1_fastfoods,
				R.drawable.level1_breads, R.drawable.level1_snacks,
				R.drawable.level1_fruits, R.drawable.level1_drinks },
				{ R.drawable.macdonald, R.drawable.lotteria, R.drawable.kfc,
					R.drawable.dunkin, R.drawable.popeyes, R.drawable.hardees }

	};
	private Integer[][] img_level3_hamburger = { { R.drawable.hamburger1,
		R.drawable.hamburger2, R.drawable.hamburger3,
		R.drawable.hamburger4, R.drawable.hamburger5,
		R.drawable.hamburger6, R.drawable.hamburger7,
		R.drawable.hamburger8, R.drawable.hamburger9,
		R.drawable.hamburger10, R.drawable.hamburger11,
		R.drawable.hamburger12, R.drawable.hamburger13 } };
	SBUser user = SBUser.getInstance();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sbinput_food);

		insert_food_name = (EditText) findViewById(R.id.insert_food_name);
		serch_food = (Button) findViewById(R.id.serch_food);
		serch_food.setOnClickListener(this);
		descrip_food = (TextView) findViewById(R.id.descrip_food);
		insert_food = (Button) findViewById(R.id.insert_food);
		insert_food.setOnClickListener(this);

		input_food = (Button) findViewById(R.id.input_food);
		input_food.setOnClickListener(this);

		level1_food_list = (Gallery) findViewById(R.id.gallery1);
		level1_food_list.setAdapter(new ImageAdapter(img_level1, this));

		level2_food_list = (Gallery) findViewById(R.id.gallery2);
		level3_food_list = (Gallery) findViewById(R.id.gallery3);

		level1_food_list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				if (position == 1) {
					level_one_position = position;
					level2_food_list.setAdapter(new ImageAdapter(
							img_level2_fastfood[position], c));
				}
			}
		});

		level2_food_list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				if (position == 0) {
					level_two_position = position;
					level3_food_list.setAdapter(new ImageAdapter(
							img_level3_hamburger[position], c));
				}
			}
		});

		level3_food_list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				// 서버에서 선택된 음식을 가져와 출력하는 부분
				level_three_position = position;
				StringBuffer buffer = new StringBuffer();
				buffer.append("level_one").append("=").append(level_one_position).append("&");
				buffer.append("level_two").append("=").append(level_two_position).append("&");
				buffer.append("level_three").append("=").append(level_three_position);
				food_description = ConnectServer.HttpPostData("display_food.php", buffer);
				ConnectServer.JsonBuilder(food, food_description);
				if(food.food_name.equals("") && food.calorie == 0 && food.gram == 0){
					new AlertDialog.Builder(SBFood.this)
					.setTitle("")
					.setMessage("음식정보를 가져올 수 없습니다.\n3G또는WIFI연결을 확인해주세요.")
					.setNeutralButton("확인", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					})
					.show();
				}
				else{
					String temp = "음식명 : " + food.food_name + ", 중량 : " + food.gram
							+ ", 열량  : " + food.calorie;
					descrip_food.setText(temp);
				}
			}
		});
	}

	class ImageAdapter extends BaseAdapter {
		private Context mContext;
		private Integer[] mThumbIds;

		public ImageAdapter(Integer[] imgs, Context c) {
			mThumbIds = imgs;
			mContext = c;
		}

		public int getCount() {
			return mThumbIds.length;
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		// create a new ImageView for each item referenced by the Adapter
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView;

			if (convertView == null) { // if it's not recycled, initialize some
				// attributes
				imageView = new ImageView(mContext);
				imageView.setLayoutParams(new Gallery.LayoutParams(127, 145));
				imageView.setScaleType(ImageView.ScaleType.FIT_XY);
				imageView.setPadding(10, 5, 10, 5);
			} else {
				imageView = (ImageView) convertView;
			}

			imageView.setImageResource(mThumbIds[position]);
			return imageView;
		}
	}

	public void onClick(View v) {
		if (v == insert_food) {
			// 유저가 음식을 선택하면 서버로 이력을 전송하는 부분
			user.eat_cal = food.calorie;
			long _timeMillis = System.currentTimeMillis();
			String temp = DateFormat.format("yyyy-MM-dd hh:mm:ss", _timeMillis)
					.toString();
			StringBuffer buffer = new StringBuffer();
			buffer.append("user_id").append("=").append(user.Id).append("&");
			buffer.append("date").append("=").append(temp).append("&");
			buffer.append("level_one").append("=").append(level_one_position)
			.append("&");
			buffer.append("level_two").append("=").append(level_two_position)
			.append("&");
			buffer.append("level_three").append("=")
			.append(level_three_position).append("&");
			buffer.append("food_name").append("=").append(food.food_name)
			.append("&");
			buffer.append("food_intake_cal").append("=").append(food.calorie);
			ConnectServer.HttpPostData("insert_food_results.php", buffer);
			new AlertDialog.Builder(this)
			.setTitle("음식")
			.setMessage(food.calorie + "kcal의 음식을 섭취하셨습니다.")
			.setNeutralButton("확인",
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,
						int which) {
					// TODO Auto-generated method stub
					user.now_cal += user.eat_cal;
					finish();
				}
			}).show();
		} else if (v == serch_food) {
			// 음식명을 검색하면 서버에서 찾아서 출력해주는 부분
			String temp_food_name = insert_food_name.getText().toString();
			StringBuffer buffer = new StringBuffer();
			buffer.append("food_name").append("=").append(temp_food_name);
			String temp_json = ConnectServer.HttpPostData("serch_food.php",
					buffer);
			if (!temp_json.equals("[]")) {
				ConnectServer.JsonBuilder(food, temp_json);
				String temp = "음식명 : " + food.food_name + ", 중량 : " + food.gram
						+ ", 열량  : " + food.calorie;
				descrip_food.setText(temp);
			} else
				Toast.makeText(SBFood.this, "해당 음식이 존재하지 않습니다.", 2000).show();
		} else if (v == input_food) {

			AlertDialog.Builder SBinput_food_builder;
			
			SBinput_food_builder = new AlertDialog.Builder(this);
			
			LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

			View sbdirect_layout = (View) inflater.inflate(
					R.layout.sbdirect_food,
					(ViewGroup) findViewById(R.id.sbdirect_Ly1));
			final EditText ed1 = (EditText) sbdirect_layout
					.findViewById(R.id.sbdirect_Et1);
			final EditText ed2 = (EditText) sbdirect_layout
					.findViewById(R.id.sbdirect_Et2);

			SBinput_food_builder.setView(sbdirect_layout);
			SBinput_food_builder.setTitle("음식 직접 입력");
			SBinput_food_builder.setPositiveButton("확인",
					new Dialog.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {

					boolean blank_flag = true;
					String edittemp[] = {	ed1.getText().toString(), ed2.getText().toString()	};
					for(int i = 0;i < edittemp.length; i++){
						if(edittemp[i].equals("")){
							blank_flag = false;
							break;
						}
					}
					if(blank_flag){
						food.food_name = ed1.getText().toString();
						food.calorie = Integer.parseInt(ed2.getText()
								.toString());
						String temp = "음식명 : " + food.food_name + ", 중량 : "
								+ food.gram + ", 열량  : " + food.calorie;
						descrip_food.setText(temp);
					}
					else{
						Toast.makeText(getApplicationContext(), "빈칸을 모두 입력해주세요!", Toast.LENGTH_LONG).show();
						
					}
					

				}
			});
			SBinput_food_builder.setNegativeButton("닫기",
					new Dialog.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
				}
			});
			SBinput_food_builder.show();

		}

	}
}