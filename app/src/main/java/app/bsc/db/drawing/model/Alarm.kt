package app.bsc.db.drawing.model

class Alarm(h: Int, m: Int, r:Int, d: Int){
    val hour = h
    val minute = m
    val reqId = r
    val daily = d

    override fun toString(): String {
        return "$hour:$minute"
    }
}