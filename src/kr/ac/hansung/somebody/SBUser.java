package kr.ac.hansung.somebody;

import android.text.format.DateFormat;
import twitter4j.Twitter;
import twitter4j.auth.AccessToken;

/**
 * 사용자 정보를 담당하는 Class Singleton class
 * @author Jaewon Lee(jaewon87@naver.com),An yongsoo(yongsu9@gmail.com)
 * @version 1.0
 */
public class SBUser {
	private static SBUser instance;
	private SBUser(){
	}
	public static SBUser getInstance() {
		if(instance == null){
			instance = new SBUser();
		}
		return instance;
	}


	/** 사용자의 ID*/ public String Id = "";				
	public String nickname;             /** 사용자의 Nickname*/
	public int gender;			        /** 사용자 성별*/
	public int height;					/** 사용자 키*/
	public int weight;					/** 사용자 체중*/
	public int age;						/** 사용자 나이*/
	public int exp;                     /** 사용자 경험치*/
	public int activationvalue;			/** 사용자 활동 지수*/
	public int activationvalue_level;   /** 사용자 활동 지수에 따른 Level*/
	public int user_bmi_level;		    /** 사용자 BMI지수별 단계*/

	public int user_level;				/** 사용자 level(초기화 필요)*/
	public int user_point;				/** 사용자 포인트 (초기화 필요)*/
	public int user_ranking;			/** 사용자 랭킹	(초기화 필요)*/

	public int goal_weight;				/** 목표체중*/
	public int goal_term;				/** 목표기간*/
	public int now_status;              /** 현재 상태*/

	public double bmi;					/** 사용자 BMI지수*/
	public double bmr;				    /** 사용자 기초대사량*/
	public double recommended_intake;   /** 사용자 권장섭취량*/
	public double total_remove_cal;				  /** 빼야되는 총 칼로리*/
	public double diet_recommended_intake;		  /** 다이어트용 하루 권장 섭취량*/
	public double total_diet_recommended_intake;  /** 다이어트용 총 권장 섭취량*/

	public double now_cal;      /** 현재 칼로리		(초기화 필요)*/
	public double used_cal; 	/** 소비한 칼로리량	(초기화 필요)*/
	public double eat_cal;  	/** 섭취한 칼로리량	(초기화 필요)*/

	public int coupon_flag = 0; /** 쿠폰 flag (데모용 삭제예정)*/
	public Twitter twitter;     /** twitter 객체*/
	public String twitID;		/** 사용자의 twitter id*/
	public String twitPW;		/** 사용자의 twitter password*/
	public AccessToken ac;      /** twitter AccessToken*/

	public int join_room_id = -1;
	String date = "";

	/**
	 * 운동결과를 서버로 전송하는 함수
	 * @param user 유저 Class
	 * @param exercise_id 해당 운동 ID
	 * @param exercise_used_cal 운동으로 소모한 칼로리
	 * @param exercise_time 운동을 한 시간
	 * @param point 운동으로 발생한 포인트
	 */
	public boolean put_exercise_results(SBUser user, int exercise_id, double exercise_used_cal, int exercise_time, int point, int exp){
		long _timeMillis = System.currentTimeMillis();
		String temp = DateFormat.format("yyyy-MM-dd hh:mm:ss", _timeMillis).toString();
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("user_id").append("=").append(user.Id).append("&"); 
		buffer.append("date").append("=").append(temp).append("&");
		buffer.append("exercise_id").append("=").append(exercise_id).append("&");
		buffer.append("exercise_used_cal").append("=").append(exercise_used_cal).append("&");
		buffer.append("exercise_time").append("=").append(exercise_time).append("&");
		buffer.append("exp").append("=").append(exp).append("&");
		buffer.append("point").append("=").append(point);
		
		try{
			ConnectServer.HttpPostData("insert_exercise_results.php", buffer);
		}catch(Exception e){
			return false;
		}
		if(user.join_room_id != -1){
			StringBuffer buffer2 = new StringBuffer();
			buffer2.append("user_id").append("=").append(user.Id).append("&"); 
			buffer2.append("room_id").append("=").append(user.join_room_id).append("&");
			buffer2.append("exercise_id").append("=").append(exercise_id).append("&");
			buffer2.append("exercise_used_cal").append("=").append(exercise_used_cal);
			
			try{
				ConnectServer.HttpPostData("update_exercise_to_group.php", buffer);
			}catch(Exception e){
				return false;
			}
		}
		return true;
	}
}
