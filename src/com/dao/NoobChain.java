package com.dao;

import java.util.ArrayList;

import com.google.gson.GsonBuilder;

public class NoobChain {
	
	public static ArrayList<Block> blockchain = new ArrayList<Block>();
	public static int difficulty = 5;

	public static String getBlock(String content) throws Exception {	
		//add our blocks to the blockchain ArrayList:

		String hash="";
		blockchain.clear();
		String c1=content.substring(0,content.length()/3);
		String c2=content.substring(content.length()/3,(content.length()/3)*2);
		String c3=content.substring((content.length()/3)*2,content.length());
		blockchain.add(new Block(c1, "0"));
		System.out.println("Trying to Mine and Storing into Peer 1... ");
		blockchain.get(0).mineBlock(difficulty);
		
		blockchain.add(new Block(c2,blockchain.get(blockchain.size()-1).hash));
		System.out.println("Trying to Mine and Storing into Peer 2... ");
		blockchain.get(1).mineBlock(difficulty);
		
		blockchain.add(new Block(c3,blockchain.get(blockchain.size()-1).hash));
		System.out.println("Trying to Mine and Storing into Peer 3... ");
		blockchain.get(2).mineBlock(difficulty);	
		
		System.out.println("\nBlockchain is Valid: " + isChainValid());
		
		System.out.println(blockchain);
		
		String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
		System.out.println("\nThe block chain: ");
		System.out.println(blockchainJson);
		
		for(Block b:blockchain)
		{
			if(b.previousHash.equals("0"))
			{
				hash=b.hash;
			}
			DBConnection.addblock(b.previousHash, b.hash, b.data);
		}
		String h=Encryption.encrypt(hash);
		return h;
	}
	
	public static Boolean isChainValid() {
		Block currentBlock; 
		Block previousBlock;
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');
		
		//loop through blockchain to check hashes:
		for(int i=1; i < blockchain.size(); i++) {
			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i-1);
			//compare registered hash and calculated hash:
			if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
				System.out.println("Current Hashes not equal");			
				return false;
			}
			//compare previous hash and registered previous hash
			if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
				System.out.println("Previous Hashes not equal");
				return false;
			}
			//check if hash is solved
			if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
				System.out.println("This block hasn't been mined");
				return false;
			}
		}
		return true;
	}
	
	public static void main(String[] args) {
		try {
			String s=getBlock("this is my first block to use this");
			System.out.println("Encrypted data "+s);
			System.out.println("decrypted data"+Encryption.decrypt(s));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
