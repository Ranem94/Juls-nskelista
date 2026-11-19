package com.example.julsnskelista

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class Present(val id: Int, val namn: String, val kategori: String, val isKlar: Boolean = false)
data class Kategori(val namn: String, val ikon: String)

class PresentViewModel(app: Application) : AndroidViewModel(app) {

    val kategorier = listOf(
        Kategori("Jul", "🎄"),
        Kategori("Födelsedag", "🎂"),
        Kategori("Mors dag" , ""),
        Kategori("Övrigt", "🎁")
    )

    var presenter = mutableStateListOf<Present>()
        private set

    var nyText by mutableStateOf("")
    var valdKategori by mutableStateOf("Jul")
    var valdFilter by mutableStateOf<String?>(null)

    private var senastBorttagen: Present? = null
    private var nextId = 1

    private val prefs = app.getSharedPreferences("presenter_prefs", 0)

    init {
        viewModelScope.launch {
            val sparad = prefs.getString("lista", "") ?: ""
            if (sparad.isNotEmpty()) {
                sparad.split("||").forEach { rad ->
                    val delar = rad.split(";;")
                    if (delar.size == 4) {
                        val p = Present(delar[0].toInt(), delar[1], delar[2], delar[3] == "true")
                        presenter.add(p)
                        if (p.id >= nextId) nextId = p.id + 1
                    }
                }
            }
        }
    }

    val filtreradePresenter: List<Present>
        get() = if (valdFilter == null) presenter else presenter.filter { it.kategori == valdFilter }

    val statistik: String
        get() {
            val klara = presenter.count { it.isKlar }
            return "$klara av ${presenter.size} klara"
        }

    fun addPresent() {
        if (nyText.isBlank()) return
        // هون المهم - لازم ياخد valdKategori مو Jul ثابت
        val ny = Present(nextId++, nyText.trim(), valdKategori, false)
        presenter.add(ny)
        nyText = ""
        spara()
    }

    fun toggleKlar(id: Int) {
        val index = presenter.indexOfFirst { it.id == id }
        if (index != -1) {
            val gammal = presenter[index]
            presenter[index] = gammal.copy(isKlar = !gammal.isKlar)
            spara()
        }
    }

    fun deletePresent(id: Int) {
        val p = presenter.find { it.id == id } ?: return
        senastBorttagen = p
        presenter.remove(p)
        spara()
    }

    fun angraDelete() {
        senastBorttagen?.let {
            presenter.add(it)
            senastBorttagen = null
            spara()
        }
    }

    fun editPresent(id: Int, nyttNamn: String) {
        if (nyttNamn.isBlank()) return
        val index = presenter.indexOfFirst { it.id == id }
        if (index != -1) {
            presenter[index] = presenter[index].copy(namn = nyttNamn.trim())
            spara()
        }
    }

    private fun spara() {
        val text = presenter.joinToString("||") { "${it.id};;${it.namn};;${it.kategori};;${it.isKlar}" }
        prefs.edit().putString("lista", text).apply()
    }
}