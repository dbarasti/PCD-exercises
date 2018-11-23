package merkleClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.rmi.server.LogStream.log;
import static merkleClient.HashUtil.md5Java;

public class MerkleValidityRequest {

	/**
	 * IP address of the authority
	 * */
	private final String authIPAddr;
	/**
	 * Port number of the authority
	 * */
	private final int  authPort;
	/**
	 * Hash value of the merkle tree root. 
	 * Known before-hand.
	 * */
	private final String mRoot;
	/**
	 * List of transactions this client wants to verify 
	 * the existence of.
	 * */
	private List<String> mRequests;
	
	/**
	 * Sole constructor of this class - marked private.
	 * */
	private MerkleValidityRequest(Builder b){
		this.authIPAddr = b.authIPAddr;
		this.authPort = b.authPort;
		this.mRoot = b.mRoot;
		this.mRequests = b.mRequest;
	}
	/**
	 * <p>Method implementing the communication protocol between the client and the authority.</p>
	 * <p>The steps involved are as follows:</p>
	 * 		<p>0. Opens a connection with the authority</p>
	 * 	<p>For each transaction the client does the following:</p>
	 * 		<p>1.: asks for a validityProof for the current transaction</p>
	 * 		<p>2.: listens for a list of hashes which constitute the merkle nodes contents</p>
	 * 	<p>Uses the utility method {isTransactionValid(String, String, List<String>) isTransactionValid} </p>
	 * 	<p>method to check whether the current transaction is valid or not.</p>
	 * */
	public Map<Boolean, List<String>> checkWhichTransactionValid() throws IOException {
		//map to be returned
		Map<Boolean, List<String>> transactionsValidity = new HashMap<>();

		//Open connection
		InetSocketAddress remoteAddr = new InetSocketAddress(authIPAddr, authPort);
		SocketChannel client = SocketChannel.open(remoteAddr);
		System.out.println("Connecting to Server on port 1111...");

		// aggiungo alla fine della lista richieste una stringa che fa chiudere il server
		mRequests.add("close");

		//for each checkRequest do:
		mRequests.forEach(checkRequest->{
			//list that will be filled with the response from the server
			List<String> transactionNodes = null;

			//send current request to receive a validity proof
			byte[] message = new String(checkRequest).getBytes();
			ByteBuffer buffer = ByteBuffer.wrap(message);
			try {
				client.write(buffer);
			} catch (IOException e) {
				e.printStackTrace();
			}
			buffer.clear();

			//get response only if I just sent something different from "close"
			if(checkRequest != "close") {
				buffer = ByteBuffer.allocate(256);
				try {
					client.read(buffer);
				} catch (IOException e) {
					e.printStackTrace();
				}
				String result = new String(buffer.array()).trim();

				System.out.println("--- Message received: " + result);
			}


			//receive validity proofs as List
			//transactionNodes=server.res;
			//uses isTransactionValid()
			/*
			boolean validity = isTransactionValid(checkRequest, transactionNodes);
			transactionsValidity.get(validity).add(checkRequest);
			*/

		});

			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}


		return transactionsValidity;
	}
	
	/**
	 * 	Checks whether a transaction 'merkleTx' is part of the merkle tree.
	 * 
	 *  @param merkleTx String: the transaction we want to validate
	 *  @param merkleNodes String: the hash codes of the merkle nodes required to compute 
	 *  the merkle root
	 *  
	 *  @return: boolean value indicating whether this transaction was validated or not.
	 * */
	private boolean isTransactionValid(String merkleTx, List<String> merkleNodes) {
		//mRoot is the Hash value of the merkle tree Root

		/*
		Leaf containing data(merkleTx) must be hashed to be computed with the list returned from the server (merkleNodes),
		then hashedConcat is initialized to this value.
		*/
		String hashedConcat = md5Java(merkleTx);

		//to obtain a result comparable to mRoot i have to concatenate&hash each node of the list with his previous
		for (String hash :
				merkleNodes) {
			hashedConcat = md5Java(hashedConcat+hash);
		}
		return  mRoot.equals(hashedConcat);
	}

	/**
	 * Builder for the MerkleValidityRequest class. 
	 * */
	public static class Builder {
		private String authIPAddr;
		private int authPort;
		private String mRoot;
		private List<String> mRequest;	
		
		public Builder(String authorityIPAddr, int authorityPort, String merkleRoot) {
			this.authIPAddr = authorityIPAddr;
			this.authPort = authorityPort;
			this.mRoot = merkleRoot;
			mRequest = new ArrayList<>();
		}
				
		public Builder addMerkleValidityCheck(String merkleHash) {
			mRequest.add(merkleHash);
			return this;
		}
		
		public MerkleValidityRequest build() {
			return new MerkleValidityRequest(this);
		}
	}
}