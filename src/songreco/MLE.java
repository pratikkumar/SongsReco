/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package songreco;

/**
 *
 * @author abhishek
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.String;
import java.util.Arrays;
import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.FrameBodyCOMM;
import org.farng.mp3.id3.FrameBodyTALB;
import org.farng.mp3.id3.FrameBodyTCOM;
import org.farng.mp3.id3.FrameBodyTCON;
import org.farng.mp3.id3.FrameBodyTDRC;
import org.farng.mp3.id3.FrameBodyTIT2;
import org.farng.mp3.id3.FrameBodyTPE1;
import org.farng.mp3.id3.FrameBodyTRCK;
import org.farng.mp3.id3.FrameBodyTYER;
import org.farng.mp3.id3.FrameBodyUSLT;
import org.farng.mp3.id3.ID3v1;
import org.farng.mp3.id3.ID3v1_1;
import org.farng.mp3.id3.ID3v2_2;
import org.farng.mp3.id3.ID3v2_3;
import org.farng.mp3.id3.ID3v2_4;
import org.farng.mp3.lyrics3.FieldBodyAUT;
import org.farng.mp3.lyrics3.FieldBodyEAL;
import org.farng.mp3.lyrics3.FieldBodyEAR;
import org.farng.mp3.lyrics3.FieldBodyETT;
import org.farng.mp3.lyrics3.FieldBodyINF;
import org.farng.mp3.lyrics3.FieldBodyLYR;
import org.farng.mp3.lyrics3.Lyrics3v1;
import org.farng.mp3.lyrics3.Lyrics3v2;

import java.util.HashMap;
import java.util.Vector;

/**
 *
 * @author pulkit
 */

// Maximum Likelihood Estimation

public class MLE {

    /**
     * @param args the command line arguments
     */
    
    public static Vector<String> recommendedSongs;
    public static int numOfPlaylist = 9;
    public static int numOfSongs = 77;
    public static int numOfAttributes = 5;
    public static String[][] matrixPlaylist = new String[numOfPlaylist][numOfAttributes];
    public static String[][] matrixDB = new String[numOfSongs][numOfAttributes];
    public static double[] rankscore= new double[numOfSongs];
    public static HashMap<String, Integer>[] attribute_counts;
    public static HashMap<String, Double>[] attribute_probability;
    public static double[] weights;
    public static void start() throws Exception
    {
        // TODO code application logic here
        calculate_weights();
        File dir = new File("/home/pratik/songReco/src/songreco/HackU_data/pl");
        int index=0;
        for (File child : dir.listFiles()) {
            //check for mp3 files only
            String type = child.getAbsolutePath().substring(child.getAbsolutePath().lastIndexOf(".")+1);
            if (type.compareTo("mp3")==0){
                System.out.println(child.getAbsolutePath());
                displayProperties(child,matrixPlaylist,index,0);
                
                index++;
            }
            
        }
        dir = new File("/home/pratik/songReco/src/songreco/HackU_data/database");
        index=0;
        for (File child : dir.listFiles()) {
            //check for mp3 files only
            String type = child.getAbsolutePath().substring(child.getAbsolutePath().lastIndexOf(".")+1);
            if (type.compareTo("mp3")==0){
                System.out.println("test test" + " " + child.getAbsolutePath());
                displayProperties(child,matrixDB,index,1);
                
                index++;
            }
            
        }
        
        
        
        System.out.println("Arbit");
        
        print(matrixDB,numOfSongs);
        
        //print(matrixPlaylist,numOfPlaylist);
        //System.out.println("------------------------------------");
        //print(matrixDB,numOfSongs);
        //        System.out.println("rahul-------------------");
        initialize_counts_and_probability();
        int[] rScores = ranksongs(matrixDB, numOfSongs);
        for (int i=0;i<numOfSongs;i++){
            System.out.println(rankscore[i]);
        }
        int size = rScores[0];
        recommendedSongs = new Vector<String>();
        for(int i = 0; i< size; i++)
        {
            System.out.println(matrixDB[rScores[i+1]][0]);
            recommendedSongs.add(matrixDB[rScores[i+1]][0]);
        }

    }
    public static void main(String[] args) throws Exception{
        // TODO code application logic here
        
        calculate_weights();
        File dir = new File("/home/abhishek/NetBeansProjects/songReco/src/songreco/HackU_data/");
        int index=0;
        for (File child : dir.listFiles()) {
            //check for mp3 files only
            String type = child.getAbsolutePath().substring(child.getAbsolutePath().lastIndexOf(".")+1);
            if (type.compareTo("mp3")==0){
                System.out.println(child.getAbsolutePath());
                displayProperties(child,matrixPlaylist,index,0);
                
                index++;
            }
            
        }
        dir = new File("/home/abhishek/NetBeansProjects/songReco/src/songreco/HackU_data/Rock");
        index=0;
        for (File child : dir.listFiles()) {
            //check for mp3 files only
            String type = child.getAbsolutePath().substring(child.getAbsolutePath().lastIndexOf(".")+1);
            if (type.compareTo("mp3")==0){
                displayProperties(child,matrixDB,index,1);
                
                index++;
            }
            
        }
        //print(matrixPlaylist,numOfPlaylist);
        //System.out.println("------------------------------------");
        
        System.out.println("Arbit");
        
        print(matrixDB,numOfSongs);
        //        System.out.println("rahul-------------------");
        initialize_counts_and_probability();
        int[] rScores = ranksongs(matrixDB, numOfSongs);
        for (int i=0;i<numOfSongs;i++){
            System.out.println(rankscore[i]);
        }
        int size = rScores[0];
        recommendedSongs = new Vector<String>();
        for(int i = 0; i< size; i++)
        {
            System.out.println(matrixDB[rScores[i+1]][0]);
            recommendedSongs.add("aaa");
        }
    }    
   public static void displayProperties(File sourceFile , String[][] s , int index, int type){     
        try{
            MP3File mp3file = new MP3File(sourceFile);
             //tag = new ID3v1(sourceFile);  
            ID3v2_2 tag = (ID3v2_2) mp3file.getID3v2Tag();
            
            if(tag == null)
            {
                if(type == 0)
                {
                    System.out.println("Playlist decremented");
                    numOfPlaylist--;
                }
                else if(type == 1)
                {
                    System.out.println("DB decremented");
                    numOfSongs--;
                }
                    
                return;
            }
            //System.out.println(tag.getGenre());
            String songTitle=tag.getSongTitle();
            String songGenre=tag.getSongGenre();
            String songAlbumTitle=tag.getAlbumTitle();
            String songAuthorComposer=tag.getAuthorComposer();
            String songLeadArtist=tag.getLeadArtist();
            s[index][0]=songTitle;
            s[index][1]=songGenre;
            s[index][2]=songAlbumTitle;
            s[index][3]=songAuthorComposer;
            s[index][4]=songLeadArtist;
        } 
        catch(IOException e ){ 
           System.out.println("Error2"); 
        }
        catch(TagException e){
            System.out.println("Error3");
        }
    }
    
