package kr.ac.hansung.somebody;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * SomeBody Intro화면을 관리하는 클래스
 * @author Jaewon Lee(jaewon87@naver.com)
 * @version 1.0
 */
public class SomeBody extends Activity {
	private static final int NEXT_MSEC = 3000;  // 다음 화면으로 넘어가는 타이머지정

	Intent intent;

	SBUser user;
	SBDBAdapter db;       // SQLite를 사용하기 위한 DBAdapter 객체 선언

	AccountManager mgr;   // Main화면에 대한 인증 정보를 가져오는 Manager
	Account[] accts;      // 폰에 등록된 사용자 정보를 가져와 담는 Account 배열
	Account acct;         // 배열에서 하나의 정보를 가져올 때 사용하는 Account 변수

	String return_value = null ;    // 메인으로 넘어가게 할 value (user_id)

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sblogin);

		// Singleton객체 Instance를 받아오는 부분
		user = SBUser.getInstance();

		// SBDBAdapter 객체를 생성함과 동시에 테이블이 없으면 테이블 생성
		db = new SBDBAdapter(this, SBDBAdapter.SQL_CREATE_INFO, "info");

		// AccountManager에서 개인 등록 정보 가져오기
		mgr = AccountManager.get(this);
		accts = mgr.getAccounts();
		for(int i=0;i<accts.length;i++){
			acct = accts[i];
		}

		db.openDB();
		String columns[] = {"user_id"};
		Cursor cursor = db.selectTable(columns, null, null, null, null, null);
		if(cursor.moveToFirst()) {
			user.Id = cursor.getString(0);
			Log.e("Return id", "value = "+user.Id);
		}
		else {
			user.Id = "";
		}
		cursor.close();
		db.close();
		initialize();
	}

	/**
	 * 사용자 상태를 초기화 시키는 메소드
	 */
	private void initialize()
	{
		user.user_level = 1;  //TestCode
		Handler handler = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				if(user.Id.equals("")) {

					/*long _timeMillis = System.currentTimeMillis();
					String temp_date = DateFormat.format("yyyy-MM-dd", _timeMillis).toString();
					user.date = temp_date;

					//사용자 상태 초기화 

					// 앱에 단말기 유저 ID등록
					user.Id = acct.name;
					user.now_cal = 0;
					user.eat_cal = 0;
					user.used_cal = 0;
					user.user_level = 1;
					user.user_point = 0;
					user.user_ranking = 3; // 나중에 서버에서 인원 수에 대비해서 초기화 시켜주면 됨.

					ContentValues values = new ContentValues();
					values.put("user_id", user.Id);

					db.openDB();
					db.insertTable(values);
					values.clear();

					values.put("user_id", user.Id);
					values.put("day_date", user.date);
					db.insertTable("daydata", values);
					db.close();

					StringBuffer buffer = new StringBuffer();
					buffer.append("user_id").append("=").append(user.Id);
					try{
						String temp_room_id = ConnectServer.HttpPostData("check_group_diet.php", buffer);
						user.join_room_id = Integer.parseInt(temp_room_id);
					}catch(Exception e){
						e.printStackTrace();
					}*/
					intent = new Intent(SomeBody.this, SBSetInfo.class);
					startActivity(intent);
				}
				finish();    // 액티비티 종료
			}
		};
		handler.sendEmptyMessageDelayed(0, NEXT_MSEC); // ms, 3초후 종료시킴
	}
}
