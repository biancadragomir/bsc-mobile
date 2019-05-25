package app.bsc.db.drawing.util

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import app.bsc.db.drawing.model.Alarm

class DBHelper(context:Context):SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VER){
    companion object {
        private val DATABASE_NAME = "Alarms.db"
        private val DATABASE_VER = 1

        // TABLE
        private val TABLE_NAME = "Alarm"
        private val COL_ID = "ID"
        private val COL_HOUR = "Hour"
        private val COL_MIN = "Min"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE_QUERY = ("CREATE TABLE  $TABLE_NAME ($COL_ID INTEGER, $COL_HOUR INTEGER, $COL_MIN INTEGER)")
        db!!.execSQL(CREATE_TABLE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db!!)
    }

    // CRUD
    val allAlarms: ArrayList<Alarm>
        get(){
            val lst_Alarms = ArrayList<Alarm>()
            val SELECT_QUERY = "SELECT * FROM $TABLE_NAME"
            val db = this.writableDatabase
            val cursor = db.rawQuery(SELECT_QUERY, null)
            if(cursor.moveToFirst()){
                do {
                    val Alarm = Alarm(cursor.getInt(cursor.getColumnIndex(COL_HOUR)), cursor.getInt(cursor.getColumnIndex(COL_MIN)))
                    lst_Alarms.add(Alarm)
                }while (cursor.moveToNext())
            }
            db.close()
            return lst_Alarms
        }

    fun addAlarm( Alarm: Alarm ){
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_HOUR, Alarm.hour)
        values.put(COL_MIN, Alarm.minute)

        db.insert(TABLE_NAME, null, values)
        db.close()
    }
}