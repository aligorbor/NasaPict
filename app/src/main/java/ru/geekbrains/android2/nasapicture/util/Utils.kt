package ru.geekbrains.android2.nasapicture.util

import java.text.SimpleDateFormat
import java.util.*

fun strDateBeforeNow(numberDays: Int): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val c = Calendar.getInstance()
    c.time = Date()
    c.add(Calendar.DATE, -numberDays)
    return sdf.format(c.time)
}