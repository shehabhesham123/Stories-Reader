package com.example.readerapp.feature.login.data.model

class User {
    var email: String? = null
        private set
    var password: String? = null
        private set
    var phone: String? = null
        private set

    constructor(email: String, password: String, phone: String) {
        this.email = email
        this.password = password
        this.phone = phone
    }

    constructor(email: String, password: String) {
        this.email = email
        this.password = password
    }

    constructor(phone: String) {
        this.phone = phone
    }
}