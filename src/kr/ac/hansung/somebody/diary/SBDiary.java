package kr.ac.hansung.somebody.diary;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kr.ac.hansung.somebody.ConnectServer;
import kr.ac.hansung.somebody.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.TableRow.LayoutParams;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


/**
 * ���� ���̾ ������ �����ϴ� Ŭ���� - �κб��� (��ó : http://funpython.com/blog/59?category=2)
 * 
 * @author Jaewon Lee(jaewon87@naver.com)
 * @version 1.0
 */
public class SBDiary extends Activity implements OnClickListener {

	private Calendar rightNow;
	private GregorianCalendar gCal;
	private int iYear = 0;
	private int iMonth = 0;
	public int count = 0;
	private int startDayOfweek = 0;
	private int maxDay = 0;
	private int oneday_width = 0;
	private int oneday_height = 0;
	public SBDayDiary day;
	ArrayList<String> daylist; // ���� ����� ������ �ִ´�. 1,2,3,4,.... 28?30?31?
	ArrayList<String> actlist; // ���ڿ� �ش��ϴ� Ȱ�������� ������ �ִ´�.

	TextView aDateTxt;

	private int dayCnt;
	private int mSelect = -1;

	HashMap<String, String> user_success_list; // ��ǥ �޼� ���θ���Ʈ key = ��¥(ex 2011-09-17) value = ��ǥ�޼�����(O,X)

