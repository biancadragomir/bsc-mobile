package app.bsc.db.drawing.model

class Alarm(h: Int, m: Int){
    val hour = h
    val minute = m

    override fun toString(): String {
        return "$hour:$minute"
    }
}