package kr.ac.hansung.somebody;

/**
 * 입력된 신체정보로 부터 분석하여 결과를 도출하는 API들의 집합. Singleton 클래스
 * @author Jaewon Lee(jaewon87@naver.com)
 * @version 1.0
 */
public class SBAnalyzer {
	private static SBAnalyzer instance;
	
	/**
	 * SBAnalyzer의 생성자
	 */
	private SBAnalyzer(){

	}

	/**
	 * Singleton의 getInstance부분
	 */
	public static SBAnalyzer getInstance() {

		if(instance == null)
			instance = new SBAnalyzer();
		return instance;
	}

	
	/**
	 * BMI수치를 계산하는 메소드
	 * @param weight 체중(kg)
	 * @param height 키(cm)
	 * @return BMI수치
	 */
	public double CalBmi(int weight, int height){

		double bmi = ((double)weight / (((double)height/100) * ((double)height/100)));
		return bmi; 
	}
	
	/**
	 * BMI수치별 BMI Level 계산 메소드
	 * @param bmi BMI수치
	 * @return BMI수치별 해당 Level
	 */
	public int BmiLevel(double bmi) {
		if(bmi < 18.5) // 저체중
			return 1;
		else if(bmi >= 18.5 && bmi < 23) // 정상
			return 2; 
		else if(bmi >= 23 && bmi < 25) // 과체중
			return 3;
		else if(bmi >= 25 && bmi < 30) // 위험체중
			return 4;
		else if(bmi >= 30 && bmi < 35) // 초도비만
			return 5;
		else if(bmi >= 35 && bmi < 40) // 중등도비만
			return 6;
		else // 고도비만
			return 0;
	}

	/**
	 * BMI Level별 상태 분류 메소드
	 * @param user_bmi_level
	 * @return BMI따른 상태문자열
	 */
	public String CalUserState(int user_bmi_level){
		if(user_bmi_level == 1)
			return("저체중");
		else if(user_bmi_level == 2)
			return("정상");
		else if(user_bmi_level == 3)
			return("과체중");
		else if(user_bmi_level == 4)
			return("위험체중");
		else if(user_bmi_level == 5)
			return("초도비만");
		else if(user_bmi_level == 6)
			return("중등도비만");
		else
			return("고도비만");
	}
	
	/**
	 * 활동량에 따른 계산된 Activation Level 계산 메소드
	 * @param activationvalue 활동량 값
	 * @return 활동량 값에 따른 해당 Level
	 */
	public int CalActivation(int activationvalue){
		if(activationvalue >= 0 && activationvalue<= 5)
			return 1;
		else if(activationvalue > 5 && activationvalue <= 15)
			return 2;
		else if(activationvalue > 15 && activationvalue <= 25)
			return 3;
		else if(activationvalue > 25 && activationvalue <= 35)
			return 4;
		else if(activationvalue > 35 && activationvalue <= 40)
			return 5;
		else
			return 0;
	}

	/**
	 * 기초대사량(BMR)공식에 의해 구하는 계산 메소드
	 * (*Harris-Benedict equation(B.E.E)방법)
	 * @param gender 성별
	 * @param height 키(cm)
	 * @param weight 몸무게(kg)
	 * @param age 나이
	 * @return 일일 기초대사량 수치
	 */
	public double CalBmr(int gender, int height, int weight, int age){
		double bmr = 0;

		if(gender == 0)
			bmr = 66.47 + (13.75 * weight) + (5 * height) - (6.76 * age);
		else if(gender == 1)
			bmr = 655.1 + (9.56*weight) + (1.85*height) - (4.68*age);
		else
			bmr = 0;

		return bmr;	
	}

	/**
	 * 활동량에 따른 일일 권장 칼로리 계산 메소드
	 * @param bmr 일일 기초대사량
	 * @param activationvalue 활동량Level
	 * @return 일일 권장 칼로리
	 */
	public double CalRecommendedIntake(double bmr, int activationvalue){
		double recommended_intake = 0;

		if(activationvalue == 1)
			recommended_intake = bmr * 1.2;
		else if(activationvalue == 2)
			recommended_intake = bmr * 1.375;
		else if(activationvalue == 3)
			recommended_intake = bmr * 1.55;
		else if(activationvalue == 4)
			recommended_intake = bmr * 1.725;
		else if(activationvalue == 5)
			recommended_intake = bmr * 1.9;
		else
			recommended_intake = 0;

		return recommended_intake;
	}

	/**
	 * 목표체중이 되기까지 소비해야 되는 칼로리 계산 메소드
	 * @param weight 현재 체중(kg)
	 * @param goal_weight 목표 체중(kg)
	 * @return 소비해야 되는 총 칼로리(현재체중->목표체중)
	 */
	public double CalTotalRemoveCalorie(int weight, int goal_weight){
		double total_remove_cal = 0;
		total_remove_cal = 7.2 * ((weight - goal_weight) * 1000);
		return total_remove_cal;
	}

	/**
	 * 다이어트용 총 권장 칼로리 계산 메소드(목표기간 * 일일 권장 칼로리-소비해야 되는 총 칼로리)
	 * @param recommended_intake 일일 권장 칼로리
	 * @param total_remove_cal 소비해야 되는 총 칼로리
	 * @param goal_term 목표기간
	 * @return 다이어트용 총 권장 칼로리
	 */
	public double CalDietRecommendedIntake(double recommended_intake, double total_remove_cal, int goal_term){
		double total_diet_recommended_intake = 0;
		total_diet_recommended_intake = (recommended_intake  * goal_term) - total_remove_cal;
		return total_diet_recommended_intake;
	}
	
	/**
	 * 일일 다이어트용 권장 칼로리와 현재 칼로리를 비교해서 현재 상태를 나타내는 메소드
	 * @param now_cal 현재 칼로리
	 * @param day_diet_rcmd_cal 일일 다이어트용 권장 칼로리 
	 * @return 현재 상태Level
	 */
	public int NowStatus(double now_cal, int day_diet_rcmd_cal) {
		double term_status = (double)day_diet_rcmd_cal - now_cal;
		if( term_status < -(day_diet_rcmd_cal))
		{
			return 0;
		}
		else if(term_status > -day_diet_rcmd_cal && term_status < -(day_diet_rcmd_cal/2) ) {
			return 1;
		}
		else if(term_status > -(day_diet_rcmd_cal/2) && term_status < -(day_diet_rcmd_cal/8)) {
			return 2;
		}
		else if(term_status > -(day_diet_rcmd_cal/8) && term_status < (day_diet_rcmd_cal/8)) {
			return 3;
		}
		else if(term_status > (day_diet_rcmd_cal/8) && term_status < (day_diet_rcmd_cal/2)) {
			return 4;
		}
		else {
			return 5;
		}
	}
}
