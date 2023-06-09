package com.checker.sa.android.db;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.apache.commons.lang.SerializationUtils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.Html;
import android.text.Spanned;
import android.util.Base64;
import android.util.Log;

import com.checker.sa.android.data.Allocations;
import com.checker.sa.android.data.AltLangStrings;
import com.checker.sa.android.data.AltLanguage;
import com.checker.sa.android.data.Answers;
import com.checker.sa.android.data.AutoValues;
import com.checker.sa.android.data.BranchProperties;
import com.checker.sa.android.data.Branches;
import com.checker.sa.android.data.Cert;
import com.checker.sa.android.data.CustomFields;
import com.checker.sa.android.data.InProgressStampData;
import com.checker.sa.android.data.ListClass;
import com.checker.sa.android.data.LoopsEntry;
import com.checker.sa.android.data.Objects;
import com.checker.sa.android.data.Order;
import com.checker.sa.android.data.Orders;
import com.checker.sa.android.data.POS_Shelf;
import com.checker.sa.android.data.ProductLocations;
import com.checker.sa.android.data.ProductProperties;
import com.checker.sa.android.data.ProductPropertyVals;
import com.checker.sa.android.data.Products;
import com.checker.sa.android.data.QuestionnaireData;
import com.checker.sa.android.data.Quota;
import com.checker.sa.android.data.Set;
import com.checker.sa.android.data.SubmitQuestionnaireData;
import com.checker.sa.android.data.Survey;
import com.checker.sa.android.data.SurveyQnA;
import com.checker.sa.android.data.Titles;
import com.checker.sa.android.data.UploadFileData;
import com.checker.sa.android.data.Workers;
import com.checker.sa.android.data.filePathDataID;
import com.checker.sa.android.data.pngItem;
import com.checker.sa.android.data.validationSets;
import com.checker.sa.android.dialog.Revamped_Loading_Dialog;
import com.checker.sa.android.helper.Constants;
import com.checker.sa.android.helper.Helper;
import com.checker.sa.android.transport.Connector;
import com.mor.sa.android.activities.CheckerApp;
import com.mor.sa.android.activities.R;

public class DBHelper {
    private static final Object lock = new Object();

    public static void deletetblLoopDataAgainstSetId(String table,
                                                     String setId, String listname) {

        String where = Constants.tblSetId + "=" + "\"" + setId + "\" and "
                + Constants.tblListName + "=" + "\"" + listname + "\"";
        DBAdapter.openDataBase();
        DBAdapter.db.delete(table, where, null);
        DBAdapter.closeDataBase();
    }

    // Insert Data in Table tblLoop
    private static void inserttblLoopData(String table,
                                          ArrayList<LoopsEntry> loops) {
        synchronized (lock) {

            DBAdapter.openDataBase("");

            for (int i = 0; i <= loops.size() - 1; i++) {

                ContentValues values = new ContentValues();
                values.put(Constants.tblSetId, loops.get(i).getSetId());
                values.put(Constants.tblColumnNumber, loops.get(i)
                        .getColumnNumber());
                values.put(Constants.tblColumnName, loops.get(i)
                        .getColumnName());
                values.put(Constants.tblColumnData, loops.get(i)
                        .getColumnData());
                values.put(Constants.tblLastColumnData, loops.get(i)
                        .getLastColumn());
                values.put(Constants.tblListName, loops.get(i).getListName());
                values.put(Constants.tblListRowIndex, loops.get(i)
                        .getRowNumber());
                values.put(Constants.tblListID, loops.get(i).getListID());

                DBAdapter.db.insert(table, null, values);
            }

            DBAdapter.closeDataBase("");
        }
    }

    // Read Data from Table tblLoop

    public static ArrayList<LoopsEntry> selecttblLoopData(String table,
                                                          String Where, ArrayList<LoopsEntry> loopEntry) {
        String[] columns = new String[8];

        columns[0] = Constants.tblSetId;
        columns[1] = Constants.tblColumnNumber;
        columns[2] = Constants.tblColumnName;
        columns[3] = Constants.tblColumnData;
        columns[4] = Constants.tblLastColumnData;
        columns[5] = Constants.tblListName;
        columns[6] = Constants.tblListRowIndex;
        columns[7] = Constants.tblListID;

        synchronized (lock) {
            DBAdapter.openDataBase();
            Cursor c = null;
            try {

                c = DBAdapter.db.query(true, table, columns, Where, null, null,
                        null, null, null);

                if (c.getCount() == 0) {
                    c.close();
                    DBAdapter.closeDataBase();
                    return loopEntry;
                }
                c.moveToFirst();
                DBAdapter.openDataBase();
                loopEntry = new ArrayList<LoopsEntry>();
                do {

                    int setId = c.getColumnIndex(columns[0]);
                    int ColumnNumber = c.getColumnIndex(columns[1]);
                    int ColumnName = c.getColumnIndex(columns[2]);
                    int ColumnData = c.getColumnIndex(columns[3]);
                    int LastColumnData = c.getColumnIndex(columns[4]);
                    int ListName = c.getColumnIndex(columns[5]);
                    int ListRowIndex = c.getColumnIndex(columns[6]);
                    int ListID = c.getColumnIndex(columns[7]);

                    try {
                        LoopsEntry loopsEntry = new LoopsEntry();
                        String msetID = c.getString(setId);
                        int mColumnNumber = c.getInt(ColumnNumber);
                        String mColumnName = c.getString(ColumnName);
                        String mColumnData = c.getString(ColumnData);
                        String mLastColumnData = c.getString(LastColumnData);
                        String mListName = c.getString(ListName);
                        String mListRowIndex = c.getString(ListRowIndex);
                        String mListID = c.getString(ListID);

                        loopsEntry.setSetId(msetID);
                        loopsEntry.setColumnNumber(mColumnNumber);
                        loopsEntry.setColumnName(mColumnName);
                        loopsEntry.setColumnData(mColumnData);
                        loopsEntry.setLastColumn(mLastColumnData);
                        loopsEntry.setListName(mListName);
                        loopsEntry.setListID(mListID);
                        try {
                            loopsEntry.setRowNumber(Integer
                                    .parseInt(mListRowIndex));
                        } catch (NumberFormatException ex) {
                            loopsEntry.setRowNumber(0);
                        }

                        loopEntry.add(loopsEntry);
                    } catch (Exception ex) {
                        Log.d("Exception", ex.toString());
                    }
                } while (c.moveToNext());
                c.close();
                DBAdapter.closeDataBase();
            } catch (Exception e) {
                System.out.println("" + e.toString());
            } finally {
                try {
                    if (!c.isClosed())
                        c.close();
                    DBAdapter.closeDataBase();
                } catch (Exception ex) {
                    Log.d("Currsor Exception", "Cursor Null Exception");
                }
            }
            return loopEntry;
        }
    }

    public static void insertQuestionnaire(String table, String[] columns,
                                           String oid, String ft, String slt, String slng, String elt,
                                           String elng, String ftime, String stime, String totalSent,
                                           String sid, ArrayList<Quota> quotas, String edit_purchase_details,
                                           String edit_purchase_payment, String edit_purchase_description,
                                           String edit_service_invoice_number, String edit_service_payment,
                                           String edit_service_description,
                                           String edit_transportation_payment,
                                           String edit_transportation_description, String millis, String rs) {
        synchronized (lock) {

            // DeleteThisSubmittedSurvey(table,Constants.DB_TABLE_SUBMITSURVEY_OID+"="+oid);
            ContentValues values = new ContentValues();
            values.put(columns[0], oid);
            values.put(columns[1], ft);
            values.put(columns[2], slt);
            values.put(columns[3], slng);
            values.put(columns[4], elt);
            values.put(columns[5], elng);

            if (!stime.equals("-1"))
                values.put(columns[6], stime);

            values.put(columns[7], ftime);
            values.put(columns[8], totalSent);
            values.put(columns[9], sid);

            values.put(columns[10], edit_purchase_details);
            values.put(columns[11], edit_purchase_payment);
            values.put(columns[12], edit_purchase_description);
            values.put(columns[13], edit_service_invoice_number);
            values.put(columns[14], edit_service_payment);
            values.put(columns[15], edit_service_description);
            values.put(columns[16], edit_transportation_payment);
            values.put(columns[17], edit_transportation_description);
            values.put(columns[18], rs);

            values.put(Constants.POST_FIELD_QUES_UNIX, millis);

            String where = Constants.DB_TABLE_SUBMITSURVEY_OID + "=" + "\""
                    + oid + "\"";
            DBAdapter.openDataBase();
            DBAdapter.db.delete(table, where, null);
            DBAdapter.openDataBase();
            DBAdapter.db.insert(table, null, values);
            DBAdapter.closeDataBase();

            deleteSubmitQuotas(Constants.DB_TABLE_SUBMITQUOTA, new String[]{
                    Constants.DB_TABLE_SUBMITQUOTA_OrderID,
                    Constants.DB_TABLE_SUBMITQUOTA_QuotaID,}, oid);
            if (quotas != null && quotas.size() > 0) {

                for (int i = 0; i < quotas.size(); i++) {
                    if (quotas.get(i).getsquoID() != null) {
                        insertSubmitQuotas(
                                Constants.DB_TABLE_SUBMITQUOTA,
                                new String[]{
                                        Constants.DB_TABLE_SUBMITQUOTA_OrderID,
                                        Constants.DB_TABLE_SUBMITQUOTA_QuotaID,},
                                quotas.get(i), oid, true);
                    }
                }
            }
        }
    }

    private static void insertSubmitQuotas(String table, String[] columns,
                                           Quota quota, String oid, boolean i) {
        ContentValues values = new ContentValues();
        values.put(columns[0], oid);
        values.put(columns[1], quota.getsquoID());
        String where = Constants.DB_TABLE_SUBMITQUOTA_QuotaID + "=" + "\""
                + quota.getsquoID() + "\" AND "
                + Constants.DB_TABLE_SUBMITQUOTA_OrderID + "=\"" + oid + "\"";
        // DBAdapter.openDataBase();
        // DBAdapter.db.delete(table, where, null);
        DBAdapter.openDataBase();
        DBAdapter.db.insert(table, null, values);
        DBAdapter.closeDataBase();

    }

    private static void deleteSubmitQuotas(String table, String[] columns,
                                           String oid) {

        String where = Constants.DB_TABLE_SUBMITQUOTA_OrderID + "=" + "\""
                + oid + "\" ";
        DBAdapter.openDataBase();
        DBAdapter.db.delete(table, where, null);

        DBAdapter.closeDataBase();

    }

    public static void insertValidateQuestionnaire(String table,
                                                   String[] columns, String oid, String ft, String slt, String slng,
                                                   String elt, String elng, String ftime, String ValidationDataID,
                                                   String totalAnswerSent) {
        synchronized (lock) {
            ContentValues values = new ContentValues();
            values.put(columns[0], oid);
            values.put(columns[1], ft);
            values.put(columns[2], slt);
            values.put(columns[3], slng);
            values.put(columns[4], elt);
            values.put(columns[5], elng);
            values.put(columns[6], ftime);
            values.put(columns[7], ValidationDataID);
            values.put(columns[8], totalAnswerSent);
            Calendar cal = Calendar.getInstance();
            String millis = cal.getTimeInMillis() + "";
            values.put(Constants.POST_FIELD_QUES_UNIX, millis);
            String where = Constants.DB_TABLE_SUBMITSURVEY_OID + "=" + "\""
                    + oid + "\"";
            DBAdapter.openDataBase();
            DBAdapter.db.delete(table, where, null);
            DBAdapter.openDataBase();
            DBAdapter.db.insert(table, null, values);
            DBAdapter.closeDataBase();
        }
    }

    public static void deleteFileInDb(String orderid, String dataid, String path) {
        synchronized (lock) {

            String where = Constants.DB_TABLE_QUESTIONNAIRE_ORDERID + "="
                    + "\"" + orderid + "\" and " + Constants.UPLOAD_FILe_DATAID
                    + "=" + "\"" + dataid + "\" and "
                    + Constants.UPLOAD_FILe_MEDIAFILE + "=" + "\"" + path
                    + "\"";
            DBAdapter.openDataBase();
            int i = DBAdapter.db.delete(Constants.UPLOAD_FILE_TABLE, where,
                    null);
            DBAdapter.closeDataBase();

        }
    }

