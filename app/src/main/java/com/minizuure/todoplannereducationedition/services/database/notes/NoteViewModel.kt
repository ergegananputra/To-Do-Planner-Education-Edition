package com.minizuure.todoplannereducationedition.services.database.notes

import androidx.lifecycle.ViewModel
import com.minizuure.todoplannereducationedition.AppDatabaseRepository
import com.minizuure.todoplannereducationedition.services.datetime.DatetimeAppManager

class NoteViewModel(
    private val appDatabaseRepository: AppDatabaseRepository
) : ViewModel() {
    val note = Note()
    val todo = Todo()

    inner class Note {
        suspend fun getAll() : List<NotesTaskTable> {
            return appDatabaseRepository.getAllNotesTasks()
        }

        suspend fun getById(id: Long) : NotesTaskTable? {
            return appDatabaseRepository.getNotesTaskById(id)
        }

        suspend fun getCount() : Int {
            return appDatabaseRepository.getCountNotesTasks()
        }

        suspend fun getByFKTaskId(fkTaskId: Long) : List<NotesTaskTable>? {
            return appDatabaseRepository.getNotesTaskByTaskId(fkTaskId)
        }

        suspend fun getByFKTaskIdAndCategory(fkTaskId: Long, category: String) : NotesTaskTable? {
            return appDatabaseRepository.getNotesTaskByTaskIdAndCategory(fkTaskId, category)
        }

        suspend fun getCountByFKTaskIdAndCategory(fkTaskId: Long, category: String) : Int {
            return appDatabaseRepository.getCountNotesTaskByTaskIdAndCategory(fkTaskId, category)
        }

        /**
         * To get the date converted to ISO8601 ZonedDateTime to String,
         * use [DatetimeAppManager] and set accuracy to day
         *
         *
         * example:
         * ```kotlin
         * // zonedDatetime from fragment args parcelable
         * val zonedDatetime : ZonedDateTime = args.selectedDatetimeDetailIso.zoneDateTime
         * val date : String = DatetimeAppManager(zonedDatetime, true).dateISO8601inString
         * ```
         *
         *
         * @return The id of the inserted notesTaskTable. (-1L if failed)
         */
        suspend fun insert(note: NotesTaskTable) : Long {
            return appDatabaseRepository.insertNotesTask(note)
        }

        /**
         * To get the date converted to ISO8601 ZonedDateTime to String,
         * use [DatetimeAppManager] and set accuracy to day
         *
         *
         * example:
         * ```kotlin
         * // zonedDatetime from fragment args parcelable
         * val zonedDatetime : ZonedDateTime = args.selectedDatetimeDetailIso.zoneDateTime
         * val date : String = DatetimeAppManager(zonedDatetime, true).dateISO8601inString
         * ```
         *
         *
         *
         * @param fkTaskId The id of the taskTable.
         * @param date The date the notesTaskTable is active. Stored in ISO8601 format.
         * @param category The category of the notesTaskTable example: Quiz, To-Pack, Memo.
         * @param description The description of the notesTaskTable.
         * @return The id of the inserted notesTaskTable. (-1L if failed)
         */
        suspend fun insert(
            fkTaskId : Long,
            category : String,
            description : String,
            date : String
        ) : Long {
            return appDatabaseRepository.insertNotesTask(
                NotesTaskTable(
                    fkTaskId = fkTaskId,
                    category = category,
                    description = description,
                    dateISO8601 = date
                )
            )
        }

        suspend fun update(
            id: Long,
            description: String
        ) : Boolean {
            val note = appDatabaseRepository.getNotesTaskById(id) ?: return false

            note.apply {
                this.description = description
            }

            appDatabaseRepository.updateNotesTask(note)
            return true
        }

        suspend fun delete(note: NotesTaskTable) {
            appDatabaseRepository.deleteNotesTask(note)
        }

        suspend fun deleteById(id: Long) {
            val noteDeleted = appDatabaseRepository.getNotesTaskById(id) ?: return
            appDatabaseRepository.deleteNotesTask(noteDeleted)
        }
    }

    inner class Todo {
        suspend fun getAll() : List<TodoNoteTable> {
            return appDatabaseRepository.getAllTodoNotes()
        }

        suspend fun getById(id: Long) : TodoNoteTable? {
            return appDatabaseRepository.getTodoNoteById(id)
        }

        suspend fun getCount() : Int {
            return appDatabaseRepository.getCountTodoNotes()
        }

        suspend fun getByFKNoteId(fkNoteId: Long) : List<TodoNoteTable>? {
            return appDatabaseRepository.getTodoNoteByFkNoteId(fkNoteId)
        }

        suspend fun insert(todo: TodoNoteTable) : Long {
            return appDatabaseRepository.insertTodoNotes(todo)
        }

        suspend fun insert(
            fkNoteTaskId : Long,
            isChecked : Boolean,
            description : String,
        ) : Long {
            return appDatabaseRepository.insertTodoNotes(
                TodoNoteTable(
                    fkNotesTaskId = fkNoteTaskId,
                    isChecked = isChecked,
                    description = description
                )
            )
        }

        suspend fun update(
            id: Long,
            isChecked: Boolean,
            description: String
        ) : Boolean{
            val todo = getById(id = id) ?: return false

            todo.apply {
                this.isChecked = isChecked
                this.description = description
            }

            appDatabaseRepository.updateTodoNotes(todo)
            return true
        }

        suspend fun updateChecked(
            id: Long,
            isChecked: Boolean
        ) : Boolean{
            val todo = getById(id = id) ?: return false

            todo.apply {
                this.isChecked = isChecked
            }

            appDatabaseRepository.updateTodoNotes(todo)
            return true
        }

        suspend fun updateDescription(
            id: Long,
            description: String
        ) : Boolean{
            val todo = getById(id = id) ?: return false

            todo.apply {
                this.description = description
            }

            appDatabaseRepository.updateTodoNotes(todo)
            return true
        }

        suspend fun delete(todo: TodoNoteTable) {
            appDatabaseRepository.deleteTodoNotes(todo)
        }

        suspend fun deleteById(id: Long) {
            val todoDeleted = getById(id = id) ?: return
            appDatabaseRepository.deleteTodoNotes(todoDeleted)
        }
    }



}