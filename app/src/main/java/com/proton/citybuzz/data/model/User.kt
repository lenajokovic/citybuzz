package com.proton.citybuzz.data.model

//USERS (USER_ID int, NAME varchar, EMAIL varchar, PASSWORD varchar, PROFILEIMAGE varchar)
class User(
    val id: Int,
    val name: String,
    val email: String,
    val password: String,
    val profileImage: String? = null // URI ili URL slike
)
