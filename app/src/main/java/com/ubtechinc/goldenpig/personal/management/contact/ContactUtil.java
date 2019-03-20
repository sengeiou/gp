package com.ubtechinc.goldenpig.personal.management.contact;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.provider.ContactsContract.CommonDataKinds.Nickname;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.CommonDataKinds.Website;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.text.TextUtils;
import android.util.Log;

import com.ubt.imlibv2.bean.MyContact;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author larson
 */
public class ContactUtil {
    private static String TAG = "ContactUtil";
    private static Context context;
    public List<Contacts> mList;
    private JSONObject contactData;
    private JSONObject jsonObject;

    /**
     * 获取应用的单例
     *
     * @return 应用的单例
     */
    public static ContactUtil getInstance(Context contex) {
        context = contex;
        if (instance == null) {
            synchronized (ContactUtil.class) {
                instance = new ContactUtil();
            }
        }
        return instance;
    }

    public static ContactUtil instance;


    private ContactUtil() {
    }

    // ContactsContract.Contacts.CONTENT_URI= content://com.android.contacts/contacts;
    // ContactsContract.Data.CONTENT_URI = content://com.android.contacts/data;

    /**
     * 获取联系人信息，并把数据转换成json数据
     *
     * @return
     * @throws JSONException
     */
    public String getContactInfo() throws JSONException {
        mList = new ArrayList<Contacts>();
        contactData = new JSONObject();
        String mimetype = "";
        int oldrid = -1;
        int contactId = -1;
        // 1.查询通讯录所有联系人信息，通过id排序，我们看下android联系人的表就知道，所有的联系人的数据是由RAW_CONTACT_ID来索引开的
        // 所以，先获取所有的人的RAW_CONTACT_ID
        Uri uri = Data.CONTENT_URI; // 联系人Uri；
        Cursor cursor = context.getContentResolver().query(uri,
                null, null, null, Data.RAW_CONTACT_ID);
        int numm = 0;
        while (cursor.moveToNext()) {
            contactId = cursor.getInt(cursor
                    .getColumnIndex(Data.RAW_CONTACT_ID));
            if (oldrid != contactId) {
                jsonObject = new JSONObject();
                contactData.put("contact" + numm, jsonObject);
                numm++;
                oldrid = contactId;
            }
            mimetype = cursor.getString(cursor.getColumnIndex(Data.MIMETYPE)); // 取得mimetype类型,扩展的数据都在这个类型里面
            // 1.1,拿到联系人的各种名字
            if (StructuredName.CONTENT_ITEM_TYPE.equals(mimetype)) {
                cursor.getString(cursor
                        .getColumnIndex(StructuredName.DISPLAY_NAME));
                String prefix = cursor.getString(cursor
                        .getColumnIndex(StructuredName.PREFIX));
                jsonObject.put("prefix", prefix);
                String firstName = cursor.getString(cursor
                        .getColumnIndex(StructuredName.FAMILY_NAME));
                jsonObject.put("firstName", firstName);
                String middleName = cursor.getString(cursor
                        .getColumnIndex(StructuredName.MIDDLE_NAME));
                jsonObject.put("middleName", middleName);
                String lastname = cursor.getString(cursor
                        .getColumnIndex(StructuredName.GIVEN_NAME));
                jsonObject.put("lastname", lastname);
                String suffix = cursor.getString(cursor
                        .getColumnIndex(StructuredName.SUFFIX));
                jsonObject.put("suffix", suffix);
                String phoneticFirstName = cursor.getString(cursor
                        .getColumnIndex(StructuredName.PHONETIC_FAMILY_NAME));
                jsonObject.put("phoneticFirstName", phoneticFirstName);

                String phoneticMiddleName = cursor.getString(cursor
                        .getColumnIndex(StructuredName.PHONETIC_MIDDLE_NAME));
                jsonObject.put("phoneticMiddleName", phoneticMiddleName);
                String phoneticLastName = cursor.getString(cursor
                        .getColumnIndex(StructuredName.PHONETIC_GIVEN_NAME));
                jsonObject.put("phoneticLastName", phoneticLastName);
            }
            // 1.2 获取各种电话信息
            if (Phone.CONTENT_ITEM_TYPE.equals(mimetype)) {
                int phoneType = cursor
                        .getInt(cursor.getColumnIndex(Phone.TYPE)); // 手机
                if (phoneType == Phone.TYPE_MOBILE) {
                    String mobile = cursor.getString(cursor
                            .getColumnIndex(Phone.NUMBER));
                    jsonObject.put("mobile", mobile);
                }
                // 住宅电话
                if (phoneType == Phone.TYPE_HOME) {
                    String homeNum = cursor.getString(cursor
                            .getColumnIndex(Phone.NUMBER));
                    jsonObject.put("homeNum", homeNum);
                }
                // 单位电话
                if (phoneType == Phone.TYPE_WORK) {
                    String jobNum = cursor.getString(cursor
                            .getColumnIndex(Phone.NUMBER));
                    jsonObject.put("jobNum", jobNum);
                }
                // 单位传真
                if (phoneType == Phone.TYPE_FAX_WORK) {
                    String workFax = cursor.getString(cursor
                            .getColumnIndex(Phone.NUMBER));
                    jsonObject.put("workFax", workFax);
                }
                // 住宅传真
                if (phoneType == Phone.TYPE_FAX_HOME) {
                    String homeFax = cursor.getString(cursor
                            .getColumnIndex(Phone.NUMBER));

                    jsonObject.put("homeFax", homeFax);
                } // 寻呼机
                if (phoneType == Phone.TYPE_PAGER) {
                    String pager = cursor.getString(cursor
                            .getColumnIndex(Phone.NUMBER));
                    jsonObject.put("pager", pager);
                }
                // 回拨号码
                if (phoneType == Phone.TYPE_CALLBACK) {
                    String quickNum = cursor.getString(cursor
                            .getColumnIndex(Phone.NUMBER));
                    jsonObject.put("quickNum", quickNum);
                }
                // 公司总机
                if (phoneType == Phone.TYPE_COMPANY_MAIN) {
                    String jobTel = cursor.getString(cursor
                            .getColumnIndex(Phone.NUMBER));
                    jsonObject.put("jobTel", jobTel);
                }
                // 车载电话
                if (phoneType == Phone.TYPE_CAR) {
                    String carNum = cursor.getString(cursor
                            .getColumnIndex(Phone.NUMBER));
                    jsonObject.put("carNum", carNum);
                } // ISDN
                if (phoneType == Phone.TYPE_ISDN) {
                    String isdn = cursor.getString(cursor
                            .getColumnIndex(Phone.NUMBER));
                    jsonObject.put("isdn", isdn);
                } // 总机
                if (phoneType == Phone.TYPE_MAIN) {
                    String tel = cursor.getString(cursor
                            .getColumnIndex(Phone.NUMBER));
                    jsonObject.put("tel", tel);
                }
                // 无线装置
                if (phoneType == Phone.TYPE_RADIO) {
                    String wirelessDev = cursor.getString(cursor
                            .getColumnIndex(Phone.NUMBER));

                    jsonObject.put("wirelessDev", wirelessDev);
                } // 电报
                if (phoneType == Phone.TYPE_TELEX) {
                    String telegram = cursor.getString(cursor
                            .getColumnIndex(Phone.NUMBER));
                    jsonObject.put("telegram", telegram);
                }
                // TTY_TDD
                if (phoneType == Phone.TYPE_TTY_TDD) {
                    String tty_tdd = cursor.getString(cursor
                            .getColumnIndex(Phone.NUMBER));
                    jsonObject.put("tty_tdd", tty_tdd);
                }
                // 单位手机
                if (phoneType == Phone.TYPE_WORK_MOBILE) {
                    String jobMobile = cursor.getString(cursor
                            .getColumnIndex(Phone.NUMBER));
                    jsonObject.put("jobMobile", jobMobile);
                }
                // 单位寻呼机
                if (phoneType == Phone.TYPE_WORK_PAGER) {
                    String jobPager = cursor.getString(cursor
                            .getColumnIndex(Phone.NUMBER));
                    jsonObject.put("jobPager", jobPager);
                } // 助理
                if (phoneType == Phone.TYPE_ASSISTANT) {
                    String assistantNum = cursor.getString(cursor
                            .getColumnIndex(Phone.NUMBER));
                    jsonObject.put("assistantNum", assistantNum);
                } // 彩信
                if (phoneType == Phone.TYPE_MMS) {
                    String mms = cursor.getString(cursor
                            .getColumnIndex(Phone.NUMBER));
                    jsonObject.put("mms", mms);
                }

                String mobileEmail = cursor.getString(cursor
                        .getColumnIndex(Email.DATA));
                jsonObject.put("mobileEmail", mobileEmail);
            }
        }
        // 查找event地址
        if (Event.CONTENT_ITEM_TYPE.equals(mimetype)) { // 取出时间类型
            int eventType = cursor.getInt(cursor.getColumnIndex(Event.TYPE)); // 生日
            if (eventType == Event.TYPE_BIRTHDAY) {
                String birthday = cursor.getString(cursor
                        .getColumnIndex(Event.START_DATE));
                jsonObject.put("birthday", birthday);
            }
            // 周年纪念日
            if (eventType == Event.TYPE_ANNIVERSARY) {
                String anniversary = cursor.getString(cursor
                        .getColumnIndex(Event.START_DATE));
                jsonObject.put("anniversary", anniversary);
            }
        }
        // 获取即时通讯消息
        if (Im.CONTENT_ITEM_TYPE.equals(mimetype)) { // 取出即时消息类型
            int protocal = cursor.getInt(cursor.getColumnIndex(Im.PROTOCOL));
            if (Im.TYPE_CUSTOM == protocal) {
                String workMsg = cursor.getString(cursor
                        .getColumnIndex(Im.DATA));
                jsonObject.put("workMsg", workMsg);
            } else if (Im.PROTOCOL_MSN == protocal) {
                String workMsg = cursor.getString(cursor
                        .getColumnIndex(Im.DATA));
                jsonObject.put("workMsg", workMsg);
            }
            if (Im.PROTOCOL_QQ == protocal) {
                String instantsMsg = cursor.getString(cursor
                        .getColumnIndex(Im.DATA));

                jsonObject.put("instantsMsg", instantsMsg);
            }
        }
        // 获取备注信息
        if (Note.CONTENT_ITEM_TYPE.equals(mimetype)) {
            String remark = cursor.getString(cursor.getColumnIndex(Note.NOTE));
            jsonObject.put("remark", remark);
        }
        // 获取昵称信息
        if (Nickname.CONTENT_ITEM_TYPE.equals(mimetype)) {
            String nickName = cursor.getString(cursor
                    .getColumnIndex(Nickname.NAME));
            jsonObject.put("nickName", nickName);
        }
        // 获取组织信息
        if (Organization.CONTENT_ITEM_TYPE.equals(mimetype)) { // 取出组织类型
            int orgType = cursor.getInt(cursor
                    .getColumnIndex(Organization.TYPE)); // 单位
            if (orgType == Organization.TYPE_CUSTOM) { // if (orgType ==
                // Organization.TYPE_WORK)
                // {
                String company = cursor.getString(cursor
                        .getColumnIndex(Organization.COMPANY));
                jsonObject.put("company", company);
                String jobTitle = cursor.getString(cursor
                        .getColumnIndex(Organization.TITLE));
                jsonObject.put("jobTitle", jobTitle);
                String department = cursor.getString(cursor
                        .getColumnIndex(Organization.DEPARTMENT));
                jsonObject.put("department", department);
            }
        }
        // 获取网站信息
        if (Website.CONTENT_ITEM_TYPE.equals(mimetype)) { // 取出组织类型
            int webType = cursor.getInt(cursor.getColumnIndex(Website.TYPE)); // 主页
            if (webType == Website.TYPE_CUSTOM) {

                String home = cursor.getString(cursor
                        .getColumnIndex(Website.URL));
                jsonObject.put("home", home);
            } // 主页
            else if (webType == Website.TYPE_HOME) {
                String home = cursor.getString(cursor
                        .getColumnIndex(Website.URL));
                jsonObject.put("home", home);
            }
            // 个人主页
            if (webType == Website.TYPE_HOMEPAGE) {
                String homePage = cursor.getString(cursor
                        .getColumnIndex(Website.URL));
                jsonObject.put("homePage", homePage);
            }
            // 工作主页
            if (webType == Website.TYPE_WORK) {
                String workPage = cursor.getString(cursor
                        .getColumnIndex(Website.URL));
                jsonObject.put("workPage", workPage);
            }
        }
        // 查找通讯地址
        if (StructuredPostal.CONTENT_ITEM_TYPE.equals(mimetype)) { // 取出邮件类型
            int postalType = cursor.getInt(cursor
                    .getColumnIndex(StructuredPostal.TYPE)); // 单位通讯地址
            if (postalType == StructuredPostal.TYPE_WORK) {
                String street = cursor.getString(cursor
                        .getColumnIndex(StructuredPostal.STREET));
                jsonObject.put("street", street);
                String ciry = cursor.getString(cursor
                        .getColumnIndex(StructuredPostal.CITY));
                jsonObject.put("ciry", ciry);
                String box = cursor.getString(cursor
                        .getColumnIndex(StructuredPostal.POBOX));
                jsonObject.put("box", box);
                String area = cursor.getString(cursor
                        .getColumnIndex(StructuredPostal.NEIGHBORHOOD));
                jsonObject.put("area", area);

                String state = cursor.getString(cursor
                        .getColumnIndex(StructuredPostal.REGION));
                jsonObject.put("state", state);
                String zip = cursor.getString(cursor
                        .getColumnIndex(StructuredPostal.POSTCODE));
                jsonObject.put("zip", zip);
                String country = cursor.getString(cursor
                        .getColumnIndex(StructuredPostal.COUNTRY));
                jsonObject.put("country", country);
            }
            // 住宅通讯地址
            if (postalType == StructuredPostal.TYPE_HOME) {
                String homeStreet = cursor.getString(cursor
                        .getColumnIndex(StructuredPostal.STREET));
                jsonObject.put("homeStreet", homeStreet);
                String homeCity = cursor.getString(cursor
                        .getColumnIndex(StructuredPostal.CITY));
                jsonObject.put("homeCity", homeCity);
                String homeBox = cursor.getString(cursor
                        .getColumnIndex(StructuredPostal.POBOX));
                jsonObject.put("homeBox", homeBox);
                String homeArea = cursor.getString(cursor
                        .getColumnIndex(StructuredPostal.NEIGHBORHOOD));
                jsonObject.put("homeArea", homeArea);
                String homeState = cursor.getString(cursor
                        .getColumnIndex(StructuredPostal.REGION));
                jsonObject.put("homeState", homeState);
                String homeZip = cursor.getString(cursor
                        .getColumnIndex(StructuredPostal.POSTCODE));
                jsonObject.put("homeZip", homeZip);
                String homeCountry = cursor.getString(cursor
                        .getColumnIndex(StructuredPostal.COUNTRY));
                jsonObject.put("homeCountry", homeCountry);
            }
            // 其他通讯地址
            if (postalType == StructuredPostal.TYPE_OTHER) {
                String otherStreet = cursor.getString(cursor
                        .getColumnIndex(StructuredPostal.STREET));
                jsonObject.put("otherStreet", otherStreet);

                String otherCity = cursor.getString(cursor
                        .getColumnIndex(StructuredPostal.CITY));
                jsonObject.put("otherCity", otherCity);
                String otherBox = cursor.getString(cursor
                        .getColumnIndex(StructuredPostal.POBOX));
                jsonObject.put("otherBox", otherBox);
                String otherArea = cursor.getString(cursor
                        .getColumnIndex(StructuredPostal.NEIGHBORHOOD));
                jsonObject.put("otherArea", otherArea);
                String otherState = cursor.getString(cursor
                        .getColumnIndex(StructuredPostal.REGION));
                jsonObject.put("otherState", otherState);
                String otherZip = cursor.getString(cursor
                        .getColumnIndex(StructuredPostal.POSTCODE));
                jsonObject.put("otherZip", otherZip);
                String otherCountry = cursor.getString(cursor
                        .getColumnIndex(StructuredPostal.COUNTRY));
                jsonObject.put("otherCountry", otherCountry);
            }
        }
        cursor.close();
        Log.d("contactData", contactData.toString());
        return contactData.toString();
    }

