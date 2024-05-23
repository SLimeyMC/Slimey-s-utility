package io.github.slimeymc.slimeys_utility.entity.renderer

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import io.github.slimeymc.slimeys_utility.SlimeysUtilityMod.ID
import io.github.slimeymc.slimeys_utility.entity.ShipSelectionEntity
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.resources.ResourceLocation
import org.lwjgl.opengl.GL11
import org.valkyrienskies.mod.common.util.toMinecraft

class ShipSelectionEntityRenderer(context: EntityRendererProvider.Context) : EntityRenderer<ShipSelectionEntity>(context) {
    private var animationTime = 0.0f

    override fun getTextureLocation(entity: ShipSelectionEntity): ResourceLocation {
        return ResourceLocation(ID, "textures/entity/ship_selection.png")
    }

    override fun render(
        entity: ShipSelectionEntity,
        entityYaw: Float,
        partialTick: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ) {
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight)
        val orientation = entity.orientation
        val aabb = entity.prevTickAABB
        // Do lerp stuff
        entity.prevTickAABB = entity.aabb

        animationTime = (animationTime + partialTick) % 0.5F

        poseStack.mulPose(orientation.toMinecraft())
        poseStack.pushPose()
        poseStack.scale(
            (aabb.maxX - aabb.minX).toFloat(),
            (aabb.maxY - aabb.minY).toFloat(),
            (aabb.maxZ - aabb.minZ).toFloat()
        )

        val tesselator = Tesselator.getInstance()
        val builder = tesselator.builder

        val lastPose  = poseStack.last().pose()

        RenderSystem.enableDepthTest()
        RenderSystem.depthFunc(GL11.GL_LEQUAL)
        RenderSystem.depthMask(true)
        RenderSystem.setShader(GameRenderer::getPositionColorShader)
        RenderSystem.lineWidth(2F)
        RenderSystem.setShaderColor(1F, 0F, 0F, 1F)

        builder.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_LIGHTMAP)
        val lightmapUV = Int.MAX_VALUE

        val drawLine = { startX: Float, startY: Float, startZ: Float, endX: Float, endY: Float, endZ: Float ->
            builder.vertex(lastPose, startX, startY, startZ).uv2(lightmapUV).endVertex()
            builder.vertex(lastPose, endX, endY, endZ).uv2(lightmapUV).endVertex()
        }

        drawLine(1F, 1F, 1F,
                0F, 1F, 1F)
        drawLine(1F, 1F, 1F,
                1F, 0F, 1F)
        drawLine(1F, 1F, 1F,
                1F, 1F, 0F)
        drawLine(0F, 1F, 1F,
                0F, 0F, 1F)
        drawLine(0F, 1F, 1F,
                0F, 1F, 0F)
        drawLine(1F, 0F, 1F,
                0F, 0F, 1F)
        drawLine(1F, 0F, 1F,
                1F, 0F, 0F)
        drawLine(0F, 1F, 1F,
                0F, 0F, 1F)
        drawLine(0F, 1F, 1F,
                0F, 1F, 0F)
        drawLine(0F, 0F, 1F,
                0F, 0F, 0F)
        drawLine(0F, 1F, 0F,
                0F, 0F, 0F)
        drawLine(1F, 0F, 0F,
                0F, 0F, 0F)

        RenderSystem.depthMask(false)
        RenderSystem.disableCull()

//        TODO: Once i am ready to render quad
//        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK)
//
//        val drawQuad = { startX: Float, startY: Float, endX: Float, endY: Float, z: Float ->
//            builder.vertex(lastPose, startX, startY, z).uv(0.5F + animationTime, 0.5F + animationTime).endVertex()
//            builder.vertex(lastPose, endX, startY, z).uv(0.5F + animationTime, animationTime).endVertex()
//            builder.vertex(lastPose, endX, endY, z).uv(animationTime, animationTime).endVertex()
//            builder.vertex(lastPose, startX, endY, z).uv(animationTime, 0.5F + animationTime).endVertex()
//        }
    }

    override fun shouldShowName(entity: ShipSelectionEntity): Boolean {
        return false
    }
}