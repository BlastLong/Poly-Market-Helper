package com.blastlong.pmh.mixin;

import com.blastlong.pmh.PolyMarketHelperClient;
import com.blastlong.pmh.data.object.LocationObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Unique
    private static final ResourceLocation BEAM_LOCATION = new ResourceLocation("textures/entity/beacon_beam.png");

    @Shadow
    @Final
    private RenderBuffers renderBuffers;;

    @Unique
    private long pmh_1_20_1$count = 0;

    @Inject(method = "Lnet/minecraft/client/renderer/LevelRenderer;renderLevel(Lcom/mojang/blaze3d/vertex/PoseStack;FJZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;)V", at = {@At("TAIL")})
    private void renderLevel(PoseStack poseStack, float $$1, long $$2, boolean $$3, Camera camera, GameRenderer gameRenderer, LightTexture $$6, Matrix4f $$7, CallbackInfo ci) {
        PolyMarketHelperClient client = PolyMarketHelperClient.getInstance();

        if (client.getLocationIndicator().isIndicating()) {
            LocationObject locationObject = client.getLocationIndicator().getLocationObject();

            long l = pmh_1_20_1$count;
            int i = 0;
            int j = 1024;
            float[] fs = new float[] { 0.9764706f, 1.0f, 0.99607843f };

            poseStack.pushPose();
            poseStack.translate(
                    (int)locationObject.x - camera.getPosition().x(),
                    locationObject.y - camera.getPosition().y(),
                    (int)locationObject.z - camera.getPosition().z()
            );
            pmh_1_20_1$renderBeaconBeam(poseStack, renderBuffers.bufferSource(), BEAM_LOCATION,
                    $$1, 1.0f, l, i, j, fs, 0.2f, 0.25f);
            poseStack.popPose();

            pmh_1_20_1$count++;
        }
    }

    @Unique
    private static void pmh_1_20_1$renderBeaconBeam(
            PoseStack $$0, MultiBufferSource $$1, ResourceLocation $$2, float $$3, float $$4, long $$5, int $$6, int $$7, float[] $$8, float $$9, float $$10
    ) {
        int $$11 = $$6 + $$7;
        $$0.pushPose();
        $$0.translate(0.5, 0.0, 0.5);
        float $$12 = (float)Math.floorMod($$5, 40) + $$3;
        float $$13 = $$7 < 0 ? $$12 : -$$12;
        float $$14 = Mth.frac($$13 * 0.2F - (float)Mth.floor($$13 * 0.1F));
        float $$15 = $$8[0];
        float $$16 = $$8[1];
        float $$17 = $$8[2];
        $$0.pushPose();
        $$0.mulPose(Axis.YP.rotationDegrees($$12 * 2.25F - 45.0F));
        float $$18 = 0.0F;
        float $$21 = 0.0F;
        float $$22 = -$$9;
        float $$23 = 0.0F;
        float $$24 = 0.0F;
        float $$25 = -$$9;
        float $$26 = 0.0F;
        float $$27 = 1.0F;
        float $$28 = -1.0F + $$14;
        float $$29 = (float)$$7 * $$4 * (0.5F / $$9) + $$28;
        pmh_1_20_1$renderPart(
                $$0,
                $$1.getBuffer(RenderType.beaconBeam($$2, false)),
                $$15,
                $$16,
                $$17,
                1F,
                $$6,
                $$11,
                0.0F,
                $$9,
                $$9,
                0.0F,
                $$22,
                0.0F,
                0.0F,
                $$25,
                0.0F,
                1.0F,
                $$29,
                $$28
        );
        $$0.popPose();
        $$18 = -$$10;
        float $$31 = -$$10;
        $$21 = -$$10;
        $$22 = -$$10;
        $$26 = 0.0F;
        $$27 = 1.0F;
        $$28 = -1.0F + $$14;
        $$29 = (float)$$7 * $$4 + $$28;
        pmh_1_20_1$renderPart(
                $$0,
                $$1.getBuffer(RenderType.beaconBeam($$2, true)),
                $$15,
                $$16,
                $$17,
                0F,
                $$6,
                $$11,
                $$18,
                $$31,
                $$10,
                $$21,
                $$22,
                $$10,
                $$10,
                $$10,
                0.0F,
                1.0F,
                $$29,
                $$28
        );
        $$0.popPose();
    }

    @Unique
    private static void pmh_1_20_1$renderPart(
            PoseStack $$0,
            VertexConsumer $$1,
            float $$2,
            float $$3,
            float $$4,
            float $$5,
            int $$6,
            int $$7,
            float $$8,
            float $$9,
            float $$10,
            float $$11,
            float $$12,
            float $$13,
            float $$14,
            float $$15,
            float $$16,
            float $$17,
            float $$18,
            float $$19
    ) {
        PoseStack.Pose $$20 = $$0.last();
        Matrix4f $$21 = $$20.pose();
        Matrix3f $$22 = $$20.normal();
        pmh_1_20_1$renderQuad($$21, $$22, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9, $$10, $$11, $$16, $$17, $$18, $$19);
        pmh_1_20_1$renderQuad($$21, $$22, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$14, $$15, $$12, $$13, $$16, $$17, $$18, $$19);
        pmh_1_20_1$renderQuad($$21, $$22, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$10, $$11, $$14, $$15, $$16, $$17, $$18, $$19);
        pmh_1_20_1$renderQuad($$21, $$22, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$12, $$13, $$8, $$9, $$16, $$17, $$18, $$19);
    }

    @Unique
    private static void pmh_1_20_1$renderQuad(
            Matrix4f $$0,
            Matrix3f $$1,
            VertexConsumer $$2,
            float $$3,
            float $$4,
            float $$5,
            float $$6,
            int $$7,
            int $$8,
            float $$9,
            float $$10,
            float $$11,
            float $$12,
            float $$13,
            float $$14,
            float $$15,
            float $$16
    ) {
        pmh_1_20_1$addVertex($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$8, $$9, $$10, $$14, $$15);
        pmh_1_20_1$addVertex($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$9, $$10, $$14, $$16);
        pmh_1_20_1$addVertex($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$11, $$12, $$13, $$16);
        pmh_1_20_1$addVertex($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$8, $$11, $$12, $$13, $$15);
    }

    @Unique
    private static void pmh_1_20_1$addVertex(
            Matrix4f $$0, Matrix3f $$1, VertexConsumer $$2, float $$3, float $$4, float $$5, float $$6, int $$7, float $$8, float $$9, float $$10, float $$11
    ) {
        $$2.vertex($$0, $$8, (float)$$7, $$9)
                .color($$3, $$4, $$5, $$6)
                .uv($$10, $$11)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal($$1, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }
}
