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
 * Ʈ���� ���� �� �޼��� ������ Class
 * 
 * @author An yongsoo(yongsu9@gmail.com)
 * @version 1.0
 */
public class SBTwitter extends Activity implements OnClickListener {
	SBUser user;
	EditText insert_text;
	Button send_message;

	public static String consumerKey = "sQwuME1MubcdUahkZC1VOA"; // "�߱޹���consumerKey ";
	public static String consumerSecret = "AEk9RMcQOj2ud2PWZdRbBwxgSGYHZyeABCVCYTiPM"; // "�߱޹���consumeSecret";

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
				Toast.makeText(SBTwitter.this, "������ ����.", Toast.LENGTH_SHORT)
						.show();
				this.setResult(RESULT_OK);
				finish();
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				Toast.makeText(SBTwitter.this, "������ ����.", Toast.LENGTH_SHORT)
						.show();
				e.printStackTrace();
			}
		} else
			Toast.makeText(SBTwitter.this, "������ �޴����� Ʈ����ID�� ����� ���ּ���.",
					Toast.LENGTH_LONG).show();
	}

	/**
	 * Ʈ���Ϳ� xAuth �����ϴ� �Լ�
	 */
	public void TwitterxAuth() {
		if (user.twitter == null) {
			ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.setOAuthConsumerKey(consumerKey);
			builder.setOAuthConsumerSecret(consumerSecret);

			user.twitter = new TwitterFactory(builder.build()).getInstance();
		} else {
			// Ʈ���� ��ü�� ������ �ȵǾ��ִ� ���.
			// user.twitter.setOAuthConsumer(consumerKey, consumerSecret);
		}

		// Ʈ���� ��ü ���� �κ�...
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
	 * Ʈ���� �α��� �˻� �Լ�
	 * 
	 * @return �α��� �ƴٸ� true �ƴϸ� false
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
