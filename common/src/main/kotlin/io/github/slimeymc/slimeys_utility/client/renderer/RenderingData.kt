package io.github.slimeymc.slimeys_utility.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Camera

interface RenderingData {
    var id: Long;
    var type: String;
    fun renderData(poseStack: PoseStack, camera: Camera)
}