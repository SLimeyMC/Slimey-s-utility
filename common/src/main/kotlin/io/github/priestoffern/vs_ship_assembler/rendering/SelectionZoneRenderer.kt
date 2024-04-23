package io.github.priestoffern.vs_ship_assembler.rendering

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.*
import com.mojang.math.Matrix4f
import io.github.priestoffern.vs_ship_assembler.util.iterateCorners
import io.github.priestoffern.vs_ship_assembler.util.toFloat
import net.minecraft.client.Camera
import net.minecraft.client.renderer.GameRenderer
import org.joml.Vector3f
import org.lwjgl.opengl.GL11
import org.valkyrienskies.mod.common.util.toJOML
import java.awt.Color
class SelectionZoneRenderer(override var id: Long, override var type: String) : RenderingData {
    var min = Vector3f()
    var max = Vector3f()
    private var borderColor: Color = Color.BLACK
    constructor(min: Vector3f, max: Vector3f, borderColor: Color, id: Long, type: String) : this(id, type) {
        this.min = min
        this.max = max
        this.borderColor = borderColor
    }

    override fun renderData(poseStack: PoseStack, camera: Camera) {
        val tesselator = Tesselator.getInstance()
        val builder = tesselator.builder

        // Enable depth testing for proper line rendering
        RenderSystem.enableDepthTest()
        RenderSystem.depthFunc(GL11.GL_LEQUAL)
        RenderSystem.depthMask(true)
        RenderSystem.setShader(GameRenderer::getPositionColorShader)

//        val light = LightTexture.pack(level!!.getBrightness(LightLayer.BLOCK, point1.toBlockPos()), level!!.getBrightness(LightLayer.SKY, point1.toBlockPos()))

        val light = Int.MAX_VALUE

        builder.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR_LIGHTMAP)

        poseStack.pushPose()

        val cameraPos = camera.position.toJOML().toFloat()

        val offsetMin = Vector3f(if (min.x >= max.x) 1.0f else 0.0f, if (min.y >= max.y) 1.0f else 0.0f, if (min.z >= max.z) 1.0f else 0.0f)
        val offsetMax = Vector3f(if (max.x > min.x) 1.0f else 0.0f, if (max.y > min.y) 1.0f else 0.0f, if (max.z > min.z) 1.0f else 0.0f)

        val transformedPointMin = min.sub(cameraPos).add(offsetMin)
        val transformedPointMax = max.sub(cameraPos).add(offsetMax)

        val matrix = poseStack.last().pose()

        drawBoundingBox(builder, matrix, borderColor.rgb, light, transformedPointMin, transformedPointMax)

        tesselator.end()

        poseStack.popPose()
    }
    fun drawBoundingBox(buf: VertexConsumer, matrix: Matrix4f,
                    colorARGB: Int, lightmapUV: Int,
                    min: Vector3f, max:Vector3f
    ) {
        val addVertex = { position: Vector3f ->
            buf.vertex(matrix, position.x, position.y, position.z).color(colorARGB).uv2(lightmapUV).endVertex()
        }
        addVertex(Vector3f(min.x, min.y, min.z))
        addVertex(Vector3f(min.x, min.y, max.z))

        addVertex(Vector3f(min.x, min.y, min.z))
        addVertex(Vector3f(min.x, max.y, min.z))

        addVertex(Vector3f(min.x, min.y, min.z))
        addVertex(Vector3f(max.x, min.y, min.z))

        addVertex(Vector3f(min.x, min.y, max.z))
        addVertex(Vector3f(min.x, max.y, max.z))

        addVertex(Vector3f(min.x, min.y, max.z))
        addVertex(Vector3f(max.x, min.y, max.z))

        addVertex(Vector3f(min.x, max.y, min.z))
        addVertex(Vector3f(max.x, max.y, min.z))

        addVertex(Vector3f(min.x, max.y, min.z))
        addVertex(Vector3f(min.x, max.y, max.z))

        addVertex(Vector3f(max.x, min.y, min.z))
        addVertex(Vector3f(max.x, max.y, min.z))

        addVertex(Vector3f(max.x, min.y, min.z))
        addVertex(Vector3f(max.x, min.y, max.z))

        addVertex(Vector3f(min.x, max.y, max.z))
        addVertex(Vector3f(max.x, max.y, max.z))

        addVertex(Vector3f(max.x, min.y, max.z))
        addVertex(Vector3f(max.x, max.y, max.z))

        addVertex(Vector3f(max.x, max.y, min.z))
        addVertex(Vector3f(max.x, max.y, max.z))
    }
}