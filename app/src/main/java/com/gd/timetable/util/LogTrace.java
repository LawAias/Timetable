package com.gd.timetable.util;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 日志类&&调试工具类
 * 
 * @author sjyBing
 * @version [版本号, 2011-12-26]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class LogTrace
{
    /**
     * StbService的日志标志
     */
    public static final String TAG = "GD";
    
    /**
     * 日志开关
     */
    private static boolean logSwitch = true;
    
    /**
     * Log日志方法 <功能详细描述>
     * 
     * @param className
     *            类名
     * @param methodName
     *            方法名
     * @param msg
     *            日志信息
     */
    public static void d(String className, String methodName, String msg)
    {
        // StringBuffer sb = new StringBuffer();
        StringBuffer sb = new StringBuffer();
        if (logSwitch)
        {
            sb.append(className).append("->").append(methodName).append("->").append(msg);
            Log.d(TAG, sb.toString());
        }
    }
    
    /**
     * Log日志方法 <功能详细描述>
     * 
     * @param className
     *            类名
     * @param methodName
     *            方法名
     * @param msg
     *            日志信息
     */
    public static void v(String className, String methodName, String msg)
    {
        StringBuffer sb = new StringBuffer();
        if (logSwitch)
        {
            sb.append(className).append("->").append(methodName).append("->").append(msg);
            Log.v(TAG, sb.toString());
        }
    }
    
    /**
     * Log日志方法 <功能详细描述>
     * 
     * @param className
     *            类名
     * @param methodName
     *            方法名
     * @param msg
     *            日志信息
     */
    public static void w(String className, String methodName, String msg)
    {
        StringBuffer sb = new StringBuffer();
        if (logSwitch)
        {
            sb.append(className).append("->").append(methodName).append("->").append(msg);
            Log.w(TAG, sb.toString());
        }
    }
    
    /**
     * Log日志方法 <功能详细描述>
     * 
     * @param className
     *            类名
     * @param methodName
     *            方法名
     * @param msg
     *            日志信息
     */
    public static void i(String className, String methodName, String msg)
    {
        StringBuffer sb = new StringBuffer();
        if (logSwitch)
        {
            sb.append(className).append("->").append(methodName).append("->").append(msg);
            Log.i(TAG, sb.toString());
        }
    }
    
    /**
     * Log日志方法 <功能详细描述>
     * 
     * @param className
     *            类名
     * @param methodName
     *            方法名
     * @param msg
     *            日志信息
     */
    public static void e(String className, String methodName, String msg)
    {
        StringBuffer sb = new StringBuffer();
        if (logSwitch)
        {
            sb.append(className).append("->").append(methodName).append("->").append(msg);
            Log.e(TAG, sb.toString());
        }
    }
    
	/* 获取耗费内存 */
	public static long getUsedMemory () {
		long total = Runtime.getRuntime().totalMemory();
		long free = Runtime.getRuntime().freeMemory();
		return total - free;
	}
	
	  public static void currTime(String className, String methodName)
	    {
		  String time = String.valueOf(System.currentTimeMillis() / 1000);
	      LogTrace.e(TAG, "time", time);
		  
		  
		  	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
					Locale.getDefault());
			Date date = new Date();
			String msg=  df.format(date);
	        StringBuffer sb = new StringBuffer();
	        if (logSwitch)
	        {
	            sb.append(className).append("->").append(methodName).append("->currtime->").append(msg);
	            Log.d(TAG, sb.toString());
	        }
	    }
	  
}
