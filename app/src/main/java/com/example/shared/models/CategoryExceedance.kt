package com.example.shared.models

data class CategoryExceedance(
    val category: Category,
    val budgetLimit: Long,
    val currentSpent: Long
)
