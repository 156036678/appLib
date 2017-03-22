package com.xiay.applib.db.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.xiay.applib.db.helper.DatabaseHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;


/**
 * 备份及恢复工具类
 */
public class DatabaseUtils {
    // 当前数据库地址
    private String DB_PATH;
    // 备份后数据库保存地址
    private String DB_BACKUP_PATH;
    private Context context;
    private MessageShow ms;
    // 备份成功状态
    private final int BACKUP_OK = 1;
    // 备份失败状态
    private final int BACKUP_FAIL = -1;
    // 恢复成功状态
    private final int RECOVER_OK = 2;
    // 恢复失败状态
    private final int RECOVER_FAIL = -2;

    public interface MessageShow {

        public void onPepare();

        public void onSuccess();

        public void onFail();


    }

    public DatabaseUtils(Context context) {
        this.context = context;
    }

    /**
     * 数据备份
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public void doDataBackUp(String DB_PATH, String DB_BACKUP_PATH, MessageShow ms) {
        this.DB_PATH = DB_PATH;
        this.DB_BACKUP_PATH = DB_BACKUP_PATH;
        this.ms = ms;
        new BackUpTask().execute();
    }

    /**
     * 数据恢复
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public void doDataRecover(String DB_PATH, String DB_BACKUP_PATH, MessageShow ms) {
        this.DB_PATH = DB_PATH;
        this.DB_BACKUP_PATH = DB_BACKUP_PATH;
        this.ms = ms;
        new RecoverTask().execute();
    }


    /**
     * 设置数据库文件保存位置
     * @param helper
     * @param databasePath SD卡路径
     * @param databaseName 数据库名字
     * @param newVersionCode 数据库版本号 （大于当前版本号执行升级操作）
     */

    public void setDatabasePath(DatabaseHelper helper, String databasePath, String databaseName, int newVersionCode) {
        File f = new File(databasePath);
        if (!f.exists()) {
            f.mkdirs();
        }
        String databasePathAndName= f.getAbsolutePath()+"/"+databaseName;
        helper.DATABASE_PATH=databasePathAndName;
        File file=new File(databasePathAndName);
        if (!file.exists()){
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(databasePathAndName,null);
            db.setVersion(newVersionCode);
            helper.onCreate(db);
        }else {
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(databasePathAndName, null);
            int oldVersionCode = db.getVersion();
            if (newVersionCode > oldVersionCode) {
                db.setVersion(newVersionCode);
                helper.onUpgrade(db, oldVersionCode, newVersionCode);
            }
        }

    }

    /**
     * 数据库备份异步任务
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    class BackUpTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (ms != null) {
                ms.onPepare();
            }
        }

        @Override
        protected Integer doInBackground(String... params) {
            // 默认路径 /data/data/(包名)/databases/*.db
            File dbFile = context.getDatabasePath(DB_PATH);
            File exportDir = new File(DB_BACKUP_PATH);
            int result = 0;
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }
            File backup = new File(exportDir, dbFile.getName());
            try {
                backup.createNewFile();
                fileCopy(dbFile, backup);
                result = BACKUP_OK;
            } catch (Exception e) {
                Log.e("backup_error", e.getMessage());
                result = BACKUP_FAIL;
            }

            return result;

        }

        @Override
        protected void onPostExecute(Integer result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            switch (result) {
                case BACKUP_OK:
                    if (ms != null) {
                        ms.onSuccess();
                    }
                    break;

                case BACKUP_FAIL:
                    if (ms != null) {
                        ms.onFail();
                    }
                    break;
            }
        }

    }

    /**
     * 数据库恢复异步任务
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    class RecoverTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            if (ms != null) {
                ms.onPepare();
            }
        }

        @Override
        protected Integer doInBackground(String... arg0) {
            int result = 0;
            File dbFile = context.getDatabasePath(DB_PATH);
            File exportDir = new File(DB_BACKUP_PATH);
            File backup = new File(exportDir, dbFile.getName());

            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }
            try {
                fileCopy(backup, dbFile);
                result = RECOVER_OK;
            } catch (Exception e) {
                // TODO: handle exception
                Log.e("recover_error", e.getMessage());
                result = RECOVER_FAIL;
            }

            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            switch (result) {
                case RECOVER_OK:
                    if (ms != null) {
                        ms.onSuccess();
                    }
                    break;

                case RECOVER_FAIL:
                    if (ms != null) {
                        ms.onFail();
                    }
                    break;
            }
        }

    }

    /**
     * 文件拷贝方法
     */
    private void fileCopy(File dbFile, File backup) throws IOException {
        FileChannel inChannel = new FileInputStream(dbFile).getChannel();
        FileChannel outChannel = new FileOutputStream(backup).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (IOException e) {
            Log.e("fileCopy_error", e.getMessage());
        } finally {
            if (inChannel != null) {
                inChannel.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
        }
    }
}
