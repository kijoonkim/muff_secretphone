package com.muffinmanz.muff_secretphone.utilities

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateUtil {
    companion object {
        val DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
        val DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
        val DEFAULT_DATE_FORMAT_YY ="yy. M. dd";
        val DEFAULT_SHORT_DATE_FORMAT = "MMdd";

        fun getNowDate(format: String?): String{
            val sdf = SimpleDateFormat(if(format == null) DEFAULT_DATETIME_FORMAT else format, Locale.KOREAN)
            return sdf.format(Date())
        }

        fun getArrDateFromString(date: String?, splitter: String?): Array<String>? {
            if(date != null && date !== "") {
                if (splitter == null || splitter === "") {
                    //ex. date - 20241022
                    val arrDate = arrayOf<String>("", "", "")
                    arrDate[0] = date.substring(0, 4)
                    arrDate[0] = date.substring(4, 6)
                    arrDate[0] = date.substring(6, 8)
                    return arrDate
                } else {
                    //ex. date: 2024-10-22, splitter: -
                    return date.split(splitter).toTypedArray()
                }
            }

            return null
        }
    }
}