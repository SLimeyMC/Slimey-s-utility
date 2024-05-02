package io.github.slimeyar.slimeys_utility.items

import io.github.slimeyar.slimeys_utility.SlimeysUtilityMod.LOGGER
import io.github.slimeyar.slimeys_utility.SlimeysUtilityMod.RENDER_TYPE
import io.github.slimeyar.slimeys_utility.SlimeysUtilityTags
import io.github.slimeyar.slimeys_utility.client.renderer.Renderer
import io.github.slimeyar.slimeys_utility.client.renderer.RenderingData
import io.github.slimeyar.slimeys_utility.client.renderer.SelectionZoneRenderer
import io.github.slimeyar.slimeys_utility.physicify.physicifyBlocks
import net.minecraft.Util
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Vec3i
import net.minecraft.network.chat.TextComponent
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.CauldronBlock
import net.minecraft.world.level.block.SoundType
import org.valkyrienskies.core.util.datastructures.DenseBlockPosSet
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.util.toJOMLF
import java.awt.Color
import java.lang.Math.*


class ShipAssemblerItem(properties: Properties): RaycastableItem(properties) {

    private var selectionStart: BlockPos? = null
    private var selectionEnd: BlockPos? = null
    private var selectionZone: RenderingData? = null

    private var selected: BlockPos? = null

    override fun use(level: Level, player: Player, interactionHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val clipResult = level.clip(clipFromPlayer(level, player, ClipContext.Fluid.NONE))
            //level.clipIncludeShips(clipFromPlayer(level, player, ClipContext.Fluid.NONE))
        LOGGER.info("${player.name} hit ${clipResult.blockPos}")
        //player.sendMessage(TextComponent(" ${clipResult.blockPos}"), Util.NIL_UUID)
        player.playSound(SoundType.AMETHYST_CLUSTER.placeSound, 1F, 1F)

        makeSelection(level, player, clipResult.blockPos)

        return super.use(level, player, interactionHand)
    }

    private fun makeSelection(level: Level, player: Player, pos: BlockPos) {
        if (level.isClientSide) return

        if (player.isShiftKeyDown and (level.getBlockState(pos).isAir)) {
            selectionStart = null
            selectionEnd = null
            if (selectionZone != null) Renderer.removeRender(selectionZone!!)
            selectionZone = null
            player.sendMessage(TextComponent("Selection reset"), Util.NIL_UUID)
            return
        }
        ItemStack.EMPTY.tag

//        if (level.getShipObjectManagingPos(pos) != null) {
//            player.sendMessage(TextComponent("Selected position is on a ship!"), Util.NIL_UUID)
//            return
//        }

        if (selectionStart == null) {
            selectionStart = pos
            player.sendMessage(TextComponent("First pos selected"), Util.NIL_UUID)
            return
        }
        if (selectionEnd == null) {
            if (selectionZone!=null) Renderer.removeRender(selectionZone!!)
            selectionZone = null

            selectionEnd = pos
            player.sendMessage(TextComponent("Second pos selected"), Util.NIL_UUID)
            val zone = SelectionZoneRenderer(
                Vec3i(selectionStart!!.x, selectionStart!!.y, selectionStart!!.z).toJOMLF(),
                Vec3i(pos.x, pos.y, pos.z).toJOMLF(),
                Color.GREEN, 0, RENDER_TYPE
            )
            selectionZone = Renderer.addRender(zone)
            return
        }

        val blockPosSet = DenseBlockPosSet()
        for (x in min(selectionStart!!.x, selectionEnd!!.x)..max(selectionStart!!.x, selectionEnd!!.x)) {
            for (y in min(selectionStart!!.y, selectionEnd!!.y)..max(selectionStart!!.y, selectionEnd!!.y)) {
                for (z in min(selectionStart!!.z, selectionEnd!!.z)..max(selectionStart!!.z, selectionEnd!!.z)) {
                    if (level.getBlockState(BlockPos(x, y, z)).tags.noneMatch { it == SlimeysUtilityTags.FORBIDDEN_ASSEMBLE })
                    blockPosSet.add(x, y, z)
                }
            }
        }

        if (blockPosSet.size > 0) {
            physicifyBlocks(level as ServerLevel, blockPosSet, 1.0)
            player.sendMessage(TextComponent("Assembled!"), Util.NIL_UUID)
        } else {
            player.sendMessage(TextComponent("Failed to Assemble: Empty ship"), Util.NIL_UUID)
        }

        if (selectionZone != null) Renderer.removeRender(selectionZone!!)
        selectionZone = null
        selectionStart = null
        selectionEnd = null
    }

    override fun onUseTick(level: Level, player: LivingEntity, stack: ItemStack, remainingUseDuration: Int) {
        super.onUseTick(level, player, stack, remainingUseDuration)

        if (selectionStart != null && selectionEnd == null) {
            if (selectionZone != null) Renderer.removeRender(selectionZone!!)
            selectionZone = null

            val parentShip = level.getShipManagingPos(selectionStart!!)
            if (parentShip == null) {
                val blockHitResult = level.clip(clipFromPlayer(level, player as Player, ClipContext.Fluid.NONE))
                if (selected == blockHitResult.blockPos) return
                selected = blockHitResult.blockPos
                val SZ = SelectionZoneRenderer(
                    Vec3i(selectionStart!!.x, selectionStart!!.y, selectionStart!!.z).toJOMLF(),
                    Vec3i(selected!!.x, selected!!.y, selected!!.z).toJOMLF(),
                    Color.GREEN, 0, RENDER_TYPE
                )
                selectionZone = Renderer.addRender(SZ)
            }// else {
//                val blockHitResult = level.clipIncludeShips(clipFromPlayer(level, player as Player, ClipContext.Fluid.NONE),
//                    true, parentShip.id)
//                if (selected == blockHitResult.blockPos) return
//                selected = blockHitResult.blockPos
//                val SZ = SelectionZoneRenderer(
//                    Vector3f(selectionStart!!.x, selectionStart!!.y, selectionStart!!.z).toJOMLF(),
//                    Vec3i(selected!!.x, selected!!.y, selected!!.z).toJOMLF(),
//                    Color.GREEN
//                )
//                selectionZone = Renderer.addRender(SZ)
//                return
//            }
        }
    }
}