package kr.ac.hansung.somebody;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Coupon�� ����Ʈ, ��볻������ �����ϴ� Ŭ���� - �κб���
 * 
 * @author Jaewon Lee(jaewon87@naver.com)
 * @version 1.0
 */
public class SBCoupon extends Activity {

	SBUser user;

	Menu coupon_menu;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		user = SBUser.getInstance();
		// ���� Demo������ flag�� ��ȭ�� �־�, �׷���̾�Ʈ�� ����Ǿ��� �� View��ȯ
		if (user.coupon_flag == 0)
			setContentView(R.layout.no_sbcoupon_mylist);
		else
			setContentView(R.layout.sbcoupon_mylist);
	}

	// �޴� ����
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		coupon_menu = menu;
		new MenuInflater(getApplication()).inflate(R.menu.coupon_menu, menu);
		return (super.onCreateOptionsMenu(menu));
	}

	// ���õ� �޴��� ���� ����
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.point) {
			Intent intent = new Intent(SBCoupon.this, SBPoint.class);
			startActivity(intent);
			return true;
		} else
			return false;
	}
}
