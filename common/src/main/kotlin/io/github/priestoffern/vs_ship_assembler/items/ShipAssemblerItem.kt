package io.github.priestoffern.vs_ship_assembler.items

import de.m_marvin.univec.impl.Vec3d
import io.github.priestoffern.vs_ship_assembler.VsShipAssemblerTags
import io.github.priestoffern.vs_ship_assembler.physicify.physicifyBlocks
import io.github.priestoffern.vs_ship_assembler.rendering.Renderer
import io.github.priestoffern.vs_ship_assembler.rendering.RenderingData
import io.github.priestoffern.vs_ship_assembler.rendering.SelectionZoneRenderer
import net.minecraft.Util
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.TextComponent
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import org.joml.Vector3d
import org.valkyrienskies.core.util.datastructures.DenseBlockPosSet
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toMinecraft
import java.awt.Color
import java.lang.Math.*


class ShipAssemblerItem(properties: Properties): RaycastableItem(properties) {

    private var selectionStart: BlockPos? = null
    private var selectionEnd: BlockPos? = null
    private var selectionZone: RenderingData? = null

    override fun use(level: Level, player: Player, interactionHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val clipResult = level.clip(
            ClipContext(
                (Vector3d(player.eyePosition.toJOML()).toMinecraft()),
                (player.eyePosition.toJOML()
                    .add(0.5, 0.5, 0.5)
                    .add(Vector3d(player.lookAngle.toJOML()).mul(10.0)) //distance
                        ).toMinecraft(),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                null
            )
        )
        //player.sendMessage(TextComponent(" ${clipResult.blockPos}"), Util.NIL_UUID)
        player.playSound(SoundType.AMETHYST_CLUSTER.placeSound, 1F, 1F)

        val pos = clipResult.blockPos

        makeSelection(level, player, pos)

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

        if (level.getShipObjectManagingPos(pos) != null) {
            player.sendMessage(TextComponent("Selected position is on a ship!"), Util.NIL_UUID)
            return
        }

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
            val zone = SelectionZoneRenderer(Vec3d(selectionStart!!.x.toDouble(),
                selectionStart!!.y.toDouble(), selectionStart!!.z.toDouble()),Vec3d(pos.x.toDouble(),pos.y.toDouble(),pos.z.toDouble()), Color.GREEN)
            selectionZone = Renderer.addRender(zone)
            return
        }

        val blockPosSet = DenseBlockPosSet()
        for (x in min(selectionStart!!.x, selectionEnd!!.x)..max(selectionStart!!.x, selectionEnd!!.x)) {
            for (y in min(selectionStart!!.y, selectionEnd!!.y)..max(selectionStart!!.y, selectionEnd!!.y)) {
                for (z in min(selectionStart!!.z, selectionEnd!!.z)..max(selectionStart!!.z, selectionEnd!!.z)) {
                    if (level.getBlockState(BlockPos(x,y,z)).tags.noneMatch { it == VsShipAssemblerTags.FORBIDDEN_ASSEMBLE })
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

    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
        super.inventoryTick(stack, level, entity, slotId, isSelected)

        if (isSelected && selectionStart != null && selectionEnd == null) {
            if (selectionZone != null) Renderer.removeRender(selectionZone!!)
            selectionZone = null

            val res = raycast(level,entity as Player,ClipContext.Fluid.NONE)
            if (res != null) {
                val SZ = SelectionZoneRenderer(Vec3d(selectionStart!!.x.toDouble(),
                    selectionStart!!.y.toDouble(), selectionStart!!.z.toDouble()),Vec3d(res.blockPos.x.toDouble(),res.blockPos.y.toDouble(),res.blockPos.z.toDouble()), Color.GREEN)
                selectionZone = Renderer.addRender(SZ)
            }
        }
    }
}