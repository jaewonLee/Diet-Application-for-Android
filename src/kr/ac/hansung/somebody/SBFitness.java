package kr.ac.hansung.somebody;

import kr.ac.hansung.somebody.motion.SBMotion;
import kr.ac.hansung.somebody.walking.SBWalking;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

/**
 * 운동종류를 선택하는 메뉴선택 클래스
 * @author Jaewon Lee(jaewon87@naver.com)
 * @version 1.0
 */
public class SBFitness extends Activity implements OnClickListener{

	Intent intent;
	
	ImageButton btn_fitness_walking; // 걷기운동 버튼
	ImageButton btn_fitness_motion;  // 모션게임 버튼
	ImageButton btn_fitness_input;   // 직접입력 버튼
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sbselfitness);
		
		btn_fitness_walking = (ImageButton)findViewById(R.id.start_walking);
		btn_fitness_motion = (ImageButton)findViewById(R.id.motion_menu);
		btn_fitness_input = (ImageButton)findViewById(R.id.insert_ex_exercise);
		
		btn_fitness_walking.setOnClickListener(this);
		btn_fitness_motion.setOnClickListener(this);
		btn_fitness_input.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v==btn_fitness_walking){
			intent = new Intent(SBFitness.this, SBWalking.class);
			startActivity(intent);
		}
		else if(v==btn_fitness_motion) {
			intent = new Intent(SBFitness.this, SBMotion.class);
			startActivity(intent);
		}
		else if(v==btn_fitness_input){
			intent = new Intent(SBFitness.this, SBInputFitness.class);
			startActivity(intent);
		}
	}
}
