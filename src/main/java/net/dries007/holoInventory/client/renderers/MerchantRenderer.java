/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 - 2017 Dries K. Aka Dries007
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.dries007.holoInventory.client.renderers;

import net.dries007.holoInventory.client.ClientEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public class MerchantRenderer implements IRenderer {

    private final List<MerchantRecipe> recipes;

    public MerchantRenderer(final MerchantRecipeList input) {
        this.recipes = input;
    }

    @Override
    public void render(final WorldClient world, final RayTraceResult hit, final Vec3d pos) {
        final Minecraft mc = Minecraft.getMinecraft();
        final RenderManager rm = mc.getRenderManager();
        final RenderItem ri = mc.getRenderItem();

        GlStateManager.translate(pos.x - TileEntityRendererDispatcher.staticPlayerX, pos.y - TileEntityRendererDispatcher.staticPlayerY, pos.z - TileEntityRendererDispatcher.staticPlayerZ);

        GlStateManager.rotate(-rm.playerViewY, 0.0F, 0.5F, 0.0F);
        GlStateManager.rotate(rm.playerViewX, 0.5F, 0.0F, 0.0F);
        GlStateManager.translate(0, 0, -0.5);

        final double d = pos.distanceTo(new Vec3d(TileEntityRendererDispatcher.staticPlayerX, TileEntityRendererDispatcher.staticPlayerY, TileEntityRendererDispatcher.staticPlayerZ));

        if (d < 1.75) return;
        GlStateManager.scale(d * 0.2, d * 0.2, d * 0.2);

        for (int row = 0; row < this.recipes.size(); row++) {
            final MerchantRecipe recipe = this.recipes.get(row);

            GlStateManager.pushMatrix();
            GlStateManager.pushAttrib();

            RenderHelper.renderStack(ri, recipe.getItemToBuy(), 4, 0, this.recipes.size(), row);
            if (recipe.hasSecondItemToBuy())
                RenderHelper.renderStack(ri, recipe.getSecondItemToBuy(), 4, 1, this.recipes.size(), row);
            RenderHelper.renderStack(ri, recipe.getItemToSell(), 4, 3, this.recipes.size(), row);

            GlStateManager.popAttrib();
            GlStateManager.popMatrix();
        }
        // Draw stack sizes later, to draw over the items (disableDepth)
        GlStateManager.disableDepth();
        for (int row = 0; row < this.recipes.size(); row++) {
            final MerchantRecipe recipe = this.recipes.get(row);

            GlStateManager.pushMatrix();
            GlStateManager.pushAttrib();

            final int color = recipe.isRecipeDisabled() ? ClientEventHandler.TEXT_COLOR_LIGHT : ClientEventHandler.TEXT_COLOR;

            RenderHelper.renderName(mc.fontRenderer, recipe.getItemToBuy(), 4, 0, this.recipes.size(), row, color);
            if (recipe.hasSecondItemToBuy())
                RenderHelper.renderName(mc.fontRenderer, recipe.getSecondItemToBuy(), 4, 1, this.recipes.size(), row, color);
            RenderHelper.renderName(mc.fontRenderer, recipe.getItemToSell(), 4, 3, this.recipes.size(), row, color);

            GlStateManager.popAttrib();
            GlStateManager.popMatrix();
        }
        GlStateManager.enableDepth();
    }

    @Override
    public boolean shouldRender() {
        return !this.recipes.isEmpty();
    }

}
