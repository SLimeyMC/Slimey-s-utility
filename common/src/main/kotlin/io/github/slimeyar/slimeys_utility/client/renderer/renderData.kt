package io.github.slimeyar.slimeys_utility.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Camera

fun renderData(poseStack: PoseStack, camera: Camera) {
    for (data in Renderer.toRender) {
        data.renderData(poseStack, camera)
    }
}