    /**
     * 获取联系人信息，并把数据转换成json数据
     *
     * @return
     * @throws JSONException
     */
    ArrayList<String> indexString;

    public List<MyContact> getContactList() {
        indexString = new ArrayList<String>();
        List<MyContact> cache = new ArrayList<MyContact>();
        try {
            String mimetype = "";
            int oldrid = -1;
            int contactId = -1;
            // 1.查询通讯录所有联系人信息，通过id排序，我们看下android联系人的表就知道，所有的联系人的数据是由RAW_CONTACT_ID来索引开的
            // 所以，先获取所有的人的RAW_CONTACT_ID
            Uri uri = Data.CONTENT_URI; // 联系人Uri；
            Cursor cursor = context.getContentResolver().query(uri,
                    null, null, null, Data.RAW_CONTACT_ID);
            if (cursor == null) {
                return cache;
            }
            while (cursor.moveToNext()) {
                contactId = cursor.getInt(cursor
                        .getColumnIndex(Data.RAW_CONTACT_ID));
                if (oldrid != contactId) {
                    MyContact contact = new MyContact();
                    cache.add(contact);
                    oldrid = contactId;
                }
                mimetype = cursor.getString(cursor.getColumnIndex(Data.MIMETYPE)); // 取得mimetype类型,扩展的数据都在这个类型里面
                // 1.1,拿到联系人的各种名字
                if (StructuredName.CONTENT_ITEM_TYPE.equals(mimetype)) {
                    String display = cursor.getString(cursor
                            .getColumnIndex(StructuredName.DISPLAY_NAME));
                    String lastname = cursor.getString(cursor
                            .getColumnIndex(StructuredName.GIVEN_NAME));
                    String firstName = cursor.getString(cursor
                            .getColumnIndex(StructuredName.FAMILY_NAME));
                    String middleName = cursor.getString(cursor
                            .getColumnIndex(StructuredName.MIDDLE_NAME));
                    if (!TextUtils.isEmpty(display)) {
                        cache.get(cache.size() - 1).name = display;
                    } else {
                        try {
                            String name = (TextUtils.isEmpty(firstName) ? "" : firstName) +
                                    (TextUtils.isEmpty(middleName) ? "" : middleName) +
                                    (TextUtils.isEmpty(lastname) ? "" : lastname);
                            cache.get(cache.size() - 1).name = name;
                        } catch (Exception e) {
                        }
                    }
                    if (!TextUtils.isEmpty(cache.get(cache.size() - 1).name)) {
                        cache.get(cache.size() - 1).name = cache.get(cache.size() - 1).name.replace(" ", "");
                    }
                }
                // 1.2 获取各种电话信息
                if (Phone.CONTENT_ITEM_TYPE.equals(mimetype)) {
                    String mobile = cursor.getString(cursor
                            .getColumnIndex(Phone.NUMBER));
                    if (!TextUtils.isEmpty(mobile)) {
                        mobile = mobile.replace(" ", "");
                    }
                    //过滤86和+86开头
                    if (!TextUtils.isEmpty(mobile)) {
                        try {
                            if (substring(mobile, 0, 2).equals("86")) {
                                mobile = substring(mobile, 2);
                            }
                            if (substring(mobile, 0, 3).equals("+86")) {
                                mobile = substring(mobile, 3);
                            }
                        } catch (Exception e) {
                        }
                    }
                    if (!TextUtils.isEmpty(mobile)) {
                        if (cache.get(cache.size() - 1).numberList == null) {
                            List<String> numberList = new ArrayList<>();
                            cache.get(cache.size() - 1).numberList = numberList;
                        }
                        cache.get(cache.size() - 1).numberList.add(mobile);
                    }
//                    int phoneType = cursor
//                            .getInt(cursor.getColumnIndex(Phone.TYPE)); // 手机
//                    if (phoneType == Phone.TYPE_MOBILE) {
//                        String mobile = cursor.getString(cursor
//                                .getColumnIndex(Phone.NUMBER));
//                        cache.get(cache.size() - 1).mobile = mobile;
//                    } else if (phoneType == Phone.TYPE_HOME) {
//                        String homeNum = cursor.getString(cursor
//                                .getColumnIndex(Phone.NUMBER));
//                        cache.get(cache.size() - 1).mobile = homeNum;
//                    } else if (phoneType == Phone.TYPE_WORK) {
//                        String jobNum = cursor.getString(cursor
//                                .getColumnIndex(Phone.NUMBER));
//                        cache.get(cache.size() - 1).mobile = jobNum;
//                    }
//                    if (!TextUtils.isEmpty(cache.get(cache.size() - 1).mobile)) {
//                        cache.get(cache.size() - 1).mobile = cache.get(cache.size() - 1).mobile.replace(" ", "");
//                    }
                }
            }
            cursor.close();

            List<MyContact> list = new ArrayList<MyContact>();
            for (int i = 0; i < cache.size(); i++) {
                if (TextUtils.isEmpty(cache.get(i).name) || cache.get(i).numberList == null || cache.get(i).numberList
                        .size() == 0) {
                    continue;
                }
                for (int j = 0; j < cache.get(i).numberList.size(); j++) {
                    MyContact myContact = new MyContact();
                    myContact.id = cache.get(i).id;
                    myContact.name = cache.get(i).name;
                    myContact.mobile = cache.get(i).numberList.get(j);
                    list.add(myContact);
                }
            }
//        for (int i = 0; i < cache.size(); i++) {
//            if (!TextUtils.isEmpty(cache.get(i).name) && !TextUtils.isEmpty(cache.get(i).mobile)) {
//                list.add(cache.get(i));
//            }
//        }
            for (int i = 0; i < list.size(); i++) {
                String pinyin = PinyinUtils.getPingYin(list.get(i).name);
                String sortString = pinyin.substring(0, 1).toUpperCase();
                if (sortString.matches("[A-Z]")) {
                    list.get(i).sortLetter = sortString.toUpperCase();
                    list.get(i).pinyin = pinyin;
                    if (!indexString.contains(sortString)) {
                        indexString.add(sortString);
                    }
                } else {
                    list.get(i).sortLetter = "#";
                    list.get(i).pinyin = "#";
                    if (!indexString.contains("#")) {
                        indexString.add("#");
                    }
                }
            }
            Collections.sort(indexString);
            return list;
        } catch (Exception e) {
            return cache;
        }
    }

