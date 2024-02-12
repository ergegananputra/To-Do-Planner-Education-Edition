package com.minizuure.todoplannereducationedition.services.api.worker

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.minizuure.todoplannereducationedition.R
import com.minizuure.todoplannereducationedition.ToDoPlannerApplication
import com.minizuure.todoplannereducationedition.services.api.firemodel.Community
import com.minizuure.todoplannereducationedition.services.api.firemodel.FireMember
import com.minizuure.todoplannereducationedition.services.api.firemodel.FireNotesTask
import com.minizuure.todoplannereducationedition.services.api.firemodel.FireRoutine
import com.minizuure.todoplannereducationedition.services.api.firemodel.FireSession
import com.minizuure.todoplannereducationedition.services.api.firemodel.FireSessionTaskProvider
import com.minizuure.todoplannereducationedition.services.api.firemodel.FireTask
import com.minizuure.todoplannereducationedition.services.api.firemodel.FireTodoNote
import com.minizuure.todoplannereducationedition.services.api.firemodel.FireUser
import com.minizuure.todoplannereducationedition.services.database.notes.NoteViewModel
import com.minizuure.todoplannereducationedition.services.database.notes.NoteViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.notes.NotesTaskTable
import com.minizuure.todoplannereducationedition.services.database.notes.TodoNoteTable
import com.minizuure.todoplannereducationedition.services.database.relations_table.SessionTaskProviderViewModel
import com.minizuure.todoplannereducationedition.services.database.relations_table.SessionTaskProviderViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.reservasion.ReservationTable
import com.minizuure.todoplannereducationedition.services.database.reservasion.ReservationViewModel
import com.minizuure.todoplannereducationedition.services.database.reservasion.ReservationViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineViewModel
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.session.SessionViewModel
import com.minizuure.todoplannereducationedition.services.database.session.SessionViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.task.TaskViewModel
import com.minizuure.todoplannereducationedition.services.database.task.TaskViewModelFactory
import com.minizuure.todoplannereducationedition.services.preferences.UserPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * This service is used to sync the local database with the Firestore database.
 * It can be used to upload the local database to Firestore or download the Firestore database to the local database.
 *
 * TODO:
 * - Orang lain belum bisa update
 */
class DatabaseSyncServices : Service() {
    private lateinit var routineViewModel : RoutineViewModel
    private lateinit var sessionViewModel : SessionViewModel
    private lateinit var taskViewModel : TaskViewModel
    private lateinit var notesViewModel : NoteViewModel
    private lateinit var sessionTaskProviderViewModel : SessionTaskProviderViewModel
    private lateinit var reservationViewModel : ReservationViewModel

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth : FirebaseAuth

