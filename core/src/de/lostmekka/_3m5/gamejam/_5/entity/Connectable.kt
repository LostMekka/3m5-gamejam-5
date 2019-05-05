package de.lostmekka._3m5.gamejam._5.entity

import com.badlogic.gdx.math.Vector2

/**
 * Copyright 2019 LostMekkaSoft
 */
interface Connectable {
    val connections: MutableList<Connectable>
    val isBase: Boolean
    val connectionOrigin: Vector2
}

fun Connectable.connect(other: Connectable) {
    this.connections.add(other)
    other.connections.add(this)
}

fun Connectable.testForBuildingDeath(hasMogulAccess: Boolean): Boolean {
    if (isBase || hasMogulAccess) return false
    val toExpand = mutableListOf(this)
    val expanded = mutableListOf<Connectable>()
    val bases = mutableListOf<Connectable>()
    while (toExpand.isNotEmpty()) {
        val current = toExpand.removeAt(0)
        if (current.isBase) {
            if (current !in bases) bases += current
            if (bases.size > 1) return false
        }
        expanded += current
        toExpand += current.connections.filter { next -> expanded.none { it === next } }
    }
    return true
}

fun Connectable.handleDeath() {
    this.clearConnections()
}

fun Connectable.clearConnections() {
    for (connection in connections) {
        connection.connections.remove(this)
    }

    connections.clear()
}