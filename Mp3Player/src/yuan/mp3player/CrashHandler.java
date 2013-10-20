package yuan.mp3player;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Properties;
import java.util.TreeSet;

import yuan.notification.TrayNotification;
import yuan.utils.FileUtils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

public class CrashHandler implements UncaughtExceptionHandler{
	
	private static final String VERSION_NAME = "versionName";    
	private static final String VERSION_CODE = "versionCode";    
	private static final String STACK_TRACE = "STACK_TRACE"; 
	private static final String CRASH_REPORTER_EXTENSION = ".error";

	private static CrashHandler crashHandler = null;
	private Context context = null;
	/**系统默认的UncaughtException处理类 */    
	private Thread.UncaughtExceptionHandler defaultHandler = null;  
	private Properties deviceCrashInfo = new Properties();
	
	private CrashHandler() {		
		//单例模型
	}
	
	public static CrashHandler getInstance() {		
		return crashHandler == null ? new CrashHandler() : crashHandler;		
	}

	public void init(Context context) {
		this.context = context;
		this.defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}
	
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && defaultHandler != null) { 
			defaultHandler.uncaughtException(thread, ex);    
		} else {
			android.os.Process.killProcess(android.os.Process.myPid());    
			System.exit(10);  
		} 
	}
	
	private boolean handleException(Throwable ex) {
		TrayNotification.removeNotification(context);
		if(ex == null) {  
			return false;		
		}  
		final String msg = ex.getLocalizedMessage(); 
		Toast.makeText(context, "程序出错啦:" + msg, Toast.LENGTH_LONG).show();    		
		collectCrashDeviceInfo(); 
		saveCrashInfoToFile(ex);
		return false;		
	}
	
	private void collectCrashDeviceInfo() {
		try {    
			PackageManager pm = context.getPackageManager();    
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES); 
							           
			if (pi != null) {    
			   deviceCrashInfo.put(VERSION_NAME, pi.versionName == null ? "not set" : pi.versionName);					  		  				     			                   
			   deviceCrashInfo.put(VERSION_CODE, String.valueOf(pi.versionCode));    
			}    
		} catch (NameNotFoundException e) {    
			Log.e("CrashHandler", "Error while collect package info", e);    
		} 
		
		//使用反射来收集设备信息.在Build类中包含各种设备信息,
		//例如: 系统版本号,设备生产商 等帮助调试程序的有用信息
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {    
				field.setAccessible(true);    
				deviceCrashInfo.put(field.getName(), (String)field.get(null).toString());    				  
				Log.d("CrashHandler", field.getName() + " : " + field.get(null));    			    
				} 
			catch (Exception e) {    
				Log.e("CrashHandler", "Error while collect crash info", e);    
			} 
		}
	}
	
	private void saveCrashInfoToFile(Throwable ex) {
        Writer info = new StringWriter();  
        PrintWriter printWriter = new PrintWriter(info);  
        ex.printStackTrace(printWriter);  
  
        Throwable cause = ex.getCause();  
        while (cause != null) {  
            cause.printStackTrace(printWriter);  
            cause = cause.getCause();  
        }  
  
        String result = info.toString();  
        printWriter.close();  
        deviceCrashInfo.put(STACK_TRACE, result);
        try {  
            long timestamp = System.currentTimeMillis();  
            String fileName = "crash-" + getCurrentTime() + CRASH_REPORTER_EXTENSION;  
            FileOutputStream trace = context.openFileOutput(fileName,Context.MODE_PRIVATE);  
            FileOutputStream fos = new FileOutputStream(FileUtils.CRASHDIR + fileName);        
            deviceCrashInfo.store(trace, null); 
            deviceCrashInfo.store(fos, null);  
            trace.flush();
            trace.close();
            fos.flush();
            fos.close();        
        } catch (Exception e) {  
            Log.e("CrashHandler", "an error occured while writing report file...", e);  
        }  
	}
	
    /** 
     * 在程序启动时候, 可以调用该函数来发送以前没有发送的报告 
     */  
    public void sendPreviousReportsToServer() {  
        sendCrashReportsToServer();  
    }  
  
    /** 
     * 把错误报告发送给服务器,包含新产生的和以前没发送的. 
     *  
     * @param ctx 
     */  
    private void sendCrashReportsToServer() {  
        String[] crFilesInSystem = getCrashFilesInSystem();
        String[] crFilesInSD = getCrashFileInSD();
        if (crFilesInSystem != null && crFilesInSystem.length > 0 && crFilesInSD != null && crFilesInSD.length > 0 ) {  
            TreeSet<String> sortedFiles = new TreeSet<String>();  
            sortedFiles.addAll(Arrays.asList(crFilesInSystem));  
            sortedFiles.addAll(Arrays.asList(crFilesInSD));
            for (String fileName : sortedFiles) {  
                File crashFile = new File(FileUtils.CRASHDIR, fileName);                             
                postReport(crashFile);  
                if(crashFile.length() == 0) 
                	crashFile.delete();// 删除已发送的报告  
            }  
        } 
    }  
  
    /** 
     * 获取错误报告文件名 
     * @param ctx 
     * @return 
     */  
    private String[] getCrashFilesInSystem() {  
        File filesDir = context.getFilesDir();        
        FilenameFilter filter = new FilenameFilter() {  
            public boolean accept(File dir, String name) {  
                return name.endsWith(CRASH_REPORTER_EXTENSION);  
            }  
        };     
        return filesDir.list(filter);  
    } 
    
    private String[] getCrashFileInSD() {
    	File filesDir = new File(FileUtils.CRASHDIR);
    	return filesDir.list();
    }
    
    private void postReport(File file) {  
        // TODO 使用HTTP Post 发送错误报告到服务器  
    } 
    
	private String getCurrentTime() {
		StringBuilder str = new StringBuilder();
	    Calendar ca = Calendar.getInstance();
	    int year = ca.get(Calendar.YEAR);//获取年份
	    int month=ca.get(Calendar.MONTH);//获取月份
	    int day=ca.get(Calendar.DATE);//获取日
	    int minute=ca.get(Calendar.MINUTE);//分
	    int hour=ca.get(Calendar.HOUR_OF_DAY);//小时
	    int second=ca.get(Calendar.SECOND);//秒	     
	    
	    str.append(year).append(".").append(month).append(".").append(day).
	    	append("_").append(hour).append("-").append(minute).append("-").append(second);	    
		return str.toString();		
	}
}
