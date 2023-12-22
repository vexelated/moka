package com.capstoneapps.moka.data.response

data class DeleteTaskResponse(
	val data: Data? = null,
	val message: String? = null,
	val status: Int? = null
)

data class DataDel(
	val taskdescription: String? = null,
	val statustask: Int? = null,
	val idtask: String? = null,
	val taskdate: String? = null,
	val taskname: String? = null
)

