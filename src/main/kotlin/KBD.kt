import isel.leic.utils.Time

const val NONE = 0.toChar()
const val K_DATA_MASK = 0X0F
const val K_VAL_MASK = 0X10
const val K_ACK_MASK = 0X01
const val K_ARRAY = "0123456789ABCD*#"

const val RX_D_MASK = 0x80   // Bit 7 das Entradas (I7)
const val RX_CLK_MASK = 0x80 // Bit 7 das Saídas (O7)
const val SERIAL = true


object KBD {
    fun init() {
        HAL.init()
    }
}

// Retorna de imediato a tecla premida ou NONE se não ha tecla premida.
private fun getKeyParallel(): Char {
    if (!HAL.isBit(K_VAL_MASK)) return NONE //Retorna se não forem escolhidos os 5 primeiros bits
    val keyIndex =
        HAL.readBits(K_DATA_MASK) //Criamos uma variável que le os bits da máscara do nosso K neste caso os 4 primeiros bits

    HAL.setBits(K_ACK_MASK) // Aciona o K_Ack

    while (HAL.isBit(K_VAL_MASK)) { // Esperar que se tire o dedo da tecla
        // Fica aqui preso num ciclo vazio a gastar tempo (busy waiting)
        // até o hardware descer o sinal do K_VAL para '0'
    }

    HAL.clrBits(K_ACK_MASK) // Limpa o sinal de confirmação para a próxima tecla

    return K_ARRAY[keyIndex] // Vai buscar a posição no array do teclado
}

private fun getKeySerial(): Char {

    if (HAL.isBit(RX_D_MASK)) return NONE

    HAL.setBits(RX_CLK_MASK) // ‘Software’ faz o Relógio Subir (1)
    val startBit = HAL.isBit(RX_D_MASK)
    HAL.clrBits(RX_CLK_MASK) // ‘Software’ faz o Relógio Descer (0)

    if (!startBit) return NONE

    // Ler os 4 bits
    var keyBits = 0
    for (i in 0 until 4) {
        HAL.setBits(RX_CLK_MASK)

        val rxData = HAL.isBit(RX_D_MASK)
        val bit = if (rxData) 1 else 0
        keyBits = keyBits or (bit shl i)

        HAL.clrBits(RX_CLK_MASK)
    }

    HAL.setBits(RX_CLK_MASK)
    val stopBit = HAL.isBit(RX_D_MASK)
    HAL.clrBits(RX_CLK_MASK)

    return if (!stopBit) K_ARRAY[keyBits] else NONE
}

fun getKeyFinal() = if (SERIAL) getKeySerial() else getKeyParallel()


// Retorna a tecla premida, caso ocorra antes do ’timeout’ (em milissegundos),
// ou NONE caso contrario.
fun waitKey(timeout: Long): Char {
    val initTime = Time.getTimeInMillis()
    var currentTime = initTime
    while (currentTime <= initTime + timeout) {
        val key = getKeyFinal()
        if (key != NONE) {
            return key
        }
        currentTime = Time.getTimeInMillis()
    }
    return NONE
}

//fun main() {
//    KBD.init()
//
//    println("--- TESTE DO DRIVER KBD ---")
//    println("Modo configurado: ${if (SERIAL) "SÉRIE" else "PARALELO"}")
//    println("Máscara RX_D: 0x${RX_D_MASK.toString(16)}")
//    println("Pressiona teclas no simulador para testar...")
//    println("(O programa espera 5 segundos por cada tecla)")
//
//    while (true) {
//        val key = waitKey(5000) // Timeout de 5 segundos
//
//        if (key != NONE) {
//            // Se leu tecla, mostra qual foi e o seu valor em hex
//            println("Tecla detetada: '$key' (Código: 0x${key.code.toString(16)})")
//            LCD.writeCMD(0x30)
//        } else {
//            // Se retorna NONE após 5 segundos, avisa o utilizador
//            println("Timeout: Nenhuma tecla premida nos últimos 5 segundos.")
//        }
//
//        // Pequeno delay para não "limpar" a consola demasiado rápido se houver erros
//        Time.sleep(100)
//    }



fun main() {
    HAL.init()
    KBD.init()

    while (true) {
        val key = getKeyFinal()

        if (key != NONE) {
            println("Tecla: '$key' (Código da tecla no array: 0x${key.code.toString(16)})")

            Thread.sleep(200)
        }

        Thread.sleep(10)
    }
}

