package hu.zsoltkiss.bookdepo.util

interface IDProvider {
    val currentUserId: Long
    fun randomUserId(): Long
}