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
 * �Ϸ� ��ǥ�� ������, �Ⱓ���� ���� ��ǥ��� �޼����� �� ��ǥ���� ���߾� �����ִ� �׷��� ���� Ŭ���� �ܺ� SunGraph Library
 * ��� ��ó : http://likeiron.blog.me/
 * 
 * @author Jaewon Lee (jaewon87@naver.com)
 * @version 1.0
 */
public class SBGraph extends Activity {

	SBUser user;

	GraphView mWidget = null; // SunGraph�� GraphView ���� ����
	LineInfo mLine1 = null; // Line�� �Ӽ��� ������ ���� ����

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sbgraph);

		user = SBUser.getInstance();
		mWidget = (GraphView) findViewById(R.id.graphview); // XML���� ������
															// GraphView�� ����

		// �׷��� ������ ����
		float[] linedata1 = new float[user.goal_term]; // ��ǥ �Ⱓ���� ������ ���� ����
		Arrays.fill(linedata1, 1, user.goal_term - 1, (float) user.now_cal);

		// �׷��� Line�� �Ӽ� ��üȭ
		mLine1 = new LineInfo("Today Goal", Color.RED, 0, linedata1);

		// X,Y�࿡ ���� ������ ��üȭ
		XYAxisInfo axis = new XYAxisInfo("Term", "Goal", 0,
				(int) user.total_diet_recommended_intake, user.goal_term,
				new Point(10, 10), Color.GRAY);

		// ��üȭ�� ��ü��� �׷��� �ʱ�ȭ
		mWidget.CreateXYAxis(axis);
		mWidget.AddLine(mLine1);

		// �ӽ÷� mLine1�� ����ִ� ������ ����, �����Ͱ� '���̾�Ʈ������~����'������ ��Ÿ���� ��
		float tmplinedata[] = mLine1.getLineInnerData();
		for (int i = 0; i < tmplinedata.length; i++)
			tmplinedata[i] = (int) i;

		// line��ü�� �ӽ� array ����
		mLine1.setLineInnerData(tmplinedata);
	}
}
