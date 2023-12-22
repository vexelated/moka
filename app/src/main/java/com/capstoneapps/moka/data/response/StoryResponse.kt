package com.capstoneapps.moka.data.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class StoryResponse(
	@field:SerializedName("listStory")
	val listStory: List<ListStoryItem> = emptyList(),
	val error: Boolean? = null,
	val message: String? = null
)

data class ListStoryItem(
	@field:SerializedName("photoUrl")
	val photoUrl: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("lon")
	val lon: Double = 0.0, // Menghapus tanda tanya (?) agar lon menjadi non-nullable

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("lat")
	val lat: Double = 0.0 // Menghapus tanda tanya (?) agar lat menjadi non-nullable
): Parcelable {
	constructor(parcel: Parcel) : this(
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readDouble(),
		parcel.readString(),
		parcel.readDouble()
	)

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(photoUrl)
		parcel.writeString(createdAt)
		parcel.writeString(name)
		parcel.writeString(description)
		parcel.writeDouble(lon)
		parcel.writeString(id)
		parcel.writeDouble(lat)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<ListStoryItem> {
		override fun createFromParcel(parcel: Parcel): ListStoryItem {
			return ListStoryItem(parcel)
		}

		override fun newArray(size: Int): Array<ListStoryItem?> {
			return arrayOfNulls(size)
		}
	}
}

