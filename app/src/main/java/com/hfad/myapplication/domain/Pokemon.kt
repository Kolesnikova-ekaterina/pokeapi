package com.hfad.myapplication.domain

data class Pokemon (
    val id: Int,
    val name: String,
    val base_experience: Int,
    val height: Int,
    val is_default: Boolean,
    val order: Int,
    val weight: Int,
    val sprites: Sprite,
    )

data class Sprite(
    val front_default: String
)

data class NameId(
    val name: String
)

data class ApiData(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<NameId>
)