package kr.ac.hansung.somebody;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Coupon의 리스트, 사용내역등을 관리하는 클래스 - 부분구현
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
		// 현재 Demo용으로 flag의 변화를 주어, 그룹다이어트가 실행되었을 때 View전환
		if (user.coupon_flag == 0)
			setContentView(R.layout.no_sbcoupon_mylist);
		else
			setContentView(R.layout.sbcoupon_mylist);
	}

	// 메뉴 선택
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		coupon_menu = menu;
		new MenuInflater(getApplication()).inflate(R.menu.coupon_menu, menu);
		return (super.onCreateOptionsMenu(menu));
	}

	// 선택된 메뉴에 대한 구현
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.point) {
			Intent intent = new Intent(SBCoupon.this, SBPoint.class);
			startActivity(intent);
			return true;
		} else
			return false;
	}
}
