package com.capstoneapps.moka.data.response

data class UpdateTaskResponse(
	val data: Data? = null,
	val message: String? = null,
	val status: Int? = null
)

data class DataUp(
	val taskId: String? = null
)

