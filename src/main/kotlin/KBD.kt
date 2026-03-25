import isel.leic.UsbPort
import isel.leic.utils.Time

const val NONE = 0.toChar()
const val K_DATA_MASK = 0X0F
const val K_VAL_MASK = 0X10
const val K_ACK_MASK = 0X01
const val K_ARRAY = "0123456789ABCD*#"


object KBD {
    fun init() {
        HAL.init()
    }
}

// Retorna de imediato a tecla premida ou NONE se não ha tecla premida.
fun getKey(): Char {
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

// Retorna a tecla premida, caso ocorra antes do ’timeout’ (em milissegundos),
// ou NONE caso contrario.
fun waitKey(timeout: Long): Char {
    val initTime = Time.getTimeInMillis()
    var currentTime = initTime
    while (currentTime <= initTime + timeout) {
        val key = getKey()
        if (key != NONE) {
            return key
        }
        currentTime = Time.getTimeInMillis()
    }
    return NONE
}



