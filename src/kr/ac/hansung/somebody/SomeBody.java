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
 * SomeBody Introȭ���� �����ϴ� Ŭ����
 * @author Jaewon Lee(jaewon87@naver.com)
 * @version 1.0
 */
public class SomeBody extends Activity {
	private static final int NEXT_MSEC = 3000;  // ���� ȭ������ �Ѿ�� Ÿ�̸�����

	Intent intent;

	SBUser user;
	SBDBAdapter db;       // SQLite�� ����ϱ� ���� DBAdapter ��ü ����

	AccountManager mgr;   // Mainȭ�鿡 ���� ���� ������ �������� Manager
	Account[] accts;      // ���� ��ϵ� ����� ������ ������ ��� Account �迭
	Account acct;         // �迭���� �ϳ��� ������ ������ �� ����ϴ� Account ����

	String return_value = null ;    // �������� �Ѿ�� �� value (user_id)

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sblogin);

		// Singleton��ü Instance�� �޾ƿ��� �κ�
		user = SBUser.getInstance();

		// SBDBAdapter ��ü�� �����԰� ���ÿ� ���̺��� ������ ���̺� ����
		db = new SBDBAdapter(this, SBDBAdapter.SQL_CREATE_INFO, "info");

		// AccountManager���� ���� ��� ���� ��������
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
	 * ����� ���¸� �ʱ�ȭ ��Ű�� �޼ҵ�
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

					//����� ���� �ʱ�ȭ 

					// �ۿ� �ܸ��� ���� ID���
					user.Id = acct.name;
					user.now_cal = 0;
					user.eat_cal = 0;
					user.used_cal = 0;
					user.user_level = 1;
					user.user_point = 0;
					user.user_ranking = 3; // ���߿� �������� �ο� ���� ����ؼ� �ʱ�ȭ �����ָ� ��.

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
				finish();    // ��Ƽ��Ƽ ����
			}
		};
		handler.sendEmptyMessageDelayed(0, NEXT_MSEC); // ms, 3���� �����Ŵ
	}
}
