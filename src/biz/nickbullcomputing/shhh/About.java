package biz.nickbullcomputing.shhh;

import android.os.Bundle;
import android.app.Activity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.widget.TextView;

public class About extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		TextView txtPortfolio = (TextView) findViewById(R.id.txtPortfolio);
		txtPortfolio.setClickable(true);
		txtPortfolio.setMovementMethod(LinkMovementMethod.getInstance());
		String portfolio = "Developer:- <a href='http://www.nickbull-computing.biz/index.html'>Nicholas Bull<a/>";
		txtPortfolio.setText(Html.fromHtml(portfolio));
		
		TextView txtEmail = (TextView) findViewById(R.id.txtEmail);
		txtEmail.setClickable(true);
		txtEmail.setMovementMethod(LinkMovementMethod.getInstance());
		String email = "<a href='mailto:nickbull@nickbull-computing.biz'>Send Feedback<a/>";
		txtEmail.setText(Html.fromHtml(email));
	}	

}
