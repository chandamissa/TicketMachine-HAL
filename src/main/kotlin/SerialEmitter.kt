// Envia tramas para os diferentes modulos Serial Receiver.

object SerialEmitter {

    enum class Peripheral { LCD, TICKET }

    private const val SDX_MASK = 0x01   // UsbPort.O0
    private const val SCLK_MASK = 0x02  // UsbPort.O1
    private const val LCD_SEL_MASK = 0x04 // UsbPort.O2
    private const val TD_SEL_MASK = 0x08  // UsbPort.O3

    // Inicia a classe
    fun init() {
        HAL.setBits(LCD_SEL_MASK)
        HAL.setBits(TD_SEL_MASK)
        HAL.clrBits(SDX_MASK or SCLK_MASK)
    }

    // Envia uma trama para o SerialReceiver
    // identificado o periférico de destino em ’addr’,
    // os bits de dados em ’data’
    // e em ’size’ o número de bits a enviar.

    fun send(addr: Peripheral, data: Int) {
        val selMask = if (addr == Peripheral.LCD) LCD_SEL_MASK else TD_SEL_MASK
        HAL.clrBits(selMask)

        repeat(10) { bitIdx ->
            val bit = (data shr bitIdx) and 1

            if (bit == 1) HAL.setBits(SDX_MASK)
            else HAL.clrBits(SDX_MASK)

            HAL.setBits(SCLK_MASK)
            HAL.clrBits(SCLK_MASK)
        }

        HAL.setBits(selMask)
    }

    // Retorna informação se o periférico esta ocupado
    fun isBusy(): Boolean = false

}

fun main() {
    // 1. Acordar as fundações
    HAL.init()

    // 2. Colocar os pinos do emissor no estado de repouso
    SerialEmitter.init()

    println("SerialEmitter iniciado. Pinos em repouso.")

    // 3. Preparar um pacote de teste.
    // O valor 0x55 (em binário: 00 0101 0101) é ótimo porque alterna os bits.
    val pacoteDeTeste = 0x055

    println("A enviar o pacote: 0x$pacoteDeTeste para o LCD...")

    // 4. Enviar!
    SerialEmitter.send(SerialEmitter.Peripheral.LCD, pacoteDeTeste)

    println("Transmissão concluída!")
}
