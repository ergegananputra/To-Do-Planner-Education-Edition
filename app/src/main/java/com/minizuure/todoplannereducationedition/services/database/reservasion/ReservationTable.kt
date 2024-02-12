package com.minizuure.todoplannereducationedition.services.database.reservasion

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * [ReservationTable]
 *
 *
 * Reservation table is used for mapping the id from firebase to local database.
 *
 *
 * Jika host memiliki id = 0, maka di orang lain dapat berbeda, untuk menjembatani hal ini
 * dan tidak mengganggu database user lain, maka kita menggunakan tabel ini untuk mapping.
 */
@Entity(
    tableName = "reservation_table",
)
data class ReservationTable (
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "document_id")
    val documentId: String = "",

    @ColumnInfo(name = "community_id")
    val communityId: String,

    @ColumnInfo(name = "table_name")
    val tableName: String,

    @ColumnInfo(name = "id_firebase")
    val idFirebase: Long,

    @ColumnInfo(name = "id_local")
    var idLocal: Long = 0,

    @ColumnInfo(name = "additional_data_local", defaultValue = "")
    var additionalDataLocal: String = "",

    @ColumnInfo(name = "additional_data_firebase", defaultValue = "")
    var additionalDataFirebase: String = ""


)



