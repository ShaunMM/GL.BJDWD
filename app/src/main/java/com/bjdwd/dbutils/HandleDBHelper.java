package com.bjdwd.dbutils;

import android.content.Context;

import com.bjdwd.R;

/**
 * Created by dell on 2017/3/23.
 * 重写父类的抽象方法告诉它数据库信息以及建表语句，然后提供一个单例供外部获取
 */
public class HandleDBHelper extends DataBaseHelper {

    private static HandleDBHelper mHandleDBHelpere;

    private HandleDBHelper(Context context) {
        super(context);
    }

    public static HandleDBHelper getInstance(Context context) {
        if (mHandleDBHelpere == null) {
            synchronized (DataBaseHelper.class) {
                if (mHandleDBHelpere == null) {
                    mHandleDBHelpere = new HandleDBHelper(context);
                    if (mHandleDBHelpere.getDB() == null || !mHandleDBHelpere.getDB().isOpen()) {
                        mHandleDBHelpere.open();
                    }
                }
            }
        }
        return mHandleDBHelpere;
    }

    @Override
    protected int getMDbVersion(Context context) {
//        return Integer.valueOf(context.getResources().getStringArray(R.array.DATABASE_INFO)[1]);
        return context.getResources().getInteger(R.integer.DATABASE_VERSION);
    }

    @Override
    protected String getDbName(Context context) {
        return context.getResources().getStringArray(R.array.DATABASE_INFO)[0];
    }

    @Override
    protected String[] getDbCreateSql(Context context) {
        return context.getResources().getStringArray(R.array.CREATE_TABLE_SQL);
    }

    @Override
    protected String[] getDbUpdateSql(Context context) {
        return context.getResources().getStringArray(R.array.UPDATE_TABLE_SQL);
    }
}
