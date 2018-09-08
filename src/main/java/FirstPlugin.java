/*
@author G.M
 */

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerDropItemEvent;
import cn.nukkit.plugin.PluginBase;

import java.io.*;
import java.util.HashSet;

public class FirstPlugin extends PluginBase {
    private HashSet<String> notAllowed;
    static final String PATH="plugins/DroppingControl/worlds.txt";
    static final String PATH2="plugins/DroppingControl/message.txt";
    private String msgType;
    private String msg;

    @Override
    public void onLoad() {
        getLogger().info("Loading DroppingControl by G.M.");
        getLogger().info("Created for BITE-YT.");
        notAllowed=new HashSet<String>();
        File f1=new File(PATH);
        File f2=new File(PATH2);
        try {
            if (!f1.getParentFile().exists()) {
                f1.getParentFile().mkdirs();
            }
            if(!f2.getParentFile().exists()){
                f2.getParentFile().mkdirs();
            }
            if(!f2.exists()){
                f2.createNewFile();

                PrintWriter pw=new PrintWriter(new BufferedWriter(new FileWriter(f2)));
                pw.println("message type: message");
                pw.println("message: You are not allowed to drop items in this world.");
                pw.close();
            }
            if (!f1.exists()) {
                getLogger().info("Creating config file.");
                f1.createNewFile();
            }
        }
        catch (Exception e){
            Server.getInstance().getLogger().logException(e);
        }
    }

    public void onEnable() {
        getLogger().info("Loading configs.");
        getServer().getPluginManager().registerEvents(new MyEventListener(this), this);

        try{
            File f=new File(PATH);
            BufferedReader br=new BufferedReader(new FileReader(f));
            String world="";

            while(world!=null){
                world=br.readLine();
                notAllowed.add(world);
            }

            f=new File(PATH2);
            br=new BufferedReader(new FileReader(f));
            msgType = br.readLine().split(": ")[1].toLowerCase();
            msg = br.readLine().split(": ")[1];

            br.close();
        }catch (Exception e){
            Server.getInstance().getLogger().logException(e);
        }

        getLogger().info("Successfully enabled!");

    }

    String getMsgType(){
        return msgType;
    }

    String getMsg(){
        return msg;
    }

    HashSet<String> getBannedWorlds(){
        return notAllowed;
    }
}

class MyEventListener implements Listener {

    private final FirstPlugin plugin;

    public MyEventListener(FirstPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerItemDrop(PlayerDropItemEvent e){
        Player p=e.getPlayer();
        String msgType=plugin.getMsgType();
        String msg=plugin.getMsg();
        if(!p.isOp()&&plugin.getBannedWorlds().contains(p.getLevel().getName())){
            e.setCancelled();
            switch (msgType){
                case "popup":
                    p.sendPopup(msg);
                    break;
                case "actionbar":
                    p.sendActionBar(msg);
                    break;
                case "title":
                    p.sendTitle(msg);
                    break;
                case "tip":
                    p.sendTip(msg);
                    break;
                default:
                    p.sendMessage(msg);

            }
        }
    }
}




