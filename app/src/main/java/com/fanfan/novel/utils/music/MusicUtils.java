package com.fanfan.novel.utils.music;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.fanfan.robot.app.common.Constants;
import com.fanfan.novel.utils.system.FileUtil;
import com.fanfan.novel.utils.bitmap.ImageUtils;
import com.fanfan.novel.utils.media.MediaFile;
import com.fanfan.novel.utils.system.PreferencesUtils;
import com.fanfan.novel.utils.system.SystemUtils;
import com.fanfan.robot.R;
import com.fanfan.robot.app.NovelApp;
import com.fanfan.robot.model.Music;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by android on 2018/1/10.
 */

public class MusicUtils {

    private static final String SELECTION = MediaStore.Audio.AudioColumns.SIZE + " >= ? AND "
            + MediaStore.Audio.AudioColumns.DURATION + " >= ?";

    /**
     * 扫描歌曲
     */
    @NonNull
    public static List<Music> scanMusic(Context context, boolean isTF) {
        List<Music> musicList = new ArrayList<>();


        long filterSize = parseLong(PreferencesUtils.getString(context, context.getString(R.string.setting_key_filter_size), "5")) * 1024;
        long filterTime = parseLong(PreferencesUtils.getString(context, context.getString(R.string.setting_key_filter_time), "5")) * 1000;

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{
                        BaseColumns._ID,
                        MediaStore.Audio.AudioColumns.IS_MUSIC,
                        MediaStore.Audio.AudioColumns.TITLE,
                        MediaStore.Audio.AudioColumns.ARTIST,
                        MediaStore.Audio.AudioColumns.ALBUM,
                        MediaStore.Audio.AudioColumns.ALBUM_ID,
                        MediaStore.Audio.AudioColumns.DATA,
                        MediaStore.Audio.AudioColumns.DISPLAY_NAME,
                        MediaStore.Audio.AudioColumns.SIZE,
                        MediaStore.Audio.AudioColumns.DURATION
                },
                SELECTION,
                new String[]{
                        String.valueOf(filterSize),
                        String.valueOf(filterTime)
                },
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor == null) {
            return musicList;
        }

        int i = 0;
        while (cursor.moveToNext()) {
            // 是否为音乐，魅族手机上始终为0
            int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.IS_MUSIC));
            if (!SystemUtils.isFlyme() && isMusic == 0) {
                continue;
            }

            long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
            String title = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE)));
            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
            String album = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM)));
            long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));
            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
            String fileName = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME)));
            long fileSize = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));

            Music music = new Music();
            music.setTitle(title);
            music.setArtist(artist);
            music.setAlbum(album);
            music.setAlbumId(albumId);
            music.setDuration(duration);
            music.setPath(path);
            music.setFileName(fileName);
            music.setFileSize(fileSize);
            if (fileName.endsWith(".wav")) {

            } else {
                musicList.add(music);
            }
        }
        cursor.close();
        if (isTF) {
            List<Music> musics = loadTFCard("Android/data/com.netease.cloudmusic/files/Documents/Music/");
            if (musics != null) {
                musicList.addAll(musics);
            }
        }
        return musicList;
    }

    private static List<Music> loadTFCard(String musicPath) {
        List<Music> musicList = new ArrayList<>();
        if (!FileUtil.isMountSdcard()) {
            return null;
        }
        String mountSdcards = FileUtil.getStoragePath(NovelApp.getInstance().getApplicationContext(), true);
        File musicFile = new File(mountSdcards + File.separator + musicPath);
        if (!musicFile.exists() || musicFile.isFile()) {
            return null;
        }
        File[] files = musicFile.listFiles();
        if (files.length == 0) {
            return null;
        }
        for (File file : files) {
            if (MediaFile.isAudioFileType(file.getAbsolutePath())) {
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                Uri uri = Uri.fromFile(file);
                mmr.setDataSource(NovelApp.getInstance().getApplicationContext(), uri);
                String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                String album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                long duration = Long.valueOf(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                String path = file.getAbsolutePath();
                String fileName = file.getName();
                long fileSize = 0;
                try {
                    FileInputStream fis = new FileInputStream(file);
                    fileSize = fis.available();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Music music = new Music();
                music.setTitle(title);
                music.setArtist(artist);
                music.setAlbum(album);
                music.setAlbumId(-1);
                music.setDuration(duration);
                music.setPath(path);
                music.setFileName(fileName);
                music.setFileSize(fileSize);
                musicList.add(music);
            }
        }
        return musicList;
    }

    private static long parseLong(String s) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static Uri getMediaStoreAlbumCoverUri(long albumId) {
        Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        return ContentUris.withAppendedId(artworkUri, albumId);
    }

    public static byte[] getMediaDataAlbumPic(final String filePath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(filePath); //设置数据源
        return retriever.getEmbeddedPicture(); //得到字节型数据
    }

    public static Bitmap createAlbumArt(final String filePath) {
        Bitmap bitmap = null;
        //能够获取多媒体文件元数据的类
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath); //设置数据源
            byte[] embedPic = retriever.getEmbeddedPicture(); //得到字节型数据
            if (embedPic == null) {
                return null;
            }
            bitmap = BitmapFactory.decodeByteArray(embedPic, 0, embedPic.length); //转换为图片
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return bitmap;
    }


    public static boolean isAudioControlPanelAvailable(Context context) {
        return isIntentAvailable(context, new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL));
    }

    private static boolean isIntentAvailable(Context context, Intent intent) {
        return context.getPackageManager().resolveActivity(intent, PackageManager.GET_RESOLVED_FILTER) != null;
    }

    public static Bitmap loadCoverFromMediaStore(Context context, Music music) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = MusicUtils.getMediaStoreAlbumCoverUri(music.getAlbumId());
        InputStream is;
        try {
            is = resolver.openInputStream(uri);
        } catch (FileNotFoundException ignored) {
            return null;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeStream(is, null, options);
    }


    public static Bitmap loadRound(Context context, Music music) {
        return loadCover(context, music, Type.ROUND);
    }

    public static Bitmap loadCover(Context context, Music music, Type type) {
        Bitmap bitmap = null;
        if (music != null) {

            bitmap = music.getAlbumId() == -1 ?
                    MusicUtils.createAlbumArt(music.getPath()) : MusicUtils.loadCoverFromMediaStore(context, music);
        }
        if (bitmap != null) {
            return bitmap;
        }
        bitmap = getDefaultCover(context, type);
        return bitmap;
    }

    public static Bitmap getDefaultCover(Context context, Type type) {
        switch (type) {
            case BLUR:
                return BitmapFactory.decodeResource(context.getResources(), R.mipmap.play_page_default_bg);
            case ROUND:
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.placeholder_disk_play_song);
                bitmap = ImageUtils.resizeImage(bitmap, Constants.displayWidth / 2, Constants.displayHeight / 2);
                return bitmap;
            default:
                return BitmapFactory.decodeResource(context.getResources(), R.mipmap.default_cover);
        }
    }

    private enum Type {
        BLUR("#BLUR"),
        ROUND("#ROUND");

        private String value;

        Type(String value) {
            this.value = value;
        }
    }

}
