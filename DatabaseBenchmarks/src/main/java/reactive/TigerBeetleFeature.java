package reactive;


import com.tigerbeetle.*;

import java.math.BigInteger;
import java.util.concurrent.ThreadLocalRandom;

public class TigerBeetleFeature {
    public static void main(String[] args) throws Exception {

        byte[] clusterID = UInt128.asBytes(0);
        String[] replicaAddresses = new String[] { "3000" };
        try (var client = new Client(clusterID, replicaAddresses)) {

            var debitAccountId= ThreadLocalRandom.current().nextLong(0, 1_000L);
            var creditAccountId=ThreadLocalRandom.current().nextLong(0, 1_000L);
            // Create two accounts
            AccountBatch accounts = new AccountBatch(2);

            accounts.add();
            accounts.setId(debitAccountId);
            accounts.setLedger(1);
            accounts.setCode(1);

            accounts.add();
            accounts.setId(creditAccountId);
            accounts.setLedger(1);
            accounts.setCode(1);

            CreateAccountResultBatch accountErrors = client.createAccounts(accounts);
            while (accountErrors.next()) {
                System.err.printf("Error creating account %d: %s\n",
                        accountErrors.getIndex(),
                        accountErrors.getResult());
                assert false;
            }

            var transferId=ThreadLocalRandom.current().nextLong(0, 1_000L);
            TransferBatch transfers = new TransferBatch(1);
            transfers.add();
            transfers.setId(transferId);
            transfers.setDebitAccountId(debitAccountId);
            transfers.setCreditAccountId(creditAccountId);
            transfers.setLedger(1);
            transfers.setCode(1);
            transfers.setAmount(10);

            CreateTransferResultBatch transferErrors = client.createTransfers(transfers);
            while (transferErrors.next()) {
                transferErrors.getResult();
                System.err.printf("Error creating transfer %d: %s\n",
                        transferErrors.getIndex(),
                        transferErrors.getResult());
                assert false;
            }

            IdBatch ids = new IdBatch(2);
            ids.add(debitAccountId);
            ids.add(creditAccountId);

            accounts = client.lookupAccounts(ids);
            assert accounts.getCapacity() == 2;

            while (accounts.next()) {
                long idLS = accounts.getId(UInt128.LeastSignificant);
                long idMS = accounts.getId(UInt128.MostSignificant);

                if (idMS == 0 && idLS == debitAccountId) {
                    assert accounts.getDebitsPosted().intValueExact() == 10;
                    assert accounts.getCreditsPosted().intValueExact() == 0;
                    System.out.println("Debit account OK: " + idLS);
                }
                else if (idMS == 0 && idLS == creditAccountId) {
                    assert accounts.getDebitsPosted().intValueExact() == 0;
                    assert accounts.getCreditsPosted().intValueExact() == 10;
                    System.out.println("Credit account OK: " + idLS);
                }
                else {
                    throw new IllegalStateException(
                            "Unexpected account ID: " + BigInteger.valueOf(idLS)
                    );
                }
            }


        }
    }


}
