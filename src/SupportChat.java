package de.Fabian996.SupportChat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SupportChat extends JavaPlugin implements Listener{
	
	public List<String> support = new ArrayList();
	public HashMap<String, String> suppchat = new HashMap();
	public static File csfiel = new File("plugins/SupportChat", "config.yml");
	public static FileConfiguration scfg = YamlConfiguration.loadConfiguration(csfiel);
	public static HashMap<UUID, SupportTimer> supptimer = new HashMap();
	
	public void onEnable(){
		System.out.println("The Plugin was activated!");
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		getCommand("support").setExecutor(this);
		if(!csfiel.exists())
			createDefaultConfig();
	}
	
	private void createDefaultConfig() {
	    scfg.options().header("//Variables: \n//pname = Playername \n//The variables prefix and message are\n//only viable for the chatformat\n//prefix = Prefix \n//message = Message");
	    scfg.options().copyHeader(true);
	    scfg.set("Prefix", "&7[&aSupportChat&7]&r");
	    scfg.set("Messages.playersentsupportrequest", "&aYou have sent successfully a support request!");
	    scfg.set("Messages.informadminsaboutsupporthelp", "&aThe player pname needs support!");
	    scfg.set("Messages.playersendsupportchatclose", "&aThe Support-Chat has closed!");
	    scfg.set("Messages.supportclosedchat", "&aYou have closed the Support-Chat!");
	    scfg.set("Messages.playeralreadyinsupportlist", "&aYou are already in the support-req list!");
	    scfg.set("Messages.playernotinsupportlist", "&aYou are not in the support-req list!");
	    scfg.set("Messages.adminstartssupportingplayer", "&aYou are now in Support-Chat with: pname");
	    scfg.set("Messages.tellplayerthatheisinsupportchat", "&aYour support request has been accepted. You are now in Support-Chat with pname");
	    scfg.set("Messages.nopermsforcmd", "&cYou dont have permissions for this command!");
	    scfg.set("Messages.playerdontneedsupport", "&cThe player pname dont need any support.");
	    scfg.set("Messages.playerkickedfromsupportchat", "&cYou have got kicked out of the Support-Chat.");
	    scfg.set("Messages.adminkickedplayerfromsupportchat", "&aYou have kicked pname out of the Support-Chat!");
	    scfg.set("Chatformat.ChatformatSupporter", "prefix&3 pname: message");
	    scfg.set("Chatformat.ChatformatPlayer", "prefix&3 pname: message");
	    scfg.set("Chatformat.ChatformatAdminviewer", "prefix&3 pname: message");
	    scfg.set("NoSpamSupportTimer..Enabled", Boolean.valueOf(false));
	    scfg.set("NoSpamSupportTimer..Delayinsecs", Integer.valueOf(15));
	    scfg.set("NoSpamSupportTimer..Message", "Please wait a few seconds to request support.");
	    try
	    {
	      scfg.save(csfiel);
	    }
	    catch (IOException e)
	    {
	      e.printStackTrace();
	    }
	}


	  public void onDisable() {
	    System.out.println("The plugin was deactivated!");
	  }
	  public String PP() {
	    String prefix = "";
	    if (scfg.getString("Prefix") != null) {
	      prefix = ChatColor.translateAlternateColorCodes('&', scfg.getString("Prefix"));
	    }
	    if (prefix.equals("")) {
	      System.out.println("Error! Could not find the String for the Prefix!");
	    }
	    return prefix;
	  }

	  public static void removeTimer(UUID uuid) {
		    if (supptimer.containsKey(uuid)) {
		      ((SupportTimer)supptimer.get(uuid)).end();
		      supptimer.remove(uuid);
		    }
		  }

		  public static void addTimer(UUID uuid) {
		    if (!supptimer.containsKey(uuid)) {
		      SupportTimer stimer = new SupportTimer(uuid, scfg.getInt("NoSpamSupportTimer..Delayinsecs") * 1L);
		      supptimer.put(uuid, stimer);

		      Bukkit.getScheduler().runTaskAsynchronously(Bukkit.getPluginManager().getPlugin("SupportChat"), stimer);
		    }
		  }

	  @EventHandler
	  public void onPlayerChat(AsyncPlayerChatEvent e) {
	    Player p = e.getPlayer();
	    if (this.suppchat.get(p.getName().toLowerCase()) != null) {
	      e.setCancelled(true);
	      Player t = Bukkit.getServer().getPlayer((String)this.suppchat.get(e.getPlayer().getName().toLowerCase()));
	      String suppmessage = scfg.getString("Chatformat.ChatformatSupporter");
	      suppmessage = ChatColor.translateAlternateColorCodes('&', suppmessage);
	      suppmessage = suppmessage.replaceAll("prefix", PP());
	      suppmessage = suppmessage.replaceAll("pname", p.getName());
	      suppmessage = suppmessage.replaceAll("message", e.getMessage());

	      p.sendMessage(suppmessage);

	      String playermessage = scfg.getString("Chatformat.ChatformatPlayer");
	      playermessage = ChatColor.translateAlternateColorCodes('&', playermessage);
	      playermessage = playermessage.replaceAll("prefix", PP());
	      playermessage = playermessage.replaceAll("pname", p.getName());
	      playermessage = playermessage.replaceAll("message", e.getMessage());

	      t.sendMessage(playermessage);

	      String adminmessage = scfg.getString("Chatformat.ChatformatAdminviewer");
	      adminmessage = ChatColor.translateAlternateColorCodes('&', adminmessage);
	      adminmessage = adminmessage.replaceAll("prefix", PP());
	      adminmessage = adminmessage.replaceAll("pname", p.getName());
	      adminmessage = adminmessage.replaceAll("message", e.getMessage());
	      for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
	        if ((!pl.hasPermission("SupportChat.Support")) || 
	          (pl == p) || (pl == t)) continue;
	        pl.sendMessage(adminmessage);
	      }
	    }
	  }
	  
	  @EventHandler
	  public void onLeave(PlayerQuitEvent e) {
	    Player p = e.getPlayer();
	    if (supptimer.containsKey(e.getPlayer().getUniqueId())) {
	      supptimer.remove(e.getPlayer().getUniqueId());
	    }
	    if (this.suppchat.get(p.getName().toLowerCase()) != null) {
	      this.suppchat.remove(p.getName().toLowerCase());
	    }
	    if (this.support.contains(p.getName().toLowerCase()))
	      this.support.remove(p.getName().toLowerCase());
	  }
	  

	  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	    if ((sender instanceof Player)) {
	      Player p = (Player)sender;
	      Player pl;
	      if (args.length == 0) {
	        if (!supptimer.containsKey(p.getUniqueId())) {
	          if (!this.support.contains(p.getName().toLowerCase())) {
	            this.support.add(p.getName().toLowerCase());
	            String s = ChatColor.translateAlternateColorCodes('&', scfg.getString("Messages.playersentsupportrequest"));
	            s = s.replaceAll("pname", p.getName());
	            p.sendMessage(PP() + " " + s);
	            for (Iterator localIterator = Bukkit.getServer().getOnlinePlayers().iterator(); localIterator.hasNext(); ) { pl = (Player)localIterator.next();
	              if (pl.hasPermission("SupportChat.support")) {
	                String n = ChatColor.translateAlternateColorCodes('&', scfg.getString("Messages.informadminsaboutsupporthelp"));
	                n = n.replaceAll("pname", p.getName());
	                pl.sendMessage(PP() + " " + n);
	              } }
	          }
	          else
	          {
	            String n = ChatColor.translateAlternateColorCodes('&', scfg.getString("Messages.playeralreadyinsupportlist"));
	            n = n.replaceAll("pname", p.getName());
	            p.sendMessage(PP() + " " + n);
	          }
	        } else {
	          String o = ChatColor.translateAlternateColorCodes('&', scfg.getString("NoSpamSupportTimer..Message"));
	          o = o.replaceAll("pname", p.getName());
	          p.sendMessage(PP() + " " + o);
	        }

	      }

	      if (args.length == 1)
	      {
	        if (p.hasPermission("SupportChat.support"))
	        {
	          if (args[0].equalsIgnoreCase("list")) {
	            p.sendMessage(ChatColor.AQUA + "[]=====" + ChatColor.GOLD + "Support List" + ChatColor.AQUA + "=====[]");
	            for (String s : this.support) {
	              p.sendMessage(ChatColor.DARK_AQUA + s);
	            }
	            p.sendMessage(ChatColor.AQUA + "[]====================[]");
	            return true;
	          }

	          if (args[0].equalsIgnoreCase("reload")) {
	            if (p.hasPermission("SupportChat.support"))
	            {
	              try {
	                scfg.load(csfiel);
	                p.sendMessage(PP() + " " + "§7You have successfully reloaded the config!");
	              }
	              catch (FileNotFoundException e)
	              {
	                e.printStackTrace();
	              }
	              catch (IOException e)
	              {
	                e.printStackTrace();
	              }
	              catch (InvalidConfigurationException e)
	              {
	                e.printStackTrace();
	              }
	            } else {
	              String o = ChatColor.translateAlternateColorCodes('&', scfg.getString("Messages.nopermsforcmd"));
	              o = o.replaceAll("pname", p.getName());
	              p.sendMessage(PP() + " " + o);
	            }

	          }
	          else if (args[0].equalsIgnoreCase("close")) {
	            if (this.suppchat.containsKey(p.getName().toLowerCase())) {
	              Player t = Bukkit.getServer().getPlayer((String)this.suppchat.get(p.getName().toLowerCase()));
	              if (this.support.contains(t.getName())) {
	                this.support.remove(t.getName());
	              }

	              if (this.support.contains(p.getName().toLowerCase())) {
	                this.support.remove(t.getName().toLowerCase());
	              }

	              this.suppchat.remove(t.getName().toLowerCase());
	              this.suppchat.remove(p.getName().toLowerCase());

	              if (scfg.getString("NoSpamSupportTimer..Enabled").equals("true")) {
	                addTimer(t.getUniqueId());
	              }

	              String s = ChatColor.translateAlternateColorCodes('&', scfg.getString("Messages.supportclosedchat"));
	              s = s.replaceAll("pname", p.getName());
	              p.sendMessage(PP() + " " + s);
	              String n = ChatColor.translateAlternateColorCodes('&', scfg.getString("Messages.playersendsupportchatclose"));
	              n = n.replaceAll("pname", p.getName());
	              t.sendMessage(PP() + " " + n);
	            }
	            else {
	              String o = ChatColor.translateAlternateColorCodes('&', scfg.getString("Messages.playernotinsupportlist"));
	              o = o.replaceAll("pname", p.getName());
	              p.sendMessage(PP() + " " + o);
	            }

	          }
	          else if (this.support.contains(args[0].toLowerCase())) {
	            this.suppchat.put(p.getName().toLowerCase(), args[0].toLowerCase());
	            this.suppchat.put(args[0].toLowerCase(), p.getName().toLowerCase());
	            Player t = Bukkit.getServer().getPlayer(args[0]);
	            String ii = ChatColor.translateAlternateColorCodes('&', scfg.getString("Messages.adminstartssupportingplayer"));
	            ii = ii.replaceAll("pname", p.getName());
	            p.sendMessage(PP() + " " + ii);
	            String i = ChatColor.translateAlternateColorCodes('&', scfg.getString("Messages.tellplayerthatheisinsupportchat"));
	            i = i.replaceAll("pname", p.getName());
	            t.sendMessage(PP() + " " + i);
	          }
	          else {
	            String o = ChatColor.translateAlternateColorCodes('&', scfg.getString("Messages.playerdontneedsupport"));
	            o = o.replaceAll("pname", args[0].toLowerCase());
	            p.sendMessage(PP() + " " + o);
	          }
	        }
	        else
	        {
	          String o = ChatColor.translateAlternateColorCodes('&', scfg.getString("Messages.nopermsforcmd"));
	          o = o.replaceAll("pname", p.getName());
	          p.sendMessage(PP() + " " + o);
	        }
	      }

	      if (args.length == 2) {
	        if (p.hasPermission("SupportChat.Support")) {
	          if (args[0].equalsIgnoreCase("kick")) {
	            Player t = Bukkit.getServer().getPlayer(args[1].toLowerCase());
	            if (this.support.contains(args[1].toLowerCase())) {
	              this.support.remove(args[1].toLowerCase());
	              this.suppchat.remove(p.getName().toLowerCase());
	              String o = ChatColor.translateAlternateColorCodes('&', scfg.getString("Messages.playerkickedfromsupportchat"));
	              o = o.replaceAll("pname", p.getName());
	              t.sendMessage(PP() + " " + o);

	              if (scfg.getString("NoSpamSupportTimer..Enabled").equals("true")) {
	                addTimer(t.getUniqueId());
	              }

	              String i = ChatColor.translateAlternateColorCodes('&', scfg.getString("Messages.adminkickedplayerfromsupportchat"));
	              i = i.replaceAll("pname", p.getName());
	              p.sendMessage(PP() + " " + i);
	            }
	            else
	            {
	              String o = ChatColor.translateAlternateColorCodes('&', scfg.getString("Messages.playerdontneedsupport"));
	              o = o.replaceAll("pname", args[1].toLowerCase());
	              p.sendMessage(PP() + " " + o);
	            }
	          }
	        }
	        else
	        {
	          String o = ChatColor.translateAlternateColorCodes('&', scfg.getString("Messages.nopermsforcmd"));
	          o = o.replaceAll("pname", p.getName());
	          p.sendMessage(PP() + " " + o);
	        }
	      }

	    }

	    return true;
	  }
	  
	  
	  
	  
	  
	  
}
