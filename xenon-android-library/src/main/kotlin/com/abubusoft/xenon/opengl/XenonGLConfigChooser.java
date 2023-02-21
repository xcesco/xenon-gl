package com.abubusoft.xenon.opengl;

import com.abubusoft.xenon.android.surfaceview.ConfigOptions;
import com.abubusoft.xenon.android.surfaceview16.ArgonConfigChooser16;
import com.abubusoft.xenon.android.surfaceview16.SmartConfigChooser;

public class XenonGLConfigChooser {
	
	static ConfigOptions options;
	
	static ArgonConfigChooser16 configChooser;

	public static void setOptions(ConfigOptions value)
	{
		options=value;
	}
	
	public static ArgonConfigChooser16 build()
	{
		if (configChooser==null)
		{
			configChooser=new SmartConfigChooser(options);
		}
		return configChooser;
	}

	public static ConfigOptions getOptions() {
		return options;
	}

}
