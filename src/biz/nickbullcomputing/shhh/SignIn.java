package biz.nickbullcomputing.shhh;

import java.io.FileOutputStream;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class SignIn extends Activity 
{
	static final int ABOUT = Menu.FIRST;

	public final static String USERNAME_MESSAGE = "com.example.libnoise.MESSAGE";
	
	EditText txtUsername;
	public static boolean signedIn = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in);
		txtUsername = (EditText) findViewById(R.id.txtUsername);
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
    	MenuItem itemAdd = menu.add(0,ABOUT, Menu.NONE, R.string.title_activity_about);
		return true;		
	}
	 public boolean onOptionsItemSelected (MenuItem item)
	    {
	    	super.onOptionsItemSelected(item);  
	    	
	    	switch (item.getItemId())
	    	{
	    		case(ABOUT):
	    		{
	    			Intent intent = new Intent(SignIn.this, About.class);
	    			startActivity(intent);
	    			return true;
	    		}	    		    	
	    	}
	    	return false;    
	    }
	
	public void hideSoftKeyboard(View view)
	{
	    if(getCurrentFocus()!=null && getCurrentFocus() instanceof EditText)
	    {
	        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	        imm.hideSoftInputFromWindow(txtUsername.getWindowToken(), 0);
	    }
	}
	public void signedInButton(View view)
    {
		String sUsername;
		sUsername = txtUsername.getText().toString();

		if (sUsername.matches("SignIn")) 
		{
		    Toast.makeText(this, "You did not enter a username", Toast.LENGTH_SHORT).show();
		    return;
		}
		else
		{
			//send text from the textbox to the textview in the playzone 
			//and button in main activity
			
			try
			{
				FileOutputStream fos = openFileOutput("Username.txt", Context.MODE_PRIVATE);
				fos.write(sUsername.getBytes());
				fos.close();
				signedIn = true;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
			Intent intent = new Intent(this, MainActivity.class);
	    	startActivity(intent);
	    	finish();
		}

	

}
