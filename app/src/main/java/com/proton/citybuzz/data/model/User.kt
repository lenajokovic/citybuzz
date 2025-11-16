package com.proton.citybuzz.data.model

//USERS (USER_ID int, NAME varchar, EMAIL varchar, PASSWORD varchar, PROFILEIMAGE varbinary)
class User(
    val id: Int,
    var name: String,
    var email: String,
    var password: String,
    val profileImage: ByteArray? = null
)
