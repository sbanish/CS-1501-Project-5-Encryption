// Shawn Banish - Project 5

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;

public class MySign{
	public static void main(String[] args)throws FileNotFoundException, NoSuchAlgorithmException, IOException{
		if(args.length !=2){
			System.out.println("Please use appropriate flags ");
			return;
		}
		
		String flag = args[0];
		String filename = args[1];
		
		if (flag.equals("s")){
			s(filename);
		}
		else if(flag.equals("v")){
			v(filename);
		}
	}
	
	public static void s(String file)throws FileNotFoundException, NoSuchAlgorithmException, IOException{
		System.out.println("Signing File " + file);
		File f = new File(file);
		Scanner s = new Scanner(f);
		ArrayList<String> fileLines = new ArrayList<>();
		String fileString = "";
		while(s.hasNext()){
			String str = s.nextLine();
			fileLines.add(str);
			fileString+=str;
		}
		MessageDigest m = MessageDigest.getInstance("SHA-256");
		
		BigInteger hash = new BigInteger(m.digest(fileString.getBytes())).abs();
		
		try{
			File priv = new File("privkey.rsa");
			s = new Scanner(priv);
			BigInteger d = new BigInteger(s.nextLine());
			BigInteger n = new BigInteger(s.nextLine());
			
			BigInteger decrypt = hash.modPow(d, n);
			
			FileWriter writer = new FileWriter(file + ".signed");
			writer.write(decrypt + "\n");
			for (String line : fileLines){
				writer.write(line + "\n");
			}
			writer.close();
			System.out.println("File Signature Saved to " + file + ".signed" );
		}catch (FileNotFoundException e){
			System.out.println("privkey.rsa not found");
			return;
		}
	}
	
	public static void v(String file)throws FileNotFoundException, NoSuchAlgorithmException {
		System.out.println("Verifying File " + file);
		File f = new File(file);
		Scanner s = new Scanner(f);
		ArrayList<String> fileLines = new ArrayList<>();
		String fileString = "";
		BigInteger decrypted = new BigInteger(s.nextLine());
		while(s.hasNext()){
			String str = s.nextLine();
			fileLines.add(str);
			fileString+=str;
		}
		MessageDigest m = MessageDigest.getInstance("SHA-256");
		
		BigInteger hash = new BigInteger(m.digest(fileString.getBytes())).abs();
		
		try{
			File pub = new File("pubkey.rsa");
			Scanner scan = new Scanner(pub);
			String s1 = scan.nextLine();
			String s2 = scan.nextLine();
			
			BigInteger e = new BigInteger(s1);
			BigInteger n = new BigInteger(s2);
			
			BigInteger encrypted = decrypted.modPow(e, n);
			
			boolean equals = hash.equals(encrypted);
			
			if (equals){
				System.out.println("Signature Valid");
			}
			else {
				System.out.println("Signature Invalid");
			}
			
		}catch (FileNotFoundException e){
			System.out.println("pubkey.rsa not found");
			return;
		}
	}
}