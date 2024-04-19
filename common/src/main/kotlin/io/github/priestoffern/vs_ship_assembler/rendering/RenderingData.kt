package io.github.priestoffern.vs_ship_assembler.rendering

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Camera

interface RenderingData {
    var id: Long;
    var type: String;
    fun renderData(poseStack: PoseStack, camera: Camera)
}