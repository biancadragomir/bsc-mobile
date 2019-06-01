package app.bsc.db.drawing.util.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import app.bsc.db.drawing.model.Alarm

class DBHelper(context:Context):SQLiteOpenHelper(context,
    DATABASE_NAME, null,
    DATABASE_VER
){
    companion object {
        private val DATABASE_NAME = "Alarms.db"
        private val DATABASE_VER = 1

        // TABLE
        private val TABLE_NAME = "Alarm"
        private val COL_ID = "ID"
        private val COL_HOUR = "Hour"
        private val COL_MIN = "Min"
        private val COL_REQ_ID ="Req"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE_QUERY = ("CREATE TABLE  $TABLE_NAME ($COL_ID INTEGER, $COL_HOUR INTEGER, $COL_MIN INTEGER, $COL_REQ_ID INTEGER)")
        db!!.execSQL(CREATE_TABLE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db!!)
    }

    val allAlarms: ArrayList<Alarm>
        get(){
            val lstAlarms = ArrayList<Alarm>()
            val selectQuery = "SELECT * FROM $TABLE_NAME"
            val db = this.writableDatabase
            val cursor = db.rawQuery(selectQuery, null)
            if(cursor.moveToFirst()){
                do {
                    val Alarm = Alarm(cursor.getInt(cursor.getColumnIndex(COL_HOUR)),
                        cursor.getInt(cursor.getColumnIndex(COL_MIN)),
                        cursor.getInt(cursor.getColumnIndex(COL_REQ_ID))
                    )
                    lstAlarms.add(Alarm)
                }while (cursor.moveToNext())
            }
            db.close()
            cursor.close()
            return lstAlarms
        }

    fun addAlarm( alarm: Alarm ){
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(COL_HOUR, alarm.hour)
        values.put(COL_MIN, alarm.minute)
        values.put(COL_REQ_ID, alarm.reqId)

        db.insert(TABLE_NAME, null, values)
        Log.i("DBHelper: ", "added alarm with req id = " + alarm.reqId.toString())
        db.close()
    }

    fun deleteAlarm( alarm: Alarm ){
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$COL_REQ_ID=?", arrayOf(alarm.reqId.toString()))
        Log.i("DBHelper: ", "deleted alarm with req id = " + alarm.reqId.toString())
        db.close()
    }
}