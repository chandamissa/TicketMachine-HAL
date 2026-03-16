import isel.leic.UsbPort

object HAL {

    // A nossa "memória"
    private var outState = 0

    fun init() {
        UsbPort.write(outState)
    }

    fun isBit(mask: Int) = (UsbPort.read() and mask) != 0

    fun readBits(mask: Int): Int {
        val num = UsbPort.read() and mask
        return num
    }

    fun writeBits(mask: Int, value: Int) {
        outState = (outState and mask.inv()) or value
        UsbPort.write(outState)
    }

    fun setBits(mask: Int) = writeBits(mask, mask)

    fun clrBits(mask: Int) = writeBits(mask, 0)
}

fun main(args: Array<String>) {
    HAL.init()

    println("A testar a ligação ao UsbPort...")
    while (true) {
        val value = UsbPort.read()
        UsbPort.write(value)
    }
}