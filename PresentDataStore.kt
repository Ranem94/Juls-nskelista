package com.example.julsnskelista

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("juls_prefs")
private val PRESENTER_KEY = stringPreferencesKey("presenter_json")
private val gson = Gson()

class PresentDataStore(private val context: Context) {
    fun getPresenter(): Flow<List<Present>> {
        return context.dataStore.data.map { prefs ->
            val json = prefs[PRESENTER_KEY] ?: "[]"
            val type = object : TypeToken<List<Present>>() {}.type
            try { gson.fromJson(json, type) } catch (e: Exception) { emptyList() }
        }
    }
    suspend fun savePresenter(list: List<Present>) {
        val json = gson.toJson(list)
        context.dataStore.edit { it[PRESENTER_KEY] = json }
    }
}