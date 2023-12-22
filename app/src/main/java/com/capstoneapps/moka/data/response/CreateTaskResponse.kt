package com.capstoneapps.moka.data.response

data class CreateTaskResponse(
	val data: Data? = null,
	val message: String? = null,
	val status: Int? = null
)

data class Data(
	val taskId: String? = null
)

