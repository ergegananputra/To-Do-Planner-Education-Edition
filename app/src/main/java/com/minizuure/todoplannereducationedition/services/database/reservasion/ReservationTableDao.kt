package com.minizuure.todoplannereducationedition.services.database.reservasion

import androidx.room.Dao
import androidx.room.Query
import com.minizuure.todoplannereducationedition.services.database.BaseIODao

@Dao
interface ReservationTableDao : BaseIODao<ReservationTable>{

    @Query("SELECT * FROM reservation_table")
    suspend fun getAll(): List<ReservationTable>

    @Query("SELECT * FROM reservation_table WHERE document_id = :documentId")
    suspend fun getById(documentId : String): ReservationTable?

    @Query("SELECT * FROM reservation_table WHERE community_id = :communityId")
    suspend fun getAllByCommunity(communityId : String): List<ReservationTable>

    @Query("SELECT * FROM reservation_table WHERE community_id = :communityId AND table_name = :tableName")
    suspend fun getAllByCommunityAndTable(communityId : String, tableName: String): List<ReservationTable>

    @Query("SELECT COUNT(*) FROM reservation_table")
    suspend fun getCount(): Int


    @Query("""
        SELECT * 
        FROM reservation_table 
        WHERE 
            community_id = :communityId 
            AND table_name = :tableName 
            AND id_firebase = :idFirebase
        LIMIT 1
            """)
    suspend fun getByIdsFirebase(communityId : String, tableName : String, idFirebase : Long): ReservationTable?

    @Query("""
        SELECT * 
        FROM reservation_table 
        WHERE 
            community_id = :communityId 
            AND table_name = :tableName 
            AND id_local = :idLocal
        LIMIT 1
            """)
    suspend fun getByIdsLocal(communityId : String, tableName : String, idLocal : Long): ReservationTable?

}