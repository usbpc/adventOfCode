package advent2018

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import java.lang.IllegalStateException

/*
Immune System:
790 units each with 3941 hit points with an attack that does 48 bludgeoning damage at initiative 5
624 units each with 2987 hit points with an attack that does 46 bludgeoning damage at initiative 16
5724 units each with 9633 hit points (immune to bludgeoning, slashing, fire) with an attack that does 16 slashing damage at initiative 9
1033 units each with 10664 hit points with an attack that does 89 slashing damage at initiative 1
6691 units each with 9773 hit points (weak to slashing) with an attack that does 13 bludgeoning damage at initiative 12
325 units each with 11916 hit points (weak to bludgeoning) with an attack that does 276 slashing damage at initiative 8
1517 units each with 6424 hit points with an attack that does 35 bludgeoning damage at initiative 13
1368 units each with 9039 hit points (immune to bludgeoning) with an attack that does 53 slashing damage at initiative 4
3712 units each with 5377 hit points (immune to cold, radiation; weak to fire) with an attack that does 14 slashing damage at initiative 14
3165 units each with 8703 hit points (weak to slashing, bludgeoning) with an attack that does 26 radiation damage at initiative 11

Infection:
1113 units each with 44169 hit points (immune to bludgeoning; weak to radiation) with an attack that does 57 fire damage at initiative 7
3949 units each with 20615 hit points (weak to radiation, cold) with an attack that does 9 bludgeoning damage at initiative 6
602 units each with 35167 hit points (immune to bludgeoning, cold; weak to fire) with an attack that does 93 radiation damage at initiative 20
1209 units each with 34572 hit points with an attack that does 55 bludgeoning damage at initiative 3
902 units each with 12983 hit points (immune to fire) with an attack that does 28 fire damage at initiative 19
1132 units each with 51353 hit points with an attack that does 66 radiation damage at initiative 15
7966 units each with 49894 hit points (immune to bludgeoning) with an attack that does 9 cold damage at initiative 10
3471 units each with 18326 hit points (weak to radiation) with an attack that does 8 fire damage at initiative 18
110 units each with 38473 hit points (weak to bludgeoning; immune to fire) with an attack that does 640 slashing damage at initiative 2
713 units each with 42679 hit points (weak to slashing) with an attack that does 102 bludgeoning damage at initiative 17

 */

