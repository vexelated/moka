package com.capstoneapps.moka.data.response

data class GetTaskResponse(
	val data: List<DataItem?>? = null,
	val message: String? = null,
	val status: Int? = null
)

data class DataItem(
	val taskdescription: String? = null,
	val statustask: Int? = null,
	val idtask: String? = null,
	val taskdate: String? = null,
	val taskname: String? = null
) {
}

