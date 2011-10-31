package kr.ac.hansung.somebody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import kr.ac.hansung.somebody.group.SBGroupRoom;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * ������ ��Ž� �ʿ��� �Լ��� ��Ƴ��� Class
 * 
 * @author An yongsoo(yongsu9@gmail.com)
 * @version 1.0
 */
public class ConnectServer {
	public final static String server_address = "http://113.198.80.206/";

	/**
	 * ���ڷ� ������ php ���������� Json ������ String�� �޾ƿ��� �Լ�
	 * 
	 * @param temp
	 *            php �������� �ּҸ� ����
	 * @return ������ php���������� �޾ƿ� Json ������ String
	 */
	public static String DownloadHtml(String temp) {
		String addr = server_address + temp;
		StringBuilder jsonHtml = new StringBuilder();
		try {
			// ���� url ����
			URL url = new URL(addr);
			// Ŀ�ؼ� ��ü ����
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			// ����Ǿ�����.
			if (conn != null) {
				conn.setConnectTimeout(10000);
				conn.setUseCaches(false);
				// ����Ǿ��� �ڵ尡 ���ϵǸ�.
				if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
					BufferedReader br = new BufferedReader(
							new InputStreamReader(conn.getInputStream(),
									"EUC-KR"));
					for (;;) {
						// ���� �������� �ؽ�Ʈ�� ���δ����� �о� ����.
						String line = br.readLine();
						if (line == null)
							break;
						// ����� �ؽ�Ʈ ������ jsonHtml�� �ٿ�����
						else if (!line.equals(""))
							jsonHtml.append(line + "\n");
					}
					br.close();
				}
				conn.disconnect();
			}
		} catch (Exception e) {
			return "error";
		}
		return jsonHtml.toString();
	}

	/**
	 * �������� ó���� Data�� ���� �� ����� ������ Json������ String�� �޾� ���� �Լ�
	 * 
	 * @param temp
	 *            php �������� �ּҸ� ����
	 * @param send_data
	 *            �������� ó���� ������
	 * @return ������ php���������� �޾ƿ� Json ������ String
	 */
	public static String HttpPostData(String temp, StringBuffer send_data) {
		String addr = server_address + temp;
		String temp_data = null;
		try {
			// --------------------------
			// URL �����ϰ� �����ϱ�
			// --------------------------
			URL url = new URL(addr); // URL ����
			HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // ����
			// --------------------------
			// ���� ��� ���� - �⺻���� �����̴�
			// --------------------------
			conn.setDefaultUseCaches(false);
			conn.setDoInput(true); // �������� �б� ��� ����
			conn.setDoOutput(true); // ������ ���� ��� ����
			conn.setRequestMethod("POST"); // ���� ����� POST

			// �������� ������ <Form>���� ���� �Ѿ�� �Ͱ� ���� ������� ó���϶�� �� �˷��ش�
			conn.setRequestProperty("content-type",
					"application/x-www-form-urlencoded");
			// --------------------------
			// ������ �� ����
			// --------------------------
			StringBuffer buffer = send_data;

			OutputStreamWriter outStream = new OutputStreamWriter(
					conn.getOutputStream(), "EUC-KR");
			PrintWriter writer = new PrintWriter(outStream);
			writer.write(buffer.toString());
			writer.flush();

			writer.close();
			outStream.close();
			// --------------------------
			// ������ ���� �� �ޱ�
			// --------------------------
			InputStreamReader tmp = new InputStreamReader(
					conn.getInputStream(), "EUC-KR");
			BufferedReader reader = new BufferedReader(tmp);
			StringBuilder builder = new StringBuilder();
			String str;
			while ((str = reader.readLine()) != null) { // �������� ���δ����� ������ ���̹Ƿ�
														// ���δ����� �д´�
				builder.append(str); // View�� ǥ���ϱ� ���� ���� ������ �߰�
			}

			temp_data = builder.toString();
			reader.close();
			tmp.close();
			conn.disconnect();

		} catch (MalformedURLException e) {

		} catch (IOException e) {

		}
		return temp_data;
	}

	/**
	 * ���ڷ� ������ Json������ String�� Json��ä�� ��ȯ�� SBUser���� ��ü�� ����
	 * 
	 * @param user
	 *            Json��ü�� Data�� �����ų SBUser�� ��ü
	 * @param json
	 *            Json��ü�� �����ϱ� ���� Json������ String
	 */
	public static void JsonBuilder(SBUser user, String json) {
		try {
			JSONArray ja = new JSONArray(json);

			JSONObject order = ja.getJSONObject(0);

			user.nickname = order.getString("nick");
			user.weight = order.getInt("weight");
			user.age = order.getInt("age");
			user.gender = order.getInt("gender");
			user.height = order.getInt("height");
			user.user_level = order.getInt("level");
			user.exp = order.getInt("exp");
			user.goal_weight = order.getInt("goal_weight");
			user.goal_term = order.getInt("goal_term");
			user.bmr = order.getDouble("bmr");
			user.bmi = order.getDouble("bmi");
			user.activationvalue = order.getInt("act");
			user.recommended_intake = order.getDouble("rcmd_intake");
			user.total_diet_recommended_intake = order
					.getDouble("diet_rcmd_intake");
			user.total_remove_cal = order.getDouble("total_remove_cal");
			// user.diet_rcmd_intake_day = order.getInt("diet_rcmd_intake_day");
			user.user_point = order.getInt("point");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ���ڷ� ������ Json������ String�� Json��ä�� ��ȯ�� SBGroupRoom���� ��ü�� ����
	 * 
	 * @param room
	 *            Json��ü�� Data�� �����ų SBGroupRoom�� ��ü
	 * @param json
	 *            Json��ü�� �����ϱ� ���� Json������ String
	 */
	public static void JsonBuilder(SBGroupRoom room, String json) {
		try {
			JSONArray ja = new JSONArray(json);

			JSONObject order = ja.getJSONObject(0);

			room.room_id = order.getInt("room_id");
			room.room_name = order.getString("room_name");
			room.room_maker = order.getString("room_maker");
			room.limit_level = order.getInt("limit_level");
			room.persons = order.getInt("persons");
			room.limit_remain_term = order.getInt("limit_remain_term");
			room.walking_id = order.getInt("walking_id");
			room.walking_goal = order.getDouble("walking_goal");
			room.motion_id = order.getInt("motion_id");
			room.motion_goal = order.getDouble("motion_goal");
			room.suc_flag = order.getInt("suc_flag");
			room.maker_nick = order.getString("maker_nick");
			room.mission_name = order.getString("mission_name");
			room.join_person = order.getInt("join_person");
			room.m_limit_term = order.getInt("m_limit_term");
			room.m_walking_goal = order.getDouble("m_walking_goal");
			room.m_motion_goal = order.getDouble("m_motion_goal");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ���ڷ� ������ Json������ String�� Json��ä�� ��ȯ�� Food���� ��ü�� ����
	 * 
	 * @param food
	 *            Json��ü�� Data�� �����ų Food�� ��ü
	 * @param json
	 *            Json��ü�� �����ϱ� ���� Json������ String
	 */
	public static void JsonBuilder(Food food, String json) {
		try {
			JSONArray ja = new JSONArray(json);

			JSONObject order = ja.getJSONObject(0);

			food.food_name = order.getString("food_name");
			food.gram = order.getInt("gram");
			food.calorie = order.getDouble("calorie");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}