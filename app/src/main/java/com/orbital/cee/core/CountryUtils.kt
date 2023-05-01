package com.orbital.cee.core

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orbital.cee.model.Country
import java.io.IOException

fun countryList(context: Context): MutableList<Country> {
    val jsonFileString = getJsonDataFromAsset(context = context, "Countries.json")
    val type = object : TypeToken<List<Country>>() {}.type
    return Gson().fromJson(jsonFileString, type)
}

fun getJsonDataFromAsset(
    context: Context,
    fileName: String
): String? {
    val jsonString: String
    try {
        jsonString = context.assets.open(fileName).bufferedReader().use {
            it.readText()
        }
    } catch (exp: IOException) {
        exp.printStackTrace()
        return null
    }

    return jsonString
}
fun List<Country>.searchCountryList(countryName: String): MutableList<Country> {
    val countryList = mutableListOf<Country>()
    this.forEach {
        if (it.name.lowercase().contains(countryName.lowercase()) ||
            it.dialCode.contains(countryName.lowercase())
        ) {
            countryList.add(it)
        }
    }
    return countryList
}
fun List<Country>.searchCountry(countryCode: String): Country? {
    val countryList = mutableListOf<Country>()
    this.forEach {
        if (it.code == countryCode.uppercase() || it.dialCode.contains(countryCode.uppercase())
        ) {
            countryList.add(it)
        }
    }
    return if (countryList.size >1){
        countryList[0]
    }else{
        null
    }
}

fun localeToEmoji(
    countryCode: String
) : String {
    val firstLetter = Character.codePointAt(countryCode, 0) - 0x41 + 0x1F1E6
    val secondLetter = Character.codePointAt(countryCode, 1) - 0x41 + 0x1F1E6
    return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
}














