package com.fanfan.novel.utils.grammer;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;

import com.fanfan.novel.utils.bitmap.BitmapUtils;
import com.fanfan.novel.utils.media.MediaFile;
import com.fanfan.novel.utils.system.AppUtil;
import com.fanfan.novel.utils.system.FileUtil;
import com.fanfan.robot.R;
import com.fanfan.robot.app.NovelApp;
import com.fanfan.robot.app.common.Constants;
import com.fanfan.robot.db.manager.NavigationDBManager;
import com.fanfan.robot.db.manager.SiteDBManager;
import com.fanfan.robot.db.manager.VideoDBManager;
import com.fanfan.robot.db.manager.VoiceDBManager;
import com.fanfan.robot.model.NavigationBean;
import com.fanfan.robot.model.SiteBean;
import com.fanfan.robot.model.VideoBean;
import com.fanfan.robot.model.VoiceBean;
import com.hankcs.hanlp.HanLP;
import com.seabreeze.log.Print;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class LoadDataUtils {

    public static List<NavigationBean> loadNavigation() {

        List<NavigationBean> navigationBeanList = new ArrayList<>();

        List<String> navigationFiles = loadNavigationImage();

        String[] navigation = resArray(R.array.navigation);
        String[] navigationData = resArray(R.array.navigation_data);
        String[] localNavigation = resArray(R.array.local_navigation);
        String[] localNavigationDatail = resArray(R.array.local_navigation_datail);


        for (int i = 0; i < localNavigation.length; i++) {
            NavigationBean navigationBean = new NavigationBean();

            int navigationIndex = new Random().nextInt(navigation.length);

            navigationBean.setSaveTime(System.currentTimeMillis());
            navigationBean.setTitle(localNavigation[i]);
            navigationBean.setGuide(localNavigationDatail[i]);
            navigationBean.setDatail(localNavigationDatail[i]);
            navigationBean.setNavigation(navigation[navigationIndex]);
            navigationBean.setNavigationData(navigationData[navigationIndex]);
            if (navigationFiles != null && navigationFiles.size() > 0) {
                for (int j = 0; j < navigationFiles.size(); j++) {
                    if (navigationFiles.get(j).indexOf(localNavigation[i]) > 0) {
                        navigationBean.setImgUrl(navigationFiles.get(j));
                    }
                }
//                int imageIndex = new Random().nextInt(imageFiles.size());
//                navigationBean.setImgUrl(imageFiles.get(imageIndex).getAbsolutePath());
            }
            navigationBeanList.add(navigationBean);
        }
        return navigationBeanList;
    }

    public static List<SiteBean> loadSite() {

        List<SiteBean> siteBeanList = new ArrayList<>();

        String[] localSiteName = resArray(R.array.local_site);
        String[] localSiteUrl = resArray(R.array.local_site_url);

        for (int i = 0; i < localSiteName.length; i++) {
            SiteBean siteBean = new SiteBean();
            siteBean.setName(localSiteName[i]);
            siteBean.setUrl(localSiteUrl[i]);
            siteBean.setSaveTime(System.currentTimeMillis());
            siteBeanList.add(siteBean);
        }
        return siteBeanList;
    }

    public static List<VideoBean> loadVideoBean() {

        List<VideoBean> videoBeanList = new ArrayList<>();

        List<File> videoFiles = LoadDataUtils.loadVideoFile(Constants.RES_DIR_NAME);
        if (videoFiles != null && videoFiles.size() > 0) {
            for (File file : videoFiles) {
                VideoBean videoBean = new VideoBean();

                String name = file.getName().substring(0, file.getName().indexOf("."));
                String showTitle = name.replace(" ", "").replace("-", "");
                long size = FileUtil.getFileSize(file);
                Bitmap bitmap = FileUtil.getVideoThumb(file.getAbsolutePath());
                String savePath = Constants.PROJECT_PATH + "video" + File.separator + name + ".jpg";
                BitmapUtils.saveBitmapToFile(bitmap, "video", name + ".jpg");

                videoBean.setSize(size);
                videoBean.setVideoName(name);
                videoBean.setVideoUrl(file.getAbsolutePath());
                videoBean.setVideoImage(savePath);
                videoBean.setShowTitle(showTitle);
                videoBean.setSaveTime(System.currentTimeMillis());
                videoBeanList.add(videoBean);
            }
        }
        return videoBeanList;
    }

    public static List<VoiceBean> loadVoiceBean() {

        List<VoiceBean> voiceBeanList = new ArrayList<>();

        List<File> imageFiles = loadImageFile(Constants.RES_DIR_NAME);

        String[] localVoiceQuestion = resArray(R.array.local_voice_question);
        String[] localVoiceAnswer = resArray(R.array.local_voice_answer);

        String[] action = resArray(R.array.action);
        String[] actionOrder = resArray(R.array.action_order);

        String[] expression = resArray(R.array.expression);
        String[] expressionData = resArray(R.array.expression_data);


        for (int i = 0; i < localVoiceQuestion.length; i++) {
            VoiceBean voiceBean = new VoiceBean();

            String showTitle = localVoiceQuestion[i];
            voiceBean.setSaveTime(System.currentTimeMillis());
            voiceBean.setShowTitle(showTitle);
            voiceBean.setVoiceAnswer(localVoiceAnswer[i]);


            int actionIndex = new Random().nextInt(action.length);
            int expressionIndex = new Random().nextInt(expression.length);

            voiceBean.setExpression(expression[expressionIndex]);
            voiceBean.setExpressionData(expressionData[expressionIndex]);
            voiceBean.setAction(action[actionIndex]);
            voiceBean.setActionData(actionOrder[actionIndex]);

            if (imageFiles != null && imageFiles.size() > 0) {
                int imageIndex = new Random().nextInt(imageFiles.size());
                voiceBean.setImgUrl(imageFiles.get(imageIndex).getAbsolutePath());
            }

            List<String> keywordList = HanLP.extractKeyword(localVoiceQuestion[i], 5);
            if (keywordList != null && keywordList.size() > 0) {
                Print.i(keywordList);

                if (keywordList.size() == 1) {
                    voiceBean.setKey1(showTitle);
                } else {
                    for (int j = 0; j < keywordList.size(); j++) {
                        String key = keywordList.get(j);
                        if (j == 0) {
                            voiceBean.setKey1(key);
                        } else if (j == 1) {
                            voiceBean.setKey2(key);
                        } else if (j == 2) {
                            voiceBean.setKey3(key);
                        } else if (j == 4) {
                            voiceBean.setKey4(key);
                        }
                    }
                }
            } else {
                voiceBean.setKey1(showTitle);
            }

            voiceBeanList.add(voiceBean);
        }
        return voiceBeanList;
    }

    public static List<File> loadImageFile(String dirName) {

        List<File> imageFiles = new ArrayList<>();

        String dirPath = Environment.getExternalStorageDirectory() + File.separator + dirName;
        File dirFile = new File(dirPath);
        if (!dirFile.exists() || dirFile.isFile()) {
            dirFile.mkdirs();
            return null;
        }
        File[] files = dirFile.listFiles();
        if (files.length == 0) {
            return null;
        }
        for (File file : files) {
            if (MediaFile.isImageFileType(file.getAbsolutePath())) {
                imageFiles.add(file);
            }
        }
        return imageFiles;
    }

    public static List<File> loadVideoFile(String dirName) {

        List<File> videoFiles = new ArrayList<>();

        String dirPath = Environment.getExternalStorageDirectory() + File.separator + dirName;
        File dirFile = new File(dirPath);
        if (!dirFile.exists() || dirFile.isFile()) {
            dirFile.mkdirs();
            return null;
        }
        File[] files = dirFile.listFiles();
        if (files.length == 0) {
            return null;
        }
        for (File file : files) {
            if (MediaFile.isVideoFileType(file.getAbsolutePath())) {
                videoFiles.add(file);
            }
        }
        return videoFiles;
    }


    public static List<String> loadNavigationImage() {

        List<String> navigationFiles = new ArrayList<>();

        AssetManager manager = NovelApp.getInstance().getApplicationContext().getResources().getAssets();
        try {
            String fileNames[] = manager.list("train");
            if (fileNames != null && fileNames.length > 0) {
                for (int i = 0; i < fileNames.length; i++) {
                    navigationFiles.add("file:///android_asset/train/" + fileNames[i]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return navigationFiles;
    }

    public static String[] resArray(int resId) {
        return NovelApp.getInstance().getApplicationContext().getResources().getStringArray(resId);
    }

    public static String getLexiconContents() {

        VoiceDBManager voiceDBManager = new VoiceDBManager();
        VideoDBManager videoDBManager = new VideoDBManager();
        NavigationDBManager navigationDBManager = new NavigationDBManager();
        SiteDBManager siteDBManager = new SiteDBManager();

        //语法更新
        //存放三类语法中的关键词
        Set<String> setL1 = new HashSet<>();
        Set<String> setL2 = new HashSet<>();
        Set<String> setL3 = new HashSet<>();
        Set<String> setL4 = new HashSet<>();

        List<VoiceBean> voiceBeanList = voiceDBManager.loadAll();
        for (VoiceBean bean : voiceBeanList) {
            if (bean.getKey1() != null)
                setL1.add(bean.getKey1());
            if (bean.getKey2() != null)
                setL2.add(bean.getKey2());
            if (bean.getKey3() != null)
                setL3.add(bean.getKey3());
            if (bean.getKey4() != null)
                setL4.add(bean.getKey4());
        }

        //读取语法文件
        String localGrammar = GrammerUtils.getLocalGrammar(NovelApp.getInstance().getApplicationContext());
        StringBuilder sb = new StringBuilder(localGrammar);

        //程序中
        List<String> localStrings = AppUtil.getLocalStrings();
        setL1.addAll(localStrings);

        //视频
        List<VideoBean> videoBeanList = videoDBManager.loadAll();
        for (VideoBean videoBean : videoBeanList) {
            setL1.add(videoBean.getShowTitle());
        }

        //导航
        List<NavigationBean> navigationBeanList = navigationDBManager.loadAll();
        for (NavigationBean navigationBean : navigationBeanList) {
            setL1.add(navigationBean.getTitle());
        }

        //网址
        List<SiteBean> siteBeanList = siteDBManager.loadAll();
        for (SiteBean siteBean : siteBeanList) {
            setL1.add(siteBean.getName());
        }

        //语音
        StringBuilder sbL1 = GrammerUtils.insertSet(sb, setL1, GrammerUtils.getIndexL1(localGrammar));
        StringBuilder sbL2 = GrammerUtils.insertSet(sbL1, setL2, GrammerUtils.getIndexL2(sbL1));
        StringBuilder sbL3 = GrammerUtils.insertSet(sbL2, setL3, GrammerUtils.getIndexL3(sbL2));
        StringBuilder sbL4 = GrammerUtils.insertSet(sbL3, setL4, GrammerUtils.getIndexL4(sbL3));

        return sbL4.toString();
    }


    public static List<VoiceBean> loadExcelVoiceBean(String workPath) throws IOException, BiffException {

        File newFile = new File(workPath);
        if (!newFile.exists()) {
            return null;
        }

        Workbook workbook = Workbook.getWorkbook(newFile);
        Sheet sheet = workbook.getSheet(0);
        int sheetRows = sheet.getRows();

        List<VoiceBean> voiceBeanList = new ArrayList<>();

        for (int i = 1; i < sheetRows; i++) {

            VoiceBean bean = new VoiceBean();

            String showTitle = sheet.getCell(1, i).getContents().trim();

            bean.setSaveTime(System.currentTimeMillis());
            bean.setShowTitle(showTitle);
            bean.setVoiceAnswer(sheet.getCell(2, i).getContents().trim());

            //截取关键字
            List<String> keywordList = HanLP.extractKeyword(showTitle, 5);
            if (keywordList != null && keywordList.size() > 0) {
                Print.i(keywordList);

                if (keywordList.size() == 1) {
                    bean.setKey1(showTitle);
                } else {
                    for (int j = 0; j < keywordList.size(); j++) {
                        String key = keywordList.get(j);
                        if (j == 0) {
                            bean.setKey1(key);
                        } else if (j == 1) {
                            bean.setKey2(key);
                        } else if (j == 2) {
                            bean.setKey3(key);
                        } else if (j == 4) {
                            bean.setKey4(key);
                        }
                    }
                }
            } else {
                bean.setKey1(showTitle);
            }

            voiceBeanList.add(bean);
        }
        workbook.close();

        return voiceBeanList;
    }

}
