package com.ider.ytb_tv.utils;

import android.content.Context;

import java.lang.reflect.Method;

/**
 * Created by ider-eric on 2016/8/10.
 */
public class PropertyReader {



    public static String getString(Context context, String key) {

        String ret= "";

        try{

            ClassLoader cl = context.getClassLoader();
            @SuppressWarnings("rawtypes")
            Class SystemProperties = cl.loadClass("android.os.SystemProperties");

            //参数类型
            @SuppressWarnings("rawtypes")
            Class[] paramTypes= new Class[1];
            paramTypes[0]= String.class;

            Method get = SystemProperties.getMethod("get", paramTypes);

            //参数
            Object[] params= new Object[1];
            params[0]= new String(key);

            ret= (String) get.invoke(SystemProperties, params);

        }catch( IllegalArgumentException iAE ){
            throw iAE;
        }catch( Exception e ){
            ret= "";
        }

        return ret;

    }

}
