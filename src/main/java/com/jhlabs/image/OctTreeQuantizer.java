/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import java.io.PrintStream;
import java.util.Vector;

/**
 * An image Quantizer based on the Octree algorithm. This is a very basic implementation
 * at present and could be much improved by picking the nodes to reduce more carefully
 * (i.e. not completely at random) when I get the time.
 *
 * @author Jerry Huxtable
 * @author Gunnar Hillert
 */
public class OctTreeQuantizer implements Quantizer {

	/**
	 * The greatest depth the tree is allowed to reach.
	 */
	static final int MAX_LEVEL = 5;

	private int nodes = 0;
	private final OctTreeNode root;
	private int reduceColors;
	private int maximumColors;
	private int colors = 0;
	private final Vector[] colorList;

	public OctTreeQuantizer() {
		setup(256);
		this.colorList = new Vector[MAX_LEVEL + 1];
		for (int i = 0; i < MAX_LEVEL + 1; i++) {
			this.colorList[i] = new Vector();
		}
		this.root = new OctTreeNode();
	}

	/**
	 * Initialize the quantizer. This should be called before adding any pixels.
	 * @param numColors the number of colors we're quantizing to.
	 */
	@Override
	public void setup(int numColors) {
		this.maximumColors = numColors;
		this.reduceColors = Math.max(512, numColors * 2);
	}

	/**
	 * Add pixels to the quantizer.
	 * @param pixels the array of ARGB pixels
	 * @param offset the offset into the array
	 * @param count  the count of pixels
	 */
	@Override
	public void addPixels(int[] pixels, int offset, int count) {
		for (int i = 0; i < count; i++) {
			insertColor(pixels[i + offset]);
			if (this.colors > this.reduceColors) {
				reduceTree(this.reduceColors);
			}
		}
	}

	@Override
	public int getIndexForColor(int rgb) {
		int red = (rgb >> 16) & 0xff;
		int green = (rgb >> 8) & 0xff;
		int blue = rgb & 0xff;

		OctTreeNode node = this.root;

		for (int level = 0; level <= MAX_LEVEL; level++) {
			OctTreeNode child;
			int bit = 0x80 >> level;

			int index = 0;
			if ((red & bit) != 0) {
				index += 4;
			}
			if ((green & bit) != 0) {
				index += 2;
			}
			if ((blue & bit) != 0) {
				index += 1;
			}

			child = node.leaf[index];

			if (child == null) {
				return node.index;
			}
			else if (child.isLeaf) {
				return child.index;
			}
			else {
				node = child;
			}
		}
		System.out.println("getIndexForColor failed");
		return 0;
	}

	private void insertColor(int rgb) {
		int red = (rgb >> 16) & 0xff;
		int green = (rgb >> 8) & 0xff;
		int blue = rgb & 0xff;

		OctTreeNode node = this.root;

//		System.out.println("insertColor="+Integer.toHexString(rgb));
		for (int level = 0; level <= MAX_LEVEL; level++) {
			OctTreeNode child;
			int bit = 0x80 >> level;

			int index = 0;
			if ((red & bit) != 0) {
				index += 4;
			}
			if ((green & bit) != 0) {
				index += 2;
			}
			if ((blue & bit) != 0) {
				index += 1;
			}

			child = node.leaf[index];

			if (child == null) {
				node.children++;

				child = new OctTreeNode();
				child.parent = node;
				node.leaf[index] = child;
				node.isLeaf = false;
				this.nodes++;
				this.colorList[level].addElement(child);

				if (level == MAX_LEVEL) {
					child.isLeaf = true;
					child.count = 1;
					child.totalRed = red;
					child.totalGreen = green;
					child.totalBlue = blue;
					child.level = level;
					this.colors++;
					return;
				}

				node = child;
			}
			else if (child.isLeaf) {
				child.count++;
				child.totalRed += red;
				child.totalGreen += green;
				child.totalBlue += blue;
				return;
			}
			else {
				node = child;
			}
		}
		System.out.println("insertColor failed");
	}

	private void reduceTree(int numColors) {
		for (int level = MAX_LEVEL - 1; level >= 0; level--) {
			Vector v = this.colorList[level];
			if (v != null && v.size() > 0) {
				for (int j = 0; j < v.size(); j++) {
					OctTreeNode node = (OctTreeNode) v.elementAt(j);
					if (node.children > 0) {
						for (int i = 0; i < 8; i++) {
							OctTreeNode child = node.leaf[i];
							if (child != null) {
								if (!child.isLeaf) {
									System.out.println("not a leaf!");
								}
								node.count += child.count;
								node.totalRed += child.totalRed;
								node.totalGreen += child.totalGreen;
								node.totalBlue += child.totalBlue;
								node.leaf[i] = null;
								node.children--;
								this.colors--;
								this.nodes--;
								this.colorList[level + 1].removeElement(child);
							}
						}
						node.isLeaf = true;
						this.colors++;
						if (this.colors <= numColors) {
							return;
						}
					}
				}
			}
		}

		System.out.println("Unable to reduce the OctTree");
	}

	@Override
	public int[] buildColorTable() {
		int[] table = new int[this.colors];
		buildColorTable(this.root, table, 0);
		return table;
	}

	/**
	 * A quick way to use the quantizer. Just create a table the right size and pass in the pixels.
	 * @param inPixels the array of ARGB pixels
	 * @param table the pre-allocated color table to populate with quantized colors
	 */
	public void buildColorTable(int[] inPixels, int[] table) {
		int count = inPixels.length;
		this.maximumColors = table.length;
		for (int i = 0; i < count; i++) {
			insertColor(inPixels[i]);
			if (this.colors > this.reduceColors) {
				reduceTree(this.reduceColors);
			}
		}
		if (this.colors > this.maximumColors) {
			reduceTree(this.maximumColors);
		}
		buildColorTable(this.root, table, 0);
	}

	private int buildColorTable(OctTreeNode node, int[] table, int index) {
		if (this.colors > this.maximumColors) {
			reduceTree(this.maximumColors);
		}

		if (node.isLeaf) {
			int count = node.count;
			table[index] = 0xff000000 |
					((node.totalRed / count) << 16) |
					((node.totalGreen / count) << 8) |
					node.totalBlue / count;
			node.index = index++;
		}
		else {
			for (int i = 0; i < 8; i++) {
				if (node.leaf[i] != null) {
					node.index = index;
					index = buildColorTable(node.leaf[i], table, index);
				}
			}
		}
		return index;
	}

	/**
	 * An Octtree node.
	 */
	class OctTreeNode {
		int children;
		int level;
		OctTreeNode parent;
		OctTreeNode[] leaf = new OctTreeNode[8];
		boolean isLeaf;
		int count;
		int totalRed;
		int totalGreen;
		int totalBlue;
		int index;

		/**
		 * A debugging method which prints the tree out.
		 * @param s the stream to print to
		 * @param level the current level of recursion
		 */
		private void list(PrintStream s, int level) {
			for (int i = 0; i < level; i++) {
				System.out.print(' ');
			}
			if (this.count == 0) {
				System.out.println(this.index + ": count=" + this.count);
			}
			else {
				System.out.println(this.index + ": count=" + this.count + " red=" + (this.totalRed / this.count) + " green=" + (this.totalGreen / this.count) + " blue=" + (this.totalBlue / this.count));
			}
			for (int i = 0; i < 8; i++) {
				if (this.leaf[i] != null) {
					this.leaf[i].list(s, level + 2);
				}
			}
		}
	}
}

