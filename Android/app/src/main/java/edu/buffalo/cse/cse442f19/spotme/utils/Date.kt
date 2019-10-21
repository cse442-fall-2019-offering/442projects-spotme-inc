package edu.buffalo.cse.cse442f19.spotme.utils

import android.util.Log
import edu.buffalo.cse.cse442f19.spotme.User
import java.time.LocalDateTime

class Date {

    var year: Int = 0
    var month: Int = 0
    var day: Int = 0

    constructor(year: Int, month: Int, day: Int) {

        this.year = year;
        this.month = month;
        this.day = day;
    }

    constructor(dateString: String) {

        var array = dateString.split('-')
        for (i in array) {
            Log.d("i", i);
        }
        this.year = array[0].toInt()
        this.month = array[1].toInt()
        this.day = array[2].toInt()
    }

    override fun toString(): String {

        return "$year-$month-$day"
    }

    companion object {
        @JvmStatic
        fun getCurrentDate(): Date {

            var localDateTime = LocalDateTime.now()

            return Date(localDateTime.year, localDateTime.monthValue, localDateTime.dayOfMonth)
        }

        @JvmStatic
        fun getAge(birthdayDate: Date): Int {
            var currentDate = Date.getCurrentDate()
            var age: Int = currentDate.year - birthdayDate.year
            if (currentDate.month < birthdayDate.month) {

                if (currentDate.day < birthdayDate.day) {

                    age -= 1;
                }
            }

            return age;
        }
//        var localDateTime =
//        var today: Date = ;
        //var currentUser: User? = null
    }
}