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
 * 월별 다이어리 내용을 관리하는 클래스 - 부분구현 (출처 : http://funpython.com/blog/59?category=2)
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
	ArrayList<String> daylist; // 일자 목록을 가지고 있는다. 1,2,3,4,.... 28?30?31?
	ArrayList<String> actlist; // 일자에 해당하는 활동내용을 가지고 있는다.

	TextView aDateTxt;

	private int dayCnt;
	private int mSelect = -1;

	HashMap<String, String> user_success_list; // 목표 달성 여부리스트 key = 날짜(ex 2011-09-17) value = 목표달성여부(O,X)

	String str_user_day; // 날짜별 Date String값
	String goal_result; // 목표 달성 여부
	String[] start_day; // 시작 날짜

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

		btnMPrev.setText("이전");
		btnMNext.setText("다음");
		aDateTxt = (TextView) findViewById(R.id.CalendarMonthTxt);

		SetSuccessMonth();
		makeCalendardata(iYear, iMonth);

	}

	// 달력의 일자를 표시한다.
	private void printDate(String thisYear, String thisMonth) {

		if (thisMonth.length() == 1) {
			aDateTxt.setText(String.valueOf(thisYear) + "." + "0" + thisMonth);
		} else {
			aDateTxt.setText(String.valueOf(thisYear) + "." + thisMonth);
		}
	}

	// 달력에 표시할 일자를 배열에 넣어 구성한다.
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

		daylist.add("일");
		actlist.add("");
		daylist.add("월");
		actlist.add("");
		daylist.add("화");
		actlist.add("");
		daylist.add("수");
		actlist.add("");
		daylist.add("목");
		actlist.add("");
		daylist.add("금");
		actlist.add("");
		daylist.add("토");
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

		for (int i = 1; i <= maxDay; i++) // 일자를 넣는다.
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

		// 자투리..그러니까 빈칸을 넣어 달력 모양을 이쁘게 만들어 준다.
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
				// calender_oneday를 생성해 내용을 넣는다.
				oneday[dayCnt] = new SBDayDiary(getApplicationContext());

				// 요일별 색상 정하기
				if ((dayCnt % 7) == 0) {
					oneday[dayCnt].setTextDayColor(Color.RED);
				} else if ((dayCnt % 7) == 6) {
					oneday[dayCnt].setTextDayColor(Color.GRAY);
				} else {
					oneday[dayCnt].setTextDayColor(Color.BLACK);
				}

				// 요일 표시줄 설정
				if (dayCnt >= 0 && dayCnt < 7) {
					oneday[dayCnt].setBgDayPaint(Color.DKGRAY); // 배경색상
					oneday[dayCnt].setTextDayTopPadding(8); // 일자표시 할때 top
					// padding
					oneday[dayCnt].setTextDayColor(Color.WHITE); // 일자의 글씨 색상
					oneday[dayCnt].setTextDaySize(20); // 일자의 글씨크기
					oneday[dayCnt].setLayoutParams(new LayoutParams(
							oneday_width, 35)); // 일자 컨트롤 크기
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

					// 이전 달 블럭 표시
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

						// 다음 달 블럭 표시
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
						// 현재 달 블럭 표시
					} else {
						oneday[dayCnt].setTextDaySize(24);
						oneday[dayCnt].setYear(iYear);
						oneday[dayCnt].setMonth(iMonth);

						// 오늘 표시
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

				oneday[dayCnt].setTextDay(daylist.get(dayCnt).toString()); // 요일,일자
				// 넣기
				oneday[dayCnt].setTextActCnt(actlist.get(dayCnt).toString());// 활동내용
				// 넣기
				oneday[dayCnt].setId(dayCnt); // 생성한 객체를 구별할수 있는 id넣기
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
	 * 숫자를 2자리 문자로 변환, 2 -> 02
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
	 * 서브 클래스에서 오버라이드 해서 터치한 날짜 입력 받기
	 * 
	 * @param oneday
	 */
	protected void onTouched(SBDayDiary oneday) {

	}

	/**
	 * 해당 일이 기준일 범위 안에 있는지 검사
	 * 
	 * @param test
	 *            검사할 날짜
	 * @param basis
	 *            기준 날짜
	 * @param during
	 *            기간(일)
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
	 * 오늘 달력으로 이동
	 */
	public void gotoToday() {
		final Calendar today = Calendar.getInstance();
		iYear = today.get(Calendar.YEAR);
		iMonth = today.get(Calendar.MONTH);
		makeCalendardata(today.get(Calendar.YEAR), today.get(Calendar.MONTH));
	}

	/**
	 * 서버에서 배열로 목표 달성 여부를 받아옴
	 */
	public void SetSuccessMonth() {
		String check_goal;
		user_success_list.clear();

		String Json = ConnectServer.DownloadHtml("display_day_goal.php");
		if(Json.equals("")){
			new AlertDialog.Builder(this)
			.setTitle("")
			.setMessage("정보를 가져올수 없습니다.\n3G또는WIFI연결을 확인해주세요.")
			.setNeutralButton("확인", new DialogInterface.OnClickListener() {
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