class Day24(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 24

    private val lineRegex = """\d+ units each with \d+ hit points (?:\((.+)\) )?with an attack that does \d+ (.+) damage at initiative \d+""".toRegex()

    private val input = adventOfCode.getInput(2018, day)

    private fun processInput(data : String, boost: Int = 0) : List<Group> {
        val (immune, infection) = data.split("\n\n")
        return createGroups(immune, Side.IMMUNE_SYSTEM, boost) + createGroups(infection, Side.INFECTION)
    }

    /**
     *     private data class Group(
    var units: Int,
    val attackPower : Int,
    val hitpoints : Int,
    val initiative: Int,
    val attackType: AttackType,
    val immune: Set<AttackType>,
    val weakness: Set<AttackType>,
    val side: Side
     */

    private fun createGroups(info: String, side: Side, boost: Int = 0) : List<Group>{
        val out = mutableListOf<Group>()
        info.lines().drop(1).forEach { line ->
            val numbers = line.extractInts()

            var weakness = setOf<AttackType>()
            var immunetie = setOf<AttackType>()

            val attack = lineRegex.matchEntire(line)?.let { match ->
                //println(match.groups)
                if (match.groups[1] == null) {
                    //Only attack found
                    AttackType.fromString(match.groups[2]!!.value)
                } else {
                    val tmp = getWeakAndImmu(match.groups[1]!!.value)
                    weakness = tmp.first
                    immunetie = tmp.second

                    AttackType.fromString(match.groups[2]!!.value)
                }
            } ?: throw IllegalStateException("Regex couldn't match: $line")

            out.add(Group(numbers[0], numbers[2] + boost, numbers[1] , numbers[3], attack, immunetie, weakness, side))
        }
        return out
    }

    private fun getWeakAndImmu(string: String) : Pair<Set<AttackType>, Set<AttackType>>{
        val toProcess = string.split("; ")
        var weakness : Set<AttackType> = setOf()
        var immuneties : Set<AttackType> = setOf()
        for (item in toProcess) {
            if (item.startsWith("immune to ")) {
                immuneties = item.drop(10).split(", ").map { AttackType.fromString(it) }.toSet()
            } else {
                weakness = item.drop(8).split(", ").map { AttackType.fromString(it) }.toSet()
            }
        }
        return weakness to immuneties
    }
    enum class AttackType {
        BLUDGEONING, SLASHING, FIRE, COLD, RADIATION;
        companion object {
            fun fromString(string: String) : AttackType {
                return when (string[0]) {
                    'b' -> BLUDGEONING
                    's' -> SLASHING
                    'f' -> FIRE
                    'c' -> COLD
                    'r' -> RADIATION
                    else -> throw IllegalStateException("unknown attack type: $string")
                }
            }
        }
    }
    enum class Side {
        IMMUNE_SYSTEM,
        INFECTION
    }
    private data class Group(
            var units: Int,
            val attackPower : Int,
            val hitpoints : Int,
            val initiative: Int,
            val attackType: AttackType,
            val immune: Set<AttackType>,
            val weakness: Set<AttackType>,
            val side: Side
    ) : Comparable<Group> {
        override fun compareTo(other: Group): Int {
            return if (other.attackDamage == attackDamage) {
                other.initiative - initiative
            } else {
                other.attackDamage - attackDamage
            }
        }

        fun getDamageDone(attacker: Group) : Int {
            if (attacker.attackType !in immune) {
                return if (attacker.attackType in weakness) {
                    (attacker.attackDamage * 2)
                } else {
                    attacker.attackDamage
                }
            }
            return 0
        }

        fun unitsKilled(attacker: Group) : Int {
            if (attacker.attackType !in immune) {
                return if (attacker.attackType in weakness) {
                    (attacker.attackDamage * 2) / hitpoints
                } else {
                    attacker.attackDamage / hitpoints
                }
            }
            return 0
        }

        fun dealDamage(attacker: Group) {
            if (attacker.attackType !in immune) {
                units -= if (attacker.attackType in weakness) {
                    (attacker.attackDamage * 2) / hitpoints
                } else {
                    attacker.attackDamage / hitpoints
                }
            }
        }

        val attackDamage : Int
            get() = units * attackPower
    }

    override fun part1(): String {
        val fighters = processInput(input).toMutableList()

        while (fighters.groupBy { it.side }.count() > 1) {
            fighters.sort()
            val attackMap = mutableMapOf<Group, Group>()
            val attackedSet = mutableSetOf<Group>()

            for (fighter in fighters) {
                val damageDoneSorted = fighters
                        .filter { it.side != fighter.side }
                        .filter { f -> f !in attackedSet }
                        .sortedBy { f -> f.getDamageDone(fighter) }

                val sameDamage = damageDoneSorted.takeLastWhile { f -> f.getDamageDone(fighter) == damageDoneSorted.last().getDamageDone(fighter) }

                var tmp = sameDamage.sortedBy { f -> f.attackDamage }

                val sameDanger = tmp.takeLastWhile { f -> f.attackDamage == tmp.last().attackDamage }

                sameDanger.maxBy { f -> f.initiative }?.let { toAttack ->
                    if (toAttack.getDamageDone(fighter) > 0) {
                        attackMap[fighter] = toAttack
                        attackedSet.add(toAttack)
                    }
                }
            }

            attackMap.toSortedMap(Comparator { o1, o2 -> o2.initiative - o1.initiative})
                    .forEach { (attacker, attacked) ->
                        attacked.dealDamage(attacker)
                    }

            fighters.removeIf { f -> f.units <= 0 }
            //fighters.forEach { println(it) }
            //println("ROUND OVER!")
        }

        //fighters.forEach { println(it) }

        return "" + fighters.sumBy { it.units }
    }


    override fun part2(): String {
        var boost = 1
        loop@while (true) {
            val fighters = processInput(input, boost++).toMutableList()

            while (fighters.groupBy { it.side }.count() > 1) {
                fighters.sort()
                val attackMap = mutableMapOf<Group, Group>()
                val attackedSet = mutableSetOf<Group>()

                for (fighter in fighters) {
                    var tmp = fighters
                            .filter { it.side != fighter.side }
                            .filter { f -> f !in attackedSet }
                            .sortedBy { f -> f.getDamageDone(fighter) }

                    val sameDamage = tmp.takeLastWhile { f -> f.getDamageDone(fighter) == tmp.last().getDamageDone(fighter) }

                    tmp = sameDamage.sortedBy { f -> f.attackDamage }

                    val sameDanger = tmp.takeLastWhile { f -> f.attackDamage == tmp.last().attackDamage }

                    sameDanger.maxBy { f -> f.initiative }?.let { toAttack ->
                        if (toAttack.getDamageDone(fighter) > 0) {
                            attackMap[fighter] = toAttack
                            attackedSet.add(toAttack)
                        }
                    }
                }
                var unitsKilled = 0
                attackMap.toSortedMap(Comparator { o1, o2 -> o2.initiative - o1.initiative})
                        .forEach { (attacker, attacked) ->
                            unitsKilled += attacked.unitsKilled(attacker)
                            attacked.dealDamage(attacker)
                        }

                if (unitsKilled == 0) {
                    println("Going to next round cause no one was killed.")
                    continue@loop
                }

                fighters.removeIf { f -> f.units <= 0 }
                //fighters.forEach { println(it) }
                //println("ROUND OVER!")
            }
            println("One round done. $boost")
            if (fighters.first().side != Side.IMMUNE_SYSTEM)
                continue

            //fighters.forEach { println(it) }

            return "" + fighters.sumBy { it.units }
        }
    }
}