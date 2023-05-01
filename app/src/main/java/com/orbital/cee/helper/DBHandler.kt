package com.orbital.cee.helper

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.mapbox.geojson.Point
import java.util.Date

class DBHandler
    (context: Context?) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + START_TIME_COL + " LONG,"
                + END_TIME_COL + " LONG,"
                + DURATION_COL + " INTEGER,"
                + SPEED_AVERAGE_COL + " INTEGER,"
                + MAX_SPEED_COL + " INTEGER,"
                + DISTANCE_COL + " FLOAT(52),"
                + LAT_LON_LIST_COL + " TEXT,"
                + IS_FAV_COL + " BOOL,"
                + ALERT_COUNT_COL + " INTEGER)")
        Log.d("DEBUG_SQL_LITE","CREATE")
        db.execSQL(query)
    }

    fun addNewTrip(
        startTime: Long? = null,
        endTime: Long? = null,
        duration: Int? = null,
        speedAverage: Int = 0,
        maxSpeed: Int = 0,
        distance:  Float = 0f,
        listOfLatLon: String = "",
        isFavorite: Boolean = false,
        alertCount: Int = 0
    ) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(START_TIME_COL, startTime)
        values.put(END_TIME_COL, endTime)
        values.put(DURATION_COL, duration)
        values.put(SPEED_AVERAGE_COL, speedAverage)
        values.put(MAX_SPEED_COL, maxSpeed)
        values.put(DISTANCE_COL, distance)
        values.put(LAT_LON_LIST_COL, listOfLatLon)
        values.put(IS_FAV_COL, isFavorite)
        values.put(ALERT_COUNT_COL, alertCount)
        db.insert(TABLE_NAME, null, values)
        db.close()
        Log.d("DEBUG_SQL_LITE","ADD")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    companion object {
        private const val DB_NAME = "ceeDb"
        private const val DB_VERSION = 1
        private const val TABLE_NAME = "tripsTbl"
        private const val ID_COL = "id"
        private const val START_TIME_COL = "startTime"
        private const val END_TIME_COL = "endTime"
        private const val DURATION_COL = "duration"
        private const val SPEED_AVERAGE_COL = "speedAverage"
        private const val MAX_SPEED_COL = "maxSpeed"
        private const val DISTANCE_COL = "distance"
        private const val LAT_LON_LIST_COL = "listOfLatLon"
        private const val IS_FAV_COL = "isFavorite"
        private const val ALERT_COUNT_COL = "alertCount"

    }
    fun readTrips(): ArrayList<TripModel>? {
        val db = this.readableDatabase
        val cursorCourses: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)
        val courseModelArrayList: ArrayList<TripModel> = ArrayList()
        if (cursorCourses.moveToFirst()) {
            do {
                courseModelArrayList.add(
                    TripModel(
                        cursorCourses.getInt(0),
                        cursorCourses.getLong(1),
                        cursorCourses.getLong(2),
                        cursorCourses.getInt(3),
                        cursorCourses.getInt(4),
                        cursorCourses.getInt(5),
                        cursorCourses.getFloat(6),
                        cursorCourses.getString(7),
                        cursorCourses.getInt(8)>0,
                        cursorCourses.getInt(9),
                    )
                )
            } while (cursorCourses.moveToNext())
        }

        cursorCourses.close()
        return courseModelArrayList
    }
}
data class TripModel(
    var id: Int = 0,
    var startTime: Long? = null,
    var endTime: Long? = null,
    var duration: Int? = null,
    var speedAverage: Int = 0,
    var maxSpeed: Int = 0,
    var distance:  Float = 0f,
    var listOfLatLon: String = "",
    var isFavorite : Boolean = false,
    var alertCount: Int = 0
)