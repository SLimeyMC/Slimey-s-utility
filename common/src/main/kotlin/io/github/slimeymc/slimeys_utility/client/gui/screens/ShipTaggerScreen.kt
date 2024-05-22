package io.github.slimeymc.slimeys_utility.client.gui.screens

import dev.architectury.networking.NetworkManager
import io.github.slimeymc.slimeys_utility.SlimeysUtilityMod
import io.github.slimeymc.slimeys_utility.SlimeysUtilityMod.LOGGER
import io.github.slimeymc.slimeys_utility.SlimeysUtilityNetworkings.SHIP_TAGGING_CONFIRM_PACKET_ID
import io.netty.buffer.Unpooled
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TranslatableComponent
import net.minecraft.resources.ResourceLocation
import org.thinkingstudio.obsidianui.Position
import org.thinkingstudio.obsidianui.screen.SpruceScreen
import org.thinkingstudio.obsidianui.widget.SpruceButtonWidget
import org.thinkingstudio.obsidianui.widget.SpruceLabelWidget
import org.thinkingstudio.obsidianui.widget.text.SpruceTextFieldWidget
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.core.util.writeVec3d


class ShipTaggerScreen(title: Component?, val ship: Ship) : SpruceScreen(title) {
    // Once i get around adding texture to it
    private val TEXTURE = ResourceLocation(SlimeysUtilityMod.MOD_ID, "textures/gui/ship_tagger.png")

    private lateinit var name: SpruceTextFieldWidget

    private lateinit var position: Position

    override fun init() {
        super.init()
        LOGGER.info("entering screen")
        position = Position.center(this.width, this.height / 6)
        this.addRenderableWidget(
            SpruceLabelWidget(position,
                TranslatableComponent("slimeys_utility.ship_tagger.title"),
                400, true)
        )
        position.move(0, 20)
        name = this.addRenderableWidget(
            SpruceTextFieldWidget(position, 200, 20,
                TranslatableComponent("slimeys_utility.ship_tagger.text_field"))
        )
        position.relativeY = this.height / 5 * 3
        this.addRenderableWidget(
            SpruceButtonWidget(position, 150, 20,
                TranslatableComponent("slimeys_utility.ship_tagger.confirm")
            ) {
                val buf = FriendlyByteBuf(Unpooled.buffer())
                buf.writeVec3d(ship.transform.positionInWorld)
                buf.writeUtf(name.text)
                NetworkManager.sendToServer(SHIP_TAGGING_CONFIRM_PACKET_ID, buf)
            }
        )

    }
}