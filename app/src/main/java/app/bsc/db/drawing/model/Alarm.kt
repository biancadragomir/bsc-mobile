package app.bsc.db.drawing.model

class Alarm(h: Int, m: Int, r:Int){
    val hour = h
    val minute = m
    val reqId = r

    override fun toString(): String {
        return "$hour:$minute"
    }
}