    public ArrayList<String> getIndexString() {
//        ArrayList<String> al = new ArrayList<String>();
//        al.addAll(indexString.subList(3,8));
        return indexString;
    }

    public List<MyContact> fetchContact() {
        List<MyContact> cache = new ArrayList<>();
        try {
            ContentResolver cr = context.getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            if (cur == null) {
                return cache;
            }
            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        while (pCur.moveToNext()) {
                            String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone
                                    .NUMBER));
                            if (!TextUtils.isEmpty(phoneNo)) {
                                phoneNo = phoneNo.replace(" ", "");
                            }
                            int type = pCur.getInt(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                            switch (type) {
                                case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                                    try {
                                        if (!TextUtils.isEmpty(phoneNo)) {
                                            MyContact contactClass = new MyContact();
                                            contactClass.id = id;
                                            contactClass.name = name;
                                            contactClass.mobile = phoneNo;
                                            cache.add(contactClass);
                                        }
                                    } catch (Exception e) {
                                        Log.d(TAG, e.toString());
                                    }
                                    break;
                                case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                                    try {
                                        if (!TextUtils.isEmpty(phoneNo)) {
                                            MyContact contactClass = new MyContact();
                                            contactClass.id = id;
                                            contactClass.name = name;
                                            contactClass.mobile = phoneNo;
                                            cache.add(contactClass);
                                        }
                                    } catch (Exception e) {
                                        Log.d(TAG, e.toString());
                                    }
                                    break;
                                case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                                    try {
                                        if (!TextUtils.isEmpty(phoneNo)) {
                                            MyContact contactClass = new MyContact();
                                            contactClass.id = id;
                                            contactClass.name = name;
                                            contactClass.mobile = phoneNo;
                                            cache.add(contactClass);
                                        }
                                    } catch (Exception e) {
                                        Log.d(TAG, e.toString());
                                    }
                                    break;
                                case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME:
                                    try {
                                        if (!TextUtils.isEmpty(phoneNo)) {
                                            MyContact contactClass = new MyContact();
                                            contactClass.id = id;
                                            contactClass.name = name;
                                            contactClass.mobile = phoneNo;
                                            cache.add(contactClass);
                                        }
                                    } catch (Exception e) {
                                        Log.d(TAG, e.toString());
                                    }
                                    break;
                                case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK:
                                    try {
                                        if (!TextUtils.isEmpty(phoneNo)) {
                                            MyContact contactClass = new MyContact();
                                            contactClass.id = id;
                                            contactClass.name = name;
                                            contactClass.mobile = phoneNo;
                                            cache.add(contactClass);
                                        }
                                    } catch (Exception e) {
                                        Log.d(TAG, e.toString());
                                    }
                                    break;
                                case ContactsContract.CommonDataKinds.Phone.TYPE_MAIN:
                                    try {
                                        if (!TextUtils.isEmpty(phoneNo)) {
                                            MyContact contactClass = new MyContact();
                                            contactClass.id = id;
                                            contactClass.name = name;
                                            contactClass.mobile = phoneNo;
                                            cache.add(contactClass);
                                        }
                                    } catch (Exception e) {
                                        Log.d(TAG, e.toString());
                                    }
                                    break;
                                case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                                    try {
                                        if (!TextUtils.isEmpty(phoneNo)) {
                                            MyContact contactClass = new MyContact();
                                            contactClass.id = id;
                                            contactClass.name = name;
                                            contactClass.mobile = phoneNo;
                                            cache.add(contactClass);
                                        }
                                    } catch (Exception e) {
                                        Log.d(TAG, e.toString());
                                    }
                                    break;
                                case ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM:
                                    try {
                                        if (!TextUtils.isEmpty(phoneNo)) {
                                            MyContact contactClass = new MyContact();
                                            contactClass.id = id;
                                            contactClass.name = name;
                                            contactClass.mobile = phoneNo;
                                            cache.add(contactClass);
                                        }
                                    } catch (Exception e) {
                                        Log.d(TAG, e.toString());
                                    }
                                    break;
                                case ContactsContract.CommonDataKinds.Phone.TYPE_PAGER:
                                    try {
                                        if (!TextUtils.isEmpty(phoneNo)) {
                                            MyContact contactClass = new MyContact();
                                            contactClass.id = id;
                                            contactClass.name = name;
                                            contactClass.mobile = phoneNo;
                                            cache.add(contactClass);
                                        }
                                    } catch (Exception e) {
                                        Log.d(TAG, e.toString());
                                    }
                                    break;
                            }
                        }
                        pCur.close();
                    }
                }
            }
            List<MyContact> list = new ArrayList<MyContact>();
            for (int i = 0; i < cache.size(); i++) {
                if (!TextUtils.isEmpty(cache.get(i).name) && !TextUtils.isEmpty(cache.get(i).mobile)) {
                    list.add(cache.get(i));
                }
            }

            indexString = new ArrayList<String>();
            for (int i = 0; i < list.size(); i++) {
                String pinyin = PinyinUtils.getPingYin(list.get(i).name);
                String sortString = pinyin.substring(0, 1).toUpperCase();
                if (sortString.matches("[A-Z]")) {
                    list.get(i).sortLetter = sortString.toUpperCase();
                    if (!indexString.contains(sortString)) {
                        indexString.add(sortString);
                    }
                } else {
                    list.get(i).sortLetter = "#";
                    if (!indexString.contains("#")) {
                        indexString.add("#");
                    }
                }
            }
            Collections.sort(indexString);
            return list;
        } catch (Exception e) {
            return cache;
        }
    }

    /**
     * 截取字符串
     *
     * @param s
     * @param from
     * @return
     */
    public static String substring(String s, int from) {
        try {
            return s.substring(from);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String substring(String s, int from, int len) {
        try {
            return s.substring(from, from + len);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
