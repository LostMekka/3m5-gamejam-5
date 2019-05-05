package de.lostmekka._3m5.gamejam._5.entity

/**
 * Copyright 2019 LostMekkaSoft
 */
interface Connectable {
    val connections: MutableList<Connectable>
}
