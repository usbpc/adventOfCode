package advent2018

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import java.util.*
import kotlin.math.abs

class Day15(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 15
    private val input = adventOfCode.getInput(2018, day).lines()

    private fun inputSpaceArray() = input.map { row -> row.map { c -> if (c == 'E' || c == 'G') '.' else c}}

    enum class Type {
        ELF,
        GOBLIN
    }

    data class Fighter(val type: Type, var x: Int, var y: Int, var health : Int = 200) : Comparable<Fighter> {
        fun getHit() {
            health -= 3
        }

        override fun compareTo(other: Fighter): Int =
                if (this.y == other.y) {
                    this.x - other.x
                } else {
                    this.y - other.y
                }
        fun distanceTo(other: Fighter) : Int {
            return abs(x - other.x) + abs(y - other.y)
        }

        fun positionAsPoint() = Point(x, y)
    }

    private fun characterList() : List<Fighter> {
        val out = mutableListOf<Fighter>()

        input.forEachIndexed { y, row ->
            row.forEachIndexed { x, c ->
                when (c) {
                    'E' -> out.add(Fighter(Type.ELF, x, y))
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

    fun floodFill(from: Point, area: List<List<Char>>, fighters: List<Fighter>) : Array<IntArray> {
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

    private fun List<List<Char>>.printPlayfield(fighters: List<Fighter>) {
        val builder = StringBuilder()
        this.forEachIndexed { y, list ->
            val units = mutableListOf<Fighter>()
            list.forEachIndexed { x, c ->
                val occupied = fighters.singleOrNull { f -> f.x == x && f.y == y }
                if (occupied != null) {
                    units.add(occupied)
                    when (occupied.type) {
                        Type.ELF -> builder.append('E')
                        Type.GOBLIN -> builder.append('G')
                    }
                } else {
                    builder.append(c)
                }
            }
            builder.append("  ")
            units.forEach { f ->
                when (f.type) {
                    Type.ELF -> builder.append('E')
                    Type.GOBLIN -> builder.append('G')
                }
                builder.append('(')
                builder.append(f.health)
                builder.append("), ")
            }
            if (units.isNotEmpty()) {
                repeat(2) { builder.deleteCharAt(builder.lastIndex) }
            }
            builder.append('\n')
        }
        println(builder)
    }

    override fun part1(): String {
        var characters = characterList().sorted()
        val arena = inputSpaceArray()

        var counter = 0

        loop@while (characters.any { p -> p.type == Type.GOBLIN } && characters.any { p -> p.type == Type.ELF }) {
            //println("Round: $counter")
            //arena.printPlayfield(characters)
            val dead = mutableSetOf<Fighter>()
            for (character in characters) {
                if (characters.filter { p -> p !in dead }.none { p -> p.type == Type.GOBLIN } || characters.filter { p -> p !in dead }.none { p -> p.type == Type.ELF }) {
                    characters = characters.filter { c -> c !in dead }.sorted()
                    break@loop
                }
                if (character in dead)
                    continue

                val allReachable = floodFill(character.positionAsPoint(), arena, characters.filter { p -> p !in dead })

                if (false) {
                    allReachable.forEach { thing ->
                        thing.forEach { num ->
                            if (num != Int.MIN_VALUE) {
                                print(num.toString().padStart(3, ' '))
                            } else {
                                print("  X")
                            }
                        }
                        print('\n')
                    }
                    print('\n')
                }

                var toAttack = character.positionAsPoint().adjacent().let { adjacentPoints ->
                    characters
                            .filter { c -> c.type != character.type }
                            .filter { c -> c !in dead }
                            .filter { ch -> adjacentPoints.any { p -> ch.x == p.x && ch.y == p.y } }
                            .minBy { c -> c.health }
                }
                if (toAttack == null) {
                    characters
                            .filterNot { other -> other === character }
                            .filter { other -> other.type != character.type }
                            .filter { other -> other !in dead }
                            //.also { println(it) }
                            .flatMap { other -> other.positionAsPoint().adjacent() }
                            //.also { println(it) }
                            .filter { p -> arena[p.y][p.x] == '.' }
                            //.also { println(it) }
                            //.filter { p -> characters.filter { c -> c !in dead }.none { c -> c.y == p.y && c.x == p.x } } //After this all that are in range (as on website)
                            .filter { p -> allReachable[p.y][p.x] > 0 } //Reachable
                            //.also { println(it) }
                            .minBy { p -> allReachable[p.y][p.x] }?.let { closest ->
                                val mapToSuccess = floodFill(closest, arena, characters.filter { c -> c !== character }.filter { c -> c !in dead })
                                val builder = StringBuilder()
                                mapToSuccess.forEach { thing ->
                                    thing.forEach { num ->
                                        if (num != Int.MIN_VALUE) {
                                            builder.append(num.toString().padStart(3, ' '))
                                        } else {
                                            builder.append("  X")
                                        }
                                    }
                                    builder.append('\n')
                                }

                                character.positionAsPoint().adjacent()
                                        .filter { p -> mapToSuccess[p.y][p.x] != Int.MIN_VALUE }
                                        .minBy { p -> mapToSuccess[p.y][p.x]}!!
                                        .let { newP ->
                                            character.y = newP.y
                                            character.x = newP.x
                                        }
                            }
                    toAttack = character.positionAsPoint().adjacent().let { adjacentPoints ->
                        characters
                                .filter { c -> c.type != character.type }
                                .filter { c -> c !in dead }
                                .filter { ch -> adjacentPoints.any { p -> ch.x == p.x && ch.y == p.y } }
                                .minBy { c -> c.health }
                    }
                }

                toAttack?.let {toAttack ->
                    toAttack.getHit()
                    if (toAttack.health < 0) {
                        dead.add(toAttack)
                    }
                }
            }
            characters = characters.filter { c -> c !in dead }.sorted()
            counter++
        }

        println("Round: $counter")
        arena.printPlayfield(characters)

        return "${counter * characters.sumBy { c -> c.health }}"
    }

    override fun part2(): String {


        return ""
    }
}