    public static void uploadFilesInDB(String table, String[] columns,
                                       String orderid, ArrayList<Uri> files,
                                       ArrayList<filePathDataID> uploadedFilesDataIDs, String input,
                                       String millis, String setName) {
        synchronized (lock) {
            if (input.contains("ubmit")) {
                String where = Constants.DB_TABLE_QUESTIONNAIRE_ORDERID + "="
                        + "\"" + orderid + "\"";
                DBAdapter.openDataBase();
                DBAdapter.db.delete(Constants.UPLOAD_FILE_TABLE, where, null);
                DBAdapter.closeDataBase();
            }
            ContentValues values = new ContentValues();

            SimpleDateFormat stimeformat = new SimpleDateFormat(
                    "yyyy-MM-dd  kk:mm:ss", Locale.ENGLISH);
            long l = 0;
            Date date = null;
            try {
                date = stimeformat.parse(stimeformat.format(new Date()
                        .getTime()));
                // l = date.getTime();

            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            for (int i = 0; i < uploadedFilesDataIDs.size(); i++) {
                values.clear();

                values.put(columns[1], uploadedFilesDataIDs.get(i)
                        .getFilePath());
                values.put(columns[2], uploadedFilesDataIDs.get(i).getDataID());
                values.put(columns[3], uploadedFilesDataIDs.get(i)
                        .getUPLOAD_FILe_ORDERID());
                if (date != null)
                    values.put(columns[4], cleanBranchName(uploadedFilesDataIDs
                            .get(i).getUPLOAD_FILe_BRANCH_NAME())
                            + " ("
                            + date
                            + ")");
                else
                    values.put(columns[4], uploadedFilesDataIDs.get(i)
                            .getUPLOAD_FILe_BRANCH_NAME());
                values.put(columns[5], uploadedFilesDataIDs.get(i)
                        .getUPLOAD_FILe_CLIENT_NAME());
                values.put(columns[6], millis);
                values.put(columns[7], setName);
                values.put(columns[8], uploadedFilesDataIDs.get(i)
                        .getUPLOAD_FILe_Sample_size());

                values.put(columns[9], uploadedFilesDataIDs.get(i)
                        .getUPLOAD_FILe_PRODUCTID());
                values.put(columns[10], uploadedFilesDataIDs.get(i)
                        .getUPLOAD_FILe_LOCATIONID());
                DBAdapter.openDataBase();
                long c = DBAdapter.db.insert(table, null, values);
                DBAdapter.closeDataBase();
            }
        }
    }

    public static ArrayList<filePathDataID> getQuestionnaireUploadFiles(
            String table, String[] columns, String orderid, String orderby,
            ArrayList<filePathDataID> items) {
        try {
            UploadFileData fd = (UploadFileData) convertFromBytesFileTbl("files_"
                    + orderid + ".txt");
            if (fd != null)
                return fd.getItemPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<filePathDataID>();
    }

    public static void uploadFiles(String table, String[] columns,
                                   String orderid, ArrayList<Uri> files,
                                   ArrayList<filePathDataID> uploadedFilesDataIDs, String input,
                                   String millis, String setName) {
        UploadFileData allFile = null;

        if (uploadedFilesDataIDs != null) {
            try {
                allFile = new UploadFileData();
                allFile.setItemPath(uploadedFilesDataIDs);
                allFile.setMillis(millis);
                allFile.setSetName(setName);
                convertToBytesFileTbl(allFile, "files_" + orderid + ".txt");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private static String cleanBranchName(String upload_FILe_BRANCH_NAME) {
        if (upload_FILe_BRANCH_NAME != null
                && upload_FILe_BRANCH_NAME.contains("(")
                && upload_FILe_BRANCH_NAME.contains(")")) {
            int index = upload_FILe_BRANCH_NAME.indexOf("(");
            String branch = upload_FILe_BRANCH_NAME.substring(index);
            return branch;
        }
        return upload_FILe_BRANCH_NAME;
    }

    public static void updateValidationQuestionnaire(String table,
                                                     String[] columns, QuestionnaireData qd, POS_Shelf shelf_data,
                                                     String setId) {

        // Log.v("questionnaireData.size()", ""+questionnaireData.size());
        // for(int index =0; index<questionnaireData.size(); index++)
        synchronized (lock) {
            // QuestionnaireData qd = questionnaireData.get(index);
            ContentValues values = new ContentValues();
            values.put(columns[0], qd.getDataID());
            values.put(columns[1], qd.getQuestionText());
            values.put(columns[2], qd.getBranchID());
            values.put(columns[3], qd.getWorkerID());
            values.put(columns[4], qd.getOrderID());

            String where = Constants.DB_TABLE_QUESTIONNAIRE_ORDERID + "="
                    + "\"" + qd.getOrderID() + "\"" + " AND "
                    + Constants.DB_TABLE_ANSWERS_DATAID + "=" + "\""
                    + qd.getDataID() + "\"";
            DBAdapter.openDataBase();
            DBAdapter.db.delete(table, where, null);
            DBAdapter.openDataBase();
            DBAdapter.db.delete(Constants.VALITION_ANSWER_TABLE, where, null);

            DBAdapter.openDataBase();
            DBAdapter.db.insert(table, null, values);
            saveAnswerData(Constants.VALITION_ANSWER_TABLE, new String[]{
                    Constants.DB_TABLE_ANSWERS_ANSWERID,
                    Constants.DB_TABLE_ANSWERS_ATEXT,
                    Constants.DB_TABLE_ANSWERS_AVALUE,
                    Constants.DB_TABLE_ANSWERS_DATAID,
                    Constants.DB_TABLE_ANSWERS_ORDERID,
                    Constants.DB_TABLE_ANSWERS_MI,
                    Constants.DB_TABLE_ANSWERS_RANK}, qd);
            values = null;

            DBAdapter.closeDataBase();

            SavePOSData(shelf_data, setId);
        }
    }

    public static void updateThisQuestionnaire(String table, String[] columns,
                                               QuestionnaireData questionnaireData, POS_Shelf shelf_data,
                                               String setId) {
        // synchronized (lock)
        {

            DBAdapter.openDataBase();

            {
                QuestionnaireData qd = questionnaireData;
                ContentValues values = new ContentValues();
                values.put(columns[0], qd.getDataID());
                values.put(columns[1], qd.getQuestionText());
                values.put(columns[3], qd.getQuestionTypeLink());
                values.put(columns[4], qd.getObjectType());
                values.put(columns[5], qd.getBranchID());
                values.put(columns[6], qd.getWorkerID());
                values.put(columns[7], qd.getOrderID());
                if (qd.getAnswerText() != null
                        && !qd.getAnswerText().equals("")) {
                    values.put(columns[8], qd.getAnswerText());

                } else if (qd.getMi() != null && !qd.getMi().equals("")) {
                    values.put(columns[8], qd.getMi());
                } else
                    values.put(columns[8], qd.getFreetext());
                values.put(columns[9], qd.getFinishtime());
                if (columns.length == 11)
                    values.put(columns[10], qd.getLoopinfo());
                if (DBHelper.isQuestionnaireListAvailable(
                        Constants.DB_TABLE_QUESTIONNAIRE, new String[]{
                                Constants.DB_TABLE_QUESTIONNAIRE_DATAID,
                                Constants.DB_TABLE_QUESTIONNAIRE_QTEXT,
                                Constants.DB_TABLE_QUESTIONNAIRE_ORDERID,
                                Constants.DB_TABLE_QUESTIONNAIRE_FT},
                        Constants.DB_TABLE_QUESTIONNAIRE_ORDERID + "=" + "\""
                                + qd.getOrderID() + "\"" + " AND "
                                + Constants.DB_TABLE_ANSWERS_DATAID + "="
                                + "\"" + qd.getDataID() + "\"",
                        Constants.DB_TABLE_QUESTIONNAIRE_DATAID)) {
                    String where = Constants.DB_TABLE_QUESTIONNAIRE_ORDERID
                            + "=" + "\"" + qd.getOrderID() + "\"" + " AND "
                            + Constants.DB_TABLE_ANSWERS_DATAID + "=" + "\""
                            + qd.getDataID() + "\"";
                    DBAdapter.openDataBase();
                    try {
                        DBAdapter.db.delete(Constants.DB_TABLE_QUESTIONNAIRE,
                                where, null);
                        DBAdapter.openDataBase();
                        // Log.v("Deelte - DB_TABLE_QUESTIONNAIRE", "   "+del);
                        // DBAdapter.openDataBase();
                        DBAdapter.db.delete(Constants.DB_TABLE_ANSWERS, where,
                                null);
                        // DBAdapter.openDataBase();
                        // DBAdapter.db.delete(Constants.DB_TABLE_POS,
                        // Constants.DB_TABLE_QUESTIONNAIRE_ORDERID + "="
                        // + "\"" + qd.getOrderID() + "\"", null);
                    } catch (Exception ex) {
                        String str = "";
                        str += "";

                    }

                }
                DBAdapter.openDataBase();
                DBAdapter.db.insert(table, null, values);
                // Log.v("updateQuestionnaire()",
                // values.toString()+"   "+insert);
                saveAnswerData(Constants.DB_TABLE_ANSWERS, new String[]{
                        Constants.DB_TABLE_ANSWERS_ANSWERID,
                        Constants.DB_TABLE_ANSWERS_ATEXT,
                        Constants.DB_TABLE_ANSWERS_AVALUE,
                        Constants.DB_TABLE_ANSWERS_DATAID,
                        Constants.DB_TABLE_ANSWERS_ORDERID,
                        Constants.DB_TABLE_ANSWERS_MI,
                        Constants.DB_TABLE_ANSWERS_DISPLAYCONDITION,
                        Constants.DB_TABLE_ANSWERS_RANK}, qd);

                values = null;

                DBHelper.isQuestionnaireListAvailable(
                        Constants.DB_TABLE_QUESTIONNAIRE, new String[]{
                                Constants.DB_TABLE_QUESTIONNAIRE_DATAID,
                                Constants.DB_TABLE_QUESTIONNAIRE_QTEXT,
                                Constants.DB_TABLE_QUESTIONNAIRE_ORDERID,
                                Constants.DB_TABLE_QUESTIONNAIRE_FT},
                        Constants.DB_TABLE_QUESTIONNAIRE_ORDERID + "=" + "\""
                                + qd.getOrderID() + "\"" + " AND "
                                + Constants.DB_TABLE_ANSWERS_DATAID + "="
                                + "\"" + qd.getDataID() + "\"",
                        Constants.DB_TABLE_QUESTIONNAIRE_DATAID);
            }
            DBAdapter.closeDataBase();
            // SavePOSData(shelf_data, setId);
        }
    }

    public static void updateQuestionnaire(String table, String[] columns,
                                           ArrayList<QuestionnaireData> questionnaireData,
                                           POS_Shelf shelf_data, String setId) {
        // synchronized (lock)
        {
            DBAdapter.openDataBase();
            for (int index = 0; index < questionnaireData.size(); index++) {
                QuestionnaireData qd = questionnaireData.get(index);
                ContentValues values = new ContentValues();
                values.put(columns[0], qd.getDataID());
                values.put(columns[1], qd.getQuestionText());
                values.put(columns[3], qd.getQuestionTypeLink());
                values.put(columns[4], qd.getObjectType());
                values.put(columns[5], qd.getBranchID());
                values.put(columns[6], qd.getWorkerID());
                values.put(columns[7], qd.getOrderID());
                if (qd.getFreetext() == null || qd.getFreetext().equals(""))
                    values.put(columns[8], qd.getAnswerText());
                else
                    values.put(columns[8], qd.getFreetext());
                values.put(columns[9], qd.getFinishtime());
                values.put(columns[10], qd.getLoopinfo());

                if (DBHelper.isQuestionnaireListAvailable(
                        Constants.DB_TABLE_QUESTIONNAIRE, new String[]{
                                Constants.DB_TABLE_QUESTIONNAIRE_DATAID,
                                Constants.DB_TABLE_QUESTIONNAIRE_QTEXT,
                                Constants.DB_TABLE_QUESTIONNAIRE_ORDERID,},
                        Constants.DB_TABLE_QUESTIONNAIRE_ORDERID + "=" + "\""
                                + qd.getOrderID() + "\"" + " AND "
                                + Constants.DB_TABLE_ANSWERS_DATAID + "="
                                + "\"" + qd.getDataID() + "\"",
                        Constants.DB_TABLE_QUESTIONNAIRE_DATAID)) {
                    String where = Constants.DB_TABLE_QUESTIONNAIRE_ORDERID
                            + "=" + "\"" + qd.getOrderID() + "\"" + " AND "
                            + Constants.DB_TABLE_ANSWERS_DATAID + "=" + "\""
                            + qd.getDataID() + "\"";

                    DBAdapter.closeDataBase();
                    DBAdapter.openDataBase(true);
                    try {
                        DBAdapter.db.delete(Constants.DB_TABLE_QUESTIONNAIRE,
                                where, null);
                        DBAdapter.closeDataBase();
                        DBAdapter.openDataBase(true);
                        // Log.v("Deelte - DB_TABLE_QUESTIONNAIRE", "   "+del);
                        // DBAdapter.openDataBase();
                        DBAdapter.db.delete(Constants.DB_TABLE_ANSWERS, where,
                                null);
                        DBAdapter.closeDataBase();
                        DBAdapter.openDataBase(true);
                        DBAdapter.db.delete(Constants.DB_TABLE_POS,
                                Constants.DB_TABLE_QUESTIONNAIRE_ORDERID + "="
                                        + "\"" + qd.getOrderID() + "\"", null);
                    } catch (Exception ex) {
                        String str = "";
                        str += "";

                    }

                }
                DBAdapter.closeDataBase();
                DBAdapter.openDataBase(true);
                DBAdapter.db.insert(table, null, values);
                // Log.v("updateQuestionnaire()",
                // values.toString()+"   "+insert);
                saveAnswerData(Constants.DB_TABLE_ANSWERS, new String[]{
                        Constants.DB_TABLE_ANSWERS_ANSWERID,
                        Constants.DB_TABLE_ANSWERS_ATEXT,
                        Constants.DB_TABLE_ANSWERS_AVALUE,
                        Constants.DB_TABLE_ANSWERS_DATAID,
                        Constants.DB_TABLE_ANSWERS_ORDERID,
                        Constants.DB_TABLE_ANSWERS_MI,
                        Constants.DB_TABLE_ANSWERS_DISPLAYCONDITION,
                        Constants.DB_TABLE_ANSWERS_RANK}, qd);

                values = null;
            }
            DBAdapter.closeDataBase();
            SavePOSData(shelf_data, setId);
        }
    }

    public static void SavePOSData(POS_Shelf shelf, String setId) {
        Products current_product;
        ProductLocations current_location;
        double current_price = -1;
        int current_quantity = -1;
        String current_note = "";
        String current_date = "";
        int nameValueCount = 0;
        if (shelf.listProducts == null || shelf.listProductLocations == null)
            return;
        for (int i = 0; i < shelf.listProducts.size(); i++) {
            for (int j = 0; j < shelf.listProductLocations.size(); j++) {
                current_product = shelf.listProducts.get(i);
                current_location = shelf.listProductLocations.get(j);
                current_price = -1;
                current_price = shelf.price_item.getPrice(
                        current_product.getProductID(),
                        current_location.getProdLocationID());

                current_note = "";
                current_note = shelf.note_item.getItemText(
                        current_product.getProductID(),
                        current_location.getProdLocationID());

                current_quantity = -1;
                current_quantity = shelf.quantity_item.getQuantity(
                        current_product.getProductID(),
                        current_location.getProdLocationID());

                current_date = "";
                current_date = shelf.expiration_item.getItemText(
                        current_product.getProductID(),
                        current_location.getProdLocationID());
                if (current_price > -1 || current_quantity > -1
                        || !current_note.equals("") || !current_date.equals("")) {
                    ContentValues values = new ContentValues();
                    values.put(Constants.DB_TABLE_POS_ProductId,
                            String.valueOf(current_product.getProductID()));
                    values.put(Constants.DB_TABLE_POS_SetId,
                            String.valueOf(setId));
                    values.put(Constants.DB_TABLE_POS_LocationId, String
                            .valueOf(current_location.getProdLocationID()));
                    values.put(Constants.DB_TABLE_POS_OrderId,
                            String.valueOf(shelf.getOrder_id()));

                    if (current_price > -1) {
                        values.put(Constants.DB_TABLE_POS_Price,
                                String.valueOf(current_price));
                    }

                    if (current_quantity > -1) {
                        values.put(Constants.DB_TABLE_POS_Quantity,
                                String.valueOf(current_quantity));
                    }

                    if (!current_note.equals("")) {
                        values.put(Constants.DB_TABLE_POS_Notee,
                                String.valueOf(current_note));
                    }

                    if (!current_date.equals("")) {
                        values.put(Constants.DB_TABLE_POS_date,
                                String.valueOf(current_date));
                    }

                    if (DBHelper.isPOSDataListAvailable(
                            Constants.DB_TABLE_POS,
                            new String[]{Constants.DB_TABLE_POS_ProductId,},
                            Constants.DB_TABLE_POS_ProductId
                                    + "="
                                    + "\""
                                    + String.valueOf(current_product
                                    .getProductID())
                                    + "\""
                                    + " AND "
                                    + Constants.DB_TABLE_POS_LocationId
                                    + "="
                                    + "\""
                                    + String.valueOf(current_location
                                    .getProdLocationID()) + "\""
                                    + " AND " + Constants.DB_TABLE_POS_OrderId
                                    + "=" + "\"" + shelf.getOrder_id() + "\"",
                            Constants.DB_TABLE_POS)) {
                        DBAdapter.openDataBase();

                        DBAdapter.db.delete(
                                Constants.DB_TABLE_POS,
                                Constants.DB_TABLE_POS_ProductId
                                        + "="
                                        + "\""
                                        + String.valueOf(current_product
                                        .getProductID())
                                        + "\""
                                        + " AND "
                                        + Constants.DB_TABLE_POS_LocationId
                                        + "="
                                        + "\""
                                        + String.valueOf(current_location
                                        .getProdLocationID()) + "\""
                                        + " AND "
                                        + Constants.DB_TABLE_POS_OrderId + "="
                                        + "\"" + shelf.getOrder_id() + "\"",
                                null);
                        DBAdapter.closeDataBase();
                    }
                    DBAdapter.openDataBase();

                    DBAdapter.db.insert(Constants.DB_TABLE_POS, null, values);

                    DBAdapter.closeDataBase();
                }

            }

        }
    }

    public static boolean isPOSDataListAvailable(String table,
                                                 String[] columns, String whereclause, String orderby) {
        Cursor c = null;
        try {
            DBAdapter.openDataBase();
            c = DBAdapter.db.query(true, table, columns, whereclause, null,
                    null, null, null, null);

            if (c.getCount() == 0) {
                c.close();
                DBAdapter.closeDataBase();
                return false;
            }
            c.moveToFirst();
            c.close();
            DBAdapter.closeDataBase();
            return true;
        } catch (Exception e) {
            System.out.println("Exception:   " + e.toString());
        } finally {
            if (c != null && !c.isClosed())
                c.close();
            DBAdapter.closeDataBase();
        }
        return false;
    }

    public static void saveAnswerData(String table, String[] columns,
                                      QuestionnaireData questionData) {
        synchronized (lock) {
            ArrayList<Answers> ansList = questionData.getAnswersList();
            if (questionData.getQuestionTypeLink() != null
                    && questionData.getQuestionTypeLink().equals("4")) {
                ContentValues values = new ContentValues();
                values.put(columns[1], DBAdapter.encrypt(
                        questionData.getAnswerText(), Constants.EncryptionKey));
                values.put(columns[3], questionData.getDataID());
                values.put(columns[4], questionData.getOrderID());
                values.put(Constants.DB_TABLE_ANSWERS_ENCRYPTED, "true");
                values.put(columns[5], DBAdapter.encrypt(questionData.getMi(),
                        Constants.EncryptionKey));
                DBAdapter.openDataBase(true);
                DBAdapter.db.insert(table, null, values);
                // Log.v("saveAnswerData()", values.toString()+"   "+insert);
                DBAdapter.closeDataBase();
                values = null;
            } else {
                for (int index = 0; index < ansList.size(); index++) {
                    Answers qd = ansList.get(index);
                    // if (qd==null) continue;
                    ContentValues values = new ContentValues();
                    values.put(columns[0], qd.getAnswerID());
                    String ans = qd.getAnswer();
                    ans = DBAdapter.encrypt(ans, Constants.EncryptionKey);
                    values.put(columns[1], ans);
                    values.put(columns[2], qd.getValue());
                    values.put(Constants.DB_TABLE_ANSWERS_ENCRYPTED, "true");
                    values.put(columns[3], questionData.getDataID());
                    values.put(columns[4], questionData.getOrderID());
                    values.put(columns[5], questionData.getAnswerText());
                    values.put(columns[6], qd.getAnswerDisplayCondition());
                    values.put(columns[7], qd.getRank());
                    // values.put(columns[7], index);
                    DBAdapter.closeDataBase();
                    DBAdapter.openDataBase(true);
                    DBAdapter.db.insert(table, null, values);
                    // Log.v("saveAnswerData()",
                    // values.toString()+"   "+insert);
                    values = null;
                }
                DBAdapter.closeDataBase();
            }
        }
    }

    public static boolean isQuestionnaireListAvailable(String table,
                                                       String[] columns, String whereclause, String orderby) {
        synchronized (lock) {
            DBAdapter.openDataBase();
            Cursor c = DBAdapter.db.query(true, table, columns, whereclause,
                    null, null, null, orderby + " ASC", null);

            try {
                if (c.getCount() == 0) {
                    c.close();
                    DBAdapter.closeDataBase();
                    return false;
                }
                c.moveToFirst();
                if (columns.length > 3) {
                    int cindex = c.getColumnIndex(Constants.DB_TABLE_QUESTIONNAIRE_FT);
                    String str = c.getString(cindex);
                    int i = 0;
                    i++;
                }
                c.close();
                DBAdapter.closeDataBase();
                return true;
            } catch (Exception e) {
                System.out.println("Exception:   " + e.toString());
            } finally {
                if (!c.isClosed())
                    c.close();
                DBAdapter.closeDataBase();
            }
            return false;
        }
    }

    public static void updateSurveySelectedBranch(String branch, String id) {
        synchronized (lock) {
            if (branch.contains("Kms)")) {
                branch = branch.replace(
                        branch.substring(branch.indexOf("("),
                                branch.indexOf(")") + 1), "");
            }
            ContentValues values = new ContentValues();
            values.put(Constants.DB_TABLE_JOBLIST_ORDERID, id);
            values.put(Constants.DB_TABLE_JOBLIST_BL, branch);
            DBAdapter.openDataBase();
            DBAdapter.db
                    .update(Constants.DB_TABLE_JOBLIST, values,
                            Constants.DB_TABLE_JOBLIST_ORDERID + "=" + "\""
                                    + id + "\"", null);
            // Log.v("Change status to DB = ", ""+rowaffected);
            DBAdapter.closeDataBase();

            if (DBHelper
                    .isOrderAvailable(Constants.DB_TABLE_JOBLIST,
                            new String[]{Constants.DB_TABLE_JOBLIST_BL,},
                            Constants.DB_TABLE_JOBLIST_ORDERID + "=" + "\""
                                    + id + "\"")) {
            }
        }
    }

    public static void updateOrderLastid(String id, String did) {
        synchronized (lock) {
            DBAdapter.openDataBase();
            ContentValues values = new ContentValues();
            values.put(Constants.DB_TABLE_ORDERS_ORDERID, did);
            values.put(Constants.DB_TABLE_ORDERS_LASTDATAID, did);
            if (DBHelper.isOrderAvailable(Constants.DB_TABLE_ORDERS,
                    new String[]{Constants.DB_TABLE_ORDERS_STATUS,},
                    Constants.DB_TABLE_ORDERS_ORDERID + "=" + "\"" + id + "\"")) {
                DBAdapter.openDataBase();
                DBAdapter.db
                        .update(Constants.DB_TABLE_ORDERS, values,
                                Constants.DB_TABLE_ORDERS_ORDERID
                                        + "=" + "\"" + id + "\"", null);
                DBAdapter.closeDataBase();
            }
        }
    }

    public static void updateOrders(String table, String[] columns, String id,
                                    String val, String time, String did) {
        synchronized (lock) {
            DBAdapter.openDataBase();
            ContentValues values = new ContentValues();
            values.put(columns[0], id);
            values.put(columns[1], val);
            values.put(columns[2], time);
            if (did != null)
                values.put(Constants.DB_TABLE_ORDERS_LASTDATAID, did);
            else {
                did = DBHelper.getOrderLastDataId(Constants.DB_TABLE_ORDERS,
                        new String[]{Constants.DB_TABLE_ORDERS_LASTDATAID,},
                        Constants.DB_TABLE_ORDERS_ORDERID + "=" + "\"" + id
                                + "\"");
                if (did != null)
                    values.put(Constants.DB_TABLE_ORDERS_LASTDATAID, did);
            }
            if (DBHelper.isOrderAvailable(Constants.DB_TABLE_ORDERS,
                    new String[]{Constants.DB_TABLE_ORDERS_STATUS,},
                    Constants.DB_TABLE_ORDERS_ORDERID + "=" + "\"" + id + "\"")) {
                DBAdapter.openDataBase();

                DBAdapter.db.delete(table, Constants.DB_TABLE_ORDERS_ORDERID
                        + "=" + "\"" + id + "\"", null);

                DBAdapter.db.insert(table, null, values);

            } else {
                DBAdapter.openDataBase();
                DBAdapter.db.insert(table, null, values);

            }
            values.clear();
            values.put(columns[0], id);
            values.put(Constants.DB_TABLE_JOBLIST_TS, time);
            values.put(Constants.DB_TABLE_JOBLIST_SN, val);
            DBAdapter.db
                    .update(Constants.DB_TABLE_JOBLIST, values,
                            Constants.DB_TABLE_JOBLIST_ORDERID + "=" + "\""
                                    + id + "\"", null);
            // Log.v("Change status to DB = ", ""+rowaffected);
            DBAdapter.closeDataBase();
        }
    }

    public static void updateOrderOnServerStatusInDB(String id, String val) {
        synchronized (lock) {
            DBAdapter.openDataBase();
            ContentValues values = new ContentValues();

            values.put(Constants.DB_TABLE_JOBLIST_sinprogressonserver, val);
            DBAdapter.db
                    .update(Constants.DB_TABLE_JOBLIST, values,
                            Constants.DB_TABLE_JOBLIST_ORDERID + "=" + "\""
                                    + id + "\"", null);
            // Log.v("Change status to DB = ", ""+rowaffected);
            DBAdapter.closeDataBase();
        }
    }

    public static void updateOrderOnServerStatus(String orderid) {
        for (int i = 0; Orders.getOrders() != null
                && i < Orders.getOrders().size(); i++) {
            if (Orders.getOrders().get(i) != null
                    && Orders.getOrders().get(i).getOrderID() != null
                    && Orders.getOrders().get(i).getOrderID().equals(orderid)) {
                Orders.getOrders().get(i).setIsJobInProgressOnServer("true");
                try {
                    updateOrderOnServerStatusInDB(orderid, "true");
                } catch (Exception ex) {
                    int k = 0;
                    k++;
                }
            }
        }
    }

    public static void deleteRecords(String where) {
        synchronized (lock) {
            DBAdapter.openDataBase();
            DBAdapter.db.delete(Constants.DB_TABLE_QA, null, null);
            DBAdapter.openDataBase();
            DBAdapter.db.delete(Constants.DB_TABLE_SETS, null, null);
            DBAdapter.openDataBase();
            DBAdapter.db.delete(Constants.DB_TABLE_WORKERS, null, null);
            DBAdapter.openDataBase();
            DBAdapter.db.delete(Constants.DB_TABLE_BRANCHES, null, null);
            DBAdapter.openDataBase();
            DBAdapter.db.delete(Constants.DB_TABLE_QUES, null, null);
            DBAdapter.closeDataBase();
            DBAdapter.openDataBase();
            DBAdapter.db.delete(Constants.PNG_FILE_TABLE, null, null);
            DBAdapter.closeDataBase();
        }
    }

    public static void deleteCompletedRecords(String where) {
        synchronized (lock) {
            DBAdapter.openDataBase();
            DBAdapter.db.delete(Constants.DB_TABLE_SUBMITSURVEY, where, null);
            // DBAdapter.openDataBase();
            // DBAdapter.db.delete(Constants.DB_TABLE_QUESTIONNAIRE, where,
            // null);
            DBAdapter.closeDataBase();
        }
    }

    public static void deleteJoblistRecordsFromIP(String where) {
        try {
            synchronized (lock) {
                DBAdapter.openDataBase();
                int i = DBAdapter.db.delete(Constants.DB_TABLE_JOBLIST, where,
                        null);
                int j = DBAdapter.db.delete(
                        Constants.DB_TABLE_CustomOrderFields, where, null);
                DBAdapter.db.delete(Constants.DB_TABLE_QUESTIONNAIRE, where,
                        null);
                DBAdapter.db.delete(Constants.DB_TABLE_ANSWERS, where, null);
                DBAdapter.db.delete(Constants.DB_TABLE_SUBMITSURVEY, where,
                        null);
                DBAdapter.db
                        .delete(Constants.DB_TABLE_SUBMITQUOTA, where, null);
                DBAdapter.db.delete(Constants.DB_TABLE_ORDERS, where, null);
                DBAdapter.db.delete(Constants.DB_TABLE_CustomOrderFields,
                        where, null);
                DBAdapter.closeDataBase();
            }
        } catch (Exception ex) {
        }
    }

    public static void deleteJoblistRecords(String where) {
        try {
            synchronized (lock) {
                DBAdapter.openDataBase();
                int i = DBAdapter.db.delete(Constants.DB_TABLE_JOBLIST, where,
                        null);
                DBAdapter.closeDataBase();
                DBAdapter.openDataBase();
                int j = DBAdapter.db.delete(
                        Constants.DB_TABLE_CustomOrderFields, where, null);
                DBAdapter.closeDataBase();
            }
        } catch (Exception ex) {
        }
    }

    public static void AddOrders(ArrayList<Order> joblistvals) {
        synchronized (lock) {
            ContentValues values = new ContentValues();
            Random r = new Random();
            for (int ordercount = 0; ordercount < joblistvals.size(); ordercount++) {
                values.clear();
                Order order = joblistvals.get(ordercount);
                values.put(Constants.DB_TABLE_JOBLIST_sRegionName,
                        order.getRegionName());
                values.put(Constants.DB_TABLE_JOBLIST_sProjectName,
                        order.getProjectName());
                values.put(Constants.DB_TABLE_JOBLIST_sProjectID,
                        order.getProjectID());
                values.put(Constants.DB_TABLE_JOBLIST_sinprogressonserver,
                        order.getIsJobInProgressOnServer());
                values.put(Constants.DB_TABLE_JOBLIST_AllowShopperToReject,
                        order.getAllowShoppersToRejectJobs());
                values.put(Constants.DB_TABLE_JOBLIST_ORDERID,
                        order.getOrderID());
                values.put(Constants.DB_TABLE_JOBLIST_DATE, order.getDate());
                values.put(Constants.DB_TABLE_JOBLIST_DESC,
                        order.getDescription());
                values.put(Constants.DB_TABLE_JOBLIST_SETNAME,
                        order.getSetName());
                values.put(Constants.DB_TABLE_JOBLIST_BRIEFING,
                        order.getBriefingContent());
                values.put(Constants.DB_TABLE_JOBLIST_SETLINK,
                        order.getSetLink());
                values.put(Constants.DB_TABLE_JOBLIST_CN, order.getClientName());
                values.put(Constants.DB_TABLE_JOBLIST_BFN,
                        order.getBranchFullname());
                values.put(Constants.DB_TABLE_JOBLIST_BN, order.getBranchName());
                values.put(Constants.DB_TABLE_JOBLIST_CITYNAME,
                        order.getCityName());
                values.put(Constants.DB_TABLE_JOBLIST_ADDRESS,
                        order.getAddress());
                values.put(Constants.DB_TABLE_JOBLIST_BP,
                        order.getBranchPhone());
                values.put(Constants.DB_TABLE_JOBLIST_OH,
                        order.getOpeningHours());
                values.put(Constants.DB_TABLE_JOBLIST_TS, order.getTimeStart());
                values.put(Constants.DB_TABLE_JOBLIST_TE, order.getTimeEnd());

                values.put(Constants.DB_TABLE_JOBLIST_SETID, order.getSetID());
                values.put(Constants.DB_TABLE_JOBLIST_BL, order.getBranchLat());
                values.put(Constants.DB_TABLE_JOBLIST_BLNG,
                        order.getBranchLong());
                values.put(Constants.DB_TABLE_JOBLIST_FN, order.getFullname());
                values.put(Constants.DB_TABLE_JOBLIST_JC, order.getCount());
                values.put(Constants.DB_TABLE_JOBLIST_JI, order.getIndex());
                values.put(Constants.DB_TABLE_JOBLIST_BLINK,
                        order.getBranchLink());
                values.put(Constants.DB_TABLE_JOBLIST_MID, order.getMassID());
                values.put(Constants.DB_TABLE_CHECKER_CODE,
                        order.getCheckerCode());
                values.put(Constants.DB_TABLE_CHECKER_LINK,
                        order.getCheckerLink());
                values.put(Constants.DB_TABLE_BRANCH_CODE,
                        order.getBranchCode());
                values.put(Constants.DB_TABLE_PURCHASE_DESCRIPTION,
                        order.getsPurchaseDescription());
                values.put(Constants.DB_TABLE_PURCHASE, order.getsPurchase());
                values.put(Constants.DB_TABLE_JOBLIST_sPurchaseLimit,
                        order.getsPurchaseLimit());
                values.put(
                        Constants.DB_TABLE_JOBLIST_sNonRefundableServicePayment,
                        order.getsNonRefundableServicePayment());
                values.put(Constants.DB_TABLE_JOBLIST_sTransportationPayment,
                        order.getsTransportationPayment());
                values.put(Constants.DB_TABLE_JOBLIST_sCriticismPayment,
                        order.getsCriticismPayment());
                values.put(Constants.DB_TABLE_JOBLIST_sBonusPayment,
                        order.getsBonusPayment());

                DBAdapter.openDataBase();
                int st = isJobAvailable(
                        Constants.DB_TABLE_JOBLIST,
                        new String[]{Constants.DB_TABLE_JOBLIST_SN},
                        Constants.DB_TABLE_JOBLIST_ORDERID + "=" + "\""
                                + order.getOrderID() + "\"",
                        Constants.DB_TABLE_JOBLIST_ORDERID);
                if (st == 1) {
                    deleteJoblistRecords(Constants.DB_TABLE_JOBLIST_ORDERID
                            + "=" + "\"" + order.getOrderID() + "\"");
                    values.put(Constants.DB_TABLE_JOBLIST_SN,
                            order.getStatusName());
                } else if (st == 2) {
                    deleteJoblistRecords(Constants.DB_TABLE_JOBLIST_ORDERID
                            + "=" + "\"" + order.getOrderID() + "\"");
                    values.put(Constants.DB_TABLE_JOBLIST_SN, "Completed");
                    DBHelper.updateOrders(Constants.DB_TABLE_ORDERS,
                            new String[]{Constants.DB_TABLE_ORDERS_ORDERID,
                                    Constants.DB_TABLE_ORDERS_STATUS,
                                    Constants.DB_TABLE_ORDERS_START_TIME,},
                            order.getOrderID(), "Completed", "", null);
                } else if (st > 200)
                    continue;
                else {

                    try {
                        DBAdapter.openDataBase();
                        DBAdapter.db.delete(Constants.DB_TABLE_ORDERS, Constants.DB_TABLE_QUESTIONNAIRE_ORDERID
                                + "="
                                + "\""
                                + order.getOrderID()
                                + "\"", null);
                        DBAdapter.closeDataBase();
                    } catch (Exception ex) {
                        int i = 0;
                        i++;

                    }
                    values.put(Constants.DB_TABLE_JOBLIST_SN,
                            order.getStatusName());
                }
                try {
                    DBAdapter.openDataBase();
                    DBAdapter.db.insert(Constants.DB_TABLE_JOBLIST, null,
                            values);
                    if (order.getCustomFields() != null) {
                        saveCustomFields(order.getCustomFields(),
                                order.getCustomFieldVals(), order.getOrderID());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            DBAdapter.closeDataBase();
            values = null;
        }
    }

    public static void AddSurveysAsOrders(ArrayList<Survey> joblistvals) {
        synchronized (lock) {
            ContentValues values = new ContentValues();
            Random r = new Random();
            for (int ordercount = 0; ordercount < joblistvals.size(); ordercount++) {
                if (joblistvals.get(ordercount).getListAllocations() != null
                        && joblistvals.get(ordercount).getListAllocations()
                        .size() > 0) {
                    int allocations = 0;
                    int doneCount = 0;
                    try {
                        allocations = Integer.valueOf(joblistvals
                                .get(ordercount).getListAllocations().get(0)
                                .getAllocation());
                        doneCount = Integer.valueOf(joblistvals.get(ordercount)
                                .getListAllocations().get(0).getSurveyCount());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    int eligibleCount = 0;
                    for (int j = doneCount; j < (allocations); j++) {
                        values.clear();
                        Survey order = joblistvals.get(ordercount);
                        values.put(Constants.DB_TABLE_JOBLIST_ORDERID, "-"
                                + order.getSurveyID() + "_" + j);
                        values.put(Constants.DB_TABLE_JOBLIST_DATE, "");
                        values.put(Constants.DB_TABLE_JOBLIST_SN, "survey");
                        values.put(Constants.DB_TABLE_JOBLIST_DESC,
                                order.getLandingPage());
                        values.put(Constants.DB_TABLE_JOBLIST_SETNAME,
                                order.getSetLink());
                        values.put(Constants.DB_TABLE_JOBLIST_BRIEFING,
                                order.getRedirectAfter());
                        values.put(Constants.DB_TABLE_JOBLIST_SETLINK,
                                order.getSetLink());
                        values.put(Constants.DB_TABLE_JOBLIST_CN, "");
                        values.put(Constants.DB_TABLE_JOBLIST_BFN,
                                order.getThankYouMessage());
                        values.put(Constants.DB_TABLE_JOBLIST_BN,
                                order.getSurveyName());
                        values.put(Constants.DB_TABLE_JOBLIST_CITYNAME, "");
                        values.put(Constants.DB_TABLE_JOBLIST_ADDRESS, "");
                        values.put(Constants.DB_TABLE_JOBLIST_BP,
                                order.getBranchFullName());
                        values.put(Constants.DB_TABLE_JOBLIST_OH, "");
                        values.put(Constants.DB_TABLE_JOBLIST_TS, "");
                        values.put(Constants.DB_TABLE_JOBLIST_TE, "");

                        values.put(Constants.DB_TABLE_JOBLIST_SETID,
                                order.getSetLink());
                        values.put(Constants.DB_TABLE_JOBLIST_BLNG,
                                order.getBranchLink());
                        values.put(Constants.DB_TABLE_JOBLIST_FN, "");
                        values.put(Constants.DB_TABLE_JOBLIST_JC,
                                order.getCount());
                        values.put(Constants.DB_TABLE_JOBLIST_JI, "");
                        values.put(Constants.DB_TABLE_JOBLIST_BLINK,
                                order.getBranchLink());
                        values.put(Constants.DB_TABLE_JOBLIST_MID, "");
                        values.put(Constants.DB_TABLE_CHECKER_CODE, "");
                        values.put(Constants.DB_TABLE_CHECKER_LINK, "");
                        values.put(Constants.DB_TABLE_BRANCH_CODE, "");
                        values.put(Constants.DB_TABLE_PURCHASE_DESCRIPTION, "");
                        values.put(Constants.DB_TABLE_PURCHASE, "");
                        DBAdapter.openDataBase();

                        String blink = null;
                        int st = isJobAvailable(Constants.DB_TABLE_JOBLIST,
                                new String[]{Constants.DB_TABLE_JOBLIST_SN},
                                Constants.DB_TABLE_JOBLIST_ORDERID + "=" + "\""
                                        + "-" + order.getSurveyID() + "_" + j
                                        + "\"",
                                Constants.DB_TABLE_JOBLIST_ORDERID);

                        if (st == 1) {
                            deleteJoblistRecords(Constants.DB_TABLE_JOBLIST_ORDERID
                                    + "="
                                    + "\""
                                    + "-"
                                    + order.getSurveyID()
                                    + "_" + j + "\"");
                            blink = getBranchLink(Constants.DB_TABLE_JOBLIST_ORDERID
                                    + "="
                                    + "\""
                                    + "-"
                                    + order.getSurveyID()
                                    + "_" + j + "\"");

                            values.put(Constants.DB_TABLE_JOBLIST_SN, "survey");
                        } else if (st == 2) {

                            values.put(Constants.DB_TABLE_JOBLIST_SN,
                                    "Completed");

                        } else if (st == 212 || st == 213) {
                            if (st == 212) {
                                doneCount = Integer.valueOf(joblistvals
                                        .get(ordercount).getListAllocations()
                                        .get(0).getSurveyCount());
                                joblistvals.get(ordercount)
                                        .getListAllocations().get(0)
                                        .setSurveyCount((doneCount + 1) + "");
                            }
                            continue;
                        } else
                            values.put(Constants.DB_TABLE_JOBLIST_SN, "survey");

                        if (blink == null) {

                            values.put(Constants.DB_TABLE_JOBLIST_BL, "");
                        } else
                            values.put(Constants.DB_TABLE_JOBLIST_BL, blink);
                        if (eligibleCount < (allocations - doneCount)) {
                            try {
                                DBAdapter.openDataBase();
                                DBAdapter.db.insert(Constants.DB_TABLE_JOBLIST,
                                        null, values);

                                DBAdapter.closeDataBase();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        eligibleCount++;

                    }
                }
            }
        }

    }


    public static String DeleteCertificateOrderAndAnswers(Cert cert) {
        String orderid = "CC" + cert.getCertificateID() + "SS"
                + cert.getSetID();
        synchronized (lock) {
            DBAdapter.openDataBase();

            String blink = null;
            int st = isJobAvailable(Constants.DB_TABLE_JOBLIST,
                    new String[]{Constants.DB_TABLE_JOBLIST_SN},
                    Constants.DB_TABLE_JOBLIST_ORDERID + "=" + "\"" + orderid
                            + "\"", Constants.DB_TABLE_JOBLIST_ORDERID);

            if (st > 0) {
                deleteJoblistRecords(Constants.DB_TABLE_JOBLIST_ORDERID + "="
                        + "\"" + orderid + "\"");
            }

            try {
                DBAdapter.openDataBase();

                DBAdapter.closeDataBase();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return orderid;
    }

    public static String AddCertificateOrder(Cert cert) {
        String orderid = "CC" + cert.getCertificateID() + "SS"
                + cert.getSetID();
        synchronized (lock) {
            ContentValues values = new ContentValues();
            values.put(Constants.DB_TABLE_JOBLIST_ORDERID, orderid);
            values.put(Constants.DB_TABLE_JOBLIST_DATE, cert.getDateGiven());
            values.put(Constants.DB_TABLE_JOBLIST_SN, "In progress");
            values.put(Constants.DB_TABLE_JOBLIST_DESC, "Certificate");
            values.put(Constants.DB_TABLE_JOBLIST_SETNAME,
                    cert.getCertificateName());
            byte[] data = Base64.decode(cert.getDescription(), Base64.DEFAULT);
            String briefing = "";
            try {
                briefing = new String(data, "UTF-8");
            } catch (UnsupportedEncodingException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            values.put(Constants.DB_TABLE_JOBLIST_BRIEFING, briefing);
            values.put(Constants.DB_TABLE_JOBLIST_SETLINK, cert.getSetID());
            values.put(Constants.DB_TABLE_JOBLIST_CN, cert.getCompanyLink());
            values.put(Constants.DB_TABLE_JOBLIST_BFN, "certificate");
            values.put(Constants.DB_TABLE_JOBLIST_BN, cert.getCertificateName());
            values.put(Constants.DB_TABLE_JOBLIST_CITYNAME, "certificate");
            values.put(Constants.DB_TABLE_JOBLIST_ADDRESS, "certificate");
            values.put(Constants.DB_TABLE_JOBLIST_BP, "Checkertificate");
            values.put(Constants.DB_TABLE_JOBLIST_OH, "00:00");
            values.put(Constants.DB_TABLE_JOBLIST_TS, "00:00");
            values.put(Constants.DB_TABLE_JOBLIST_TE, "00:00");

            values.put(Constants.DB_TABLE_JOBLIST_SETID,
                    cert.getDependencySetLink());
            values.put(Constants.DB_TABLE_JOBLIST_BLNG, "certificate");
            values.put(Constants.DB_TABLE_JOBLIST_FN, "certificate");
            values.put(Constants.DB_TABLE_JOBLIST_JC, 1);
            values.put(Constants.DB_TABLE_JOBLIST_JI, "certificate");
            values.put(Constants.DB_TABLE_JOBLIST_BLINK, "certificate");
            values.put(Constants.DB_TABLE_JOBLIST_MID, "certificate");
            values.put(Constants.DB_TABLE_CHECKER_CODE, "certificate");
            values.put(Constants.DB_TABLE_CHECKER_LINK, "certificate");
            values.put(Constants.DB_TABLE_BRANCH_CODE, "certificate");
            values.put(Constants.DB_TABLE_PURCHASE_DESCRIPTION, "certificate");
            values.put(Constants.DB_TABLE_PURCHASE, "certificate");
            DBAdapter.openDataBase();

            String blink = null;
            int st = isJobAvailable(Constants.DB_TABLE_JOBLIST,
                    new String[]{Constants.DB_TABLE_JOBLIST_SN},
                    Constants.DB_TABLE_JOBLIST_ORDERID + "=" + "\"" + orderid
                            + "\"", Constants.DB_TABLE_JOBLIST_ORDERID);

            if (st > 0) {
                deleteJoblistRecords(Constants.DB_TABLE_JOBLIST_ORDERID + "="
                        + "\"" + orderid + "\"");
            }

            try {
                DBAdapter.openDataBase();
                long rowid = DBAdapter.db.insert(Constants.DB_TABLE_JOBLIST,
                        null, values);

                DBAdapter.closeDataBase();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return orderid;
    }

    public static String currentBranch = null;
    private static int counterr;
    private static Revamped_Loading_Dialog staticpd;
    private static ArrayList<Objects> currentObjectList;

    public static int isJobAvailable(String table, String[] columns,
                                     String whereclause, String orderby)
    // 0-false
    // 1-true
    // 2-already completed
    {
        synchronized (lock) {
            currentBranch = null;

            // DBAdapter.openDataBase();
            Cursor c = DBAdapter.db.query(true, table, columns, whereclause,
                    null, null, null, orderby + " ASC", null);

            try {
                if (c.getCount() == 0) {
                    c.close();
                    DBAdapter.closeDataBase();
                    return 0;
                }
                c.moveToFirst();
                int status_column = c.getColumnIndex(columns[0]);

                String status = (c.getString(status_column));
                if (status.contains("ploaded on"))
                    return 2;
                if (status.contains("ompleted"))
                    return 212;
                if (status.contains("rogress"))
                    return 213;
                c.close();
                DBAdapter.closeDataBase();
                return 1;
            } catch (Exception e) {
                System.out.println("Exception:   " + e.toString());
            } finally {
                if (!c.isClosed())
                    c.close();
                DBAdapter.closeDataBase();
            }
            return 0;
        }
    }

    public static byte[] readFile(String file) throws IOException {
        // Open file
        RandomAccessFile f = new RandomAccessFile(new File(file), "r");
        try {
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength)
                throw new IOException("File size >= 2 GB");
            // Read file and return data
            byte[] data = new byte[length];
            f.readFully(data);
            return data;
        } finally {
            f.close();
        }
    }

    @SuppressLint("NewApi")
    public static void convertToBytesFileTbl(Serializable object, String name)
            throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            byte[] bytes = SerializationUtils.serialize(object);

            File root = CheckerApp.localFilesDir;//android.os.Environment.getExternalStorageDirectory();
            String path = root.getAbsolutePath()
                    + "/mnt/sdcard/Checker_binaries/";
            File dir = new File(path);
            if (dir.exists() == false) {
                dir.mkdirs();
            }

            File file = new File(path, name);
            file.createNewFile();
            {

                FileOutputStream fos = new FileOutputStream(file);
                fos.write(bytes);
                fos.close();
                int i = 0;
                i++;
            }
        }

    }

    public static String readHTMLFromFile(String name) throws IOException {
        //*Don't* hardcode "/sdcard"
        File root = CheckerApp.localFilesDir;//android.os.Environment.getExternalStorageDirectory();
        String path = root.getAbsolutePath()
                + "/mnt/sdcard/Checker_htmls/";
        File dir = new File(path);
        if (dir.exists() == false) {
            return "";
        }

        File file = new File(path, name);

        if (!file.exists()) {
            return "";
        }

//Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
        }

//Find the view by its id
        return text.toString();
    }

    public static String writeOrGetImagePathPreview(String name, Bitmap bmp) {
        File root = CheckerApp.localFilesDir;//android.os.Environment.getExternalStorageDirectory();
        String path = root.getAbsolutePath()
                + "/mnt/sdcard/Checker_htmls/";
        File dir = new File(path);
        if (dir.exists() == false) {
            dir.mkdirs();
        }
        File file = new File(path, name);
        if (file.exists()) {
            return "file://" + file.getAbsolutePath();
        }

        try (FileOutputStream out = new FileOutputStream(file.getAbsolutePath())) {
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "file://" + file.getAbsolutePath();
    }

    public static void writeHTMLToFile(String content, String name) {
        try {
            //File file = new File(Environment.getExternalStorageDirectory() + "/test.txt");
            File root = CheckerApp.localFilesDir;//android.os.Environment.getExternalStorageDirectory();
            String path = root.getAbsolutePath()
                    + "/mnt/sdcard/Checker_htmls/";
            File dir = new File(path);
            if (dir.exists() == false) {
                dir.mkdirs();
            }

            File file = new File(path, name);
            file.createNewFile();
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file);
            writer.append(content);
            writer.flush();
            writer.close();
        } catch (IOException e) {
        }
    }

    private static void downloadFile(String cl, String fileName,
                                     Revamped_Loading_Dialog this_dialog) {
        try {
            String fileURL = getImagePath(cl, fileName);

            File root = CheckerApp.localFilesDir;//android.os.Environment.getExternalStorageDirectory();
            String path = root.getAbsolutePath() + "/mnt/sdcard/CheckerVideos/";
            File dir = new File(path);
            if (dir.exists() == false) {
                dir.mkdirs();
            }

            URL url = new URL(fileURL);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);

            c.setRequestProperty("Accept-Encoding", "identity");
            int size = c.getContentLength();
            if (size < 2000)
                size = 15 * 1024 * 1024;
            try {
                c.connect();
            } catch (Exception e) {
                Log.d("Error....", e.toString());
            }

            File rootFile = new File(path, fileName);
            if (rootFile.exists()) {
                if (this_dialog != null)
                    this_dialog.changePercentage((int) 100, 100);
                return;
            }

            rootFile.createNewFile();

            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream(rootFile);
            byte data[] = new byte[1024];
            long total = 0;
            int count = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                output.write(data, 0, count);
                if (size > 0) {
                    if (total > (10 * 1024 * 1024)) size = 30 * 1024 * 1024;
                    if (total > (20 * 1024 * 1024)) size = 50 * 1024 * 1024;
                    //double percentage=(double)(total/size)*100.0;
                    if (this_dialog != null)
                        this_dialog.changePercentage((int) total, size);
                }
            }
            if (this_dialog != null)
                this_dialog.changePercentage((int) size, size);
            output.flush();
            output.close();
        } catch (Exception e) {
            Log.d("Error....", e.toString());
        }
    }

    public static Object convertFromBytesFileTbl(String name)
            throws IOException {

        File root = CheckerApp.localFilesDir;//android.os.Environment.getExternalStorageDirectory();
        String path = root.getAbsolutePath() + "/mnt/sdcard/Checker_binaries/";
        File file = new File(path, name);
        if (file.exists()) {
            Object obj = SerializationUtils.deserialize(readFile(file
                    .getAbsolutePath()));
            return obj;
        }
        return null;
    }


    private static Object clone(Serializable object) {
        byte[] bytes = SerializationUtils.serialize(object);
        return SerializationUtils.deserialize(bytes);
    }

    public static Object convertArchiveFromBytes(String filename)
            throws IOException {

        File root = CheckerApp.localFilesDir;//android.os.Environment.getExternalStorageDirectory();
        String path = root.getAbsolutePath() + "/mnt/sdcard/Checker_archive_binaries/";
        File file = new File(path, filename);
        if (file.exists()) {
            Object obj = SerializationUtils.deserialize(readFile(file
                    .getAbsolutePath()));
            return obj;
        }
        return null;
    }

    public static void convertArchiveToBytes(Serializable object, String name)
            throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            byte[] bytes = SerializationUtils.serialize(object);
            File root = CheckerApp.localFilesDir;//android.os.Environment.getExternalStorageDirectory();
            String path = root.getAbsolutePath()
                    + "/mnt/sdcard/Checker_archive_binaries/";
            File dir = new File(path);
            if (dir.exists() == false) {
                dir.mkdirs();
            }

            File file = new File(path, name);
            file.createNewFile();
            {

                FileOutputStream fos = new FileOutputStream(file);
                fos.write(bytes);
                fos.close();
                int i = 0;
                i++;
            }
        }

    }

    public static void convertToBytes(Serializable object, String name)
            throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            byte[] bytes = SerializationUtils.serialize(object);

            File root = CheckerApp.localFilesDir;//android.os.Environment.getExternalStorageDirectory();
            String path = root.getAbsolutePath()
                    + "/mnt/sdcard/Checker_binaries/";
            File dir = new File(path);
            if (dir.exists() == false) {
                dir.mkdirs();
            }

            File file = new File(path, "set_" + name + ".txt");
            file.createNewFile();
            {

                FileOutputStream fos = new FileOutputStream(file);
                fos.write(bytes);
                fos.close();
                int i = 0;
                i++;
            }
        }

    }

    public static void convertToBytes(Serializable object, String name,
                                      boolean isset) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            byte[] bytes = SerializationUtils.serialize(object);

            File root = CheckerApp.localFilesDir;// android.os.Environment.getExternalStorageDirectory();
            String path = root.getAbsolutePath()
                    + "/mnt/sdcard/Checker_binaries/";
            File dir = new File(path);
            if (dir.exists() == false) {
                dir.mkdirs();
            }

            File file = new File(path, name);
            file.createNewFile();
            {

                FileOutputStream fos = new FileOutputStream(file);
                fos.write(bytes);
                fos.close();
                int i = 0;
                i++;
            }
        }

    }

    public static Object convertFromBytes(String filename, boolean notSet)
            throws IOException {

        File root = CheckerApp.localFilesDir;//android.os.Environment.getExternalStorageDirectory();
        String path = root.getAbsolutePath() + "/mnt/sdcard/Checker_binaries/";
        File file = new File(path, filename);
        if (file.exists()) {
            Object obj = SerializationUtils.deserialize(readFile(file
                    .getAbsolutePath()));
            return obj;
        }
        return null;
    }

    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }

    public static void convertToBytes(Set set, String setid,
                                      String orderid, boolean onlyForInProgress) throws IOException {
        if (onlyForInProgress) {
            currentObjectList = new ArrayList<Objects>();
            try {
                AddQuestionnaire(null, set.getListObjects(),
                        set.getSetID(), set.getCompanyLink());
            } catch (Exception ex) {
                int i = 0;
                i++;
            }

            set.setListObjectsfromDB(currentObjectList);
            currentObjectList = new ArrayList<Objects>();
        }
        Serializable object = set;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            byte[] bytes = SerializationUtils.serialize(object);

            File root = CheckerApp.localFilesDir;//android.os.Environment.getExternalStorageDirectory();
            String path = root.getAbsolutePath()
                    + "/mnt/sdcard/Checker_binaries/";
            File dir = new File(path);
            if (dir.exists() == false) {
                dir.mkdirs();
            }

            File file = new File(path, "set_" + setid + "order_" + orderid
                    + ".txt");
            file.createNewFile();
            {

                FileOutputStream fos = new FileOutputStream(file);
                fos.write(bytes);
                fos.close();
                int i = 0;
                i++;
            }
        }

    }

    public static String duplicateSet(String setid, String orderid)
            throws IOException {

        File root = CheckerApp.localFilesDir;//android.os.Environment.getExternalStorageDirectory();
        String path = root.getAbsolutePath() + "/mnt/sdcard/Checker_binaries/";
        File src = new File(path, "set_" + setid + ".txt");
        if (src.exists()) {
            File dest = new File(path, "set_" + setid + "order_" + orderid
                    + ".txt");
            copy(src, dest);
            return dest.getAbsolutePath();
        }
        return null;
    }

    public static boolean orderSetExists(String setid, String orderid)
            throws IOException {

        String filename = "set_" + setid + ".txt";
        if (orderid != null)
            filename = "set_" + setid + "order_" + orderid + ".txt";
        File root = CheckerApp.localFilesDir;//android.os.Environment.getExternalStorageDirectory();
        String path = root.getAbsolutePath() + "/mnt/sdcard/Checker_binaries/";
        File file = new File(path, filename);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    public static Object convertFromBytesWithOrder(String setid, String orderid)
            throws IOException {

        String filename = "set_" + setid + ".txt";
        if (orderid != null)
            filename = "set_" + setid + "order_" + orderid + ".txt";
        File root = CheckerApp.localFilesDir;//android.os.Environment.getExternalStorageDirectory();
        String path = root.getAbsolutePath() + "/mnt/sdcard/Checker_binaries/";
        File file = new File(path, filename);
        if (file.exists()) {
            Object obj = SerializationUtils.deserialize(readFile(file
                    .getAbsolutePath()));
            return obj;
        } else {
            filename = "set_" + setid + ".txt";
            file = new File(path, filename);
            if (file.exists()) {
                Object obj = SerializationUtils.deserialize(readFile(file
                        .getAbsolutePath()));
                return obj;
            }
        }
        return null;
    }

    public static Object convertFromBytes(String setid) throws IOException {

        File root = CheckerApp.localFilesDir;// android.os.Environment.getExternalStorageDirectory();
        String path = root.getAbsolutePath() + "/mnt/sdcard/Checker_binaries/";
        File file = new File(path, "set_" + setid + ".txt");
        if (file.exists()) {
            Object obj = SerializationUtils.deserialize(readFile(file
                    .getAbsolutePath()));
            return obj;
        }
        return null;
    }

    public static void convertToBytes(Serializable object, String file_name,
                                      String username) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            byte[] bytes = SerializationUtils.serialize(object);

            File root = CheckerApp.localFilesDir;//android.os.Environment.getExternalStorageDirectory();
            String path = root.getAbsolutePath()
                    + "/mnt/sdcard/Checker_binaries/";
            File dir = new File(path);
            if (dir.exists() == false) {
                dir.mkdirs();
            }

            File file = null;
            if (username == null) {
                file = new File(path, file_name);
            } else {
                file = new File(path, "flow_" + username + file_name + ".txt");
            }
            file.createNewFile();
            {

                FileOutputStream fos = new FileOutputStream(file);
                fos.write(bytes);
                fos.close();
                int i = 0;
                i++;
            }
        }

    }

    public static Object convertFromBytes(String file_name, String username)
            throws IOException {

        File root = CheckerApp.localFilesDir;//android.os.Environment.getExternalStorageDirectory();
        String path = root.getAbsolutePath() + "/mnt/sdcard/Checker_binaries/";
        File file = null;
        if (username != null)
            file = new File(path, "flow_" + username + file_name + ".txt");
        else
            file = new File(path, file_name);

        if (file.exists()) {
            Object obj = SerializationUtils.deserialize(readFile(file
                    .getAbsolutePath()));
            return obj;
        }
        return null;
    }

    public static ArrayList<Set> AddSetss(ArrayList<Set> setvals,
                                          Revamped_Loading_Dialog this_dialog, ArrayList<ListClass> arrayList) {
        synchronized (lock) {
            for (int i = 0; i < arrayList.size(); i++) {
                DBHelper.deletetblLoopDataAgainstSetId(Constants.tblLoops,
                        arrayList.get(i).getSetLink(), arrayList.get(i)
                                .getListName());
                DBHelper.inserttblLoopData(Constants.tblLoops,
                        arrayList.get(i).loopData);
            }

            ArrayList<Set> allCorrectSets = new ArrayList<Set>();
            ContentValues values = new ContentValues();
            DBAdapter.openDataBase("start");
            for (int ordercount = 0; ordercount < setvals.size(); ordercount++) {
                try {
                    values.clear();
                    Set set = setvals.get(ordercount);
                    if (this_dialog != null && set != null
                            && set.getSetName() != null
                            && !set.getSetName().equals("")) {

                        this_dialog.changeMessage(this_dialog.getContext()
                                .getResources()
                                .getString(R.string.aasaving_db_questionnaire)
                                .replace("##", set.getSetName()));
                    }

                    values.put(Constants.DB_TABLE_SETS_SETID, set.getSetID());
                    values.put(Constants.DB_TABLE_SET_NAME, set.getSetName());
                    String briefing = set.getBriefingContent();
                    values.put(Constants.DB_TABLE_SET_COMP_LINK,
                            set.getCompanyLink());
                    values.put(Constants.DB_TABLE_SET_DESC,
                            set.getSetDescription());
                    values.put(Constants.DB_TABLE_SET_CODE, set.getSetCode());
                    values.put(Constants.DB_TABLE_SET_SHOWSAVEANDEXIT,
                            set.getShowSaveAndExitButton());
                    values.put(Constants.DB_TABLE_SET_SHOWTOC, set.getShowTOC());
                    values.put(Constants.DB_TABLE_SET_SHOWPREVIEW,
                            set.getShowPreviewButton());
                    values.put(Constants.DB_TABLE_SET_SHOWBACK,
                            set.getShowBackButton());
                    values.put(Constants.DB_TABLE_SET_CLIENTNAME,
                            set.getClientName());
                    values.put(Constants.DB_TABLE_SET_SHOWFREETEXT,
                            set.getShowFreeTextBox());
                    values.put(
                            Constants.DB_TABLE_SET_ENABLE_NON_ANSWERED_CONFIRMATION,
                            set.getEnableNonansweredConfirmation());
                    values.put(
                            Constants.DB_TABLE_SET_ENABLE_QUESTION_NUMBERING_INFORM,
                            set.getEnableQuestionNumberingInForm());
                    values.put(
                            Constants.DB_TABLE_SET_ENABLE_VALIDATION_QUESTION,
                            set.getEnableValidationQuestion());
                    values.put(
                            Constants.DB_TABLE_SET_ALLOW_CHECKER_TO_SET_FINISHTIME,
                            set.getAllowCheckerToSetFinishTime());
                    values.put(Constants.DB_TABLE_SET_ALLOW_CRIT_FILE_UPLOAD,
                            set.getAllowCritFileUpload());
                    values.put(Constants.DB_TABLE_SET_ALTLANG_ID,
                            set.getAltLangID());
                    values.put(Constants.DB_TABLE_SET_ANSWERS_ACT_AS_SUBMIT,
                            set.getAnswersActAsSubmit());

                    values.put(Constants.DB_TABLE_SET_DefaultPaymentForChecker,
                            set.getDefaultPaymentForChecker());
                    values.put(Constants.DB_TABLE_SET_DefaultBonusPayment,
                            set.getDefaultBonusPayment());
                    values.put(Constants.DB_TABLE_SET_AskForServiceDetails,
                            set.getAskForServiceDetails());
                    values.put(Constants.DB_TABLE_SET_AskForPurchaseDetails,
                            set.getAskForPurchaseDetails());
                    values.put(
                            Constants.DB_TABLE_SET_AskForTransportationDetails,
                            set.getAskForTransportationDetails());
                    values.put(
                            Constants.DB_TABLE_SET_AutoApproveTransportation,
                            set.getAutoApproveTransportation());
                    values.put(Constants.DB_TABLE_SET_AutoApprovePayment,
                            set.getAutoApprovePayment());
                    values.put(Constants.DB_TABLE_SET_AutoApproveService,
                            set.getAutoApproveService());
                    values.put(Constants.DB_TABLE_SET_AutoApprovePurchase,
                            set.getAutoApprovePurchase());
                    values.put(Constants.DB_TABLE_SET_ForceStamping,
                            set.getForceImageStamp());
                    values.put(Constants.DB_TABLE_SETS_AllowCheckerToSetLang,
                            set.getAllowCheckerToSetLang());
                    values.put(
                            Constants.DB_TABLE_SETS_isDifferentLangsAvailable,
                            set.getIsDifferentLangsAvailable());

                    DBAdapter.openDataBase();
                    DBAdapter.db.insert(Constants.DB_TABLE_SETS, null, values);
                    try {
                        AddQuestionnaire(this_dialog, set.getListObjects(),
                                set.getSetID(), set.getCompanyLink());
                    } catch (Exception ex) {
                        int i = 0;
                        i++;
                    }

                    set.setListObjectsfromDB(currentObjectList);
                    currentObjectList = new ArrayList<Objects>();
                    allCorrectSets.add(set);
                    if (set != null && set.getListObjects() != null)
                        set.setObjectCountAtTimeOfSaving(set.getListObjects()
                                .size());
                    convertToBytes(set, set.getSetID());
                    if (this_dialog != null)
                        this_dialog
                                .changeMessage("+"
                                        + this_dialog
                                        .getContext()
                                        .getResources()
                                        .getString(
                                                R.string.aasaving_db_questionnaire)
                                        .replace("##", set.getSetName()));

                    // //////////////////////////////////
                } catch (Exception e) {
                    int i = 0;
                    i++;
                    i++;
                }
            }
            DBAdapter.closeDataBase("end");
            return allCorrectSets;
        }
    }

    public static ArrayList<Survey> AddSurveys(ArrayList<Survey> surveys,
                                               boolean isAddSurveys) {
        synchronized (lock) {
            DeleteAllSurveyRelated();
            if (isAddSurveys)
                AddSurveysAsOrders(surveys);
            ArrayList<Survey> allCorrectSets = new ArrayList<Survey>();
            ContentValues values = new ContentValues();
            for (int ordercount = 0; ordercount < surveys.size(); ordercount++) {
                try {
                    values.clear();
                    Survey set = surveys.get(ordercount);

                    {
                        String where = Constants.DB_TABLE_SURVEY_SurveyID + "="
                                + "\"" + set.getSurveyID() + "\"";
                        deletePOSdata(Constants.DB_TABLE_SURVEY, where);
                    }
                    values.put(Constants.DB_TABLE_SURVEY_SurveyID,
                            set.getSurveyID());
                    values.put(Constants.DB_TABLE_SURVEY_SurveyName,
                            set.getSurveyName());
                    values.put(Constants.DB_TABLE_SURVEY_SetLink,
                            set.getSetLink());
                    values.put(Constants.DB_TABLE_SURVEY_LandingPage,
                            set.getLandingPage());
                    values.put(Constants.DB_TABLE_SURVEY_QuotaReachedMessage,
                            set.getQuotaReachedMessage());
                    values.put(Constants.DB_TABLE_SURVEY_RedirectAfter,
                            set.getRedirectAfter());
                    values.put(Constants.DB_TABLE_SURVEY_SurveyCount,
                            set.getSurveyCount());
                    values.put(Constants.DB_TABLE_SURVEY_TargetQuota,
                            set.getTargetQuota());
                    values.put(Constants.DB_TABLE_SURVEY_ThankYouMessage,
                            set.getThankYouMessage());
                    values.put(Constants.DB_TABLE_SURVEY_Branch_NAME,
                            set.getBranchFullName());

                    DBAdapter.openDataBase();
                    DBAdapter.db
                            .insert(Constants.DB_TABLE_SURVEY, null, values);
                    Survey survey = AddAllocations(surveys.get(ordercount));
                    survey = AddQuotas(surveys.get(ordercount));
                } catch (Exception e) {
                    // TODO: handle exception
                    int i = 0;
                    i++;
                }
            }
            DBAdapter.closeDataBase();
            return allCorrectSets;
        }
    }

    private static void DeleteAllSurveyRelated() {
        deleteAllRecords(Constants.DB_TABLE_ALLOCATIONS);
        deleteAllRecords(Constants.DB_TABLE_QUOTAS);
        deleteAllRecords(Constants.DB_TABLE_SURVEY);
        deleteAllRecords(Constants.DB_TABLE_SURVEYQNA);

    }

    private static Survey AddAllocations(Survey survey) {
        ContentValues values = new ContentValues();
        for (int ordercount = 0; ordercount < survey.getListAllocations()
                .size(); ordercount++) {
            try {
                Allocations allocation = survey.getListAllocations().get(
                        ordercount);
                values.clear();
                {
                    String where = Constants.DB_TABLE_ALLOCATIONS_fsqqaID + "="
                            + "\"" + allocation.getfsqqaID() + "\"";
                    deletePOSdata(Constants.DB_TABLE_ALLOCATIONS, where);
                }

                values.put(Constants.DB_TABLE_ALLOCATIONS_Allocation,
                        allocation.getAllocation());
                values.put(Constants.DB_TABLE_ALLOCATIONS_SurveyID,
                        survey.getSurveyID());
                values.put(Constants.DB_TABLE_ALLOCATIONS_SurveyCount,
                        allocation.getSurveyCount());
                values.put(Constants.DB_TABLE_ALLOCATIONS_fsqqaID,
                        allocation.getfsqqaID());

                DBAdapter.openDataBase();
                DBAdapter.db.insert(Constants.DB_TABLE_ALLOCATIONS, null,
                        values);

            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        DBAdapter.closeDataBase();

        return survey;
    }

    private static Survey AddQuotas(Survey survey) {
        ContentValues values = new ContentValues();
        for (int ordercount = 0; ordercount < survey.getListQuotas().size(); ordercount++) {
            try {

                Quota quota = survey.getListQuotas().get(ordercount);
                {
                    String where = Constants.DB_TABLE_QUOTAS_squoID + "="
                            + "\"" + quota.getsquoID() + "\"";
                    deletePOSdata(Constants.DB_TABLE_QUOTAS, where);
                }
                values.clear();
                values.put(Constants.DB_TABLE_QUOTAS_squoID, quota.getsquoID());
                values.put(Constants.DB_TABLE_QUOTAS_SurveyId,
                        survey.getSurveyID());
                values.put(Constants.DB_TABLE_QUOTAS_QuotaName,
                        quota.getQuotaName());
                values.put(Constants.DB_TABLE_QUOTAS_QuotaMin,
                        quota.getQuotaMin());
                values.put(Constants.DB_TABLE_QUOTAS_DoneCount,
                        quota.getDoneCount());
                values.put(Constants.DB_TABLE_QUOTAS_ActionToTake,
                        quota.getActionToTake());

                DBAdapter.openDataBase();
                DBAdapter.db.insert(Constants.DB_TABLE_QUOTAS, null, values);

                quota = AddSurveyQnAs(quota);

            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        DBAdapter.closeDataBase();

        return survey;
    }

    private static Quota AddSurveyQnAs(Quota quota) {
        ContentValues values = new ContentValues();
        for (int ordercount = 0; ordercount < quota.getqnas().size(); ordercount++) {
            try {
                values.clear();
                SurveyQnA qna = quota.getqnas().get(ordercount);

                {
                    String where = Constants.DB_TABLE_SURVEYQNA_fsqqID + "="
                            + "\"" + qna.getfsqqID() + "\"";
                    deletePOSdata(Constants.DB_TABLE_SURVEYQNA, where);
                }
                values.put(Constants.DB_TABLE_SURVEYQNA_quotaId,
                        quota.getsquoID());
                values.put(Constants.DB_TABLE_SURVEYQNA_AnswerLink,
                        qna.getAnswerLink());
                values.put(Constants.DB_TABLE_SURVEYQNA_DataLink,
                        qna.getDataLink());
                values.put(Constants.DB_TABLE_SURVEYQNA_fsqqID, qna.getfsqqID());

                DBAdapter.openDataBase();
                DBAdapter.db.insert(Constants.DB_TABLE_SURVEYQNA, null, values);

            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        DBAdapter.closeDataBase();

        return quota;
    }

    public static void AddWorkers(ArrayList<Workers> joblistvals, String setid) {
        synchronized (lock) {
            ContentValues values = new ContentValues();
            for (int ordercount = 0; ordercount < joblistvals.size(); ordercount++) {
                values.clear();
                Workers order = joblistvals.get(ordercount);
                values.put(Constants.DB_TABLE_WORKERS_WID, order.getWorkersID());
                values.put(Constants.DB_TABLE_WORKERS_SETID, setid);
                values.put(Constants.DB_TABLE_WORKERS_WORKERID,
                        order.getWorkerID());
                values.put(Constants.DB_TABLE_WORKERS_WN, order.getWorkerName());
                values.put(Constants.DB_TABLE_WORKERS_BL, order.getBranchLink());
                DBAdapter.openDataBase();
                DBAdapter.db.insert(Constants.DB_TABLE_WORKERS, null, values);
            }
        }
    }

    public static void AddBranches(ArrayList<Branches> joblistvals, String setid) {
        synchronized (lock) {
            ContentValues values = new ContentValues();
            for (int ordercount = 0; ordercount < joblistvals.size(); ordercount++) {
                values.clear();
                Branches order = joblistvals.get(ordercount);
                values.put(Constants.DB_TABLE_BRANCHES_BID, order.getBranchID());
                values.put(Constants.DB_TABLE_BRANCHES_SETID, setid);
                values.put(Constants.DB_TABLE_BRANCHES_BN,
                        order.getBranchName());
                values.put(Constants.DB_TABLE_BRANCHES_BLAT,
                        order.getBranchLat());
                values.put(Constants.DB_TABLE_BRANCHES_BLONG,
                        order.getBranchLong());
                DBAdapter.openDataBase();
                DBAdapter.db.insert(Constants.DB_TABLE_BRANCHES, null, values);
            }
        }
    }

    public static void AddProducts(ArrayList<Products> productlistvals,
                                   String setid) {
        synchronized (lock) {
            ContentValues values = new ContentValues();
            if (productlistvals.size() > 0) {
                String where = Constants.DB_TABLE_PRODUCTS_SETID + "=" + "\""
                        + setid + "\"";
                deletePOSdata(Constants.DB_TABLE_PRODUCTS, where);
            }
            for (int ordercount = 0; ordercount < productlistvals.size(); ordercount++) {
                values.clear();
                Products order = productlistvals.get(ordercount);
                values.put(Constants.DB_TABLE_PRODUCTS_AddNote,
                        order.getAddNote());
                values.put(Constants.DB_TABLE_PRODUCTS_Bold, order.getBold());
                values.put(Constants.DB_TABLE_PRODUCTS_CheckExpiration,
                        order.getCheckExpiration());
                values.put(Constants.DB_TABLE_PRODUCTS_CheckPacking,
                        order.getCheckPacking());
                values.put(Constants.DB_TABLE_PRODUCTS_CheckPrice,
                        order.getCheckPrice());
                values.put(Constants.DB_TABLE_PRODUCTS_CheckQuantity,
                        order.getCheckQuantity());
                values.put(Constants.DB_TABLE_PRODUCTS_CheckShelfLevel,
                        order.getCheckShelfLevel());
                values.put(Constants.DB_TABLE_PRODUCTS_ClientLink,
                        order.getClientLink());
                values.put(Constants.DB_TABLE_PRODUCTS_IsActive,
                        order.getIsActive());
                values.put(Constants.DB_TABLE_PRODUCTS_Order, order.getOrder());
                values.put(Constants.DB_TABLE_PRODUCTS_PID,
                        order.getProductID());
                values.put(Constants.DB_TABLE_PRODUCTS_PN,
                        order.getProductName());
                values.put(Constants.DB_TABLE_PRODUCTS_ProductCode,
                        order.getProductCode());
                values.put(Constants.DB_TABLE_PRODUCTS_prop_id_51,
                        order.getprop_id_51());
                values.put(Constants.DB_TABLE_PRODUCTS_prop_id_52,
                        order.getprop_id_52());
                values.put(Constants.DB_TABLE_PRODUCTS_SETID, setid);
                values.put(Constants.DB_TABLE_PRODUCTS_Size, order.getSize());
                values.put(Constants.DB_TABLE_PRODUCTS_TakePicture,
                        order.getTakePicture());
                DBAdapter.openDataBase();
                DBAdapter.db.insert(Constants.DB_TABLE_PRODUCTS, null, values);
            }
        }
    }

    public static void AddProductLocations(
            ArrayList<ProductLocations> productlistvals, String setid) {
        synchronized (lock) {
            if (productlistvals.size() > 0) {
                String where = Constants.DB_TABLE_PRODUCT_LOCATION_SETID + "="
                        + "\"" + setid + "\"";
                deletePOSdata(Constants.DB_TABLE_PRODUCT_LOCATION, where);
            }
            ContentValues values = new ContentValues();
            for (int ordercount = 0; ordercount < productlistvals.size(); ordercount++) {
                values.clear();
                ProductLocations order = productlistvals.get(ordercount);
                values.put(Constants.DB_TABLE_PRODUCT_LOCATION_SETID, setid);
                values.put(Constants.DB_TABLE_PRODUCT_LOCATION_PID,
                        order.getProdLocationID());
                values.put(Constants.DB_TABLE_PRODUCT_LOCATION_location,
                        order.getProductLocation());
                values.put(Constants.DB_TABLE_PRODUCT_CLientLink,
                        order.getClientLink());

                DBAdapter.openDataBase();
                DBAdapter.db.insert(Constants.DB_TABLE_PRODUCT_LOCATION, null,
                        values);
            }
        }
    }

    public static void AddProductProperties(
            ArrayList<ProductProperties> productPropertis, String setid) {
        synchronized (lock) {
            if (productPropertis.size() > 0) {
                String where = Constants.DB_TABLE_PRODUCT_PROPERTIES_SetId
                        + "=" + "\"" + setid + "\"";
                deletePOSdata(Constants.DB_TABLE_PRODUCT_PROPERTIES, where);
            }
            ContentValues values = new ContentValues();
            for (int ordercount = 0; ordercount < productPropertis.size(); ordercount++) {
                values.clear();
                ProductProperties order = productPropertis.get(ordercount);
                values.put(Constants.DB_TABLE_PRODUCT_PROPERTIES_SetId, setid);
                values.put(
                        Constants.DB_TABLE_PRODUCT_PROPERTIES_AllowOtherAddition,
                        order.getAllowOtherAddition());
                values.put(Constants.DB_TABLE_PRODUCT_PROPERTIES_ClientLink,
                        order.getClientLink());
                values.put(Constants.DB_TABLE_PRODUCT_PROPERTIES_IsActive,
                        order.getIsActive());
                values.put(Constants.DB_TABLE_PRODUCT_PROPERTIES_Mandatory,
                        order.getMandatory());
                values.put(Constants.DB_TABLE_PRODUCT_PROPERTIES_Order,
                        order.getOrder());
                values.put(Constants.DB_TABLE_PRODUCT_PROPERTIES_ProdPropID,
                        order.getProdPropID());
                values.put(Constants.DB_TABLE_PRODUCT_PROPERTIES_PropertyName,
                        order.getPropertyName());
                try {
                    // if (productPropertis.get(ordercount) != null
                    // && productPropertis.get(ordercount)
                    // .getPropertyList() == null)
                    {
                        AddPropertyVals(productPropertis.get(ordercount)
                                .getPropertyList(), setid, productPropertis
                                .get(ordercount).getProdPropID());
                    }
                } catch (Exception e) {
                    DBAdapter.closeDataBase();
                }
                DBAdapter.openDataBase();
                DBAdapter.db.insert(Constants.DB_TABLE_PRODUCT_PROPERTIES,
                        null, values);
            }
        }
    }

    public static void AddPropertyVals(
            ArrayList<ProductPropertyVals> productPropertyVals, String setid,
            String propId) {
        synchronized (lock) {
            ContentValues values = new ContentValues();

            DBAdapter.openDataBase();
            for (int ordercount = 0; ordercount < productPropertyVals.size(); ordercount++) {
                values.clear();
                ProductPropertyVals order = productPropertyVals.get(ordercount);
                values.put(
                        Constants.DB_TABLE_PRODUCT_PROPERTIES_Values_Content,
                        order.getContent());
                values.put(
                        Constants.DB_TABLE_PRODUCT_PROPERTIES_Values_IsActive,
                        order.getIsActive());
                values.put(Constants.DB_TABLE_PRODUCT_PROPERTIES_Values_Order,
                        order.getOrder());
                values.put(
                        Constants.DB_TABLE_PRODUCT_PROPERTIES_Values_ProdPropID,
                        propId);
                values.put(
                        Constants.DB_TABLE_PRODUCT_PROPERTIES_Values_ValueID,
                        order.getValueID());
                values.put(
                        Constants.DB_TABLE_PRODUCT_PROPERTIES_Values_PropLink,
                        order.getPropLink());
                values.put(Constants.DB_TABLE_PRODUCT_PROPERTIES_Values_SetId,
                        setid);
                DBAdapter.db.insert(
                        Constants.DB_TABLE_PRODUCT_PROPERTIES_Values, null,
                        values);
            }

            DBAdapter.closeDataBase();
        }
    }

    public static ArrayList<pngItem> getIcons() {
        ArrayList<pngItem> qd = new ArrayList<pngItem>();
        String table_inner = Constants.PNG_FILE_TABLE;
        String[] columns_inner = new String[]{Constants.PNG_FILe_DATAID,
                Constants.PNG_FILe_SETID, Constants.PNG_FILe_MEDIAFILE,};

        DBAdapter.openDataBase();
        Cursor cc = DBAdapter.db.query(true, table_inner, columns_inner, null,
                null, null, null, null, null);
        if (cc.getCount() == 0) {
            cc.close();
            DBAdapter.closeDataBase();
            return null;
        }

        cc.moveToFirst();
        pngItem q = new pngItem();
        do {
            int DataID = cc.getColumnIndex(columns_inner[0]);
            int SETID = cc.getColumnIndex(columns_inner[1]);
            int MEDIAFILE = cc.getColumnIndex(columns_inner[2]);

            q.SetID = (cc.getString(SETID));
            q.DataID = (cc.getString(DataID));
            q.MediaFile = (cc.getString(MEDIAFILE));

            addIcon(qd, q);

            q = new pngItem();
        } while (cc.moveToNext());

        return qd;

    }

    private static void addIcon(ArrayList<pngItem> qd, pngItem q) {
        for (int i = 0; i < qd.size(); i++) {
            if (q.MediaFile == null || qd.get(i).MediaFile != null
                    && qd.get(i).MediaFile.equals(q.MediaFile))
                return;
        }
        qd.add(q);
    }

    public static String getImagePath(String cl, String pfn) {

        StringBuffer sb = new StringBuffer();
        sb.append(Helper.getSystemURL());
        sb.append("/checker-files/media/");
        sb.append(cl);
        sb.append("/pictures/");
        sb.append(pfn);
        return sb.toString();
    }

    public static void AddQuestionaireActual(
            Revamped_Loading_Dialog this_dialog, String setid, String cl,
            Objects order, String objCode, int i, int size) {
        if (DBHelper.currentObjectList == null) {
            DBHelper.currentObjectList = new ArrayList<Objects>();
        }

        String saving_obj = "Saving objects ";
        try {
            saving_obj = this_dialog.getContext().getString(R.string.saving_object);
        } catch (Exception ex) {

        }
        Objects thisItem = ((Objects) clone(order));
        if (order.getPictureFilename() != null) {
            if (order.getPictureFilename().toLowerCase().endsWith(".mp4")
                    || order.getPictureFilename().toLowerCase()
                    .endsWith(".avi")
                    || order.getPictureFilename().toLowerCase()
                    .endsWith(".3gp")) {

                if (this_dialog != null)
                    this_dialog.changeMessage(saving_obj + i + " of " + size + " VID="
                            + order.getPictureFilename());
                downloadFile(cl, order.getPictureFilename(), this_dialog);

            } else {
                if (this_dialog != null)
                    this_dialog.changeMessage(saving_obj + i + " of " + size
                            + " IMG=" + order.getPictureFilename());
                byte[] b = Connector.getImagebyteArr(getImagePath(cl,
                        thisItem.getPictureFilename()), this_dialog);
                thisItem.setPicturedata(b);
                if (this_dialog != null)
                    this_dialog.changePercentage(100, 100);
            }
        }

        if (this_dialog != null) {
            this_dialog.changeMessage("Saving Objects " + i + " of " + size);
        }
        if (objCode != null) {
            thisItem.setObjectCode(objCode);
        }
        DBHelper.currentObjectList.add(thisItem);

        // ContentValues values = new ContentValues();
        //
        // values.clear();
        // try {
        // if (order == null) {
        // return;
        // }
        //
        // values.put(Constants.DB_TABLE_QUES_LoopCondition,
        // order.getLoopCondition());
        // values.put(Constants.DB_TABLE_QUES_RandomizeLoop,
        // order.getRandomizeLoop());
        // values.put(Constants.DB_TABLE_QUES_LoopSource,
        // order.getLoopSource());
        // values.put(Constants.DB_TABLE_QUES_LoopName, order.getLoopName());
        // values.put(Constants.DB_TABLE_QUES_LoopFormat,
        // order.getLoopFormat());
        //
        // values.put(Constants.DB_TABLE_QUES_FONT, order.getFont());
        // values.put(Constants.DB_TABLE_QUES_COLOR, order.getColor());
        // values.put(Constants.DB_TABLE_QUES_SIZE, order.getSize());
        // values.put(Constants.DB_TABLE_QUES_BOLD, order.getBold());
        // values.put(Constants.DB_TABLE_QUES_ITALIC, order.getItalics());
        // values.put(Constants.DB_TABLE_QUES_UL, order.getUnderline());
        //
        // values.put(Constants.DB_TABLE_QUES_ATTACHMENT,
        // order.getAttachment());
        // values.put(Constants.DB_TABLE_QUES_DATAID, order.getDataID());
        // values.put(Constants.DB_TABLE_QUES_OT, order.getObjectType());
        // if (order.getPictureFilename() != null) {
        // byte[] b = Connector.getImagebyteArr(getImagePath(cl,
        // order.getPictureFilename()));
        // values.put(Constants.DB_TABLE_QUES_PFN, b);
        // }
        // values.put(Constants.DB_TABLE_QUES_MAND, order.getMandatory());+++
        // values.put(Constants.DB_TABLE_QUES_CUSTOM_LINK,
        // order.getCustomScaleLink());
        // values.put(Constants.DB_TABLE_QUES_DT, order.getDisplayType());
        // values.put(Constants.DB_TABLE_QUES_AO, order.getAnswerOrdering());
        // values.put(Constants.DB_TABLE_QUES_MT, order.getMiType());
        // values.put(Constants.DB_TABLE_QUES_MM, order.getMiMandatory());
        // values.put(Constants.DB_TABLE_QUES_MD, order.getMiDescription());
        // values.put(Constants.DB_TABLE_QUES_Q, order.getQuestion());
        // values.put(Constants.DB_TABLE_QUES_QD,
        // order.getQuestionDescription());
        // values.put(Constants.DB_TABLE_QUES_QTL, order.getQuestionTypeLink());
        // values.put(Constants.DB_TABLE_QUES_UIT, order.getUseInTOC());
        // values.put(Constants.DB_TABLE_QUES_SID, setid);
        // values.put(Constants.DB_TABLE_QUES_TEXT, order.getText());
        // values.put(Constants.DB_TABLE_QUES_TID, order.getTextID());
        // values.put(Constants.DB_TABLE_QUES_OBJECTDISCONDITINO,
        // order.getObjectDisplayCondition());
        // if (objCode != null) {
        // if (objCode.contains("S8A")) {
        // int i = 0;
        // i++;
        // }
        // values.put(Constants.DB_TABLE_QUES_OBJECTCODE, objCode);
        // } else {
        // values.put(Constants.DB_TABLE_QUES_OBJECTCODE,
        // order.getObjectCode());
        // }
        // if (order.getQuestion() != null
        // && order.getQuestion().toLowerCase().contains("boarding")) {
        // int i = 0;
        // i++;
        // }
        // values.put(Constants.DB_TABLE_QUES_MI_MIN, order.getMiNumberMin());
        // values.put(Constants.DB_TABLE_QUES_MI_MAX, order.getMiNumberMax());
        // values.put(Constants.DB_TABLE_QUES_MAX_ANSWER,
        // order.getMaxAnswersForMultiple());
        // values.put(Constants.DB_TABLE_QUES_MI_FREE_TEXT_MIN,
        // order.getMiFreeTextMinlength());
        // values.put(Constants.DB_TABLE_QUES_MI_FREE_TEXT_MAX,
        // order.getMiFreeTextMaxlength());
        // values.put(Constants.DB_TABLE_QUES_URLCONTENT,
        // order.getUrlContent());
        // values.put(Constants.DB_TABLE_QUES_DESTINATION_OBJECT,
        // order.getDestinationObject());
        // values.put(Constants.DB_TABLE_QUES_URL_ID, order.getUrlID());
        // values.put(Constants.DB_TABLE_QUES_DEST_DESC,
        // order.getDestinationDescription());
        // values.put(Constants.DB_TABLE_QUES_BRANCHINPUTCAPTION,
        // order.getBranchInputCaption());
        // values.put(Constants.DB_TABLE_QUES_WORKERINPUTCAPTION,
        // order.getWorkerInputCaption());
        // values.put(Constants.DB_TABLE_QUES_BranchInputMandatory,
        // order.getBranchInputMandatory());
        // values.put(Constants.DB_TABLE_QUES_WORKERInputMandatory,
        // order.getWorkerInputMandatory());
        // values.put(Constants.DB_TABLE_QUES_DynamicTitlesDefaultAmount,
        // order.getDynamicTitlesDefaultAmount());
        // values.put(Constants.DB_TABLE_QUES_PipingSourceDataLink,
        // order.getPipingSourceDataLink());
        // values.put(Constants.DB_TABLE_QUES_randomTitlesOrder,
        // order.getRandomTitlesOrder());
        // values.put(Constants.DB_TABLE_QUES_questionOrientation,
        // order.getQuestionOrientation());
        // values.put(Constants.DB_TABLE_QUES_groupName, order.getGroupName());
        // values.put(Constants.DB_TABLE_QUES_RandomQuestionrder,
        // order.getRandomQuestionOrder());
        // values.put(Constants.DB_TABLE_QUES_AS, order.getAnswersSource());
        // values.put(Constants.DB_TABLE_QUES_AF, order.getAnswersFormat());
        // values.put(Constants.DB_TABLE_QUES_AC, order.getAnswersCondition());
        //
        // if (order.getQuestion() != null
        // && order.getQuestion().contains("one you smoke")) {
        // int i = 0;
        // i++;
        // }
        // DBAdapter.openDataBase();
        // DBAdapter.db.insert(Constants.DB_TABLE_QUES, null, values);
        // AddQAnswers(order.getListAnswers(), setid, order.getDataID());
        //
        // deleteQuestionRecord(order.getDataID());
        // if (order.altmiDescription != null
        // && order.altmiDescription.size() > 0) {
        // // AddAltMiDescriptions();
        // }
        //
        // if (order.getDataID() != null
        // && order.getDataID().contains("186997")) {
        // int hj = 0;
        // hj++;
        // }
        //
        // if (order.altTexts != null && order.altTexts.size() > 0) {
        // AddAltTexts(setid, order.getDataID(), order.altTexts,
        // order.altQuestionTexts, order.altQuestionDescription,
        // order.altGroupNames);
        // }
        // if (order.getQuestionTitles() != null)
        // AddQTitles(order.getQuestionTitles(), setid, order.getDataID());
        // if (order.getListAutoValues() != null)
        // AddAutoValues(order.getListAutoValues(), setid,
        // order.getDataID());
        // if (order.getQuestionLinks() != null)
        // AddQLinks(order.getQuestionLinks(), setid, order.getDataID());
        // if (order.getQuestionGroups() != null)
        // AddQGroups(order.getQuestionGroups(), setid, order.getDataID());
        // } catch (Exception ex) {
        // int k = 0;
        // k++;
        // }
    }

    private static void AddAltTexts(String setid, String dataID,
                                    ArrayList<AltLangStrings> altText,
                                    ArrayList<AltLangStrings> altQuestionTexts,
                                    ArrayList<AltLangStrings> altQuestionDescription,
                                    ArrayList<AltLangStrings> altGroupNames) {
        synchronized (lock) {
            String where = Constants.DB_TABLE_alt_SetID + "=" + "\"" + setid
                    + "\"" + " AND " + Constants.DB_TABLE_alt_DataID + "="
                    + "\"" + dataID + "\"";
            deletePOSdata(Constants.DB_TABLE_alt_questions, where);
            ContentValues values = new ContentValues();
            int i = 0;
            for (int ordercount = 0; ordercount < altText.size(); ordercount++) {

                values.clear();
                values.put(Constants.DB_TABLE_alt_SetID, setid);
                values.put(Constants.DB_TABLE_alt_DataID, dataID);
                values.put(Constants.DB_TABLE_alt_AltLangID,
                        altText.get(ordercount).AltLngID);
                values.put(Constants.DB_TABLE_alt_QText,
                        altText.get(ordercount).text);
                values.put(Constants.DB_TABLE_alt_Question,
                        altQuestionTexts.get(ordercount).text);
                values.put(Constants.DB_TABLE_alt_QuestionDescription,
                        altQuestionDescription.get(ordercount).text);
                if (altGroupNames.get(ordercount).text != null
                        && altGroupNames.get(ordercount).text.length() > 0) {
                    int ii = 0;
                    ii++;
                }
                values.put(Constants.DB_TABLE_alt_GroupName,
                        altGroupNames.get(ordercount).text);
                DBAdapter.openDataBase();
                try {
                    DBAdapter.db.insert(Constants.DB_TABLE_alt_questions, null,
                            values);
                } catch (Exception e) {
                    Log.d("Exception", "-Exception-");
                }
            }
            int j = 0;
            j++;
        }

    }

    private static void AddAltAnswers(String sid, String did, String aid,
                                      ArrayList<AltLangStrings> altanswers) {
        synchronized (lock) {
            String where = Constants.DB_TABLE_alt_SetID + "=" + "\"" + sid
                    + "\"" + " AND " + Constants.DB_TABLE_alt_DataID + "="
                    + "\"" + did + "\"" + " AND "
                    + Constants.DB_TABLE_alt_AnswerID + "=" + "\"" + aid + "\"";
            deletePOSdata(Constants.DB_TABLE_alt_answers, where);
            ContentValues values = new ContentValues();
            int i = 0;
            for (int ordercount = 0; ordercount < altanswers.size(); ordercount++) {

                values.clear();
                values.put(Constants.DB_TABLE_alt_SetID, sid);
                values.put(Constants.DB_TABLE_alt_DataID, did);
                values.put(Constants.DB_TABLE_alt_AnswerID, aid);
                values.put(Constants.DB_TABLE_alt_AltLangID,
                        altanswers.get(ordercount).AltLngID);
                values.put(Constants.DB_TABLE_alt_Answer,
                        altanswers.get(ordercount).text);
                DBAdapter.openDataBase();
                try {
                    DBAdapter.db.insert(Constants.DB_TABLE_alt_answers, null,
                            values);
                } catch (Exception e) {
                    Log.d("Exception", "-Exception-");
                }
            }
            int j = 0;
            j++;
        }

    }

    private static void AddAltTitles(String sid, String did, String qgtid,
                                     ArrayList<AltLangStrings> altanswers) {
        synchronized (lock) {
            String where = Constants.DB_TABLE_alt_SetID + "=" + "\"" + sid
                    + "\"" + " AND " + Constants.DB_TABLE_alt_DataID + "="
                    + "\"" + did + "\"" + " AND "
                    + Constants.DB_TABLE_alt_qgtid + "=" + "\"" + qgtid + "\"";
            deletePOSdata(Constants.DB_TABLE_alt_TblTitle, where);
            ContentValues values = new ContentValues();
            int i = 0;
            for (int ordercount = 0; ordercount < altanswers.size(); ordercount++) {

                values.clear();
                values.put(Constants.DB_TABLE_alt_SetID, sid);
                values.put(Constants.DB_TABLE_alt_DataID, did);
                values.put(Constants.DB_TABLE_alt_qgtid, qgtid);
                values.put(Constants.DB_TABLE_alt_AltLangID,
                        altanswers.get(ordercount).AltLngID);
                values.put(Constants.DB_TABLE_alt_Title,
                        altanswers.get(ordercount).text);
                DBAdapter.openDataBase();
                try {
                    DBAdapter.db.insert(Constants.DB_TABLE_alt_TblTitle, null,
                            values);
                } catch (Exception e) {
                    Log.d("Exception", "-Exception-");
                }
            }
            int j = 0;
            j++;
        }

    }

    public static void AddQuestionnaire(Revamped_Loading_Dialog this_dialog,
                                        ArrayList<Objects> joblistvals, String setid, String cl) {
        synchronized (lock) {
            for (int ordercount = 0; ordercount < joblistvals.size() - 1; ordercount++) {
                Objects order = joblistvals.get(ordercount);
                Objects questionObject = order;

                Objects questionObjectPrevious = null;

                if (ordercount > 0) {
                    questionObjectPrevious = joblistvals.get(ordercount - 1);
                }

                String data_id = questionObject.getDataID();
                if (data_id.contains("_")) {
                    data_id = data_id.substring(0, data_id.indexOf("_"));
                }
                questionObject.setDataID(data_id);

                if (questionObjectPrevious != null
                        && questionObjectPrevious.getObjectType().equals("2")
                        && questionObject.getListAutoValues() != null
                        && questionObject.getListAutoValues().size() > 0) {
                    AddPageBreak(setid, cl,
                            questionObjectPrevious.getObjectDisplayCondition(), ordercount, joblistvals.size());
                }
                AddQuestionaireActual(this_dialog, setid, cl, questionObject,
                        null, ordercount + 1, joblistvals.size());
                if (questionObject.getQuestionTitles() != null
                        && questionObject.getQuestionLinks() != null) {
                    // ShowAlert(QuestionnaireActivity.this, "SHOW LINKS",
                    // "links", "ok");
                    ArrayList<Objects> currentObject = new ArrayList<Objects>();
                    for (int j = 0; j < questionObject.getQuestionLinks()
                            .size(); j++) {
                        for (int i = 0; i < joblistvals.size(); i++) {
                            if (joblistvals.get(i).getDataID().contains("_")) {
                                joblistvals.get(i).setDataID(
                                        joblistvals
                                                .get(i)
                                                .getDataID()
                                                .substring(
                                                        0,
                                                        joblistvals.get(i)
                                                                .getDataID()
                                                                .indexOf("_")));
                            }
                            if (joblistvals
                                    .get(i)
                                    .getDataID()
                                    .equals(questionObject.getQuestionLinks()
                                            .get(j))) {
                                currentObject.add(joblistvals.get(i));
                                break;
                            }
                        }
                    }

                    if (currentObject != null && currentObject.size() > 0) {
                        ArrayList<Objects> groupList = new ArrayList<Objects>();
                        for (int j = 0; j < currentObject.size(); j++) {
                            for (int i = 0; i < questionObject
                                    .getQuestionTitles().size(); i++) {
                                data_id = questionObject.getDataID();
                                if (data_id.contains("_")) {
                                    data_id = data_id.substring(0,
                                            data_id.indexOf("_"));
                                }
                                if (currentObject.get(j).getDataID()
                                        .contains("_")) {
                                    currentObject
                                            .get(j)
                                            .setDataID(
                                                    currentObject
                                                            .get(j)
                                                            .getDataID()
                                                            .substring(
                                                                    0,
                                                                    currentObject
                                                                            .get(j)
                                                                            .getDataID()
                                                                            .indexOf(
                                                                                    "_")));
                                }
                                currentObject.get(j).setDataID(
                                        currentObject.get(j).getDataID()
                                                .toString()
                                                + "_"
                                                + data_id
                                                + "-"
                                                + questionObject
                                                .getQuestionTitles()
                                                .get(i).getqgtID()
                                                + ";"
                                                + questionObject
                                                .getQuestionTitles()
                                                .get(i).getTitleText());
                                currentObject.get(j).setGroupName(
                                        questionObject.getGroupName());

                                currentObject.get(j).altGroupNames = questionObject.altGroupNames;
                                // setGroupName(
                                // questionObject.getGroupName());
                                //
                                String objCode = questionObject.getObjectCode()
                                        + ";"
                                        + questionObject.getQuestionTitles()
                                        .get(i).getqgtID() + ";"
                                        + currentObject.get(j).getObjectCode();
                                AddQuestionaireActual(this_dialog, setid, cl,
                                        currentObject.get(j), objCode, ordercount + 1, joblistvals.size());
                            }
                        }

                    }

                } else if (questionObject.getQuestionLinks() != null) {
                    if (questionObject.getDataID().equals("131643")) {
                        int k = 0;
                        k++;
                    }
                    // ShowAlert(QuestionnaireActivity.this, "SHOW LINKS",
                    // "links", "ok");
                    ArrayList<Objects> groupList = new ArrayList<Objects>();
                    for (int j = 0; j < questionObject.getQuestionLinks()
                            .size(); j++) {
                        for (int i = 0; i < joblistvals.size(); i++) {
                            data_id = questionObject.getDataID();
                            if (data_id.contains("_")) {
                                data_id = data_id.substring(0,
                                        data_id.indexOf("_"));
                            }
                            if (joblistvals.get(i).getDataID().contains("_")) {
                                joblistvals.get(i).setDataID(
                                        joblistvals
                                                .get(i)
                                                .getDataID()
                                                .substring(
                                                        0,
                                                        joblistvals.get(i)
                                                                .getDataID()
                                                                .indexOf("_")));
                            }
                            if (joblistvals
                                    .get(i)
                                    .getDataID()
                                    .equals(questionObject.getQuestionLinks()
                                            .get(j))) {

                                joblistvals.get(i).setDataID(

                                        joblistvals.get(i).getDataID() + "_" + data_id);

                                String objCode = questionObject.getObjectCode()
                                        + ";0;"
                                        + joblistvals.get(i).getObjectCode();
                                joblistvals.get(i).setGroupName(
                                        questionObject.getGroupName());
                                AddQuestionaireActual(this_dialog, setid, cl,
                                        joblistvals.get(i), objCode, ordercount + 1, joblistvals.size());
                            }
                        }
                    }
                }

            }
        }
    }

    private static void AddPageBreak(String setid, String cl,
                                     String displayCondition, int count, int size) {

        Objects questionObject = new Objects();
        questionObject.setDataID(((int) (Math.random() * 1000)) + "");
        questionObject.setObjectOrder("7");
        questionObject.setObjectDisplayCondition("");
        questionObject.setObjectCode("PB");
        questionObject.setObjectType("1");
        questionObject.setObjectLink("0");
        questionObject.setUseInTOC("0");
        questionObject.setPropertyLinkToInventory("0");
        questionObject.setIndex("12");

        AddQuestionaireActual(null, setid, cl, questionObject, null, count, size);

    }

    private static ArrayList<Titles> randomizeTitles(
            ArrayList<Titles> listTitles) {
        ArrayList<Titles> randomizeList = new ArrayList<Titles>();
        Calendar calendar = Calendar.getInstance();
        System.out.println("Seconds in current minute = "
                + calendar.get(Calendar.SECOND));
        Random randomGenerator = new Random(Calendar.SECOND);
        int size = listTitles.size();
        while (randomizeList.size() != size) {
            int n = randomGenerator.nextInt(listTitles.size());
            randomizeList.add(listTitles.get(n));
            listTitles.remove(listTitles.get(n));
        }

        return randomizeList;
    }

    private static ArrayList<String> randomizeQuestions(
            ArrayList<String> listObjects) {
        ArrayList<String> randomizeList = new ArrayList<String>();
        Calendar calendar = Calendar.getInstance();
        System.out.println("Seconds in current minute = "
                + calendar.get(Calendar.SECOND));
        Random randomGenerator = new Random(Calendar.SECOND);
        int size = listObjects.size();
        while (randomizeList.size() != size) {
            int n = randomGenerator.nextInt(listObjects.size());
            randomizeList.add(listObjects.get(n));
            listObjects.remove(listObjects.get(n));
        }

        return randomizeList;
    }

    public static void AddQAnswers(ArrayList<Answers> joblistvals, String sid,
                                   String did) {
        synchronized (lock) {
            ContentValues values = new ContentValues();
            int i = 0;
            for (int ordercount = 0; ordercount < joblistvals.size(); ordercount++) {

                values.clear();
                Answers order = joblistvals.get(ordercount);
                values.put(Constants.DB_TABLE_QA_SETID, sid);
                values.put(Constants.DB_TABLE_QA_DID, did);
                values.put(Constants.DB_TABLE_QA_AID, order.getAnswerID());
                values.put(Constants.DB_TABLE_QA_ANS, order.getAnswer());
                values.put(Constants.DB_TABLE_QA_VAL, order.getValue());
                values.put(Constants.DB_TABLE_QA_COL, order.getColor());
                values.put(Constants.DB_TABLE_QA_BOLD, order.getBold());
                values.put(Constants.DB_TABLE_QA_ITALIC, order.getItalics());
                values.put(Constants.DB_TABLE_QA_UL, order.getUnderline());
                values.put(Constants.DB_TABLE_QA_CODE, order.getAnswerCode());
                values.put(Constants.DB_TABLE_QA_JUMPTO, order.getJumpTo());
                if (order.getIconName() != null) {
                    saveIconLink(sid, did, order.getIconName());
                }

                values.put(Constants.DB_TABLE_QA_ICONNAME, order.getIconName());
                values.put(Constants.DB_TABLE_QA_DISPLAYCONDITION,
                        order.getAnswerDisplayCondition());
                values.put(Constants.DB_TABLE_QA_HIDEADDITIONALINFO,
                        order.getHideAdditionalInfo());
                values.put(Constants.DB_TABLE_QA_ADDITIONALINFOMANDATORY,
                        order.getAdditionalInfoMandatory());
                values.put(Constants.DB_TABLE_QA_CINDEX, order.index);
                values.put(Constants.DB_TABLE_QA_ClearOtherAnswers,
                        order.getClearOtherAnswers());
                DBAdapter.openDataBase();
                try {
                    DBAdapter.db.insert(Constants.DB_TABLE_QA, null, values);
                } catch (Exception e) {
                    Log.d("Exception", "-Exception-");
                }

                if (order.altanswers != null && order.altanswers.size() > 0) {
                    AddAltAnswers(sid, did, order.getAnswerID(),
                            order.altanswers);
                }
            }
            int j = 0;
            j++;
        }
    }

    private static void saveIconLink(String sid, String did, String iconName) {
        ContentValues values = new ContentValues();
        values.put(Constants.PNG_FILe_SETID, sid);
        values.put(Constants.PNG_FILe_DATAID, did);
        values.put(Constants.PNG_FILe_MEDIAFILE, iconName);
        DBAdapter.openDataBase();
        DBAdapter.db.insert(Constants.PNG_FILE_TABLE, null, values);
        DBAdapter.closeDataBase();
    }

    public static void AddQLinks(ArrayList<String> joblistvals, String sid,
                                 String did) {
        synchronized (lock) {
            String where = Constants.DB_TABLE_QLINKS_SETID + "=" + "\"" + sid
                    + "\"" + " AND " + Constants.DB_TABLE_QLINKS_DataID + "="
                    + "\"" + did + "\"";
            deletePOSdata(Constants.DB_TABLE_QLINKS, where);
            ContentValues values = new ContentValues();
            int i = 0;
            for (int ordercount = 0; ordercount < joblistvals.size(); ordercount++) {

                values.clear();
                String order = joblistvals.get(ordercount);
                values.put(Constants.DB_TABLE_QLINKS_SETID, sid);
                values.put(Constants.DB_TABLE_QLINKS_DataID, did);
                values.put(Constants.DB_TABLE_QLINKS_DataLink, order);
                DBAdapter.openDataBase();
                try {
                    DBAdapter.db
                            .insert(Constants.DB_TABLE_QLINKS, null, values);
                } catch (Exception e) {
                    Log.d("Exception", "-Exception-");
                }
            }
            int j = 0;
            j++;
        }
    }

    public static void AddQGroups(ArrayList<String> joblistvals, String sid,
                                  String did) {
        synchronized (lock) {
            String where = Constants.DB_TABLE_QGROUPS_SETID + "=" + "\"" + sid
                    + "\"" + " AND " + Constants.DB_TABLE_QGROUPS_DataID + "="
                    + "\"" + did + "\"";
            deletePOSdata(Constants.DB_TABLE_QGROUPS, where);
            ContentValues values = new ContentValues();
            int i = 0;
            for (int ordercount = 0; ordercount < joblistvals.size(); ordercount++) {

                values.clear();
                String order = joblistvals.get(ordercount);
                values.put(Constants.DB_TABLE_QGROUPS_SETID, sid);
                values.put(Constants.DB_TABLE_QGROUPS_DataID, did);
                values.put(Constants.DB_TABLE_QGROUPS_DataLink, order);
                DBAdapter.openDataBase();
                try {
                    DBAdapter.db.insert(Constants.DB_TABLE_QGROUPS, null,
                            values);
                } catch (Exception e) {
                    Log.d("Exception", "-Exception-");
                }
            }
            int j = 0;
            j++;
        }
    }

    public static void AddAutoValues(ArrayList<AutoValues> jobautovals,
                                     String sid, String did) {
        synchronized (lock) {
            String where = Constants.DB_TABLE_AUTOVALUES_SetLink + "=" + "\""
                    + sid + "\"" + " AND "
                    + Constants.DB_TABLE_AUTOVALUES_DataLink + "=" + "\"" + did
                    + "\"";
            deletePOSdata(Constants.DB_TABLE_AUTOVALUES, where);
            ContentValues values = new ContentValues();
            int i = 0;
            for (int ordercount = 0; ordercount < jobautovals.size(); ordercount++) {

                values.clear();
                AutoValues order = jobautovals.get(ordercount);
                values.put(Constants.DB_TABLE_AUTOVALUES_avID, order.getAvID());
                values.put(Constants.DB_TABLE_AUTOVALUES_Condition,
                        order.getCondition());
                values.put(Constants.DB_TABLE_AUTOVALUES_Priority,
                        order.getPriority());
                values.put(Constants.DB_TABLE_AUTOVALUES_DataLink, did);
                values.put(Constants.DB_TABLE_AUTOVALUES_SetLink, sid);
                values.put(Constants.DB_TABLE_AUTOVALUES_Value_AnswerCode,
                        order.getValue_AnswerCode());
                DBAdapter.openDataBase();
                try {
                    DBAdapter.db.insert(Constants.DB_TABLE_AUTOVALUES, null,
                            values);
                } catch (Exception e) {
                    Log.d("Exception", "-Exception-");
                }
            }
            int j = 0;
            j++;
        }
    }

    public static void AddQTitles(ArrayList<Titles> joblistvals, String sid,
                                  String did) {
        synchronized (lock) {
            String where = Constants.DB_TABLE_QTITLES_SETID + "=" + "\"" + sid
                    + "\"" + " AND " + Constants.DB_TABLE_QTITLES_DID + "="
                    + "\"" + did + "\"";
            deletePOSdata(Constants.DB_TABLE_QTITLES, where);
            ContentValues values = new ContentValues();
            int i = 0;
            for (int ordercount = 0; ordercount < joblistvals.size(); ordercount++) {

                values.clear();
                Titles order = joblistvals.get(ordercount);
                values.put(Constants.DB_TABLE_QTITLES_SETID, sid);
                values.put(Constants.DB_TABLE_QTITLES_DID, did);
                values.put(Constants.DB_TABLE_QTITLES_DataLink, "");
                values.put(Constants.DB_TABLE_QTITLES_ggtID, order.getqgtID());
                values.put(Constants.DB_TABLE_QTITLES_TitleCode,
                        order.getTitleCode());
                values.put(Constants.DB_TABLE_QTITLES_TitleText,
                        order.getTitleText());
                values.put(Constants.DB_TABLE_QTITLES_DisplayCondition,
                        order.getDisplayCondition());
                DBAdapter.openDataBase();
                try {
                    DBAdapter.db.insert(Constants.DB_TABLE_QTITLES, null,
                            values);
                } catch (Exception e) {
                    Log.d("Exception", "-Exception-");
                }

                if (order.altTitles != null && order.altTitles.size() > 0) {
                    AddAltTitles(sid, did, order.getqgtID(), order.altTitles);
                }
            }
            int j = 0;
            j++;
        }
    }

    public static boolean isSetsRecordsAvailable(String table, String[] columns) {
        synchronized (lock) {
            DBAdapter.openDataBase();
            Cursor c = DBAdapter.db.query(true, table, columns, null, null,
                    null, null, null, null);

            try {
                if (c.getCount() == 0) {
                    c.close();
                    DBAdapter.closeDataBase();
                    return false;
                }
                c.close();
                DBAdapter.closeDataBase();
                return true;
            } catch (Exception e) {
                System.out.println("Exception:   " + e.toString());
            } finally {
                if (!c.isClosed())
                    c.close();
                DBAdapter.closeDataBase();
            }
            return false;
        }
    }

    public static ArrayList<Survey> getSurveyyRecords() {
        synchronized (lock) {

            ArrayList<Survey> surveys = new ArrayList<Survey>();

            // DBAdapter.closeDataBase();
            try {
                String table = Constants.DB_TABLE_SURVEY;
                String[] columns = new String[]{
                        Constants.DB_TABLE_SURVEY_LandingPage,
                        Constants.DB_TABLE_SURVEY_QuotaReachedMessage,
                        Constants.DB_TABLE_SURVEY_RedirectAfter,
                        Constants.DB_TABLE_SURVEY_SetLink,
                        Constants.DB_TABLE_SURVEY_SurveyCount,
                        Constants.DB_TABLE_SURVEY_SurveyID,
                        Constants.DB_TABLE_SURVEY_SurveyName,
                        Constants.DB_TABLE_SURVEY_TargetQuota,
                        Constants.DB_TABLE_SURVEY_ThankYouMessage,
                        Constants.DB_TABLE_SURVEY_Branch_NAME};

                DBAdapter.openDataBase();
                Cursor c = DBAdapter.db.query(true, table, columns, null, null,
                        null, null, null, null);
                if (c.getCount() == 0) {
                    c.close();
                    DBAdapter.closeDataBase();
                    return null;
                }

                c.moveToFirst();
                Survey order = new Survey();
                do {
                    int DB_TABLE_SURVEY_LandingPage = c
                            .getColumnIndex(columns[0]);
                    int DB_TABLE_SURVEY_QuotaReachedMessage = c
                            .getColumnIndex(columns[1]);
                    int DB_TABLE_SURVEY_RedirectAfter = c
                            .getColumnIndex(columns[2]);
                    int DB_TABLE_SURVEY_SetLink = c.getColumnIndex(columns[3]);
                    int DB_TABLE_SURVEY_SurveyCount = c
                            .getColumnIndex(columns[4]);
                    int DB_TABLE_SURVEY_SurveyID = c.getColumnIndex(columns[5]);
                    int DB_TABLE_SURVEY_SurveyName = c
                            .getColumnIndex(columns[6]);
                    int DB_TABLE_SURVEY_TargetQuota = c
                            .getColumnIndex(columns[7]);
                    int DB_TABLE_SURVEY_ThankYouMessage = c
                            .getColumnIndex(columns[8]);
                    int DB_TABLE_SURVEY_branch = c.getColumnIndex(columns[9]);
                    order.setBranchFullname(c.getString(DB_TABLE_SURVEY_branch));
                    order.setLandingPage(c
                            .getString(DB_TABLE_SURVEY_LandingPage));
                    order.setQuotaReachedMessage(c
                            .getString(DB_TABLE_SURVEY_QuotaReachedMessage));
                    order.setRedirectAfter(c
                            .getString(DB_TABLE_SURVEY_RedirectAfter));
                    order.setSetLink(c.getString(DB_TABLE_SURVEY_SetLink));
                    order.setSurveyCount(c
                            .getString(DB_TABLE_SURVEY_SurveyCount));
                    order.setSurveyID(c.getString(DB_TABLE_SURVEY_SurveyID));
                    order.setSurveyName(c.getString(DB_TABLE_SURVEY_SurveyName));
                    order.setTargetQuota(c
                            .getString(DB_TABLE_SURVEY_TargetQuota));
                    order.setThankYouMessage(c
                            .getString(DB_TABLE_SURVEY_ThankYouMessage));
                    surveys.add(order);
                    order = GetAllocations(order);
                    order = GetQuotas(order);
                    order = new Survey();
                } while (c.moveToNext());

                c.close();
                DBAdapter.closeDataBase();

            } catch (Exception ex) {
                int i = 0;
                i++;
            }

            return surveys;
        }
    }

    private static Survey GetQuotas(Survey order) {
        String table_inner = Constants.DB_TABLE_QUOTAS;
        String[] columns_inner = new String[]{
                Constants.DB_TABLE_QUOTAS_DoneCount,
                Constants.DB_TABLE_QUOTAS_QuotaMin,
                Constants.DB_TABLE_QUOTAS_QuotaName,
                Constants.DB_TABLE_QUOTAS_squoID,
                Constants.DB_TABLE_QUOTAS_SurveyId,
                Constants.DB_TABLE_QUOTAS_ActionToTake,};

        DBAdapter.openDataBase();
        Cursor cc = DBAdapter.db.query(
                true,
                table_inner,
                columns_inner,
                Constants.DB_TABLE_QUOTAS_SurveyId + "=" + "\""
                        + order.getSurveyID() + "\"", null, null, null, null,
                null);

        // Cursor cc = DBAdapter.db.query(
        // true,
        // table_inner,
        // columns_inner,null, null, null, null, null,
        // null);
        int count = cc.getCount();
        if (count == 0) {
            cc.close();
            DBAdapter.closeDataBase();
            return order;
        }

        cc.moveToFirst();
        Quota quota = new Quota();
        do {
            int DB_TABLE_QUOTAS_DoneCount = cc.getColumnIndex(columns_inner[0]);
            int DB_TABLE_QUOTAS_QuotaMin = cc.getColumnIndex(columns_inner[1]);
            int DB_TABLE_QUOTAS_QuotaName = cc.getColumnIndex(columns_inner[2]);
            int DB_TABLE_QUOTAS_squoID = cc.getColumnIndex(columns_inner[3]);
            int DB_TABLE_QUOTAS_ActionToTake = cc
                    .getColumnIndex(columns_inner[5]);

            quota.setDoneCount(cc.getString(DB_TABLE_QUOTAS_DoneCount));
            quota.setQuotaMin(cc.getString(DB_TABLE_QUOTAS_QuotaMin));
            quota.setQuotaName(cc.getString(DB_TABLE_QUOTAS_QuotaName));
            quota.setActionToTake(cc.getString(DB_TABLE_QUOTAS_ActionToTake));
            quota.setsquoID(cc.getString(DB_TABLE_QUOTAS_squoID));
            quota.setSurveyLink(order.getSurveyID());

            if (order.getListQuotas() != null) {
                order.setQuotas(quota);
            }

            quota = GetSurveyQnA(quota);
            quota = new Quota();
        } while (cc.moveToNext());

        return order;
    }

    private static Quota GetSurveyQnA(Quota quota) {
        String table_inner = Constants.DB_TABLE_SURVEYQNA;
        String[] columns_inner = new String[]{
                Constants.DB_TABLE_SURVEYQNA_AnswerLink,
                Constants.DB_TABLE_SURVEYQNA_DataLink,
                Constants.DB_TABLE_SURVEYQNA_fsqqID,
                Constants.DB_TABLE_SURVEYQNA_quotaId,};

        DBAdapter.openDataBase();
        Cursor cc = DBAdapter.db.query(
                true,
                table_inner,
                columns_inner,
                Constants.DB_TABLE_SURVEYQNA_quotaId + "=" + "\""
                        + quota.getsquoID() + "\"", null, null, null, null,
                null);
        if (cc.getCount() == 0) {
            cc.close();
            DBAdapter.closeDataBase();
            return quota;
        }
        cc.moveToFirst();
        SurveyQnA surveyQna = new SurveyQnA();
        do {
            int DB_TABLE_SURVEYQNA_AnswerLink = cc
                    .getColumnIndex(columns_inner[0]);
            int DB_TABLE_SURVEYQNA_DataLink = cc
                    .getColumnIndex(columns_inner[1]);
            int DB_TABLE_SURVEYQNA_fsqqID = cc.getColumnIndex(columns_inner[2]);

            surveyQna
                    .setAnswerLink(cc.getString(DB_TABLE_SURVEYQNA_AnswerLink));
            surveyQna.setDataLink(cc.getString(DB_TABLE_SURVEYQNA_DataLink));
            surveyQna.setfsqqID(cc.getString(DB_TABLE_SURVEYQNA_fsqqID));
            if (quota.getqnas() != null) {
                quota.setqna(surveyQna);
            }
            surveyQna = new SurveyQnA();
        } while (cc.moveToNext());

        return quota;
    }

    private static Survey GetAllocations(Survey order) {
        String table_inner = Constants.DB_TABLE_ALLOCATIONS;
        String[] columns_inner = new String[]{
                Constants.DB_TABLE_ALLOCATIONS_Allocation,
                Constants.DB_TABLE_ALLOCATIONS_fsqqaID,
                Constants.DB_TABLE_ALLOCATIONS_SurveyCount,};

        DBAdapter.openDataBase();
        Cursor cc = DBAdapter.db.query(
                true,
                table_inner,
                columns_inner,
                Constants.DB_TABLE_ALLOCATIONS_SurveyID + "=" + "\""
                        + order.getSurveyID() + "\"", null, null, null, null,
                null);
        if (cc.getCount() == 0) {
            cc.close();
            DBAdapter.closeDataBase();
            return order;
        }

        cc.moveToFirst();
        Allocations allocation = new Allocations();
        do {
            int DB_TABLE_ALLOCATIONS_Allocation = cc
                    .getColumnIndex(columns_inner[0]);
            int DB_TABLE_ALLOCATIONS_fsqqaID = cc
                    .getColumnIndex(columns_inner[1]);
            int DB_TABLE_ALLOCATIONS_SurveyCount = cc
                    .getColumnIndex(columns_inner[2]);

            allocation.setAllocation(cc
                    .getString(DB_TABLE_ALLOCATIONS_Allocation));
            allocation.setfsqqaID(cc.getString(DB_TABLE_ALLOCATIONS_fsqqaID));
            allocation.setSurveyCount(cc
                    .getString(DB_TABLE_ALLOCATIONS_SurveyCount));
            if (order.getListAllocations() != null) {
                order.setAllocations(allocation);
            }
            allocation = new Allocations();
        } while (cc.moveToNext());

        return order;
    }

    public static Set getSplitSetsRecords(String table, String[] columns,
                                          String whereclause) {
        synchronized (lock) {
            DBAdapter.openDataBase();
            Cursor c = DBAdapter.db.query(true, table, columns, whereclause,
                    null, null, null, null, null);
            // DBAdapter.closeDataBase();
            try {
                if (c.getCount() == 0) {
                    c.close();
                    DBAdapter.closeDataBase();
                    return null;
                }
                Set order = new Set();
                c.moveToFirst();
                DBAdapter.openDataBase();
                do {
                    int id = c.getColumnIndex(columns[0]);
                    int name = c.getColumnIndex(columns[1]);
                    int cl = c.getColumnIndex(columns[2]);
                    int desc = c.getColumnIndex(columns[3]);
                    int code = c.getColumnIndex(columns[4]);
                    int exitandsave = c.getColumnIndex(columns[5]);
                    int toc = c.getColumnIndex(columns[6]);
                    int preview = c.getColumnIndex(columns[7]);
                    int cn = c.getColumnIndex(columns[8]);
                    int ft = c.getColumnIndex(columns[9]);

                    int setnonansweredconfirmation = c
                            .getColumnIndex(columns[10]);
                    int tosetenablequestionnuminformc = c
                            .getColumnIndex(columns[11]);
                    int enablevalidationques = c.getColumnIndex(columns[12]);
                    int setcheckerfinishtime = c.getColumnIndex(columns[13]);
                    int allowfileupload = c.getColumnIndex(columns[14]);
                    int altlangid = c.getColumnIndex(columns[15]);
                    int answerassubmit = 0;
                    if (columns.length > 16) {
                        answerassubmit = c.getColumnIndex(columns[16]);
                        order.setAnswersActAsSubmit(c.getString(answerassubmit));
                    }

                    order.setSetID(c.getString(id));
                    order.setSetName(c.getString(name));
                    order.setCompanyLink(c.getString(cl));
                    order.setSetDescription(c.getString(desc));
                    order.setSetCode(c.getString(code));
                    order.setShowSaveAndExitButton(c.getString(exitandsave));
                    order.setShowTOC(c.getString(toc));
                    order.setShowPreviewButton(c.getString(preview));
                    order.setClientName(c.getString(cn));
                    order.setShowFreeTextBox(c.getString(ft));
                    order.setEnableNonansweredConfirmation(c
                            .getString(setnonansweredconfirmation));
                    order.setEnableQuestionNumberingInForm(c
                            .getString(tosetenablequestionnuminformc));
                    order.setEnableValidationQuestion(c
                            .getString(enablevalidationques));
                    order.setAllowCheckerToSetFinishTime(c
                            .getString(setcheckerfinishtime));
                    order.setAllowCritFileUpload(c.getString(allowfileupload));
                    order.setAltLangID(c.getString(altlangid));

                    try {

                        order.addListObjectsfromDB(getQuestionnaireRecords(
                                Constants.DB_TABLE_QUES,
                                new String[]{
                                        Constants.DB_TABLE_QUES_FONT,
                                        Constants.DB_TABLE_QUES_COLOR,
                                        Constants.DB_TABLE_QUES_SIZE,
                                        Constants.DB_TABLE_QUES_BOLD,
                                        Constants.DB_TABLE_QUES_ITALIC,
                                        Constants.DB_TABLE_QUES_UL,
                                        Constants.DB_TABLE_QUES_ATTACHMENT,
                                        Constants.DB_TABLE_QUES_DATAID,
                                        Constants.DB_TABLE_QUES_OT,
                                        Constants.DB_TABLE_QUES_PFN,
                                        Constants.DB_TABLE_QUES_MAND,
                                        Constants.DB_TABLE_QUES_DT,
                                        Constants.DB_TABLE_QUES_AO,
                                        Constants.DB_TABLE_QUES_MT,
                                        Constants.DB_TABLE_QUES_MM,
                                        Constants.DB_TABLE_QUES_MD,
                                        Constants.DB_TABLE_QUES_Q,
                                        Constants.DB_TABLE_QUES_QD,
                                        Constants.DB_TABLE_QUES_QTL,
                                        Constants.DB_TABLE_QUES_UIT,
                                        Constants.DB_TABLE_QUES_SID,
                                        Constants.DB_TABLE_QUES_TEXT,
                                        Constants.DB_TABLE_QUES_TID,
                                        Constants.DB_TABLE_QUES_OBJECTDISCONDITINO,
                                        Constants.DB_TABLE_QUES_OBJECTCODE,
                                        Constants.DB_TABLE_QUES_MI_MIN,
                                        Constants.DB_TABLE_QUES_MI_MAX,
                                        Constants.DB_TABLE_QUES_MAX_ANSWER,
                                        Constants.DB_TABLE_QUES_MI_FREE_TEXT_MIN,
                                        Constants.DB_TABLE_QUES_MI_FREE_TEXT_MAX,
                                        Constants.DB_TABLE_QUES_URLCONTENT,
                                        Constants.DB_TABLE_QUES_DESTINATION_OBJECT,
                                        Constants.DB_TABLE_QUES_URL_ID,
                                        Constants.DB_TABLE_QUES_DEST_DESC,
                                        Constants.DB_TABLE_QUES_WORKERINPUTCAPTION,
                                        Constants.DB_TABLE_QUES_BRANCHINPUTCAPTION,
                                        Constants.DB_TABLE_QUES_DynamicTitlesDefaultAmount,
                                        Constants.DB_TABLE_QUES_PipingSourceDataLink,
                                        Constants.DB_TABLE_QUES_randomTitlesOrder,
                                        Constants.DB_TABLE_QUES_questionOrientation,
                                        Constants.DB_TABLE_QUES_groupName,
                                        Constants.DB_TABLE_QUES_RandomQuestionrder,
                                        Constants.DB_TABLE_QUES_CUSTOM_LINK,
                                        Constants.DB_TABLE_QUES_BranchInputMandatory,
                                        Constants.DB_TABLE_QUES_WORKERInputMandatory,
                                        Constants.DB_TABLE_QUES_AS,
                                        Constants.DB_TABLE_QUES_AF,
                                        Constants.DB_TABLE_QUES_AC,
                                        Constants.DB_TABLE_QUES_LoopCondition,
                                        Constants.DB_TABLE_QUES_RandomizeLoop,
                                        Constants.DB_TABLE_QUES_LoopSource,
                                        Constants.DB_TABLE_QUES_LoopName,
                                        Constants.DB_TABLE_QUES_LoopFormat,},
                                Constants.DB_TABLE_SETS_SETID + "=" + "\""
                                        + order.getSetID() + "\"", null, null));
                    } catch (Exception ex) {

                    }
                    order.setListProducts(getProductsRecords(
                            Constants.DB_TABLE_PRODUCTS,
                            new String[]{
                                    Constants.DB_TABLE_PRODUCTS_AddNote,
                                    Constants.DB_TABLE_PRODUCTS_Bold,
                                    Constants.DB_TABLE_PRODUCTS_CheckExpiration,
                                    Constants.DB_TABLE_PRODUCTS_CheckPacking,
                                    Constants.DB_TABLE_PRODUCTS_CheckPrice,
                                    Constants.DB_TABLE_PRODUCTS_CheckQuantity,
                                    Constants.DB_TABLE_PRODUCTS_CheckShelfLevel,
                                    Constants.DB_TABLE_PRODUCTS_ClientLink,
                                    Constants.DB_TABLE_PRODUCTS_IsActive,
                                    Constants.DB_TABLE_PRODUCTS_Order,
                                    Constants.DB_TABLE_PRODUCTS_PID,
                                    Constants.DB_TABLE_PRODUCTS_PN,
                                    Constants.DB_TABLE_PRODUCTS_ProductCode,
                                    Constants.DB_TABLE_PRODUCTS_prop_id_51,
                                    Constants.DB_TABLE_PRODUCTS_prop_id_52,
                                    Constants.DB_TABLE_PRODUCTS_Size,
                                    Constants.DB_TABLE_PRODUCTS_TakePicture},
                            Constants.DB_TABLE_PRODUCTS_SETID + "=" + "\""
                                    + order.getSetID() + "\""));

                    order.setListProductLocations(getProductListRecords(
                            Constants.DB_TABLE_PRODUCT_LOCATION,
                            new String[]{
                                    Constants.DB_TABLE_PRODUCT_LOCATION_PID,
                                    Constants.DB_TABLE_PRODUCT_LOCATION_location,
                                    Constants.DB_TABLE_PRODUCT_CLientLink,},
                            Constants.DB_TABLE_PRODUCT_LOCATION_SETID + "="
                                    + "\"" + order.getSetID() + "\""));

                    order.setListProductProperties(getProductPropertiesRecord(
                            Constants.DB_TABLE_PRODUCT_PROPERTIES,
                            new String[]{
                                    Constants.DB_TABLE_PRODUCT_PROPERTIES_AllowOtherAddition,
                                    Constants.DB_TABLE_PRODUCT_PROPERTIES_ClientLink,
                                    Constants.DB_TABLE_PRODUCT_PROPERTIES_IsActive,
                                    Constants.DB_TABLE_PRODUCT_PROPERTIES_Mandatory,
                                    Constants.DB_TABLE_PRODUCT_PROPERTIES_Order,
                                    Constants.DB_TABLE_PRODUCT_PROPERTIES_ProdPropID,
                                    Constants.DB_TABLE_PRODUCT_PROPERTIES_PropertyName,
                                    Constants.DB_TABLE_PRODUCT_PROPERTIES_AllowOtherAddition},
                            Constants.DB_TABLE_PRODUCT_PROPERTIES_SetId + "="
                                    + "\"" + order.getSetID() + "\""));

                    order.setListWorkers(getWorkersRecords(
                            Constants.DB_TABLE_WORKERS,
                            new String[]{Constants.DB_TABLE_WORKERS_WID,
                                    Constants.DB_TABLE_WORKERS_WORKERID,
                                    Constants.DB_TABLE_WORKERS_WN,
                                    Constants.DB_TABLE_WORKERS_BL,},
                            Constants.DB_TABLE_SETS_SETID + "=" + "\""
                                    + order.getSetID() + "\""));

                    order.setListBranches(getBranchesRecords(
                            Constants.DB_TABLE_BRANCHES,
                            new String[]{Constants.DB_TABLE_BRANCHES_BID,
                                    Constants.DB_TABLE_BRANCHES_BN,
                                    Constants.DB_TABLE_BRANCHES_BLAT,
                                    Constants.DB_TABLE_BRANCHES_BLONG,},

                            Constants.DB_TABLE_SETS_SETID + "=" + "\""
                                    + order.getSetID() + "\""));

                } while (c.moveToNext());
                c.close();
                DBAdapter.closeDataBase();
                // order.currentSurveys = getSurveyRecords();
                return order;
            } catch (Exception e) {
                System.out.println("Exception:   " + e.toString());
            } finally {
                if (!c.isClosed())
                    c.close();
                DBAdapter.closeDataBase();
            }
            return null;
        }
    }

    public static int getQuestionaireObjectsLength(String table,
                                                   String[] columns, String whereclause, String OrderBy, String limit) {
        DBAdapter.openDataBase();
        Cursor c = DBAdapter.db.query(false, table, columns, whereclause, null,
                null, null, OrderBy, null);

        try {
            int count = c.getCount();
            return count;
        } catch (Exception ex) {
        }
        return 0;
    }

    public static Set getSetsReasdcords(Revamped_Loading_Dialog pd, Set oldset,
                                        String table, String[] columns, String whereclause) {
        synchronized (lock) {
            DBAdapter.openDataBase();
            Cursor c = DBAdapter.db.query(true, table, columns, whereclause,
                    null, null, null, null, null);
            // DBAdapter.closeDataBase();
            try {
                if (c.getCount() == 0) {
                    c.close();
                    DBAdapter.closeDataBase();
                    return null;
                }
                Set order = new Set();
                c.moveToFirst();
                DBAdapter.openDataBase();
                do {
                    int id = c.getColumnIndex(columns[0]);
                    int name = c.getColumnIndex(columns[1]);
                    int cl = c.getColumnIndex(columns[2]);
                    int desc = c.getColumnIndex(columns[3]);
                    int code = c.getColumnIndex(columns[4]);
                    int exitandsave = c.getColumnIndex(columns[5]);
                    int toc = c.getColumnIndex(columns[6]);
                    int preview = c.getColumnIndex(columns[7]);
                    int cn = c.getColumnIndex(columns[8]);
                    int ft = c.getColumnIndex(columns[9]);

                    int setnonansweredconfirmation = c
                            .getColumnIndex(columns[10]);
                    int tosetenablequestionnuminformc = c
                            .getColumnIndex(columns[11]);
                    int enablevalidationques = c.getColumnIndex(columns[12]);
                    int setcheckerfinishtime = c.getColumnIndex(columns[13]);
                    int allowfileupload = c.getColumnIndex(columns[14]);
                    int altlangid = c.getColumnIndex(columns[15]);
                    int back = c.getColumnIndex(columns[16]);
                    int answerassubmit = 0;
                    if (columns.length > 17) {
                        answerassubmit = c.getColumnIndex(columns[17]);
                        order.setAnswersActAsSubmit(c.getString(answerassubmit));

                        int DB_TABLE_SET_DefaultPaymentForChecker = c
                                .getColumnIndex(columns[18]);
                        int DB_TABLE_SET_DefaultBonusPayment = c
                                .getColumnIndex(columns[19]);
                        int DB_TABLE_SET_AskForServiceDetails = c
                                .getColumnIndex(columns[20]);
                        int DB_TABLE_SET_AskForPurchaseDetails = c
                                .getColumnIndex(columns[21]);
                        int DB_TABLE_SET_AskForTransportationDetails = c
                                .getColumnIndex(columns[22]);
                        int DB_TABLE_SET_AutoApproveTransportation = c
                                .getColumnIndex(columns[23]);
                        int DB_TABLE_SET_AutoApprovePayment = c
                                .getColumnIndex(columns[24]);
                        int DB_TABLE_SET_AutoApproveService = c
                                .getColumnIndex(columns[25]);
                        int DB_TABLE_SET_AutoApprovePurchase = c
                                .getColumnIndex(columns[26]);
                        int DB_TABLE_SETS_AllowCheckerToSetLang = c
                                .getColumnIndex(columns[27]);
                        int DB_TABLE_SETS_isDifferentLangsAvailable = c
                                .getColumnIndex(columns[28]);

                        order.setAllowCheckerToSetLang(c
                                .getString(DB_TABLE_SETS_AllowCheckerToSetLang));
                        order.setIsDifferentLangsAvailable(c
                                .getString(DB_TABLE_SETS_isDifferentLangsAvailable));

                        order.setDefaultPaymentForChecker(c
                                .getString(DB_TABLE_SET_DefaultPaymentForChecker));
                        order.setDefaultBonusPayment(c
                                .getString(DB_TABLE_SET_DefaultBonusPayment));
                        order.setAskForServiceDetails(c
                                .getString(DB_TABLE_SET_AskForServiceDetails));
                        order.setAskForPurchaseDetails(c
                                .getString(DB_TABLE_SET_AskForPurchaseDetails));
                        order.setAskForTransportationDetails(c
                                .getString(DB_TABLE_SET_AskForTransportationDetails));
                        order.setAutoApproveTransportation(c
                                .getString(DB_TABLE_SET_AutoApproveTransportation));
                        order.setAutoApprovePayment(c
                                .getString(DB_TABLE_SET_AutoApprovePayment));
                        order.setAutoApproveService(c
                                .getString(DB_TABLE_SET_AutoApproveService));
                        order.setAutoApprovePurchase(c
                                .getString(DB_TABLE_SET_AutoApprovePurchase));

                    }
                    order.setShowBackButton(c.getString(back));
                    order.setSetID(c.getString(id));
                    order.setSetName(c.getString(name));
                    order.setCompanyLink(c.getString(cl));
                    order.setSetDescription(c.getString(desc));
                    order.setSetCode(c.getString(code));
                    order.setShowSaveAndExitButton(c.getString(exitandsave));
                    order.setShowTOC(c.getString(toc));
                    order.setShowPreviewButton(c.getString(preview));
                    order.setClientName(c.getString(cn));
                    order.setShowFreeTextBox(c.getString(ft));
                    order.setEnableNonansweredConfirmation(c
                            .getString(setnonansweredconfirmation));
                    order.setEnableQuestionNumberingInForm(c
                            .getString(tosetenablequestionnuminformc));
                    order.setEnableValidationQuestion(c
                            .getString(enablevalidationques));
                    order.setAllowCheckerToSetFinishTime(c
                            .getString(setcheckerfinishtime));
                    order.setAllowCritFileUpload(c.getString(allowfileupload));
                    order.setAltLangID(c.getString(altlangid));

                    // try {
                    counterr = 0;
                    staticpd = pd;
                    int length = getQuestionaireObjectsLength(
                            Constants.DB_TABLE_QUES,
                            new String[]{
                                    Constants.DB_TABLE_QUES_FONT,
                                    Constants.DB_TABLE_QUES_COLOR,
                                    Constants.DB_TABLE_QUES_SIZE,
                                    Constants.DB_TABLE_QUES_BOLD,
                                    Constants.DB_TABLE_QUES_ITALIC,
                                    Constants.DB_TABLE_QUES_UL,
                                    Constants.DB_TABLE_QUES_ATTACHMENT,
                                    Constants.DB_TABLE_QUES_DATAID,
                                    Constants.DB_TABLE_QUES_OT,
                                    Constants.DB_TABLE_QUES_PFN,
                                    Constants.DB_TABLE_QUES_MAND,
                                    Constants.DB_TABLE_QUES_DT,
                                    Constants.DB_TABLE_QUES_AO,
                                    Constants.DB_TABLE_QUES_MT,
                                    Constants.DB_TABLE_QUES_MM,
                                    Constants.DB_TABLE_QUES_MD,
                                    Constants.DB_TABLE_QUES_Q,
                                    Constants.DB_TABLE_QUES_QD,
                                    Constants.DB_TABLE_QUES_QTL,
                                    Constants.DB_TABLE_QUES_UIT,
                                    Constants.DB_TABLE_QUES_SID,
                                    Constants.DB_TABLE_QUES_TEXT,
                                    Constants.DB_TABLE_QUES_TID,
                                    Constants.DB_TABLE_QUES_OBJECTDISCONDITINO,
                                    Constants.DB_TABLE_QUES_OBJECTCODE,
                                    Constants.DB_TABLE_QUES_MI_MIN,
                                    Constants.DB_TABLE_QUES_MI_MAX,
                                    Constants.DB_TABLE_QUES_MAX_ANSWER,
                                    Constants.DB_TABLE_QUES_MI_FREE_TEXT_MIN,
                                    Constants.DB_TABLE_QUES_MI_FREE_TEXT_MAX,
                                    Constants.DB_TABLE_QUES_URLCONTENT,
                                    Constants.DB_TABLE_QUES_DESTINATION_OBJECT,
                                    Constants.DB_TABLE_QUES_URL_ID,
                                    Constants.DB_TABLE_QUES_DEST_DESC,
                                    Constants.DB_TABLE_QUES_WORKERINPUTCAPTION,
                                    Constants.DB_TABLE_QUES_BRANCHINPUTCAPTION,
                                    Constants.DB_TABLE_QUES_DynamicTitlesDefaultAmount,
                                    Constants.DB_TABLE_QUES_PipingSourceDataLink,
                                    Constants.DB_TABLE_QUES_randomTitlesOrder,
                                    Constants.DB_TABLE_QUES_questionOrientation,
                                    Constants.DB_TABLE_QUES_groupName,
                                    Constants.DB_TABLE_QUES_RandomQuestionrder,
                                    Constants.DB_TABLE_QUES_WORKERInputMandatory,
                                    Constants.DB_TABLE_QUES_BranchInputMandatory,
                                    Constants.DB_TABLE_QUES_AS,
                                    Constants.DB_TABLE_QUES_AF,
                                    Constants.DB_TABLE_QUES_AC,

                                    Constants.DB_TABLE_QUES_LoopCondition,
                                    Constants.DB_TABLE_QUES_RandomizeLoop,
                                    Constants.DB_TABLE_QUES_LoopSource,
                                    Constants.DB_TABLE_QUES_LoopName,
                                    Constants.DB_TABLE_QUES_LoopFormat,},
                            Constants.DB_TABLE_SETS_SETID + "=" + "\""
                                    + order.getSetID() + "\"", null, null);
                    int count = 0;
                    String nxt = "";
                    do {

                        ArrayList<Objects> tmbObjs = getQuestionnaireRecords(
                                Constants.DB_TABLE_QUES,
                                new String[]{
                                        Constants.DB_TABLE_QUES_FONT,
                                        Constants.DB_TABLE_QUES_COLOR,
                                        Constants.DB_TABLE_QUES_SIZE,
                                        Constants.DB_TABLE_QUES_BOLD,
                                        Constants.DB_TABLE_QUES_ITALIC,
                                        Constants.DB_TABLE_QUES_UL,
                                        Constants.DB_TABLE_QUES_ATTACHMENT,
                                        Constants.DB_TABLE_QUES_DATAID,
                                        Constants.DB_TABLE_QUES_OT,
                                        Constants.DB_TABLE_QUES_PFN,
                                        Constants.DB_TABLE_QUES_MAND,
                                        Constants.DB_TABLE_QUES_DT,
                                        Constants.DB_TABLE_QUES_AO,
                                        Constants.DB_TABLE_QUES_MT,
                                        Constants.DB_TABLE_QUES_MM,
                                        Constants.DB_TABLE_QUES_MD,
                                        Constants.DB_TABLE_QUES_Q,
                                        Constants.DB_TABLE_QUES_QD,
                                        Constants.DB_TABLE_QUES_QTL,
                                        Constants.DB_TABLE_QUES_UIT,
                                        Constants.DB_TABLE_QUES_SID,
                                        Constants.DB_TABLE_QUES_TEXT,
                                        Constants.DB_TABLE_QUES_TID,
                                        Constants.DB_TABLE_QUES_OBJECTDISCONDITINO,
                                        Constants.DB_TABLE_QUES_OBJECTCODE,
                                        Constants.DB_TABLE_QUES_MI_MIN,
                                        Constants.DB_TABLE_QUES_MI_MAX,
                                        Constants.DB_TABLE_QUES_MAX_ANSWER,
                                        Constants.DB_TABLE_QUES_MI_FREE_TEXT_MIN,
                                        Constants.DB_TABLE_QUES_MI_FREE_TEXT_MAX,
                                        Constants.DB_TABLE_QUES_URLCONTENT,
                                        Constants.DB_TABLE_QUES_DESTINATION_OBJECT,
                                        Constants.DB_TABLE_QUES_URL_ID,
                                        Constants.DB_TABLE_QUES_DEST_DESC,
                                        Constants.DB_TABLE_QUES_WORKERINPUTCAPTION,
                                        Constants.DB_TABLE_QUES_BRANCHINPUTCAPTION,
                                        Constants.DB_TABLE_QUES_DynamicTitlesDefaultAmount,
                                        Constants.DB_TABLE_QUES_PipingSourceDataLink,
                                        Constants.DB_TABLE_QUES_randomTitlesOrder,
                                        Constants.DB_TABLE_QUES_questionOrientation,
                                        Constants.DB_TABLE_QUES_groupName,
                                        Constants.DB_TABLE_QUES_RandomQuestionrder,
                                        Constants.DB_TABLE_QUES_CUSTOM_LINK,
                                        Constants.DB_TABLE_QUES_WORKERInputMandatory,
                                        Constants.DB_TABLE_QUES_BranchInputMandatory,
                                        Constants.DB_TABLE_QUES_AS,
                                        Constants.DB_TABLE_QUES_AF,
                                        Constants.DB_TABLE_QUES_AC,

                                        Constants.DB_TABLE_QUES_LoopCondition,
                                        Constants.DB_TABLE_QUES_RandomizeLoop,
                                        Constants.DB_TABLE_QUES_LoopSource,
                                        Constants.DB_TABLE_QUES_LoopName,
                                        Constants.DB_TABLE_QUES_LoopFormat,},
                                Constants.DB_TABLE_SETS_SETID + "=" + "\""
                                        + order.getSetID() + "\"", null, nxt
                                        + "60");
                        if (tmbObjs.size() > 0) {
                            order.addListObjectsfromDB(tmbObjs);
                            count += 60;
                            nxt = count + " , ";
                            length = tmbObjs.size();
                        } else {
                            // order.addListObjectsfromDB(tmbObjs);

                        }
                        if (tmbObjs.size() < 60)
                            length = 0;
                    } while (length > 0);

                    // } catch (Exception ex) {
                    //
                    // }
                    if (oldset == null)
                        order.setListProducts(getProductsRecords(
                                Constants.DB_TABLE_PRODUCTS,
                                new String[]{
                                        Constants.DB_TABLE_PRODUCTS_AddNote,
                                        Constants.DB_TABLE_PRODUCTS_Bold,
                                        Constants.DB_TABLE_PRODUCTS_CheckExpiration,
                                        Constants.DB_TABLE_PRODUCTS_CheckPacking,
                                        Constants.DB_TABLE_PRODUCTS_CheckPrice,
                                        Constants.DB_TABLE_PRODUCTS_CheckQuantity,
                                        Constants.DB_TABLE_PRODUCTS_CheckShelfLevel,
                                        Constants.DB_TABLE_PRODUCTS_ClientLink,
                                        Constants.DB_TABLE_PRODUCTS_IsActive,
                                        Constants.DB_TABLE_PRODUCTS_Order,
                                        Constants.DB_TABLE_PRODUCTS_PID,
                                        Constants.DB_TABLE_PRODUCTS_PN,
                                        Constants.DB_TABLE_PRODUCTS_ProductCode,
                                        Constants.DB_TABLE_PRODUCTS_prop_id_51,
                                        Constants.DB_TABLE_PRODUCTS_prop_id_52,
                                        Constants.DB_TABLE_PRODUCTS_Size,
                                        Constants.DB_TABLE_PRODUCTS_TakePicture},
                                Constants.DB_TABLE_PRODUCTS_SETID + "=" + "\""
                                        + order.getSetID() + "\""));

                    if (oldset == null)
                        order.setListProductLocations(getProductListRecords(
                                Constants.DB_TABLE_PRODUCT_LOCATION,
                                new String[]{
                                        Constants.DB_TABLE_PRODUCT_LOCATION_PID,
                                        Constants.DB_TABLE_PRODUCT_LOCATION_location,
                                        Constants.DB_TABLE_PRODUCT_CLientLink,},
                                Constants.DB_TABLE_PRODUCT_LOCATION_SETID + "="
                                        + "\"" + order.getSetID() + "\""));

                    if (oldset == null)
                        order.setListProductProperties(getProductPropertiesRecord(
                                Constants.DB_TABLE_PRODUCT_PROPERTIES,
                                new String[]{
                                        Constants.DB_TABLE_PRODUCT_PROPERTIES_AllowOtherAddition,
                                        Constants.DB_TABLE_PRODUCT_PROPERTIES_ClientLink,
                                        Constants.DB_TABLE_PRODUCT_PROPERTIES_IsActive,
                                        Constants.DB_TABLE_PRODUCT_PROPERTIES_Mandatory,
                                        Constants.DB_TABLE_PRODUCT_PROPERTIES_Order,
                                        Constants.DB_TABLE_PRODUCT_PROPERTIES_ProdPropID,
                                        Constants.DB_TABLE_PRODUCT_PROPERTIES_PropertyName,
                                        Constants.DB_TABLE_PRODUCT_PROPERTIES_AllowOtherAddition},
                                Constants.DB_TABLE_PRODUCT_PROPERTIES_SetId
                                        + "=" + "\"" + order.getSetID() + "\""));

                    if (oldset == null)
                        order.setListWorkers(getWorkersRecords(
                                Constants.DB_TABLE_WORKERS, new String[]{
                                        Constants.DB_TABLE_WORKERS_WID,
                                        Constants.DB_TABLE_WORKERS_WORKERID,
                                        Constants.DB_TABLE_WORKERS_WN,
                                        Constants.DB_TABLE_WORKERS_BL,},
                                Constants.DB_TABLE_SETS_SETID + "=" + "\""
                                        + order.getSetID() + "\""));

                    if (oldset == null)
                        order.setListBranches(getBranchesRecords(
                                Constants.DB_TABLE_BRANCHES, new String[]{
                                        Constants.DB_TABLE_BRANCHES_BID,
                                        Constants.DB_TABLE_BRANCHES_BN,
                                        Constants.DB_TABLE_BRANCHES_BLAT,
                                        Constants.DB_TABLE_BRANCHES_BLONG,},
                                Constants.DB_TABLE_SETS_SETID + "=" + "\""
                                        + order.getSetID() + "\""));

                } while (c.moveToNext());
                c.close();
                DBAdapter.closeDataBase();
                // order.currentSurveys = getSurveyRecords();
                validationSets.setOrRemoveSets(order);
                return order;
            } catch (Exception e) {
                System.out.println("Exception:   " + e.toString());
            } finally {
                if (!c.isClosed())
                    c.close();
                DBAdapter.closeDataBase();
            }
            return null;
        }
    }

    public static ArrayList<String> getAllSets(String table, String[] columns) {
        synchronized (lock) {
            DBAdapter.openDataBase();
            Cursor c = DBAdapter.db.query(true, table, columns, null, null,
                    null, null, null, null);
            // DBAdapter.closeDataBase();
            try {
                if (c.getCount() == 0) {
                    c.close();
                    DBAdapter.closeDataBase();
                    return new ArrayList<String>();
                }
                c.moveToFirst();
                DBAdapter.openDataBase();
                ArrayList<String> allsets = new ArrayList<String>();
                do {
                    int id = c.getColumnIndex(columns[0]);
                    allsets.add(c.getString(id));

                } while (c.moveToNext());
                c.close();
                DBAdapter.closeDataBase();
                return allsets;
            } catch (Exception e) {
                System.out.println("Exception:   " + e.toString());
            } finally {
                if (!c.isClosed())
                    c.close();
                DBAdapter.closeDataBase();
            }
            return null;
        }
    }

    public static Set getSetsRecordForsadJobDetail(String table,
                                                   String[] columns, String whereclause) {
        synchronized (lock) {
            DBAdapter.openDataBase();
            Cursor c = DBAdapter.db.query(true, table, columns, whereclause,
                    null, null, null, null, null);
            // DBAdapter.closeDataBase();
            try {
                if (c.getCount() == 0) {
                    c.close();
                    DBAdapter.closeDataBase();
                    return null;
                }
                Set order = new Set();
                c.moveToFirst();
                DBAdapter.openDataBase();
                do {
                    int id = c.getColumnIndex(columns[0]);
                    int name = c.getColumnIndex(columns[1]);
                    int cl = c.getColumnIndex(columns[2]);
                    int desc = c.getColumnIndex(columns[3]);
                    int code = c.getColumnIndex(columns[4]);
                    int exitandsave = c.getColumnIndex(columns[5]);
                    int toc = c.getColumnIndex(columns[6]);
                    int preview = c.getColumnIndex(columns[7]);
                    int cn = c.getColumnIndex(columns[8]);
                    int ft = c.getColumnIndex(columns[9]);

                    int setnonansweredconfirmation = c
                            .getColumnIndex(columns[10]);
                    int tosetenablequestionnuminformc = c
                            .getColumnIndex(columns[11]);
                    int enablevalidationques = c.getColumnIndex(columns[12]);
                    int setcheckerfinishtime = c.getColumnIndex(columns[13]);
                    int allowfileupload = c.getColumnIndex(columns[14]);
                    int altlangid = c.getColumnIndex(columns[15]);
                    int back = c.getColumnIndex(columns[16]);
                    int answerassubmit = 0;
                    if (columns.length > 17) {
                        answerassubmit = c.getColumnIndex(columns[17]);
                        order.setAnswersActAsSubmit(c.getString(answerassubmit));

                        int DB_TABLE_SET_DefaultPaymentForChecker = c
                                .getColumnIndex(columns[18]);
                        int DB_TABLE_SET_DefaultBonusPayment = c
                                .getColumnIndex(columns[19]);
                        int DB_TABLE_SET_AskForServiceDetails = c
                                .getColumnIndex(columns[20]);
                        int DB_TABLE_SET_AskForPurchaseDetails = c
                                .getColumnIndex(columns[21]);
                        int DB_TABLE_SET_AskForTransportationDetails = c
                                .getColumnIndex(columns[22]);
                        int DB_TABLE_SET_AutoApproveTransportation = c
                                .getColumnIndex(columns[23]);
                        int DB_TABLE_SET_AutoApprovePayment = c
                                .getColumnIndex(columns[24]);
                        int DB_TABLE_SET_AutoApproveService = c
                                .getColumnIndex(columns[25]);
                        int DB_TABLE_SET_AutoApprovePurchase = c
                                .getColumnIndex(columns[26]);
                        int DB_TABLE_SETS_AllowCheckerToSetLang = c
                                .getColumnIndex(columns[27]);
                        int DB_TABLE_SETS_isDifferentLangsAvailable = c
                                .getColumnIndex(columns[28]);

                        order.setAllowCheckerToSetLang(c
                                .getString(DB_TABLE_SETS_AllowCheckerToSetLang));
                        order.setIsDifferentLangsAvailable(c
                                .getString(DB_TABLE_SETS_isDifferentLangsAvailable));

                        order.setDefaultPaymentForChecker(c
                                .getString(DB_TABLE_SET_DefaultPaymentForChecker));
                        order.setDefaultBonusPayment(c
                                .getString(DB_TABLE_SET_DefaultBonusPayment));
                        order.setAskForServiceDetails(c
                                .getString(DB_TABLE_SET_AskForServiceDetails));
                        order.setAskForPurchaseDetails(c
                                .getString(DB_TABLE_SET_AskForPurchaseDetails));
                        order.setAskForTransportationDetails(c
                                .getString(DB_TABLE_SET_AskForTransportationDetails));
                        order.setAutoApproveTransportation(c
                                .getString(DB_TABLE_SET_AutoApproveTransportation));
                        order.setAutoApprovePayment(c
                                .getString(DB_TABLE_SET_AutoApprovePayment));
                        order.setAutoApproveService(c
                                .getString(DB_TABLE_SET_AutoApproveService));
                        order.setAutoApprovePurchase(c
                                .getString(DB_TABLE_SET_AutoApprovePurchase));

                    }
                    order.setShowBackButton(c.getString(back));
                    order.setSetID(c.getString(id));
                    order.setSetName(c.getString(name));
                    order.setCompanyLink(c.getString(cl));
                    order.setSetDescription(c.getString(desc));
                    order.setSetCode(c.getString(code));
                    order.setShowSaveAndExitButton(c.getString(exitandsave));
                    order.setShowTOC(c.getString(toc));
                    order.setShowPreviewButton(c.getString(preview));
                    order.setClientName(c.getString(cn));
                    order.setShowFreeTextBox(c.getString(ft));
                    order.setEnableNonansweredConfirmation(c
                            .getString(setnonansweredconfirmation));
                    order.setEnableQuestionNumberingInForm(c
                            .getString(tosetenablequestionnuminformc));
                    order.setEnableValidationQuestion(c
                            .getString(enablevalidationques));
                    order.setAllowCheckerToSetFinishTime(c
                            .getString(setcheckerfinishtime));
                    order.setAllowCritFileUpload(c.getString(allowfileupload));
                    order.setAltLangID(c.getString(altlangid));

                    int count = 0;
                    String nxt = "";

                    ArrayList<Objects> tmbObjs = getQuestionnaireRecords(
                            Constants.DB_TABLE_QUES,
                            new String[]{
                                    Constants.DB_TABLE_QUES_FONT,
                                    Constants.DB_TABLE_QUES_COLOR,
                                    Constants.DB_TABLE_QUES_SIZE,
                                    Constants.DB_TABLE_QUES_BOLD,
                                    Constants.DB_TABLE_QUES_ITALIC,
                                    Constants.DB_TABLE_QUES_UL,
                                    Constants.DB_TABLE_QUES_ATTACHMENT,
                                    Constants.DB_TABLE_QUES_DATAID,
                                    Constants.DB_TABLE_QUES_OT,
                                    Constants.DB_TABLE_QUES_PFN,
                                    Constants.DB_TABLE_QUES_MAND,
                                    Constants.DB_TABLE_QUES_DT,
                                    Constants.DB_TABLE_QUES_AO,
                                    Constants.DB_TABLE_QUES_MT,
                                    Constants.DB_TABLE_QUES_MM,
                                    Constants.DB_TABLE_QUES_MD,
                                    Constants.DB_TABLE_QUES_Q,
                                    Constants.DB_TABLE_QUES_QD,
                                    Constants.DB_TABLE_QUES_QTL,
                                    Constants.DB_TABLE_QUES_UIT,
                                    Constants.DB_TABLE_QUES_SID,
                                    Constants.DB_TABLE_QUES_TEXT,
                                    Constants.DB_TABLE_QUES_TID,
                                    Constants.DB_TABLE_QUES_OBJECTDISCONDITINO,
                                    Constants.DB_TABLE_QUES_OBJECTCODE,
                                    Constants.DB_TABLE_QUES_MI_MIN,
                                    Constants.DB_TABLE_QUES_MI_MAX,
                                    Constants.DB_TABLE_QUES_MAX_ANSWER,
                                    Constants.DB_TABLE_QUES_MI_FREE_TEXT_MIN,
                                    Constants.DB_TABLE_QUES_MI_FREE_TEXT_MAX,
                                    Constants.DB_TABLE_QUES_URLCONTENT,
                                    Constants.DB_TABLE_QUES_DESTINATION_OBJECT,
                                    Constants.DB_TABLE_QUES_URL_ID,
                                    Constants.DB_TABLE_QUES_DEST_DESC,
                                    Constants.DB_TABLE_QUES_WORKERINPUTCAPTION,
                                    Constants.DB_TABLE_QUES_BRANCHINPUTCAPTION,
                                    Constants.DB_TABLE_QUES_DynamicTitlesDefaultAmount,
                                    Constants.DB_TABLE_QUES_PipingSourceDataLink,
                                    Constants.DB_TABLE_QUES_randomTitlesOrder,
                                    Constants.DB_TABLE_QUES_questionOrientation,
                                    Constants.DB_TABLE_QUES_groupName,
                                    Constants.DB_TABLE_QUES_RandomQuestionrder,
                                    Constants.DB_TABLE_QUES_CUSTOM_LINK,
                                    Constants.DB_TABLE_QUES_WORKERInputMandatory,
                                    Constants.DB_TABLE_QUES_BranchInputMandatory,
                                    Constants.DB_TABLE_QUES_AS,
                                    Constants.DB_TABLE_QUES_AF,
                                    Constants.DB_TABLE_QUES_AC,

                                    Constants.DB_TABLE_QUES_LoopCondition,
                                    Constants.DB_TABLE_QUES_RandomizeLoop,
                                    Constants.DB_TABLE_QUES_LoopSource,
                                    Constants.DB_TABLE_QUES_LoopName,
                                    Constants.DB_TABLE_QUES_LoopFormat,},
                            Constants.DB_TABLE_SETS_SETID + "=" + "\""
                                    + order.getSetID() + "\"", null, nxt + "5");

                    if (tmbObjs.size() > 0) {
                        order.addListObjectsfromDB(tmbObjs);
                    }

                    order.setListProducts(getProductsRecords(
                            Constants.DB_TABLE_PRODUCTS,
                            new String[]{
                                    Constants.DB_TABLE_PRODUCTS_AddNote,
                                    Constants.DB_TABLE_PRODUCTS_Bold,
                                    Constants.DB_TABLE_PRODUCTS_CheckExpiration,
                                    Constants.DB_TABLE_PRODUCTS_CheckPacking,
                                    Constants.DB_TABLE_PRODUCTS_CheckPrice,
                                    Constants.DB_TABLE_PRODUCTS_CheckQuantity,
                                    Constants.DB_TABLE_PRODUCTS_CheckShelfLevel,
                                    Constants.DB_TABLE_PRODUCTS_ClientLink,
                                    Constants.DB_TABLE_PRODUCTS_IsActive,
                                    Constants.DB_TABLE_PRODUCTS_Order,
                                    Constants.DB_TABLE_PRODUCTS_PID,
                                    Constants.DB_TABLE_PRODUCTS_PN,
                                    Constants.DB_TABLE_PRODUCTS_ProductCode,
                                    Constants.DB_TABLE_PRODUCTS_prop_id_51,
                                    Constants.DB_TABLE_PRODUCTS_prop_id_52,
                                    Constants.DB_TABLE_PRODUCTS_Size,
                                    Constants.DB_TABLE_PRODUCTS_TakePicture},
                            Constants.DB_TABLE_PRODUCTS_SETID + "=" + "\""
                                    + order.getSetID() + "\""));

                    order.setListProductLocations(getProductListRecords(
                            Constants.DB_TABLE_PRODUCT_LOCATION,
                            new String[]{
                                    Constants.DB_TABLE_PRODUCT_LOCATION_PID,
                                    Constants.DB_TABLE_PRODUCT_LOCATION_location,
                                    Constants.DB_TABLE_PRODUCT_CLientLink,},
                            Constants.DB_TABLE_PRODUCT_LOCATION_SETID + "="
                                    + "\"" + order.getSetID() + "\""));

                    order.setListProductProperties(getProductPropertiesRecord(
                            Constants.DB_TABLE_PRODUCT_PROPERTIES,
                            new String[]{
                                    Constants.DB_TABLE_PRODUCT_PROPERTIES_AllowOtherAddition,
                                    Constants.DB_TABLE_PRODUCT_PROPERTIES_ClientLink,
                                    Constants.DB_TABLE_PRODUCT_PROPERTIES_IsActive,
                                    Constants.DB_TABLE_PRODUCT_PROPERTIES_Mandatory,
                                    Constants.DB_TABLE_PRODUCT_PROPERTIES_Order,
                                    Constants.DB_TABLE_PRODUCT_PROPERTIES_ProdPropID,
                                    Constants.DB_TABLE_PRODUCT_PROPERTIES_PropertyName,
                                    Constants.DB_TABLE_PRODUCT_PROPERTIES_AllowOtherAddition},
                            Constants.DB_TABLE_PRODUCT_PROPERTIES_SetId + "="
                                    + "\"" + order.getSetID() + "\""));

                    order.setListWorkers(getWorkersRecords(
                            Constants.DB_TABLE_WORKERS,
                            new String[]{Constants.DB_TABLE_WORKERS_WID,
                                    Constants.DB_TABLE_WORKERS_WORKERID,
                                    Constants.DB_TABLE_WORKERS_WN,
                                    Constants.DB_TABLE_WORKERS_BL,},
                            Constants.DB_TABLE_SETS_SETID + "=" + "\""
                                    + order.getSetID() + "\""));

                    order.setListBranches(getBranchesRecords(
                            Constants.DB_TABLE_BRANCHES,
                            new String[]{Constants.DB_TABLE_BRANCHES_BID,
                                    Constants.DB_TABLE_BRANCHES_BN,
                                    Constants.DB_TABLE_BRANCHES_BLAT,
                                    Constants.DB_TABLE_BRANCHES_BLONG,},
                            Constants.DB_TABLE_SETS_SETID + "=" + "\""
                                    + order.getSetID() + "\""));

                } while (c.moveToNext());
                c.close();
                DBAdapter.closeDataBase();
                return order;
            } catch (Exception e) {
                System.out.println("Exception:   " + e.toString());
            } finally {
                if (!c.isClosed())
                    c.close();
                DBAdapter.closeDataBase();
            }
            return null;
        }
    }

    public static ArrayList<Branches> getBranchesRecords(String table,
                                                         String[] columns, String whereclause) {
        synchronized (lock) {
            DBAdapter.openDataBase();
            Cursor c = DBAdapter.db.query(false, table, columns, whereclause,
                    null, null, null, null, null);
            try {
                if (c.getCount() == 0) {
                    c.close();
                    return null;
                }
                ArrayList<Branches> objects = new ArrayList<Branches>();
                c.moveToFirst();
                do {
                    Branches order = new Branches();
                    int bid = c.getColumnIndex(columns[0]);
                    int bn = c.getColumnIndex(columns[1]);
                    if (columns.length >= 2) {
                        int bla = c.getColumnIndex(columns[2]);
                        int bln = c.getColumnIndex(columns[3]);
                        order.setBranchLat(c.getString(bla));
                        order.setBranchLong(c.getString(bln));
                    }
                    order.setBranchID(c.getString(bid));
                    order.setBranchName(c.getString(bn));
                    objects.add(order);
                } while (c.moveToNext());
                c.close();
                return objects;
            } catch (Exception e) {
                System.out.println("Exception:   " + e.toString());
            } finally {
                if (!c.isClosed())
                    c.close();
            }
            return null;
        }
    }

    public static ArrayList<ProductProperties> getProductPropertiesRecord(
            String table, String[] columns, String whereclause) {
        synchronized (lock) {
            DBAdapter.openDataBase();
            Cursor c = DBAdapter.db.query(false, table, columns, whereclause,
                    null, null, null, null, null);
            try {
                if (c.getCount() == 0) {
                    c.close();
                    return null;
                }
                ArrayList<ProductProperties> objects = new ArrayList<ProductProperties>();
                c.moveToFirst();
                do {
                    ProductProperties productProperties = new ProductProperties();

                    // Constants.DB_TABLE_PRODUCT_PROPERTIES_AllowOtherAddition,
                    // Constants.DB_TABLE_PRODUCT_PROPERTIES_ClientLink,
                    // Constants.DB_TABLE_PRODUCT_PROPERTIES_IsActive,
                    // Constants.DB_TABLE_PRODUCT_PROPERTIES_Mandatory,
                    // Constants.DB_TABLE_PRODUCT_PROPERTIES_Order,
                    // Constants.DB_TABLE_PRODUCT_PROPERTIES_ProdPropID,
                    // Constants.DB_TABLE_PRODUCT_PROPERTIES_PropertyName

                    int allowOtherAdd = c.getColumnIndex(columns[0]);
                    int clientLink = c.getColumnIndex(columns[1]);
                    int isActive = c.getColumnIndex(columns[2]);
                    int mandatory = c.getColumnIndex(columns[3]);
                    int order = c.getColumnIndex(columns[4]);
                    int propid = c.getColumnIndex(columns[5]);
                    int propname = c.getColumnIndex(columns[6]);

                    productProperties.setAllowOtherAddition(c
                            .getString(allowOtherAdd));
                    productProperties.setClientLink(c.getString(clientLink));
                    productProperties.setIsActive(c.getString(isActive));
                    productProperties.setMandatory(c.getString(mandatory));
                    productProperties.setOrder(c.getString(order));
                    productProperties.setProdPropID(c.getString(propid));
                    productProperties.setPropertyName(c.getString(propname));

                    productProperties
                            .setPropertyList(getProductPropertyValsRecord(
                                    Constants.DB_TABLE_PRODUCT_PROPERTIES_Values,
                                    new String[]{
                                            Constants.DB_TABLE_PRODUCT_PROPERTIES_Values_Content,
                                            Constants.DB_TABLE_PRODUCT_PROPERTIES_Values_IsActive,
                                            Constants.DB_TABLE_PRODUCT_PROPERTIES_Values_Order,
                                            Constants.DB_TABLE_PRODUCT_PROPERTIES_Values_PropLink,
                                            Constants.DB_TABLE_PRODUCT_PROPERTIES_Values_ValueID},
                                    whereclause
                                            + " AND "
                                            + Constants.DB_TABLE_PRODUCT_PROPERTIES_ProdPropID
                                            + " = \""
                                            + productProperties.getProdPropID()
                                            + "\""));

                    objects.add(productProperties);
                } while (c.moveToNext());
                c.close();
                return objects;
            } catch (Exception e) {
                System.out.println("Exception:   " + e.toString());
            } finally {
                if (!c.isClosed())
                    c.close();
            }
            return null;
        }
    }

    public static ArrayList<ProductPropertyVals> getProductPropertyValsRecord(
            String table, String[] columns, String whereclause) {
        synchronized (lock) {
            DBAdapter.openDataBase();
            Cursor c = DBAdapter.db.query(false, table, columns, whereclause,
                    null, null, null, null, null);
            try {
                if (c.getCount() == 0) {
                    c.close();
                    return null;
                }
                ArrayList<ProductPropertyVals> objects = new ArrayList<ProductPropertyVals>();
                c.moveToFirst();
                do {
                    ProductPropertyVals productProperties = new ProductPropertyVals();

                    // //
                    // Constants.DB_TABLE_PRODUCT_PROPERTIES_Values_Content,
                    // Constants.DB_TABLE_PRODUCT_PROPERTIES_Values_IsActive,
                    // Constants.DB_TABLE_PRODUCT_PROPERTIES_Values_Order,
                    // Constants.DB_TABLE_PRODUCT_PROPERTIES_Values_PropLink,
                    // Constants.DB_TABLE_PRODUCT_PROPERTIES_Values_ValueID

                    int Content = c.getColumnIndex(columns[0]);
                    int Order = c.getColumnIndex(columns[2]);
                    int IsActive = c.getColumnIndex(columns[1]);
                    int ValueID = c.getColumnIndex(columns[4]);
                    int proplink = c.getColumnIndex(columns[3]);

                    productProperties.setContent(c.getString(Content));
                    productProperties.setOrder(c.getString(Order));
                    productProperties.setIsActive(c.getString(IsActive));
                    productProperties.setValueID(c.getString(ValueID));
                    productProperties.setPropLink(c.getString(proplink));

                    objects.add(productProperties);
                } while (c.moveToNext());
                c.close();
                return objects;
            } catch (Exception e) {
                System.out.println("Exception:   " + e.toString());
            } finally {
                if (!c.isClosed())
                    c.close();
            }
            return null;
        }
    }

    public static ArrayList<ProductLocations> getProductListRecords(
            String table, String[] columns, String whereclause) {
        synchronized (lock) {
            DBAdapter.openDataBase();
            Cursor c = DBAdapter.db.query(false, table, columns, whereclause,
                    null, null, null, null, null);
            try {
                if (c.getCount() == 0) {
                    c.close();
                    return null;
                }
                ArrayList<ProductLocations> objects = new ArrayList<ProductLocations>();
                c.moveToFirst();
                do {
                    ProductLocations productLocation = new ProductLocations();

                    int pid = c.getColumnIndex(columns[0]);
                    int location = c.getColumnIndex(columns[1]);
                    int clientLink = c.getColumnIndex(columns[2]);

                    productLocation.setClientLink(c.getString(clientLink));
                    productLocation.setProdLocationID(c.getString(pid));
                    productLocation.setProductLocation(c.getString(location));

                    objects.add(productLocation);
                } while (c.moveToNext());
                c.close();
                return objects;
            } catch (Exception e) {
                System.out.println("Exception:   " + e.toString());
            } finally {
                if (!c.isClosed())
                    c.close();
            }
            return null;
        }
    }

    public static ArrayList<Products> getProductsRecords(String table,
                                                         String[] columns, String whereclause) {
        synchronized (lock) {
            DBAdapter.openDataBase();
            Cursor c = DBAdapter.db.query(false, table, columns, whereclause,
                    null, null, null, null, null);
            try {
                if (c.getCount() == 0) {
                    c.close();
                    return null;
                }
                ArrayList<Products> objects = new ArrayList<Products>();
                c.moveToFirst();
                do {
                    Products product = new Products();
                    // Constants.DB_TABLE_PRODUCTS_AddNote
                    // ,Constants.DB_TABLE_PRODUCTS_Bold
                    // ,Constants.DB_TABLE_PRODUCTS_CheckExpiration
                    // ,Constants.DB_TABLE_PRODUCTS_CheckPacking
                    // ,Constants.DB_TABLE_PRODUCTS_CheckPrice
                    // ,Constants.DB_TABLE_PRODUCTS_CheckQuantity
                    // ,Constants.DB_TABLE_PRODUCTS_CheckShelfLevel
                    // ,Constants.DB_TABLE_PRODUCTS_ClientLink
                    // ,Constants.DB_TABLE_PRODUCTS_IsActive
                    // ,Constants.DB_TABLE_PRODUCTS_Order
                    // ,Constants.DB_TABLE_PRODUCTS_PID
                    // ,Constants.DB_TABLE_PRODUCTS_PN
                    // ,Constants.DB_TABLE_PRODUCTS_ProductCode
                    // ,Constants.DB_TABLE_PRODUCTS_prop_id_51
                    // ,Constants.DB_TABLE_PRODUCTS_prop_id_52
                    // ,Constants.DB_TABLE_PRODUCTS_Size
                    // ,Constants.DB_TABLE_PRODUCTS_TakePicture
                    int addnote = c.getColumnIndex(columns[0]);
                    int bold = c.getColumnIndex(columns[1]);
                    int chkExpire = c.getColumnIndex(columns[2]);
                    int chkPacking = c.getColumnIndex(columns[3]);
                    int chkPrice = c.getColumnIndex(columns[4]);
                    int chkQuantity = c.getColumnIndex(columns[5]);
                    int chkShelfLevel = c.getColumnIndex(columns[6]);
                    int clientLink = c.getColumnIndex(columns[7]);
                    int isActive = c.getColumnIndex(columns[8]);
                    int order = c.getColumnIndex(columns[9]);
                    int PN = c.getColumnIndex(columns[10]);
                    int ProductCode = c.getColumnIndex(columns[11]);
                    int PID = c.getColumnIndex(columns[12]);
                    int prop_51 = c.getColumnIndex(columns[13]);
                    int prop_52 = c.getColumnIndex(columns[14]);
                    int size = c.getColumnIndex(columns[15]);
                    int takePicture = c.getColumnIndex(columns[16]);

                    product.setAddNote(c.getString(addnote));
                    product.setBold(c.getString(bold));
                    product.setCheckExpiration(c.getString(chkExpire));
                    product.setCheckPacking(c.getString(chkPacking));
                    product.setCheckPrice(c.getString(chkPrice));
                    product.setCheckQuantity(c.getString(chkQuantity));
                    product.setCheckShelfLevel(c.getString(chkShelfLevel));
                    product.setClientLink(c.getString(clientLink));
                    product.setIsActive(c.getString(isActive));
                    product.setOrder(c.getString(order));
                    product.setProductCode(c.getString(PID));
                    product.setProductID(c.getString(PN));
                    product.setProductName(c.getString(ProductCode));
                    product.setprop_id_51(c.getString(prop_51));
                    product.setprop_id_52(c.getString(prop_52));
                    product.setSize(c.getString(size));
                    product.setTakePicture(c.getString(takePicture));

                    objects.add(product);
                } while (c.moveToNext());
                c.close();
                return objects;
            } catch (Exception e) {
                System.out.println("Exception:   " + e.toString());
            } finally {
                if (!c.isClosed())
                    c.close();
            }
            return null;
        }
    }

    public static ArrayList<Workers> getWorkersRecords(String table,
                                                       String[] columns, String whereclause) {
        synchronized (lock) {
            DBAdapter.openDataBase();
            Cursor c = DBAdapter.db.query(false, table, columns, whereclause,
                    null, null, null, null, null);
            try {
                if (c.getCount() == 0) {
                    c.close();
                    return null;
                }
                ArrayList<Workers> objects = new ArrayList<Workers>();
                c.moveToFirst();
                do {
                    Workers order = new Workers();
                    int wsid = c.getColumnIndex(columns[0]);
                    int wid = c.getColumnIndex(columns[1]);
                    int wn = c.getColumnIndex(columns[2]);
                    int bl = c.getColumnIndex(columns[3]);

                    order.setWorkersID(c.getString(wsid));
                    order.setWorkerID(c.getString(wid));
                    order.setWorkerName(c.getString(wn));
                    order.setBranchLink(c.getString(bl));
                    objects.add(order);
                } while (c.moveToNext());
                c.close();
                return objects;
            } catch (Exception e) {
                System.out.println("Exception:   " + e.toString());
            } finally {
                if (!c.isClosed())
                    c.close();
            }
            return null;
        }
    }

    // public static Objects getThisObject(String table,
    // String[] columns, String whereclause,int i)
    // {
    // whereclause+= "\" AND "
    // + Constants.DB_TABLE_Ques + "="
    // + "\"" + order.getDataID() + "\""
    // Cursor c = DBAdapter.db.query(false, table, columns, whereclause,
    // null, null, null, null, null);
    //
    // }
    public static ArrayList<Objects> getQuestionnaireRecords(String table,
                                                             String[] columns, String whereclause, String OrderBy, String limit) {
        synchronized (lock) {
            DBAdapter.openDataBase();
            Cursor c = DBAdapter.db.query(false, table, columns, whereclause,
                    null, null, null, OrderBy, limit);

            ArrayList<Objects> objects = new ArrayList<Objects>();
            try {
                int count = c.getCount();
                if (c.getCount() == 0) {

                    c.close();
                    DBAdapter.closeDataBase();
                    return objects;
                }
                c.moveToFirst();
                boolean isOkay = true;
                int ijk = 0;
                do {
                    ijk++;
                    Objects order = new Objects();
                    int font = c.getColumnIndex(columns[0]);
                    int col = c.getColumnIndex(columns[1]);
                    int size = c.getColumnIndex(columns[2]);
                    int bold = c.getColumnIndex(columns[3]);
                    int it = c.getColumnIndex(columns[4]);
                    int ul = c.getColumnIndex(columns[5]);
                    int attach = c.getColumnIndex(columns[6]);
                    int dataid = c.getColumnIndex(columns[7]);
                    int ot = c.getColumnIndex(columns[8]);
                    int pfn = c.getColumnIndex(columns[9]);
                    int mand = c.getColumnIndex(columns[10]);
                    int dt = c.getColumnIndex(columns[11]);
                    int ao = c.getColumnIndex(columns[12]);
                    int mt = c.getColumnIndex(columns[13]);
                    int mm = c.getColumnIndex(columns[14]);
                    int md = c.getColumnIndex(columns[15]);
                    int q = c.getColumnIndex(columns[16]);
                    int qd = c.getColumnIndex(columns[17]);
                    int qtl = c.getColumnIndex(columns[18]);
                    int uit = c.getColumnIndex(columns[19]);
                    int sid = c.getColumnIndex(columns[20]);
                    int text = c.getColumnIndex(columns[21]);
                    int tid = c.getColumnIndex(columns[22]);
                    int dispobjcond = c.getColumnIndex(columns[23]);
                    int objcode = c.getColumnIndex(columns[24]);
                    int minumin = c.getColumnIndex(columns[25]);
                    int minmunmax = c.getColumnIndex(columns[26]);
                    int maxans = c.getColumnIndex(columns[27]);
                    int textmin = c.getColumnIndex(columns[28]);
                    int textmax = c.getColumnIndex(columns[29]);
                    int uricont = c.getColumnIndex(columns[30]);
                    int destobj = c.getColumnIndex(columns[31]);
                    int urlid = c.getColumnIndex(columns[32]);
                    int destdesc = c.getColumnIndex(columns[33]);
                    int wid = c.getColumnIndex(columns[34]);
                    int bid = c.getColumnIndex(columns[35]);

                    int DB_TABLE_QUES_DynamicTitlesDefaultAmount = c
                            .getColumnIndex(columns[36]);
                    int DB_TABLE_QUES_PipingSourceDataLink = c
                            .getColumnIndex(columns[37]);
                    int DB_TABLE_QUES_randomTitlesOrder = c
                            .getColumnIndex(columns[38]);
                    int DB_TABLE_QUES_questionOrientation = c
                            .getColumnIndex(columns[39]);
                    int DB_TABLE_QUES_groupName = c.getColumnIndex(columns[40]);
                    int DB_TABLE_QUES_RandomQuestionrder = c
                            .getColumnIndex(columns[41]);
                    int DB_TABLE_QUES_CustomLink = c
                            .getColumnIndex(columns[42]);
                    int DB_TABLE_QUES_worker_imput_mi = c
                            .getColumnIndex(columns[43]);
                    int DB_TABLE_QUES_branch_imput_mi = c
                            .getColumnIndex(columns[44]);

                    int DB_TABLE_QUES_AS = c.getColumnIndex(columns[45]);
                    int DB_TABLE_QUES_AF = c.getColumnIndex(columns[46]);
                    int DB_TABLE_QUES_AC = c.getColumnIndex(columns[47]);

                    int DB_TABLE_QUES_LoopCondition = c
                            .getColumnIndex(columns[48]);
                    int DB_TABLE_QUES_RandomizeLoop = c
                            .getColumnIndex(columns[49]);
                    int DB_TABLE_QUES_LoopSource = c
                            .getColumnIndex(columns[50]);
                    int DB_TABLE_QUES_LoopName = c.getColumnIndex(columns[51]);
                    int DB_TABLE_QUES_LoopFormat = c
                            .getColumnIndex(columns[52]);

                    // Constants.DB_TABLE_QUES_LoopCondition,
                    // Constants.DB_TABLE_QUES_RandomizeLoop,
                    // Constants.DB_TABLE_QUES_LoopSource,
                    // Constants.DB_TABLE_QUES_LoopName,
                    // Constants.DB_TABLE_QUES_LoopFormat,

                    order.setLoopName(c.getString(DB_TABLE_QUES_LoopName));
                    order.setLoopSource(c.getString(DB_TABLE_QUES_LoopSource));
                    order.setRandomizeLoop(c
                            .getString(DB_TABLE_QUES_RandomizeLoop));
                    order.setLoopCondition(c
                            .getString(DB_TABLE_QUES_LoopCondition));
                    order.setLoopFormat(c.getString(DB_TABLE_QUES_LoopFormat));

                    order.setAnswersSource(c.getString(DB_TABLE_QUES_AS));
                    order.setAnswersFormat(c.getString(DB_TABLE_QUES_AF));
                    order.setAnswersCondition(c.getString(DB_TABLE_QUES_AC));
                    order.setAnswersFormat(c.getString(DB_TABLE_QUES_AF));
                    order.setAnswersCondition(c.getString(DB_TABLE_QUES_AC));

                    order.setAnswersSource(c.getString(DB_TABLE_QUES_AS));
                    order.setAnswersFormat(c.getString(DB_TABLE_QUES_AF));
                    order.setAnswersCondition(c.getString(DB_TABLE_QUES_AC));

                    order.setCustomScaleLink(c
                            .getString(DB_TABLE_QUES_CustomLink));
                    order.setDynamicTitlesDefaultAmount(c
                            .getString(DB_TABLE_QUES_DynamicTitlesDefaultAmount));

                    order.setBranchInputMandatory(c
                            .getString(DB_TABLE_QUES_branch_imput_mi));
                    order.setWorkerInputMandatory(c
                            .getString(DB_TABLE_QUES_worker_imput_mi));

                    order.setPipingSourceDataLink(c
                            .getString(DB_TABLE_QUES_PipingSourceDataLink));
                    order.setRandomTitlesOrder(c
                            .getString(DB_TABLE_QUES_randomTitlesOrder));
                    order.setQuestionsOrientation(c
                            .getString(DB_TABLE_QUES_questionOrientation));
                    order.setGroupName(c.getString(DB_TABLE_QUES_groupName));
                    order.setRandomQuestionsOrder(c
                            .getString(DB_TABLE_QUES_RandomQuestionrder));

                    order.setFont(c.getString(font));
                    order.setColor(c.getString(col));
                    order.setSize(c.getString(size));
                    order.setBold(c.getString(bold));
                    order.setItalics(c.getString(it));
                    order.setUnderline(c.getString(ul));
                    order.setAttachment(c.getString(attach));
                    order.setDataID(c.getString(dataid));
                    order.setObjectType(c.getString(ot));
                    order.setPictureData(c.getBlob(pfn));
                    order.setMandatory(c.getString(mand));
                    order.setDisplayType(c.getString(dt));
                    order.setAnswerOrdering(c.getString(ao));
                    order.setMiType(c.getString(mt));
                    order.setMiMandatory(c.getString(mm));
                    order.setMiDescription(c.getString(md));
                    order.setQuestion(c.getString(q));
                    if (order.getQuestion() != null
                            && order.getQuestion().toLowerCase()
                            .contains("boarding")) {
                        int i = 0;
                        i++;
                    }
                    order.setQuestionDescription(c.getString(qd));
                    order.setQuestionTypeLink(c.getString(qtl));
                    order.setUseInTOC(c.getString(uit));
                    order.setText(c.getString(text));
                    order.setTextID(c.getString(tid));
                    order.setObjectDisplayCondition(c.getString(dispobjcond));
                    order.setObjectCode(c.getString(objcode));
                    order.setMiNumberMin(c.getString(minumin));
                    order.setMiNumberMax(c.getString(minmunmax));
                    order.setMaxAnswersForMultiple(c.getString(maxans));
                    order.setMiFreeTextMinlength(c.getString(textmin));
                    order.setMiFreeTextMaxlength(c.getString(textmax));
                    order.setUrlContent(c.getString(uricont));
                    order.setDestinationObject(c.getString(destobj));
                    order.setUrlID(c.getString(urlid));
                    order.setDestinationDescription(c.getString(destdesc));
                    order.setWorkerInputCaption(c.getString(wid));
                    order.setBranchInputCaption(c.getString(bid));

                    if (order.getObjectType().equals("4")) {
                        order.setListAnswers(getAnswersRecords(
                                Constants.DB_TABLE_QA,
                                new String[]{
                                        Constants.DB_TABLE_QA_AID,
                                        Constants.DB_TABLE_QA_ANS,
                                        Constants.DB_TABLE_QA_VAL,
                                        Constants.DB_TABLE_QA_COL,
                                        Constants.DB_TABLE_QA_BOLD,
                                        Constants.DB_TABLE_QA_ITALIC,
                                        Constants.DB_TABLE_QA_UL,
                                        Constants.DB_TABLE_QA_CODE,
                                        Constants.DB_TABLE_QA_JUMPTO,
                                        Constants.DB_TABLE_QA_ICONNAME,
                                        Constants.DB_TABLE_QA_HIDEADDITIONALINFO,
                                        Constants.DB_TABLE_QA_ADDITIONALINFOMANDATORY,
                                        Constants.DB_TABLE_QA_DISPLAYCONDITION,
                                        Constants.DB_TABLE_QA_ClearOtherAnswers,},
                                Constants.DB_TABLE_SETS_SETID + "=" + "\""
                                        + c.getString(sid) + "\" AND "
                                        + Constants.DB_TABLE_QA_DID + "="
                                        + "\"" + order.getDataID() + "\"",
                                Constants.DB_TABLE_QA_CINDEX));
                    }

                    List<AutoValues> autoValues = getQAutoValues(
                            Constants.DB_TABLE_AUTOVALUES,
                            new String[]{
                                    Constants.DB_TABLE_AUTOVALUES_avID,
                                    Constants.DB_TABLE_AUTOVALUES_Condition,
                                    Constants.DB_TABLE_AUTOVALUES_DataLink,
                                    Constants.DB_TABLE_AUTOVALUES_Priority,
                                    Constants.DB_TABLE_AUTOVALUES_SetLink,
                                    Constants.DB_TABLE_AUTOVALUES_Value_AnswerCode,},
                            Constants.DB_TABLE_AUTOVALUES_SetLink + "=" + "\""
                                    + c.getString(sid) + "\" AND "
                                    + Constants.DB_TABLE_AUTOVALUES_DataLink
                                    + "=" + "\"" + order.getDataID() + "\"",
                            Constants.DB_TABLE_AUTOVALUES_Priority);
                    if (autoValues != null)
                        for (int i = 0; i < autoValues.size(); i++)
                            order.setListAutoValues(autoValues.get(i));

                    List<Titles> titles = getQTitles(
                            Constants.DB_TABLE_QTITLES,
                            new String[]{
                                    Constants.DB_TABLE_QTITLES_DID,
                                    Constants.DB_TABLE_QTITLES_DataLink,
                                    Constants.DB_TABLE_QTITLES_ggtID,
                                    Constants.DB_TABLE_QTITLES_TitleText,
                                    Constants.DB_TABLE_QTITLES_TitleCode,
                                    Constants.DB_TABLE_QTITLES_SETID,
                                    Constants.DB_TABLE_QTITLES_DisplayCondition,},
                            Constants.DB_TABLE_QTITLES_SETID + "=" + "\""
                                    + c.getString(sid) + "\" AND "
                                    + Constants.DB_TABLE_QTITLES_DID + "="
                                    + "\"" + order.getDataID() + "\"",
                            Constants.DB_TABLE_QTITLES_DID);
                    if (titles != null)
                        for (int i = 0; i < titles.size(); i++)
                            order.addTitles(titles.get(i));

                    List<String> groups = getQGroups(
                            Constants.DB_TABLE_QGROUPS,
                            new String[]{Constants.DB_TABLE_QGROUPS_DataLink,},
                            Constants.DB_TABLE_QGROUPS_SETID + "=" + "\""
                                    + c.getString(sid) + "\" AND "
                                    + Constants.DB_TABLE_QGROUPS_DataID + "="
                                    + "\"" + order.getDataID() + "\"",
                            Constants.DB_TABLE_QGROUPS_DataID);

                    if (groups != null)
                        for (int i = 0; i < groups.size(); i++)
                            order.addQuestionGroups(groups.get(i));

                    List<String> links = getQLinks(
                            Constants.DB_TABLE_QLINKS,
                            new String[]{Constants.DB_TABLE_QLINKS_DataLink,},
                            Constants.DB_TABLE_QLINKS_SETID + "=" + "\""
                                    + c.getString(sid) + "\" AND "
                                    + Constants.DB_TABLE_QLINKS_DataID + "="
                                    + "\"" + order.getDataID() + "\"",
                            Constants.DB_TABLE_QLINKS_DataID);
                    if (links != null)
                        for (int i = 0; i < links.size(); i++)
                            order.addQuestionLinks(links.get(i));

                    if (order.getQuestion() != null
                            && order.getQuestion().contains("one you smoke")) {
                        int i = 0;
                        i++;
                    }

                    order = getAltTexts(order, whereclause);

                    objects.add(order);
                    if (staticpd != null && order != null
                            && order.getQuestion() != null
                            && order.getQuestion().length() > 0) {
                        try {
                            staticpd.changeMessage(" Question: "
                                    + order.getQuestion());
                        } catch (Exception ex) {
                        }
                    }
                    if (ijk > 75) {
                        int kqd = 0;
                        kqd++;
                    }
                    DBAdapter.openDataBase();

                } while (c.moveToNext());
                c.close();
                DBAdapter.closeDataBase();
            } catch (Exception e) {
                System.out.println("Exception:   " + e.toString());
            } finally {
                if (!c.isClosed())
                    c.close();
                DBAdapter.closeDataBase();
            }
            return objects;
        }
    }

    private static List<AutoValues> getQAutoValues(String table,
                                                   String[] columns, String whereclause, String orderby) {
        synchronized (lock) {
            DBAdapter.openDataBase();
            Cursor c = DBAdapter.db.query(true, table, columns, whereclause,
                    null, null, null, orderby + " ASC", null);
            try {
                if (c.getCount() == 0) {
                    c.close();
                    DBAdapter.closeDataBase();
                    return null;
                }
                ArrayList<AutoValues> autoValues = new ArrayList<AutoValues>();
                c.moveToFirst();
                do {
                    AutoValues order = new AutoValues();
                    int DB_TABLE_AUTOVALUES_avID = c.getColumnIndex(columns[0]);
                    int DB_TABLE_AUTOVALUES_Condition = c
                            .getColumnIndex(columns[1]);
                    int DB_TABLE_AUTOVALUES_DataLink = c
                            .getColumnIndex(columns[2]);
                    int DB_TABLE_AUTOVALUES_Priority = c
                            .getColumnIndex(columns[3]);
                    int DB_TABLE_AUTOVALUES_SetLink = c
                            .getColumnIndex(columns[4]);
                    int DB_TABLE_AUTOVALUES_Value_AnswerCode = c
                            .getColumnIndex(columns[5]);

                    order.setavID(c.getString(DB_TABLE_AUTOVALUES_avID));
                    order.setCondition(c
                            .getString(DB_TABLE_AUTOVALUES_Condition));
                    order.setDataLink(c.getString(DB_TABLE_AUTOVALUES_DataLink));
                    order.setPriority(c.getString(DB_TABLE_AUTOVALUES_Priority));
                    order.setSetLink(c.getString(DB_TABLE_AUTOVALUES_SetLink));
                    order.setValue_AnswerCode(c
                            .getString(DB_TABLE_AUTOVALUES_Value_AnswerCode));
                    autoValues.add(order);
                } while (c.moveToNext());
                c.close();
                DBAdapter.closeDataBase();
                return autoValues;
            } catch (Exception e) {
                System.out.println("Exception:   " + e.toString());
            } finally {
                if (!c.isClosed())
                    c.close();
                DBAdapter.closeDataBase();
            }
            return null;
        }
    }

    private static ArrayList<Answers> getAnswersRecords(String table,
                                                        String[] columns, String whereclause, String orderby) {
        synchronized (lock) {
            DBAdapter.openDataBase();
            Cursor c = DBAdapter.db.query(true, table, columns, whereclause,
                    null, null, null, orderby + " ASC", null);
            try {
                if (c.getCount() == 0) {
                    c.close();
                    DBAdapter.closeDataBase();
                    return null;
                }
                ArrayList<Answers> answers = new ArrayList<Answers>();
                c.moveToFirst();
                do {
                    Answers order = new Answers();
                    int id = c.getColumnIndex(columns[0]);
                    int ans = c.getColumnIndex(columns[1]);
                    int val = c.getColumnIndex(columns[2]);
                    int col = c.getColumnIndex(columns[3]);
                    int bold = c.getColumnIndex(columns[4]);
                    int italic = c.getColumnIndex(columns[5]);
                    int ul = c.getColumnIndex(columns[6]);
                    int code = c.getColumnIndex(columns[7]);
                    int jumpto = c.getColumnIndex(columns[8]);
                    int iconName = c.getColumnIndex(columns[9]);
                    int hideinfo = c.getColumnIndex(columns[10]);
                    int additionalInfoMandatory = c.getColumnIndex(columns[11]);
                    int dc = c.getColumnIndex(columns[12]);
                    int clearans = c.getColumnIndex(columns[13]);

                    order.setClearOtherAnswers(c.getString(clearans));
                    order.sethIconName(c.getString(iconName));
                    order.setAnswerID(c.getString(id));
                    order.setAnswer(c.getString(ans));
                    order.setValue(c.getString(val));
                    order.setColor(c.getString(col));
                    order.setBold(c.getString(bold));
                    order.setItalics(c.getString(italic));
                    order.setUnderline(c.getString(ul));
                    order.setAnswerCode(c.getString(code));
                    order.setJumpTo(c.getString(jumpto));
                    order.setHideAdditionalInfo(c.getString(hideinfo));
                    order.setAdditionalInfoMandatory(c
                            .getString(additionalInfoMandatory));
                    order.setAnswerDisplayCondition(c.getString(dc));

                    order = getAltAnswers(order, whereclause);
                    answers.add(order);
                } while (c.moveToNext());
                c.close();
                DBAdapter.closeDataBase();
                return answers;
            } catch (Exception e) {
                System.out.println("Exception:   " + e.toString());
            } finally {
                if (!c.isClosed())
                    c.close();
                DBAdapter.closeDataBase();
            }
            return null;
        }
    }

    private static Objects getAltTexts(Objects order, String whereclause) {
        synchronized (lock) {
            whereclause = whereclause + " AND " + Constants.DB_TABLE_alt_DataID
                    + "=\"" + order.getDataID() + "\"";
            if (order.getDataID() != null
                    && order.getDataID().contains("186997")) {
                int hj = 0;
                hj++;
            }
            DBAdapter.openDataBase();

            String[] columns = new String[]{Constants.DB_TABLE_alt_QText,
                    Constants.DB_TABLE_alt_Question,
                    Constants.DB_TABLE_alt_QuestionDescription,
                    Constants.DB_TABLE_alt_AltLangID,
                    Constants.DB_TABLE_alt_GroupName};
            Cursor c = DBAdapter.db.query(true,
                    Constants.DB_TABLE_alt_questions, columns, whereclause,
                    null, null, null, null, null);
            try {

                if (c.getCount() == 0) {
                    c.close();
                    DBAdapter.closeDataBase();
                    return order;
                }
                c.moveToFirst();
                do {
                    int DB_TABLE_alt_QText = c.getColumnIndex(columns[0]);
                    int DB_TABLE_alt_Question = c.getColumnIndex(columns[1]);
                    int DB_TABLE_alt_QuestionDescription = c
                            .getColumnIndex(columns[2]);
                    int DB_TABLE_alt_AltLangID = c.getColumnIndex(columns[3]);
                    int DB_TABLE_alt_GroupName = c.getColumnIndex(columns[4]);

                    String sDB_TABLE_alt_QText = c
                            .getString(DB_TABLE_alt_QText);
                    String sDB_TABLE_alt_Question = c
                            .getString(DB_TABLE_alt_Question);

                    String sDB_TABLE_alt_GroupName = c
                            .getString(DB_TABLE_alt_GroupName);
                    String sDB_TABLE_alt_QuestionDescription = c
                            .getString(DB_TABLE_alt_QuestionDescription);
                    String sDB_TABLE_alt_AltLangID = c
                            .getString(DB_TABLE_alt_AltLangID);

                    order.altTexts.add(new AltLangStrings(
                            sDB_TABLE_alt_AltLangID, sDB_TABLE_alt_QText));

                    order.altGroupNames.add(new AltLangStrings(
                            sDB_TABLE_alt_AltLangID, sDB_TABLE_alt_GroupName));

                    order.altQuestionDescription.add(new AltLangStrings(
                            sDB_TABLE_alt_AltLangID,
                            sDB_TABLE_alt_QuestionDescription));

                    order.altQuestionTexts.add(new AltLangStrings(
                            sDB_TABLE_alt_AltLangID, sDB_TABLE_alt_Question));

                } while (c.moveToNext());
                c.close();
                DBAdapter.closeDataBase();
                return order;
            } catch (Exception e) {
                System.out.println("Exception:   " + e.toString());
            } finally {
                if (!c.isClosed())
                    c.close();
                DBAdapter.closeDataBase();
            }
            return null;
        }
    }

    private static Answers getAltAnswers(Answers order, String whereclause) {
        synchronized (lock) {
            whereclause = whereclause + " AND "
                    + Constants.DB_TABLE_alt_AnswerID + "=\""
                    + order.getAnswerID() + "\"";
            DBAdapter.openDataBase();

            String[] columns = new String[]{Constants.DB_TABLE_alt_Answer,
                    Constants.DB_TABLE_alt_AltLangID};
            Cursor c = DBAdapter.db.query(true, Constants.DB_TABLE_alt_answers,
                    columns, whereclause, null, null, null, null, null);
            try {

                if (c.getCount() == 0) {
                    c.close();
                    DBAdapter.closeDataBase();
                    return order;
                }
                c.moveToFirst();
                do {
                    int DB_TABLE_alt_Answer = c.getColumnIndex(columns[0]);
                    int DB_TABLE_alt_AltLangID = c.getColumnIndex(columns[1]);

                    String sDB_TABLE_alt_QText = c
                            .getString(DB_TABLE_alt_Answer);
                    String sDB_TABLE_alt_AltLangID = c
                            .getString(DB_TABLE_alt_AltLangID);

                    order.altanswers.add(new AltLangStrings(
                            sDB_TABLE_alt_AltLangID, sDB_TABLE_alt_QText));

                } while (c.moveToNext());
                c.close();
                DBAdapter.closeDataBase();
                return order;
            } catch (Exception e) {
                System.out.println("Exception:   " + e.toString());
            } finally {
                if (!c.isClosed())
                    c.close();
                DBAdapter.closeDataBase();
            }
            return null;
        }
    }

    private static Titles getAltTitles(Titles order, String whereclause) {
        synchronized (lock) {
            whereclause = whereclause + " AND " + Constants.DB_TABLE_alt_qgtid
                    + "=\"" + order.getqgtID() + "\"";
            DBAdapter.openDataBase();

            String[] columns = new String[]{Constants.DB_TABLE_alt_Title,
                    Constants.DB_TABLE_alt_AltLangID};
            Cursor c = DBAdapter.db.query(true,
                    Constants.DB_TABLE_alt_TblTitle, columns, whereclause,
                    null, null, null, null, null);
            try {

                if (c.getCount() == 0) {
                    c.close();
                    DBAdapter.closeDataBase();
                    return order;
                }
                c.moveToFirst();
                do {
                    int DB_TABLE_alt_Answer = c.getColumnIndex(columns[0]);
                    int DB_TABLE_alt_AltLangID = c.getColumnIndex(columns[1]);

                    String sDB_TABLE_alt_QText = c
                            .getString(DB_TABLE_alt_Answer);
                    String sDB_TABLE_alt_AltLangID = c
                            .getString(DB_TABLE_alt_AltLangID);

                    order.altTitles.add(new AltLangStrings(
                            sDB_TABLE_alt_AltLangID, sDB_TABLE_alt_QText));

                } while (c.moveToNext());
                c.close();
                DBAdapter.closeDataBase();
                return order;
            } catch (Exception e) {
                System.out.println("Exception:   " + e.toString());
            } finally {
                if (!c.isClosed())
                    c.close();
                DBAdapter.closeDataBase();
            }
            return null;
        }
    }

    public static ArrayList<String> getQLinks(String table, String[] columns,
                                              String whereclause, String orderby) {
        synchronized (lock) {
            DBAdapter.openDataBase();
            Cursor c = DBAdapter.db.query(true, table, columns, whereclause,
                    null, null, null, orderby + " ASC", null);
            try {
                if (c.getCount() == 0) {
                    c.close();
                    DBAdapter.closeDataBase();
                    return null;
                }
                ArrayList<String> answers = new ArrayList<String>();
                c.moveToFirst();
                do {
                    String order = "";
                    int id = c.getColumnIndex(columns[0]);

                    order = c.getString(id);
                    answers.add(order);
                } while (c.moveToNext());
                c.close();
                DBAdapter.closeDataBase();
                return answers;
            } catch (Exception e) {
                System.out.println("Exception:   " + e.toString());
            } finally {
                if (!c.isClosed())
                    c.close();
                DBAdapter.closeDataBase();
            }
            return null;
        }
    }

    public static ArrayList<String> getQGroups(String table, String[] columns,
                                               String whereclause, String orderby) {
        synchronized (lock) {
            DBAdapter.openDataBase();
            Cursor c = DBAdapter.db.query(true, table, columns, whereclause,
                    null, null, null, orderby + " ASC", null);
            try {
                if (c.getCount() == 0) {
                    c.close();
                    DBAdapter.closeDataBase();
                    return null;
                }
                ArrayList<String> answers = new ArrayList<String>();
                c.moveToFirst();
                do {
                    String order = "";
                    int id = c.getColumnIndex(columns[0]);

                    order = c.getString(id);
                    answers.add(order);
                } while (c.moveToNext());
                c.close();
                DBAdapter.closeDataBase();
                return answers;
            } catch (Exception e) {
                System.out.println("Exception:   " + e.toString());
            } finally {
                if (!c.isClosed())
                    c.close();
                DBAdapter.closeDataBase();
            }
            return null;
        }
    }

    public static ArrayList<Titles> getQTitles(String table, String[] columns,
                                               String whereclause, String orderby) {
        synchronized (lock) {
            DBAdapter.openDataBase();
            Cursor c = DBAdapter.db.query(true, table, columns, whereclause,
                    null, null, null, orderby + " ASC", null);
            try {
                if (c.getCount() == 0) {
                    c.close();
                    DBAdapter.closeDataBase();
                    return null;
                }
                ArrayList<Titles> answers = new ArrayList<Titles>();
                c.moveToFirst();
                do {
                    Titles order = new Titles();
                    int DataID = c.getColumnIndex(columns[0]);
                    int DataLink = c.getColumnIndex(columns[1]);
                    int ggtID = c.getColumnIndex(columns[2]);
                    int TitleText = c.getColumnIndex(columns[3]);
                    int TitleCode = c.getColumnIndex(columns[4]);
                    int SetID = c.getColumnIndex(columns[5]);
                    int displayCondition = c.getColumnIndex(columns[6]);

                    order.setqgtID(c.getString(ggtID));
                    order.setTitleCode(c.getString(TitleCode));
                    order.setTitleText(c.getString(TitleText));
                    order.setDisplayCondition(c.getString(displayCondition));

                    order = getAltTitles(order, whereclause);

                    answers.add(order);
                } while (c.moveToNext());
                c.close();
                DBAdapter.closeDataBase();
                return answers;
            } catch (Exception e) {
                System.out.println("Exception:   " + e.toString());
            } finally {
                if (!c.isClosed())
                    c.close();
                DBAdapter.closeDataBase();
            }
            return null;
        }
    }

    public static boolean isOrderAvailable(String table, String[] columns,
                                           String whereclause) {
        synchronized (lock) {
            Cursor c = null;
            try {
                DBAdapter.openDataBase();
                c = DBAdapter.db.query(true, table, columns, whereclause, null,
                        null, null, null, null);
                // DBAdapter.closeDataBase();
                if (c.getCount() == 0) {
                    c.close();
                    DBAdapter.closeDataBase();
                    return false;
                }
                c.moveToFirst();
                c.close();
                DBAdapter.closeDataBase();
                return true;
            } catch (Exception e) {
                System.out.println("Exception:   " + e.toString());
            } finally {
                if (!c.isClosed())
                    c.close();
                DBAdapter.closeDataBase();
            }
            return false;
        }
    }

    public static String getOrderStartTime(String table, String[] columns,
                                           String whereclause) {

        Cursor c = null;
        try {
            DBAdapter.openDataBase();
            c = DBAdapter.db.query(true, table, columns, null, null, null,
                    null, null, null);
            // DBAdapter.closeDataBase();
            if (c.getCount() == 0) {
                c.close();
                DBAdapter.closeDataBase();
                return null;
            }
            String time = "";
            c.moveToFirst();
            do {
                int st = c.getColumnIndex(Constants.DB_TABLE_ORDERS_START_TIME);
                time = c.getString(st);

                int id = c.getColumnIndex(Constants.DB_TABLE_ORDERS_ORDERID);
                String orderid = c.getString(id);

                int stat = c.getColumnIndex(Constants.DB_TABLE_ORDERS_STATUS);
                String status = c.getString(stat);
                c.close();

            } while (c.moveToNext());
            DBAdapter.closeDataBase();
            return time;
        } catch (Exception e) {
            System.out.println("Exception:   " + e.toString());
        } finally {
            if (!c.isClosed())
                c.close();
            DBAdapter.closeDataBase();
        }
        return null;

    }

    public static String getOrderLastDataId(String table, String[] columns,
                                            String whereclause) {

        Cursor c = null;
        try {
            DBAdapter.openDataBase();
            c = DBAdapter.db.query(true, table, columns, whereclause, null, null,
                    null, null, null);
            // DBAdapter.closeDataBase();
            if (c.getCount() == 0) {
                c.close();
                DBAdapter.closeDataBase();
                return null;
            }
            String time = "";
            c.moveToFirst();
            do {
                int st = c.getColumnIndex(Constants.DB_TABLE_ORDERS_LASTDATAID);
                time = c.getString(st);
            } while (c.moveToNext());
            DBAdapter.closeDataBase();
            return time;
        } catch (Exception e) {
            System.out.println("Exception:   " + e.toString());
        } finally {
            if (!c.isClosed())
                c.close();
            DBAdapter.closeDataBase();
        }
        return null;

    }

    public static String getOrderStatus(String table, String[] columns,
                                        String whereclause) {
        synchronized (lock) {
            Cursor c = null;
            try {
                DBAdapter.openDataBase();
                c = DBAdapter.db.query(true, table, columns, whereclause, null,
                        null, null, null, null);
                // DBAdapter.closeDataBase();
                if (c.getCount() == 0) {
                    c.close();
                    DBAdapter.closeDataBase();
                    return null;
                }
                c.moveToFirst();

                int st = c.getColumnIndex(Constants.DB_TABLE_JOBLIST_SN);
                String status = c.getString(st);
                c.close();
                DBAdapter.closeDataBase();
                return status;
            } catch (Exception e) {
                System.out.println("Exception:   " + e.toString());
            } finally {
                if (!c.isClosed())
                    c.close();
                DBAdapter.closeDataBase();
            }
            return null;
        }
    }

    static Spanned sp;
    public static String whereOrderNotArchived = Constants.DB_TABLE_ORDERS_STATUS + " <> 'archived'";
    public static String whereJobListNotArchived = Constants.DB_TABLE_JOBLIST_SN + " <> 'archived'";

    public static ArrayList<Order> getOrders(String where, String table,
                                             String[] columns, String orderby) {
        synchronized (lock) {

            ArrayList<Order> orders = new ArrayList<Order>();
            DBAdapter.openDataBase();
            if (DBAdapter.db == null)
                return null;
            Cursor c = DBAdapter.db.query(true, table, columns, where, null,
                    null, null, orderby + " ASC", null);
            int count = c.getCount();
            if (count == 0) {
                c.close();
                DBAdapter.closeDataBase();
                return null;
            }

            try {

                c.moveToFirst();
                do {
                    try {
                        Order order = new Order();
                        if (columns.length == 3) {
                            int orderid = c.getColumnIndex(columns[0]);
                            int st = c.getColumnIndex(columns[1]);
                            int startime = c.getColumnIndex(columns[2]);
                            order.setOrderID(c.getString(orderid));
                            order.setStatusName(c.getString(st));
                            order.setTimeStart(c.getString(startime));
                        } else {
                            int orderid = c.getColumnIndex(columns[0]);
                            int date = c.getColumnIndex(columns[1]);
                            int st = c.getColumnIndex(columns[2]);
                            int desc = c.getColumnIndex(columns[3]);
                            int sn = c.getColumnIndex(columns[4]);
                            int sl = c.getColumnIndex(columns[5]);
                            int cn = c.getColumnIndex(columns[6]);
                            int bfn = c.getColumnIndex(columns[7]);
                            int bn = c.getColumnIndex(columns[8]);
                            int cityn = c.getColumnIndex(columns[9]);
                            int addr = c.getColumnIndex(columns[10]);
                            int bp = c.getColumnIndex(columns[11]);
                            int oh = c.getColumnIndex(columns[12]);
                            int ts = c.getColumnIndex(columns[13]);
                            int te = c.getColumnIndex(columns[14]);
                            int sid = c.getColumnIndex(columns[15]);
                            int bl = c.getColumnIndex(columns[16]);
                            int blng = c.getColumnIndex(columns[17]);
                            int fn = c.getColumnIndex(columns[18]);
                            int jc = c.getColumnIndex(columns[19]);
                            int ji = c.getColumnIndex(columns[20]);
                            int blnk = c.getColumnIndex(columns[21]);
                            int mid = c.getColumnIndex(columns[22]);
                            int cc = c.getColumnIndex(columns[23]);
                            int cl = c.getColumnIndex(columns[24]);
                            int bc = c.getColumnIndex(columns[25]);
                            c.getColumnIndex(columns[26]);
                            int pd = c.getColumnIndex(columns[27]);
                            int p = c.getColumnIndex(columns[28]);
                            int briefing = c.getColumnIndex(columns[29]);
                            int sPurchaseLimit = c.getColumnIndex(columns[30]);
                            int sNonRefundableServicePayment = c
                                    .getColumnIndex(columns[31]);
                            int sTransportationPayment = c
                                    .getColumnIndex(columns[32]);
                            int sCriticismPayment = c
                                    .getColumnIndex(columns[33]);
                            int sBonusPayment = c.getColumnIndex(columns[34]);
                            int sAllowRejection = c.getColumnIndex(columns[35]);
                            int sInProgress = c.getColumnIndex(columns[36]);
                            int sProjectName = c.getColumnIndex(columns[37]);
                            int sRegionName = c.getColumnIndex(columns[38]);
                            int deletedJob = c.getColumnIndex(columns[39]);
                            if (columns.length > 40) {
                                int projectid = c.getColumnIndex(columns[40]);
                                order.setProjectID(c.getString(projectid));
                            }

                            order.setIsJobDeleted(c.getString(deletedJob));
                            order.setProjectName(c.getString(sProjectName));
                            order.setRegionName(c.getString(sRegionName));
                            order.setIsJobInProgressOnServer(c
                                    .getString(sInProgress));
                            order.setAllowShoppersToRejectJobs(c
                                    .getString(sAllowRejection));
                            order.setsPurchaseLimit(c.getString(sPurchaseLimit));

                            order.setsNonRefundableServicePayment(c
                                    .getString(sNonRefundableServicePayment));

                            order.setsTransportationPayment(c
                                    .getString(sTransportationPayment));

                            order.setsCriticismPayment(c
                                    .getString(sCriticismPayment));

                            order.setsBonusPayment(c.getString(sBonusPayment));

                            order.setOrderID(c.getString(orderid));
                            order.setStatusName(c.getString(st));

                            order.setDate(c.getString(date));
                            order.setDescription(c.getString(desc));
                            order.setSetName(c.getString(sn));
                            order.setSetLink(c.getString(sl));
                            order.setClientName(c.getString(cn));
                            order.setBranchFullname(c.getString(bfn));
                            if (c.getString(bn) != null) {
                                sp = Html.fromHtml(c.getString(bn));
                                if (sp != null)
                                    order.setBranchName(sp.toString());
                            } else {
                                order.setBranchName("");
                            }
                            order.setCityName(c.getString(cityn));
                            order.setAddress(c.getString(addr));
                            order.setBranchPhone(c.getString(bp));
                            order.setOpeningHours(c.getString(oh));
                            order.setTimeStart(c.getString(ts));
                            order.setTimeEnd(c.getString(te));
                            order.setSetID(c.getString(sid));
                            order.setBranchLat(c.getString(bl));
                            order.setBranchLong(c.getString(blng));
                            order.setFullname(c.getString(fn));
                            order.setJobCount(c.getInt(jc));
                            order.setIndex(c.getInt(ji));
                            order.setBranchLink(c.getString(blnk));
                            order.setMassID(c.getString(mid));
                            order.setCheckerCode(c.getString(cc));
                            order.setCheckerLink(c.getString(cl));
                            order.setBranchCode(c.getString(bc));
                            // order.setset(c.getString(sc));
                            order.setsPurchaseDescription(c.getString(pd));
                            order.setsPurchase(c.getString(p));
                            order.setBriefingContent(c.getString(briefing));

                            if (order.getOrderID().contains("-")
                                    && order.getStatusName()
                                    .contains("rogress")) {
                                int i = 0;
                                i++;
                            }

                        }
                        orders.add(order);
                    } catch (Exception e) {
                        System.out.println("Exception:   " + e.toString());
                    }
                } while (c.moveToNext());
                c.close();
                DBAdapter.closeDataBase();
                return orders;
            } catch (Exception e) {
                System.out.println("Exception:   " + e.toString());
            } finally {
                if (!c.isClosed())
                    c.close();
                DBAdapter.closeDataBase();
            }
            return null;
        }
    }

    public static Order getOrder(String table, String[] columns, String orderby) {
        synchronized (lock) {
            ArrayList<Order> orders = new ArrayList<Order>();
            DBAdapter.openDataBase();
            if (DBAdapter.db == null)
                return null;
            Cursor c = DBAdapter.db.query(true, table, columns, orderby, null,
                    null, null, null, null);

            try {
                if (c.getCount() == 0) {
                    c.close();
                    DBAdapter.closeDataBase();
                    return null;
                }

                c.moveToFirst();

                Order order = new Order();
                if (columns.length == 3) {
                    int orderid = c.getColumnIndex(columns[0]);
                    int st = c.getColumnIndex(columns[1]);
                    int startime = c.getColumnIndex(columns[2]);
                    order.setOrderID(c.getString(orderid));
                    order.setStatusName(c.getString(st));
                    order.setTimeStart(c.getString(startime));
                } else {
                    int orderid = c.getColumnIndex(columns[0]);
                    int date = c.getColumnIndex(columns[1]);
                    int st = c.getColumnIndex(columns[2]);
                    int desc = c.getColumnIndex(columns[3]);
                    int sn = c.getColumnIndex(columns[4]);
                    int sl = c.getColumnIndex(columns[5]);
                    int cn = c.getColumnIndex(columns[6]);
                    int bfn = c.getColumnIndex(columns[7]);
                    int bn = c.getColumnIndex(columns[8]);
                    int cityn = c.getColumnIndex(columns[9]);
                    int addr = c.getColumnIndex(columns[10]);
                    int bp = c.getColumnIndex(columns[11]);
                    int oh = c.getColumnIndex(columns[12]);
                    int ts = c.getColumnIndex(columns[13]);
                    int te = c.getColumnIndex(columns[14]);
                    int sid = c.getColumnIndex(columns[15]);
                    int bl = c.getColumnIndex(columns[16]);
                    int blng = c.getColumnIndex(columns[17]);
                    int fn = c.getColumnIndex(columns[18]);
                    int jc = c.getColumnIndex(columns[19]);
                    int ji = c.getColumnIndex(columns[20]);
                    int blnk = c.getColumnIndex(columns[21]);
                    int mid = c.getColumnIndex(columns[22]);
                    int cc = c.getColumnIndex(columns[23]);
                    int cl = c.getColumnIndex(columns[24]);
                    int bc = c.getColumnIndex(columns[25]);
                    c.getColumnIndex(columns[26]);
                    int pd = c.getColumnIndex(columns[27]);
                    int p = c.getColumnIndex(columns[28]);
                    int briefing = c.getColumnIndex(columns[29]);
                    int sPurchaseLimit = c.getColumnIndex(columns[30]);
                    int sNonRefundableServicePayment = c
                            .getColumnIndex(columns[31]);
                    int sTransportationPayment = c.getColumnIndex(columns[32]);
                    int sCriticismPayment = c.getColumnIndex(columns[33]);
                    int sBonusPayment = c.getColumnIndex(columns[34]);

                    order.setsPurchaseLimit(c.getString(sPurchaseLimit));

                    order.setsNonRefundableServicePayment(c
                            .getString(sNonRefundableServicePayment));

                    order.setsTransportationPayment(c
                            .getString(sTransportationPayment));

                    order.setsCriticismPayment(c.getString(sCriticismPayment));

                    order.setsBonusPayment(c.getString(sBonusPayment));

                    order.setOrderID(c.getString(orderid));
                    order.setStatusName(c.getString(st));

                    order.setDate(c.getString(date));
                    order.setDescription(c.getString(desc));
                    order.setSetName(c.getString(sn));
                    order.setSetLink(c.getString(sl));
                    order.setClientName(c.getString(cn));
                    order.setBranchFullname(c.getString(bfn));
                    sp = Html.fromHtml(c.getString(bn));
                    if (sp != null)
                        order.setBranchName(sp.toString());
                    order.setCityName(c.getString(cityn));
                    order.setAddress(c.getString(addr));
                    order.setBranchPhone(c.getString(bp));
                    order.setOpeningHours(c.getString(oh));
                    order.setTimeStart(c.getString(ts));
                    order.setTimeEnd(c.getString(te));
                    order.setSetID(c.getString(sid));
                    order.setBranchLat(c.getString(bl));
                    order.setBranchLong(c.getString(blng));
                    order.setFullname(c.getString(fn));
                    order.setJobCount(c.getInt(jc));
                    order.setIndex(c.getInt(ji));
                    order.setBranchLink(c.getString(blnk));
                    order.setMassID(c.getString(mid));
                    order.setCheckerCode(c.getString(cc));
                    order.setCheckerLink(c.getString(cl));
                    order.setBranchCode(c.getString(bc));
                    // order.setset(c.getString(sc));
                    order.setsPurchaseDescription(c.getString(pd));
                    order.setsPurchase(c.getString(p));
                    order.setBriefingContent(c.getString(briefing));

                    if (order.getOrderID().contains("-")
                            && order.getStatusName().contains("rogress")) {
                        int i = 0;
                        i++;
                    }

                }

                c.close();
                DBAdapter.closeDataBase();
                return order;
            } catch (Exception e) {
                System.out.println("Exception:   " + e.toString());
            } finally {
                if (!c.isClosed())
                    c.close();
                DBAdapter.closeDataBase();
            }
            return null;
        }
    }

    public static String getBranchLink(String where) {
        synchronized (lock) {
            DBAdapter.openDataBase();
            if (DBAdapter.db == null)
                return null;

            String[] columns = new String[]{Constants.DB_TABLE_JOBLIST_BL,};
            Cursor c = DBAdapter.db.query(true, Constants.DB_TABLE_JOBLIST,
                    columns, where, null, null, null, null, null);

            try {
                if (c.getCount() == 0) {
                    c.close();
                    DBAdapter.closeDataBase();
                    return null;
                }

                c.moveToFirst();
                int blink = c.getColumnIndex(Constants.DB_TABLE_JOBLIST_BL);
                String bl = c.getString(blink);
                c.close();

                DBAdapter.closeDataBase();
                return bl;
            } catch (Exception e) {
                System.out.println("Exception:   " + e.toString());
            } finally {
                if (!c.isClosed())
                    c.close();
                DBAdapter.closeDataBase();
            }
            return null;
        }
    }

    public static ArrayList<CustomFields> getCustomFields(String orderid) {
        synchronized (lock) {
            String where = Constants.DB_TABLE_QUESTIONNAIRE_ORDERID + "="
                    + "\"" + orderid + "\"";
            ArrayList<CustomFields> branchProps = new ArrayList<CustomFields>();
            DBAdapter.openDataBase();
            if (DBAdapter.db == null)
                return null;
            Cursor c = null;
            String[] columns = new String[]{
                    Constants.DB_TABLE_CustomOrderFields_customfield_name,
                    Constants.DB_TABLE_CustomOrderFields_customfield_value,};

            try {
                c = DBAdapter.db.query(true,
                        Constants.DB_TABLE_CustomOrderFields, columns, where,
                        null, null, null, null, null);

                if (c.getCount() == 0) {
                    c.close();
                    DBAdapter.closeDataBase();
                    return null;
                }

                c.moveToFirst();
                do {
                    CustomFields branchProp = new CustomFields();
                    int name = c.getColumnIndex(columns[0]);
                    int value = c.getColumnIndex(columns[1]);
                    branchProp.setName(c.getString(name));
                    branchProp.setValue(c.getString(value));
                    if (branchProp.getValue() != null
                            && !branchProp.getValue().equals("")
                            && !branchProp.getValue().equals("0")
                            && !branchProp.getValue().equals("0.00"))
                        branchProps.add(branchProp);
                } while (c.moveToNext());
                c.close();
                DBAdapter.closeDataBase();
                return branchProps;
            } catch (Exception e) {
                System.out.println("Exception:   " + e.toString());
            } finally {
                if (!c.isClosed())
                    c.close();
                DBAdapter.closeDataBase();
            }
            return null;
        }
    }

    public static ArrayList<BranchProperties> getBranchPropds(String table,
                                                              String[] columns, String orderby) {
        synchronized (lock) {
            ArrayList<BranchProperties> branchProps = new ArrayList<BranchProperties>();
            DBAdapter.openDataBase();
            if (DBAdapter.db == null)
                return null;
            Cursor c = null;
            try {
                c = DBAdapter.db.query(true, table, columns, null, null, null,
                        null, orderby + " ASC", null);

                if (c.getCount() == 0) {
                    c.close();
                    DBAdapter.closeDataBase();
                    return null;
                }

                c.moveToFirst();
                do {
                    BranchProperties branchProp = new BranchProperties();
                    {
                        int valueid = c.getColumnIndex(columns[0]);
                        int propid = c.getColumnIndex(columns[1]);
                        int propname = c.getColumnIndex(columns[2]);
                        int content = c.getColumnIndex(columns[3]);
                        int ID = c.getColumnIndex(columns[4]);
                        branchProp.setValueID(c.getString(valueid));
                        branchProp.setPropID(c.getString(propid));
                        branchProp.setPropertyName(c.getString(propname));
                        branchProp.setContent(c.getString(content));
                        branchProp.setID(c.getString(ID));
                    }

                    branchProps.add(branchProp);
                } while (c.moveToNext());
                c.close();
                DBAdapter.closeDataBase();
                return branchProps;
            } catch (Exception e) {
                System.out.println("Exception:   " + e.toString());
            } finally {
                if (!c.isClosed())
                    c.close();
                DBAdapter.closeDataBase();
            }
            return null;
        }
    }

    public static ArrayList<SubmitQuestionnaireData> getSubmitQuestionnaireList(
            String table, String[] columns, String whereclause, String orderby) {
        synchronized (lock) {

            DBAdapter.closeDataBase();
            DBAdapter.openDataBase(true);
            whereclause = Constants.DB_TABLE_SUBMITSURVEY_UNEMPTY_QUES_COUNT
                    + " != '-1'";
            Cursor c = DBAdapter.db.query(true, table, columns, whereclause,
                    null, null, null, orderby + " ASC", null);
            // DBAdapter.closeDataBase();
            try {

                if (c.getCount() == 0) {
                    c.close();
                    DBAdapter.closeDataBase();
                    return null;
                } else {
                    DBAdapter.closeDataBase();
                    DBAdapter.openDataBase(true);
                }

                ArrayList<SubmitQuestionnaireData> submitQuestionnaireData = new ArrayList<SubmitQuestionnaireData>();
                c.moveToFirst();
                do {
                    SubmitQuestionnaireData qd = new SubmitQuestionnaireData();
                    int oid = c.getColumnIndex(columns[0]);
                    int ft = c.getColumnIndex(columns[1]);
                    int slt = c.getColumnIndex(columns[2]);
                    int slng = c.getColumnIndex(columns[3]);
                    int elt = c.getColumnIndex(columns[4]);
                    int elng = c.getColumnIndex(columns[5]);
                    int stime = c.getColumnIndex(columns[6]);
                    int ftime = c.getColumnIndex(columns[7]);
                    int totalSent = c.getColumnIndex(columns[8]);
                    int sid = c.getColumnIndex(columns[9]);
                    int unix = c.getColumnIndex(columns[10]);
                    int DB_TABLE_SUBMITSURVEY_purchase_details = c
                            .getColumnIndex(columns[11]);
                    int DB_TABLE_SUBMITSURVEY_purchase_payment = c
                            .getColumnIndex(columns[12]);
                    int DB_TABLE_SUBMITSURVEY_purchase_description = c
                            .getColumnIndex(columns[13]);
                    int DB_TABLE_SUBMITSURVEY_service_invoice_number = c
                            .getColumnIndex(columns[14]);
                    int DB_TABLE_SUBMITSURVEY_service_payment = c
                            .getColumnIndex(columns[15]);
                    int DB_TABLE_SUBMITSURVEY_service_description = c
                            .getColumnIndex(columns[16]);
                    int DB_TABLE_SUBMITSURVEY_transportation_payment = c
                            .getColumnIndex(columns[17]);
                    int DB_TABLE_SUBMITSURVEY_transportation_description = c
                            .getColumnIndex(columns[18]);

                    int DB_TABLE_SUBMITSURVEY_rs = c
                            .getColumnIndex(columns[19]);

                    qd.setRs(c.getString(DB_TABLE_SUBMITSURVEY_rs));

                    qd.setDB_TABLE_SUBMITSURVEY_purchase_details(c
                            .getString(DB_TABLE_SUBMITSURVEY_purchase_details));

                    qd.setDB_TABLE_SUBMITSURVEY_purchase_payment(c
                            .getString(DB_TABLE_SUBMITSURVEY_purchase_payment));

                    qd.setDB_TABLE_SUBMITSURVEY_purchase_description(c
                            .getString(DB_TABLE_SUBMITSURVEY_purchase_description));

                    qd.setDB_TABLE_SUBMITSURVEY_service_invoice_number(c
                            .getString(DB_TABLE_SUBMITSURVEY_service_invoice_number));

                    qd.setDB_TABLE_SUBMITSURVEY_service_payment(c
                            .getString(DB_TABLE_SUBMITSURVEY_service_payment));

                    qd.setDB_TABLE_SUBMITSURVEY_service_description(c
                            .getString(DB_TABLE_SUBMITSURVEY_service_description));

                    qd.setDB_TABLE_SUBMITSURVEY_transportation_payment(c
                            .getString(DB_TABLE_SUBMITSURVEY_transportation_payment));

                    qd.setDB_TABLE_SUBMITSURVEY_transportation_description(c
                            .getString(DB_TABLE_SUBMITSURVEY_transportation_description));

                    qd.setOrderid(c.getString(oid));
                    qd.setFt(c.getString(ft));
                    qd.setSlt(c.getString(slt));
                    qd.setSlng(c.getString(slng));
                    qd.setElt(c.getString(elt));
                    qd.setElng(c.getString(elng));
                    qd.setFtime(c.getString(ftime));
                    qd.setStime(c.getString(stime));
                    qd.setTotalSent(c.getString(totalSent));
                    qd.setSID(c.getString(sid));
                    qd.setUnix(c.getString(unix));

                    qd = GetSubmitQuota(qd);
                    // if (qd.getTotalIntSent() > 0)
                    submitQuestionnaireData.add(qd);
                } while (c.moveToNext());
                c.close();
                DBAdapter.closeDataBase();
                return submitQuestionnaireData;
            } catch (Exception e) {
                System.out.println("Exception:   " + e.toString());
            } finally {
                if (!c.isClosed())
                    c.close();
                DBAdapter.closeDataBase();
            }
            return null;
        }
    }

    public static SubmitQuestionnaireData getSubmitQuestionnaireSingle(
            String table, String[] columns, String whereclause, String orderby) {
        synchronized (lock) {

            DBAdapter.closeDataBase();
            DBAdapter.openDataBase(true);
            whereclause = whereclause

                    + " and " + Constants.DB_TABLE_SUBMITSURVEY_UNEMPTY_QUES_COUNT
                    + " != '-1'";
            // whereclause = whereclause + " or "
            // + Constants.DB_TABLE_SUBMITSURVEY_UNEMPTY_QUES_COUNT
            // + " = '-1'";
            Cursor c = DBAdapter.db.query(true, table, columns, whereclause,
                    null, null, null, orderby + " ASC", null);
            // DBAdapter.closeDataBase();
            try {

                if (c.getCount() == 0) {
                    c.close();
                    DBAdapter.closeDataBase();
                    return null;
                } else {
                    DBAdapter.closeDataBase();
                    DBAdapter.openDataBase(true);
                }

                c.moveToFirst();

                SubmitQuestionnaireData qd = new SubmitQuestionnaireData();
                int oid = c.getColumnIndex(columns[0]);
                int ft = c.getColumnIndex(columns[1]);
                int slt = c.getColumnIndex(columns[2]);
                int slng = c.getColumnIndex(columns[3]);
                int elt = c.getColumnIndex(columns[4]);
                int elng = c.getColumnIndex(columns[5]);
                int stime = c.getColumnIndex(columns[6]);
                int ftime = c.getColumnIndex(columns[7]);
                int totalSent = c.getColumnIndex(columns[8]);
                int sid = c.getColumnIndex(columns[9]);
                int unix = c.getColumnIndex(columns[10]);
                int DB_TABLE_SUBMITSURVEY_purchase_details = c
                        .getColumnIndex(columns[11]);
                int DB_TABLE_SUBMITSURVEY_purchase_payment = c
                        .getColumnIndex(columns[12]);
                int DB_TABLE_SUBMITSURVEY_purchase_description = c
                        .getColumnIndex(columns[13]);
                int DB_TABLE_SUBMITSURVEY_service_invoice_number = c
                        .getColumnIndex(columns[14]);
                int DB_TABLE_SUBMITSURVEY_service_payment = c
                        .getColumnIndex(columns[15]);
                int DB_TABLE_SUBMITSURVEY_service_description = c
                        .getColumnIndex(columns[16]);
                int DB_TABLE_SUBMITSURVEY_transportation_payment = c
                        .getColumnIndex(columns[17]);
                int DB_TABLE_SUBMITSURVEY_transportation_description = c
                        .getColumnIndex(columns[18]);
                int DB_TABLE_SUBMITSURVEY_RS = c
                        .getColumnIndex(columns[19]);

                qd.setRs(c
                        .getString(DB_TABLE_SUBMITSURVEY_RS));

                qd.setDB_TABLE_SUBMITSURVEY_purchase_details(c
                        .getString(DB_TABLE_SUBMITSURVEY_purchase_details));

                qd.setDB_TABLE_SUBMITSURVEY_purchase_payment(c
                        .getString(DB_TABLE_SUBMITSURVEY_purchase_payment));

                qd.setDB_TABLE_SUBMITSURVEY_purchase_description(c
                        .getString(DB_TABLE_SUBMITSURVEY_purchase_description));

                qd.setDB_TABLE_SUBMITSURVEY_service_invoice_number(c
                        .getString(DB_TABLE_SUBMITSURVEY_service_invoice_number));

                qd.setDB_TABLE_SUBMITSURVEY_service_payment(c
                        .getString(DB_TABLE_SUBMITSURVEY_service_payment));

                qd.setDB_TABLE_SUBMITSURVEY_service_description(c
                        .getString(DB_TABLE_SUBMITSURVEY_service_description));

                qd.setDB_TABLE_SUBMITSURVEY_transportation_payment(c
                        .getString(DB_TABLE_SUBMITSURVEY_transportation_payment));

                qd.setDB_TABLE_SUBMITSURVEY_transportation_description(c
                        .getString(DB_TABLE_SUBMITSURVEY_transportation_description));

                qd.setOrderid(c.getString(oid));
                qd.setFt(c.getString(ft));
                qd.setSlt(c.getString(slt));
                qd.setSlng(c.getString(slng));
                qd.setElt(c.getString(elt));
                qd.setElng(c.getString(elng));
                qd.setFtime(c.getString(ftime));
                qd.setStime(c.getString(stime));
                qd.setTotalSent(c.getString(totalSent));
                qd.setSID(c.getString(sid));
                qd.setUnix(c.getString(unix));

                c.close();
                DBAdapter.closeDataBase();
                return qd;
            } catch (Exception e) {
                System.out.println("Exception:   " + e.toString());
            } finally {
                if (!c.isClosed())
                    c.close();
                DBAdapter.closeDataBase();
            }
            return null;
        }
    }

    public static ArrayList<Quota> getQuotas(String order_id) {
        ArrayList<Quota> qd = new ArrayList<Quota>();
        String table_inner = Constants.DB_TABLE_SUBMITQUOTA;
        String[] columns_inner = new String[]{
                Constants.DB_TABLE_SUBMITQUOTA_OrderID,
                Constants.DB_TABLE_SUBMITQUOTA_QuotaID,};

        DBAdapter.openDataBase();
        Cursor cc = DBAdapter.db.query(true, table_inner, columns_inner,
                Constants.DB_TABLE_SUBMITQUOTA_OrderID + "=" + "\"" + order_id
                        + "\"", null, null, null, null, null);
        if (cc.getCount() == 0) {
            cc.close();
            DBAdapter.closeDataBase();
            return null;
        }

        cc.moveToFirst();
        Quota q = new Quota();
        do {
            int DB_TABLE_SUBMITQUOTA_OrderID = cc
                    .getColumnIndex(columns_inner[0]);
            int DB_TABLE_SUBMITQUOTA_QuotaID = cc
                    .getColumnIndex(columns_inner[1]);

            q.setsquoID(cc.getString(DB_TABLE_SUBMITQUOTA_QuotaID));
            q.setSurveyLink(cc.getString(DB_TABLE_SUBMITQUOTA_OrderID));

            {
                qd.add(q);
            }
            q = new Quota();
        } while (cc.moveToNext());

        return qd;

    }

    private static SubmitQuestionnaireData GetSubmitQuota(
            SubmitQuestionnaireData qd) {
        String table_inner = Constants.DB_TABLE_SUBMITQUOTA;
        String[] columns_inner = new String[]{
                Constants.DB_TABLE_SUBMITQUOTA_OrderID,
                Constants.DB_TABLE_SUBMITQUOTA_QuotaID,};

        DBAdapter.openDataBase();
        Cursor cc = DBAdapter.db.query(
                true,
                table_inner,
                columns_inner,
                Constants.DB_TABLE_SUBMITQUOTA_OrderID + "=" + "\""
                        + qd.getOrderid() + "\"", null, null, null, null, null);
        if (cc.getCount() == 0) {
            cc.close();
            DBAdapter.closeDataBase();
            return qd;
        }

        cc.moveToFirst();
        Quota q = new Quota();
        do {
            int DB_TABLE_SUBMITQUOTA_OrderID = cc
                    .getColumnIndex(columns_inner[0]);
            int DB_TABLE_SUBMITQUOTA_QuotaID = cc
                    .getColumnIndex(columns_inner[1]);

            q.setsquoID(cc.getString(DB_TABLE_SUBMITQUOTA_QuotaID));

            {
                qd.addQuota(q);
            }
            q = new Quota();
        } while (cc.moveToNext());

        return qd;
    }

    public static ArrayList<QuestionnaireData> getQuestionnaireList(
            String table, String[] columns, String whereclause, String orderby,
            ArrayList<QuestionnaireData> items) {
        synchronized (lock) {
            DBAdapter.openDataBase(true);
            Cursor c = DBAdapter.db.query(true, table, columns, whereclause,
                    null, null, null, orderby + " ASC", null);
            // DBAdapter.closeDataBase();
            try {
                if (c.getCount() == 0) {
                    c.close();
                    DBAdapter.closeDataBase();
                    return items;
                }
                c.moveToFirst();
                DBAdapter.openDataBase();
                do {
                    QuestionnaireData qd = new QuestionnaireData();
                    int dataID = c.getColumnIndex(columns[0]);
                    int qtext = c.getColumnIndex(columns[1]);
                    int orderid = c.getColumnIndex(columns[2]);
                    int qtl = c.getColumnIndex(columns[3]);
                    int ot = c.getColumnIndex(columns[4]);
                    int bid = c.getColumnIndex(columns[5]);
                    int wid = c.getColumnIndex(columns[6]);
                    int ft = c.getColumnIndex(columns[7]);
                    int ftime = c.getColumnIndex(columns[8]);
                    int loopinfo = c.getColumnIndex(columns[9]);
                    // int mitype = c.getColumnIndex(columns[10]);
                    //
                    // qd.setMiType(c.getString(mitype));
                    qd.setLoopinfo(c.getString(loopinfo));
                    qd.setDataID(c.getString(dataID));
                    qd.setQuestionText(c.getString(qtext));
                    qd.setOrderID(c.getString(orderid));
                    qd.setQuestionTypeLink(c.getString(qtl));
                    qd.setObjectType(c.getString(ot));
                    qd.setBranchID(c.getString(bid));
                    qd.setWorkerID(c.getString(wid));
                    qd.setFreetext(c.getString(ft));
                    qd.setAnswerText(c.getString(ft));
                    qd.setFinishtime(c.getString(ftime));

                    try {
                        List<Answers> ans = getAnswersList(
                                Constants.DB_TABLE_ANSWERS,
                                new String[]{
                                        Constants.DB_TABLE_ANSWERS_ANSWERID,
                                        Constants.DB_TABLE_ANSWERS_ATEXT,
                                        Constants.DB_TABLE_ANSWERS_MI,
                                        Constants.DB_TABLE_ANSWERS_BRANCHID,
                                        Constants.DB_TABLE_ANSWERS_WORKERID,
                                        Constants.DB_TABLE_ANSWERS_DISPLAYCONDITION,
                                        Constants.DB_TABLE_ANSWERS_RANK,
                                        Constants.DB_TABLE_ANSWERS_ENCRYPTED},
                                whereclause + " AND "
                                        + Constants.DB_TABLE_ANSWERS_DATAID
                                        + "=" + "\"" + qd.getDataID() + "\"",
                                Constants.DB_TABLE_ANSWERS_ANSWERID);
                        for (int i = 0; i < ans.size(); i++)
                            qd.setAnswers(ans.get(i));

                        if (ans.size() > 0 && ans.get(0).getAnswerID() == null) {
                            if (ans.get(0).getAnswer() != null
                                    && ans.get(0).getAnswer().toLowerCase()
                                    .equals("not filled"))
                                qd.setAnswerText("");
                            else
                                qd.setAnswerText(ans.get(0).getAnswer());
                            qd.setMi(ans.get(0).getMi());
                        } else if (ans.size() > 0) {
                            qd.setAnswerText(ans.get(0).getMi());
                        }
                    } catch (Exception ex) {
                        int i = 0;
                        i++;
                    }
                    items.add(qd);
                } while (c.moveToNext());
                c.close();
                DBAdapter.closeDataBase();
            } catch (Exception e) {
                System.out.println("getQuestionnaireList() Exception:   "
                        + e.toString());
            } finally {
                if (!c.isClosed())
                    c.close();
                DBAdapter.closeDataBase();
            }
            return items;
        }
    }

    public static POS_Shelf getShelfItems(String table, String[] columns,
                                          String whereclause, POS_Shelf items, boolean isQuestionScreen) {
        if (items.listProducts == null || items.listProductLocations == null)
            return items;
        synchronized (lock) {
            DBAdapter.openDataBase();
            Cursor c = DBAdapter.db.query(true, table, columns, whereclause,
                    null, null, null, null, null);
            // // DBAdapter.closeDataBase();
            try {
                if (c.getCount() == 0) {
                    c.close();
                    DBAdapter.closeDataBase();
                    if (!isQuestionScreen)
                        items = null;
                } else {
                    c.moveToFirst();
                    DBAdapter.openDataBase();
                    do {
                        int location_id = c.getColumnIndex(columns[0]);
                        int order_id = c.getColumnIndex(columns[1]);
                        int price = c.getColumnIndex(columns[2]);
                        int product_id = c.getColumnIndex(columns[3]);
                        int property_id = c.getColumnIndex(columns[4]);
                        int quantity = c.getColumnIndex(columns[5]);
                        int setid = c.getColumnIndex(columns[6]);
                        int note = c.getColumnIndex(columns[7]);
                        int date = c.getColumnIndex(columns[8]);

                        String location_ids = c.getString(location_id);
                        String order_ids = c.getString(order_id);
                        String prices = c.getString(price);
                        String product_ids = c.getString(product_id);
                        String property_ids = c.getString(property_id);
                        String quantitys = c.getString(quantity);
                        String setids = c.getString(setid);
                        String notes = c.getString(note);
                        String dates = c.getString(date);

                        items.setItemFromdb(location_ids, order_ids, prices,
                                product_ids, property_ids, quantitys, setids,
                                notes, dates);

                    } while (c.moveToNext());
                    c.close();
                    DBAdapter.closeDataBase();
                }
            } catch (Exception e) {
                System.out.println("getQuestionnaireList() Exception:   "
                        + e.toString());
            } finally {
                if (!c.isClosed())
                    c.close();
                DBAdapter.closeDataBase();
            }
        }

        return items;
    }

    public static String getShelfSetIdItemsForJobList(String table,
                                                      String[] columns, String whereclause) {
        // if (items.listProducts == null || items.listProductLocations == null)
        // return items;
        synchronized (lock) {
            DBAdapter.openDataBase();
            Cursor c = DBAdapter.db.query(true, table, columns, whereclause,
                    null, null, null, null, null);
            // // DBAdapter.closeDataBase();
            try {
                if (c.getCount() == 0) {
                    c.close();
                    DBAdapter.closeDataBase();
                } else {
                    c.moveToFirst();
                    DBAdapter.openDataBase();

                    int setid = c.getColumnIndex(columns[0]);

                    String setids = c.getString(setid);
                    c.close();

                    DBAdapter.closeDataBase();
                    return setids;
                }
            } catch (Exception e) {
                System.out.println("getQuestionnaireList() Exception:   "
                        + e.toString());
            } finally {
                if (!c.isClosed())
                    c.close();
                DBAdapter.closeDataBase();
            }
        }

        return null;
    }

    public static String getSetIdFromOrder(String table, String[] columns,
                                           String whereclause) {
        // if (items.listProducts == null || items.listProductLocations == null)
        // return items;
        synchronized (lock) {
            DBAdapter.openDataBase();
            Cursor c = DBAdapter.db.query(true, table, columns, whereclause,
                    null, null, null, null, null);
            // // DBAdapter.closeDataBase();
            try {
                if (c.getCount() == 0) {
                    c.close();
                    DBAdapter.closeDataBase();
                } else {
                    c.moveToFirst();
                    DBAdapter.openDataBase();

                    int setid = c.getColumnIndex(columns[0]);

                    String setids = c.getString(setid);
                    c.close();

                    DBAdapter.closeDataBase();
                    return setids;
                }
            } catch (Exception e) {
                System.out.println("getQuestionnaireList() Exception:   "
                        + e.toString());
            } finally {
                if (!c.isClosed())
                    c.close();
                DBAdapter.closeDataBase();
            }
        }

        return null;
    }

    public static ArrayList<filePathDataID> getQuestionnaireUploadFilesInDB(
            String table, String[] columns, String whereclause, String orderby,
            ArrayList<filePathDataID> items) {
        synchronized (lock) {
            // Constants.UPLOAD_FILe_MEDIAFILE,
            // Constants.UPLOAD_FILe_DATAID,
            // Constants.UPLOAD_FILe_ORDERID,
            // Constants.UPLOAD_FILe_BRANCH_NAME,
            // Constants.UPLOAD_FILe_CLIENT_NAME,
            // Constants.UPLOAD_FILe_DATE,
            // Constants.UPLOAD_FILe_SET_NAME,
            // Constants.UPLOAD_FILe_SAMPLE_SIZE,
            //Constants.UPLOAD_FILe_PRODUCTID,
            //Constants.UPLOAD_FILe_LOCATIONID,

            items.clear();
            DBAdapter.openDataBase();
            Cursor c = DBAdapter.db.query(true, table, columns, whereclause,
                    null, Constants.UPLOAD_FILe_MEDIAFILE, null, orderby
                            + " ASC", null);
            // DBAdapter.closeDataBase();
            try {
                int count = c.getCount();
                count += 0;
                if (c.getCount() == 0) {
                    c.close();
                    DBAdapter.closeDataBase();
                    return items;
                }
                c.moveToFirst();
                DBAdapter.openDataBase();
                do {
                    int mediafile = c.getColumnIndex(columns[0]);
                    int DataID = c.getColumnIndex(columns[1]);
                    int UPLOAD_FILe_ORDERID = c.getColumnIndex(columns[2]);
                    int UPLOAD_FILe_BRANCH_NAME = c.getColumnIndex(columns[3]);
                    int UPLOAD_FILe_CLIENT_NAME = c.getColumnIndex(columns[4]);
                    int UPLOAD_FILe_DATE = c.getColumnIndex(columns[5]);
                    int UPLOAD_FILe_SET_NAME = c.getColumnIndex(columns[6]);
                    int UPLOAD_FILe_SAMPLE_SIZE = c.getColumnIndex(columns[7]);
                    int UPLOAD_FILe_PRODUCTID = c.getColumnIndex(columns[8]);
                    int UPLOAD_FILe_Location = c.getColumnIndex(columns[9]);
                    filePathDataID fid = new filePathDataID();
                    // fid.setDataID(c.getColumnIndex(columns[0]););
                    fid.setUPLOAD_FILe_PRODUCTID(c.getString(UPLOAD_FILe_PRODUCTID));
                    fid.setUPLOAD_FILe_LOCATIONID(c.getString(UPLOAD_FILe_Location));
                    fid.setFilePath(c.getString(mediafile));
                    fid.setDataID(c.getString(DataID), false);
                    fid.setUPLOAD_FILe_Sample_size(c
                            .getString(UPLOAD_FILe_SAMPLE_SIZE));
                    fid.setUPLOAD_FILe_ORDERID(c.getString(UPLOAD_FILe_ORDERID));
                    fid.setUPLOAD_FILe_BRANCH_NAME(c
                            .getString(UPLOAD_FILe_BRANCH_NAME));
                    fid.setUPLOAD_FILe_CLIENT_NAME(c
                            .getString(UPLOAD_FILe_CLIENT_NAME));
                    fid.setUPLOAD_FILe_DATE(c.getString(UPLOAD_FILe_DATE));
                    fid.setUPLOAD_FILe_SET_NAME(c
                            .getString(UPLOAD_FILe_SET_NAME));
                    items.add(fid);
                } while (c.moveToNext());
                c.close();
                DBAdapter.closeDataBase();
            } catch (Exception e) {
                System.out.println("getQuestionnaireList() Exception:   "
                        + e.toString());
            } finally {
                if (!c.isClosed())
                    c.close();
                DBAdapter.closeDataBase();
            }
            return items;
        }
    }

    public static ArrayList<filePathDataID> getOrphanQuestionnaireUploadFiles(
            String table, String[] columns, String whereclause, String orderby,
            ArrayList<filePathDataID> items) {
        synchronized (lock) {
            // Constants.UPLOAD_FILe_MEDIAFILE,
            // Constants.UPLOAD_FILe_DATAID,
            // Constants.UPLOAD_FILe_ORDERID,
            // Constants.UPLOAD_FILe_BRANCH_NAME,
            // Constants.UPLOAD_FILe_CLIENT_NAME,
            // Constants.UPLOAD_FILe_DATE,
            // Constants.UPLOAD_FILe_SET_NAME,
            // Constants.UPLOAD_FILe_SAMPLE_SIZE,
            items.clear();
            DBAdapter.openDataBase();
            Cursor c = DBAdapter.db.query(true, table, columns, null, null,
                    Constants.UPLOAD_FILe_MEDIAFILE, null, orderby + " ASC",
                    null);
            // DBAdapter.closeDataBase();
            try {
                if (c.getCount() == 0) {
                    c.close();
                    DBAdapter.closeDataBase();
                    return items;
                }
                c.moveToFirst();
                DBAdapter.openDataBase();
                do {
                    int mediafile = c.getColumnIndex(columns[0]);
                    int DataID = c.getColumnIndex(columns[1]);
                    int UPLOAD_FILe_ORDERID = c.getColumnIndex(columns[2]);
                    int UPLOAD_FILe_BRANCH_NAME = c.getColumnIndex(columns[3]);
                    int UPLOAD_FILe_CLIENT_NAME = c.getColumnIndex(columns[4]);
                    int UPLOAD_FILe_DATE = c.getColumnIndex(columns[5]);
                    int UPLOAD_FILe_SET_NAME = c.getColumnIndex(columns[6]);
                    int UPLOAD_FILe_SAMPLE_SIZE = c.getColumnIndex(columns[7]);


                    int UPLOAD_FILe_PRODUCTID = c.getColumnIndex(columns[8]);
                    int UPLOAD_FILe_Location = c.getColumnIndex(columns[9]);
                    filePathDataID fid = new filePathDataID();
                    // fid.setDataID(c.getColumnIndex(columns[0]););
                    fid.setUPLOAD_FILe_PRODUCTID(c.getString(UPLOAD_FILe_PRODUCTID));
                    fid.setUPLOAD_FILe_LOCATIONID(c.getString(UPLOAD_FILe_Location));
                    fid.setFilePath(c.getString(mediafile));
                    fid.setDataID(c.getString(DataID), false);
                    fid.setUPLOAD_FILe_Sample_size(c
                            .getString(UPLOAD_FILe_SAMPLE_SIZE));
                    fid.setUPLOAD_FILe_ORDERID(c.getString(UPLOAD_FILe_ORDERID));
                    fid.setUPLOAD_FILe_BRANCH_NAME(c
                            .getString(UPLOAD_FILe_BRANCH_NAME));
                    fid.setUPLOAD_FILe_CLIENT_NAME(c
                            .getString(UPLOAD_FILe_CLIENT_NAME));
                    fid.setUPLOAD_FILe_DATE(c.getString(UPLOAD_FILe_DATE));
                    fid.setUPLOAD_FILe_SET_NAME(c
                            .getString(UPLOAD_FILe_SET_NAME));
                    items.add(fid);
                } while (c.moveToNext());
                c.close();
                DBAdapter.closeDataBase();
            } catch (Exception e) {
                System.out.println("getQuestionnaireList() Exception:   "
                        + e.toString());
            } finally {
                if (!c.isClosed())
                    c.close();
                DBAdapter.closeDataBase();
            }

            ArrayList<filePathDataID> itemstemp = new ArrayList<filePathDataID>();
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).getUPLOAD_FILe_DATE() == null
                        || items.get(i).getUPLOAD_FILe_DATE().length() <= 0) {

                } else {
                    itemstemp.add(items.get(i));
                }
            }
            return itemstemp;
        }
    }

    public static ArrayList<Answers> getAnswersList(String table,
                                                    String[] columns, String whereclause, String orderby) {
        synchronized (lock) {
            ArrayList<Answers> litems = new ArrayList<Answers>();
            DBAdapter.openDataBase();
            Cursor c = DBAdapter.db.query(true, table, columns, whereclause,
                    null, null, null, orderby + " ASC", null);
            try {
                if (c.getCount() == 0) {
                    c.close();
                    DBAdapter.closeDataBase();
                    return litems;
                }
                c.moveToFirst();
                do {
                    Answers items = new Answers();
                    int ansID = c.getColumnIndex(columns[0]);
                    int anstext = c.getColumnIndex(columns[1]);
                    int mi = c.getColumnIndex(columns[2]);
                    int dc = c.getColumnIndex(columns[5]);
                    int rank = c.getColumnIndex(columns[6]);

                    int encryption = c
                            .getColumnIndex(Constants.DB_TABLE_ANSWERS_ENCRYPTED);
                    String isEncryption = null;
                    if (encryption >= 0)
                        isEncryption = c.getString(encryption);

                    // int bid = c.getColumnIndex(columns[3]);
                    // int wid = c.getColumnIndex(columns[4]);
                    String ans = c.getString(anstext);
                    items.setAnswerID(c.getString(ansID));
                    if (isEncryption != null && isEncryption.contains("true")) {
                        items.setAnswer(DBAdapter.decrypt(ans,
                                Constants.EncryptionKey));
                        items.setMi(DBAdapter.decrypt(c.getString(mi),
                                Constants.EncryptionKey));
                    } else {
                        items.setAnswer(ans);
                        items.setMi(c.getString(mi));
                    }
                    items.setAnswerDisplayCondition(c.getString(dc));
                    int rankk = 0;
                    try {
                        rankk = Integer.parseInt(c.getString(rank));
                    } catch (Exception ex) {

                    }
                    items.setRank(rankk);
                    // items.setAnswerID(c.getString(bid));
                    // items.setAnswerID(c.getString(wid));

                    litems.add(items);
                } while (c.moveToNext());
                c.close();
                DBAdapter.closeDataBase();
            } catch (Exception e) {
                System.out.println("Exception:   " + e.toString());
            } finally {
                if (!c.isClosed())
                    c.close();
                DBAdapter.closeDataBase();
            }
            return litems;
        }
    }

    private static void deleteAllRecords(String table) {
        synchronized (lock) {
            DBAdapter.openDataBase();
            DBAdapter.db.delete(table, null, null);
            DBAdapter.closeDataBase();
        }
    }

    public static void deletePOSdata(String table, String where) {
        DBAdapter.openDataBase();
        DBAdapter.db.delete(table, where, null);
        DBAdapter.closeDataBase();
    }

    private static void deleteRecord(String OrderID) {
        synchronized (lock) {
            DBAdapter.openDataBase();
            String where = Constants.DB_TABLE_QUESTIONNAIRE_ORDERID + "="
                    + "\"" + OrderID + "\"";
            DBAdapter.db.delete(Constants.DB_TABLE_QUESTIONNAIRE, where, null);
            DBAdapter.openDataBase();
            DBAdapter.db.delete(Constants.DB_TABLE_ANSWERS, where, null);
            DBAdapter.closeDataBase();
        }
    }

    private static void deleteQuestionRecord(String dataID) {
        synchronized (lock) {

            // public static final String DB_TABLE_QTITLES = "QuestionTitles";
            // public static final String DB_TABLE_QLINKS = "QuestionLinks";
            // public static final String DB_TABLE_QGROUPS = "QuestionGroups";
            //
            DBAdapter.openDataBase();
            String where = Constants.DB_TABLE_QTITLES_DID + "=" + "\"" + dataID
                    + "\"";
            DBAdapter.db.delete(Constants.DB_TABLE_QTITLES, where, null);
            DBAdapter.openDataBase();
            DBAdapter.db.delete(Constants.DB_TABLE_QLINKS, where, null);
            DBAdapter.openDataBase();
            DBAdapter.db.delete(Constants.DB_TABLE_QGROUPS, where, null);
            DBAdapter.closeDataBase();
        }
    }

    public static void updateAnswerData(String table, String[] columns,
                                        QuestionnaireData questionData) {
        synchronized (lock) {
            ArrayList<Answers> ansList = questionData.getAnswersList();
            if (ansList == null || (ansList != null && ansList.size() == 0)) {
                ContentValues values = new ContentValues();
                values.put(columns[1], DBAdapter.encrypt(
                        questionData.getAnswerText(), Constants.EncryptionKey));
                values.put(columns[3], questionData.getDataID());
                values.put(columns[4], questionData.getOrderID());
                values.put(Constants.DB_TABLE_ANSWERS_ENCRYPTED, "true");
                values.put(columns[5], DBAdapter.encrypt(questionData.getMi(),
                        Constants.EncryptionKey));
                DBAdapter.openDataBase();
                DBAdapter.db.update(table, values, null, null);
                DBAdapter.closeDataBase();
                values = null;
            } else {
                // DBAdapter.openDataBase();
                for (int index = 0; index < ansList.size(); index++) {
                    Answers qd = ansList.get(index);
                    ContentValues values = new ContentValues();
                    values.put(columns[0], qd.getAnswerID());

                    values.put(columns[1], DBAdapter.encrypt(qd.getAnswer(),
                            Constants.EncryptionKey));
                    values.put(Constants.DB_TABLE_ANSWERS_ENCRYPTED, "true");
                    values.put(columns[2], qd.getValue());
                    values.put(columns[3], questionData.getDataID());
                    values.put(columns[4], questionData.getOrderID());
                    DBAdapter.openDataBase();
                    DBAdapter.db.update(table, values, null, null);
                    values = null;
                }
                DBAdapter.closeDataBase();
            }
        }
    }

    public static void deleteRecordbyOrdeid(String where) {
        synchronized (lock) {
            DBAdapter.openDataBase();
            DBAdapter.db.delete(Constants.DB_TABLE_QUESTIONNAIRE, where, null);
            DBAdapter.openDataBase();
            DBAdapter.db.delete(Constants.DB_TABLE_ANSWERS, where, null);
            DBAdapter.openDataBase();
            DBAdapter.db.delete(Constants.DB_TABLE_SUBMITSURVEY, where, null);
            DBAdapter.openDataBase();
            DBAdapter.db.delete(Constants.DB_TABLE_SUBMITQUOTA, where, null);
            DBAdapter.openDataBase();
            DBAdapter.db.delete(Constants.DB_TABLE_ORDERS, where, null);
            DBAdapter.closeDataBase();
            DBAdapter.db.delete(Constants.DB_TABLE_CustomOrderFields, where,
                    null);
            DBAdapter.closeDataBase();

        }
        // db.endTransaction();
    }

    public static void saveProps(ArrayList<BranchProperties> branchProps) {
        if (branchProps == null)
            return;
        synchronized (lock) {
            ContentValues values = new ContentValues();
            for (int ordercount = 0; ordercount < branchProps.size(); ordercount++) {
                values.clear();
                BranchProperties order = branchProps.get(ordercount);
                values.put(Constants.DB_TABLE_BRANCH_PROPS_PropID,
                        order.getPropID());
                values.put(Constants.DB_TABLE_BRANCH_PROPS_BranchID,
                        order.getID());
                values.put(Constants.DB_TABLE_BRANCH_PROPS_ValueID,
                        order.getValueID());
                values.put(Constants.DB_TABLE_BRANCH_PROPS_PropertyName,
                        order.getPropertyName());
                values.put(Constants.DB_TABLE_BRANCH_PROPS_Content,
                        order.getContent());
                DBAdapter.openDataBase();

                try {
                    DBAdapter.openDataBase();
                    DBAdapter.db.insert(Constants.DB_TABLE_BRANCH_PROPS, null,
                            values);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            DBAdapter.closeDataBase();
            values = null;
        }

    }

    public static void saveCustomFields(ArrayList<String> CustomFieldNames,
                                        ArrayList<String> CustomFieldVals, String orderid) {
        if (CustomFieldNames == null)
            return;
        synchronized (lock) {
            ContentValues values = new ContentValues();
            for (int ordercount = 0; ordercount < CustomFieldNames.size(); ordercount++) {
                values.clear();
                String name = CustomFieldNames.get(ordercount);
                String vals = CustomFieldVals.get(ordercount);
                values.put(Constants.DB_TABLE_CustomOrderFields_OrderID,
                        orderid);
                values.put(
                        Constants.DB_TABLE_CustomOrderFields_customfield_name,
                        name);
                values.put(
                        Constants.DB_TABLE_CustomOrderFields_customfield_value,
                        vals);
                DBAdapter.openDataBase();

                try {
                    DBAdapter.openDataBase();
                    DBAdapter.db.insert(Constants.DB_TABLE_CustomOrderFields,
                            null, values);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            DBAdapter.closeDataBase();
            values = null;
        }

    }

    public static void saveCustomFieldAns(ArrayList<String> CustomFieldNames,
                                          ArrayList<String> CustomFieldVals,
                                          ArrayList<String> CustomFieldAns, String orderid) {
        if (CustomFieldNames == null)
            return;
        synchronized (lock) {
            ContentValues values = new ContentValues();
            for (int ordercount = 0; ordercount < CustomFieldNames.size(); ordercount++) {
                values.clear();
                String name = CustomFieldNames.get(ordercount);
                String vals = CustomFieldVals.get(ordercount);
                String ans = CustomFieldAns.get(ordercount);
                values.put(Constants.DB_TABLE_CustomOrderFieldsAns_OrderID,
                        orderid);
                values.put(
                        Constants.DB_TABLE_CustomOrderFieldsAns_customfield_name,
                        name);
                values.put(
                        Constants.DB_TABLE_CustomOrderFieldsAns_customfield_value,
                        vals);
                values.put(
                        Constants.DB_TABLE_CustomOrderFieldsAns_customfield_text,
                        ans);
                DBAdapter.openDataBase();

                try {
                    DBAdapter.openDataBase();
                    DBAdapter.db.insert(
                            Constants.DB_TABLE_CustomOrderFieldsAns, null,
                            values);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            DBAdapter.closeDataBase();
            values = null;
        }

    }

    public static ArrayList<AltLanguage> getLanguages(boolean isSelected) {
        ArrayList<AltLanguage> qd = new ArrayList<AltLanguage>();
        try {
            // "CREATE TABLE \"tbl_alt_questions\" (\"AltLangID\" VARCHAR,
            // \"SetID\" VARCHAR, \"DataID\" VARCHAR, \"Question\" VARCHAR," +
            // " \"QuestionDescription\" VARCHAR, \"MiDescription\" VARCHAR)";

            String table_inner = Constants.DB_TABLE_language;
            String[] columns_inner = new String[]{
                    Constants.DB_TABLE_language_AltLangDirection,
                    Constants.DB_TABLE_language_AltLangID,
                    Constants.DB_TABLE_language_AltLangName,
                    Constants.DB_TABLE_language_CompanyLink,
                    Constants.DB_TABLE_language_InterfaceLanguage,
                    Constants.DB_TABLE_language_IsActive,
                    Constants.DB_TABLE_language_IsSelected};

            DBAdapter.openDataBase();
            Cursor cc = DBAdapter.db.query(true, table_inner, columns_inner,
                    null, null, null, null, null, null);
            if (cc.getCount() == 0) {
                cc.close();
                DBAdapter.closeDataBase();
                return qd;
            }

            cc.moveToFirst();
            AltLanguage q = null;
            do {
                //
                // Constants.DB_TABLE_language_AltLangDirection,
                // Constants.DB_TABLE_language_AltLangID,
                // Constants.DB_TABLE_language_AltLangName,
                // Constants.DB_TABLE_language_CompanyLink,
                // Constants.DB_TABLE_language_InterfaceLanguage,
                // Constants.DB_TABLE_language_IsActive,
                // Constants.DB_TABLE_language_IsSelected

                q = new AltLanguage();
                int DB_TABLE_language_AltLangDirection = cc
                        .getColumnIndex(columns_inner[0]);
                int DB_TABLE_language_AltLangID = cc
                        .getColumnIndex(columns_inner[1]);
                int DB_TABLE_language_AltLangName = cc
                        .getColumnIndex(columns_inner[2]);
                int DB_TABLE_language_CompanyLink = cc
                        .getColumnIndex(columns_inner[3]);
                int DB_TABLE_language_InterfaceLanguage = cc
                        .getColumnIndex(columns_inner[4]);
                int DB_TABLE_language_IsActive = cc
                        .getColumnIndex(columns_inner[5]);
                int DB_TABLE_language_IsSelected = cc
                        .getColumnIndex(columns_inner[6]);

                q.setAltLangDirection(cc
                        .getString(DB_TABLE_language_AltLangDirection));
                q.setAltLangID(cc.getString(DB_TABLE_language_AltLangID));
                q.setAltLangName(cc.getString(DB_TABLE_language_AltLangName));
                q.setCompanyLink(cc.getString(DB_TABLE_language_CompanyLink));
                q.setInterfaceLanguage(cc
                        .getString(DB_TABLE_language_InterfaceLanguage));
                q.setIsActive(cc.getString(DB_TABLE_language_IsActive));
                q.setIsSelected(cc.getString(DB_TABLE_language_IsSelected));
                if (isSelected && q.getIsSelected() != null
                        && q.getIsSelected().equals("1"))
                    qd.add(q);
                else if (!isSelected)
                    qd.add(q);

            } while (cc.moveToNext());

            return qd;
        } catch (Exception ex) {
            return qd;
        }
    }

    public static void saveLanguages(ArrayList<AltLanguage> altLanguages,
                                     boolean isDownloadingNew) {
        if (altLanguages == null)
            return;

        if (isDownloadingNew) {
            ArrayList<AltLanguage> oldSelectedLangs = DBHelper
                    .getLanguages(true);

            for (int i = 0; oldSelectedLangs != null && i < altLanguages.size(); i++) {

                for (int j = 0; j < oldSelectedLangs.size(); j++) {
                    if (altLanguages.get(i).getAltLangID() != null
                            && oldSelectedLangs.get(j).getAltLangID() != null
                            && altLanguages
                            .get(i)
                            .getAltLangID()
                            .equals(oldSelectedLangs.get(j)
                                    .getAltLangID())) {
                        altLanguages.get(i).setIsSelected("1");
                    }
                }
            }
        }

        DBAdapter.openDataBase();
        DBAdapter.db.delete(Constants.DB_TABLE_language, null, null);
        DBAdapter.closeDataBase();

        synchronized (lock) {
            ContentValues values = new ContentValues();
            for (int ordercount = 0; ordercount < altLanguages.size(); ordercount++) {
                values.clear();
                AltLanguage thisLang = altLanguages.get(ordercount);
                String id = thisLang.getAltLangID();
                String name = thisLang.getAltLangName();
                String direction = thisLang.getAltLangDirection();
                String companyLink = thisLang.getCompanyLink();
                String interfaceLan = thisLang.getInterfaceLanguage();
                String isActive = thisLang.getIsActive();
                String isSelected = thisLang.getIsSelected();
                values.put(Constants.DB_TABLE_language_AltLangID, id);
                values.put(Constants.DB_TABLE_language_AltLangName, name);
                values.put(Constants.DB_TABLE_language_AltLangDirection,
                        direction);
                values.put(Constants.DB_TABLE_language_CompanyLink, companyLink);
                values.put(Constants.DB_TABLE_language_InterfaceLanguage,
                        interfaceLan);
                values.put(Constants.DB_TABLE_language_IsActive, isActive);
                values.put(Constants.DB_TABLE_language_IsSelected, isSelected);
                DBAdapter.openDataBase();

                try {
                    DBAdapter.openDataBase();
                    DBAdapter.db.insert(Constants.DB_TABLE_language, null,
                            values);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            DBAdapter.closeDataBase();
            values = null;
        }

    }

    public static void deleteProps() {
        synchronized (lock) {
            DBAdapter.openDataBase();
            DBAdapter.db.delete(Constants.DB_TABLE_BRANCH_PROPS, null, null);
            DBAdapter.closeDataBase();
        }

    }

    public static void incrementTriesAgainstOrderId(String where, String tries) {
        ContentValues values = new ContentValues();
        int triess = 0;
        try {
            triess = Integer.parseInt(tries);
        } catch (Exception ex) {

        }
        values.put(Constants.DB_TABLE_SUBMITSURVEY_UNEMPTY_QUES_COUNT,
                (triess + 1) + "");
        DBAdapter.openDataBase();
        DBAdapter.db.update(Constants.DB_TABLE_SUBMITSURVEY, values, where,
                null);
        DBAdapter.closeDataBase();
    }

    public static boolean deleteFile(String orderid, String filePath) {

        try {
            UploadFileData fd = (UploadFileData) convertFromBytesFileTbl("files_"
                    + orderid + ".txt");
            if (fd != null && fd.getItemPath().size() > 0) {
                for (int i = 0; i < fd.getItemPath().size(); i++) {
                    if (fd.getItemPath() != null
                            && fd.getItemPath().get(i).getFilePath()
                            .equals(filePath)) {
                        fd.getItemPath().remove(i);
                        convertToBytes(fd, "files_" + orderid + ".txt");
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public static void updateTimeStamp(String time, String orderID)
            throws IOException {
        ArrayList<InProgressStampData> data = (ArrayList<InProgressStampData>) convertFromBytes(
                "inprogresstimestamps.txt", true);
        int i = datacontains(data, orderID);
        if (data != null && !data.isEmpty() && i >= 0) {
            data.remove(i);
            // return;
        } else if (data == null) {
            data = new ArrayList<InProgressStampData>();
        }
        data.add(new InProgressStampData(time, orderID));
        convertToBytes(data, "inprogresstimestamps.txt", true);
    }

    public static void saveTimeStamp(String time, String orderID)
            throws IOException {
        ArrayList<InProgressStampData> data = (ArrayList<InProgressStampData>) convertFromBytes(
                "inprogresstimestamps.txt", true);
        int i = datacontains(data, orderID);
        if (data != null && !data.isEmpty() && i >= 0) {
            // data.remove(i);
            return;
        } else if (data == null) {
            data = new ArrayList<InProgressStampData>();
        }
        data.add(new InProgressStampData(time, orderID));
        convertToBytes(data, "inprogresstimestamps.txt", true);
    }

    public static void deleteTimeStamp(String time, String orderID)
            throws IOException {
        ArrayList<InProgressStampData> data = (ArrayList<InProgressStampData>) convertFromBytes(
                "inprogresstimestamps.txt", true);
        int i = datacontains(data, orderID);
        if (data != null && !data.isEmpty() && i >= 0) {
            data.remove(i);
        }

        convertToBytes(data, "inprogresstimestamps.txt", true);
    }

    public static String getTimeStamp(String orderID) throws IOException {

        ArrayList<InProgressStampData> data = (ArrayList<InProgressStampData>) convertFromBytes(
                "inprogresstimestamps.txt", true);
        int i = datacontains(data, orderID);
        if (data != null && !data.isEmpty() && i >= 0) {
            return data.get(i).getTimestamp();
        }

        return null;
    }

    private static int datacontains(ArrayList<InProgressStampData> data,
                                    String orderID) {
        int pos = -1;
        for (int i = 0; data != null && i < data.size(); i++) {
            if (data.get(i).getOrderID() != null
                    && data.get(i).getOrderID().equals(orderID)) {
                pos = i;
            }
        }
        return pos;
    }

    public static boolean deleteSet(String setid, String orderid) {
        String filename = "set_" + setid + ".txt";
        filename = "set_" + setid + "order_" + orderid + ".txt";
        File root = CheckerApp.localFilesDir;//android.os.Environment.getExternalStorageDirectory();
        String path = root.getAbsolutePath() + "/mnt/sdcard/Checker_binaries/";
        File file = new File(path, filename);
        if (file.exists()) {
            return file.delete();
        } else
            return true;

    }

    public static boolean isSetExist(String setid, String orderid) {
        String filename = "set_" + setid + ".txt";
        filename = "set_" + setid + "order_" + orderid + ".txt";
        File root = CheckerApp.localFilesDir;//android.os.Environment.getExternalStorageDirectory();
        String path = root.getAbsolutePath() + "/mnt/sdcard/Checker_binaries/";
        File file = new File(path, filename);
        if (file.exists()) {
            return true;
        } else
            return false;

    }

    public static void updateDeletedJobsStatus(String wheree) {
        if (wheree == null || wheree.trim().equals(""))
            return;
        ContentValues values = new ContentValues();
        values.put(Constants.DB_TABLE_JOBLIST_sdeletedjob, "true");
        DBAdapter.openDataBase();
        DBAdapter.db.update(Constants.DB_TABLE_JOBLIST, values, wheree, null);
        DBAdapter.closeDataBase();

    }

}
