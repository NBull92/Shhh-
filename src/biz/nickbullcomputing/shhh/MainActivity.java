package biz.nickbullcomputing.shhh;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity 
{
	static final int ABOUT = Menu.FIRST;
	boolean signedIn = false;
	boolean isPlaying;
	boolean submitted;
	int counter = 0;
	
	Button btnStartR;
	Button btnUserID;
	
	StringBuffer stringBuffer = new StringBuffer();
	String localHighScore;
	
	TextView txtCurrentValue;
	TextView txtHighscoreValue;
	
	private MediaRecorder recorder;
	private String OUTPUT_FILE;	
	Timer updateTimer;	
	
	double[] array = new double[10];//4];
	int i = 0;
	double Average = 0;
	String formatted;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		String buttonText;
		String signInText = "Sign In!";
		
		btnStartR = (Button) findViewById(R.id.btnStartR);
		btnUserID = (Button) findViewById(R.id.btnUserID);
		
		txtCurrentValue = (TextView)  findViewById(R.id.txtCurrentValue);				
		txtHighscoreValue = (TextView)  findViewById(R.id.txtHighscoreValue);
		
		updateTimer = new Timer();
		OUTPUT_FILE = Environment.getExternalStorageDirectory()+"/audiorecorder.3gpp";
		
		//reading in username
		try
		{
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(openFileInput("Username.txt")));
			String inputString;		
			while ((inputString = inputReader.readLine()) != null)
			{
				stringBuffer.append(inputString);
		    }
			btnUserID.setText(stringBuffer.toString());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		buttonText = btnUserID.getText().toString();
		
		if(buttonText.equals(signInText))
	    {
			signedIn = false;
	    }
		else
        {
        	signedIn = true;
        }
		
		//checking a creating a temporary local highscore file
				File dir = getFilesDir();
				File file = new File(dir, "tempLocal.txt");				
				try 
				{
					if(file.exists())
					{
						//do nothing
						//file.delete();
					}
					if(!file.exists() )
					{
						String temp = "\r\n";
						FileOutputStream fos;			
						fos = openFileOutput("tempLocal.txt", Context.MODE_PRIVATE);	
						
						//players Scores
						fos.write("5".getBytes());
						fos.write(temp.getBytes());		
						fos.write("4".getBytes());
						fos.write(temp.getBytes());	
						fos.write("3".getBytes());
						fos.write(temp.getBytes());	
						fos.write("2".getBytes());
						fos.write(temp.getBytes());	
						fos.write("1".getBytes());
						fos.write(temp.getBytes());
						
						//Players Username
						fos.write("Guest".getBytes());
						fos.write(temp.getBytes());		
						fos.write("Guest".getBytes());
						fos.write(temp.getBytes());	
						fos.write("Guest".getBytes());
						fos.write(temp.getBytes());	
						fos.write("Guest".getBytes());
						fos.write(temp.getBytes());	
						fos.write("Guest".getBytes());
						
						//libraries
						fos.close();
					}		
				} 
				catch (IOException e) 
				{
					
					e.printStackTrace();
				}
	}

	protected void onStart()
	{
		super.onStart();
		updateTimer = new Timer();
		updateTimer.schedule(new UpdateTask(new Handler(), this), 0, 100);//<---- equals half a second. this equals a second ---> 1000);
	}
	
	protected void onStop()
	{
		super.onStop();
		updateTimer.cancel();
		updateTimer.purge();
	}
	
	public void update()
	{
		getLocalHighscore();
		
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
				btnUserID.setText(stringBuffer.toString());				
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			btnUserID.setText("Sign In!".toString());
		}
		if(isPlaying)
		{
			btnStartR.setText("Recording...");
			btnStartR.setTextSize(25.0f);
			counter++;
			txtCurrentValue.setText(localHighScore);
						
			if(i<10)
			{
				array[i] = getAmplitude();
				i++;
			}
			else
			{
				DecimalFormat format = new DecimalFormat("##.##");
				
				for(i=0;i<10;i++)
				{
					Average += array[i];
					System.out.println("array["+i+"]"+" "+array[i]);
				}				
				
				Average = Average/10; 
				formatted = format.format(Average);
				txtCurrentValue.setText(formatted);
				i = 0;	
				saveScore();
			}

			if(counter == 55)
			{
				isPlaying = false;
				stopRecording();				
				counter = 0;
				txtCurrentValue.setText(localHighScore);
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() 
				{
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				        switch (which){
				        case DialogInterface.BUTTON_POSITIVE:
				            //Yes button clicked
				        	//if yes submit score to high scores 
				        	SubmitLocalRecording();
				            break;

				        case DialogInterface.BUTTON_NEGATIVE:
				            //No button clicked
				        	//if no close message box and do nothing
				            break;
				        }
				    }
				};

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage("Do you wish to Submit your score?").setPositiveButton("Yes", dialogClickListener)
				    .setNegativeButton("No", dialogClickListener).show();
				
			}
		}
		else
		{
			btnStartR.setText("GO!!");
			btnStartR.setTextSize(50.0f);
			
		}
		if(submitted)
		{
			//System.out.println("updateTempLocal");
			updateTempLocal();
			//System.out.println("DONE");
		}
	}
	
	private class UpdateTask extends TimerTask
	{
		Handler handler;
		MainActivity ref;
		
		public UpdateTask(Handler handler, MainActivity ref)
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
	
	public void goButton(View view)
	{
		if(isPlaying == false)
		{
			
			Toast.makeText(this, "Recording", Toast.LENGTH_SHORT).show();
			txtCurrentValue.setText(R.string.blank_score);
			isPlaying = true;
			
			try
			{
				File dir = getFilesDir();
				File file = new File(dir, "PlayerScore.txt");
				if(file.exists())
				{
					file.delete();
				}
				if(!file.exists() )
				{
					String temp = "0";
					FileOutputStream fos = openFileOutput("PlayerScore.txt", Context.MODE_PRIVATE);
					fos.write(temp.getBytes());		
					fos.close();
				}
				beginRecording();				
			}
			catch(Exception e)
			{
				e.printStackTrace();
				Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
				System.out.println("error");
			}
		}
		else
		{
			Toast.makeText(this, "Already Recording", Toast.LENGTH_SHORT).show();
		}		
	}	
	
	private void stopRecording() 
	{
		if(recorder != null)
		{
			recorder.stop();			
			recorder.release();
		}
	}
	
	private void beginRecording() throws Exception 
	{
		ditchMediaRecorder();
		File outFile = new File(OUTPUT_FILE);
		
		if(outFile.exists())
		{
			outFile.delete();
			System.out.println("file deleted");
		}

		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);		
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);	
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setOutputFile(OUTPUT_FILE);
		if (recorder != null) {
	        try {
	            recorder.prepare();

	        } catch (IllegalStateException e) 
	        {
	        	System.out.println("IllegalStateException");    
	        } catch (IOException e) 
	        {
	        	System.out.println(e);
	        	e.printStackTrace();
	        }
		}
		recorder.start();	
		recorder.getMaxAmplitude();
	}

	private void ditchMediaRecorder() 
	{
		if(recorder != null)
		{
			recorder.release();
		}
	}
	
	public double getAmplitude()
	{
		if (recorder != null)
		{
			return (recorder.getMaxAmplitude()/100.0);//2700.0);
		}
		else
		{			
			return 0;
		}
	}
	
	public void getLocalHighscore()
	{
		String localHighscore = "0";
		String[] notNeededData = new String[10]; 
		try
		{	
			File dir = getFilesDir();
			File tempFile = new File(dir,"tempLocal.txt");
			Scanner readFile = new Scanner( tempFile );
			
			while (readFile.hasNextLine())
			{
				for(int j = 0;j<10;j++)
				{
					if(j < 1)
					{
						localHighscore = readFile.nextLine();
					}
					if(j >= 1)
					{
						notNeededData[j] = readFile.nextLine();
					}
					
				}
			}
			
			txtHighscoreValue.setText(localHighscore);
		}
		catch(Exception e)
		{
			e.printStackTrace();			
		}	
				
	}

	public void updateTempLocal()
	{
		
		String[] score = new String[10]; 
		String[] usernames = new String[10];
			
		
		try
		{
			File dir = getFilesDir();
			File tempFile = new File(dir,"localHighScore.txt");
			Scanner readFile = new Scanner( tempFile );
			//System.out.println("File Created!");
			FileOutputStream fos = openFileOutput("tempLocal.txt", Context.MODE_PRIVATE);
			
			//while(!readDone)
			//{
				//System.out.println("!Done");
				while (readFile.hasNextLine())
				{
					//System.out.println("second While Loop");
					for(int j = 0;j<10;j++)
					{
						if(j<5)
						{
							//System.out.println("For Loop!" + j);
							score[j] = readFile.nextLine();
							//System.out.println(score[j]);
						}
						
						if(j>=5)
						{
							//System.out.println("For Loop!" + j);
							usernames[j-5] = readFile.nextLine();
							//System.out.println(usernames[j-5]);							
						}
					}
					//System.out.println("Exit For Loop");
					
				}	
				//System.out.println("Exit second while");
			//}
			//System.out.println("DONE");	
			
			for(int i = 0;i<10;i++)
			{
				if(i<5)
				{
					//System.out.println("For Loop!" + i + "this is i");
					//System.out.println(score[i]);
					
					fos.write(score[i].getBytes());
					//System.out.println("printed");
					fos.write("\r\n".getBytes());
				}
				if(i>=5)
				{
					//System.out.println("For Loop!" + i);
					//System.out.println(usernames[i-5]);
					
					fos.write(usernames[i-5].getBytes());
					//System.out.println("printed");
					if(i!=9)
					{
						fos.write("\r\n".getBytes());
					}
				}
			}
			//System.out.println("Exit For Loop");
			
			fos.close(); 
			//System.out.println("Close File");
		}
		catch(Exception e)
		{
			e.printStackTrace();			
		}	
		submitted = false; System.out.println("submitted = false");
		for(int j = 0;j<10;j++)
		{
			if(j < 5)
			{				
				score[j] = null;
				//System.out.println("For Loop!scores = null" + j);
			}
			if(j>=5)
			{
				usernames[j] = null;
				//System.out.println("For Loop!usernames = null" + j);
			}
		}
	}
	
	
	public void SubmitLocalRecording()
	{//----THIS IS LOCAL SAVE----//
		
		//System.out.println("Submit Started");
		
		//when pressed the current score will be taken and added 
		//to the thatUsersHighScore list if valid too
		//Scores lower will be moved down and if move down to rank six they will be deleted.
	
		String[] score = new String[10]; 
		String[] usernames = new String[10];
		String tmp;
		String tmp2;
		String[] newScores = new String[10]; 
		String[] newUsernames = new String[10];		
		
		boolean readScoresDone = false;
		
		try
		{	
			//System.out.println("In the Try");
			File dir = getFilesDir();
			File tempFile = new File(dir,"tempLocal.txt");
			Scanner readFile = new Scanner( tempFile );
			//System.out.println("File Created!");
			FileOutputStream fos = openFileOutput("localHighScore.txt", Context.MODE_PRIVATE);
			
			while(!readScoresDone)
			{
				//System.out.println("!Done");
				while (readFile.hasNextLine())
				{
					//System.out.println("second While Loop");
					for(int j = 0;j<10;j++)
					{
						if(j<5)
						{
							//System.out.println("For Loop!" + j);
							score[j] = readFile.nextLine();
						}
						if(j>=5)
						{
							//System.out.println("For Loop!" + j + "boom");
							usernames[j] = readFile.nextLine();
							//System.out.println(usernames[j] + j);
						}
						
					}
					//System.out.println("Exit For Loop");
					readScoresDone = true;
				}	
				//System.out.println("Exit second while");
				readScoresDone = true;
			}
			//System.out.println("DONE");			
			
			//search for sign in! as name then set to guest
			String btnUserName;
			

				//System.out.println("Test");
				if (btnUserID.getText().toString() == "Sign In!")
				{
					btnUserName = "Guest";
					System.out.println("Test1");
				}
				else
				{
					btnUserName = btnUserID.getText().toString();
				}
			
			
			//if local < score0 tmp = score 0 else newscore0 = score0 && tmp = local
			if(Double.parseDouble(localHighScore) >= Double.parseDouble(score[0]))
			{
				tmp = score[0];
				newScores[0] = localHighScore;
				newUsernames[5] = btnUserName;
				//System.out.println("username = buttonText" + newUsernames[5] );
				//System.out.println("tmp = score[0]");
			}
			else
			{
				newScores[0] = score[0];
				//System.out.println("newScore = score[0]" + score[0]);
				newUsernames[5] = usernames[5];
				tmp = localHighScore;
			}
			//if tmp < score 1 tmp2 = score1 else newscore1 = score1 && tmp2 = tmp
			if(Double.parseDouble(tmp) >= Double.parseDouble(score[1]))
			{
				tmp2 = score[1];
				newScores[1] = tmp;
				if(tmp == localHighScore)
				{
					newUsernames[6] = btnUserName;
					//System.out.println("HELLO" + newUsernames[6] );
					Toast.makeText(this, "You've made a new high score", Toast.LENGTH_SHORT).show();
					
				}
				else
				{
					newUsernames[6] = usernames[5];
				}
			}
			else
			{
				newScores[1] = score[1];
				tmp2 = tmp;
				if(score[1] == localHighScore)
				{
					newUsernames[6] = btnUserName;
					Toast.makeText(this, "You've made a new high score", Toast.LENGTH_SHORT).show();
				}
				else
				{
					newUsernames[6] = usernames[6];
				}
				
			}
			//if tmp2 < score2 tmp = score2 else newscore2 = score2 && tmp = tmp2
			if(Double.parseDouble(tmp2) >= Double.parseDouble(score[2]))
			{
				tmp = score[2];
				newScores[2] = tmp2;
				if(tmp2 == localHighScore)
				{
					newUsernames[7] = btnUserName;
					Toast.makeText(this, "You've made a new high score", Toast.LENGTH_SHORT).show();
				}
				else
				{
					newUsernames[7] = usernames[6];
				}
				
			}
			else
			{
				newScores[2] = score[2];
				tmp = tmp2;
				if(score[2] == localHighScore)
				{
					newUsernames[7] = btnUserName;
					Toast.makeText(this, "You've made a new high score", Toast.LENGTH_SHORT).show();
				}
				else
				{
					newUsernames[7] = usernames[7];
				}
				
			}
			//if tmp < score3 tmp2 = score3 else newscore3 = score3 && tmp2 = tmp
			if(Double.parseDouble(tmp) >= Double.parseDouble(score[3]))
			{
				tmp2 = score[3];
				newScores[3] = tmp;
				if(tmp == localHighScore)
				{
					newUsernames[8] = btnUserName;
					Toast.makeText(this, "You've made a new high score", Toast.LENGTH_SHORT).show();
				}
				else
				{
					newUsernames[8] = usernames[7];
				}
			}
			else
			{
				newScores[3] = score[3];
				tmp2 = tmp;
				if(score[3] == localHighScore)
				{
					newUsernames[8] = btnUserName;
					Toast.makeText(this, "You've made a new high score", Toast.LENGTH_SHORT).show();
				}
				else
				{
					newUsernames[8] = usernames[8];
				}
			}
			//if tmp2 < score4 tmp = score4 else newscore4 = score4 && tmp = tmp2
			if(Double.parseDouble(tmp2) >= Double.parseDouble(score[4]))
			{
				tmp = score[4];
				newScores[4] = tmp2;
				if(tmp2 == localHighScore)
				{
					newUsernames[9] = btnUserName;
					Toast.makeText(this, "You've made a new high score", Toast.LENGTH_SHORT).show();
				}
				else
				{
					newUsernames[9] = usernames[8];
				}
			}
			else
			{
				newScores[4] = score[4];
				tmp = tmp2;
				if(score[4] == localHighScore)
				{
					newUsernames[9] = btnUserName;
					Toast.makeText(this, "You've made a new high score", Toast.LENGTH_SHORT).show();
				}
				else
				{
					newUsernames[9] = usernames[9];
				}
			}
			//System.out.println("newScores[0]" + newScores[0]);
			for(int j = 0;j<10;j++)
			{
				if(j < 5)
				{
					//System.out.println("For Loop! new scores" + j);
					//System.out.println(newScores[j]);
					//score[j] = readFile.nextLine();
					
					fos.write(newScores[j].getBytes());
					//System.out.println("printed");
					fos.write("\r\n".getBytes());
				}
				if(j>=5)
				{
					//System.out.println("For Loop!" + j);
					//System.out.println(newUsernames[j]);
					
					fos.write(newUsernames[j].getBytes());
					//System.out.println("printed");
					if(j!=9)
					{
						fos.write("\r\n".getBytes());
					}
				}
			}
			//System.out.println("Exit For Loop");
				//System.out.println("Printed localHighScores");					
			fos.close(); 
				//System.out.println("Close File");
				
		}
		catch(Exception e)
		{
			e.printStackTrace();			
		}	
		submitted = true; 
		//System.out.println("submitted = true");
		
		Toast.makeText(this, "Submission Successfull", Toast.LENGTH_SHORT).show();
		
		for(int j = 0;j<10;j++)
		{
			if(j < 5)
			{				
				score[j] = null;
				//System.out.println("For Loop!scores = null" + j);
				newScores[j] = null;
				//System.out.println("For Loop!newScores = null" + j);
			}
			if(j>=5)
			{
				usernames[j] = null;
				//System.out.println("For Loop!usernames = null" + j);
				newUsernames[j] = null;
				//System.out.println("For Loop!newUsernames = null" + j);
			}
		}
	}
	
	public void saveScore()
	{
		String sPlayerScore;
		String currentScoreSaved;
		sPlayerScore = formatted;
		try
		{		
			
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(openFileInput("PlayerScore.txt")));
			String inputString;StringBuffer stringBuffer = new StringBuffer();
			
			while ((inputString = inputReader.readLine()) != null)
			{
		        stringBuffer.append(inputString + "\n");
		    }
			currentScoreSaved = stringBuffer.toString();
			
			//TrueFalse.setText(String.valueOf(currentScoreSaved));
			

			if(Double.parseDouble(sPlayerScore) > Double.parseDouble(currentScoreSaved))
			{
				FileOutputStream fos = openFileOutput("PlayerScore.txt", Context.MODE_PRIVATE);
				fos.write(sPlayerScore.getBytes());		
				fos.close();
				//System.out.println("sPlayerScore = " + sPlayerScore);
				localHighScore = sPlayerScore;
				System.out.println("localHighScore = " + localHighScore);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}	
	}
	
	public void signInButton(View view)
    {
    	if(signedIn == false)
		{
    		// If not signed in the launch signin page	
    		Intent intent = new Intent(MainActivity.this, SignIn.class); 
        	startActivity(intent);
		}
		else
		{	
			//sign out
			//show a yes no message box
			
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
	
	public void menuButton(View view)
	{
		// send user name info back to main page 
	 	Intent intent = new Intent(MainActivity.this, MainMenu.class);
		startActivity(intent);
		finish();
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
    			Intent intent = new Intent(MainActivity.this, About.class);
    			startActivity(intent);
    			return true;
    		}	    		    	
    	}
    	return false;    
    }
}
