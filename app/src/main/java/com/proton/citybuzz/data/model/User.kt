package com.proton.citybuzz.data.model

//USERS (USER_ID int, NAME varchar, EMAIL varchar, PASSWORD varchar, PROFILEIMAGE varchar)
class User(
    val id: Int,
    var name: String,
    var email: String,
    var password: String,
    val profileImage: String? = null // URI ili URL slike
)
