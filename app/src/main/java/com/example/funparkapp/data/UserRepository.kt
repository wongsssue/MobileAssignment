package com.example.funparkapp.data

class UserRepository(private val userDao: UserDao) {
    suspend fun registerUser(user: UserType) {
        userDao.insert(user)
    }

    suspend fun getUserByUsername(username: String): UserType? {
        return userDao.getUserByUsername(username)
    }

    suspend fun changeUsername(oldUsername: String, newUsername: String) {
        // 1. Get the user with the old username
        val user = userDao.getUserByUsername(oldUsername)

        // 2. Update the username
        if (user != null) {
            val updatedUser = user.copy(username = newUsername)
            userDao.updateUser(updatedUser)
        }
    }

    suspend fun changePassword(username: String, newPassword: String) {
        // 1. Get the user
        val user = userDao.getUserByUsername(username)

        // 2. Update the password
        if (user != null) {
            val updatedUser = user.copy(password = newPassword) // Assuming you're not hashing
            userDao.updateUser(updatedUser)
        }
    }

    suspend fun getAllUsers(): List<UserType> {
        return userDao.getAllUsers()
    }

    suspend fun updateUser(user: UserType) {
        userDao.updateUser(user)
    }

    suspend fun deleteUser(user: UserType) {
        userDao.deleteUser(user)
    }

    suspend fun updateUserPoints(username: String, newPoints: Int) {
        userDao.updateUserPoints(username, newPoints)
    }
}