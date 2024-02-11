package com.minizuure.todoplannereducationedition.services.database.reservasion

import androidx.lifecycle.ViewModel
import com.minizuure.todoplannereducationedition.AppDatabaseRepository

class ReservationViewModel(
    private val appDatabaseRepository: AppDatabaseRepository
) : ViewModel() {

    suspend fun getAll() : List<ReservationTable> {
        return appDatabaseRepository.getAllReservations()
    }

    suspend fun getById(documentId : String) : ReservationTable? {
        return appDatabaseRepository.getReservationById(documentId)
    }

    suspend fun getAllByCommunity(communityId : String) : List<ReservationTable> {
        return appDatabaseRepository.getAllReservationByCommunity(communityId)
    }

    suspend fun getAllByCommunityAndTable(communityId : String, tableName: String) : List<ReservationTable> {
        return appDatabaseRepository.getAllReservationByCommunityAndTable(communityId, tableName)
    }

    suspend fun getCount() : Int {
        return appDatabaseRepository.getCountReservations()
    }

    suspend fun getByIdsFirebase(communityId : String, tableName : String, idFirebase : Long) : ReservationTable? {
        return appDatabaseRepository.getReservationByIdsFirebase(communityId, tableName, idFirebase)
    }

    suspend fun getByIdsLocal(communityId : String, tableName : String, idLocal : Long) : ReservationTable? {
        return appDatabaseRepository.getReservationByIdsLocal(communityId, tableName, idLocal)
    }

    suspend fun insert(reservationTable: ReservationTable) {
        appDatabaseRepository.insertReservation(reservationTable)
    }


    suspend fun delete(reservationTable: ReservationTable) {
        appDatabaseRepository.deleteReservation(reservationTable)
    }

    suspend fun update(reservationTable: ReservationTable) {
        appDatabaseRepository.updateReservation(reservationTable)
    }

}