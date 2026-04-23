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

package net.dries007.holoInventory.network.response;

import com.google.common.base.Strings;
import io.netty.buffer.ByteBuf;
import net.dries007.holoInventory.client.renderers.MerchantRenderer;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MerchantRecipes extends ResponseMessage {

    private String name;
    private NBTTagCompound tag;

    @SuppressWarnings("unused")
    public MerchantRecipes() {

    }

    public MerchantRecipes(final int id, final IMerchant entity, final EntityPlayerMP player) {
        super(id);
        this.name = entity.getDisplayName().getFormattedText();
        MerchantRecipeList recipes = entity.getRecipes(player);
        if (recipes == null) recipes = new MerchantRecipeList();
        this.tag = recipes.getRecipiesAsTags();
    }

    @Override
    public void fromBytes(final ByteBuf buf) {
        super.fromBytes(buf);
        this.name = ByteBufUtils.readUTF8String(buf);
        this.tag = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(final ByteBuf buf) {
        super.toBytes(buf);
        ByteBufUtils.writeUTF8String(buf, Strings.nullToEmpty(this.name));
        ByteBufUtils.writeTag(buf, this.tag);
    }

    public static class Handler implements IMessageHandler<MerchantRecipes, IMessage> {
        @Override
        public IMessage onMessage(final MerchantRecipes message, final MessageContext ctx) {
            ResponseMessage.handle(message, new MerchantRenderer(new MerchantRecipeList(message.tag)));
            return null;
        }
    }
}