    public static void print(String[][] s , int index){
        for (int i=0;i<index;i++){
            for (int j=0;j< numOfAttributes ; j++){
                System.out.println(s[i][j]);
            }
            System.out.println("------------");
        }
    }
   


    
    
    
    public static void initialize_counts_and_probability()
    {
        attribute_counts = new HashMap[numOfAttributes];
        attribute_probability = new HashMap[numOfAttributes];
        for( int j = 0 ; j < numOfAttributes ; j++)
        {
            attribute_counts[j] = new HashMap();
            attribute_probability[j] = new HashMap();
        }
        for( int i = 0 ; i < numOfPlaylist; i++)
        {
            for( int j = 0 ; j < numOfAttributes ; j++)
            {
                // Lower Case
                if(matrixPlaylist[i][j] == null || matrixPlaylist[i][j] == "")
                    continue;
                matrixPlaylist[i][j] = matrixPlaylist[i][j].toLowerCase();
                        
                if (attribute_counts[j].containsKey(matrixPlaylist[i][j])) 
                {
                    Integer val = attribute_counts[j].get(matrixPlaylist[i][j]);
                    val++;
                    attribute_counts[j].put(matrixPlaylist[i][j], val);
                    attribute_probability[j].put(matrixPlaylist[i][j], calculate_probability(val,numOfPlaylist));
                    
                } 
                else 
                {
                    attribute_counts[j].put(matrixPlaylist[i][j], 1);
                    attribute_probability[j].put(matrixPlaylist[i][j], calculate_probability(1,numOfPlaylist));
                }
                
            }
            
        }
        
    }
    
    // To be done Later
    public static double calculate_weights()
    {
        weights = new double[numOfAttributes];
        weights[0] = 0;
        weights[1] = 1;
        weights[2] = 1;
        weights[3] = 1;
        weights[4] = 1;
        
        return 0;
    }
    public static double calculate_score(String[] song)
    {
        double score = 1.0;
        
        for(int j = 0 ; j < numOfAttributes ; j++)
        {
            if(song[j] == "" || song[j] == null) continue;
            if (attribute_probability[j].containsKey(song[j].toLowerCase())){
            double current_prob = attribute_probability[j].get(song[j].toLowerCase());
            score *= current_prob;
            score *= weights[j];
            }
        }
        if(score == 1.0)
            score = 0.0;
        return score;
    }
    
    public static int[] ranksongs(String[][] Songs, int numOfSongsInDatabase)
    {
        int[] ret_val = new int[6];
        ret_val[0] = 0;
        for(int i = 0; i < numOfSongsInDatabase ; i++)
        {
            rankscore[i] = 0;
            rankscore[i] = calculate_score(Songs[i]);
            System.out.println("RankScore" + rankscore[i]);
        }
        
        int maxindex;
        int count = 1;
        for(int i = 0; i < numOfSongsInDatabase; i++)
        {
            maxindex = i;
            for(int j = i+1; j < numOfSongsInDatabase; j++)
            {
                System.out.println("check" +  rankscore[j] + "  " + rankscore[i]);
                if(rankscore[j]>rankscore[i])
                {
                    maxindex = j;
                    System.out.println("larger index" + maxindex);
                }
            }
            
         
            double temp = rankscore[maxindex];
            rankscore[maxindex] = rankscore[i];
            rankscore[i] = temp;
            ret_val[0]++;
            ret_val[count] = maxindex;
            System.out.println("maxindex" + maxindex + matrixDB[maxindex][1]);
            count++;
            if(count == 6)
                break;
        }
        System.out.println("Test" + count);
        return ret_val;
    }
    
    public static double calculate_probability(int num, int denom)
    {
        // Divide Function
        if(denom <= 0)
        {
            return 0;
        }
        else
        {
            return (double)num/(double)denom;
        }
    }
    
    
    /**
     * @param args the command line arguments
     */
}

