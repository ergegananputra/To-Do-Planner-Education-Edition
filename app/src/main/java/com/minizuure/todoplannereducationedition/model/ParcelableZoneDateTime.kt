package com.minizuure.todoplannereducationedition.model

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.Keep
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Keep
data class ParcelableZoneDateTime(val zoneDateTime: ZonedDateTime) : Parcelable {
    constructor(parcel: Parcel) : this(
        ZonedDateTime.parse(parcel.readString(), DateTimeFormatter.ISO_ZONED_DATE_TIME)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(zoneDateTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ParcelableZoneDateTime> {
        override fun createFromParcel(parcel: Parcel): ParcelableZoneDateTime {
            return ParcelableZoneDateTime(parcel)
        }

        override fun newArray(size: Int): Array<ParcelableZoneDateTime?> {
            return arrayOfNulls(size)
        }
    }

}