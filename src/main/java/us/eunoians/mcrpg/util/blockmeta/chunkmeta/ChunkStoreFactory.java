package us.eunoians.mcrpg.util.blockmeta.chunkmeta;

import org.bukkit.World;

/**
 * This code is not mine. It is copyright from the original mcMMO allowed for use by their license. Modified 12/7/18
 * It was released under the GPLv3 license
 */

public class ChunkStoreFactory {
  protected static ChunkStore getChunkStore(World world, int x, int z){
	// TODO: Add in loading from config what type of store we want.
	return new PrimitiveChunkStore(world, x, z);
  }
}
