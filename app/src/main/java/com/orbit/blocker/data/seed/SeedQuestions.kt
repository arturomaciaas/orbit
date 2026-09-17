package com.orbit.blocker.data.seed

import com.orbit.blocker.data.model.Question
import com.orbit.blocker.data.model.QuizTopic

/**
 * Curated starter question bank. All entries are marked [Question.seeded] = true so a
 * future "reset bank" can distinguish them from user-authored questions. The user can
 * edit or delete any of these from the Quiz Bank screen.
 */
object SeedQuestions {

    fun all(): List<Question> = systemDesign + chess + softwareEngineering + aws

    private fun q(
        topic: QuizTopic,
        prompt: String,
        choices: List<String>,
        correctIndex: Int,
        explanation: String,
    ) = Question(
        topic = topic,
        prompt = prompt,
        choices = choices,
        correctIndex = correctIndex,
        explanation = explanation,
        seeded = true,
    )

    private val systemDesign = listOf(
        q(
            QuizTopic.SYSTEM_DESIGN,
            "Which technique most directly reduces read load on a primary database?",
            listOf("Read replicas", "Increasing the primary's disk size", "Adding more application servers", "Using a stronger password policy"),
            0,
            "Read replicas serve read traffic, offloading the primary and improving read scalability.",
        ),
        q(
            QuizTopic.SYSTEM_DESIGN,
            "What is the main purpose of a load balancer?",
            listOf("Encrypt data at rest", "Distribute incoming requests across servers", "Compress images", "Store session cookies"),
            1,
            "A load balancer spreads traffic across backends to improve availability and utilization.",
        ),
        q(
            QuizTopic.SYSTEM_DESIGN,
            "In the CAP theorem, a network partition forces a system to choose between which two properties?",
            listOf("Consistency and Availability", "Caching and Persistence", "Concurrency and Atomicity", "Compression and Authentication"),
            0,
            "During a partition (P), a distributed system must trade off Consistency versus Availability.",
        ),
        q(
            QuizTopic.SYSTEM_DESIGN,
            "Which cache eviction policy removes the entry unused for the longest time?",
            listOf("FIFO", "LRU", "Random", "Write-through"),
            1,
            "LRU (Least Recently Used) evicts the entry that has gone longest without access.",
        ),
        q(
            QuizTopic.SYSTEM_DESIGN,
            "What does idempotency guarantee for an API request?",
            listOf("It always returns cached data", "Repeating it has the same effect as doing it once", "It never fails", "It runs faster each time"),
            1,
            "An idempotent operation can be retried safely; repeats do not change the result beyond the first.",
        ),
        q(
            QuizTopic.SYSTEM_DESIGN,
            "A message queue between services primarily provides what benefit?",
            listOf("Stronger encryption", "Tighter coupling", "Asynchronous decoupling and buffering", "Lower storage cost"),
            2,
            "Queues decouple producers from consumers and absorb bursts, smoothing load.",
        ),
    )

    private val chess = listOf(
        q(
            QuizTopic.CHESS,
            "What is the only piece that can jump over other pieces?",
            listOf("Bishop", "Rook", "Knight", "Queen"),
            2,
            "The knight moves in an L-shape and is the only piece that leaps over others.",
        ),
        q(
            QuizTopic.CHESS,
            "Castling is not allowed if which of these is true?",
            listOf("The king has already moved", "It is the endgame", "You have a queen", "Your opponent castled first"),
            0,
            "Castling is illegal if the king (or the involved rook) has moved, or the king is/passes through check.",
        ),
        q(
            QuizTopic.CHESS,
            "What is the term for a pawn reaching the opponent's back rank?",
            listOf("En passant", "Promotion", "Zugzwang", "Fork"),
            1,
            "A pawn reaching the last rank is promoted, usually to a queen.",
        ),
        q(
            QuizTopic.CHESS,
            "Which opening begins 1.e4 e5 2.Nf3 Nc6 3.Bb5?",
            listOf("Italian Game", "Ruy Lopez", "Sicilian Defense", "French Defense"),
            1,
            "3.Bb5 characterizes the Ruy Lopez (Spanish Opening).",
        ),
        q(
            QuizTopic.CHESS,
            "A 'fork' is a tactic where one piece does what?",
            listOf("Blocks a check", "Attacks two or more pieces at once", "Promotes early", "Defends the king"),
            1,
            "A fork attacks multiple targets simultaneously, winning material.",
        ),
        q(
            QuizTopic.CHESS,
            "What does 'zugzwang' mean?",
            listOf("A winning sacrifice", "Any move worsens the position", "A drawn endgame", "A double check"),
            1,
            "In zugzwang, a player is forced to move and every option damages their position.",
        ),
    )

