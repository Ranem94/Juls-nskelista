package com.example.julsnskelista

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { JulsApp() } }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JulsApp() {
    val context = LocalContext.current
    val vm = remember { PresentViewModel(context.applicationContext as Application) }
    var editDialogId by remember { mutableStateOf<Int?>(null) }
    var editText by remember { mutableStateOf("") }

    Scaffold(topBar = { TopAppBar(title = { Text("Juls önskelista 🎁") }) }) { padding ->
        Column(Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
            Text(vm.statistik)
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(value = vm.nyText, onValueChange = { vm.nyText = it }, label = { Text("Ny present") }, modifier = Modifier.weight(1f))
                Spacer(Modifier.width(8.dp))
                Button(onClick = { vm.addPresent() }) { Text("Lägg till") }
            }
            Spacer(Modifier.height(8.dp))

            // Här är ändringen för Kategori
            var expanded by remember { mutableStateOf(false) }
            Box {
                OutlinedButton(onClick = { expanded = true }) {
                    Text(vm.valdKategori)
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    vm.kategorier.forEach { kat ->
                        DropdownMenuItem(
                            text = { Text("${kat.ikon} ${kat.namn}") },
                            onClick = {
                                vm.valdKategori = kat.namn
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            LazyColumn {
                items(vm.presenter, key = { it.id }) { p ->
                    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = p.isKlar, onCheckedChange = { vm.toggleKlar(p.id) })
                            Column(Modifier.weight(1f)) { Text(p.namn); Text(p.kategori, style = MaterialTheme.typography.bodySmall) }
                            IconButton(onClick = { editDialogId = p.id; editText = p.namn }) { Icon(Icons.Default.Edit, "") }
                            IconButton(onClick = { vm.deletePresent(p.id) }) { Icon(Icons.Default.Delete, "") }
                        }
                    }
                }
            }
        }
    }
    if (editDialogId != null) {
        AlertDialog(
            onDismissRequest = { editDialogId = null },
            title = { Text("Redigera") },
            text = { TextField(value = editText, onValueChange = { editText = it }) },
            confirmButton = { Button(onClick = { vm.editPresent(editDialogId!!, editText); editDialogId = null }) { Text("Spara") } },
            dismissButton = { TextButton(onClick = { editDialogId = null }) { Text("Avbryt") } }
        )
    }
}