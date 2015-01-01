package org.tomaswoj.basilisk;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.util.Log;


public class CPULoad 
{
    static long total = 0;
    static long idle = 0;


    static float usage = 0;

public CPULoad( )
{
    readUsage( );
}

public static float getUsage( )
{
    readUsage( );
    return usage;
}

private static void readUsage( )
{
    try
    {
        BufferedReader reader = new BufferedReader( new InputStreamReader( new FileInputStream( "/proc/stat" ) ), 1000 );
        String load = reader.readLine();
        reader.close();     

        String[] toks = load.split(" ");
        //Log.v("droiddos", "proc/stat line:"+load);

        long currTotal = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4]);
        long currIdle = Long.parseLong(toks[5]);
        //Log.v("droiddos", "proc/currtotal:"+currTotal);
        //Log.v("droiddos", "proc/total:"+total);
        //Log.v("droiddos", "proc/curridle:"+currIdle);        
        //Log.v("droiddos", "proc/idle:"+idle);

        usage = (currTotal - total) * 100.0f / (currTotal - total + currIdle - idle);
        total = currTotal;
        idle = currIdle;
        
        //Log.v("droiddos", "readcpu:"+usage);
    }
    catch( IOException ex )
    {
        ex.printStackTrace();           
    }
}
}
