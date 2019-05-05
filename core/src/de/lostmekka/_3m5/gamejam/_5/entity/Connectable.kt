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

fun Connectable.testForBuildingDeath(hasMogulAccess: Boolean): List<Connectable> {
    if (isBase) return listOf()
    val dyingEntities = mutableListOf<Connectable>()
    var magistrateCount = 0
    if (hasMogulAccess) magistrateCount++

    for (child in connections) {
        var childHasConnectionToBase = false
        val toExpand = mutableListOf(child)
        val expanded = mutableListOf<Connectable>()
        val linedUpEntities = mutableListOf(child)
        var isSingleLine = true
        while (!childHasConnectionToBase && toExpand.isNotEmpty()) {
            val current = toExpand.removeAt(0)
            if (current.isBase) {
                childHasConnectionToBase = true
                break
            }
            expanded += current
            val nextConnections = current.connections.filter { next -> expanded.none { it === next } }
            toExpand += nextConnections
            isSingleLine = isSingleLine && nextConnections.size == 1
            if (isSingleLine) linedUpEntities += current
        }
        if (childHasConnectionToBase) {
            magistrateCount++
        } else {
            dyingEntities += linedUpEntities
        }
    }

    return if (magistrateCount >= 2) listOf() else dyingEntities
}
