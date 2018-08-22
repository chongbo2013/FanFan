package com.fanfan.novel.plugin;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.fanfan.robot.R;

import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

/**
 * Created by android on 2018/2/3.
 */

public class PluginActivity extends AppCompatActivity {

    private ClassLoader mPluginClassLoader;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //插件APK路径
        //  /data/user/0/com.nan.dynalmic/files/plugin-debug.apk
        String dexPath = getFileStreamPath("plugin-debug.apk").getAbsolutePath();
        //DexClassLoader加载的时候Dex文件释放的路径
        //  /data/user/0/com.nan.dynalmic/app_dex
        String fileReleasePath = getDir("dex", Context.MODE_PRIVATE).getAbsolutePath();

        Log.e("acy", dexPath);
        Log.e("acy", fileReleasePath);

        //通过DexClassLoader加载插件APK
        mPluginClassLoader = new DexClassLoader(dexPath, fileReleasePath, null, getClassLoader());


        launchTarget();

//        launchTarget2();
    }

    private void launchTarget() {

        //通过反射调用插件的代码
        try {
            Class<?> beanClass = mPluginClassLoader.loadClass("com.fanfan.novel.plugin.Bean");
            Object beanObject = beanClass.newInstance();

            Method setNameMethod = beanClass.getMethod("setName", String.class);
            setNameMethod.setAccessible(true);
            Method getNameMethod = beanClass.getMethod("getName");
            getNameMethod.setAccessible(true);

            setNameMethod.invoke(beanObject, "huannan");
            String name = (String) getNameMethod.invoke(beanObject);

            Toast.makeText(PluginActivity.this, name, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void launchTarget2() {
        //通过面向接口编程调用插件的代码

        Class<?> beanClass = null;
        try {
            beanClass = mPluginClassLoader.loadClass("com.fanfan.novel.plugin.Bean");
            IBean bean = (IBean) beanClass.newInstance();

            bean.setName("test");
            Toast.makeText(PluginActivity.this, bean.getName(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Class<?> dynamicClass = null;
        try {
            dynamicClass = mPluginClassLoader.loadClass("com.fanfan.novel.plugin.Dynamic");
            IDynamic dynamic = (IDynamic) dynamicClass.newInstance();

            dynamic.methodWithCallback(new Callback() {
                @Override
                public void callback(IBean bean) {
                    //插件回调宿主
                    Toast.makeText(PluginActivity.this, bean.getName(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 加载插件的资源：通过AssetManager添加插件的APK资源路径
     */
    protected void loadPluginResources(String mDexPath) {
        //反射加载资源
        AssetManager mAssetManager = null;
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, mDexPath);
            mAssetManager = assetManager;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Resources superRes = super.getResources();
        Resources mResources = new Resources(mAssetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
        Resources.Theme mTheme = mResources.newTheme();
        mTheme.setTo(super.getTheme());

    }


}
