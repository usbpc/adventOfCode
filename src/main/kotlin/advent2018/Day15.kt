package advent2018

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import java.util.*

class Day15(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 15
    private val input = adventOfCode.getInput(2018, day).lines()

    private fun inputSpaceArray() = input.map { row -> row.map { c -> if (c == 'E' || c == 'G') '.' else c}}

    enum class Type {
        ELF,
        GOBLIN
    }

    data class Fighter(val type: Type, var x: Int, var y: Int, var health : Int = 200, val attackPower : Int = 3) : Comparable<Fighter> {
        fun getHit(amount: Int) {
            health -= amount
        }

        override fun compareTo(other: Fighter): Int =
                if (this.y == other.y) {
                    this.x - other.x
                } else {
                    this.y - other.y
                }

        fun positionAsPoint() = Point(x, y)
    }

    private fun characterList(elfAttack : Int = 3) : List<Fighter> {
        val out = mutableListOf<Fighter>()

        input.forEachIndexed { y, row ->
            row.forEachIndexed { x, c ->
                when (c) {
                    'E' -> out.add(Fighter(Type.ELF, x, y, attackPower = elfAttack))
                    'G' -> out.add(Fighter(Type.GOBLIN, x, y))
                }
            }
        }

        return out
    }

    data class Point(val x: Int, val y: Int) : Comparable<Point> {
        fun adjacent() = listOf(
                Point(x, y-1),
                Point(x-1, y),
                Point(x+1, y),
                Point(x, y+1)
                )
        override fun compareTo(other: Point): Int =
                if (this.y == other.y) {
                    this.x - other.x
                } else {
                    this.y - other.y
                }
    }

    private fun findClosest(from: Point, area: List<List<Char>>, fighters: List<Fighter>) : Array<IntArray> {
        val out = Array(area.size) { IntArray(area.first().size) }

        area.forEachIndexed { y, list ->
            list.forEachIndexed { x, c ->
                if (c == '#')
                    out[y][x] = Int.MIN_VALUE
            }
        }

        fighters
                .filterNot { f -> f.x == from.x && f.y == from.y }
                .forEach { fighter ->
            out[fighter.y][fighter.x] = Int.MIN_VALUE
        }

        val queue = ArrayDeque<Point>()
        queue.add(Point(from.x, from.y))

        while(queue.isNotEmpty()) {
            val cur = queue.poll()
            cur.adjacent()
                    .filter { p -> p.y >= 0 && p.x >= 0 && p.y < area.size && p.x < area.first().size}
                    .filter { p -> out[p.y][p.x] != Int.MIN_VALUE }
                    .filter { p -> p != from }
                    .filter { p -> out[p.y][p.x] == 0 }
                    .forEach { p ->
                        queue.add(p)
                        out[p.y][p.x] = out[cur.y][cur.x] + 1
                    }
        }

        return out
    }

    @Suppress("unused")
    private fun List<List<Char>>.printPlayfield(fighters: List<Fighter>) {
        val builder = StringBuilder()
        this.forEachIndexed { y, list ->
            val fightersInLine = mutableListOf<Fighter>()
            list.forEachIndexed { x, c ->
                val occupied = fighters.singleOrNull { f -> f.x == x && f.y == y }
                if (occupied != null) {
                    fightersInLine.add(occupied)
                    when (occupied.type) {
                        Type.ELF -> builder.append('E')
                        Type.GOBLIN -> builder.append('G')
                    }
                } else {
                    builder.append(c)
                }
            }
            builder.append("  ")
            fightersInLine.forEach { f ->
                when (f.type) {
                    Type.ELF -> builder.append('E')
                    Type.GOBLIN -> builder.append('G')
                }
                builder.append('(')
                builder.append(f.health)
                builder.append("), ")
            }
            if (fightersInLine.isNotEmpty()) {
                repeat(2) { builder.deleteCharAt(builder.lastIndex) }
            }
            builder.append('\n')
        }
        println(builder)
    }

    private fun Fighter.doTurn(arena: List<List<Char>>, fighters: List<Fighter>) : Fighter? {
        val characters = fighters.sorted()
        val allReachable = findClosest(this.positionAsPoint(), arena, characters)

        var toAttack = this.positionAsPoint().adjacent().let { adjacentPoints ->
            characters
                    .filter { c -> c.type != this.type }
                    .filter { c -> adjacentPoints.any { p -> c.x == p.x && c.y == p.y } }
                    .minBy { c -> c.health }
        }
        if (toAttack == null) {
            characters
                    .filter { other -> other.type != this.type }
                    .filterNot { other -> other === this }
                    .flatMap { other -> other.positionAsPoint().adjacent() }
                    .filter { p -> arena[p.y][p.x] == '.' }
                    .filter { p -> allReachable[p.y][p.x] > 0 } //Reachable
                    .minBy { p -> allReachable[p.y][p.x] }?.let { closest ->
                        val mapToSuccess = findClosest(closest, arena, characters.filter { c -> c !== this })

                        this.positionAsPoint().adjacent()
                                .filter { p -> mapToSuccess[p.y][p.x] != Int.MIN_VALUE }
                                .minBy { p -> mapToSuccess[p.y][p.x]}!!
                                .let { newP ->
                                    this.y = newP.y
                                    this.x = newP.x
                                }
                    }
            toAttack = this.positionAsPoint().adjacent().let { adjacentPoints ->
                characters
                        .filter { c -> c.type != this.type }
                        .filter { c -> adjacentPoints.any { p -> c.x == p.x && c.y == p.y } }
                        .minBy { c -> c.health }
            }
        }

        toAttack?.let { victim ->
            victim.getHit(this.attackPower)
            if (victim.health < 0) {
                return victim
            }
        }
        return null
    }

    override fun part1(): String {
        var characters = characterList().sorted()
        val arena = inputSpaceArray()

        var counter = 0

        loop@while (characters.any { c -> c.type == Type.GOBLIN } && characters.any { c -> c.type == Type.ELF }) {
            val dead = mutableSetOf<Fighter>()
            for (character in characters) {
                if (characters.none { c -> c.type == Type.GOBLIN } || characters.none { c -> c.type == Type.ELF }) {
                    break@loop
                }
                if (character in dead)
                    continue

                character.doTurn(arena, characters)
                        ?.let { justDied ->
                            dead.add(justDied)
                            characters = characters.filter { c -> c !== justDied }
                        }
            }
            characters = characters.sorted()
            counter++
        }

        return "${counter * characters.sumBy { c -> c.health }}"
    }

    override fun part2(): String {
        var attack = 4
        outer@while (true) {
            var characters = characterList(attack++).sorted()
            val arena = inputSpaceArray()

            var counter = 0

            loop@while (characters.any { c -> c.type == Type.GOBLIN } && characters.any { c -> c.type == Type.ELF }) {
                val dead = mutableSetOf<Fighter>()
                for (character in characters) {
                    if (characters.none { c -> c.type == Type.GOBLIN } || characters.none { c -> c.type == Type.ELF }) {
                        break@loop
                    }
                    if (character in dead)
                        continue

                    if (
                            character.doTurn(arena, characters)
                                ?.let { justDied ->
                                    dead.add(justDied)
                                    characters = characters.filter { c -> c !== justDied }
                                    justDied.type == Type.ELF
                                } == true
                    ) { continue@outer }
                }
                characters = characters.sorted()
                counter++
            }

            return "${counter * characters.sumBy { c -> c.health }}"
        }

    }
}