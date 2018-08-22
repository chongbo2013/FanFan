package com.fanfan.novel.utils.tele;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.fanfan.robot.model.xf.Telephone;

import java.util.ArrayList;

/**
 * Created by zhangyuanyuan on 2017/12/13.
 */

public class TelePhoneUtils {

    public static ArrayList<Telephone> queryContacts(Context context, String phoneName) {
        ArrayList<Telephone> telephones = new ArrayList<>();
        // 获取用来操作数据的类的对象，对联系人的基本操作都是使用这个对象
        ContentResolver cr = context.getContentResolver();
        // 根据姓名查询时的，查询条件，主要用在cu游标上  mimetype='vnd.android.cursor.item/name' AND data1 LIKE '%张三%'
//        String selection = ContactsContract.Data.MIMETYPE + "='"
//                + ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
//                + "' AND " + ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME + " LIKE " + "'%" + phoneName + "%'";
        // 查询contacts表的所有记录
        Cursor cursor;
        if (phoneName != null) {
            String selection = ContactsContract.PhoneLookup.DISPLAY_NAME + "=?";
            String[] selectionArgs = new String[]{phoneName};
            cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, selection, selectionArgs, null);
        } else {
            cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        }
        // 如果记录不为空
        if (cursor.getCount() > 0) {
            // 游标初始指向查询结果的第一条记录的上方，执行moveToNext函数会判断
            // 下一条记录是否存在，如果存在，指向下一条记录。否则，返回false。
            while (cursor.moveToNext()) {
                Telephone telephone = new Telephone();
                ArrayList<String> phoneNumbers = new ArrayList<>();

                // 获取联系人ID
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                // 获取RawContacts表的游标
                Cursor rawContactCur = cr.query(ContactsContract.RawContacts.CONTENT_URI, null,
                        ContactsContract.RawContacts._ID + "=?", new String[]{id}, null);
                String rawContactId = "";
                // 该查询结果一般只返回一条记录，所以我们直接让游标指向第一条记录
                if (rawContactCur.moveToFirst()) {
                    // 读取第一条记录的RawContacts._ID列的值
                    rawContactId = rawContactCur.getString(rawContactCur.getColumnIndex(ContactsContract.RawContacts._ID));
                }
                // 关闭游标
                rawContactCur.close();

                // 读取号码
                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    // 根据查询RAW_CONTACT_ID查询该联系人的号码
//                    Cursor phoneCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                            null, ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID + "=?",
//                            new String[]{rawContactId}, null);
                    Cursor phoneCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                            new String[]{id}, null);
                    // 一个联系人可能有多个号码，需要遍历
                    while (phoneCur.moveToNext()) {
                        // 获取号码
                        String number = phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        // 获取号码类型
                        String type = phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                        phoneNumbers.add(number);
                    }
                    phoneCur.close();
                }
                telephone.setPhone(phoneNumbers);
                telephone.setName(contactName);
                telephones.add(telephone);
            }
            cursor.close();
        }
        return telephones;
    }

    public void addContact(Context context, String name, String phoneNum) {
        ContentValues values = new ContentValues();
        Uri rawContactUri = context.getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContactUri);
        // 向data表插入数据
        if (name != "") {
            values.clear();
            values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);
            context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
        }
        // 向data表插入电话号码
        if (phoneNum != "") {
            values.clear();
            values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNum);
            values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
            context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
        }
    }

    public void deleteContact(Context context, long rawContactId) {
        context.getContentResolver().delete(
                ContentUris.withAppendedId(ContactsContract.RawContacts.CONTENT_URI, rawContactId), null, null);
    }

}
