package io.github.priestoffern.vs_ship_assembler.rendering

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Camera

fun renderData(poseStack: PoseStack, camera: Camera) {

    for (data in Renderer.toRender) {
        if (data!=null) data.renderData(poseStack, camera)

    }
}