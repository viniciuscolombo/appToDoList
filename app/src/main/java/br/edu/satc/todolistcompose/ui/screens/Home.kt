@file:OptIn(ExperimentalMaterial3Api::class)

package br.edu.satc.todolistcompose.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import br.edu.satc.todolistcompose.TaskData
import br.edu.satc.todolistcompose.ui.components.TaskCard
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    tasks: List<TaskData>,
    onAddTask: (TaskData) -> Unit,
    onTaskUpdated: (TaskData) -> Unit,
    onTaskDelete: (TaskData) -> Unit
) {
    var showNewTaskSheet by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<TaskData?>(null) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text(text = "ToDoList UniSATC") },
                actions = {
                    IconButton(onClick = { /* Ação para configurações */ }) {
                        Icon(Icons.Rounded.Settings, contentDescription = "Configurações")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Nova tarefa") },
                icon = { Icon(Icons.Filled.Add, contentDescription = "Adicionar Tarefa") },
                onClick = { showNewTaskSheet = true }
            )
        }
    ) { innerPadding ->
        // Lista de tarefas
        Column(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .padding(top = innerPadding.calculateTopPadding())
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            tasks.forEach { task ->
                TaskCard(
                    title = task.title,
                    description = task.description,
                    complete = task.complete,
                    onToggleComplete = {
                        val updatedTask = task.copy(complete = !task.complete)
                        onTaskUpdated(updatedTask)
                    },
                    onEdit = { taskToEdit = task },
                    onDelete = { onTaskDelete(task) }
                )
            }
        }
        // Modal para nova tarefa
        if (showNewTaskSheet) {
            NewTask(
                showBottomSheet = showNewTaskSheet,
                onComplete = { showNewTaskSheet = false },
                onAddTask = onAddTask
            )
        }
        // Modal para edição de tarefa (BottomSheet) – exibido se houver tarefa a ser editada
        taskToEdit?.let { task ->
            EditTask(
                task = task,
                onUpdateTask = { updatedTask ->
                    onTaskUpdated(updatedTask)
                    taskToEdit = null
                },
                onDismiss = { taskToEdit = null }
            )
        }
    }
}

@Composable
fun NewTask(
    showBottomSheet: Boolean,
    onComplete: () -> Unit,
    onAddTask: (TaskData) -> Unit
) {
    if (showBottomSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val scope = rememberCoroutineScope()

        var taskTitle by remember { mutableStateOf("") }
        var taskDescription by remember { mutableStateOf("") }

        ModalBottomSheet(
            onDismissRequest = { onComplete() },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = taskTitle,
                    onValueChange = { taskTitle = it },
                    label = { Text("Título da Tarefa") }
                )
                OutlinedTextField(
                    value = taskDescription,
                    onValueChange = { taskDescription = it },
                    label = { Text("Descrição da Tarefa") }
                )
                Button(
                    modifier = Modifier.padding(top = 16.dp),
                    onClick = {
                        onAddTask(
                            TaskData(
                                title = taskTitle,
                                description = taskDescription,
                                complete = false
                            )
                        )
                        scope.launch {
                            sheetState.hide()
                            onComplete()
                        }
                    }
                ) {
                    Text("Salvar")
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTask(
    task: TaskData,
    onUpdateTask: (TaskData) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var taskTitle by remember { mutableStateOf(task.title) }
    var taskDescription by remember { mutableStateOf(task.description) }

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = taskTitle,
                onValueChange = { taskTitle = it },
                label = { Text("Título da tarefa") }
            )
            OutlinedTextField(
                value = taskDescription,
                onValueChange = { taskDescription = it },
                label = { Text("Descrição da tarefa") }
            )
            Button(
                modifier = Modifier.padding(top = 16.dp),
                onClick = {
                    val updatedTask = task.copy(
                        title = taskTitle,
                        description = taskDescription
                    )
                    onUpdateTask(updatedTask)
                    scope.launch { sheetState.hide() }
                }
            ) {
                Text("Salvar Alterações")
            }
        }
    }
}
