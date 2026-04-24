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
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class InventoryRenderer implements IRenderer {
    private final List<ItemStack> stacks;

    public InventoryRenderer(final List<ItemStack> input) {
        this.stacks = new ArrayList<>(input.size());
        for (final ItemStack stack : input)
            outer:{
                if (stack == null) continue;
                for (final ItemStack stack2 : this.stacks) {
                    if (!ItemStack.areItemStackTagsEqual(stack, stack2) || !ItemStack.areItemsEqual(stack, stack2))
                        continue;
                    stack2.grow(stack.getCount());
                    break outer;
                }
                this.stacks.add(stack);
            }
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

        if (d < 1.5d) return;
        GlStateManager.scale(d * 0.2, d * 0.2, d * 0.2);

        final int cols;
        if (this.stacks.size() <= 9) cols = this.stacks.size();
        else if (this.stacks.size() <= 27) cols = 9;
        else if (this.stacks.size() <= 54) cols = 11;
        else if (this.stacks.size() <= 90) cols = 14;
        else if (this.stacks.size() <= 109) cols = 18;
        else cols = 21;
        final int rows = 1 + ((this.stacks.size() % cols == 0) ? (this.stacks.size() / cols) - 1 : this.stacks.size() / cols);

        if (rows > 4) GlStateManager.scale(0.8, 0.8, 0.8);

        int r = 0;
        int c = 0;
        for (final ItemStack stack : this.stacks) {
            RenderHelper.renderStack(ri, stack, cols, c, rows, r);
            if (++c == cols) {
                r++;
                c = 0;
            }
        }

        // Draw stack sizes later, to draw over the items (disableDepth)
        r = 0;
        c = 0;
        GlStateManager.disableDepth();
        for (final ItemStack stack : this.stacks) {
            RenderHelper.renderName(mc.fontRenderer, stack, cols, c, rows, r, ClientEventHandler.TEXT_COLOR);
            if (++c == cols) {
                r++;
                c = 0;
            }
        }
        GlStateManager.enableDepth();
    }

    @Override
    public boolean shouldRender() {
        return !this.stacks.isEmpty();
    }
}
