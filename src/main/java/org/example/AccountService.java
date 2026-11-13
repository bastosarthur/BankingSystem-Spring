package org.example;

import org.example.dto.FindTransactionResponse;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;


@Service
public class AccountService {

    private final TransactionDAO transactionDAO;

    private final AccountDAO accDAO;

    public AccountService(TransactionDAO transactionDAO, AccountDAO accDAO) {
        this.transactionDAO = transactionDAO;
        this.accDAO = accDAO;
    }

    @PostConstruct
    public void initialize() {
        System.out.println("AccountService initialized successfully");
    }

    public int createAccount(Account account, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            boolean cpfExists = accDAO.findAccountByCpf(session, account.getCpf());
            if (cpfExists) {
                return -1; // CPF ALREADY REGISTERED.
            }
            Transaction transaction = session.beginTransaction();
            try {
                int accountNumber = accDAO.persistAccount(session, account);
                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
                int password_success = accDAO.persistPassword(session, accountNumber, hashedPassword);
                if (password_success != 1) {
                    transaction.rollback();
                    return -2; // ACCOUNT_PASSWORD NOT INSERTED WITH NEW VALUE.
                }
                transaction.commit();
                return accountNumber;
            } catch (RuntimeException exception) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                throw exception;
            }
        }
    }

    public Account accountLogin(int accountNumber, String tryPassword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Account account = accDAO.findAccount(session, accountNumber);
            if (account == null) {
                return null;
            }
            String hashedPassword = accDAO.getPasswordHash(session, account.getAccountNumber());
            if (BCrypt.checkpw(tryPassword, hashedPassword)) {
                account.setLogged(true);
                account.setLoginTime(System.currentTimeMillis());
                return account;
            }
            return null;
        }
    }

    public int deposit(int accountNumber, BigDecimal amount) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Account managedAccount = accDAO.findAccount(session, accountNumber);
                if (managedAccount == null) {
                    transaction.rollback();
                    return -1; // ACCOUNT NOT FOUND ON THE DATABASE
                }
                managedAccount.setBalance(managedAccount.getBalance().add(amount));
                BankTransaction BankTransaction = new BankTransaction("deposit", null,
                        managedAccount, amount, true);
                int transactionId = transactionDAO.persistTransaction(session, BankTransaction);
                if (transactionId == 0) {
                    transaction.rollback();
                    return -2; // FAILED TO PERSIST TRANSACTION
                }

                transaction.commit();
                return transactionId;
            } catch (RuntimeException exception) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                throw exception;
            }

        }
    }

    public int withdraw(int accountNumber, BigDecimal amount) {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Account managedAccount = accDAO.findAccount(session, accountNumber);
                if (managedAccount == null) {
                    transaction.rollback();
                    return -1; // ACCOUNT NOT FOUND ON THE DATABASE
                }
                if (managedAccount.getBalance().compareTo(amount) < 0) {
                    transaction.rollback();
                    return -2; // NOT ENOUGH BALANCE.
                }
                managedAccount.setBalance(managedAccount.getBalance().subtract(amount));
                BankTransaction BankTransaction = new BankTransaction("withdraw", managedAccount,
                                null, amount, true);
                int transactionId = transactionDAO.persistTransaction(session, BankTransaction);
                if (transactionId == 0) {
                    transaction.rollback();
                    return -3; // ERROR PERSISTING THE TRANSACTION;
                }
                transaction.commit();
                return transactionId;
            } catch (RuntimeException exception) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                throw exception;
            }
        }
    }

    public FindTransactionResponse getTransaction(int accountNumber, int transactionId) {
        /*
        if (!account.isLogged()) {
            return null;
        }
        */
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            BankTransaction transaction = transactionDAO.findTransaction(session, accountNumber, transactionId);

            if (transaction == null) {
                return null;
            }

            String status;
            if (transaction.getStatus()) {
                status = "CONFIRMED";
            } else {
                status = "CANCELED";
            }
            Integer recipient =
                    transaction.getRecipient() == null ? null : transaction.getRecipient().getAccountNumber();
            Integer sender =
                    transaction.getSender() == null ? null : transaction.getSender().getAccountNumber();

            return new FindTransactionResponse(
                    transaction.getAmount(), transaction.getId(), recipient,
                    sender, status, transaction.getTransactionDate(), transaction.getType());
        }
    }

    public List<FindTransactionResponse> getAccountTransactions(int accountNumber) {
        /*
        if (!account.isLogged()) {
            return null;
        }
        */
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<BankTransaction> transactionList = transactionDAO.findAccountTransactions(
                    session, accountNumber);

            if (transactionList.isEmpty()) {
                return null;
            }

            List<FindTransactionResponse> requestResponseList = new ArrayList<>();
            String status;
            for (BankTransaction transaction : transactionList) {
                if (transaction.getStatus()) {
                    status = "CONFIRMED";
                } else {
                    status = "CANCELED";
                }
                Integer recipient =
                        transaction.getRecipient() == null ? null : transaction.getRecipient().getAccountNumber();
                Integer sender =
                        transaction.getSender() == null ? null : transaction.getSender().getAccountNumber();

                requestResponseList.add(new FindTransactionResponse(
                        transaction.getAmount(), transaction.getId(), recipient,
                        sender, status, transaction.getTransactionDate(), transaction.getType()));
            }
            return requestResponseList;
        }
    }

    public BigDecimal getBalance(int accountNumber) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Account managedAccount = accDAO.findAccount(session, accountNumber);
            if (managedAccount == null) {
                return null;
            }
            return managedAccount.getBalance();
        }
    }

    public int transfer(int senderAccount, int recipientAccount, BigDecimal amount) {
        /*
        if (!account.isLogged()) {
            return -1; // ACCOUNT NOT LOGGED IN
        }
        */
        if (senderAccount == recipientAccount) {
            return -1; // TRANSFER TO SAME ACCOUNT
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Account managedSender = accDAO.findAccount(session, senderAccount);
                if (managedSender == null) {
                    transaction.rollback();
                    return -2; // SENDER ACCOUNT NOT FOUND ON THE DATABASE
                }
                if (managedSender.getBalance().compareTo(amount) < 0) {
                    transaction.rollback();
                    return -3; // NOT ENOUGH BALANCE
                }
                managedSender.setBalance(managedSender.getBalance().subtract(amount));
                int successReceiver = transactionDAO.updateRecipient(session, recipientAccount, amount);
                if (successReceiver != 1) {
                    transaction.rollback();
                    return -4; // RECIPIENT ACCOUNT NOT FOUND ON THE DATABASE
                }
                Account receiver = session.getReference(Account.class, recipientAccount);
                BankTransaction newTransaction = new BankTransaction("transfer", managedSender, receiver,
                        amount, true);
                int transactionId = transactionDAO.persistTransaction(session, newTransaction);
                if (transactionId == 0) {
                    transaction.rollback();
                    return 0; // FAILED TO PERSIST TRANSACTION
                }
                transaction.commit();
                return transactionId;
            } catch (RuntimeException exception) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                throw exception;
            }
        }
    }

    public List<FindTransactionResponse> searchTransactionsByDate(int accountNumber, LocalDate date) {
        /*
        if (!account.isLogged()) {
            return null;
        }
        */
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<BankTransaction> transactionByDateList =
                    transactionDAO.findAccountTransactionsByDate(session, accountNumber, date);

            if (transactionByDateList.isEmpty()) {
                return null;
            }

            List<FindTransactionResponse> requestResponseList = new ArrayList<>();
            String status;
            for (BankTransaction transaction : transactionByDateList) {
                if (transaction.getStatus()) {
                    status = "CONFIRMED";
                } else {
                    status = "CANCELED";
                }
                Integer recipient =
                        transaction.getRecipient() == null ? null : transaction.getRecipient().getAccountNumber();
                Integer sender =
                        transaction.getSender() == null ? null : transaction.getSender().getAccountNumber();

                requestResponseList.add(new FindTransactionResponse(
                        transaction.getAmount(), transaction.getId(), recipient,
                        sender, status, transaction.getTransactionDate(), transaction.getType()));
            }
            return requestResponseList;
        }
    }

    public List<FindTransactionResponse> depositList(int accountNumber) {
        /*
        if (!account.isLogged()) {
            return null;
        }
        */
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<BankTransaction> depositList = transactionDAO.depositTransactions(session, accountNumber);

            List<FindTransactionResponse> requestResponseList = new ArrayList<>();
            String status;
            for (BankTransaction transaction : depositList) {
                if (transaction.getStatus()) {
                    status = "CONFIRMED";
                } else {
                    status = "CANCELED";
                }
                Integer recipient =
                        transaction.getRecipient() == null ? null : transaction.getRecipient().getAccountNumber();
                Integer sender =
                        transaction.getSender() == null ? null : transaction.getSender().getAccountNumber();

                requestResponseList.add(new FindTransactionResponse(
                        transaction.getAmount(), transaction.getId(), recipient,
                        sender, status, transaction.getTransactionDate(), transaction.getType()));
            }
            return requestResponseList;
        }
    }

    public List<BankTransaction> withdrawList(Account account) {
        if (!account.isLogged()) {
            return null;
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return transactionDAO.withdrawTransactions(session, account.getAccountNumber());
        }
    }
}
