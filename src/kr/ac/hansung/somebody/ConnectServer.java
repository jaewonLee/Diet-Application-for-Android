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
 * 서버와 통신시 필요한 함수를 모아놓은 Class
 * 
 * @author An yongsoo(yongsu9@gmail.com)
 * @version 1.0
 */
public class ConnectServer {
	public final static String server_address = "http://113.198.80.206/";

	/**
	 * 인자로 지정된 php 페이지에서 Json 형태의 String을 받아오는 함수
	 * 
	 * @param temp
	 *            php 페이지의 주소를 지정
	 * @return 지정된 php페이지에서 받아온 Json 형태의 String
	 */
	public static String DownloadHtml(String temp) {
		String addr = server_address + temp;
		StringBuilder jsonHtml = new StringBuilder();
		try {
			// 연결 url 설정
			URL url = new URL(addr);
			// 커넥션 객체 생성
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			// 연결되었으면.
			if (conn != null) {
				conn.setConnectTimeout(10000);
				conn.setUseCaches(false);
				// 연결되었음 코드가 리턴되면.
				if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
					BufferedReader br = new BufferedReader(
							new InputStreamReader(conn.getInputStream(),
									"EUC-KR"));
					for (;;) {
						// 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
						String line = br.readLine();
						if (line == null)
							break;
						// 저장된 텍스트 라인을 jsonHtml에 붙여넣음
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
	 * 서버에서 처리될 Data를 보낸 후 결과로 생성된 Json형태의 String을 받아 오는 함수
	 * 
	 * @param temp
	 *            php 페이지의 주소를 지정
	 * @param send_data
	 *            서버에서 처리될 데이터
	 * @return 지정된 php페이지에서 받아온 Json 형태의 String
	 */
	public static String HttpPostData(String temp, StringBuffer send_data) {
		String addr = server_address + temp;
		String temp_data = null;
		try {
			// --------------------------
			// URL 설정하고 접속하기
			// --------------------------
			URL url = new URL(addr); // URL 설정
			HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // 접속
			// --------------------------
			// 전송 모드 설정 - 기본적인 설정이다
			// --------------------------
			conn.setDefaultUseCaches(false);
			conn.setDoInput(true); // 서버에서 읽기 모드 지정
			conn.setDoOutput(true); // 서버로 쓰기 모드 지정
			conn.setRequestMethod("POST"); // 전송 방식은 POST

			// 서버에게 웹에서 <Form>으로 값이 넘어온 것과 같은 방식으로 처리하라는 걸 알려준다
			conn.setRequestProperty("content-type",
					"application/x-www-form-urlencoded");
			// --------------------------
			// 서버로 값 전송
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
			// 서버로 부터 값 받기
			// --------------------------
			InputStreamReader tmp = new InputStreamReader(
					conn.getInputStream(), "EUC-KR");
			BufferedReader reader = new BufferedReader(tmp);
			StringBuilder builder = new StringBuilder();
			String str;
			while ((str = reader.readLine()) != null) { // 서버에서 라인단위로 보내줄 것이므로
														// 라인단위로 읽는다
				builder.append(str); // View에 표시하기 위해 라인 구분자 추가
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
	 * 인자로 지정된 Json형태의 String을 Json객채로 변환후 SBUser형의 객체에 적용
	 * 
	 * @param user
	 *            Json객체의 Data를 적용시킬 SBUser형 객체
	 * @param json
	 *            Json객체를 생성하기 위한 Json형태의 String
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
	 * 인자로 지정된 Json형태의 String을 Json객채로 변환후 SBGroupRoom형의 객체에 적용
	 * 
	 * @param room
	 *            Json객체의 Data를 적용시킬 SBGroupRoom형 객체
	 * @param json
	 *            Json객체를 생성하기 위한 Json형태의 String
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
	 * 인자로 지정된 Json형태의 String을 Json객채로 변환후 Food형의 객체에 적용
	 * 
	 * @param food
	 *            Json객체의 Data를 적용시킬 Food형 객체
	 * @param json
	 *            Json객체를 생성하기 위한 Json형태의 String
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