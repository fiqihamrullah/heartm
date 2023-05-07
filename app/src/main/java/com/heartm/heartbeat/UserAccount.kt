package com.heartm.heartbeat

class UserAccount
{
    companion object
    {
        private var username: String? = null
        private var doctorName: String? = null
        private var doctorPhoneNumber: String? = null
        private var token: String? = null
        private val expirity: Int = 0
        private var access : Int = 0
        private var ID: String? = null
        private var email: String? = null

        private var usia:Int=0
        private var gender:String=""


        fun setID(id: String)
        {
            UserAccount.ID = id
        }


        fun setUsia(usia:Int)
        {
            UserAccount.usia = usia
        }

        fun getUsia():Int
        {
            return UserAccount.usia
        }

        fun setGender(gender:String)
        {
            UserAccount.gender = gender
        }


        fun getGender():String
        {
           return UserAccount.gender
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

        fun setDoctorName(name:String)
        {
            UserAccount.doctorName = name
        }

        fun getDoctorName():String?
        {
            return doctorName
        }

        fun setDoctorPhoneNumber(phone:String)
        {
            UserAccount.doctorPhoneNumber = phone
        }

        fun getDoctorPhoneNumber():String?
        {
                return doctorPhoneNumber
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