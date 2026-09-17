package com.orbit.blocker.domain.quiz

import com.orbit.blocker.data.model.Question

/**
 * Pure selection helpers for building a quiz from a bank. Kept separate from the
 * DAO's RANDOM() query so selection strategy is unit-testable and deterministic
 * when a seeded [random] is supplied.
 */
object QuestionSelector {

    /**
     * Picks [count] questions, spreading across the distinct topics present in [pool]
     * as evenly as possible (round-robin over shuffled per-topic buckets). If [pool]
     * has fewer than [count] questions, returns all of them shuffled.
     */
    fun pickBalanced(
        pool: List<Question>,
        count: Int,
        random: kotlin.random.Random = kotlin.random.Random.Default,
    ): List<Question> {
        if (count <= 0 || pool.isEmpty()) return emptyList()
        if (pool.size <= count) return pool.shuffled(random)

        val buckets = pool.groupBy { it.topic }
            .mapValues { (_, qs) -> qs.shuffled(random).toMutableList() }
            .values
            .toMutableList()
            .apply { shuffle(random) }

        val result = ArrayList<Question>(count)
        while (result.size < count && buckets.any { it.isNotEmpty() }) {
            for (bucket in buckets) {
                if (result.size >= count) break
                if (bucket.isNotEmpty()) result.add(bucket.removeAt(0))
            }
        }
        return result
    }
}
