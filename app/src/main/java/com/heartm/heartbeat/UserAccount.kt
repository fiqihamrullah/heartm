package com.heartm.heartbeat

class UserAccount
{
    companion object
    {
        private var username: String? = null
        private var token: String? = null
        private val expirity: Int = 0
        private var access : Int = 0
        private var ID: String? = null
        private var email: String? = null


        fun setID(id: String) {
            UserAccount.ID = id
        }


        fun getID(): String? {
            return ID
        }


        fun setToken(token: String) {
            UserAccount.token = token
        }

        fun getToken(): String? {
            return token
        }

        fun setUserName(username: String) {
            UserAccount.username = username
        }

        fun getUsername(): String? {
            return username
        }

        fun setAccess(access:Int)
        {
            UserAccount.access = access
        }

        fun getAccess():Int
        {
            return access
        }

        fun setEmail(email: String) {
            UserAccount.email = email
        }

        fun getEmail(): String? {
            return email
        }


    }
}