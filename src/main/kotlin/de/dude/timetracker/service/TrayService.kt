package de.dude.timetracker.service

import de.dude.library.action.ActionBus
import de.dude.library.javafx.action.LifeCycleAction
import de.dude.library.util.void
import de.dude.timetracker.action.TimerAction
import de.dude.timetracker.action.TrayAction
import java.awt.*
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import javax.swing.SwingUtilities

private const val FONT_PROPERTY_KEY = "awt.font.desktophints"
private const val IDLE_IMAGE_PATH = "/images/timer_icon.png"
private const val ICON_SIZE = 16

object TrayService : LifeCycleAction, TimerAction, Service {

    // TextAttribute.TRACKING, -0.120F maybe useful?
    private val timeFont = Font("Cornerstone", Font.PLAIN, 11)
    private val idleImage = ImageIO.read(javaClass.getResource(IDLE_IMAGE_PATH))
    private val trayIconLeft = createTrayIcon()
    private val trayIconRight = createTrayIcon()
    private val renderConfig = Toolkit.getDefaultToolkit().getDesktopProperty(FONT_PROPERTY_KEY) as Map<*, *>
    private val iconColor = Color(0, 200, 255)
    private var hasTwoIcons = false

    init {
        ActionBus.hookForever(this)
    }

    override fun start() = SystemTray.getSystemTray().apply {
        if (!trayIcons.contains(trayIconLeft)) add(trayIconLeft)
    }.void

    override fun stop() {
        SystemTray.getSystemTray().remove(trayIconLeft)
        SystemTray.getSystemTray().remove(trayIconRight)
    }

    private fun createTrayIcon() = TrayIcon(idleImage).apply {
        addMouseListener(object : MouseListener {
            override fun mouseEntered(e: MouseEvent?) = void
            override fun mouseExited(e: MouseEvent?) = void
            override fun mouseClicked(e: MouseEvent?) = void
            override fun mousePressed(e: MouseEvent?) {
                if (SwingUtilities.isRightMouseButton(e)) ActionBus.call<TrayAction> { onRightClick() }
            }
            override fun mouseReleased(e: MouseEvent?) {
                if (SwingUtilities.isLeftMouseButton(e)) ActionBus.call<TrayAction> { onClick() }
            }
        })
    }

    private fun showTime(minuteText: String, hourText: String?) {
        if (hourText == null) {
            SystemTray.getSystemTray().remove(trayIconRight)
            hasTwoIcons = false
            trayIconLeft.image = createTextImage(minuteText, true)
        } else {
            trayIconLeft.image = createTextImage(hourText)
            trayIconRight.image = createTextImage(minuteText, true)
            if (!hasTwoIcons) {
                SystemTray.getSystemTray().add(trayIconRight)
                hasTwoIcons = true
            }
        }
    }

    private fun createTextImage(text: String, alignLeft: Boolean = false) =
        BufferedImage(ICON_SIZE, ICON_SIZE, BufferedImage.TYPE_INT_ARGB).apply {
/*            createGraphics().apply {
                //setRenderingHints(renderConfig)
                color = iconColor
                val textWidth = fontMetrics.getStringBounds(text, this).width.toInt()
                val x: Int = if (alignLeft) {
                    drawLine(13, 2, 13, 2)
                    drawLine(13, 13, 13, 13)
                    drawLine(15, 2, 15, 13)
                    0
                } else {
                    drawLine(2, 2, 2, 2)
                    drawLine(2, 13, 2, 13)
                    drawLine(0, 2, 0, 13)
                    15 - textWidth
                }
                drawLine(2, 0, 13, 0)
                drawLine(2, 15, 13, 15)

                font = timeFont
                color = Color.WHITE
                drawString(text, x, 11)
                dispose()
            }*/
            createGraphics().apply {
                setRenderingHints(renderConfig)
                font = timeFont
                color = Color.WHITE
                val textWidth = fontMetrics.getStringBounds(text, this).width.toInt()
                val x = if (alignLeft) 0 else ICON_SIZE - textWidth
                val x2 = if (alignLeft) textWidth + 1 else ICON_SIZE
                drawString(text, x + 1, 11)
                color = iconColor
                drawLine(x, 14, x2, 14)
                drawLine(x, 15, x2, 15)
                dispose()
            }
        }

    private fun showIdle() {
        SystemTray.getSystemTray().remove(trayIconRight)
        hasTwoIcons = false
        trayIconLeft.image = idleImage
    }

    override fun onUpdate(hours: Int, minutes: Int) {
        var hourText: String? = null
        val minuteText = if (hours > 0) {
            hourText = hours.toString()
            String.format("%02d", minutes % 60)
        } else {
            String.format("%2s", minutes)
        }
        showTime(minuteText, hourText)
    }

    override fun onStop() {
        showIdle()
    }

    override fun onExit() = stop()

}