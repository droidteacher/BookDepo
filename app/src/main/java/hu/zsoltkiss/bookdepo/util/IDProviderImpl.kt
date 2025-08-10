package hu.zsoltkiss.bookdepo.util

import kotlin.random.Random

class IDProviderImpl: IDProvider {
    override val currentUserId: Long
        get() = 101L

    override fun randomUserId(): Long {
        return Random.nextLong(1, 100)
    }
}