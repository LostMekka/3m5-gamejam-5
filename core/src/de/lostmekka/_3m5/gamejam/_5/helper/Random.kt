package de.lostmekka._3m5.gamejam._5.helper

import java.util.*

/**
 * Copyright 2019 LostMekkaSoft
 */

private val random = Random()

fun <T> List<T>.randomElement() = this[random.nextInt(size)]
