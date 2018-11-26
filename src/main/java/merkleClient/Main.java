package merkleClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;


public class Main {

	public static final void main(String args[]) throws IOException {
		
		String merkleRoot = HashUtil.md5Java("0000000000");
		String merkleTx_1 = HashUtil.md5Java("0000000001");
		String merkleTx_2 = HashUtil.md5Java("0000000020");
		
		Map<Boolean, List<String>> report = new MerkleValidityRequest.Builder("localhost", 1111, merkleRoot)
								 									.addMerkleValidityCheck(merkleTx_1)
								 									.addMerkleValidityCheck(merkleTx_2)
								 									.build()
								 									.checkWhichTransactionValid();		

		//print non-valid transactions
		System.out.println("non-valid transactions: ");
		report.get(false).forEach(System.out::println);

		//print valid transactions
		report.get(true).forEach(System.out::println);

	}	
}
