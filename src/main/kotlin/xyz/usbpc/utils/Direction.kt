package xyz.usbpc.utils

enum class Direction {
    UP {
        override fun left(): Direction = LEFT
        override fun right(): Direction = RIGHT
        override fun back(): Direction = DOWN
    },
    DOWN {
        override fun left(): Direction = RIGHT
        override fun right(): Direction = LEFT
        override fun back(): Direction = UP
    },
    LEFT {
        override fun left(): Direction = DOWN
        override fun right(): Direction = UP
        override fun back(): Direction = RIGHT
    },
    RIGHT {
        override fun left(): Direction = UP
        override fun right(): Direction = DOWN
        override fun back(): Direction = LEFT
    };

    abstract fun left(): Direction
    abstract fun right(): Direction
    abstract fun back(): Direction
}