package com.example.julsnskelista

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import kotlinx.coroutines.launch

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
    var editId by remember { mutableStateOf<Int?>(null) }
    var editText by remember { mutableStateOf("") }
    val snack = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Juls onskelista") }) },
        snackbarHost = { SnackbarHost(snack) }
    ) { p ->
        Column(Modifier.padding(p).padding(16.dp).fillMaxSize()) {
            Text(vm.statistik)
            Spacer(Modifier.height(12.dp))
            Text("Du lagger till i: ${vm.valdKategori}", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(value = vm.nyText, onValueChange = { vm.nyText = it }, label = { Text("Ny present") }, modifier = Modifier.weight(1f))
                Spacer(Modifier.width(8.dp))
                Button(onClick = { vm.addPresent() }) { Text("Lagg till") }
            }
            Spacer(Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(selected = vm.valdFilter == null, onClick = { vm.valdFilter = null }, label = { Text("Alla") })
                }
                items(vm.kategorier) { k ->
                    FilterChip(
                        selected = vm.valdFilter == k.namn,
                        onClick = {
                            vm.valdFilter = k.namn
                            vm.valdKategori = k.namn
                        },
                        label = { Text("${k.ikon} ${k.namn}") }
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            if (vm.presenter.isEmpty()) {
                Text("Inga presenter an!")
            } else if (vm.filtreradePresenter.isEmpty()) {
                Text("Inga presenter i denna kategori.")
            } else {
                LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    items(vm.filtreradePresenter, key = { it.id }) { pr ->
                        Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = pr.isKlar, onCheckedChange = { vm.toggleKlar(pr.id) })
                                Column(Modifier.weight(1f)) { Text(pr.namn); Text(pr.kategori, style = MaterialTheme.typography.bodySmall) }
                                IconButton(onClick = { editId = pr.id; editText = pr.namn }) { Icon(Icons.Default.Edit, "Redigera") }
                                IconButton(onClick = {
                                    vm.deletePresent(pr.id)
                                    scope.launch {
                                        val r = snack.showSnackbar("Present borttagen", "Angra", duration = SnackbarDuration.Short)
                                        if (r == SnackbarResult.ActionPerformed) vm.angraDelete()
                                    }
                                }) { Icon(Icons.Default.Delete, "Ta bort") }
                            }
                        }
                    }
                }
            }
        }
    }
    if (editId != null) {
        AlertDialog(
            onDismissRequest = { editId = null },
            title = { Text("Redigera") },
            text = { TextField(value = editText, onValueChange = { editText = it }) },
            confirmButton = { Button(onClick = { vm.editPresent(editId!!, editText); editId = null }) { Text("Spara") } },
            dismissButton = { TextButton(onClick = { editId = null }) { Text("Avbryt") } }
        )
    }
}
