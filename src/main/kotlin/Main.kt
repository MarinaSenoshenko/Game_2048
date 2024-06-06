import java.awt.GridLayout
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.image.BufferedImage
import java.util.*
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.JOptionPane.INFORMATION_MESSAGE
import kotlin.math.pow
import kotlin.random.Random

class MainFrame : JFrame() {
    private val image = mutableListOf<BufferedImage>()

    enum class Direction {
        UP, DOWN, LEFT, RIGHT
    }

    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(600, 600)
        isResizable = false
        iconImage = ImageIcon(Objects.requireNonNull(MainFrame::class.java.getResource("2048.png"))).image

        val panel = JPanel(GridLayout(4, 4, 5, 5))
        add(panel)

        val random = Random(2)
        var integerValue = 0

        for (i in 1..12) {
            val bufferedImage = ImageIO.read(MainFrame::class.java.getResource("$integerValue.png"))
            integerValue = 2.0.pow(i.toDouble()).toInt()
            image.add(bufferedImage)
        }

        val labels = Array(16) { JLabel() }

        for (i in 0..15) {
            labels[i].icon = ImageIcon(image[0])
            labels[i].text = 0.toString()
            panel.add(labels[i])
        }

        dropImages(labels, random)

        addKeyListener(object : KeyListener {
            override fun keyTyped(e: KeyEvent?) {}
            override fun keyPressed(e: KeyEvent?) {}

            override fun keyReleased(e: KeyEvent?) {
                if (e != null) {
                    when (e.keyCode) {
                        KeyEvent.VK_UP -> turnImages(labels, Direction.UP)
                        KeyEvent.VK_DOWN -> turnImages(labels, Direction.DOWN)
                        KeyEvent.VK_LEFT -> turnImages(labels, Direction.LEFT)
                        KeyEvent.VK_RIGHT -> turnImages(labels, Direction.RIGHT)
                    }
                }
            }
        })

        isVisible = true
    }

    private fun dropImages(labels: Array<JLabel>, random: Random) {
        labels.forEach { label ->
            label.icon = ImageIcon(image[0])
            label.text = 0.toString()
        }

        val positions = Array(2) { random.nextInt(16) }
        positions.forEach { position ->
            labels[position].icon = ImageIcon(image[1])
            labels[position].text = 1.toString()
        }
    }

    private fun changeImage(labels: Array<JLabel>, i: Int, offset: Int) {
        if (labels[i].text == labels[i + offset].text && labels[i].text != 0.toString()) {
            val cellNumber = labels[i + offset].text.toInt() + 1
            labels[i + offset].text = cellNumber.toString()
            labels[i + offset].icon = ImageIcon(image[cellNumber])
            labels[i].icon = ImageIcon(image[0])
            labels[i].text = 0.toString()
        }
        else if ((labels[i].text != 0.toString()) && (labels[i + offset].text == 0.toString()) ) {
            labels[i + offset].icon = labels[i].icon
            labels[i + offset].text = labels[i].text
            labels[i].icon = ImageIcon(image[0])
            labels[i].text = 0.toString()
        }
    }

    private fun changeAllImages(labels: Array<JLabel>, range: IntRange, offset: Int, filter: (Int) -> Boolean = { true }) {
        for (i in range) {
            if (filter(i)) {
                changeImage(labels, i, offset)
            }
        }
    }

    private fun checkGameOver(labels: Array<JLabel>) : Boolean {
        for (i in 0 .. 15) {
            if (labels[i].text == 0.toString()) {
                return true
            }
        }
        return false
    }

    private fun placeNewImage(labels: Array<JLabel>) {
        if (checkGameOver(labels)) {
            val random = Random(1)
            var position = random.nextInt(16)
            while (labels[position].text != 0.toString()) {
                position = random.nextInt(16)
            }

            labels[position].icon = ImageIcon(image[1])
            labels[position].text = 1.toString()
        }
        else {
            JOptionPane.showMessageDialog(null, "You lose!", "Game over", INFORMATION_MESSAGE)
        }
    }

    private fun turnImages(labels: Array<JLabel>, direction: Direction) {
        when (direction) {
            Direction.UP -> changeAllImages(labels, 4..15, -4)
            Direction.DOWN -> changeAllImages(labels, 0..11, 4)
            Direction.LEFT -> changeAllImages(labels, 1..15, -1) { it % 4 != 0 }
            Direction.RIGHT -> changeAllImages(labels, 0..14, 1) { (it + 1) % 4 != 0 }
        }
        placeNewImage(labels)
    }
}

fun main() {
    MainFrame()
}
