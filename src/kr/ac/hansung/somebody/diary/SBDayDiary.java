package kr.ac.hansung.somebody.diary;

import java.util.Calendar;

import android.net.wifi.*;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

/**
 * ���ں� ���̾ ������ �����ϴ� Ŭ���� (����History, �History ����) - �̱���
 * 
 * @author Jaewon Lee(jaewon87@naver.com)
 * @version 1.0
 */

public class SBDayDiary extends View {

	ScanResult a;

	private int year;
	private int month;
	private int day;
	private int dayOfWeek;

	private String textDay;
	private String textActCnt;

	private Paint bgDayPaint;
	private Paint bgSelectedDayPaint;
	private Paint bgActcntPaint;
	private Paint bgTodayPaint;
	private Paint textDayPaint;
	private Paint textActcntPaint;

	private int textDayTopPadding;
	private int textDayLeftPadding;
	private int textActcntTopPadding;
	private int textActcntLeftPadding;

	private Paint mPaint;

	private boolean mSelected;
	public boolean isToday = false;
	
	public SBDayDiary(Context context, android.util.AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SBDayDiary(Context context) {
		super(context);
		init();

	}

	private void init() {
		bgDayPaint = new Paint();
		bgSelectedDayPaint = new Paint();
		bgActcntPaint = new Paint();
		textDayPaint = new Paint();
		textActcntPaint = new Paint();
		bgTodayPaint = new Paint();
		bgDayPaint.setColor(Color.WHITE);
		bgActcntPaint.setColor(Color.YELLOW);
		textDayPaint.setColor(Color.WHITE);
		textDayPaint.setAntiAlias(true);
		textActcntPaint.setColor(Color.WHITE);
		textActcntPaint.setAntiAlias(true);
		bgTodayPaint.setColor(Color.GREEN);
		rect = new RectF();

		setTextDayTopPadding(0);
		setTextDayLeftPadding(0);

		setTextActcntTopPadding(0);
		setTextActcntLeftPadding(0);

		mPaint = new Paint();

		mSelected = false;
	}

	RectF rect;

	@Override
	protected void onDraw(Canvas canvas) {
		if (mSelected) {
			canvas.drawPaint(bgSelectedDayPaint);
		} else {
			if (isToday) {
				canvas.drawPaint(bgTodayPaint);
			} else {
				canvas.drawPaint(bgDayPaint);
			}
		}

		int width = this.getWidth() / 2;
		int height = this.getHeight() / 2;

		int textDaysize = (int) textDayPaint.measureText(getTextDay()) / 2;
		int textActsize = (int) textActcntPaint.measureText(getTextActCnt()) / 2;
		canvas.drawText(getTextDay(), width - textDaysize
				+ getTextDayLeftPadding(), height + getTextDayTopPadding(),
				textDayPaint);
		// ���� ǥ�� ����
		// if(getTextActCnt() != "")
		// {
		// rect.set(10, 45, 55, 65);
		// canvas.drawRoundRect(rect, 10, 30, bgActcntPaint);
		// }

		canvas.drawText(getTextActCnt(), width - textActsize
				+ getTextActcntLeftPadding(), height
				+ getTextActcntTopPadding(), textActcntPaint);

		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setColor(Color.BLACK);
		canvas.drawLine(0, this.getHeight() - 1, this.getWidth() - 1,
				this.getHeight() - 1, mPaint);
		canvas.drawLine(this.getWidth() - 1, 0, this.getWidth() - 1,
				this.getHeight() - 1, mPaint);
	}

	public int getTextDayTopPadding() {
		return this.textDayTopPadding;
	}

	public int getTextDayLeftPadding() {
		return this.textDayLeftPadding;
	}

	public void setTextDayTopPadding(int top) {
		this.textDayTopPadding = top;
	}

	public void setTextDayLeftPadding(int left) {
		this.textDayLeftPadding = left;
	}

	public int getTextActcntTopPadding() {
		return this.textActcntTopPadding;
	}

	public int getTextActcntLeftPadding() {
		return this.textActcntLeftPadding;
	}

	public void setTextActcntTopPadding(int top) {
		this.textActcntTopPadding = top;
	}

	public void setTextActcntLeftPadding(int left) {
		this.textActcntLeftPadding = left;
	}

	public void setBgTodayPaint(int color) {
		this.bgTodayPaint.setColor(color);
	}

	public void setBgDayPaint(int color) {
		this.bgDayPaint.setColor(color);
	}

	public void setBgSelectedDayPaint(int color) {
		this.bgSelectedDayPaint.setColor(color);
	}

	public void setBgActcntPaint(int color) {
		this.bgActcntPaint.setColor(color);
	}

	public void setSelected(boolean selected) {
		this.mSelected = selected;
	}

	public boolean getSelected() {
		return this.mSelected;
	}

	/**
	 * ���ڿ� ǥ�õ� �� ����
	 * 
	 * @return
	 */
	public String getTextDay() {
		return this.textDay;
	}

	/**
	 * ���ڿ� ǥ���� �� �Է�
	 * 
	 * @param string
	 */
	public void setTextDay(String string) {
		this.textDay = string;
	}

	/**
	 * �ΰ� ���� ǥ�õ� �� ����
	 * 
	 * @return
	 */
	public String getTextActCnt() {
		return this.textActCnt;
	}

	/**
	 * �ΰ� ���� ǥ���� �� �Է�
	 * 
	 * @param string
	 */
	public void setTextActCnt(String string) {
		this.textActCnt = string;
	}

	/**
	 * ���� �۾� ����
	 * 
	 * @param color
	 */
	public void setTextDayColor(int color) {
		this.textDayPaint.setColor(color);
	}

	/**
	 * ���� �۾� ũ��
	 * 
	 * @param size
	 */
	public void setTextDaySize(int size) {
		this.textDayPaint.setTextSize(size);
	}

	/**
	 * �ΰ� ���� ���� ����
	 * 
	 * @param color
	 */
	public void setTextActcntColor(int color) {
		this.textActcntPaint.setColor(color);
	}

	/**
	 * �ΰ� ���� ���� ũ��
	 * 
	 * @param size
	 */
	public void setTextActcntSize(int size) {
		this.textActcntPaint.setTextSize(size);
	}

	/**
	 * �⵵
	 * 
	 * @param _year
	 */
	public void setYear(int _year) {
		year = _year;
	}

	/**
	 * @return �⵵
	 */
	public int getYear() {
		return year;
	}

	/**
	 * ��
	 * 
	 * @param _month
	 *            0~11, Calendar.JANUARY ~ Calendar.DECEMBER
	 */
	public void setMonth(int _month) {
		month = Math.min(Calendar.DECEMBER, Math.max(Calendar.JANUARY, _month));
		month = _month;
	}

	/**
	 * @return �� 0~11, Calendar.JANUARY ~ Calendar.DECEMBER
	 */
	public int getMonth() {
		return month;
	}

	/**
	 * �� 1~31
	 */
	public void setDay(int _day) {
		day = Math.min(31, Math.max(1, _day));
		day = _day;
	}

	/**
	 * @return �� 1~31
	 */
	public int getDay() {
		return day;
	}

	/**
	 * ���� 1~7<br/>
	 * Calendar.SUNDAY ~ Calendar.SATURDAY
	 */
	public void setDayOfWeek(int _dayOfWeek) {
		dayOfWeek = Math.min(Calendar.SATURDAY,
				Math.max(Calendar.SUNDAY, _dayOfWeek));
		dayOfWeek = _dayOfWeek;
	}

	/**
	 * @return ���� 1~7, Calendar.SUNDAY ~ Calendar.SATURDAY
	 */
	public int getDayOfWeek() {
		return dayOfWeek;
	}

	/**
	 * �ش� ������ �ѱ۷� ����
	 * 
	 * @return "��", "��", "ȭ", "��", "��", "��", "��"
	 */
	public String getDayOfWeekKorean() {
		final String[] korean = { "����", "��", "��", "ȭ", "��", "��", "��", "��" };
		return korean[dayOfWeek];
	}

	/**
	 * �ش� ������ ����� ����
	 * 
	 * @return "Sun", "Mon", "Tues", "Wednes", "Thurs", "Fri", "Satur"
	 */
	public String getDayOfWeekEnglish() {
		final String[] korean = { "E", "Sun", "Mon", "Tues", "Wednes", "Thurs",
				"Fri", "Satur" };
		return korean[dayOfWeek];
	}

	/**
	 * �⺻ ���� ����
	 * 
	 * @param srcDay
	 */
	public void copyData(SBDayDiary srcDay) {
		setYear(srcDay.getYear());
		setMonth(srcDay.getMonth());
		setDay(srcDay.getDay());
		setDayOfWeek(srcDay.getDayOfWeek());
		setTextDay(srcDay.getTextDay());
		setTextActCnt(srcDay.getTextActCnt());
	}

}