    private val softwareEngineering = listOf(
        q(
            QuizTopic.SOFTWARE_ENGINEERING,
            "What is the time complexity of binary search on a sorted array?",
            listOf("O(n)", "O(log n)", "O(n log n)", "O(1)"),
            1,
            "Binary search halves the search space each step, giving O(log n).",
        ),
        q(
            QuizTopic.SOFTWARE_ENGINEERING,
            "In Git, which command creates a new commit that undoes a previous commit?",
            listOf("git revert", "git reset --hard", "git rm", "git stash"),
            0,
            "git revert creates a new commit that reverses changes, preserving history.",
        ),
        q(
            QuizTopic.SOFTWARE_ENGINEERING,
            "What does the 'S' in SOLID stand for?",
            listOf("Separation", "Single Responsibility", "Stateless", "Synchronization"),
            1,
            "The Single Responsibility Principle: a class should have one reason to change.",
        ),
        q(
            QuizTopic.SOFTWARE_ENGINEERING,
            "Which data structure operates first-in, first-out (FIFO)?",
            listOf("Stack", "Queue", "Tree", "Hash map"),
            1,
            "A queue processes elements in the order they were added (FIFO).",
        ),
        q(
            QuizTopic.SOFTWARE_ENGINEERING,
            "What is a race condition?",
            listOf("A slow algorithm", "A bug from uncontrolled concurrent access to shared state", "A compiler error", "A type mismatch"),
            1,
            "Race conditions arise when concurrent operations interleave over shared state without synchronization.",
        ),
        q(
            QuizTopic.SOFTWARE_ENGINEERING,
            "What does an immutable object guarantee?",
            listOf("Its state cannot change after construction", "It uses less memory always", "It is thread-unsafe", "It cannot be garbage collected"),
            0,
            "Immutable objects cannot be modified after creation, which makes them inherently thread-safe to share.",
        ),
    )

    private val aws = listOf(
        q(
            QuizTopic.AWS,
            "Which AWS service provides object storage?",
            listOf("Amazon EC2", "Amazon S3", "Amazon RDS", "Amazon VPC"),
            1,
            "Amazon S3 (Simple Storage Service) stores objects in buckets.",
        ),
        q(
            QuizTopic.AWS,
            "What does an AWS IAM role primarily provide?",
            listOf("A static password", "Temporary permissions assumable by entities", "A billing alert", "A DNS record"),
            1,
            "IAM roles grant temporary credentials that trusted entities can assume.",
        ),
        q(
            QuizTopic.AWS,
            "Which service runs code without provisioning servers?",
            listOf("AWS Lambda", "Amazon EBS", "Amazon Route 53", "AWS Direct Connect"),
            0,
            "AWS Lambda runs functions on demand without server management (serverless).",
        ),
        q(
            QuizTopic.AWS,
            "Amazon RDS is best described as what?",
            listOf("A managed relational database service", "A CDN", "An object store", "A message queue"),
            0,
            "RDS manages relational databases like PostgreSQL and MySQL, handling patching and backups.",
        ),
        q(
            QuizTopic.AWS,
            "What is the purpose of an Auto Scaling group?",
            listOf("Encrypt EBS volumes", "Automatically adjust the number of EC2 instances to demand", "Route DNS globally", "Store secrets"),
            1,
            "Auto Scaling groups add or remove instances based on load or schedules.",
        ),
        q(
            QuizTopic.AWS,
            "Which service is a fully managed NoSQL key-value database?",
            listOf("Amazon DynamoDB", "Amazon Redshift", "AWS Glue", "Amazon Athena"),
            0,
            "DynamoDB is AWS's managed NoSQL key-value and document database.",
        ),
    )
}
