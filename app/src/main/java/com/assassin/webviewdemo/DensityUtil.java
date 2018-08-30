package com.assassin.webviewdemo;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * 像素和dp,sp之间的转换
 */
public final class DensityUtil 
{

	
	private DensityUtil()
	{
		throw new RuntimeException("工具类还要new么？");
	}

	public static int dp2px(Context paramContext, float paramFloat) {
		float f = paramContext.getResources().getDisplayMetrics().density;
		return (int) (paramFloat * f + 0.5F);
	}

	public static int px2dp(Context paramContext, float paramFloat) {
		float f = paramContext.getResources().getDisplayMetrics().density;
		return (int) (paramFloat / f + 0.5F);
	}
	
	
	public static DisplayMetrics getDisplayMetrics(Context context)
	{
		DisplayMetrics outMetrics = new DisplayMetrics();
		((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics;
	}
	
}