package br.com.legion.economy;

import br.com.idea.api.shared.ApiProvider;
import br.com.idea.api.shared.user.User;
import br.com.legion.economy.transactions.Transaction;
import br.com.legion.economy.transactions.TransactionType;

import java.util.Date;
import java.util.Map;

public class EconomyAPI {

    public static Double get(String name) {
        Double points = EconomyProvider.Cache.Local.ECONOMY.provide().get(name);
        if (points == null) {
            return 0.0;
        }

        return points;
    }

    public static void deposit(Integer source, String name, double points) {
        Double points0 = EconomyProvider.Cache.Local.ECONOMY.provide().get(name);
        if (points0 == null) {
            points0 = 0.0;
        }

        EconomyProvider.Repositories.ECONOMY.provide().insertOrUpdate(name, points0 + points);
        EconomyProvider.Cache.Local.ECONOMY.provide().invalidate(name);

        User user = ApiProvider.Cache.Local.USERS.provide().get(name);

        EconomyProvider.Repositories.TRANSACTIONS.provide().insert(
                user.getId(),
                new Transaction(
                        source,
                        TransactionType.RECEIVED,
                        new Date(System.currentTimeMillis()),
                        points
                )
        );

        EconomyProvider.Cache.Local.TRANSACTIONS.provide().invalidate(user.getId());
    }

    public static void withdraw(Integer source, String name, double points) {
        Double points0 = EconomyProvider.Cache.Local.ECONOMY.provide().get(name);
        if (points0 == null) {
            points0 = 0.0;
        }

        double newBalance = points0 - points;
        if (newBalance < 0.0) {
            newBalance = 0.0;
        }

        EconomyProvider.Repositories.ECONOMY.provide().insertOrUpdate(name, newBalance);
        EconomyProvider.Cache.Local.ECONOMY.provide().invalidate(name);

        User user = ApiProvider.Cache.Local.USERS.provide().get(name);

        EconomyProvider.Repositories.TRANSACTIONS.provide().insert(
                user.getId(),
                new Transaction(
                        source,
                        TransactionType.SENT,
                        new Date(System.currentTimeMillis()),
                        newBalance
                )
        );

        EconomyProvider.Cache.Local.TRANSACTIONS.provide().invalidate(user.getId());
    }

    public static void define(String name, double points) {
        EconomyProvider.Repositories.ECONOMY.provide().insertOrUpdate(name, points);
        EconomyProvider.Cache.Local.ECONOMY.provide().invalidate(name);
    }

    public static boolean has(String name, double points) {
        Double points0 = EconomyProvider.Cache.Local.ECONOMY.provide().get(name);
        if (points0 == null) {
            return false;
        }

        return points0 >= points;
    }

    public static Map<String, Double> getTop(int index) {
        return EconomyProvider.Cache.Local.ECONOMY.provide().getRank(index);
    }
}
