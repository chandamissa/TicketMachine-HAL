// Escreve no LCD usando a interface a 8 bits.
object LCD {
    // Dimensao do display.
    const val LINES = 2
    const val COLS = 16

    // Escreve um byte de comando/dados no LCD em serie
    private fun writeByteSerial(rs: Boolean, data: Int) {
        val rs1 = if (rs) 1 else 0
        val pacote = (rs1) or (data shl 1) or (1 shl 9)
        val pacote2 = (rs1) or (data shl 1) or (0 shl 9)
        SerialEmitter.send(SerialEmitter.Peripheral.LCD, pacote)
        SerialEmitter.send(SerialEmitter.Peripheral.LCD, pacote2)
    }

    // Escreve um byte de comando/dados no LCD
    private fun writeByte(rs: Boolean, data: Int) {
        writeByteSerial(rs, data)
    }

    // Escreve um comando no LCD
    private fun writeCMD(data: Int) {
        writeByte(false, data)
    }

    // Escreve um dado no LCD
    private fun writeDATA(data: Int) {
        writeByte(true, data)
    }

    // Envia a sequencia de iniciacao para comunicacao a 8 bits.
    fun init() {
        Thread.sleep(15)
        writeCMD(0x30)
        Thread.sleep(5)
        writeCMD(0x30)
        Thread.sleep(1)
        writeCMD(0x30)
        writeCMD(0x38)
        writeCMD(0x08)
        clear()
        writeCMD(0x06)
        writeCMD(0x0F)
    }

    // Escreve um carater na posicao corrente.
    fun write(c: Char) {
        writeDATA(c.code)
    }

    // Escreve uma string na posicao corrente.
    fun write(text: String) {
        text.forEach { write(it) }
    }

    // Envia comando para posicionar cursor (’line ’:0..LINES-1 , ’column ’:0..COLS-1)
    fun cursor(line: Int = LINES, column: Int = COLS )   {
        when (line) {
            0 -> writeCMD(0x80 + column)
            else -> writeCMD(0xC0 + column)
        }
    }

    // Envia comando para limpar o ecra e posicionar o cursor em (0,0)
    fun clear() {
        writeCMD(0x01)
        Thread.sleep(2)
    }
}

fun main() {
    println("A iniciar o sistema...")

    // 1. Inicializar o Hardware Abstraction Layer (Abre a porta com o SimDig)
    HAL.init()
    println("HAL inicializado.")

    // 2. Inicializar o LCD (Executa a tua coreografia de arranque de 8 bits)
    println("A acordar o LCD...")
    LCD.init()
    println("LCD pronto e configurado!")

    // 3. Testar a escrita na primeira linha (Linha 0)
    LCD.cursor(0, 0) // Vai para o início da linha de cima
    LCD.write("ISEL - LEIC") // O teu curso em destaque!
    println("Primeira linha enviada.")

    // 4. Testar a mudança de linha (Linha 1)
    LCD.cursor(1, 0) // Salta para a linha de baixo
    LCD.write("A funcionar!!!")
    println("Segunda linha enviada.")

    // --- TESTE EXTRA: Limpeza de ecrã ---
    // Vamos esperar 3 segundos para conseguires ler a mensagem no simulador,
    // e depois testamos se o teu clear() está a funcionar bem.
    println("A aguardar 3 segundos antes de limpar...")
    Thread.sleep(3000)

    LCD.clear()
    LCD.cursor(0, 4) // Escreve um pouco mais para o meio
    LCD.write("Concluido!")
    println("Teste finalizado com sucesso!")
}