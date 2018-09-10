package biz.nickbullcomputing.shhh;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class HighScores extends Activity 
{
	static final int ABOUT = Menu.FIRST;

	private ListView lstScores;
	private ListView lstNames;
	private ArrayAdapter<String> listScoreAdapter ;
	private ArrayAdapter<String> listNameAdapter ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_high_scores);
		
		String[] score = new String[5]; 
		String[] usernames = new String[5];
				
		
		//checking a creating a temporary local highscore file
				File checkDir = getFilesDir();
				File tempFile = new File(checkDir, "tempLocal.txt");				
				try 
				{
					if(tempFile.exists())
					{
						//do nothing
						//tempFile.delete();
					}
					if(!tempFile.exists() )
					{						
						String temp = "\r\n";
						FileOutputStream fos;			
						fos = openFileOutput("tempLocal.txt", Context.MODE_PRIVATE);	
						
						//players Scores
						fos.write("175.83".getBytes());
						fos.write(temp.getBytes());		
						fos.write("164.5".getBytes());
						fos.write(temp.getBytes());	
						fos.write("123.89".getBytes());
						fos.write(temp.getBytes());	
						fos.write("82.6".getBytes());
						fos.write(temp.getBytes());	
						fos.write("30.67".getBytes());
						fos.write(temp.getBytes());
						
						//Players Username
						fos.write("Niko".getBytes());
						fos.write(temp.getBytes());		
						fos.write("Mario".getBytes());
						fos.write(temp.getBytes());	
						fos.write("Chief".getBytes());
						fos.write(temp.getBytes());	
						fos.write("Tommy".getBytes());
						fos.write(temp.getBytes());	
						fos.write("Claude".getBytes());
						
						//libraries
						fos.close();
					}		
				} 
				catch (IOException e) 
				{

					System.out.println("test fail" + e);
					e.printStackTrace();
				}
		
	    // Find the ListView resource. 
		lstScores = (ListView) findViewById( R.id.lstScores );
		lstNames = (ListView) findViewById( R.id.lstNames );
	    try
		{	
			//System.out.println("In the Try");
			File dir = getFilesDir();
			//File tempFile = new File(dir,"tempLocal.txt");
			Scanner readFile = new Scanner( tempFile );			

			System.out.println("test tempfile");
				while (readFile.hasNextLine())
				{
					System.out.println("second While Loop");
					for(int j = 0;j<10;j++)
					{
						System.out.println("For Loop!" + j);
						if(j<5)
						{
							score[j] = readFile.nextLine();
						}
						if(j>=5)
						{
							usernames[j-5] = readFile.nextLine();
						}
					}
					System.out.println("Exit For Loop");					
				}	
				System.out.println("Exit second while");	
		
	    ArrayList<String> highScoresList = new ArrayList<String>();
	    highScoresList.addAll( Arrays.asList(score) );	    
	    	System.out.println("made highScoresList");
	    
	    ArrayList<String> namesScoresList = new ArrayList<String>();
	    namesScoresList.addAll( Arrays.asList(usernames) );
	    	System.out.println("made namesScoresList");
	    // Create ArrayAdapter using the planet list.
	    listScoreAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, highScoresList);
	    	System.out.println("made listScoreAdapter");
	    listNameAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, namesScoresList);
	    	System.out.println("made listNameAdapter");
	    	
	    // Set the ArrayAdapter as the ListView's adapter.
	    lstScores.setAdapter( listScoreAdapter );
    	System.out.println("set adapter lstScores");

	    lstNames.setAdapter( listNameAdapter );
    	System.out.println("set adapter lstScores");

		}
		catch(Exception e)
		{
			e.printStackTrace();	

			System.out.println(e);
		}	
	}

	public void menuButton(View view)
    {
    	// Do something in response to button
    	Intent intent = new Intent(HighScores.this, MainMenu.class);
     	startActivity(intent);
     	finish();
    }
	public void playButton(View view)
    {
    	// Do something in response to button
    	Intent intent = new Intent(HighScores.this, MainActivity.class);
     	startActivity(intent);
     	finish();
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
	    			Intent intent = new Intent(HighScores.this, About.class);
	    			startActivity(intent);
	    			return true;
	    		}	    		    	
	    	}
	    	return false;    
	    }

}
