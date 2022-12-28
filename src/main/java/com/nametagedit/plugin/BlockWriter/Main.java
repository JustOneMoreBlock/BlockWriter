package com.nametagedit.plugin.BlockWriter.Main;
 
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
 
 
 public class Main
   extends JavaPlugin {
   private Map<String, bw_Text> bw_data = new HashMap<String, bw_Text>();
   private Map<Character, String> chars = new HashMap<Character, String>();
   private Document doc = null;
   protected FileConfiguration config = null;
    
   public void onDisable() {
     this.bw_data = null;
     this.chars = null;
     this.doc = null;
     this.config = null;
   }
 
 
   
   public void onEnable() {
     if (!setup()) {
       
       log("[Block Writer] No chars file found - stopping!");
       setEnabled(false);
     }
     else {
       
       PluginDescriptionFile pdf = getDescription();
       log("[Block Writer] Version " + pdf.getVersion() + " Enabled!");
     } 
   }
 
 
   
   private boolean setup() {
     try {
       String loc = String.valueOf(getDataFolder().getPath()) + File.separator + "chars.txt";
       DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
       this.doc = factory.newDocumentBuilder().parse(new File(loc));
     }
     catch (Exception ne) {
       
       ne.printStackTrace();
       return false;
     } 
     NodeList list = this.doc.getElementsByTagName("char");
     for (int i = 0; i < list.getLength(); i++) {
       
       Element el = (Element)list.item(i);
       this.chars.put(Character.valueOf(el.getAttribute("value").charAt(0)), el.getAttribute("blocks"));
     } 
     if (this.config == null) { this.config = getConfig(); }
     else
     
     { File f = new File(getDataFolder() + File.separator + "config.yml");
       if (f.exists())
         
         try {
           this.config.load(f);
         }
         catch (Exception ne) {
           
           ne.printStackTrace();
         }   }
     
     return true;
   } private void write_chars(String[] args, Player p) {
     byte b1;
     int i;
     String[] arrayOfString1;
     for (i = (arrayOfString1 = args).length, b1 = 0; b1 < i; ) { String s = arrayOfString1[b1]; byte b; int k; String[] arrayOfString;
       for (k = (arrayOfString = this.config.getString("banned_words", "").split(" ")).length, b = 0; b < k; ) { String s2 = arrayOfString[b];
         if (s.equalsIgnoreCase(s2.trim())) {
           
           p.sendMessage(ChatColor.RED + "[Block Writer] Disallowed word!");
           log("[Block Writer] " + p.getName() + " tried to write a banned word"); return;
         }  b++; }
        b1++; }
     
     bw_Text bwt = this.bw_data.get(p.getName());
     if (bwt == null) bwt = new bw_Text();
     
     Block block = p.getTargetBlock(null, this.config.getInt("reach", 25)).getRelative(BlockFace.UP);
     
     bwt.start_block = block;
     
     double yaw = (p.getLocation().getYaw() + 22.5D) % 360.0D;
     if (yaw < 0.0D) yaw += 360.0D; 
     BlockFace bf = null;
     if (yaw > 65.0D && yaw <= 155.0D) { bf = BlockFace.EAST; }
     else if (yaw > 155.0D && yaw <= 245.0D) { bf = BlockFace.SOUTH; }
     else if (yaw > 245.0D && yaw <= 335.0D) { bf = BlockFace.WEST; }
     else if (yaw > 335.0D || yaw <= 65.0D) { bf = BlockFace.NORTH; }
     
     bwt.bf = bf;
     this.bw_data.put(p.getName(), bwt);
     
     backup_blocks(count_blocks(args), p.getName());
     
     int count = 0;
     boolean custom = false;
     
     if (bwt.materials != null) custom = true;
 
     
     ArrayList<MaterialData> mds = null;
     
     if (custom) { mds = bwt.materials; }
     else
     { String s;
       
       if (this.config.get("default_material") instanceof String)
       { s = this.config.getString("default_material"); }
       else { s = Integer.toString(this.config.getInt("default_material", 5)); }
        mds = get_materials(s.split(" "), p); }
     
     MaterialData md = mds.get(0);
     
     count = mds.size();
     
     Block block2 = block; byte b2; int j; String[] arrayOfString2;
     for (j = (arrayOfString2 = args).length, b2 = 0; b2 < j; ) { String s = arrayOfString2[b2]; byte b; int k;
       char[] arrayOfChar;
       for (k = (arrayOfChar = s.toCharArray()).length, b = 0; b < k; ) { char c = arrayOfChar[b];
         
         String[] squares = ((String)this.chars.get(Character.valueOf(c))).split(","); byte b3; int m;
         String[] arrayOfString3;
         for (m = (arrayOfString3 = squares).length, b3 = 0; b3 < m; ) { String s2 = arrayOfString3[b3];
           
           Integer integer = Integer.valueOf(Integer.parseInt(s2.trim()));
           block2 = block.getRelative(bf, integer.intValue() / 7);
           block2.getRelative(BlockFace.UP, integer.intValue() % 7).setTypeIdAndData(md.getItemTypeId(), md.getData(), false);
           b3++; }
         
         if (mds.size() > 1) {
           
           count++;
           md = mds.get(count % mds.size());
         } 
         block = block2.getRelative(bf, 2); b++; }
       
       block = block2.getRelative(bf, 6);
       b2++; }
   
   }
 
   
   void log(Object message) {
     String s;
     if (message instanceof Integer) { s = Integer.toString(((Integer)message).intValue()); }
     else { s = (String)message; }
      getServer().getLogger().info(s);
   }
 
 
   
   private ArrayList<MaterialData> get_materials(String[] args, Player p) {
     ArrayList<MaterialData> mds = new ArrayList<MaterialData>(); byte b; int i; String[] arrayOfString;
     for (i = (arrayOfString = args).length, b = 0; b < i; ) { String arg = arrayOfString[b];
       if (arg.length() > 0) {
         
         String[] numbers = arg.split(":");
         
         try {
           Material mat = Material.getMaterial(Integer.parseInt(numbers[0]));
           
           if (mat != null) {
             byte b1; int j; String[] arrayOfString1;
             for (j = (arrayOfString1 = args).length, b1 = 0; b1 < j; ) { String s = arrayOfString1[b1]; byte b2; int k; String[] arrayOfString2;
               for (k = (arrayOfString2 = this.config.getString("banned_materials", "6 8 9 10 11 26 27 28").split(" ")).length, b2 = 0; b2 < k; ) { String s2 = arrayOfString2[b2]; if (s.equalsIgnoreCase(s2.trim()))
                 
                 { p.sendMessage(ChatColor.RED + "[Block Writer] That material isn't allowed!");
                   return null; }  b2++; }
                b1++; }
              MaterialData md = new MaterialData(mat);
             if (numbers.length == 2) md.setData(Byte.parseByte(numbers[1])); 
             mds.add(md);
           }
           else {
             
             p.sendMessage(ChatColor.RED + "[Block Writer] Bad material ID");
             return null;
           }
         
         } catch (Exception ne) {
           
           p.sendMessage(ChatColor.RED + "[Block Writer] Number IDs only!");
           return null;
         } 
       }  b++; }
      return mds;
   }
 
 
   
   private int count_blocks(String[] args) {
     int count = 0; byte b; int i; String[] arrayOfString;
     for (i = (arrayOfString = args).length, b = 0; b < i; ) { String s = arrayOfString[b]; byte b1; int j;
       char[] arrayOfChar;
       for (j = (arrayOfChar = s.toCharArray()).length, b1 = 0; b1 < j; ) { char c = arrayOfChar[b1];
         
         String[] squares = ((String)this.chars.get(Character.valueOf(c))).split(",");
         int temp = 0; byte b2; int k; String[] arrayOfString1;
         for (k = (arrayOfString1 = squares).length, b2 = 0; b2 < k; ) { String s2 = arrayOfString1[b2];
           
           Integer integer = Integer.valueOf(Integer.parseInt(s2.trim()));
           if (integer.intValue() > temp) temp = integer.intValue();  b2++; }
         
         count += temp / 7 + 1; b1++; }
       
       count += s.length() - 1; b++; }
     
     count += (args.length - 1) * 5;
     return count;
   }
 
 
   
   private void set_materials(String[] args, Player p) {
     bw_Text bwt = this.bw_data.get(p.getName());
     if (bwt == null) bwt = new bw_Text(); 
     ArrayList<MaterialData> mds = get_materials(args, p);
     if (mds == null)
       return; 
     bwt.materials = mds;
     this.bw_data.put(p.getName(), bwt);
     p.sendMessage(ChatColor.GREEN + "[Block Writer] Materials changed!");
   }
 
 
 
   
   private void restore_blocks(String p) {
     bw_Text bwt = this.bw_data.get(p);
     Block block = bwt.start_block;
     Block block2 = block;
     for (int i = 0; i < 7; i++) {
       
       for (int j = 0; j < (bwt.backup[0]).length; j++) {
         
         block2.setTypeIdAndData(bwt.backup[i][j].getItemTypeId(), bwt.backup[i][j].getData(), false);
         block2 = block2.getRelative(bwt.bf);
       } 
       block = block.getRelative(BlockFace.UP);
       block2 = block;
     } 
     bwt.backup = null;
   }
 
 
   
   private void backup_blocks(int columns, String p) {
     bw_Text bwt = this.bw_data.get(p);
     bwt.backup = new MaterialData[7][columns];
     Block block = bwt.start_block;
     Block block2 = block;
     
     for (int i = 0; i < 7; i++) {
       
       for (int j = 0; j < columns; j++) {
         
         bwt.backup[i][j] = new MaterialData(block2.getType(), block2.getData());
         block2 = block2.getRelative(bwt.bf);
       } 
       block = block.getRelative(BlockFace.UP);
       block2 = block;
     } 
   }
 
 
 
   
   private void remove_blocks(String arg, Player p) {
     try {
       int columns = Integer.parseInt(arg);
       
       double yaw = (p.getLocation().getYaw() + 22.5D) % 360.0D;
       if (yaw < 0.0D) yaw += 360.0D; 
       BlockFace bf = null;
       if (yaw > 65.0D && yaw <= 155.0D) { bf = BlockFace.EAST; }
       else if (yaw > 155.0D && yaw <= 245.0D) { bf = BlockFace.SOUTH; }
       else if (yaw > 245.0D && yaw <= 335.0D) { bf = BlockFace.WEST; }
       else if (yaw > 335.0D || yaw <= 65.0D) { bf = BlockFace.NORTH; }
       
       bw_Text bwt = this.bw_data.get(p.getName());
       if (bwt == null) bwt = new bw_Text(); 
       bwt.bf = bf;
       
       Block b = p.getTargetBlock(null, this.config.getInt("reach", 25)).getRelative(BlockFace.UP);
       Block b2 = b;
       bwt.start_block = b;
       this.bw_data.put(p.getName(), bwt);
       
       backup_blocks(columns, p.getName());
       for (int k = 0; k < 7; k++)
       {
         b2 = b;
         for (int l = 0; l < columns; l++) {
           
           b2.setType(Material.AIR);
           b2 = b2.getRelative(bf);
         } 
         b = b.getRelative(BlockFace.UP);
       }
     
     } catch (NumberFormatException ex) {
       
       p.sendMessage(ChatColor.RED + "[Block Writer] Must be a number!");
       return;
     } 
   }
 
 
   
   public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
     if (!(sender instanceof Player)) return false; 
     Player p = (Player)sender;
     
     if (cmd.getName().equalsIgnoreCase("undo_blocks") && args.length == 0) {
       
       bw_Text bwt = this.bw_data.get(p.getName());
       if (bwt.backup != null) { restore_blocks(p.getName()); }
       else { p.sendMessage(ChatColor.RED + "[Block Writer] Nothing to undo!"); }
        return true;
     } 
     
     if (cmd.getName().equalsIgnoreCase("set_material") && args.length > 0) {
       
       set_materials(args, p);
       return true;
     } 
     
     if (cmd.getName().equalsIgnoreCase("write_blocks") && args.length > 0) {
       
       write_chars(args, p);
       return true;
     } 
     
     if (cmd.getName().equalsIgnoreCase("count_blocks") && args.length > 0) {
       
       p.sendMessage(ChatColor.GREEN + "[Block Writer] That would need " + count_blocks(args) + " blocks");
       return true;
     } 
     
     if (cmd.getName().equalsIgnoreCase("reload_block_writer") && args.length == 0) {
       
       setup();
       p.sendMessage(ChatColor.GREEN + "[Block Writer] Config reloaded!");
       return true;
     } 
     
     if (cmd.getName().equalsIgnoreCase("remove_blocks") && args.length == 1) {
       
       remove_blocks(args[0], p);
       return true;
     } 
     
     return false;
   }
 }