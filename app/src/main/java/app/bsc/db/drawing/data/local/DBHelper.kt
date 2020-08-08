package app.bsc.db.drawing.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import app.bsc.db.drawing.model.Alarm

class DBHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME, null,
    DATABASE_VER
) {
    companion object {
        private const val DATABASE_NAME = "Alarms.db"
        private const val DATABASE_VER = 1

        // TABLE
        private const val TABLE_NAME = "Alarm"
        private const val COL_ID = "ID"
        private const val COL_HOUR = "Hour"
        private const val COL_MIN = "Min"
        private const val COL_REQ_ID = "Req"
        private const val COL_DAILY = "Daily"

        private const val TAG = "DBHelper.kt"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE_QUERY =
            ("CREATE TABLE  $TABLE_NAME ($COL_ID INTEGER, $COL_HOUR INTEGER, $COL_MIN INTEGER, $COL_REQ_ID INTEGER unique, $COL_DAILY INTEGER)")
        db?.execSQL(CREATE_TABLE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    val allAlarms: ArrayList<Alarm>
        get() {
            val lstAlarms = ArrayList<Alarm>()
            val selectQuery = "SELECT * FROM $TABLE_NAME"
            val db = this.writableDatabase
            val cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    with(cursor) {
                        lstAlarms.add(
                            Alarm(
                                getInt(cursor.getColumnIndex(COL_HOUR)),
                                getInt(cursor.getColumnIndex(COL_MIN)),
                                getInt(cursor.getColumnIndex(COL_REQ_ID)),
                                getInt(cursor.getColumnIndex(COL_DAILY))
                            )
                        )
                    }
                } while (cursor.moveToNext())
            }
            db.close()
            cursor.close()
            return lstAlarms
        }

    fun addAlarm(alarm: Alarm) {
        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, ContentValues().apply {
            put(COL_HOUR, alarm.hour)
            put(COL_MIN, alarm.minute)
            put(COL_REQ_ID, alarm.reqId)
            put(COL_DAILY, alarm.daily)
        })

        Log.i(TAG, "Added alarm with ID ${alarm.reqId}")
        db.close()
    }

    fun deleteAlarm(alarm: Alarm) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$COL_REQ_ID=?", arrayOf(alarm.reqId.toString()))
        Log.i(TAG, "Deleted alarm with ID ${alarm.reqId}")
        db.close()
    }

    fun deleteAlarmById(reqId: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$COL_REQ_ID=?", arrayOf(reqId.toString()))
        Log.i(TAG, "Deleted alarm with ID $reqId")
        db.close()
    }

    fun getMaxReqId(): Int {
        val db = this.writableDatabase

        val cursor = db!!.rawQuery(
            "SELECT Req FROM Alarm\n" +
                    "WHERE Req=(SELECT MAX(Req) from Alarm) order by Req desc limit 1;"
            , null
        )

        var id = 0
        if (cursor.moveToFirst())
            id = cursor.getInt(cursor.getColumnIndex(COL_REQ_ID))
        cursor.close()
        Log.i(TAG, "Maximum database ID: $id")
        return id
    }
}