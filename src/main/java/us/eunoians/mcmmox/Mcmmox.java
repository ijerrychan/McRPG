package us.eunoians.mcmmox;

import com.cyr1en.mcutils.PluginUpdater;
import com.cyr1en.mcutils.logger.Logger;
import com.cyr1en.mcutils.utils.reflection.Initializable;
import com.cyr1en.mcutils.utils.reflection.annotation.Initialize;
import com.cyr1en.mcutils.utils.reflection.annotation.process.Initializer;
import com.google.common.base.Charsets;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import us.eunoians.mcmmox.api.displays.DisplayManager;
import us.eunoians.mcmmox.api.util.FileManager;
import us.eunoians.mcmmox.api.util.HiddenConfig;
import us.eunoians.mcmmox.api.util.RemoteTransferTracker;
import us.eunoians.mcmmox.commands.*;
import us.eunoians.mcmmox.events.mcmmo.*;
import us.eunoians.mcmmox.events.vanilla.*;
import us.eunoians.mcmmox.localization.LocalizationFiles;
import us.eunoians.mcmmox.players.PlayerManager;
import us.eunoians.mcmmox.util.blockmeta.chunkmeta.ChunkManager;
import us.eunoians.mcmmox.util.blockmeta.chunkmeta.ChunkManagerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.Collections;
import java.util.List;

public class Mcmmox extends JavaPlugin implements Initializable {

  private static Mcmmox instance;
  @Getter
  private PluginUpdater pluginUpdater;
  @Getter
  private LocalizationFiles localizationFiles;
  @Getter
  private FileManager fileManager;
  @Getter
  private DisplayManager displayManager;
  @Getter
  private static ChunkManager placeStore;
  @Getter
  private RemoteTransferTracker remoteTransferTracker;


  @Override
  public void onEnable() {
    Bukkit.getScheduler().runTaskLater(this, () -> Initializer.initAll(this), 1L);
  }

  @Override
  public void onDisable() {
    if (!Initializer.finished())
      Initializer.interrupt();
    PlayerManager.shutDownManager();
  }

  @Initialize(priority = 0)
  private void preInit() {
    Logger.init("McMMOX");
    /*var configManager = new ConfigManager(this);
    mConfigManager = new MConfigManager(configManager);
    /*if (!mConfigManager.setupConfigs(
            GeneralConfig.class, SwordsConfig.class))
      getServer().shutdown();*/
    //Logger.setDebugMode(mConfigManager.getGeneralConfig().isDebugMode());
    //Locale.init(mConfigManager);
  }

  // @Initialize(priority = 1)
  // Ignore sanity while in development
  private void sanity() {
    if (ProxySelector.getDefault() == null) {
      ProxySelector.setDefault(new ProxySelector() {
        private final List<Proxy> DIRECT_CONNECTION = Collections.unmodifiableList(Collections.singletonList(Proxy.NO_PROXY));

        public void connectFailed(URI arg0, SocketAddress arg1, IOException arg2) {
        }

        public List<Proxy> select(URI uri) {
          return DIRECT_CONNECTION;
        }
      });
    }
    pluginUpdater = new PluginUpdater(this, "https://contents.cyr1en.com/mcmmox/plinfo");
    pluginUpdater.setOut(true);
    if (fileManager.getFile(FileManager.Files.CONFIG).getBoolean("Configuration.AutoUpdate")) {
      if (pluginUpdater.needsUpdate())
        pluginUpdater.update();
      else
        Logger.info("No updates were found!");
    } else {
      Logger.info("New version of McMMOX is available: " + pluginUpdater.getVersion());
      Logger.info("Click to download new version: " + pluginUpdater.getDownloadURL());
    }
  }

  @Initialize(priority = 2)
  private void initPrimaryInstance() {
    //localizationFiles = new LocalizationFiles(this, true);
    instance = this;
    fileManager = FileManager.getInstance().setup(this);
    remoteTransferTracker = new RemoteTransferTracker();
	placeStore = ChunkManagerFactory.getChunkManager(); // Get our ChunkletManager
	File folder = new File(getDataFolder(), File.separator + "PlayerData");
    if(!folder.exists()){folder.mkdir();}
    displayManager = DisplayManager.getInstance();
	HiddenConfig.getInstance();
	PlayerManager.startSave(this);
  }

  @Initialize(priority = 3)
  private void initCmds() {
    getCommand("mcmmox").setExecutor(new McMMOStub());
    getCommand("mcdisplay").setExecutor(new McDisplay());
    getCommand("mcadmin").setExecutor(new McAdmin());
    getCommand("mclink").setExecutor(new McLink());
    getCommand("mcunlink").setExecutor(new McUnlink());
    getCommand("mchelp").setExecutor(new McHelp());
  }

  @Initialize(priority = 4)
  private void initListener() {
    getServer().getPluginManager().registerEvents(new PlayerLoginEvent(), this);
    getServer().getPluginManager().registerEvents(new MoveEvent(), this);
    getServer().getPluginManager().registerEvents(new PlayerLogoutEvent(), this);
    getServer().getPluginManager().registerEvents(new InvClickEvent(this), this);
    getServer().getPluginManager().registerEvents(new AbilityActivate(), this);
    getServer().getPluginManager().registerEvents(new McMMOPlayerLevelChange(), this);
    getServer().getPluginManager().registerEvents(new VanillaDamageEvent(), this);
    getServer().getPluginManager().registerEvents(new McMMOExpGain(), this);
    getServer().getPluginManager().registerEvents(new InvCloseEvent(), this);
    getServer().getPluginManager().registerEvents(new McMMOBleed(), this);
    getServer().getPluginManager().registerEvents(new CheckReadyEvent(), this);
    getServer().getPluginManager().registerEvents(new ShiftToggle(), this);
    getServer().getPluginManager().registerEvents(new BlockListener(this), this);
    getServer().getPluginManager().registerEvents(new WorldListener(this), this);
    getServer().getPluginManager().registerEvents(new McLink(), this);
    getServer().getPluginManager().registerEvents(new BreakEvent(), this);
    getServer().getPluginManager().registerEvents(new AbilityUpgrade(), this);
    getServer().getPluginManager().registerEvents(new LoadoutAdd(), this);
    getServer().getPluginManager().registerEvents(new InteractHandler(), this);
    getServer().getPluginManager().registerEvents(new DisarmHandler(), this);
  }

  public static Mcmmox getInstance() {
    if (instance == null)
      throw new NullPointerException("Plugin was not initialized.");
    return instance;
  }

  public String getPluginPrefix() {
    return getLangFile().getString("Messages.PluginInfo.PluginPrefix");
  }

  public FileManager getFileManager() {
    return fileManager;
  }

  @Override
  public FileConfiguration getConfig(){
    return fileManager.getFile(FileManager.Files.CONFIG);
  }

  public FileConfiguration getLangFile(){
    return FileManager.Files.fromString(getConfig().getString("Configuration.LangFile")).getFile();
  }

  public InputStreamReader getResourceAsReader(String fileName) {
	InputStream in = getResource(fileName);
	return in == null ? null : new InputStreamReader(in, Charsets.UTF_8);
  }
}
