package me.tryfle.stormy.hooks;

import java.util.ListIterator;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import net.weavemc.loader.api.Hook;

public class CPSHook extends Hook {
	@Override
	public void transform(@NotNull ClassNode classNode, @NotNull Hook.AssemblerConfig assemblerConfig) {
		int listCount = 0, methodCount = 0;
		boolean leftClick = false, rightClick = false;
		if (classNode.name.startsWith("com/moonsworth/lunar/client")) {
			for (FieldNode field : classNode.fields) {
				if (field.desc.equals("Lit/unimi/dsi/fastutil/longs/LongList;")) {
					listCount++;
				}
			}
			for (MethodNode method : classNode.methods) {
				if (listCount == 2) {
					for (ListIterator<AbstractInsnNode> listIterator = method.instructions.iterator(); listIterator.hasNext();) {
						AbstractInsnNode insn = listIterator.next();
						int opcode = insn.getOpcode();
						if (opcode == 154) {
							leftClick = true;
						}
						if (insn.getNext() != null) {
							int nextOpcode = insn.getNext().getOpcode();
							if (opcode == 4 && nextOpcode == 160) {
								rightClick = true;
							}
						}
					}
					if (leftClick && rightClick && method.desc.equals("()I") && methodCount < 2) {
						method.instructions.clear();
						method.instructions.add(new FieldInsnNode(178, "me/tryfle/stormy/utils/CPSHandler", "INSTANCE", "Lme/tryfle/stormy/utils/CPSHandler;"));
						method.instructions.add(new MethodInsnNode(182, "me/tryfle/stormy/utils/CPSHandler", (methodCount < 1) ? "getLeftCps" : "getRightCps", "()I"));
						method.instructions.add(new InsnNode(172));
						methodCount++;
					}
				}
			}
		}
	}
}
