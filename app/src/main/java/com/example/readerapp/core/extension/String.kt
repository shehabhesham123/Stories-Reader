package com.example.readerapp.core.extension

import com.google.gson.Gson

fun <T> String.Companion.toPojo(json: String, classT: Class<T>): T {
    return Gson().fromJson(json, classT)
}