    private fun setupViewModelFactory() {
        val app : ToDoPlannerApplication = (application as ToDoPlannerApplication)

        val routineFactory = RoutineViewModelFactory(app.appRepository)
        routineViewModel = routineFactory.create(RoutineViewModel::class.java)

        val sessionFactory = SessionViewModelFactory(app.appRepository)
        sessionViewModel = sessionFactory.create(SessionViewModel::class.java)

        val taskFactory = TaskViewModelFactory(app.appRepository)
        taskViewModel = taskFactory.create(TaskViewModel::class.java)

        val notesFactory = NoteViewModelFactory(app.appRepository)
        notesViewModel = notesFactory.create(NoteViewModel::class.java)

        val sessionTaskProviderFactory = SessionTaskProviderViewModelFactory(app.appRepository)
        sessionTaskProviderViewModel = sessionTaskProviderFactory.create(SessionTaskProviderViewModel::class.java)

        val reservationViewModelFactory = ReservationViewModelFactory(app.appRepository)
        reservationViewModel = reservationViewModelFactory.create(ReservationViewModel::class.java)
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        setupViewModelFactory()
        when(intent?.action) {
            SyncType.DOWNLOAD.toString() -> CoroutineScope(Dispatchers.Default).launch{
                downloadDatabase()
            }
            SyncType.CREATE_UPLOAD.toString() -> CoroutineScope(Dispatchers.Default).launch{
                uploadDatabase()
            }
            SyncType.UPDATE.toString() -> CoroutineScope(Dispatchers.Default).launch{
                updateDatabase()
            }
            SyncType.CANCEL_ALL_OPERATIONS.toString() -> stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun failureAction(e: Exception, notification: NotificationCompat.Builder, msg: String = "Upload") {
        val errorMsg = getString(R.string.error_uploading_to_firestore)
        notification.setContentText(errorMsg)
        startForeground(1, notification.build())
        Log.e("DatabaseSyncServices", "Error in $msg: ${e.message.toString()}")
        stopSelf()
    }
    private suspend fun updateDatabase() {
        val channelId = "TODO_PLANNER_EDUCATION_EDITION_BY_MINIZUURE_SERVICES_NOTIFICATIONS"
        val syncTitle = getString(R.string.syncronizing)
        val contextDescription = getString(R.string.uploading)
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification_emblem)
            .setContentTitle("$syncTitle...")
            .setContentText(contextDescription)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(null)
        startForeground(1, notification.build())

        val errorMessage = "Update"

        try {
            val databaseJobs : MutableList<Job> = mutableListOf()

            val communityId = UserPreferences(this@DatabaseSyncServices).communityId.also {
                // Validate community ID
                if (it.isEmpty()) {
                    throw Exception("Community ID is empty")
                }
            }

            val reservations = reservationViewModel.getAllByCommunity(communityId)
            val localIdDumps = mutableListOf<Long>()

            val fireSessionTable = FireSession()
            val fireTaskTable = FireTask()
            val fireSessionTaskProviderTable = FireSessionTaskProvider()
            val fireNoteTaskTable = FireNotesTask()
            val fireTodoNoteTable = FireTodoNote()



            for (reservation in reservations) {
                val documentId = reservation.documentId
                val localId = reservation.idLocal
                val additionalLocal = reservation.additionalDataLocal
                val additionalFirebase = reservation.additionalDataFirebase


                when(val tableName = reservation.tableName) {
                    fireSessionTable.table -> {
                        val fireReference = firestore.collection(fireSessionTable.parent).document(communityId).collection(tableName).document(documentId)
                        val session = sessionViewModel.getById(localId) ?: continue // Skip if session is not found
                        localIdDumps.add(localId)

                        fireReference.get().addOnSuccessListener {
                            val fireSession = it.toObject(FireSession::class.java) ?: return@addOnSuccessListener
                            fireSession.apply {
                                this.title = session.title
                                this.timeStart = session.timeStart
                                this.timeEnd = session.timeEnd
                                this.selectedDays = session.selectedDays
                                this.isCustomSession = session.isCustomSession
                                this.update_at = Timestamp.now()
                            }
                            fireReference.set(fireSession.firebaseMap).addOnSuccessListener {
                                Log.i("DatabaseSyncServices", "Session ${session.title} updated to Firestore")
                            }.addOnFailureListener {
                                Log.e("DatabaseSyncServices", it.message.toString())
                            }
                        }.addOnFailureListener {
                            Log.e("DatabaseSyncServices", it.message.toString())
                        }
                    }
                    fireTaskTable.table -> {
                        val fireReference = firestore.collection(fireTaskTable.parent).document(communityId).collection(tableName).document(documentId)
                        val task = taskViewModel.getById(localId) ?: continue // Skip if task is not found
                        localIdDumps.add(localId)

                        fireReference.get().addOnSuccessListener {
                            val fireTask = it.toObject(FireTask::class.java) ?: return@addOnSuccessListener
                            fireTask.apply {
                                this.title = task.title
                                this.updatedAt = task.updatedAt
                                this.isSharedToCommunity = true
                                this.update_at = Timestamp.now()
                            }
                            fireReference.set(fireTask.firebaseMap).addOnSuccessListener {
                                Log.i("DatabaseSyncServices", "Task ${task.title} updated to Firestore")
                            }.addOnFailureListener {
                                Log.e("DatabaseSyncServices", it.message.toString())
                            }
                        }.addOnFailureListener {
                            Log.e("DatabaseSyncServices", it.message.toString())
                        }
                    }
                    fireSessionTaskProviderTable.table -> {
                        val fireReference = firestore.collection(fireSessionTaskProviderTable.parent).document(communityId).collection(tableName).document(documentId)
                        val localId = hashMapOf<Char, Long>(
                            'I' to additionalLocal.split("T")[0].toLong(),
                            'T' to additionalLocal.split("T")[1].split("S")[0].toLong(),
                            'S' to additionalLocal.split("S")[1].toLong()
                        )
                        val firebaseId = hashMapOf<Char, Long>(
                            'I' to additionalFirebase.split("T")[0].toLong(),
                            'T' to additionalFirebase.split("T")[1].split("S")[0].toLong(),
                            'S' to additionalFirebase.split("S")[1].toLong()
                        )

                        val provider = sessionTaskProviderViewModel.getByPrimaryKeys(localId['I']!!.toInt(), localId['T']!!, localId['S']!!) ?: continue // Skip if provider is not found

                        fireReference.get().addOnSuccessListener {
                            val fireProvider = it.toObject(FireSessionTaskProvider::class.java) ?: return@addOnSuccessListener
                            fireProvider.apply {
                                this.locationLink = provider.locationLink
                                this.locationName = provider.locationName
                                this.update_at = Timestamp.now()
                            }
                            fireReference.set(fireProvider.firebaseMap).addOnSuccessListener {
                                Log.i("DatabaseSyncServices", "Provider ${provider.indexDay}T${provider.fkTaskId}S${provider.fkSessionId} updated to Firestore")
                            }.addOnFailureListener {
                                Log.e("DatabaseSyncServices", it.message.toString())
                            }
                        }
                    }
                    fireNoteTaskTable.table -> {
                        val fireReference = firestore.collection(fireNoteTaskTable.parent).document(communityId).collection(tableName).document(documentId)
                        val note = notesViewModel.note.getById(localId) ?: continue // Skip if note is not found
                        localIdDumps.add(localId)

                        fireReference.get().addOnSuccessListener {
                            val fireNote = it.toObject(FireNotesTask::class.java) ?: return@addOnSuccessListener
                            fireNote.apply {
                                this.description = note.description
                                this.updatedAt = note.updatedAt
                                this.update_at = Timestamp.now()
                            }
                            fireReference.set(fireNote.firebaseMap).addOnSuccessListener {
                                Log.i("DatabaseSyncServices", "Note ${note.category} updated to Firestore")
                            }.addOnFailureListener {
                                Log.e("DatabaseSyncServices", it.message.toString())
                            }
                        }.addOnFailureListener {
                            Log.e("DatabaseSyncServices", it.message.toString())
                        }
                    }
                    fireTodoNoteTable.table -> {
                        val fireReference = firestore.collection(fireTodoNoteTable.parent).document(communityId).collection(tableName).document(documentId)
                        val note = notesViewModel.todo.getById(localId) ?: continue // Skip if note is not found
                        localIdDumps.add(localId)

                        fireReference.get().addOnSuccessListener {
                            val fireNote = it.toObject(FireTodoNote::class.java) ?: return@addOnSuccessListener
                            fireNote.apply {
                                this.description = note.description
                                this.updatedAt = note.updatedAt
                                this.update_at = Timestamp.now()
                            }
                            fireReference.set(fireNote.firebaseMap).addOnSuccessListener {
                                Log.i("DatabaseSyncServices", "Todo ${note.description} updated to Firestore")
                            }.addOnFailureListener {
                                Log.e("DatabaseSyncServices", it.message.toString())
                            }
                        }.addOnFailureListener {
                            Log.e("DatabaseSyncServices", it.message.toString())
                        }
                    }
                }
            }

            // TODO: Bits above is not be able to adds new local data to Firestore

            stopSelf()
        } catch (e: Exception) {
            failureAction(e, notification, errorMessage)
        }

    }

    private suspend fun uploadDatabase() {
        val channelId = "TODO_PLANNER_EDUCATION_EDITION_BY_MINIZUURE_SERVICES_NOTIFICATIONS"
        val syncTitle = getString(R.string.syncronizing)
        val contextDescription = getString(R.string.uploading)
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification_emblem)
            .setContentTitle("$syncTitle...")
            .setContentText(contextDescription)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(null)
        startForeground(1, notification.build())

        try {
            val databaseJobs : MutableList<Job> = mutableListOf()


            val isHost = UserPreferences(this@DatabaseSyncServices).isCommunityHost
            val localRoutineId = withContext(Dispatchers.IO) {
                val comId = UserPreferences(this@DatabaseSyncServices).communityId
                val routine = routineViewModel.getByCommunity(comId)
                routine?.id ?: return@withContext comId.toLong()
            }

            val routine = routineViewModel.getById(localRoutineId).also {
                if (it != null) {
                    notification.setContentText("Routine ${it.title}...")
                    startForeground(1, notification.build())
                }
            } ?: return stopSelf()

            val sessions = sessionViewModel.getByRoutineId(localRoutineId).also {
                notification.setContentText("Session (${it.size}...")
                startForeground(1, notification.build())
            }

            val tasks = taskViewModel.getByRoutineId(localRoutineId).also {
                notification.setContentText("Task (${it.size}...")
                startForeground(1, notification.build())
            }

            val provider = sessionTaskProviderViewModel.getByRoutineId(localRoutineId).also {
                notification.setContentText("Relations (${it.size}...")
                startForeground(1, notification.build())
            }

            val notes = mutableListOf<NotesTaskTable>()
            val todo = mutableListOf<TodoNoteTable>()

            for (task in tasks) {
                val note = notesViewModel.note.getByFKTaskId(task.id)
                note?.let { noteTasks ->
                    notes.addAll(noteTasks)

                    noteTasks.forEach { noteTask ->
                        notification.setContentText("Getting ${task.title} content...")
                        startForeground(1, notification.build())

                        val todoNote = notesViewModel.todo.getByFKNoteId(noteTask.id)
                        todoNote?.let { todos ->
                            todo.addAll(todos)
                        }
                    }

                }
            }

            notification.setContentText("Preparation to $contextDescription...")
            startForeground(1, notification.build())

            if (auth.currentUser == null) {
                val errorMsg = getString(R.string.error_auth_user_not_logged_in)
                notification.setContentText(errorMsg)
                startForeground(1, notification.build())
                delay(60000)
                stopSelf()
                return
            }



            // NOTE: Upload to Firestore

            val communityId = if (isHost) {
                auth.currentUser!!.uid.also {
                    UserPreferences(this@DatabaseSyncServices).communityId = it
                }
            } else {
                UserPreferences(this@DatabaseSyncServices).communityId
            }

            val tempCommunity = firestore.collection(Community().table).document(communityId).get().await().toObject(Community::class.java)

            val community : Community = tempCommunity?.apply {
                this.update_at = Timestamp.now()
            }
                ?: Community(
                    reference_id = firestore.collection(Community().table).document(communityId),
                    owner_references = firestore.collection(FireUser().table).document(auth.currentUser!!.uid),

                    members_references = firestore.collection(FireMember().parent).document(communityId),
                    routine_references = firestore.collection(FireRoutine().table).document(communityId),
                    session_references = firestore.collection(FireSession().parent).document(communityId),
                    task_references = firestore.collection(FireTask().parent).document(communityId),
                    provider_references = firestore.collection(FireSessionTaskProvider().parent).document(communityId),
                    notes_task_references = firestore.collection(FireNotesTask().parent).document(communityId),
                    todo_notes_references = firestore.collection(FireTodoNote().parent).document(communityId),

                    update_at = Timestamp.now(),
                    created_at = Timestamp.now()
                )

            firestore.collection(community.table)
                .document(communityId)
                .set(community.firebaseMap)
                .addOnSuccessListener {

                    notification.setContentText("$contextDescription ${routine.title}...")
                    startForeground(1, notification.build())

                    routine.apply {
                        this.isSharedToCommunity = true
                        this.communityId = communityId
                    }

                    val fireRoutine = FireRoutine(
                        routineTable = routine,
                        reference_id = community.routine_references,
                        created_at = community.update_at,
                        update_at = community.created_at
                    )

                    community.routine_references!!.set(fireRoutine.firebaseMap)
                        .addOnSuccessListener {
                            Log.i("DatabaseSyncServices", "Routine ${routine.title} uploaded to Firestore")

                            notification.setContentText("$contextDescription session...")
                            startForeground(1, notification.build())

                            databaseJobs.add(
                                CoroutineScope(Dispatchers.IO).launch {
                                    reservationViewModel.insert(
                                        ReservationTable(
                                            documentId = communityId,
                                            communityId = communityId,
                                            tableName = fireRoutine.table,
                                            idFirebase = fireRoutine.id,
                                            idLocal = routine.id,
                                        )
                                    )

                                    routineViewModel.update(routine)

                                    Log.v("DatabaseSyncServices", "Reservation Routine ${routine.title}")
                                }
                            )



                        }

                    for (session in sessions) {
                        val fireSession = FireSession(
                            sessionTable = session,
                            reference_id = null,
                            created_at = community.update_at,
                            update_at = community.created_at
                        )
                        val documentId = "${communityId}${fireSession.table}${session.id}"
                        community.session_references!!.collection(fireSession.table)
                            .document(documentId)
                            .set(fireSession.firebaseMap)
                            .addOnSuccessListener {
                                Log.i("DatabaseSyncServices", "Session ${session.title} uploaded to Firestore")

                                notification.setContentText("$contextDescription ${session.title}...")
                                startForeground(1, notification.build())

                                databaseJobs.add(
                                    CoroutineScope(Dispatchers.IO).launch {
                                        reservationViewModel.insert(
                                            ReservationTable(
                                                documentId = documentId,
                                                communityId = communityId,
                                                tableName = fireSession.table,
                                                idFirebase = fireSession.id,
                                                idLocal = session.id,
                                            )
                                        )
                                        Log.v("DatabaseSyncServices", "Reservation Session ${session.title}")
                                    }
                                )

                            }
                            .addOnFailureListener {
                                Log.e("DatabaseSyncServices", it.message.toString())
                            }
                    }

                    for (task in tasks) {
                        val fireTask = FireTask(
                            taskTable = task,
                            reference_id = null,
                            created_at = community.update_at,
                            update_at = community.created_at
                        )
                        val documentId = "${communityId}${fireTask.table}${task.id}"
                        community.task_references!!.collection(fireTask.table)
                            .document(documentId)
                            .set(fireTask.firebaseMap)
                            .addOnSuccessListener {
                                Log.i("DatabaseSyncServices", "Task ${task.title} uploaded to Firestore")

                                notification.setContentText("$contextDescription ${task.title}...")
                                startForeground(1, notification.build())

                                databaseJobs.add(
                                    CoroutineScope(Dispatchers.IO).launch {
                                        reservationViewModel.insert(
                                            ReservationTable(
                                                documentId = documentId,
                                                communityId = communityId,
                                                tableName = fireTask.table,
                                                idFirebase = fireTask.id,
                                                idLocal = task.id,
                                            )
                                        )
                                        Log.v("DatabaseSyncServices", "Reservation Task ${task.title}")
                                    }
                                )

                            }
                            .addOnFailureListener {
                                Log.e("DatabaseSyncServices", it.message.toString())
                            }
                    }

                    for (providerIter in provider) {
                        val fireProvider = FireSessionTaskProvider(
                            sessionTaskProvider = providerIter,
                            reference_id = null,
                            created_at = community.update_at,
                            update_at = community.created_at
                        )
                        val additional = "${providerIter.indexDay}T${providerIter.fkTaskId}S${providerIter.fkSessionId}"
                        val documentId = "${communityId}${fireProvider.table}$additional"
                        community.provider_references!!.collection(fireProvider.table)
                            .document(documentId)
                            .set(fireProvider.firebaseMap)
                            .addOnSuccessListener {
                                Log.i("DatabaseSyncServices",
                                    "Provider ${providerIter.indexDay}" +
                                            "T${providerIter.fkTaskId}" +
                                            "S${providerIter.fkSessionId} " +
                                            "uploaded to Firestore")
                                databaseJobs.add(
                                    CoroutineScope(Dispatchers.IO).launch {
                                        reservationViewModel.insert(
                                            ReservationTable(
                                                documentId = documentId,
                                                communityId = communityId,
                                                tableName = fireProvider.table,
                                                idFirebase = 0,
                                                additionalDataLocal = additional,
                                                additionalDataFirebase = additional
                                            )
                                        )
                                        Log.v("DatabaseSyncServices", "Reservation Provider ${additional}")
                                    }
                                )
                            }
                            .addOnFailureListener {
                                Log.e("DatabaseSyncServices", it.message.toString())
                            }
                    }

                    for (note in notes) {
                        val fireNote = FireNotesTask(
                            notesTask = note,
                            reference_id = null,
                            created_at = community.update_at,
                            update_at = community.created_at
                        )
                        val documentId = "${communityId}${fireNote.table}${note.id}"
                        community.notes_task_references!!.collection(fireNote.table)
                            .document(documentId)
                            .set(fireNote.firebaseMap)
                            .addOnSuccessListener {
                                Log.i("DatabaseSyncServices", "Note ${note.category} uploaded to Firestore")

                                notification.setContentText("$contextDescription ${note.category}...")
                                startForeground(1, notification.build())


                                databaseJobs.add(
                                    CoroutineScope(Dispatchers.IO).launch {
                                        reservationViewModel.insert(
                                            ReservationTable(
                                                documentId = documentId,
                                                communityId = communityId,
                                                tableName = fireNote.table,
                                                idFirebase = fireNote.id,
                                                idLocal = note.id,
                                            )
                                        )
                                        Log.v("DatabaseSyncServices", "Reservation Note ${note.category}")
                                    }
                                )

                            }
                    }

                    for (note in todo) {
                        val fireNote = FireTodoNote(
                            todoNote = note,
                            reference_id = null,
                            created_at = community.update_at,
                            update_at = community.created_at
                        )
                        val documentId = "${communityId}${fireNote.table}${note.id}"
                        community.todo_notes_references!!.collection(fireNote.table)
                            .document(documentId)
                            .set(fireNote.firebaseMap)
                            .addOnSuccessListener {
                                Log.i("DatabaseSyncServices", "Todo ${note.description} uploaded to Firestore")

                                databaseJobs.add(
                                    CoroutineScope(Dispatchers.IO).launch {
                                        reservationViewModel.insert(
                                            ReservationTable(
                                                documentId = documentId,
                                                communityId = communityId,
                                                tableName = fireNote.table,
                                                idFirebase = fireNote.id,
                                                idLocal = note.id,
                                            )
                                        )
                                        Log.v("DatabaseSyncServices", "Reservation Todo ${note.description}")
                                    }

                                )

                            }
                    }


                    notification.setContentText("$contextDescription all done...")
                    startForeground(1, notification.build())


                }. addOnFailureListener {
                    failureAction(it, notification)
                    return@addOnFailureListener
                }


            databaseJobs.joinAll()
            Log.i("DatabaseSyncServices", "All databaseJobs done")
            stopSelf()


        } catch (e: Exception) {
            Log.e("DatabaseSyncServices", e.message.toString())
            failureAction(e, notification)
        }


    }


    private suspend fun downloadDatabase() {
        val channelId = "TODO_PLANNER_EDUCATION_EDITION_BY_MINIZUURE_SERVICES_NOTIFICATIONS"
        val syncTitle = getString(R.string.syncronizing)
        val contextDescription = getString(R.string.downloading)
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification_emblem)
            .setContentTitle("$syncTitle...")
            .setContentText(contextDescription)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(null)
            .build()
        startForeground(1, notification)

        // TODO: Download from Firestore
    }

    enum class SyncType {
        DOWNLOAD,
        CREATE_UPLOAD,
        UPDATE,
        CANCEL_ALL_OPERATIONS
    }



}