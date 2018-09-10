package biz.nickbullcomputing.shhh;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainMenu extends Activity 
{
	static final int ABOUT = Menu.FIRST;
	boolean signedIn = false;
	Button btnSignIn;
	Timer updateTimer;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);        
                
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        
        String buttonText;
        String signInText = "Sign In!";
		try
		{
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(openFileInput("Username.txt")));
			String inputString;StringBuffer stringBuffer = new StringBuffer();
			while ((inputString = inputReader.readLine()) != null)
			{
		        stringBuffer.append(inputString);
		    }
			btnSignIn.setText(stringBuffer.toString());
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		buttonText = btnSignIn.getText().toString();
		
		if(buttonText.equals(signInText))
	    {
			signedIn = false;
	    }
		else
        {
        	signedIn = true;
        }
		
    }
    protected void onStart()
	{
		super.onStart();
		updateTimer = new Timer();
		updateTimer.schedule(new UpdateTask(new Handler(), this), 0, 1000);// this equals a second ---> 1000);
	}
	protected void onStop()
	{
		super.onStop();
		updateTimer.cancel();
		updateTimer.purge();
	}
	public void update()
	{
		if(signedIn)
		{
			try
			{
				BufferedReader inputReader = new BufferedReader(new InputStreamReader(openFileInput("Username.txt")));
				String inputString;StringBuffer stringBuffer = new StringBuffer();
				while ((inputString = inputReader.readLine()) != null)
				{
			        stringBuffer.append(inputString);
			       
			    }
				btnSignIn.setText(stringBuffer.toString());
				
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			btnSignIn.setText("Sign In!".toString());
		}
	}
	private class UpdateTask extends TimerTask
	{
		Handler handler;
		MainMenu ref;
		
		public UpdateTask(Handler handler, MainMenu ref)
		{
			super();
			this.handler = handler;
			this.ref = ref;
		}
		@Override
		public void run()
		{
			handler.post(new Runnable()
			{
				public void run()
				{
					ref.update();
				}
			});
		}
	}

	public void playButton(View view)
    {
    	Intent intent = new Intent(MainMenu.this, MainActivity.class);
    	startActivity(intent);
    	finish();
    }
    
    public void highScoresButton(View view)
    {
    	Intent intent = new Intent(MainMenu.this, HighScores.class);
     	startActivity(intent);
     	finish();
    }  
    
	public void signInButton(View view)
    {
		if(signedIn == false)
		{
    		// If not signed in the launch signin page	
    		Intent intent = new Intent(MainMenu.this, SignIn.class); 
        	startActivity(intent);
		}
		else
		{			
			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() 
			{
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			        switch (which){
			        case DialogInterface.BUTTON_POSITIVE:
			            //Yes button clicked
			        	//if yes sign them out - Set contents to Sign In! 
			        	try 
			        	{
							FileOutputStream fos = openFileOutput("Username.txt", Context.MODE_PRIVATE);
							fos.write("Sign In!".getBytes());
							fos.close(); 
							signedIn = false;
							
						} 
			        	catch (FileNotFoundException e) 
						{
							e.printStackTrace();
						} 
			        	catch (IOException e) 
						{
			        		e.printStackTrace();
						}
			            break;

			        case DialogInterface.BUTTON_NEGATIVE:
			            //No button clicked
			        	//if no close message box and do nothing
			            break;
			        }
			    }
			};

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Do you wish to sign out?").setPositiveButton("Yes", dialogClickListener)
			    .setNegativeButton("No", dialogClickListener).show();
			
			if(signedIn == false)
			{
				Toast.makeText(this, "Successfully Signed Out", Toast.LENGTH_SHORT).show();
			}
		}   
    	
    	
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		//create and add new menu items
    	MenuItem itemAdd = menu.add(0,ABOUT, Menu.NONE, R.string.title_activity_about);
		
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_about, menu);		
		return true;		
	}
	 public boolean onOptionsItemSelected (MenuItem item)
    {
    	super.onOptionsItemSelected(item);  
    	
    	switch (item.getItemId())
    	{
    		case(ABOUT):
    		{
    			Intent intent = new Intent(MainMenu.this, About.class);
    			startActivity(intent);
    			return true;
    		}	    		    	
    	}
    	return false;    
    }

}