	String str_user_day; // ��¥�� Date String��
	String goal_result; // ��ǥ �޼� ����
	String[] start_day; // ���� ��¥

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sbdiary_month);

		user_success_list = new HashMap<String, String>();
		rightNow = Calendar.getInstance();
		gCal = new GregorianCalendar();
		iYear = rightNow.get(Calendar.YEAR);
		iMonth = rightNow.get(Calendar.MONTH);

		Button btnMPrev = (Button) findViewById(R.id.btn_calendar_prevmonth);
		btnMPrev.setOnClickListener(this);
		Button btnMNext = (Button) findViewById(R.id.btn_calendar_nextmonth);
		btnMNext.setOnClickListener(this);

		btnMPrev.setText("����");
		btnMNext.setText("����");
		aDateTxt = (TextView) findViewById(R.id.CalendarMonthTxt);

		SetSuccessMonth();
		makeCalendardata(iYear, iMonth);

	}

	// �޷��� ���ڸ� ǥ���Ѵ�.
	private void printDate(String thisYear, String thisMonth) {

		if (thisMonth.length() == 1) {
			aDateTxt.setText(String.valueOf(thisYear) + "." + "0" + thisMonth);
		} else {
			aDateTxt.setText(String.valueOf(thisYear) + "." + thisMonth);
		}
	}

	// �޷¿� ǥ���� ���ڸ� �迭�� �־� �����Ѵ�.
	private void makeCalendardata(int thisYear, int thisMonth) {
		printDate(String.valueOf(thisYear), String.valueOf(thisMonth + 1));

		rightNow.set(thisYear, thisMonth, 1);
		gCal.set(thisYear, thisMonth, 1);
		startDayOfweek = rightNow.get(Calendar.DAY_OF_WEEK);

		maxDay = gCal.getActualMaximum((Calendar.DAY_OF_MONTH));
		if (daylist == null)
			daylist = new ArrayList<String>();
		daylist.clear();

		if (actlist == null)
			actlist = new ArrayList<String>();
		actlist.clear();

		daylist.add("��");
		actlist.add("");
		daylist.add("��");
		actlist.add("");
		daylist.add("ȭ");
		actlist.add("");
		daylist.add("��");
		actlist.add("");
		daylist.add("��");
		actlist.add("");
		daylist.add("��");
		actlist.add("");
		daylist.add("��");
		actlist.add("");

		if (startDayOfweek != 1) {
			gCal.set(thisYear, thisMonth - 1, 1);
			int prevMonthMaximumDay = (gCal
					.getActualMaximum((Calendar.DAY_OF_MONTH)) + 2);
			for (int i = startDayOfweek; i > 1; i--) {
				daylist.add(Integer.toString(prevMonthMaximumDay - i));
				actlist.add("p");
			}
		}

		for (int i = 1; i <= maxDay; i++) // ���ڸ� �ִ´�.
		{
			daylist.add(Integer.toString(i));
			actlist.add("");
		}

		int dayDummy = (startDayOfweek - 1) + maxDay;
		if (dayDummy > 35) {
			dayDummy = 42 - dayDummy;
		} else {
			dayDummy = 35 - dayDummy;
		}

		// ������..�׷��ϱ� ��ĭ�� �־� �޷� ����� �̻ڰ� ����� �ش�.
		if (dayDummy != 0) {
			for (int i = 1; i <= dayDummy; i++) {
				daylist.add(Integer.toString(i));
				actlist.add("n");
			}
		}

		makeCalendar();
	}

	private void makeCalendar() {
		final SBDayDiary[] oneday = new SBDayDiary[daylist.size()];
		final Calendar today = Calendar.getInstance();
		TableLayout tl = (TableLayout) findViewById(R.id.tl_calendar_monthly);
		tl.removeAllViews();

		dayCnt = 0;
		int maxRow = ((daylist.size() > 42) ? 7 : 6);
		int maxColumn = 7;

		oneday_width = getWindow().getWindowManager().getDefaultDisplay()
		.getWidth();
		oneday_height = getWindow().getWindowManager().getDefaultDisplay()
		.getHeight();

		oneday_height = ((((oneday_height >= oneday_width) ? oneday_height
				: oneday_width) - tl.getTop()) / (maxRow + 1)) - 10;
		oneday_width = (oneday_width / maxColumn) + 1;

		int daylistsize = daylist.size() - 1;
		for (int i = 1; i <= maxRow; i++) {
			TableRow tr = new TableRow(this);
			tr.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT));
			for (int j = 1; j <= maxColumn; j++) {
				// calender_oneday�� ������ ������ �ִ´�.
				oneday[dayCnt] = new SBDayDiary(getApplicationContext());

				// ���Ϻ� ���� ���ϱ�
				if ((dayCnt % 7) == 0) {
					oneday[dayCnt].setTextDayColor(Color.RED);
				} else if ((dayCnt % 7) == 6) {
					oneday[dayCnt].setTextDayColor(Color.GRAY);
				} else {
					oneday[dayCnt].setTextDayColor(Color.BLACK);
				}

				// ���� ǥ���� ����
				if (dayCnt >= 0 && dayCnt < 7) {
					oneday[dayCnt].setBgDayPaint(Color.DKGRAY); // ������
					oneday[dayCnt].setTextDayTopPadding(8); // ����ǥ�� �Ҷ� top
					// padding
					oneday[dayCnt].setTextDayColor(Color.WHITE); // ������ �۾� ����
					oneday[dayCnt].setTextDaySize(20); // ������ �۾�ũ��
					oneday[dayCnt].setLayoutParams(new LayoutParams(
							oneday_width, 35)); // ���� ��Ʈ�� ũ��
					oneday[dayCnt].isToday = false;

				} else {

					oneday[dayCnt].isToday = false;
					oneday[dayCnt].setDayOfWeek(dayCnt % 7 + 1);
					oneday[dayCnt].setDay(Integer.valueOf(daylist.get(dayCnt))
							.intValue());
					oneday[dayCnt].setTextActcntSize(14);
					oneday[dayCnt].setTextActcntColor(Color.BLACK);
					oneday[dayCnt].setTextActcntTopPadding(18);
					oneday[dayCnt]
					       .setBgSelectedDayPaint(Color.rgb(0, 162, 232));
					oneday[dayCnt].setBgTodayPaint(Color.LTGRAY);
					oneday[dayCnt].setBgActcntPaint(Color.rgb(251, 247, 176));
					oneday[dayCnt].setLayoutParams(new LayoutParams(
							oneday_width, oneday_height));

					// ���� �� �� ǥ��
					if (actlist.get(dayCnt).equals("p")) {
						oneday[dayCnt].setTextDaySize(18);
						actlist.set(dayCnt, "");
						oneday[dayCnt].setTextDayTopPadding(-4);

						if (iMonth - 1 < Calendar.JANUARY) {
							oneday[dayCnt].setMonth(Calendar.DECEMBER);
							oneday[dayCnt].setYear(iYear - 1);
						} else {
							oneday[dayCnt].setMonth(iMonth - 1);
							oneday[dayCnt].setYear(iYear);
						}

						// ���� �� �� ǥ��
					} else if (actlist.get(dayCnt).equals("n")) {
						oneday[dayCnt].setTextDaySize(18);
						actlist.set(dayCnt, "");
						oneday[dayCnt].setTextDayTopPadding(-4);
						if (iMonth + 1 > Calendar.DECEMBER) {
							oneday[dayCnt].setMonth(Calendar.JANUARY);
							oneday[dayCnt].setYear(iYear + 1);
						} else {
							oneday[dayCnt].setMonth(iMonth + 1);
							oneday[dayCnt].setYear(iYear);
						}
						// ���� �� �� ǥ��
					} else {
						oneday[dayCnt].setTextDaySize(24);
						oneday[dayCnt].setYear(iYear);
						oneday[dayCnt].setMonth(iMonth);

						// ���� ǥ��
						if (oneday[dayCnt].getDay() == today.get(Calendar.DAY_OF_MONTH)
								&& oneday[dayCnt].getMonth() == today.get(Calendar.MONTH)
								&& oneday[dayCnt].getYear() == today.get(Calendar.YEAR)) {

							oneday[dayCnt].isToday = true;
							oneday[dayCnt].invalidate();
							mSelect = dayCnt;
						}
						String temp = "" + oneday[dayCnt].getYear() + "-" + (oneday[dayCnt].getMonth()+1) + "-" + oneday[dayCnt].getDay();
						if(user_success_list.containsKey(temp))
							actlist.set(dayCnt, user_success_list.get(temp));
					}

					oneday[dayCnt]
					       .setOnLongClickListener(new OnLongClickListener() {
					    	   @Override
					    	   public boolean onLongClick(View v) {
					    		   return false;
					    	   }
					       });

					oneday[dayCnt].setOnTouchListener(new OnTouchListener() {

						@Override
						public boolean onTouch(View v, MotionEvent event) {

							if (oneday[v.getId()].getTextDay() != ""
								&& event.getAction() == MotionEvent.ACTION_UP) {
								if (mSelect != -1) {
									oneday[mSelect].setSelected(false);
									oneday[mSelect].invalidate();
								}
								oneday[v.getId()].setSelected(true);
								oneday[v.getId()].invalidate();
								mSelect = v.getId();

								onTouched(oneday[mSelect]);
							}
							return false;
						}
					});
				}

				oneday[dayCnt].setTextDay(daylist.get(dayCnt).toString()); // ����,����
				// �ֱ�
				oneday[dayCnt].setTextActCnt(actlist.get(dayCnt).toString());// Ȱ������
				// �ֱ�
				oneday[dayCnt].setId(dayCnt); // ������ ��ü�� �����Ҽ� �ִ� id�ֱ�
				oneday[dayCnt].invalidate();
				tr.addView(oneday[dayCnt]);
				if (daylistsize != dayCnt) {
					dayCnt++;
				} else {
					break;
				}
			}
			tl.addView(tr, new TableLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		}
	}

	/**
	 * ���ڸ� 2�ڸ� ���ڷ� ��ȯ, 2 -> 02
	 * 
	 * @param value
	 * @return
	 */
	protected String doubleString(int value) {
		String temp;

		if (value < 10) {
			temp = "0" + String.valueOf(value);

		} else {
			temp = String.valueOf(value);
		}
		return temp;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_calendar_nextmonth:
			if (iMonth == 11) {
				iYear = iYear + 1;
				iMonth = 0;
			} else {
				iMonth = iMonth + 1;
			}
			makeCalendardata(iYear, iMonth);
			break;
		case R.id.btn_calendar_prevmonth:
			if (iMonth == 0) {
				iYear = iYear - 1;
				iMonth = 11;
			} else {
				iMonth = iMonth - 1;
			}
			makeCalendardata(iYear, iMonth);
			break;
		}
	}

	/**
	 * ���� Ŭ�������� �������̵� �ؼ� ��ġ�� ��¥ �Է� �ޱ�
	 * 
	 * @param oneday
	 */
	protected void onTouched(SBDayDiary oneday) {

	}

	/**
	 * �ش� ���� ������ ���� �ȿ� �ִ��� �˻�
	 * 
	 * @param test
	 *            �˻��� ��¥
	 * @param basis
	 *            ���� ��¥
	 * @param during
	 *            �Ⱓ(��)
	 * @return
	 */
	protected boolean isInside(SBDayDiary test, SBDayDiary basis, int during) {
		Calendar calbasis = Calendar.getInstance();
		calbasis.set(basis.getYear(), basis.getMonth(), basis.getDay());
		calbasis.add(Calendar.DAY_OF_MONTH, during);

		Calendar caltest = Calendar.getInstance();
		caltest.set(test.getYear(), test.getMonth(), test.getDay());

		if (caltest.getTimeInMillis() < calbasis.getTimeInMillis()) {
			return true;
		}
		return false;
	}

	/**
	 * ���� �޷����� �̵�
	 */
	public void gotoToday() {
		final Calendar today = Calendar.getInstance();
		iYear = today.get(Calendar.YEAR);
		iMonth = today.get(Calendar.MONTH);
		makeCalendardata(today.get(Calendar.YEAR), today.get(Calendar.MONTH));
	}

	/**
	 * �������� �迭�� ��ǥ �޼� ���θ� �޾ƿ�
	 */
	public void SetSuccessMonth() {
		String check_goal;
		user_success_list.clear();

		String Json = ConnectServer.DownloadHtml("display_day_goal.php");
		if(Json.equals("")){
			new AlertDialog.Builder(this)
			.setTitle("")
			.setMessage("������ �����ü� �����ϴ�.\n3G�Ǵ�WIFI������ Ȯ�����ּ���.")
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
				String temp_date = order.getString("day_date");
				int temp_check_goal = order.getInt("check_goal");
				if(temp_check_goal == 1){
					check_goal = "O";
				}
				else
					check_goal = "X";

				user_success_list.put(temp_date, check_goal);
			}
		} catch(JSONException e) {
			e.printStackTrace();
		}
	}
}
