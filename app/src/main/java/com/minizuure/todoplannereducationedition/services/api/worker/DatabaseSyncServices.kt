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

/**
 * This service is used to sync the local database with the Firestore database.
 * It can be used to upload the local database to Firestore or download the Firestore database to the local database.
 *
 * TODO:
 * - Datbase masih berantakan di firestore
 * - sink donwload belum
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
            SyncType.DOWNLOAD.toString() -> downloadDatabase()
            SyncType.UPLOAD.toString() -> CoroutineScope(Dispatchers.Default).launch{
                uploadDatabase()
            }
            SyncType.CANCEL_ALL_OPERATIONS.toString() -> stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
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

        fun failureAction(e: Exception) {
            val errorMsg = getString(R.string.error_uploading_to_firestore)
            notification.setContentText(errorMsg)
            startForeground(1, notification.build())
            Log.e("DatabaseSyncServices", "Error: ${e.message.toString()}")
            stopSelf()
        }

        try {
            val databaseJobs : MutableList<Job> = mutableListOf()


            val isHost = UserPreferences(this@DatabaseSyncServices).isCommunityHost
            val localRoutineId = UserPreferences(this@DatabaseSyncServices).communityId.toLong()

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

            val community : Community = Community(
                owner_references = firestore.collection(FireUser().table).document(auth.currentUser!!.uid),
                update_at = Timestamp.now(),
                created_at = Timestamp.now()
            )

            // NOTE: Upload to Firestore

            firestore.collection(community.table).add(community.firebaseMap).addOnSuccessListener {communityRef ->
                val communityId = communityRef.id

                community.apply {
                    reference_id = communityRef
                    members_references = firestore.collection(FireMember().parent).document(communityId)
                    routine_references = firestore.collection(FireRoutine().table).document(communityId)
                    session_references = firestore.collection(FireSession().parent).document(communityId)
                    task_references = firestore.collection(FireTask().parent).document(communityId)
                    provider_references = firestore.collection(FireSessionTaskProvider().parent).document(communityId)
                    notes_task_references = firestore.collection(FireNotesTask().parent).document(communityId)
                    todo_notes_references = firestore.collection(FireTodoNote().parent).document(communityId)
                }
                communityRef.update(community.firebaseMap).addOnSuccessListener {

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
                        val documentId = "${communityId}${fireProvider.table}${providerIter.indexDay}T${providerIter.fkTaskId}S${providerIter.fkSessionId}"
                        community.provider_references!!.collection(fireProvider.table)
                            .document(documentId)
                            .set(fireProvider.firebaseMap)
                            .addOnSuccessListener {
                                Log.i("DatabaseSyncServices",
                                    "Provider ${providerIter.indexDay}" +
                                            "T${providerIter.fkTaskId}" +
                                            "S${providerIter.fkSessionId} " +
                                            "uploaded to Firestore")
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



                }.addOnFailureListener {
                    failureAction(it)
                    return@addOnFailureListener
                }

            }. addOnFailureListener {
                failureAction(it)
                return@addOnFailureListener
            }


            databaseJobs.joinAll()
            Log.i("DatabaseSyncServices", "All databaseJobs done")
            stopSelf()


        } catch (e: Exception) {
            Log.e("DatabaseSyncServices", e.message.toString())
            failureAction(e)
        }


    }


    private fun downloadDatabase() {
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
    }

    enum class SyncType {
        DOWNLOAD,
        UPLOAD,
        CANCEL_ALL_OPERATIONS
    }



}