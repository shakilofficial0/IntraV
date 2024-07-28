package com.codebumble.intrav

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object ServerStorage {
    private const val PREFS_NAME = "servers_prefs"
    private const val SERVERS_KEY = "servers"

    fun saveServers(context: Context, servers: List<Server>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val json = Gson().toJson(servers)
        editor.putString(SERVERS_KEY, json)
        editor.apply()
    }

    fun loadServers(context: Context): List<Server> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(SERVERS_KEY, "[]")
        val type = object : TypeToken<List<Server>>() {}.type
        return Gson().fromJson(json, type)
    }
}
