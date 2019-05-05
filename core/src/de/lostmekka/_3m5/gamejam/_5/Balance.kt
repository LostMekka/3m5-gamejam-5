package de.lostmekka._3m5.gamejam._5

import com.badlogic.gdx.graphics.Color

/**
 * Copyright 2019 LostMekkaSoft
 */

const val MogulSpeed = 4.0f

const val towerHP = 30
const val towerAttackRadius = 5f
const val towerAttackDamage = 8
const val towerCooldown = 0.5f

const val dudeHP = 10
const val dudesight = 16f
const val dudeSpeed = 0.8f
const val dudeMeleeRadius = 0.2f
const val dudeMeleeDamage = 1
const val dudeMeleeCooldown = 1f

const val dudeSpawnerCooldown = 2f
const val dudeSpawnerRandomX = 3f
const val dudeSpawnerRandomY = 3f

const val laserLifetime = 0.1f
val laserColor = Color.GOLD

var slaves = 0
val buildPreviewGood = Color(0f, 1f, 0.3f, 0.5f)
val buildPreviewBad = Color(1f, 0f, 0f, 0.5f)
val buildCaravanPostMogulDistance = 5f
val buildCaravanPostPostDistance = 5f
val buildCaravanPostMagistrateDistance = 5f
