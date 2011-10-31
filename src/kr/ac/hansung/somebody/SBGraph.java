package kr.ac.hansung.somebody;

import java.util.Arrays;

import com.sunb.lib.SunGraph.GraphView;
import com.sunb.lib.SunGraph.LineInfo;
import com.sunb.lib.SunGraph.XYAxisInfo;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;

/**
 * 하루 목표량 단위로, 기간동안 일일 목표대비 달성량을 총 목표량에 비추어 보여주는 그래프 구현 클래스 외부 SunGraph Library
 * 사용 출처 : http://likeiron.blog.me/
 * 
 * @author Jaewon Lee (jaewon87@naver.com)
 * @version 1.0
 */
public class SBGraph extends Activity {

	SBUser user;

	GraphView mWidget = null; // SunGraph의 GraphView 변수 선언
	LineInfo mLine1 = null; // Line의 속성을 접근할 변수 선언

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sbgraph);

		user = SBUser.getInstance();
		mWidget = (GraphView) findViewById(R.id.graphview); // XML에서 지정한
															// GraphView와 연결

		// 그려질 데이터 생성
		float[] linedata1 = new float[user.goal_term]; // 목표 기간으로 데이터 공간 세팅
		Arrays.fill(linedata1, 1, user.goal_term - 1, (float) user.now_cal);

		// 그려질 Line의 속성 구체화
		mLine1 = new LineInfo("Today Goal", Color.RED, 0, linedata1);

		// X,Y축에 대한 정보를 구체화
		XYAxisInfo axis = new XYAxisInfo("Term", "Goal", 0,
				(int) user.total_diet_recommended_intake, user.goal_term,
				new Point(10, 10), Color.GRAY);

		// 구체화된 객체들로 그래프 초기화
		mWidget.CreateXYAxis(axis);
		mWidget.AddLine(mLine1);

		// 임시로 mLine1에 들어있는 정보를 얻어와, 데이터가 '다이어트시작일~현재'까지만 나타나게 함
		float tmplinedata[] = mLine1.getLineInnerData();
		for (int i = 0; i < tmplinedata.length; i++)
			tmplinedata[i] = (int) i;

		// line객체에 임시 array 세팅
		mLine1.setLineInnerData(tmplinedata);
	}
}
