package app.bsc.db.drawing.model

data class Alarm(val hour: Int, val minute: Int, val reqId: Int, val daily: Int) {
    override fun toString(): String {
        return "$hour:$minute"
    }
}