package com.rebelkeithy.dualhotbar;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.network.play.client.C16PacketClientStatus;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class InventoryChangeHandler 
{
	public static KeyBinding swapkey;
	public int mousePrev = -1;
	public int slot = -1;
	
	public int selectedItem;

	public static KeyBinding selectKey;
	public boolean swapKeyDown;
    public long[] keyTimes = new long[9];
    public int lastKey = -1;
    public int clickCount = 0;
    
    public boolean[] keyWasDown = new boolean[9];
    public boolean[] changeInv = new boolean[9];

    @SubscribeEvent
    public void postTickEvent(TickEvent.ClientTickEvent event)
    {    	 
		if(Minecraft.getMinecraft().thePlayer == null)
			return;
		
    	if(event.phase == TickEvent.Phase.START)
    	{
            for (int j = 0; j < 9; ++j)
            {

            	if(Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindsHotbar[j].getKeyCode()))
    			{
            		selectedItem = Minecraft.getMinecraft().thePlayer.inventory.currentItem;
    			}
            }
        	mousePrev = Mouse.getDWheel();
        	
    		if(Keyboard.isKeyDown(swapkey.getKeyCode()) && Math.abs(mousePrev - Mouse.getDWheel()) > 0)
    		{
    			if(swapKeyDown == false)
    			{
	    			swapKeyDown = true;
    				System.out.println(Mouse.getX() + " " + Mouse.getY());
	    			Minecraft mc = Minecraft.getMinecraft();
    				PlayerControllerMP controller = Minecraft.getMinecraft().playerController;
    				EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;

    				int window = player.inventoryContainer.windowId;
    				
    				controller.updateController();

    				System.out.println(mousePrev);
    				if(mousePrev < 0)
    				{
    					if(DualHotbarConfig.twoLayerRendering)
    					{
		    				for(int i = 9; i < 18; i++)
		    				{
		    					controller.windowClick(window, i, 0, 0, player);
		    					if(DualHotbarConfig.numHotbars > 1)
		    						controller.windowClick(window, i+27, 0, 0, player);
		    					if(DualHotbarConfig.numHotbars > 2)
		    						controller.windowClick(window, i+18, 0, 0, player);
		    					if(DualHotbarConfig.numHotbars > 3)
		    						controller.windowClick(window, i+9, 0, 0, player);
		    					controller.windowClick(window, i, 0, 0, player);
		    				}
    					}
    					else if(DualHotbarConfig.numHotbars == 4)
    					{
							for(int i = 9; i < 27; i++)
		    				{
		    					controller.windowClick(window, i, 0, 0, player);
		    					controller.windowClick(window, i+18, 0, 0, player);
		    					controller.windowClick(window, i, 0, 0, player);
		    				}
    					}
    				}
    				else
    				{
    					if(DualHotbarConfig.twoLayerRendering)
    					{
    						System.out.println("test");
		    				for(int i = 9; i < 18; i++)
		    				{
		    					controller.windowClick(window, i, 0, 0, player);
		    					if(DualHotbarConfig.numHotbars > 3)
		    						controller.windowClick(window, i+27, 0, 0, player);
		    					if(DualHotbarConfig.numHotbars > 2)
		    						controller.windowClick(window, i+18, 0, 0, player);
		    					if(DualHotbarConfig.numHotbars > 1)
		    						controller.windowClick(window, i+9, 0, 0, player);
		    					controller.windowClick(window, i, 0, 0, player);
		    				}
    					} 
    					if(DualHotbarConfig.numHotbars == 4)
    					{
							for(int i = 9; i < 27; i++)
		    				{
		    					controller.windowClick(window, i, 0, 0, player);
		    					controller.windowClick(window, i+18, 0, 0, player);
		    					controller.windowClick(window, i, 0, 0, player);
		    				}
    					}
    				}
    				/*
    				for(int i = 18; i < 27; i++)
    				{
    					controller.windowClick(window, i, 0, 0, player);
    					controller.windowClick(window, i+9, 0, 0, player);
    					controller.windowClick(window, i, 0, 0, player);
    				}*/
    				
    				slot = player.inventory.currentItem;
    			}
    		}
    		else
    		{
    			swapKeyDown = false;
    		}
    	}
    	
    	if(event.phase == TickEvent.Phase.END)
    	{   	
    		// If using ctrl-scroll to swap hotbars, put the players selected slot back to what it was before the scroll
    		if(slot != -1)
    		{
    			Minecraft.getMinecraft().thePlayer.inventory.currentItem = slot;
    			slot = -1;
    		}
    		
    		if(!DualHotbarConfig.enable || !DualHotbarMod.installedOnServer)
    		{
    			return;
    		}
    		
    		Minecraft mc = Minecraft.getMinecraft();
    		long time = System.currentTimeMillis();
            for (int j = 0; j < 9; ++j)
            {

            	if(Keyboard.isKeyDown(mc.gameSettings.keyBindsHotbar[j].getKeyCode()))
    			{
            		// If using the modifier + inv key combo, we can set the inventory slot without any more checking
            		if(Keyboard.isKeyDown(selectKey.getKeyCode()))
            		{
            			Minecraft.getMinecraft().thePlayer.inventory.currentItem = j + 9;
            			continue;
            		}
            		
            		// Only let this code run when the key is first press, not while it is being held
            		if(keyWasDown[j])
            		{
            			continue;
            		}
            		
            		for(int i = 0; i < DualHotbarConfig.numHotbars; i++)
            		{
            			if(selectedItem == j + i*9)
            			{
            				Minecraft.getMinecraft().thePlayer.inventory.currentItem = (j + 9*(i+1)) % (DualHotbarConfig.numHotbars*9);
            			}
            		}
            		
            		// If this key is the same as the last key pressed, and the time difference was less than 900ms, and double tapping is enabled
            		// then increment clickCount. Otherwise reset clickCount back to 0
            		if(lastKey == j && DualHotbarConfig.doubleTap && time - keyTimes[j] < 900)
            		{
            			clickCount++;
            			
            			//if(clickCount > 1)
            			//	clickCount = 0;
            		}
            		else
            		{
            			clickCount = 0;
            		}
            		
            		// If clickCount = 1 then there was a double click, since 0 was the first click
            		if(clickCount > 0)
            		{
            			//Minecraft.getMinecraft().thePlayer.inventory.currentItem = j + 9;
                		
    	            	//Minecraft.getMinecraft().thePlayer.inventory.currentItem = (j + 9 * (clickCount)) % (DualHotbarConfig.numHotbars*9);
                		
            		}
            		
            		lastKey = j;
        			keyTimes[j] = time;
            		keyWasDown[j] = true;
            		
            	}
            	else
            	{
            		keyWasDown[j] = false;
            	}
            }
    	}
    }
}
