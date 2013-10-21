package com.music.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 进行网络读取时，需要先判断手机是否联网当相关处理
 * @author Administrator
 */
public class Network {

	/**
	 * 判断网络的类型
	 * @param context
	 * @return 返回网络的类型名
	 */
	@SuppressLint("DefaultLocale") public static String netType(Context context) {      
        NetworkInfo info = getNetworkInfo(context);
        String typeName = info.getTypeName();
        if (typeName.equalsIgnoreCase("wifi")) {
        	System.out.print("正在使用wifi上网！！！");
        } else {
            typeName = info.getExtraInfo().toLowerCase();                
            //3gnet/3gwap/uninet/uniwap/cmnet/cmwap/ctnet/ctwap
            System.out.print("正在使用收费网络上网！！！");
        }     
    	return typeName;    
    }
	
	/***
	 * 判断是否已联网
	 * @param context
	 * @return 联网时返回true，否则false
	 */
	public static boolean isAccessNetwork(Context context) {
		try {
			NetworkInfo info = getNetworkInfo(context);
			return info!=null && info.isConnected(); 
		} catch (Exception e) {
			System.out.println("判断联网时出错！！！");
			return false;	
		}	
	}
		 
	private static NetworkInfo getNetworkInfo(Context context) {
		ConnectivityManager manager = (ConnectivityManager) 
				context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return manager.getActiveNetworkInfo();		
	}
}
