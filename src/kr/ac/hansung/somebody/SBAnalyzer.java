package kr.ac.hansung.somebody;

/**
 * �Էµ� ��ü������ ���� �м��Ͽ� ����� �����ϴ� API���� ����. Singleton Ŭ����
 * @author Jaewon Lee(jaewon87@naver.com)
 * @version 1.0
 */
public class SBAnalyzer {
	private static SBAnalyzer instance;
	
	/**
	 * SBAnalyzer�� ������
	 */
	private SBAnalyzer(){

	}

	/**
	 * Singleton�� getInstance�κ�
	 */
	public static SBAnalyzer getInstance() {

		if(instance == null)
			instance = new SBAnalyzer();
		return instance;
	}

	
	/**
	 * BMI��ġ�� ����ϴ� �޼ҵ�
	 * @param weight ü��(kg)
	 * @param height Ű(cm)
	 * @return BMI��ġ
	 */
	public double CalBmi(int weight, int height){

		double bmi = ((double)weight / (((double)height/100) * ((double)height/100)));
		return bmi; 
	}
	
	/**
	 * BMI��ġ�� BMI Level ��� �޼ҵ�
	 * @param bmi BMI��ġ
	 * @return BMI��ġ�� �ش� Level
	 */
	public int BmiLevel(double bmi) {
		if(bmi < 18.5) // ��ü��
			return 1;
		else if(bmi >= 18.5 && bmi < 23) // ����
			return 2; 
		else if(bmi >= 23 && bmi < 25) // ��ü��
			return 3;
		else if(bmi >= 25 && bmi < 30) // ����ü��
			return 4;
		else if(bmi >= 30 && bmi < 35) // �ʵ���
			return 5;
		else if(bmi >= 35 && bmi < 40) // �ߵ��
			return 6;
		else // ����
			return 0;
	}

	/**
	 * BMI Level�� ���� �з� �޼ҵ�
	 * @param user_bmi_level
	 * @return BMI���� ���¹��ڿ�
	 */
	public String CalUserState(int user_bmi_level){
		if(user_bmi_level == 1)
			return("��ü��");
		else if(user_bmi_level == 2)
			return("����");
		else if(user_bmi_level == 3)
			return("��ü��");
		else if(user_bmi_level == 4)
			return("����ü��");
		else if(user_bmi_level == 5)
			return("�ʵ���");
		else if(user_bmi_level == 6)
			return("�ߵ��");
		else
			return("����");
	}
	
	/**
	 * Ȱ������ ���� ���� Activation Level ��� �޼ҵ�
	 * @param activationvalue Ȱ���� ��
	 * @return Ȱ���� ���� ���� �ش� Level
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
	 * ���ʴ�緮(BMR)���Ŀ� ���� ���ϴ� ��� �޼ҵ�
	 * (*Harris-Benedict equation(B.E.E)���)
	 * @param gender ����
	 * @param height Ű(cm)
	 * @param weight ������(kg)
	 * @param age ����
	 * @return ���� ���ʴ�緮 ��ġ
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
	 * Ȱ������ ���� ���� ���� Į�θ� ��� �޼ҵ�
	 * @param bmr ���� ���ʴ�緮
	 * @param activationvalue Ȱ����Level
	 * @return ���� ���� Į�θ�
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
	 * ��ǥü���� �Ǳ���� �Һ��ؾ� �Ǵ� Į�θ� ��� �޼ҵ�
	 * @param weight ���� ü��(kg)
	 * @param goal_weight ��ǥ ü��(kg)
	 * @return �Һ��ؾ� �Ǵ� �� Į�θ�(����ü��->��ǥü��)
	 */
	public double CalTotalRemoveCalorie(int weight, int goal_weight){
		double total_remove_cal = 0;
		total_remove_cal = 7.2 * ((weight - goal_weight) * 1000);
		return total_remove_cal;
	}

	/**
	 * ���̾�Ʈ�� �� ���� Į�θ� ��� �޼ҵ�(��ǥ�Ⱓ * ���� ���� Į�θ�-�Һ��ؾ� �Ǵ� �� Į�θ�)
	 * @param recommended_intake ���� ���� Į�θ�
	 * @param total_remove_cal �Һ��ؾ� �Ǵ� �� Į�θ�
	 * @param goal_term ��ǥ�Ⱓ
	 * @return ���̾�Ʈ�� �� ���� Į�θ�
	 */
	public double CalDietRecommendedIntake(double recommended_intake, double total_remove_cal, int goal_term){
		double total_diet_recommended_intake = 0;
		total_diet_recommended_intake = (recommended_intake  * goal_term) - total_remove_cal;
		return total_diet_recommended_intake;
	}
	
	/**
	 * ���� ���̾�Ʈ�� ���� Į�θ��� ���� Į�θ��� ���ؼ� ���� ���¸� ��Ÿ���� �޼ҵ�
	 * @param now_cal ���� Į�θ�
	 * @param day_diet_rcmd_cal ���� ���̾�Ʈ�� ���� Į�θ� 
	 * @return ���� ����Level
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
