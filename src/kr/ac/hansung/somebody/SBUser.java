package kr.ac.hansung.somebody;

import android.text.format.DateFormat;
import twitter4j.Twitter;
import twitter4j.auth.AccessToken;

/**
 * ����� ������ ����ϴ� Class Singleton class
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


	/** ������� ID*/ public String Id = "";				
	public String nickname;             /** ������� Nickname*/
	public int gender;			        /** ����� ����*/
	public int height;					/** ����� Ű*/
	public int weight;					/** ����� ü��*/
	public int age;						/** ����� ����*/
	public int exp;                     /** ����� ����ġ*/
	public int activationvalue;			/** ����� Ȱ�� ����*/
	public int activationvalue_level;   /** ����� Ȱ�� ������ ���� Level*/
	public int user_bmi_level;		    /** ����� BMI������ �ܰ�*/

	public int user_level;				/** ����� level(�ʱ�ȭ �ʿ�)*/
	public int user_point;				/** ����� ����Ʈ (�ʱ�ȭ �ʿ�)*/
	public int user_ranking;			/** ����� ��ŷ	(�ʱ�ȭ �ʿ�)*/

	public int goal_weight;				/** ��ǥü��*/
	public int goal_term;				/** ��ǥ�Ⱓ*/
	public int now_status;              /** ���� ����*/

	public double bmi;					/** ����� BMI����*/
	public double bmr;				    /** ����� ���ʴ�緮*/
	public double recommended_intake;   /** ����� ���強�뷮*/
	public double total_remove_cal;				  /** ���ߵǴ� �� Į�θ�*/
	public double diet_recommended_intake;		  /** ���̾�Ʈ�� �Ϸ� ���� ���뷮*/
	public double total_diet_recommended_intake;  /** ���̾�Ʈ�� �� ���� ���뷮*/

	public double now_cal;      /** ���� Į�θ�		(�ʱ�ȭ �ʿ�)*/
	public double used_cal; 	/** �Һ��� Į�θ���	(�ʱ�ȭ �ʿ�)*/
	public double eat_cal;  	/** ������ Į�θ���	(�ʱ�ȭ �ʿ�)*/

	public int coupon_flag = 0; /** ���� flag (����� ��������)*/
	public Twitter twitter;     /** twitter ��ü*/
	public String twitID;		/** ������� twitter id*/
	public String twitPW;		/** ������� twitter password*/
	public AccessToken ac;      /** twitter AccessToken*/

	public int join_room_id = -1;
	String date = "";

	/**
	 * ������ ������ �����ϴ� �Լ�
	 * @param user ���� Class
	 * @param exercise_id �ش� � ID
	 * @param exercise_used_cal ����� �Ҹ��� Į�θ�
	 * @param exercise_time ��� �� �ð�
	 * @param point ����� �߻��� ����Ʈ
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
