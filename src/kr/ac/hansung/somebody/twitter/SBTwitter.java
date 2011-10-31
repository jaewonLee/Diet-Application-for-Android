package kr.ac.hansung.somebody.twitter;

import kr.ac.hansung.somebody.R;
import kr.ac.hansung.somebody.SBUser;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 트위터 연동 및 메세지 보내기 Class
 * 
 * @author An yongsoo(yongsu9@gmail.com)
 * @version 1.0
 */
public class SBTwitter extends Activity implements OnClickListener {
	SBUser user;
	EditText insert_text;
	Button send_message;

	public static String consumerKey = "sQwuME1MubcdUahkZC1VOA"; // "발급받은consumerKey ";
	public static String consumerSecret = "AEk9RMcQOj2ud2PWZdRbBwxgSGYHZyeABCVCYTiPM"; // "발급받은consumeSecret";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sbtwitter);

		Intent intent = getIntent();
		String message = intent.getStringExtra("message");

		insert_text = (EditText) findViewById(R.id.inserttext);
		insert_text.setText(message);
		send_message = (Button) findViewById(R.id.sendmessage);
		send_message.setOnClickListener(this);
		user = SBUser.getInstance();
		if (user.ac == null)
			TwitterxAuth();
	}

	@Override
	public void onClick(View arg0) {
		if (this.isLogin()) {
			try {
				user.twitter.updateStatus(insert_text.getText().toString());
				Toast.makeText(SBTwitter.this, "포스팅 성공.", Toast.LENGTH_SHORT)
						.show();
				this.setResult(RESULT_OK);
				finish();
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				Toast.makeText(SBTwitter.this, "포스팅 실패.", Toast.LENGTH_SHORT)
						.show();
				e.printStackTrace();
			}
		} else
			Toast.makeText(SBTwitter.this, "내정보 메뉴에서 트위터ID를 등록을 해주세요.",
					Toast.LENGTH_LONG).show();
	}

	/**
	 * 트위터에 xAuth 인증하는 함수
	 */
	public void TwitterxAuth() {
		if (user.twitter == null) {
			ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.setOAuthConsumerKey(consumerKey);
			builder.setOAuthConsumerSecret(consumerSecret);

			user.twitter = new TwitterFactory(builder.build()).getInstance();
		} else {
			// 트위터 객체가 인증이 안되어있는 경우.
			// user.twitter.setOAuthConsumer(consumerKey, consumerSecret);
		}

		// 트위터 객체 인증 부분...
		try {
			if( (user.twitID != null) || (user.twitPW != null)){
				user.ac = user.twitter
				.getOAuthAccessToken(user.twitID, user.twitPW);				
			}
		} catch (TwitterException te) {
			te.printStackTrace();

			user.twitter = null;
		}
	}

	/**
	 * 트위터 로그인 검사 함수
	 * 
	 * @return 로그인 됐다면 true 아니면 false
	 */
	public boolean isLogin() {
		if (user.twitter == null) {
			return false;
		}
		if (user.twitter.getAuthorization().isEnabled()) {
			return true;
		} else {
			return false;
		}
	}
}
