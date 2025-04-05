package com.narxoz.myapplication.data

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.narxoz.myapplication.models.User
import com.narxoz.myapplication.models.UserRole

class UserDao(private val database: AppDatabase) {

    fun createUser(user: User): Long {
        val db = database.writableDatabase
        val values = ContentValues().apply {
            put(AppDatabase.COLUMN_USERNAME, user.username)
            put(AppDatabase.COLUMN_PASSWORD, user.password)
            put(AppDatabase.COLUMN_ROLE, user.role.name.lowercase())
            put(AppDatabase.COLUMN_FULL_NAME, user.fullName)
            put(AppDatabase.COLUMN_EMAIL, user.email)
        }
        
        return db.insert(AppDatabase.TABLE_USERS, null, values)
    }
    
    fun getUserById(id: Long): User? {
        val db = database.readableDatabase
        val cursor = db.query(
            AppDatabase.TABLE_USERS,
            null,
            "${AppDatabase.COLUMN_ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        
        return if (cursor.moveToFirst()) {
            val user = cursorToUser(cursor)
            cursor.close()
            user
        } else {
            cursor.close()
            null
        }
    }
    
    fun getUserByUsername(username: String): User? {
        val db = database.readableDatabase
        val cursor = db.query(
            AppDatabase.TABLE_USERS,
            null,
            "${AppDatabase.COLUMN_USERNAME} = ?",
            arrayOf(username),
            null,
            null,
            null
        )
        
        return if (cursor.moveToFirst()) {
            val user = cursorToUser(cursor)
            cursor.close()
            user
        } else {
            cursor.close()
            null
        }
    }
    
    fun getAllUsers(): List<User> {
        val users = mutableListOf<User>()
        val db = database.readableDatabase
        val cursor = db.query(
            AppDatabase.TABLE_USERS,
            null,
            null,
            null,
            null,
            null,
            null
        )
        
        while (cursor.moveToNext()) {
            users.add(cursorToUser(cursor))
        }
        
        cursor.close()
        return users
    }
    
    fun getUsersByRole(role: UserRole): List<User> {
        val users = mutableListOf<User>()
        val db = database.readableDatabase
        val cursor = db.query(
            AppDatabase.TABLE_USERS,
            null,
            "${AppDatabase.COLUMN_ROLE} = ?",
            arrayOf(role.name.lowercase()),
            null,
            null,
            null
        )
        
        while (cursor.moveToNext()) {
            users.add(cursorToUser(cursor))
        }
        
        cursor.close()
        return users
    }
    
    fun updateUser(user: User): Int {
        val db = database.writableDatabase
        val values = ContentValues().apply {
            put(AppDatabase.COLUMN_USERNAME, user.username)
            put(AppDatabase.COLUMN_PASSWORD, user.password)
            put(AppDatabase.COLUMN_ROLE, user.role.name.lowercase())
            put(AppDatabase.COLUMN_FULL_NAME, user.fullName)
            put(AppDatabase.COLUMN_EMAIL, user.email)
        }
        
        return db.update(
            AppDatabase.TABLE_USERS,
            values,
            "${AppDatabase.COLUMN_ID} = ?",
            arrayOf(user.id.toString())
        )
    }
    
    fun deleteUser(id: Long): Int {
        val db = database.writableDatabase
        return db.delete(
            AppDatabase.TABLE_USERS,
            "${AppDatabase.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
    }
    
    fun authenticateUser(username: String, password: String): User? {
        val db = database.readableDatabase
        val cursor = db.query(
            AppDatabase.TABLE_USERS,
            null,
            "${AppDatabase.COLUMN_USERNAME} = ? AND ${AppDatabase.COLUMN_PASSWORD} = ?",
            arrayOf(username, password),
            null,
            null,
            null
        )
        
        return if (cursor.moveToFirst()) {
            val user = cursorToUser(cursor)
            cursor.close()
            user
        } else {
            cursor.close()
            null
        }
    }
    
    private fun cursorToUser(cursor: Cursor): User {
        val idIndex = cursor.getColumnIndex(AppDatabase.COLUMN_ID)
        val usernameIndex = cursor.getColumnIndex(AppDatabase.COLUMN_USERNAME)
        val passwordIndex = cursor.getColumnIndex(AppDatabase.COLUMN_PASSWORD)
        val roleIndex = cursor.getColumnIndex(AppDatabase.COLUMN_ROLE)
        val fullNameIndex = cursor.getColumnIndex(AppDatabase.COLUMN_FULL_NAME)
        val emailIndex = cursor.getColumnIndex(AppDatabase.COLUMN_EMAIL)
        val createdAtIndex = cursor.getColumnIndex(AppDatabase.COLUMN_CREATED_AT)
        
        return User(
            id = cursor.getLong(idIndex),
            username = cursor.getString(usernameIndex),
            password = cursor.getString(passwordIndex),
            role = UserRole.fromString(cursor.getString(roleIndex)),
            fullName = cursor.getString(fullNameIndex),
            email = if (emailIndex != -1 && !cursor.isNull(emailIndex)) cursor.getString(emailIndex) else null,
            createdAt = if (createdAtIndex != -1 && !cursor.isNull(createdAtIndex)) cursor.getString(createdAtIndex) else null
        )
    }
} 