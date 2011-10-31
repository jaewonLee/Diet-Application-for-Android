package kr.ac.hansung.somebody;

/**
 * 운동종류를 직접 입력 할 수 있는 클래스
 * 
 * @author MinSik Kim(minsik1218@naver.com)
 * @version 1.0
 */
public class SBExercise {

	String exercise_name; // 운동명
	int exercise_id; // 운동종류 별 ID
	int exercise_time; // 운동시간
	double calorie; // 해당 운동의 칼로리 소모량

	SBExercise() {
		exercise_name = "";
		calorie = 0;
	}
	
}
