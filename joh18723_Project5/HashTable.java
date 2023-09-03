import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class HashTable<T>{
    NGen<T>[] hashTable;
    public String type = "general";
    int uniqueWords;

    //TODO: Create a constructor that takes in a length and initializes the hash table array
    public HashTable(int length, String type){
        hashTable = new NGen[length];
        this.type = type;
    }
    public HashTable(){
        hashTable = new NGen[125];
    }
    //TODO: Implement a simple hash function
    /*
    all of the following hash functions will mod the hash value by the length
    of the hash table to make sure that it stays in the bounds of the table
    some of the functions also take the absolute value of this mod because I was
    getting negative indexes from some hash functions.
     */
    public int hash1(T item) {
        //simply add all characters in the string together, this was my first
        //attempt at writing a hash function, it is my worst performing function
        String s = (String) item;
        int hash=0;
        for(int i=0; i<s.length(); i++){
            hash+=s.charAt(i);
        }
        hash = hash%hashTable.length;
        return hash;
    }
    public int hash2(T item){
        //add together all characters in the string and distribute with prime numbers
        //this function did better than hash1 in distributing the words and reducing
        //collisions
        String s = (String) item;
        int hash=7;
        for(int i = 0; i<s.length(); i++){
            hash = hash*31 + s.charAt(i);
        }
        hash = Math.abs(hash%hashTable.length);
        return hash;
    }

    //TODO: Implement a second (different and improved) hash function
    public int hash3(T item) {
        //add the first and last characters and then subtract all middle characters, while distributing with prime numbers
        String s = (String) item;
        int hash=31;
        hash = hash*7 + s.charAt(0) + s.charAt(s.length()-1);
        for(int i =1; i< s.length()-1;i++){
            hash = hash - s.charAt(i)*11;
        }
        hash = Math.abs(hash%hashTable.length);
        return hash;
    }
    public int keywordHash(T item){
        //this is the specific hash function for the java reserved words in the keywords.txt file
        String s = (String) item;
        int hash = 7;
        hash = hash+s.charAt(0) + s.charAt(s.length()-1);
        for(int i = 1; i < s.length()-1; i++){
            hash = hash - s.charAt(i);
        }
        hash = Math.abs(hash%hashTable.length);
        return hash;
    }
    public int keywordHash2(T item){
        String s = (String) item;
        int hash = 31;
        if(s.length() < 4){
            for(int i =0; i<s.length(); i++){
                hash = hash*31 + s.charAt(i);
            }
            hash = Math.abs(hash%hashTable.length);
            return hash;
        }
        hash = hash + s.charAt(0)*7;
        for(int i =1; i<s.length(); i++){
            if(i%2==0){
                hash = hash*31*s.charAt(i);
            }
            else{
                hash = (hash*7)%s.charAt(i);
            }
        }
        hash = Math.abs(hash%hashTable.length);
        return hash;
    }

    //TODO: Implement the add method which adds an item to the hash table using your best performing hash function
    // Does NOT add duplicate items
    public void add(T item) {
        NGen<T> word;
        int hashIndex;
        if(this.type.equals("general")) {
            hashIndex = hash3(item);
        }
        else{
            hashIndex = hash3(item);
        }
        if(hashTable[hashIndex] != null){ //the spot in the hashtable is not empty, must link
            if(hashTable[hashIndex].getData().equals(item)){
                return; //don't add duplicates to the hash table
            }
            //must navigate to the end of the list that is already there
            NGen<T> ptr = hashTable[hashIndex];
            while(ptr.getNext() != null){
                if(ptr.getData().equals(item)){
                    return; //duplicate
                }
                ptr = ptr.getNext(); //iterate through the linked list
            }
            if(ptr.getData().equals(item)){
                return; //duplicate
            }
            word = new NGen<T>(item,null);
            ptr.setNext(word);//set link to item
            uniqueWords++;
            return;
        }
        word = new NGen<T>(item, null);
        hashTable[hashIndex] = word; //if there is nothing in the same spot
        uniqueWords++;
    }

    // ** Already implemented -- no need to change **
    // Adds all words from a given file to the hash table using the add(T item) method above
    @SuppressWarnings("unchecked")
    public void addWordsFromFile(String fileName) {
        Scanner fileScanner = null;
        String word;
        try {
            fileScanner = new Scanner(new File(fileName));
        }
        catch (FileNotFoundException e) {
            System.out.println("File: " + fileName + " not found.");
            System.exit(1);
        }
        while (fileScanner.hasNext()) {
            word = fileScanner.next();
            word = word.replaceAll("\\p{Punct}", ""); // removes punctuation
            this.add((T) word);
        }
    }
    //TODO: Implement the display method which prints the indices of the hash table and the number of words "hashed"
    // to each index. Also prints:
    // - total number of unique words
    // - number of empty indices
    // - number of nonempty indices
    // - average collision length
    // - length of longest chain
    public void display() {
        int wordsAtIndex;
        int maxWordsAtIndex=0;
        int emptyIndices=0;
        for(int i =0; i < hashTable.length;i++){
            if(hashTable[i] == null){ //there are no words at this index.
                System.out.println(i + ": " + 0);
                emptyIndices +=1;
                continue;
            }
            wordsAtIndex =1; //there this at least 1 word at this index.
            NGen<T> ptr = hashTable[i];
            while(ptr.getNext() != null){ //iterate through the linked list at the index, adding to the wordsAtIndex variable each time
                wordsAtIndex +=1;
                ptr = ptr.getNext();
            }
            if(wordsAtIndex > maxWordsAtIndex){
                maxWordsAtIndex= wordsAtIndex;
            }
            System.out.println(i + ": " + wordsAtIndex);
        }
        System.out.println("Unique words: " + uniqueWords);
        System.out.println("Empty Indices: " + emptyIndices);
        System.out.println("Nonempty Indices: " + (hashTable.length-emptyIndices));
        System.out.println("Average Collision Length: " + (uniqueWords/(hashTable.length-emptyIndices)));
        System.out.println("Length of longest chain: " + maxWordsAtIndex);
    }

    // TODO: Create a hash table, store all words from "canterbury.txt", and display the table
    //  Create another hash table, store all words from "keywords.txt", and display the table
    public static void main(String args[]) {
        /*
        for the general case, I started with a hash function that summed all the character values in the string and
        then took the mod with respect to the table length. Next I tried using some prime number scalars to make the
        entries more evenly distributed. Finally, with hash3, my best general hash function, I increased the complexity again
        and decided to sum the first and last character values and then subtract all middle character values, again using
        prime scalars along the way.
         */
        HashTable<String> genHashTable= new HashTable<>(125,"general");
        genHashTable.addWordsFromFile("gettysburg.txt");
        System.out.println("General Hash: " );
        genHashTable.display();

        /*
        for the specific, case I started experimenting with writing different hash functions that may cater towards the keywords set more specifically, but after writing
        keywordHash and keywordHash2, I found that hash3 was still better at evenly distributing the words in the hash table, so I made it so that regardless of it the type
        of the hash table is general or specific, it utilizes the hash3 hash function.
         */
        HashTable<String> keywordHashTable = new HashTable<>(13,"specific");
        keywordHashTable.addWordsFromFile("keywords.txt");
        System.out.println("------------------------------------------------------------------");
        System.out.println("Specific Hash: ");
        keywordHashTable.display();
        /*
        in conclusion, out of the functions that I thought of and wrote, hash3 was my
        best performing hash function, for both the general case and the specific case,
        it was the best at reducing collisions by distributing the elements along the table.
        After experimenting with table lengths, I found that even numbers typically
        worked better for my hash3 function, and that I can reduce the size of the table
        to 14 before the longest chain goes over 6 when using hash3 on keywords.txt.
         */
    }
}
