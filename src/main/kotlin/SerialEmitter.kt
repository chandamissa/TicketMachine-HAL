// Envia tramas para os diferentes modulos Serial Receiver.

object SerialEmitter {

    enum class Peripheral { LCD, TICKET }

    private var SDX = 0x01
    private var SCLK = 0x02
    private var LCD_Sel = 0x04

    // Inicia a classe
    fun init() {
        HAL.setBits(LCD_Sel)
        HAL.clrBits(SDX or SCLK)
    }

    // Envia uma trama para o SerialReceiver
// identificado o periférico de destino em ’addr’,
// os bits de dados em ’data’
// e em ’size’ o número de bits a enviar.

    fun send(addr: Peripheral, data: Int) {
        HAL.clrBits(LCD_Sel)
        repeat(10) {
            val bit = (data shr it) and 1
            if (bit == 1) {
                HAL.setBits(SDX)
            } else {
                HAL.clrBits(SDX)
            }
            HAL.setBits(SCLK)
            HAL.clrBits(SCLK)
        }
        HAL.setBits(LCD_Sel)
    }

    // Retorna informação se o periférico esta ocupado
    fun isBusy(): Boolean {
        return false
    }

}

fun main() {
    // 1. Acordar as fundações (Podes precisar disto dependendo de como o professor fez o HAL)
    HAL.init()

    // 2. Colocar os pinos do emissor no estado de repouso
    SerialEmitter.init()

    println("SerialEmitter iniciado. Pinos em repouso.")

    // 3. Vamos preparar um pacote de teste.
    // O valor 0x55 (em binário: 00 0101 0101) é ótimo porque alterna os bits.
    val pacoteDeTeste = 0x055

    println("A enviar o pacote: 0x$pacoteDeTeste para o LCD...")

    // 4. Enviar!
    SerialEmitter.send(SerialEmitter.Peripheral.LCD, pacoteDeTeste)

    println("Transmissão concluída!")
}
